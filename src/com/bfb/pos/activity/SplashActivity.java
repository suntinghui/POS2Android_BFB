package com.bfb.pos.activity;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.WindowManager;

import com.bfb.pos.agent.client.ApplicationEnvironment;
import com.bfb.pos.agent.client.Constant;
import com.bfb.pos.dynamic.core.Event;
import com.bfb.pos.R;

public class SplashActivity extends BaseActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	    getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN); //设置全屏
        
        setContentView(R.layout.splash);
        
        new SplashTask().execute();
	}
	
	class SplashTask extends AsyncTask<Object, Object, Object>{

		@Override
		protected Object doInBackground(Object... arg0) {
			try{
				// 利用闪屏界面初始化系统
				long startTime = System.currentTimeMillis();
				ApplicationEnvironment.getInstance().initialize(SplashActivity.this.getApplication());
				long endTime = System.currentTimeMillis();
				long cashTime = endTime - startTime;
				Log.e("Splash Time", String.valueOf(cashTime));
				
				// 从效果上保证最少要保持闪屏界面不少于1500ms
				// 因为现在有取地址会占用一些时间，所以就不加额外等待时间了。
//				if (cashTime < 1500){
//					Thread.sleep(1500 - cashTime);
//				}
				
				return null;
				
			}catch(Exception e){
				e.printStackTrace();
				return null;
			}
			
		}

		@Override
		protected void onPostExecute(Object result) {
			if (Constant.isStatic){
				Intent intent = new Intent(SplashActivity.this, LRCatalogActivity.class);
				SplashActivity.this.startActivity(intent);
				SplashActivity.this.finish();
				
				return;
			}
			
			if (ApplicationEnvironment.getInstance().isNetworkAvailable){
				Intent intent = new Intent(SplashActivity.this, LoginActivity.class);
				SplashActivity.this.startActivity(intent);
				SplashActivity.this.finish();
				
			} else{
				AlertDialog.Builder builder = new Builder(BaseActivity.getTopActivity());
				builder.setTitle("提示");
				builder.setCancelable(false);
				builder.setMessage(R.string.noNetTips);
				builder.setPositiveButton("设置网络", new android.content.DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						Intent intent = new Intent(Settings.ACTION_WIRELESS_SETTINGS);
						startActivity(intent);
						
						finish();
					}
				});
				builder.setNegativeButton("取消", new android.content.DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
						finish();
					}
				});
				
				builder.show();
			}
		}
		
	}
	
}
