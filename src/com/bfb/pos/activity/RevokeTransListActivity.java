package com.bfb.pos.activity;

import java.util.ArrayList;
import java.util.HashMap;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import com.bfb.pos.agent.client.Constant;
import com.bfb.pos.agent.client.db.TransferSuccessDBHelper;
import com.bfb.pos.dynamic.core.Event;
import com.bfb.pos.model.TransferSuccessModel;
import com.bfb.pos.util.DateUtil;
import com.bfb.pos.util.StringUtil;
import com.dhc.dynamic.parse.ParseView;
import com.bfb.pos.R;

public class RevokeTransListActivity extends BaseActivity implements OnClickListener, OnItemClickListener {
	
	private Button backButton 			= null;
	private ListView listView 			= null;
	
	private ArrayList<TransferSuccessModel> transList = new ArrayList<TransferSuccessModel>();
	private ArrayList<HashMap<String, String>> mylist = new ArrayList<HashMap<String, String>>();
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		this.setContentView(R.layout.revoke_trans_list);
		
		this.findViewById(R.id.topInfoView);
		backButton = (Button) this.findViewById(R.id.backButton);
		backButton.setOnClickListener(this);
		
		listView = (ListView) this.findViewById(R.id.transList);
		
    	SimpleAdapter mSchedule = new SimpleAdapter(this, 
    			mylist,
    			R.layout.queryrevoke_listitem,  
    			new String[] {"accountNo","dateTime", "amount"},   
    			new int[] {R.id.revokeAccountNo, R.id.revokeDateTime, R.id.revokeAmount});  
    	
    	listView.setAdapter(mSchedule);
    	listView.setOnItemClickListener(this);
    	
    	new QueryRevokeListTask().execute();
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// 当撤销一笔交易结束时回退到本列表界面。
		/*
		if (resultCode == Activity.RESULT_OK){
			new QueryRevokeListTask().execute();
		}
		*/
		
		// 直接调用父类的方法，结果是撤销一笔交易结束时直接回到交易结果界面。
		super.onActivityResult(requestCode, resultCode, data);
	}
	
	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
		// 跳转到确认页面
		if(Constant.isAISHUA){
			Intent intent = new Intent(RevokeTransListActivity.this, ASRevokePwd3Activity.class);
			
			try {

				Event event = new Event(null, "swip", null);
				String fsk = "swipeCard|null";
				event.setFsk(fsk);
				event.setActionType(ParseView.ACTION_TYPE_LOCAL);
				event.setAction("RevokeTransConfirmActivity");
				HashMap<String, String> map = transList.get(arg2).getContent();
				event.setStaticActivityDataMap(map);
				event.trigger();

			} catch (Exception e) {
				e.printStackTrace();
			}
		}else{
			Intent intent = new Intent(RevokeTransListActivity.this, RevokeTransConfirmActivity.class);
			intent.putExtra("map", transList.get(arg2).getContent());
			startActivityForResult(intent, 0);		
		}
		
	}
	
	@Override
	public void onClick(View view) {
		switch(view.getId()){
		case R.id.backButton:
			// 这一步的返回我觉得应该是连同关闭前一个输入密码的界面
			this.setResult(RESULT_OK);
			this.finish();
			break;
		}
		
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK){
			this.setResult(RESULT_OK);
			this.finish();
			return false;
		}
		
		return super.onKeyDown(keyCode, event);
	}

	class QueryRevokeListTask extends AsyncTask<Object, Object, Object>{

		@Override
		protected void onPreExecute() {
			RevokeTransListActivity.this.showDialog(PROGRESS_DIALOG, RevokeTransListActivity.this.getResources().getString(R.string.refreshingData));
		}
		
		@Override
		protected Object doInBackground(Object... arg0) {
			TransferSuccessDBHelper helper = new TransferSuccessDBHelper();
			// 从数据库中查询出的所有需要进行撤销的交易
			transList = helper.queryNeedRevokeTransfer();
			
			return null;
		}

		@Override
		protected void onPostExecute(Object result) {
			mylist.clear();
			
			for(TransferSuccessModel model : transList){  
	    		HashMap<String, String> map = new HashMap<String, String>();
	    		map.put("accountNo", StringUtil.formatAccountNo(model.getContent().get("field2")));  
	    		map.put("dateTime", DateUtil.formatDateTime(model.getContent().get("field13") + model.getContent().get("field12"))); 
	    		map.put("amount", StringUtil.String2SymbolAmount(model.getContent().get("field4")));
	    		mylist.add(map);  
	    	}
			
			// 设置EmptyView
	    	ImageView emptyView = new ImageView(RevokeTransListActivity.this);
			emptyView.setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT));
			emptyView.setImageResource(R.drawable.nodata);
			emptyView.setScaleType(ScaleType.CENTER_INSIDE);
			((ViewGroup)listView.getParent()).addView(emptyView);
			listView.setEmptyView(emptyView);
			//////////////////////////
			
			((SimpleAdapter)listView.getAdapter()).notifyDataSetChanged();
			
			RevokeTransListActivity.this.hideDialog(PROGRESS_DIALOG);
			
		}

	}

}
