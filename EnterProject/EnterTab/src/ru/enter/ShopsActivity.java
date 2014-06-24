package ru.enter;

import java.util.ArrayList;

import com.flurry.android.FlurryAgent;

import ru.enter.base.BaseMenuActivity;
import ru.enter.beans.ShopBean;
import ru.enter.dialogs.ProgressDialogFragment;
import ru.enter.dialogs.alert.NoShopsInCityDialogFragment;
import ru.enter.fragments.ShopBottomFragment;
import ru.enter.fragments.ShopBottomMapFragment;
import ru.enter.fragments.ShopLeftFragment;
import ru.enter.fragments.ShopTopFragment;
import ru.enter.parsers.ShopsParser;
import ru.enter.route.Road;
import ru.enter.utils.Constants;
import ru.enter.utils.PreferencesManager;
import ru.enter.utils.URLManager;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.SparseArray;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout.LayoutParams;

public class ShopsActivity extends BaseMenuActivity {//implements OnNavigationListener{
	
	public final static int MODE_INFO = 0;
	public final static int MODE_MAP = 1;
	private int mShopMode = MODE_MAP;
	
	public final static int MAP_MODE_ALL = 0;
	public final static int MAP_MODE_SINGLE = 1;
	private int mMapMarkerMode = MAP_MODE_ALL;
	
	public final static int MAP_VIEWMODE_NORMAL = 0;
	public final static int MAP_VIEWMODE_FULLSCREEN = 1;
	private int mMapViewMode = MAP_VIEWMODE_NORMAL;
	
	public static String TAG_FR_LEFT = "tagLeft";
	public static String TAG_FR_TOP = "tagTop";
	public static String TAG_FR_BOTTOM = "tagBottom";
	
	private FrameLayout flLeft;
	private FrameLayout flTop;
	//private FrameLayout flBottom;
	
