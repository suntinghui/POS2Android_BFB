package com.bfb.pos.fsk;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;

import com.bfb.pos.activity.BaseActivity;
import com.bfb.pos.agent.client.AppDataCenter;
import com.bfb.pos.agent.client.Constant;
import com.itron.android.lib.Logger;
import com.itron.protol.android.CommandReturn;

public class FSKService extends Service {
	
	Logger logger = Logger.getInstance(FSKService.class);
	
	public static final byte[] check_key=new byte[]{(byte)0xD5,(byte)0x2A,(byte)0x09,(byte)0x2C,(byte)0xF0,(byte)0x12,(byte)0xDD,(byte)0x0A};
	
	public static final int RESULT_SUCCESS 				= 0; // 成功
	public static final int RESULT_FAILED_ENCRYPT 		= 1; // 加密失败
	public static final int RESULT_FAILED_CHECKMAC 		= 2;// 校验mac失败
	
	private static final int MAX_EXECCOUNT				= 2;
	private static int execCount						= 0;
	
	private CommandControllerEx commandController = null;
	
	private String fskCommand = null;
	
	@Override
	public void onCreate() {
		super.onCreate();
		
		FSKStateChangeListener listener = new FSKStateChangeListener();
		commandController = new CommandControllerEx(this, listener);
		commandController.Init(check_key);
		
		// TODO 生产版本要改成False
		logger.setDebug(false);
	}

	@Override
	public IBinder onBind(Intent arg0) {
		return null;
	}
	
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		fskCommand = intent.getStringExtra("FSKCOMMAND");
		
		if(null != fskCommand && !"".equals(fskCommand)){
			FSKThread fskThread = new FSKThread();
			fskThread.start();
		}
		
