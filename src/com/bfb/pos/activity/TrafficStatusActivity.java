package com.bfb.pos.activity;

import java.text.DecimalFormat;
import java.util.HashMap;

import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

import com.bfb.pos.agent.client.Constant;
import com.bfb.pos.util.TrafficUtil;
import com.bfb.pos.R;

public class TrafficStatusActivity extends BaseActivity {
	
	private static final long MB = 1024 * 1024 ;
	
	private Button backButton;
	
	private TextView dayMobileView = null;
	private TextView dayWifiView = null;
	private TextView monthMobileView = null;
	private TextView monthWifiView = null;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature( Window.FEATURE_NO_TITLE );
		
	    setContentView(R.layout.traffic_status);
	    
		dayMobileView = ((TextView)this.findViewById(R.id.dayMobile));
		dayWifiView = ((TextView)this.findViewById(R.id.dayWifi));
		monthMobileView = ((TextView)this.findViewById(R.id.monthMobile));
		monthWifiView = ((TextView)this.findViewById(R.id.monthWifi));
		
		HashMap<String, Long> map = TrafficUtil.getInstance().getTraffic();
		
		dayMobileView.setText(getTrafficFormat(map.get(Constant.DAY_MOBILESEND)+map.get(Constant.DAY_MOBILESRECEIVE)));
		dayWifiView.setText(getTrafficFormat(map.get(Constant.DAY_WIFISEND)+map.get(Constant.DAY_WIFIRECEIVE)));
		
		monthMobileView.setText(getTrafficFormat(map.get(Constant.MONTH_MOBILESEND)+map.get(Constant.MONTH_MOBILESRECEIVE)));
		monthWifiView.setText(getTrafficFormat(map.get(Constant.MONTH_WIFISEND)+map.get(Constant.MONTH_WIFIRECEIVE)));
		
		
		this.findViewById(R.id.topInfoView);
		backButton = (Button)this.findViewById(R.id.backButton);
		backButton.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				TrafficStatusActivity.this.finish();
			}
		});

	}
	
	private String getTrafficFormat(long l){
		DecimalFormat format = new DecimalFormat("0.00");
		if (l < MB){
			return format.format(l/1024) + " KB";
		} else {
			return format.format(l/MB) + " MB";
		}
	}
}
