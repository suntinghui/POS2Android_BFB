package com.bfb.pos.client.exception;

public class HttpException extends Exception {

	private static final long serialVersionUID = -5428667955683030083L;
	
	public HttpException(String errorMsg){
		super(errorMsg);
	}
	
	public HttpException(int errorCode){
		super(getErrorMsg(errorCode));
	}
	
	public HttpException(int errorCode, String systemReason){
		super(getErrorMsg(errorCode) + "(" +systemReason+")");
	}
	
	private static String getErrorMsg(int code){
		switch(code){
		case 404:
			return "请求链接无效(404)";
			
		case 500:
			return "服务器异常，请与客服联系(500)";
			
		case 900:
			return "网络传输协议出错";
			
		case 901:
			return "连接超时,请重试";
			
		case 902:
			return "网络中断，请重试";
			
		case 903:
			//return "网络数据流传输出错";
			return "连接服务器超时，请重试";
			
			default:
			return "未知错误("+code+"),请重试或与客服联系";
			
		}
	}
}
