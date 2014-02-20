package com.bfb.pos.activity;

import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bfb.pos.model.TransferDetailModel;
import com.bfb.pos.util.StringUtil;
import com.bfb.pos.R;

public class TransferDetailActivity extends BaseActivity implements OnClickListener {
	
	private TransferDetailModel model = null;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		this.setContentView(R.layout.activity_transfer_detail);
		
		this.findViewById(R.id.topInfoView);
		
		Button btn_back = (Button) this.findViewById(R.id.btn_back);
		btn_back.setOnClickListener(this);
		
		Button btn_confirm = (Button) this.findViewById(R.id.btn_confirm);
		btn_confirm.setOnClickListener(this);
		
		model = (TransferDetailModel) getIntent().getSerializableExtra("model");
		
		TextView tv_account1 = (TextView)this.findViewById(R.id.tv_account1);
		TextView tv_amount = (TextView)this.findViewById(R.id.tv_amount);
		TextView tv_card_branch_id = (TextView)this.findViewById(R.id.tv_card_branch_id);
		TextView tv_local_log = (TextView)this.findViewById(R.id.tv_local_log);
		TextView tv_localdate = (TextView)this.findViewById(R.id.tv_localdate);
		TextView tv_localtime = (TextView)this.findViewById(R.id.tv_localtime);
		TextView tv_snd_log = (TextView)this.findViewById(R.id.tv_snd_log);
		TextView tv_snd_cycle = (TextView)this.findViewById(R.id.tv_snd_cycle);
		TextView tv_systransid = (TextView)this.findViewById(R.id.tv_systransid);
		TextView tv_rspmsg = (TextView)this.findViewById(R.id.tv_rspmsg);
		TextView tv_flag = (TextView)this.findViewById(R.id.tv_flag);
		
		tv_account1.setText(StringUtil.formatAccountNo(model.getAccount1()));
		tv_amount.setText("¥ "+ model.getAmount());
		tv_card_branch_id.setText(model.getCard_branch_id());
		tv_local_log.setText(model.getLocal_log());
		tv_localdate.setText(model.getLocaldate());
		tv_localtime.setText(model.getLocaltime());
		tv_snd_log.setText(model.getSnd_log());
		tv_snd_cycle.setText(model.getSnd_cycle());
		tv_systransid.setText(model.getSystransid());
		tv_rspmsg.setText(model.getRspmsg());
		tv_flag.setText(model.getFlag());
		
		
	}
	

	@Override
	public void onClick(View arg0) {
		// TODO Auto-generated method stub
		switch (arg0.getId()) {
		case R.id.btn_back:
			this.finish();
			break;
		case R.id.btn_confirm:
			this.finish();
			break;

		default:
			break;
		}
	}


}
