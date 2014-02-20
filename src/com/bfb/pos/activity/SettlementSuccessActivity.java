package com.bfb.pos.activity;

import java.util.HashMap;

import com.bfb.pos.R;

import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

public class SettlementSuccessActivity extends BaseActivity {
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		this.setContentView(R.layout.settlementsuccess);
		
		@SuppressWarnings("unchecked")
		HashMap<String, String> map = (HashMap<String, String>) this.getIntent().getSerializableExtra("map");
		
		((TextView)this.findViewById(R.id.fieldMessage)).setText(map.get("fieldMessage"));
		
		((TextView)this.findViewById(R.id.debitCount)).setText(map.get("debitCount")+" 笔");
		((TextView)this.findViewById(R.id.debitAmount)).setText(map.get("debitAmount"));
		
		((TextView)this.findViewById(R.id.creditCount)).setText(map.get("creditCount")+" 笔");
		((TextView)this.findViewById(R.id.creditAmount)).setText(map.get("creditAmount"));
		
		((Button)this.findViewById(R.id.okButton)).setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View arg0) {
				setResult(RESULT_OK);
				finish();
			}
			
		});
	}

}
