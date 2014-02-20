package com.bfb.pos.activity;

import java.util.HashMap;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

import com.bfb.pos.activity.view.PasswordWithIconView;
import com.bfb.pos.agent.client.ApplicationEnvironment;
import com.bfb.pos.agent.client.Constant;
import com.bfb.pos.dynamic.core.Event;
import com.bfb.pos.R;


public class FindPwdModifyActivity extends BaseActivity {
	private Button btn_confirm;
	private PasswordWithIconView et_pwd_new;
	private PasswordWithIconView et_pwd_confirm;
	private Boolean b_flag = false;
	private String smscode ; 
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_getpwd_modify_login_pwd);
		
		Button btn_back = (Button)this.findViewById(R.id.btn_back);
		btn_back.setOnClickListener(listener);
		btn_confirm = (Button)this.findViewById(R.id.btn_confirm);
		btn_confirm.setOnClickListener(listener);
		
		Intent intent = this.getIntent();
		smscode = intent.getStringExtra("smscode");
		b_flag = intent.getBooleanExtra("b_flag", false);
		TextView tv_title = (TextView)this.findViewById(R.id.titleTV);
		tv_title.setText(b_flag == false ? "修改登录密码":"修改支付密码");
		
		et_pwd_new = (PasswordWithIconView)this.findViewById(R.id.et_pwd_new);
		et_pwd_confirm = (PasswordWithIconView)this.findViewById(R.id.et_pwd_confirm);
		et_pwd_new.setIconAndHint(R.drawable.icon_pwd, "新密码");
		et_pwd_confirm.setIconAndHint(R.drawable.icon_pwd, "确认密码");
		
	}

	private OnClickListener listener = new OnClickListener() {

		@Override
		public void onClick(View v) {
			switch(v.getId()){
			case R.id.btn_confirm:
				{
					if(checkValue()){
						try{
							String type = "0";
							Event event = new Event(null,"getPassword", null);
							if(b_flag == false){
								event.setTransfer("089015");
							}else{
								type = "1";
								event.setTransfer("089017");	
							}
							
							HashMap<String, String> map = new HashMap<String, String>();
							map.put("smscode", smscode);
							map.put("type", type);
							map.put("tel", ApplicationEnvironment.getInstance().getPreferences().getString(Constant.PHONENUM, ""));
							map.put(Constant.PASS, et_pwd_confirm.getEncryptPWD());
							event.setStaticActivityDataMap(map);
					        event.trigger();
					        
						}catch(Exception e){
							e.printStackTrace();
						}
					}
					break;
				}
			case R.id.btn_back:
			{
				FindPwdModifyActivity.this.finish();
				break;
			}
			}
			
		}

		
	};
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.login, menu);
		return true;
	}

	private Boolean checkValue(){
		
		if(et_pwd_new.getText().length() == 0){
			this.showToast("新密码不能为空！");
			return false;
		}else if(et_pwd_confirm.getText().length() == 0){
			this.showToast("确认密码不能为空！");
			return false;
		}else if(!(et_pwd_new.getMd5PWD().equals(et_pwd_confirm.getMd5PWD()))){
			this.showToast("两次密码输入不一致！");
			et_pwd_new.setText("");
			et_pwd_confirm.setText("");
			return false;
		}
		return true;
	}
	
	
}
