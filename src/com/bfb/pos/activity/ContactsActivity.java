package com.bfb.pos.activity;

import java.util.ArrayList;
import java.util.HashMap;

import android.content.Intent;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import com.bfb.pos.R;

public class ContactsActivity extends BaseActivity implements OnClickListener, OnItemClickListener {
	
	 private ListView listView = null;  
	 private RelativeLayout contactsLayout = null;
	 private Button backButton = null;
	 private Button confirmButton = null;
	 
	 private ArrayList<HashMap<String, Object>> list = new ArrayList<HashMap<String, Object>>(); 
	 
    @Override 
    public void onCreate(Bundle savedInstanceState) {  
        super.onCreate(savedInstanceState); 
        
        this.setContentView(R.layout.contacts); 
        
        this.findViewById(R.id.topInfoView);

        listView = (ListView)this.findViewById(R.id.contactsList);
        contactsLayout = (RelativeLayout)this.findViewById(R.id.contactsLayout);
          
        backButton = (Button)this.findViewById(R.id.backButton);
        backButton.setOnClickListener(this);
        confirmButton = (Button)this.findViewById(R.id.okButton);
        confirmButton.setOnClickListener(this);
        
        
      //生成适配器的Item和动态数组对应的元素  
        SimpleAdapter listItemAdapter = new SimpleAdapter(ContactsActivity.this,list,//数据源   
            R.layout.contact_listitem,//ListItem的XML实现  
            //动态数组与ImageItem对应的子项          
            new String[] {"ItemTitle", "ItemText"},   
            //ImageItem的XML文件里面的一个ImageView,两个TextView ID  
            new int[] {R.id.nameTV,R.id.phonenumberTV}  
        );             
       
        listView.setAdapter(listItemAdapter);  
        listView.setOnItemClickListener(this);
        
        
        new GetContactTask().execute(); 
    }
    
    @Override
	public void onClick(View v) {
		ContactsActivity.this.finish();
	}
    
	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
		String number = list.get(position).get("ItemText").toString();
	    // 验证手机号码的正确性
		if (null == number || "".equals(number)){
			Toast.makeText(this, this.getResources().getString(R.string.phoneNoNull), Toast.LENGTH_SHORT).show();
		} else if (!number.matches("^(1(([35][0-9])|(47)|[8][01236789]))\\d{8}$")){
			Toast.makeText(this, this.getResources().getString(R.string.phoneNoIllegal), Toast.LENGTH_SHORT).show();
		} else {
			Intent _intent = new Intent();
			_intent.putExtra("phoneNumber", number);
			setResult(CONTEXT_RESTRICTED, _intent);//CONTEXT_RESTRICTED  int型变量，可自定义
			finish();
		}
	}
    
    class GetContactTask extends AsyncTask<Object, Object, Object>{
		@Override
		protected void onPostExecute(Object result) {
			ContactsActivity.this.hideDialog(PROGRESS_DIALOG);
			
			if (list.size() == 0){
	        	listView.setVisibility(View.GONE);
	        	contactsLayout.setVisibility(View.VISIBLE);
	        } else {
	        	((SimpleAdapter)listView.getAdapter()).notifyDataSetChanged();
	        }
			
		}

		@Override
		protected void onPreExecute() {
			ContactsActivity.this.showDialog(PROGRESS_DIALOG, ContactsActivity.this.getResources().getString(R.string.queryingContact));
		}

		@Override
		protected Object doInBackground(Object... arg0) {
			setData();
			return null;
		}
    	
    }
    
	//生成动态数组，加入数据
    private ArrayList<HashMap<String, Object>> setData(){
        Cursor cursor = getContentResolver().query(ContactsContract.Contacts.CONTENT_URI, null, null, null, "sort_key_alt");     
        while (cursor.moveToNext()){    
	         HashMap<String, Object> map = new HashMap<String, Object>();
	         String phoneName = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
	         map.put("ItemTitle", phoneName);//电话 姓名
	         String contactId = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts._ID));  
	         String hasPhone = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER));  
	           
            if (hasPhone.compareTo("1") == 0){  
                Cursor phones = getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,null,ContactsContract.CommonDataKinds.Phone.CONTACT_ID +" = "+ contactId,null, null);       
                while (phones.moveToNext()){     
                 String phoneNumber = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));      
                    map.put("ItemText", phoneNumber); // 多个号码如何处理
                }       
                phones.close();      
            }
            
            list.add(map);
        }
        cursor.close(); 
        
        return list;
    }

}
