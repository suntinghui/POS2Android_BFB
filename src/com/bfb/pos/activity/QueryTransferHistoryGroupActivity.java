package com.bfb.pos.activity;

import java.util.ArrayList;
import java.util.HashMap;

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
import android.widget.Toast;

import com.bfb.pos.agent.client.AppDataCenter;
import com.bfb.pos.agent.client.SystemConfig;
import com.bfb.pos.dynamic.core.Event;
import com.bfb.pos.R;

public class QueryTransferHistoryGroupActivity extends BaseActivity implements OnClickListener, OnItemClickListener {
	
	private Button backButton = null;
	private ListView listView = null;
	
	private ArrayList<HashMap<String, String>> mapList = new ArrayList<HashMap<String, String>>();
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		this.setContentView(R.layout.trans_list);
		
		this.findViewById(R.id.topInfoView);
		((TextView)this.findViewById(R.id.titleView)).setText(R.string.historyGroup);
		
		backButton = (Button) this.findViewById(R.id.backButton);
		backButton.setOnClickListener(this);
		
		listView = (ListView) this.findViewById(R.id.transList);
		//生成适配器的Item和动态数组对应的元素  
        SimpleAdapter listItemAdapter = new SimpleAdapter(this, mapList,
            R.layout.querytransgroup_listitem, 
            new String[] {"tranCode", "tranName", "total", "amount"},   
            new int[] {R.id.groupTypeID, R.id.groupType, R.id.groupCount, R.id.groupAmount}  
        );             
       
        listView.setAdapter(listItemAdapter);  
        listView.setOnItemClickListener(this);
        
        // 设置EmptyView
        ImageView emptyView = new ImageView(QueryTransferHistoryGroupActivity.this);
		emptyView.setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT));
		emptyView.setImageResource(R.drawable.nodata);
		emptyView.setScaleType(ScaleType.CENTER_INSIDE);
		((ViewGroup)listView.getParent()).addView(emptyView);
		listView.setEmptyView(emptyView);
		
		// 设置数据
		try{
	        String content = this.getIntent().getStringExtra("detail");
	        String[] items = content.split("\\|");
	        for (String item : items) {
	        	HashMap<String, String> tempMap = new HashMap<String, String>();
	        	String[] detail = item.split("\\^");
	        	for (String str : detail){
					String[] fieldArray = str.split(":");
					if (fieldArray.length == 1){
						tempMap.put(fieldArray[0], "");
					} else if (fieldArray.length == 2){
						tempMap.put(fieldArray[0], fieldArray[1]);
					}
					
					tempMap.put("tranName", AppDataCenter.getTransferName(tempMap.get("tranCode")));
				}
	        	mapList.add(tempMap);
	        }
	        
	        ((SimpleAdapter)listView.getAdapter()).notifyDataSetChanged();
	        
		} catch(Exception e){
			e.printStackTrace();
		}
		
	}
	
	@Override
	public void onClick(View arg0) {
		this.finish();
	}
	
	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
		if (mapList.get(position).get("total").trim().equals("0")){
			Toast.makeText(this, AppDataCenter.getTransferName(mapList.get(position).get("tranCode"))+" 交易没有明细可供查询", Toast.LENGTH_SHORT).show();
			return;
		}
		
		try{
			Event event = new Event(null,"transfer", null);
	        event.setFsk("Get_PsamNo|null#Get_VendorTerID|null");
	        event.setTransfer("600000002");
	        HashMap<String, String> map = new HashMap<String, String>();
	        map.put("pageNo", "1");
	        map.put("pageSize", String.valueOf(SystemConfig.getPageSize()));
	        map.put("tranCode", mapList.get(position).get("tranCode"));
	        map.put("totalCount", mapList.get(position).get("total"));
	        map.put("BeginDate", this.getIntent().getStringExtra("BeginDate"));
	        map.put("EndDate", this.getIntent().getStringExtra("EndDate"));
	        event.setStaticActivityDataMap(map);
	        event.trigger();
	        
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
}
