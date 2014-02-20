package com.dhcc.pos.packets;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import com.dhcc.pos.packets.parse.cnFieldParseInfo;
import com.dhcc.pos.packets.util.ConvertUtil;

/**
 * 消息工厂
 */
public class CnMessageFactory {

	private static CnMessageFactory instance = new CnMessageFactory();

	private CnMessageFactory() {
		try {
			// ConfigParser.createFromXMLConfigFile(null);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static CnMessageFactory getInstance() {

		return instance;

	}

	/**
	 * typeTemplates在createFromXMLConfigFile时赋值 用来存放各种类型的消息 ,格式：(
	 * msgtypeid,cnMessage) msgtypeid 消息类型 cnMessage 消息
	 */
	private Map<String, CnMessage> typeTemplates = new HashMap<String, CnMessage>();

	/**
	 * 存放用来解析的字段，格式:((msgtypeid, (fieldID, fieldInfo)) msgtypeid 消息类型 cnMessage
	 * 消息 fieldInfo 字段信息
	 */
	private Map<String, Map<Integer, cnFieldParseInfo>> parseMap = new TreeMap<String, Map<Integer, cnFieldParseInfo>>();

	/**
	 * 字段出现的消息中的顺序，格式 ：( msgtypeid, fieldID) msgtypeid 消息类型 fieldID 字段id
	 */
	private Map<String, List<Integer>> parseOrder = new HashMap<String, List<Integer>>();

	/**
	 * 报头包含的消息类型,格式 (msgtypeid, headerlength) msgtypeid 消息类型 headerlength 报头长度
	 */
	private Map<String, Integer> msgHeadersAttr = new HashMap<String, Integer>();

	/**
	 * 报头包含的消息类型,格式 (msgtypeid, TPDUlength) msgtypeid 消息类型 TPDUlength TPDU头长度
	 */
	private Map<String, Integer> msgTPDUlengthAttr = new HashMap<String, Integer>();

	/**
	 * 二进消息标识符，用来表示创建或解析时，使用的是二进制消息 true 使用的是二进制消息 false，用来表示创建或解析时，使用的是ASCII码
	 * 默认为 false，
	 */
	private boolean useBinary;

	/**
	 * 设置二进消息标识符，
	 * 
	 * @param flag
	 *            ：true 使用的是二进制消息
	 */
	public void setUseBinary(boolean flag) {
		useBinary = flag;
	}

	/**
	 * 获取二进消息标识符，
	 * 
	 * @return
	 */
	public boolean getUseBinary() {
		return useBinary;
	}

	/**
	 * 通过模板中指定的消息类型id创建报文（消息） 并根据该模板中已有的域给位图赋值 设定了： etx: 报文组装完成设置结束符 Binary:
	 * 如果设置为true, 报文中的各报文域按照二进制组成报文 Fields: bit map 位图 设置字段域，由于字段域1被
	 * 用来存放位图，设置字段域应从2开始 field7: 当前日期 field11: 系统跟踪号
	 */
	public CnMessage newMessagefromTemplate(String msgtypeid) {
		CnMessage m = new CnMessage(msgtypeid, msgTPDUlengthAttr.get(msgtypeid), msgHeadersAttr.get(msgtypeid));
		CnMessage templ = null;
		// 是否使用二进制
		m.setBinary(useBinary);

		/**
		 * Copy the values from the template (通过报文模板来赋初值)
		 * 当创建消息工厂时如果消息配置文件未配置template 此template为null （此template多为测试用）
		 * */

		templ = typeTemplates.get(msgtypeid);

		if (templ != null) {
			for (int i = 2; i < 128; i++) {
				if (templ.hasField(i)) {
					/* 给bit map位图赋值 */
					m.setField(i, templ.getField(i).clone());
				}
			}
		}
		return m;
	}

	/**
	 * 通过字节数组来创建消息
	 * 
	 * @param buf
	 *            8583 消息字节数组
	 * @param msgTPDUlength
	 *            TPDU长度
	 * @param msgheaderlength
	 *            报文长度
	 * @return
	 */
	public CnMessage parseMessage(byte[] respMsg, int msgTPDUlength, int msgHeaderLength) throws ParseException {
		CnMessage m = null;
		byte[] msgTypeByte = new byte[2];
		/* 消息类型 */
		String msgType = null;

		String TPDU = null;
		String msgHeader = null;
		/* 位图起止位置 */
		int bitmapStart = -1;
		/* 位图结束位置 */
		int bitmapEnd = -1;
		/**
		 * 拿到消息类型
		 * */
		System.arraycopy(respMsg, msgTPDUlength / 2 + msgHeaderLength / 2, msgTypeByte, 0, 2);

		msgType = ConvertUtil._bcd2Str(msgTypeByte);

		/**
		 * 
		 * */
		m = new CnMessage(msgType, msgTPDUlength, msgHeaderLength);
		// TODO it only parses ASCII messages for now

		/**
		 * 得到TPDU信息 由于报头为bcd压缩故此除2
		 * */
		msgTPDUlength = msgTPDUlength / 2;
		byte[] msgTPDUData = new byte[msgTPDUlength];
		System.arraycopy(respMsg, 0, msgTPDUData, 0, msgTPDUlength);
		TPDU = ConvertUtil._bcd2Str(msgTPDUData);
		/**
		 * 设置TPDU的数据
		 * */
		if (m.setMessageTPDUData(0, TPDU.getBytes()) == false) {
			System.out.println("设置TPDU出错。");
			System.exit(-1);
		}

		/**
		 * 得到报文头信息 由于报头为bcd压缩故此除2
		 * */
		msgHeaderLength = msgHeaderLength / 2;
		byte[] msgHeaderData = new byte[msgHeaderLength];
		System.arraycopy(respMsg, msgTPDUlength, msgHeaderData, 0, msgHeaderLength);
		msgHeader = ConvertUtil._bcd2Str(msgHeaderData);
		/**
		 * 设置报文头的数据
		 * */
		if (m.setMessageHeaderData(0, msgHeader.getBytes()) == false) {
			System.out.println("设置报文头出错。");
			System.exit(-1);
		}

		System.out.println("respMsg TPDU: \t[" + new String(m.getmsgTPDU()) + "]");
		System.out.println("respMsg Hearder: \t[" + new String(m.getmsgHeader()) + "]");

		/**
		 * 位图
		 * */
		byte[] bitmap = new byte[8];

		System.arraycopy(respMsg, msgTPDUlength + msgHeaderLength + msgTypeByte.length, bitmap, 0, bitmap.length);
		System.out.println("接收到的位图：\r" + ConvertUtil.trace(bitmap));

		/** 位图以base64形式显示 */

		String bitmapStr = ConvertUtil.bytesToHexString(bitmap);
		System.out.println("bitmap:" + bitmapStr);

		// Parse the bitmap (primary first)
		BitSet bs = new BitSet(64);
		int pos = 0;

		bitmapStart = msgTPDUlength + msgHeaderLength + msgTypeByte.length;
		bitmapEnd = msgTPDUlength + msgHeaderLength + msgTypeByte.length + 8;

		for (int i = bitmapStart; i < bitmapEnd; i++) {
			int bit = 128;
			for (int b = 0; b < 8; b++) {
				/* (respMsg[i] & bit) != 0 时此域为有效域 */
				bs.set(++pos, (respMsg[i] & bit) != 0);
				/* 右移一位代表初始值128/2的值 */
				bit >>= 1;
			}
		}

		/**
		 * 当bs.get(0)为true时代表有扩展位图此为128位 16个字节；
		 * */
		// Check for secondary bitmap and parse if necessary
		if (bs.get(0)) {
			for (int i = bitmapStart + 8; i < bitmapStart + 8 * 2; i++) {
				int bit = 128;
				for (int b = 0; b < 8; b++) {
					bs.set(++pos, (respMsg[i] & bit) != 0);
					bit >>= 1;
				}
			}
			/* tpdu长+头文件长度+消息类型+位图16（当位图第0位为true时 代表是128位位图，故此占16个字节） */
			pos = bitmapStart + 8 * 2;
			bitmapEnd = bitmapEnd + 8;
		} else {
			/* tpdu长+头文件长度+消息类型+位图8（当位图第0位为false时 代表是64位位图，故此占8个字节） */

			pos = bitmapEnd;

		}

		System.out.println("\t位图: \t" + bs.toString());

		// Parse each field
		Map<Integer, cnFieldParseInfo> parseGuide = parseMap.get(m.getMsgTypeID());
		List<Integer> index = parseOrder.get(m.getMsgTypeID()); // 该类型报文应该存在的域ID集合
		System.out.println("\tindex:\t" + index);
		if (index == null) {
			RuntimeException e = new RuntimeException("在XML文件中未定义报文类型[" + m.getMsgTypeID() + "]的解析配置, 无法解析该类型的报文!! 请完善配置!");
			System.out.println(e.getMessage());

			throw e;
		}

		// 检查2到128，如果收到的报文位图中指示有此域，而XML配置文件中确未指定，则报警告！
		for (int fieldnum = 2; fieldnum <= 128; fieldnum++) {
			if (bs.get(fieldnum)) {
				if (!index.contains(Integer.valueOf(fieldnum))) { // 不包含时
					System.out.println("收到类型为[" + m.getMsgTypeID() + "]的报文中的位图指示含有第[" + fieldnum + "]域,但XML配置文件中未配置该域. 这可能会导致解析错误,建议检查或完善XML配置文件！");
				}
			}
		}
		for (Integer i : index) {
			cnFieldParseInfo fpi = parseGuide.get(i);
			String value62 = null;
			if (bs.get(i)) {
				/* 别处声明此useBinary */
				useBinary = true;
				cnValue val = useBinary ? fpi.parseBinary(respMsg, pos, i) : fpi.parseBinary(respMsg, pos, i);

				m.setField(i, val);
				if (i == 62) {
					value62 = String.valueOf(m.getField(i).getValue());
					m.setValue(i, value62, cnType.LLLVAR, value62.length());
				}
				System.out.println("\tField=【" + i + "】\t< " + m.getField(i).getType() + "  >\t^" + pos + "^\t(" + m.getField(i).getLength() + ")\t[" + m.getField(i).toString() + "]\t--->\t[" + m.getObjectValue(i) + "]");
				/***/

				/***/
				if (useBinary && !(val.getType() == cnType.ALPHA || val.getType() == cnType.LLVAR || val.getType() == cnType.LLLVAR)) {
					pos += (val.getLength() / 2) + (val.getLength() % 2);
				} else {
					pos += val.getLength();
				}
				if (val.getType() == cnType.LLVAR) {
					pos += useBinary ? 1 : 2;
				} else if (val.getType() == cnType.LLLVAR) {
					pos += useBinary ? 2 : 3;
				} else if (val.getType() == cnType.LLNVAR) {
					pos += useBinary ? 1 : 2;
				} else if (val.getType() == cnType.LLLNVAR) {
					pos += useBinary ? 2 : 3;
				}
			}
		}
		return m;
	}

	public void setHeaders(Map<String, Integer> value) {
		msgHeadersAttr.clear();
		msgHeadersAttr.putAll(value);
	}

	public void setTPDU(Map<String, Integer> value) {
		msgTPDUlengthAttr.clear();
		msgTPDUlengthAttr.putAll(value);
	}

	public void setTPDUlengthAttr(String msgtypeid, Integer TPDUlength) {
		msgTPDUlengthAttr.put(msgtypeid, TPDUlength);
	}

	public Integer getTPDUlengthAttr(String msgtypeid) {
		return msgTPDUlengthAttr.get(msgtypeid);
	}

	public void setHeaderLengthAttr(String msgtypeid, Integer headerlen) {
		msgHeadersAttr.put(msgtypeid, headerlen);
	}

	public Integer getHeaderLengthAttr(String msgtypeid) {
		return msgHeadersAttr.get(msgtypeid);
	}

	public void addMessageTemplate(CnMessage templ) {
		if (templ != null) {
			typeTemplates.put(templ.getMsgTypeID(), templ);
		}
	}

	public void removeMessageTemplate(String msgtypeid) {
		typeTemplates.remove(msgtypeid);
	}

	public void setMessageTemplate(String msgtypeid, CnMessage templ) {
		if (templ == null) {
			typeTemplates.remove(msgtypeid);
		} else {
			typeTemplates.put(msgtypeid, templ);
		}
	}

	public void setParseMap(String msgtypeid, Map<Integer, cnFieldParseInfo> map) {
		parseMap.put(msgtypeid, map);
		ArrayList<Integer> index = new ArrayList<Integer>();
		index.addAll(map.keySet());

		/* 升序排序 */
		Collections.sort(index);

		System.out.println("Adding parse map for type: [" + msgtypeid + "] with fields " + index);

		/* 升序排序的域赋给parseOrder */
		parseOrder.put(msgtypeid, index);
	}

	public Map<Integer, cnFieldParseInfo> getParseMap(String msgtypeid) {
		return parseMap.get(msgtypeid);
	}

}
