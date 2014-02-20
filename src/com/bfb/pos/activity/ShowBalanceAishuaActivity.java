package com.bfb.pos.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

import com.bfb.pos.agent.client.Constant;
import com.bfb.pos.dynamic.core.Event;
import com.bfb.pos.util.StringUtil;
import com.dhc.dynamic.parse.ParseView;
import com.bfb.pos.R;

public class ShowBalanceAishuaActivity extends BaseActivity implements OnClickListener {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_showbalance_aishua);

		this.findViewById(R.id.topInfoView);

		Intent intent = this.getIntent();
		String balance_tmp = intent.getStringExtra("balance");
		String balance = StringUtil.String2SymbolAmount(balance_tmp.substring(9));
		String accountNo = StringUtil.formatAccountNo(intent.getStringExtra("accountNo"));
		
		Button btn_back = (Button) this.findViewById(R.id.btn_back);
		btn_back.setOnClickListener(this);
		Button btn_swip = (Button) this.findViewById(R.id.btn_confirm);
		btn_swip.setOnClickListener(this);
		
		TextView tv_balance = (TextView)this.findViewById(R.id.tv_balance);
		tv_balance.setText(balance != null ? balance:"");
		TextView tv_accountNo = (TextView)this.findViewById(R.id.tv_accountNo);
		tv_accountNo.setText(accountNo != null ? accountNo:"");
		

	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btn_back:
			this.finish();

			break;
		case R.id.btn_confirm:
			this.setResult(RESULT_OK);
			this.finish();
			break;

		default:
			break;
		}

	}

}
