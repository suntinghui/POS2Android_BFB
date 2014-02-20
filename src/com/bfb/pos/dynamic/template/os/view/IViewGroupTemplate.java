package com.bfb.pos.dynamic.template.os.view;

import java.util.List;
import java.util.Vector;

import com.bfb.pos.dynamic.component.Component;
import com.bfb.pos.dynamic.component.ViewException;
import com.bfb.pos.dynamic.model.ViewGroupBean;

import android.content.Context;
import android.view.View;

public interface IViewGroupTemplate {
	
	public View rewind(List<ViewGroupBean> components) throws ViewException;

}
