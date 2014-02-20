package com.bfb.pos.agent.client;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import org.kxml2.io.KXmlParser;
import org.kxml2.kdom.Document;
import org.kxml2.kdom.Element;
import org.kxml2.kdom.Node;

import android.util.Log;

import com.bfb.pos.util.XMLUtil;

/**
 * 
 * @author STH
 * 
 */
public class NetClient {

	private boolean initialized = false;

	public boolean getInitialized() {
		return initialized;
	}

	public void Reset() {
		initialized = false;
		// ApplicationEnvironment.sessionId = null;
		// NOTE: re-handshake again. is that right?

	}

	public byte[] transferMsg(HashMap<String, String> map) throws IOException {
		byte[] data = this.serialize(map);
		byte[] outMsg = null;
		Log.i("sendXML", new String(data, "utf-8"));
		try {
			outMsg = HttpManager.getInstance().sendRequest(HttpManager.URL_XML_TYPE, "test", data);// 非动态还需要用到吗？？？
			if (outMsg == null || outMsg.length == 0) {
				Log.i("receiveXML", "无返回值");
			} else {
				Log.i("receiveXML", new String(outMsg));
			}
		} catch (Exception e) {
			 Log.i("error", e.getMessage());
			 e.printStackTrace();
		}
		return outMsg;

	}
	
	/**
	 * 将数据序列化组装成XML发送到服务器端
	 * 
	 * @param map
	 *            从客户端过来的数据以HashMap的形式存在
	 * @return
	 */
	private byte[] serialize(HashMap<String, String> map) {
		try {
			Document doc = new Document();
			Element root = new Element();
			root.setName("request");
			root.setAttribute(null, "actionId", map.get("actionId"));
			doc.addChild(Node.ELEMENT, root);
			// 遍历hashmap
			Iterator<Entry<String, String>> iter = map.entrySet().iterator();
			while (iter.hasNext()) {
				Map.Entry<String, String> entry = (Map.Entry<String, String>) iter.next();
				String key = entry.getKey();
				if (!key.equals("actionId")) {
					String val = entry.getValue();
					XMLUtil.setParamElement(root, key, val);
				}
			}
			
			// 拼装XML
			String xmlBody = XMLUtil.toString(doc.getRootElement());
			if (!xmlBody.startsWith("<?xml"))
				xmlBody = "<?xml version=\"1.0\"?>" + xmlBody;

			return xmlBody.getBytes("UTF-8");

		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public Element deserialize(byte[] bytes){
		InputStream is = new ByteArrayInputStream(bytes);
		KXmlParser parser = new KXmlParser();
		try {
			BufferedReader reader = new BufferedReader(new InputStreamReader(is),8192);   
	        StringBuilder sb = new StringBuilder();   
	        String line = null; 
            while ((line = reader.readLine()) != null) {   
            	if (line.trim().length() > 0){
            		sb.append(line);  
            	}
             }
			
            is = new ByteArrayInputStream(sb.toString().getBytes());
            
			parser.setInput(is, "UTF-8");
			Document doc = new Document();
			doc.parse(parser);
			
			Element root = doc.getRootElement();
			return root;
		}catch(Exception e){
			e.printStackTrace();
			return null;
		}
	}

}