		// 使用START_NOT_STICKY返回值时，如果在执行完onStartCommand后，服务被异常kill掉，系统不会自动重启该服务。
		return START_NOT_STICKY;
	}

	// 在2.0以后的版本如果重写了onStartCommand，那onStart将不会被调用，注：在2.0以前是没有onStartCommand方法
	@Override
	public void onStart(Intent intent, int startId) {
		super.onStart(intent, startId);
	}
	
	class FSKThread extends Thread{
		@Override
		public void run() {
			doAction();
		}
	}
	
	private void doAction(){
		++execCount;
		
		try{
			CommandReturn cmdReturn = null;
			
			String[] fsks = fskCommand.split("#");
			for (String aFsk : fsks){
				final String[] fields = aFsk.split("\\|");
				if (fields.length == 2){
					if(Constant.isUploadSalesSlip){
						//上传签购单时什么也不做  静默上传
					}else{
						BaseActivity.getTopActivity().showDialog(BaseActivity.PROGRESS_DIALOG, getTips(fields[0]));						
					}
					
					cmdReturn = invokeMethod(fields[0], parseArgs(fields[1]));
					
					if(cmdReturn.Return_Result == (byte)0x00){
						// 操作成功
						AppDataCenter.setFSKArgs(cmdReturn); // 设置数据至AppDataCenter
						
					}else if (cmdReturn.Return_Result == (byte)0x01){
						// 执行命令超时
//						if (execCount < MAX_EXECCOUNT){
//							doAction();
//						} else {
//							BaseActivity.getTopActivity().showDialog(BaseActivity.MODAL_DIALOG, "响应超时:请检查点付宝是否插入并开机或重新插拔刷卡设备");
//							return;
//						}
						
						BaseActivity.getTopActivity().showDialog(BaseActivity.MODAL_DIALOG, "响应超时，请检查刷卡设备是否插入并开机或重新插拔刷卡设备");
						return;
						
					} else if(cmdReturn.Return_Result == (byte)0x0A){
						// 用户在点付宝上按下取消键
						BaseActivity.getTopActivity().showDialog(BaseActivity.NONMODAL_DIALOG, "用户取消操作");
						return;
						
					} else if(cmdReturn.Return_Result == (byte)0x0B){
						// Mac校验失败
						Message message = new Message();
						message.what = RESULT_FAILED_CHECKMAC; 
						message.obj = cmdReturn;
						message.setTarget(FSKOperator.fskHandler);
						message.sendToTarget();
						return;
						
					} else {
						BaseActivity.getTopActivity().showDialog(BaseActivity.NONMODAL_DIALOG, getErrorMsg(cmdReturn.Return_Result));
						return;
					}
					
				} else {
					BaseActivity.getTopActivity().hideDialog(BaseActivity.PROGRESS_DIALOG);
					Log.e("event method","event property 'fsk' must be format: (methodName|argType:value,argType:vlaue...)");
					Log.e("event method","or event property 'fsk' must be format: (methodName|null)");
					return;
				}
			}
			
			BaseActivity.getTopActivity().hideDialog(BaseActivity.PROGRESS_DIALOG);
			
        	Message message = new Message();
			message.what = 0; // 肯定是成功的
			message.obj = cmdReturn;
			message.setTarget(FSKOperator.fskHandler);
			message.sendToTarget();
			
		} catch(Exception e){
			e.printStackTrace();
			if (execCount < MAX_EXECCOUNT){
				doAction();
			} else {
				BaseActivity.getTopActivity().showDialog(BaseActivity.MODAL_DIALOG, "刷卡设备操作失败，请重试");
			}
			
		} finally{
			execCount = 0;
		}
	}
	
	private CommandReturn invokeMethod(String methodName, Object[] argsObject){
		try {
			Class<?>[] argsClass =  null;
			
			if (null != argsObject){
				argsClass = new Class[argsObject.length];
				int i=0;
				// 需要注意的是不能使用包装类替换基本类型，比如不能使用Integer.class代替int.class
				for (Object obj : argsObject){
					if (obj instanceof Integer)
						argsClass[i++] = int.class;
					else if (obj instanceof Boolean)
						argsClass[i++] = boolean.class;
					else if (obj instanceof String)
						argsClass[i++] = String.class;
					
				}
			}
			
			Class<?> cmdCtlExClass = commandController.getClass();
			Method method = cmdCtlExClass.getMethod(methodName, argsClass);
			return (CommandReturn) method.invoke(commandController, argsObject);
			
		} catch (SecurityException e) {
			e.printStackTrace();
		} catch (NoSuchMethodException e) {
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		}
		
		return null;
	}
	
	private Object[] parseArgs(String args){
		if (null == args || args.trim().equals("") || args.trim().equals("null"))
			return null;
		
		String[] argArray = args.split(",");
		Object[] argsObject = new Object[argArray.length];
		
		int i=0;
		for (String arg : argArray){
			String[] temp = arg.split(":");
			
			if (temp[1].trim().startsWith("__"))
				temp[1] = AppDataCenter.getValue(temp[1]);
			
			if ("int".equalsIgnoreCase(temp[0]) || "integer".equalsIgnoreCase(temp[0])){
				argsObject[i++] = Integer.valueOf(temp[1]);
			} else if ("string".equalsIgnoreCase(temp[0])){
				argsObject[i++] = temp[1];
			} else if ("bool".equalsIgnoreCase(temp[0]) || "boolean".equalsIgnoreCase(temp[0])){
				argsObject[i++] = Boolean.valueOf(temp[1]); // must be "true" is true
			}
		}
		return argsObject;
	}
	
	/**
	 * 根据点付宝不同的操作，提示不同的信息
	 * 
	 * @param methodName 通过方法名来区分操作类型
	 * @return
	 */
	private String getTips(String methodName){
		if ("Get_PsamNo".equals(methodName) || "Get_VendorTerID".equals(methodName)){
			return "正在读取商户终端信息";
		} else if("Get_MAC".equals(methodName)){
			return "正在加密发送报文...";
		} else if("Get_CheckMAC".equals(methodName)){
			return "正在校验响应报文...";
		} else if("Get_RenewKey".equals(methodName)){
			return "正在写入工作密钥";
		} else if("Set_PtrData".equals(methodName)){
			return "正在打印凭条，请稍候";
		}
		/*
		else if("Get_CardTrack".equals(methodName)){
			return "正在读取银行卡磁道数据";
		} else if("Get_PIN".equals(methodName)){
			return "正在加密银行卡密码";
		}
		*/
		
		return "正在操作设备，请保持连接";
	}
	
	// 应答码表
	private String getErrorMsg(byte errorCode) {
		// 当应答结果不是00或者FX时，表示PSAM卡操作失败
		if (errorCode == (byte) 0x01)
			return "执行命令超时";
		if (errorCode == (byte) 0x02)
			return "PSAM卡认证失败";
		if (errorCode == (byte) 0x03)
			return "PSAM卡上电失败或者不存在";
		if (errorCode == (byte) 0x04)
			return "PSAM卡操作失败";
		if (errorCode == (byte) 0x0A)
			return "用户退出";
		if (errorCode == (byte) 0x0B)
			return "MAC校验失败";
		if (errorCode == (byte) 0x0C)
			return "终端加密失败";
		if (errorCode == (byte) 0x0F)
			return "PSAM卡状态异常";
		if (errorCode == (byte) 0x20)
			return "不匹配的主命令码";
		if (errorCode == (byte) 0x21)
			return "不匹配的子命令码";
		if (errorCode == (byte) 0x50)
			return "获取电池电量失败";
		if (errorCode == (byte) 0x80)
			return "数据接收正确";
		if (errorCode == (byte) 0xE0)
			return "重传数据无效";
		if (errorCode == (byte) 0xE1)
			return "终端设置待机信息失败";
		// 如果为FX的话，表示下行(即手机至密键)数据格式错误
		if (errorCode == (byte) 0xF0)
			return "不识别的包头";
		if (errorCode == (byte) 0xF1)
			return "不识别的主命令码";
		if (errorCode == (byte) 0xF2)
			return "不识别的子命令码";
		if (errorCode == (byte) 0xF3)
			return "该版本不支持此指令";
		if (errorCode == (byte) 0xF4)
			return "随机数长度错误";
		if (errorCode == (byte) 0xF5)
			return "不支持的部件";
		if (errorCode == (byte) 0xF6)
			return "不支持的模式";
		if (errorCode == (byte) 0xF7)
			return "数据域长度错误";
		if (errorCode == (byte) 0xFC)
			return "数据域内容有误";
		if (errorCode == (byte) 0xFD)
			return "终端ID错误";
		if (errorCode == (byte) 0xFE)
			return "MAC_TK校验失败";
		if (errorCode == (byte) 0xFF)
			return "校验和错误";

		return "未知错误！！！"; // 如果未开启点付宝。。。
	}



}
