package com.bfb.pos.activity;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.bfb.pos.agent.client.ApplicationEnvironment;
import com.bfb.pos.agent.client.Constant;
import com.bfb.pos.dynamic.component.ViewException;
import com.bfb.pos.dynamic.core.Event;
import com.bfb.pos.model.CityModel;
import com.bfb.pos.model.UserBankModel;
import com.bfb.pos.util.Bank;
import com.bfb.pos.util.BankParse;
import com.bfb.pos.util.Province;
import com.bfb.pos.util.ProvinceParse;
import com.bfb.pos.R;

//帐户提现  修改帐户记录
public class DrawMoneyModifyAccountActivity extends BaseActivity implements OnClickListener {

	private final int SING_CHOICE_DIALOG = 1;
	private ArrayList<String> bankNameList = new ArrayList<String>();
	private ArrayList<String> bankCodeList = new ArrayList<String>();
	private String selectBankName;
	private String selectBankCode;

	private ProvinceParse parse;
	private BankParse parse_bank;

	private Spinner spinner0, spinner1, spinner2;

	private Province currentProvince;

	private CityModel currentCity;
	private Bank currentBank;
	
	private EditText et_account = null;
	private EditText et_account_confirm = null;
	private Button btn_bank_branch;
	
	private TextView tv_mastername = null;
	private UserBankModel userBankModel = null;

	private String bankbranchid = null;
	private String bankbranchname = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		requestWindowFeature(Window.FEATURE_NO_TITLE);
		this.setContentView(R.layout.activity_drawmoney_add_account);

		Button btn_back = (Button) this.findViewById(R.id.btn_back);
		btn_back.setOnClickListener(this);

		TextView tv_title = (TextView)this.findViewById(R.id.titleTV);
		tv_title.setText("修改帐户记录");
		Button confirmButton = (Button) this.findViewById(R.id.btn_confirm);
		confirmButton.setOnClickListener(this);

		btn_bank_branch = (Button) this.findViewById(R.id.btn_bank_branch);
		btn_bank_branch.setOnClickListener(this);
		
		Intent intent = this.getIntent();
		userBankModel = (UserBankModel) intent.getSerializableExtra("userBankModel");
		
		tv_mastername = (TextView)this.findViewById(R.id.tv_mastername);
		tv_mastername.setText(userBankModel.getMastername());
		
		et_account = (EditText)this.findViewById(R.id.et_account);
		et_account.setText(userBankModel.getBankaccount());
		et_account_confirm = (EditText)this.findViewById(R.id.et_account_confirm);
		et_account_confirm.setText(userBankModel.getBankaccount());

		parse = ProvinceParse.build(this, R.raw.province, R.raw.cities);
		parse_bank = BankParse.build(this, R.raw.banks);
		spinner0 = (Spinner) findViewById(R.id.spinner0);
		spinner1 = (Spinner) findViewById(R.id.spinner1);
		spinner2 = (Spinner) findViewById(R.id.spinner2);

		int index_province = 0;
		int index_bank = 0;
		List<Province> list_province = parse.getProvinces();
		for (int i = 0; i < list_province.size(); i++) {
			if(list_province.get(i).getCode()  == 310000){
				index_province = i;
			}
		}
		List<Bank> list_banks = parse_bank.getBanks();
		for (int i = 0; i < list_banks.size(); i++) {
			if(list_banks.get(i).getCode()  == 104){
				index_bank = i;
			}
		}
		ArrayAdapter<Bank> bankAdapter = new ArrayAdapter<Bank>(this, R.layout.simple_spinner_item, android.R.id.text1, list_banks);
		ArrayAdapter<Province> provinceAdapter = new ArrayAdapter<Province>(this, R.layout.simple_spinner_item, android.R.id.text1, list_province);
		spinner0.setAdapter(bankAdapter);
		spinner0.setSelection(index_bank);
		spinner1.setAdapter(provinceAdapter);
		spinner1.setSelection(index_province);

