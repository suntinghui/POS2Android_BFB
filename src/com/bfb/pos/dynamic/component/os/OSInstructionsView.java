package com.bfb.pos.dynamic.component.os;

import com.bfb.pos.activity.view.InstructionsForUseView;
import com.bfb.pos.dynamic.component.Component;
import com.bfb.pos.dynamic.component.ViewException;
import com.bfb.pos.dynamic.core.ViewPage;

public class OSInstructionsView extends StructComponent {
	
	private String instructionId;
	
	public OSInstructionsView(ViewPage viewPage) {
		super(viewPage);
	}

	@Override
	public InstructionsForUseView toOSComponent() throws ViewException {
		InstructionsForUseView view = new InstructionsForUseView(this.getContext());
		view.setTag(this.getId());
		view.getInstructionsText().setText(view.getInstructionContent(instructionId));
		return view;
	}

	@Override
	protected Component construction(ViewPage viewPage) {
		return new OSInstructionsView(viewPage);
	}

	public void setInstructionId(String instructionId) {
		this.instructionId = instructionId;
	}
	
}
