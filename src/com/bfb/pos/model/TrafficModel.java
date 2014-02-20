package com.bfb.pos.model;

public class TrafficModel {
	
	private long rxLength;
	private long txLength;
	private long wifiRxlength;
	private long wifiTxlength;
	
	public TrafficModel(){
		
	}
	
	public TrafficModel(long rxLength, long txLength, long wifiRxlength,long wifiTxlength) {
		super();
		this.rxLength = rxLength;
		this.txLength = txLength;
		this.wifiRxlength = wifiRxlength;
		this.wifiTxlength = wifiTxlength;
	}
	public long getRxLength() {
		return rxLength;
	}
	public void setRxLength(long rxLength) {
		this.rxLength = rxLength;
	}
	public long getTxLength() {
		return txLength;
	}
	public void setTxLength(long txLength) {
		this.txLength = txLength;
	}
	public long getWifiRxlength() {
		return wifiRxlength;
	}
	public void setWifiRxlength(long wifiRxlength) {
		this.wifiRxlength = wifiRxlength;
	}
	public long getWifiTxlength() {
		return wifiTxlength;
	}
	public void setWifiTxlength(long wifiTxlength) {
		this.wifiTxlength = wifiTxlength;
	}
	
	

}
