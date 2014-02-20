package com.bfb.pos.dynamic.component.os;

import java.io.IOException;

import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Toast;

import com.bfb.pos.dynamic.component.Component;
import com.bfb.pos.dynamic.component.Input;
import com.bfb.pos.dynamic.component.ViewException;
import com.bfb.pos.dynamic.core.ViewPage;

public class OSButton extends Input {

	public OSButton(ViewPage viewPage) {
		super(viewPage);
	}
	
	public void loadInputValue(){
		
	}
	
	@Override
	public DButton toOSComponent() throws ViewException {
		DButton button = new DButton(this.getContext());
		button.setTag(this.getId());
		button.setText(null == this.getValue()?"":this.getValue().toString());
		button.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {				
				try {
					OSButton.this.trigger();
				} catch (ViewException e) {
					e.printStackTrace();
				} catch (IOException e) {
					Toast.makeText(OSButton.this.getContext(), e.getMessage(), Toast.LENGTH_LONG).show();
				}
			} 
		});
		return button;
	}

	@Override
	protected Component construction(ViewPage viewPage) {
		return new OSButton(viewPage);
	}

	@Override
	public boolean validator() {
		return true;
	}
}