		spinner0.setOnItemSelectedListener(new BankAdapter());
		spinner1.setOnItemSelectedListener(new ProvinceAdapter());
		spinner2.setOnItemSelectedListener(new CityAdapter());
	}

	@Override
	public void onClick(View v) {
		switch(v.getId()){
		case R.id.btn_back:
			this.finish();
			break;
		case R.id.btn_bank_branch:
			Intent intent = new Intent(DrawMoneyModifyAccountActivity.this, BankSearchActivity.class);
			intent.putExtra("bankCode", currentBank.getCode());
			intent.putExtra("provinceCode", currentCity.getProvince_code());
			intent.putExtra("cityCode", currentCity.getCode());
			DrawMoneyModifyAccountActivity.this.startActivityForResult(intent,1);
			break;
		case R.id.btn_confirm:
			if(checkValue()){
				actionModifyBank();
			}
			break;
		default:
			break;
		}
	}

	private Boolean checkValue(){
		String tmp_account = et_account.getText().toString();
		String tmp_account_confirm = et_account_confirm.getText().toString();
		if(et_account.getText().length() == 0){
			this.showToast("收款帐号不能为空！");
			return false;
		}
		if(et_account_confirm.getText().length() == 0){
			this.showToast("确认帐号不能为空！");
			return false;
		}
		if(bankbranchname == null){
			this.showToast("支行信息不能为空！");
			return false;
		}
		if(!tmp_account.equals(tmp_account_confirm) ){
			this.showToast("输入帐号不正确，请重新输入！");
			et_account_confirm.setText("");
			return false;
		}
		
		return true;
	}
	@Override
	protected Dialog onCreateDialog(int id) {
		Dialog dialog = null;
		switch (id) {
		case SING_CHOICE_DIALOG:
			Builder builder = new AlertDialog.Builder(this);
			// builder.setIcon(R.drawable.basketball);
			// builder.setTitle(R.string.brach);
			final ChoiceOnClickListener choiceListener = new ChoiceOnClickListener();
			builder.setSingleChoiceItems((String[]) DrawMoneyModifyAccountActivity.this.bankNameList.toArray(new String[0]), 0, choiceListener);

			DialogInterface.OnClickListener btnListener = new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialogInterface, int which) {

					int choiceWhich = choiceListener.getWhich();
				}
			};
			dialog = builder.create();
			break;
		}
		return dialog;
	}

	private class ChoiceOnClickListener implements DialogInterface.OnClickListener {

		private int which = 0;

		@Override
		public void onClick(DialogInterface dialogInterface, int which) {
			this.which = which;
			selectBankName = bankNameList.get(which);
			selectBankCode = bankCodeList.get(which);
			Bundle budle = new Bundle();
			budle.putString("bankValue", selectBankName);
			budle.putString("bankCode", selectBankCode);
			DrawMoneyModifyAccountActivity.this.setResult(RESULT_CANCELED, DrawMoneyModifyAccountActivity.this.getIntent().putExtras(budle));
			DrawMoneyModifyAccountActivity.this.finish();

		}

		public int getWhich() {
			return which;
		}
	}

	public void onBankChange(int position) {
		currentBank = parse_bank.getBanks().get(position);
	}

	class BankAdapter implements OnItemSelectedListener {

		/**
		 * (non-Javadoc)
		 * 
		 * @see android.widget.AdapterView.OnItemSelectedListener#onItemSelected(android.widget.AdapterView,
		 *      android.view.View, int, long)
		 */
		@Override
		public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
			onBankChange(position);
		}

		/**
		 * (non-Javadoc)
		 * 
		 * @see android.widget.AdapterView.OnItemSelectedListener#onNothingSelected(android.widget.AdapterView)
		 */
		@Override
		public void onNothingSelected(AdapterView<?> parent) {

		}

	}

	public void onProvinChange(int position) {
		currentProvince = parse.getProvinces().get(position);
		ArrayAdapter<CityModel> cityAdapter = new ArrayAdapter<CityModel>(this, R.layout.simple_spinner_item, android.R.id.text1, currentProvince.getCities());
		spinner2.setAdapter(cityAdapter);
	}

	class ProvinceAdapter implements OnItemSelectedListener {

		/**
		 * (non-Javadoc)
		 * 
		 * @see android.widget.AdapterView.OnItemSelectedListener#onItemSelected(android.widget.AdapterView,
		 *      android.view.View, int, long)
		 */
		@Override
		public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
			onProvinChange(position);
		}

		/**
		 * (non-Javadoc)
		 * 
		 * @see android.widget.AdapterView.OnItemSelectedListener#onNothingSelected(android.widget.AdapterView)
		 */
		@Override
		public void onNothingSelected(AdapterView<?> parent) {

		}

	}

	final class CityAdapter extends ProvinceAdapter {

		@Override
		public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
			currentCity = currentProvince.getCities().get(position);
		}
	}

	protected void dialog(String message) {

		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setMessage(message).setCancelable(false).setPositiveButton(R.string.confirm2, new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int id) {
				dialog.dismiss();
			}
		});
		AlertDialog alert = builder.create();
		alert.show();

	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == event.KEYCODE_BACK) {
			selectBankName = "";
			selectBankCode = "";
			Bundle budle = new Bundle();
			budle.putString("bankValue", selectBankName);
			budle.putString("bankCode", selectBankCode);
			DrawMoneyModifyAccountActivity.this.setResult(RESULT_CANCELED, DrawMoneyModifyAccountActivity.this.getIntent().putExtras(budle));
			DrawMoneyModifyAccountActivity.this.finish();
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}
	
	private void actionModifyBank(){
		Event event = new Event(null,"bankAdd", null);
        event.setTransfer("089009");
        HashMap<String, String> map = new HashMap<String, String>();
        //TODO
        map.put("merchant_name", userBankModel.getMerchant_name());
        map.put("mastername", userBankModel.getMastername());
        map.put("bankaccount", et_account_confirm.getText().toString());//银行卡号
        map.put("banks", currentBank.getName());//银行名称
        map.put("bankno", currentBank.getCode()+"");//银行编号
        map.put("area", currentProvince.getName());//地区
        map.put("city", currentCity.getName());//城市
        map.put("addr", bankbranchname);//地址
		map.put("tel", ApplicationEnvironment.getInstance().getPreferences().getString(Constant.PHONENUM, ""));
        event.setStaticActivityDataMap(map);
        try {
			event.trigger();
		} catch (ViewException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
//		super.onActivityResult(requestCode, resultCode, data);
		if(resultCode == 5){
			bankbranchid = data.getStringExtra("bankbranchid");
			bankbranchname = data.getStringExtra("bankbranchname");
			btn_bank_branch.setText(bankbranchname);
		}
	}
}
