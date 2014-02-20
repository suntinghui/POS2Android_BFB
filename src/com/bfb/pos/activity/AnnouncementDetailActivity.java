package com.bfb.pos.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

import com.bfb.pos.model.AnnouncementModel;
import com.dhc.dynamic.util.DateUtil;
import com.bfb.pos.R;

public class AnnouncementDetailActivity extends BaseActivity implements OnClickListener {
	
	private Button btn_back = null;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		this.setContentView(R.layout.activity_annountce_detail);
		
		this.findViewById(R.id.topInfoView);
		
		btn_back = (Button) this.findViewById(R.id.btn_back);
		btn_back.setOnClickListener(this);
		
		TextView tv_title = (TextView)this.findViewById(R.id.tv_title);
		TextView tv_content = (TextView)this.findViewById(R.id.tv_content);
		TextView tv_time = (TextView)this.findViewById(R.id.tv_time);
		
		Intent intent = this.getIntent();
		AnnouncementModel model = (AnnouncementModel) intent.getSerializableExtra("model");
		
		String date_str = model.getNotice_date();
		String time_str = model.getNotice_time();
		String tmp_data_time = DateUtil.formatDateTime(date_str+time_str); 

		tv_title.setText(model.getNotice_title() == null ? "":model.getNotice_title());
		tv_content.setText(model.getNotice_content() == null ? "":model.getNotice_content());
		tv_time.setText(tmp_data_time);
	}
	

	@Override
	public void onClick(View arg0) {
		// TODO Auto-generated method stub
		switch (arg0.getId()) {
		case R.id.btn_back:
			this.finish();
			break;

		default:
			break;
		}
	}
	
	
}
