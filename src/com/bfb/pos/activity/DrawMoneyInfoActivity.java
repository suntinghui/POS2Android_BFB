package com.bfb.pos.activity;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.AdapterView.OnItemSelectedListener;

import com.bfb.pos.agent.client.AppDataCenter;
import com.bfb.pos.dynamic.core.Event;
import com.dhc.dynamic.util.StringUtil;
import com.bfb.pos.R;

//帐户提现  信息界面
public class DrawMoneyInfoActivity extends BaseActivity implements OnClickListener {

	private TextView tv_name = null;
	private TextView tv_phone = null;
	private TextView tv_current_day_money = null;
	private TextView tv_bank = null;
	private TextView tv_city = null;
	private TextView tv_account = null;
	private EditText et_money = null;
	private Spinner spinner = null;

	private HashMap<String, String> recMap = null;
	private ArrayAdapter<String> adapter;
	private String[] items = { "普通提现(免手续费)", "快速提现"};
	
	@SuppressWarnings("unchecked")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		this.setContentView(R.layout.activity_drawmoney_info);

		Button btn_back = (Button) this.findViewById(R.id.btn_back);
		btn_back.setOnClickListener(this);

		Intent intent = this.getIntent();
		recMap = (HashMap<String, String>) intent.getSerializableExtra("map");
		
		tv_name = (TextView) this.findViewById(R.id.tv_name);
		tv_name.setText(recMap.get("mastername") == null ? "":recMap.get("mastername"));
		
		tv_phone = (TextView) this.findViewById(R.id.tv_phone);
		tv_phone.setText(recMap.get("tel") == null ? "":recMap.get("tel"));
		tv_current_day_money = (TextView) this.findViewById(R.id.tv_current_day_money);
		tv_current_day_money.setText(recMap.get("balance") == null ? "":recMap.get("balance"));
		
		tv_bank = (TextView) this.findViewById(R.id.tv_bank);
		tv_bank.setText(recMap.get("banks") == null ? "":recMap.get("banks"));
		tv_city = (TextView) this.findViewById(R.id.tv_city);
		tv_city.setText(recMap.get("city") == null ? "":recMap.get("city"));
		tv_account = (TextView) this.findViewById(R.id.tv_account);
		tv_account.setText(recMap.get("bankaccount") == null ? "": StringUtil.formatAccountNo(recMap.get("bankaccount")));
		
		et_money = (EditText) this.findViewById(R.id.et_money);
		spinner = (Spinner) this.findViewById(R.id.spinner);
		
		ArrayAdapter<String> aa = new ArrayAdapter<String>(this,R.layout.simple_spinner_item,items); //第二个参数表示spinner没有展开前的UI类型
		spinner.setAdapter(aa);

		spinner.setOnItemSelectedListener(new SpinnerSelectedListener());
		
		Button btn_confirm = (Button) this.findViewById(R.id.btn_confirm);
		btn_confirm.setOnClickListener(this);

	}

	// 使用数组形式操作
	class SpinnerSelectedListener implements OnItemSelectedListener {

		public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2, long arg3) {

		}

		public void onNothingSelected(AdapterView<?> arg0) {
		}
	}

	@Override
	public void onClick(View arg0) {
		switch (arg0.getId()) {
		case R.id.btn_back:
			this.finish();
			break;
		case R.id.btn_confirm:
			if(et_money.getText().length() == 0){
				this.showToast("金额不能为空！");
			}else if((Integer.valueOf(et_money.getText().toString()) > Integer.valueOf(recMap.get("balance")))){
				this.showToast("余额不足！");
			}else if(Integer.valueOf(et_money.getText().toString()) < 100){
				this.showToast("提款金额不能小于100！");
			}else{
//				actionDrawMoney();
				recMap.put("field4", et_money.getText().toString());
				Intent intent = new Intent(DrawMoneyInfoActivity.this, DrawMoneyConfirmActivity.class);
				intent.putExtra("map", recMap);
				DrawMoneyInfoActivity.this.startActivity(intent);
				
			}
			break;
		case R.id.btn_sms:
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
			map.put("tel", AppDataCenter.getValue("__PHONENUMWITHLABEL"));
			map.put("time", date);
			event.setStaticActivityDataMap(map);
	        event.trigger();
	        
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	private void actionDrawMoney(){
		try{
			
			Event event = new Event(null,"drawMoney", null);
	        event.setTransfer("080002");
	        String fsk="Get_VendorTerID|null";
			event.setFsk(fsk);
	        HashMap<String, String> map = new HashMap<String, String>();
	        map.put("field4", et_money.getText().toString());
	        event.setStaticActivityDataMap(map);
	        event.trigger();
	        
		}catch(Exception e){
			e.printStackTrace();
		}
	}
}