	private ArrayList<ShopBean> mShopArray;
	private ShopBean mCurrentShop;
	private SparseArray<Road> mRoutes = null; //new SparseArray<Road>(); //Hashmap<Shop.Id,Road>
	private boolean firstRun = true;
	private boolean mFirstTimeInSession;
		
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.shop_ac);
	
		flLeft = (FrameLayout)findViewById(R.id.shop_ac_left_frame);
		flTop = (FrameLayout)findViewById(R.id.shop_ac_top_frame);
		//flBottom = (FrameLayout)findViewById(R.id.shop_ac_bottom_frame);

		setTitleCenter(getResources().getString(R.string.actionbar_shops));
	     
		if(mShopArray == null) new ShopsLoader().execute();
		else{
			startLeft(true);
			//initRightMap();
		}
		
		mFirstTimeInSession = true;
	}
	
		
	@Override
	public void onStart() {
		super.onStart();
		FlurryAgent.onStartSession(this, "3X5JRBVFV7QWXNP3Q8H2");
		onConfigurationChanged(getResources().getConfiguration());
		
		if(mFirstTimeInSession){
			mFirstTimeInSession = false;
			FlurryAgent.logEvent(Constants.FLURRY_EVENT.Go_To_Shops.toString());
		}
	}	
	
	@Override
	protected void onStop()
	{
		super.onStop();		
		FlurryAgent.onEndSession(this);
	}
	
	public void startLeft(){
		startLeft(false);
	}
	
	public void startLeft(boolean tapOnFirst) {
		FragmentManager manager = getFragmentManager();
		FragmentTransaction transaction = manager.beginTransaction();
		ShopLeftFragment leftFragment = ShopLeftFragment.getInstance();
		if (tapOnFirst) {
			Bundle args = new Bundle();
			args.putBoolean(ShopLeftFragment.ARG_TAPONFIRST, true);
			leftFragment.setArguments(args);
			if (mShopArray != null && mShopArray.isEmpty()) {

				if (getFragmentManager().findFragmentByTag("NoShopsInCityDialogFragment") == null) {

					NoShopsInCityDialogFragment frg = NoShopsInCityDialogFragment
							.getInstance();
					frg.setonClickListener(new OnClickListener() {

						@Override
						public void onClick(DialogInterface dialog, int which) {
							// TODO Auto-generated method stub
							finish();
						}
					});
					frg.show(getFragmentManager(), "NoShopsInCityDialogFragment");
				}
			}
		}
		transaction.replace(R.id.shop_ac_left_frame, leftFragment, TAG_FR_LEFT);
		transaction.commit();
	}
	
	/* not used, version with 
	private void initRightMap() {
		flTop.setVisibility(View.GONE); //hide top panel & show map with shops
		FragmentManager manager = getFragmentManager();
		FragmentTransaction transaction = manager.beginTransaction();
		ShopBottomMapFragment mapFragment = ShopBottomMapFragment.getInstance();
		Bundle args = new Bundle();
		args.putString(ShopBottomMapFragment.ARG_ZOOM,"zoom");
		mapFragment.setArguments(args);
		transaction.replace(R.id.shop_ac_bottom_frame, mapFragment,TAG_FR_BOTTOM);
		transaction.commit();
	} */
		
	public void startRight() {
		FragmentManager manager = getFragmentManager();
		FragmentTransaction transaction = manager.beginTransaction();
		ShopTopFragment topFragment = ShopTopFragment.getInstance();
		//flTop.setVisibility(View.VISIBLE);
		transaction.replace(R.id.shop_ac_top_frame, topFragment,TAG_FR_TOP);
		switch (mShopMode) {
			case MODE_INFO:
				ShopBottomFragment infoFragment = ShopBottomFragment.getInstance();		
				transaction.replace(R.id.shop_ac_bottom_frame, infoFragment,TAG_FR_BOTTOM);
				break;
			case MODE_MAP:
				ShopBottomMapFragment mapFragment = ShopBottomMapFragment.getInstance();
				Bundle b = new Bundle();
				b.putBoolean(ShopBottomMapFragment.ARG_FIRSTRUN, firstRun);
				mapFragment.setArguments(b);
				firstRun = false;
				transaction.replace(R.id.shop_ac_bottom_frame, mapFragment,TAG_FR_BOTTOM);
				break;
			default:
				break;
		}
		transaction.commit();		
	}
	//refresh current shop from tap on map's marker with updating left & top fragments
	public void refreshFromMap(){
		FragmentManager manager = getFragmentManager();
		ShopLeftFragment left = (ShopLeftFragment) manager.findFragmentByTag(TAG_FR_LEFT);
		if( left==null ){
			startLeft();
		}else left.onShopChanged(mCurrentShop);
		ShopTopFragment top =  (ShopTopFragment) manager.findFragmentByTag(TAG_FR_TOP);
		if( top==null ){
			startRight();
		}else top.onShopChanged(mCurrentShop);
	}
	
	//called from 2 places: 1)mapballoon button 'Info', 2)topfragment button 'Info'
	public void refreshInfoMode(){
		FragmentManager manager = getFragmentManager();
		FragmentTransaction transaction = manager.beginTransaction();
		ShopBottomFragment bottomFragment = ShopBottomFragment.getInstance();
		transaction.replace(R.id.shop_ac_bottom_frame, bottomFragment,TAG_FR_BOTTOM);
		transaction.commit();
		ShopTopFragment top =  (ShopTopFragment) manager.findFragmentByTag(TAG_FR_TOP);
		top.setButtonMode(false);
		if( mMapViewMode==MAP_VIEWMODE_FULLSCREEN ) //if map in fullscreen mode we should collapse it
			toggleMapViewMode();
	}
		
	public ArrayList<ShopBean> getShopArray(){
		return mShopArray;
	}
	
	public ShopBean getCurrentShop(){
		return mCurrentShop;
	}
	
	public void setCurrentShop(ShopBean currentShop){
		mCurrentShop = currentShop;
	}
	
	public int getShopMode() {
		return mShopMode;
	}

	public void setShopMode(int mShopMode) {
		this.mShopMode = mShopMode;
	}

	public int getMapMarkerMode() {
		return mMapMarkerMode;
	}

	public void toggleMapMarkerMode(){
		setMapMarkerMode(mMapMarkerMode==MAP_MODE_ALL ? MAP_MODE_SINGLE : MAP_MODE_ALL);
	}
	
	public void setMapMarkerMode(int mMapMarkerMode) {
		this.mMapMarkerMode = mMapMarkerMode;
		Intent notifyMarkerMode = new Intent();
	    notifyMarkerMode.setAction(Maps.RECEIVER_MAPS_MARKER);
	    notifyMarkerMode.putExtra(Maps.EXTRA_RECEIVER_MARKER_MODE,mMapMarkerMode);
	    sendBroadcast(notifyMarkerMode);
	}
	
	public SparseArray<Road> getRoutes() {
		return mRoutes;
	}
	
	public void updateShopRoute(int shopId,Road route){
		FragmentManager manager = getFragmentManager();
		ShopLeftFragment left = (ShopLeftFragment) manager.findFragmentByTag(TAG_FR_LEFT);
		if(left!=null){
			left.updateShopRoute(shopId, route);
		}
	}
	
	public void setRoutes(SparseArray<Road> mRoutes) {
		this.mRoutes = mRoutes;
		FragmentManager manager = getFragmentManager();
		ShopLeftFragment left = (ShopLeftFragment) manager.findFragmentByTag(TAG_FR_LEFT);
		if(left!=null){
			left.setRoutes(mRoutes);
		}
	}

	public void toggleMapViewMode(){
		//TODO: resizing animation
		if( mMapViewMode==MAP_VIEWMODE_NORMAL ){
			mMapViewMode = MAP_VIEWMODE_FULLSCREEN;
			flLeft.setVisibility(View.GONE);
			flTop.setVisibility(View.GONE);
		}
		else{
			mMapViewMode = MAP_VIEWMODE_NORMAL;
			flLeft.setVisibility(View.VISIBLE);
			flTop.setVisibility(View.VISIBLE);
		}
	}
	
	class ShopsLoader extends AsyncTask<Void, Void, ArrayList<ShopBean>> {

		private ProgressDialogFragment progress;

		@Override
		protected void onPreExecute() {
			progress = ProgressDialogFragment.getInstance();
			progress.show(getFragmentManager(), "progress");
		}

		@Override
		protected ArrayList<ShopBean> doInBackground(Void... params) {
			return new ShopsParser(URLManager.getShopList(PreferencesManager.getCityid())).parseFull();
		}
		
		@Override
		protected void onPostExecute(ArrayList<ShopBean> result) {
			mShopArray = result;
			progress.dismiss();
			startLeft(true);
			//initRightMap();
		}
	}
	
	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		
		FrameLayout frameLeft = (FrameLayout) findViewById(R.id.shop_ac_left_frame);
		FrameLayout topLeft = (FrameLayout) findViewById(R.id.shop_ac_top_frame);
		
		if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
			LayoutParams params = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT, 2f);
			frameLeft.setLayoutParams(params);
			topLeft.setLayoutParams(params);
		} else if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {
			LayoutParams params_left = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT, 1.4f);
			LayoutParams params_top = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT, 1.2f);
			frameLeft.setLayoutParams(params_left);
			topLeft.setLayoutParams(params_top);
		}
	}
	
}
