package com.bfb.pos.dynamic.template.os;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.MarginLayoutParams;
import android.widget.ImageView;
import android.widget.TextView;

import com.bfb.pos.activity.view.TextWithLabelView;
import com.bfb.pos.dynamic.component.Component;
import com.bfb.pos.dynamic.component.ViewException;
import com.bfb.pos.dynamic.component.os.DButton;
import com.bfb.pos.dynamic.core.ViewPage;
import com.bfb.pos.dynamic.model.ViewGroupBean;
import com.bfb.pos.dynamic.parse.ParseView;
import com.bfb.pos.dynamic.template.StructTemplate;
import com.bfb.pos.dynamic.template.Template;
import com.bfb.pos.dynamic.template.os.layout.LayoutTemplate;
import com.bfb.pos.dynamic.template.os.layout.ViewGroupLayoutParamsTemplate;
import com.bfb.pos.dynamic.template.os.view.IViewGroupTemplate;

/**
 * 系统模板，可包括用到的所有模板
 * 
 */
public class OSTemplate {
	
	private HashMap<String, Map<String, CSSTemplate>> pageTemplates;
	private HashMap<String, StructTemplate> structTemplates;
	private HashMap<String, LayoutTemplate> layoutTemplates;
	private HashMap<String, IViewGroupTemplate> viewGroupTemplates;
	private HashMap<String, ViewGroupLayoutParamsTemplate> viewGroupLayoutParamsTemplates;
	
	public OSTemplate() {
		this.pageTemplates   = new HashMap<String,  Map<String, CSSTemplate>>();
		this.structTemplates = new HashMap<String,  StructTemplate>();
		this.layoutTemplates  = new HashMap<String, LayoutTemplate>();
		this.viewGroupTemplates = new HashMap<String, IViewGroupTemplate>();
		this.viewGroupLayoutParamsTemplates = new HashMap<String, ViewGroupLayoutParamsTemplate>();
	}
	public void addAPageTemplate(CSSTemplate pageTemplate) {
		Map<String, CSSTemplate> pageTs = null;
		if (null == this.pageTemplates.get(pageTemplate.getType())) {
			pageTs = new HashMap<String, CSSTemplate>();
		} else {
			pageTs = this.pageTemplates.get(pageTemplate.getType());
		}
		pageTs.put(pageTemplate.getId(), pageTemplate);
		
		this.pageTemplates.put(pageTemplate.getType(), pageTs);
		
	}
	public void addAStructTemplate(StructTemplate structTemplate) {
		this.structTemplates.put(structTemplate.getId(), structTemplate);
	}
	public void addALayoutTemplate(LayoutTemplate layoutTemplate) {
		this.layoutTemplates.put(layoutTemplate.getId(), layoutTemplate);
	}
	public void addAViewGroupTemplates(IViewGroupTemplate viewGroupTemplate) {
		this.viewGroupTemplates.put(((Template) viewGroupTemplate).getId(), viewGroupTemplate);
	}
	public void addAViewGroupLayoutParamsTemplate(ViewGroupLayoutParamsTemplate viewGroupLayoutParamsTemplate) {
		this.viewGroupLayoutParamsTemplates.put(((Template) viewGroupLayoutParamsTemplate).getId(), viewGroupLayoutParamsTemplate);
	}
	/**
	 * TODO
	 * 为页面模板加类型
	 */
	public View pageRewind(View comView, String templateId) throws ViewException {
		String tempType = null;
		String tempId   = templateId;
		// TODO start , 参考if...else
		if (comView instanceof TextWithLabelView) {
			tempType = ParseView.TEMPLATE_TYPE_TEXT;
			if (null == templateId) { // 默认模板
				tempId = ParseView.TEMPLATE_DEFAULT_TEXT;
			}
		}else if (comView instanceof DButton) {
			tempType = ParseView.TEMPLATE_TYPE_BUTTON;
			if (null == templateId) { // 默认模板
				tempId = ParseView.TEMPLATE_DEFAULT_BUTTON;
			}
		}else if(comView instanceof TextView){
			tempType = ParseView.TEMPLATE_TYPE_LABEL;
			if(null == templateId){
				tempId = ParseView.TEMPLATE_DEFAULT_LABEL;
			}
		}else if(comView instanceof ImageView){
			tempType = ParseView.TEMPLATE_TYPE_IMAGEVIEW;
			if(null == templateId){
				tempId = ParseView.TEMPLATE_DEFAULT_IMAGEVIEW;
			}
		}
		
		if (null == tempType) {
			return comView;
			//TODO 需要判断view类型来决定采用哪类模板
//			throw new ViewException("no such template type in component template types!");
		}
		if (null == this.pageTemplates.get(tempType).get(tempId)) {
			throw new ViewException("no such template["+templateId+"] in component templates!");
		}
		// TODO END
		return this.pageTemplates.get(tempType).get(tempId).rewind(comView);

	}
	
