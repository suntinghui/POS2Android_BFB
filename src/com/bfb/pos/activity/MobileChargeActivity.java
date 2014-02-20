package com.bfb.pos.activity;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;

import org.kxml2.io.KXmlParser;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import android.content.Intent;
import android.os.Bundle;
import android.text.InputFilter;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import com.bfb.pos.activity.view.TextWithLabelView;
import com.bfb.pos.agent.client.AppDataCenter;
import com.bfb.pos.agent.client.Constant;
import com.bfb.pos.agent.client.StaticNetClient;
import com.bfb.pos.agent.client.TransferLogic;
import com.bfb.pos.dynamic.core.Event;
import com.bfb.pos.model.RechargeModel;
import com.bfb.pos.util.AssetsUtil;
import com.bfb.pos.util.StringUtil;
import com.bfb.pos.R;

public class MobileChargeActivity extends BaseActivity implements OnClickListener, OnItemSelectedListener {
	
	private Button backButton = null;
	
	private TextWithLabelView phoneNumberText = null; // 输入的手机号码
	private TextWithLabelView againPhoneNumberText = null; // 再一次输入手机号码
	private Button contactButton = null;
	private Button okButton = null;
	
    private Spinner spinner = null;  
    private ArrayAdapter<RechargeModel> adapter;
    
    private ArrayList<RechargeModel> list = new ArrayList<RechargeModel>();
    
	@Override
	protected void onCreate(Bundle savedInstanceState) {
	   super.onCreate(savedInstanceState);
		
	   setContentView(R.layout.mobile_recharge);
	   
	   this.findViewById(R.id.topInfoView);
	   
	   getPhoneRechargeValue();
	   
	       
	   phoneNumberText = (TextWithLabelView)findViewById(R.id.phoneNumberText);
	   againPhoneNumberText = (TextWithLabelView)findViewById(R.id.againPhoneNumberText);
	   
	   contactButton = (Button)this.findViewById(R.id.contactButton);
	   backButton = (Button)this.findViewById(R.id.backButton);
	   okButton = (Button)this.findViewById(R.id.okButton);
	   
	   contactButton.setOnClickListener(this);
	   backButton.setOnClickListener(this);
	   okButton.setOnClickListener(this);
	   phoneNumberText.getEditText().setFilters( new  InputFilter[]{ new  InputFilter.LengthFilter(11)});
	   againPhoneNumberText.getEditText().setFilters( new  InputFilter[]{ new  InputFilter.LengthFilter(11)});
	   phoneNumberText.setHintWithLabel(this.getResources().getString(R.string.rechargePhoneNo), this.getResources().getString(R.string.pInputRechargePhoneNo));
	   againPhoneNumberText.setHintWithLabel(this.getResources().getString(R.string.rechargePhoneNoAgain), this.getResources().getString(R.string.pInputRechargePhoneNoAgain));
	   
	   phoneNumberText.setText(AppDataCenter.getValue("__PHONENUM"));
	   againPhoneNumberText.setText(AppDataCenter.getValue("__PHONENUM"));
	   
	   spinner = (Spinner) findViewById(R.id.Spinner01);  
	   adapter = new ArrayAdapter<RechargeModel>(this,R.layout.myspinner, list);
	   adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);  
	   spinner.setAdapter(adapter);  
	  
	   spinner.setOnItemSelectedListener(this);  
	}
	
	@Override
	public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
		
	}

	@Override
	public void onNothingSelected(AdapterView<?> arg0) {
		
	} 
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (null != data){
			Bundle bundle = data.getExtras();
		    String str = bundle.getString("phoneNumber");
		    if(null != str){
		    	phoneNumberText.setText(str);
		    	againPhoneNumberText.setText(str);
		    }
		}
		
	}
	
	private boolean checkValue(){
		if ("".equals(phoneNumberText.getText().trim())){
			Toast.makeText(this, "请输入要充值手机号码", Toast.LENGTH_SHORT).show();
			return false;
		} else if ("".equals(againPhoneNumberText.getText().trim())){
			Toast.makeText(this, "请再次输入手机号码", Toast.LENGTH_SHORT).show();
			return false;
		} else if(!phoneNumberText.getText().equals(againPhoneNumberText.getText())){
			Toast.makeText(this, "两次输入的手机号码不相同", Toast.LENGTH_SHORT).show();
			return false;
		}
		
		return true;
	}
	
	@Override
	public void onClick(View arg0) {
		switch(arg0.getId()){
		case R.id.backButton:
			finish();
			break;
		
		case R.id.okButton:
			if (!this.checkValue()){
				return;
			}
			
			// 因为在这里有可能交易后验证服务器响应数据时点付宝发生异常，这时应该检查冲正。
			if (TransferLogic.getInstance().reversalAction()){
				return;
			}
			
			String amountString = StringUtil.amount2String(list.get(spinner.getSelectedItemPosition()).getSellingPrice());
			
			if (Constant.isStatic){
				StaticNetClient.demo_amount = amountString;
			}
			
			try{
				Event event = new Event(null,"MobileReharge", null);
		        String fskStr = "Get_PsamNo|null#Get_VendorTerID|null#Get_EncTrack|int:0,int:0,string:null,int:60#Get_PIN|int:0,int:0,string:"+ amountString + ",string:null,string:__PAN,int:60";
		        event.setFsk(fskStr);
		        event.setTransfer("700000001");
		        HashMap<String, String> tempMap = new HashMap<String, String>();
		        tempMap.put("field4", StringUtil.amount2String(StringUtil.formatAmount(Float.parseFloat(list.get(spinner.getSelectedItemPosition()).getFaceValue()))));
		        tempMap.put("mobile", phoneNumberText.getText());
		        event.setStaticActivityDataMap(tempMap);
		        
		        event.trigger();
		        
			}catch(Exception e){
				e.printStackTrace();
			}
			break;
			
		case R.id.contactButton:
			Intent intent = new Intent(this, ContactsActivity.class);
			startActivityForResult(intent, 0);
			break;
		}
		
	}
	
	public void getPhoneRechargeValue(){
		try{
			InputStream stream = AssetsUtil.getInputStreamFromPhone("phonerecharge.xml");
			KXmlParser parser = new KXmlParser(); 
			parser.setInput(stream,"utf-8");
	        int eventType = parser.getEventType();
	        while(eventType!=XmlPullParser.END_DOCUMENT){  
	            switch(eventType){  
	            case XmlPullParser.START_TAG:
	                if("item".equalsIgnoreCase(parser.getName())){
	                	String key = parser.getAttributeValue(null, "key");
	                	String value = parser.getAttributeValue(null, "value");
	                	RechargeModel model = new RechargeModel(key, value);
	                	list.add(model);
	                }  
	                 
	                break;
	            }  
	            eventType = parser.next();//进入下一个元素并触发相应事件  
	        }
	        
		}catch(IOException e){
			e.printStackTrace();
		} catch (XmlPullParserException e) {
			e.printStackTrace();
		}
		
	}

}
