package com.bfb.pos.dynamic.component.os;

import android.widget.Toast;

import com.bfb.pos.activity.view.PasswordWithLabelView;
import com.bfb.pos.dynamic.component.Component;
import com.bfb.pos.dynamic.component.Input;
import com.bfb.pos.dynamic.component.ViewException;
import com.bfb.pos.dynamic.core.ViewPage;

public class OSPassword extends Input {
	
	private String leftLabel;
	private String hintLabel;
	private String judge;
	
	private PasswordWithLabelView password;
	
	public OSPassword(ViewPage viewPage) {
		super(viewPage);
	}

	@Override
	public PasswordWithLabelView toOSComponent() throws ViewException {
		password = new PasswordWithLabelView(this.getContext());
	   
		//设置密码
		//edit.setTransformationMethod(PasswordTransformationMethod.getInstance());
//		password.getEditText().setInputType(InputType.TYPE_TEXT_VARIATION_PASSWORD);
		if(null != hintLabel){
			password.getEditText().setHint(hintLabel);
		}
		if(null != leftLabel){
			password.getTextView().setText(this.getLeftLabel());
		}
		return password;
	}
	public String getLeftLabel(){
		return null == leftLabel? "": leftLabel;
	}
	
	public String getHint(){
		return this.hintLabel;
	}
	
	public String getJudge(){
		return this.judge;
	}
	
	public void setLeftLabel(String leftLabel){
		this.leftLabel = leftLabel;
	}
	
	public void setHint(String hintLabel){
		this.hintLabel = hintLabel;
	}
	
	public void setJudge(String judge){
		this.judge = judge;
	}
	
	@Override
	protected Component construction(ViewPage viewPage) {
		return new OSPassword(viewPage);
	}
	
	@Override
	protected void copyParams(Component src, Component des) {
		super.copyParams(src, des);
	}
	
	public void loadInputValue(){
		this.getViewPage().addAPageValue(this.getId(), password.getEncryptPWD());
		// 将id_MD5作为另一个控件，存放密码MD5值。
		this.getViewPage().addAPageValue(this.getId()+"_MD5", password.getMd5PWD());
	}
	
	@Override
	public boolean validator() {
		// 这里设计的有问题，本是不应该取明文的。
		String inputStr = password.getEditText().getText().toString().trim();
		if ("".equals(inputStr)){
			Toast.makeText(this.getContext(), this.getHint(), Toast.LENGTH_SHORT).show();
			return false;
		}
		
		if (inputStr.length() != 6){
			Toast.makeText(this.getContext(), "请输入6位商户密码", Toast.LENGTH_SHORT).show();
			return false;
		}
		
		if (null != this.getJudge()){
			try{
				String[] judgeArray = this.getJudge().split("\\|");
				for (String str : judgeArray){
					String[] tempArray = str.split(":");
					if (tempArray.length != 2)
						break;
					
					if ("=".equals(tempArray[0].trim())){
						// 只能通过MD5值来比较，拿不到原值。
						if (!((String)this.getViewPage().getPageValue(this.getId()+"_MD5")).equals((String)this.getViewPage().getPageValue(tempArray[1]+"_MD5"))){
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
