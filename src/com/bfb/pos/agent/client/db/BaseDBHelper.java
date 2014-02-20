package com.bfb.pos.agent.client.db;

import com.bfb.pos.agent.client.ApplicationEnvironment;

import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class BaseDBHelper extends SQLiteOpenHelper {

	private static final String DATABASE_NAME 			= "Pos2Android.db";
	private static final int DATABASE_VERSION 			= 1;
	
	public static final String PAYEEACCOUNT_TABLE		= "PAYEEACCOUNT_TABLE";
	public static final String TRANSFERSUCCESS_TABLE	= "TRANSFERSUCCESS_TABLE";
	public static final String REVERSAL_TABLE			= "REVERSAL_TABLE";
	public static final String SIGNIMAGE_TABLE			= "SIGNIMAGE_TABLE";
	public static final String ANNOUNCEMENT_TABLE		= "ANNOUNCEMENT_TABLE";
	
	public BaseDBHelper() {
		super(ApplicationEnvironment.getInstance().getApplication(), DATABASE_NAME, null, DATABASE_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		try{
			String payeeSql = "CREATE TABLE " + PAYEEACCOUNT_TABLE + " (id INTEGER PRIMARY KEY AUTOINCREMENT, accountNo TEXT, name TEXT, phoneNo TEXT, bank TEXT, bankCode TEXT)";
			db.execSQL(payeeSql);
			
			String successSql = "CREATE TABLE " + TRANSFERSUCCESS_TABLE + " (id INTEGER PRIMARY KEY AUTOINCREMENT, tracenum TEXT, transCode TEXT,batchNum TEXT, date TEXT, amount INTEGER, content BLOB, revoke INTEGER)";
			db.execSQL(successSql);
			
			String reversalSql = "CREATE TABLE " + REVERSAL_TABLE + " (id INTEGER PRIMARY KEY AUTOINCREMENT, tracenum TEXT, batchNum TEXT, date TEXT, content BLOB, state INTEGER, count INTEGER)";
			db.execSQL(reversalSql);
			
			String signImageSql = "CREATE TABLE " + SIGNIMAGE_TABLE + " (id INTEGER PRIMARY KEY AUTOINCREMENT, tracenum TEXT, uploadFlag INTEGER, receMobile TEXT, content BLOB)";
			db.execSQL(signImageSql);
			
			String announcementSql = "CREATE TABLE " + ANNOUNCEMENT_TABLE +" (id INTEGER PRIMARY KEY AUTOINCREMENT, number TEXT, title TEXT, date TEXT, content TEXT)";
			db.execSQL(announcementSql);
			
			Log.e("database", "---成功创建数据库---");
		
		}catch(Exception e){
			e.printStackTrace();
			Log.e("database", "---创建数据库失败---");
		}
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

	}
	
	/**
	 * http://wlhejj.iteye.com/blog/983998
	 
	 当调用SQLiteOpenHelper的getWritableDatabase()或者getReadableDatabase()方法获取用于操作数据库的SQLiteDatabase实例的时候，
	 如果数据库不存在，Android系统会自动生成一个数据库，接着调用onCreate()方法，onCreate()方法在初次生成数据库时才会被调用，
	 在onCreate()方法里可以生成数据库表结构及添加一些应用使用到的初始化数据。onUpgrade()方法在数据库的版本发生变化时会被调用，
	 数据库的版本是由程序员控制的，假设数据库现在的版本是1，由于业务的需要，修改了数据库表的结构，这时候就需要升级软件，
	 升级软件时希望更新用户手机里的数据库表结构，为了实现这一目的，可以把原来的数据库版本设置为2(或其他数值)，
	 并且在onUpgrade()方法里面实现表结构的更新。当软件的版本升级次数比较多，这时在onUpgrade()方法里面可以根据原版号和目标版本号进行判断，
	 然后作出相应的表结构及数据更新。 

	getWritableDatabase()和getReadableDatabase()方法都可以获取一个用于操作数据库的SQLiteDatabase实例。
	但getWritableDatabase() 方法以读写方式打开数据库，一旦数据库的磁盘空间满了，数据库就只能读而不能写，
	倘若使用的是getWritableDatabase() 方法就会出错。getReadableDatabase()方法先以读写方式打开数据库，
	如果数据库的磁盘空间满了，就会打开失败，当打开失败后会继续尝试以只读方式打开数据库。 
	 */

}
