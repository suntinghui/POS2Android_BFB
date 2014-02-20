/**
 * 
 */
package com.bfb.pos.dynamic.template.os.layout;

import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.bfb.pos.dynamic.component.ViewException;

/**
 * @author DongXiaoping 
 *
 */
public class RelativeLayoutParamsTemplate extends
		ViewGroupLayoutParamsTemplate {
	private String rule;
	
	/**
	 * @param id
	 * @param name
	 */
	public RelativeLayoutParamsTemplate(String id, String name) {
		super(id, name);
		// TODO Auto-generated constructor stub
	}
	
	public String getRule() {
		return rule;
	}

	public void setRule(String rule) {
		this.rule = rule;
	}
	
	public RelativeLayout.LayoutParams getRelativeRule(RelativeLayout.LayoutParams layoutParams, Integer[] componentTargetId, Integer index) throws ViewException {
		// 不需要指定 anchor， 其 anchor 自动为 Parent View
		if ("ALIGN_PARENT_LEFT".equalsIgnoreCase(this.getRule()))
			layoutParams.addRule(RelativeLayout.ALIGN_PARENT_LEFT, RelativeLayout.TRUE);
		else if ("ALIGN_PARENT_RIGHT".equalsIgnoreCase(this.getRule()))
			layoutParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT, RelativeLayout.TRUE);
		else if ("ALIGN_PARENT_BOTTOM".equalsIgnoreCase(this.getRule()))
			layoutParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, RelativeLayout.TRUE);
		else if ("ALIGN_PARENT_TOP".equalsIgnoreCase(this.getRule()))
			layoutParams.addRule(RelativeLayout.ALIGN_PARENT_TOP, RelativeLayout.TRUE);

		// 如果 anchor 为 TRUE，在 Parent 中 水平居中/水平和垂直均居中/垂直居中。
		else if ("CENTER_HORIZONTAL".equalsIgnoreCase(this.getRule()))
			layoutParams.addRule(RelativeLayout.CENTER_HORIZONTAL, RelativeLayout.TRUE);
		else if ("CENTER_IN_PARENT".equalsIgnoreCase(this.getRule()))
			layoutParams.addRule(RelativeLayout.CENTER_IN_PARENT, RelativeLayout.TRUE);
		else if ("CENTER_VERTICAL".equalsIgnoreCase(this.getRule()))
			layoutParams.addRule(RelativeLayout.CENTER_VERTICAL, RelativeLayout.TRUE);

		// 本 View 的 底边/左边/右边/顶边 和 anchor 指定的 View 的 底边/左边/右边/顶边 对齐。
		else if ("ALIGN_BOTTOM".equalsIgnoreCase(this.getRule()))
			layoutParams.addRule(RelativeLayout.ALIGN_BOTTOM, this.parseID(componentTargetId, index));
		else if ("ALIGN_LEFT".equalsIgnoreCase(this.getRule()))
			layoutParams.addRule(RelativeLayout.ALIGN_LEFT, this.parseID(componentTargetId, index));
		else if ("ALIGN_TOP".equalsIgnoreCase(this.getRule()))
			layoutParams.addRule(RelativeLayout.ALIGN_TOP, this.parseID(componentTargetId, index));
		else if ("ALIGN_RIGHT".equalsIgnoreCase(this.getRule()))
			layoutParams.addRule(RelativeLayout.ALIGN_RIGHT, this.parseID(componentTargetId, index));
		else if ("ALIGN_BASELINE".equalsIgnoreCase(this.getRule()))
			layoutParams.addRule(RelativeLayout.ALIGN_BASELINE, this.parseID(componentTargetId, index));
		
		// 本 View 位于 anchor 指定的 View 的 上边/下边/左边/右边。
		else if ("BELOW".equalsIgnoreCase(this.getRule()))
			layoutParams.addRule(RelativeLayout.BELOW, this.parseID(componentTargetId, index));
		else if ("ABOVE".equalsIgnoreCase(this.getRule()))
			layoutParams.addRule(RelativeLayout.ABOVE, this.parseID(componentTargetId, index));
		else if ("LEFT_OF".equalsIgnoreCase(this.getRule()))
			layoutParams.addRule(RelativeLayout.LEFT_OF, this.parseID(componentTargetId, index));
		else if ("RIGHT_OF".equalsIgnoreCase(this.getRule()))
			layoutParams.addRule(RelativeLayout.RIGHT_OF, this.parseID(componentTargetId, index));
		return layoutParams;
	}
	
	private int parseID(Integer[] id, Integer index) throws ViewException {
		if(null == id) {
			throw new ViewException("Relative component is not exist!");
		}
		try {
			return id[index++];
		} catch (Exception e) {
			throw new ViewException("Relative component is not enough!");
		}
	}
	
//	@Override
//	public ViewGroup.LayoutParams toLayoutParams(ViewGroup.LayoutParams lp, Integer[] componentTargetId, Integer index) throws ViewException {
//		RelativeLayout.LayoutParams layoutParams = null;
//		try {
//			layoutParams = new RelativeLayout.LayoutParams(
//					null == this.getWidth()? ViewGroup.LayoutParams.WRAP_CONTENT: Integer.parseInt(this.getWidth()),
//							null == this.getHeight() ?ViewGroup.LayoutParams.WRAP_CONTENT :Integer.parseInt(this.getHeight()));
//			if (null != componentTargetId) {
//				layoutParams.addRule(RelativeLayout.LEFT_OF, componentTargetId[0]);
//			} else {
//				layoutParams.addRule(RelativeLayout.ALIGN_PARENT_LEFT, RelativeLayout.TRUE);
//			}
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//		return layoutParams;
//	}
	
	@Override
	public ViewGroup.LayoutParams toLayoutParams(ViewGroup.LayoutParams lp, Integer[] componentTargetId, Integer index) throws ViewException {
		RelativeLayout.LayoutParams layoutParams = null;
		try {
			if (null == lp) {
				layoutParams = new RelativeLayout.LayoutParams(
						null == this.getWidth()? ViewGroup.LayoutParams.WRAP_CONTENT: Integer.parseInt(this.getWidth()),
								null == this.getHeight() ?ViewGroup.LayoutParams.WRAP_CONTENT :Integer.parseInt(this.getHeight()));
				layoutParams = this.getRelativeRule(layoutParams, componentTargetId, index);
			} else {
				layoutParams = this.getRelativeRule((RelativeLayout.LayoutParams) lp, componentTargetId, index);
			}
		} catch(java.lang.ClassCastException ce) {
			throw new ViewException("LayoutParamsTemplate["+this.getId()+"],class cast exception!");
		} catch(ViewException ve) {
			throw ve;
		} catch (Exception e) {
			throw new ViewException("LayoutParamsTemplate["+this.getId()+"],the type of height or width must be Integer!");
		}
		return layoutParams;
	}
}
