package com.bfb.pos.activity;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bfb.pos.agent.client.ApplicationEnvironment;
import com.bfb.pos.agent.client.Constant;
import com.bfb.pos.dynamic.component.ViewException;
import com.bfb.pos.dynamic.core.Event;
import com.bfb.pos.model.BankModel;
import com.bfb.pos.util.ActivityUtil;
import com.bfb.pos.R;

public class BankSearchActivity extends BaseActivity {
	private Button btn_back;
	private EditText et_search = null;
	private ListView listView;
	private Adapter adapter;
	private ArrayList<BankModel> arrayList = new ArrayList<BankModel>();
	private HashMap<String, Object> recievMap = null;
	private int bankCode = 0;
	private int provinceCode = 0;
	private int cityCode = 0;
	private int totalPage;
	private int currentPage = 0;
	private int firstSearch = 0;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_bank_search);

		btn_back = (Button) this.findViewById(R.id.btn_back);
		btn_back.setOnClickListener(listener);

		Intent intent = this.getIntent();
		bankCode = intent.getIntExtra("bankCode", 0);
		provinceCode = intent.getIntExtra("provinceCode", 0);
		cityCode = intent.getIntExtra("cityCode", 0);

		et_search = (EditText) this.findViewById(R.id.et_search);
		LinearLayout ll_search = (LinearLayout) this.findViewById(R.id.ll_search);
		ll_search.setOnClickListener(listener);

		listView = (ListView) this.findViewById(R.id.listview);
		adapter = new Adapter(this);
		listView.setAdapter(adapter);
		listView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
				Intent intent = new Intent();
				intent.putExtra("bankbranchid", arrayList.get(arg2).getCode());
				intent.putExtra("bankbranchname", arrayList.get(arg2).getName());
				BankSearchActivity.this.setResult(5, intent);
				finish();

			}

		});

		refresh();
	}

//	@SuppressWarnings("unchecked")
//	protected void onNewIntent(Intent intent) {
//
//		super.onNewIntent(intent);
//
//		setIntent(intent);
//
//		recievMap = (HashMap<String, Object>) intent.getSerializableExtra("map");
//		ArrayList<BankModel> tmp_array = (ArrayList<BankModel>) recievMap.get("object");
//		for (int i = 0; i < tmp_array.size(); i++) {
//			BankModel object = tmp_array.get(i);
//			arrayList.add(object);
//		}
//		int count = Integer.valueOf((String) recievMap.get("page_count"));
//		totalPage = (count + Integer.parseInt(Constant.PAGESIZE) - 1) / Integer.parseInt(Constant.PAGESIZE);
//		if (arrayList != null) {
//			adapter.notifyDataSetChanged();
//		} else {
//			ActivityUtil.setEmptyView(listView);
//		}
//
//	}

	public void fromLogic(HashMap<String, Object> map){
		recievMap = map;
		ArrayList<BankModel> tmp_array = (ArrayList<BankModel>) recievMap.get("object");
		for (int i = 0; i < tmp_array.size(); i++) {
			BankModel object = tmp_array.get(i);
			arrayList.add(object);
		}
		int count = Integer.valueOf((String) recievMap.get("page_count"));
		totalPage = (count + Integer.parseInt(Constant.PAGESIZE) - 1) / Integer.parseInt(Constant.PAGESIZE);
		if (arrayList != null) {
			adapter.notifyDataSetChanged();
		} else {
			ActivityUtil.setEmptyView(listView);
		}
	}
	private void refresh() {
		Event event = new Event(null, "bankSearch", null);
		event.setTransfer("089011");
		HashMap<String, String> map = new HashMap<String, String>();
		map.put("bankid", bankCode + "");
		map.put("provinceid", provinceCode + "");
		map.put("cityid", cityCode + "");
		map.put("page_current", ++currentPage + "");
		map.put("page_size", Constant.PAGESIZE);
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

	private void actionSearch() {
		Event event = new Event(null, "bankSearch", null);
		event.setTransfer("089012");
		HashMap<String, String> map = new HashMap<String, String>();
		map.put("bankid", bankCode + "");
		map.put("provinceid", provinceCode + "");
		map.put("cityid", cityCode + "");
		map.put("bankbranchname", et_search.getText().toString());
		map.put("page_current", ++currentPage + "");
		map.put("page_size", Constant.PAGESIZE);
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

	private OnClickListener listener = new OnClickListener() {

		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.btn_back:
				finish();
				break;
			case R.id.ll_search:
				if (et_search.getText() != null) {
					if (firstSearch == 0) {
						arrayList.clear();
						currentPage = 0;
					}
					firstSearch = 1;
					actionSearch();
				} else {
					showToast("关键字不能为空！");
				}

				break;
			// case R.id.iv_clear:
			// et_search.setText("");
			// break;
			case R.id.moreButton:
				loadMore();
			default:
				break;
			}

		}
	};

	public final class ViewHolder {
		public RelativeLayout contentLayout;
		public RelativeLayout moreLayout;

		public TextView tv_content;
		public Button btn_more;

	}

	public class Adapter extends BaseAdapter {
		private LayoutInflater mInflater;

		public Adapter(Context context) {
			this.mInflater = LayoutInflater.from(context);
		}

		public int getCount() {
			if (currentPage + 1 < totalPage) {
				return arrayList.size() + 1;
			} else {
				return arrayList.size();
			}
		}

		public Object getItem(int arg0) {
			return arrayList.get(arg0);
		}

		public long getItemId(int arg0) {
			return arg0;
		}

		public View getView(int position, View convertView, ViewGroup parent) {
			ViewHolder holder = null;
			if (null == convertView) {
				holder = new ViewHolder();

				convertView = mInflater.inflate(R.layout.left_right_arrow_listitem, null);

				holder.contentLayout = (RelativeLayout) convertView.findViewById(R.id.reLayout_content);
				holder.moreLayout = (RelativeLayout) convertView.findViewById(R.id.moreLayout);
				holder.tv_content = (TextView) convertView.findViewById(R.id.tv_content);
				holder.btn_more = (Button) convertView.findViewById(R.id.moreButton);
				holder.btn_more.setOnClickListener(listener);

				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}

			if (currentPage + 1 < totalPage) {
				if (position == arrayList.size()) {
					holder.contentLayout.setVisibility(View.GONE);
					holder.moreLayout.setVisibility(View.VISIBLE);
				} else {
					holder.contentLayout.setVisibility(View.VISIBLE);
					holder.moreLayout.setVisibility(View.GONE);

					holder.tv_content.setText(((BankModel) (arrayList.get(position))).getName());
				}
			} else {
				holder.contentLayout.setVisibility(View.VISIBLE);
				holder.moreLayout.setVisibility(View.GONE);

				holder.tv_content.setText(((BankModel) (arrayList.get(position))).getName());
			}

			return convertView;
		}
	}

	private void loadMore() {
		Event event = new Event(null, "bankSearch", null);
		event.setTransfer("089011");
		HashMap<String, String> map = new HashMap<String, String>();
		map.put("bankid", bankCode + "");
		map.put("provinceid", provinceCode + "");
		map.put("cityid", cityCode + "");
		map.put("page_current", ++currentPage + "");
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

//	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//		BankSearchActivity.this.setResult(RESULT_OK, data);
//		this.finish();
//		
//	}
}
