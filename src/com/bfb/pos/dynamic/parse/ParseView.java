package com.bfb.pos.dynamic.parse;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Vector;

import org.kxml2.io.KXmlParser;
import org.xmlpull.v1.XmlPullParser;

import android.util.Log;

import com.bfb.pos.agent.client.AppDataCenter;
import com.bfb.pos.dynamic.component.Body;
import com.bfb.pos.dynamic.component.Component;
import com.bfb.pos.dynamic.component.Hidden;
import com.bfb.pos.dynamic.component.Param;
import com.bfb.pos.dynamic.component.Target;
import com.bfb.pos.dynamic.component.ViewException;
import com.bfb.pos.dynamic.component.expression.Else;
import com.bfb.pos.dynamic.component.expression.If;
import com.bfb.pos.dynamic.component.os.OSAccount;
import com.bfb.pos.dynamic.component.os.OSButton;
import com.bfb.pos.dynamic.component.os.OSDatePicker;
import com.bfb.pos.dynamic.component.os.OSImage;
import com.bfb.pos.dynamic.component.os.OSInstructionsView;
import com.bfb.pos.dynamic.component.os.OSLabel;
import com.bfb.pos.dynamic.component.os.OSPassword;
import com.bfb.pos.dynamic.component.os.OSSelect;
import com.bfb.pos.dynamic.component.os.OSText;
import com.bfb.pos.dynamic.component.os.OSTopInfo;
import com.bfb.pos.dynamic.component.os.frame.Layout;
import com.bfb.pos.dynamic.component.os.frame.ViewGroup;
import com.bfb.pos.dynamic.core.Event;
import com.bfb.pos.dynamic.core.ViewContext;
import com.bfb.pos.dynamic.core.ViewPage;
import com.bfb.pos.dynamic.model.OptionEntity;
import com.bfb.pos.dynamic.model.SelectEntity;
import com.bfb.pos.dynamic.template.os.ButtonTemplate;
import com.bfb.pos.dynamic.template.os.CSSTemplate;
import com.bfb.pos.dynamic.template.os.ImageViewTemplate;
import com.bfb.pos.dynamic.template.os.LabelTemplate;
import com.bfb.pos.dynamic.template.os.TextTemplate;
import com.bfb.pos.dynamic.template.os.layout.LinearLayoutParamsTemplate;
import com.bfb.pos.dynamic.template.os.layout.LinearLayoutTemplate;
import com.bfb.pos.dynamic.template.os.layout.RelativeLayoutParamsTemplate;
import com.bfb.pos.dynamic.template.os.layout.RelativeLayoutTemplate;
import com.bfb.pos.dynamic.template.os.struct.FunctionPageTemplate;
import com.bfb.pos.dynamic.template.os.view.GridViewTemplate;
import com.bfb.pos.dynamic.template.os.view.ListViewTemplate;
import com.bfb.pos.util.AssetsUtil;
import com.bfb.pos.util.StringUtil;

public final class ParseView {
	public static final String ACTION_TYPE_LOCAL				= "LOCAL";
	public static final String ACTION_TYPE_CLOSE				= "CLOSE"; 
	public static final String ACTION_TYPE_FINISH				= "FINISH";
	public static final String ACTION_TYPE_GETPREPAGEVALUES		= "GETPREPAGEVALUES"; 
	public static final String ACTION_TYPE_SUBMIT 				= "SUBMIT";
	
	public static String TEMPLATE_NULL							= "NULL";
	public static String TEMPLATE_DEFAULT_STRUCT_FP				= "default_struct_function_page";
	
	public static String TEMPLATE_DEFAULT_TEXT					= "default_text";
	public static String TEMPLATE_DEFAULT_BUTTON				= "default_button";
	public static String TEMPLATE_DEFAULT_LABEL					= "default_label";
	public static String TEMPLATE_DEFAULT_IMAGEVIEW				= "default_imageView";
	
	public static String TEMPLATE_DEFAULT_LAYOUT				= "default_layout";
	public static String TEMPLATE_DEFAULT_VIEWGROUP				= "default_viewGroup";
	
	public static final String TEMPLATE_TYPE_STRUCT				= "TYPE_struct";
	public static final String TEMPLATE_TYPE_TEXT				= "TYPE_text";
	public static final String TEMPLATE_TYPE_BUTTON				= "TYPE_button";
	public static final String TEMPLATE_TYPE_LABEL				= "TYPE_label";
	public static final String TEMPLATE_TYPE_IMAGEVIEW			= "TYPE_imageView";
	
	public static boolean hasInitTemplate = false;
	
