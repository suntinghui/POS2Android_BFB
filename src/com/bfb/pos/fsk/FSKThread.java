package com.bfb.pos.fsk;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.bfb.pos.activity.BaseActivity;
import com.bfb.pos.agent.client.AppDataCenter;
import com.bfb.pos.agent.client.ApplicationEnvironment;
import com.itron.protol.android.CommandReturn;

public class FSKThread extends Thread{
	
	private static final byte[] check_key=new byte[]{(byte)0xD5,(byte)0x2A,(byte)0x09,(byte)0x2C,(byte)0xF0,(byte)0x12,(byte)0xDD,(byte)0x0A};
	private CommandControllerEx commandController;
	
	private String fskStr;
	private Handler handler;
	
	/**
	 * 
	 * !!!!!!!!!!!!!!!!!!!!!-----  此类没有使用，采用Service的形式来实现   ---------!!!!!!!!!!!!!!!!!!!!!!
	 * 
	 * 执行线程取得的值，如果操作点付宝时只执行一个操作，可以通过msg.obj来强转为CommandReturn来取值
	 * 如果一次执行多个操作，则必须要通过AppDataCenter来取值
	 * 为避免可能的错误发生及操作的统一性，建议通过AppDataCenter
	 * 
	 * @param fskStr format: (methodName|argType:value,argType:vlaue...) or (methodName|null)
	 * @param handler
	 */
	
	@Deprecated
	public FSKThread(String fskStr, Handler handler){
		this.fskStr = fskStr;
		this.handler = handler;
	}
	
	@Override
	public void run() {
		BaseActivity.getTopActivity().showDialog(BaseActivity.PROGRESS_DIALOG, "正在操作刷卡设备，请保持连接");
		
		FSKStateChangeListener listener = new FSKStateChangeListener();
		commandController = new CommandControllerEx(ApplicationEnvironment.getInstance().getApplication(), listener);
		commandController.Init(check_key);
		
		CommandReturn cmdReturn = null;
		
		boolean flag = commandController.isDevicePresent();
		if (flag){
			String[] fsks = this.fskStr.split("#");
			for (String aFsk : fsks){
				final String[] fields = aFsk.split("\\|");
				if (fields.length == 2){
					if (fields[0].equals("Get_MAC")){ // 可能不及格，先这样。
						BaseActivity.getTopActivity().showDialog(BaseActivity.PROGRESS_DIALOG, "正在加密数据...");
					}else if(fields[0].equals("Get_CheckMAC")){
						BaseActivity.getTopActivity().showDialog(BaseActivity.PROGRESS_DIALOG, "正在校验MAC...");
					}
					
					cmdReturn = invokeMethod(fields[0], parseArgs(fields[1]));
		        	AppDataCenter.setFSKArgs(cmdReturn);
		        	
				} else {
					Log.e("event method","event property 'fsk' must be format: (methodName|argType:value,argType:vlaue...)");
					Log.e("event method","or event property 'fsk' must be format: (methodName|null)");
				}
			}
			
			BaseActivity.getTopActivity().hideDialog(BaseActivity.PROGRESS_DIALOG);
			
        	Message message = new Message();
			message.what = 0;
			message.obj = cmdReturn;
			message.setTarget(this.handler);
			message.sendToTarget();
		} else {
			BaseActivity.getTopActivity().hideDialog(BaseActivity.PROGRESS_DIALOG);
			BaseActivity.getTopActivity().showDialog(BaseActivity.MODAL_DIALOG, "刷卡设备检测错误，请检查设备是否插入并开机或重新插拔后再试");
		}
		
		commandController.ReleaseDevice();
	}


	private CommandReturn invokeMethod(String methodName, Object[] argsObject){
		try {
			Class[] argsClass =  null;
			
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
			
			Class cmdCtlExClass = commandController.getClass();
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

}
