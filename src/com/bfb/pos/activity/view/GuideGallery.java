package com.bfb.pos.activity.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.widget.Gallery;

public class GuideGallery extends Gallery {

	public GuideGallery(Context context) {
		super(context);
	}
	
	public GuideGallery(Context context,AttributeSet attrs) {
		super(context, attrs);
	}
	
	public GuideGallery(Context context, AttributeSet attrs,
			int defStyle) {
		super(context, attrs, defStyle);
	}

	@Override
	public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
		// TODO Auto-generated method stub
		int kEvent;
        if(isScrollingLeft(e1, e2)){ //Check if scrolling left
          kEvent = KeyEvent.KEYCODE_DPAD_LEFT;
        }
        else{ //Otherwise scrolling right
          kEvent = KeyEvent.KEYCODE_DPAD_RIGHT;
        }
        onKeyDown(kEvent, null);
        return true;  
		
	}
	private boolean isScrollingLeft(MotionEvent e1, MotionEvent e2){
        return e2.getX() > e1.getX();
    }

}
