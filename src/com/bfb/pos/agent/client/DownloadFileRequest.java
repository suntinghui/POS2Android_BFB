package com.bfb.pos.agent.client;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.ConnectException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.text.DecimalFormat;

import com.bfb.pos.activity.BaseActivity;
import com.bfb.pos.activity.view.LKAlertDialog;
import com.bfb.pos.activity.view.LKScheduleDialog;
import com.bfb.pos.util.FileUtil;

import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.DialogInterface.OnClickListener;
import android.os.AsyncTask;
import android.util.Log;

public class DownloadFileRequest {
	
	private static final int MB = 1024 * 1024;
	
	private static DownloadFileRequest request = null;
	
	private Context context;
	private String fileURL;
	private String fileName;
	
	public static DownloadFileRequest sharedInstance(){
		if (null == request){
			request = new DownloadFileRequest();
		}
		
		return request;
	}
	
	public void downloadAndOpen(Context context, String fileURL, String fileName){
		this.context = context;
		this.fileURL = fileURL;
		this.fileName = fileName;
		
		if (FileUtil.fileExists(fileName)){
			FileUtil.openFile(fileName);
		} else {
			
			if (!ApplicationEnvironment.getInstance().checkNetworkAvailable()){
				BaseActivity.getTopActivity().showToast("网络连接不可用，请稍候重试");
			} else {
				new DownloadFileTask().execute();
			}
			
		}
		
	}
	
	// 下载任务
	class DownloadFileTask extends AsyncTask<Object, Object, Object>{
		InputStream inStream = null;
		FileOutputStream outStream = null;
		HttpURLConnection conn = null;
		
		LKScheduleDialog dialog = null;
		
		int totalSize = 0;
		float totalMB = 0.0f;
		
		public DownloadFileTask(){
			dialog = new LKScheduleDialog(context);
			dialog.setTitle("正在下载文件");
			dialog.setCancelable(false);
			// 如果是按返回键消失的，要删除该文件，否则会造成未下载完成就去打开。
			dialog.setOnCancelListener(new OnCancelListener(){
				@Override
				public void onCancel(DialogInterface arg0) {
					FileUtil.deleteFile(fileName);
					conn.disconnect();
				}
			});
			
			dialog.setNegativeButton("取消下载", new OnClickListener(){
				@Override
				public void onClick(DialogInterface dialog, int arg1) {
					FileUtil.deleteFile(fileName);
					conn.disconnect();
				}
				
			});
			
			dialog.create().show();
		}
		
		@Override
		protected void onPreExecute() {
			
		}

		@Override
		protected Object doInBackground(Object... params) {
			
			try {
				URL url = new URL(fileURL);
				conn = (HttpURLConnection) url.openConnection();
				conn.setConnectTimeout(10000);
				conn.setReadTimeout(10000);
//				conn.setRequestProperty("Content-Type","application/x-www-form-urlencoded");
//				conn.setRequestMethod("POST");
//				conn.setDoOutput(true);
				HttpURLConnection.setFollowRedirects(true);
				
				conn.connect();
				
				inStream = conn.getInputStream();
				
				totalSize = conn.getContentLength(); // 获取响应文件的总大小
				
				// TODO exception totalSize = -1; stream == null, android UnknownlengthInputstream
				if (null == inStream || totalSize == -1){
					throw new ConnectException();
				}
				
				if (totalSize == 0){
					throw new IOException();
				}
				
				totalMB = format(totalSize);
				if (null != inStream){
					File file = new File(FileUtil.getDownloadPath(), fileName);
					outStream = new FileOutputStream(file);
					byte[] buffer = new byte[1024 * 20]; // 这个值设置太小，会导致频繁更新而卡死界面
					int downloadedSize = 0;
					
					int refreshCount = 0;
					int count = -1;  
			        while((count = inStream.read(buffer)) != -1) { 
			            outStream.write(buffer, 0, count);
			            downloadedSize += count;
						// 更新下载进度
			            refreshCount++;
			            if (refreshCount % 20 == 0){ // 避免频繁更新，每读取30次才刷新一次进度。
			            	this.publishProgress(downloadedSize);
			            }
			        }
			        
			        buffer = null;
				}
				
				return null;
				
			} catch (MalformedURLException e) {
				e.printStackTrace();
				return "文件下载失败";
				
			} catch(ConnectException e){
				e.printStackTrace();
				return "文件下载失败，有可能是网络异常或文件不存在。";
				
			} catch(SocketTimeoutException e){
				e.printStackTrace();
				return "连接服务器超时，请检查您的网络环境是否正常或稍候再试。";
				
			}  catch (IOException e) {
				e.printStackTrace();
				return "文件下载失败。";
				
			} catch (Exception e) {
				e.printStackTrace();
				return "下载失败，请稍候再试";
			} finally {
				try {
					conn.disconnect();
					inStream.close();
					outStream.close();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
		
		@Override
		protected void onProgressUpdate(Object... values) {
			int downloadedSize = (Integer) values[0];
			int schedule = downloadedSize * 100 / totalSize;
			StringBuffer sb = new StringBuffer();
			sb.append(format(downloadedSize)).append("M/").append(totalMB).append("M");
			
			dialog.setDetail(sb.toString());
			dialog.setProgress(schedule);
		}
		
		@Override
		protected void onPostExecute(Object result) {
			dialog.dismiss();
			
			if (null == result){
				Log.e("download", "下载完成，打开文件");
				
				FileUtil.openFile(fileName);
				
			} else {
				// 下载失败要删除已创建的缓存文件
				FileUtil.deleteFile(fileName);
				
				LKAlertDialog tempDialog = new LKAlertDialog(context);
				tempDialog.setTitle("提示");
				tempDialog.setMessage((String)result);
				tempDialog.setPositiveButton("确定", new android.content.DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
					}
				});
				
				tempDialog.create().show();
			}
		}
		
		private float format(int l){
			return Float.valueOf(new DecimalFormat("#.00").format(l*1.0f/MB));
		}
		
	}
	
}

