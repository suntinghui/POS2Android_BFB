package com.bfb.pos.dynamic.template.os;

import android.view.View;
import android.widget.TextView;

import com.bfb.pos.dynamic.parse.ParseView;

public class LabelTemplate extends CSSTemplate {
	
	private String bold;
	
	public LabelTemplate(String id, String name) {
		super(id, name);
		this.setType(ParseView.TEMPLATE_TYPE_LABEL);
	}
	
	@Override
	public TextView rewind(View structComponent) {
		if (null != this.getColor()) {
			((TextView)structComponent).setTextColor(this.getColor());
		}
		if (null != this.getSize()) {
			((TextView)structComponent).setTextSize(this.getSize());
		}
		if (null != this.getBgImage()) {
			((TextView)structComponent).setBackgroundResource(structComponent.getContext().getResources().getIdentifier(bgImg, "drawable", structComponent.getContext().getPackageName()));
		}
		if(null != this.getGravity()){
			((TextView)structComponent).setGravity(this.getGravity());
		}
		if(null != this.getBold()){
			((TextView)structComponent).getPaint().setFakeBoldText(this.getBold());
		}
		
		
		return ((TextView)structComponent);
	}
	
	public Boolean getBold(){
		if(null == bold){
			return null;
		}
		
		if("TRUE".equalsIgnoreCase(bold)){
			return true;
		}else{
			return false;
		}
	}
	
	public void setBold(String bold){
		this.bold = bold;
	}

}


