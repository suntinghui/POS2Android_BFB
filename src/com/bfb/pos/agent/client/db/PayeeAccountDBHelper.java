package com.bfb.pos.agent.client.db;

import java.util.ArrayList;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.bfb.pos.model.PayeeAccountModel;

public class PayeeAccountDBHelper extends BaseDBHelper {
	
	// 查询出所有的收款人
	public ArrayList<PayeeAccountModel> queryAll(){
		ArrayList<PayeeAccountModel> list = new ArrayList<PayeeAccountModel>();
		
		SQLiteDatabase db = this.getReadableDatabase();
		try{
			String sql = "SELECT accountNo, name, phoneNo, bank, bankCode FROM "+ PAYEEACCOUNT_TABLE; 
			Cursor cursor = db.rawQuery(sql, null);
			while (cursor.moveToNext()){
				PayeeAccountModel model = new PayeeAccountModel(cursor.getString(0), cursor.getString(1), cursor.getString(2), cursor.getString(3), cursor.getString(4));
				list.add(model);
			}
			cursor.close();
			
		} catch(Exception e){
			e.printStackTrace();
		} finally{
			db.close();
		}
		
		return list;
	}
	
	/**
	 * 根据账号检索一个联系人
	 * 
	 * @param accountNo
	 * @return 返回null则说明不存在此联系人
	 */
	public PayeeAccountModel queryAPayeeWithAccountNo(String accountNo){
		SQLiteDatabase db = this.getReadableDatabase();
		PayeeAccountModel model = null;
		try{
			String sql = "SELECT name, phoneNo, bank, bankCode FROM "+ PAYEEACCOUNT_TABLE + " WHERE accountNo = ?"; 
			Cursor cursor = db.rawQuery(sql, new String[]{accountNo});
			if (cursor.moveToFirst()){
				model = new PayeeAccountModel(accountNo, cursor.getString(0),  cursor.getString(1), cursor.getString(2), cursor.getString(3));
			}
			cursor.close();
			
		} catch(Exception e){
			e.printStackTrace();
		} finally{
			db.close();
		}
		
		return model;
	}
	
	public boolean insertPayeeAccount(PayeeAccountModel model){
		SQLiteDatabase db = this.getWritableDatabase();
		try{
			ContentValues value = new ContentValues();
			value.put("accountNo", model.getAccountNo());
			value.put("name", model.getName());
			value.put("phoneNo", model.getPhoneNo());
			value.put("bank", model.getBank());
			value.put("bankCode", model.getBankCode());
			db.insert(PAYEEACCOUNT_TABLE, null, value);
			
			return true;
			
		} catch(Exception e){
			e.printStackTrace();
			return false;
		} finally{
			db.close();
		}
	}
	
	// 删除选定的收款人信息
	public boolean deletePayeeAccounts(ArrayList<String> list){
		SQLiteDatabase db = this.getWritableDatabase();
		try{
			for(String accountNo : list){
				db.delete(PAYEEACCOUNT_TABLE, "accountNo = ?", new String[]{accountNo});
			}
			return true;
			
		} catch(Exception e){
			e.printStackTrace();
			return false;
		} finally{
			db.close();
		}
	}

}
