package com.bfb.pos.activity;

import java.util.ArrayList;
import java.util.HashMap;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.bfb.pos.activity.view.InputAmountWithUpperView;
import com.bfb.pos.activity.view.TextWithLabelView;
import com.bfb.pos.agent.client.Constant;
import com.bfb.pos.agent.client.StaticNetClient;
import com.bfb.pos.agent.client.db.PayeeAccountDBHelper;
import com.bfb.pos.dynamic.core.Event;
import com.bfb.pos.model.PayeeAccountModel;
import com.bfb.pos.util.FormatCurrency;
import com.bfb.pos.util.StringUtil;
import com.bfb.pos.R;

// 银行转账 -- 从服务器获取收款人列表 输入金额
public class BankTransferInputActivity extends BaseActivity implements OnClickListener, OnItemSelectedListener {
	
	private Button backButton 							= null;
	
	private Spinner accountSpinner						= null;
	private TextView nameView							= null;
	private TextView bankView							= null;
	private InputAmountWithUpperView amountText     	= null;
	private TextWithLabelView remarkText				= null;
	private Button okButton								= null;
	
	private PayeeAccountModel selectedModel				= null;
	
	private ArrayList<PayeeAccountModel> accountList	= new ArrayList<PayeeAccountModel>();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature( Window.FEATURE_NO_TITLE );
		
		this.setContentView(R.layout.banktransfer_input);
		
		this.findViewById(R.id.topInfoView);
		
		backButton = (Button) this.findViewById(R.id.backButton);
		
		accountSpinner = (Spinner) this.findViewById(R.id.accountNoSpinner);
		nameView = (TextView) this.findViewById(R.id.nameView);
		bankView = (TextView) this.findViewById(R.id.bankView);
		amountText = (InputAmountWithUpperView) this.findViewById(R.id.amount);
		remarkText = (TextWithLabelView) this.findViewById(R.id.remarkText);
		okButton = (Button) this.findViewById(R.id.okButton);
		
		remarkText.setHintWithLabel(this.getResources().getString(R.string.remark), this.getResources().getString(R.string.pInputRemark));
		
		backButton.setOnClickListener(this);
		okButton.setOnClickListener(this);
		accountSpinner.setOnItemSelectedListener(this);
		
		// 启动任务从数据库中加载数据
		new QueryAccontTask().execute();
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == 1){
			// 从添加联系人页面返回，启动任务从数据库中加载数据
			new QueryAccontTask().execute();
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
			
		case R.id.okButton:
			try{
				String amount = FormatCurrency.formatAmount(amountText.getAmount()).replace(",", ""); 
				if ("".equals(amount) || "0.00".equals(amount)){
					Toast.makeText(this, this.getResources().getString(R.string.pInputAmount), Toast.LENGTH_SHORT).show();
					break;
				}
				
				String amountStr = StringUtil.amount2String(amount);
				
				if (Constant.isStatic){
					StaticNetClient.demo_amount = amountStr;
				}
				
				Event event = new Event(null,"transfer", null);
		        String fskStr = "Get_PsamNo|null#Get_VendorTerID|null#Get_EncTrack|int:0,int:0,string:null,int:60#Get_PIN|int:0,int:0,string:"+ amountStr +",string:null,string:__PAN,int:60";
		        event.setFsk(fskStr);
		        event.setTransfer("200001111");
		        HashMap<String, String> map = new HashMap<String, String>();
		        map.put("field4", amountStr);
		        map.put("field103", selectedModel.getAccountNo()); 
		        map.put("fieldPayee", selectedModel.getName()); 
		        map.put("fieldIssuerCode", selectedModel.getBankCode()); 
		        map.put("filedRemark", remarkText.getText());
		        event.setStaticActivityDataMap(map);
		        event.trigger();
			}catch(Exception e){
				e.printStackTrace();
			}
			break;
		}
	}
	
	@Override
	public void onItemSelected(AdapterView<?> arg0, View arg1, int positon, long arg3) {
		if (!accountList.isEmpty()){
			// 最后一项为添加联系人
			if (positon == accountList.size()-1){
				startActivityForResult(new Intent(BankTransferInputActivity.this, AddPayeeAccountActivity.class), 1);
				
			} else {
				selectedModel = accountList.get(positon);
				// 姓名&开户行
				nameView.setText(this.getResources().getString(R.string.name) + selectedModel.getName());
				bankView.setText(this.getResources().getString(R.string.bank) + selectedModel.getBank());
			}
		}
	}

	@Override
	public void onNothingSelected(AdapterView<?> arg0) {
	}
	
	
	class QueryAccontTask extends AsyncTask<Object, Object, Object>{
		@Override
		protected void onPostExecute(Object result) {
			if (!accountList.isEmpty()){
				// 为了更好的用户体验，在下拉列表中添加一项“添加联系人”选择。不过前提是，用户最起码已经有一项选择。。。
				PayeeAccountModel tempModel = new PayeeAccountModel("添加联系人","","","","");
				accountList.add(tempModel);
				
				ArrayAdapter<PayeeAccountModel> payeeAccountAdapter = new ArrayAdapter<PayeeAccountModel>(BankTransferInputActivity.this, R.layout.myspinner, accountList);
				payeeAccountAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
				accountSpinner.setAdapter(payeeAccountAdapter);
				
			} else {
				AlertDialog.Builder dialog = new Builder(BankTransferInputActivity.this);
				dialog.setTitle("提示");
				dialog.setMessage(R.string.noContact);
				dialog.setCancelable(false);
				dialog.setNegativeButton("确定", new android.content.DialogInterface.OnClickListener(){
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
						startActivityForResult(new Intent(BankTransferInputActivity.this, AddPayeeAccountActivity.class), 1);
					}
				});
				dialog.setPositiveButton("取消", new android.content.DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
						finish();
					}
				});
				dialog.show();
			}
			
			BankTransferInputActivity.this.hideDialog(BaseActivity.PROGRESS_DIALOG);
		}

		@Override
		protected Object doInBackground(Object... arg0) {
			PayeeAccountDBHelper helper = new PayeeAccountDBHelper();
			accountList = helper.queryAll();
			
			return null;
		}

		@Override
		protected void onPreExecute() {
			BankTransferInputActivity.this.showDialog(BaseActivity.PROGRESS_DIALOG, BankTransferInputActivity.this.getResources().getString(R.string.queryingAccountIn));
		}
		
	}

}
