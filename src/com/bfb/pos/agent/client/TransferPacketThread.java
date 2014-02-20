package com.bfb.pos.agent.client;

import java.io.FileNotFoundException;
import java.io.UnsupportedEncodingException;
import java.security.Key;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONStringer;
import org.json.JSONTokener;

import android.annotation.SuppressLint;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;

import com.bfb.pos.activity.BaseActivity;
import com.bfb.pos.agent.client.db.ReversalDBHelper;
import com.bfb.pos.client.exception.HttpException;
import com.bfb.pos.fsk.FSKOperator;
import com.bfb.pos.fsk.FSKService;
import com.bfb.pos.model.FieldModel;
import com.bfb.pos.model.ReversalModel;
import com.bfb.pos.model.TransferModel;
import com.bfb.pos.util.ByteUtil;
import com.bfb.pos.util.StringUtil;
import com.bfb.pos.util.UnionDes;
import com.dhcc.pos.core.SocketTransport;
import com.dhcc.pos.core.TxActionImp;
import com.dhcc.pos.packets.util.ConvertUtil;
import com.itron.android.ftf.Util;
import com.itron.protol.android.CommandReturn;

/**
 * @author sth
 * 
 */

public class TransferPacketThread extends Thread {
	private String tmp_mac = null;
	byte[] respByte = null;
	private String transferCode; // 交易码
	private TransferModel transferModel;
	private HashMap<String, String> map; // 字段值，需要替换由config解析出来的value值
	private Handler handler; // 响应的Handler

	private HashMap<String, String> sendFieldMap;
	private HashMap<String, String> receiveFieldMap;

	private JSONStringer sendJSONStringer;

	byte[] sendByte = new byte[] {};

	private TxActionImp action;

	public TransferPacketThread(String transferCode, HashMap<String, String> map, Handler handler) {
		this.transferCode = transferCode;
		this.map = map;
		this.handler = handler;
	}

	public HashMap<String, String> getSendMap() {
		return sendFieldMap;
	}

	public HashMap<String, String> getReceMap() {
		return receiveFieldMap;
	}

	@Override
	public void run() {
		Looper.prepare();
		// BaseActivity.getTopActivity().showDialog("正在处理交易，请稍候 ",
		// transferCode);
		this.pack(this.map);
		Looper.loop();
	}

