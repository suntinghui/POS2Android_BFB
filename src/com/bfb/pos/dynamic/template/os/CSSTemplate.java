/**
 * 
 */
package com.bfb.pos.dynamic.template.os;

import java.util.HashMap;

import android.graphics.Color;
import android.util.Log;
import android.view.Gravity;
import android.widget.RelativeLayout;

import com.bfb.pos.dynamic.template.Template;


/**
 * 样式模板父类
 * @author DongXiaoping
 *
 */
public abstract class CSSTemplate extends Template implements IPageTemplate {
	protected String color;
	private String size;
	protected String bgImg;
	private String gravity;
	private String lineSpacingExtra;
	
	public CSSTemplate(String id, String name) {
		super(id, name);
	}
	
	public Integer getSize() {
		try{
			return null == this.size ? null : Integer.valueOf(size);
		}catch(Exception e){
			Log.i("dynamic", "size must be Integer.");
		}
		
		return null;
	}
	
	public Integer getColor() {
		if(null == this.color){
			return null;
		}
		
		if ("BLACK".equalsIgnoreCase(this.color)) {
			return Color.BLACK;
		} else if ("BLUE".equalsIgnoreCase(this.color)) {
			return Color.BLUE;
		} else if ("CYAN".equalsIgnoreCase(this.color)) {
			return Color.CYAN;
		} else if ("DKGRAY".equalsIgnoreCase(this.color)) {
			return Color.DKGRAY;
		} else if ("GRAY".equalsIgnoreCase(this.color)) {
			return Color.GRAY;
		} else if ("GREEN".equalsIgnoreCase(this.color)) {
			return Color.GREEN;
		} else if ("LTGRAY".equalsIgnoreCase(this.color)) {
			return Color.LTGRAY;
		} else if ("MAGENTA".equalsIgnoreCase(this.color)) {
			return Color.MAGENTA;
		} else if ("RED".equalsIgnoreCase(this.color)) {
			return Color.RED;
		} else if ("TRANSPARENT".equalsIgnoreCase(this.color)) {
			return Color.TRANSPARENT;
		} else if ("WHITE".equalsIgnoreCase(this.color)) {
			return Color.WHITE;
		} else if ("YELLOW".equalsIgnoreCase(this.color)) {
			return Color.YELLOW;
		} else if ("ORANGE".equalsIgnoreCase(this.color)){
			return Color.parseColor("#f4850d");
		} 
		return Color.DKGRAY;
	}
	
	public String getBgImage() {
		return bgImg;
	}
	
	public Integer getGravity() {
		if(null == this.gravity){
			return null;
		}
		
		if("CENTER".equalsIgnoreCase(gravity))
			return Gravity.CENTER;
		else if("CENTER_HORIZONTAL".equalsIgnoreCase(gravity))
			return Gravity.CENTER_HORIZONTAL;
		else if("CENTER_VERTICAL".equals(gravity))
			return Gravity.CENTER_VERTICAL;
		else if("LEFT".equals(gravity))
			return Gravity.LEFT;
		else if("RIGHT".equals(gravity))
			return Gravity.RIGHT;
		else if("BOTTOM".equals(gravity))
			return Gravity.BOTTOM;
		else if("TOP".equals(gravity))
			return Gravity.TOP;
		
		return Gravity.LEFT;
	}

	public void setGravity(String gravity) {
		this.gravity = gravity;
	}
	
	public void setColor(String color) {
		this.color = color;
	}
	
	public void setSize(String size) {
		this.size = size;
	}
	
	public void setBgImage(String bgImg) {
		this.bgImg = bgImg;
	}

}
