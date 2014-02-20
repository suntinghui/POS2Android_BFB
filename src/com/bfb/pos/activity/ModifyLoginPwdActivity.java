package com.bfb.pos.activity;

import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.InputType;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;

import com.bfb.pos.activity.view.PasswordWithIconView;
import com.bfb.pos.activity.view.TextWithIconView;
import com.bfb.pos.agent.client.ApplicationEnvironment;
import com.bfb.pos.agent.client.Constant;
import com.bfb.pos.dynamic.core.Event;
import com.bfb.pos.R;

//修改登录密码
public class ModifyLoginPwdActivity extends BaseActivity implements OnClickListener {
	private PasswordWithIconView et_pwd_old;
	private PasswordWithIconView et_pwd_new;
	private PasswordWithIconView et_pwd_confirm;
	private EditText et_sms;
	private Button btn_sms;
	private int recLen = 10;
    Timer timer = new Timer(); 
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		this.setContentView(R.layout.activity_modify_login_pwd);

		Button btn_back = (Button) this.findViewById(R.id.btn_back);
		btn_back.setOnClickListener(this);
		Button btn_confirm = (Button) this.findViewById(R.id.btn_confirm);
		btn_confirm.setOnClickListener(this);
		
		et_pwd_old = (PasswordWithIconView)this.findViewById(R.id.et_pwd_old);
		et_pwd_old.setIconAndHint(R.drawable.icon_pwd, "原登录密码");
		
		et_pwd_new = (PasswordWithIconView)this.findViewById(R.id.et_pwd_new);
		et_pwd_new.setIconAndHint(R.drawable.icon_pwd, "新登录密码");
		
		et_pwd_confirm = (PasswordWithIconView)this.findViewById(R.id.et_pwd_confirm);
		et_pwd_confirm.setIconAndHint(R.drawable.icon_pwd, "确认登录密码");
		
		et_sms = (EditText)this.findViewById(R.id.et_sms);
		btn_sms = (Button) this.findViewById(R.id.btn_sms);
		btn_sms.setOnClickListener(this);
	}

	@Override
	public void onClick(View arg0) {
		switch (arg0.getId()) {
		case R.id.btn_back:
			this.finish();
			break;
		case R.id.btn_confirm:
			if(checkValue()){
				HashMap<String, String> map = new HashMap<String, String>();
				map.put("old_password", et_pwd_old.getEncryptPWD());
				map.put("new_password", et_pwd_new.getEncryptPWD());
				map.put("smscode", et_sms.getText().toString());
				map.put("type", "0");
				map.put("tel", ApplicationEnvironment.getInstance().getPreferences().getString(Constant.PHONENUM, ""));
				try{
					Event event = new Event(null,"modifyLoginPwd", null);
			        event.setTransfer("089003");
			        event.setStaticActivityDataMap(map);
			        event.trigger();
			        
				}catch(Exception e){
					e.printStackTrace();
				}
			}
			
			break;
		case R.id.btn_sms:
			this.showToast("短信已发送，请注意查收!");
			actionGetSms();	
			
			break;
		default:
			break;
		}

	}

	@SuppressLint("SimpleDateFormat")
	private void actionGetSms(){
		SimpleDateFormat sDateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");   
		String date = sDateFormat.format(new java.util.Date());
		try{
			Event event = new Event(null,"getSms", null);
			event.setTransfer("089006");
			HashMap<String, String> map = new HashMap<String, String>();
			map.put("time", date);
			map.put("tel", ApplicationEnvironment.getInstance().getPreferences().getString(Constant.PHONENUM, ""));
			event.setStaticActivityDataMap(map);
	        event.trigger();
	        
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);
		
		switch (requestCode) {
		case 1:
			switch (resultCode) {
			case Activity.RESULT_OK:
			
			}
			break;

		default:
			break;
		}
	}

	private Boolean checkValue(){
		if(et_pwd_old.getText().length() == 0){
			this.showToast("原密码不能为空！");
			return false;
		}
		if(et_pwd_new.getText().length() == 0){
			this.showToast("新密码不能为空！");
			return false;
		}
		if(et_pwd_confirm.getText().length() == 0){
			this.showToast("确认密码不能为空！");
			return false;
		}
		if(!et_pwd_new.getMd5PWD().equals(et_pwd_confirm.getMd5PWD())){
			this.showToast("密码输入不一致，请重新输入！");
			et_pwd_new.setText("");
			et_pwd_confirm.setText("");
			return false;
		}
		return true;
	}
	
	@SuppressLint("HandlerLeak")
	final Handler handler = new Handler(){  
        @Override  
        public void handleMessage(Message msg){  
            switch (msg.what) {  
            case 1:  
                btn_sms.setText(String.format("请等待短信，%d秒", recLen));  
                if(recLen < 0){
                	btn_sms.setText("获取短信校验码");
                    timer.cancel();  
                }  
            }  
        }  
    };  
  
    TimerTask task = new TimerTask() {  
        @Override  
        public void run() {  
            recLen--;  
            Message message = new Message();  
            message.what = 1;  
            handler.sendMessage(message);  
        }  
    };
    
    public void refreshSMSBtn(){
        timer.schedule(task, 1000, 1000);       // timeTask  

    }
}
