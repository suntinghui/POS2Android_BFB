package com.bfb.pos.agent.client;

import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import org.apache.http.conn.ssl.SSLSocketFactory;

import android.util.Log;

public class MySSLSocketFactory extends SSLSocketFactory {
	
	/*
	 * http://stackoverflow.com/questions/2642777/trusting-all-certificates-using-httpclient-over-https/6378872#6378872
	 */
	
	SSLContext sslContext = SSLContext.getInstance("TLS");

    public MySSLSocketFactory(KeyStore truststore) throws NoSuchAlgorithmException, KeyManagementException, KeyStoreException, UnrecoverableKeyException {
        
    	super(truststore);

        TrustManager tm = new X509TrustManager() {
            public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {
            	Log.e("https", "检查客户端的可信任状态...");
            }

            public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {
            	Log.e("https", "检查服务器的可信任状态...");
            }

            public X509Certificate[] getAcceptedIssuers() {
            	Log.e("https", "获取接受的发行商数组...");
                return null;
            }
        };

        sslContext.init(null, new TrustManager[] {tm}, null);
        
    }
    
    
    // 只有实现下面这两个方法才会调用上面的方法。则能实现相关的检查
    // BUT!!! 现在不用做任何检查就可以实现验证服务器证书？？？
    @Override
    public Socket createSocket() throws IOException {
    	Log.e("MySSLSocketFactory", "createSocket 1");
        return sslContext.getSocketFactory().createSocket();
    }
    
    @Override
    public Socket createSocket(Socket socket, String host, int port, boolean autoClose) throws IOException, UnknownHostException {
    	Log.e("MySSLSocketFactory", "createSocket 2");
        return sslContext.getSocketFactory().createSocket(socket, host, port, autoClose);
    }

}
