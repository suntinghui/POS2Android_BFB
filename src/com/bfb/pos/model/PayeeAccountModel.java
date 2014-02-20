package com.bfb.pos.model;

import com.bfb.pos.util.StringUtil;

// 收款人
public class PayeeAccountModel {
	
	private String accountNo = "";
	private String name = "";
	private String phoneNo = "";
	private String bank = "";
	private String bankCode = ""; // 为了检索方便,同时也避免根据银行的名字来检索银行号易造成错误的情况
	
	public PayeeAccountModel(){
		
	}
	
	public PayeeAccountModel(String accountNo, String name, String phoneNo, String bank, String bankCode) {
		this.accountNo = accountNo;
		this.name = name;
		this.phoneNo = phoneNo;
		this.bank = bank;
		this.bankCode = bankCode;
	}
	
	public String getAccountNo() {
		return accountNo;
	}
	public void setAccountNo(String accountNo) {
		this.accountNo = accountNo;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getPhoneNo(){
		return phoneNo;
	}
	public void setPhoneNo(String phoneNo){
		this.phoneNo = phoneNo;
	}
	public String getBank() {
		return bank;
	}
	public void setBank(String bank) {
		this.bank = bank;
	}
	
	public String getBankCode() {
		return bankCode;
	}

	public void setBankCode(String bankCode) {
		this.bankCode = bankCode;
	}

	public String toString(){
		return StringUtil.formatAccountNo(accountNo);
	}

}
