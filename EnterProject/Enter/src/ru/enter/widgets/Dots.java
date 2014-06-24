package ru.enter.widgets;

import ru.enter.R;
import android.content.Context;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

public class Dots extends LinearLayout{

	private static final int MAX_DOTS = 12;
	private Context context;
	private LinearLayout view;
	private int NUM;
	public Dots(Context context,int num) {
		super(context);
		this.context = context;
		
	}
	
	public Dots(Context context, AttributeSet attrs) {
		super(context, attrs);
		this.context = context;
	}
	public void initDots(int count,int countOnPage,int selected){
		removeAllViews();
//		initDots(Math.round(count/countOnPage), selected);
//		double d = Math.ceil(count / countOnPage);
		int d = (count + (countOnPage-1))/countOnPage;
		initDots(d, selected);
	}
	
	public void initDots(int num,int selected){
		NUM = num;
		
		LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		view = (LinearLayout) inflater.inflate(R.layout.dots, null);
		
		LayoutParams mainLayoutParams = new LayoutParams(LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT);
		mainLayoutParams.gravity = Gravity.CENTER;
		
		setPadding(0, 8, 0, 8);
		setLayoutParams(mainLayoutParams);
		
		LayoutParams dotLayoutParams = new LayoutParams(LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT);
		if(num>MAX_DOTS)
			num = MAX_DOTS;
		for(int i = 0;i<num;i++)
		{
			ImageView dot_img = new ImageView(context);
			dot_img.setLayoutParams(dotLayoutParams);
			dot_img.setPadding(5, 0, 5, 0);
			
			if(i == selected)
				dot_img.setImageResource(R.drawable.dot);
			else
				dot_img.setImageResource(R.drawable.dot_);
			dot_img.setTag(i);
			
			if(i == 0 || i == num - 1){
				dot_img.setVisibility(View.GONE);
			}
			
			view.addView(dot_img);
		}
		
		
		addView(view);
	}

	public void setSelected(int selected){
		int summ = view.getChildCount();
		if(NUM > MAX_DOTS){
			if(selected < NUM-MAX_DOTS/2 && selected > MAX_DOTS/2-1)
				selected = MAX_DOTS/2;
			if(selected >= NUM-MAX_DOTS/2)
				selected = MAX_DOTS -(NUM-selected);
		}
		for(int i = 0;i<summ;i++)
		{
			ImageView dot_img = (ImageView)findViewWithTag(i);
			if(i == selected)
				dot_img.setImageResource(R.drawable.dot);
			else
				dot_img.setImageResource(R.drawable.dot_);
		}
	}
}
