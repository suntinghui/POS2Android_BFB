package com.bfb.pos.activity;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.bfb.pos.activity.view.TextWithLabelView;
import com.bfb.pos.agent.client.Constant;
import com.bfb.pos.util.GMailSender;
import com.bfb.pos.R;

public class FeedbackActivity extends BaseActivity implements OnClickListener, TextWatcher {
	
	private static final int MAXINPUTCOUNT	= 150;
	
	private EditText contentText			= null;
	private TextView countView				= null;
	private TextWithLabelView mailText		= null;
	private Button okButton					= null;
	private Button backButton				= null;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		this.setContentView(R.layout.feedback);
		
		contentText = (EditText) this.findViewById(R.id.contentText);
		contentText.setFilters(new InputFilter[]{new InputFilter.LengthFilter(MAXINPUTCOUNT)}); 
		contentText.addTextChangedListener(this);
		
		countView = (TextView) this.findViewById(R.id.counter);
		countView.setText("您还可输入"+MAXINPUTCOUNT+"个字");
		
		mailText = (TextWithLabelView) this.findViewById(R.id.mailText);
		mailText.setHintWithLabel("您的邮箱", "选填，便于我们给您答复");
		
		okButton = (Button) this.findViewById(R.id.okButton);
		okButton.setOnClickListener(this);
		backButton = (Button) this.findViewById(R.id.backButton);
		backButton.setOnClickListener(this);
	}
	
	@Override
	public void afterTextChanged(Editable editable) {
		countView.setText("您还可输入"+(MAXINPUTCOUNT-editable.toString().length()) + "个字");
	}

	@Override
	public void beforeTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
	}

	@Override
	public void onTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
	}

	@Override
	public void onClick(View view) {
		switch(view.getId()){
		case R.id.backButton:
			this.finish();
			break;
			
		case R.id.okButton:
			if (checkInput()){
				
				new SendEmailTask().execute();
				
				AlertDialog.Builder builder = new Builder(this);
				builder.setTitle("提示");
				builder.setMessage("信息发送成功，真诚感谢您的支持。谢谢");
				builder.setPositiveButton("确定", new android.content.DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
						finish();
					}
				});
				
				builder.show();
			}
			break;
		}
	}
	
	private boolean checkInput(){
		if ("".equals(contentText.getText().toString())){
			Toast.makeText(this, "请填写反馈信息，谢谢您的支持！", Toast.LENGTH_SHORT).show();
			return false;
		}
		
		if (!"".equals(mailText.getText()) && !mailText.getText().matches("^([a-z0-9A-Z]+[-|\\.]?)+[a-z0-9A-Z]@([a-z0-9A-Z]+(-[a-z0-9A-Z]+)?\\.)+[a-zA-Z]{2,}$")){
			Toast.makeText(this, "邮箱格式不正确", Toast.LENGTH_SHORT).show();
			return false;
		}
		
		return true;
	}
	
	class SendEmailTask extends AsyncTask<Object, Object, Object>{

		@Override
		protected Object doInBackground(Object... params) {
			String content = contentText.getText().toString();
			if (!"".equals(mailText.getText().trim())){
				this.sendEmail(content + "  【反馈信息来自:" + mailText.getText() + "】");
			} else {
				this.sendEmail(content);
			}
			return null;
		}
		
		private void sendEmail(String content){
			try {    
	             GMailSender sender = new GMailSender();
	             sender.sendMail("意见反馈", // subject   
	                    content   // content
	                    );    
	        } catch (Exception e) {    
	            Log.e("SendMail", e.getMessage(), e);    
	        	
	        }  
		}
		
	}

}
