package com.bfb.pos.activity;

import java.util.HashMap;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.bfb.pos.activity.view.PasswordWithIconView;
import com.bfb.pos.agent.client.AppDataCenter;
import com.bfb.pos.agent.client.Constant;
import com.bfb.pos.dynamic.core.Event;
import com.bfb.pos.util.ByteUtil;
import com.bfb.pos.util.UnionDes;
import com.bfb.pos.util.XORUtil;
import com.bfb.pos.R;
import com.dhcc.pos.packets.util.ConvertUtil;

//帐户提现  信息界面
public class DrawMoneyConfirmActivity extends BaseActivity implements OnClickListener {

	private TextView tv_trade_name = null;
	private TextView tv_account = null;
	private TextView tv_money = null;
	private PasswordWithIconView et_pwd_pay;
	
	private HashMap<String, String> recMap = null;
	
	@SuppressWarnings("unchecked")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		this.setContentView(R.layout.activity_drawmoney_confirm);

		Button btn_back = (Button) this.findViewById(R.id.btn_back);
		btn_back.setOnClickListener(this);

		Intent intent = this.getIntent();
		recMap = (HashMap<String, String>) intent.getSerializableExtra("map");
		
		tv_trade_name = (TextView) this.findViewById(R.id.tv_trade_name);
		
		tv_account = (TextView) this.findViewById(R.id.tv_account);
		tv_account.setText(recMap.get("tel") == null ? "":recMap.get("tel"));

		tv_money = (TextView) this.findViewById(R.id.tv_money);
		tv_money.setText("¥ "+recMap.get("field4"));
		et_pwd_pay = (PasswordWithIconView)this.findViewById(R.id.et_pwd_pay);
		et_pwd_pay.setIconAndHint(R.drawable.icon_pwd, "支付密码");
		
		Button btn_confirm = (Button) this.findViewById(R.id.btn_confirm);
		btn_confirm.setOnClickListener(this);

	}

	@Override
	public void onClick(View arg0) {
		switch (arg0.getId()) {
		case R.id.btn_back:
			this.finish();
			break;
		case R.id.btn_confirm:
			if(et_pwd_pay.getText().length() == 0){
				this.showToast("密码不能为空！");
			}else{
				actionDrawMoney();
			}
			break;

		default:
			break;
		}

	}

	@SuppressLint("DefaultLocale")
	private void actionDrawMoney(){
		if(Constant.isAISHUA){
			try {

				Event event = new Event(null, "xiaofei", null);
				event.setTransfer("080002");
				// String fsk="Get_VendorTerID|null";
				// event.setFsk(fsk);
				HashMap<String, String> map = new HashMap<String, String>();

				map.put("pwd", et_pwd_pay.getEncryptPWD());
				
				Random r = new Random();
				String flagStr = "01";
				String randomStr = String.format("%016d", r.nextInt(100));
				AppDataCenter.setRandom(randomStr);
				map.put("RANDROMSTR", flagStr+randomStr);
				map.put("field4", Integer.valueOf(recMap.get("field4"))*100+"");
				event.setStaticActivityDataMap(map);
				event.trigger();

			} catch (Exception e) {
				e.printStackTrace();
			}
		}else{
			try{
				
				Event event = new Event(null,"drawMoney", null);
		        event.setTransfer("080002");
		        String fsk="Get_VendorTerID|null";
				event.setFsk(fsk);
		        HashMap<String, String> map = new HashMap<String, String>();
		        map.put("field4", recMap.get("field4"));
		        map.put("pwd", et_pwd_pay.getEncryptPWD());
		        event.setStaticActivityDataMap(map);
		        event.trigger();
		        
			}catch(Exception e){
				e.printStackTrace();
			}
		}
		
	}
	
}
