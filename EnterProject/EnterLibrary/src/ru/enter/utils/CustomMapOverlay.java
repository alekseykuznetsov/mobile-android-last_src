package ru.enter.utils;

import android.content.Context;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.OverlayItem;

public class CustomMapOverlay extends OverlayItem{
	
	private Context context;
	private boolean forward;
	private int mId;
	
	public CustomMapOverlay(GeoPoint point, String title, int id,Context context, boolean forward) {
		super(point, title, "");
		this.context = context;
		mId = id;
		this.setForward(forward);
	}
	public Context getContext() {
		return context;
	}
	public void setContext(Context context) {
		this.context = context;
	}
	public boolean isForward() {
		return forward;
	}
	public void setForward(boolean forward) {
		this.forward = forward;
	}
	
	public int getId(){
		return mId;
	}
	

}
