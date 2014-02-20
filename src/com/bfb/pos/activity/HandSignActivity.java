package com.bfb.pos.activity;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.InputFilter;
import android.text.InputType;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bfb.pos.activity.view.TextWithLabelView;
import com.bfb.pos.agent.client.Constant;
import com.bfb.pos.agent.client.db.TransferSuccessDBHelper;
import com.bfb.pos.R;

public class HandSignActivity extends BaseActivity implements OnClickListener {
	
	private TextView amountText = null;
	private TextWithLabelView phoneNumText = null;
	private Bitmap bitmap = null;
	
	private Button backButton = null;
	private Button okButton = null;
	private Button clearButton = null;
	private LinearLayout paintLayout = null;
	
	private PaintView paintView = null;
	
	private boolean hasSign = false; // 简单判断用户是否有签名
	
	private String md5Value = null;
	private String signImageName = null;
	private String tracenum = null;
	private String phoneNum = null;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		this.setContentView(R.layout.handsign);
		
		amountText = (TextView) this.findViewById(R.id.amount);
		
		phoneNumText = (TextWithLabelView) this.findViewById(R.id.phoneNum);
		phoneNumText.getEditText().setInputType(InputType.TYPE_CLASS_NUMBER);
		phoneNumText.getEditText().setFilters(new InputFilter[]{ new InputFilter.LengthFilter(11)});
		phoneNumText.setHintWithLabel(this.getResources().getString(R.string.phoneNo2), this.getResources().getString(R.string.pInputPhoneNoOut));
		
		backButton = (Button) this.findViewById(R.id.backButton);
		backButton.setOnClickListener(this);
		okButton = (Button) this.findViewById(R.id.okButton);
		okButton.setOnClickListener(this);
		clearButton = (Button) this.findViewById(R.id.clearButton);
		clearButton.setOnClickListener(this);
		
		paintLayout = (LinearLayout)this.findViewById(R.id.paintLayout);
		
		Intent intent = this.getIntent();
		tracenum = intent.getStringExtra("tracenum");
		md5Value = intent.getStringExtra("MD5");
		signImageName = intent.getStringExtra("signImageName");
		
		if (null == md5Value || "".equals(md5Value) || null == signImageName || "".equals(signImageName)){
			Toast.makeText(this, "系统发生异常，请重试！", Toast.LENGTH_SHORT).show();
			this.finish();
		}
		
