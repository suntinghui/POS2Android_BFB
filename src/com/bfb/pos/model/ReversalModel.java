package com.bfb.pos.model;

import java.util.HashMap;

import com.bfb.pos.util.DateUtil;
import com.bfb.pos.util.StringUtil;

public class ReversalModel {
	
	private String traceNum = "";
	private String batchNum = "";
	private String date = "";
	private HashMap<String, String> content = new HashMap<String, String>(); // 交易报文内容
	private int state = 1; // 1为失败，０为成功
	private int count = 0;
	
	public ReversalModel(){
	}
	
	public ReversalModel(String traceNum, String batchNum, String date,
			HashMap<String, String> content, int state, int count) {
		this.traceNum = traceNum;
		this.batchNum = batchNum;
		this.date = date;
		this.content = content;
		this.state = state;
		this.count = count;
	}
	public String getTraceNum() {
		return traceNum;
	}
	public void setTraceNum(String traceNum) {
		this.traceNum = traceNum;
	}
	public String getBatchNum() {
		return batchNum;
	}
	public void setBatchNum(String batchNum) {
		this.batchNum = batchNum;
	}
	public String getDate() {
		return date;
	}
	
	public void setDate(String date) {
		if (date.matches("^\\d{4}-\\d{2}-\\d{2}$")){
			this.date = date;
		} else {
			this.date = DateUtil.formatDateStr(date);
		}
	}
	
	public HashMap<String, String> getContent() {
		return content;
	}

	public void setContent(HashMap<String, String> content) {
		this.content = content;
	}

	/**
	 * 0表示冲正成功，1表示冲正失败
	 * @return 
	 */
	public int getState() {
		return state;
	}
	public void setState(int state) {
		this.state = state;
	}
	public int getCount() {
		return count;
	}
	public void setCount(int count) {
		this.count = count;
	}
	
}
