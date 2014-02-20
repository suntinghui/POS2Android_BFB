package com.bfb.pos.dynamic.component;

import com.bfb.pos.dynamic.core.ViewPage;

/**
 * 模板标签，应用于当页面中出现以某个约定符（如：<@mbsid）出现的标签时，<BR/>
 * 使用结构模板将自动根据规则替换其对应的应用界面或一类标签
 * @author DongXiaoping
 *
 */
public class Target extends Component{
	
	public Target(){
		super();
	}
	
	/**
	 * 标签标识
	 */
	private String id = "System_target_"; 
	/**
	 * 是否是界面级，如果为true，则该标签将对应于一个界面；如果为false，则启用属性tar
	 */
	private boolean page = true; 
	/**
	 * 如果page为false则不能为空，对应于具体的当前应用界面中的某一类标签
	 */
	private String tar;
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public boolean isPage() {
		return page;
	}
	public void setPage(boolean page) {
		this.page = page;
	}
	public String getTar() {
		return tar;
	}
	public void setTar(String tar) {
		this.tar = tar;
	}
	@Override
	protected Component construction(ViewPage viewPage) {
		return new Target();
	}
	
}
