package com.bfb.pos.dynamic.component;

import android.view.View;

public interface ITransView {
	public abstract View toComponent() throws ViewException;
}
