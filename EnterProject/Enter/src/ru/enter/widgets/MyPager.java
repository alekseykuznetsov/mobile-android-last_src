package ru.enter.widgets;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.ViewConfiguration;

public class MyPager extends ViewPager{

	private float mFromX;
	private float mFromY;
	private int mMoveSlop;

	public MyPager(Context context, AttributeSet attrs) {
		super(context, attrs);
		ViewConfiguration vc = ViewConfiguration.get(context);
	    mMoveSlop = vc.getScaledTouchSlop();
	}

	public MyPager(Context context) {
		this(context, null);
	}
	
	@Override
	public boolean onInterceptTouchEvent(MotionEvent event) {
		int action = event.getAction();

	      if (action == MotionEvent.ACTION_DOWN) {
	    	  mFromX = event.getX();
	    	  mFromY = event.getY();
	      } else if (action == MotionEvent.ACTION_MOVE) {
	    	  float mToX = event.getX();
	      	  float mToY = event.getY();
	    	  if (isMoving(mToX, mToY)){
	    		  DragAndDropView.setMoving();
	    	  }
	    		  
	      }
		return super.onInterceptTouchEvent(event);
	}
	
	
	private boolean isMoving(float toX, float toY){
    	float deltaX = Math.abs(mFromX - toX);
    	float deltaY = Math.abs(mFromY - toY);
    	return (deltaX > mMoveSlop) || (deltaY > mMoveSlop);
    }

}
