package com.bfb.pos.util;

import java.util.Stack;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.ImageView.ScaleType;

import com.bfb.pos.activity.BaseActivity;
import com.bfb.pos.R;

public class ActivityUtil {
	
	private static Stack<Activity> activityStack = new Stack<Activity>();
	
	public static void shake(Context context,View v){
		Animation shake = AnimationUtils.loadAnimation(context, R.anim.shake);
		// 震动并获得焦点
        v.startAnimation(shake);
        v.requestFocus();
	}
	
	/*******
	 * 
	public static void pushActivity(Activity activity){
		activityStack.add(activity);
	}
	
	public static Activity peekActivity(){
		return activityStack.peek();
	}
	
	public static Activity popActivity(){
		return activityStack.pop();
	}
	
	******/
	
	public static void setEmptyView(ListView listView){
		ImageView emptyView = new ImageView(BaseActivity.getTopActivity());
		
		RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(LayoutParams.FILL_PARENT,LayoutParams.FILL_PARENT);
		lp.addRule(RelativeLayout.CENTER_IN_PARENT);
		emptyView.setLayoutParams(lp);
		
		emptyView.setImageResource(R.drawable.nodata);
		emptyView.setScaleType(ScaleType.CENTER_INSIDE);
		
		((ViewGroup)listView.getParent()).addView(emptyView);
		listView.setEmptyView(emptyView);
		
	}

}
