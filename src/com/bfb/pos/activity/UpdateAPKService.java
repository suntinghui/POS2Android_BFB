package com.bfb.pos.activity;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.ConnectException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.text.DecimalFormat;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.os.IBinder;
import android.widget.RemoteViews;
import android.widget.Toast;

import com.bfb.pos.agent.client.AppDataCenter;
import com.bfb.pos.agent.client.ApplicationEnvironment;
import com.bfb.pos.agent.client.Constant;
import com.bfb.pos.dynamic.component.ViewException;
import com.bfb.pos.dynamic.core.Event;
import com.bfb.pos.R;

// Service 补课：
// http://zy77612.iteye.com/blog/1292649
// http://blog.csdn.net/by317966834/article/details/7591502
// http://www.cnblogs.com/newcj/archive/2011/03/14/1983782.html
	
public class UpdateAPKService extends Service {
	
	private static final int NOTIFICATION_TAG = 100;
	
	private static String newAPKName = "Pos2Android_new.apk";
	
	private static final int MB = 1024 * 1024;
	
	private DownloadAPKTask downloadTask = null;
	
	@Override
	public void onCreate(){
	}
	
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		if (null != downloadTask && downloadTask.getStatus()==AsyncTask.Status.RUNNING){
			Toast.makeText(this, this.getResources().getString(R.string.downloadingNewAPK), Toast.LENGTH_SHORT).show();
			
		} else {
			if (null != intent){
				// 响应
				if (null != intent.getStringExtra("flag")){
					int serverVersonCode = Integer.parseInt(intent.getStringExtra("serverVersionCode"));
					
					if (serverVersonCode <= Integer.parseInt(AppDataCenter.getValue("__VERSIONCODE"))){
						
						AlertDialog.Builder builder = new Builder(BaseActivity.getTopActivity());
						builder.setTitle("提示");
						builder.setMessage("已经是最新版本");
						builder.setNegativeButton("确定", new android.content.DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int which) {
								stopSelf();
								dialog.dismiss();
							}
						});
						
						builder.show();
						
					} else {
						newAPKName = intent.getStringExtra("apkName");
						
						AlertDialog.Builder builder = new Builder(BaseActivity.getTopActivity());
						builder.setTitle("提示");
						builder.setMessage(R.string.canUpdate);
						builder.setPositiveButton(R.string.downloadAPK, new android.content.DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int which) {
								if (!checkSDCard()){
									Toast.makeText(BaseActivity.getTopActivity(), UpdateAPKService.this.getResources().getString(R.string.noSDCard), Toast.LENGTH_SHORT).show();
									return;
								}
								
								downloadTask = new DownloadAPKTask();
								downloadTask.execute();
							}
						});
						builder.setNegativeButton("取消", new android.content.DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int which) {
								stopSelf();
								dialog.dismiss();
							}
						});
						
						builder.show();
					}
					
				} else {
					// 请求
					Event event = new Event(null,"checkupdate", null);
			        event.setTransfer("999000002");
			        try {
						event.trigger();
					} catch (ViewException e) {
						e.printStackTrace();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
		}
		
		return START_NOT_STICKY;
	}

	@Override
	public IBinder onBind(Intent arg0) {
		return null;
	}
	
	/**
	 * 检查是否存在SD卡，如果不存在，则返回false，不能下载更新
	 * 
	 * @return
	 */
	private boolean checkSDCard(){
		/*
		   MEDIA_BAD_REMOVAL:表明SDCard 被卸载前己被移除 
		   MEDIA_CHECKING:表明对象正在磁盘检查 
		   MEDIA_MOUNTED:表明sd对象是存在并具有读/写权限 
		   MEDIA_MOUNTED_READ_ONLY:表明对象权限为只读 
		   MEDIA_NOFS:表明对象为空白或正在使用不受支持的文件系统 
		   MEDIA_REMOVED:如果不存在 SDCard 返回 
		   MEDIA_SHARED:如果 SDCard 未安装 ，并通过 USB 大容量存储共享 返回 
		   MEDIA_UNMOUNTABLE:返回 SDCard 不可被安装 如果 SDCard 是存在但不可以被安装 
		   MEDIA_UNMOUNTED:返回 SDCard 已卸掉如果 SDCard 是存在但是没有被安装
		 */
		return Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED);
	}
	
	class DownloadAPKTask extends AsyncTask<Object, Object, Object>{
		private Notification notification = null;
		private NotificationManager manager = null;
		
		int totalSize = 0;
		float totalMB = 0.0f;
		
		@Override
		protected void onPreExecute() {
			// 注意，如果使用了contentView，那么便不要使用Notification.setLatestEventInfo。后面的设置会替换前面的设置
			notification = new Notification(R.drawable.ic_launcher, UpdateAPKService.this.getResources().getString(R.string.startDownload), System.currentTimeMillis());
	        notification.contentView = new RemoteViews(ApplicationEnvironment.getInstance().getApplication().getPackageName(), R.layout.notify_updateapk);
	        notification.contentView.setProgressBar(R.id.progressBar, 100, 0, false);
	        notification.contentView.setTextViewText(R.id.textView, "进度 0%");
	        notification.flags = Notification.FLAG_ONGOING_EVENT|Notification.FLAG_NO_CLEAR;
	        // 在没有下完之前用户点击，什么也不做，因为有可能程序已经退出，就不弄那么麻烦了。但是必须要设置该值，否则在有的手机上会崩溃
	        // java.lang.IllegalArgumentException: contentIntent required: pkg=com.bfb.pos id=100 notification=Notification(vibrate=null,sound=null,defaults=0x0,flags=0x22)
	        PendingIntent pendingIntent = PendingIntent.getActivity(UpdateAPKService.this, 0, new Intent(), 0);
	        notification.contentIntent = pendingIntent;
	        
	        manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
	        manager.notify(NOTIFICATION_TAG, notification);
		}

		@Override
		protected Object doInBackground(Object... arg0) {
			try {
				URL url = new URL(Constant.DOWNLOADAPKURL + newAPKName);
				URLConnection conn = url.openConnection();
				conn.connect();
				InputStream stream = conn.getInputStream();
				totalSize = conn.getContentLength(); // 获取响应文件的总大小
				totalMB = format(totalSize);
				// TODO exception totalSize <= 0; stream == null
				if (null != stream){
					File file = new File(Environment.getExternalStorageDirectory(), newAPKName);
					FileOutputStream outStream = new FileOutputStream(file);
					byte[] buffer = new byte[1024 * 20]; // 这个值设置太小，会导致频繁更新而卡死界面
					int downloadedSize = 0;
					
					int refreshCount = 0;
					int count = -1;  
			        while((count = stream.read(buffer)) != -1) { 
			            outStream.write(buffer, 0, count);
			            downloadedSize += count;
						// 更新下载进度
			            refreshCount++;
			            if (refreshCount % 30 == 0){ // 避免频繁更新，每读取30次才刷新一次进度。
			            	this.publishProgress(downloadedSize);
			            }
			        }
			        
			        buffer = null;
				}
				
				return null;
				
			} catch (MalformedURLException e) {
				e.printStackTrace();
				return "下载失败";
			} catch(ConnectException e){
				e.printStackTrace();
				return "无法链接服务器，请确认您已开启网络或稍候再试";
			} catch (Exception e) {
				e.printStackTrace();
				return "下载失败，请稍候再试";
			}
		}

		@Override
		protected void onProgressUpdate(Object... values) {
			int downloadedSize = (Integer) values[0];
			int schedule = downloadedSize * 100 / totalSize;
			StringBuffer sb = new StringBuffer();
			sb.append(schedule).append("%").append(" (").append(format(downloadedSize)).append("m/").append(totalMB).append("m)");
			
			notification.contentView.setTextViewText(R.id.textView, sb.toString());
			notification.contentView.setProgressBar(R.id.progressBar, 100, schedule, false);
			
			manager.notify(NOTIFICATION_TAG, notification);
		}
		
		private float format(int l){
			return Float.valueOf(new DecimalFormat("#.00").format(l*1.0f/MB));
		}
		
		@Override
		protected void onPostExecute(Object result) {
			try{
				if (null == result){
					notification.tickerText = UpdateAPKService.this.getResources().getString(R.string.downloadDone);
					notification.when = System.currentTimeMillis();
					
					Intent intent = new Intent(Intent.ACTION_VIEW);
					intent.setDataAndType(Uri.fromFile(new File(Environment.getExternalStorageDirectory(), newAPKName)),"application/vnd.android.package-archive");
					PendingIntent pendingIntent = PendingIntent.getActivity(UpdateAPKService.this, 0, intent, 0);
					notification.contentIntent = pendingIntent;
					notification.setLatestEventInfo(UpdateAPKService.this, UpdateAPKService.this.getResources().getString(R.string.app_name), UpdateAPKService.this.getResources().getString(R.string.downloadDone), pendingIntent);
					notification.flags = Notification.FLAG_AUTO_CANCEL;
					notification.defaults = Notification.DEFAULT_SOUND;
					manager.notify(NOTIFICATION_TAG, notification);
					
				} else {
					// 清除notification
					manager.cancel(NOTIFICATION_TAG);
					
					AlertDialog.Builder builder = new Builder(BaseActivity.getTopActivity());
					builder.setTitle("提示");
					builder.setMessage((String)result);
					builder.setPositiveButton("确定", new android.content.DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int which) {
							dialog.dismiss();
						}
					});
					
					builder.show();
				}
			} finally{
				stopSelf();
			}
		}
		
	}

}
