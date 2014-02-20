package com.bfb.pos.activity;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.os.Bundle;
import android.os.Vibrator;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.bfb.pos.agent.client.Constant;
import com.bfb.pos.agent.client.StaticNetClient;
import com.bfb.pos.agent.client.TransferLogic;
import com.bfb.pos.dynamic.core.Event;
import com.bfb.pos.util.StringUtil;
import com.dhc.dynamic.parse.ParseView;
import com.bfb.pos.R;

public class InputMoneyActivity extends BaseActivity implements OnClickListener {

	private Vibrator vibrator;

	private String inputString = "0.00";

	private LinearLayout dateLayout = null;
	private LinearLayout displayPadLayout = null;

	private Button backButton = null;
	private Button button_1 = null;
	private Button button_2 = null;
	private Button button_3 = null;
	private Button button_4 = null;
	private Button button_5 = null;
	private Button button_6 = null;
	private Button button_7 = null;
	private Button button_8 = null;
	private Button button_9 = null;
	private Button button_0 = null;
	private Button button_delete = null;
	private Button button_submit = null;
	private Button button_clear = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		this.setContentView(R.layout.input_money);
		dateLayout = (LinearLayout) this.findViewById(R.id.dateLayout);
		displayPadLayout = (LinearLayout) this.findViewById(R.id.displayPadLayout);

