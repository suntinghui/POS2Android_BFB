package com.bfb.pos.model;

import java.io.Serializable;
import java.util.HashMap;

public class AnnouncementModel implements Serializable{

	private static final long serialVersionUID = -8307674336517627131L;
	
	private String tel = "";//手机号
	private String branch_id = "";//机构编号
	private String notice_title = "";//公告标题
	private String notice_content = "";//公告内容
	private String notice_time = "";//时间
	private String notice_date = "";//日期
	private String notice_status = "";//状态
	private String notice_type = "";
	
	public AnnouncementModel(){
		
	}
	
	public AnnouncementModel(String tel, String branch_id, String notice_title, String notice_content) {
		this.tel = tel;
		this.branch_id = branch_id;
		this.notice_title = notice_title;
		this.notice_content = notice_content;
	}
	
	public AnnouncementModel (HashMap<String, String> map){
		if (map.containsKey("tel")){
			this.tel = map.get("tel");
		} 
		if (map.containsKey("branch_id")){
			this.branch_id = map.get("branch_id");
		}
		if (map.containsKey("notice_title")){
			this.notice_title = map.get("notice_title");
		}
		if (map.containsKey("notice_content")){
			this.notice_content = map.get("notice_content");
		}
	}
	
	public String getNotice_time() {
		return notice_time;
	}

	public void setNotice_time(String notice_time) {
		this.notice_time = notice_time;
	}

	public String getNotice_date() {
		return notice_date;
	}

	public void setNotice_date(String notice_date) {
		this.notice_date = notice_date;
	}

	public String getNotice_status() {
		return notice_status;
	}

	public void setNotice_status(String notice_status) {
		this.notice_status = notice_status;
	}

	public String getNotice_type() {
		return notice_type;
	}

	public void setNotice_type(String notice_type) {
		this.notice_type = notice_type;
	}
	public String getTel() {
		return tel;
	}

	public void setTel(String tel) {
		this.tel = tel;
	}

	public String getBranch_id() {
		return branch_id;
	}

	public void setBranch_id(String branch_id) {
		this.branch_id = branch_id;
	}

	public String getNotice_title() {
		return notice_title;
	}

	public void setNotice_title(String notice_title) {
		this.notice_title = notice_title;
	}

	public String getNotice_content() {
		return notice_content;
	}

	public void setNotice_content(String notice_content) {
		this.notice_content = notice_content;
	}
	
	public static long getSerialversionuid() {
		return serialVersionUID;
	}
}
