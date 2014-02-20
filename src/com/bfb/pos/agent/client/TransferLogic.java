package com.bfb.pos.agent.client;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONStringer;
import org.json.JSONTokener;
import org.kxml2.io.KXmlParser;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences.Editor;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.bfb.pos.activity.AnnouncementListActivity;
import com.bfb.pos.activity.AuthenticationInfoActivity;
import com.bfb.pos.activity.BankSearchActivity;
import com.bfb.pos.activity.BaseActivity;
import com.bfb.pos.activity.DrawMoneyActivity;
import com.bfb.pos.activity.FaileActivity;
import com.bfb.pos.activity.FindPwdModifyActivity;
import com.bfb.pos.activity.LRCatalogActivity;
import com.bfb.pos.activity.SettlementSuccessActivity;
import com.bfb.pos.activity.TimeoutService;
import com.bfb.pos.activity.TransferDetailListActivity;
import com.bfb.pos.agent.client.db.TransferSuccessDBHelper;
import com.bfb.pos.agent.client.db.UploadSignImageDBHelper;
import com.bfb.pos.dynamic.component.ViewException;
import com.bfb.pos.dynamic.core.Event;
import com.bfb.pos.dynamic.core.ViewPage;
import com.bfb.pos.fsk.FSKOperator;
import com.bfb.pos.model.AnnouncementModel;
import com.bfb.pos.model.BankModel;
import com.bfb.pos.model.FieldModel;
import com.bfb.pos.model.TransferDetailModel;
import com.bfb.pos.model.TransferModel;
import com.bfb.pos.model.TransferSuccessModel;
import com.bfb.pos.model.UserModel;
import com.bfb.pos.util.AssetsUtil;
import com.bfb.pos.util.PhoneUtil;
import com.bfb.pos.util.StringUtil;

public class TransferLogic {
	private String verndor = null;
	private String terid = null;
	private static final String GENERALTRANSFER = "GENERALTRANSFER";
	private static final String UPLOADSIGNIMAGETRANSFER = "UPLOADSIGNIMAGETRANSFER";

	public static TransferLogic instance = null;

	private HashMap<String, TransferPacketThread> transferMap = null;

	public static TransferLogic getInstance() {
		if (null == instance) {
			instance = new TransferLogic();
		}

		return instance;
	}

	public TransferLogic() {
		transferMap = new HashMap<String, TransferPacketThread>();
	}

	// 动态机制通过此方法执行相应的逻辑。
	public void transferAction(String transferCode, HashMap<String, String> map) {

		Handler transferHandler = new Handler() {
			@SuppressWarnings("unchecked")
			@Override
			public void handleMessage(Message msg) {
				// 只能处理且只用处理正确的消息
				switch (msg.what) {
				case 0:
					actionDone((HashMap<String, String>) msg.obj);
					break;
				}
			}
		};

		/** 进行逻辑处理 **/
		TransferPacketThread thread = new TransferPacketThread(transferCode, map, transferHandler);

		if (transferCode.equals("089014")) { // 签购单上传  500000001
			transferMap.put(TransferLogic.UPLOADSIGNIMAGETRANSFER, thread);
		} else {
			transferMap.put(TransferLogic.GENERALTRANSFER, thread);
		}

		thread.start();
	}

	private void actionDone(HashMap<String, String> fieldMap) {
		String transferCode = fieldMap.get("fieldTrancode");

		if ("089016".equals(transferCode)) { // 登录
			this.loginDone(fieldMap);
			
		} else if ("089000".equals(transferCode)) { // 交易流水
			this.QueryTransListDone(fieldMap);

		} else if ("089002".equals(transferCode)) { // 找回密码 验证用户信息
			this.findPwdDone(fieldMap);

		} else if ("089001".equals(transferCode)) { // 注册
			this.registrDone(fieldMap);

		} else if ("089007".equals(transferCode)) { // 获取提款银行账号
			this.getBankAccountDone(fieldMap);

		} else if ("089004".equals(transferCode)) { // 公告列表
			this.getAnnounceListDone(fieldMap);

		} else if ("089010".equals(transferCode)) { // 完善注册
			this.registImproveDone(fieldMap);

		} else if ("089011".equals(transferCode)) { // 获取支行
			this.getBranchBankDone(fieldMap);
			
		} else if ("089012".equals(transferCode)) { // 搜索支行
			this.getBranchBankDone(fieldMap);

		} else if ("089013".equals(transferCode)) { // 获取商户注册信息
			this.getMerchantInfoDone(fieldMap);

		} else if ("089015".equals(transferCode)) { // 设置新密码 登录
			this.getSetNewPwdDone(fieldMap);

		} else if ("089017".equals(transferCode)) { // 设置新密码 支付
			this.getSetNewPwdDone(fieldMap);

		} else if ("089018".equals(transferCode)) { // 版本号
			this.getVersionDone(fieldMap);

		} else if ("089020".equals(transferCode)) { // 实名认证
			this.authenticationDone(fieldMap);

		} else if ("089003".equals(transferCode)) { // 修改密码
			this.modifyPwdDone(fieldMap);

		} else if ("089006".equals(transferCode)) { // 短信验证码
			this.getSmsDone(fieldMap);

		} else if ("089008".equals(transferCode)) { // 新增提款银行账号
			this.addBankAccountDone(fieldMap);

		} else if ("089009".equals(transferCode)) { // 修改提款银行账号
			this.modifyBankAccountDone(fieldMap);

		} else if ("100005".equals(transferCode)) { // 检验商户密码 登陆
			this.loginDone(fieldMap);

		} else if ("020001".equals(transferCode)) { // 银行卡余额查询
			this.queryBalanceDone(fieldMap);

		} else if("086000".equals(transferCode)){ //签到
			this.signDone(fieldMap);
			
		} else if ("020022".equals(transferCode)) { // 收款
			this.receiveTransDone(fieldMap);

		} else if ("020023".equals(transferCode)) { // 收款撤销
			this.revokeTransDone(fieldMap);

		} else if (AppDataCenter.getReversalMap().containsValue(transferCode)) { // 冲正
			gotoCommonSuccessActivity(fieldMap.get("fieldMessage"));

		} else if ("200001111".equals(transferCode)) { // 银行卡付款
			this.bankTransferDone(fieldMap);

		} else if ("089014".equals(transferCode)) { // 签购单上传  500000001
			this.uploadReceiptDone(fieldMap);

		} else if ("600000001".equals(transferCode)) {
			this.queryHistoryGroupDone(fieldMap);

		} else if ("600000002".equals(transferCode)) {
			this.queryHistoryListDone(fieldMap);

		} else if ("999000002".equals(transferCode)) {
			this.checkUpdateAPK(fieldMap);

		} else if ("999000003".equals(transferCode)) {
			this.downloadSecurityCode(fieldMap);

		} else {
			gotoCommonSuccessActivity(fieldMap.get("fieldMessage"));
		}
	}
	/**
	 * 登陆
	 */
	@SuppressLint("CommitPrefEdits")
	private void loginDone(HashMap<String, String> fieldMap) {
		Editor editor = ApplicationEnvironment.getInstance().getPreferences().edit();
		if ("1".equals(fieldMap.get("respmsg"))) {
			
			String jsonStr = fieldMap.get("apires");
			HashMap<String, String> receiveFieldMap = new HashMap<String, String>();

			try {
				JSONTokener parse = new JSONTokener(jsonStr);
				JSONObject content = (JSONObject) parse.nextValue();

				@SuppressWarnings("unchecked")
				Iterator<String> keys = content.keys();
				while (keys.hasNext()) {
					String key = (String) keys.next();
					receiveFieldMap.put(key, content.getString(key));

				}
			} catch (JSONException e) {
				e.printStackTrace();
			}
			editor.putString(Constant.MD5KEY, receiveFieldMap.get("md5key"));
			editor.commit();
			Constant.status = receiveFieldMap.get("status");
			// 登陆成功，跳转到菜单界面
			BaseActivity.getTopActivity().startActivity(new Intent("com.bfb.pos.lrcatalog"));
			BaseActivity.getTopActivity().finish();

		} else if ("0".equals(fieldMap.get("respmsg"))) {
			TransferLogic.getInstance().gotoCommonFaileActivity("登录失败");
		}
	}
		
