package com.bfb.pos.activity;

import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.InputType;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;

import com.bfb.pos.activity.view.PasswordWithIconView;
import com.bfb.pos.activity.view.TextWithIconView;
import com.bfb.pos.dynamic.core.Event;
import com.bfb.pos.util.PatternUtil;
import com.bfb.pos.R;

public class RegisterActivity  extends BaseActivity {
	private Button btn_back;
	private Button btn_regist;
	private ImageButton ib_agree;
	private TextWithIconView et_phone;
	private PasswordWithIconView et_pwd_new;
	private PasswordWithIconView et_pwd_confirm;
	private EditText et_sms;
	private Button btn_sms;
	
	private int recLen = 10;
    Timer timer = new Timer(); 
    
	private Boolean isAgree = false;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_register);
		
		btn_back = (Button) this.findViewById(R.id.btn_back);
		btn_back.setOnClickListener(listener);
		btn_regist = (Button) this.findViewById(R.id.btn_regist);
		btn_regist.setOnClickListener(listener);
		ib_agree = (ImageButton)this.findViewById(R.id.ib_agree);
		ib_agree.setOnClickListener(listener);
		
		et_phone = (TextWithIconView)this.findViewById(R.id.et_phone);
		et_phone.setInputType(InputType.TYPE_CLASS_NUMBER);
        et_phone.setHintString("手机号码");
        et_phone.setIcon(R.drawable.icon_phone);
        
		et_pwd_new = (PasswordWithIconView)this.findViewById(R.id.et_pwd_new);
		et_pwd_new.setIconAndHint(R.drawable.icon_pwd, "新密码");
		
		et_pwd_confirm = (PasswordWithIconView)this.findViewById(R.id.et_pwd_confirm);
		et_pwd_confirm.setIconAndHint(R.drawable.icon_pwd, "确认密码");
		
		et_sms = (EditText)this.findViewById(R.id.et_sms);
		et_sms.setInputType(InputType.TYPE_CLASS_NUMBER);
		btn_sms = (Button) this.findViewById(R.id.btn_sms);
		btn_sms.setOnClickListener(listener);
		
		Button btn_protocol = (Button)this.findViewById(R.id.btn_protocol);
		btn_protocol.setOnClickListener(listener);
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
			case R.id.btn_protocol:
				actionShowProtocol();
				break;
			case R.id.ib_agree:
				
				if(isAgree){
					ib_agree.setBackgroundResource(R.drawable.remeberpwd_n);
				}else{
					ib_agree.setBackgroundResource(R.drawable.remeberpwd_s);
				}
				isAgree = !isAgree;
				break;
			case R.id.btn_sms:
				if(et_phone.getText().length() == 0){
					RegisterActivity.this.showToast("手机号不能为空!");
				}else{
					RegisterActivity.this.showToast("短信已发送，请注意查收!");
					actionGetSms();	
				}
				
				break;
			default:
				break;
			}
			
		}
	};
	
	@SuppressLint("SimpleDateFormat")
	private void actionGetSms(){
		SimpleDateFormat sDateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");   
		String date = sDateFormat.format(new java.util.Date());
		try{
			Event event = new Event(null,"getSms", null);
			event.setTransfer("089006");
			HashMap<String, String> map = new HashMap<String, String>();
			map.put("tel", et_phone.getText());
			map.put("time", date);
			event.setStaticActivityDataMap(map);
	        event.trigger();
	        
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	private void actionShowProtocol(){
		Intent intent = new Intent(RegisterActivity.this, ShowProtocolActivity.class);
		RegisterActivity.this.startActivity(intent);
	}
	private void actionRegist(){
		try{
			Event event = new Event(null,"register", null);
			event.setTransfer("089001");
			
			HashMap<String, String> map = new HashMap<String, String>();
			map.put("tel", et_phone.getText());
			map.put("smscode", et_sms.getText().toString());
			map.put("logpass", et_pwd_confirm.getEncryptPWD());
			event.setStaticActivityDataMap(map);
	        event.trigger();
	        
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	private Boolean checkValue(){
		if(et_phone.getText().length() == 0){
			this.showToast("手机号不能为空!");
			return false;
		}
		if(et_pwd_new.getText().length() == 0){
			this.showToast("密码不能为空！");
			return false;
		}
		if(et_pwd_confirm.getText().length() == 0){
			this.showToast("密码不能为空！");
			return false;
		}
		if(!(et_pwd_new.getMd5PWD().equals(et_pwd_confirm.getMd5PWD()))){
			this.showToast("密码输入不一致，请重新输入！");
			et_pwd_new.setText("");
			et_pwd_confirm.setText("");
			return false;
		}
		if(!isAgree){
			this.showToast("请先阅读并同意服务协议！");
			return false;
		}
		return true;
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);
		
		if(resultCode == RESULT_OK){
			
		}
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
