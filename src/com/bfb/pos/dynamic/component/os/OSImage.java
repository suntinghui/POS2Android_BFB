package com.bfb.pos.dynamic.component.os;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.widget.ImageView;

import com.bfb.pos.agent.client.Constant;
import com.bfb.pos.dynamic.component.Component;
import com.bfb.pos.dynamic.component.ViewException;
import com.bfb.pos.dynamic.core.ViewPage;
import com.bfb.pos.util.InputStreamUtils;
import com.bfb.pos.util.TrafficUtil;
import com.bfb.pos.R;

public class OSImage extends StructComponent{
	
	private String imageName;
	
	public OSImage(ViewPage viewPage) {
		super(viewPage);
	}

	@Override
	public ImageView toOSComponent() throws ViewException {
		ImageView image = new ImageView(this.getContext());
		image.setTag(this.getId());
		if (imageName.startsWith("icon_")){
			image.setImageBitmap(getBitmap());
		} else {
			image.setImageResource(getIconId(imageName));
		}
		
		return image;
	}

	@Override
	protected Component construction(ViewPage viewPage) {
		return new OSImage(viewPage);
	}
	
	@Override
	protected void copyParams(Component src, Component des) {
		super.copyParams(src, des);
		((OSImage)des).setImageName(((OSImage)src).getImageName());
	}
	
	private int getIconId(String imgName) {
		int resourceId = this.getContext().getResources().getIdentifier(imgName, "drawable", this.getContext().getPackageName());
		// if failure, return the default icon resource id to instead.
		if (resourceId == 0)
			resourceId = R.drawable.icon;
		
		return resourceId;
	}
	
	 private Bitmap getBitmap(){
		 try{
			// 首先判断本地是否有图片，如果有直接返回，如果没有则从网络上下载图片并保存在本地 
			 File imageFile = new File(Constant.IMAGEPATH+imageName+".png");
			 if (imageFile.exists()){
				 Log.i("===", "local image file "+imageName);
				 return BitmapFactory.decodeFile(Constant.IMAGEPATH+imageName+".png");//通过BitmapFactory将图片文件转成Bitmap
			 } else {
				 Log.i("===", "net image file "+imageName);
				 URL url = new URL(Constant.IMAGEURL + imageName + ".png");
	             HttpURLConnection conn = (HttpURLConnection)url.openConnection();
	             conn.setDoInput(true);
	             conn.connect(); 
	             InputStream inputStream=conn.getInputStream();
	             Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
	             
	             // TODO traffic
	             TrafficUtil.getInstance().setTraffic(TrafficUtil.TYPE_RECEIVE, InputStreamUtils.InputStreamTOByte(inputStream).length);
	             // traffic end
	             
	             this.saveFile(bitmap);
	             return bitmap;
			 }
		 }catch(Exception e){
			 Log.i("===", "get file error:"+e.getMessage());
		 }
		 
		 return null;
	 }
	  
	    
    public void saveFile(Bitmap bm){
    	try{
    		File dirFile = new File(Constant.IMAGEPATH);   
            if(!dirFile.exists()){   
                dirFile.mkdir();   
            }
            
            File file = new File(Constant.IMAGEPATH+imageName+".png");   
            BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(file));   
            bm.compress(Bitmap.CompressFormat.PNG, 80, bos);   
            bos.flush();   
            bos.close();
    	}catch(Exception e){
    		Log.i("===", "save file error:"+e.getMessage());
    	}
           
    }

	public String getImageName() {
		return imageName;
	}

	public void setImageName(String imageName) {
		this.imageName = imageName;
	}
	
}
