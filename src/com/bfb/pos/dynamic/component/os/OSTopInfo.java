package com.bfb.pos.dynamic.component.os;

import com.bfb.pos.activity.view.TopInfoView;
import com.bfb.pos.dynamic.component.Component;
import com.bfb.pos.dynamic.component.ViewException;
import com.bfb.pos.dynamic.core.ViewPage;

public class OSTopInfo extends StructComponent {
	
	public OSTopInfo(ViewPage viewPage) {
		super(viewPage);
	}

	@Override
	public TopInfoView toOSComponent() throws ViewException {
		TopInfoView topInfo = new TopInfoView(this.getContext());
		topInfo.setTag(this.getId());
		return topInfo;
	}

	@Override
	protected Component construction(ViewPage viewPage) {
		return new OSTopInfo(viewPage);
	}
}
