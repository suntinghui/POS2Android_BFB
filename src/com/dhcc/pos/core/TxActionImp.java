package com.dhcc.pos.core;

import java.math.BigDecimal;
import java.nio.ByteBuffer;
import java.text.ParseException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import com.bfb.pos.agent.client.ApplicationEnvironment;
import com.bfb.pos.agent.client.Constant;
import com.bfb.pos.R;
import com.dhcc.pos.packets.CnMessage;
import com.dhcc.pos.packets.CnMessageFactory;
import com.dhcc.pos.packets.cnType;
import com.dhcc.pos.packets.parse.cnConfigParser;
import com.dhcc.pos.packets.parse.cnFieldParseInfo;
import com.dhcc.pos.packets.util.ConvertUtil;

public class TxActionImp {

	CnMessageFactory mfact;

	CnMessage m;

	private Map<String, Object> req_map = null;
	private String clientTransferCode = "";

	String TPDU = "6000120000";
	String msgHeader = "603110000000";

	public byte[] first(Map<String, Object> reqMap) {
		req_map = reqMap;

		// 请求报文中取得交易码
		clientTransferCode = (String) req_map.get("fieldTrancode");

		if (clientTransferCode == null)
			throw new IllegalArgumentException("请求报文未有消息类型(交易码)");
		if (clientTransferCode.length() < 4)
			throw new IllegalArgumentException("请求报文异常交易码:" + clientTransferCode);

		String msgType = clientTransferCode.substring(0, 4);
		req_map.put("fieldTrancode", msgType);

		return this.process();
	}

