package com.bfb.pos.activity;

import org.androidpn.client.ServiceManager;

import com.bfb.pos.agent.client.ApplicationEnvironment;
import com.bfb.pos.agent.client.Constant;
import com.bfb.pos.R;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences.Editor;
import android.net.ConnectivityManager;

public class BootBroadcastReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		// ServiceManager不是Android的类
		ServiceManager serviceManager = new ServiceManager(context);
        serviceManager.setNotificationIcon(R.drawable.ic_launcher);
        
		if (intent.getAction().equals(Intent.ACTION_BOOT_COMPLETED)){
			// 开机启动服务
	        serviceManager.startService();
	        
		} else if (intent.getAction().equals(Intent.ACTION_SHUTDOWN)){
			// 关机时关闭服务，以使推送服务器及时得到响应状态。
			serviceManager.stopService();
			
		} else if(intent.getAction().equals(Intent.ACTION_PACKAGE_REPLACED)){
			// 更新应用后要再次显示新手引导
			Editor editor = ApplicationEnvironment.getInstance().getPreferences().edit();
			editor.putBoolean(Constant.NEWAPP, true);
			editor.commit();
			
		} else if ( intent.getAction().equals(ConnectivityManager.CONNECTIVITY_ACTION)){
			//Intent中ConnectivityManager.EXTRA_NO_CONNECTIVITY这个关键字表示着当前是否连接上了网络
		    //true 代表网络断开   false 代表网络没有断开
			ApplicationEnvironment.getInstance().isNetworkAvailable = !intent.getBooleanExtra(ConnectivityManager.EXTRA_NO_CONNECTIVITY, false);
		}
		
	}

}
