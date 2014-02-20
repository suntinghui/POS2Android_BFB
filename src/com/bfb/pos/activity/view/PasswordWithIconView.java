package com.bfb.pos.activity.view;

import java.math.BigInteger;
import java.security.KeyFactory;
import java.security.PublicKey;
import java.security.spec.RSAPublicKeySpec;

import javax.crypto.Cipher;

import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.text.Editable;
import android.text.InputFilter;
import android.text.InputType;
import android.text.TextWatcher;
import android.text.method.PasswordTransformationMethod;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnFocusChangeListener;
import android.view.View.OnTouchListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.PopupWindow.OnDismissListener;
import android.widget.TextView;

import com.bfb.pos.activity.BaseActivity;
import com.bfb.pos.agent.client.ApplicationEnvironment;
import com.bfb.pos.agent.client.Constant;
import com.bfb.pos.agent.client.TransferLogic;
import com.bfb.pos.util.StringUtil;
import com.itron.android.ftf.Util;
import com.bfb.pos.R;

public class PasswordWithIconView extends LinearLayout implements TextWatcher, OnFocusChangeListener, OnTouchListener {

	private String 	encryptPWD 			= "";
	private String 	md5PWD 				= "";
	
	private LinearLayout rootLayout		= null;
	private ImageView iv_icon			= null;
	private EditText editText			= null;
	
	private PopupWindow popup			= null;
	private Context context				= null;
	
	private InputMethodManager imm		= null;
	
	public PasswordWithIconView(Context context) {
		super(context);
		
		init(context);
	}

	public PasswordWithIconView(Context context, AttributeSet attrs) {
		super(context, attrs);
		
		init(context);
	}
	
	private void init(Context context){
		this.context = context;
		
		LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		inflater.inflate(R.layout.password_with_icon, this);
		
		rootLayout = (LinearLayout) this.findViewById(R.id.rootLayout);
		iv_icon = (ImageView) this.findViewById(R.id.iv_icon);
		editText = (EditText) this.findViewById(R.id.text);
		
		editText.setFilters(new InputFilter[]{ new InputFilter.LengthFilter(6)});
		editText.setInputType(InputType.TYPE_CLASS_NUMBER); 
		editText.setTransformationMethod(PasswordTransformationMethod.getInstance());
		
		editText.addTextChangedListener(this);
		editText.setOnFocusChangeListener(this);
		editText.setOnTouchListener(this);
	}
	
	public String getEncryptPWD() {
		return encryptPWD;// 这才是正确的， 只是投机取巧，不想改所有的RSA加密改为md5
//		return getMd5PWD();
	}

	// 因为RSA加密每次得到的密文不同，所以用MD5值比较输入内容是否相同
	public String getMd5PWD() {
		return md5PWD;
	}
	
	// 对密码进行RSA加密
	public void encryptPassword(String pwd) throws Exception{
		pwd = pwd + "FF";
		
		// 公钥初始化
		String mod = ApplicationEnvironment.getInstance().getPreferences().getString(Constant.PUBLICKEY_MOD, Constant.INIT_PUBLICKEY_MOD);
		String exp = ApplicationEnvironment.getInstance().getPreferences().getString(Constant.PUBLICKEY_EXP, Constant.INIT_PUBLICKEY_EXP);
		
		if ("".equals(mod) || "".equals(exp)){
			throw new Exception(this.getResources().getString(R.string.noPublicKey));
		}
			
		try{
			BigInteger m = new BigInteger(mod, 16);
		    BigInteger e = new BigInteger(exp, 16);
		    RSAPublicKeySpec keySpec = new RSAPublicKeySpec(m, e);
		    KeyFactory fact = KeyFactory.getInstance("RSA");
		    PublicKey pubKey = fact.generatePublic(keySpec);
		    
		    //Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
		    Cipher cipher = Cipher.getInstance("RSA");
		  
			cipher.init(Cipher.ENCRYPT_MODE, pubKey);
			byte[] cipherData = cipher.doFinal(pwd.getBytes());
			
			encryptPWD = Util.BinToHex(cipherData, 0, cipherData.length).substring(0, 32);//截取32位
			//Log.e("pwd_encry", encryptPWD);
			pwd = "";
			
		}catch(Exception e){
			throw new Exception(this.getResources().getString(R.string.noPublicKey));
		}
		
	}
	
	// 取密码的MD5值
	public void calcMD5Password(String pwd){
		md5PWD = StringUtil.MD5Crypto(pwd);
		pwd = "";
	}

