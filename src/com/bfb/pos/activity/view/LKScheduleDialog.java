package com.bfb.pos.activity.view;

import com.bfb.pos.R;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

public class LKScheduleDialog extends Dialog {
	
	private Context context;
	private String title;
	private ProgressBar progressBar;
	private TextView progressView;
	private TextView detailView;
	
	private View contentView;
	
	private String negativeButtonText;
	
	private DialogInterface.OnClickListener negativeButtonClickListener;

	public LKScheduleDialog(Context context) {
		super(context, R.style.Dialog);
		this.context = context;
	}

	public LKScheduleDialog(Context context, int theme) {
		super(context, theme);
		this.context = context;
	}

	public void setTitle(String title) {
		this.title = title;
	}
	
	public void setProgress(int progress){
		this.progressBar.setProgress(progress);
		this.progressView.setText("已完成:"+progress+"%");
	}
	
	public void setDetail(String detail){
		this.detailView.setText(detail);
	}
	
	public void setNegativeButton(String negativeButtonText,
			DialogInterface.OnClickListener listener) {
		this.negativeButtonText = negativeButtonText;
		this.negativeButtonClickListener = listener;
	}

	public LKScheduleDialog create() {
		LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		// instantiate the dialog with the custom Theme
		View layout = inflater.inflate(R.layout.schedule_dialog_layout, null);
		this.addContentView(layout, new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT));
		// set the dialog title
		((TextView) layout.findViewById(R.id.title)).setText(title);
		this.progressBar = (ProgressBar) layout.findViewById(R.id.progressBar);
		this.progressBar.setMax(100);
		this.progressBar.setIndeterminate(false);
		this.progressView = (TextView) layout.findViewById(R.id.progress);
		this.detailView = (TextView) layout.findViewById(R.id.detail);
		
		// set the cancel button
		if (negativeButtonText != null) {
			((Button) layout.findViewById(R.id.negativeButton)).setText(negativeButtonText);
			if (negativeButtonClickListener != null) {
				((Button) layout.findViewById(R.id.negativeButton)).setOnClickListener(new View.OnClickListener() {
					public void onClick(View v) {
						negativeButtonClickListener.onClick(LKScheduleDialog.this,DialogInterface.BUTTON_NEGATIVE);
					}
				});
			}
		} else {
			// if no confirm button just set the visibility to GONE
			layout.findViewById(R.id.negativeButton).setVisibility(View.GONE);
		}
		
		// set the confirm button
		if (contentView != null) {
			// if no message set
			// add the contentView to the dialog body
			((LinearLayout) layout.findViewById(R.id.message)).removeAllViews();
			((LinearLayout) layout.findViewById(R.id.message)).addView(contentView, new LayoutParams(LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT));
		}
		
		this.setContentView(layout);
		
		return this;
	}
}
