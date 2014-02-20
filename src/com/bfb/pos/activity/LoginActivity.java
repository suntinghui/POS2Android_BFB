package com.bfb.pos.activity;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.security.KeyFactory;
import java.security.PublicKey;
import java.security.spec.RSAPublicKeySpec;
import java.util.HashMap;

import javax.crypto.Cipher;

import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.bfb.pos.activity.view.PasswordWithIconView;
import com.bfb.pos.agent.client.ApplicationEnvironment;
import com.bfb.pos.agent.client.Constant;
import com.bfb.pos.dynamic.component.ViewException;
import com.bfb.pos.dynamic.core.Event;
import com.bfb.pos.util.ByteUtil;
import com.bfb.pos.util.StringUtil;
import com.bfb.pos.util.UnionDes;
import com.bfb.pos.util.XORUtil;
import com.bfb.pos.R;
import com.itron.android.ftf.Util;

public class LoginActivity extends BaseActivity {
	private EditText userNameET;
	private PasswordWithIconView et_pwd;
	private ImageView rememberIV;
	private Button getPwdButton;
	private Button registerButton;
	private Button loginButton;

	private Boolean isRemember;

	private final String IMAGE_TYPE = "image/*";
	private final int IMAGE_CODE = 5; // 这里的IMAGE_CODE是自己任意定义的
	private Bitmap bm = null;

	private String[] types = new String[] { "刷卡键盘", "音频POS", "刷卡头" };
	private ButtonOnClick btn_click = new ButtonOnClick(0);

	private SharedPreferences preferences = ApplicationEnvironment.getInstance().getPreferences();

