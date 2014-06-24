package ru.enter.route;

import java.util.ArrayList;

import android.graphics.drawable.Drawable;

import com.google.android.maps.ItemizedOverlay;
import com.google.android.maps.OverlayItem;

public class OverlayWithMarkers extends ItemizedOverlay<OverlayItem>{
	private ArrayList<OverlayItem> mOverlays = new ArrayList<OverlayItem>();
	
	public OverlayWithMarkers(Drawable userPositionMarker) {
		//наш маркер делаем отцентрированным
		super(boundCenterBottom(userPositionMarker));
	}
	
	
	public void addOverlay(OverlayItem overlay) {
	    mOverlays.add(overlay);
	    //вызывает createItem()
	    populate();
	}

	@Override
	protected OverlayItem createItem(int i) {
		return mOverlays.get(i);
	}

	@Override
	public int size() {
		return mOverlays.size();
	}
	
}
