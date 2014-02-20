package com.bfb.pos.activity;

import java.util.ArrayList;
import java.util.HashMap;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.bfb.pos.agent.client.db.PayeeAccountDBHelper;
import com.bfb.pos.model.PayeeAccountModel;
import com.bfb.pos.util.StringUtil;
import com.bfb.pos.R;

public class PayeeAccountListActivity extends BaseActivity implements OnClickListener, OnItemClickListener, OnItemLongClickListener {
	
	private Button backButton = null;
	private ListView listView = null;
	private ArrayList<PayeeAccountModel> list = null;
	
	private boolean editMode = false;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		this.setContentView(R.layout.payeeaccount_list);
		
		this.findViewById(R.id.topInfoView);
		
		backButton = (Button) this.findViewById(R.id.backButton);
		backButton.setOnClickListener(this);
		
		listView = (ListView) this.findViewById(R.id.accountsList);
		listView.setOnItemClickListener(this);
		listView.setOnItemLongClickListener(this);
		
		// 提示用户menu按纽进行操作
		Toast.makeText(this, "请按菜单键添加编辑联系人", Toast.LENGTH_SHORT).show();
		
		new QueryAccountListTask().execute();
		
	}
	
	@Override
	public void onItemClick(AdapterView<?> adapterView, View view, int position, long arg3) {
		if (editMode){
			PayeeAccountModel model = list.get(position);
			PayeeAccountHolder holder = (PayeeAccountHolder) view.getTag();
			holder.checkBox.toggle();
			((PayeeAccountAdapter)listView.getAdapter()).isSelectedMap.put(model.getAccountNo(), holder.checkBox.isChecked());
			
			// 自动调出Menu
			// this.openOptionsMenu();
		}
	}
	
	@Override
	public boolean onItemLongClick(AdapterView<?> arg0, View arg1, int arg2,long arg3) {
		if (!editMode){
			loadEditMode();
			// 自动调出Menu
			this.openOptionsMenu();
			
		}
		return false;
	}
	
	private void loadEditMode(){
		editMode = true;
		((PayeeAccountAdapter)listView.getAdapter()).isSelectedMap.clear();
		((PayeeAccountAdapter)listView.getAdapter()).notifyDataSetChanged();
		
		// 这里是一个小的细节问题，之所以这样处理是没有其它更好办法的折中处理。因为没有找到让checkBox的图片靠右显示的办法，在
		// 编辑模式下的右边空着太多，不很美观。所以取消ListView的MarginRight。在效果上稍微好点。 ^ ~ ^
		LinearLayout.LayoutParams lp1 = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT); 
		lp1.setMargins(10, 10, 0, 10);
		listView.setLayoutParams(lp1);
	}
	
	private void quitEditMode(){
		editMode = false;
		((PayeeAccountAdapter)listView.getAdapter()).notifyDataSetChanged();
		
		// 恢复。。。
		LinearLayout.LayoutParams lp2 = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT); 
		lp2.setMargins(10, 10, 10, 10);
		listView.setLayoutParams(lp2);
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode == Activity.RESULT_OK){
			PayeeAccountDBHelper helper = new PayeeAccountDBHelper();
			list = helper.queryAll();
			
			((PayeeAccountAdapter)listView.getAdapter()).notifyDataSetChanged();
		}
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK){
			if (editMode){
				quitEditMode();
			} else {
				this.finish();
			}
			
			return false;
		}
		
		return super.onKeyDown(keyCode, event);
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.payeeaccount_menu, menu);
		return true;
	}
	
	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		MenuItem editMenuItem = menu.findItem(R.id.editMenu);
		MenuItem addMenuItem = menu.findItem(R.id.addMenu);
		
		MenuItem cancelEditMenuItem = menu.findItem(R.id.cancelEditMenu);
		MenuItem selectAllMenuItem = menu.findItem(R.id.selectAllMenu);
		MenuItem cancelSelectAllMenuItem = menu.findItem(R.id.cancelSelectAllMenu);
		MenuItem deleteMenuItem = menu.findItem(R.id.deleteMenu);
		
		editMenuItem.setVisible(!editMode);
		addMenuItem.setVisible(!editMode);
		
		cancelEditMenuItem.setVisible(editMode);
		selectAllMenuItem.setVisible(editMode);
		cancelSelectAllMenuItem.setVisible(editMode);
		deleteMenuItem.setVisible(editMode);
		
		editMenuItem.setEnabled(listView.getCount() == 0 ? false : true);
		deleteMenuItem.setEnabled(((PayeeAccountAdapter)listView.getAdapter()).isSelectedMap.containsValue(true));
		
		return super.onPrepareOptionsMenu(menu);
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch(item.getItemId()){
		case R.id.addMenu:
			this.startActivityForResult(new Intent(this, AddPayeeAccountActivity.class), 0);
			return true;
			
		case R.id.editMenu:
			this.loadEditMode();
			
			return true;
			
		case R.id.cancelEditMenu:
			this.quitEditMode();
			
			return true;
			
		case R.id.selectAllMenu:
			for (PayeeAccountModel model : list){
				((PayeeAccountAdapter)listView.getAdapter()).isSelectedMap.put(model.getAccountNo(), true);
			}
			((PayeeAccountAdapter)listView.getAdapter()).notifyDataSetChanged();
			return true;
			
		case R.id.cancelSelectAllMenu:
			for (PayeeAccountModel model : list){
				((PayeeAccountAdapter)listView.getAdapter()).isSelectedMap.put(model.getAccountNo(), false);
			}
			((PayeeAccountAdapter)listView.getAdapter()).notifyDataSetChanged();
			return true;
			
		case R.id.deleteMenu:
			ArrayList<String> selectList = new ArrayList<String>();
			HashMap<String, Boolean> tempMap = ((PayeeAccountAdapter)listView.getAdapter()).isSelectedMap;
			for (String str : tempMap.keySet()){
				if (tempMap.get(str))
					selectList.add(str);
			}
			
			PayeeAccountDBHelper helper = new PayeeAccountDBHelper();
			boolean flag = helper.deletePayeeAccounts(selectList);
			
			if(flag){
				Toast.makeText(this, "成功删除所选收款人信息", Toast.LENGTH_SHORT).show();
			}else{
				Toast.makeText(this, "删除收款人信息出现错误", Toast.LENGTH_SHORT).show();
			}
			
			PayeeAccountDBHelper helper2 = new PayeeAccountDBHelper();
			list = helper2.queryAll();
			
			((PayeeAccountAdapter)listView.getAdapter()).notifyDataSetChanged();
			
			break;
		}
		
		return false;
	}

	@Override
	public void onClick(View view) {
		switch(view.getId()){
		case R.id.backButton:
			this.finish();
			break;
		}
	}

	public final class PayeeAccountHolder{
		public TextView accountNoView;
		public TextView nameView;
		public TextView bankView;
		public CheckBox checkBox;
	}
	class PayeeAccountAdapter extends BaseAdapter{
		
		private Context m_context;
		private LayoutInflater m_listContainer;
		public HashMap<String, Boolean> isSelectedMap;
		
		public PayeeAccountAdapter(Context context){
			this.m_context = context;
			this.m_listContainer = LayoutInflater.from(m_context);
			isSelectedMap = new HashMap<String, Boolean>();
		}
		
		@Override
		public int getCount() {
			return list.size();
		}

		@Override
		public Object getItem(int position) {
			return list.get(position);  
		}

		@Override
		public long getItemId(int arg0) {
			return 0;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup arg2) {
			PayeeAccountHolder holder = null;
			if(convertView == null){
				holder = new PayeeAccountHolder();
				convertView = m_listContainer.inflate(R.layout.payeeaccount_listitem, null);
				holder.accountNoView = (TextView) convertView.findViewById(R.id.accountNo);
				holder.nameView = (TextView) convertView.findViewById(R.id.name);
				holder.bankView = (TextView) convertView.findViewById(R.id.bank);
				holder.checkBox = (CheckBox) convertView.findViewById(R.id.checkBox);
				convertView.setTag(holder);
			}else{
				holder = (PayeeAccountHolder) convertView.getTag();
			}
			
			PayeeAccountModel model = list.get(position);
			holder.accountNoView.setText(StringUtil.formatAccountNo(model.getAccountNo()));
			holder.nameView.setText(model.getName());
			holder.bankView.setText(model.getBank());
			
			if(editMode == true){
				holder.checkBox.setVisibility(View.VISIBLE);
				if(this.isSelectedMap.containsKey(model.getAccountNo()))
					holder.checkBox.setChecked(this.isSelectedMap.get(model.getAccountNo()));
				else
					holder.checkBox.setChecked(false);
			}else{
				holder.checkBox.setVisibility(View.GONE);
			}
				
			
			return convertView;
		}
		
	}
	
	class QueryAccountListTask extends AsyncTask<Object, Object, Object>{
		
		@Override
		protected void onPreExecute() {
			PayeeAccountListActivity.this.showDialog(PROGRESS_DIALOG, PayeeAccountListActivity.this.getResources().getString(R.string.queryingAccountIn));
		}

		@Override
		protected Object doInBackground(Object... arg0) {
			PayeeAccountDBHelper helper = new PayeeAccountDBHelper();
			list = helper.queryAll();
			return null;
		}
		
		@Override
		protected void onPostExecute(Object result) {
			PayeeAccountAdapter adapter = new PayeeAccountAdapter(PayeeAccountListActivity.this);
	    	listView.setAdapter(adapter);
	    	editMode = false;
	    	
	    	ImageView emptyView = new ImageView(PayeeAccountListActivity.this);
			emptyView.setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT));
			emptyView.setImageResource(R.drawable.nodata);
			emptyView.setScaleType(ScaleType.CENTER_INSIDE);
			((ViewGroup)listView.getParent()).addView(emptyView);
			listView.setEmptyView(emptyView);
			
			PayeeAccountListActivity.this.hideDialog(PROGRESS_DIALOG);
			
			// 为了更好的用户体验，列表为空时自动打开menu
			if (list.isEmpty()){
				PayeeAccountListActivity.this.openOptionsMenu();
			}
		}
		
	}

}
