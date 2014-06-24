package ru.enter.utils;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import ru.enter.beans.ShopBean;
import ru.enter.parsers.ShopsParser;
import ru.enter.utils.URLManager;
import ru.enter.utils.Utils;
import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.widget.Toast;

public class ShopLocator {

	private static final int TIMEOUT = 15000;
	private static final int MSG_ID = 0;
	private static final int MIN_DISTANCE = 500;

	private boolean mLocationIsDone;
	private boolean mAsyncDone;

	private Context mContext;
	
	private boolean m_isStart;//Отвечает запущен процесс или нет
	private boolean m_isBackground;

	private Location mMyLocation;
	private List<ShopBean> mShopsList;
	private LocationManager mManager;

	private OnNearestShopLocateListener mListener;

	private Handler mHandler = new LocatorHandler(this);

	private static class LocatorHandler extends Handler {
		private final WeakReference<ShopLocator> weak;

		LocatorHandler(ShopLocator locator) {
			weak = new WeakReference<ShopLocator>(locator);
		}

		@Override
		public void handleMessage (Message msg) {
			ShopLocator locator = weak.get();
			if (locator != null) {
				locator.mLocationIsDone=false;
				locator.mManager.removeUpdates(locator.mLocationListener);
				locator.onFailLocate();
			}
		}
	}

	public ShopLocator(Context context) {
		m_isStart=false;
		m_isBackground=false;
		mContext = context;
	}
	
	public boolean isStart(){
		return m_isStart;
	}
	
	public List<ShopBean> getLoadedShops () {
		return mShopsList;
	}

	public void start () {
		m_isStart=true;
		m_isBackground=false;
		mLocationIsDone=false;
		mAsyncDone=false;
		mHandler.sendEmptyMessageDelayed(MSG_ID, TIMEOUT);
		onStartLocate();
		startLocation();
		startLoadShops();
	}
	
	public void startBackground() {
		m_isStart=true;
		m_isBackground=true;
		mLocationIsDone=false;
		mAsyncDone=false;
		mHandler.sendEmptyMessageDelayed(MSG_ID, TIMEOUT);
		onStartLocate();
		startLocation();
		startLoadShops();
	}

	private void startLocation () {
		mManager = (LocationManager) mContext.getSystemService(Context.LOCATION_SERVICE);
		if(!mManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
			
		}
		else
		{
			mManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, mLocationListener);
			mManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, mLocationListener);
		}
	}

	private void startLoadShops () {
		ShopsLoader loader = new ShopsLoader();
		loader.execute();
	}

	private void checkResult () {
		if (mLocationIsDone && mAsyncDone) {
			mHandler.removeMessages(MSG_ID);

			if (!Utils.isEmptyList(mShopsList)) {

				Location location = new Location("");
				
				
				for (ShopBean shop : mShopsList) {
					
					
					location.setLatitude(Double.parseDouble(shop.getLatitude()));
					location.setLongitude(Double.parseDouble(shop.getLongitude()));

					float distanceFromHere = location.distanceTo(mMyLocation);
					

					if (distanceFromHere < MIN_DISTANCE&&mLocationIsDone) {
						if(m_isBackground) {
							onBackgroundLocated(shop);
						} else {
							onShopLocated(shop);
						}
						
						return;
					}/**/
				}
			}

			// если дошли сюда значит ошибка
			if(m_isBackground) {
				onBackgroundFailLocate();
			} else {
				onFailLocate();
			}
		}
	}

	// -----------------------------------ASYNCTASK----------------------------------------//

	private class ShopsLoader extends AsyncTask<Void, Void, ArrayList<ShopBean>> {

		@Override
		protected ArrayList<ShopBean> doInBackground (Void... params) {
			ArrayList<ShopBean> result = null;
			try {
				result = new ShopsParser(URLManager.getShopList(PreferencesManager.getCityid())).parse();
			} catch (Exception e) {
			}
			return result;
		}

		@Override
		protected void onPostExecute (ArrayList<ShopBean> result) {
			mShopsList = result;
			/*ShopBean shop = new ShopBean();
			//shop.setId(13);
			shop.setId(1);
			shop.setName("Супер Офиc ID-East");
			shop.setLatitude("48.69734975");
			shop.setLongitude("44.49420655");
			if (mShopsList != null){
				mShopsList.add(shop);
			}*/

			mAsyncDone = true;
			checkResult();
		}
	}

	// ------------------------------------LOCATOR----------------------------------------//
	private LocationListener mLocationListener = new LocationListener() {

		@Override
		public void onStatusChanged (String provider, int status, Bundle extras) {}

		@Override
		public void onProviderEnabled (String provider) {}

		@Override
		public void onProviderDisabled (String provider) {}

		@Override
		public void onLocationChanged (Location location) {
			mMyLocation = location;
			mLocationIsDone = true;
			checkResult();
			mManager.removeUpdates(mLocationListener);
		}
	};

	// ----------------------------------LISTENER----------------------------------------//

	public void setOnNearestShopLocateListener (OnNearestShopLocateListener listener) {
		mListener = listener;
	}

	public interface OnNearestShopLocateListener {
		void onStartLocate ();
		
		void onBackgroundLocated(ShopBean shop);
		
		void onBackgroundFailLocate();
		
		void onFailLocate ();

		void onShopLocated (ShopBean shop);
	}

	private void onStartLocate () {
		if (mListener != null) {
			mListener.onStartLocate();
		}
	}
	
	private void onBackgroundFailLocate () {
		if (mListener != null) {
			mListener.onBackgroundFailLocate();
		}
		m_isStart=false;
	}
	
	

	private void onBackgroundLocated (ShopBean shop) {
		if (mListener != null) {
			mListener.onBackgroundLocated(shop);
		}
		m_isStart=false;
	}
	

	private void onFailLocate () {
		if (mListener != null) {
			mListener.onFailLocate();
		}
		m_isStart=false;
	}
	
	

	private void onShopLocated (ShopBean shop) {
		if (mListener != null) {
			mListener.onShopLocated(shop);
		}
		m_isStart=false;
	}
}
