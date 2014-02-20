package com.bfb.pos.dynamic.core;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;
import java.util.Set;
import java.util.Vector;

import android.view.View;

import com.bfb.pos.dynamic.component.Body;
import com.bfb.pos.dynamic.component.Component;
import com.bfb.pos.dynamic.component.Target;
import com.bfb.pos.dynamic.component.ViewException;
import com.bfb.pos.dynamic.component.expression.Logic;
import com.bfb.pos.dynamic.component.os.StructComponent;
import com.bfb.pos.dynamic.component.os.frame.FrameComponent;
import com.bfb.pos.dynamic.parse.ParseView;
import com.bfb.pos.dynamic.regex.Regex;

public class ViewPage {
	
	private String pageId;
	private String pageName;
	private String pageBack;
	private String isForbidBack;//屏蔽返回键
	private Body body;
	private ArrayList<String> viewIndex;
	private HashMap<String, Component> components;
	private HashMap<String, Event> events;
	private HashMap<String, Object> page;
	private HashMap<Integer, Integer> targetIds;
	private Target target;
	private String template;
	private String prePage;
	
	public ViewPage(){
	}
	
	public ViewPage(String pageId) {
		this.pageId = pageId;
		viewIndex  = new ArrayList<String>();
		components = new HashMap<String, Component>();
		events     = new HashMap<String, Event>();
		targetIds  = new HashMap<Integer, Integer>();
		this.cleanPage();
	}

	public Integer random() {
		return new Random().nextInt();
	}
	public String getPageId() {
		return pageId;
	}

	public String getPageName() {
		return pageName;
	}
	
	public String getPageBack(){
		if (null == this.pageBack || "".trim().equals(this.pageBack)){
			return "0";
		}
		
		return pageBack;
	}
	
	public ViewPage getPrePage() {
		return ViewContext.getInstance().getViewPage(this.prePage);
	}

	public void setPrePage(ViewPage prePage) {
		this.prePage = prePage.getPageId();
	}

	public void setIsForbidBack(String forbidBack){
		this.isForbidBack = forbidBack;
	} 
	public String getIsForbidBack(){
		return isForbidBack;
	}
	public void setPageName(String pageName) {
		this.pageName = pageName;
	}
	
	public void setPageBack(String pageBack){
		this.pageBack = pageBack;
	}
	
	public Body getBody() {
		return body;
	}
	public void setBody(Body body) {
		this.body = body;
	}
	public void addAViewIndex(Component component) {
		Integer id = null == component.getTargetId() ?Math.abs(this.random())/10000 :component.getTargetId();
		while (null != this.getATargetId(id)) {
			id = Math.abs(this.random())/10000;
		}
		component.setTargetId(id);
		this.addATargetId(component);
		this.viewIndex.add(component.getId());
	}
	public ArrayList<String> getViewIndex() {
		return viewIndex;
	}
	public void setTarget(Target target) {
		this.target=target;
	}
	public Target getTarget() {
		return this.target;
	}
	public void addATargetId(Component component) {
		this.targetIds.put(component.getTargetId(), component.getTargetId());
	}
	public Integer getATargetId(Integer id) {
		return this.targetIds.get(id);
	}
	public void addAComponent(Component component) {
		this.addAViewIndex(component);
		this.components.put(component.getId(), component);
	}
	public void addAllComponents(ViewPage viewPage) {
		for (String com:viewPage.getComponentsSet()) {
			this.components.put(com, viewPage.getComponent(com).clone(this));
		}
	}
	public Set<String> getComponentsSet() {
		return this.components.keySet();
	}
	public Component getComponent(String componentId) {
		return this.components.get(componentId);
	}
	public void addAnEvent(Event event) {
		this.events.put(event.getActionId(), event);
	}
	public void addAllEvents(ViewPage viewPage) {
		for(String key:viewPage.events.keySet()) {
			this.events.put(key, viewPage.events.get(key).clone(this));
		}
	}
	public Event getEvent(String operationId) {
		return this.events.get(operationId);
	}
	public void addAPageValue(String key, Object value) {
		this.page.put(key, value);
	}
	public void addAllPageValues(ViewPage viewPage) throws ViewException {
		for (String com:viewPage.getComponentsSet()) {
			this.addAPageValue(com, viewPage.getPageValue(com));
		}
	}
	public Object getPageValue(String key) throws ViewException {
		Object ret = this.page.get(key);;
		if (ParseView.isComponentTarget(ret) && null == this.getTarget()) { // 非模板界面
//			ret = this.getPageValue(ViewUtil.parseComponentTarget(ret));
			ret = this.getRegexValue(ret.toString());
		}
		return ret;
	}
	
	public void addAPageValue(Component component, Object value) {
		this.addAPageValue(component.getId(), value);
	}
	public Object getPageValue(Component component) throws ViewException {
		return this.getPageValue(component.getId());
	}
	public void cleanPage() {
		page = new HashMap<String, Object>();
	}
	
	public String getTemplate() {
		return template;
	}
	public void setTemplate(String template) {
		this.template = template;
	}
	public Object getRegexValue(String regex) throws ViewException {
		return Regex.getRegexValue(this, regex);
	}
	
	public Vector<View> toOSView() throws ViewException {
		
		ViewContext.getInstance().structRewind(this, this.getTemplate());
		
		Vector<View> vector = new Vector<View>();
		Vector<Component> frameVector = new Vector<Component>();
		boolean isFrame = false;
		Component frameComponent = null;
		Logic logic = null;
		boolean isLogic = false;
		for(int i=0;i<viewIndex.size();i++){
			Component component = this.getComponent(viewIndex.get(i));
			// logic target start
			if (component instanceof Logic) {
				if (null == logic) {
					logic = (Logic) component;
					isLogic = !logic.compare();
					continue;
				} else if (logic.equals(component)) {
					isLogic = false;
					continue;
				} else if (!isLogic) {
					logic = (Logic) component;
					isLogic = !logic.compare();
				}
			}
			if (isLogic) {
				continue;
			}
			// logic target end
			if (component.isFrame()) {
				isFrame = true;
				if (null == frameComponent) {
					frameComponent = component;
					continue;
				} else if (frameComponent.equals(component)) { // 判断同一个标签结束位置
					isFrame = false;
				}
			}
			if (component instanceof StructComponent || // 界面要素
					component.isFrame()) { // Layout
				if (isFrame) {	
					frameVector.add(component);
				} else {
					// add frameComponent
					if (null != frameVector && 0 != frameVector.size()) {
						vector.add(((FrameComponent) frameComponent).toFrame(frameVector));
						frameVector = new Vector<Component>();
						frameComponent = null;
					}
					if (component instanceof StructComponent) {
						vector.add(ViewContext.getInstance().cssRewind(((StructComponent)component).toComponent(), ((StructComponent)component).getTemplate()));
					}
				}
			}
			
		}
		if (null != frameVector && 0 != frameVector.size()) {
			vector.add(((FrameComponent) frameComponent).toFrame(frameVector));
			frameVector = new Vector<Component>();
		}
		return vector;
	}

}
