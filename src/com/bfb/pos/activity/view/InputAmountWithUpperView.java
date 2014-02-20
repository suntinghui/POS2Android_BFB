package com.bfb.pos.activity.view;

import com.bfb.pos.util.ChangeCNNumber;
import com.bfb.pos.util.FormatCurrency;
import com.bfb.pos.R;

import android.content.Context;
import android.text.Editable;
import android.text.InputFilter;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.view.View.OnFocusChangeListener;

public class InputAmountWithUpperView extends LinearLayout implements TextWatcher{
	
	private TextWithLabelView amountText;
	private TextView upperView;
	
	public InputAmountWithUpperView(Context context) {
		super(context);
		
		init(context);
	}

	public InputAmountWithUpperView(Context context, AttributeSet attrs) {
		super(context, attrs);
		
		init(context);
	}
	
	private void init(Context context){
		LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		inflater.inflate(R.layout.input_amount_with_upper, this);
		
		amountText = (TextWithLabelView) this.findViewById(R.id.amountText);
		upperView = (TextView) this.findViewById(R.id.upperView);
		upperView.setVisibility(View.GONE);
		
		amountText.setHintWithLabel(this.getResources().getString(R.string.amount), this.getResources().getString(R.string.pInputAmount));
		amountText.getEditText().setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
		amountText.getEditText().setFilters(new InputFilter[]{ new InputFilter.LengthFilter(10)});
		
		amountText.getEditText().addTextChangedListener(this);
	}
	
	public String getAmount() {
		return amountText.getText();
	}

	public String getUpper() {
		return upperView.getText().toString();
	}

	// TextWatcher
	@Override
	public void afterTextChanged(Editable edt) {
		// 限制只能输入两位小数
		String temp = edt.toString();
		int posDot = temp.indexOf(".");
		if (posDot <= 0)
			return;
		if (temp.length() - posDot - 1 > 2) {
			edt.delete(posDot + 3, posDot + 4);
		}
		
		// 死循环
		// amountText.setText(FormatCurrency.formatAmount(temp));
	}

	@Override
	public void beforeTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
		
	}

	@Override
	public void onTextChanged(CharSequence s, int arg1, int arg2, int arg3) {
		upperView.setVisibility(View.VISIBLE);
		upperView.setText(this.getResources().getString(R.string.upperAmount)+ChangeCNNumber.changeNumber(s.toString()));
	}

}
