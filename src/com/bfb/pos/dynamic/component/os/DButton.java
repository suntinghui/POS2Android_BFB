package com.bfb.pos.dynamic.component.os;

import java.util.Vector;

import com.bfb.pos.dynamic.component.Component;
import com.bfb.pos.dynamic.core.ViewPage;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.Button;

public class DButton extends Button {
	
	private String gotoPage;
	private String action;
	private Vector<String> paramVector;
	
	
	public DButton(Context context) {
		super(context);
	}

	public DButton(Context context, AttributeSet attrs) {
		super(context, attrs);
	}


	public String getGotoPage() {
		return gotoPage;
	}

	public void setGotoPage(String gotoPage) {
		this.gotoPage = gotoPage;
	}
	
	public String getAction() {
		return action;
	}

	public void setAction(String action) {
		this.action = action;
	}

	public Vector<String> getParam(){
		return paramVector;
	}
	
	public void setParam(String param){
		if(null == paramVector){
			paramVector = new Vector<String>();
		}
		
		paramVector.add(param);
	}
}
