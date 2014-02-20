package com.bfb.pos.activity.view;

import android.content.Context;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.util.AttributeSet;

public class AccountWithLabelView extends TextWithLabelView implements TextWatcher{
	
	private int curPostion;
	private boolean flag = false;
	
	private String beforeStr;
	
	public AccountWithLabelView(Context context) {
		super(context);
		
		init(context);
	}

	public AccountWithLabelView(Context context, AttributeSet attrs) {
		super(context, attrs);
		
		init(context);
	}
	
	private void init(final Context context){
		this.getEditText().setFilters(new InputFilter[]{new InputFilter.LengthFilter(23)}); 
		this.getEditText().addTextChangedListener(this); 
	}
	
	@Override  
    public void onTextChanged(CharSequence s, int start, int before, int count) { 
    }  
      
    @Override  
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
    	if (!flag){
    		beforeStr = s.toString();
    	}
    }  
      
    @Override  
    public void afterTextChanged(Editable edit) {
    	String inputStr = edit.toString();
//    	if (inputStr.replace(" ", "").equals(beforeStr.replace(" ", ""))){
//    		whenDeleteSpace(beforeStr, inputStr);
//    	}
    	
    	String formatStr = this.formatAccount(inputStr);
    	
    	if (!inputStr.equals(formatStr)){
    		flag = true;
    		this.getEditText().setText(formatStr);
    	}
    	
//    	if (!flag){
//    		curPostion = this.getSelectionPosition(this.formatAccount(beforeStr), formatStr);
//    	}
    	curPostion = this.getSelPos(beforeStr, formatStr);
    	this.getEditText().setSelection(curPostion);
		flag = false;
    	
    }
    
    // 当光标定位至空格前时执行
    private void whenDeleteSpace(String srcStr, String desStr){
    	for (int i=0; i<desStr.length(); i++){
    		if (srcStr.charAt(i) != desStr.charAt(i)){
    			StringBuffer sb = new StringBuffer(srcStr);
    			sb.delete(i-1, i);
    			flag = true;
    			this.getEditText().setText(sb.toString());
    			return;
    		}
    	}
    }
    
    private int getSelPos(String beforeStr, String afterStr){
    	beforeStr = this.formatAccount(beforeStr);
    	
    	if ("".equals(beforeStr)){
    		return afterStr.length();
    	
    	} else if ("".equals(afterStr)){
    		return 0;
    		
    	} else{
    		int i=0;
        	for (i=0; i<beforeStr.length()&&i<afterStr.length(); i++){
        		if (beforeStr.charAt(i) != afterStr.charAt(i)){
        			if (beforeStr.length()<afterStr.length()){
        				return i+1;
        			} else {
        				if (i!=0 && afterStr.charAt(i-1) == ' '){
        					return i-1;
        				} else{
        					return i;
        				}
        			}
        		}
        	}
        	
			return afterStr.length();
    	}
    }
    
    private String formatAccount(String account) {
    	if ("".equals(account))
    		return "";
    	
		// 去掉所有空格，得到原始内容
		String newAccount = account.replaceAll("[^\\d]", "");
		if(newAccount.length() > 19)
			newAccount = newAccount.substring(0,19);
		int groups = newAccount.length() / 4;

		// 格式化当前输入内容account
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < groups; i++) {
			sb.append(newAccount.substring(i * 4, i * 4 + 4)).append(" ");
		}
		int left = newAccount.length() % 4;
		if (left > 0)
			sb.append(newAccount.substring(newAccount.length() - left)).append(" ");
		sb.deleteCharAt(sb.length() - 1);
		return sb.toString();
	}

}
