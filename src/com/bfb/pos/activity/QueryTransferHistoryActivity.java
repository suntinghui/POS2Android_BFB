package com.bfb.pos.activity;

import java.sql.Date;
import java.util.HashMap;

import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;

import com.bfb.pos.activity.view.PasswordWithLabelView;
import com.bfb.pos.activity.view.PickerDateView;
import com.bfb.pos.agent.client.SystemConfig;
import com.bfb.pos.dynamic.core.Event;
import com.bfb.pos.util.DateUtil;
import com.bfb.pos.R;

public class QueryTransferHistoryActivity extends BaseActivity implements OnClickListener {
	
	private PickerDateView pickerDate 			= null;
	private PasswordWithLabelView passwordText 	= null;
	
	private Button backButton = null;
	private Button okButton = null;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		this.setContentView(R.layout.queryhistory);
		
		pickerDate = (PickerDateView) this.findViewById(R.id.pickerDate);
		passwordText = (PasswordWithLabelView) this.findViewById(R.id.passwordText);
		passwordText.setHintWithLabel(this.getResources().getString(R.string.pwd2), this.getResources().getString(R.string.pInputNewPwd));
		
		backButton = (Button) this.findViewById(R.id.backButton);
		backButton.setOnClickListener(this);
		okButton = (Button) this.findViewById(R.id.okButton);
		okButton.setOnClickListener(this);
	}
	
	private boolean checkInput(){
		// 注意这里采用的是java.sql.Date。
		Date startDate = Date.valueOf(pickerDate.getStartDate()); 
		Date endDate = Date.valueOf(pickerDate.getEndDate());
		
		// 检查结束日期是否大于开始日期
		if (startDate.compareTo(endDate) > 0){
			Toast.makeText(this, "开始日期不能大于结束日期", Toast.LENGTH_SHORT).show();
			return false;
		}
		
		// 检查结束日期与开始日期之间的差距不能大于设置的时间间隔
		if (DateUtil.daysBetween(startDate, endDate) > SystemConfig.getHistoryInterval()){
			Toast.makeText(this, "开始日期和结束日期相差不能超过"+ SystemConfig.getHistoryInterval() + "天", Toast.LENGTH_SHORT).show();
			return false;
		}
		
		if (passwordText.getText().trim().length() != 6){
			Toast.makeText(this, this.getResources().getString(R.string.pInputNewPwd), Toast.LENGTH_SHORT).show();
			return false;
		}
		
		return true;
	}

	@Override
	public void onClick(View view) {
		switch(view.getId()){
		case R.id.backButton:
			this.finish();
			break;
			
		case R.id.okButton:
			if (checkInput()){
				try{
					Event event = new Event(null,"transfer", null);
			        event.setFsk("Get_PsamNo|null#Get_VendorTerID|null");
			        event.setTransfer("600000001");
			        HashMap<String, String> map = new HashMap<String, String>();
			        map.put("fieldMerchPWD", passwordText.getEncryptPWD());
			        map.put("BeginDate", pickerDate.getStartDate().replace("-", ""));
			        map.put("EndDate", pickerDate.getEndDate().replace("-", ""));
			        event.setStaticActivityDataMap(map);
			        event.trigger();
			        
				}catch(Exception e){
					e.printStackTrace();
				}
			}
			break;
		}
	}
	
}
