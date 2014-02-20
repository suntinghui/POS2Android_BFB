package com.bfb.pos.agent.client;

import java.util.ArrayList;
import java.util.UUID;

import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.WindowManager;

import com.bfb.pos.activity.BaseActivity;
import com.bfb.pos.util.LocationUtil;
import com.bfb.pos.util.TrafficUtil;

public class ApplicationEnvironment {
	
	private static ApplicationEnvironment appEnv 	= null;
	
	public static final String POS2ANDROID 			= "POS2ANDROID";
	
	// TODO 与mainifest中的有什么区别，应该用哪一个，版本升级分大小。。。
	// 当前发布的系统的版本号。Important!
	public static final int VERSION 				= 1; 
	
	private Application application 				= null;
	private SharedPreferences preferences 			= null;
	
	public boolean isNetworkAvailable 				= true; // 网络是否可用。
	
	public NetClient netClient 						= null;
	
	public boolean initialized 						= false;
	
	// 屏幕高度和宽度
	public int screenWidth							= 0;
	public int screenHeight							= 0;
	
	public static ApplicationEnvironment getInstance(){
		if (null == appEnv){
			appEnv = new ApplicationEnvironment();
		}
		
		return appEnv;
	}
	
	public Application getApplication(){
		if (null == this.application){
			this.application = BaseActivity.getTopActivity().getApplication();
		}
		
		return this.application;
	}
	
	public SharedPreferences getPreferences(){
		if (null == preferences)
			preferences = this.getApplication().getSharedPreferences(ApplicationEnvironment.POS2ANDROID, Context.MODE_PRIVATE);
		
		return preferences;
	}
	
	/**
	 * ****注意这里的初始化工作不能读取文件进行初始化。因为有可能文件是过期的。在登陆时会下载新的文件信息，应该在
	 * 登陆更新文件成功后才读取必须的数据。
	 */
	public void initialize(Application app) {
		this.application = app;
		
		if (!initialized) {
			/**
	         * QVGA 320x240,WQVGA400 400x240,WQVGA432 432x240,HVGA 480x320,WVGA800 800x480,VGA854 854x480等
	         */
			DisplayMetrics dm = new DisplayMetrics();  
			((WindowManager)application.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay().getMetrics(dm);
			this.screenWidth = dm.widthPixels;
			this.screenHeight = dm.heightPixels;
			
	        Log.i("width--height-->",dm.widthPixels+"--"+dm.heightPixels);

	        netClient = new NetClient();
	        
			this.initFiles();
			
			// 初始化流量监控应用
			TrafficUtil.getInstance().initTraffic();
			
			isNetworkAvailable = this.checkNetworkAvailable();
			
			initialized = true;
		}
	}
	
	private void initFiles(){
		// 如果是第一次启动系统，首先需要初始化，将assets下所有的文件复制到/data/data/com.bfb.pos/assets下。
		// 然后将当前系统的版本号更新为ApplicationEnvironment.VERSION
		int currentVersion = ApplicationEnvironment.getInstance().getPreferences().getInt(Constant.VERSION, 0);
		
		// TODO 测试阶段需要及时更新修改的文件。为了方便要求每次必须要将文件copy到手机中
//		if (0 == currentVersion) {
		if (true) {
			// 说明是第一次启动系统，需要初始化系统
			UpdateClient.copyAssetsToMobile();
			// 初始化完成后更新当前版本号
			Editor editor = ApplicationEnvironment.getInstance().getPreferences().edit();
			editor.putInt(Constant.VERSION, ApplicationEnvironment.VERSION);
			
			// 向程序注入一个UUID字符串值。
			editor.putString(Constant.UUIDSTRING, UUID.randomUUID().toString());
			
			editor.putBoolean(Constant.NEWAPP, true);
			
			editor.commit();
		}
	}
	
	public boolean checkNetworkAvailable() {
		ConnectivityManager manager = (ConnectivityManager) this.getApplication().getSystemService(Context.CONNECTIVITY_SERVICE);
		if (manager == null)
			return false;
		
		NetworkInfo netinfo = manager.getActiveNetworkInfo();
		if (netinfo == null) {
			return false;
		}
		if (netinfo.isConnected()) {
			return true;
		}
		return false;
	}
	
	/*
	private boolean checkNetworkAvailable() {
		ConnectivityManager connectivity = (ConnectivityManager) this.application.getSystemService(Context.CONNECTIVITY_SERVICE);
		if (null == connectivity) {
			return false;
		} else {
			NetworkInfo[] info = connectivity.getAllNetworkInfo();
			if (null != info) {
				for (NetworkInfo nwi : info) {
					if (nwi.getState() == NetworkInfo.State.CONNECTED) {
						return true;
					}
				}
			}
		}
		return false;
	}
	
	***/
	
	private void cleanUp() {
		if (!initialized)
			return;
		
		LocationUtil.getInstance().clearLocation();
		// 关闭FSKService
		this.getApplication().stopService(new Intent("com.bfb.pos.fskService"));
		
		// 停止超时服务
		this.getApplication().stopService(new Intent("com.bfb.pos.timeoutService"));
		
		this.getApplication().stopService(new Intent("com.bfb.pos.uploadSignImageService"));
		
		// 关闭所有的Activity
		ArrayList<BaseActivity> list = BaseActivity.getAllActiveActivity();
		if (null != list){
			for (BaseActivity activity : list){
				activity.finish();
			}
		}
		
		initialized = false;
	}

	public void ForceLogout() {
		try {
			netClient.Reset();
			
			netClient = null;

			cleanUp();
			
			appEnv = null;

		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}
	
}
