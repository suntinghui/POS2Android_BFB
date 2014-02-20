package com.bfb.pos.activity.view;

import java.util.ArrayList;
import java.util.List;

import com.bfb.pos.R;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TableLayout;
import android.widget.TextView;
import android.widget.Toast;

public class PaginationView extends TableLayout implements OnClickListener {
	
	private Context context;
	private int pageSize = 1; // 每页条数
	private int totalCount = 0; // 总数据条数
	private int pageIndex = 1; // 当前页码
	List<OnPageChangedListener> eventListeners = null;
	
	private ImageView firstPage = null;
	private ImageView previousPage = null;
	private ImageView nextPage = null;
	private ImageView lastPage = null;
	private TextView pageInfo = null;

	public PaginationView(Context context) {
		super(context);
		this.context = context;
		initializeComponent();
	}

	public PaginationView(Context context, AttributeSet attrs) {
		super(context, attrs);
		this.context = context;
		initializeComponent();
	}
	
	private void initializeComponent(){
		String infService = Context.LAYOUT_INFLATER_SERVICE;
		LayoutInflater li = (LayoutInflater)getContext().getSystemService(infService);
		li.inflate(R.layout.paginationview, this);
		
		firstPage = (ImageView) this.findViewById(R.id.firstPage);
		previousPage = (ImageView) this.findViewById(R.id.previousPage);
		nextPage = (ImageView) this.findViewById(R.id.nextPage);
		lastPage = (ImageView) this.findViewById(R.id.lastPage);
		pageInfo = (TextView) this.findViewById(R.id.pageInfo);
		
		firstPage.setOnClickListener(this);
		previousPage.setOnClickListener(this);
		nextPage.setOnClickListener(this);
		lastPage.setOnClickListener(this);
		
		this.refreshComponent();
	}
	
	public void setOnPageChangedListener(OnPageChangedListener listener) {
		if (this.eventListeners == null)
			this.eventListeners = new ArrayList<OnPageChangedListener>();
		eventListeners.add(listener);
	}
	
	private void refreshComponent() {
		// TODO: 确保在UI线程中被调用
		pageInfo.setText("第"+pageIndex + "/" + getTotalPages()+"页");
		
		/**
		 * 此段代码有问题。而且效果并不好。图片在enabled=false的时候分辨不出来。
		 if(pageIndex == 1){
			firstPage.setEnabled(false);
			previousPage.setEnabled(false);
			nextPage.setEnabled(true);
			lastPage.setEnabled(true);
		}
		if(pageIndex == this.getTotalPages()){
			if(this.getTotalPages()==1){
				firstPage.setEnabled(false);
				previousPage.setEnabled(false);
				nextPage.setEnabled(false);
				lastPage.setEnabled(false);
			}else{
				firstPage.setEnabled(true);
				previousPage.setEnabled(true);
				nextPage.setEnabled(false);
				lastPage.setEnabled(false);
			}
		}
		 */
		

		// TODO: set enable/disable states of prev/next buttons
	}
	
	public void onClick(View view) {
		switch(view.getId()){
		case R.id.firstPage:
			if(pageIndex == 1){
				Toast.makeText(context,"已经是第一页",Toast.LENGTH_SHORT).show();
			}else{
				pageIndex = 1;
				firePageChangedEvent();
			}
			break;
			
		case R.id.previousPage:
			if(pageIndex == 1){
				Toast.makeText(context,"已经是第一页",Toast.LENGTH_SHORT).show();
			}else{
				pageIndex--;
				firePageChangedEvent();
			}
			
			break;
			
		case R.id.nextPage:
			if(pageIndex == getTotalPages()){
				Toast.makeText(context,"已经是最后一页",Toast.LENGTH_SHORT).show();
			}else{
				pageIndex++;
				firePageChangedEvent();
			}
			
			break;
			
		case R.id.lastPage:
			if(pageIndex == getTotalPages()){
				Toast.makeText(context,"已经是最后一页",Toast.LENGTH_SHORT).show();
			}else{
				pageIndex = getTotalPages();
				firePageChangedEvent();
			}
			break;
		}
		
	}
	
	public int getPageIndex() {
		return pageIndex;
	}

	public void setPageIndex(int pageIndex) {
		this.pageIndex = pageIndex;
		refreshComponent();
	}

	public int getPageSize() {
		return pageSize;
	}

	public void setPageSize(int pageSize) {
		this.pageSize = pageSize;
		refreshComponent();
	}

	public int getTotalCount() {
		return totalCount;
	}

	public void setTotalCount(int totalCount) {
		this.totalCount = totalCount;
		refreshComponent();
	}

	public int getTotalPages() {
		return (totalCount + (pageSize - 1)) / pageSize;
	}
	
	private void firePageChangedEvent() {
		if (this.eventListeners != null) {
			for (OnPageChangedListener l : this.eventListeners) {
				if (l != null)
					l.onClick(this, this.pageIndex);
				
				refreshComponent();
			}
		}
	}
	
	public interface OnPageChangedListener {
		public void onClick(View v, int pageIndex);
	}
	
}
