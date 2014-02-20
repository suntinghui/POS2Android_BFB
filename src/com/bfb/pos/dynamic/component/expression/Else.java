/**
 * 
 */
package com.bfb.pos.dynamic.component.expression;

import com.bfb.pos.dynamic.component.Component;
import com.bfb.pos.dynamic.component.ViewException;
import com.bfb.pos.dynamic.core.ViewPage;

/**
 * @author Administrator
 *
 */
public class Else extends Logic {
	private If belongIf;
	
	public Else(ViewPage viewPage, String conditionRegex) {
		super(viewPage, conditionRegex);
	}

	/* (non-Javadoc)
	 * @see com.zhc.mbank.dynamic.component.Component#construction(com.zhc.mbank.dynamic.core.ViewPage)
	 */
	@Override
	protected Component construction(ViewPage viewPage) {
		return new Else(viewPage, this.getConditionRegex());
	}
	
	public void SetIf(If belongIf) {
		this.belongIf = belongIf;
	}
	public If getIf() {
		return this.belongIf;
	}
	@Override
	protected void copyParams(Component src, Component des) {
		super.copyParams(src, des);
		((Else)des).SetIf((If)(((Else)src).belongIf.clone(des.getViewPage())));
	}
	@Override
	public boolean compare() throws ViewException {
		if (!this.getIf().compare()) {
			if (null == this.getConditionRegex()) {
				return true;
			} else {
				return super.compare();
			}
		} else {
			return false;
		}
	}
}
