//package ru.enter.widgets;
//
//import ru.enter.R;
//import ru.enter.utils.Converters;
//import android.content.Context;
//import android.util.AttributeSet;
//import android.view.View;
//import android.widget.ImageView;
//import android.widget.LinearLayout;
//
//public class Dots extends LinearLayout{
//
//	private static final int MAX_DOTS = 7;
//	private int count;
//	private int pages;
//	
//	public Dots(Context context) {
//		super(context);
//		init();
//	}
//	
//	public Dots(Context context, AttributeSet attrs) {
//		super(context, attrs);
//		init();
//	}
//	
//	private void init(){
//		int padding = Converters.dp2px(getContext(), 10);
//		setPadding(padding, padding, padding, padding);		
//	}
//	
//	public void initDots(int pages, int selected){
//		this.pages = pages;
//		count = pages > MAX_DOTS ? MAX_DOTS : pages;
//		
//		removeAllViews();
//		
//		LayoutParams dotLayoutParams = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
//		for(int i = 0; i < count; i++){
//			ImageView dot_img = new ImageView(getContext());
//			dot_img.setLayoutParams(dotLayoutParams);
//			dot_img.setPadding(5, 0, 5, 0);
//			
//			if(i == selected)
//				dot_img.setImageResource(R.drawable.dot_on);
//			else
//				dot_img.setImageResource(R.drawable.dot_off);
//			
//			if(i == 0 || i == count - 1){
//				dot_img.setVisibility(View.GONE);
//			}
//			
//			dot_img.setTag(i);
//			addView(dot_img);
//		}
//	}
//
//	public void setSelected(int selected){
//		if(pages > MAX_DOTS){
//			int halfDots = MAX_DOTS / 2;
//			int toCenterDot = pages - halfDots * 2;
//			
//			if(selected >= halfDots && selected < halfDots + toCenterDot)
//				selected = halfDots;
//			
//			else if(selected >= halfDots + toCenterDot)
//				selected = MAX_DOTS - (pages - selected);
//		}
//		
//		for(int i = 0; i < count; i++){
//			ImageView dot_img = (ImageView)findViewWithTag(i);
////			if(i == selected)
////				dot_img.setImageResource(R.drawable.dot);//TODO
////			else
////				dot_img.setImageResource(R.drawable.dot_);//TODO
//		}
//	}
//}
