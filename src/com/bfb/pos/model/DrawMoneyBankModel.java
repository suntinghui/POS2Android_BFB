package com.bfb.pos.model;

public class DrawMoneyBankModel {
	
	private String merchant_name;//商户名
	private String tel;//手机号
	private String mastername;//户名
	private String bankaccount;//银行卡号
	private String banks;//银行名称
	private String bankno;//银行编号
	private String area;//地区
	private String city;//城市
	private String addr;//地址
	
	public DrawMoneyBankModel(){
		
	}
	
	public String getMerchant_name() {
		return merchant_name;
	}

	public void setMerchant_name(String merchant_name) {
		this.merchant_name = merchant_name;
	}

	public String getTel() {
		return tel;
	}

	public void setTel(String tel) {
		this.tel = tel;
	}

	public String getMastername() {
		return mastername;
	}

	public void setMastername(String mastername) {
		this.mastername = mastername;
	}

	public String getBankaccount() {
		return bankaccount;
	}

	public void setBankaccount(String bankaccount) {
		this.bankaccount = bankaccount;
	}

	public String getBanks() {
		return banks;
	}

	public void setBanks(String banks) {
		this.banks = banks;
	}

	public String getBankno() {
		return bankno;
	}

	public void setBankno(String bankno) {
		this.bankno = bankno;
	}

	public String getArea() {
		return area;
	}

	public void setArea(String area) {
		this.area = area;
	}

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public String getAddr() {
		return addr;
	}

	public void setAddr(String addr) {
		this.addr = addr;
	}

}
