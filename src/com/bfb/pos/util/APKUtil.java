package com.bfb.pos.util;

import java.io.File;
import java.lang.reflect.Method;

import com.bfb.pos.agent.client.ApplicationEnvironment;
import com.bfb.pos.agent.client.Constant;

import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.util.DisplayMetrics;
import android.util.Log;

public class APKUtil {
	
	// 得到任意apk公钥信息的md5字符串
	public static String getApkSignatureMD5(String apkPath) {
		SharedPreferences sp = ApplicationEnvironment.getInstance().getPreferences();
		String apkmd5 = sp.getString(Constant.APKMD5VALUE, "");
		
		if (!apkmd5.equals("")) {
			return apkmd5;
			
		} else {
			try{
				Class<?> clazz = Class.forName("android.content.pm.PackageParser");
				Method parsePackageMethod = clazz.getMethod("parsePackage", File.class, String.class, DisplayMetrics.class, int.class);

				Object packageParser = clazz.getConstructor(String.class).newInstance("");
				Object packag = parsePackageMethod.invoke(packageParser, new File(apkPath), null, ApplicationEnvironment.getInstance().getApplication().getResources().getDisplayMetrics(), 0x0004);

				Method collectCertificatesMethod = clazz.getMethod("collectCertificates", Class.forName("android.content.pm.PackageParser$Package"), int.class);
				collectCertificatesMethod.invoke(packageParser, packag, PackageManager.GET_SIGNATURES);
				Signature mSignatures[] = (Signature[]) packag.getClass().getField("mSignatures").get(packag);

				Signature apkSignature = mSignatures.length > 0 ? mSignatures[0] : null;

				if(apkSignature != null) {
			        // 说明：没有提供md5的具体实现
					Log.e("APK Signature", apkSignature.toCharsString());
					
					Editor editor = sp.edit();
					apkmd5 = StringUtil.MD5Crypto(apkSignature.toCharsString());
					editor.putString(Constant.APKMD5VALUE, apkmd5);
					editor.commit();
					
					return apkmd5;
				}
				
				return "";

			}catch(Exception e){
				e.printStackTrace();
				return "";
			}
		}
		
	}

}
