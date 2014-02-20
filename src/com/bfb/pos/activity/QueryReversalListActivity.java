package com.bfb.pos.activity;

import java.util.ArrayList;
import java.util.HashMap;

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
import com.bfb.pos.agent.client.db.ReversalDBHelper;
import com.bfb.pos.model.ReversalModel;
import com.bfb.pos.util.StringUtil;
import com.bfb.pos.R;

public class QueryReversalListActivity extends BaseActivity implements OnClickListener, OnItemClickListener {

	private Button backButton = null;
	private ListView listView = null;
	
	private ArrayList<ReversalModel> modelList = new ArrayList<ReversalModel>();
	private ArrayList<HashMap<String, String>> mapList = new ArrayList<HashMap<String, String>>(); 
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		this.setContentView(R.layout.trans_list);
		
		this.findViewById(R.id.topInfoView);
		((TextView)this.findViewById(R.id.titleView)).setText(R.string.reversalList);
		
		backButton = (Button) this.findViewById(R.id.backButton);
		backButton.setOnClickListener(this);
		
		listView = (ListView) this.findViewById(R.id.transList);
		//生成适配器的Item和动态数组对应的元素  
        SimpleAdapter listItemAdapter = new SimpleAdapter(QueryReversalListActivity.this, mapList,
            R.layout.queryreversal_listitem, 
            new String[] {"reversalType", "reversalAmount", "reversalCount", "reversalState"},   
            //ImageItem的XML文件里面的一个ImageView,两个TextView ID  
            new int[] {R.id.reversalType, R.id.reversalAmount, R.id.reversalCount, R.id.reversalState}  
        );             
       
        listView.setAdapter(listItemAdapter);  
        listView.setOnItemClickListener(this);
		
		new GetReversalListTask().execute();
	}
	
	@Override
	public void onClick(View arg0) {
		this.finish();
	}
	
	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
		
	}
	
	class GetReversalListTask extends AsyncTask<Object, Object, Object>{
		
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			QueryReversalListActivity.this.showDialog(BaseActivity.PROGRESS_DIALOG, QueryReversalListActivity.this.getResources().getString(R.string.queryingData));
		}

		@Override
		protected Object doInBackground(Object... params) {
			ReversalDBHelper helper = new ReversalDBHelper();
			modelList = helper.queryAllReversal();
			return null;
		}

		@Override
		protected void onPostExecute(Object result) {
			if (modelList.isEmpty()){
				ImageView emptyView = new ImageView(QueryReversalListActivity.this);
				emptyView.setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT));
				emptyView.setImageResource(R.drawable.nodata);
				emptyView.setScaleType(ScaleType.CENTER_INSIDE);
				((ViewGroup)listView.getParent()).addView(emptyView);
				listView.setEmptyView(emptyView);
				
			} else {
				for (ReversalModel model : modelList){
					HashMap<String, String> tempMap = new HashMap<String, String>();
					tempMap.put("reversalType", AppDataCenter.getTransferName(model.getContent().get("fieldTrancode")));
					tempMap.put("reversalAmount", StringUtil.String2SymbolAmount(model.getContent().get("field4")));
					tempMap.put("reversalCount", model.getCount() + " 次");
					tempMap.put("reversalState", model.getState()==0?"成功":"失败");
					
					mapList.add(tempMap);
				}
				
				((SimpleAdapter)listView.getAdapter()).notifyDataSetChanged();
			}
			
			QueryReversalListActivity.this.hideDialog(BaseActivity.PROGRESS_DIALOG);
		}
		
	}

}
