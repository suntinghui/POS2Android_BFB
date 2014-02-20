package com.bfb.pos.activity;

import java.util.ArrayList;
import java.util.HashMap;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
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
import android.widget.TextView;

import com.bfb.pos.agent.client.AppDataCenter;
import com.bfb.pos.agent.client.db.TransferSuccessDBHelper;
import com.bfb.pos.model.TransferSuccessModel;
import com.bfb.pos.util.DateUtil;
import com.bfb.pos.util.StringUtil;
import com.bfb.pos.R;

public class QueryReceiptListActivity extends BaseActivity implements OnClickListener, OnItemClickListener {

	private Button backButton = null;
	private ListView listView = null;
	
	private ArrayList<TransferSuccessModel> modelList = new ArrayList<TransferSuccessModel>();
	private ArrayList<HashMap<String, String>> mapList = new ArrayList<HashMap<String, String>>(); 
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		this.setContentView(R.layout.trans_list);
		
		this.findViewById(R.id.topInfoView);
		
		((TextView)this.findViewById(R.id.titleView)).setText(R.string.receiptList);
		
		backButton = (Button) this.findViewById(R.id.backButton);
		backButton.setOnClickListener(this);
		
		listView = (ListView) this.findViewById(R.id.transList);
		//生成适配器的Item和动态数组对应的元素  
        SimpleAdapter listItemAdapter = new SimpleAdapter(QueryReceiptListActivity.this, mapList,
            R.layout.queryreceipt_listitem, 
            new String[] {"transType", "transAmount", "transAccountNo", "transDateTime"},   
            //ImageItem的XML文件里面的一个ImageView,两个TextView ID  
            new int[] {R.id.TransType, R.id.TransAmount, R.id.TransAccountNo, R.id.TransDateTime}  
        );             
       
        listView.setAdapter(listItemAdapter);  
        listView.setOnItemClickListener(this);
		
		
		@SuppressWarnings("unchecked")
		HashMap<String, String> map = (HashMap<String, String>) this.getIntent().getSerializableExtra("map");
		new GetReceiptListTask().execute(map);
	}
	
	@Override
	public void onClick(View arg0) {
		this.finish();
	}
	
	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
		Intent intent = new Intent(this, QueryReceiptDetailActivity.class);
		intent.putExtra("map", modelList.get(position).getContent());
		startActivity(intent);
	}
	
	class GetReceiptListTask extends AsyncTask<Object, Object, Object>{
		
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			QueryReceiptListActivity.this.showDialog(BaseActivity.PROGRESS_DIALOG, QueryReceiptListActivity.this.getResources().getString(R.string.queryingData));
		}

		@Override
		protected Object doInBackground(Object... params) {
			@SuppressWarnings("unchecked")
			HashMap<String, String> map = (HashMap<String, String>) params[0];
			
			TransferSuccessDBHelper helper = new TransferSuccessDBHelper();
			modelList = helper.querySomeTransfer(map.get("minAmount"), map.get("maxAmount"), map.get("startDate"), map.get("endDate"));
			return null;
		}

		@Override
		protected void onPostExecute(Object result) {
			if (modelList.isEmpty()){
				ImageView emptyView = new ImageView(QueryReceiptListActivity.this);
				emptyView.setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT));
				emptyView.setImageResource(R.drawable.nodata);
				emptyView.setScaleType(ScaleType.CENTER_INSIDE);
				((ViewGroup)listView.getParent()).addView(emptyView);
				listView.setEmptyView(emptyView);
				
			} else {
				for (TransferSuccessModel model : modelList){
					HashMap<String, String> tempMap = new HashMap<String, String>();
					tempMap.put("transType", AppDataCenter.getTransferName(model.getContent().get("fieldTrancode")));
					tempMap.put("transAmount", StringUtil.String2SymbolAmount(model.getContent().get("field4")));
					tempMap.put("transAccountNo", StringUtil.formatAccountNo(model.getContent().get("field2")));
					tempMap.put("transDateTime", DateUtil.formatDateTime(model.getContent().get("field13") + model.getContent().get("field12")));
					
					mapList.add(tempMap);
				}
				
				((SimpleAdapter)listView.getAdapter()).notifyDataSetChanged();
			}
			
			QueryReceiptListActivity.this.hideDialog(BaseActivity.PROGRESS_DIALOG);
		}
		
	}

}
