package com.bfb.pos.activity;

import java.util.ArrayList;
import java.util.EmptyStackException;
import java.util.Stack;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Gravity;
import android.view.Window;
import android.widget.Toast;

import com.bfb.pos.agent.client.AppDataCenter;
import com.bfb.pos.agent.client.HttpManager;
import com.bfb.pos.agent.client.SystemConfig;
import com.bfb.pos.agent.client.TransferLogic;
import com.bfb.pos.model.UserModel;
import com.bfb.pos.R;

public class BaseActivity extends Activity {
	
	private static Stack<BaseActivity> stack = new Stack<BaseActivity>();
	
	public static final int PROGRESS_DIALOG 	= 0; // 带滚动条的提示框 
	public static final int SIMPLE_DIALOG 		= 1; // 简单提示框，不带滚动条，需要手动调用hide方法取消
	public static final int MODAL_DIALOG		= 2; // 带确定按纽的提示框，需要用户干预才能消失
	public static final int NONMODAL_DIALOG 	= 3; // 2秒后自动消失的提示框
	public static final int COUNTUP_DIALOG 		= 4; // 正数计时带滚动条的提示框
	
	
	private ProgressDialog progressDialog = null;
	private AlertDialog.Builder simpleDialog = null;
	private AlertDialog.Builder modalDialog = null;
	private AlertDialog.Builder nonModalDialog = null;
	private ProgressDialog countUpDialog = null;
	
	private AlertDialog tempSimpleDialog = null; // for close 'SimpleDialog'
	private AlertDialog tempModalDialog = null; // for close 'ModalDialog'
	private AlertDialog tempNonModalDialog = null; // for close 'NonModalDialog'
	
	private String message = null;
	
	// for countUp
	private String transferCode = null;
	private CountUpTask countUpTask = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		
		// 更新超时时间
		TimeoutService.LastSystemTimeMillis = System.currentTimeMillis();
		
