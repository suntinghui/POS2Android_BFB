package com.bfb.pos.activity;

import java.text.SimpleDateFormat;
import java.util.HashMap;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;

import com.bfb.pos.activity.view.TextWithIconView;
import com.bfb.pos.dynamic.core.Event;
import com.bfb.pos.R;

public class ShowProtocolActivity  extends BaseActivity {
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_protocol);
		
		Button btn_back = (Button) this.findViewById(R.id.btn_back);
		btn_back.setOnClickListener(listener);
		Button btn_agree = (Button) this.findViewById(R.id.btn_agree);
		btn_agree.setOnClickListener(listener);
		
	}
	
	private OnClickListener listener = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.btn_back:
				finish();
				break;
			case R.id.btn_agree:
				ShowProtocolActivity.this.finish();

				break;
			default:
				break;
			}
			
		}
	};
	
}
