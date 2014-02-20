package com.bfb.pos.util;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.telephony.SmsManager;
import android.telephony.TelephonyManager;
import android.widget.Toast;

import com.bfb.pos.activity.BaseActivity;
import com.bfb.pos.agent.client.ApplicationEnvironment;

public class PhoneUtil {
	public static String getPhoneNum(){
		String phoneNum = (String)((TelephonyManager) ApplicationEnvironment.getInstance().getApplication().getSystemService(Context.TELEPHONY_SERVICE)).getLine1Number();
		
		if (null != phoneNum){
			if (phoneNum.startsWith("+86")){
				return phoneNum.replace("+86", "");
			} else if (phoneNum.startsWith("+086"))
				return phoneNum.replace("+086", "");
			
			return phoneNum;
		}
		
		return "";
		
	}
	
	public static String getIMEI(){
		String IMEI = (String)((TelephonyManager) ApplicationEnvironment.getInstance().getApplication().getSystemService(Context.TELEPHONY_SERVICE)).getDeviceId();
		if (null == IMEI)
			return "000000000000000";
		return IMEI;
	}
	
	public static String getIMSI(){
		String IMSI = (String)((TelephonyManager) ApplicationEnvironment.getInstance().getApplication().getSystemService(Context.TELEPHONY_SERVICE)).getSubscriberId();
		if (null == IMSI)
			return "";
		return IMSI;
	}
	
	public static void sendSMS(String phoneNum, String smsCnt) {
		try{
			SmsManager smsManager = SmsManager.getDefault();
			smsManager.sendTextMessage(phoneNum, null, smsCnt, null, null);
		} catch(Exception e){
			e.printStackTrace();
			BaseActivity.getTopActivity().runOnUiThread(new Runnable(){
				@Override
				public void run() {
					Toast.makeText(BaseActivity.getTopActivity(), "发送短信失败", Toast.LENGTH_SHORT).show();
				}
				
			});
		}
		
	}
	
	public static void sendMMS(String phoneNum){
		Intent sendIntent = new Intent(Intent.ACTION_SEND,  Uri.parse("mms://"));  
	    sendIntent.setType("image/jpeg");  
	    sendIntent.putExtra("subject", "交易签单");
	    sendIntent.putExtra("sms_body", "已成功完成交易，附上信息签名，请注意查收保存");
	    sendIntent.putExtra("address", phoneNum);
	    String url = "file://mnt//sdcard//image//123456.JPEG";  
	    sendIntent.putExtra(Intent.EXTRA_STREAM, Uri.parse(url));  
	    ApplicationEnvironment.getInstance().getApplication().startActivity(Intent.createChooser(sendIntent, "MMS:"));
	    
	}
	
}
