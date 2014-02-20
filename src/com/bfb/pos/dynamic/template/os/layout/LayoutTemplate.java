package com.bfb.pos.dynamic.template.os.layout;

import java.util.Vector;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;

import com.bfb.pos.activity.BaseActivity;
import com.bfb.pos.dynamic.component.Component;
import com.bfb.pos.dynamic.component.ViewException;
import com.bfb.pos.dynamic.component.os.StructComponent;
import com.bfb.pos.dynamic.component.os.frame.FrameComponent;
import com.bfb.pos.dynamic.core.ViewContext;
import com.bfb.pos.dynamic.template.Template;

public abstract class LayoutTemplate extends Template implements ILayoutTemplate{

	public LayoutTemplate(String id, String name) {
		super(id, name);
	}
	
	public abstract ViewGroup initLayout(Context context);
	
	@Override
	public final View rewind(Vector<Component> components) throws ViewException {
		if (null == components) {
			throw new ViewException("Params is null when excute rewind method,Layout template["+this.getId()+"]");
		}
		Vector<Component> frameVector = new Vector<Component>();
		Component frameComponent = null;
		boolean isFrame = false;
		ViewGroup layout = null;
		try {
			for (Component component:components) {
				if (null == layout) {
					layout = this.initLayout(BaseActivity.getTopActivity());
				}
				/**
				 * 判断是否是frame类型的组件
				 * 此类型的设置是在模板解析时设置的
				 */
				if (component.isFrame()) { 
					isFrame = true; // 打开被一组标签内的开关
					if (null == frameComponent) {
						frameComponent = component; 
						continue;
					} else if (frameComponent.equals(component)) { // 判断同一个标签结束位置
						isFrame = false; // 关闭被一组标签内的开关
					}
				}
				if (component instanceof StructComponent || // 界面要素
						component.isFrame()) { // Frame
					if (isFrame) {	
						frameVector.add(component);
					} else {
						// add frameComponent
						if (null != frameVector && 0 != frameVector.size()) {
							layout.addView(((FrameComponent) frameComponent).toFrame(frameVector));
							frameVector = new Vector<Component>();
							frameComponent = null;
						}
						if (component instanceof StructComponent) {
							layout.addView(ViewContext.getInstance().cssRewind(((StructComponent)component).toComponent(), ((StructComponent)component).getTemplate()));
						}
					}
				}
			}
		} catch (ViewException e) {
			throw e;
		} catch (Exception e) {
			e.printStackTrace();
			
		}
		return layout;
	}
}
