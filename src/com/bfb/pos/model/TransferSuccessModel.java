package com.bfb.pos.model;

import java.util.HashMap;

import com.bfb.pos.util.DateUtil;
import com.bfb.pos.util.StringUtil;

public class TransferSuccessModel {
	
	private String traceNum = ""; // 交易的系统追踪号
	private String transCode = ""; // 交易类型，区分消费和消费撤销
	private String date = ""; //  交易日期，做查询用
	private String amount = ""; // 交易金额， 做查询用
	private HashMap<String, String> content = new HashMap<String, String>(); // 交易报文内容
	
	public TransferSuccessModel(){
		
	}

	public TransferSuccessModel(String traceNum, String transCode, String date,
			String amount, HashMap<String, String> content) {
		this.traceNum = traceNum;
		this.transCode = transCode;
		this.date = date;
		this.amount = amount;
		this.content = content;
	}

	public String getTraceNum() {
		return traceNum;
	}

	public void setTraceNum(String traceNum) {
		this.traceNum = traceNum;
	}

	public String getTransCode() {
		return transCode;
	}

	public void setTransCode(String transCode) {
		this.transCode = transCode;
	}
	
	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		// 保证记数据库正确性
		if (date.matches("^\\d{4}-\\d{2}-\\d{2}$")){
			this.date = date;
		} else {
			this.date = DateUtil.formatDateStr(date);
		}
	}

	public long getAmount() {
		return Long.parseLong(StringUtil.amount2String(this.amount));
	}

	/**
	 * 
	 * @param amountStr 必须是12位长的银行格式数据
	 */
	public void setAmount(String amountStr) {
		this.amount = amountStr;
	}

	public HashMap<String, String> getContent() {
		return content;
	}

	public void setContent(HashMap<String, String> content) {
		this.content = content;
	}

}
