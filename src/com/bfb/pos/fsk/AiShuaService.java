package com.bfb.pos.fsk;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;

import com.bfb.pos.activity.BaseActivity;
import com.bfb.pos.agent.client.AppDataCenter;
import com.itron.cswiper4.CSwiper;

public class AiShuaService extends Service {

	private static final int MAX_EXECCOUNT = 2;
	private static int execCount = 0;

	// 爱刷
	private CSwiper aishuaCommandController = null;

	private String fskCommand = null;

	@Override
	public void onCreate() {
		super.onCreate();

		AiShuaStateChangedListener listener = new AiShuaStateChangedListener();
		aishuaCommandController = CSwiper.GetInstance(this, listener);
		aishuaCommandController.isDevicePresent();
	}

	@Override
	public IBinder onBind(Intent arg0) {
		return null;
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		fskCommand = intent.getStringExtra("FSKCOMMAND");

		if (null != fskCommand && !"".equals(fskCommand)) {
			AiShuaThread fskThread = new AiShuaThread();
			fskThread.start();
		}

		// 使用START_NOT_STICKY返回值时，如果在执行完onStartCommand后，服务被异常kill掉，系统不会自动重启该服务。
		return START_NOT_STICKY;
	}

	// 在2.0以后的版本如果重写了onStartCommand，那onStart将不会被调用，注：在2.0以前是没有onStartCommand方法
	@Override
	public void onStart(Intent intent, int startId) {
		super.onStart(intent, startId);
	}

	@Override
	public void onDestroy() {
		if (null != aishuaCommandController) {
			aishuaCommandController.deleteCSwiper();
			aishuaCommandController = null;
		}

		super.onDestroy();
	}

	class AiShuaThread extends Thread {
		@Override
		public void run() {
			doAction();
		}
	}

	private void doAction() {
		++execCount;

		try {
			String[] fsks = fskCommand.split("#");
			for (String aFsk : fsks) {
				final String[] fields = aFsk.split("\\|");
				if (fields.length == 2) {

					aishuaCommandController.registerServiceReceiver();

					String methodName = fields[0];
					if (methodName.equalsIgnoreCase("getKsn")) {
						String ksn = aishuaCommandController.getKSN();

						String newStr = new String();

						// get length of string
						int temp = ksn.length();
						if (temp % 2 == 0) {
							for (int i = 1; i <= (temp - 1); i += 2) {

								// rebuild string
								newStr += ksn.charAt(i);
							}
						} else {
							for (int i = 1; i <= (temp - 2); i += 2) {
								newStr += ksn.charAt(i);
							}
						}
						AppDataCenter.setKSN(newStr);
						Log.e("KSN", newStr);

					} else if (methodName.equalsIgnoreCase("swipeCard")) {
						aishuaCommandController.startCSwiper();

					}

				} else {
					BaseActivity.getTopActivity().hideDialog(BaseActivity.PROGRESS_DIALOG);
					Log.e("event method", "event property 'fsk' must be format: (methodName|argType:value,argType:vlaue...)");
					Log.e("event method", "or event property 'fsk' must be format: (methodName|null)");
					return;
				}
			}

			if (fsks[0].equals("getKsn|null")){
				BaseActivity.getTopActivity().hideDialog(BaseActivity.PROGRESS_DIALOG);
				Message message = new Message();
				message.what = 0; // 肯定是成功的
				message.setTarget(FSKOperator.fskHandler);
				message.sendToTarget();
			}

		} catch (Exception e) {
			e.printStackTrace();
			if (execCount < MAX_EXECCOUNT) {
				doAction();
			} else {
				BaseActivity.getTopActivity().showDialog(BaseActivity.MODAL_DIALOG, "刷卡设备操作失败，请重试");
			}

		} finally {
			execCount = 0;
		}
	}

}
