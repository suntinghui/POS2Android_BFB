package com.bfb.pos.activity;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences.Editor;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.util.Xml;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.WindowManager;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.SlidingDrawer.OnDrawerCloseListener;
import android.widget.SlidingDrawer.OnDrawerOpenListener;
import android.widget.TextView;
import android.widget.Toast;

import com.bfb.pos.activity.view.SlidingDrawerEx;
import com.bfb.pos.activity.view.viewflow.CircleFlowIndicator;
import com.bfb.pos.activity.view.viewflow.ViewFlow;
import com.bfb.pos.activity.view.viewflow.ViewFlow.ViewSwitchListener;
import com.bfb.pos.agent.client.ApplicationEnvironment;
import com.bfb.pos.agent.client.Constant;
import com.bfb.pos.agent.client.DownloadFileRequest;
import com.bfb.pos.agent.client.TransferLogic;
import com.bfb.pos.dynamic.component.ViewException;
import com.bfb.pos.dynamic.core.Event;
import com.bfb.pos.dynamic.core.ViewPage;
import com.bfb.pos.model.CatalogModel;
import com.bfb.pos.util.AssetsUtil;
import com.bfb.pos.R;

public class LRCatalogActivity extends BaseActivity implements OnItemClickListener, OnScrollListener, OnDrawerOpenListener, OnDrawerCloseListener{
	private String url = null;
	private ArrayList<CatalogModel> parentCatalogList = new ArrayList<CatalogModel>();
	private ArrayList<CatalogModel> childCatalogList = new ArrayList<CatalogModel>();
	private ArrayList<CatalogModel> currentCatalogList = new ArrayList<CatalogModel>();
	private FirstAdapter parentAdapter;
	private SecondAdapter childAdapter;
	
	private ImageView parentUpArrowImage = null;
	private ImageView parentDownArrowImage = null;
	
	private ListView parentListView = null;
	private ListView childListView = null;
	private SlidingDrawerEx slideDrawer = null;
	
	private CatalogModel currentCatalog = null;
	
	private int[] resIds = new int[] { R.drawable.beginguide01,
			R.drawable.beginguide02, R.drawable.beginguide03, R.drawable.beginguide04};
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		this.setContentView(R.layout.lr_catalog);
		
		parentUpArrowImage = (ImageView) this.findViewById(R.id.leftUpArrow);
		parentDownArrowImage = (ImageView) this.findViewById(R.id.leftDownArrow);
		
		parentListView = (ListView) this.findViewById(R.id.firstCatalog);
		childListView = (ListView) this.findViewById(R.id.secontCatalog);
		slideDrawer = (SlidingDrawerEx)this.findViewById(R.id.slidingdrawer);
		
		parentListView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
		childListView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
		slideDrawer.setSelected(false);
		
		this.getCatalogFromXML();
		
    	for (CatalogModel catalog : childCatalogList){
    		if (catalog.getParentId() == parentCatalogList.get(0).getCatalogId()){
    			currentCatalogList.add(catalog);
    		}
    	}
    	
    	parentAdapter = new FirstAdapter(this);
    	parentListView.setAdapter(parentAdapter); 
    	childAdapter = new SecondAdapter(this);
    	childListView.setAdapter(childAdapter);
    	
    	
    	parentListView.setOnItemClickListener(this);
    	parentListView.setOnScrollListener(this);
    	childListView.setOnItemClickListener(this);
    	//childListView.setOnItemLongClickListener(this);
		
    	// 抽屉事件
	    slideDrawer.setOnDrawerOpenListener(this);  
	    slideDrawer.setOnDrawerCloseListener(this); 
	    
