/**
 * 
 */
package com.bfb.pos.dynamic.template.os.layout;

import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.bfb.pos.dynamic.component.ViewException;

/**
 * @author sth 
 *
 */
public class LinearLayoutParamsTemplate extends ViewGroupLayoutParamsTemplate {
	
	private String layoutWeight;
	
	/**
	 * @param id
	 * @param name
	 */
	public LinearLayoutParamsTemplate(String id, String name) {
		super(id, name);
	}
	
	public String getLayoutWeight() {
		return layoutWeight;
	}

	public void setLayoutWeight(String layoutWeight) {
		this.layoutWeight = layoutWeight;
	}

	@Override
	public ViewGroup.LayoutParams toLayoutParams(ViewGroup.LayoutParams lp, Integer[] componentTargetId, Integer index) throws ViewException {
		LinearLayout.LayoutParams layoutParams = null;
		try {
			if (null == lp) {
				layoutParams = new LinearLayout.LayoutParams(
						null == this.getWidth()? ViewGroup.LayoutParams.WRAP_CONTENT: Integer.parseInt(this.getWidth()),
								null == this.getHeight() ?ViewGroup.LayoutParams.WRAP_CONTENT :Integer.parseInt(this.getHeight()));
			} 
			
			// weight's default value is 0.
			layoutParams.weight = null == this.getLayoutWeight() ? 0 : Integer.parseInt(layoutWeight);
			
		} catch(java.lang.ClassCastException ce) {
			throw new ViewException("LayoutParamsTemplate["+this.getId()+"],class cast exception!");
		} catch (Exception e) {
			throw new ViewException("LayoutParamsTemplate["+this.getId()+"],the type of height or width must be Integer!");
		}
		return layoutParams;
	}
}
