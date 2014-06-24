package ru.enter.utils;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.view.Display;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.FrameLayout;
import android.widget.FrameLayout.LayoutParams;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.Toast;

public class DragAndDrop {

	private static boolean dragging = false;
	private static ImageView drag_img;
	private static LayoutParams params;
	private static OnTouchListener gridTouchListener;
	private static OnItemLongClickListener gridLongListener;
	
	public static void create(final FrameLayout frameForDrag, final GridView grid)
	{
		final Context context = frameForDrag.getContext();
		Display display = ((Activity)context).getWindowManager().getDefaultDisplay();
		final int width = display.getWidth();
		final int height = display.getHeight();
		
	    params = new LayoutParams(LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT);
	    
	    final FrameLayout absolute = new FrameLayout(context);
	    
		final LayoutParams par = new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT);
		absolute.setLayoutParams(par);
		
		absolute.setBackgroundColor(Color.parseColor("#90000000"));

		
		gridTouchListener = new OnTouchListener() {
			
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				if(drag_img!=null)
				{
					switch (event.getAction()) {
					case MotionEvent.ACTION_DOWN:
						
						break;
					case MotionEvent.ACTION_MOVE:
						if(dragging)
						{
							int x = (int) event.getRawX();
							int y = (int) event.getRawY();
							drag_img.setPadding(x-width/3, y-height/4, 0, 0);
							drag_img.invalidate();
						}
						break;
					case MotionEvent.ACTION_UP:
						dragging = false;
						int x = (int) event.getRawX();
						int y = (int) event.getRawY();
						if(x>width*4/5 && y>height*4/5){
							Toast.makeText(context, "Success", Toast.LENGTH_SHORT).show();
						}
						frameForDrag.removeView(drag_img);
						absolute.removeView(drag_img);
						frameForDrag.removeView(absolute);
						drag_img = null;
						break;
					default:
						break;
					}
				}
				return dragging;
			}
		};
		gridLongListener = new OnItemLongClickListener() {

			@Override
			public boolean onItemLongClick(AdapterView<?> arg0, View arg1,
					int arg2, long arg3) {
				arg1.setDrawingCacheEnabled(true);
				grid.setEnabled(false);
				dragging = true;
				drag_img = new ImageView(context);
				drag_img.setDrawingCacheEnabled(true);
				drag_img.setImageBitmap(arg1.getDrawingCache());
				
				absolute.addView(drag_img,params);
				
				frameForDrag.addView(absolute);
				return false;
			}
		};
		
		grid.setOnItemLongClickListener(gridLongListener);
		grid.setOnTouchListener(gridTouchListener);
		
	}
}
