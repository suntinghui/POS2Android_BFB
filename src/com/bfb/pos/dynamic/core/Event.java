package com.bfb.pos.dynamic.core;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Set;

import android.app.Activity;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.bfb.pos.activity.BaseActivity;
import com.bfb.pos.agent.client.ApplicationEnvironment;
import com.bfb.pos.agent.client.TransferLogic;
import com.bfb.pos.agent.client.TransferPacketThread;
import com.bfb.pos.dynamic.component.Component;
import com.bfb.pos.dynamic.component.Input;
import com.bfb.pos.dynamic.component.Param;
import com.bfb.pos.dynamic.component.ViewException;
import com.bfb.pos.dynamic.component.os.OSSelect;
import com.bfb.pos.dynamic.parse.ParseView;
import com.bfb.pos.fsk.FSKOperator;
import com.bfb.pos.util.AssetsUtil;
import com.bfb.pos.util.StringUtil;

public class Event {
	
	private String actionId;
	private String fsk;
	private String method;
	private String action;
	private String actionType;
	private String transfer;
	private HashMap<String, Param> params;
	private HashMap<String,String> map ;
	private String nextPageId;
	private String backPageId;
	private String exceptionPageId;
	private Integer goHistory;
	public ViewPage viewPage;
	private HashMap<String, String> staticActivityDataMap = null; // 用于存放从静态界面传递过来的值
	private ViewPage nextPage;
	
	public Event(ViewPage viewPage, String actionId, String action, String... paramIDs) {
		this.actionId = actionId;
		this.action   = action;
		this.viewPage = viewPage;
		params = new HashMap<String, Param>();
		map = new HashMap<String,String>();
		for(String paramID:paramIDs) {
			this.addAParam(paramID);
		}
	}
	
	public Event clone(ViewPage viewPage) {
		String[] ps = new String[this.params.size()];
		int i =0;
		for(String key:this.params.keySet()) {
			ps[i++] = key;
		}
		Event event = new Event(viewPage, this.actionId, this.action, ps);
		event.setMethod(this.method);
		event.setFsk(this.fsk);
		event.setActionType(this.actionType);
		event.setTransfer(this.transfer);
		event.setNextPage(this.nextPageId);
		event.setBackPage(this.backPageId);
		event.setExceptionPage(this.exceptionPageId);
		event.setGoHistory(this.goHistory);
		return event;
	}
	
	public String getActionId() {
		return actionId;
	}

	public void setActionId(String actionId) {
		this.actionId = actionId;
	}

	public String getAction() {
		return action;
	}

	public void setAction(String action) {
		this.action = (null==action? null : action.trim());
	}
	
	public void setFsk(String fsk){
		this.fsk = fsk;
	}
	
	public String getFsk(){
		return this.fsk;
	}
	
	public String getMethod(){
		return this.method;
	}
	
	public void setMethod(String method){
		this.method = method;
	}

	public String getActionType() {
		return actionType;
	}

	public void setActionType(String actionType) {
		this.actionType = actionType;
	}
	
	public String getTransfer(){
		return transfer;
	}
	
	public void setTransfer(String transfer){
		this.transfer = transfer;
	}
	
	public void setStaticActivityDataMap(HashMap<String, String> map){
		this.staticActivityDataMap = map;
	}
	
	public String getNextPage() {
		return nextPageId;
	}
	public void setNextPage(String nextPage) {
		this.nextPageId = nextPage;
	}
	public String getBackPage() {
		return backPageId;
	}
	public void setBackPage(String backPage) {
		this.backPageId = backPage;
	}
	public String getExceptionPage() {
		return exceptionPageId;
	}
	public void setExceptionPage(String exceptionPage) {
		this.exceptionPageId = exceptionPage;
	}
	public void addAParam(String paramId) {
		params.put(paramId, new Param(viewPage, paramId));
	}
	public Set<String> getParamSet() {
		return this.params.keySet();
	}
	public Param getParam(String key) {
		return this.params.get(key);
	}
	public Integer getGoHistory() {
		return goHistory;
	}
	public void setGoHistory(Integer goHistory) {
		this.goHistory = goHistory;
	}
	public ViewPage getViewPage() {
		return this.viewPage;
	}
	