	/**
	 * 登陆和签到
	 */
	private void loginSignDone(HashMap<String, String> fieldMap) {
		Editor editor = ApplicationEnvironment.getInstance().getPreferences().edit();

		try {

			if (null != fieldMap) {

				// // 先判断公钥是否需要更新
				// if (fieldMap.get("keyFlag").equals("1")){ // 0-不需更新 1-需要更新
				// // 更新公钥信息
				// String[] keys = parsePublickey(fieldMap.get("field62"));
				//
				// editor.putString(Constant.PUBLICKEY_MOD, keys[0]);
				// editor.putString(Constant.PUBLICKEY_EXP, keys[1]);
				// editor.putString(Constant.PUBLICKEY_VERSION,
				// fieldMap.get("fieldnewVersion"));
				// editor.putString(Constant.PUBLICKEY_TYPE,
				// fieldMap.get("publicKeyType"));
				//
				// this.gotoCommonFaileActivity("由于安全密钥已更新，请您重新登陆");
				// return;
				// }

				// 保存商户名称
				if (fieldMap.containsKey("field43")) {
					editor.putString(Constant.MERCHERNAME, fieldMap.get("filed43"));
				}
				if (fieldMap.containsKey("field41")) {
					AppDataCenter.setField41(fieldMap.get("field41"));
				}
				if (fieldMap.containsKey("field42")) {
					AppDataCenter.setField42(fieldMap.get("field42"));
				}

				// 启动超时退出服务
				Intent intent = new Intent(BaseActivity.getTopActivity(), TimeoutService.class);
				BaseActivity.getTopActivity().startService(intent);

				// 根据62域字符串切割得到工作密钥
				String newKey = fieldMap.get("field62").replace(" ", "");
				String pinKey = null;
				String macKey = null;
				String stackKey = null;

				try {
					if (null != newKey && !"".equals(newKey)) {

						// 标准
						pinKey = newKey.substring(0, 40);
						macKey = newKey.substring(40, 56) + newKey.substring(72);
//						macKey = newKey.substring(40, 80);
						stackKey = pinKey;

					} else {
						return;
					}
				} catch (Exception e) {
					e.printStackTrace();
				}

				// 保存工作密钥
				Handler handler = new Handler() {
					@Override
					public void handleMessage(Message msg) {
						switch (msg.what) {
						case 0:
							// 登陆成功，跳转到菜单界面
							BaseActivity.getTopActivity().showDialog(BaseActivity.PROGRESS_DIALOG, "登录成功");
							BaseActivity.getTopActivity().startActivity(new Intent("com.bfb.pos.lrcatalog"));
							BaseActivity.getTopActivity().finish();
							break;
						}
					}

				};

				StringBuffer sb = new StringBuffer();
				sb.append("Get_RenewKey|string:").append(pinKey).append(",string:").append(macKey).append(",string:").append(stackKey);
				FSKOperator.execute(sb.toString(), handler);

			} else {
				this.gotoCommonFaileActivity("服务器返回异常");
			}

		} catch (Exception e) {
			this.gotoCommonFaileActivity("服务器返回异常");
			e.printStackTrace();
		} finally {
			editor.commit();
		}

	}

