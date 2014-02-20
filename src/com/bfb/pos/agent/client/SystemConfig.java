package com.bfb.pos.agent.client;

import com.bfb.pos.util.StringUtil;

public class SystemConfig {
	
	// 是否由商户的短信发送交易信息
	private static boolean sendSMS 					= false;
	private static int pageSize 					= 40;
	private static int historyInterval				= 30;
	private static int maxReversalCount 			= 3;
	private static int maxUploadSignImageCount		= 3; // 其实-1
	private static int maxTransferTimeout 			= 60;
	private static int maxLockTimeout				= 20;
	
	public static boolean isSendSMS() {
		return sendSMS;
	}

	public static int getPageSize() {
		return pageSize;
	}
	
	public static int getHistoryInterval(){
		return historyInterval;
	}

	public static int getMaxReversalCount() {
		return maxReversalCount;
	}
	
	public static int getMaxUploadSignImageCount() {
		return maxUploadSignImageCount;
	}

	public static int getMaxTransferTimeout() {
		return maxTransferTimeout;
	}
	
	public static int getMaxLockTimeout(){
		return maxLockTimeout;
	}
	

	public static void setSendSMS(String flag){
		if("true".equals(flag.trim())){
			sendSMS = true;
		} else {
			sendSMS = false;
		}
	}
	
	public static void setPageSize(String str){
		if (StringUtil.isNumeric(str)){
			pageSize = Integer.parseInt(str);
		} 
	}
	
	public static void setHistoryInterval(String str){
		if (StringUtil.isNumeric(str)){
			historyInterval = Integer.parseInt(str);
		}
	}
	
	public static void setMaxReversalCount(String value){
		if (StringUtil.isNumeric(value)){
			maxReversalCount = Integer.parseInt(value);
		}
	}
	
	public static void setMaxTransferTimeout(String value){
		if (StringUtil.isNumeric(value)){
			maxTransferTimeout = Integer.parseInt(value);
		}
	}
	
	public static void setMaxLockTimeout(String value){
		if (StringUtil.isNumeric(value)){
			maxLockTimeout = Integer.parseInt(value);
		}
	}
	
	public static void setMaxUploadSignImageCount(String value){
		if (StringUtil.isNumeric(value)){
			maxUploadSignImageCount = Integer.parseInt(value);
		}
	}

}
