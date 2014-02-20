package com.bfb.pos.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.json.JSONObject;
import org.json.JSONTokener;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.util.Log;
import android.widget.Toast;

import com.baidu.location.LocServiceMode;
import com.baidu.location.LocationChangedListener;
import com.baidu.location.LocationClient;
import com.baidu.location.ReceiveListener;
import com.bfb.pos.activity.BaseActivity;
import com.bfb.pos.agent.client.AppDataCenter;
import com.bfb.pos.R;

/*
 * 测试发现，没有SIM卡只有WIFI仍然取不到正确的地址，返回167错误，地址内空为空。
 * 
 * 取地址会占用主线程，如果有连续10次取不到地址，就不再去取地址
 */

public class LocationUtil implements ReceiveListener, LocationChangedListener{
	
	public static LocationUtil instance = null;
	public static LocationClient locationClient = null; 
	
	private static final int RETRY_MAXCOUNT = 10;
	int count = 0; // 连续取地址失败次数
	
	public static LocationUtil getInstance(){
		if (null == instance){
			instance = new LocationUtil();
		}
		
		return instance;
	}
	
	public void initLocation(Context context){
		// 百度地图
		locationClient = new LocationClient(context);
		locationClient.setProdName("WMPAY");
		// 百度坐标系 http://www.cnblogs.com/jz1108/archive/2011/07/02/2095376.html
		locationClient.setCoorType("gcj02");
		locationClient.setAddrType("province|city|district");
		locationClient.setServiceMode(LocServiceMode.Immediat);
		locationClient.addRecerveListener(getInstance());
		locationClient.start();
		locationClient.getLocation();
	}
	
	@Override
	public void onReceive(String strData) {
		if (null == strData || "InternetException".equals(strData)){
			retry(strData);
			
		} else{
			try {
				// JSON详情请参阅：http://dev.baidu.com/wiki/geolocation/index.php?title=AndroidAPI%E6%8E%A5%E5%8F%A3%E6%96%87%E6%A1%A31.x
				JSONTokener tokener = new JSONTokener(strData);
				JSONObject location = (JSONObject) tokener.nextValue();
				Log.e("addr json", location.toString());
				String errorCode = location.getJSONObject("result").getString("error");
				
				if ("161".equals(errorCode)){ // 其中“error”为161表示定位成功，其他值为失败
					count = 0; // 成功取得地址后，置0
					
					String pointX = location.getJSONObject("content").getJSONObject("point").getString("x");
					String pointY = location.getJSONObject("content").getJSONObject("point").getString("y");
					JSONObject addr = location.getJSONObject("content").getJSONObject("addr");
					
					StringBuilder sb = new StringBuilder();
					AppDataCenter.setAddress(sb.append(pointX).append(",").append(pointY).append(",").append(addr.getString("province")).append(addr.getString("city")).append(addr.getString("district")).toString());
					
					this.checkSysDate(location.getJSONObject("result").getString("time"));
					
					Log.e("address", "成功取得地址："+AppDataCenter.getValue("__ADDRESS") + " --- 将切换至后台监听模式");
					
					// 取地址成功，将切换到后台监听模式
					this.changeToBackGroundModel();
					
				} else {
					
					retry(strData);
				}
				
			} catch (Exception e) {
				retry(strData);
				
				e.printStackTrace();
			}
		}
		
	}
	
	/**
	 * 根据百度返回的日期时间比较手机的日期时间，如果两者之间相差1个小时，则提示用户手机时间可能不准确，仅是提示。
	 * @param dateTime 百度地图返回的日期时间 yyyy-MM-dd HH:mm:ss
	 */
	private void checkSysDate(String dateTime){
		if (null == dateTime || "".equals(dateTime.trim()))
			return;
		
		long spacing = 1*60*60*1000; // 1个小时的毫秒数
		
		try {
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			Date bdDate = sdf.parse(dateTime);
			
			long temp = Math.abs(bdDate.getTime() - System.currentTimeMillis());
			if (spacing < temp){
				/**
				AlertDialog.Builder modalDialog = new AlertDialog.Builder(BaseActivity.getTopActivity());
				modalDialog.setTitle("提示");
				modalDialog.setMessage(R.string.sysTimeTip);
				modalDialog.setCancelable(false);
				modalDialog.setPositiveButton("确定", new android.content.DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
					}
				});
				modalDialog.show();
				****/
				
				Toast.makeText(BaseActivity.getTopActivity(), R.string.sysTimeTip, Toast.LENGTH_LONG).show();
			}
			
			
		} catch (ParseException e) {
			e.printStackTrace();
		}
	}
	
	private void retry(String strData){
		if (count++ > RETRY_MAXCOUNT){
			Log.e("", "连续"+RETRY_MAXCOUNT+"次没有取到地址，系统停止取地址，切换至监听模式");
			this.changeToBackGroundModel();
			return;
		}
		
		try {
			Thread.sleep(3000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
		// 奇怪，在DEBUG下，系统会自动在15秒后再次检测，但在正常模式下不会执行
		int res = locationClient.getLocation();
		if (res == 1){
			locationClient.start();
			locationClient.getLocation();
		}
		
		Log.e("address", "没有取到地址，重试取地址..." + strData + getErrorDesc(res));
	}

	@Override
	public void onLocationChanged() {
		if (locationClient != null){
			// 地址发生改变，将切换到立即取地址模式
			Log.e("address", "地址发生改变，将重新取址");
			locationClient.setServiceMode(LocServiceMode.Immediat);
			locationClient.addRecerveListener(getInstance());
			locationClient.removeLocationChangedLiteners();
			locationClient.getLocation();
		}
	}
	
	private void changeToBackGroundModel(){
		if (locationClient != null){
			locationClient.setServiceMode(LocServiceMode.Background);
			locationClient.setTimeSpan(30000); // 为了防止多度频繁的扫描，百度默认最小值为3秒，这里设定为3分钟
			locationClient.addLocationChangedlistener(getInstance());
			locationClient.removeReceiveListeners();
			Log.e("address", "地图已切换至后台监听模式");
		}
	}
	
	public void clearLocation(){
		if (locationClient != null){
			locationClient.removeReceiveListeners();
			locationClient.removeLocationChangedLiteners();
			locationClient.stop();
			locationClient = null;
		}
	}
	
	private String getErrorDesc(int errorCode){
		switch(errorCode){
		case 0:
			return " 正常 ";
			
		case 1:
			return " SDK还未启动 ";
			
		case 2:
			return " 没有监听函数 ";
			
		case 6:
			return " 请求间隔过短 ";
		}
		
		return " 不可能的错误代码：" + errorCode;
		
		
	}

}
