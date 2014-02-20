package com.bfb.pos.model;

public class RechargeModel {
	
	private String faceValue; // 面值
	private String sellingPrice; // 售价
	
	public RechargeModel(){
		
	}
	
	public RechargeModel(String faceValue, String sellingPrice) {
		super();
		this.faceValue = faceValue;
		this.sellingPrice = sellingPrice;
	}
	
	public String getFaceValue() {
		return faceValue;
	}
	public void setFaceValue(String faceValue) {
		this.faceValue = faceValue;
	}
	public String getSellingPrice() {
		return sellingPrice;
	}
	public void setSellingPrice(String sellingPrice) {
		this.sellingPrice = sellingPrice;
	}
	
	@Override
	public String toString() {
		return faceValue+"元 (售价"+sellingPrice+"元)";
	}
}
