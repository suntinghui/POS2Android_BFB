package com.bfb.pos.agent.client;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.KeyStore;
import java.security.cert.Certificate;
import java.security.cert.CertificateFactory;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.HttpVersion;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.params.HttpClientParams;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.entity.InputStreamEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;

import android.util.Log;

import com.bfb.pos.activity.BaseActivity;
import com.bfb.pos.client.exception.HttpException;
import com.bfb.pos.util.InputStreamUtils;
import com.bfb.pos.util.TrafficUtil;

public class HttpManager {
	
	private static boolean isHttpsFlag				= true;
	
	private static HttpManager instance 			= null;
	
	private final static int ConnectionTimeout 		= 27000;
	private final static int SocketTimeout 			= 32000;
	private final static int SocketBufferSize 		= 8192;
	
	byte[] reqHeaderLenght = new byte[2];// 报文头
	
	private HttpClient httpClient 					= null;
	private HttpPost httpPost 						= null;
	private boolean aborted 						= false;
	
	private static final int port = 9200;

	public static final int URL_XML_TYPE = 1;
	public static final int URL_JSON_TYPE  = 2;

	public static HttpManager getInstance() {

		if (instance == null) {
			instance = new HttpManager();
		}
		return instance;
	}
	
	private HttpClient getHttpClient(){
		if (null == httpClient){
			if(isHttpsFlag){
				httpClient = initHttps();
			} else {
				httpClient = initHttp();
			}
		}
		
		return httpClient;
	}
	
	/***
	public static String getUrl() {
		return replace(URL, "{0}", serverIp);
	}

	public String replace(String text, String oldStr, String newStr) {
		int oldLen = oldStr.length();
		int k = 0;
		while (k + oldLen < text.length()) {
			k = text.indexOf(oldStr, k);
			if (k == -1) {
				return text;
			}

			text = text.substring(0, k) + newStr
					+ text.substring(k += oldLen, text.length());
		}
		return text;
	}
	
	****/
	
	public void abort() {
		if (!aborted && null != httpPost) {
			try {
				httpPost.abort();
			} catch (Exception e) {
				e.toString();
			}
		}
		aborted = true;
	}


	private HttpClient initHttp() {
		HttpParams httpParameters = new BasicHttpParams();

		HttpConnectionParams.setConnectionTimeout(httpParameters,ConnectionTimeout);
		HttpConnectionParams.setSoTimeout(httpParameters, SocketTimeout);
		HttpConnectionParams.setSocketBufferSize(httpParameters,SocketBufferSize);
		HttpClientParams.setRedirecting(httpParameters, true); // 设置重定向，默认为true
		//HttpProtocolParams.setUserAgent(httpParameters, "MBSClient/Android-1.0");
		return new DefaultHttpClient(httpParameters);
	}
	
