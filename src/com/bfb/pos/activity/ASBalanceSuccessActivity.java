package com.bfb.pos.activity;

import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

import com.bfb.pos.agent.client.AppDataCenter;
import com.bfb.pos.R;

public class ASBalanceSuccessActivity extends BaseActivity implements OnClickListener {
	
	private TextView tv_balance = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_aishua_balance_success);

		this.findViewById(R.id.topInfoView);

		Button btn_back = (Button) this.findViewById(R.id.backButton);
		btn_back.setOnClickListener(this);
		Button btn_confirm = (Button) this.findViewById(R.id.btn_confirm);
		btn_confirm.setOnClickListener(this);
		
		tv_balance = (TextView) this.findViewById(R.id.tv_balance);
		
		int type = this.getIntent().getIntExtra("TYPE", 1);
		
		if (type == 1) {
			tv_balance.setText("卡号：" + AppDataCenter.getMaskedPAN() + "\n\n余额：100.00");
		} else {
			tv_balance.setText("交易成功");
		}

	}

	@Override
	public void onClick(View arg0) {
		switch (arg0.getId()) {
		case R.id.backButton:
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