	    this.refreshArrowImage();
	    
	    
	    try{
//	    	Event event = new Event(null,"transfer", null);
//	        String fskStr = "Get_MAC|int:0,int:1,string:null,string:F133D6A3AF9014AB";
////	    	String fskStr = "Get_RenewVendorTerID|string:195300430101001,string:19530024";
//	        event.setFsk(fskStr);
//	        event.trigger();
	        
	    }catch(Exception e){
	    	
	    }
	    
        
	}
	
	@Override
	public void onWindowFocusChanged(boolean hasFocus) {
		super.onWindowFocusChanged(hasFocus);
		
		if (hasFocus && ApplicationEnvironment.getInstance().getPreferences().getBoolean(Constant.NEWAPP, false)){
			Editor editor = ApplicationEnvironment.getInstance().getPreferences().edit();
			editor.putBoolean(Constant.NEWAPP, false);
			editor.commit();
			
//			this.showBeginnerGuide();
		}
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int arg2, long arg3) {
		if (parent.getId() == R.id.firstCatalog){
			parentAdapter.setSelectItem(arg2);
			parentAdapter.notifyDataSetInvalidated();
			
			currentCatalogList.clear();
			for (CatalogModel catalog : childCatalogList){
	    		if (catalog.getParentId() == parentCatalogList.get(arg2).getCatalogId()){
	    			currentCatalogList.add(catalog);
	    		}
	    	}
			/**
			 * int locY = slideBar.getHeight()/2 - view.getHeight()/2 - view.getTop();
			Bitmap tempBitmap = Bitmap.createBitmap(slideBar, 0, locY, slideBar.getWidth(), firstListView.getHeight());
			firstListView.setBackgroundDrawable(new BitmapDrawable(tempBitmap));
			 */
			
			childAdapter.setSelectItem(0);
			childAdapter.notifyDataSetChanged();
		} else if (parent.getId() == R.id.secontCatalog){
			try{
    			childAdapter.setSelectItem(arg2);
    			
    			currentCatalog = currentCatalogList.get(arg2);
    			
    			// 先判断该交易是否已经开通
    			if (!currentCatalog.isActive()){
    				Toast.makeText(LRCatalogActivity.this, currentCatalog.getTitle()+"尚未开通，敬请期待", Toast.LENGTH_SHORT).show();
    				return;
    			}
    			
    			// 先对需要进行冲正的交易进行检查
    			if (currentCatalog.isNeedReverse()){
    				if (TransferLogic.getInstance().reversalAction())
    					return;
    			}
    			
    			this.catalogAction();
    			
			}catch(Exception e){
				e.printStackTrace();
			}
		}
	}
	
	// parentListView
	@Override
	public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
		refreshArrowImage();
	}

	@Override
	public void onScrollStateChanged(AbsListView arg0, int scrollState) {
		
	}
	
	// SlideDrawer
	@Override
	public void onDrawerOpened() {
    	slideDrawer.setSelected(true);
    	slideDrawer.getHandle().setBackgroundResource(R.drawable.handle_selected);
    	ViewFlow viewFlow = (ViewFlow) findViewById(R.id.guide_gallery);
    	viewFlow.setAdapter(new ImageAdapter(LRCatalogActivity.this));
    	CircleFlowIndicator indic = (CircleFlowIndicator) findViewById(R.id.viewflowindic);
		viewFlow.setFlowIndicator(indic);
    	
    	LinearLayout topLayout = (LinearLayout) findViewById(R.id.topLayout);
    	topLayout.setGravity(Gravity.LEFT | Gravity.CENTER_VERTICAL);
    } 
	
	@Override  
    public void onDrawerClosed() {
    	slideDrawer.setSelected(false);
    	LinearLayout topLayout = (LinearLayout) findViewById(R.id.topLayout);
    	topLayout.setGravity(Gravity.CENTER_HORIZONTAL | Gravity.CENTER_VERTICAL);
    	
    	slideDrawer.getHandle().setBackgroundResource(R.drawable.handle_normal);
    	System.gc();
    }
	
	private void catalogAction(){
		try{
//			if (currentCatalog.getCatalogId() == 61){ // 新手指导
//				
//				/**
//				 // 修改商户号终端号
//				  
//				try{
//					Event event = new Event(null,"login", null);
//			        String fskStr = "Get_RenewVendorTerID|string:105360170110159,string:00021679";
//			        event.setFsk(fskStr);
//			        event.trigger();
//			        
//				}catch(Exception e){
//					e.printStackTrace();
//				}
//				
//				***/
//				
//				this.showBeginnerGuide();
//				
//				return;
//			}
			
			switch (currentCatalog.getCatalogId()) {
			case 16:
				if(!Constant.status.equals("9")){
					this.showDialog(MODAL_DIALOG, "等待终审！");
					return;
				}
				break;
			case 21:
			case 31:
			case 32:
			case 51:
			{
				if(!Constant.status.equals("9")){
					this.showDialog(MODAL_DIALOG, "等待终审！");
					return;
				}else if (!Constant.isSign) {
					this.showDialog(MODAL_DIALOG, "用户尚未签到，请先签到！");
					return;
				}
			}
				break;

			default:
				break;
			}
			
			if (currentCatalog.getActionType().equalsIgnoreCase("dynamic")){
				ViewPage transferViewPage = new ViewPage("transfer");
		        Event event = new Event(transferViewPage,"transfer", currentCatalog.getActionId());
		        transferViewPage.addAnEvent(event);
		        event.trigger();
		        
			} else if (currentCatalog.getActionType().equalsIgnoreCase("activity")){
				Intent intent = new Intent(ApplicationEnvironment.getInstance().getApplication().getPackageName() + "." + currentCatalog.getActionId());
				BaseActivity.getTopActivity().startActivity(intent);
				
			} else if(currentCatalog.getActionType().equalsIgnoreCase("service")){
				Intent intent = new Intent(ApplicationEnvironment.getInstance().getApplication().getPackageName() + "." + currentCatalog.getActionId());
				BaseActivity.getTopActivity().startService(intent);
				
			} else if(currentCatalog.getCatalogId() == 62){
				try {

					Event event = new Event(null, "version", null);
					event.setTransfer("089018");
					event.trigger();

				} catch (Exception e) {
					e.printStackTrace();
				}
			}else {
				Toast.makeText(this, "请配置正确的actionType属性！动态-dynamic 静态-activity/service", Toast.LENGTH_SHORT).show();
				
			}
			
		} catch(Exception e){
			e.printStackTrace();
			Toast.makeText(this, "未能正确跳转界面，请检查配置文件", Toast.LENGTH_SHORT).show();
		}
	}
	
	public void showAlertView(float version, String url){
		this.url = url;
//		this.url = "https://58.221.92.138:8443/yunpaiApi/api/download/POS2Android_YLT";
		if(Constant.version<version){
			AlertDialog.Builder dialog = new AlertDialog.Builder(this);
			dialog.setTitle("提示");
			dialog.setMessage("有新版本，是否下载更新？");
			dialog.setCancelable(false);
			dialog.setPositiveButton("立即更新", new DialogInterface.OnClickListener() {

				@Override
				public void onClick(DialogInterface dialog, int arg1) {
					dialog.dismiss();
					Update(LRCatalogActivity.this.url);
				}
			});
			dialog.setNegativeButton("暂不更新", new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					dialog.dismiss();
					
				}
			});
			
			dialog.create().show();
		}else{
			AlertDialog.Builder dialog = new AlertDialog.Builder(this);
			dialog.setTitle("提示");
			dialog.setMessage("当前版本为最新版本！");
			dialog.setCancelable(false);
			dialog.setPositiveButton("确认", new DialogInterface.OnClickListener() {

				@Override
				public void onClick(DialogInterface dialog, int arg1) {
					dialog.dismiss();
				}
			});
			
			dialog.create().show();
		}
		
	}
	private void Update(String url){
		
		DownloadFileRequest.sharedInstance().downloadAndOpen(this, url, "POS2Android_YLT.apk");
	}
	private void refreshArrowImage(){
		if (parentListView.getFirstVisiblePosition() != 0){
			parentUpArrowImage.setImageResource(R.drawable.up_arrow_enable);
		} else {
			parentUpArrowImage.setImageResource(R.drawable.up_arrow_disable);
		}
		
		
		if (parentListView.getLastVisiblePosition() != parentListView.getCount()-1){
			parentDownArrowImage.setImageResource(R.drawable.down_arrow_enable);
		} else {
			parentDownArrowImage.setImageResource(R.drawable.down_arrow_disable);
		}
	}
	
	private void showBeginnerGuide(){
		View view = getLayoutInflater().inflate(R.layout.beginnerguide, null);
		FrameLayout layout = (FrameLayout) view.findViewById(R.id.content);
		final PopupWindow popup = new PopupWindow(layout, LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT, true);
		popup.setAnimationStyle(R.style.PopupAnimation);
		popup.setBackgroundDrawable(new BitmapDrawable());
		popup.setFocusable(true);
		popup.update();
		popup.showAtLocation(findViewById(R.id.bgImageView), Gravity.CENTER, 0, 0);
		
		final Button btn = (Button) view.findViewById(R.id.beginguide_btn);
		btn.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View arg0) {
				popup.dismiss();
			}
		});
		
		ViewFlow viewFlow = (ViewFlow) view.findViewById(R.id.guide_gallery);
    	viewFlow.setAdapter(new ImageAdapter(LRCatalogActivity.this));
    	viewFlow.setOnViewSwitchListener(new ViewSwitchListener(){
			@Override
			public void onSwitched(View view, int position) {
				if (position == resIds.length-1){
					btn.setVisibility(View.VISIBLE);
				} else {
					btn.setVisibility(View.GONE);
				}
			}
    	});
    	
    	CircleFlowIndicator indic = (CircleFlowIndicator) view.findViewById(R.id.viewflowindic);
		viewFlow.setFlowIndicator(indic);
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK){
			if (slideDrawer.isSelected()){
				slideDrawer.close();
				return false;
			} else{
				exit();
				return false;
			}
		}
		
		return super.onKeyDown(keyCode, event);
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
	}
	
	private void exit(){
		AlertDialog.Builder builder = new Builder(this);
		builder.setMessage("\n您确定要退出优乐通吗？");
		builder.setPositiveButton("确定", new android.content.DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
				ApplicationEnvironment.getInstance().ForceLogout();
				
				finish();
				// 必须关闭整个系统。缺点是也会关闭服务
				//android.os.Process.killProcess(android.os.Process.myPid());
			}
		});

		builder.setNegativeButton("取消", new android.content.DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
			}
		});
		
		builder.show();
	}

	// 一级菜单Adapter
	public final class FirstViewHolder{
		public ImageView catalogIcon;
		public TextView titleText;
	}
	public class FirstAdapter extends BaseAdapter {
		private LayoutInflater mInflater;
		public FirstAdapter(Context context) {
			this.mInflater = LayoutInflater.from(context);
		}
		
		public int getCount(){
			return parentCatalogList.size();
		}
		
		public Object getItem(int arg0) {
			return parentCatalogList.get(arg0);
		}
		
		public long getItemId(int arg0) {
			return arg0;
		}
		public View getView(int position, View convertView, ViewGroup parent) {
			FirstViewHolder holder = null;
			if (convertView == null) {
				holder = new FirstViewHolder();
				convertView = mInflater.inflate(R.layout.first_catalog, null);
				
				holder.catalogIcon = (ImageView) convertView.findViewById(R.id.catalogIcon);
				holder.titleText = (TextView) convertView.findViewById(R.id.catalogTitle);
				
				convertView.setTag(holder);			
			} else {
				holder = (FirstViewHolder) convertView.getTag();
			}
			
			holder.catalogIcon.setImageResource(getIconResId(parentCatalogList.get(position).getIconId()));
			holder.titleText.setText(parentCatalogList.get(position).getTitle());
			
			if (position == selectItem) {
				convertView.setBackgroundResource(R.drawable.slidebg);
				holder.titleText.setTextColor(Color.WHITE);
			} else {
				convertView.setBackgroundDrawable(null);
				holder.titleText.setTextColor(Color.rgb(200, 200, 200));
			}
			
			return convertView;
		}
		public  void setSelectItem(int selectItem) {
			 this.selectItem = selectItem;
		}
		private int  selectItem=0;
		
	}
	
	// 二级级菜单Adapter
	public final class SecondViewHolder {
		public TextView titleText;
		public ImageView suffixImage;
	}
	
	public class SecondAdapter extends BaseAdapter {
		private LayoutInflater mInflater;
		
		public SecondAdapter(Context context) {
			this.mInflater = LayoutInflater.from(context);
		}
		public int getCount() {
			return currentCatalogList.size();
		}
		public Object getItem(int arg0) {
			return currentCatalogList.get(arg0);
		}
		public long getItemId(int arg0) {
			return arg0;
		}
		public View getView(final int position, View convertView, ViewGroup parent) {
			SecondViewHolder holder = null;
			if (convertView == null) {
				holder = new SecondViewHolder();
				convertView = mInflater.inflate(R.layout.second_catalog, null);
				holder.titleText = (TextView) convertView.findViewById(R.id.catalogTitle);
				holder.suffixImage = (ImageView) convertView.findViewById(R.id.suffix);
				
				convertView.setTag(holder);			
			} else {
				holder = (SecondViewHolder) convertView.getTag();
			}
			
			CatalogModel catalog = currentCatalogList.get(position);
			holder.titleText.setText(catalog.getTitle());
			
			if (position == selectItem) {
				convertView.setBackgroundResource(R.drawable.child_catalog_select);
				holder.suffixImage.setImageResource(R.drawable.suffix_selected);
				if (24 == catalog.getCatalogId() && !ApplicationEnvironment.getInstance().getPreferences().getString(Constant.SERVER_ANNOUNCEMENT_LASTEST_NUM, "0").equals(ApplicationEnvironment.getInstance().getPreferences().getString(Constant.SYSTEM_ANNOUNCEMENT_LASTEST_NUM, "0"))){
					holder.suffixImage.setImageResource(R.drawable.new_icon);
				} else {
					holder.suffixImage.setImageResource(R.drawable.suffix_selected);
				}
				
				if (catalog.isActive()){
					holder.titleText.setTextColor(Color.WHITE);
				} else {
					holder.titleText.setTextColor(Color.GRAY);
				}
			} else {
				convertView.setBackgroundResource(R.drawable.child_catalog_normal);
				if (24 == catalog.getCatalogId() && !ApplicationEnvironment.getInstance().getPreferences().getString(Constant.SERVER_ANNOUNCEMENT_LASTEST_NUM, "").equals(ApplicationEnvironment.getInstance().getPreferences().getString(Constant.SYSTEM_ANNOUNCEMENT_LASTEST_NUM, ""))){
					holder.suffixImage.setImageResource(R.drawable.new_icon);
				} else {
					holder.suffixImage.setImageResource(R.drawable.suffix_normal);
				}
				
				if (catalog.isActive()){
					holder.titleText.setTextColor(Color.rgb(99, 99, 99));
				} else {
					holder.titleText.setTextColor(Color.rgb(200, 200, 200));
				}
			}	
			
			return convertView;
		}
		public  void setSelectItem(int selectItem) {
			 this.selectItem = selectItem;
		}
		private int selectItem = 0;
	}

	private int getIconResId(int iconId) {
		String resourceName = "icon_" + iconId;
		int resourceId = getResources().getIdentifier(resourceName, "drawable", getPackageName());
		if (resourceId == 0)
			resourceId = R.drawable.icon;
		
		return resourceId;
	}
	
	public class ImageAdapter extends BaseAdapter {
		private Context mContext;

		public ImageAdapter(Context context) {
			mContext = context;
		}

		// 返回图像总数
		public int getCount() {
			return resIds.length;
		}

		public Object getItem(int position) {
			return position;
		}

		public long getItemId(int position) {
			return position;
		}

		// 返回具体位置的ImageView对象
		public View getView(int position, View convertView, ViewGroup parent) {
			ImageView imageView = new ImageView(mContext);
			// 设置当前图像的图像（position为当前图像列表的位置）
			imageView.setImageResource(resIds[position]);
			imageView.setScaleType(ImageView.ScaleType.FIT_XY);
			imageView.setLayoutParams(new LinearLayout.LayoutParams(WindowManager.LayoutParams.FILL_PARENT, WindowManager.LayoutParams.FILL_PARENT));
			return imageView;
		}
		
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.exit_menu, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch(item.getItemId()){
			case R.id.exit:
			{
				this.exit();
				return true;
			}
		}
		return false;
	}
	
	// 解析XML得到菜单
	private void getCatalogFromXML(){
		InputStream inputStream = null;
		try{
	        CatalogModel catalog = null;
	        
	        if (Constant.isAISHUA){
	        	inputStream = AssetsUtil.getInputStreamFromPhone("catalog_aishua.xml");
	        } else {
	        	inputStream = AssetsUtil.getInputStreamFromPhone("catalog.xml");
	        }
	        
	        XmlPullParser parser = Xml.newPullParser();  
	        parser.setInput(inputStream, "UTF-8");  
	          
	        int event = parser.getEventType();//产生第一个事件  
	        while(event!=XmlPullParser.END_DOCUMENT){  
	            switch(event){  
	            case XmlPullParser.START_TAG://判断当前事件是否是标签元素开始事件  
	                if("catalog".equalsIgnoreCase(parser.getName())){
	                    catalog = new CatalogModel();  
	                }  
	                if(catalog!=null){
	                    if("catalogId".equalsIgnoreCase(parser.getName())){ 
	                        catalog.setCatalogId(Integer.parseInt(parser.nextText()));  
	                    }else if("title".equalsIgnoreCase(parser.getName())){  
	                        catalog.setTitle(parser.nextText());  
	                    }else if("parentId".equalsIgnoreCase(parser.getName())){  
	                        catalog.setParentId(Integer.parseInt(parser.nextText()));  
	                    }else if("actionType".equalsIgnoreCase(parser.getName())){
	                    	catalog.setActionType(parser.nextText());
	                    }else if("actionId".equalsIgnoreCase(parser.getName())){  
	                        catalog.setActionId(parser.nextText());
	                    }else if("transferCode".equalsIgnoreCase(parser.getName())){  
	                        catalog.setTransferCode(parser.nextText());
	                    }else if("iconId".equalsIgnoreCase(parser.getName())){  
	                        catalog.setIconId((Integer.parseInt(parser.nextText())));  
	                    }else if("description".equalsIgnoreCase(parser.getName())){  
	                        catalog.setDescription(parser.nextText());  
	                    } else if("needReverse".equalsIgnoreCase(parser.getName())){
	                    	catalog.setNeedReverse(Boolean.parseBoolean(parser.nextText()));
	                    } else if("isActive".equals(parser.getName())){
	                    	catalog.setActive(Boolean.parseBoolean(parser.nextText()));
	                    }
	                }  
	                break;
	            case XmlPullParser.END_TAG:  
	                if("catalog".equalsIgnoreCase(parser.getName())){
	                	if (0 == catalog.getParentId()){
	                		parentCatalogList.add(catalog);
	                	} else{
	                		childCatalogList.add(catalog);
	                	}
	                }  
	                break;  
	            }  
	            event = parser.next();  
	        }
	        
	        
		}catch(IOException e){
			e.printStackTrace();
		} catch (XmlPullParserException e) {
			e.printStackTrace();
		} finally{
			try {
				if (null!= inputStream)
					inputStream.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
	}

}
