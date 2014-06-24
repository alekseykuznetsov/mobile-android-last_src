package ru.enter;

import java.util.List;

import ru.enter.route.RouteAsyncLoader;
import ru.enter.utils.Utils;
import ru.enter.widgets.MyMapView;
import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.ViewGroup.LayoutParams;

import com.flurry.android.FlurryAgent;
import com.google.android.maps.MapActivity;

public class MapRoute extends MapActivity implements LocationListener{

        private MyMapView mapView;
		private LocationManager mLocationManager;
		private Location mShopLocation;
		private String mCurrentProvider;
		private RouteAsyncLoader mLoader = null;

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            
            //получили координаты магазина
            //возможно стоит повесить try
            double shopLatitude = getIntent().getExtras().getDouble(Maps.LATITUDE);
    		double shopLongitude = getIntent().getExtras().getDouble(Maps.LONGITUDE);
    		
    		mShopLocation = new Location(LocationManager.GPS_PROVIDER);
    		mShopLocation.setLatitude(shopLatitude);
    		mShopLocation.setLongitude(shopLongitude);
    		
    		mapView = new MyMapView(this, Utils.getGeoApi());
    		mapView.setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT));
    		mapView.setClickable(true);
            mapView.setBuiltInZoomControls(true);
            
            setContentView(mapView);

            //запустили сервис для получения своих координат
            mLocationManager = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
            
            //чтобы пользователь не ждал обновления, сначала покажем закешированное
            Location lastKnownLocation = tryToGetMyCachedLocation();
            
            //грузим маршрут по старым координатам
            if (lastKnownLocation != null){
            	mCurrentProvider = lastKnownLocation.getProvider();
            	loadRoute(lastKnownLocation, mShopLocation);
            }
        }
        
        @Override
    	protected void onStart()
    	{
    		super.onStart();
    		FlurryAgent.onStartSession(this, "3X5JRBVFV7QWXNP3Q8H2");
    	}
    	 
    	@Override
    	protected void onStop()
    	{
    		super.onStop();		
    		FlurryAgent.onEndSession(this);
    	}
        
        private void loadRoute (Location from, Location to){
        	if (from != null && to != null){
        	 mLoader = new RouteAsyncLoader(from, to, mapView);
        	 mLoader.execute();
        	}
        }
        
        public Location tryToGetMyCachedLocation() {
            List<String> matchingProviders = mLocationManager.getAllProviders();
            for (String provider: matchingProviders) {
            	Location location = mLocationManager.getLastKnownLocation(provider);
            	if (location != null) {
            		return location;
            	}
            }
            return null;
        }
        
		//убираем обновление координат
        protected void onPause() {
        	mLocationManager.removeUpdates(this);
        	
        	super.onPause();
        };
        
        @Override
        protected void onDestroy() {
        	if(mLoader!=null){ // removing task if user leaves MapRoute
        		mLoader.cancel(true);
        	}
	        mLocationManager.removeUpdates(this);
	        super.onDestroy();
	        mapView.cleanUpMemory(); // rescue from OOM exception
	        System.gc();
        }
        
        @Override
    	public void onLowMemory() {
    		super.onLowMemory();
    		mapView.cleanUpMemory(); // rescue from OOM exception
    	}
        
        //возобновляем получение координат
        protected void onResume() {
        	mLocationManager.requestLocationUpdates(TextUtils.isEmpty(mCurrentProvider) ? LocationManager.GPS_PROVIDER : mCurrentProvider
        			, 1000L, 500.0f, MapRoute.this);
        	super.onResume();
        };

        @Override
        protected boolean isRouteDisplayed() {
                return false;
        }

        //обновляется при обновлении нашего местоположения
		@Override
		public void onLocationChanged(Location location) {
			loadRoute(location, mShopLocation);
		}

		@Override
		public void onProviderDisabled(String provider) {}

		@Override
		public void onProviderEnabled(String provider) {}

		@Override
		public void onStatusChanged(String provider, int status, Bundle extras) {}
			
}
