package com.bfb.pos.activity;

import java.util.Vector;

import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.view.inputmethod.InputMethodManager;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.Toast;

import com.bfb.pos.dynamic.core.ViewContext;
import com.bfb.pos.dynamic.core.ViewPage;
import com.bfb.pos.util.StringUtil;
import com.bfb.pos.R;

public class DynamicActivity extends BaseActivity {
	private ViewPage currentViewPage = null;
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        this.showView();
	}
	
	@Override
	protected void onNewIntent(Intent intent) {
		super.onNewIntent(intent);
		this.setIntent(intent);
		
		this.showView();
	}
	
	private void showView(){
		Intent intent = this.getIntent();
		currentViewPage = ViewContext.getInstance().getViewPage(intent.getStringExtra("viewPageId"));
		// 比如有电话打入等引起内存紧张时，系统可能会消除ViewContext内存，导致为null。
		if (null != currentViewPage){
			this.showView(currentViewPage);
		} else {
			this.setResult(RESULT_CANCELED);
			this.finish();
		}
	}

	private void showView(ViewPage viewPage){
		LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT);
        ScrollView sv = new ScrollView(this);
        sv.setLayoutParams(layoutParams);
        sv.setBackgroundResource(R.drawable.login_bg);
        // 解决ScrollView与ListView的冲突问题
        sv.setFillViewport(true);
        
        // FOR "ScrollView can host only one direct child"
        LinearLayout layout = new LinearLayout(this);   //线性布局方式  
        layout.setOrientation(LinearLayout.VERTICAL ); //控件对其方式为垂直排列
        layout.removeAllViews();
        Vector<View> vector = null;
		try {
			vector = viewPage.toOSView();
		} catch (Exception e) {
			e.printStackTrace();
			Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG).show();
		} 
		
        for(int i=0;i<vector.size();i++){
        	layout.addView(vector.elementAt(i));
        }
         
        sv.addView(layout); 
        /**
        Animation animation = AnimationUtils.makeInAnimation(this, isBackEvent);
        isBackEvent = false;
        animation.setDuration(300);
        sv.setAnimation(animation);
        animation.startNow();
        **/
        this.setContentView(sv);
         
	}
	
	/**
	 * 返回true表示你已经处理了，就不会在onKeyDown的事件中继续传递，
	 * 所以相当于在当前activity中，所有的keyevent都被拦截了，但是对于close（）的响应是正确的，
	 * 所以当时在写代码的时候就没有注意这个引入的bug，因为close（）工作正常！
	 */
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if(keyCode == KeyEvent.KEYCODE_BACK){
			
			if (null != currentViewPage){
				ViewPage tempViewPage = currentViewPage;
				
				String back = tempViewPage.getPageBack();
				String isForbidBack = tempViewPage.getIsForbidBack();
				if("true".equalsIgnoreCase(isForbidBack)){
					return false;
				}
				
				if (StringUtil.isNumeric(back)){
					int goHistory = Integer.parseInt(back);
					
					for (int i=0; i<-goHistory; i++) {
						if (null == tempViewPage)
							return false;
						
						ViewPage preViewPage = tempViewPage.getPrePage(); 
						ViewContext.getInstance().removeViewPage(tempViewPage);
						tempViewPage = preViewPage;
					}
					
					if (null == tempViewPage || goHistory == 0)
						return false;
					
					currentViewPage = tempViewPage;
					Intent intent = new Intent(this.getPackageName() + ".dynamic");
		            intent.putExtra("viewPageId", currentViewPage.getPageId());
		            DynamicActivity.this.startActivityForResult(intent, 0);
					
				} else if("close".equalsIgnoreCase(back)){
					ViewContext.getInstance().removeViewPage(currentViewPage);
					this.setResult(RESULT_CANCELED);
					this.finish();
					
				} else if ("finish".equalsIgnoreCase(back)){// 这种情况可能很少用到，有其它替代方法
					ViewContext.getInstance().removeViewPage(currentViewPage);
					this.setResult(RESULT_OK);
					this.finish();
				}
			}
			
			return false;
			
		}else{
			return super.onKeyDown(keyCode, event);
		}
	}
	
	// 解决动态界面中点击返回按纽软键盘不消失的问题。
	@Override
	protected void onPause() {
		super.onPause();
		try{
			if (this.getCurrentFocus() != null){
				((InputMethodManager)getSystemService(INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(this.getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
			}
		} catch(Exception e){
			
		}
		
	}
	
}
