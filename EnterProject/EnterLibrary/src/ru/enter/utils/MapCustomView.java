package ru.enter.utils;

import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Field;

import android.content.Context;

import com.google.android.maps.MapView;

public class MapCustomView  extends MapView{

	public MapCustomView(Context context, String apiKey) {
		super(context, apiKey);
		// TODO Auto-generated constructor stub
	}
	
	
	//удалить за ненадобностью?
	public void cleanUpMemory(){
	    try {
	        Field fMapInView = MapView.class.getDeclaredField("mMap");
	        AccessibleObject.setAccessible(new AccessibleObject[]{fMapInView}, true);
	        fMapInView.set(this, null);
	    } catch (Exception e) {
	        e.printStackTrace();
	    }

	    try {
	        Field fConverterInView = MapView.class.getDeclaredField("mConverter");
	        AccessibleObject.setAccessible(new AccessibleObject[]{fConverterInView}, true);
	        fConverterInView.set(this, null);
	    } catch (Exception e) {
	        e.printStackTrace();
	    }

	    try {
	        Field fControllerInView = MapView.class.getDeclaredField("mController");
	        AccessibleObject.setAccessible(new AccessibleObject[]{fControllerInView}, true);
	        fControllerInView.set(this, null);
	    } catch (Exception e) {
	        e.printStackTrace();
	    }

	    try {
	        Field fZoomHelperInView = MapView.class.getDeclaredField("mZoomHelper");
	        AccessibleObject.setAccessible(new AccessibleObject[]{fZoomHelperInView}, true);
	        fZoomHelperInView.set(this, null);
	    } catch (Exception e) {
	        e.printStackTrace();
	    }
	}


}
