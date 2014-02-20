package com.bfb.pos.dynamic.model;

import java.util.Vector;

public class SelectEntity {
	
	private Vector<OptionEntity> options;
	private String selected = "";
	private String prompt = "";
	
	public SelectEntity() {
		this.options = new Vector<OptionEntity>();
	}
	public Vector<OptionEntity> getOptions() {
		return options;
	}
	public void addAnOptionEntity(OptionEntity option) {
		this.options.add(option);
	}

	public String getSelected() {
		return selected;
	}

	public void setSelected(String selected) {
		this.selected = selected;
	}
	
	public String getPrompt() {
		return prompt;
	}
	public void setPrompt(String prompt) {
		this.prompt = prompt;
	}
	/**
	 * 为在component中的getValue方法的统一处理做准备
	 */
	public String toString() {
		return this.selected;
	}
}
