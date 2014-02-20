package com.bfb.pos.dynamic.template.os.layout;

import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;

public class LinearLayoutTemplate extends LayoutTemplate {
	
	private String orientation;
	private String gravity;
	private String bgImg;
	private String padding;
	private String width;
	private String height;
	private String backgroundColor;
	
	public LinearLayoutTemplate(String id, String name) {
		super(id, name);
	}
	
	@Override
	public ViewGroup initLayout(Context context) {
		LinearLayout layout = new LinearLayout(context);
		// 为layout设置不同的模板属性
		if(null != gravity){
			layout.setGravity(this.getGravity());
		}
		if(null != orientation){
			layout.setOrientation(this.getOrientation());
		}
		if(null != this.width && null != this.height){
			layout.setLayoutParams(new LayoutParams(this.getWidth(), this.getHeight()));
		}
		if(null != this.backgroundColor) {
			layout.setBackgroundColor(this.getBackgroundColor());
		}
		if(null != bgImg){
			layout.setBackgroundResource(this.getBgImg(context));
		}
		if(null != padding){
			int[] p = this.getPadding();
			if(null != p && p.length == 4){
				layout.setPadding(p[0], p[1], p[2], p[3]);
			}
		}
		return layout;
	}
	

	public Integer getOrientation() {
		if(null == this.orientation){
			return null;
		}
		
		if("HORIZONTAL".equalsIgnoreCase(orientation))
			return LinearLayout.HORIZONTAL;
		if("VERTICAL".equalsIgnoreCase(orientation))
			return LinearLayout.VERTICAL;
		
		return LinearLayout.VERTICAL;
	}
	
	public Integer getGravity() {
		if(null == this.gravity){
			return null;
		}
		
		if("CENTER".equalsIgnoreCase(gravity))
			return Gravity.CENTER;
		if("CENTER_HORIZONTAL".equalsIgnoreCase(gravity))
			return Gravity.CENTER_HORIZONTAL;
		if("CENTER_VERTICAL".equals(gravity))
			return Gravity.CENTER_VERTICAL;
		if("LEFT".equals(gravity))
			return Gravity.LEFT;
		if("RIGHT".equals(gravity))
			return Gravity.RIGHT;
		if("BOTTOM".equals(gravity))
			return Gravity.BOTTOM;
		if("TOP".equals(gravity))
			return Gravity.TOP;
		
		return Gravity.LEFT;
	}
	
	public Integer getBackgroundColor() {
		if(null == this.backgroundColor){
			return null;
		}
		
		if ("BLACK".equalsIgnoreCase(this.backgroundColor)) {
			return Color.BLACK;
		} else if ("BLUE".equalsIgnoreCase(this.backgroundColor)) {
			return Color.BLUE;
		} else if ("CYAN".equalsIgnoreCase(this.backgroundColor)) {
			return Color.CYAN;
		} else if ("DKGRAY".equalsIgnoreCase(this.backgroundColor)) {
			return Color.DKGRAY;
		} else if ("GRAY".equalsIgnoreCase(this.backgroundColor)) {
			return Color.GRAY;
		} else if ("GREEN".equalsIgnoreCase(this.backgroundColor)) {
			return Color.GREEN;
		} else if ("LTGRAY".equalsIgnoreCase(this.backgroundColor)) {
			return Color.LTGRAY;
		} else if ("MAGENTA".equalsIgnoreCase(this.backgroundColor)) {
			return Color.MAGENTA;
		} else if ("RED".equalsIgnoreCase(this.backgroundColor)) {
			return Color.RED;
		} else if ("TRANSPARENT".equalsIgnoreCase(this.backgroundColor)) {
			return Color.TRANSPARENT;
		} else if ("WHITE".equalsIgnoreCase(this.backgroundColor)) {
			return Color.WHITE;
		} else if ("YELLOW".equalsIgnoreCase(this.backgroundColor)) {
			return Color.YELLOW;
		}
		return Color.WHITE;
	}

	public Integer getBgImg(Context context){
		if(null == this.bgImg){
			return null;
		}
		
		try{
			int resourceId = context.getResources().getIdentifier(bgImg, "drawable", context.getPackageName());
			return resourceId;
		}catch(Exception e){
			return 0;
		}
		
	}

	public int[] getPadding(){
		if(null == this.padding){
			return null;
		}
		
		String[] paddings =  padding.split(",");
		int[] p = new int[padding.length()];
		for(int i=0;i<paddings.length;i++){
			p[i] = Integer.parseInt(paddings[i]);
		}
		return p;
	}

	public Integer getWidth() {
		if(null ==this.width){
			return null;
		}
		try{
			if("FILL_PARENT".equalsIgnoreCase(width)){
				return LinearLayout.LayoutParams.FILL_PARENT;
			}else if("WRAP_CONTENT".equalsIgnoreCase(width)){
				return LinearLayout.LayoutParams.WRAP_CONTENT;
			}else{
				return Integer.parseInt(width);
			}
		}catch(Exception e){
			Log.i("dynamic", "width that in Linearlayout must be Integer.");
		}
		return -1;
	}

	public void setWidth(String width) {
		this.width = width;
	}

	public Integer getHeight() {
		if(null == this.height){
			return null;
		}
		try{
			if("FILL_PARENT".equalsIgnoreCase(height)){
				return LinearLayout.LayoutParams.FILL_PARENT;
			}else if("WRAP_CONTENT".equalsIgnoreCase(height)){
				return LinearLayout.LayoutParams.WRAP_CONTENT;
			}else{
				return Integer.parseInt(height);
			}
		}catch(Exception e){
			Log.i("dynamic", "height that in Linearlayout must be Integer.");
		}
		
		return -1;
	}
	
	public void setOrientation(String orientation) {
		this.orientation = orientation;
	}

	public void setGravity(String gravity) {
		this.gravity = gravity;
	}
	
	public void setBgImg(String bgImg){
		this.bgImg = bgImg;
	}
	
	public void setPadding(String padding){
		this.padding = padding;
	}

	public void setHeight(String height) {
		this.height = height;
	}
	
	public void setBackgroundColor(String backgroundColor) {
		this.backgroundColor = backgroundColor;
	}	
}