	int terIndex;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);

		terIndex = ApplicationEnvironment.getInstance().getPreferences().getInt(Constant.terIndex, 2);
		if (terIndex == 2) {
			Constant.isAISHUA = true;
		} else {
			Constant.isAISHUA = false;
		}
		userNameET = (EditText) this.findViewById(R.id.usernameET);

		et_pwd = (PasswordWithIconView) this.findViewById(R.id.et_pwd);
		et_pwd.setIconAndHint(R.drawable.icon_pwd, "密码");

		rememberIV = (ImageView) this.findViewById(R.id.rememberIV);
		rememberIV.setOnClickListener(listener);
		isRemember = ApplicationEnvironment.getInstance().getPreferences().getBoolean(Constant.kISREMEBER, false);
		setRemeberImageView(isRemember);
		if (isRemember) {
			et_pwd.setText(ApplicationEnvironment.getInstance().getPreferences().getString(Constant.LOGINPWD, ""));
		}

		getPwdButton = (Button) this.findViewById(R.id.getPwdButton);
		getPwdButton.setOnClickListener(listener);

		registerButton = (Button) this.findViewById(R.id.registerButton);
		registerButton.setOnClickListener(listener);

		loginButton = (Button) this.findViewById(R.id.loginButton);
		loginButton.setOnClickListener(listener);

		Button btn_set = (Button) this.findViewById(R.id.btn_set);
		btn_set.setOnClickListener(listener);
	}

	private void setRemeberImageView(Boolean remember) {
		if (remember) {
			rememberIV.setBackgroundResource(R.drawable.remeberpwd_s);
		} else {
			rememberIV.setBackgroundResource(R.drawable.remeberpwd_n);
		}
	}

	private OnClickListener listener = new OnClickListener() {

		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.rememberIV: {
				isRemember = !isRemember;
				setRemeberImageView(isRemember);
				break;
			}
			case R.id.getPwdButton: {
				getPwdAction();
				break;
			}
			case R.id.registerButton: {
				// setVerId();
				registerAction();
				// shangsong();
				break;
			}
			case R.id.btn_set:
				showTerminalList();
				break;
			case R.id.loginButton: {
				if (checkValue()) {

					SharedPreferences.Editor editor = preferences.edit();
					editor.putBoolean(Constant.kISREMEBER, isRemember);
					if (isRemember) {
						editor.putString(Constant.LOGINPWD, et_pwd.getText());
					} else {
						editor.putString(Constant.LOGINPWD, "");
					}

					Boolean firstLogin = preferences.getBoolean("firstLogin", true);
					if (firstLogin) {
						editor.putBoolean("firstLogin", false);
						showTerminalList();

					} else {
						loginAction();
					}

					editor.commit();

				}

				break;
			}
			}

		}

	};

	private void setVerId() {
		Event event = new Event(null, "login", null);
		String fsk = "Get_RenewVendorTerID|string:195300430101001,string:19530008";
		event.setFsk(fsk);
		try {
			event.trigger();
		} catch (ViewException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void shangsong() {
		try {

			Event event = new Event(null, "test", null);
			event.setTransfer("032000");// 050000

			String fsk = "Get_VendorTerID|null";
			event.setFsk(fsk);

			event.trigger();

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private class ButtonOnClick implements DialogInterface.OnClickListener {

		private int index; // 表示选项的索引

		public ButtonOnClick(int index) {
			this.index = index;
		}

		@Override
		public void onClick(DialogInterface dialog, int which) {
			if (which >= 0) {
				// 如果单击的是列表项，将当前列表项的索引保存在index中。
				// 如果想单击列表项后关闭对话框，可在此处调用dialog.cancel()
				// 或是用dialog.dismiss()方法。
				index = which;
				terIndex = index;
			} else {
				// 用户单击的是【确定】按钮
				if (which == DialogInterface.BUTTON_POSITIVE) {
					SharedPreferences.Editor editor = preferences.edit();
					editor.putInt(Constant.terIndex, terIndex);
					editor.commit();
					if (terIndex == 2) {
						Constant.isAISHUA = true;
					} else {
						Constant.isAISHUA = false;
					}
				} else if (which == DialogInterface.BUTTON_NEGATIVE) {
					dialog.dismiss();
				}
			} // 用户单击的是【取消】按钮

		}

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.login, menu);
		return true;
	}

	private Boolean checkValue() {

		if (userNameET.length() == 0) {
			this.showToast("用户名不能为空！");
			return false;
		} else if (et_pwd.getText().length() == 0) {
			this.showToast("密码不能为空！");
			return false;
		}
		return true;
	}

	private void loginAction() {

		if (checkValue()) {

			Editor editor = ApplicationEnvironment.getInstance().getPreferences().edit();
			editor.putBoolean(Constant.kISREMEBER, isRemember);
			Log.i("phone:", userNameET.getText().toString());
			editor.putString(Constant.PHONENUM, userNameET.getText().toString());// userNameET.getText().toString()
			editor.commit();
			try {

				Event event = new Event(null, "login", null);
				event.setTransfer("089016");
				HashMap<String, String> map = new HashMap<String, String>();
				map.put("tel", userNameET.getText().toString());
				map.put("logpass", et_pwd.getEncryptPWD());
				event.setStaticActivityDataMap(map);
				event.trigger();

			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		// Intent intent = new Intent(LoginActivity.this,
		// LRCatalogActivity.class);
		// LoginActivity.this.startActivity(intent);

	}

	private void registerAction() {
		byte[] out = ByteUtil.hexStringToBytes("C92C55FBB48F621D");
		String aishuamap1 = Util.BinToHex(out,0,out.length);
		int len = aishuamap1.getBytes().length;
		Intent intent = new Intent(this, RegisterActivity.class);
		startActivity(intent);
	}

	private void getPwdAction() {
		Intent intent = new Intent(LoginActivity.this, FindPasswordActivity.class);
		LoginActivity.this.startActivity(intent);
	}

	// 对密码进行RSA加密
	public String encryptPassword(String pwd) throws Exception {
		pwd = pwd + "FF";

		// 公钥初始化
		String mod = ApplicationEnvironment.getInstance().getPreferences().getString(Constant.PUBLICKEY_MOD, Constant.INIT_PUBLICKEY_MOD);
		String exp = ApplicationEnvironment.getInstance().getPreferences().getString(Constant.PUBLICKEY_EXP, Constant.INIT_PUBLICKEY_EXP);

		if ("".equals(mod) || "".equals(exp)) {
			throw new Exception(this.getResources().getString(R.string.noPublicKey));
		}

		try {
			BigInteger m = new BigInteger(mod, 16);
			BigInteger e = new BigInteger(exp, 16);
			RSAPublicKeySpec keySpec = new RSAPublicKeySpec(m, e);
			KeyFactory fact = KeyFactory.getInstance("RSA");
			PublicKey pubKey = fact.generatePublic(keySpec);

			// Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
			Cipher cipher = Cipher.getInstance("RSA");

			cipher.init(Cipher.ENCRYPT_MODE, pubKey);
			byte[] cipherData = cipher.doFinal(pwd.getBytes());

			String tmp = Util.BinToHex(cipherData, 0, cipherData.length);
			Log.i("pwd_encry", tmp);

			return tmp;

		} catch (Exception e) {
			throw new Exception(this.getResources().getString(R.string.noPublicKey));
		}

	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {

		if (resultCode != RESULT_OK) { // 此处的 RESULT_OK 是系统自定义得一个常量

			return;

		}

		bm = null;
		// 外界的程序访问ContentProvider所提供数据 可以通过ContentResolver接口
		ContentResolver resolver = getContentResolver();
		// 此处的用于判断接收的Activity是不是你想要的那个
		if (requestCode == IMAGE_CODE) {
			try {
				Uri originalUri = data.getData(); // 获得图片的uri
				bm = MediaStore.Images.Media.getBitmap(resolver, originalUri); // 显得到bitmap图片这里开始的第二部分，获取图片的路径：
				String[] proj = { MediaStore.Images.Media.DATA };
				// 好像是android多媒体数据库的封装接口，具体的看Android文档
				Cursor cursor = managedQuery(originalUri, proj, null, null, null);
				// 按我个人理解 这个是获得用户选择的图片的索引值

				// int column_index =
				// cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
				// //将光标移至开头 ，这个很重要，不小心很容易引起越界
				// cursor.moveToFirst();
				// //最后根据索引值获取图片路径
				// String path = cursor.getString(column_index);
			} catch (IOException e) {

			}

		}

	}

	public Bitmap stringtoBitmap(String string) {
		// 将字符串转换成Bitmap类型
		Bitmap bitmap = null;
		try {
			byte[] bitmapArray;
			bitmapArray = Base64.decode(string, Base64.DEFAULT);
			bitmap = BitmapFactory.decodeByteArray(bitmapArray, 0, bitmapArray.length);
		} catch (Exception e) {
			e.printStackTrace();
		}

		return bitmap;
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

	private void showTerminalList() {
		AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);
		builder.setTitle("请选择终端");
		builder.setSingleChoiceItems(types, ApplicationEnvironment.getInstance().getPreferences().getInt(Constant.terIndex, 2), btn_click);
		builder.setPositiveButton("确定", btn_click);
		builder.show();
	}
}