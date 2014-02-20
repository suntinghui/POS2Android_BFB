package com.bfb.pos.activity;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
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
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bfb.pos.agent.client.ApplicationEnvironment;
import com.bfb.pos.agent.client.Constant;
import com.bfb.pos.dynamic.component.ViewException;
import com.bfb.pos.dynamic.core.Event;
import com.bfb.pos.model.AnnouncementModel;
import com.bfb.pos.util.ActivityUtil;
import com.dhc.dynamic.util.DateUtil;
import com.bfb.pos.R;

public class AnnouncementListActivity extends BaseActivity implements OnClickListener, OnItemClickListener {
	
	private Button btn_back = null;
	private ListView listView = null;
	private Adapter adapter = null;
	
	private int totalPage;
	private int currentPage = 0;
	
	private ArrayList<AnnouncementModel> modelList = new ArrayList<AnnouncementModel>();
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		this.setContentView(R.layout.activity_annountce_list);
		
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
				//TODO
				AnnouncementModel model = modelList.get(arg2);
				Intent intent = new Intent(AnnouncementListActivity.this, AnnouncementDetailActivity.class);
				intent.putExtra("model", model);
				AnnouncementListActivity.this.startActivityForResult(intent, 0);
			}
			
		});
		refresh();

	}
	
	public final class ViewHolder{
		public RelativeLayout contentLayout;
		public RelativeLayout moreLayout;
		
		public TextView tv_title;
		public TextView tv_content;
		public TextView timeText;
		
		public Button moreButton;
	}
	
	public class Adapter extends BaseAdapter{
		private LayoutInflater mInflater;
		public Adapter(Context context) {
			this.mInflater = LayoutInflater.from(context);
		}
		
		public int getCount(){
			if (currentPage+1 < totalPage){
				return modelList.size() + 1;
			} else {
				Log.i("size:", modelList.size()+"");
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
				
				convertView = mInflater.inflate(R.layout.list_item_announce, null);
				
				holder.contentLayout = (RelativeLayout) convertView.findViewById(R.id.contentLayout);
				holder.moreLayout = (RelativeLayout) convertView.findViewById(R.id.moreLayout);
				
				holder.tv_title = (TextView) convertView.findViewById(R.id.tv_title);
				holder.tv_content = (TextView) convertView.findViewById(R.id.tv_content);
				holder.tv_content.setMinLines(2);
				holder.timeText = (TextView) convertView.findViewById(R.id.timeText);
				holder.moreButton = (Button) convertView.findViewById(R.id.moreButton);
				holder.moreButton.setOnClickListener(AnnouncementListActivity.this);
				
				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}
			
			String date_str = modelList.get(position).getNotice_date();
			String time_str = modelList.get(position).getNotice_time();
			String tmp_data_time = DateUtil.formatDateTime(date_str+time_str); 
			if (currentPage+1 < totalPage) {
				if (position == modelList.size()){
					holder.contentLayout.setVisibility(View.GONE);
					holder.moreLayout.setVisibility(View.VISIBLE);
				} else {
					holder.contentLayout.setVisibility(View.VISIBLE);
					holder.moreLayout.setVisibility(View.GONE);
					
					holder.tv_title.setText(modelList.get(position).getNotice_title());
					holder.tv_content.setText(modelList.get(position).getNotice_content());
					holder.timeText.setText(tmp_data_time);
				}
			} else {
				holder.contentLayout.setVisibility(View.VISIBLE);
				holder.moreLayout.setVisibility(View.GONE);
				
				holder.tv_title.setText(modelList.get(position).getNotice_title());
				holder.tv_content.setText(modelList.get(position).getNotice_content());
				holder.timeText.setText(tmp_data_time);
			}
			
			return convertView;
		}
	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onClick(View arg0) {
		// TODO Auto-generated method stub
		switch (arg0.getId()) {
		case R.id.btn_back:
			this.finish();
			break;

		default:
			break;
		}
	}
	
	public void refresh(){
		Event event = new Event(null,"announcelist", null);
        event.setTransfer("089004");
        HashMap<String, String> map = new HashMap<String, String>();
		map.put("tel", ApplicationEnvironment.getInstance().getPreferences().getString(Constant.PHONENUM, ""));
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
	protected void onNewIntent(Intent intent) {

		super.onNewIntent(intent);

		setIntent(intent);// must store the new intent unless getIntent() will
									// return the old one
		modelList = (ArrayList<AnnouncementModel>) intent.getSerializableExtra("array");
		if(modelList != null){
			adapter.notifyDataSetChanged();			
		}else{
			ActivityUtil.setEmptyView(listView);
		}
		
	}
}
