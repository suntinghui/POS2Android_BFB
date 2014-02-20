package com.bfb.pos.activity;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bfb.pos.agent.client.AppDataCenter;
import com.bfb.pos.agent.client.Constant;
import com.bfb.pos.agent.client.TransferLogic;
import com.bfb.pos.util.DateUtil;
import com.bfb.pos.util.PhoneUtil;
import com.bfb.pos.util.StringUtil;
import com.bfb.pos.R;
import com.dhcc.pos.packets.cnType;
import com.dhcc.pos.packets.cnValue;

public class ReceiptActivity extends BaseActivity implements OnClickListener {
	
	private Button backButton = null;
	private Button signButton = null;
	private Button okButton = null;
	private LinearLayout signLayout = null;
	private ImageView md5Image = null;
	private TextView tipsView = null;
	
	private TextView merchantNoView = null;
	private TextView merchantNameView = null;
	private TextView terminalNoView = null;
	private TextView cardNoView = null;
	private TextView bankNameView = null;
	private TextView cardValidDateView = null;
	private TextView transferDateView = null;
	private TextView transferTypeView = null;
	private TextView transferSerialView = null;
	private TextView authNoView = null;
	private TextView referNoView = null;
	private TextView batchNoView = null;
	private TextView amountView = null;
	private TextView remarkView = null;
	
	private HashMap<String, String> fieldsMap = null;
	
	private String signImageName = "";
	private String receivePhoneNo = "";
	private String imei = "";
	
	@SuppressWarnings("unchecked")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		this.setContentView(R.layout.receipt);
		
		backButton = (Button) this.findViewById(R.id.backButton);
		backButton.setOnClickListener(this);
		signButton = (Button) this.findViewById(R.id.signButton);
		signButton.setOnClickListener(this);
		okButton = (Button) this.findViewById(R.id.okButton);
		okButton.setOnClickListener(this);
		md5Image = (ImageView) this.findViewById(R.id.md5Image);
		signLayout = (LinearLayout) this.findViewById(R.id.signLayout);
		tipsView = (TextView) this.findViewById(R.id.tipsText);
		
		merchantNoView = (TextView) this.findViewById(R.id.rMerchantNo);
		merchantNameView = (TextView) this.findViewById(R.id.rMerchantName);
		terminalNoView = (TextView) this.findViewById(R.id.rTerminalNo);
		cardNoView = (TextView) this.findViewById(R.id.rCardNo);
		bankNameView = (TextView) this.findViewById(R.id.rBankName);
		cardValidDateView = (TextView) this.findViewById(R.id.rCardValidDate);
		transferDateView = (TextView) this.findViewById(R.id.rTransferDate);
		transferTypeView = (TextView) this.findViewById(R.id.rTransferType);
		transferSerialView = (TextView) this.findViewById(R.id.rTransferSerial);
		authNoView = (TextView) this.findViewById(R.id.rAuthNo);
		referNoView = (TextView) this.findViewById(R.id.rReferNo);
		batchNoView = (TextView) this.findViewById(R.id.rBatchNo);
		amountView = (TextView) this.findViewById(R.id.rAmount);
		remarkView = (TextView) this.findViewById(R.id.rRemark);
		
		try{
			fieldsMap = (HashMap<String, String>)this.getIntent().getSerializableExtra("map");
			merchantNoView.setText(fieldsMap.get("field42"));
			merchantNameView.setText(AppDataCenter.getValue("__MERCHERNAME"));
			terminalNoView.setText(fieldsMap.get("field41"));
			cardNoView.setText(StringUtil.formatAccountNo(fieldsMap.get("field2")));
			bankNameView.setText(fieldsMap.get("issuerBank"));
			cardValidDateView.setText(fieldsMap.get("field14"));
			transferDateView.setText(DateUtil.formatDateTime(fieldsMap.get("field13")+fieldsMap.get("field12")));
			transferTypeView.setText(AppDataCenter.getTransferName(fieldsMap.get("fieldTrancode")));
			transferSerialView.setText(fieldsMap.get("field11"));
			authNoView.setText(fieldsMap.get("field38"));
			referNoView.setText(fieldsMap.get("field37"));
			batchNoView.setText(fieldsMap.get("field60").substring(2, 8));
			amountView.setText(StringUtil.String2SymbolAmount(fieldsMap.get("field4")));
			remarkView.setText(fieldsMap.get("remark"));
			
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == 1){
			switch(resultCode){
			case RESULT_OK: // 表示用户已正常完成签名
				signLayout.setVisibility(View.VISIBLE);
				signButton.setVisibility(View.GONE);
				tipsView.setVisibility(View.GONE);
				okButton.setVisibility(View.VISIBLE);
				
				receivePhoneNo = data.getStringExtra("phoneNum");
				
				// 加载签名图片
				File file = new File(Constant.SIGNIMAGESPATH + signImageName+".JPEG");
				if (file.exists()){
					Bitmap bitmap = BitmapFactory.decodeFile(file.getPath());
					md5Image.setImageBitmap(bitmap);
				}
				
				break;
				
			case RESULT_CANCELED: // 表示用户取消签名
				signLayout.setVisibility(View.GONE);
				signButton.setVisibility(View.VISIBLE);
				tipsView.setVisibility(View.VISIBLE);
				okButton.setVisibility(View.GONE);
				break;
			}
		} else {
			super.onActivityResult(requestCode, resultCode, data);
		}
		
	}

	@Override
	public void onClick(View view) {
		switch(view.getId()){
		case R.id.backButton:
			this.finish();
			break;
		
		case R.id.signButton: // 去签名
			this.gotoHandSignActivity();
			break;
			
		case R.id.okButton: // 完成签名开始交易
			
			fieldsMap.put("receivePhoneNo", receivePhoneNo);
			fieldsMap.put("signImageName", signImageName);
			fieldsMap.put("imei", imei);
			TransferLogic.getInstance().uploadReceiptAction(fieldsMap);
			
			
//			while (!(this.getTopActivity() instanceof LRCatalogActivity)){
//				this.getTopActivity().finish();
//			}
			
			break;
		}
		
	}
	
	private void gotoHandSignActivity(){
		Intent intent = new Intent(this, HandSignActivity.class);
		intent.putExtra("amount", StringUtil.String2SymbolAmount(fieldsMap.get("field4")));
		intent.putExtra("tracenum", fieldsMap.get("field11"));
		// 先将图片的名称暂定为日期13+流水号11
		signImageName = fieldsMap.get("field13")+fieldsMap.get("field11");
		intent.putExtra("signImageName", signImageName);
		intent.putExtra("MD5", getMD5Value());
		this.startActivityForResult(intent, 1);
	}
	
	private String getMD5Value(){
    	String indexNo = fieldsMap.get("field37");// 银联返回的检索参考号（12位）
    	
    	// 时间戳（14位）
    	SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss"); 
    	String timeStamp = sdf.format(new Date());
    	
    	// 手机IMEI（15位）
    	imei = PhoneUtil.getIMEI();
    	
    	StringBuffer temp = new StringBuffer();
    	temp.append(indexNo).append(timeStamp).append(imei);
    	return StringUtil.MD5Crypto(temp.toString());
    }
    
}