	private void setVendorTerId(String vendor, String terid) {
        Event event = new Event(null, "login", null);
        String fsk = String.format("Get_RenewVendorTerID|string:%s,string:%s", vendor,terid);  
        event.setFsk(fsk);
        try {
                event.trigger();
        } catch (ViewException e) {
                e.printStackTrace();
        } catch (IOException e) {
                e.printStackTrace();
        }
}
	/**
	 * 签到
	 * 
	 * 当点击签到按纽后并从服务器返回JSON后执行此方法。
	 * 
	 * 执行此方法说明服务器端签到正常，没有其他的异常情况发生。 签到成功后 1、首先要更新工作密钥。按长度分别切割 2、然后将收款撤销表清空。
	 */
	private void signDone(final HashMap<String, String> fieldMap) {
		// 更新批次号
		String batchNum = fieldMap.get("field60").replace(" ", "").substring(2, 8); // 60域不带长度信息
		verndor = fieldMap.get("field42");
		terid = fieldMap.get("field41");
		
		AppDataCenter.setBatchNum(batchNum);

		// 清空上一个批次的交易成功的信息，即用于消费撤销和查询签购单的数据库表
		TransferSuccessDBHelper helper = new TransferSuccessDBHelper();
		if (helper.deleteTransfers()) {
			Log.e("debug", "更换批次后 成功 清空需清除的成功交易！");
		} else {
			Log.e("debug", "更换批次后清空需清除的成功交易 失败 ！");
		}

		// 根据62域字符串切割得到工作密钥
		String newKey = fieldMap.get("field62").replace(" ", "");
		String pinKey = null;
		String macKey = null;
		String stackKey = null;

		try {
			if (null != newKey && !"".equals(newKey)) {

				// 标准
				pinKey = newKey.substring(0, 40);
				macKey = newKey.substring(40, 56) + newKey.substring(72);
				stackKey = pinKey;

			} else {
				return;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		// 保存工作密钥
		Handler handler = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				switch (msg.what) {
				case 0:
					String tips = "签到成功\n\n[设备已成功更新工作密钥]";
					Constant.isSign = true;
					if(Constant.isAISHUA){
						tips = "签到成功！";
						AppDataCenter.setVENDOR(verndor);
						AppDataCenter.setTERID(terid);
					}else{
						setVendorTerId(verndor, terid);
					}
					TransferLogic.getInstance().gotoCommonSuccessActivity(tips);
					break;
				}
			}

		};
		if(Constant.isAISHUA){
			Constant.mackey = newKey.substring(40, 56);
			Constant.pinkey = newKey.substring(0, 32);
			String tips = "签到成功\n\n[设备已成功更新工作密钥]";
			Constant.isSign = true;
			if(Constant.isAISHUA){
				tips = "签到成功！";
				AppDataCenter.setVENDOR(verndor);
				AppDataCenter.setTERID(terid);
			}else{
				setVendorTerId(verndor, terid);
			}
			TransferLogic.getInstance().gotoCommonSuccessActivity(tips);
		}else{
			StringBuffer sb = new StringBuffer();
			sb.append("Get_RenewKey|string:").append(pinKey).append(",string:").append(macKey).append(",string:").append(stackKey);
			FSKOperator.execute(sb.toString(), handler);			
		}
			
	}

	/**
	 * 结算
	 * 
	 */
	private void settlementDone(HashMap<String, String> fieldMap) {
		try {
			String field48 = fieldMap.get("field48");
			String debitAmount = field48.substring(0, 12);
			String debitCount = field48.substring(12, 15);
			String creditAmount = field48.substring(15, 27);
			String creditCount = field48.substring(27, 30);

			HashMap<String, String> tempMap = new HashMap<String, String>();
			tempMap.put("fieldMessage", fieldMap.get("fieldMessage"));
			tempMap.put("debitAmount", StringUtil.String2SymbolAmount(debitAmount));
			tempMap.put("debitCount", debitCount);
			tempMap.put("creditAmount", StringUtil.String2SymbolAmount(creditAmount));
			tempMap.put("creditCount", creditCount);

			Intent intent = new Intent(BaseActivity.getTopActivity(), SettlementSuccessActivity.class);
			intent.putExtra("map", tempMap);
			BaseActivity.getTopActivity().startActivityForResult(intent, 0);

		} catch (Exception e) {
			this.gotoCommonFaileActivity("结算统计失败，请重试");
		}

	}

	/**
	 * 签退
	 */
	private void signOffDone(HashMap<String, String> fieldMap) {
		gotoCommonSuccessActivity(fieldMap.get("fieldMessage"));
	}

	/**
	 * 注册
	 */
	private void registrDone(HashMap<String, String> fieldMap) {

		if (fieldMap.containsKey("respmsg")) {
			if ("1".equals(fieldMap.get("respmsg"))) {
				gotoCommonSuccessActivity("注册成功");
			} else if ("0".equals(fieldMap.get("respmsg"))) {
				TransferLogic.getInstance().gotoCommonFaileActivity("注册失败");
			} else if ("2".equals(fieldMap.get("respmsg"))) {
				TransferLogic.getInstance().gotoCommonFaileActivity("用户已被注册");
			}
		}
	}

	/**
	 * 完善注册
	 */
	private void registImproveDone(HashMap<String, String> fieldMap) {

		if (fieldMap.containsKey("respmsg")) {
			if ("1".equals(fieldMap.get("respmsg"))) {
				gotoCommonSuccessActivity("注册信息已完善");
			} else if ("0".equals(fieldMap.get("respmsg"))) {
				TransferLogic.getInstance().gotoCommonFaileActivity("操作失败");
			}
		}
	}

	/**
	 * 实名认证
	 */
	private void authenticationDone(HashMap<String, String> fieldMap) {

		if (fieldMap.containsKey("respmsg")) {
			if ("1".equals(fieldMap.get("respmsg"))) {
				gotoCommonSuccessActivity("实名认证成功");
			} else if ("0".equals(fieldMap.get("respmsg"))) {
				TransferLogic.getInstance().gotoCommonFaileActivity("实名认证失败");
			}
		}
	}
	
	/**
	 * 找回密码 验证用户信息
	 */
	private void findPwdDone(HashMap<String, String> fieldMap) {

		if (fieldMap.containsKey("respmsg")) {
			if ("1".equals(fieldMap.get("respmsg"))) {
				
				String jsonStr = fieldMap.get("apires");
				HashMap<String, String> receiveFieldMap = new HashMap<String, String>();

				try {
					JSONTokener parse = new JSONTokener(jsonStr);
					JSONObject content = (JSONObject) parse.nextValue();

					@SuppressWarnings("unchecked")
					Iterator<String> keys = content.keys();
					while (keys.hasNext()) {
						String key = (String) keys.next();
						receiveFieldMap.put(key, content.getString(key));

					}
				} catch (JSONException e) {
					e.printStackTrace();
				}
				Intent intent = new Intent(BaseActivity.getTopActivity(), FindPwdModifyActivity.class);
				intent.putExtra("smscode", receiveFieldMap.get("smscode"));
				if(Constant.PASS.equals("logpass")){
					intent.putExtra("b_flag", false);
				}else{
					intent.putExtra("b_flag", true);
				}
				
				BaseActivity.getTopActivity().startActivityForResult(intent, 0);

			} else if ("0".equals(fieldMap.get("respmsg"))) {
				TransferLogic.getInstance().gotoCommonFaileActivity("验证用户信息失败");
			}
		}
	}
	/**
	 * 找回密码 设置新密码
	 */
	private void getSetNewPwdDone(HashMap<String, String> fieldMap) {

		if (fieldMap.containsKey("respmsg")) {
			if ("1".equals(fieldMap.get("respmsg"))) {
				TransferLogic.getInstance().gotoCommonSuccessActivity("设置新密码成功");

			} else if ("0".equals(fieldMap.get("respmsg"))) {
				TransferLogic.getInstance().gotoCommonFaileActivity("设置新密码失败");
			}
		}
	}
	
