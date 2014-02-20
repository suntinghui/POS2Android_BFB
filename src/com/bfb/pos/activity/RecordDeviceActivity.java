package com.bfb.pos.activity;

import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.InputFilter;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;

import com.bfb.pos.activity.view.TextWithLabelView;
import com.bfb.pos.agent.client.AppDataCenter;
import com.bfb.pos.agent.client.ApplicationEnvironment;
import com.bfb.pos.agent.client.Constant;
import com.bfb.pos.dynamic.core.Event;
import com.bfb.pos.R;

public class RecordDeviceActivity extends BaseActivity implements OnClickListener {
	
	private TextWithLabelView deviceIdText			= null;
	private Button okButton							= null;
	private Button backButton						= null;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		this.setContentView(R.layout.record_device);
		
		deviceIdText = (TextWithLabelView) this.findViewById(R.id.deviceId);
		deviceIdText.setHintWithLabel("设备ID", "请输入设备ID最后7位");
		deviceIdText.getEditText().setFilters(new InputFilter[]{new  InputFilter.LengthFilter(7)});
		deviceIdText.getEditText().setInputType(InputType.TYPE_CLASS_NUMBER);
		
		okButton = (Button) this.findViewById(R.id.okButton);
		okButton.setOnClickListener(this);
		backButton = (Button) this.findViewById(R.id.backButton);
		backButton.setOnClickListener(this);
		
		new GetDeviceIDTask().execute();
		new SetValueTask().execute();
	}
	
	@Override
	public void onClick(View view) {
		switch(view.getId()){
		case R.id.backButton:
			this.finish();
			break;
			
		case R.id.okButton:
			
			SharedPreferences sp = ApplicationEnvironment.getInstance().getPreferences();
			String deviceIDs = sp.getString(Constant.DEVICEID, "");
			String[] deviceArray = deviceIDs.split("\\|");
			for (String str : deviceArray){
				if (!str.equals("") && str.equals(deviceIdText.getText())){
					Toast.makeText(RecordDeviceActivity.this, "您已添加该设备！", Toast.LENGTH_SHORT).show();
					return;
				}
			}
			
			Editor editor = sp.edit();
			deviceIDs = deviceIDs + deviceIdText.getText() + "|";
			Log.d("===", deviceIDs);
			editor.putString(Constant.DEVICEID, deviceIDs);
			if (editor.commit()){
				Toast.makeText(RecordDeviceActivity.this, "成功添加该设备！", Toast.LENGTH_SHORT).show();
			} else {
				Toast.makeText(RecordDeviceActivity.this, "添加该设备失败！", Toast.LENGTH_SHORT).show();
			}
			
			this.finish();
			
			break;
		}
	}
	
	class GetDeviceIDTask extends AsyncTask<Object, Object, Object>{
		@Override
		protected void onPreExecute() {
		}
		
		@Override
		protected void onPostExecute(Object result) {
			try{
				Event event = new Event(null,"getDeviceID", null);
		        String fskStr = "Get_PsamNo|null";
		        event.setFsk(fskStr);
		        event.trigger();
		        
			}catch(Exception e){
				e.printStackTrace();
			}
		}

		@Override
		protected Object doInBackground(Object... arg0) {			
			
			return null;
		}
	}
	
	class SetValueTask extends AsyncTask<Object, Object, Object>{
		@Override
		protected void onPreExecute() {
		}
		
		@Override
		protected void onPostExecute(Object result) {
			deviceIdText.setText(AppDataCenter.getValue("__TERSERIALNO").substring(13));
		}

		@Override
		protected Object doInBackground(Object... arg0) {			
			while(AppDataCenter.getValue("__TERSERIALNO") == null) {
				try {
					Thread.sleep(100);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
				
			return null;
		}
	}
	
}
