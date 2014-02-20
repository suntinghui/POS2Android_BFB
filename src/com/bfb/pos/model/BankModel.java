package com.bfb.pos.model;

import java.io.Serializable;

public class BankModel implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String name;
	private String code;
	
	public BankModel(){
		
	}
	
	public BankModel(String name, String code) {
		this.name = name;
		this.code = code;
	}

	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getCode() {
		return code;
	}
	public void setCode(String code) {
		this.code = code;
	}
	
	public String toString(){
		return name;
	}

}
