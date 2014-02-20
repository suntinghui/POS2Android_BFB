package com.bfb.pos.dynamic.component.os.frame;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import android.view.View;

import com.bfb.pos.dynamic.component.Component;
import com.bfb.pos.dynamic.component.ViewException;
import com.bfb.pos.dynamic.core.ViewContext;
import com.bfb.pos.dynamic.core.ViewPage;
import com.bfb.pos.dynamic.model.ViewGroupBean;
/**
 * ViewGroup组件，注意该组件下必须循环包含FrameComponent标签组
 * @author Xiaoping Dong
 *
 */
public class ViewGroup extends FrameComponent {

	public ViewGroup(ViewPage viewPage) {
		super(viewPage);
	}

	@Override
	public View toOSFrame(Vector<Component> components) throws ViewException {
		/**
		 * ViewGroupBean，承载beanList与其的数据
		 */
		List<ViewGroupBean> viewGroupBeans = new ArrayList<ViewGroupBean>();
		/**
		 * layout规律组，用于校验
		 */
		List<Component> viewGroup = new ArrayList<Component>(); 
		FrameComponent layout = null;
		boolean isGroupFirst = false;
		boolean isGroupOther = false;
		int groupSize = 0;
		for(Component component:components) {
			if (null == layout) {
				if (!(component instanceof FrameComponent)) {
					throw new ViewException("ViewGroup must start with frameComponent target!");
				}
				layout 	= (FrameComponent) component;
				isGroupFirst = true;
				/**
				 * 创建ViewGroupBean，并为其注册可能存在的event
				 */
				ViewGroupBean bean = new ViewGroupBean();
				bean.setEvent(layout.getEvent());
				bean.addAComponent(component);
				
				viewGroupBeans.add(bean);
				viewGroup.add(component);
				/**
				 * 组校验，要求每组组件的类型必须严格匹配
				 */
				this.isMatched(viewGroup, groupSize++, component);
				continue;
			}
			if (layout.equals(component)) {
				/**
				 * 校验组，只用一次，以第一个循环layout为准
				 */
				if (isGroupFirst) {
					viewGroup.add(component);
					isGroupFirst = false;
				}
				/**
				 * 组校验，要求每组组件的类型必须严格匹配
				 */
				this.isMatched(viewGroup, groupSize++, component);
				/**
				 * 将数据加载到viewGoupBean中
				 */
				viewGroupBeans.get(viewGroupBeans.size()-1).addAComponent(component);
				isGroupOther = false;
				continue;
			}
			if (isGroupFirst||isGroupOther) {
				/**
				 * 校验组，只用一次，以第一个循环layout为准
				 */
				if (isGroupFirst) {
					viewGroup.add(component);
				}
				/**
				 * 组校验，要求每组组件的类型必须严格匹配
				 */
				this.isMatched(viewGroup, groupSize++, component);
				/**
				 * 将数据加载到viewGoupBean中
				 */
				viewGroupBeans.get(viewGroupBeans.size()-1).addAComponent(component);
			} else {
				if (!(component instanceof FrameComponent)) {
					throw new ViewException("ViewGroup,frameComponent target must be matched!");
				}
				layout 	= (Layout) component;
				isGroupOther = true;
				
				/**
				 * 组校验，要求每组组件的类型必须严格匹配
				 */
				groupSize = 0;
				this.isMatched(viewGroup, groupSize++, component);
				/**
				 * 创建ViewGroupBean，并为其注册可能存在的event
				 */
				ViewGroupBean bean = new ViewGroupBean();
				bean.setEvent(layout.getEvent());
				bean.addAComponent(component);
				
				viewGroupBeans.add(bean);
			}
		}
		View ret = ViewContext.getInstance().viewGroupRewind(this.getTemplate(), viewGroupBeans);
		return ret;
	}

	@Override
	protected Component construction(ViewPage viewPage) {
		return new ViewGroup(viewPage);
	}
	private void isMatched(List<Component> viewGroup, int srcIndex, Component component) throws ViewException {
//		if (viewGroup.get(srcIndex).getClass() == component.getClass()) {
//			throw new ViewException("ViewGroup target group is not matched!");
			// TODO 类型比较
//		}
	}
}
