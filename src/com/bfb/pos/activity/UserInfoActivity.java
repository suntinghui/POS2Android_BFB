package com.bfb.pos.activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;

import com.bfb.pos.R;

//用户信息
public class UserInfoActivity extends BaseActivity implements OnClickListener {
	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	    requestWindowFeature( Window.FEATURE_NO_TITLE );
		this.setContentView(R.layout.activity_user_info);
		
		Button btn_back = (Button)this.findViewById(R.id.btn_back);
		btn_back.setOnClickListener(this);
		
		Button btn_confirm = (Button)this.findViewById(R.id.btn_confirm);
		btn_confirm.setOnClickListener(this);
		
	}

	@Override
	public void onClick(View arg0) {
		switch (arg0.getId()) {
		case R.id.btn_back:
			this.finish();
			break;
		case R.id.btn_confirm:
			Intent intent_c = new Intent(UserInfoActivity.this, AuthenticationUpImageActivity.class);
			UserInfoActivity.this.startActivityForResult(intent_c, 1);
			break;
		default:
			break;
		}
		
	}

	
	
}
