package com.bfb.pos.agent.client;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;

import com.bfb.pos.activity.BaseActivity;

import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.util.Log;

public class UpdateClient {

	// assets下共有5个文件夹，fonts文件夹仍然放在程序中
	private static final String[] dirArray = new String[]{"config","data","template","view"};
	
	// 将程序Assets包下的所有的文件copy到手机中
	public static void copyAssetsToMobile(){
		for (String dir : dirArray){
			copyAssetsDir(dir, Constant.ASSETSPATH);
		}
	}
	
	private static void copyAssetsDir(String assetDir, String dir) {
		String[] files;
		try {
			files = ApplicationEnvironment.getInstance().getApplication().getResources().getAssets().list(assetDir);
		} catch (IOException e1) {
			return;
		}
		
		File mWorkingPath = new File(dir);
		// if this directory does not exists, make one.
		if (!mWorkingPath.exists()) {
			if (!mWorkingPath. mkdirs()) {

			}
		}

		for (int i = 0; i < files.length; i++) {
			try {
				String fileName = files[i];
				// we make sure file name not contains '.' to be a folder.
				if (!fileName.contains(".")) {
					if (0 == assetDir.length()) {
						copyAssetsDir(fileName, dir + fileName + "/");
					} else {
						copyAssetsDir(assetDir + "/" + fileName, dir + fileName+ "/");
					}
					continue;
				}
				
				File outFile = new File(mWorkingPath, fileName);
				if (outFile.exists()){
					Log.e("Copy Assets",fileName+"文件已经存在，其将要被替换，请检查！！！");
					outFile.delete();
				}
				
				InputStream in = null;
				if (0 != assetDir.length())
					in = ApplicationEnvironment.getInstance().getApplication().getAssets().open(assetDir + "/" + fileName);
				else
					in = ApplicationEnvironment.getInstance().getApplication().getAssets().open(fileName);

				OutputStream out = new FileOutputStream(outFile);

				// Transfer bytes from in to out
				byte[] buf = new byte[1024];
				int len;
				while ((len = in.read(buf)) > 0) {
					out.write(buf, 0, len);
				}

				in.close();
				out.close();
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	/**
	 *  更新文件
	 * @param currentVer 当前版本
	 * @param versionStr 服务器版本
	 * @param fileNames 要更新的文件列表
	 * @return 返回true说明更新文件成功
	 */
	public static boolean updateFiles(int currentVer, String versionStr, String fileNames){
		if(null != versionStr && versionStr.matches("\\d+")){
			int newVersion = Integer.parseInt(versionStr);
			if (currentVer < newVersion) { // 版本号不匹配，进行更新文件
				String versionPreferenceKey = "Version"+currentVer+"To"+newVersion; // 规定的KEY值
				SharedPreferences pre = ApplicationEnvironment.getInstance().getPreferences();
				
				String surplusFiles = ""; 
				// 上次有未更新完成的文件
				if(pre.contains(versionPreferenceKey)){
					surplusFiles = pre.getString(versionPreferenceKey, "");
				} else {
					surplusFiles = fileNames;
				}
				
				Editor editor = pre.edit();
				// 存储还未完成的文件名
				ArrayList<String> tempList = new ArrayList<String>();
				
				try{
					if (null != surplusFiles) {
						String[] fileArray = surplusFiles.replace(" ", "").split("\\|");
						ArrayList<String> list =  new ArrayList<String>();
						for (String fileName : fileArray){
							list.add(fileName);
							tempList.add(fileName);
						}
						
						for (Iterator<String> iterator=list.listIterator(); iterator.hasNext();){
							//BaseActivity.getTopActivity().showDialog(BaseActivity.PROGRESS_DIALOG, "正在更新文件");
							// 下载文件获得InputStream
							String fileName = iterator.next();
							InputStream is = UpdateClient.downloadFile(fileName);
							if (null != is){
								boolean flag = UpdateClient.saveFile(fileName, is);
								
								if (flag){
									// 一个文件更新成功，则更新Preferences
									tempList.remove(fileName);
								}else{
									// 如果有一个文件下载失败，则表示更新失败，返回不再下载更新，让用户重新进行更新
									return false;
								}
							} else {
								return false;
							}
						}
						
						return true;
						
					} else {
						return false;
					}
					
				} catch(Exception e){
					e.printStackTrace();
					return false;
					
				} finally{
					if (tempList.size() == 0){
						if (pre.contains(versionPreferenceKey)){
							editor.remove(versionPreferenceKey);
						}
					} else {
						StringBuffer sb = new StringBuffer();
						for (Iterator<String> it=tempList.listIterator(); it.hasNext();){
							sb.append(it.next()).append("|");
						}
						sb.deleteCharAt(sb.length()-1);
						editor.putString(versionPreferenceKey, sb.toString());
					}
					editor.commit();
				}
				
			} else {
				// 不需要更新文件
				return true;
			}
			
		} else {
			return false;
		}
	}
	
	// 根据文件名拼出文件在服务器上的地址，并下载返回InputStream
	 public static InputStream downloadFile(String fileName){
		 BaseActivity.getTopActivity().showDialog(BaseActivity.PROGRESS_DIALOG, "正在下载更新文件...");
		 InputStream is = null;
		 try{
			 URL url = new URL(Constant.FILESURL + (fileName.endsWith(".xml")?fileName:(fileName+".xml"))); 
            HttpURLConnection conn = (HttpURLConnection)url.openConnection();
            conn.setDoInput(true);
            conn.connect(); 
            is=conn.getInputStream();
            return is;
		 }catch(Exception e){
			 e.printStackTrace();
			 return null;
		 }
	 }
	
	// 保存文件到手机中
	public static boolean saveFile(String fileName, InputStream is){
		try{
			boolean isExists = false;
			
			File mWorkingPath = new File(Constant.ASSETSPATH);
			File outFile = new File(mWorkingPath, fileName);
			if (outFile.exists()){
				isExists = true;
				outFile.delete();
			}
			OutputStream out = new FileOutputStream(outFile);
			// Transfer bytes from in to out
//			byte[] buf = new byte[is.available()];
			byte[] buf = new byte[1024];
			int len;
			while ((len = is.read(buf)) > 0) {
				out.write(buf, 0, len);
			}
			out.close();
			
			if (isExists){
				Log.d("UPDATE", fileName + "文件已经被删除替换！");
				
			}else{
				Log.d("UPDATE", fileName + "文件已经添加！");
				
			}
			return true;
		}catch(FileNotFoundException e){
			e.printStackTrace();
			
		}catch(IOException e){
			e.printStackTrace();
		}
		return false;
	}
	
}
