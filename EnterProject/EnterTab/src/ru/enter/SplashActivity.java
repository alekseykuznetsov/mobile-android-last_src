package ru.enter;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import com.flurry.android.FlurryAgent;

import ru.enter.beans.CitiesBean;
import ru.enter.dialogs.CitiesDialogFragment;
import ru.enter.dialogs.DisableCityDialogFragment;
import ru.enter.dialogs.alert.EnableCityDialogFragment;
import ru.enter.dialogs.alert.EnableCityNoFindDialogFragment;
import ru.enter.dialogs.alert.EnableNoCityDialogFragment;
import ru.enter.dialogs.alert.EnableNoCitysDialogFragment;
import ru.enter.dialogs.alert.FirstStartDialogFragment;
import ru.enter.listeners.OnCitySelectListener;
import ru.enter.listeners.OnFirstStartListener;
import ru.enter.loaders.CityLoader;
import ru.enter.utils.PreferencesManager;
import ru.enter.utils.Utils;

import android.app.Activity;
import android.app.LoaderManager.LoaderCallbacks;
import android.content.DialogInterface.OnClickListener;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

public class SplashActivity extends Activity{
	
	//data for testlite
	private boolean isTestLite = true;
	private int versionBuild = 7;
	private TextView labelTestLite;
	
	
	private static final long SHOW_TIME = 2000;
	private static final int LOADER_ID=202;
	
	private Handler mHandler = new LeakSafeHandler(this);
	
	private Context context;
		
	private Location mMyLocation;
	private LocationManager mManager;
	private ArrayList<CitiesBean> mCities;
	private String cityName;
	private boolean isMyLoc, isGetCitys;
	private int citysState=0;
	public final static int DISABLE_LOCATION = 1;//отказ от определения места, только через выбор города
	public final static int ENABLE_LOCATION_NO_CITYS = 2;//разрешено определять, но не смогли загрузить списки городов
	public final static int ENABLE_LOCATION_NO_CITY = 3;//разрешено определять, загрузили списки городов, не определили позицию
	public final static int ENABLE_LOCATION_CITY_NO_FIND = 4;//разрешено определять, загрузили списки городов, определили позицию, но не устроило
	public final static int ENABLE_LOCATION_CITY = 5;//разрешено определять, загрузили списки городов, определили позицию
	private OnFirstStartListener firstListner = new OnFirstStartListener() {
		
		@Override
		public void updates() {
			// TODO Auto-generated method stub
			switch (citysState) {
			case DISABLE_LOCATION:
				if(PreferencesManager.getCityName().equals(""))
					showDialogDisable();
				break;		

			case ENABLE_LOCATION_CITY_NO_FIND:
				if(PreferencesManager.getCityName().equals(""))
					showDialogDisable();
				break;
				
			case ENABLE_LOCATION_NO_CITYS:				
				showDialogNoCitys();
				break;
				
			case ENABLE_LOCATION_NO_CITY:				
				if(PreferencesManager.getCityName().equals(""))
					showDialogNoCity();
				break;

			case ENABLE_LOCATION_CITY:				
				if(PreferencesManager.getCityName().equals(""))
					showDialogCity();
				break;
				
			default:
				break;
			}
		}		
		
	};
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		context=this;
		setContentView(R.layout.splash_ac);
		labelTestLite = (TextView) findViewById(R.id.splash_text_build);
		if(isTestLite){
			String text=getVersionTestLite(this,versionBuild);
			labelTestLite.setText(text);
			labelTestLite.setVisibility(View.VISIBLE);
		} else {
			labelTestLite.setVisibility(View.GONE);
		}
		startLoadBanners();
		startLoadCities();
		if(PreferencesManager.getCityName().equals("")){
			showFirstStartDialog();
		} else
			mHandler.sendEmptyMessageDelayed(0, SHOW_TIME);
		getLoaderManager().initLoader(LOADER_ID, null, mCallback);
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
	
	private void runMain(){
		Intent intent = new Intent();
		intent.setClass(this, MainActivity.class);
		startActivity(intent);
	}
	
	private void startLoadBanners(){
		
	}
	
	private void startLoadCities(){
		if (PreferencesManager.getCityid() == -1){
			
		}
	}
	//dialogs
	private void showFirstStartDialog(){
		if (getFragmentManager().findFragmentByTag("firststartdlg") == null) {
			FirstStartDialogFragment dialogFragment = FirstStartDialogFragment.getInstance();
			OnClickListener listner = new OnClickListener() {
				
				@Override
				public void onClick(DialogInterface dialog, int which) {
					// TODO Auto-generated method stub
					switch (which) {
					case DialogInterface.BUTTON_POSITIVE:
						citysState=DISABLE_LOCATION;
						showDialogSelectCity();
						break;
						
					case DialogInterface.BUTTON_NEGATIVE:
						startLocation();
						break;
		
					default:
						break;
					}
				}
			};
			dialogFragment.setonClickListener(listner);
			dialogFragment.show(getFragmentManager(), "firststartdlg");
		}
	}
	
