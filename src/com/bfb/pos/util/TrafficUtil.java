package com.bfb.pos.util;

import java.util.HashMap;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.net.ConnectivityManager;
import android.util.Log;

import com.bfb.pos.agent.client.AppDataCenter;
import com.bfb.pos.agent.client.ApplicationEnvironment;
import com.bfb.pos.agent.client.Constant;

public class TrafficUtil {
	
	public static int TYPE_SEND 			= 1;
	public static int TYPE_RECEIVE 			= 2;

	public static TrafficUtil instance = null;
	private static ConnectivityManager connManager = null;
	
	public static TrafficUtil getInstance(){
		if (null == instance){
			instance = new TrafficUtil();
		}
		
		return instance;
	}
	
	public void initTraffic(){
		SharedPreferences pre = ApplicationEnvironment.getInstance().getPreferences();
		String trafficMonth = pre.getString(Constant.TRAFFIC_MONTH, "01");
		String trafficDay = pre.getString(Constant.TRAFFIC_DAY, "01");
		
		String date = AppDataCenter.getValue("__yyyy-MM-dd");
		
		if (date.length() == 4){
			if (!date.substring(0, 2).equals(trafficMonth)){ // month
				Editor editor = ApplicationEnvironment.getInstance().getPreferences().edit();
				editor.putLong(Constant.MONTH_MOBILESEND, 0);
				editor.putLong(Constant.MONTH_MOBILESRECEIVE, 0);
				editor.putLong(Constant.MONTH_WIFISEND, 0);
				editor.putLong(Constant.MONTH_WIFIRECEIVE, 0);
				editor.commit();
			}
			
			if (!date.substring(2, 4).equals(trafficDay)){ // day
				Editor editor = ApplicationEnvironment.getInstance().getPreferences().edit();
				editor.putLong(Constant.DAY_MOBILESEND, 0);
				editor.putLong(Constant.DAY_MOBILESRECEIVE, 0);
				editor.putLong(Constant.DAY_WIFISEND, 0);
				editor.putLong(Constant.DAY_WIFIRECEIVE, 0);
				editor.commit();
			}
		}
		
	}
	
	
	/**
	 * @return 以MAP的数据形式返回流量统计值，key 取值： WIFISEND or WIFiRECEIVE or MOBILESEND or MOBILESRECEIVE
	 */
	public HashMap<String, Long> getTraffic(){
		SharedPreferences pre = ApplicationEnvironment.getInstance().getPreferences();
		
		HashMap<String, Long> trafficMap = new HashMap<String, Long>();
		
		// month
		trafficMap.put(Constant.MONTH_WIFISEND, pre.getLong(Constant.MONTH_WIFISEND, 0));
		trafficMap.put(Constant.MONTH_WIFIRECEIVE, pre.getLong(Constant.MONTH_WIFIRECEIVE, 0));
		trafficMap.put(Constant.MONTH_MOBILESEND, pre.getLong(Constant.MONTH_MOBILESEND, 0));
		trafficMap.put(Constant.MONTH_MOBILESRECEIVE, pre.getLong(Constant.MONTH_MOBILESRECEIVE, 0));
		
		// day
		trafficMap.put(Constant.DAY_WIFISEND, pre.getLong(Constant.DAY_WIFISEND, 0));
		trafficMap.put(Constant.DAY_WIFIRECEIVE, pre.getLong(Constant.DAY_WIFIRECEIVE, 0));
		trafficMap.put(Constant.DAY_MOBILESEND, pre.getLong(Constant.DAY_MOBILESEND, 0));
		trafficMap.put(Constant.DAY_MOBILESRECEIVE, pre.getLong(Constant.DAY_MOBILESRECEIVE, 0));
		
		return trafficMap;
	}
	
	/**
	 * 
	 * @param type Type_Send or Type_Receive
	 * @param length 数据长度
	 */
	public void setTraffic(int type , long length){
		try{
			long startTime = System.currentTimeMillis();
			
			int nType = getConnectivityManager().getActiveNetworkInfo().getType();
			
			long monthTotalLength = 0;
			long dayTotalLength = 0;
			
			SharedPreferences pre = ApplicationEnvironment.getInstance().getPreferences();
			Editor editor = pre.edit();
			
			if (nType == ConnectivityManager.TYPE_MOBILE){
				monthTotalLength = pre.getLong(type==TYPE_SEND?Constant.MONTH_MOBILESEND:Constant.MONTH_MOBILESRECEIVE, 0);
				editor.putLong(type==TYPE_SEND?Constant.MONTH_MOBILESEND:Constant.MONTH_MOBILESRECEIVE, monthTotalLength + length);
				
				dayTotalLength = pre.getLong(type==TYPE_SEND?Constant.DAY_MOBILESEND:Constant.DAY_MOBILESRECEIVE, 0);
				editor.putLong(type==TYPE_SEND?Constant.DAY_MOBILESEND:Constant.DAY_MOBILESRECEIVE, dayTotalLength + length);
				
			} else{
				monthTotalLength = pre.getLong(type==TYPE_SEND?Constant.MONTH_WIFISEND:Constant.MONTH_WIFIRECEIVE, 0);
				editor.putLong(type==TYPE_SEND?Constant.MONTH_WIFISEND:Constant.MONTH_WIFIRECEIVE, monthTotalLength + length);
				
				dayTotalLength = pre.getLong(type==TYPE_SEND?Constant.DAY_WIFISEND:Constant.DAY_WIFIRECEIVE, 0);
				editor.putLong(type==TYPE_SEND?Constant.DAY_WIFISEND:Constant.DAY_WIFIRECEIVE, dayTotalLength + length);
			}
			
			editor.commit();
			
			long endTime = System.currentTimeMillis();
			
			Log.e("记录流量所用时间", endTime-startTime + " ms");
			
		}catch(Exception e){
			// 出现异常不做处理
			e.printStackTrace();
		}
	}
	
	private ConnectivityManager getConnectivityManager(){
		if (null == connManager){
			connManager = (ConnectivityManager) ApplicationEnvironment.getInstance().getApplication().getSystemService(Context.CONNECTIVITY_SERVICE);
		}
		
		return connManager;
	}
	
	
}
