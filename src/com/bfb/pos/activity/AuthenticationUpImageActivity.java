package com.bfb.pos.activity;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;

import com.bfb.pos.agent.client.AppDataCenter;
import com.bfb.pos.agent.client.ApplicationEnvironment;
import com.bfb.pos.agent.client.Constant;
import com.bfb.pos.dynamic.component.ViewException;
import com.bfb.pos.dynamic.core.Event;
import com.bfb.pos.R;

//实名认证 上图图片
public class AuthenticationUpImageActivity extends BaseActivity implements OnClickListener {

	private ImageView iv_0;
	private ImageView iv_1;
	private ImageView iv_2;
	private ImageView iv_3;
	
	private String bitmap_str_0 = null;
	private String bitmap_str_1 = null;
	private String bitmap_str_2 = null;
	private String bitmap_str_3 = null;
	
	private String merchant_id;
	
	private int current_index = 0;
	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		this.setContentView(R.layout.activity_authentication_upimage);

		Button btn_back = (Button) this.findViewById(R.id.btn_back);
		btn_back.setOnClickListener(this);

		Button btn_confirm = (Button) this.findViewById(R.id.btn_confirm);
		btn_confirm.setOnClickListener(this);

		Intent intent = this.getIntent();
		merchant_id = intent.getStringExtra("merchant_id");
		iv_0 = (ImageView) this.findViewById(R.id.iv_0);
		iv_1 = (ImageView) this.findViewById(R.id.iv_1);
		iv_2 = (ImageView) this.findViewById(R.id.iv_2);
		iv_3 = (ImageView) this.findViewById(R.id.iv_3);

		iv_0.setOnClickListener(this);
		iv_1.setOnClickListener(this);
		iv_2.setOnClickListener(this);
		iv_3.setOnClickListener(this);
	}

	@Override
	public void onClick(View arg0) {
		switch (arg0.getId()) {
		case R.id.btn_back:
			this.finish();
			break;
		case R.id.btn_confirm:
			
			HashMap<String, String> map = new HashMap<String, String>();
			map.put("img15", bitmap_str_0);
			map.put("img13", bitmap_str_1);
			map.put("img17", bitmap_str_2);
			map.put("img14", bitmap_str_3);
			
			
			map.put("merchant_id", merchant_id);
			map.put("tel", ApplicationEnvironment.getInstance().getPreferences().getString(Constant.PHONENUM, ""));

			Event event = new Event(null,"identifyMerchant", null);
			event.setStaticActivityDataMap(map);
	        event.setTransfer("089020");
	        try {
	        	BaseActivity.getTopActivity().showDialog("正在上传图片，请稍候 ", "089020");
				event.trigger();
			} catch (ViewException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			break;
		case R.id.iv_0:
			current_index = 0;
			actionCamera();
			break;
		case R.id.iv_1:
			current_index = 1;
			actionCamera();
			break;

		case R.id.iv_2:
			current_index = 2;
			actionCamera();
			break;

		case R.id.iv_3:
			current_index = 3;
			actionCamera();
			break;

		default:
			break;
		}

	}

	private void actionCamera(){
		Intent getImageByCamera= new Intent("android.media.action.IMAGE_CAPTURE");   
        startActivityForResult(getImageByCamera, 1);   
	}
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//		super.onActivityResult(requestCode, resultCode, data);
		
		if(requestCode == 1){
			Bitmap bm = null;
			 try {
//                super.onActivityResult(requestCode, resultCode, data);
                    Bundle extras = data.getExtras();
                    bm = (Bitmap) extras.get("data");
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();     
                    bm.compress(Bitmap.CompressFormat.JPEG , 100, baos);
//                        mContent=baos.toByteArray();
                } catch (Exception e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                }
       if(bm != null){
       	switch (current_index) {
			case 0:
				
				Log.i("bm0:", bm.toString());
				bitmap_str_0 = bitmaptoString(bm);
				iv_0.setImageBitmap(bm);
				break;
			case 1:
				Log.i("bm1:", bm.toString());
				bitmap_str_1 = bitmaptoString(bm);
				iv_1.setImageBitmap(bm);
				break;
			case 2:
				Log.i("bm2:", bm.toString());
				bitmap_str_2 = bitmaptoString(bm);
				iv_2.setImageBitmap(bm);
				break;
			case 3:
				Log.i("bm3:", bm.toString());
				bitmap_str_3 = bitmaptoString(bm);
				iv_3.setImageBitmap(bm);
				break;

			default:
				break;
			}
       	
       }	
		}
	}
	public String bitmaptoString(Bitmap bitmap) {

		// 将Bitmap转换成字符串
		String string = null;
		ByteArrayOutputStream bStream = new ByteArrayOutputStream();
		bitmap.compress(CompressFormat.PNG, 100, bStream);
		byte[] bytes = bStream.toByteArray();
		string = Base64.encodeToString(bytes, Base64.DEFAULT);
		return string;

	}
	
}
