package com.bfb.pos.model;

import java.util.ArrayList;

public class TransferModel {

	private boolean shouldMac;
	private boolean isJson;
	
	private ArrayList<FieldModel> fieldList;
	
	public TransferModel(){
		fieldList = new ArrayList<FieldModel>();
	}

	public boolean shouldMac() {
		return shouldMac;
	}

	public boolean isJson() {
		return isJson;
	} 
	
	public void setShouldMac(String shouldMac) {
		if ("true".equalsIgnoreCase(shouldMac))
			this.shouldMac = true;
		else
			this.shouldMac = false;
	}
	
	public void setIsJson(String isJson) {
		if ("true".equalsIgnoreCase(isJson))
			this.isJson = true;
		else
			this.isJson = false;
	}

	public ArrayList<FieldModel> getFieldList() {
		return fieldList;
	}

	public void addField(FieldModel field) {
		this.fieldList.add(field);
	}
	
}