		amountText.setText(intent.getStringExtra("amount"));
		
	}
	
	// 当activity显示出来的时候执行此方法
	@Override
	 public void onWindowFocusChanged(boolean hasFocus) {
		super.onWindowFocusChanged(hasFocus);
		
		if (hasFocus && null==paintView){
			// 首先创建一个新的位图
			bitmap = Bitmap.createBitmap(paintLayout.getWidth(), paintLayout.getHeight(), Config.ARGB_8888);
			
			paintView = new PaintView(this);
			paintLayout.addView(paintView, new LayoutParams(paintLayout.getWidth(), paintLayout.getHeight()));
			paintView.requestFocus();
		}
	 }
	
	private void drawMD5OnView(){
		Canvas canvas = new Canvas(bitmap);
		Paint p = new Paint();
		p.setColor(Color.parseColor("#dcdbda"));
		p.setTypeface(Typeface.create("null", Typeface.BOLD_ITALIC));
		float textWidth = p.measureText("ABCDEFGH"); 
		p.setTextSize(textWidth);
		float textSpace = (paintLayout.getWidth() - textWidth*8)/7;
		float textHeight = paintLayout.getHeight() / 4;
		
		for (int i=0; i<4; i++){
			String cell = md5Value.substring(md5Value.length()-8*(4-i), md5Value.length()-8*(3-i));
			for (int j=0; j<8; j++){
				canvas.drawText(String.valueOf(cell.charAt(j)), (textWidth+textSpace)*j, textHeight*(i+1), p);
			}
		}
		canvas.save(Canvas.ALL_SAVE_FLAG);
		canvas.restore();
	}

	@Override
	public void onClick(View view) {
		switch(view.getId()){
		case R.id.okButton:
			if (hasSign){
				phoneNum = phoneNumText.getText();
				// 要么不输入，只要输入就要保证正确格式
				if (!"".equals(phoneNum.trim()) && !phoneNum.matches("^(1(([35][0-9])|(47)|[8][01236789]))\\d{8}$")){
					Toast.makeText(this, this.getResources().getString(R.string.phoneNoIllegal), Toast.LENGTH_SHORT).show();
					break;
				} 
				
				new SaveImageTask().execute();
				
			} else {
				AlertDialog.Builder builder = new Builder(HandSignActivity.this);
				builder.setMessage("您还没有签名，请先完成签名");
				builder.setPositiveButton("确定", null);
				builder.create().show();
			}
			
			break;
			
		case R.id.backButton:
			this.setResult(RESULT_CANCELED, null);
			this.finish();
			break;
			
		case R.id.clearButton:
			if (hasSign){
				paintView.clear();
			}
			
			break;
		}
	}
	
	private Bitmap scaleBitmap(){
	    int width = bitmap.getWidth();
	    int height = bitmap.getHeight();
	    // 设置想要的大小
	    int newWidth = this.getWindowManager().getDefaultDisplay().getWidth();
	    int newHeight = height;
	    // 计算缩放比例
	    float scaleWidth = ((float) newWidth) / width;
	    float scaleHeight = ((float) newHeight) / height;
	    // 取得想要缩放的matrix参数
	    Matrix matrix = new Matrix();
	    matrix.postScale(scaleWidth, scaleWidth); // 使其等比缩放
	    // 得到新的图片
	    Bitmap newBitmap = Bitmap.createBitmap(bitmap, 0, 0, width, height, matrix, true);
	    
	    Canvas canvas = new Canvas(newBitmap);
	    canvas.drawColor(Color.parseColor("#fafad2"));
	    canvas.drawBitmap(bitmap, 0, 0, null);
	    
	    return newBitmap;
	}
	
	private void saveBitmapToFile(Bitmap mBitmap, String bitName){
		File mWorkingPath = new File(Constant.SIGNIMAGESPATH);
		if (!mWorkingPath.exists()) {
			if (!mWorkingPath. mkdirs()) {

			}
		}
		
		File f = new File(Constant.SIGNIMAGESPATH + bitName + ".JPEG");
		try {
			f.createNewFile();
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		FileOutputStream fOut = null;
		try {
			fOut = new FileOutputStream(f);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		
		mBitmap.compress(Bitmap.CompressFormat.JPEG, 50, fOut);
		
		try {
			fOut.flush();
			fOut.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	class PaintView extends View {
		private Paint paint;
		private Canvas cacheCanvas;
		private Path path;

		public PaintView(Context context) {
			super(context);
			
			initView();			
		}

		private void initView(){
			// 首先打印水印
			drawMD5OnView();
			this.setBackgroundDrawable(new BitmapDrawable(bitmap));
			
			// 初始化画笔，准备签名
			paint = new Paint();
			paint.setAntiAlias(true);
			paint.setStrokeWidth(5);
			paint.setStyle(Paint.Style.STROKE);
			paint.setColor(Color.BLACK);					
			path = new Path();
			cacheCanvas = new Canvas(bitmap);
			
		}
		
		public void clear() {
			hasSign = false;
			
			if (cacheCanvas != null) {
				paintLayout.removeAllViews();
				
				// 首先创建一个新的位图
				bitmap = Bitmap.createBitmap(paintLayout.getWidth(), paintLayout.getHeight(), Config.ARGB_8888);
				
				paintView = new PaintView(HandSignActivity.this);
				paintLayout.addView(paintView, new LayoutParams(paintLayout.getWidth(), paintLayout.getHeight()));
				paintLayout.invalidate();
			}
		}
		
		@Override
		protected void onDraw(Canvas canvas) {
			canvas.drawBitmap(bitmap, 0, 0, null);
			canvas.drawPath(path, paint);
		}

		@Override
		protected void onSizeChanged(int w, int h, int oldw, int oldh) {
			
			int curW = bitmap != null ? bitmap.getWidth() : 0;
			int curH = bitmap != null ? bitmap.getHeight() : 0;
			if (curW >= w && curH >= h) {
				return;
			}

			if (curW < w)
				curW = w;
			if (curH < h)
				curH = h;

			Bitmap newBitmap = Bitmap.createBitmap(curW, curH, Bitmap.Config.ARGB_8888);
			Canvas newCanvas = new Canvas();
			newCanvas.setBitmap(newBitmap);
			if (bitmap != null) {
				newCanvas.drawBitmap(bitmap, 0, 0, null);
			}
			bitmap = newBitmap;
			cacheCanvas = newCanvas;
		}

		private float cur_x, cur_y;

		@Override
		public boolean onTouchEvent(MotionEvent event) {
			
			float x = event.getX();
			float y = event.getY();

			switch (event.getAction()) {
			case MotionEvent.ACTION_DOWN: {
				cur_x = x;
				cur_y = y;
				path.moveTo(cur_x, cur_y);
				break;
			}

			case MotionEvent.ACTION_MOVE: {
				// 简单认为用户移动了手指即有签名
				hasSign = true;
				
				path.quadTo(cur_x, cur_y, x, y);
				cur_x = x;
				cur_y = y;
				break;
			}

			case MotionEvent.ACTION_UP: {
				cacheCanvas.drawPath(path, paint);
				path.reset();
				break;
			}
			}

			invalidate();

			return true;
		}
	}
	
	class SaveImageTask extends AsyncTask<Object, Object, Object>{
		
		@Override
		protected void onPreExecute() {
			HandSignActivity.this.showDialog(BaseActivity.PROGRESS_DIALOG, "正在保存签名信息");
		}

		@Override
		protected Object doInBackground(Object... arg0) {
			// 保存图片
			saveBitmapToFile(scaleBitmap(), signImageName);
			
			/***
			TransferSuccessDBHelper helper = new TransferSuccessDBHelper();
			helper.updateATransfer(tracenum, signImageName, phoneNum);
			*****/
			return null;
		}

		@Override
		protected void onPostExecute(Object result) {
			HandSignActivity.this.hideDialog(BaseActivity.PROGRESS_DIALOG);
			Intent intent = new Intent();
			intent.putExtra("phoneNum", phoneNum);
			setResult(RESULT_OK, intent);
			finish();
		}

	}

}
