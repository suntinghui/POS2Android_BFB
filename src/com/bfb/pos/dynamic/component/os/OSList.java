package com.bfb.pos.dynamic.component.os;

import java.util.Vector;

import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.bfb.pos.dynamic.component.Component;
import com.bfb.pos.dynamic.component.ViewException;
import com.bfb.pos.dynamic.core.ViewPage;
import com.bfb.pos.dynamic.model.OptionEntity;
import com.bfb.pos.dynamic.model.SelectEntity;


public class OSList extends StructComponent {
	public OSList(ViewPage viewPage) {
		super(viewPage);
	}

	private java.util.Vector<String> options;
	
	public java.util.Vector<String> getOptions() {
		return options;
	}

	public void setOptions(java.util.Vector<String> options) {
		this.options = options;
	}
	@Override
	
	public ListView toOSComponent() throws ViewException {
		ListView list = new ListView(this.getContext());
		list.setTag(this.getId());
		
		final SelectEntity selectEntity = (SelectEntity) this.getValue();
		if (null != selectEntity) {
			final Vector<String> vector = new Vector<String>();
			
			for (OptionEntity option:selectEntity.getOptions()) {
				vector.add(option.getValue(viewPage));
			}
			
			ArrayAdapter<String> array_adapter = new ArrayAdapter<String> (this.getContext(), android.R.layout.simple_list_item_1, vector);
			list.setAdapter (array_adapter);
			list.setSelection(vector.indexOf(selectEntity.getSelected()));
			
			list.setOnItemSelectedListener(new OnItemSelectedListener(){
				@Override
				public void onItemSelected(AdapterView<?> arg0, View arg1,int arg2, long arg3) {
					selectEntity.setSelected(vector.elementAt(arg2));
				}

				@Override
				public void onNothingSelected(AdapterView<?> arg0) {
				}
				
			});
		}
		return list;
	}
	@Override
	protected void copyParams(Component src, Component des) {
		super.copyParams(src, des);
		if (null != ((OSList)src).options) {
			((OSList)des).setOptions((Vector<String>) ((OSList)src).options.clone());
		}
	}

	@Override
	protected Component construction(ViewPage viewPage) {
		return new OSList(viewPage);
	}
}