	/**
	 * 版本号
	 */
	private void getVersionDone(HashMap<String, String> fieldMap) {

		if (fieldMap.containsKey("respmsg")) {
			if ("1".equals(fieldMap.get("respmsg"))) {
				String jsonStr = fieldMap.get("apires");
				HashMap<String, String> receiveFieldMap = new HashMap<String, String>();

				try {
					JSONTokener parse = new JSONTokener(jsonStr);
					JSONObject content = (JSONObject) parse.nextValue();

					@SuppressWarnings("unchecked")
					Iterator<String> keys = content.keys();
					while (keys.hasNext()) {
						String key = (String) keys.next();
						receiveFieldMap.put(key, content.getString(key));

					}
				} catch (JSONException e) {
					e.printStackTrace();
				}
				float version = Float.valueOf(receiveFieldMap.get("version"));
				((LRCatalogActivity)BaseActivity.getTopActivity()).showAlertView(version, receiveFieldMap.get("url"));

			} else if ("0".equals(fieldMap.get("respmsg"))) {
				TransferLogic.getInstance().gotoCommonFaileActivity("设置新密码失败");
			}
		}
	}
	/**
	 * 修改密码
	 */
	private void modifyPwdDone(HashMap<String, String> fieldMap) {

		if (fieldMap.containsKey("respmsg")) {
			if ("1".equals(fieldMap.get("respmsg"))) {
				gotoCommonSuccessActivity("密码修改成功");

			} else if ("0".equals(fieldMap.get("respmsg"))) {
				TransferLogic.getInstance().gotoCommonFaileActivity("密码修改失败");
			} else if ("2".equals(fieldMap.get("respmsg"))) {
				TransferLogic.getInstance().gotoCommonFaileActivity("原始密码错误");
			}
		}
	}

	/**
	 * 短信验证码
	 */
	private void getSmsDone(HashMap<String, String> fieldMap) {
		// 以短信形式接收
		BaseActivity.getTopActivity().refreshSMSBtn();
		
	}

	/**
	 * 获取提款银行账号
	 */
	private void getBankAccountDone(HashMap<String, String> fieldMap) {
		BaseActivity.getTopActivity().hideDialog(BaseActivity.COUNTUP_DIALOG);
		if (fieldMap.containsKey("respmsg")) {
			if ("1".equals(fieldMap.get("respmsg"))) {

				String jsonStr = fieldMap.get("apires");
				HashMap<String, String> receiveFieldMap = new HashMap<String, String>();

				try {
					JSONTokener parse = new JSONTokener(jsonStr);
					JSONObject content = (JSONObject) parse.nextValue();

					@SuppressWarnings("unchecked")
					Iterator<String> keys = content.keys();
					while (keys.hasNext()) {
						String key = (String) keys.next();
						receiveFieldMap.put(key, content.getString(key));

					}
				} catch (JSONException e) {
					e.printStackTrace();
				}
				((DrawMoneyActivity)(BaseActivity.getTopActivity())).setReceiveMap(receiveFieldMap);
			} else if ("0".equals(fieldMap.get("respmsg"))) {
				TransferLogic.getInstance().gotoCommonFaileActivity("获取提款银行账号失败");
			}
		}
	}
	
	/**
	 * 交易流水
	 */
	private void QueryTransListDone(HashMap<String, String> fieldMap) {
		BaseActivity.getTopActivity().hideDialog(BaseActivity.COUNTUP_DIALOG);
		HashMap<String, Object> map = new HashMap<String, Object>();
		ArrayList<TransferDetailModel> arrayModel = new ArrayList<TransferDetailModel>();
		if (fieldMap.containsKey("respmsg")) {
			if ("1".equals(fieldMap.get("respmsg"))) {

				try {
					String jsonStr = fieldMap.get("apires");
					JSONObject result = new JSONObject(jsonStr);
					JSONArray jsonArray = result.getJSONArray("object");

					String page_count = result.getString("page_count");
					map.put("total", page_count);
					if (jsonArray != null && jsonArray.length() > 0) {
						for (int i = 0; i < jsonArray.length(); i++) {
							JSONObject picsObj = (JSONObject) jsonArray.opt(i);
							TransferDetailModel model = new TransferDetailModel();
							model.setAccount1(picsObj.optString("account1", ""));
							model.setAmount(picsObj.optString("amount", ""));
							model.setCard_branch_id(picsObj.optString("card_branch_id", ""));
							model.setLocal_log(picsObj.optString("local_log", ""));
							model.setLocaldate(picsObj.optString("localdate", ""));
							model.setLocaltime(picsObj.optString("localtime", ""));
							model.setSnd_cycle(picsObj.optString("snd_cycle", ""));
							model.setSystransid(picsObj.optString("systransid", ""));
							model.setRspmsg(picsObj.optString("rspmsg", ""));
							model.setFlag(picsObj.optString("flag", ""));
							

							arrayModel.add(model);
						}
					}

					map.put("list", arrayModel);
				} catch (JSONException e) {
					e.printStackTrace();
				}

				TransferDetailListActivity activity = (TransferDetailListActivity) BaseActivity.getTopActivity();
				activity.fromLogic(map);
				
			} else if ("0".equals(fieldMap.get("respmsg"))) {
				TransferLogic.getInstance().gotoCommonFaileActivity("获取交易流水失败");
			}
		}
	}
	
	/**
	 * 公告查询
	 */
	private void getAnnounceListDone(HashMap<String, String> fieldMap) {
		ArrayList<AnnouncementModel> arrayModel = new ArrayList<AnnouncementModel>();
		if (fieldMap.containsKey("respmsg")) {
			if ("1".equals(fieldMap.get("respmsg"))) {

				try {
					String jsonStr = fieldMap.get("apires");
					JSONTokener parse = new JSONTokener(jsonStr);
					JSONArray jsonArray = (JSONArray) parse.nextValue();

					if (jsonArray != null && jsonArray.length() > 0) {
						for (int i = 0; i < jsonArray.length(); i++) {
							JSONObject picsObj = (JSONObject) jsonArray.opt(i);
							AnnouncementModel model = new AnnouncementModel();
							model.setTel(picsObj.optString("tel", ""));
							model.setBranch_id(picsObj.optString("branch_id", ""));
							model.setNotice_content(picsObj.optString("notice_content", ""));
							model.setNotice_title(picsObj.optString("notice_title", ""));
							model.setNotice_date(picsObj.optString("notice_date", ""));
							model.setNotice_time(picsObj.optString("notice_time", ""));
							model.setNotice_date(picsObj.optString("notice_date", ""));
							arrayModel.add(model);
						}
					}

				} catch (JSONException e) {
					e.printStackTrace();
				}

				Intent intent = new Intent(BaseActivity.getTopActivity(), AnnouncementListActivity.class);
				intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
				intent.putExtra("array", arrayModel);
				BaseActivity.getTopActivity().startActivityForResult(intent, 0);
			} else if ("0".equals(fieldMap.get("respmsg"))) {
				TransferLogic.getInstance().gotoCommonFaileActivity("获取提款银行账号失败");
			}
		}
	}

