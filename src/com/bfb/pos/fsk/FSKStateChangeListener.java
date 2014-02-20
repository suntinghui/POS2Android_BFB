package com.bfb.pos.fsk;

import android.util.Log;

import com.bfb.pos.activity.BaseActivity;
import com.itron.protol.android.CommandStateChangedListener;

public class FSKStateChangeListener implements CommandStateChangedListener {

	@Override
	public void OnCheckCRCErr() {
		Log.e("状态监听", "接收数据校验和错误");

	}

	@Override
	public void OnConnectErr() {
		Log.e("状态监听", "OnConnectErr。。。");
	}

	@Override
	public void OnDevicePlug() {
		Log.e("状态监听", "设备插入");
		// BaseActivity.getTopActivity().showDialog(BaseActivity.NONMODAL_DIALOG, "点付宝已与手机连接");
	}

	@Override
	public void OnDevicePresent() {
		Log.e("状态监听", "设备检测正常。（设备在，并且通讯正常）");
	}

	@Override
	public void OnDeviceUnPlug() {
		Log.e("状态监听", "设备未插入");
		BaseActivity.getTopActivity().showDialog(BaseActivity.NONMODAL_DIALOG, "刷卡设备已从手机中拔出");
	}

	@Override
	public void OnDeviceUnPresent() {
		Log.e("状态监听", "设备检测错误。");
		// BaseActivity.getTopActivity().showDialog(BaseActivity.MODAL_DIALOG, "点付宝检测错误，请检查点付宝是否插入并开机或重新插拔点付宝");
	}

	@Override
	public void OnKeyError() {
		Log.e("状态监听", "校验密钥错误");
	}

	@Override
	public void OnNoAck() {
		Log.e("状态监听", "没有返回的应答");
	}

	@Override
	public void OnPrinting() {
		Log.e("状态监听", "接收打印数据");
	}

	@Override 
	public void OnTimeout() {
		Log.e("状态监听", "接收数据超时");
		
		// 在这里处理用户长时间未操作可能造成的界面无法返回情况！！！
//		ApplicationEnvironment.getInstance().hideDialog(BaseActivity.PROGRESS_DIALOG);
//		BaseActivity.getTopActivity().showDialog(BaseActivity.NONMODAL_DIALOG, "接收数据超时，请重试");
	}

	@Override
	public void OnWaitingOper() {
		Log.e("状态监听", "等待用户操作");
		
		// 现在已知余额查询时竟然也会触发。
		//BaseActivity.getTopActivity().showDialog(BaseActivity.PROGRESS_DIALOG, "用户输入完成后请按[确认]键");
	}

	@Override
	public void OnWaitingPin() {
		Log.e("状态监听", "等待PIN输入");
		
		BaseActivity.getTopActivity().showDialog(BaseActivity.PROGRESS_DIALOG, "请用户输入密码并按[确认]键");
	}

	@Override
	public void OnWaitingcard() {
		Log.e("状态监听", "等待刷卡");
		
		BaseActivity.getTopActivity().showDialog(BaseActivity.PROGRESS_DIALOG, "请用户刷卡并按[确认]键");
	}

	@Override
	public void onGetCardNo(String arg0) {
		Log.e("状态监听", "onGetCardNo");
	}

	@Override
	public void onGetKsn(String arg0) {
		Log.e("状态监听", "onGetKsn");
	}

}
