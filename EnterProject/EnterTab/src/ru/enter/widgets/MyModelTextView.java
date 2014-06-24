package ru.enter.widgets;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.TextView;

public class MyModelTextView extends TextView {
	
	public MyModelTextView(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
	}

	public MyModelTextView(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
	}
	
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		// TODO Auto-generated method stub
		return false;
	}
	
	
	

}