	public Vector<View> structRewind(ViewPage currentPage, String templateId) throws ViewException {
		String tempId   = templateId;
		if (null == tempId) { // 默认模板
			tempId = ParseView.TEMPLATE_DEFAULT_STRUCT_FP;
		}
		if (ParseView.TEMPLATE_NULL.equalsIgnoreCase(tempId)) {
			return new Vector<View>();
		}
		if (null == this.structTemplates.get(tempId)) {
			throw new ViewException("no such template["+templateId+"] in struct templates!");
		} else {
			return this.structTemplates.get(tempId).rewind(currentPage);
		}
	}
	
	public View layoutRewind(String templateId, Vector<Component> components) throws ViewException {
		String tempId   = templateId;
		if (null == tempId) { // 默认模板
			tempId = ParseView.TEMPLATE_DEFAULT_LAYOUT;
		}
		if (null == this.layoutTemplates.get(tempId)) {
			throw new ViewException("no such template["+templateId+"] in layout templates!");
		} else {
			return this.layoutTemplates.get(tempId).rewind(components);
		}
	}
	
	public View viewGroupRewind(String templateId, List<ViewGroupBean> viewGroupBeans) throws ViewException {
		String tempId   = templateId;
		if (null == tempId) { // 默认模板
			tempId = ParseView.TEMPLATE_DEFAULT_VIEWGROUP;
		}
		if (null == this.viewGroupTemplates.get(tempId)) {
			throw new ViewException("no such template["+templateId+"] in viewGroup templates!");
		} else {
			return this.viewGroupTemplates.get(tempId).rewind(viewGroupBeans);
		}
	}
	
	public ViewGroup.LayoutParams viewGroupLayoutParamsRewind(Component component) throws ViewException {
		String tempIDstr = component.getLayoutParamsTemplate();
		
		String rcIDstr = component.getRelativeComponents();
		Integer[] rcIDs = null;
		
		if (null == tempIDstr || 0 == tempIDstr.trim().length()) {
			return null;
		}
		
		if (null != rcIDstr) {
			String[] tmps = rcIDstr.split(",");
			rcIDs = new Integer[tmps.length];
			int i = 0;
			for (String tmp:tmps) {
				try {
					rcIDs[i++] = component.getViewPage().getComponent(tmp).getTargetId();
				} catch (Exception e) {
					throw new ViewException("Component ["+component.getId()+"]'s relative component["+tmp+"] is not exist!");
				}
			}
		}
		
		String[] tempIDs = tempIDstr.split(",");
		ViewGroup.LayoutParams ret = null;
		Integer index = 0;
		for (String tempID:tempIDs) {
			if (null == this.viewGroupLayoutParamsTemplates.get(tempID)) {
				throw new ViewException("no such template["+tempID+"] in viewGroupLayoutParams templates!");
			}
			
			ret = this.viewGroupLayoutParamsTemplates.get(tempID).toLayoutParams(ret, rcIDs, index);
			
			// set margin
			if(ret instanceof MarginLayoutParams){
				if (null != component.getMargin()) {
					if (4 != component.getMargin().split(",").length) {
						throw new ViewException("Component["+component.getId()+"],the arguments by margining must be 'int,int,int,int'!");
					}
					int[] margins = new int[4];
					String[] tmp   = component.getMargin().split(",");
					for(int i=0; i< 4; i++) {
						try {
							margins[i] = Integer.parseInt(tmp[i]);
						} catch (Exception e) {
							throw new ViewException("Component["+component.getId()+",the arguments by padding type must be integer!");
						}
					}
					((MarginLayoutParams) ret).setMargins(margins[0], margins[1], margins[2], margins[3]);
					
				}
			}
			
			
		}
		
		return ret; 
	}
}
