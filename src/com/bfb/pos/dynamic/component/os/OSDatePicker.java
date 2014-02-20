package com.bfb.pos.dynamic.component.os;

import java.sql.Date;
import java.util.regex.Pattern;

import android.util.Log;
import android.widget.Toast;

import com.bfb.pos.activity.view.PickerDateView;
import com.bfb.pos.dynamic.component.Component;
import com.bfb.pos.dynamic.component.Input;
import com.bfb.pos.dynamic.component.ViewException;
import com.bfb.pos.dynamic.core.ViewPage;
import com.bfb.pos.util.DateUtil;

public class OSDatePicker extends Input {
	
	private PickerDateView pickerDate = null;
	
	private String interval = "30"; // 设置开始日期和结束日期之间相差的天数。默认为30天。

	public OSDatePicker(ViewPage viewPage) {
		super(viewPage);
	}
	
	@Override
	public PickerDateView toOSComponent() throws ViewException {
		pickerDate = new PickerDateView(this.getContext());
		
		return pickerDate;
	}
	
	public void setInterval(String interval){
		if (null == interval || "".equals(interval))
			return;
		
		// 判断是否为数字
		Pattern pattern = Pattern.compile("[0-9]*");
		if (!pattern.matcher(interval).matches()){
			Log.e("datePicker", "interval must be number!");
			return;
		}
		
		this.interval = interval;
	}
	
	public int getInterval(){
		return Integer.parseInt(this.interval);
	}


	// 注意这一种特殊情况，并不采用datePicker的id,而是加了两个新的id。分别是startDate和endDate。日期格式是yyyyMMdd
	@Override
	public void loadInputValue() {
		this.getViewPage().addAPageValue("startDate", pickerDate.getStartDate());
		this.getViewPage().addAPageValue("endDate", pickerDate.getEndDate());
	}

	@Override
	public boolean validator() {
		// 注意这里采用的是java.sql.Date。
		Date startDate = Date.valueOf(pickerDate.getStartDate()); 
		Date endDate = Date.valueOf(pickerDate.getEndDate());
		
		// 检查结束日期是否大于开始日期
		if (startDate.compareTo(endDate) > 0){
			Toast.makeText(this.getContext(), "开始日期不能大于结束日期", Toast.LENGTH_SHORT).show();
			return false;
		}
		
		// 检查结束日期与开始日期之间的差距不能大于设置的时间间隔
		if (DateUtil.daysBetween(startDate, endDate) > this.getInterval()){
			Toast.makeText(this.getContext(), "开始日期和结束日期相差不能超过"+ this.interval + "天", Toast.LENGTH_SHORT).show();
			return false;
		}
		
		return true;
	}
	
	@Override
	protected Component construction(ViewPage viewPage) {
		return new OSDatePicker(viewPage);
	}

}
