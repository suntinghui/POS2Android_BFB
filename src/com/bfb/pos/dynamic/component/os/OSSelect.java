package com.bfb.pos.dynamic.component.os;

import java.io.IOException;
import java.util.Vector;

import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import com.bfb.pos.dynamic.component.Component;
import com.bfb.pos.dynamic.component.ViewException;
import com.bfb.pos.dynamic.core.ViewPage;
import com.bfb.pos.dynamic.model.OptionEntity;
import com.bfb.pos.dynamic.model.SelectEntity;
import com.bfb.pos.R;

public class OSSelect extends StructComponent {
	public OSSelect(ViewPage viewPage) {
		super(viewPage);
	}

	private java.util.Vector<String> options;
	private String judge;
	
	public java.util.Vector<String> getOptions() {
		return options;
	}

	public void setOptions(java.util.Vector<String> options) {
		this.options = options;
	}
	
	public String getJudge(){
		return this.judge;
	}
	
	public void setJudge(String judge){
		this.judge = judge;
	}
	
	@Override
	
	public Spinner toOSComponent() throws ViewException {
		Spinner spinner = new Spinner(this.getContext());
		spinner.setTag(this.getId());
		
		final SelectEntity selectEntity = (SelectEntity) this.getValue();
		spinner.setPrompt(null == selectEntity.getPrompt()?"请选择":selectEntity.getPrompt());
		LayoutParams params = new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT);
		params.width = 200;
		spinner.setLayoutParams(params);
		spinner.setBackgroundResource(R.drawable.selectbg);
		
		if (null != selectEntity) {
			final Vector<String> vector = new Vector<String>();
			
			for (OptionEntity option:selectEntity.getOptions()) {
				vector.add(option.getValue(viewPage));
				// vector.add(option.getKey());
			}
			
			ArrayAdapter<String> array_adapter = new ArrayAdapter<String> (this.getContext(), R.layout.myspinner, vector);
			array_adapter.setDropDownViewResource ( android.R.layout.simple_spinner_dropdown_item );        
			spinner.setAdapter (array_adapter);
			spinner.setSelection(vector.indexOf(selectEntity.getSelected())); // 设置默认选中项
			
			spinner.setOnItemSelectedListener(new OnItemSelectedListener(){
				@Override
				public void onItemSelected(AdapterView<?> arg0, View arg1,int arg2, long arg3) {
					selectEntity.setSelected(selectEntity.getOptions().elementAt(arg2).getKey());
					
					try {
						OSSelect.this.trigger();
					} catch (ViewException e) {
						e.printStackTrace();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}

				@Override
				public void onNothingSelected(AdapterView<?> arg0) {
				}
				
			});
		}
		return spinner;
	}
	
	public boolean validator() {
		if (null != this.getJudge()){
			try{
				String[] judgeArray = this.getJudge().split("\\|");
				for (String str : judgeArray){
					String[] tempArray = str.split(":");
					if (tempArray.length != 2)
						break;
					
					if ("=".equals(tempArray[0].trim())){
						if (!(((SelectEntity)this.getViewPage().getPageValue(this.getId())).toString()).equals(((SelectEntity)this.getViewPage().getPageValue(tempArray[1])).toString())){
							Toast.makeText(this.getContext(), "两次选择的值应相等，请核对", Toast.LENGTH_SHORT).show();
							return false;
						}
					} else if("#".equals(tempArray[0].trim())){
 						if ((((SelectEntity)this.getViewPage().getPageValue(this.getId())).toString()).equals(((SelectEntity)this.getViewPage().getPageValue(tempArray[1])).toString())){
							Toast.makeText(this.getContext(), "两次选择的值应不同，请核对", Toast.LENGTH_SHORT).show();
							return false;
						}
					}
				}
				
			}catch(Exception e){
				e.printStackTrace();
			}
		}
		
		return true;
	}

	@SuppressWarnings("unchecked")
	@Override
	protected void copyParams(Component src, Component des) {
		super.copyParams(src, des);
		if (null != ((OSSelect)src).options) {
			((OSSelect)des).setOptions((Vector<String>) ((OSSelect)src).options.clone());
		}
	}
	
	@Override
	protected Component construction(ViewPage viewPage) {
		return new OSSelect(viewPage);
	}
}
