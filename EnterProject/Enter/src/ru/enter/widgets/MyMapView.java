package ru.enter.widgets;

import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Field;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;

import com.google.android.maps.MapView;

public class MyMapView extends MapView{

	public MyMapView(Context context, String apiKey) {
		super(context, apiKey);
	}

	public MyMapView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	public MyMapView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}
	
	@Override
	public boolean onTouchEvent(MotionEvent ev) {
	    int action = ev.getAction();
	    switch (action) {
	    case MotionEvent.ACTION_DOWN:
	        // Disallow ScrollView to intercept touch events.
	        this.getParent().requestDisallowInterceptTouchEvent(true);
	        break;

	    case MotionEvent.ACTION_UP:
	        // Allow ScrollView to intercept touch events.
	        this.getParent().requestDisallowInterceptTouchEvent(false);
	        break;
	    }

	    // Handle MapView's touch events.
	    super.onTouchEvent(ev);
	    return true;
	}
	
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
