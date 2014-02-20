/**
 * 
 */
package com.bfb.pos.dynamic.template;

/**
 * @author:Xiaoping Dong
 */
public abstract class Template{
	/**
	 * 模板编号，同一个控件，不同编号必须不一致<br/>
	 * 如：css_text_01
	 */
	private String id;
	/**
	 * 模板名称；可为空
	 */
	private String name;
	/**
	 * 模板类型，不可为空，每一类名称必须不一样<br/>
	 * 如：TextTemplate为TextTemplate，名称唯一即可
	 */
	private String type;
	
	public Template(String id, String name) {
		this.id   = id;
		this.name = name;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	
}
