package com.bfb.pos.agent.client.db;

import java.util.ArrayList;
import java.util.HashMap;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.bfb.pos.agent.client.AppDataCenter;
import com.bfb.pos.agent.client.SystemConfig;
import com.bfb.pos.model.ReversalModel;
import com.bfb.pos.util.HashMapSerialize;

public class ReversalDBHelper extends BaseDBHelper {
	
	private static final int MAX_COUNT 		= 50;
	
	// 当一个交易需要冲正时，记录该交易的发送报文，以备冲正
	public boolean insertATransaction(ReversalModel model){
		SQLiteDatabase db = this.getWritableDatabase();
		int count = this.queryCount(db);
		Log.e("reversal count", "" + count);
		if (count > MAX_COUNT){
			this.deleteFirstRecord(db);
		}
		
		try{
			ContentValues value = new ContentValues();
			value.put("tracenum", model.getTraceNum());
			value.put("batchNum", AppDataCenter.getValue("__BATCHNUM"));
			value.put("date", model.getDate());
			value.put("content", HashMapSerialize.serialize(model.getContent()));
			value.put("state", 1); // 开始默认为冲正失败
			value.put("count", 0); // 开始次数为0
			
			// the row ID of the newly inserted row, or -1 if an error occurred
			long rowId = db.insert(REVERSAL_TABLE, null, value);
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
	
	//　查询出所有的冲正记录
	public ArrayList<ReversalModel> queryAllReversal(){
		ArrayList<ReversalModel> list = new ArrayList<ReversalModel>();
		SQLiteDatabase db = this.getReadableDatabase();
		try{
			String[] columns = new String[]{"content, state, count"};
			Cursor cursor = db.query(REVERSAL_TABLE, columns, null, null, null, null, null);
			while (cursor.moveToNext()){
				ReversalModel model = new ReversalModel();
				model.setContent(HashMapSerialize.deserialize(cursor.getBlob(0)));
				model.setState(cursor.getInt(1));
				model.setCount(cursor.getInt(2));
				list.add(model);
			}
			cursor.close();
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			db.close();
		}
		
		return list;
	}
	
	public int queryCount(SQLiteDatabase db){
		Cursor cursor = null;
		try{
			String sql = "SELECT tracenum FROM " + REVERSAL_TABLE;
			cursor = db.rawQuery(sql, null);
			return cursor.getCount();
		} catch(Exception e){
			e.printStackTrace();
			return 0;
		} finally{
			cursor.close();
		}
	}
	
	/** 
	 * 查询出需要冲正的记录
	 * 
	 * @return 当返回值不为null时说明有需要冲正的记录
	 */
	public HashMap<String, String> queryNeedReversal(){
		HashMap<String, String> map = null;
		SQLiteDatabase db = this.getReadableDatabase();
		try{
			StringBuffer sb = new StringBuffer();
			// 只查询最后一条语句是否满足条件，避免数据量过大时耗时太长。倒序只比较最后一条记录
			sb.append("SELECT content FROM ");
			sb.append(REVERSAL_TABLE);
			sb.append(" WHERE state=1 AND count<");
			sb.append(SystemConfig.getMaxReversalCount());
			sb.append(" AND datetime(date) = datetime('");
			sb.append(AppDataCenter.getValue("__yyyy-MM-dd"));
			sb.append("') AND batchNum = '");
			sb.append(AppDataCenter.getValue("__BATCHNUM"));
			// 这条语句不是判断最后一条记录是否满足条件，而是从满足条件的记录拿出最后一条记录
			//sb.append("' order by id desc limit 0,1");
			sb.append("' AND id = (SELECT MAX(id) FROM ");
			sb.append(REVERSAL_TABLE);
			sb.append(")");
			Cursor cursor = db.rawQuery(sb.toString(), null);
			if (cursor.moveToFirst()){
				map = HashMapSerialize.deserialize(cursor.getBlob(0));
			}
			cursor.close();
			
			return map;
			
		}catch(Exception e){
			e.printStackTrace();
			return null;
		} finally{
			db.close();;
		}
	}
	
	// 当冲正成功后，修改冲正状态为成功
	public boolean updateReversalState(String traceNum){
		SQLiteDatabase db = this.getWritableDatabase();
		try{
			ContentValues value = new ContentValues();
			value.put("state", 0); // 0表示冲正成功
			int rows = db.update(REVERSAL_TABLE, value, "tracenum = ?", new String[]{traceNum});
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
	
	// 当冲正失败后，修改冲正次数
	public boolean updateReversalCount(String tracenum){
		SQLiteDatabase db = this.getWritableDatabase();
		try{
			StringBuffer sb = new StringBuffer();
			sb.append("UPDATE ");
			sb.append(REVERSAL_TABLE);
			sb.append(" SET count = (SELECT count FROM ");
			sb.append(REVERSAL_TABLE);
			sb.append(" WHERE tracenum = '");
			sb.append(tracenum);
			sb.append("') + 1 WHERE tracenum = '");
			sb.append(tracenum);
			sb.append("'");
			db.execSQL(sb.toString());
			
			return true;
			
		}catch(Exception e){
			e.printStackTrace();
			return false;
			
		}finally{
			db.close();
		}
	}
	
	/**
	 * 当交易成功后，则删除原准备冲正的记录。
	 * 
	 * @param tracenum
	 * @return
	 */
	public boolean deleteAReversalTrans(String tracenum){
		SQLiteDatabase db = this.getWritableDatabase();
		try{
			int rows = db.delete(REVERSAL_TABLE, "tracenum = ?", new String[]{tracenum});
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
	 * 删除数据库中的第一条记录
	 * @param db
	 */
	public void deleteFirstRecord(SQLiteDatabase db){
		try{
			String sql = "DELETE FROM "+REVERSAL_TABLE +" WHERE id = (SELECT min(id) FROM "+ REVERSAL_TABLE +" )";
			db.execSQL(sql);
		} catch(Exception e){
			e.printStackTrace();
		}
	}
	
	public boolean deleteAll(){
		SQLiteDatabase db = this.getWritableDatabase();
		try {
			db.delete(REVERSAL_TABLE, null, null);
			return true;
			
		} catch (Exception e) {
			e.printStackTrace();
			return false;
			
		} finally{
			db.close();
		}
	}

}