	@Override
	public void afterTextChanged(Editable editable) {
		String pwd = editable.toString();
		if (pwd.length() == 6){
			try {
				encryptPassword(pwd);
				calcMD5Password(pwd);
    			pwd = "";
    			
			} catch (Exception e) {
				getEditText().setText("");
				TransferLogic.getInstance().gotoCommonFaileActivity(e.getMessage());
			}
		}
	}

	@Override
	public void beforeTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
	}

	@Override
	public void onTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
	}
	
	// 点击打密码输入框时弹出自定义软键盘
	@Override
	public boolean onTouch(View v, MotionEvent event) {
		if (event.getAction() == MotionEvent.ACTION_DOWN){
			this.showPopup();
		}
		return false;
	}

	// 主要是用于有的输入法选择下一项时弹出自定义软键盘
	// 但是也要防止刚进入界面时，界面中密码框第一个获得焦点时弹出软键盘，设置其为只有系统键盘弹出时才弹出自定义软键盘
	// isActive()并不是判断软键盘是否已弹出，而是是否处于活动状态，即此控件是否能调用软键盘
	/*
	 * if(getWindow().getAttributes().softInputMode==WindowManager.LayoutParams.SOFT_INPUT_STATE_UNSPECIFIED){
		 //隐藏软键盘
		 getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
		 getWindow().getAttributes().softInputMode=WindowManager.LayoutParams.SOFT_INPUT_STATE_UNSPECIFIED;
		}
	 */
	@Override
	public void onFocusChange(View v, boolean hasFocus) {
		// TODO 测试值为290.或是在showPopup中调用强制显示软键盘。。。
		//Log.e("++", ""+BaseActivity.getTopActivity().getWindow().getAttributes().softInputMode);
		
		if (hasFocus && BaseActivity.getTopActivity().getWindow().getAttributes().softInputMode != 290){
			this.showPopup();
			
		} else {
			this.hidePopup();
		}
	}
	
	private void showPopup(){
		if (null != popup && popup.isShowing()){
			return;
		}
		
		RandomPWDKeyboardView keyboardView = new RandomPWDKeyboardView(context);
		popup = new PopupWindow(keyboardView,LayoutParams.FILL_PARENT,LayoutParams.WRAP_CONTENT, true);
		// 如果没有设置此项目，setOutsideTouchable setFocusable将不起作用。而且必须在setOutsideTouchable setFocusable前面设置
		// 请放心，设置 BackgroundDrawable 并不会改变你在配置文件中设置的背景颜色或图像。
		popup.setBackgroundDrawable(new BitmapDrawable()); 
		popup.setFocusable(false); // 必须为false。否则系统键盘会遮挡自定义键盘
		popup.setTouchable(true); // 必须为true。否则自定义键盘不起作用无法点击，会使背后的系统键盘响应点击事件。
		popup.setOutsideTouchable(false);
		popup.setOnDismissListener(new OnDismissListener(){
			@Override
			public void onDismiss() {
				// 可能有重复调用，但是一定要保证关闭自定义软键盘后一定关闭了系统键盘
				closeSystemKeyBoard();
			}
			
		});
		
		// 非常非常重要的一步，完美解决返回按纽让系统键盘与自定义键盘同时消失！！！
		this.editText.setOnKeyListener(new OnKeyListener(){
			@Override
			public boolean onKey(View v, int keyCode, KeyEvent event) {
				if (keyCode == KeyEvent.KEYCODE_BACK){
					popup.dismiss();
				}
				return false;
			}
			
		});
		
		// 必须设置的属性
		keyboardView.setEditText(editText);
		keyboardView.setPopup(popup);
		
		popup.showAtLocation(this, Gravity.BOTTOM, 0, 0);
	}
	
	// 隐藏系统自定义软键盘
	private void hidePopup(){
		if (null != popup && popup.isShowing()){
			// 保证关闭自定义软键盘的时候一定关闭了系统键盘。不过使用系统键盘也没有关系，也能保证加密完成交易。
			closeSystemKeyBoard();
			popup.dismiss();
		}
	}
	
	// 关闭系统输入法
	private void closeSystemKeyBoard(){
		 this.getIMM().hideSoftInputFromWindow(editText.getWindowToken(), 0);
	}
	
	private InputMethodManager getIMM(){
		if (null == imm){
			imm = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
		}
		
		return imm;
	}
	
	public EditText getEditText(){
		return this.editText;
	}
	
	public void setHint(String hint){
		editText.setHint(hint);
	}
	
	public void setIconAndHint(int icon, String hint){
		iv_icon.setBackgroundResource(icon);
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
