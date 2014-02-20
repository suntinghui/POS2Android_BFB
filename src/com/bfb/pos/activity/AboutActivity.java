package com.bfb.pos.activity;

import com.bfb.pos.R;

import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class AboutActivity extends BaseActivity {
	
	private Button backButton = null;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		this.setContentView(R.layout.about);
		 
		backButton = (Button) this.findViewById(R.id.backButton);
		backButton.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View arg0) {
				finish();
				
			}
			
		});
	}

}
