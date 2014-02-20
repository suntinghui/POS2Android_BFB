package com.bfb.pos.activity.view;

import com.bfb.pos.R;

import android.content.Context;
import android.content.res.TypedArray;
import android.text.InputType;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.view.View.OnClickListener;

public class EditTextWithClearView extends RelativeLayout implements OnClickListener {
	
	private EditText editText = null;
	private ImageView clearImage = null;
	private ImageView leftImage = null;

	public EditTextWithClearView(Context context) {
		super(context);
	}
	
	public EditTextWithClearView(Context context, AttributeSet attrs) {
		super(context, attrs);
		
		RelativeLayout layout = (RelativeLayout) LayoutInflater.from(context).inflate(R.layout.layout_edittext_clear, this, true);
		editText = (EditText) layout.findViewById(R.id.editText);
		clearImage = (ImageView) layout.findViewById(R.id.clearImage);
		clearImage.setOnClickListener(this);
		leftImage = (ImageView) layout.findViewById(R.id.leftImage);
		
		TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.EditTextWithClearView);  
		CharSequence hint = typedArray.getText(R.styleable.EditTextWithClearView_hint);
		if (null != hint){
			editText.setHint(hint);
		}
		
		int inputType = typedArray.getInteger(R.styleable.EditTextWithClearView_inputType, 0);
		if (inputType == 0){
			editText.setInputType(InputType.TYPE_CLASS_TEXT);
		} else if (inputType == 1){
			editText.setInputType(InputType.TYPE_CLASS_TEXT |InputType.TYPE_TEXT_VARIATION_PASSWORD);
		}
		
		int imageType = typedArray.getInteger(R.styleable.EditTextWithClearView_leftImage, 0);
		if (imageType == 0){
			leftImage.setVisibility(View.GONE);
		} else if (imageType == 1) {
			leftImage.setVisibility(View.VISIBLE);
			leftImage.setImageResource(R.drawable.img_edittext_user);
		} else if (imageType == 2){
			leftImage.setVisibility(View.VISIBLE);
			leftImage.setImageResource(R.drawable.img_edittext_pwd);
		} else {
			leftImage.setVisibility(View.GONE);
		}
		
		typedArray.recycle();
	}
	
	public void setText(String str){
		this.editText.setText(null == str ? "" : str);
	}
	
	public String getText(){
		return this.editText.getText().toString();
	}
	
	public void setHint(String hint){
		this.editText.setHint(hint);
	}

	@Override
	public void onClick(View v) {
		switch(v.getId()){
		case R.id.clearImage:
			editText.setText("");
			break;
		}
		
	}

	

}