		backButton = (Button) this.findViewById(R.id.backButton);
		backButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				finish();
			}

		});

		button_1 = (Button) this.findViewById(R.id.button_1);
		button_2 = (Button) this.findViewById(R.id.button_2);
		button_3 = (Button) this.findViewById(R.id.button_3);
		button_4 = (Button) this.findViewById(R.id.button_4);
		button_5 = (Button) this.findViewById(R.id.button_5);
		button_6 = (Button) this.findViewById(R.id.button_6);
		button_7 = (Button) this.findViewById(R.id.button_7);
		button_8 = (Button) this.findViewById(R.id.button_8);
		button_9 = (Button) this.findViewById(R.id.button_9);
		button_0 = (Button) this.findViewById(R.id.button_0);
		button_delete = (Button) this.findViewById(R.id.button_delete);
		button_submit = (Button) this.findViewById(R.id.button_submit);
		button_clear = (Button) this.findViewById(R.id.clearButton);

		button_1.setOnClickListener(this);
		button_2.setOnClickListener(this);
		button_3.setOnClickListener(this);
		button_4.setOnClickListener(this);
		button_5.setOnClickListener(this);
		button_6.setOnClickListener(this);
		button_7.setOnClickListener(this);
		button_8.setOnClickListener(this);
		button_9.setOnClickListener(this);
		button_0.setOnClickListener(this);
		button_delete.setOnClickListener(this);
		button_submit.setOnClickListener(this);
		button_clear.setOnClickListener(this);

		this.showDateLayout();
		this.refreshDisplayPad(inputString);
	}

	@Override
	public void onClick(View view) {
		// 震动提示
		if (null == vibrator)
			vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);

		vibrator.vibrate(15);

		int tag = Integer.parseInt((String) view.getTag());
		switch (tag) {
		case 11: // 删除
			if ("0.00".equals(inputString))
				return;

			pressDeleteButton(11);
			break;

		case 12: // 确定
			if ("0.00".equals(inputString)) {
				this.showDialog(BaseActivity.NONMODAL_DIALOG, "请输入有效金额");
			} else if(inputString.length() > 6){
				this.showDialog(BaseActivity.NONMODAL_DIALOG, "最大限额1,000,000");
			}else {
				pressSubmitButton();
			}
			break;

		case 13:// 清除
			pressClearButton();
			break;

		default: // 数字 0-9
			if (inputString.length() > 12)
				return;

			pressNumericButton(tag);
			break;
		}
	}

	private void pressNumericButton(int tag) {
		inputString = this.formatString(inputString, tag);
		this.refreshDisplayPad(inputString);
	}

	private void pressDeleteButton(int tag) {
		inputString = this.formatString(inputString, tag);
		this.refreshDisplayPad(inputString);
	}

	private void pressSubmitButton() {
		if (Constant.isAISHUA) {
			// Intent intent = new Intent(this,
			// ASBalancePwd2Activity.class);
			// this.startActivityForResult(intent, 0);

			try {

				Event event = new Event(null, "swip", null);
				String fsk = "swipeCard|null";
				event.setFsk(fsk);
				event.setActionType(ParseView.ACTION_TYPE_LOCAL);
				event.setAction("ASBalancePwd2Activity");
				HashMap<String, String> map = new HashMap<String, String>();
				map.put("amount", StringUtil.amount2String(inputString));
				event.setStaticActivityDataMap(map);
				event.trigger();

			} catch (Exception e) {
				e.printStackTrace();
			}

			return;
		}

		// 因为在这里有可能交易后验证服务器响应数据时点付宝发生异常，这时应该检查冲正。
		if (TransferLogic.getInstance().reversalAction()) {
			return;
		}

		if (Constant.isStatic) {
			StaticNetClient.demo_amount = inputString.replace(".", "");
		}

		try {
			Event event = new Event(null, "transfer", null);
			String fskStr = "Get_PsamNo|null#Get_VendorTerID|null#Get_CardTrack|int:60#Get_PIN|int:0,int:0,string:" + inputString.replace(".", "") + ",string:null,string:__PAN,int:60";
			event.setFsk(fskStr);
			event.setTransfer("020022");
			HashMap<String, String> map = new HashMap<String, String>();
			map.put("field4", StringUtil.amount2String(inputString));
			event.setStaticActivityDataMap(map);
			event.trigger();

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	private void pressClearButton() {
		inputString = "0.00";
		this.refreshDisplayPad(inputString);
	}

	private void showDateLayout() {
		dateLayout.removeAllViews();

		String currentDate = this.getDate();
		for (int i = 0; i < currentDate.length(); i++) {
			dateLayout.addView(this.getDigitDateImage(currentDate.charAt(i)));
		}
	}

	private void refreshDisplayPad(String money) {
		displayPadLayout.removeAllViews();

		for (int i = 0; i < money.length(); i++) {
			displayPadLayout.addView(this.getDigitMoneyImage(money.charAt(i)));
		}
	}

	private String getDate() {
		SimpleDateFormat format = new SimpleDateFormat("yyyy/MM/dd");
		return format.format(new Date());
	}

	/**
	 * 
	 * @param srcStr
	 *            原字符串
	 * @param tag
	 *            代表一种操作。如果是0-9表示输入，如果是11表示删除一个字符
	 * @return 返回新的字符串
	 */
	private String formatString(String srcStr, int tag) {
		double temp = Double.parseDouble(srcStr);
		double des = 0.00;
		if (tag == 11) { // 删除一位
			des = temp / 10;// -0.005防止四舍五入
		} else {
			des = temp * 10 + 0.01 * tag;
		}
		// 一个奇怪的问题，0.005->0.00 , 0.055->0.06
		// return String.format("%.2f", des);
		// SO des = temp * 10 + 0.01 * tag - 0.005; 不再减0.005

		String str = String.format("%.3f", des);
		return str.substring(0, str.length() - 1);
	}

	private ImageView getDigitMoneyImage(char c) {
		int resourceId = this.getResources().getIdentifier("digit_green_" + (c == '.' ? "dot" : c), "drawable", this.getPackageName());
		Bitmap bmp = BitmapFactory.decodeResource(getResources(), resourceId);

		// http://blog.csdn.net/hustspy1990/article/details/6676303
		float scaleWidth = 0.7f;
		float scaleHeight = 0.7f;
		// 设置图片缩小比例
		if (inputString.length() > 12) {
			scaleWidth = 0.35f;
			scaleHeight = 0.35f;
		} else if (inputString.length() > 10) {
			scaleWidth = 0.4f;
			scaleHeight = 0.4f;
		} else if (inputString.length() > 8) {
			scaleWidth = 0.5f;
			scaleHeight = 0.5f;
		} else if (inputString.length() > 7) {
			scaleWidth = 0.6f;
			scaleHeight = 0.6f;
		}

		int bmpWidth = bmp.getWidth();
		int bmpHeight = bmp.getHeight();

		// 产生ReSize之后的bmp对象
		Matrix matrix = new Matrix();
		matrix.postScale(scaleWidth, scaleHeight);
		Bitmap resizeBmp = Bitmap.createBitmap(bmp, 0, 0, bmpWidth, bmpHeight, matrix, true);

		ImageView imageView = new ImageView(this);
		imageView.setImageBitmap(resizeBmp);

		return imageView;
	}

	private ImageView getDigitDateImage(char c) {
		int resourceId = this.getResources().getIdentifier("digit_little_" + (c == '/' ? "slash" : c), "drawable", this.getPackageName());
		Bitmap bmp = BitmapFactory.decodeResource(getResources(), resourceId);

		// http://blog.csdn.net/hustspy1990/article/details/6676303
		float scaleWidth = 0.8f;
		float scaleHeight = 0.8f;

		int bmpWidth = bmp.getWidth();
		int bmpHeight = bmp.getHeight();

		// 产生ReSize之后的bmp对象
		Matrix matrix = new Matrix();
		matrix.postScale(scaleWidth, scaleHeight);
		Bitmap resizeBmp = Bitmap.createBitmap(bmp, 0, 0, bmpWidth, bmpHeight, matrix, true);

		ImageView imageView = new ImageView(this);
		imageView.setImageBitmap(resizeBmp);

		return imageView;
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_MENU) {
			return false;
		} else {
			return super.onKeyDown(keyCode, event);
		}
	}

}
