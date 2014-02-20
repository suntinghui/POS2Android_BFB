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

import com.bfb.pos.activity.view.PaginationView;
import com.bfb.pos.activity.view.PaginationView.OnPageChangedListener;
import com.bfb.pos.agent.client.AppDataCenter;
import com.bfb.pos.agent.client.SystemConfig;
import com.bfb.pos.agent.client.TransferLogic;
import com.bfb.pos.util.DateUtil;
import com.bfb.pos.util.StringUtil;
import com.bfb.pos.R;

public class QueryTransferHistoryListActivity extends BaseActivity implements OnClickListener, OnItemClickListener, OnPageChangedListener {
	
	private Button backButton = null;
	private ListView listView = null;
	private PaginationView pager = null;
	
	private int totalCount = 0;
	
	// 存放请求的数据。key为页码，value为本页码的数据。
	private HashMap<String, ArrayList<HashMap<String, String>>> allDataMap = new HashMap<String, ArrayList<HashMap<String, String>>>();
	// 存放本页面所有数据 
	private ArrayList<HashMap<String, String>> currentList = null;
	// 存放本页面ListView显示的数据
	private ArrayList<HashMap<String, String>> displayList = new ArrayList<HashMap<String, String>>(); 
	
	@SuppressWarnings("unchecked")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.trans_list);
		
		this.findViewById(R.id.topInfoView);
		((TextView)this.findViewById(R.id.titleView)).setText(R.string.historyList);
		
		backButton = (Button) this.findViewById(R.id.backButton);
		backButton.setOnClickListener(this);
		
		pager = (PaginationView) this.findViewById(R.id.pager);
		pager.setPageSize(SystemConfig.getPageSize());
		pager.setOnPageChangedListener(this);
		pager.setVisibility(View.VISIBLE);
		
		listView = (ListView) this.findViewById(R.id.transList);
		
		//生成适配器的Item和动态数组对应的元素  
        SimpleAdapter listItemAdapter = new SimpleAdapter(this, displayList,
            R.layout.queryreceipt_listitem, 
            new String[] {"transType", "transAmount", "transAccountNo", "transDateTime"},   
            //ImageItem的XML文件里面的一个ImageView,两个TextView ID  
            new int[] {R.id.TransType, R.id.TransAmount, R.id.TransAccountNo, R.id.TransDateTime}  
        );             
       
        listView.setAdapter(listItemAdapter);  
        listView.setOnItemClickListener(this);
		
        totalCount = this.getIntent().getIntExtra("totalCount", 0);
		this.refreshData((HashMap<String, String>) this.getIntent().getSerializableExtra("map"));
	}
	
	@SuppressWarnings("unchecked")
	@Override
	protected void onNewIntent(Intent intent) {
		super.onNewIntent(intent);
		
		this.refreshData((HashMap<String, String>) intent.getSerializableExtra("map"));
	}

	@Override
	public void onClick(View arg0) {
		this.setResult(RESULT_OK);
		this.finish();
	}
	
	// 分页
	@Override
	public void onClick(View v, int pageIndex) {
		// 请求pageIndex页面的数据
		this.requestData(pageIndex);
	}
	
	private void requestData(int pageIndex){
		String currentPage = String.valueOf(pageIndex);
		
		if (allDataMap != null && allDataMap.containsKey(currentPage)){
			currentList = allDataMap.get(currentPage);
			this.refreshActivity();
			
		} else {
			TransferLogic.getInstance().queryHistoryAction(currentPage);
		}
	}
	
	private void refreshData(HashMap<String, String> map){
		new QueryHistoryDataTask().execute(map);
	}
	
	private void refreshActivity(){
		if (totalCount == 0){
			pager.setVisibility(View.GONE);
			return;
		}
		
		if (null == currentList || currentList.size() == 0){
			// 设置空页面
			ImageView emptyView = new ImageView(this);
			emptyView.setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT));
			emptyView.setImageResource(R.drawable.nodata);
			emptyView.setScaleType(ScaleType.CENTER_INSIDE);
			((ViewGroup)listView.getParent()).addView(emptyView);
			listView.setEmptyView(emptyView);
		}
		
		displayList.clear();
		
		for (HashMap<String, String> map : currentList){
			HashMap<String, String> tempMap = new HashMap<String, String>();
			tempMap.put("transType", AppDataCenter.getTransferName(map.get("tranCode")));
			tempMap.put("transAmount", map.get("tranAmt"));
			tempMap.put("transAccountNo", StringUtil.formatAccountNo(map.get("cardNo")));
			tempMap.put("transDateTime", DateUtil.formatDateTime(map.get("tranDate")+map.get("tranTime")));
			
			displayList.add(tempMap);
		}
		
		((SimpleAdapter)listView.getAdapter()).notifyDataSetChanged();
	}
	
	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
		HashMap<String, String> map = currentList.get(position);
		Intent intent =  new Intent(this, QueryTransferHistoryDetailActivity.class);
		intent.putExtra("map", map);
		this.startActivity(intent);
	}
	
	class QueryHistoryDataTask extends AsyncTask<Object, Object, Object>{

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			QueryTransferHistoryListActivity.this.showDialog(BaseActivity.PROGRESS_DIALOG, "正在刷新数据...");
		}

		@Override
		protected Object doInBackground(Object... params) {
			// field11:text,field12:text  | field11:text,field12:text
			try{
				@SuppressWarnings("unchecked")
				HashMap<String, String> receMap = (HashMap<String, String>)params[0];
				
				// 总条数
				if (null != receMap && receMap.size() != 0){
					if (totalCount > 0){
						// 取明细
						currentList = new ArrayList<HashMap<String, String>>();
						String fieldContent = receMap.get("detail");
						if ("".equals(fieldContent.trim()))
							return null;
							
						String[] contentArray = fieldContent.split("\\|");
						for (String content : contentArray){
							HashMap<String, String> map = new HashMap<String, String>();
							String[] tempArray = content.split("\\^");
							for (String str : tempArray){
								String[] fieldArray = str.split(":");
								if (fieldArray.length == 1){
									map.put(fieldArray[0], "");
								} else if (fieldArray.length == 2){
									map.put(fieldArray[0], fieldArray[1]);
								}
							}
							currentList.add(map);
						}
					}
					
				}
				
			}catch(Exception e){
				e.printStackTrace();
				
				QueryTransferHistoryListActivity.this.runOnUiThread(new Runnable(){
					@Override
					public void run() {
						TransferLogic.getInstance().gotoCommonFaileActivity("查询明细出现异常，请重试！");
					}
				});
			}
			return null;
		}

		@Override
		protected void onPostExecute(Object result) {
			try{
				pager.setTotalCount(totalCount);
				if (totalCount > 0){
					allDataMap.put(String.valueOf(pager.getPageIndex()), currentList);
				}
					
				refreshActivity();
				QueryTransferHistoryListActivity.this.hideDialog(BaseActivity.PROGRESS_DIALOG);
					
			}catch(Exception e){
				TransferLogic.getInstance().gotoCommonFaileActivity("刷新数据失败，请重试！");
			}
			
		}

	}

}
