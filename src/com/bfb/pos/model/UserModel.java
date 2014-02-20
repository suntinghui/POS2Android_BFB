package com.bfb.pos.model;

import java.io.Serializable;

public class UserModel implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String merchant_name;//商户名
	private String pid;//身份证
	private String img13;//身份证图片（正面）
	private String img17;//身份证图片（反面）
	private String img14;//身份证图片（二合一）
	private String img15;//大头照
	private String is_identify;//身份认证 0 未认证  1 已认证
	private String is_complete;//完善注册信息 	0 未完善  1 已完善
	private String status;//商户状态 0 已注册未完善用户信息 1 已完善未审核 2 已初审，等待终审 9 终审通过
	private String merchant_id;//商户号

	public String getMerchant_id() {
		return merchant_id;
	}

	public void setMerchant_id(String merchant_id) {
		this.merchant_id = merchant_id;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getMerchant_name() {
		return merchant_name;
	}
 
	public void setMerchant_name(String merchant_name) {
		this.merchant_name = merchant_name;
	}


	public String getPid() {
		return pid;
	}

	public void setPid(String pid) {
		this.pid = pid;
	}

	public String getImg13() {
		return img13;
	}

	public void setImg13(String img13) {
		this.img13 = img13;
	}

	public String getImg17() {
		return img17;
	}

	public void setImg17(String img17) {
		this.img17 = img17;
	}

	public String getImg14() {
		return img14;
	}

	public void setImg14(String img14) {
		this.img14 = img14;
	}

	public String getImg15() {
		return img15;
	}

	public void setImg15(String img15) {
		this.img15 = img15;
	}

	public String getIs_identify() {
		return is_identify;
	}

	public void setIs_identify(String is_identify) {
		this.is_identify = is_identify;
	}

	public String getIs_complete() {
		return is_complete;
	}

	public void setIs_complete(String is_complete) {
		this.is_complete = is_complete;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}

	public UserModel(){
		
	}
	
}
