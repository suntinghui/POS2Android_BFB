package com.bfb.pos.dynamic.component;

import java.io.IOException;

import android.view.View;
import android.view.ViewGroup;

import com.bfb.pos.dynamic.core.Event;
import com.bfb.pos.dynamic.core.ViewContext;
import com.bfb.pos.dynamic.core.ViewPage;
import com.bfb.pos.dynamic.regex.Regex;

public abstract class Component{
	private String id;
	private String name;
	private int targetId;
	private String paramGroup;
	protected ViewPage viewPage;
	private String template;
	private String layoutParamsTemplates; // for android,type likes ID1,ID2,ID3...
	private String padding; // for android,format as 0,1,2,3
	private String margin;  // for android,format as 0,1,2,3
	private String relativeComponents; // for android,type likes component1,component2,component3...
	private String event;
	private boolean frame;
	
	public Component(){
	}
	
	public boolean isFrame() {
		return frame;
	}
	public void setFrame(boolean frame) {
		this.frame = frame;
	}
	public Event getEvent() {
		return this.getViewPage().getEvent(this.event);
	}
	public void setEvent(Event event) {
		this.event = event.getActionId();
	}
	public void trigger() throws ViewException, IOException {
		if (null == this.event || 0 == this.event.trim().length()) 
			return;
		
		this.getEvent().trigger();
	}
	
	public String getParamGroup() {
		return paramGroup;
	}
	public void setParamGroup(String paramGroup) {
		this.paramGroup = paramGroup;
	}
	public ViewPage getViewPage() {
		return viewPage;
	}
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public Integer getTargetId() {
		return targetId;
	}
	public void setTargetId(int targetId) {
		this.targetId = targetId;
	}
	/**
	 * 下拉的处理由SelectEntity中的toString来屏蔽
	 */
	public Object getValue() throws ViewException {
		return this.getViewPage().getPageValue(this);
	}
	public String getTemplate() {
		return template;
	}
	public void setTemplate(String template) {
		this.template = template;
	}
	public String getLayoutParamsTemplate() {
		return layoutParamsTemplates;
	}
	public void setLayoutParamsTemplate(String layoutParamsTemplates) {
		this.layoutParamsTemplates = layoutParamsTemplates;
	}
	public String getPadding() {
		return this.padding;
	}
	public void setPadding(String padding) {
		this.padding = padding;
	}
	public String getMargin() {
		return margin;
	}
	public void setMargin(String margin) {
		this.margin = margin;
	}
	public String getRelativeComponents() {
		return relativeComponents;
	}
	public void setRelativeComponents(String relativeComponents) {
		this.relativeComponents = relativeComponents;
	}
	public Object getRegexValue(String regex) throws ViewException {
		return Regex.getRegexValue(this, regex);
	}
	
	public Component clone(ViewPage viewPage) {
		Component component =  this.construction(viewPage);
		this.copy(this, component);
		this.copyParams(this, component);
		return component;
	}
	protected abstract Component construction(ViewPage viewPage);
	protected void copyParams (Component src, Component des) {
		// 各结构类实现，若无参数拷贝，无需实现
	}
	public View initOSParams(View view) throws ViewException {
		view.setId(this.getTargetId());
		if (null != this.getPadding()) {
			if (4 != this.getPadding().split(",").length) {
				throw new ViewException("Component["+this.getId()+"],the arguments by padding must be 'int,int,int,int'!");
			}
			int[] paddings = new int[4];
			String[] tmp   = this.getPadding().split(",");
			for(int i=0; i< 4; i++) {
				try {
					paddings[i] = Integer.parseInt(tmp[i]);
				} catch (Exception e) {
					throw new ViewException("Component["+this.getId()+",the arguments by padding type must be integer!");
				}
			}
			view.setPadding(paddings[0], paddings[1], paddings[2], paddings[3]);
			
		}
		if (null != this.getLayoutParamsTemplate()) {
			ViewGroup.LayoutParams layoutParam = ViewContext.getInstance().getLayoutParam(this);
			view.setLayoutParams(layoutParam);
		}
		return view;
	}
	
	private void copy(Component src, Component des) {
		des.setName(src.name);
		des.setId(src.id);
		des.setTargetId(src.targetId);
		des.setParamGroup(src.paramGroup);
		// not need viewpage
		des.setTemplate(src.template);
		des.setLayoutParamsTemplate(src.layoutParamsTemplates);
		des.setPadding(src.padding);
		des.setMargin(src.margin);
		des.setRelativeComponents(src.relativeComponents);	
		des.event = src.event;
		des.setFrame(this.frame);
	}
	
}
