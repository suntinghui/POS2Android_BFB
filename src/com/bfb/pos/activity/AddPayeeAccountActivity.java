package com.bfb.pos.activity;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

import org.kxml2.io.KXmlParser;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import android.os.AsyncTask;
import android.os.Bundle;
import android.text.InputFilter;
import android.text.InputType;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import com.bfb.pos.activity.view.TextWithLabelView;
import com.bfb.pos.agent.client.db.PayeeAccountDBHelper;
import com.bfb.pos.model.BankModel;
import com.bfb.pos.model.PayeeAccountModel;
import com.bfb.pos.util.AssetsUtil;
import com.bfb.pos.R;

public class AddPayeeAccountActivity extends BaseActivity implements OnClickListener {
	
	private Button backButton = null;
	
	private TextWithLabelView accountNoText = null;
	private TextWithLabelView accountNoAgainText = null;
	private TextWithLabelView nameText = null;
	private TextWithLabelView phoneNoText = null;
	private Spinner bankSpinner = null;
	private Button okButton = null;
	
	private ArrayList<BankModel> bankList = new ArrayList<BankModel>();
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		this.setContentView(R.layout.add_payeeaccount);
		
		this.findViewById(R.id.topInfoView);
		
		backButton = (Button) this.findViewById(R.id.backButton);
		backButton.setOnClickListener(this);
		okButton = (Button) this.findViewById(R.id.okButton);
		okButton.setOnClickListener(this);
		
		accountNoText = (TextWithLabelView) this.findViewById(R.id.accountNoText);
		accountNoText.setHintWithLabel(this.getResources().getString(R.string.accountInNo), this.getResources().getString(R.string.pInputAccountInNo));
		accountNoText.getEditText().setInputType(InputType.TYPE_CLASS_NUMBER);
		accountNoText.getEditText().setFilters(new InputFilter[]{ new InputFilter.LengthFilter(19)});
		
		accountNoAgainText = (TextWithLabelView) this.findViewById(R.id.accountNoAgainText); 
		accountNoAgainText.setHintWithLabel(this.getResources().getString(R.string.inputAgain), this.getResources().getString(R.string.pInputAccountInNoAgain));
		accountNoAgainText.getEditText().setInputType(InputType.TYPE_CLASS_NUMBER);
		accountNoAgainText.getEditText().setFilters(new InputFilter[]{ new InputFilter.LengthFilter(19)});
		
		nameText = (TextWithLabelView) this.findViewById(R.id.nameText);
		nameText.setHintWithLabel(this.getResources().getString(R.string.accountInName), this.getResources().getString(R.string.pInputAccountInName));
		
		phoneNoText = (TextWithLabelView) this.findViewById(R.id.phoneNoText);
		phoneNoText.setHintWithLabel(this.getResources().getString(R.string.phoneNo), this.getResources().getString(R.string.pInputPhoneNoIn));
		phoneNoText.getEditText().setInputType(InputType.TYPE_CLASS_NUMBER);
		phoneNoText.getEditText().setFilters(new InputFilter[]{ new InputFilter.LengthFilter(11)});
		
		bankSpinner = (Spinner) this.findViewById(R.id.bankSpinner);
		
