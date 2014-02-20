package com.bfb.pos.dynamic.template.os;

import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.bfb.pos.dynamic.parse.ParseView;

public class ImageViewTemplate extends CSSTemplate {
	
	private String adjustViewBounds;
	private String maxWidth;
	private String maxHeight;
	private String padding;
	
	public ImageViewTemplate(String id, String name) {
		super(id, name);
		this.setType(ParseView.TEMPLATE_TYPE_IMAGEVIEW);
	}
	
	@Override
	public ImageView rewind(View structComponent) {
		if(null != adjustViewBounds){
			((ImageView)structComponent).setAdjustViewBounds(this.getAdjustViewBounds());
		}
		if(null != maxWidth){
			((ImageView)structComponent).setMaxWidth(this.getMaxWidth());
		}
		if(null != maxHeight){
			((ImageView)structComponent).setMaxHeight(this.getMaxHeight());
		}
		if(null != padding){
			int[] p = this.getPadding();
			if(null != p && p.length == 4){
				((ImageView)structComponent).setPadding(p[0], p[1], p[2], p[3]);
			}
		}
		
		return ((ImageView)structComponent);
	}

	public boolean getAdjustViewBounds() {
		return "false".equalsIgnoreCase(adjustViewBounds) ? false : true;
	}

	public void setAdjustViewBounds(String adjustViewBounds) {
		this.adjustViewBounds = adjustViewBounds;
	}

	public Integer getMaxWidth() {
		try{
			return Integer.parseInt(maxWidth);
		}catch(Exception e){
			Log.i("dyanmic", "maxWidth of ImageView must be Integer.");
			return 0;
		}
	}

	public void setMaxWidth(String maxWidth) {
		this.maxWidth = maxWidth;
	}

	public Integer getMaxHeight() {
		try{
			return Integer.parseInt(maxHeight);
		}catch(Exception e){
			Log.i("dyanmic", "maxHeight of ImageView must be Integer.");
			return 0;
		}
	}

	public void setMaxHeight(String maxHeight) {
		this.maxHeight = maxHeight;
	}

	public int[] getPadding(){
		String[] paddings =  padding.split(",");
		int[] p = new int[padding.length()];
		for(int i=0;i<paddings.length;i++){
			p[i] = Integer.parseInt(paddings[i]);
		}
		return p;
	}

	public void setPadding(String padding) {
		this.padding = padding;
	}

}
