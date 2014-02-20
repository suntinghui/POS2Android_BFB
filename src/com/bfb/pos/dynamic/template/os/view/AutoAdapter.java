package com.bfb.pos.dynamic.template.os.view;

import java.util.List;
import java.util.Vector;

import com.bfb.pos.dynamic.component.Component;
import com.bfb.pos.dynamic.component.ViewException;
import com.bfb.pos.dynamic.component.ViewRuntimeException;
import com.bfb.pos.dynamic.component.os.frame.FrameComponent;
import com.bfb.pos.dynamic.model.ViewGroupBean;

import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

public class AutoAdapter extends BaseAdapter {
	
	private List<ViewGroupBean> viewGroupBeans = null;
	private static int count = 0;
	
	public void setViewGroupBean(List<ViewGroupBean> viewGroupBeans) {
		this.viewGroupBeans = viewGroupBeans;
	}
	public List<ViewGroupBean> getViewGroupBeans() {
		return this.viewGroupBeans;
	}

	@Override
	public int getCount() {
		return this.getViewGroupBeans().size();
	}

	@Override
	public Object getItem(int position) {
		return position;
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		Vector<Component> frameVector = null;
		FrameComponent frameComponent = null;
		ViewGroupBean viewBean = this.getViewGroupBeans().get(position);
		View ret = null;
		for (Component component:viewBean.getComponents()) {
			if (null == frameVector) {
				frameVector = new Vector<Component>();
				frameComponent = (FrameComponent) component;
				continue;
			}
			if (frameComponent.equals(component)) {
				try {
					return ret = frameComponent.toFrame(frameVector);
				} catch (Exception e) {
					throw new ViewRuntimeException(e.getMessage());
				}
			}
			frameVector.add(component);
		}
		return ret;
	}

}
