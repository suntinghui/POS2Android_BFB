package com.bfb.pos.activity.view;

import java.util.Calendar;

import com.bfb.pos.agent.client.AppDataCenter;
import com.bfb.pos.util.DateUtil;
import com.bfb.pos.R;

import android.app.DatePickerDialog;
import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.DatePicker;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.view.View.OnClickListener;

public class PickerDateView extends LinearLayout implements OnClickListener {
	
	// startDate
	private int sYear;
    private int sMonth;
    private int sDay;
    
    // endDate
    private int eYear;
    private int eMonth;
    private int eDay;
    
	private Context context;
	
	private TextView startDateEdit = null;
	private TextView endDateEdit = null;
	private LinearLayout startLayout = null;
	private LinearLayout endLayout = null;
	
	private Calendar c = Calendar.getInstance();
	
	public PickerDateView(Context context) {
		super(context);
		this.context = context;
		initializeComponent();
	}
	
	/**
	 * 注意绝对不对缺少此构造器，否则会报android.view.InflateException: Binary XML file line #异常。
	 * @param context
	 * @param attrs
	 */
	public PickerDateView(Context context, AttributeSet attrs) {
		super(context, attrs);
		this.context = context;
		initializeComponent();
	}
	
	private void initializeComponent(){
		// Inflate the view from the layout resource.
		String infService = Context.LAYOUT_INFLATER_SERVICE;
		LayoutInflater li = (LayoutInflater)getContext().getSystemService(infService);
		/**
		 * attachToRoot-- true,以代码形式嵌入，false,以XML形式嵌入 ？？？
		 * 
		 * Whether the inflated hierarchy should be attached to the root parameter? 
		 * If false, root is only used to create the correct 
		 * subclass of LayoutParams for the root view in the XML.
		 * 
		 * 以XML形式取不到下面的属性值，NULLPointerException ？？？
		 */
		li.inflate(R.layout.select_date, this);
		
		// Get references to the child controls.
		// 以XML形式取不到这些值，NULLPointerException
		startDateEdit = (TextView) this.findViewById(R.id.startdate_text);
		endDateEdit = (TextView) this.findViewById(R.id.enddate_text);
		startLayout = (LinearLayout)this.findViewById(R.id.startLayout);
		endLayout = (LinearLayout)this.findViewById(R.id.endLayout);
		
		
		c.setTime(DateUtil.string2Date(AppDataCenter.getValue("__YYYYMMDD")));
		
        sYear = c.get(Calendar.YEAR);
        sMonth = c.get(Calendar.MONTH);
        sDay = 1;
        eYear = c.get(Calendar.YEAR);
        eMonth = c.get(Calendar.MONTH);
        eDay = c.get(Calendar.DAY_OF_MONTH);
        
        // 为EditText设置初始值，起始日期默认为第一天，截止日期默认为当天
        startDateEdit.setText(getDateString(sYear,sMonth,sDay));
        endDateEdit.setText(getDateString(eYear,eMonth,eDay));
		
		startLayout.setOnClickListener(this);
		endLayout.setOnClickListener(this);
	}
	
	private String getDateString(int year,int month,int day){
		// Month is 0 based so add 1
		StringBuffer sb = new StringBuffer();
		sb.append(year).append("年").append(month+1).append("月").append(day).append("日");
		return sb.toString();
	}
	
	public String getStartDate() {
		return new StringBuilder().append(sYear).append("-").append(String.format("%02d", sMonth + 1))
				.append("-").append(String.format("%02d", sDay)).toString();
	}
	
	public String getEndDate(){
		return new StringBuilder().append(eYear).append("-").append(String.format("%02d", eMonth + 1))
		.append("-").append(String.format("%02d", eDay)).toString();
	}
	
	public String getToday(){
		return new StringBuilder().append(c.get(Calendar.YEAR)).append("-").append(c.get(Calendar.MONTH) + 1)
		.append("-").append(c.get(Calendar.DAY_OF_MONTH)).toString();
	}
	
	private void showPickStartDateDialog(){
		new DatePickerDialog(context,startDateSetListener,sYear,sMonth,sDay).show();
	}
	
	private void showPickEndDateDialog(){
		new DatePickerDialog(context,endDateSetListener,eYear,eMonth,eDay).show();
	}
	
	 private DatePickerDialog.OnDateSetListener startDateSetListener = new DatePickerDialog.OnDateSetListener() {

		public void onDateSet(DatePicker view, int year, int monthOfYear,int dayOfMonth) {
			sYear = year;
			sMonth = monthOfYear;
			sDay = dayOfMonth;
			startDateEdit.setText(getDateString(year,monthOfYear,dayOfMonth));
		}
	};

	private DatePickerDialog.OnDateSetListener endDateSetListener = new DatePickerDialog.OnDateSetListener() {

		public void onDateSet(DatePicker view, int year, int monthOfYear,int dayOfMonth) {
			eYear = year;
			eMonth = monthOfYear;
			eDay = dayOfMonth;
			endDateEdit.setText(getDateString(year,monthOfYear,dayOfMonth));
		}
	};

	@Override
	public void onClick(View v) {
		switch(v.getId()){
		case R.id.startLayout:
			showPickStartDateDialog();
			break;
			
		case R.id.endLayout:
			showPickEndDateDialog();
			break;
		}
		
	}

}
