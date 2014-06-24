package ru.enter.maps;

import ru.enter.R;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.RelativeLayout.LayoutParams;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapView;

/**
 * Helper class for showing shop info.
 */
public class ShopInfoPopup {
	Context context;
	LayoutInflater inflater;
	MapView mapView;
	View popup;
	GeoPoint gp;
	Animation mAnimation;
	boolean isVisible = false;
	int BOTTOM_OFFSET = 50;

	public ShopInfoPopup (Context ctx,MapView mView, int layout) {
		context = ctx;
		inflater = LayoutInflater.from(context);
		mapView = mView;
		ViewGroup parent = (ViewGroup) mapView.getParent();
		mAnimation = AnimationUtils.loadAnimation(context, R.anim.popup_animation);
		popup = inflater.inflate(layout, parent, false);
		//popup.setAnimation(mAnimation);
		popup.setOnClickListener(new View.OnClickListener() {
			public void onClick (View v) {
				hide();
			}
		});
	}

	public View getPopupForPoint (GeoPoint gp) {
		this.gp = gp;
		return popup;
	}

	public void show () {
		hide();
	    MapView.LayoutParams params = new MapView.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT,gp, MapView.LayoutParams.BOTTOM_CENTER);
	    params.y-=BOTTOM_OFFSET; //show view above the marker
	    mapView.addView(popup, params);
	}

	public void hide () {
		mapView.removeView(popup);
	}
}