		new GetBankTask().execute();
	}
	
	@Override
	public void onClick(View view) {
		switch(view.getId()){
		case R.id.backButton:
			this.finish();
			break;
			
		case R.id.okButton:
			// 检查输入项
			if (!checkInput())
				return;
			
			BankModel bank = ((BankModel)bankSpinner.getSelectedItem());
			PayeeAccountModel model = new PayeeAccountModel(accountNoText.getText(), nameText.getText(),phoneNoText.getText(), bank.getName(), bank.getCode());
			// 检索联系人是否已经存在
			PayeeAccountDBHelper helper1 = new PayeeAccountDBHelper();
			if (null != helper1.queryAPayeeWithAccountNo(accountNoText.getText())){
				Toast.makeText(this, this.getResources().getString(R.string.existAccount), Toast.LENGTH_SHORT).show();
			} else {
				// 添加联系人
				PayeeAccountDBHelper helper2 = new PayeeAccountDBHelper();
				boolean flag = helper2.insertPayeeAccount(model);
				if (flag){
					Toast.makeText(this, this.getResources().getString(R.string.addAccountSuccess), Toast.LENGTH_SHORT).show();
					
					this.setResult(RESULT_OK);
				} else {
					Toast.makeText(this, this.getResources().getString(R.string.addAccountFailure), Toast.LENGTH_SHORT).show();
					
					this.setResult(RESULT_CANCELED);
				}
				this.finish();
			}
			
			break;
		}
	}
	
	private boolean checkInput(){
		if ("".equals(accountNoText.getText().trim())){
			Toast.makeText(this, this.getResources().getString(R.string.accountInNoNull), Toast.LENGTH_SHORT).show();
			return false;
		} 
		if (!accountNoText.getText().matches("^\\d{16,}$")){
			Toast.makeText(this, this.getResources().getString(R.string.accountNoIllegal), Toast.LENGTH_SHORT).show();
			return false;
		}
		if ("".equals(accountNoAgainText.getText().trim())){
			Toast.makeText(this, this.getResources().getString(R.string.accountInNoAgainNull), Toast.LENGTH_SHORT).show();
			return false;
		}
		if (!accountNoAgainText.getText().matches("^\\d{16,}$")){
			Toast.makeText(this, this.getResources().getString(R.string.accountNoIllegal), Toast.LENGTH_SHORT).show();
			return false;
		}
		if (!accountNoText.getText().trim().equals(accountNoAgainText.getText().trim())){
			Toast.makeText(this, this.getResources().getString(R.string.accountNoNotEqual), Toast.LENGTH_SHORT).show();
			return false;
		}
		
		if ("".equals(nameText.getText().trim())){
			Toast.makeText(this, this.getResources().getString(R.string.nameNull), Toast.LENGTH_SHORT).show();
			return false;
		}
		
		if (!"".equals(phoneNoText.getText().trim()) && !phoneNoText.getText().matches("^(1(([35][0-9])|(47)|[8][01236789]))\\d{8}$")){
			Toast.makeText(this, this.getResources().getString(R.string.phoneNoIllegal), Toast.LENGTH_SHORT).show();
			return false;
		}
		
		/***
		if (!nameText.getText().matches("^[\u4e00-\u9fa5]*$")){
			Toast.makeText(this, this.getResources().getString(R.string.nameIllegal), Toast.LENGTH_SHORT).show();
			return false;
		}
		****/
		
		return true;
	}
	
	private void getBankList(){
		try{
			InputStream stream = AssetsUtil.getInputStreamFromPhone("bank.xml");
			KXmlParser parser = new KXmlParser(); 
			parser.setInput(stream,"utf-8");
	        int eventType = parser.getEventType();
	        while(eventType!=XmlPullParser.END_DOCUMENT){  
	            switch(eventType){  
	            case XmlPullParser.START_TAG:
	                if("bank".equalsIgnoreCase(parser.getName())){
	                	BankModel model = new BankModel(parser.getAttributeValue(null, "name"), parser.getAttributeValue(null, "code"));
	                    bankList.add(model);
	                }  
	                 
	                break;
	            }  
	            eventType = parser.next();  
	        }
	        
		}catch(IOException e){
			e.printStackTrace();
			
		} catch (XmlPullParserException e) {
			e.printStackTrace();
			
		} 
	}
	
	class GetBankTask extends AsyncTask<Object, Object, Object>{

		@Override
		protected void onPostExecute(Object result) {
			@SuppressWarnings({ "rawtypes", "unchecked" })
			ArrayAdapter<String> payeeAccountAdapter = new ArrayAdapter(AddPayeeAccountActivity.this, R.layout.myspinner, bankList);
			payeeAccountAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
			bankSpinner.setAdapter(payeeAccountAdapter);
		}

		@Override
		protected Object doInBackground(Object... arg0) {
			getBankList();
			return null;
		}
		
	}

}