	private void showDialogSelectCity(){
		if (getFragmentManager().findFragmentByTag("citydialog") == null) {
			CitiesDialogFragment dialogFragment = CitiesDialogFragment.getInstance();
			dialogFragment.setOnCitySelectListener(new OnCitySelectListener() {
				@Override
				public void onCitySelect(CitiesBean city) {
					mHandler.sendEmptyMessage(0);
				}
			});
			dialogFragment.setOnFirstStartListener(firstListner);
			dialogFragment.show(getFragmentManager(), "citydialog");
		}
	}
	
	private void showDialogDisable(){
		if (getFragmentManager().findFragmentByTag("disablecitydialog") == null) {
			DisableCityDialogFragment dialogFragment = DisableCityDialogFragment.getInstance();
			OnClickListener listner = new OnClickListener() {
				
				@Override
				public void onClick(DialogInterface dialog, int which) {
					// TODO Auto-generated method stub
					switch (which) {
					case DialogInterface.BUTTON_POSITIVE:
						citysState=DISABLE_LOCATION;
						showDialogSelectCity();
						break;
						
					case DialogInterface.BUTTON_NEGATIVE:
						finish();
						break;
		
					default:
						break;
					}
				}
			};
			dialogFragment.setonClickListener(listner);
			dialogFragment.show(getFragmentManager(), "disablecitydialog");
		}
	}
	
	private void showDialogNoCitys(){
		if (getFragmentManager().findFragmentByTag("enablenocitysdialog") == null) {
			EnableNoCitysDialogFragment dialogFragment = EnableNoCitysDialogFragment.getInstance();
			OnClickListener listner = new OnClickListener() {
				
				@Override
				public void onClick(DialogInterface dialog, int which) {
					// TODO Auto-generated method stub
					switch (which) {
					case DialogInterface.BUTTON_NEUTRAL:
						finish();
						break;
		
					default:
						break;
					}
				}
			};
			dialogFragment.setonClickListener(listner);
			dialogFragment.show(getFragmentManager(), "enablenocitysdialog");
		}
	}
	
	private void showDialogNoCity(){
		if (getFragmentManager().findFragmentByTag("enablenocitydialog") == null) {
			EnableNoCityDialogFragment dialogFragment = EnableNoCityDialogFragment.getInstance();
			OnClickListener listner = new OnClickListener() {
				
				@Override
				public void onClick(DialogInterface dialog, int which) {
					// TODO Auto-generated method stub
					switch (which) {
					case DialogInterface.BUTTON_POSITIVE:
						isMyLoc=false;
						startLocation();
						break;
					case DialogInterface.BUTTON_NEGATIVE:
						showDialogSelectCity();
						break;
		
					default:
						break;
					}
				}
			};
			dialogFragment.setonClickListener(listner);
			dialogFragment.show(getFragmentManager(), "enablenocitydialog");
		}
	}
	
	private void showDialogCity(){
		if (getFragmentManager().findFragmentByTag("enablecitydialog") == null) {
			EnableCityDialogFragment dialogFragment = EnableCityDialogFragment.getInstance();
			dialogFragment.setServiceMessage(cityName);
			OnClickListener listner = new OnClickListener() {
				
				@Override
				public void onClick(DialogInterface dialog, int which) {
					// TODO Auto-generated method stub
					switch (which) {
					case DialogInterface.BUTTON_POSITIVE:
						findCityInCitys();
						break;
					case DialogInterface.BUTTON_NEGATIVE:
						showDialogSelectCity();
						break;
		
					default:
						break;
					}
				}
			};
			dialogFragment.setonClickListener(listner);
			dialogFragment.show(getFragmentManager(), "enablecitydialog");
		}
	}
	
	private void showDialogCityNoFind(){
		if (getFragmentManager().findFragmentByTag("enablecitynofinddialog") == null) {
			EnableCityNoFindDialogFragment dialogFragment = EnableCityNoFindDialogFragment.getInstance();
			OnClickListener listner = new OnClickListener() {
				
				@Override
				public void onClick(DialogInterface dialog, int which) {
					// TODO Auto-generated method stub
					switch (which) {
					case DialogInterface.BUTTON_NEUTRAL:
						showDialogSelectCity();
						break;					
					default:
						break;
					}
				}
			};
			dialogFragment.setonClickListener(listner);
			dialogFragment.show(getFragmentManager(), "enablecitynofinddialog");
		}
	}
	
	//sorting
		private void findCityInCitys()	{
			int i=0;
			CitiesBean bean=null;
			for(i=0;i<mCities.size();i++){
				bean=mCities.get(i);
				if(cityName.equals(bean.getName()))				
					break;			
			}
			if(i<mCities.size()){
				PreferencesManager.setCityName(bean.getName());
		    	PreferencesManager.setCityId(bean.getId());
		    	PreferencesManager.setCityHasShop(bean.isHasShop());
				//setCity(null, bean.getName(), bean.getId());
		    	mHandler.sendEmptyMessage(0);
			} else {
				citysState=ENABLE_LOCATION_CITY_NO_FIND;
				showDialogCityNoFind();
			}
		}
	
