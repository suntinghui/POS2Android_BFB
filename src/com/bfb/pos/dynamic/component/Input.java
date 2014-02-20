package com.bfb.pos.dynamic.component;

import com.bfb.pos.dynamic.component.os.StructComponent;
import com.bfb.pos.dynamic.core.ViewPage;

public abstract class Input extends StructComponent {
	
	public Input(ViewPage viewPage) {
		super(viewPage);
	}
	
	// 加载用户输入
	public abstract void loadInputValue();
	
	// 验证输入的合法性
	public abstract boolean validator();

}
