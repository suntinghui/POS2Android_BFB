package com.bfb.pos.activity;

import java.util.HashMap;

import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

import com.bfb.pos.agent.client.AppDataCenter;
import com.bfb.pos.util.DateUtil;
import com.bfb.pos.util.StringUtil;
import com.bfb.pos.R;

public class QueryReceiptDetailActivity extends BaseActivity implements OnClickListener {

	private Button backButton = null;
	
	private TextView merchantNo = null;
	private TextView merchantName = null;
	private TextView terminalNo = null;
	private TextView cardNo = null;
	private TextView bankName = null;
	private TextView cardValidDate = null;
	private TextView transferDate = null;
	private TextView transferType = null;
	private TextView transferSerial = null;
	private TextView authNo = null;
	private TextView referNo = null;
	private TextView batchNo = null;
	private TextView amount = null;
	private TextView remark = null;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		this.setContentView(R.layout.queryreceipt_detail);
		
		backButton = (Button) this.findViewById(R.id.backButton);
		backButton.setOnClickListener(this);
		
		merchantNo = (TextView) this.findViewById(R.id.rMerchantNo);
		merchantName = (TextView) this.findViewById(R.id.rMerchantName);
		terminalNo = (TextView) this.findViewById(R.id.rTerminalNo);
		cardNo = (TextView) this.findViewById(R.id.rCardNo);
		bankName = (TextView) this.findViewById(R.id.rBankName);
		cardValidDate = (TextView) this.findViewById(R.id.rCardValidDate);
		transferDate = (TextView) this.findViewById(R.id.rTransferDate);
		transferType = (TextView) this.findViewById(R.id.rTransferType);
		transferSerial = (TextView) this.findViewById(R.id.rTransferSerial);
		authNo = (TextView) this.findViewById(R.id.rAuthNo);
		referNo = (TextView) this.findViewById(R.id.rReferNo);
		batchNo = (TextView) this.findViewById(R.id.rBatchNo);
		amount = (TextView) this.findViewById(R.id.rAmount);
		remark = (TextView) this.findViewById(R.id.rRemark);
		
		@SuppressWarnings("unchecked")
		HashMap<String, String> map = (HashMap<String, String>)this.getIntent().getSerializableExtra("map");
		
		try{
			merchantNo.setText(map.get("field42"));
			merchantName.setText(AppDataCenter.getValue("__MERCHERNAME"));
			terminalNo.setText(map.get("field41"));
			cardNo.setText(StringUtil.formatAccountNo(map.get("field2")));
			bankName.setText(map.get("issuerBank"));
			cardValidDate.setText(map.get("field15"));
			transferDate.setText(DateUtil.formatDateTime(map.get("field13")+map.get("field12")));
			transferType.setText(AppDataCenter.getTransferName(map.get("fieldTrancode")));
			transferSerial.setText(map.get("field11"));
			authNo.setText(map.get("field38"));
			referNo.setText(map.get("field37"));
			batchNo.setText(map.get("field60").substring(2, 8));
			amount.setText(StringUtil.String2SymbolAmount(map.get("field4")));
			remark.setText(""); // TODO : 未设定
			
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	@Override
	public void onClick(View arg0) {
		this.finish();
	}
}
