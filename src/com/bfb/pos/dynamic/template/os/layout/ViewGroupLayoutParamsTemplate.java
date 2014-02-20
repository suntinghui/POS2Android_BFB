/**
 * 
 */
package com.bfb.pos.dynamic.template.os.layout;

import android.view.ViewGroup;

import com.bfb.pos.dynamic.component.Component;
import com.bfb.pos.dynamic.component.ViewException;
import com.bfb.pos.dynamic.template.Template;

/**
 * @author DongXiaoping
 */
public abstract class ViewGroupLayoutParamsTemplate extends Template {
	private String height;
	private String width;
	public ViewGroupLayoutParamsTemplate(String id, String name) {
		super(id, name);
	}
	public String getHeight() {
		return height;
	}
	public void setHeight(String height) {
		this.height = height;
	}
	public String getWidth() {
		return width;
	}
	public void setWidth(String width) {
		this.width = width;
	}
	public ViewGroup.LayoutParams toLayoutParams(ViewGroup.LayoutParams lp, Integer[] componentTargetId, Integer index) throws ViewException {
		ViewGroup.LayoutParams layoutParams = null;
		try {
			layoutParams = new ViewGroup.LayoutParams(
					null == this.getWidth()? ViewGroup.LayoutParams.WRAP_CONTENT: Integer.parseInt(this.getWidth()),
							null == this.getHeight() ?ViewGroup.LayoutParams.WRAP_CONTENT :Integer.parseInt(this.getHeight()));
		} catch (Exception e) {
			throw new ViewException("LayoutParamsTemplate["+this.getId()+"],the type of height or width must be Integer!");
		}
		return layoutParams;
	}
}