	/**
	 * 获取商户注册信息
	 */
	private void getMerchantInfoDone(HashMap<String, String> fieldMap) {
		BaseActivity.getTopActivity().hideDialog(BaseActivity.COUNTUP_DIALOG);
		if (fieldMap.containsKey("respmsg")) {
			if ("1".equals(fieldMap.get("respmsg"))) {
				UserModel model = new UserModel();
				try {
					String jsonStr = fieldMap.get("apires");
					JSONObject result = new JSONObject(jsonStr);
					model.setMerchant_name(result.optString("merchant_name", ""));
					model.setPid(result.optString("pid", ""));
					model.setIs_identify(result.optString("is_identify", ""));
					model.setIs_complete(result.optString("is_complete", ""));
					model.setStatus(result.optString("status", ""));
					model.setMerchant_id(result.optString("merchant_id", ""));
				} catch (JSONException e) {
					e.printStackTrace();
				}
				BaseActivity.getTopActivity().fromLogic(model);
				
			} else if ("0".equals(fieldMap.get("respmsg"))) {
				TransferLogic.getInstance().gotoCommonFaileActivity("找回密码失败");
			}
		}
	}
	/**
	 * 获取支行
	 */
	private void getBranchBankDone(HashMap<String, String> fieldMap) {
		HashMap<String, Object> map = new HashMap<String, Object>();
		ArrayList<BankModel> arrayModel = new ArrayList<BankModel>();
		if (fieldMap.containsKey("respmsg")) {
			if ("1".equals(fieldMap.get("respmsg"))) {

				try {
					String jsonStr = fieldMap.get("apires");
					JSONObject result = new JSONObject(jsonStr);
					JSONArray jsonArray = result.getJSONArray("object");

					String page_count = result.getString("page_count");
					map.put("page_count", page_count);
//					JSONArray jsonArray = (JSONArray) parse.nextValue();

					if (jsonArray != null && jsonArray.length() > 0) {
						for (int i = 0; i < jsonArray.length(); i++) {
							JSONObject picsObj = (JSONObject) jsonArray.opt(i);
							BankModel model = new BankModel();

							model.setCode(picsObj.optString("bankbranchid", ""));
							model.setName(picsObj.optString("bankbranchname", ""));
							arrayModel.add(model);
						}
					}
					map.put("object", arrayModel);
				} catch (JSONException e) {
					e.printStackTrace();
				}
				BankSearchActivity activity = (BankSearchActivity) BaseActivity.getTopActivity();
				activity.fromLogic(map);
			} else if ("0".equals(fieldMap.get("respmsg"))) {
				TransferLogic.getInstance().gotoCommonFaileActivity("获取支行信息失败");
			}
		}
	}
	
	
	/**
	 * 新增提款银行帐号
	 */
	private void addBankAccountDone(HashMap<String, String> fieldMap) {

 		if (fieldMap.containsKey("respmsg")) {
			if ("1".equals(fieldMap.get("respmsg"))) {
				gotoCommonSuccessActivity("新增成功");

			} else if ("0".equals(fieldMap.get("respmsg"))) {
				TransferLogic.getInstance().gotoCommonFaileActivity("新增失败");
			}
		}
	}

	/**
	 * 修改提款银行帐号
	 */
	private void modifyBankAccountDone(HashMap<String, String> fieldMap) {

		if (fieldMap.containsKey("respmsg")) {
			if ("1".equals(fieldMap.get("respmsg"))) {
				gotoCommonSuccessActivity("修改成功");

			} else if ("0".equals(fieldMap.get("respmsg"))) {
				TransferLogic.getInstance().gotoCommonFaileActivity("修改失败");
			}
		}
	}

	/**
	 * 解析公钥字符串，得到mod exp
	 * 
	 * @param key
	 *            公钥的ascii
	 * @return mod exp 数组
	 */
	private String[] parsePublickey(String key) {
		// 计算key的字节长度并保存到数组
		String[] bytes = new String[key.length() / 2];
		for (int i = 0; i < key.length() / 2; i++) {
			bytes[i] = key.substring(i * 2, i * 2 + 2);
		}
		// Byte Length
		int index = 1;
		int length = Integer.parseInt(bytes[index], 16);
		if (length > 128)
			index += length - 128;

		// Modulus Length
		index += 2;
		length = Integer.parseInt(bytes[index], 16);
		if (length > 128) {
			int i = length - 128;
			String lenStr = "";
			for (int j = index + 1; j < index + i + 1; j++)
				lenStr += bytes[j];

			index += i;
			length = Integer.parseInt(lenStr, 16);
		}

		// 保存mod值
		StringBuffer modBuff = new StringBuffer();
		for (int i = index + 1; i < index + 1 + length; i++)
			modBuff.append(bytes[i]);

		// Exponent Length
		index += length + 2;
		length = Integer.parseInt(bytes[index], 16);
		if (length > 128) {
			int i = length - 128;
			String lenStr = "";
			for (int j = index + 1; j < index + i + 1; j++)
				lenStr += bytes[j];

			index += i;
			length = Integer.parseInt(lenStr, 16);
		}

		// 保存exponent值
		index += 1;
		StringBuffer expBuff = new StringBuffer();
		for (int i = index; i < index + length; i++)
			expBuff.append(bytes[i]);

		return new String[] { modBuff.toString(), expBuff.toString() };
	}

