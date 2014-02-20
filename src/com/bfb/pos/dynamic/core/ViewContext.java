package com.bfb.pos.dynamic.core;

import java.util.HashMap;
import java.util.List;
import java.util.Vector;

import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

import com.bfb.pos.activity.BaseActivity;
import com.bfb.pos.dynamic.component.Component;
import com.bfb.pos.dynamic.component.ViewException;
import com.bfb.pos.dynamic.model.ViewGroupBean;
import com.bfb.pos.dynamic.parse.ParseView;
import com.bfb.pos.dynamic.template.StructTemplate;
import com.bfb.pos.dynamic.template.os.CSSTemplate;
import com.bfb.pos.dynamic.template.os.OSTemplate;
import com.bfb.pos.dynamic.template.os.layout.LayoutTemplate;
import com.bfb.pos.dynamic.template.os.layout.ViewGroupLayoutParamsTemplate;
import com.bfb.pos.dynamic.template.os.view.IViewGroupTemplate;

public class ViewContext {
	
	private HashMap<String, ViewPage> viewPages		= null;
	private OSTemplate osTemplate					= null;
	private String osType							= null;
	private boolean rewind							= false;
	private boolean render							= false;
	
	private static ViewContext viewContext 			= null;
	
	public static ViewContext getInstance(){
		if (null == viewContext){
			viewContext = new ViewContext();
		}
		return viewContext;
	}
	
	public ViewContext() {
		viewPages = new HashMap<String, ViewPage>();
		osTemplate = new OSTemplate();
		ParseView.hasInitTemplate = false;
	}
	
	/**
	 * 用于有时被系统销毁后重新赋值
	 * 
	 * @param viewContext
	 */
	public void setViewContext(ViewContext viewContext){
		if (null != viewContext){
			ViewContext.viewContext = viewContext;
		}
	}
	
	public String getOsType() {
		return osType;
	}
	public void setOsType(String osType) {
		this.osType = osType;
	}
	public boolean isRewind() {
		return rewind;
	}
	public void setRewind(boolean rewind) {
		this.rewind = rewind;
	}
	public boolean isRender() {
		return render;
	}
	public void setRender(boolean render) {
		this.render = render;
	}
	public void addViewPage(ViewPage viewPage) {
		this.viewPages.put(viewPage.getPageId(), viewPage);
		Log.e("added viewpage", this.viewPages.size() + "");
	}
	public void removeViewPage(ViewPage viewPage){
		this.viewPages.remove(viewPage.getPageId());
		Log.e("removed viewpage", this.viewPages.size() + "");
	}
	public ViewPage getViewPage(String pageId) {
		return this.viewPages.get(pageId);
	}
	public BaseActivity getContext() {
		return BaseActivity.getTopActivity();
	}
	public void addACssTemplate(CSSTemplate pageTemplate) {
		this.osTemplate.addAPageTemplate(pageTemplate);
	}
	public void addAStructTemplate(StructTemplate structTemplate) {
		this.osTemplate.addAStructTemplate(structTemplate);
	}
	public void addALayoutTemplate(LayoutTemplate layoutTemplate) {
		this.osTemplate.addALayoutTemplate(layoutTemplate);
	}
	public void addAViewGroupTemplates(IViewGroupTemplate viewGroupTemplate) {
		this.osTemplate.addAViewGroupTemplates(viewGroupTemplate);
	}
	public void addAViewGroupLayoutParamsTemplates(ViewGroupLayoutParamsTemplate viewGroupLayoutParamsTemplate) {
		this.osTemplate.addAViewGroupLayoutParamsTemplate(viewGroupLayoutParamsTemplate);
	}
	public View cssRewind(View structComponent, String templateId) throws ViewException {
		return this.osTemplate.pageRewind(structComponent, templateId);
	}
	public Vector<View> structRewind(ViewPage viewPage, String templateId) throws ViewException {
		return this.osTemplate.structRewind(viewPage, templateId);
	}
	public View layoutRewind(String templateId, Vector<Component> components) throws ViewException {
		return this.osTemplate.layoutRewind(templateId, components);
	}
	public View viewGroupRewind(String templateId, List<ViewGroupBean> viewGroupBeans) throws ViewException {
		return this.osTemplate.viewGroupRewind(templateId, viewGroupBeans);
	}
	public ViewGroup.LayoutParams getLayoutParam(Component component) throws ViewException {
		return this.osTemplate.viewGroupLayoutParamsRewind(component);
	}

}