	/**
	 * 根据配置文件生成JSON
	 * 
	 * @param tranCode
	 *            交易码
	 * @param dataMap
	 *            如果在界面中的值需要参与JSON的拼装
	 * @return 返回拼装的报文
	 */
	private void pack(HashMap<String, String> dataMap) {
		// BaseActivity.getTopActivity().showDialog("正在处理交易，请稍候 ",
		// transferCode);
		try {
			String configXml = "con_req_" + this.transferCode + ".xml";
			if (Constant.isAISHUA) {
				if (this.transferCode.equals("086000") || this.transferCode.equals("020001") || this.transferCode.equals("020022") || this.transferCode.equals("020023") || this.transferCode.equals("080002")) {
					configXml = "con_req_" + this.transferCode + "_aishua" + ".xml";
				}

			}
			transferModel = TransferLogic.getInstance().parseConfigXML(configXml);

			// 在报文的配置中，有可能值来自于本报文中某一个域的值，为了检索的效率，在tempMap中将已解析的值存储，在后面用到时直接在tmepMap中查找
			// 取本报文的值用$做前缀，此时一定要注意，取此值时，前面一定要已经有这个域的值
			sendFieldMap = new HashMap<String, String>();
			StringBuilder macsb = new StringBuilder();

			JSONStringer tmpSendJSONString = new JSONStringer();
			tmpSendJSONString.object();

			for (FieldModel model : transferModel.getFieldList()) {
				StringBuffer sb = new StringBuffer();

				String[] values = model.getValue().split("#");
				for (String value : values) {
					if (value.startsWith("$")) {
						// 如果报文中某一域的值取自此报文的其他域的值，其值规定为将key的首末用'$'做前后缀
						// for example：field60 - 012#__PASMNO#$field11$
						if (sendFieldMap.containsKey(value.substring(1, value.length() - 1))) {
							sb.append(sendFieldMap.get(value.substring(1, value.length() - 1)));
						} else {
							Log.e("conf_req_" + this.transferCode + ".xml WRONG", "Set the value of '" + model.getKey() + "' before setting the value of '" + value.substring(1, value.length() - 1) + "' !!!");
						}

					} else if (value.startsWith("__")) {
						// 首先检查此值是否来自界面输入
						if (null != dataMap && dataMap.containsKey(value.substring(2))) {
							sb.append(dataMap.get(value.substring(2)));
						} else {
							// 如果不是来自界面，那么就在AppDataCenter中寻找这个值。
							sb.append(AppDataCenter.getValue(value));
						}

					} else {
						// 如果不带下划线则直接将值拼装。
						sb.append(value);
					}
				}

				model.setValue(sb.toString());

				// 进行一步特殊处理，fieldImage为上传签购单的图片内容，一般为20-30K。我认为在其他地方不会使用该值，所以不在map中保存。
				if (!model.getKey().equals("img") && !model.getKey().equals("macstr")) {
					sendFieldMap.put(model.getKey(), model.getValue());
				}

				// json 计算mac的字段
				if (model.getKey().equals("macstr")) {
					tmp_mac = model.getValue();
				}

				if (!model.getKey().trim().equals("fieldTrancode") && !model.getKey().trim().equals("isJson") && !model.getKey().equals("macstr")) {
					tmpSendJSONString.key(model.getKey()).value(model.getValue());
				}

				if (!model.getKey().trim().equals("fieldTrancode")) {
					macsb.append(FormatFieldValue.format(model.getKey(), model.getValue()));
				}
			}

			// 如果该交易需要进行冲正，则将其记入数据库冲正表中。注意，这里可能会有问题，因为有可能网络不通，直接打回，也就是说没有从手机发出交易就需要进行充正。
			if (AppDataCenter.getReversalMap().containsKey(this.transferCode)) {
				ReversalModel model = new ReversalModel();
				model.setTraceNum(sendFieldMap.get("field11"));
				model.setDate(AppDataCenter.getValue("__yyyy-MM-dd"));
				model.setContent(sendFieldMap);

				ReversalDBHelper helper = new ReversalDBHelper();
				helper.insertATransaction(model);
			}

			if (transferModel.isJson()) {
				tmpSendJSONString.endObject();
				sendJSONStringer = new JSONStringer().object();
				String tmp_str = tmpSendJSONString.toString();
				sendJSONStringer.key("arg").value(tmp_str);
				if (transferModel.shouldMac()) {// 需要进行MAC计算
					String str = null;
					String[] tmpArray = tmp_mac.split(",");
					StringBuilder macBuilder = new StringBuilder();
					for (int i = 0; i < tmpArray.length; i++) {
						macBuilder.append(sendFieldMap.get(tmpArray[i]));
					}
					if (this.transferCode.equals("089020")) {
						str = sendFieldMap.get("tel") + sendFieldMap.get("merchant_id");
					} else {
						str = macBuilder.toString();
					}
					if (this.transferCode.equals("089014")) {
						Constant.isUploadSalesSlip = true;
					} else {
						Constant.isUploadSalesSlip = false;
					}
					String md5key = ApplicationEnvironment.getInstance().getPreferences().getString(Constant.MD5KEY, "");
					sendJSONStringer.key("mac").value(StringUtil.MD5Crypto(str + md5key));
					sendJSONStringer.endObject();
					this.sendPacket();
				} else {
					sendJSONStringer.endObject();
					this.sendPacket();
				}
			} else {
				Map<String, Object> tempMap = new HashMap<String, Object>();
				tempMap.putAll(this.sendFieldMap);

				Log.i("初始化上下文：", sendFieldMap.toString());
				// 初始化上下文
				action = new TxActionImp();
				sendByte = action.first(tempMap);

				if (transferModel.shouldMac()) {// 需要进行MAC计算

					if (Constant.isAISHUA) {

						try {
							// pinkey
							String str0 = Constant.mackey;
							/** macKey:mac密钥(明文); macKeyTemp:起始macKey */
							Key macKey, macKeyTemp;
							/** 数组类macKey */
							byte[] macKeyByte;

							/** macValue */
							byte[] macValue;

							macKeyTemp = UnionDes.getKey(ByteUtil.hexStringToBytes(str0));
							macKeyByte = UnionDes.des(ConvertUtil.hexStringToBytes(AppDataCenter.getRANDOM()), macKeyTemp);

							macKey = UnionDes.getKey(macKeyByte);

							byte[] tempByte0 = new byte[sendByte.length - 8 - 11];
							System.arraycopy(sendByte, 11, tempByte0, 0, tempByte0.length);

							macValue = UnionDes.xorDataAndMac(tempByte0, macKey);

							byte[] tempByte = new byte[sendByte.length];
							System.arraycopy(sendByte, 0, tempByte, 0, sendByte.length - 8);
							System.arraycopy(macValue, 0, tempByte, tempByte.length - 8, 8);

							sendByte = tempByte;
							sendPacket();

						} catch (IllegalStateException e) {
							e.printStackTrace();
						} catch (Exception e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					} else {
						byte[] tempByte = new byte[sendByte.length - 8 - 11];
						System.arraycopy(sendByte, 11, tempByte, 0, tempByte.length);
						CalcMacHandler calcHandler = new CalcMacHandler();
						FSKOperator.execute("Get_MAC|int:0,int:1,string:null,string:" + StringUtil.bytes2HexString(tempByte), calcHandler);
					}

				} else {
					this.sendPacket();
				}
			}

		} catch (IllegalStateException e) {
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			if (this.transferCode.equals("089006")) {

			}else{
				TransferLogic.getInstance().gotoCommonFaileActivity(e.getMessage());	
			}
			
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@SuppressWarnings("static-access")
	private void sendPacket() {
		if (this.transferCode.equals("086000") && (AppDataCenter.getPsamnoOrKsn() == null || AppDataCenter.getPsamnoOrKsn().length() == 0)) {
			BaseActivity.getTopActivity().showDialog(BaseActivity.getTopActivity().MODAL_DIALOG, "请插入设备");
			return;
		}
		if (transferModel.isJson()) {
			Log.e("send JSON", AppDataCenter.getMethod_Json(this.transferCode) + ":   " + sendJSONStringer.toString());
		}
		// 如果是冲正则提示冲正。
		if (AppDataCenter.getReversalMap().containsValue(this.transferCode)) {
			BaseActivity.getTopActivity().showDialog("正在进行冲正，请稍候 ", transferCode);

		} else if (this.transferCode.equals("086000")) {
			BaseActivity.getTopActivity().showDialog("正在签到，请稍候 ", transferCode);

		} else if (this.transferCode.equals("089014")) { // 上传签购单，静默
			// do nothing

		} else if (this.transferCode.equals("089018")) {

		} else if (this.transferCode.equals("999000003")) {
			BaseActivity.getTopActivity().showDialog("正在获取验证码", transferCode);

		} else if (this.transferCode.equals("089006")) {

		} else {
			BaseActivity.getTopActivity().showDialog("正在处理交易，请稍候 ", transferCode);
		}

		try {
			if (Constant.isStatic) {
				respByte = StaticNetClient.getMessageByTransCode(this.transferCode);
			} else {
				if (transferModel.isJson()) {
					respByte = HttpManager.getInstance().sendRequest(HttpManager.URL_JSON_TYPE, this.transferCode, sendJSONStringer.toString().getBytes("GBK"));
					parseJson(new String(respByte, "GBK"));
				} else {
					respByte = new SocketTransport().sendData(sendByte);
					HashMap<String, Object> respMap = action.afterProcess(respByte);

					receiveFieldMap = new HashMap<String, String>();
					for (String key : respMap.keySet()) {
						this.receiveFieldMap.put(key, (String) respMap.get(key));
					}

					parse();
				}

			}
			Log.i("hidecount ", "hidecount base before");
			BaseActivity.getTopActivity().hideDialog(BaseActivity.COUNTUP_DIALOG);
			Log.i("hidecount ", "hidecount base after");
		} catch (HttpException e) {
			BaseActivity.getTopActivity().hideDialog(BaseActivity.COUNTUP_DIALOG);
			BaseActivity.getTopActivity().hideDialog(BaseActivity.PROGRESS_DIALOG);
			if (this.transferCode.equals("089006")) {

			}else{
				TransferLogic.getInstance().gotoCommonFaileActivity(e.getMessage());				
			}

		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();

		} catch (Exception e) {
			e.printStackTrace();
			if (this.transferCode.equals("089018") || this.transferCode.equals("089006")) {

			} else {
				TransferLogic.getInstance().gotoCommonFaileActivity("服务器响应异常，请重试");

			}

		}
	}

	private void parse() {
		try {
			// 如果是上传签购单交易 原来是500000001
			if (this.transferCode.equals("089014")) {
				if (receiveFieldMap.containsKey("field39") && receiveFieldMap.get("field39").equals("00")) {
					Message message = new Message();
					message.what = 0; // 回调TransferLogic
					message.obj = receiveFieldMap;
					message.setTarget(handler);
					message.sendToTarget();
				}

			} else {
				if (transferModel.shouldMac()) {
					if (Constant.isAISHUA) {
						// pinkey
						String str0 = Constant.mackey;
						/** macKey:mac密钥(明文); macKeyTemp:起始macKey */
						Key macKey, macKeyTemp;
						/** 数组类macKey */
						byte[] macKeyByte;

						/** macValue */
						byte[] macValue;

						String random = (int) (Math.random() * 50 + 1) + "";

						macKeyTemp = UnionDes.getKey(ByteUtil.hexStringToBytes(str0));

						macKeyByte = UnionDes.des(ConvertUtil.hexStringToBytes(AppDataCenter.getRANDOM()), macKeyTemp);

						macKey = UnionDes.getKey(macKeyByte);

						byte[] tempByte0 = new byte[respByte.length - 8 - 11];
						System.arraycopy(respByte, 11, tempByte0, 0, tempByte0.length);

						macValue = UnionDes.xorDataAndMac(tempByte0, macKey);
						if (new String(macValue).equals(receiveFieldMap.get("field64"))) {
							checkField39();
						} else {
							TransferLogic.getInstance().gotoCommonFaileActivity("校验服务器响应数据失败，请重新交易");
						}
					} else {
						byte[] tempByte = new byte[respByte.length - 8 - 11];
						System.arraycopy(respByte, 11, tempByte, 0, tempByte.length);

						CheckMacHandler checkHandler = new CheckMacHandler();
						FSKOperator.execute("Get_CheckMAC|int:0,int:0,string:null,string:" + StringUtil.bytes2HexString(tempByte) + ",string:" + receiveFieldMap.get("field64"), checkHandler);// 计算MAC的数据+MAC（8字节）

					}

				} else {
					checkField39();
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void parseJson(String jsonStr) {
		Log.e("rece JSON", jsonStr);

		receiveFieldMap = new HashMap<String, String>();
		receiveFieldMap.put("fieldTrancode", transferCode);

		try {
			JSONTokener parse = new JSONTokener(jsonStr);
			JSONObject content = (JSONObject) parse.nextValue();

			@SuppressWarnings("unchecked")
			Iterator<String> keys = content.keys();
			while (keys.hasNext()) {
				String key = (String) keys.next();
				receiveFieldMap.put(key, content.getString(key));

			}

			String arg_str = receiveFieldMap.get("apires").replace(",", "");
			// 如果是上传签购单交易 500000001
			if (this.transferCode.equals("089014")) {
				if (receiveFieldMap.containsKey("field39") && receiveFieldMap.get("field39").equals("00")) {
					Message message = new Message();
					message.what = 0; // 回调TransferLogic
					message.obj = receiveFieldMap;
					message.setTarget(handler);
					message.sendToTarget();
				}

			} else {
				if (transferModel.shouldMac()) {
					if (receiveFieldMap.get("macstr").length() == 0) {
						if (this.transferCode.equals("089018") || this.transferCode.equals("089006")) {

						} else {
							TransferLogic.getInstance().gotoCommonFaileActivity("操作失败");
						}
						
					} else {
						if (receiveFieldMap.get("mac") != null) {
							String md5key = ApplicationEnvironment.getInstance().getPreferences().getString(Constant.MD5KEY, "");
							if ((receiveFieldMap.get("mac").equals(StringUtil.MD5Crypto(receiveFieldMap.get("macstr") + md5key)))) {
								Message message = new Message();
								message.what = 0; // 回调TransferLogic
								message.obj = receiveFieldMap;
								message.setTarget(handler);
								message.sendToTarget();
							} else {
								TransferLogic.getInstance().gotoCommonFaileActivity("操作失败");
								
							}
						}
					}
				} else {
					Message message = new Message();
					message.what = 0; // 回调TransferLogic
					message.obj = receiveFieldMap;
					message.setTarget(handler);
					message.sendToTarget();
				}
			}

		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	private void checkField39() {
		if (receiveFieldMap.containsKey("field39")) {
			String field39 = receiveFieldMap.get("field39");

			// 收到应答后，如果此交易是一笔可能发冲正的交易且响应码不是98，则删除冲正表中的此条交易记录
			if (!field39.equals("98") && AppDataCenter.getReversalMap().containsKey(this.transferCode)) {
				ReversalDBHelper helper = new ReversalDBHelper();
				helper.deleteAReversalTrans(receiveFieldMap.get("field11"));
			}

			if (field39.equals("00")) {
				// 只有在交易成功的时候取服务器日期
				if (receiveFieldMap.containsKey("field13")) {
					AppDataCenter.setServerDate(receiveFieldMap.get("field13"));
				}

				if (AppDataCenter.getReversalMap().containsValue(this.transferCode)) {
					// 交易成功。如果这笔交易是冲正交易，则要更新冲正表，将这笔交易的状态置为冲正成功。
					ReversalDBHelper helper = new ReversalDBHelper();
					helper.updateReversalState(receiveFieldMap.get("field11"));
				}

				Message message = new Message();
				message.what = 0; // 回调TransferLogic
				message.obj = receiveFieldMap;
				message.setTarget(handler);
				message.sendToTarget();

			} else if (field39.equals("98")) { // 当39域为98时要冲正。98 - 银联收不到发卡行应答
				TransferLogic.getInstance().gotoCommonFaileActivity("没有收到发卡行应答");
				TransferLogic.getInstance().reversalAction();

			} else {
				// 39域不为00，交易失败，跳转到交易失败界面。其它失败情况比如MAC计算失败直接弹窗提示用户重新交易。
				// 如果是点付宝出现异常，已在FSKService中直接处理掉
				String str_error = "交易失败，请重试!";
				if (field39.equals("A0")) {
					str_error = "校验错，请重新签到！";
				} else if (field39.equals("A1")) {
					str_error = "A1！";
				} else {
					Integer error = Integer.valueOf(field39);
					switch (error) {
					case 03:
						str_error = "商户未登记";
						break;
					case 04:
					case 07:
					case 34:
					case 37:
					case 41:
					case 43:
						str_error = "没收卡,请联系收单行";
						break;
					case 13:
						str_error = "交易金额超限,请重试";
						break;
					case 14:
						str_error = "无效卡号,请联系发卡行";
						break;
					case 31:
					case 15:
						str_error = "此卡不能受理";
						break;
					case 22:
						str_error = "操作有误,请重试";
						break;
					case 33:
					case 54:
						str_error = "过期卡,请联系发卡行";
						break;
					case 35:
						str_error = "非会员卡或会员信息错";
						break;
					case 36:
						str_error = "非会员卡,不能做此交易";
						break;
					case 38:
						str_error = "密码错误次数超限";
						break;
					case 55:
						str_error = "密码错,请重试";
						break;
					case 58:
						str_error = "终端无效,请联系收单行或银联";
						break;
					case 61:
						str_error = "金额太大";
						break;
					case 65:
						str_error = "超出取款次数限制";
						break;
					case 67:
						str_error = "没收卡";
						break;
					case 68:
						str_error = "交易超时,请重试";
						break;
					case 75:
						str_error = "密码错误次数超限";
						break;
					case 77:
						str_error = "请向网络中心签到";
						break;
					case 79:
						str_error = "POS 终端重传脱机数据";
						break;
					case 01:
					case 02:
					case 05:
					case 06:
					case 19:
					case 20:
					case 21:
					case 23:
					case 25:
					case 39:
					case 40:
					case 42:
					case 44:
					case 52:
					case 53:
					case 56:
					case 57:
					case 59:
					case 60:
					case 62:
					case 63:
					case 64:
					case 93:
						str_error = "交易失败，请联系发卡行！";
						break;
					case 9:
					case 12:
					case 30:
					case 90:
					case 91:
					case 92:
					case 94:
					case 95:
					case 96:
					case 98:
						str_error = "交易失败，请重试!";
						break;
					case 51:
						str_error = "余额不足,请查询";
						break;
					case 99:
						str_error = "校验错，请重新签到！";
						break;
					case 97:
						str_error = "终端未登记,请联系收单行或银联";
						break;
					default:
						break;
					}
				}

				TransferLogic.getInstance().gotoCommonFaileActivity(str_error);
			}
		} else {
			// 没有收到39域
			TransferLogic.getInstance().gotoCommonFaileActivity("交易失败，请重试 (39)");
		}
	}

	@SuppressLint("HandlerLeak")
	class CalcMacHandler extends Handler {

		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case 0:
				CommandReturn cmdReturn = (CommandReturn) msg.obj;
				if (cmdReturn.Return_Result == 0) { // mac计算成功
					if (transferModel.isJson()) {
						try {
							tmp_mac = Util.BytesToString(cmdReturn.Return_PSAMMAC);
							sendJSONStringer.key("mac").value(Util.BytesToString(cmdReturn.Return_PSAMMAC));
							sendJSONStringer.endObject();

							sendPacket();
						} catch (JSONException e) {
							e.printStackTrace();
						} catch (IllegalStateException e) {
							e.printStackTrace();
						}
					} else {
						try {
							byte[] tempByte = new byte[sendByte.length];
							System.arraycopy(sendByte, 0, tempByte, 0, sendByte.length - 8);
							System.arraycopy(cmdReturn.Return_PSAMMAC, 0, tempByte, tempByte.length - 8, 8);

							sendByte = tempByte;
							sendPacket();

						} catch (IllegalStateException e) {
							e.printStackTrace();
						}
					}

				} else { // mac计算失败
					BaseActivity.getTopActivity().showDialog(BaseActivity.MODAL_DIALOG, "加密数据时出现异常，请重试.");
				}

				break;
			}
		}
	}

	@SuppressLint("HandlerLeak")
	class CheckMacHandler extends Handler {

		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case 0:
				if (transferModel.isJson()) {
					Message message = new Message();
					message.what = 0; // 回调TransferLogic
					message.obj = receiveFieldMap;
					message.setTarget(handler);
					message.sendToTarget();
				} else {
					checkField39();
				}

				break;

			case FSKService.RESULT_FAILED_CHECKMAC:
				// 如果是有对应冲正的交易，则发起第一次的自动冲正
				if (AppDataCenter.getReversalMap().containsKey(transferCode)) {
					TransferLogic.getInstance().gotoCommonFaileActivity("校验服务器响应数据失败");

					TransferLogic.getInstance().reversalAction();
				} else {
					TransferLogic.getInstance().gotoCommonFaileActivity("校验服务器响应数据失败，请重新交易");
				}
				break;
			}
		}

	}

}
