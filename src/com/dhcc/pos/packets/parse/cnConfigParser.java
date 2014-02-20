package com.dhcc.pos.packets.parse;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.TreeMap;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import com.dhcc.pos.packets.CnMessage;
import com.dhcc.pos.packets.CnMessageFactory;
import com.dhcc.pos.packets.cnType;

/**
 * 配置文件解析器
 * 
 * @author maple
 * 
 */
public class cnConfigParser {

	static CnMessageFactory mfact = null;

	/**
	 * 通过xml文件创建消息工厂，
	 * 
	 * @param filepath
	 *            xml 文件完整路径
	 * @return
	 * @throws Exception
	 */
	public static CnMessageFactory createFromXMLConfigFile(InputStream stream) throws Exception {
		mfact = CnMessageFactory.getInstance();

		try {

			if (stream != null) {
				try {
					// 解析
					parse(mfact, stream);
				} finally {
					try {
						stream.close();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			// throws new FileNotFoundException("找不到文件");
		}
		return mfact;
	}

	/**
	 * 解析xml文件并初始化相关配置信息
	 * 
	 * @param mfact
	 * @param stream
	 * @throws IOException
	 */
	protected static void parse(CnMessageFactory mfact, InputStream stream) throws IOException {
		final DocumentBuilderFactory docfact = DocumentBuilderFactory.newInstance();
		/**
		 * 变量
		 * */
		DocumentBuilder docb = null;
		Document doc = null;
		NodeList nodes = null;
		Element root, elem = null;

		try {
			docb = docfact.newDocumentBuilder();
			doc = docb.parse(stream);
		} catch (ParserConfigurationException ex) {
			System.out.println("Cannot parse XML configuration:" + ex);
			return;
		} catch (SAXException ex) {
			System.out.println("Parsing XML configuration：" + ex);
			return;
		}
		root = doc.getDocumentElement();
		// Read the j8583cn message configure headers
		nodes = root.getElementsByTagName("header");
		System.out.println("############\theader :\t############");

		for (int i = 0; i < nodes.getLength(); i++) {
			elem = (Element) nodes.item(i);
			int headerLength = Integer.parseInt(elem.getAttribute("headerLength"));
			int tpduLength = Integer.parseInt(elem.getAttribute("tpduLength"));

			if (elem.getChildNodes() == null || elem.getChildNodes().getLength() == 0) {
				throw new IOException("Invalid header element");
			}

			String msgtypeid = elem.getChildNodes().item(0).getNodeValue();

			if (msgtypeid.length() != 4) {
				throw new IOException("Invalid msgtypeid for header: " + elem.getAttribute("msgtypeid"));
			}

			/** 设置tpdu 报文头 */
			mfact.setTPDUlengthAttr(msgtypeid, tpduLength);
			mfact.setHeaderLengthAttr(msgtypeid, headerLength);

			System.out.println("Adding 8583 header for msgtypeid: " + msgtypeid + "tpduLength:" + tpduLength + "  headerLength: " + headerLength);
		}

		// Read the message templates
		nodes = root.getElementsByTagName("template");

		System.out.println("############\ttemplate:new cnMessage()\t############");
		CnMessage m = null;

		for (int i = 0; i < nodes.getLength(); i++) {
			elem = (Element) nodes.item(i);
			String msgtypeid = elem.getAttribute("msgtypeid");
			if (msgtypeid.length() != 4) {
				throw new IOException("Invalid type for template: " + msgtypeid);
			}
			NodeList fields = elem.getElementsByTagName("field");
			m = new CnMessage();
			m.setMsgTypeID(msgtypeid);

			for (int j = 0; j < fields.getLength(); j++) {
				Element f = (Element) fields.item(j);
				int fieldid = Integer.parseInt(f.getAttribute("id"));
				cnType datatype = cnType.valueOf(f.getAttribute("datatype"));
				int length = 0;
				if (f.getAttribute("length").length() > 0) {
					length = Integer.parseInt(f.getAttribute("length"));
				}

				String init_filed_data = f.getChildNodes().item(0) == null ? null : f.getChildNodes().item(0).getNodeValue();

				m.setValue(fieldid, init_filed_data, datatype, length);
			}
			mfact.addMessageTemplate(m);
		}

		// Read the parsing guides
		nodes = root.getElementsByTagName("parseinfo");
		System.out.println("############\tparseinfo: map\t############");
		for (int i = 0; i < nodes.getLength(); i++) {
			elem = (Element) nodes.item(i);
			String msgtypeid = elem.getAttribute("msgtypeid");

			if (msgtypeid.length() != 4) {
				throw new IOException("Invalid type for parse guide: " + msgtypeid);
			}
			NodeList fields = elem.getElementsByTagName("field");

			/*
			 * new cnFieldParseInfo(datatype, length) 给解析的字段赋值 以fieldid作为key
			 * cnFieldParseInfo内值为value
			 */
			Map<Integer, cnFieldParseInfo> parseMap = new TreeMap<Integer, cnFieldParseInfo>();

			for (int j = 0; j < fields.getLength(); j++) {
				Element f = (Element) fields.item(j);
				int fieldid = Integer.parseInt(f.getAttribute("id"));

				cnType datatype = cnType.valueOf(f.getAttribute("datatype"));
				int length = 0;
				if (f.getAttribute("length").length() > 0) {
					length = Integer.parseInt(f.getAttribute("length"));
				}

				boolean isOk = false;
				if (f.getAttribute("isOk") != null && !(f.getAttribute("isOk").equalsIgnoreCase("false"))) {
					isOk = Boolean.parseBoolean(f.getAttribute("isOk"));
				}

				/*
				 * new cnFieldParseInfo(datatype, length) 给解析的字段赋值
				 * 然后以fieldid作为key 把上面内容放入parseMap中
				 */
				parseMap.put(fieldid, new cnFieldParseInfo(datatype, length, isOk));

			}
			/*
			 * 以msgtypeid作为key 将parseMap放入（CnMessageFactory）的ParseMap之中
			 */
			mfact.setParseMap(msgtypeid, parseMap);
		}

	}

}
