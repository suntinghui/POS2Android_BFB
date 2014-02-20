package com.bfb.pos.util;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.Security;
import java.util.Properties;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.mail.Message;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import android.util.Log;

import com.bfb.pos.agent.client.Constant;
 
public class GMailSender extends javax.mail.Authenticator {    
    private Session session;    
 
    static {    
        Security.addProvider(new com.bfb.pos.util.JSSEProvider());    
    }   
 
	public native String getMailHost();
	public native String getMailUsernameFrom();
	public native String getMailPassWordFrom();
	public native String getMailUserNameTo();
	
	static {
		System.loadLibrary("hello-jni");
	}
    public GMailSender() {    
        Properties props = new Properties();    
        props.setProperty("mail.transport.protocol", "smtp");   
        Log.e("maihost:", getMailHost());
        props.setProperty("mail.host", getMailHost());    
        props.put("mail.smtp.auth", "true");    
        props.put("mail.smtp.port", "465");    
        props.put("mail.smtp.socketFactory.port", "465");    
        props.put("mail.smtp.socketFactory.class",    
                "javax.net.ssl.SSLSocketFactory");    
        props.put("mail.smtp.socketFactory.fallback", "false");    
        props.setProperty("mail.smtp.quitwait", "false");    
 
        session = Session.getDefaultInstance(props, this);    
    }    
 
    protected PasswordAuthentication getPasswordAuthentication() {  
        return new PasswordAuthentication(getMailUsernameFrom(), getMailPassWordFrom());    
    }    
 
    public synchronized void sendMail(String subject, String body) throws Exception {    
        try{ 
        MimeMessage message = new MimeMessage(session);    
        DataHandler handler = new DataHandler(new ByteArrayDataSource(body.getBytes(), "text/plain"));    
        message.setSender(new InternetAddress(getMailUsernameFrom()));    
        message.setSubject(subject);    
        message.setDataHandler(handler);    
        if (getMailUserNameTo().indexOf(',') > 0)    
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(getMailUserNameTo()));    
        else   
            message.setRecipient(Message.RecipientType.TO, new InternetAddress(getMailUserNameTo()));    
        Transport.send(message);    
        }catch(Exception e){ 
 
        } 
    }    
 
    public class ByteArrayDataSource implements DataSource {    
        private byte[] data;    
        private String type;    
 
        public ByteArrayDataSource(byte[] data, String type) {    
            super();    
            this.data = data;    
            this.type = type;    
        }    
 
        public ByteArrayDataSource(byte[] data) {    
            super();    
            this.data = data;    
        }    
 
        public void setType(String type) {    
            this.type = type;    
        }    
 
        public String getContentType() {    
            if (type == null)    
                return "application/octet-stream";    
            else   
                return type;    
        }    
 
        public InputStream getInputStream() throws IOException {    
            return new ByteArrayInputStream(data);    
        }    
 
        public String getName() {    
            return "ByteArrayDataSource";    
        }    
 
        public OutputStream getOutputStream() throws IOException {    
            throw new IOException("Not Supported");    
        }    
    }    
}   