	public static ViewPage paraseViewXML(ViewPage preViewPage, String xml) {
		Event event = null;
		ViewPage viewPage = null;
		InputStream inputStream = null;
		
		OSSelect select = null;
		
		try{
			KXmlParser parser = new KXmlParser();  
			inputStream = StringUtil.getInputStream(xml);
			Log.i("parser screen", xml);
			// 编码格式可能与手机相关
			parser.setInput(inputStream,"utf-8");
			// 获取事件类型
	        int eventType = parser.getEventType();
	        Vector<Layout> layouts = new Vector<Layout>();
	        Vector<ViewGroup> viewGroups = new Vector<ViewGroup>();
	        Vector<If> myIfs = new Vector<If>();
	        If endIf = null;
	        Vector<Else> myElses = new Vector<Else>();
	        while (eventType != XmlPullParser.END_DOCUMENT) {
	        	switch(eventType){
	        	case XmlPullParser.START_TAG:
	        		if("body".equalsIgnoreCase(parser.getName())){
	        			String pageId = parser.getAttributeValue(null, "pageId");
	        			isDataStrMustBeEntered("Page" ,"body", "pageId", pageId);
	        			viewPage = new ViewPage(pageId);
	        			viewPage.setPrePage(preViewPage);
	        			viewPage.setTemplate(parser.getAttributeValue(null, "templateId"));
	        			viewPage.setPageName(parser.getAttributeValue(null, "pageName"));
	        			viewPage.setPageBack(parser.getAttributeValue(null, "pageBack"));
	        			viewPage.setIsForbidBack(parser.getAttributeValue(null, "isForbidBack"));
	        			
	        		}else if("init".equalsIgnoreCase(parser.getName())){
	        			String actionId = parser.getAttributeValue(null, "actionId");
	        			if (actionId != null){
	        				String action = parser.getAttributeValue(null, "action");
	        				
		        			event = new Event(viewPage,actionId, action);
		        			event.setActionType(parser.getAttributeValue(null, "actionType"));
		        			event.setFsk(parser.getAttributeValue(null, "fsk"));
		        			event.setMethod(parser.getAttributeValue(null, "method"));
		        			event.setTransfer(parser.getAttributeValue(null, "transfer"));
		        			Body body = new Body(viewPage);
		        			body.addAnEvent(event);
	        			}
	        			
	        		}else if("label".equalsIgnoreCase(parser.getName())){
	        			OSLabel label = new OSLabel(viewPage);
	        			label.setId(strValidator(viewPage.getPageId(), "label", "id", parser.getAttributeValue(null, "id")));
    					initData(label, parser.getAttributeValue(null, "value"));
    					label.setTemplate(parser.getAttributeValue(null, "templateId"));
    					label.setPadding(parser.getAttributeValue(null, "padding"));
    					label.setMargin(parser.getAttributeValue(null, "margin"));
    					label.setLayoutParamsTemplate(parser.getAttributeValue(null, "layoutParamsTemplateIds"));
    					label.setRelativeComponents(parser.getAttributeValue(null, "relativeComponents"));
	        			viewPage.addAComponent(label);
	        			
	        		}else if("image".equalsIgnoreCase(parser.getName())){
	        			OSImage image = new OSImage(viewPage);
	        			image.setId(strValidator(viewPage.getPageId(), "image", "id", parser.getAttributeValue(null, "id")));
	        			image.setTemplate(parser.getAttributeValue(null, "templateId"));
	        			image.setImageName(parser.getAttributeValue(null, "src"));
	        			image.setPadding(parser.getAttributeValue(null, "padding"));
	        			image.setMargin(parser.getAttributeValue(null, "margin"));
	        			image.setLayoutParamsTemplate(parser.getAttributeValue(null, "layoutParamsTemplateIds"));
	        			image.setRelativeComponents(parser.getAttributeValue(null, "relativeComponents"));
	        			viewPage.addAComponent(image);
	        			
	        		}else if("select".equalsIgnoreCase(parser.getName())){
	        			select = new OSSelect(viewPage);
	        			SelectEntity selectEntity = new SelectEntity();
	        			select.setId(strValidator(viewPage.getPageId(), "select", "id", parser.getAttributeValue(null, "id")));
	        			selectEntity.setPrompt(parser.getAttributeValue(null, "prompt"));
	        			select.getViewPage().addAPageValue(select, selectEntity);
	        			select.setJudge(parser.getAttributeValue(null, "judge"));;
	        			select.setPadding(parser.getAttributeValue(null, "padding"));
	        			select.setMargin(parser.getAttributeValue(null, "margin"));
	        			select.setLayoutParamsTemplate(parser.getAttributeValue(null, "layoutParamsTemplateIds"));
	        			select.setRelativeComponents(parser.getAttributeValue(null, "relativeComponents"));
	        			String actionId   = parser.getAttributeValue(null, "actionId");
        				if (actionId != null){
        					String action     = parser.getAttributeValue(null, "action");
	        				event = new Event(viewPage, actionId, action);
	        				event.setMethod(parser.getAttributeValue(null, "method"));
	        				String tmp = parser.getAttributeValue(null, "goHistory");
	        				Integer history	  = null == tmp ? null : Integer.parseInt(tmp);
	        				event.setGoHistory(history);
	        				event.setActionType(parser.getAttributeValue(null, "actionType"));
	        				event.setFsk(parser.getAttributeValue(null, "fsk"));
	        				event.setTransfer(parser.getAttributeValue(null, "transfer"));
	        				select.setEvent(event);
        					viewPage.addAnEvent(event);
        				}
	        			viewPage.addAComponent(select);
	        			
	        		}else if("option".equalsIgnoreCase(parser.getName())){
	        			String key = parser.getAttributeValue(null, "key");
	        			String value = parser.getAttributeValue(null, "value");
	        			
	        			OptionEntity optionEntity  = new OptionEntity();
        				optionEntity.setKey(null == key ? "":key);
        				optionEntity.setValue(null == value ? "": value);
        				
        				if(null != select){
        					SelectEntity selectEntity = (null == select.getValue()? new SelectEntity(): (SelectEntity)select.getValue());
        					selectEntity.addAnOptionEntity(optionEntity);
        					setScopeData(viewPage,select.getId(),selectEntity);
        				}
        				
	        		} else if ("topInfoView".equalsIgnoreCase(parser.getName())){
	        			// only for WMPay
        				OSTopInfo topInfo = new OSTopInfo(viewPage);
        				topInfo.setId(strValidator(viewPage.getPageId(), "text", "id", parser.getAttributeValue(null, "id")));
        				topInfo.setTemplate(parser.getAttributeValue(null, "templateId"));
        				topInfo.setPadding(parser.getAttributeValue(null, "padding"));
        				topInfo.setMargin(parser.getAttributeValue(null, "margin"));
        				topInfo.setLayoutParamsTemplate(parser.getAttributeValue(null, "layoutParamsTemplateIds"));
        				topInfo.setRelativeComponents(parser.getAttributeValue(null, "relativeComponents"));
        				viewPage.addAComponent(topInfo);
        				
        			} else if("instructionsView".equalsIgnoreCase(parser.getName())){
        				OSInstructionsView instructionView = new OSInstructionsView(viewPage);
        				instructionView.setId(strValidator(viewPage.getPageId(), "text", "id", parser.getAttributeValue(null, "id")));
        				instructionView.setTemplate(parser.getAttributeValue(null, "templateId"));
        				instructionView.setPadding(parser.getAttributeValue(null, "padding"));
        				instructionView.setMargin(parser.getAttributeValue(null, "margin"));
        				instructionView.setLayoutParamsTemplate(parser.getAttributeValue(null, "layoutParamsTemplateIds"));
        				instructionView.setRelativeComponents(parser.getAttributeValue(null, "relativeComponents"));
        				instructionView.setInstructionId(parser.getAttributeValue(null, "instructionId"));
        				viewPage.addAComponent(instructionView);
        				
        			} else if("input".equalsIgnoreCase(parser.getName())){
	        			String type = parser.getAttributeValue(null, "type");
	        			if("text".equalsIgnoreCase(type)){
	        				OSText text = new OSText(viewPage);
	        				text.setId(strValidator(viewPage.getPageId(), "text", "id", parser.getAttributeValue(null, "id")));
	        				initData(text, parser.getAttributeValue(null, "value"));
	        				text.setTemplate(parser.getAttributeValue(null, "templateId"));
	        				text.setAllowNull(parser.getAttributeValue(null, "allowNull"));
	        				text.setPattern(parser.getAttributeValue(null, "pattern"));
	        				text.setJudge(parser.getAttributeValue(null, "judge"));
	        				text.setHint(parser.getAttributeValue(null, "hint"));
	        				text.setEditAble(parser.getAttributeValue(null, "editable"));
	        				text.setInputType(parser.getAttributeValue(null, "inputType"));
	        				text.setMaxLength(parser.getAttributeValue(null, "maxLength"));
	        				text.setLeftImage(parser.getAttributeValue(null, "leftImage"));
	        				text.setLeftLabel(parser.getAttributeValue(null, "leftLabel"));
	        				text.setPadding(parser.getAttributeValue(null, "padding"));
	        				text.setMargin(parser.getAttributeValue(null, "margin"));
	        				text.setLayoutParamsTemplate(parser.getAttributeValue(null, "layoutParamsTemplateIds"));
	        				text.setRelativeComponents(parser.getAttributeValue(null, "relativeComponents"));
		        			viewPage.addAComponent(text);
		        			
	        			}else if("button".equalsIgnoreCase(type)){
	        				OSButton button = new OSButton(viewPage);
	        				button.setId(strValidator(viewPage.getPageId(), "button", "id",parser.getAttributeValue(null, "id")));
	        				initData(button,parser.getAttributeValue(null, "value"));
	        				button.setTemplate(parser.getAttributeValue(null, "templateId"));
	        				button.setPadding(parser.getAttributeValue(null, "padding"));
	        				button.setMargin(parser.getAttributeValue(null, "margin"));
	        				button.setLayoutParamsTemplate(parser.getAttributeValue(null, "layoutParamsTemplateIds"));
	        				button.setRelativeComponents(parser.getAttributeValue(null, "relativeComponents"));
	        				String actionId   = parser.getAttributeValue(null, "actionId");
	        				if (actionId != null){
	        					String action     = parser.getAttributeValue(null, "action");
		        				event = new Event(viewPage, actionId, action);
		        				event.setMethod(parser.getAttributeValue(null, "method"));
		        				String tmp = parser.getAttributeValue(null, "goHistory");
		        				Integer history	  = null == tmp ? null : Integer.parseInt(tmp);
		        				event.setGoHistory(history);
		        				event.setActionType(parser.getAttributeValue(null, "actionType"));
		        				event.setFsk(parser.getAttributeValue(null, "fsk"));
		        				event.setTransfer(parser.getAttributeValue(null, "transfer"));
			        			button.setEvent(event);
	        					viewPage.addAnEvent(event);
	        				}
		        			viewPage.addAComponent(button);
		        			
	        			}else if("password".equalsIgnoreCase(type)){
	        				OSPassword password = new OSPassword(viewPage);
	        				password.setId(strValidator(viewPage.getPageId(), "password", "id", parser.getAttributeValue(null, "id")));
	        				initData(password, parser.getAttributeValue(null, "value"));
	        				password.setTemplate(parser.getAttributeValue(null, "templateId"));
	        				password.setHint(parser.getAttributeValue(null, "hint"));
	        				password.setLeftLabel(parser.getAttributeValue(null, "leftLabel"));
	        				password.setJudge(parser.getAttributeValue(null, "judge"));
	        				password.setPadding(parser.getAttributeValue(null, "padding"));
	        				password.setMargin(parser.getAttributeValue(null, "margin"));
	        				password.setLayoutParamsTemplate(parser.getAttributeValue(null, "layoutParamsTemplateIds"));
	        				password.setRelativeComponents(parser.getAttributeValue(null, "relativeComponents"));
		        			viewPage.addAComponent(password);
		        			
	        			}else if("account".equalsIgnoreCase(type)){
	        				OSAccount account = new OSAccount(viewPage);
	        				account.setId(strValidator(viewPage.getPageId(), "account", "id", parser.getAttributeValue(null, "id")));
	        				initData(account, parser.getAttributeValue(null, "value"));
	        				account.setTemplate(parser.getAttributeValue(null, "templateId"));
	        				account.setPattern(parser.getAttributeValue(null, "pattern"));
	        				account.setJudge(parser.getAttributeValue(null, "judge"));
	        				account.setHint(parser.getAttributeValue(null, "hint"));
	        				account.setInputType(parser.getAttributeValue(null, "filter"));
	        				account.setLeftLabel(parser.getAttributeValue(null, "leftLabel"));
	        				account.setPadding(parser.getAttributeValue(null, "padding"));
	        				account.setMargin(parser.getAttributeValue(null, "margin"));
	        				account.setLayoutParamsTemplate(parser.getAttributeValue(null, "layoutParamsTemplateIds"));
	        				account.setRelativeComponents(parser.getAttributeValue(null, "relativeComponents"));
		        			viewPage.addAComponent(account);
		        			
	        			} else if("datePicker".equalsIgnoreCase(type)){
	        				OSDatePicker picker = new OSDatePicker(viewPage);
	        				picker.setId(strValidator(viewPage.getPageId(), "account", "id", parser.getAttributeValue(null, "id")));
	        				picker.setTemplate(parser.getAttributeValue(null, "templateId"));
	        				picker.setInterval(parser.getAttributeValue(null, "interval"));
	        				picker.setPadding(parser.getAttributeValue(null, "padding"));
	        				picker.setMargin(parser.getAttributeValue(null, "margin"));
	        				picker.setLayoutParamsTemplate(parser.getAttributeValue(null, "layoutParamsTemplateIds"));
	        				picker.setRelativeComponents(parser.getAttributeValue(null, "relativeComponents"));
	        				viewPage.addAComponent(picker);
	        			} 
	        			
	        		}else if("param".equalsIgnoreCase(parser.getName())){
	        			if(null != event){
	        				String id = parser.getAttributeValue(null, "id");
	        				event.addAParam(strValidator(viewPage.getPageId(), "param", "id", id));
	        				
	        				Param param = event.getParam(id);
	        				if(null != parser.getAttributeValue(null, "paramGroup")){
	        					param.setParamGroup(parser.getAttributeValue(null, "paramGroup"));
	        				}
	        			}
	        		}else if("_iframe_".equalsIgnoreCase(parser.getName())){
	        			viewPage.addAViewIndex(new Target());
	        			viewPage.setTarget(new Target());
	        			viewPage.setTemplate(null);
	        		}else if("hidden".equalsIgnoreCase(parser.getName())){
	        			Hidden hidden = new Hidden(viewPage);
	        			hidden.setId(strValidator(viewPage.getPageId(), "hidden", "id", parser.getAttributeValue(null, "id")));
	        			viewPage.addAComponent(hidden);
        				initData(hidden, parser.getAttributeValue(null, "value"));
        				
	        		}else if("layout".equalsIgnoreCase(parser.getName())){
	        			Layout layout = new Layout(viewPage);
	        			layout.setId(strValidator(viewPage.getPageId(), "layout", "id", parser.getAttributeValue(null, "id")));
	        			layout.setTemplate(strValidator(viewPage.getPageId(), "layout", "templateId", parser.getAttributeValue(null, "templateId")));
	        			layout.setPadding(parser.getAttributeValue(null, "padding"));
	        			layout.setMargin(parser.getAttributeValue(null, "margin"));
    					layout.setLayoutParamsTemplate(parser.getAttributeValue(null, "layoutParamsTemplateIds"));
    					layout.setRelativeComponents(parser.getAttributeValue(null, "relativeComponents"));
	        			
	        			String actionId   = parser.getAttributeValue(null, "actionId");
	        			if (null != actionId) {
		        			String action     = parser.getAttributeValue(null, "action");
	        				
	        				event = new Event(viewPage, actionId, action);
	        				String tmp = parser.getAttributeValue(null, "goHistory");
	        				Integer history	  = null == tmp ? null : Integer.parseInt(tmp);
	        				event.setGoHistory(history);
	        				event.setFsk(parser.getAttributeValue(null, "fsk"));
	        				event.setMethod(parser.getAttributeValue(null, "method"));
	        				event.setActionType(parser.getAttributeValue(null, "actionType"));
	        				event.setTransfer(parser.getAttributeValue(null, "transfer"));
	        				layout.setEvent(event);
	        				viewPage.addAnEvent(event);
	        			}
	        			layout.setFrame(true);
	        			viewPage.addAComponent(layout);
	        			layouts.add(layout);
	        			
	        		}else if("viewGroup".equalsIgnoreCase(parser.getName())){
	        			ViewGroup viewGroup = new ViewGroup(viewPage);
	        			viewGroup.setId(strValidator(viewPage.getPageId(), "viewGroup", "id", parser.getAttributeValue(null, "id")));
	        			viewGroup.setTemplate(strValidator(viewPage.getPageId(), "viewGroup", "templateId", parser.getAttributeValue(null, "templateId")));
	        			viewGroup.setPadding(parser.getAttributeValue(null, "padding"));
	        			viewGroup.setMargin(parser.getAttributeValue(null, "margin"));
	        			viewGroup.setLayoutParamsTemplate(parser.getAttributeValue(null, "layoutParamsTemplateIds"));
	        			viewGroup.setRelativeComponents(parser.getAttributeValue(null, "relativeComponents"));
	        			viewGroup.setFrame(true);
	        			viewPage.addAComponent(viewGroup);
	        			viewGroups.add(viewGroup);
	        		}else if("IF".equalsIgnoreCase(parser.getName())){
	        			If myIf = new If(viewPage, strValidator(viewPage.getPageId(), "If", "condition", parser.getAttributeValue(null, "condition")));
	        			myIf.setId(strValidator(viewPage.getPageId(), "If", "id", parser.getAttributeValue(null, "id")));
	        			viewPage.addAComponent(myIf);
	        			myIfs.add(myIf);
	        		}else if("ELSE".equalsIgnoreCase(parser.getName())){
	        			Else myElse = new Else(viewPage, parser.getAttributeValue(null, "condition"));
	        			myElse.setId(strValidator(viewPage.getPageId(), "Else", "id", parser.getAttributeValue(null, "id")));
	        			if (null == endIf) {
	        				throw new ViewException("Else target must be matched with If target!");
	        			}
	        			myElse.SetIf(endIf);
	        			endIf = null;
	        			viewPage.addAComponent(myElse);
	        			myElses.add(myElse);
	        		}
	        		
	        		break;
	        		
	        		
	        	case XmlPullParser.END_TAG:
	        		if("layout".equalsIgnoreCase(parser.getName())){
	        			if (layouts.size()<=0) {
	        				throw new ViewException("Layout target is not matched!");
	        			}
	        			viewPage.addAViewIndex(layouts.get(layouts.size()-1));
	        			layouts.remove(layouts.get(layouts.size()-1));
	        		} else if("viewGroup".equalsIgnoreCase(parser.getName())){
	        			if (viewGroups.size()<=0) {
	        				throw new ViewException("ViewGroup target is not matched!");
	        			}
	        			viewPage.addAViewIndex(viewGroups.get(viewGroups.size()-1));
	        			viewGroups.remove(viewGroups.get(viewGroups.size()-1));
	        		} else if("IF".equalsIgnoreCase(parser.getName())){
	        			if (myIfs.size()<=0) {
	        				throw new ViewException("If target is not matched!");
	        			}
	        			endIf = myIfs.get(myIfs.size()-1);
	        			viewPage.addAViewIndex(endIf);
	        			myIfs.remove(myIfs.get(myIfs.size()-1));
	        		} else if("ELSE".equalsIgnoreCase(parser.getName())){
	        			if (myElses.size()<=0) {
	        				throw new ViewException("Else target is not matched!");
	        			}
	        			viewPage.addAViewIndex(myElses.get(myElses.size()-1));
	        			myElses.remove(myElses.get(myElses.size()-1));
	        		}
	        		break;
	        	}
	        	
	        	eventType = parser.next();
	        }
	        
	        if(null != viewPage.getBody()){
				viewPage.getBody().init();
			}
	        
		}catch(Exception e){
			e.printStackTrace();
		} finally{
			try {
				if(null!=inputStream)
					inputStream.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		ViewContext.getInstance().addViewPage(viewPage);
		return viewPage; 
	}
	
	public static void parseDataXML(ViewPage viewPage, String xml) {
		ViewPage view = viewPage;
		String tagName = ""; // 有意义的起始标签的名字
		boolean isFromPrePage = false;
		InputStream inputStream = null;
    	try{
    		inputStream = StringUtil.getInputStream(xml);
    		Log.i("parser data", xml);
    		
			KXmlParser parser = new KXmlParser();  
			parser.setInput(inputStream,"utf-8");
			// 获取事件类型
	        int eventType = parser.getEventType();
	        while (eventType != XmlPullParser.END_DOCUMENT) {
	        	switch(eventType){
	        	case XmlPullParser.START_TAG:
	        		if("data".equalsIgnoreCase(parser.getName())){
	        			String pageId = parser.getAttributeValue(null, "pageId");
	        			view = ViewContext.getInstance().getViewPage(strValidator("Page", "data", "pageId", pageId));
	        			isFromPrePage = "FROMPREPAGE".equalsIgnoreCase(parser.getAttributeValue(null, "origin"))?true:false;
	        			
	        		} else if("item".equalsIgnoreCase(parser.getName())){
	        			String key = parser.getAttributeValue(null, "key");
	        			String value = parser.getAttributeValue(null, "value");
	        			
	        			OptionEntity optionEntity  = new OptionEntity();
        				optionEntity.setKey(null == key ? "":key);
        				optionEntity.setValue(null == value ? "": value);
        				
        				Component com = view.getComponent(tagName);
        				if(com instanceof OSSelect){
        					SelectEntity selectEntity = null == com.getValue()? new SelectEntity(): (SelectEntity)com.getValue();
        					selectEntity.addAnOptionEntity(optionEntity);
        					setScopeData(view,tagName,selectEntity);
        				}
        				
	        		}else{
	        			tagName = parser.getName();
	        			String value = "";
	        			if (isFromPrePage){
	        				value = (String) view.getPrePage().getPageValue(tagName);
	        			} else {
	        				value = parser.getAttributeValue(null, "value");
	        			}
	        			
	        			setScopeData(view,tagName,strValidator(view.getPageId(), tagName, "value", value));
	        		}
	        		break;
	        	}
	        	eventType = parser.next();
	        }
	        
		}catch(Exception e){
			e.printStackTrace();
			
		} finally{
			try {
				if (null!=inputStream)
				 inputStream.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
	}
	
	public static void parseTemplateXML(ViewPage viewPage, String xml){
		InputStream inputStream = null;
    	try{
    		 inputStream = AssetsUtil.getInputStreamFromPhone("template.xml");
    		
			KXmlParser parser = new KXmlParser();  
			parser.setInput(inputStream,"utf-8");
			// 获取事件类型
	        int eventType = parser.getEventType();
	        while (eventType != XmlPullParser.END_DOCUMENT) {
	        	switch(eventType){
	        	case XmlPullParser.START_TAG:
	        		if("label".equalsIgnoreCase(parser.getName())){
	        			CSSTemplate labelTem = new LabelTemplate(parser.getAttributeValue(null, "templateId"),null);
	        			labelTem.setColor(parser.getAttributeValue(null, "color"));
	        			labelTem.setSize(parser.getAttributeValue(null, "size"));
	        			labelTem.setBgImage(parser.getAttributeValue(null, "bgImg"));
	        			labelTem.setGravity(parser.getAttributeValue(null, "gravity"));
	        			((LabelTemplate) labelTem).setBold(parser.getAttributeValue(null, "bold"));
	        			ViewContext.getInstance().addACssTemplate(labelTem);
	        			if (null != parser.getAttributeValue(null, "default")) { // 设置默认模板，配置文件中每一类必须有一项是default
	        				ParseView.TEMPLATE_DEFAULT_LABEL = labelTem.getId();
	        			}
	        		}else if("button".equalsIgnoreCase(parser.getName())){
	        			CSSTemplate buttonTem = new ButtonTemplate(parser.getAttributeValue(null, "templateId"),null);
	        			buttonTem.setColor(parser.getAttributeValue(null, "color"));
	        			buttonTem.setSize(parser.getAttributeValue(null, "size"));
	        			buttonTem.setBgImage(parser.getAttributeValue(null, "bgImg"));
	        			((ButtonTemplate) buttonTem).setBgImg_down(parser.getAttributeValue(null, "bgImg_down"));
	        			buttonTem.setGravity(parser.getAttributeValue(null, "gravity"));
	        			ViewContext.getInstance().addACssTemplate(buttonTem);
	        			if (null != parser.getAttributeValue(null, "default")) { // 设置默认模板，配置文件中每一类必须有一项是default
	        				ParseView.TEMPLATE_DEFAULT_BUTTON = buttonTem.getId();
	        			}
	        		}else if("text".equalsIgnoreCase(parser.getName())){
	        			CSSTemplate textTem = new TextTemplate(parser.getAttributeValue(null, "templateId"), null);
	        			textTem.setColor(parser.getAttributeValue(null, "color"));
	        			textTem.setSize(parser.getAttributeValue(null, "size"));
	        			textTem.setGravity(parser.getAttributeValue(null, "gravity"));
	        			textTem.setBgImage(parser.getAttributeValue(null, "bgImg"));
	        			ViewContext.getInstance().addACssTemplate(textTem);
	        			if (null != parser.getAttributeValue(null, "default")) { // 设置默认模板，配置文件中每一类必须有一项是default
	        				ParseView.TEMPLATE_DEFAULT_TEXT = textTem.getId();
	        			}
	        		}else if("image".equalsIgnoreCase(parser.getName())){
	        			CSSTemplate imageTem = new ImageViewTemplate(parser.getAttributeValue(null, "templateId"),null);
//	        			((ImageViewTemplate) imageTem).setPadding(parser.getAttributeValue(null, "padding"));
	        			((ImageViewTemplate) imageTem).setAdjustViewBounds(parser.getAttributeValue(null, "adjustViewBounds"));
	        			((ImageViewTemplate) imageTem).setMaxHeight(parser.getAttributeValue(null, "maxHeight"));
	        			((ImageViewTemplate) imageTem).setMaxWidth(parser.getAttributeValue(null, "maxWidth"));
	        			ViewContext.getInstance().addACssTemplate(imageTem);
	        			if (null != parser.getAttributeValue(null, "default")) { // 设置默认模板，配置文件中每一类必须有一项是default
	        				ParseView.TEMPLATE_DEFAULT_IMAGEVIEW = imageTem.getId();
	        			}
	        		}else if("struct".equalsIgnoreCase(parser.getName())){
	        			FunctionPageTemplate st = new FunctionPageTemplate(parser.getAttributeValue(null, "templateId"), null);
	        			
	        			ViewContext.getInstance().addAStructTemplate(st);
	        			if (null != parser.getAttributeValue(null, "default")) { // 设置默认模板，配置文件中每一类必须有一项是default
	        				ParseView.TEMPLATE_DEFAULT_STRUCT_FP = st.getId();
	        			}
	        		}else if("linearLayout".equalsIgnoreCase(parser.getName())){
	        			LinearLayoutTemplate linearLayoutTemplate = new LinearLayoutTemplate(parser.getAttributeValue(null, "templateId"),null);
	        			linearLayoutTemplate.setOrientation(parser.getAttributeValue(null, "orientation"));
	        			linearLayoutTemplate.setGravity(parser.getAttributeValue(null, "gravity"));
	        			linearLayoutTemplate.setBackgroundColor(parser.getAttributeValue(null, "backgroundColor"));
	        			linearLayoutTemplate.setBgImg(parser.getAttributeValue(null, "bgImg"));
//	        			linearLayoutTemplate.setPadding(parser.getAttributeValue(null, "padding"));
	        			linearLayoutTemplate.setWidth(parser.getAttributeValue(null, "width"));
	        			linearLayoutTemplate.setHeight(parser.getAttributeValue(null, "height"));
	        			if (null != parser.getAttributeValue(null, "default")) { // 设置默认模板，配置文件中每一类必须有一项是default
	        				ParseView.TEMPLATE_DEFAULT_LAYOUT = linearLayoutTemplate.getId();
	        			}
	        			
	        			ViewContext.getInstance().addALayoutTemplate(linearLayoutTemplate);
	        			
	        		}else if("relativeLayout".equalsIgnoreCase(parser.getName())){
	        			RelativeLayoutTemplate relativeLayoutTemplate = new RelativeLayoutTemplate(parser.getAttributeValue(null, "templateId"),null);
	        			// RelativeyLayout中不要使用Gravity属性
	        			// relativeLayoutTemplate.setGravity(parser.getAttributeValue(null, "gravity"));
	        			relativeLayoutTemplate.setBackgroundColor(parser.getAttributeValue(null, "backgroundColor"));
	        			relativeLayoutTemplate.setBgImg(parser.getAttributeValue(null, "bgImg"));
//	        			relativeLayoutTemplate.setPadding(parser.getAttributeValue(null, "padding"));
	        			relativeLayoutTemplate.setWidth(parser.getAttributeValue(null, "width"));
	        			relativeLayoutTemplate.setHeight(parser.getAttributeValue(null, "height"));
	        			if (null != parser.getAttributeValue(null, "default")) { // 设置默认模板，配置文件中每一类必须有一项是default
	        				ParseView.TEMPLATE_DEFAULT_LAYOUT = relativeLayoutTemplate.getId();
	        			}
	        			
	        			ViewContext.getInstance().addALayoutTemplate(relativeLayoutTemplate);
	        			
	        		}else if("gridView".equalsIgnoreCase(parser.getName())){
	        			GridViewTemplate gridViewTemplate = new GridViewTemplate(parser.getAttributeValue(null, "templateId"),null);
	        			gridViewTemplate.setBgImg(parser.getAttributeValue(null, "bgImg"));
	        			gridViewTemplate.setColumnWidth(parser.getAttributeValue(null, "columnWidth"));
	        			gridViewTemplate.setNumColumns(parser.getAttributeValue(null, "numColumns"));
        				gridViewTemplate.setVerticalSpacing(parser.getAttributeValue(null, "verticalSpacing"));
        				gridViewTemplate.setHorizontalSpacing(parser.getAttributeValue(null, "horizontalSpacing"));
	        			
	        			ViewContext.getInstance().addAViewGroupTemplates(gridViewTemplate);
	        			
	        		}else if("listView".equalsIgnoreCase(parser.getName())){
	        			ListViewTemplate listViewTemplate = new ListViewTemplate(parser.getAttributeValue(null, "templateId"),null);
	        			listViewTemplate.setBgImg(parser.getAttributeValue(null, "bgImg"));
	        			
	        			ViewContext.getInstance().addAViewGroupTemplates(listViewTemplate);
	        			
	        		}else if("linearLayoutParams".equals(parser.getName())){
	        			LinearLayoutParamsTemplate linearLayoutParamsTemplate = new LinearLayoutParamsTemplate(parser.getAttributeValue(null, "templateId"),null);
	        			linearLayoutParamsTemplate.setLayoutWeight(parser.getAttributeValue(null, "weight"));
	        			linearLayoutParamsTemplate.setHeight(parser.getAttributeValue(null, "height"));
	        			linearLayoutParamsTemplate.setWidth(parser.getAttributeValue(null, "width"));
	        			
	        			ViewContext.getInstance().addAViewGroupLayoutParamsTemplates(linearLayoutParamsTemplate);
	        			
	        		}else if("relativeLayoutParams".equals(parser.getName())){
	        			RelativeLayoutParamsTemplate relativeLayoutParamsTemplate = new RelativeLayoutParamsTemplate(parser.getAttributeValue(null, "templateId"),null);
	        			relativeLayoutParamsTemplate.setRule(parser.getAttributeValue(null, "rule"));
	        			relativeLayoutParamsTemplate.setHeight(parser.getAttributeValue(null, "height"));
	        			relativeLayoutParamsTemplate.setWidth(parser.getAttributeValue(null, "width"));
	        			
	        			ViewContext.getInstance().addAViewGroupLayoutParamsTemplates(relativeLayoutParamsTemplate);
	        			
	        		}
	        		break;
	        	}
	        	eventType = parser.next();
	        }
	        
		} catch(Exception e){
			e.printStackTrace();
		} finally{
			try {
				if (null!= inputStream)
					inputStream.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	public static ViewPage parseXML(ViewPage viewPage, String xml) {
		ViewPage view = viewPage;
		
//		// 模板只初始化一次
		if(!hasInitTemplate){
			parseTemplateXML(view,null);
			hasInitTemplate = true;
		}
		
		String[] bodys = xml.split("</body>");
		for (String body:bodys) {
			String[] datas = body.split("</data>");
			for (String data:datas) {
//				if (data.contains("<body")) { 
//					view = paraseViewXML(view, data);
//				} else if(data.contains("<data")) { 
//					parseDataXML(view, data);
//				}
				
				// 用于解析XML中含有template的情况。
				String[] templates = data.split("</template>");
				for(String template:templates) {
					if (template.contains("<body")) { 
						view = paraseViewXML(view, template);
					} else if(template.contains("<data")) { 
						parseDataXML(view, template);
					} else if(template.contains("<template")) {
						parseTemplateXML(view, template);
					}
				}
			}
		}
		
		return view;
	}
	
	private static void initData(Component c, Object data) {
		if(null == data || 0 == data.toString().trim().length()){
			//return;
			setScopeData(c.getViewPage(), c.getId(), "");
			return;
		}
		
		setScopeData(c.getViewPage(), c.getId(), data);
	}
	
	public static void setScopeData(ViewPage viewPage, String componentId, Object value) {
		// 在这里做扩展，如果需要执行本地方法取得一些固定的参数值，则在这里进行特殊的处理
		if (value instanceof String && ((String)value).startsWith("__")){
			value = AppDataCenter.getValue((String)value);
		}
		
		Component component = viewPage.getComponent(componentId);
		if (null == component) {
			if (component instanceof OSSelect){
				@SuppressWarnings("unchecked")
				HashMap<String, String> map = (HashMap<String, String>) value;
				for (String key : map.keySet()){
	    			OptionEntity optionEntity  = new OptionEntity();
					optionEntity.setKey(key);
					optionEntity.setValue(map.get(key));
					
					try{
						SelectEntity selectEntity = (null == component.getValue()? new SelectEntity(): (SelectEntity)component.getValue());
						selectEntity.addAnOptionEntity(optionEntity);
						setScopeData(viewPage,componentId,selectEntity);
						
					} catch(Exception e){
						e.printStackTrace();
					}
				}
				
			} else {
				viewPage.addAPageValue(componentId, value);
			}
			
			return;
		}
		
		viewPage.getComponent(componentId).getViewPage().addAPageValue(componentId, value);
	}
	
	public static boolean isComponentTarget(Object object) {
		return null != object && object.toString().startsWith("{") && object.toString().endsWith("}");
	}
	public static String parseComponentTarget(Object object) {
		return object.toString().substring(1, object.toString().length()-1);
	}
	public static String toComponentTarget(Object object) {
		return "{" + object.toString().trim() +"}";
	}
	
	public static boolean isDataStrEnter (String str) {
		return null != str && 0 != str.trim().length();
	}
	public static boolean isDataStrMustBeEntered (String page, String component, String key, String value) throws ViewException {
		if (!isDataStrEnter(value)) {
			throw new ViewException("The Component["+component+"]'s Key:["+key+"] of page["+page+"] must be entered!");
		}
		return true;
	}
	public static String strValidator (String page, String component, String key, String value) throws ViewException {
		isDataStrMustBeEntered(page,component, key, value);
		return value;
	}
}
