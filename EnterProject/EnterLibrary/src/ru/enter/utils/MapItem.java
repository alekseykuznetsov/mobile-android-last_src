package ru.enter.utils;

import java.util.ArrayList;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;

import com.google.android.maps.ItemizedOverlay;
import com.google.android.maps.OverlayItem;

public class MapItem extends ItemizedOverlay {

	private ArrayList<CustomMapOverlay> mOverlays = new ArrayList<CustomMapOverlay>();
	private Context mContext;
	private static OnBalloonTap monBalloopTap;

	public MapItem(Drawable defaultMarker) {
		super(boundCenterBottom(defaultMarker));
	}

	public MapItem(Drawable defaultMarker, Context context) {
		super(boundCenterBottom(defaultMarker));
		mContext = context;
	}

	@Override
	protected OverlayItem createItem(int i) {
		return mOverlays.get(i);
	}

	@Override
	public int size() {
		return mOverlays.size();
	}

	public void addOverlay(CustomMapOverlay overlay) {
		mOverlays.add(overlay);
		populate();
	}

	@Override
	protected boolean onTap(int index) {
		CustomMapOverlay item = mOverlays.get(index);
		if(item.isForward())
			if(!item.getTitle().equals(""))
			{
				monBalloopTap.showOnBalloonTap(item.getContext(),
						item.getTitle(),
						String.valueOf(item.getPoint().getLatitudeE6() / 1E6),
						String.valueOf(item.getPoint().getLongitudeE6() / 1E6));
				/*Intent intent = new Intent().setClass(item.getContext(), ShopCard.class);
				intent.putExtra(Constants.ShopCard_SHOP_ID, item.getTitle());
				intent.putExtra(Constants.Maps_LATITUDE, String.valueOf(item.getPoint().getLatitudeE6()/1E6));
				intent.putExtra(Constants.Maps_LONGITUDE, String.valueOf(item.getPoint().getLongitudeE6()/1E6));
				item.getContext().startActivity(intent);*/
			}
		return true;
	}
	
	public static void setOnBalloopTap(OnBalloonTap onBalloopTap) {
		monBalloopTap = onBalloopTap;
	}

}
