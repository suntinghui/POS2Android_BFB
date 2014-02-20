package com.bfb.pos.activity;

import java.util.HashMap;

import android.os.Bundle;
import android.text.InputFilter;
import android.text.InputType;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.Toast;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.TextView;

import com.bfb.pos.activity.view.TextWithLabelView;
import com.bfb.pos.agent.client.AppDataCenter;
import com.bfb.pos.agent.client.TransferLogic;
import com.bfb.pos.agent.client.db.PayeeAccountDBHelper;
import com.bfb.pos.model.PayeeAccountModel;
import com.bfb.pos.util.DateUtil;
import com.bfb.pos.util.StringUtil;
import com.bfb.pos.R;

public class TransferSuccessWithSendSmsActivity extends BaseActivity implements OnClickListener, OnCheckedChangeListener  {
	
	private CheckBox checkBox = null;
	private TextWithLabelView phoneNoText = null;
	
	private Button okButton = null;
	
	HashMap<String, String> receMap = null;
	
	@SuppressWarnings("unchecked")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		this.setContentView(R.layout.transfersuccess_sendsms);
		
		checkBox = (CheckBox) this.findViewById(R.id.checkBox);
		checkBox.setOnCheckedChangeListener(this);
		
		phoneNoText = (TextWithLabelView) this.findViewById(R.id.phoneNoText);
		phoneNoText.setHintWithLabel(this.getResources().getString(R.string.phoneNo2), this.getResources().getString(R.string.pInputPhoneNoIn));
		phoneNoText.getEditText().setInputType(InputType.TYPE_CLASS_NUMBER);
		phoneNoText.getEditText().setFilters(new InputFilter[]{ new InputFilter.LengthFilter(11)});
		
		okButton = (Button) this.findViewById(R.id.okButton);
		okButton.setOnClickListener(this);
		
		receMap = (HashMap<String, String>) this.getIntent().getSerializableExtra("map");
		
		try{
			String amount = receMap.get("field4");
			String accountInNo = receMap.get("field103");
			
			((TextView)this.findViewById(R.id.btAmount)).setText(StringUtil.String2SymbolAmount(amount));
			((TextView)this.findViewById(R.id.btFee)).setText("0.00 元");// TODO : 未设定
			((TextView)this.findViewById(R.id.btState)).setText(receMap.get("fieldMessage"));
			((TextView)this.findViewById(R.id.btTranIn)).setText(StringUtil.formatAccountNo(accountInNo));
			((TextView)this.findViewById(R.id.btTransferSerial)).setText(receMap.get("field11"));
			((TextView)this.findViewById(R.id.btTransOut)).setText(StringUtil.formatAccountNo(receMap.get("field102")));
			((TextView)this.findViewById(R.id.btTransType)).setText(AppDataCenter.getTransferName(receMap.get("fieldTrancode")));
			((TextView)this.findViewById(R.id.btTransTime)).setText(DateUtil.formatDateTime(receMap.get("field13")+receMap.get("field12")));
			
			if (!"".equals(accountInNo)){
				// 为提升用户体验，根据收款账号，在数据库中检索中收款人的手机号。
				PayeeAccountDBHelper helper = new PayeeAccountDBHelper();
				PayeeAccountModel model = helper.queryAPayeeWithAccountNo(accountInNo);
				phoneNoText.setText(model.getPhoneNo());
			}
			
		} catch(Exception e){
			e.printStackTrace();
		}
	}
	
	@Override
	public void onClick(View view) {
		switch (view.getId()){
		case R.id.okButton:
			if (checkBox.isChecked()){ // 发送短信
				String phoneNo = phoneNoText.getText();
				if ("".equals(phoneNo)){
					Toast.makeText(this, this.getResources().getString(R.string.phoneNoNull), Toast.LENGTH_SHORT).show();
					return;
				}
				if (!phoneNo.matches("^(1(([35][0-9])|(47)|[8][01236789]))\\d{8}$")){
					Toast.makeText(this, this.getResources().getString(R.string.phoneNoIllegal), Toast.LENGTH_SHORT).show();
					return;
				}
				
				// 发送手机号至服务器
				receMap.put("receivePhoneNo", phoneNo);
				TransferLogic.getInstance().uploadReceiptAction(receMap);
				
			} else {
				this.setResult(RESULT_OK);
				this.finish();
			}
			
			break;
		}
	}
	
	@Override
	public void onCheckedChanged(CompoundButton button, boolean flag) {
		switch(button.getId()){
		case R.id.checkBox:
			phoneNoText.setVisibility(flag ? View.VISIBLE : View.GONE);
//			okButton.setText(flag ? this.getResources().getString(R.string.sendSMS) : this.getResources().getString(R.string.done));
			break;
		}
		
	}

}
