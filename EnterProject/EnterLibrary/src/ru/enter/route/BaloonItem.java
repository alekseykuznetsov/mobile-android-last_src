package ru.enter.route;

import java.util.ArrayList;

import ru.enter.DataManagement.BasketData;
import ru.enter.beans.CheckoutBean;
import ru.enter.utils.CustomMapOverlay;
import ru.enter.utils.OnBalloonTap;
import android.app.Activity;
import android.graphics.drawable.Drawable;

import com.google.android.maps.MapView;
import com.google.android.maps.OverlayItem;

public class BaloonItem extends BalloonItemizedOverlay<OverlayItem> {

	private ArrayList<OverlayItem> m_overlays = new ArrayList<OverlayItem>();
	private static OnBalloonTap monBalloopTap;

	public BaloonItem(Drawable defaultMarker, MapView mapView) {
		super(boundCenterBottom(defaultMarker), mapView); // !important : fix 2493, baloons must have bottom|center gravity 
		//super(boundCenter(defaultMarker), mapView);     // ( only center gravity leads to marker displacement ) 
	}

	public void addOverlay(OverlayItem overlay) {
		m_overlays.add(overlay);
		populate();
	}

	@Override
	protected OverlayItem createItem(int i) {
		return m_overlays.get(i);
	}

	@Override
	public int size() {
		return m_overlays.size();
	}

	@Override
	protected boolean onBalloonTap(int index, OverlayItem item) {
		CustomMapOverlay customItem = (CustomMapOverlay) m_overlays.get(index);
		if (customItem.isForward()) {
			if (customItem.getId() != 0) {
				if(monBalloopTap!= null)
					monBalloopTap.showOnBalloonTap(customItem.getContext(),
						String.valueOf(customItem.getId()),
						String.valueOf(item.getPoint().getLatitudeE6() / 1E6),
						String.valueOf(item.getPoint().getLongitudeE6() / 1E6));
				/*
				 * Intent intent = new
				 * Intent().setClass(customItem.getContext(), ShopCard.class);
				 * intent.putExtra(Constants.ShopCard_SHOP_ID,
				 * String.valueOf(customItem.getId()));
				 * intent.putExtra(Constants.Maps_LATITUDE,
				 * String.valueOf(customItem.getPoint().getLatitudeE6()/1E6));
				 * intent.putExtra(Constants.Maps_LONGITUDE,
				 * String.valueOf(customItem.getPoint().setOnBalloopTapgetLongitudeE6()/1E6));
				 * customItem.getContext().startActivity(intent);
				 */
			}
		} else {
			// если это оформление заказа просто закрываю карту и запоминаю
			// название и айди магазина
			BasketData data = BasketData.getInstance();
			CheckoutBean b = data.getCheckoutBean();
			b.setShop_id(customItem.getId());
			b.setShopAddress(customItem.getTitle());
			((Activity) customItem.getContext()).finish();
		}
		return true;
	}

	public static void setOnBalloopTap(OnBalloonTap onBalloopTap) {
		monBalloopTap = onBalloopTap;
	}
	
	
	
}
