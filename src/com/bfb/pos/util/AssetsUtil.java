package com.bfb.pos.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import com.bfb.pos.agent.client.ApplicationEnvironment;
import com.bfb.pos.agent.client.Constant;

public class AssetsUtil {
	
	public static final String Folder_View 		= "view";
	public static final String Folder_Config 	= "config";
	public static final String Folder_Template 	= "template";
	public static final String Folder_Data		= "data";
	
	public static final int Type_View 			= 1;
	public static final int Type_Config 		= 2;
	public static final int Type_Template 		= 3;
	public static final int Type_Data 			= 4;
	
	// 从Assets下读取文件
	public static InputStream getInputStreamFromAssets(String fileName, int type){
		InputStream inputStream = null;
		try{
			StringBuilder sb = new StringBuilder();
			
			switch(type){
			case Type_View:
				sb.append(Folder_View).append("/").append(fileName);
				break;
				
			case Type_Config:
				sb.append(Folder_Config).append("/").append(fileName);
				break;
				
			case Type_Template:
				sb.append(Folder_Template).append("/").append(fileName);
				break;
				
			case Type_Data:
				sb.append(Folder_Data).append("/").append(fileName);
				break;
			}
			
			if (!fileName.endsWith(".xml")){
				sb.append(".xml");
			}
			
			inputStream = ApplicationEnvironment.getInstance().getApplication().getAssets().open(sb.toString());
			return inputStream;
			
		}catch(IOException e){
			e.printStackTrace();
		} 
		
		return inputStream;
	}
	
	// 从手机中读取文件
	public static InputStream getInputStreamFromPhone(String fileName) throws FileNotFoundException {
		InputStream stream = null;
		String path = Constant.ASSETSPATH+fileName;
		if (!fileName.endsWith(".xml"))
			path += ".xml";
			
		File file = new File(path);
		FileInputStream fileIS;
		try {
			fileIS = new FileInputStream(file);
		} catch (FileNotFoundException e) {
			throw new FileNotFoundException("加载系统文件出错，请重新登录！");
		}
        StringBuffer sb=new StringBuffer();
        BufferedReader buf = new BufferedReader(new InputStreamReader(fileIS));
        String readString = new String();

        try{
        	while((readString = buf.readLine())!= null){
                sb.append(readString);
            }
         
	        stream = StringUtil.getInputStream(sb.toString());
	        return stream;
	         
        } catch(IOException e){
        	return null;
        }
		                 
	}
	

}
