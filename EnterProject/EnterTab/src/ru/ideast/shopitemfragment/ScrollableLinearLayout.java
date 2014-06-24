package ru.ideast.shopitemfragment;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.LinearLayout;

public class ScrollableLinearLayout extends LinearLayout {
	private float xDistance, yDistance, lastX, lastY;
	
	public interface onVerticalScrollListener{
		void onVercticalScrollUp();
		void onVercticalScrollDown();
	}
	
	private onVerticalScrollListener mListener;

	public void setOnVerticalScrollListener(onVerticalScrollListener mListener) {
		this.mListener = mListener;
	}

	public ScrollableLinearLayout(final Context ctxt, AttributeSet attrs) {
		super(ctxt, attrs);
	}

	@Override
	public boolean onInterceptTouchEvent(MotionEvent ev) {
		switch (ev.getAction()) {
		case MotionEvent.ACTION_DOWN:
			xDistance = yDistance = 0f;
			lastX = ev.getX();
			lastY = ev.getY();
			break;
		case MotionEvent.ACTION_MOVE:
			final float curX = ev.getX();
			final float curY = ev.getY();
			xDistance += curX - lastX;
			yDistance += curY - lastY;
			lastX = curX;
			lastY = curY;
			if (Math.abs(yDistance) > Math.abs(xDistance)){
				if(yDistance > 0){
					Log.i("ScrollableLinearLayout", "More then 0");
//					if(mListener!=null)
//						mListener.onVercticalScroll();
				}
				
				if(yDistance < 0){
					Log.i("ScrollableLinearLayout", "Less then 0");
				}
				
				
				
			}
		}

		return super.onInterceptTouchEvent(ev);
	}


}
