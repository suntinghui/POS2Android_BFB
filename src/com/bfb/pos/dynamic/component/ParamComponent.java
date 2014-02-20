package com.bfb.pos.dynamic.component;

import com.bfb.pos.dynamic.core.ViewPage;

public abstract class ParamComponent extends Component {
	public ParamComponent(ViewPage viewPage) {
		this.viewPage = viewPage;
	}
	
	public ParamComponent(ViewPage viewPage, String paramId) {
		this.viewPage = viewPage;
		this.setId(paramId);
	}
	@Override
	public Object getValue() throws ViewException {
		if (this.isParamGroup()) {
			this.getViewPage().addAPageValue(this.getParamGroup(), this.getViewPage().getPageValue(this));
		}
		return this.getViewPage().getPageValue(this);
	}
	private boolean isParamGroup() {
		return (null != this.getParamGroup());
	}
}
