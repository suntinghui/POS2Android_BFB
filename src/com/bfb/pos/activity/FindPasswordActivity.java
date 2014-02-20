package com.bfb.pos.activity;

import java.text.SimpleDateFormat;
import java.util.HashMap;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.text.InputFilter;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

import com.bfb.pos.activity.view.TextWithIconView;
import com.bfb.pos.agent.client.AppDataCenter;
import com.bfb.pos.agent.client.ApplicationEnvironment;
import com.bfb.pos.agent.client.Constant;
import com.bfb.pos.dynamic.core.Event;
import com.bfb.pos.util.PatternUtil;
import com.bfb.pos.R;

public class FindPasswordActivity extends BaseActivity implements OnClickListener {
	
	private Button btn_back;
	
	private TextWithIconView et_phone;
	private TextWithIconView et_name;
	private TextWithIconView et_identy_card;
	private EditText et_sms;
	private Button btn_sms;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
        setContentView(R.layout.activity_find_password);
        
        this.findViewById(R.id.topInfoView);
        
        btn_back = (Button)this.findViewById(R.id.btn_back);
        et_phone = (TextWithIconView)this.findViewById(R.id.et_phone);
        et_phone.setInputType(InputType.TYPE_CLASS_NUMBER);
        et_phone.setHintString("手机号码");
        String phone = ApplicationEnvironment.getInstance().getPreferences().getString(Constant.PHONENUM, "");
        if (phone.length() != 0) {
        	et_phone.setText(phone);			
		}
        et_phone.setIcon(R.drawable.icon_phone);
        et_name = (TextWithIconView)this.findViewById(R.id.et_name);
        et_identy_card = (TextWithIconView)this.findViewById(R.id.et_identy_card);
        et_identy_card.setIcon(R.drawable.icon_idcard);
        et_sms = (EditText) this.findViewById(R.id.et_sms);
        btn_sms = (Button) this.findViewById(R.id.btn_sms);
        Button btn_confirm = (Button)this.findViewById(R.id.btn_confirm);
        
        et_name.setHintString("真实姓名");
        et_identy_card.setHintString("身份证");
        et_identy_card.getEditText().setFilters(new InputFilter[]{new  InputFilter.LengthFilter(18)});  
        
        
        btn_confirm.setOnClickListener(this);
        btn_back.setOnClickListener(this);
        btn_sms.setOnClickListener(this);
	}
	
	@Override
	protected void onStart() {
		super.onStart();
		
	}
	
	
	@Override
	public void onClick(View v) {
		switch(v.getId()){
			case R.id.btn_sms:
				if(et_phone.getText().length() == 0){
					this.showToast("手机号不能为空!");
				}else{
					actionGetSms();	
				}
				
				break;
				
			case R.id.btn_confirm:
				if(checkValue()){
					actionNext();					
				}
				
				break;
				
			case R.id.btn_back:
				FindPasswordActivity.this.finish();
				break;
		}
		
	}
	
	@SuppressLint("SimpleDateFormat")
	private void actionGetSms(){
		SimpleDateFormat sDateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");   
		String date = sDateFormat.format(new java.util.Date());
		this.showToast("短信已发送，请注意查收!");
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
	
	private void actionNext(){
		Editor editor = ApplicationEnvironment.getInstance().getPreferences().edit();
		editor.putString(Constant.PHONENUM, et_phone.getText());
		editor.commit();
		try{
			Constant.PASS = "logpass";
			Event event = new Event(null,"checkInfo", null);
			event.setTransfer("089002");
			HashMap<String, String> map = new HashMap<String, String>();
			map.put("tel", et_phone.getText());
			map.put("smscode", et_sms.getText().toString());
			map.put("pid", et_identy_card.getText());
			map.put("merchant_name", et_name.getText());
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
		if(et_name.getText().length() == 0){
			this.showToast("姓名不能为空！");
			return false;
		}
		if(et_identy_card.getText().length() == 0){
			this.showToast("身份证号码不能为空！");
			return false;
		}
		if(!PatternUtil.isValidIDNum(et_identy_card.getText())){
			this.showToast("身份证号码不合法!");
			return false;
		}
		return true;
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);
		
	}
	
	
}