	/**
	 * 这是第一次以后的查询交易明细，直接从第一次的请求报文中拿出所有的值，以后只是替换欲请求的页码。五个值包括密码也直接从原请求报文中取值
	 * 
	 * @param currentPage
	 *            要请求的当前页面
	 */
	public void queryHistoryAction(String currentPage) {
		HashMap<String, String> sendMap = transferMap.get(GENERALTRANSFER).getSendMap();
		// 验证上次请求的信息是否还存在，如果存在则直接更新上次的请求页面直接请求数据，否则失败让用户重新操作

		if (null != sendMap && sendMap.containsKey("fieldTrancode")) {
			if (sendMap.containsKey("field11")) {
				sendMap.put("TRACEAUDITNUM", sendMap.get("field11")); // 还是使用一个流水号
			}
			sendMap.put("pageNo", currentPage); // 替换查询页码
			this.transferAction(sendMap.get("fieldTrancode"), sendMap);
		} else {
			TransferLogic.getInstance().gotoCommonFaileActivity("查询明细时出现异常，请重试！");
		}
	}

	private void queryHistoryGroupDone(HashMap<String, String> fieldMap) {
		HashMap<String, String> sendMap = transferMap.get(GENERALTRANSFER).getSendMap();
		if (null != sendMap && sendMap.containsKey("BeginDate") && sendMap.containsKey("EndDate")) {
			Intent intent = new Intent("com.bfb.pos.queryTransferHistoryGroupActivity");
			intent.putExtra("detail", fieldMap.get("detail"));
			intent.putExtra("BeginDate", sendMap.get("BeginDate"));
			intent.putExtra("EndDate", sendMap.get("EndDate"));
			BaseActivity.getTopActivity().startActivity(intent);
		} else {
			TransferLogic.getInstance().gotoCommonFaileActivity("查询明细时出现异常，请重试！");
		}
	}

	private void queryHistoryListDone(HashMap<String, String> fieldMap) {
		HashMap<String, String> sendMap = transferMap.get(GENERALTRANSFER).getSendMap();
		if (null != sendMap && sendMap.containsKey("totalCount")) {
			Intent intent = new Intent("com.bfb.pos.queryTransferHistoryList");
			intent.putExtra("map", fieldMap);
			intent.putExtra("totalCount", Integer.parseInt(sendMap.get("totalCount")));
			BaseActivity.getTopActivity().startActivity(intent);
		} else {
			TransferLogic.getInstance().gotoCommonFaileActivity("查询明细时出现异常，请重试！");
		}
	}

	/**
	 * 银行卡余额查询
	 */
	private void queryBalanceDone(final HashMap<String, String> fieldMap) {
		if(Constant.isAISHUA){
			Intent intent = new Intent(ApplicationEnvironment.getInstance().getApplication().getPackageName() + ".showBalanceAishua");
			intent.putExtra("balance", fieldMap.get("field54"));
			intent.putExtra("availableBalance", fieldMap.get("field4"));
			intent.putExtra("accountNo", fieldMap.get("field2"));
			intent.putExtra("message", fieldMap.get("fieldMessage"));
			BaseActivity.getTopActivity().startActivityForResult(intent, 0);
		}else{
			Intent intent = new Intent(ApplicationEnvironment.getInstance().getApplication().getPackageName() + ".showBalance");
			intent.putExtra("balance", fieldMap.get("field54"));
			intent.putExtra("availableBalance", fieldMap.get("field4"));
			intent.putExtra("accountNo", fieldMap.get("field2"));
			intent.putExtra("message", fieldMap.get("fieldMessage"));
			BaseActivity.getTopActivity().startActivityForResult(intent, 0);
		}
		
	}

	/**
	 * 收款或收款撤销成功，记录数据以备查询签购单
	 */
	private void recordSuccessTransfer(HashMap<String, String> fieldMap) {
		TransferSuccessModel model = new TransferSuccessModel();
		model.setAmount(fieldMap.get("field4"));
		model.setTraceNum(fieldMap.get("field11"));
		model.setTransCode(fieldMap.get("fieldTrancode"));
		model.setDate(fieldMap.get("field13"));
		model.setContent(fieldMap);
		TransferSuccessDBHelper helper = new TransferSuccessDBHelper();
		boolean flag = helper.insertATransfer(model);
		if (!flag) {
			Log.e("DATABASE", "成功交易写入数据库时操作失败。。。");
		}
	}