		stack.push(this);
	}

	@Override
	protected void onNewIntent(Intent intent) {
		super.onNewIntent(intent);
		
		//stack.push(this);
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode == Activity.RESULT_OK){
			this.setResult(Activity.RESULT_OK);
			this.finish();
		}
	}
	
	@Override
	public void startActivity(Intent intent) {
		super.startActivity(intent);
		// 然后会调用 startActivityForResult();
	}

	@Override
	public void startActivityForResult(Intent intent, int requestCode) {
		super.startActivityForResult(intent, requestCode);
		
		// 保证跳转页面后本页面没有弹出窗
		this.hideDialog(COUNTUP_DIALOG);
		this.hideDialog(MODAL_DIALOG);
		this.hideDialog(NONMODAL_DIALOG);
		this.hideDialog(PROGRESS_DIALOG);
		this.hideDialog(SIMPLE_DIALOG);
	}

	@Override
	public void finish() {
		synchronized(this) {
			if (!stack.empty()){
				stack.pop();
			}
		}
		
		super.finish();
	}
	
	public static BaseActivity getTopActivity(){
		// TODO 应该怎么处理？
		try{
			return stack.peek();
		} catch (EmptyStackException e){
			return stack.peek();
		}
	}
	
	public static ArrayList<BaseActivity> getAllActiveActivity() {
		if (null == stack || stack.isEmpty()){
			return null;
		}
		
		ArrayList<BaseActivity> list = new ArrayList<BaseActivity>();
		for (int i=0; i<stack.size(); i++){
			list.add(stack.get(i));
		}
		
		return list;
	}
	
	// 根据类型弹出不同的等待提示框
	public void showDialog(String message, String transferCode){
		this.message = message;
		this.transferCode = transferCode;
		
		this.runOnUiThread(new Runnable(){
			@Override
			public void run() {
				showDialog(COUNTUP_DIALOG);
			}
			
		});
		
	}
	
	public void showDialog(final int type, String message){
		this.message = message;
		
		this.runOnUiThread(new Runnable(){
			@Override
			public void run() {
				showDialog(type);
			}
		});
	}
	
	@Override
	protected Dialog onCreateDialog(int id) {
		switch(id){
		
		case PROGRESS_DIALOG:
			// 这里应该关闭其它提示型的对话框
			this.hideDialog(SIMPLE_DIALOG);
			this.hideDialog(NONMODAL_DIALOG);
			this.hideDialog(COUNTUP_DIALOG);
			this.hideDialog(MODAL_DIALOG);
			
			this.createDefaultDialog();
			progressDialog.setMessage(null==message?"":message);
			progressDialog.show();
			break;
			
		case SIMPLE_DIALOG:
			// 这里应该关闭其它提示型的对话框
			this.hideDialog(PROGRESS_DIALOG);
			this.hideDialog(NONMODAL_DIALOG);
			this.hideDialog(COUNTUP_DIALOG);
			this.hideDialog(MODAL_DIALOG);
			
			this.createSimpleDialog();
			simpleDialog.setMessage(null==message?"":message);
			tempSimpleDialog = simpleDialog.show();
			break;
			
		case MODAL_DIALOG:
			// 这里应该关闭其它提示型的对话框
			this.hideDialog(PROGRESS_DIALOG);
			this.hideDialog(SIMPLE_DIALOG);
			this.hideDialog(NONMODAL_DIALOG);
			this.hideDialog(COUNTUP_DIALOG);
			this.hideDialog(MODAL_DIALOG);
			
			this.createModalDialog();
			modalDialog.setMessage(null == message?"":message);
			tempModalDialog = modalDialog.show();
			break;
			
		case NONMODAL_DIALOG:
			// 这里应该关闭其它提示型的对话框
			this.hideDialog(PROGRESS_DIALOG);
			this.hideDialog(SIMPLE_DIALOG);
			this.hideDialog(COUNTUP_DIALOG);
			this.hideDialog(MODAL_DIALOG);
			
			this.createNonModalDialog();
			nonModalDialog.setMessage(null == message?"请稍候":message);
			tempNonModalDialog = nonModalDialog.show();
			new NonModalTask().execute();
			break;
			
		case COUNTUP_DIALOG:
			// 这里应该关闭其它提示型的对话框
			// 不能由于弹出其他窗口而关闭，因为这涉及到交易。比如交易时拔出点付宝会弹出窗口，但是在交易时也不应该关闭。
			this.hideDialog(PROGRESS_DIALOG);
			this.hideDialog(SIMPLE_DIALOG);
			this.hideDialog(NONMODAL_DIALOG);
			this.hideDialog(MODAL_DIALOG);
			
			this.createCountUpDialog();
			countUpDialog.setMessage(null==message?this.getResources().getString(R.string.pWaitAMonment):message);
			countUpDialog.show();
			
			countUpTask = new CountUpTask();
			countUpTask.execute();
			
			break;
		}
		
		return super.onCreateDialog(id);
	}
	
	private void createDefaultDialog(){
		if (null == progressDialog){
			progressDialog = new ProgressDialog(this);
			progressDialog.setIndeterminate(true);
			progressDialog.setCancelable(false);
		}
	}
	
	private void createSimpleDialog(){
		if (null == simpleDialog){
			simpleDialog = new AlertDialog.Builder(this);
			simpleDialog.setTitle("提示");
			simpleDialog.setCancelable(false);
		}
	}
	
	private void createModalDialog(){
		if (null == modalDialog){
			modalDialog = new Builder(this);
			modalDialog.setTitle("提示");
			modalDialog.setCancelable(false);
			modalDialog.setPositiveButton("确定", new android.content.DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int which) {
					tempModalDialog.dismiss();
					dialog.dismiss();
				}
			});
		}
	}
	
	private void createNonModalDialog(){
		if (null == nonModalDialog){
			nonModalDialog = new AlertDialog.Builder(this);
			nonModalDialog.setTitle("提示");
			nonModalDialog.setCancelable(false);
		}
	}
	
	private void createCountUpDialog(){
		if (null == countUpDialog){
			countUpDialog = new ProgressDialog(this);
			countUpDialog.setIndeterminate(true);
			countUpDialog.setCancelable(false);
		}
	}
	
	public void hideDialog(int type){
		switch(type){
		
		case PROGRESS_DIALOG:
			if (null != progressDialog && progressDialog.isShowing()){
				progressDialog.dismiss();
			}
			break;
			
		case SIMPLE_DIALOG:
			if (null != tempSimpleDialog && tempSimpleDialog.isShowing()){
				tempSimpleDialog.dismiss();
			}
			break;
			
		case NONMODAL_DIALOG:
			if (null != tempNonModalDialog && tempNonModalDialog.isShowing()){
				tempNonModalDialog.dismiss();
			}
			break;
			
		case MODAL_DIALOG: // 一般不要关闭模式对话框，而应该由用户去触发 
			
			break;
			
		case COUNTUP_DIALOG:
			if (null != countUpTask){
				countUpTask.cancel(true);
			}
			
			if (null != countUpDialog && countUpDialog.isShowing()){
				countUpDialog.dismiss();
			}
			break;
		}
	}
	
	class NonModalTask extends AsyncTask<Object, Object, Object>{

		@Override
		protected Object doInBackground(Object... arg0) {
			
			try {
				Thread.sleep(2000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			
			return null;
		}
		
		@Override
		protected void onPostExecute(Object result) {
			if (null != tempNonModalDialog && tempNonModalDialog.isShowing()){
				tempNonModalDialog.dismiss();
			}
		}
		
	}
	
	final class CountUpTask extends AsyncTask<Object, Object, Object>{
		
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
		}

		@Override
		protected Object doInBackground(Object... arg0) {
			int i=1;
			while(i <= SystemConfig.getMaxTransferTimeout()) {
				
				this.publishProgress(i++);
				
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
			
			return null;
		}
		
		@Override
		protected void onProgressUpdate(Object... values) {
			if (null != countUpDialog && countUpDialog.isShowing()){
				countUpDialog.setMessage(message+" "+ (Integer)values[0] +"s");
			}
		}
		
		@Override
		protected void onPostExecute(Object result) {
			if (null != countUpDialog && countUpDialog.isShowing()){
				countUpDialog.dismiss();
				
				if(null != transferCode){
					if (AppDataCenter.getReversalMap().containsKey(transferCode)){
						TransferLogic.getInstance().gotoCommonFaileActivity("交易超时");
						TransferLogic.getInstance().reversalAction();
					} else if (AppDataCenter.getReversalMap().containsValue(transferCode)){
						TransferLogic.getInstance().gotoCommonFaileActivity("冲正超时");
					} else {
						TransferLogic.getInstance().gotoCommonFaileActivity("交易超时，请重试");
					}
				}
			}
		}

		@Override
		protected void onCancelled() {
			HttpManager.getInstance().abort();
			super.onCancelled();
		}
		
	}

	public void showToast(String message){
		Toast toast = Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT);
		toast.setGravity(Gravity.CENTER, 0, 0);
		toast.show();
	}
	
	//更新短信计时器
	public void refreshSMSBtn(){
		
	}
	
	public void fromLogic(UserModel model){
		
	}
}
