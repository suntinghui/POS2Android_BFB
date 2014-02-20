package com.bfb.pos.dynamic.component.os;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.graphics.drawable.Drawable;
import android.text.Editable;
import android.text.InputFilter;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.Log;
import android.widget.Toast;

import com.bfb.pos.activity.view.TextWithLabelView;
import com.bfb.pos.dynamic.component.Component;
import com.bfb.pos.dynamic.component.Input;
import com.bfb.pos.dynamic.component.ViewException;
import com.bfb.pos.dynamic.core.ViewPage;
import com.bfb.pos.R;

public class OSText extends Input {
	
	private String allowNull;
	private String pattern;
	private String judge;
	
	private String hint;
	private String editAble;
	private String inputType;
	private String maxLength;
	private String leftImage;
	private String leftLabel;
	
	TextWithLabelView text;
	
	public OSText(ViewPage viewPage) {
		super(viewPage);
	}

	@Override
	public TextWithLabelView toOSComponent() throws ViewException {
		text = new TextWithLabelView(this.getContext());
		text.setTag(this.getId());
		text.getEditText().setText(null == this.getValue() ? "" : this.getValue().toString());
		
		if(null != hint){
			text.setHint(hint);
		}
		if (null != editAble){
			text.setFocusable(this.getEditAble());
		}
		if(null != inputType){
			text.getEditText().setInputType(this.getInputType());
		}
		if(null != maxLength){
			text.getEditText().setFilters(new InputFilter[]{new InputFilter.LengthFilter(this.getMaxLength())});
		}
		if(null != leftImage){
			Drawable leftImage = this.getContext().getResources().getDrawable(this.getLeftImage());
			// 调用setCompoundDrawables时，必须调用Drawable.setBounds()方法,否则图片不显示
//			leftImage.setBounds(0, 0, leftImage.getMinimumWidth(), leftImage.getMinimumHeight());
			leftImage.setBounds(0, 0, leftImage.getMinimumWidth(), leftImage.getMinimumHeight());
			text.getEditText().setCompoundDrawables(leftImage, null, null, null); //设置左图标
		}
		if(null != leftLabel){
			text.getTextView().setText(this.getLeftLabel());
		}
		
		return text;
	}
	
	@Override
	protected void copyParams(Component src, Component des) {
		super.copyParams(src, des);
		((OSText)des).allowNull = ((OSText)src).allowNull;
		((OSText)des).hint      = ((OSText)src).hint;
		((OSText)des).inputType = ((OSText)src).inputType;
		((OSText)des).maxLength = ((OSText)src).maxLength;
		((OSText)des).leftImage = ((OSText)src).leftImage;
		((OSText)des).leftLabel = ((OSText)src).leftLabel;
	}

	@Override
	protected Component construction(ViewPage viewPage) {
		return new OSText(viewPage);
	}
	
	public void loadInputValue(){
		OSText.this.getViewPage().addAPageValue(getId(), text.getText().toString().trim());
	}
	
	@Override
	public boolean validator() {
		String inputStr = text.getEditText().getText().toString().trim();
		
		if (!this.getAllowNull() && "".equals(inputStr)){
			Toast.makeText(this.getContext(), this.getHint(), Toast.LENGTH_SHORT).show();
			return false;
		}
		
		if (null != this.getPattern()){
			Pattern pattern = Pattern.compile(this.getPattern());
			Matcher matcher = pattern.matcher(inputStr);
			if (!matcher.matches()){
				Toast.makeText(this.getContext(), this.getLeftLabel().replace(" ", "")+"输入不合法，请检查", Toast.LENGTH_SHORT).show();
				return false;
			} 
		}
		
		if (null != this.getJudge()){
			try{
				String[] judgeArray = this.getJudge().split("\\|");
				for (String str : judgeArray){
					String[] tempArray = str.split(":");
					if (tempArray.length != 2)
						break;
					
					if ("=".equals(tempArray[0].trim())){
						if (!((String)text.getText().toString().trim()).equals((String)this.getViewPage().getPageValue(tempArray[1]))){
							Toast.makeText(this.getContext(), "两次输入不一致，请核对", Toast.LENGTH_SHORT).show();
							return false;
						}
					} 
					/*
					else {
						float f1 = Float.valueOf(text.getText().toString().trim());
						float f2 = Float.valueOf((String)this.getViewPage().getPageValue(tempArray[1]));
						
						if(">".equals(tempArray[0].trim()) && f1<f2){
							Toast.makeText(this.getContext(), "两次输入不一致，请核对", Toast.LENGTH_SHORT).show();
							return false;
						}
					}
					*/
				}
				
			}catch(Exception e){
				e.printStackTrace();
			}
		}
		
		return true;
	}
	
	public boolean getAllowNull() {
		return "false".equals(allowNull) ? false : true;
	}
	
	public String getPattern(){
		return this.pattern;
	}
	
	public String getJudge(){
		return this.judge;
	}
	
	public boolean getEditAble() throws ViewException{
		if ("false".equals(editAble))
			return false;
		else if ("value".equals(editAble) && (null == this.getValue()))
			return true;
		else if ("value".equals(editAble) && (null != this.getValue()))
			return false;
		
		return true;
	}

	public String getHint() {
		return hint;
	}

	public Integer getInputType() {
		if("numeric".equalsIgnoreCase(inputType)){
			return InputType.TYPE_CLASS_NUMBER;
		}else if("phone".equalsIgnoreCase(inputType)){//拨号键盘
			return InputType.TYPE_CLASS_PHONE;
		}else if("datetime".equalsIgnoreCase(inputType)){
			return InputType.TYPE_CLASS_DATETIME;
		}else if("decimal".equalsIgnoreCase(inputType)){
			return InputType.TYPE_CLASS_NUMBER|InputType.TYPE_NUMBER_FLAG_DECIMAL;
		}else if("signed".equalsIgnoreCase(inputType)){
			return InputType.TYPE_CLASS_NUMBER|InputType.TYPE_NUMBER_FLAG_SIGNED;
		}else if("email".equalsIgnoreCase(inputType)){
			return InputType.TYPE_CLASS_TEXT|InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS;
		}
		return InputType.TYPE_CLASS_TEXT;
	}

	public Integer getMaxLength() {
		try{
			return Integer.parseInt(maxLength);
		}catch(Exception e){
			Log.i("dynamic", "maxLength must be Integer.");
			return 0;
		}
	}
	
	public int getLeftImage(){
		int resourceId = this.getContext().getResources().getIdentifier(leftImage, "drawable", this.getContext().getPackageName());
		// if failure, return the default icon resource id to instead.
		if (resourceId == 0)
			resourceId = R.drawable.icon;
		
		return resourceId;
	}
	
	public String getLeftLabel(){
		return null == leftLabel? "": leftLabel;
	}

	public void setAllowNull(String allowNull) {
		this.allowNull = allowNull;
	}
	
	public void setPattern(String pattern){
		this.pattern = pattern;
	}
	
	public void setJudge(String judge){
		this.judge = judge;
	}
	
	public void setEditAble(String editAble){
		this.editAble = editAble;
	}
	
	public void setHint(String hint){
		this.hint = hint;
	}
	
	public void setInputType(String inputType){
		this.inputType = inputType;
	}
	
	public void setMaxLength(String maxLength){
		this.maxLength = maxLength;
	}
	
	public void setLeftImage(String leftImage){
		this.leftImage = leftImage;
	}
	
	public void setLeftLabel(String leftLabel){
		this.leftLabel = leftLabel;
	}

}