	/**
	 * 收款
	 */
	private void receiveTransDone(HashMap<String, String> fieldMap) {
		recordSuccessTransfer(fieldMap);

		try {
			if(Constant.isAISHUA){
				ViewPage transferViewPage = new ViewPage("transfersuccess");
				Event event = new Event(transferViewPage, "transfersuccess", "transfersuccess");
				event.setStaticActivityDataMap(fieldMap);
				transferViewPage.addAnEvent(event);
				event.trigger();
			}else{
				if (AppDataCenter.getValue("__TERSERIALNO").startsWith("001917")) { 
					// 打印
					Intent intent = new Intent("com.bfb.pos.PrintReceiptActivity");
					intent.putExtra("content", fieldMap);
					BaseActivity.getTopActivity().startActivityForResult(intent, 0);

				} else {
					ViewPage transferViewPage = new ViewPage("transfersuccess");
					Event event = new Event(transferViewPage, "transfersuccess", "transfersuccess");
					event.setStaticActivityDataMap(fieldMap);
					transferViewPage.addAnEvent(event);
					event.trigger();
				}
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}

		/***
		 * try{ SharedPreferences sp =
		 * ApplicationEnvironment.getInstance().getPreferences(); String
		 * deviceIDs = sp.getString(Constant.DEVICEID, ""); String[] deviceArray
		 * = deviceIDs.split("\\|"); for (String str : deviceArray) { if
		 * (!str.equals("") &&
		 * str.equals(AppDataCenter.getValue("__TERSERIALNO").substring(13))){
		 * ViewPage transferViewPage = new ViewPage("transfersuccess"); Event
		 * event = new
		 * Event(transferViewPage,"transfersuccess","transfersuccess");
		 * event.setStaticActivityDataMap(fieldMap);
		 * transferViewPage.addAnEvent(event); event.trigger();
		 * 
		 * return; } }
		 * 
		 * // 打印 Intent intent = new Intent("com.bfb.pos.printReceipt");
		 * intent.putExtra("content", fieldMap);
		 * BaseActivity.getTopActivity().startActivityForResult(intent, 0);
		 * }catch(Exception e){ e.printStackTrace(); }
		 ****/
	}

	/**
	 * 收款撤销
	 */
	private void revokeTransDone(HashMap<String, String> fieldMap) {
		recordSuccessTransfer(fieldMap);

//		TransferSuccessDBHelper helper = new TransferSuccessDBHelper();
//		helper.updateRevokeFalg(fieldMap.get("field11"));
		HashMap<String, String> sendMap = transferMap.get(GENERALTRANSFER).getSendMap();
		TransferSuccessDBHelper helper = new TransferSuccessDBHelper();
		helper.updateRevokeFalg(sendMap.get("field61").substring(6));
		
		try {
			if(Constant.isAISHUA){
				ViewPage transferViewPage = new ViewPage("transfersuccess");
				Event event = new Event(transferViewPage, "transfersuccess", "transfersuccess");
				event.setStaticActivityDataMap(fieldMap);
				transferViewPage.addAnEvent(event);
				event.trigger();
			}else{
				if (AppDataCenter.getValue("__TERSERIALNO").startsWith("001917")) {
					// 打印
					Intent intent = new Intent("com.bfb.pos.PrintReceiptActivity");
					intent.putExtra("content", fieldMap);
					BaseActivity.getTopActivity().startActivityForResult(intent, 0);

				} else {
					ViewPage transferViewPage = new ViewPage("transfersuccess");
					Event event = new Event(transferViewPage, "transfersuccess", "transfersuccess");
					event.setStaticActivityDataMap(fieldMap);
					transferViewPage.addAnEvent(event);
					event.trigger();
				}
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}

		/***
		 * try{ SharedPreferences sp =
		 * ApplicationEnvironment.getInstance().getPreferences(); String
		 * deviceIDs = sp.getString(Constant.DEVICEID, ""); String[] deviceArray
		 * = deviceIDs.split("\\|"); for (String str : deviceArray) { if
		 * (!str.equals("") &&
		 * str.equals(AppDataCenter.getValue("__TERSERIALNO").substring(13))){
		 * ViewPage transferViewPage = new ViewPage("transfersuccess"); Event
		 * event = new
		 * Event(transferViewPage,"transfersuccess","transfersuccess");
		 * event.setStaticActivityDataMap(fieldMap);
		 * transferViewPage.addAnEvent(event); event.trigger();
		 * 
		 * return; } }
		 * 
		 * // 打印 Intent intent = new Intent("com.bfb.pos.printReceipt");
		 * intent.putExtra("content", fieldMap);
		 * BaseActivity.getTopActivity().startActivityForResult(intent, 0);
		 * }catch(Exception e){ e.printStackTrace(); }
		 ***/
	}

	/**
	 * 银行账户转账 (付款)
	 */
	private void bankTransferDone(HashMap<String, String> fieldMap) {
		Intent intent = new Intent(ApplicationEnvironment.getInstance().getApplication().getPackageName() + ".transferSuccessSendSms");
		intent.putExtra("map", fieldMap);
		BaseActivity.getTopActivity().startActivityForResult(intent, 0);
	}

	/**
	 * 冲正
	 */
	public boolean reversalAction() {
		return false;

		/*
		 * if (Constant.isStatic){ return false; }
		 * 
		 * ReversalDBHelper helper = new ReversalDBHelper(); HashMap<String,
		 * String> map = helper.queryNeedReversal();
		 * 
		 * if (null == map || map.size() == 0){ return false; } else {
		 * BaseActivity
		 * .getTopActivity().showDialog(BaseActivity.PROGRESS_DIALOG,
		 * "正在发起冲正交易，请稍候...");
		 * 
		 * // 更新冲正表，则冲正次数加1。 // 注意这可能有问题，因为如果网络不通，直接没有从手机中发出交易，也已经使冲正次数发生变更
		 * ReversalDBHelper DBhelper = new ReversalDBHelper();
		 * DBhelper.updateReversalCount(map.get("field11"));
		 * 
		 * // 将原交易的transferCode改为对应的冲正的transferCode map.put("fieldTrancode",
		 * AppDataCenter.getReversalMap().get(map.get("fieldTrancode")));
		 * 
		 * this.transferAction(map.get("fieldTrancode"), map);
		 * 
		 * return true; }
		 */
	}

	/**
	 * 签购单上传
	 * 
	 * 上传签购单接口也用于只传送手机号，如转账
	 */
	public void uploadReceiptAction(HashMap<String, String> fieldsMap) {
		// 静态演示模式下不上传签购单。
		if (Constant.isStatic) {
			BaseActivity.getTopActivity().setResult(BaseActivity.RESULT_OK);
			BaseActivity.getTopActivity().finish();

			return;
		}

		try {
			HashMap<String, String> map = new HashMap<String, String>();
//			map.put("field41", AppDataCenter.getValue("__TERID"));
//			map.put("field42", AppDataCenter.getValue("__VENDOR"));
//			map.put("termMobile", AppDataCenter.getValue("__PHONENUM"));
//			map.put("ReaderID", AppDataCenter.getValue("__TERSERIALNO"));
//			map.put("PSAMID", AppDataCenter.getValue("__PSAMNO"));
//
//			map.put("field7", transferMap.get(GENERALTRANSFER).getSendMap().get("field7"));
//			map.put("field11", fieldsMap.get("field11"));
//			map.put("batchNum", fieldsMap.get("field60").substring(2, 8));
			
			map.put("local_log", fieldsMap.get("field37"));
			map.put("merchant_id", fieldsMap.get("field42"));
			map.put("filedIMEI", fieldsMap.get("imei"));
			map.put("fieldMobile", fieldsMap.get("receivePhoneNo"));

			if (fieldsMap.containsKey("signImageName")) {
				String imagePath = Constant.SIGNIMAGESPATH + fieldsMap.get("signImageName") + ".JPEG";
				map.put("img", StringUtil.Image2Base64(imagePath));

				// 删除签名图片
				File f = new File(imagePath);
				if (f.exists()) {
					if (f.delete()) {
						Log.e("SignImage", "文件删除成功！");
					} else {
						Log.e("SignImage", "文件删除失败！");
					}
				} else {
					Log.e("SignImage", "签名图片不存在！");
				}
			} else {
				map.put("img", "");
			}

			// 写入数据库
			UploadSignImageDBHelper helper = new UploadSignImageDBHelper();
			if (helper.insertATransfer(fieldsMap.get("field11"), fieldsMap.get("receivePhoneNo"), map)) {
				Log.e("sign image", "成功写入数据库");
			} else {
				Log.e("sign image", "写入数据库失败");
			}

		} catch (Exception e) {
			e.printStackTrace();

		} finally {
			try {
				BaseActivity.getTopActivity().startService(new Intent("com.bfb.pos.uploadSignImageService"));

				BaseActivity.getTopActivity().setResult(BaseActivity.RESULT_OK);
				BaseActivity.getTopActivity().finish();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * 签购单上传完成
	 */
	private void uploadReceiptDone(HashMap<String, String> fieldMap) {
		String field11 = transferMap.get(UPLOADSIGNIMAGETRANSFER).getSendMap().get("field11");

		UploadSignImageDBHelper helper = new UploadSignImageDBHelper();

		// 发送短信
		String receMobile = helper.queryReceMobile(field11);
		if (SystemConfig.isSendSMS() && !"".equals(receMobile)) {
			PhoneUtil.sendSMS(receMobile, fieldMap.get("field44"));
		}

		// 签购单上传成功更新数据库
		helper.updateUploadFlagSuccess(field11);

		// gotoCommonSuccessActivity(fieldMap.get("fieldMessage"));
	}

	/**
	 * 检查更新
	 */
	private void checkUpdateAPK(HashMap<String, String> fieldMap) {
		Intent intent = new Intent("com.bfb.pos.updateAPKService");
		intent.putExtra("flag", "response");
		intent.putExtra("apkName", fieldMap.get("version_name"));
		intent.putExtra("serverVersionCode", fieldMap.get("version_number"));
		BaseActivity.getTopActivity().startService(intent);
	}

	/**
	 * 取验证码
	 */
	private void downloadSecurityCode(HashMap<String, String> fieldMap) {

		// 跳转到哪一个页面不确定。注意一定要在欲跳转的页面设置setAction("');
		Intent intent = new Intent(BaseActivity.getTopActivity().getIntent().getAction());
		intent.putExtra("flag", "getSecurityCode");
		intent.putExtra("securityCode", fieldMap.get("captcha"));
		BaseActivity.getTopActivity().startActivity(intent);
		BaseActivity.getTopActivity().hideDialog(BaseActivity.PROGRESS_DIALOG);
	}

	public TransferModel parseConfigXML(String confName) throws FileNotFoundException {
		TransferModel transfer = new TransferModel();

		InputStream stream = null;

		FieldModel field = null;
		try {
			stream = AssetsUtil.getInputStreamFromPhone(confName);
		} catch (FileNotFoundException e) {
			throw new FileNotFoundException("加载系统文件异常(" + confName + ")");
		}

		try {
			KXmlParser parser = new KXmlParser();
			parser.setInput(stream, "utf-8");
			// 获取事件类型
			int eventType = parser.getEventType();
			while (eventType != XmlPullParser.END_DOCUMENT) {
				switch (eventType) {
				case XmlPullParser.START_TAG:
					if ("root".equalsIgnoreCase(parser.getName())) {
						transfer.setShouldMac(parser.getAttributeValue(null, "shouldMac"));
						transfer.setIsJson(parser.getAttributeValue(null, "isJson"));
					} else if ("field".equalsIgnoreCase(parser.getName())) {
						field = new FieldModel();
						field.setKey(parser.getAttributeValue(null, "key"));
						field.setValue(parser.getAttributeValue(null, "value"));
						// field.setMacField(parser.getAttributeValue(null,
						// "macField"));
					}

					break;
				case XmlPullParser.END_TAG:
					if ("field".equalsIgnoreCase(parser.getName())) {
						transfer.addField(field);
					}
					break;
				}
				eventType = parser.next();// 进入下一个元素并触发相应事件
			}

		} catch (IOException e) {
			e.printStackTrace();

		} catch (XmlPullParserException e) {
			e.printStackTrace();

		} finally {
			try {
				if (null != stream)
					stream.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		return transfer;
	}

	/**
	 * 跳转到通用的成功界面，只显示一行提示信息
	 */
	public void gotoCommonSuccessActivity(String prompt) {
		if (null == prompt || "".equals(prompt.trim())) {
			prompt = "交易成功";
		}

		try {
			ViewPage transferViewPage = new ViewPage("tradesuccess");
			HashMap<String, String> map = new HashMap<String, String>();
			map.put("message", prompt);
			Event event = new Event(transferViewPage, "tradesuccess", "tradesuccess");
			event.setStaticActivityDataMap(map);
			transferViewPage.addAnEvent(event);
			event.trigger();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 跳转到通用的失败界面，只显示一行错误提示信息。
	 */
	public void gotoCommonFaileActivity(String prompt) {
//		try {
//			ViewPage transferViewPage = new ViewPage("tradefailed");
//			HashMap<String, String> map = new HashMap<String, String>();
//			map.put("failedReason", prompt);
//			Event event = new Event(transferViewPage, "tradefailed", "tradefailed");
//			event.setStaticActivityDataMap(map);
//			transferViewPage.addAnEvent(event);
//			event.trigger();
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
		
		
		Intent intent = new Intent(BaseActivity.getTopActivity(), FaileActivity.class);
		intent.putExtra("prompt", prompt);
		BaseActivity.getTopActivity().startActivityForResult(intent, 1);
	}

	/**
	 * 读取系统配置信息
	 */
	private boolean loadSystemConfig() {
		try {
			InputStream stream = AssetsUtil.getInputStreamFromPhone("systemconfig.xml");
			KXmlParser parser = new KXmlParser();
			parser.setInput(stream, "utf-8");
			int eventType = parser.getEventType();
			while (eventType != XmlPullParser.END_DOCUMENT) {
				switch (eventType) {
				case XmlPullParser.START_TAG:
					if ("item".equalsIgnoreCase(parser.getName())) {
						String key = parser.getAttributeValue(null, "key");
						if (key.equals("sendSMS")) {
							SystemConfig.setSendSMS(parser.getAttributeValue(null, "value"));
						} else if (key.equals("pageSize")) {
							SystemConfig.setPageSize(parser.getAttributeValue(null, "value"));
						} else if (key.equals("historyInterval")) {
							SystemConfig.setHistoryInterval(parser.getAttributeValue(null, "value"));
						} else if (key.equals("maxReversalCount")) {
							SystemConfig.setMaxReversalCount(parser.getAttributeValue(null, "value"));
						} else if (key.equals("maxTransferTimeout")) {
							SystemConfig.setMaxTransferTimeout(parser.getAttributeValue(null, "value"));
						} else if (key.equals("maxLockTimeout")) {
							SystemConfig.setMaxLockTimeout(parser.getAttributeValue(null, "value"));
						}
					}

					break;
				}
				eventType = parser.next();// 进入下一个元素并触发相应事件
			}

			return true;

		} catch (IOException e) {
			e.printStackTrace();
			return false;
		} catch (XmlPullParserException e) {
			e.printStackTrace();
			return false;
		}
	}

}
