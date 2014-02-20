package com.bfb.pos.dynamic.component.os;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.text.InputType;
import android.widget.Toast;

import com.bfb.pos.activity.view.AccountWithLabelView;
import com.bfb.pos.dynamic.component.Component;
import com.bfb.pos.dynamic.component.Input;
import com.bfb.pos.dynamic.component.ViewException;
import com.bfb.pos.dynamic.core.ViewPage;

public class OSAccount extends Input {

	private String leftLabel;
	private String hintLabel;
	private String inputType;
	
	private String pattern;
	private String judge;
	
	private AccountWithLabelView account;
	
	public OSAccount(ViewPage viewPage) {
		super(viewPage);
	}

	@Override
	public AccountWithLabelView toOSComponent() throws ViewException {
		account = new AccountWithLabelView(this.getContext());
	   
		//设置密码
		//edit.setTransformationMethod(PasswordTransformationMethod.getInstance());
		account.getEditText().setInputType(InputType.TYPE_CLASS_NUMBER);
		if(null != hintLabel){
			account.getEditText().setHint(hintLabel);
		}
		if(null != leftLabel){
			account.getTextView().setText(this.getLeftLabel());
		}
		if(null != inputType){
			account.getEditText().setInputType(this.getInputType());
		}
		return account;
	}
	
	public void setInputType(String inputType){
		this.inputType = inputType;
	}

	public Integer getInputType() {
		if("numeric".equals(inputType)){
			return InputType.TYPE_CLASS_NUMBER;
		}else if("phone".equals(inputType)){//拨号键盘
			return InputType.TYPE_CLASS_PHONE;
		}else if("datetime".equals(inputType)){
			return InputType.TYPE_CLASS_DATETIME;
		}
		return InputType.TYPE_CLASS_TEXT;
	}
	
	public String getLeftLabel(){
		return null == leftLabel? "": leftLabel;
	}
	
	public void setLeftLabel(String leftLabel){
		this.leftLabel = leftLabel;
	}
	
	public String getHint(){
		return this.hintLabel;
	}
	
	public void setHint(String hintLabel){
		this.hintLabel = hintLabel;
	}
	
	public void setPattern(String pattern){
		this.pattern = pattern;
	}
	
	public void setJudge(String judge){
		this.judge = judge;
	}
	
	public String getPattern(){
		return this.pattern;
	}
	
	public String getJudge(){
		return this.judge;
	}
	
	@Override
	protected Component construction(ViewPage viewPage) {
		return new OSPassword(viewPage);
	}
	
	@Override
	protected void copyParams(Component src, Component des) {
		super.copyParams(src, des);
		
		((OSAccount)des).inputType = ((OSAccount)src).inputType;
	}
	
	public void loadInputValue(){
		this.getViewPage().addAPageValue(this, account.getEditText().getText().toString().replace(" ", ""));
	}
	
	@Override
	public boolean validator() {
		String inputStr = account.getEditText().getText().toString().trim();
		if ("".equals(inputStr)){
			Toast.makeText(this.getContext(), this.getHint(), Toast.LENGTH_SHORT).show();
			return false;
		}
		
		if (inputStr.length()  < 16){
			Toast.makeText(this.getContext(), "银行账号不能少于16位", Toast.LENGTH_SHORT).show();
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
						if (!((String)this.getViewPage().getPageValue(this)).equals((String)this.getViewPage().getPageValue(tempArray[1]))){
							Toast.makeText(this.getContext(), "两次输入不一致，请核对", Toast.LENGTH_SHORT).show();
							return false;
						}
					}
				}
				
			}catch(Exception e){
				e.printStackTrace();
			}
		}
		
		return true;
	}
}
