package com.bfb.pos.activity;

import java.util.HashMap;

import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.text.Layout;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bfb.pos.activity.view.PasswordWithIconView;
import com.bfb.pos.activity.view.TextWithIconView;
import com.bfb.pos.agent.client.ApplicationEnvironment;
import com.bfb.pos.agent.client.Constant;
import com.bfb.pos.dynamic.core.Event;
import com.bfb.pos.model.UserModel;
import com.bfb.pos.util.PatternUtil;
import com.bfb.pos.R;

public class RegisterImproveModifyActivity  extends BaseActivity {
	private Button btn_back;
	
	//未完善
	private Button btn_regist;
	private TextWithIconView et_merchant_name;
	private TextWithIconView et_pid;
	private TextWithIconView et_email;
	
	private PasswordWithIconView et_pwd_pay;
	private PasswordWithIconView et_pwd_confirm;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_register_improve_modify);
		
		Intent intent = this.getIntent();
		String tmp_merchart_name = intent.getStringExtra("merchart_name");
		String tmp_pid = intent.getStringExtra("pid");
		
		btn_back = (Button) this.findViewById(R.id.btn_back);
		btn_back.setOnClickListener(listener);
		
		btn_regist = (Button) this.findViewById(R.id.btn_regist);
		btn_regist.setOnClickListener(listener);
		et_merchant_name = (TextWithIconView)this.findViewById(R.id.et_merchant_name);
		et_merchant_name.setHintString("商户名称");
		et_merchant_name.setInputType(InputType.TYPE_CLASS_TEXT);
		et_merchant_name.setIcon(R.drawable.icon_merchant_name);
		if(tmp_merchart_name !=null){
			et_merchant_name.setText(tmp_merchart_name);
		}
		et_pid = (TextWithIconView)this.findViewById(R.id.et_pid);
		et_pid.setIcon(R.drawable.icon_idcard);
		et_pid.setHintString("身份证");
		if(tmp_pid != null){
			et_pid.setText(tmp_pid);
		}
		et_email = (TextWithIconView)this.findViewById(R.id.et_email);
		et_email.setInputType(InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
		et_email.setHintString("邮箱地址");
		et_email.setIcon(R.drawable.icon_mail);

		et_pwd_pay = (PasswordWithIconView)this.findViewById(R.id.et_pwd_pay);
		et_pwd_pay.setIconAndHint(R.drawable.icon_pwd, "设置支付密码");
		et_pwd_confirm = (PasswordWithIconView)this.findViewById(R.id.et_pwd_confirm);
		et_pwd_confirm.setIconAndHint(R.drawable.icon_pwd, "确认支付密码");
		
	}
	
	private OnClickListener listener = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.btn_back:
				finish();
				break;
			case R.id.btn_regist:
				if(checkValue()){
					actionRegist();
				}
				break;
			default:
				break;
			}
			
		}
	};
	
	private void actionRegist(){
		try{
			Event event = new Event(null,"registerImprove", null);
			event.setTransfer("089010");
			HashMap<String, String> map = new HashMap<String, String>();
			map.put("merchant_name", et_merchant_name.getText());
			map.put("pid", et_pid.getText());
			map.put("email", et_email.getText());
			map.put("paypass", et_pwd_confirm.getEncryptPWD());
			map.put("tel", ApplicationEnvironment.getInstance().getPreferences().getString(Constant.PHONENUM, ""));
			event.setStaticActivityDataMap(map);
	        event.trigger();
	        
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	private Boolean checkValue(){
		if(et_merchant_name.getText().length() == 0){
			this.showToast("商户名称不能为空!");
			return false;
		}
		if(et_pid.getText().length() == 0){
			this.showToast("身份证不能为空!");
			return false;
		}
		if(!PatternUtil.isValidIDNum(et_pid.getText())){
			this.showToast("身份证号码不合法!");
			return false;
		}
		
		if(et_email.getText().length() == 0){
			this.showToast("邮箱地址不能为空!");
			return false;
		}
		if(!PatternUtil.isValidEmail(et_email.getText())){
			this.showToast("邮箱地址不合法!");
			return false;
		}
		if(et_pwd_pay.getText().length() == 0){
			this.showToast("密码不能为空！");
			return false;
		}
		if(et_pwd_confirm.getText().length() == 0){
			this.showToast("密码不能为空！");
			return false;
		}
		if(!et_pwd_pay.getMd5PWD().equals(et_pwd_confirm.getMd5PWD())){
			this.showToast("密码输入不一致，请重新输入！");
			et_pwd_pay.setText("");
			et_pwd_confirm.setText("");
			return false;
		}
		return true;
	}
	
}
