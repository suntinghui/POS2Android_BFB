package com.bfb.pos.activity;

import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

import com.bfb.pos.agent.client.Constant;
import com.bfb.pos.dynamic.core.Event;
import com.dhc.dynamic.parse.ParseView;
import com.bfb.pos.R;

public class ASSwipActivity extends BaseActivity implements OnClickListener {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_aishua_swip);

		this.findViewById(R.id.topInfoView);

		Button btn_back = (Button) this.findViewById(R.id.btn_back);
		btn_back.setOnClickListener(this);
		Button btn_swip = (Button) this.findViewById(R.id.btn_swip);
		btn_swip.setOnClickListener(this);

	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btn_back:
			this.finish();

			break;
		case R.id.btn_swip:
			swipAction();

			break;

		default:
			break;
		}

	}

	private void swipAction() {
		if (!Constant.isAISHUA) {
			try {

				Event event = new Event(null, "sign", null);
				event.setTransfer("020001");
				String fsk = "Get_PsamNo|null#Get_VendorTerID|null#Get_CardTrack|int:60#Get_PIN|int:0,int:0,string:0,string:null,string:__PAN,int:60";
				event.setFsk(fsk);
				event.trigger();

			} catch (Exception e) {
				e.printStackTrace();
			}
		} else {
			try {

				Event event = new Event(null, "swip", null);
				String fsk = "swipeCard|null";
				event.setFsk(fsk);
				event.setActionType(ParseView.ACTION_TYPE_LOCAL);
				event.setAction("ASBalancePwdActivity");
				event.trigger();

			} catch (Exception e) {
				e.printStackTrace();
			}
		}

	}
}
