package com.bfb.pos.dynamic.template.os.layout;

import java.util.Vector;

import com.bfb.pos.dynamic.component.Component;
import com.bfb.pos.dynamic.component.ViewException;

import android.view.View;

public interface ILayoutTemplate {
	
	public View rewind(Vector<Component> components) throws ViewException;

}
