package com.bfb.pos.agent.client.db;

import java.util.ArrayList;
import java.util.HashMap;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.bfb.pos.agent.client.SystemConfig;
import com.bfb.pos.util.HashMapSerialize;

public class UploadSignImageDBHelper extends BaseDBHelper {
	
	// 在交易成功后添加一个成功的交易，这时是没有签名图片和手机号的。在签完字后Update。
	public boolean insertATransfer(String traceNum, String receMobile, HashMap<String, String> content){
		SQLiteDatabase db = this.getWritableDatabase();
		try{
			ContentValues value = new ContentValues();
			value.put("tracenum", traceNum);
			value.put("receMobile", receMobile);
			value.put("content", HashMapSerialize.serialize(content));
			value.put("uploadFlag", 1); // 0为上传成功，其它则为上传次数，默认值为1
			
			// the row ID of the newly inserted row, or -1 if an error occurred
			long rowId = db.insert(SIGNIMAGE_TABLE, null, value);
			if (rowId == -1){
				return false;
			} else {
				return true;
			}
			
		} catch(Exception e){
			e.printStackTrace();
			return false;
		} finally{
			db.close();
		}
	}
	
	public String queryReceMobile(String traceNum){
		SQLiteDatabase db = this.getReadableDatabase();
		try{
			Cursor cursor = db.query(SIGNIMAGE_TABLE, new String[]{"receMobile"}, "tracenum=?", new String[]{traceNum}, null, null, null);
			while (cursor.moveToNext()){
				return cursor.getString(0);
			}
		} catch(Exception e){
			e.printStackTrace();
		} finally{
			db.close();
		}
		
		return "";
	}
	
	// 查询出所有没有成功上传签名图片的交易
	// 有可能因为程序被强制关闭或掉电等情况他造成该交易没有签名图片
	public ArrayList<HashMap<String, String>> queryNeedUploadSignImageTransfer(){
		ArrayList<HashMap<String, String>> list = new ArrayList<HashMap<String, String>>();
		SQLiteDatabase db = this.getWritableDatabase();
		try{
			String[] columns = new String[]{"tracenum, uploadFlag, content"};
			
			Cursor cursor = db.query(SIGNIMAGE_TABLE, columns, null, null, null, null, null);
			while (cursor.moveToNext()){
				int count = cursor.getInt(1);
				// 已经成功上传或超过设置的上传签购单的最大次数,则删除该交易
				if (count == 0 || count > SystemConfig.getMaxUploadSignImageCount()) {
					this.deleteATransfer(db, cursor.getString(0));
				} else {
					this.updateUploadFlag(db, cursor.getString(0));
					
					list.add(HashMapSerialize.deserialize(cursor.getBlob(2)));
				}
			}
			cursor.close();
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			db.close();
		}
		
		return list;
	}
	
	public HashMap<String, String> queryAUploadSignImageTransfer(){
		SQLiteDatabase db = this.getWritableDatabase();
		try{
			StringBuffer sb = new StringBuffer();
			sb.append("SELECT tracenum, uploadFlag, content FROM ");
			sb.append(SIGNIMAGE_TABLE);
			sb.append(" ORDER BY id DESC LIMIT 0,1");
			
			Cursor cursor = db.rawQuery(sb.toString(), null);
			while (cursor.moveToNext()){
				int count = cursor.getInt(1);
				// 已经成功上传或超过设置的上传签购单的最大次数,则删除该交易
				if (count == 0 || count > SystemConfig.getMaxUploadSignImageCount()) {
					this.deleteATransfer(db, cursor.getString(0));
				} else {
					this.updateUploadFlag(db, cursor.getString(0));
					
					return HashMapSerialize.deserialize(cursor.getBlob(2));
				}
			}
			cursor.close();
		}catch(Exception e){
			e.printStackTrace();
			return null;
		}finally{
			db.close();
		}
		
		return null;
	}
	
	private boolean deleteATransfer(SQLiteDatabase db, String traceNum){
		try{
			db.delete(SIGNIMAGE_TABLE, "tracenum = ?", new String[]{traceNum});
			return true;
		}catch(Exception e){
			e.printStackTrace();
			return false;
		}
	}
	
	/**
	 * 当图片上传成功后修改交易置为已成功上传 -- uploadFlag = true
	 * 
	 * @param traceNum
	 * @return
	 */
	public boolean updateUploadFlagSuccess(String traceNum){
		SQLiteDatabase db = this.getWritableDatabase();
		try{
			ContentValues value = new ContentValues();
			value.put("uploadFlag", 0);
			int rows = db.update(SIGNIMAGE_TABLE, value, "tracenum = ?", new String[]{traceNum});
			if (rows == 1){
				return true;
			} else {
				return false;
			}
		} catch(Exception e){
			e.printStackTrace();
			return false;
		} finally{
			db.close();
		}
	}
	
	/**
	 * 当图片将要发起上传上传将上传次数加1
	 * @param traceNum
	 * @return
	 */
	private boolean updateUploadFlag(SQLiteDatabase db, String traceNum){
		try{
			StringBuffer sb = new StringBuffer();
			sb.append("UPDATE ");
			sb.append(SIGNIMAGE_TABLE);
			sb.append(" SET uploadFlag = (SELECT uploadFlag FROM ");
			sb.append(SIGNIMAGE_TABLE);
			sb.append(" WHERE tracenum = '");
			sb.append(traceNum);
			sb.append("') + 1 WHERE tracenum = '");
			sb.append(traceNum);
			sb.append("'");
			db.execSQL(sb.toString());
			
			return true;
			
		}catch(Exception e){
			e.printStackTrace();
			return false;
			
		}
	}
	
}
