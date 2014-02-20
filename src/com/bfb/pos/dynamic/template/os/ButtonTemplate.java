package com.bfb.pos.dynamic.template.os;

import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;

import com.bfb.pos.dynamic.component.os.DButton;
import com.bfb.pos.dynamic.parse.ParseView;

public class ButtonTemplate extends CSSTemplate {
	
	private String bgImg_down;

	public ButtonTemplate(String id, String name) {
		super(id, name);
		this.setType(ParseView.TEMPLATE_TYPE_BUTTON);
	}
	
	@Override
	public DButton rewind(final View structComponent) {
		if (null != this.getColor()) {
			((DButton)structComponent).setTextColor(this.getColor());
		}
		if(null != this.getSize()){
			((DButton)structComponent).setTextSize(this.getSize());
		}
		if (null != this.getBgImage()) {
			((DButton)structComponent).setBackgroundResource(structComponent.getContext().getResources().getIdentifier(bgImg, "drawable", structComponent.getContext().getPackageName()));
			// button's height is 50.if less than 50,the Text will only display the upper part
			// ((DButton)structComponent).setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
		}
		if(null != this.getGravity()){
			((DButton)structComponent).setGravity(this.getGravity());
		}
		
		if(null != this.getBgImg_down()){
			((DButton)structComponent).setOnTouchListener(new OnTouchListener(){
				@Override
				public boolean onTouch(View v, MotionEvent event) {
					if(event.getAction() == MotionEvent.ACTION_DOWN){
						((DButton)structComponent).setBackgroundResource(structComponent.getContext().getResources().getIdentifier(bgImg_down, "drawable", structComponent.getContext().getPackageName()));
					} else if(event.getAction() == MotionEvent.ACTION_UP){
						((DButton)structComponent).setBackgroundResource(structComponent.getContext().getResources().getIdentifier(bgImg, "drawable", structComponent.getContext().getPackageName()));
					}
					return false;
				}
				
			});
		}
		return ((DButton)structComponent);
	}

	public String getBgImg_down() {
		return bgImg_down;
	}

	public void setBgImg_down(String bgImg_down) {
		this.bgImg_down = bgImg_down;
	}
	
}
