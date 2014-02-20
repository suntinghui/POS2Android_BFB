package com.bfb.pos.activity;

import java.util.HashMap;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bfb.pos.agent.client.ApplicationEnvironment;
import com.bfb.pos.agent.client.Constant;
import com.bfb.pos.dynamic.core.Event;
import com.bfb.pos.model.UserBankModel;
import com.dhc.dynamic.util.StringUtil;
import com.bfb.pos.R;

//帐户提现  用户须知
public class DrawMoneyActivity extends BaseActivity implements OnClickListener {
	private Button btn_go_info = null;
	private Button btn_add_account = null;
	private TextView tv_name = null;
	private TextView tv_account = null;
	private HashMap<String, String> recMap = null;
	private RelativeLayout reLayout = null;

	private UserBankModel userBankModel = new UserBankModel();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		this.setContentView(R.layout.activity_drawmoney);

		Button btn_back = (Button) this.findViewById(R.id.btn_back);
		btn_back.setOnClickListener(this);

//		btn_go_info = (Button) this.findViewById(R.id.btn_go_info);
//		btn_go_info.setOnClickListener(this);
		btn_add_account = (Button) this.findViewById(R.id.btn_add_account);
		btn_add_account.setOnClickListener(this);
		
		reLayout = (RelativeLayout)this.findViewById(R.id.reLayout_btn);
		reLayout.setOnClickListener(this);
		reLayout.setOnLongClickListener(longListener);

		tv_name = (TextView)this.findViewById(R.id.tv_name);
		tv_account = (TextView)this.findViewById(R.id.tv_account);
		refresh();
	}

	@Override
	public void onClick(View arg0) {
		switch (arg0.getId()) {
		case R.id.btn_back:
			this.finish();
			break;
		case R.id.btn_add_account:
			Intent intent_a = new Intent(DrawMoneyActivity.this, DrawMoneyAddAccountActivity.class);
			intent_a.putExtra("merchant_name", userBankModel.getMerchant_name());
			intent_a.putExtra("mastername", userBankModel.getMastername());
			DrawMoneyActivity.this.startActivityForResult(intent_a, 1);
			
			break;
		case R.id.reLayout_btn:
			Intent intent_c = new Intent(DrawMoneyActivity.this, DrawMoneyInfoActivity.class);
			intent_c.putExtra("map", recMap);
			DrawMoneyActivity.this.startActivityForResult(intent_c, 1);
			
			
			break;
		default:
			break;
		}

	}

	private void refresh() {
		try {
			Event event = new Event(null, "getBankAccount", null);
			event.setTransfer("089007");
			HashMap<String, String> map = new HashMap<String, String>();
			map.put("tel", ApplicationEnvironment.getInstance().getPreferences().getString(Constant.PHONENUM, ""));
	        event.setStaticActivityDataMap(map);
			event.trigger();

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void setReceiveMap(HashMap<String, String> map){
		recMap = map;
		userBankModel.setMerchant_name(recMap.get("merchant_name"));
		userBankModel.setMastername(recMap.get("mastername"));
		userBankModel.setBankaccount(recMap.get("bankaccount"));
		userBankModel.setBanks(recMap.get("banks"));
		userBankModel.setBankno(recMap.get("bankno"));
		userBankModel.setArea(recMap.get("area"));
		userBankModel.setCity(recMap.get("city"));
		userBankModel.setAddr(recMap.get("addr"));
		userBankModel.setIs_complete(recMap.get("is_complete"));
		if(userBankModel.getBankaccount() != null && userBankModel.getBankaccount().length() != 0 ){
			reLayout.setVisibility(View.VISIBLE);
			tv_name.setText(userBankModel.getMastername());
			tv_account.setText(userBankModel.getBankaccount());	
			
			btn_add_account.setVisibility(View.GONE);
			
		}else{
			btn_add_account.setVisibility(View.VISIBLE);
			reLayout.setVisibility(View.GONE);
		}
	}
	
	private OnLongClickListener longListener = new OnLongClickListener() {
		
		@Override
		public boolean onLongClick(View arg0) {
			Intent intent = new Intent(DrawMoneyActivity.this, DrawMoneyModifyAccountActivity.class );
			intent.putExtra("userBankModel", userBankModel);
			DrawMoneyActivity.this.startActivity(intent);
			
			return false;
		}
	};
	
}
