package com.bfb.pos.dynamic.template.os.view;

import java.util.List;

import android.util.Log;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;

import com.bfb.pos.activity.BaseActivity;
import com.bfb.pos.dynamic.component.ViewException;
import com.bfb.pos.dynamic.component.ViewRuntimeException;
import com.bfb.pos.dynamic.model.ViewGroupBean;
import com.bfb.pos.dynamic.template.Template;

public class GridViewTemplate extends Template implements IViewGroupTemplate{
	
	private String numColumns;
	private String columnWidth;
	private String verticalSpacing;
	private String horizontalSpacing;
	private String bgImg;

	public GridViewTemplate(String id, String name) {
		super(id, name);
	}
	
	@Override
	public View rewind(final List<ViewGroupBean> viewGroupBeans) throws ViewException {
		if (null == viewGroupBeans) {
			throw new ViewException("Params is null when excute rewind method,GridView template["+this.getId()+"]");
		}
		if (null == viewGroupBeans.get(0)) {
			throw new ViewException("Bean list is null when excute rewind method,GridView template["+this.getId()+"]");
		}
		GridView gridView = null;
		
		for (ViewGroupBean viewGroupBean:viewGroupBeans) {
			if (null == gridView) {
				gridView = new GridView(BaseActivity.getTopActivity());
				break;
			}
		}
		gridView.setLayoutParams(new GridView.LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT));
		if(null != columnWidth){
			gridView.setColumnWidth(this.getColumnWidth());
		}
		if(null != numColumns){
			gridView.setNumColumns(this.getNumColumns());
		}
		if(null != bgImg){
			gridView.setBackgroundResource(this.getBgImg());
		}
		if(null != verticalSpacing){
			gridView.setHorizontalSpacing(this.getVerticalSpacing());
		}
		if(null != horizontalSpacing){
			gridView.setHorizontalSpacing(this.getHorizontalSpacing());
		}
		
		AutoAdapter adapter = new AutoAdapter();
		adapter.setViewGroupBean(viewGroupBeans);
		gridView.setAdapter(adapter);
		gridView.setOnItemClickListener(new OnItemClickListener(){
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,long arg3) {
				ViewGroupBean viewBean = viewGroupBeans.get(arg2);
				if (null != viewBean.getEvent()) {
					try {
						viewBean.getEvent().trigger();
					} catch (Exception e) {
						throw new ViewRuntimeException(e.getMessage());
					} 
				}
			}
		});

		return gridView;
	}
	
	public Integer getBgImg() {
		if(null == bgImg){
			return null;
		}
		
		int resourceId = BaseActivity.getTopActivity().getResources().getIdentifier(bgImg, "drawable", BaseActivity.getTopActivity().getPackageName());
		return resourceId;
	}
	
	public Integer getNumColumns() {
		if(null == this.numColumns){
			return null;
		}
		
		try{
			if("AUTO_FIT".equals(numColumns)){
				return GridView.AUTO_FIT;
			}else{
				return Integer.parseInt(numColumns);
			}
		}catch(Exception e){
			Log.i("dynamic","NumColumns that in GridView must be is Integer.");
			return GridView.AUTO_FIT;
		}
	}

	public Integer getColumnWidth() {
		if(null == this.columnWidth){
			return null;
		}
		try{
			return Integer.parseInt(columnWidth);
		}catch(Exception e){
			Log.i("dyanmic", "ColumnWidth that in GridView must be is Integer.");
			return 90;
		}
	}
	
	public Integer getVerticalSpacing() {
		if(null == this.verticalSpacing){
			return null;
		}
		
		try{
			return Integer.parseInt(verticalSpacing);
		}catch(Exception e){
			Log.i("dyanmic", "VerticalSpacing that in GridView must be is Integer.");
			return 10;
		}
	}

	public Integer getHorizontalSpacing() {
		if(null == this.horizontalSpacing){
			return null;
		}
		try{
			return Integer.parseInt(horizontalSpacing);
		}catch(Exception e){
			Log.i("dyanmic", "HorizontalSpacing that in GridView must be is Integer.");
			return 10;
		}
	}

	public void setColumnWidth(String columnWidth) {
		this.columnWidth = columnWidth;
	}

	public void setNumColumns(String numColumns) {
		this.numColumns = numColumns;
	}
	
	public void setBgImg(String bgImg) {
		this.bgImg = bgImg;
	}
	
	public void setVerticalSpacing(String verticalSpacing) {
		this.verticalSpacing = verticalSpacing;
	}

	public void setHorizontalSpacing(String horizontalSpacing) {
		this.horizontalSpacing = horizontalSpacing;
	}
}
