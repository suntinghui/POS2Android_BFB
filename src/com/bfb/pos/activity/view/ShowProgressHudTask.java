package com.bfb.pos.activity.view;

import com.bfb.pos.activity.BaseActivity;
import com.bfb.pos.R;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.graphics.drawable.AnimationDrawable;
import android.os.AsyncTask;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

public class ShowProgressHudTask extends AsyncTask<String, String, Void> implements OnCancelListener {
	
	ProgressHUD mProgressHUD;    	

	@Override
	protected void onPreExecute() {
    	mProgressHUD = ProgressHUD.show(BaseActivity.getTopActivity(),"请稍候", true,true,this);
		super.onPreExecute();
	}
	
	@Override
	protected Void doInBackground(String... params) {
		try {
			publishProgress(params[1]);
			Thread.sleep(Integer.parseInt(params[0]));
			publishProgress("");
			
			/**
			publishProgress("Connecting");
			Thread.sleep(2000);
			publishProgress("Downloading");
			Thread.sleep(5000);
			publishProgress("Done");
			**/
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	@Override
	protected void onProgressUpdate(String... values) {
		mProgressHUD.setMessage(values[0]);
		super.onProgressUpdate(values);
	}
	
	@Override
	protected void onPostExecute(Void result) {
		mProgressHUD.dismiss();
		super.onPostExecute(result);
	}

	@Override
	public void onCancel(DialogInterface dialog) {
		this.cancel(true);
		mProgressHUD.dismiss();
	}		
}

class ProgressHUD extends Dialog {
	
	public ProgressHUD(Context context) {
		super(context);
	}

	public ProgressHUD(Context context, int theme) {
		super(context, theme);
	}


	public void onWindowFocusChanged(boolean hasFocus){
		ImageView imageView = (ImageView) findViewById(R.id.spinnerImageView);
        AnimationDrawable spinner = (AnimationDrawable) imageView.getBackground();
        spinner.start();
    }
	
	public void setMessage(CharSequence message) {
		if(message != null && message.length() > 0) {
			findViewById(R.id.message).setVisibility(View.VISIBLE);			
			TextView txt = (TextView)findViewById(R.id.message);
			txt.setText(message);
			txt.invalidate();
		}
	}
	
	public static ProgressHUD show(Context context, CharSequence message, boolean indeterminate, boolean cancelable,
			OnCancelListener cancelListener) {
		ProgressHUD dialog = new ProgressHUD(context,R.style.ProgressHUD);
		dialog.setTitle("");
		dialog.setContentView(R.layout.progress_hud);
		if(message == null || message.length() == 0) {
			dialog.findViewById(R.id.message).setVisibility(View.GONE);			
		} else {
			TextView txt = (TextView)dialog.findViewById(R.id.message);
			txt.setText(message);
		}
		dialog.setCancelable(cancelable);
		dialog.setOnCancelListener(cancelListener);
		dialog.getWindow().getAttributes().gravity=Gravity.CENTER;
		WindowManager.LayoutParams lp = dialog.getWindow().getAttributes();  
		lp.dimAmount=0.2f;
		dialog.getWindow().setAttributes(lp); 
		//dialog.getWindow().addFlags(WindowManager.LayoutParams.FLAG_BLUR_BEHIND);
		dialog.show();
		return dialog;
	}	
}
