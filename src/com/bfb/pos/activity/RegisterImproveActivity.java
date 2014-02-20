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

public class RegisterImproveActivity  extends BaseActivity {
	private Button btn_back;
	private UserModel model;
	//已完善
	private TextView tv_merchant_name;
	private TextView tv_type;
	private TextView tv_num;
	private TextView tv_status;
	
	private Button btn_modify ;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_register_improve);
		
		btn_back = (Button) this.findViewById(R.id.btn_back);
		btn_back.setOnClickListener(listener);
		
		tv_merchant_name = (TextView)this.findViewById(R.id.tv_merchant_name);
		tv_type = (TextView)this.findViewById(R.id.tv_type);
		tv_num = (TextView)this.findViewById(R.id.tv_num);
		tv_status = (TextView)this.findViewById(R.id.tv_status);
		
		btn_modify = (Button)this.findViewById(R.id.btn_modify);
		btn_modify.setOnClickListener(listener);
		
		getRegisterInfo();
		
	}
	
	private OnClickListener listener = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.btn_back:
				finish();
				break;
			case R.id.btn_modify:
				Intent intent = new Intent(RegisterImproveActivity.this, RegisterImproveModifyActivity.class);
				intent.putExtra("merchart_name", model.getMerchant_name());
				intent.putExtra("pid", model.getPid());
				RegisterImproveActivity.this.startActivityForResult(intent, 0);
				break;
			default:
				break;
			}
			
		}
	};
	
	private void getRegisterInfo(){
		try{
			//获取商户注册信息
			Event event = new Event(null,"getMerchantInfo", null);
			event.setTransfer("089013");
			HashMap<String, String> map = new HashMap<String, String>();
			map.put("tel", ApplicationEnvironment.getInstance().getPreferences().getString(Constant.PHONENUM, ""));
	        event.setStaticActivityDataMap(map);
	        event.trigger();
	        
		}catch(Exception e){
			e.printStackTrace();
		}
	}

	public void fromLogic(UserModel tmp_model){
		model = tmp_model;
		String tmp_status = tmp_model.getStatus();
		String status = null;
		tv_merchant_name.setText(tmp_model.getMerchant_name() == null ? "无":tmp_model.getMerchant_name());
		tv_type.setText("身份证");
		tv_num.setText(tmp_model.getPid() == null ? "无":tmp_model.getPid());
		
		
		if(tmp_status.equals("0")){
			status = "已注册未完善用户信息";
			btn_modify.setVisibility(View.VISIBLE);
		}else if(tmp_status.equals("1")){
			status = "已完善未审核";
			btn_modify.setVisibility(View.VISIBLE);
		}else if(tmp_status.equals("2")){
			status = "已初审，等待终审";
			btn_modify.setVisibility(View.VISIBLE);
		}else if(tmp_status.equals("9")){
			status = "终审通过";
		}
		tv_status.setText(status);
	}
}
