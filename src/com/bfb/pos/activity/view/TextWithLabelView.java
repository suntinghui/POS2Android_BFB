package com.bfb.pos.activity.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bfb.pos.R;

public class TextWithLabelView extends LinearLayout{
	
	private LinearLayout rootLayout;
	private TextView textView;
	private EditText editText;
	
	public TextWithLabelView(Context context) {
		super(context);
		
		init(context);
	}

	public TextWithLabelView(Context context, AttributeSet attrs) {
		super(context, attrs);
		
		init(context);
	}
	
	private void init(Context context){
		LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		inflater.inflate(R.layout.text_with_label, this);
		
		rootLayout = (LinearLayout) this.findViewById(R.id.rootLayout);
		textView = (TextView) this.findViewById(R.id.label);
		editText = (EditText) this.findViewById(R.id.text);
	}
	
	public EditText getEditText(){
		return this.editText;
	}
	
	public TextView getTextView(){
		return this.textView;
	}
	
	public void setHint(String hint){
		editText.setHint(hint);
	}
	
	public void setLabel(String label){
		textView.setText(label);
	}
	
	public void setHintWithLabel(String label, String hint){
		textView.setText(label);
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
