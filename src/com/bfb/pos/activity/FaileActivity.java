package com.bfb.pos.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

import com.bfb.pos.agent.client.Constant;
import com.bfb.pos.dynamic.core.Event;
import com.dhc.dynamic.parse.ParseView;
import com.bfb.pos.R;

public class FaileActivity extends BaseActivity implements OnClickListener {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_faile);

		this.findViewById(R.id.topInfoView);

		Intent intent = this.getIntent();
		String prompt = intent.getStringExtra("prompt");
		Button btn_swip = (Button) this.findViewById(R.id.btn_confirm);
		btn_swip.setOnClickListener(this);
		
		TextView tv_promt = (TextView)this.findViewById(R.id.tv_promt);
		tv_promt.setText(prompt != null ? prompt:"交易失败");
		

	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btn_confirm:
			this.setResult(RESULT_OK);
			this.finish();
			break;

		default:
			break;
		}

	}

}
