package com.bfb.pos.activity.view;

import android.content.Context;
import android.text.InputType;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bfb.pos.R;

//左边图片，中间输入，右边无,普通输入框
public class TextWithIconView extends LinearLayout{
	
	private LinearLayout rootLayout;
	private ImageView iv_icon;
	private EditText editText;
	
	public TextWithIconView(Context context) {
		super(context);
		
		init(context);
	}

	public TextWithIconView(Context context, AttributeSet attrs) {
		super(context, attrs);
		
		init(context);
	}
	
	private void init(Context context){
		LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		inflater.inflate(R.layout.text_with_icon, this);
		
		rootLayout = (LinearLayout) this.findViewById(R.id.rootLayout);
		iv_icon = (ImageView) this.findViewById(R.id.iv_icon);
		editText = (EditText) this.findViewById(R.id.text);
	}
	
	public EditText getEditText(){
		return this.editText;
	}
	
	public void setIcon(int resource){
		iv_icon.setBackgroundResource(resource);
	}
	
	public void setHintString(String hint){
		editText.setHint(hint);
	}
	
	public void setText(String text){
		editText.setText(text);
	}
	
	public String getText(){
		return editText.getText().toString();
	}
	
	public void setRootLayoutBg(int resid){
		this.rootLayout.setBackgroundResource(resid);
	}
	
	public void setHint(){
		rootLayout.setVisibility(View.INVISIBLE);
	}
	
	public void setInputType(int type){
		editText.setInputType(type);
	}
	 
}