	//services
	private void startLocation () {
		if (!isMyLoc) {
			cityName="";
			mLocationListener.alreadyStart = false;
			mManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
			
			if(!mManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
				Toast.makeText(context,"Не удалось обнаружить GPS модуль. Пожалуйста, выберите город из списка вручную.", Toast.LENGTH_LONG).show();
				showDialogSelectCity();
			}
			else
			{
				mManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0,	mLocationListener);
				mManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, mLocationListener);
			}
		}
		if (!isGetCitys) {
			getLoaderManager().restartLoader(LOADER_ID, null, mCallback);
		}
		_handler.sendEmptyMessageDelayed(MSG_ID, TIMEOUT);
	}

	private class LocListener implements LocationListener{

		public boolean alreadyStart;
		
		@Override
		public void onLocationChanged(Location location) {
			if(alreadyStart)
				return;
			alreadyStart = true;
			cityName="";
			mMyLocation = location;
			mManager.removeUpdates(mLocationListener);
			Geocoder gc= new Geocoder(context);
			List<Address> list=null;
			Address adr=null; 
			//
			try {
				list = gc.getFromLocation(mMyLocation.getLatitude(),  mMyLocation.getLongitude(), 1);
				if(!Utils.isEmptyList(list)){
					adr=list.get(0);
					cityName=adr.getLocality();
					cityName=(cityName==null?"":cityName);
				}
					
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			isMyLoc=true;
			myLocationIsDone();
		}

		@Override
		public void onStatusChanged(String provider, int status, Bundle extras) {
		}

		@Override
		public void onProviderEnabled(String provider) {
		}

		@Override
		public void onProviderDisabled(String provider) {
		}
		
	}
	
	private LocListener mLocationListener = new LocListener();
	
	
		LoaderCallbacks<List<CitiesBean>> mCallback = new LoaderCallbacks<List<CitiesBean>>() {
		
		@Override
		public Loader<List<CitiesBean>> onCreateLoader(int id, Bundle args) {
			// TODO Auto-generated method stub
			return new CityLoader(context);
		}
		
		@Override
		public void onLoaderReset(Loader<List<CitiesBean>> loader) {
			// TODO Auto-generated method stub
			
		}
		
		@Override
		public void onLoadFinished(Loader<List<CitiesBean>> loader, List<CitiesBean> data) {
			mCities=(ArrayList<CitiesBean>) data;
			isGetCitys=true;
			myLocationIsDone();
		}		
	};
	
	private void myLocationIsDone(){
		if(isGetCitys&&isMyLoc){
			_handler.removeMessages(MSG_ID);
			if(Utils.isEmptyList(mCities)) citysState=ENABLE_LOCATION_NO_CITYS;
			else if(cityName.equals("")) citysState=ENABLE_LOCATION_NO_CITY;
			else citysState=ENABLE_LOCATION_CITY;
			firstListner.updates();			
		}
	}
	
	// для таймаута, если не успел определить или загрузить
		private static final int TIMEOUT = 10000;
		private static final int MSG_ID = 0;
		private Handler _handler = new Handler() {
			public void handleMessage (Message msg) {
				mManager.removeUpdates(mLocationListener);				
				isMyLoc=false;
				getLoaderManager().destroyLoader(LOADER_ID);
				//if(mLoader!=null) mLoader.cancel(false);
				if(Utils.isEmptyList(mCities)) {
					citysState=ENABLE_LOCATION_NO_CITYS;
				} else {
					citysState=ENABLE_LOCATION_NO_CITY;
				}
				firstListner.updates();	
			}
			
		};
	
	//
	public static class LeakSafeHandler extends Handler {

		private final WeakReference<SplashActivity> mActivity;

		LeakSafeHandler(SplashActivity activity) {
			mActivity = new WeakReference<SplashActivity>(activity);
		}

		@Override
		public void handleMessage(Message msg) {
			SplashActivity activity = mActivity.get();
			if (activity != null) {
				activity.runMain();
			}
		}
	}
	
	@Override
	public void onBackPressed () {
		super.onBackPressed();
		mHandler.removeMessages(0);
	}
		
	
private static String getVersionTestLite(Context context,int version) {
    	
    	String versionName = null, result;
        try {
            versionName = context.getPackageManager().getPackageInfo(context.getPackageName(), 0).versionName;
        }
        catch (final NameNotFoundException e) {            
        }
        if (versionName == null) {
            versionName = "unknown";
        }
        
        result = "version "+versionName+" , build "+Integer.toString(version);
        
        return result;
    }
	
}
