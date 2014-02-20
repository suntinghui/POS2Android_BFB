package com.bfb.pos.activity;

import java.util.HashMap;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bfb.pos.agent.client.ApplicationEnvironment;
import com.bfb.pos.agent.client.Constant;
import com.bfb.pos.dynamic.core.Event;
import com.bfb.pos.model.UserModel;
import com.bfb.pos.R;

//签到
public class SignActivity extends BaseActivity implements OnClickListener {
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		this.setContentView(R.layout.activity_sign);

		Button btn_back = (Button) this.findViewById(R.id.btn_back);
		btn_back.setOnClickListener(this);

		Button btn_sign = (Button)this.findViewById(R.id.btn_sign);
		btn_sign.setOnClickListener(this);

	}

	@Override
	public void onClick(View arg0) {
		switch (arg0.getId()) {
		case R.id.btn_back:
			this.finish();
			break;
		case R.id.btn_sign:
			refresh();
			
			break;

		default:
			break;
		}

	}

	private void refresh(){
		try {

			Event event = new Event(null, "sign", null);
			event.setTransfer("086000");
			String fsk="Get_PsamNo|null";
			if(Constant.isAISHUA){
				fsk = "getKsn|null";
			}
			event.setFsk(fsk);
			HashMap<String, String> map = new HashMap<String, String>();
			map.put("username", ApplicationEnvironment.getInstance().getPreferences().getString(Constant.PHONENUM, ""));
			event.setStaticActivityDataMap(map);
			event.trigger();

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void fromLogic(UserModel model){
		

	}
}
