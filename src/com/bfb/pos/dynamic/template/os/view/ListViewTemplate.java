package com.bfb.pos.dynamic.template.os.view;

import java.util.List;

import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

import com.bfb.pos.activity.BaseActivity;
import com.bfb.pos.dynamic.component.ViewException;
import com.bfb.pos.dynamic.component.ViewRuntimeException;
import com.bfb.pos.dynamic.model.ViewGroupBean;
import com.bfb.pos.dynamic.template.Template;

public class ListViewTemplate extends Template implements IViewGroupTemplate{
	
	private String bgImg;

	public ListViewTemplate(String id, String name) {
		super(id, name);
	}
	
	@Override
	public View rewind(final List<ViewGroupBean> viewGroupBeans) throws ViewException {
		if (null == viewGroupBeans) {
			throw new ViewException("Params is null when excute rewind method,ListView template["+this.getId()+"]");
		}
		if (null == viewGroupBeans.get(0)) {
			throw new ViewException("Bean list is null when excute rewind method,ListView template["+this.getId()+"]");
		}
		ListView listView = null;
		for (ViewGroupBean viewGroupBean:viewGroupBeans) {
			if (null == listView) {
				listView = new ListView(BaseActivity.getTopActivity());
				break;
			}
		}
		listView.setLayoutParams(new ListView.LayoutParams(ListView.LayoutParams.FILL_PARENT, ListView.LayoutParams.WRAP_CONTENT));
		listView.setCacheColorHint(0);
		
		if(null != bgImg){
			listView.setBackgroundResource(this.getBgImg());
		}
		
		AutoAdapter adapter = new AutoAdapter();
		adapter.setViewGroupBean(viewGroupBeans);
		
		
		listView.setAdapter(adapter);
		listView.setOnItemClickListener(new OnItemClickListener(){
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

		return listView;
	}
	
	public Integer getBgImg() {
		if(null == bgImg){
			return null;
		}
		
		int resourceId = BaseActivity.getTopActivity().getResources().getIdentifier(bgImg, "drawable", BaseActivity.getTopActivity().getPackageName());
		return resourceId;
	}
	
	
	public void setBgImg(String bgImg) {
		this.bgImg = bgImg;
	}
	
}
