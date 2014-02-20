package com.bfb.pos.agent.client.db;

import java.util.ArrayList;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.bfb.pos.agent.client.AppDataCenter;
import com.bfb.pos.model.TransferSuccessModel;
import com.bfb.pos.util.HashMapSerialize;
import com.bfb.pos.util.StringUtil;

public class TransferSuccessDBHelper extends BaseDBHelper {
	
	// 在交易成功后添加一个成功的交易，这时是没有签名图片和手机号的。在签完字后Update。
	public boolean insertATransfer(TransferSuccessModel model){
		SQLiteDatabase db = this.getWritableDatabase();
		try{
			ContentValues value = new ContentValues();
			value.put("tracenum", model.getTraceNum());
			value.put("transCode", model.getTransCode());
			value.put("batchNum", AppDataCenter.getValue("__BATCHNUM"));
			value.put("amount", model.getAmount());
			value.put("date", model.getDate());
			value.put("content", HashMapSerialize.serialize(model.getContent()));
			value.put("revoke", 1); // 1是该交易还未撤销，0为该交易已经撤销成功。
			
			// the row ID of the newly inserted row, or -1 if an error occurred
			long rowId = db.insert(TRANSFERSUCCESS_TABLE, null, value);
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
	
	public ArrayList<TransferSuccessModel> queryAll(){
		ArrayList<TransferSuccessModel> list = new ArrayList<TransferSuccessModel>();
		SQLiteDatabase db = this.getReadableDatabase();
		try{
			String[] columns = new String[]{"content"};
			Cursor cursor = db.query(TRANSFERSUCCESS_TABLE, columns, null, null, null, null, null);
			while (cursor.moveToNext()){
				TransferSuccessModel model = new TransferSuccessModel();
				model.setContent(HashMapSerialize.deserialize(cursor.getBlob(0)));
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
	
	// 查询出所有的需要进行消费撤销的交易，注意查询条件必须是 当日 本批次的交易 且 是消费交易 且 该消费交易没有被撤销过
	public ArrayList<TransferSuccessModel> queryNeedRevokeTransfer(){
		ArrayList<TransferSuccessModel> list = new ArrayList<TransferSuccessModel>();
		SQLiteDatabase db = this.getReadableDatabase();
		try{
			String[] columns = new String[]{"content"};
			
			StringBuffer selection = new StringBuffer();
			selection.append("transCode = '020022' AND revoke = 1 AND batchNum = '");
			selection.append(AppDataCenter.getValue("__BATCHNUM"));
			selection.append("' AND datetime(date) = datetime('");
			//selection.append(AppDataCenter.getValue("__yyyy-MM-dd"));
			selection.append(AppDataCenter.getServerDate());
			selection.append("')");
			
			Cursor cursor = db.query(TRANSFERSUCCESS_TABLE, columns, selection.toString(), null, null, null, null);
			while (cursor.moveToNext()){
				TransferSuccessModel model = new TransferSuccessModel();
				model.setContent(HashMapSerialize.deserialize(cursor.getBlob(0)));
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
	
	/**
	 * 查询符合条件的交易用于签购单查询
	 * 
	 * @param minAmount 最小金额
	 * @param maxAmount 最大金额
	 * @param startDate 起始日期
	 * @param endDate 结束日期
	 * @return
	 */
	public ArrayList<TransferSuccessModel> querySomeTransfer(String minAmount, String maxAmount, String startDate, String endDate){
		ArrayList<TransferSuccessModel> list = new ArrayList<TransferSuccessModel>();
		
		SQLiteDatabase db = this.getReadableDatabase();
		try{
			String[] columns = new String[]{"content"};
			StringBuffer selection = new StringBuffer();
			
			selection.append("amount BETWEEN ");
			selection.append(Long.parseLong(StringUtil.amount2String(StringUtil.formatAmount(Float.parseFloat(minAmount)))));
			selection.append(" AND ");
			selection.append(Long.parseLong(StringUtil.amount2String(StringUtil.formatAmount(Float.parseFloat(maxAmount)))));
			
			selection.append(" AND ");
			selection.append("datetime(date) >=  datetime('").append(startDate).append("')");
			selection.append(" AND ");
			selection.append("datetime(date) <= datetime('").append(endDate).append("')");
			selection.append(" AND batchNum = '");
			selection.append(AppDataCenter.getValue("__BATCHNUM"));
			selection.append("'");
			
			Cursor cursor = db.query(TRANSFERSUCCESS_TABLE, columns, selection.toString(), null, null, null, null);
			while (cursor.moveToNext()){
				TransferSuccessModel model = new TransferSuccessModel();
				model.setContent(HashMapSerialize.deserialize(cursor.getBlob(0)));
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

	/**
	 * 因为查询签购单时只查询本批次内的交易，所以
	 * 在更换批次时删除所有的交易，但是要保留没有成功上传签名图片的交易，以便继续上传签名图片 
	 ***/ 
	public boolean deleteTransfers(){
		SQLiteDatabase db = this.getWritableDatabase();
		try{
			db.delete(TRANSFERSUCCESS_TABLE, null, null);
			return true;
		}catch(Exception e){
			e.printStackTrace();
			return false;
		}finally{
			db.close();
		}
	}
	
	public boolean deleteATransfer(String traceNum){
		SQLiteDatabase db = this.getWritableDatabase();
		try{
			db.delete(TRANSFERSUCCESS_TABLE, "tracenum = ?", new String[]{traceNum});
			return true;
		}catch(Exception e){
			e.printStackTrace();
			return false;
		}finally{
			db.close();
		}
	}
	
	/**
	 * 当一个交易被撤销成功后，将原消费交易置为已经撤销
	 * 
	 * @param traceNum
	 * @return
	 */
	public boolean updateRevokeFalg(String traceNum){
		SQLiteDatabase db = this.getWritableDatabase();
		try{
			ContentValues value = new ContentValues();
			value.put("revoke", 0);
			int rows = db.update(TRANSFERSUCCESS_TABLE, value, "tracenum = ?", new String[]{traceNum});
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
	
}
