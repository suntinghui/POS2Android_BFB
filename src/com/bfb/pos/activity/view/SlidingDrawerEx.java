package com.bfb.pos.activity.view;

import com.bfb.pos.agent.client.ApplicationEnvironment;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.SlidingDrawer;

// 继承此类是为了实现让Handle（Button） 靠右
public class SlidingDrawerEx extends SlidingDrawer {
	
	private int mHandleMarginRight = 0;

    public int getmHandleMarginRight() {
        return mHandleMarginRight;
    }

    public void setmHandleMarginRight(int mHandleMarginRight) {
        this.mHandleMarginRight = mHandleMarginRight;
    }
   

    public SlidingDrawerEx(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
            super.onLayout(changed, l, t, r, b);
            final int height = b - t;

            final View handle = this.getHandle();

            int childWidth = handle.getMeasuredWidth();
            int childHeight = handle.getMeasuredHeight();

            int childRight;
            int childTop;

            final View content = this.getContent();

            childRight = r + mHandleMarginRight;
            childTop = this.isOpened() ? this.getPaddingTop() : height
                            - childHeight + this.getPaddingBottom();

            content.layout(0, this.getPaddingTop() + childHeight,
                            content.getMeasuredWidth(), this.getPaddingTop() + childHeight
                                            + content.getMeasuredHeight());

            handle.layout(ApplicationEnvironment.getInstance().screenWidth-childWidth, childTop, childRight, childTop
                            + childHeight);
    }

}
