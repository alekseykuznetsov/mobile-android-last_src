package ru.enter;


import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.json.JSONException;

import com.flurry.android.FlurryAgent;

import ru.enter.Listeners.LoadListener;
import ru.enter.adapters.ShopAdapter;
import ru.enter.beans.ShopBean;
import ru.enter.parsers.ShopsParser;
import ru.enter.utils.PreferencesManager;
import ru.enter.utils.URLManager;
import ru.enter.utils.Utils;
import ru.enter.widgets.HeaderFrameManager;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.FloatMath;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.ProgressBar;

public class ShopsActivity extends Activity implements LocationListener, OnItemClickListener{
	private ProgressBar progress;
	private ShopAdapter adapter;
	private int mCityId;
	private LocationManager mLocationManager;
	private Location mLastKnownLocation;
	private static LoadListener loadListener;
	public static ArrayList<ShopBean> shopBeans;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.shops);
		Bundle bundle = getIntent().getExtras();
		if(bundle != null && bundle.containsKey("LINK"))
			((FrameLayout) findViewById(R.id.shop_list_frame))
			.addView(HeaderFrameManager.getHeaderView(this, "В магазинах", false));
		else
			((FrameLayout) findViewById(R.id.shop_list_frame)).setVisibility(View.GONE);
		
		mCityId = PreferencesManager.getCityid();
		ListView list = (ListView) findViewById(R.id.shops_listView);
		progress = (ProgressBar) findViewById(R.id.shops_progress);
		
		shopBeans = new ArrayList<ShopBean>();
		
		adapter = new ShopAdapter(this);
		new Download().execute(URLManager.getShopList(mCityId));
		list.setAdapter(adapter);
		list.setOnItemClickListener(this);
		
        mLocationManager = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
        mLastKnownLocation = tryToGetMyCachedLocation();
        
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
    
    //возобновляем получение координат
    protected void onResume() {
    	if (Utils.CITY_CHANGED_FLAG){
    		mCityId = PreferencesManager.getCityid();
    		new Download().execute(URLManager.getShopList(mCityId));
    		//TODO устанавливаю этот флаг по сути для одних магазинов. Проверил флаг - убрал
    		Utils.CITY_CHANGED_FLAG = false;
    	}
    	mLocationManager.requestLocationUpdates(mLastKnownLocation==null ? LocationManager.GPS_PROVIDER : mLastKnownLocation.getProvider(), 1000L, 500.0f, this);
    	super.onResume();
    };
    
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
    
	public static void setLoadListener(LoadListener l){
		loadListener = l;
	}
	
	private void refreshDistances(Location myLocation){
		setDistances(shopBeans, myLocation);
		Collections.sort(shopBeans, distanceComparator);
		adapter.setObjects(shopBeans);
	}
	
	private class Download extends AsyncTask<String, Void, ArrayList<ShopBean>> {
	    
	    @Override
		 protected void onPreExecute() {
			 progress.setVisibility(View.VISIBLE);
		 }
	    
		protected ArrayList<ShopBean> doInBackground(String... urls) {
			ArrayList<ShopBean> result = null;

			try {
				result = new ShopsParser(URLManager.getShopList(mCityId)).parse();
			} catch (IOException e) {
				// NOP
			} catch (JSONException e) {
				// NOP
			}
			 
			 return result;
	     }

	     protected void onPostExecute(ArrayList<ShopBean> result) {
	    	 
	    	 if (result == null) {
	    		 //Toast.makeText(ShopsActivity.this, "Ошибка получения данных", Toast.LENGTH_SHORT).show();
	    		 showNoShopsDialog("Ошибка получения данных");
	    	 } else {
	    		 if(result.isEmpty()){
	    			 showNoShopsDialog("В данном городе пока нет магазинов");
	    			 //Toast.makeText(ShopsActivity.this, "В данном городе пока нет магазинов", Toast.LENGTH_SHORT).show();
	    		 } else {
	    			 shopBeans = result;
		    		 if(loadListener!=null) loadListener.isDone(true);
	    		 }
	    	 }
	    	 progress.setVisibility(View.GONE);
	    	 //обновляем по закешированной локации
	    	 refreshDistances(mLastKnownLocation);
	     }
	 }
	
	private void showNoShopsDialog(String mes) {
		Context context = getParent().getParent();
		AlertDialog.Builder dlg = new AlertDialog.Builder(context);
		dlg.setMessage(mes)
				.setPositiveButton("Ок", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int whichButton) {
						finish();
					}
				}).create().show();
	}
	
	private Comparator<ShopBean> distanceComparator = new Comparator<ShopBean>() {
		public int compare(ShopBean shop1, ShopBean shop2) {
			if (shop1.getDistance() < shop2.getDistance()) return -1;
			else if (shop1.getDistance() > shop2.getDistance()) return 1;
			else return 0;
		};
	};
	
	private void setDistances(ArrayList<ShopBean> shops, Location myLocation){
		for (ShopBean shop : shops){
			float distance = distance(Float.parseFloat(shop.getLatitude()), Float.parseFloat(shop.getLongitude()), myLocation);
			shop.setDistance(distance);
		}
	}
	
	public static float distance (float lat_a, float lng_a, Location loc) {
		
		if(loc == null)
			return 0;
		
		
		float lat_b = (float) loc.getLatitude();
		float lng_b = (float) loc.getLongitude();
		  float pk = (float) (180/Math.PI);

		  float a1 = lat_a / pk;
		  float a2 = lng_a / pk;
		  float b1 = lat_b / pk;
		  float b2 = lng_b / pk;

		  float t1 = FloatMath.cos(a1)*FloatMath.cos(a2)*
		     FloatMath.cos(b1)*FloatMath.cos(b2);
		  float t2 = FloatMath.cos(a1)*FloatMath.sin(a2)*
		     FloatMath.cos(b1)*FloatMath.sin(b2);
		  float t3 = FloatMath.sin(a1)*FloatMath.sin(b1);
		  double tt = Math.acos(t1 + t2 + t3);
		 
		  double itog = 6366000*tt;
		  
		  return (float) itog;
	}

	@Override
	public void onLocationChanged(Location location) {
		refreshDistances(location);
	}
	@Override
	public void onProviderDisabled(String provider) {}
	
	@Override
	public void onProviderEnabled(String provider) {}
	
	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) {}

	@Override
	public void onItemClick(AdapterView<?> arg0, View view, int arg2, long arg3) {
		ShopBean shop = (ShopBean) view.getTag();
		Intent intent = new Intent().setClass(this, ShopCard.class);
		intent.putExtra(Maps.LATITUDE, shop.getLatitude());
		intent.putExtra(Maps.LONGITUDE, shop.getLongitude());
		intent.putExtra(ShopCard.SHOP_ID, shop.getId());
		startActivity(intent);
	}
}