	public void trigger() throws ViewException, IOException {
		try{
			nextPage = this.getViewPage();
			
			// 关闭界面，显示菜单界面
			if (null != this.getActionType() && this.actionType.equalsIgnoreCase(ParseView.ACTION_TYPE_CLOSE)) {
				ViewContext.getInstance().removeViewPage(nextPage);
				BaseActivity.getTopActivity().setResult(Activity.RESULT_CANCELED);
				BaseActivity.getTopActivity().finish();
				return;
			}
			
			if (null != this.getActionType() && this.actionType.equalsIgnoreCase(ParseView.ACTION_TYPE_FINISH)) {
				ViewContext.getInstance().removeViewPage(nextPage);
				BaseActivity.getTopActivity().setResult(Activity.RESULT_OK);
				BaseActivity.getTopActivity().finish();
				return;
			}
			
			// **如果几个动态界面之间向前关闭到其中某个界面的时候用此属性，而不要用finish并setResult的方法。**
			// goHistory还是调用的startActivityForResult，但是Dynamic是singleTop模式，所以还是只有这一个Activity。
			if(null != this.getGoHistory()) {
				for (int i=0; i<-this.getGoHistory(); i++) {
					nextPage = viewPage.getPrePage(); 
					ViewContext.getInstance().removeViewPage(viewPage);
					
					viewPage = nextPage;
				}
				
				this.gotoDynamicActivity(viewPage);
				return;
			}
			
			
			// 在这里检查输入项是否合适？Toast
			// 除了关闭界面或返回界面即离开此界面的行为都需要检查输入项。
			// fsk或method或transfer可能不需要检查输入项，但不能等到以上各项都完成后再检查输入项不合格会再次触发以上行为
			
			if (!this.loadInputAndvalidator())
				return;
			
				
			if (null != this.fsk){
				Handler handler = new Handler(){
					@Override
					public void handleMessage(Message msg) {
						switch(msg.what){
						case 0:
							triggerPart1();
							break;
						}
					}
				};
				
				FSKOperator.execute(this.fsk, handler);
				
			} else {
				this.triggerPart1();
			}
			
		}catch(Exception e){
			e.printStackTrace();
		}

	}
	
	private void triggerPart1(){
		try{
			// 通过反射机制执行指定的方法
			if (null != this.method){
				String[] fields = this.method.split("\\|");
				if (fields.length == 3){
					this.invokeMethod(fields[0], fields[1], this.parseMethodArgs(fields[2]));
					
				} else {
					Log.e("event method","event property 'method' must be format: (className|methodName|argType:componentId,argType:componentId...)");
					Log.e("event method","or event property 'method' must be format: (className|methodName|null)");
				}
			}
			
			//////////////////////////////////////////////////////////////////////////////////////
			
			map.put("actionId", this.action);
			for(String key : this.getParamSet()){
				Object value = this.getParam(key).getValue();
				map.put(key, value.toString());
			}
			
			if (null != this.staticActivityDataMap){
				for (String key : this.staticActivityDataMap.keySet()){
					map.put(key, this.staticActivityDataMap.get(key));
				}
			}
			 
			// 8583交易
			// 如果有指定的action，则进行完transfer指定的交易后继续进行动态处理，也就是只是简单的进行界面跳转展示数据。
			// 如果没有指定action。则到TransferLogic类中进行特殊处理。
			if (null != this.getTransfer()){
				if (null != this.getAction() && !"".equals(this.getAction())){
					Handler transferHandler = new Handler(){
						@SuppressWarnings("unchecked")
						@Override
						public void handleMessage(Message msg) {
							// 只能处理且只用处理正确的消息
							switch(msg.what){
							case 0:
								triggerPart2((HashMap<String, String>)msg.obj);
								break;
							}
						}
					};
					
					/**进行逻辑处理**/
					TransferPacketThread thread = new TransferPacketThread(this.getTransfer(), map, transferHandler);
					thread.start();
					
				} else{
					TransferLogic.getInstance().transferAction(this.getTransfer(), map);
				}
				
			} else {
				this.triggerPart2(null);
			}
			
		} catch(Exception e){
			e.printStackTrace();
		}
	}
	
