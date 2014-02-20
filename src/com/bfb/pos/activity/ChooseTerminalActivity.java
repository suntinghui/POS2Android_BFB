package com.bfb.pos.activity;

import com.bfb.pos.activity.DrawMoneyAddAccountActivity.BankAdapter;
import com.bfb.pos.model.CityModel;
import com.bfb.pos.util.Bank;
import com.bfb.pos.util.Province;
import com.bfb.pos.R;

import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.AdapterView.OnItemSelectedListener;

public class ChooseTerminalActivity extends BaseActivity implements OnClickListener {
	
	private Button backButton = null;
	private Spinner spinner0 = null;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		this.setContentView(R.layout.activity_choose_terminal);
		 
		backButton = (Button) this.findViewById(R.id.btn_back);
		backButton.setOnClickListener(this);
		spinner0 = (Spinner) findViewById(R.id.spinner0);
		String[] array = {"刷卡键盘", "音频pos", "刷卡头"};
		ArrayAdapter<String> bankAdapter = new ArrayAdapter<String>(this, R.layout.simple_spinner_item, android.R.id.text1, array);
		spinner0.setAdapter(bankAdapter);

		spinner0.setOnItemSelectedListener(new ChooseAdapter());
		
		

	}
	@Override
	public void onClick(View arg0) {
		switch (arg0.getId()) {
		case R.id.btn_back:
			this.finish();
			break;

		default:
			break;
		}
		
	}

	class ChooseAdapter implements OnItemSelectedListener {

		/**
		 * (non-Javadoc)
		 * 
		 * @see android.widget.AdapterView.OnItemSelectedListener#onItemSelected(android.widget.AdapterView,
		 *      android.view.View, int, long)
		 */
		@Override
		public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
			onChange(position);
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

	public void onChange(int position) {
		

	}
}
