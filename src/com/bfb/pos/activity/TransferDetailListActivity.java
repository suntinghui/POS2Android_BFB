package com.bfb.pos.activity;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bfb.pos.agent.client.Constant;
import com.bfb.pos.dynamic.component.ViewException;
import com.bfb.pos.dynamic.core.Event;
import com.bfb.pos.model.TransferDetailModel;
import com.bfb.pos.util.ActivityUtil;
import com.bfb.pos.R;

public class TransferDetailListActivity extends BaseActivity implements OnClickListener, OnItemClickListener {
	
	private Button btn_back = null;
	private ListView listView = null;
	private Adapter adapter = null;
	
	private int totalPage;
	private int currentPage = 0;
	
	private ArrayList<TransferDetailModel> modelList = new ArrayList<TransferDetailModel>();
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		this.setContentView(R.layout.activity_transfer_detail_list);
		
		this.findViewById(R.id.topInfoView);
		
		btn_back = (Button) this.findViewById(R.id.btn_back);
		btn_back.setOnClickListener(this);
		
		listView = (ListView)this.findViewById(R.id.listview);
//		ActivityUtil.setEmptyView(listView);
		
		adapter = new Adapter(this);
		listView.setAdapter(adapter);
		listView.setOnItemClickListener(new OnItemClickListener(){

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
				Intent intent = new Intent(TransferDetailListActivity.this, TransferDetailActivity.class);
				intent.putExtra("model", modelList.get(arg2));
				TransferDetailListActivity.this.startActivity(intent);
			}
			
		});
		refresh();

	}
	
	public final class ViewHolder{
		public LinearLayout contentLayout;
		public RelativeLayout moreLayout;
		
		public TextView tv_account1;
		public TextView tv_amount;
		public TextView tv_local_log;
		
		public Button moreButton;
	}
	
	public class Adapter extends BaseAdapter{
		private LayoutInflater mInflater;
		public Adapter(Context context) {
			this.mInflater = LayoutInflater.from(context);
		}
		
		public int getCount(){
			
			if (currentPage < totalPage){
				return modelList.size() + 1;
			} else {
				return modelList.size();
			}
		}
		
		public Object getItem(int arg0){
			return modelList.get(arg0);
		}
		
		public long getItemId(int arg0){
			return arg0;
		}
		
		public View getView(int position, View convertView, ViewGroup parent){
			ViewHolder holder = null;
			if (null == convertView){
				holder = new ViewHolder();
				
				convertView = mInflater.inflate(R.layout.list_item_transfer, null);
				
				holder.contentLayout = (LinearLayout) convertView.findViewById(R.id.contentLayout);
				holder.moreLayout = (RelativeLayout) convertView.findViewById(R.id.moreLayout);
				
				holder.tv_account1 = (TextView) convertView.findViewById(R.id.tv_account1);
				holder.tv_amount = (TextView) convertView.findViewById(R.id.tv_amount);
				holder.tv_local_log = (TextView) convertView.findViewById(R.id.tv_local_log);
				holder.moreButton = (Button) convertView.findViewById(R.id.moreButton);
				holder.moreButton.setOnClickListener(TransferDetailListActivity.this);
				
				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}
			
			if (currentPage < totalPage) {
				if (position == modelList.size()){
					holder.contentLayout.setVisibility(View.GONE);
					holder.moreLayout.setVisibility(View.VISIBLE);
				} else {
					holder.contentLayout.setVisibility(View.VISIBLE);
					holder.moreLayout.setVisibility(View.GONE);
					
					holder.tv_account1.setText(modelList.get(position).getAccount1()==null?"":modelList.get(position).getAccount1());
					holder.tv_amount.setText(modelList.get(position).getAmount()==null?"":("¥ " + modelList.get(position).getAmount()));
					holder.tv_local_log.setText(modelList.get(position).getLocal_log()==null?"":modelList.get(position).getLocal_log());
				}
			} else {
				holder.contentLayout.setVisibility(View.VISIBLE);
				holder.moreLayout.setVisibility(View.GONE);
				
				holder.tv_account1.setText(modelList.get(position).getAccount1()==null?"":modelList.get(position).getAccount1());
				holder.tv_amount.setText(modelList.get(position).getAmount()==null?"":("¥ " + modelList.get(position).getAmount()));
				holder.tv_local_log.setText(modelList.get(position).getLocal_log()==null?"":modelList.get(position).getLocal_log());
			}
			
			return convertView;
		}
	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
		
		Intent intent = new Intent(TransferDetailListActivity.this, TransferDetailActivity.class);
		intent.putExtra("model", modelList.get(arg2));
		TransferDetailListActivity.this.startActivity(intent);
		
		
	}

	@Override
	public void onClick(View arg0) {
		// TODO Auto-generated method stub
		switch (arg0.getId()) {
		case R.id.btn_back:
			this.finish();
			break;
		case R.id.moreButton:
			loadMoreData();
			break;
		default:
			break;
		}
	}
	
	public void refresh(){
		Event event = new Event(null,"queryTransList", null);
        event.setTransfer("089000");
        HashMap<String, String> map = new HashMap<String, String>();
		map.put("page_current", ++currentPage +"");
		map.put("page_size", Constant.PAGESIZE);
		event.setStaticActivityDataMap(map);
        try {
			event.trigger();
		} catch (ViewException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	@SuppressWarnings("unchecked")
	public void fromLogic(HashMap<String, Object> map){
		ArrayList<TransferDetailModel> list = (ArrayList<TransferDetailModel>) map.get("list");
		modelList.addAll(list);
		int count = Integer.valueOf((String) map.get("total"));
		
		totalPage = (count + Integer.parseInt(Constant.PAGESIZE) - 1) / Integer.parseInt(Constant.PAGESIZE);
		
		if(modelList != null){
			adapter.notifyDataSetChanged();			
		}else{
			ActivityUtil.setEmptyView(listView);
		}
	}
	private void loadMoreData(){
		refresh();
	}
}
