package ru.enter.widgets;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.Button;

public class TabButton extends Button {
	public TabButton(Context context) {
		super(context);
		setTextSize(12);
		setTypeface(Typeface.DEFAULT_BOLD);
		setTextColor(Color.WHITE);
		setBackgroundColor(Color.rgb(147, 147, 147));
	}
	
	public TabButton(Context context, AttributeSet attrs) {
		super(context, attrs);
		setTextSize(12);
		setTypeface(Typeface.DEFAULT_BOLD);
		setTextColor(Color.WHITE);
		setBackgroundColor(Color.rgb(147, 147, 147));
	}
	
	public TabButton(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		setTextSize(12);
		setTypeface(Typeface.DEFAULT_BOLD);
		setTextColor(Color.WHITE);
		setBackgroundColor(Color.rgb(147, 147, 147));
	}
	
	@Override
	public void setSelected(boolean selected) {
		super.setSelected(selected);
		if(selected){
			this.setTextColor(Color.BLACK);
			this.setBackgroundColor(Color.WHITE);
		}else{
			this.setTextColor(Color.WHITE);
			this.setBackgroundColor(Color.rgb(147, 147, 147));
		}
	}

}