	private HttpClient initHttps() {
		HttpParams httpParameters = new BasicHttpParams();

		HttpProtocolParams.setVersion(httpParameters, HttpVersion.HTTP_1_1);
		HttpConnectionParams.setConnectionTimeout(httpParameters,ConnectionTimeout);
		HttpConnectionParams.setSoTimeout(httpParameters, SocketTimeout);
		HttpConnectionParams.setSocketBufferSize(httpParameters,SocketBufferSize);
		HttpClientParams.setRedirecting(httpParameters, true);
		HttpProtocolParams.setUserAgent(httpParameters, "MBSClient/Android-1.0");
		InputStream instream = null;
		
		try {
			instream = HttpManager.class.getResourceAsStream("/ylt.cer");
			//读取证书
	        CertificateFactory cerFactory = CertificateFactory.getInstance("X.509"); 
	        Certificate cer = cerFactory.generateCertificate(instream);
	        //创建一个证书库，并将证书导入证书库
	        KeyStore keyStore = KeyStore.getInstance(KeyStore.getDefaultType()); 
	        keyStore.load(null, null);
	        keyStore.setCertificateEntry("trust", cer);
	        //把证书库作为信任证书库
			//SSLSocketFactory socketFactory = new MySSLSocketFactory(keyStore);
	        
	        SSLSocketFactory socketFactory = new SSLSocketFactory(keyStore);
			socketFactory.setHostnameVerifier(SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);
			Scheme sch = new Scheme("https", socketFactory, port);
			SchemeRegistry registry = new SchemeRegistry();
			registry.register(sch);
			
			ClientConnectionManager ccm = new ThreadSafeClientConnManager(httpParameters, registry);
			
			return new DefaultHttpClient(ccm, httpParameters);
		
		}catch(Exception e){
			BaseActivity.getTopActivity().runOnUiThread(new Runnable(){
				@Override
				public void run() {
					TransferLogic.getInstance().gotoCommonFaileActivity("系统加载文件出错，请重新启动程序！");
				}
				
			});
		}
		/*
		catch (CertificateException e) {
			e.printStackTrace();
		} catch (KeyStoreException e) {
			e.printStackTrace();
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		} catch (KeyManagementException e) {
			e.printStackTrace();
		} catch (UnrecoverableKeyException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		*/
		
		
		finally{
			try {
				instream.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		return new DefaultHttpClient();
	}
	
	public byte[] sendRequest(int type, String transferCode ,byte[] outBytes) throws HttpException{
		
		// 记录上行流量
		TrafficUtil.getInstance().setTraffic(TrafficUtil.TYPE_SEND, outBytes.length);
		
		byte[] bArray = null;
	
		HttpResponse response = null;
		InputStream responseStream =null;
		
		///////////////////
//		int reqMsgLen = outBytes.length;
//
//		reqHeaderLenght[0] = (byte) ((reqMsgLen & 0xff00) >> 8);
//		reqHeaderLenght[1] = (byte) (reqMsgLen & 0xff);
//
//		/**
//		 * 组装字节类型报文 数据长度+{头文件（tpdu[BCD压缩5字节]+头文件[BCD压缩6字节]）+
//		 * 报文类型【BCD压缩2字节】+位图【8字节】&&位图对应的域值}
//		 * */
//		System.out.println("reqMsgLen：" + reqMsgLen);
//		ByteBuffer sendBuf = ByteBuffer.allocate(reqHeaderLenght.length + reqMsgLen);
//		/* 2个字节的报文长度值 */
//		sendBuf.put(reqHeaderLenght);
//		/* 头文件（tpdu[BCD压缩5字节]+头文件[BCD压缩6字节]）+ 报文类型【BCD压缩2字节】+位图【8字节】&&位图对应的域值+ */
//		sendBuf.put(outBytes);
//
//		outBytes = sendBuf.array();
		
		//////////////////
		
		if (type == HttpManager.URL_JSON_TYPE){
			httpPost = new HttpPost(Constant.JSONURL+AppDataCenter.getMethod_Json(transferCode));
		} else {
			httpPost = new HttpPost(Constant.XMLURL);
		}
		httpPost.setHeader("Content-Type", "application/octet-stream");
		
		
		try {
			//httpPost.setEntity(new StringEntity(new String(outBytes, "GBK"),"GBK"));
			InputStream is = new ByteArrayInputStream(outBytes);
			InputStreamEntity reqEntity = new InputStreamEntity(is, is.available());
			httpPost.setEntity(reqEntity);
			
//			httpPost.setEntity(new ByteArrayEntity(outBytes));
//			httpPost.setHeader("Content-type", "application/octet-stream");
		} catch (Exception e1) {
			throw new HttpException(e1.getMessage());
		}
		
		try {
			response = this.getHttpClient().execute(httpPost);
		} catch (ClientProtocolException e1) {
			// ClientProtocolException - in case of an http protocol error
			e1.printStackTrace();
			throw new HttpException(900);
			
		} catch (IOException e1) {
			e1.printStackTrace();
			// IOException - in case of a problem or the connection was aborted
			throw new HttpException(903);
		}
		
		int statusCode = response.getStatusLine().getStatusCode();
		
		if (statusCode == HttpStatus.SC_OK){
			try {
				responseStream = response.getEntity().getContent();
				if (null != responseStream){
					System.out.println("response:" + responseStream);
					bArray = InputStreamUtils.InputStreamTOByte(responseStream);
					// 记录下行流量
					TrafficUtil.getInstance().setTraffic(TrafficUtil.TYPE_RECEIVE, bArray.length);
				}
			} catch (IllegalStateException e) {
				e.printStackTrace();
				throw new HttpException(e.getMessage());
			} catch (IOException e) {
				e.printStackTrace();
				throw new HttpException(e.getMessage());
			} finally{
				if(null != responseStream)
					try {
						responseStream.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
					
				this.getHttpClient().getConnectionManager().closeExpiredConnections();
			}
			
		} else{
			String reason = response.getStatusLine().getReasonPhrase();
			Log.e("connect error", reason);
			throw new HttpException(statusCode);
		}
		
		return bArray;
		
	}
}