	/*
	 * 主处理函数
	 */
	public byte[] process() {
		System.out.println("\t####################process####################" + "\r");

		try {
			// 创建请求数据
			byte[] reqMsg = beforeProcess();
			
			return reqMsg;

			/**
			socketTransport = new SocketTransport();

			respMsg = socketTransport.sendData(reqMsg);

			// 解析返回(响应)数据
			afterProcess(respMsg);
			*/
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public CnMessage registerReqMsg() {
		String msgType = (String) req_map.get("fieldTrancode");

		Map<Integer, cnFieldParseInfo> parseMap = mfact.getParseMap(msgType);

		m.setBinary(true);

		/**
		 * 设置TPDU的数据
		 * */
		if (m.setMessageTPDUData(0, TPDU.getBytes()) == false) {
			System.out.println("设置TPDU出错。");
			System.exit(-1);
		}
		
		/**
		 * 设置报文头的数据
		 * */

		if (m.setMessageHeaderData(0, msgHeader.getBytes()) == false) {
			System.out.println("设置报文头出错。");
			System.exit(-1);
		}
		/* =================变量声明================= */
		Iterator<Integer> it = parseMap.keySet().iterator();
		/** 字段 */
		int fieldId = 0;
		/** 字段值 */
		Object value = null;
		
		cnFieldParseInfo xfield = null;
		/**字段类型*/
		cnType type = null;
		
		// if(!req_map.isEmpty()){
		while (it.hasNext()) {
			fieldId = it.next();

			if (parseMap.get(fieldId) != null && !parseMap.get(fieldId).equals("")) {

				xfield = parseMap.get(fieldId);
				type = xfield.getType();
				/** 屏蔽请求报文的无值域 req_map中不为空时有效 */
				if (req_map.get("field" + String.valueOf(fieldId)) != null && !req_map.get("field" + String.valueOf(fieldId)).equals("")) {

					value = req_map.get("field" + String.valueOf(fieldId));
					/* 当等于0时为变量域 故此拿请求过来的值求出大小 */
					if (type != cnType.AMOUNT) {
						if (xfield.getLength() != 0 && xfield.getIsOk() == true) {
							if (value == null || value.equals("")) {
								throw new IllegalArgumentException(
										String.valueOf(fieldId)
												+ " 域 is must input! ");
							} else if (value.toString().length() != xfield
									.getLength()) {
								if (value.toString().length() < xfield
										.getLength()) {
									throw new IllegalArgumentException(
											String.valueOf(fieldId)
													+ " 域值 Too Short! ");
								} else {
									throw new IllegalArgumentException(
											String.valueOf(fieldId)
													+ " 域值 Too Long! ");
								}

							}
							m.setValue(fieldId, value, type, xfield.getLength());
						} else if (xfield.getLength() == 0 && xfield.getIsOk() == true) {
							if (value == null) {
								throw new IllegalArgumentException(String.valueOf(fieldId) + " 域 is must input! ");
							}

							if (type == cnType.AMOUNT) {

								m.setValue(fieldId, BigDecimal.valueOf(Double.parseDouble(value.toString())), type, xfield.getLength());
							} else {
								m.setValue(fieldId, value, type, value.toString().length());
							}
						} else if (xfield.getLength() != 0 && xfield.getIsOk() == false) {
							if (value != null) {
								if (value.toString().length() != xfield
										.getLength()) {
									if (value.toString().length() < xfield
											.getLength()) {
										throw new IllegalArgumentException(
												String.valueOf(fieldId)
														+ " 域值 Too Short! ");
									} else {
										throw new IllegalArgumentException(
												String.valueOf(fieldId)
														+ " 域值 Too Long!");
									}

								}

								if (type == cnType.AMOUNT) {

									m.setValue(fieldId, BigDecimal.valueOf(Double.parseDouble(value.toString())), type, xfield.getLength());
								} else {
									m.setValue(fieldId, value, type, value.toString().length());
								}

							}

						} else if (xfield.getLength() == 0 && xfield.getIsOk() == false) {
							if (value != null) {
								if (!value.toString().trim().equals(""))
									m.setValue(fieldId, value, type, value.toString().length());
							}

						}
					} else {
						m.setValue(fieldId, BigDecimal.valueOf(Double.parseDouble(value.toString()) / 100.00), type, xfield.getLength());
					}
				}
			} else {
				System.out.println("没有此'" + parseMap.get(fieldId) + "'的value");
			}
		}

		return m;
	}

	/**
	 * 组装报文
	 * 
	 * @param m
	 *            ,context
	 * @return reqMsg (tpdu+头文件+报文类型+位图+位图对应的域值)
	 */
	private byte[] depacketize() {
		/* 组装请求报文 */
		byte[] reqMsg = null;
		/* TPDU */
		byte[] msgTPDU = null;
		/* 头文件 */
		byte[] msgHeader = null;
		/* 报文类型 */
		byte[] msgtypeid = null;

		/**
		 * 进行BCD码压缩
		 * */
		msgTPDU = ConvertUtil.byte2BCD(m.getmsgTPDU());
		msgHeader = ConvertUtil.byte2BCD(m.getmsgHeader());
		msgtypeid = ConvertUtil._str2Bcd(m.getMsgTypeID());

		/**
		 * data :位图[8字节]+ {11域【3字节BCD码】+其余所有域值（个别域值前加上BCD码压缩的2个字节的长度值_左补0）}
		 * */
		byte[] data = m.writeInternal();

		System.out.println("位图和域值长度:\t" + data.length);
		System.out.println("位图和域值:\t" + ConvertUtil.trace(data));

		/**
		 * 组装字节类型报文；（tpdu[BCD压缩5字节]+头文件[BCD压缩6字节]）+
		 * 报文类型【BCD压缩2字节】+位图【8字节】&&位图对应的域值
		 * */
		ByteBuffer sendBuf = ByteBuffer.allocate(msgTPDU.length + msgHeader.length + msgtypeid.length + data.length);
		/* TPDU */
		sendBuf.put(msgTPDU);
		/* 头文件 */
		sendBuf.put(msgHeader);
		/* 报文类型 */
		sendBuf.put(msgtypeid);
		/* 位图+位图对应的域值 */
		sendBuf.put(data);

		reqMsg = sendBuf.array();
		

		return reqMsg;
	}

	/*
	 * 创建消息工厂并创建请求 context容器设置tranCode、req_map、req_xml
	 */
	public byte[] beforeProcess() {
		System.out.println("\t####################beforeProcess####################" + "\r");
		
		/**
		 * 通过指定的报文配置文件创建消息工厂（mfact）
		 * */
		try {
			/* 通过xml消息配置文件创建消息工厂， */
			if(!Constant.isAISHUA){
				mfact = cnConfigParser.createFromXMLConfigFile(ApplicationEnvironment.getInstance().getApplication().getResources().openRawResource(R.raw.msg_config));
			}else{
				mfact = cnConfigParser.createFromXMLConfigFile(ApplicationEnvironment.getInstance().getApplication().getResources().openRawResource(R.raw.msg_config_aishua));
			}
			

		} catch (Exception e) {
			e.printStackTrace();
		}

		/**
		 * 根据模板创建并初始化一个报文对象（如果配置文件中没有配置template会跳过）
		 * */
		m = mfact.newMessagefromTemplate((String)this.req_map.get("fieldTrancode"));

		m = registerReqMsg();

		print(m);

		byte[] reqMsg = depacketize();

		return reqMsg;
	}

	/*
	 * 解析返回（响应）数据 context容器设置resp_map、resp_json
	 */
	public HashMap<String, Object> afterProcess(byte[] resp_byte) {
		System.out.println("\t##########################afterProcess##########################" + "\r");
		HashMap<String, Object> resp_map = new HashMap<String, Object>();
		
		try {
			resp_map.put("fieldTrancode", this.clientTransferCode);

			CnMessage resp_msg = mfact.parseMessage(resp_byte, mfact.getTPDUlengthAttr((String) this.req_map.get("fieldTrancode")), mfact.getHeaderLengthAttr((String) this.req_map.get("fieldTrancode")));
			for (int i = 0; i < 128; i++) {
				if (resp_msg.hasField(i)) {
					resp_map.put("field" + i, resp_msg.getField(i).toString());
				}
			}

			System.out.println("\rResp_json:\r" + resp_byte + "\r");

		} catch (NullPointerException e) {
			throw new NullPointerException();
		} catch (ParseException e) {
			e.printStackTrace();
		}
		
		return resp_map;
	}

	// 输出一个报文内容
	private static void print(CnMessage m) {
		System.out.println("--------------------NEW MESSAGE--------------------------------- " + m.getField(11));
		System.out.println("Message TPDU = \t[" + new String(m.getmsgTPDU()) + "]");
		System.out.println("Message Header = \t[" + new String(m.getmsgHeader()) + "]");
		System.out.println("Message TypeID = \t[" + m.getMsgTypeID() + "]");
		m.hasField(1);
		
		for (int i = 2; i < 128; i++) {
			if (m.hasField(i)) {
				System.out.println("Field: " + i + " <" + m.getField(i).getType() + ">\t(" + m.getField(i).getLength() + ")\t[" + m.getField(i).toString() + "]" + "      \t[" + m.getObjectValue(i) + "]");
			}
		}
	}

	public void setMfact(CnMessageFactory mfact) {
		this.mfact = mfact;
	}

}