	private void triggerPart2(HashMap<String, String> receMap){
		try{
			if (null != receMap){
				for (String key : receMap.keySet()){
					map.put(key, receMap.get(key));
				}
			}
			
			// 跳转到非动态Acitvity，注意以下几项的处理顺序
			if (null != this.getActionType() && this.actionType.equalsIgnoreCase(ParseView.ACTION_TYPE_LOCAL)) {
				if ("".equals(this.getAction()) || null == this.getAction())
					return;
				
				Intent intent = new Intent(ApplicationEnvironment.getInstance().getApplication().getPackageName() + "." + this.getAction());
				intent.putExtra("map", map); // 向静态界面传递数据
				BaseActivity.getTopActivity().startActivityForResult(intent, 0);
				
				return;
			}
			
			if (null != this.getAction() && !"".equals(this.getAction())){
				// 联网处理交易
				if (null != this.getActionType() && this.actionType.equalsIgnoreCase(ParseView.ACTION_TYPE_SUBMIT)){
					InputStream inputStream = new ByteArrayInputStream(ApplicationEnvironment.getInstance().netClient.transferMsg(map));
					
					nextPage = ParseView.parseXML(this.getViewPage(), StringUtil.inputStreamToString(inputStream));
					
				} else {
					InputStream inputStream = null;
					try{
						inputStream = AssetsUtil.getInputStreamFromPhone(this.action);
			            
			            nextPage = ParseView.parseXML(this.getViewPage(), StringUtil.inputStreamToString(inputStream));
					} finally{
						try {
							if (null!= inputStream)
								inputStream.close();
						} catch (IOException e) {
							e.printStackTrace();
						}
					}
		            
		            // 加载静态的Activity传递的数据。
		            if (null != this.map){
		            	for (String key : this.map.keySet()){
		            		ParseView.setScopeData(nextPage, key, this.map.get(key));
		            	}
		            }
		            
				}
				
				this.gotoDynamicActivity(nextPage);
			}
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	private void gotoDynamicActivity(ViewPage vp){
		// 显示界面
		Intent intent = new Intent(ApplicationEnvironment.getInstance().getApplication().getPackageName() + ".dynamic");
        //intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP); // 解决 StartActivityForResult 与 signleTop 不起作用
		intent.putExtra("viewPageId", vp.getPageId());
        BaseActivity.getTopActivity().startActivityForResult(intent, 0);
	}
	
	// 检查输入项
	private boolean loadInputAndvalidator() throws ViewException {
		if (null != this.getViewPage()){
			for(int i=0;i<this.getViewPage().getViewIndex().size();i++){
				Component com = this.getViewPage().getComponent(this.getViewPage().getViewIndex().get(i));
				if(com instanceof Input){
					// 加载用户输入值至控件
					((Input) com).loadInputValue();
					
					// 或利用正则表达式验证用户输入
					if (!((Input) com).validator()){
						return false;
					}
					
				} else if (com instanceof OSSelect){
					if (!((OSSelect) com).validator()){
						return false;
					}
				}
			}
			
			return true;
		}
		
		return true;
	}
	
	private void invokeMethod(String className, String methodName, Object[] argsObject){
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
			
			Class<?> classType = Class.forName(className);
			Method method = classType.getMethod(methodName, argsClass);
			method.invoke(null, argsObject);
			
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
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		
	}
	
	private Object[] parseMethodArgs(String args){
		try{
			if (null == args || "".equals(args) || "null".equals(args))
				return null;
			
			String[] argArray = args.split(",");
			Object[] argsObject = new Object[argArray.length];
			
			int i=0;
			for (String arg : argArray){
				String[] temp = arg.split(":");
				if ("int".equalsIgnoreCase(temp[0]) || "integer".equalsIgnoreCase(temp[0])){
					argsObject[i++] = Integer.valueOf((String)this.viewPage.getComponent(temp[1]).getValue());
				} else if ("string".equalsIgnoreCase(temp[0])){
					argsObject[i++] = (String)this.viewPage.getComponent(temp[1]).getValue();
				} else if ("bool".equalsIgnoreCase(temp[0]) || "boolean".equalsIgnoreCase(temp[0])){
					argsObject[i++] = Boolean.valueOf((String)this.viewPage.getComponent(temp[1]).getValue()); // must be "true" is true
				}
			}
			return argsObject;
		}catch(Exception e){
			e.printStackTrace();
		}
		
		return null;
	}
	
}
