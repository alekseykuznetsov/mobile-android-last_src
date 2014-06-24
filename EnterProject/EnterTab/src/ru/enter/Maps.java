package ru.enter;

import java.util.ArrayList;
import java.util.List;

import ru.enter.beans.ShopBean;
import ru.enter.dialogs.ProgressSmallDialogFragment;
import ru.enter.maps.PolylineDecoder;
import ru.enter.maps.RouteOverlay;
import ru.enter.maps.RoutesJsonDecoder;
import ru.enter.maps.ShopInfoPopup;
import ru.enter.maps.json.MapRouteResponse;
import ru.enter.route.Road;
import ru.enter.utils.CustomMapOverlay;
import ru.enter.utils.MapCustomView;
import ru.enter.utils.Utils;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.util.SparseArray;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;
import android.widget.Toast;

import com.flurry.android.FlurryAgent;
import com.google.android.maps.GeoPoint;
import com.google.android.maps.ItemizedOverlay;
import com.google.android.maps.MapActivity;
import com.google.android.maps.MapController;
import com.google.android.maps.MapView;
import com.google.android.maps.MyLocationOverlay;
import com.google.android.maps.Overlay;
import com.google.android.maps.OverlayItem;

public class Maps extends MapActivity implements LocationListener {

	private static String TAG = "Maps";
	
	public final static int MAP_MODE_ALL = 0;
	public final static int MAP_MODE_SINGLE = 1;
	
	public static final String EXTRA_SHOPS = "shops";
	public static final String EXTRA_START_SHOP = "startShop";
	public static final String EXTRA_LATITUDE = "LATITUDE";
	public static final String EXTRA_LONGITUDE = "LONGITUDE";
	public static final String EXTRA_FORWARD = "FORWARD";
	public static final String EXTRA_STATE = "STATE";
	public static final String EXTRA_FIRST_START = "FIRSTSTART";
	
	public static String RECEIVER_MAPS_MARKER = "ru.enter.maps.receiver.marker";
	public static String RECEIVER_MAPS_ZOOM = "ru.enter.maps.receiver.zoom";
	public static String EXTRA_RECEIVER_MARKER_MODE = "markerMode";
	
	
	private static final String MAP_API_KEY = Utils.getGeoApi(); //"0_dLDLUNqQ0eSEpzs6XoBVEiKA5QnirsW9nZlvg"; 
	
    private ReceiverMaps mMarkerModeReceiver = null;
    private Boolean isRegistered = false;
    
    private MapCustomView mMapView;
    private MapController mMapController;
    //overlays    
    private MyLocationOverlay mMyLocationOverlay;
    private MarkerItemizedOverlay mShopMarkersOverlay;
    private RouteOverlay mRouteOverlay;
    private List<Overlay> mapOverlays;
    private Drawable mMarkerDrawable;
    //beans
    private ArrayList<ShopBean> mShops;
    private ShopBean mCurrentShop;
    private ShopBean mStartShop;
    //geo
    private ArrayList<GeoPoint> mShopGeopoints;
    private LocationManager mLocationManager;
    
    private int WAIT = 2000;
    //private int MESSAGE_ZOOMTO_AREA = 0;
    private int MESSAGE_ZOOMTO_CURRENT = 1;
    
    
	
	private String mLatitude;
	private String mLongtitude;
	private boolean mForward = false;
	private boolean mFirstStart;
	
	public enum State {shop,     /* ShopsActivity */
		   			   shopcheck,/* ShopsListWithCheckActivity */ 
                       other};   /* for other cases(view one marker by its lat/long)*/
                       
	private State mState = State.shop;

	
    private Handler handler = new Handler() {
		@Override
        public void handleMessage(Message msg) 
        {
			if( msg.what==MESSAGE_ZOOMTO_CURRENT ){
	            if(mMapView!=null){
	            	MapController controller = mMapView.getController();
	            	if (controller!=null){
	            		try{
	            			ShopBean s = (ShopBean)msg.obj;
	            			GeoPoint curr = new GeoPoint((int)( Float.parseFloat(s.getLatitude())*1E6), 
								 					 	 (int)( Float.parseFloat(s.getLongitude())*1E6));
	            			if(mFirstStart) //bug 2593(initial zooming)
	            				zoomToArea();
	            			if(curr!=null){
	            				controller.animateTo(curr);
	            				mShopMarkersOverlay.tapOnMarker(s);
	            			}
	            			//controller.scrollBy(1, 1);
	            		}catch (Exception e) {	
	            			e.printStackTrace();
	            		}
            		}
	            }
			}
        }
    };
    
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);        
        /* dont work on tablet god knows why, map freezes every 1sec. so its impossible to use
        mRotateView = new RotateView(this);
        mMapView = new MapCustomView(this, MAP_API_KEY);
        mMapView.setClickable(true);
        mMapView.setBuiltInZoomControls(true);
        mRotateView.addView(mMapView);
        setContentView(mRotateView);*/
        
        
        mMapView = new MapCustomView(this, MAP_API_KEY);
        mMapView.setClickable(true);
        mMapView.setReticleDrawMode(MapView.ReticleDrawMode.DRAW_RETICLE_NEVER); // fix 2586(the UFO bug)
        mMapView.setBuiltInZoomControls(true);
        setContentView(mMapView); 
        
        mMyLocationOverlay = new MyLocationOverlay(this, mMapView);
        mMapView.getOverlays().add(mMyLocationOverlay);
        //mMapView.setEnabled(true);
        mMapController = mMapView.getController();
        //receiver
        mMarkerModeReceiver = new ReceiverMaps();
        //geo
        mLocationManager = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
        //extras        
        Bundle bundle = getIntent().getExtras();
        if(bundle != null)
        {
        	mShops = (ArrayList<ShopBean>)bundle.getSerializable(EXTRA_SHOPS);
        	mStartShop = (ShopBean)bundle.getSerializable(EXTRA_START_SHOP);
        	mCurrentShop = mStartShop;
        	mLatitude = bundle.getString(EXTRA_LATITUDE);
        	mLongtitude = bundle.getString(EXTRA_LONGITUDE);
        	mForward = bundle.getBoolean(EXTRA_FORWARD);
        	mState = State.valueOf(bundle.getString(EXTRA_STATE)!=null?bundle.getString(EXTRA_STATE):"other");
        	mFirstStart = bundle.getBoolean(EXTRA_FIRST_START);
        }
        mapOverlays = mMapView.getOverlays();
		mMarkerDrawable = this.getResources().getDrawable(R.drawable.icon_pin);
        mapOverlays.add(mMyLocationOverlay);
		CreateData();
    	if( mStartShop!=null ){
    		mMapView.invalidate();
    		Message msg = new Message();
    		msg.what = MESSAGE_ZOOMTO_CURRENT;
    		msg.obj = mStartShop;
    		handler.sendMessage(msg);
    	}
		/* need to clarify WFT
		switch (state) {
		case other:
			
			break;
		case shop:
			CreateData();
			break;
			//if(ShopsActivity.shopBeans == null)
			/*if(ShopsActivity.shopBeans.isEmpty())
			{
		        ShopsActivity.setLoadListener(new LoadListener() {
					
					@Override
					public void isDone(boolean done) {
						CreateData();
					}
				});
			}
			else
				CreateData();
			break; 
		case shopcheck:
			if(ShopsListWithCheckActivity.bean == null)
			{
				ShopsListWithCheckActivity.setLoadListener(new LoadListener() {
					
					@Override
					public void isDone(boolean done) {
						CreateData();
					}
				});
			}
			else
				CreateData();
			break;
		default:
			break;
		}*/
		
    }   
    
    private void CreateData()
    {
    	mShopGeopoints = new ArrayList<GeoPoint>();
    	int lat;
    	int lon;
    	GeoPoint point;
    	CustomMapOverlay overlayitem;
    	ShopsActivity act = (ShopsActivity) this.getParent();
		int mode = act.getMapMarkerMode();
    	switch (mState) {
    	//непонятный кейс, я его немного попилил в CustomMapOverlay, upd:  я тоже не знаю зачем оно тут
		case other:
			/*lat = (int) (Float.parseFloat(mLatitude)*1E6);
    		lon = (int) (Float.parseFloat(mLongtitude)*1E6);
			point = new GeoPoint(lat,lon);
			//overlayitem = new CustomMapOverlay(point, "", 0,this,false);
			//baloonItem.addOverlay(overlayitem);
			//baloonItem.setText("Подробнее");
			mShopGeopoints.add(point);
			Log.i("test","2");*/
			break;
		case shop:
    		setMarkerMode(mode);
    		getPoints(mShops, mode);
			//handler.sendEmptyMessage(MESSAGE_ZOOMTO_AREA);
			//for(ShopBean b :ShopsActivity.shopBeans)
			/*for(ShopBean b : shops)
			{
				lat = (int) (Float.parseFloat(b.getLatitude())*1E6);
	    		lon = (int) (Float.parseFloat(b.getLongitude())*1E6);
				point = new GeoPoint(lat,lon);
				mShopMarkersOverlay.addOverlay(new OverlayItem(point, b.getName(),b.getId()+""));
				//overlayitem = new CustomMapOverlay(point, b.getName(), b.getId(),this,forward);
				//baloonItem.addOverlay(overlayitem);
				//baloonItem.setText("Подробнее");
				points.add(point);
				Log.i("test","1");
			}*/
			
			break;
		case shopcheck:
    		setMarkerMode(mode);
    		getPoints(mShops, mode);
			//пришли из геолокации на первом шаге заказа товара
			/*for(ShopBean b :ShopsListWithCheckActivity.bean)
			{
				lat = (int) (Float.parseFloat(b.getLatitude())*1E6);
	    		lon = (int) (Float.parseFloat(b.getLongitude())*1E6);
				point = new GeoPoint(lat,lon);
				overlayitem = new CustomMapOverlay(point, b.getName(), b.getId(), this, false);
				baloonItem.addOverlay(overlayitem);
				baloonItem.setText("Выбрать");
				points.add(point);
				Log.i("test","1");
			}*/
			break;

		default:
			break;
		}

    	/*
    	if( act.getRoutes()==null ){ 
	    	Location last = tryToGetMyCachedLocation();
	    	if(last!=null){
	    		loadRoads(new GeoPoint((int)(last.getLatitude()*1E6),(int)(last.getLongitude()*1E6)));
	    	}
    	}*/
    	/*
		mMyLocationOverlay.runOnFirstFix(new Runnable(){ 
	      	public void run(){
	      		loadRoads(mMyLocationOverlay.getMyLocation());
			}
	    });*/
    }
    
    public void zoomToArea(){
		GeoPoint my = mMyLocationOverlay.getMyLocation();
		if(my != null && !mShopGeopoints.contains(my))
			mShopGeopoints.add(my);
    	int minLatitude = Integer.MAX_VALUE;
        int maxLatitude = Integer.MIN_VALUE;
        int minLongitude = Integer.MAX_VALUE;
        int maxLongitude = Integer.MIN_VALUE;

        for(int i = 0;i < mShopGeopoints.size();i++)
        	for(int j = i;j<mShopGeopoints.size();j++)
        	{
        		maxLatitude = Math.max(maxLatitude, Math.max(mShopGeopoints.get(i).getLatitudeE6(), mShopGeopoints.get(j).getLatitudeE6()));
                minLatitude = Math.min(minLatitude, Math.min(mShopGeopoints.get(i).getLatitudeE6(), mShopGeopoints.get(j).getLatitudeE6()));
                maxLongitude = Math.max(maxLongitude, Math.max(mShopGeopoints.get(i).getLongitudeE6(), mShopGeopoints.get(j).getLongitudeE6()));
                minLongitude = Math.min(minLongitude, Math.min(mShopGeopoints.get(i).getLongitudeE6(), mShopGeopoints.get(j).getLongitudeE6()));
        	}
        if(mMapView!=null){
        	MapController controller = mMapView.getController();
        	if (controller!=null){
        		controller.zoomToSpan(Math.abs(maxLatitude - minLatitude), Math.abs(maxLongitude - minLongitude));
        		controller.animateTo(new GeoPoint((maxLatitude + minLatitude)/2,(maxLongitude + minLongitude)/2 ));
        		controller.zoomOut();
        	}
        }
    }
    //for bug 2551
    public void zoomToRoute(Road road){
    	int minLatitude = Integer.MAX_VALUE;
        int maxLatitude = Integer.MIN_VALUE;
        int minLongitude = Integer.MAX_VALUE;
        int maxLongitude = Integer.MIN_VALUE;
        ArrayList<GeoPoint> points = road.mGeoPoint;
        if(road.mGeoPoint!=null){
	        for(int i = 0;i < points.size();i++)
	        	for(int j = i;j<points.size();j++)
	        	{
	        		maxLatitude = Math.max(maxLatitude, Math.max(points.get(i).getLatitudeE6(), points.get(j).getLatitudeE6()));
	                minLatitude = Math.min(minLatitude, Math.min(points.get(i).getLatitudeE6(), points.get(j).getLatitudeE6()));
	                maxLongitude = Math.max(maxLongitude, Math.max(points.get(i).getLongitudeE6(), points.get(j).getLongitudeE6()));
	                minLongitude = Math.min(minLongitude, Math.min(points.get(i).getLongitudeE6(), points.get(j).getLongitudeE6()));
	        	}
	        if(mMapView!=null){
	        	MapController controller = mMapView.getController();
	        	if (controller!=null){
	        		controller.zoomToSpan(Math.abs(maxLatitude - minLatitude), Math.abs(maxLongitude - minLongitude));
	        		controller.animateTo(new GeoPoint((maxLatitude + minLatitude)/2,(maxLongitude + minLongitude)/2 ));
	        		controller.zoomOut();
	        	}
	        }
        }
    }
    
    
    public void setMarkerMode(int mode){
    	if(mShopMarkersOverlay!=null){
    		mShopMarkersOverlay.hidePopup();
    		mapOverlays.remove(mShopMarkersOverlay);
    	}
    	mShopMarkersOverlay = new MarkerItemizedOverlay(mMarkerDrawable, this);
    	ShopsActivity act =(ShopsActivity)getParent();
    	mCurrentShop = act.getCurrentShop();
    	mShops = act.getShopArray();
    	switch (mode) {
			case MAP_MODE_ALL:
				mShopMarkersOverlay.addShops(mShops);
				break;
			case MAP_MODE_SINGLE:
				mShopMarkersOverlay.addShop(mCurrentShop);
				break;
			default:
				break;
		}
    	mapOverlays.add(mShopMarkersOverlay);
    	mMapView.invalidate();
    }
    
    
    private void getPoints(ArrayList<ShopBean> shops,int mode){
    	try{
    		GeoPoint point;
    		switch (mode) {
			case MAP_MODE_ALL:
		    	for(ShopBean s:shops){
		    		point = new GeoPoint( (int)( Float.parseFloat(s.getLatitude())*1E6), 
		    							  (int)( Float.parseFloat(s.getLongitude())*1E6) );
		    		mShopGeopoints.add(point);
		    		//Log.d("POINTS", point.toString());
		    	}
				break;
			case MAP_MODE_SINGLE:
				point = new GeoPoint( (int)( Float.parseFloat(mCurrentShop.getLatitude())*1E6), 
						 			  (int)( Float.parseFloat(mCurrentShop.getLongitude())*1E6) );
	    		mShopGeopoints.add(point);
				break;
			default:
				break;
			}

    	}catch (Exception e) { 	e.printStackTrace();  }
    }
    
    @Override
    protected void onPause() {
    	super.onPause();
        if (isRegistered) {
            unregisterReceiver(mMarkerModeReceiver);
            isRegistered = false;
        }
    	mLocationManager.removeUpdates(this);
    }
    
    @Override
    protected void onResume() {	
        super.onResume();
        if (!isRegistered) {
        	IntentFilter filter = new IntentFilter();
        	filter.addAction(RECEIVER_MAPS_MARKER);
        	filter.addAction(RECEIVER_MAPS_ZOOM);
            registerReceiver(mMarkerModeReceiver, filter);
            isRegistered = true;
        }
        if(mLocationManager.isProviderEnabled(LocationManager.GPS_PROVIDER))
        {
        	mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000L, 500.0f,this);
        	mMyLocationOverlay.enableMyLocation();
        }
    }
    
    @Override
	protected void onStart()
	{
		super.onStart();
		FlurryAgent.onStartSession(this, "3X5JRBVFV7QWXNP3Q8H2");
	}	

    @Override
    protected void onStop() {    	
        mMyLocationOverlay.disableMyLocation();
        FlurryAgent.onEndSession(this);
        super.onStop();
    }
    
    @Override
    protected void onDestroy() {
    	super.onDestroy();
    	mMapView.cleanUpMemory(); // rescue from OOM exception
    	System.gc();
    }

    @Override
    protected boolean isRouteDisplayed() {
        return false;
    }
	
	@Override
	public void onLowMemory() {
		super.onLowMemory();
		mMapView.cleanUpMemory(); // rescue from OOM exception
	}
	
	public void setCurrentShop(ShopBean shop){
		if(shop!=null){
			mCurrentShop = shop;
			mShopMarkersOverlay.tapOnMarker(shop);
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
    
    private ShopBean findById(int id){
    	if(mShops!=null && mShops.size() > 0){
    		for(ShopBean s:mShops){
    			if(s.getId()==id){
    				return s;
    			}
    		}
    	}
    	return null;
    }
    
    private Road loadRouteByShop(ShopBean shop,GeoPoint from){
    	try{
			double lat = Double.parseDouble(shop.getLatitude());
			double lon = Double.parseDouble(shop.getLongitude());
			GeoPoint gpShop = new GeoPoint((int)(lat*1E6),(int)(lon*1E6));
			RoutesJsonDecoder decoder = new RoutesJsonDecoder(this);
			MapRouteResponse route = decoder.getRoute(from,gpShop);
			Road road = new Road();
			if(route.status.equals("OK")){
				road.result = Road.OK;
				road.mLength = route.routes.get(0).legs.get(0).distance.text;
				road.mDuration = route.routes.get(0).legs.get(0).duration.text;
				road.mGeoPoint = (ArrayList<GeoPoint>) PolylineDecoder.decodePoints(route.routes.get(0).polyline.points);
				return road;	
			}
    	}catch(Exception e){
    		e.printStackTrace();
    	}
		return null;
    }
    
	private void loadRoads(GeoPoint me,int shopId){
		if( me!=null ){
			new RoutesAsyncLoader(this, me, mShops,shopId).execute();
		}
	}
	
	private void loadRoad(GeoPoint me,ShopBean shop){
		if( me!=null ){
			new RouteAsyncLoader(this, me,shop).execute();
		}
	}
	
	private void drawRoad(Road road,int shopId){
		if(mRouteOverlay!=null)
			mapOverlays.remove(mRouteOverlay);
		if(road!=null){
			mRouteOverlay = new RouteOverlay(road.mGeoPoint,shopId);
			mapOverlays.add(mRouteOverlay);
			mShopMarkersOverlay.hidePopup(); // fix 2592
			zoomToRoute(road);
			//zoomToArea();
		}
	}
	
	/**
	 * Overlay for Enter shops in current city.
	 */
	class MarkerItemizedOverlay extends ItemizedOverlay<OverlayItem> {
		Context mContext;
		ShopInfoPopup mPanel;
		private ArrayList<OverlayItem> items = new ArrayList<OverlayItem>();

		public MarkerItemizedOverlay (Drawable defaultMarker, Context context) {
			super(boundCenterBottom(defaultMarker));
			mContext = context;
			switch (mState) {
			case shop:
				mPanel = new ShopInfoPopup(Maps.this,mMapView,R.layout.tablet_balloon_shops);
				break;
			case shopcheck:
				mPanel = new ShopInfoPopup(Maps.this,mMapView,R.layout.tablet_balloon_shopcheck);
				break;
			default:
				break;
			}
			
		}

		public void addOverlay (OverlayItem overlay) {
			items.add(overlay);
		}
	
		public void addShop(ShopBean s){
			if(s!=null){
				GeoPoint gp = new GeoPoint((int)(Float.parseFloat(s.getLatitude()) * 1E6), 
						(int)(Float.parseFloat(s.getLongitude()) * 1E6));
				items.add(new OverlayItem(gp, s.getName(), s.getId()+""));
			}
			populate();
		}
		
		public void addShops (ArrayList<ShopBean> shops) {
			if(shops!=null && shops.size()>0){
				GeoPoint gp = null;
				for(ShopBean s:shops){
					gp = new GeoPoint((int)(Float.parseFloat(s.getLatitude())*1E6), 
								      (int)(Float.parseFloat(s.getLongitude())* 1E6));
					items.add(new OverlayItem(gp, s.getName(), s.getId()+""));
				}
			}
			populate();
			
		}
		
		@Override
		protected OverlayItem createItem (int i) {
				return items.get(i);
		}

		@Override
		public int size () {
			return items.size();
		}

		public void hidePopup(){
			mPanel.hide();
		}
		
		public void tapOnMarker(ShopBean shop){
			int index = -1;
			for(int i=0;i<items.size();i++){ // search marker by it's title
				if(shop.getName().equals(items.get(i).getTitle())){
					index = i;
					break;
				}
			}
			if(index >=0){ 
				onTap(index); //lauch default tap event
			}
		}
		
		@Override
		protected boolean onTap (int index) {
			OverlayItem item = items.get(index);
			final ShopsActivity act = (ShopsActivity) ((Activity)mContext).getParent();
			if(act.getMapMarkerMode()==MAP_MODE_ALL) //fix bug 2600, changing shop only if in all_markers mode
				mCurrentShop = mShops.get(index);    
			act.setCurrentShop(mCurrentShop);
			act.refreshFromMap();
			// remove route if we tap on another marker
			if( mRouteOverlay!=null && mRouteOverlay.getShopId()!=mCurrentShop.getId() ){
				mapOverlays.remove(mRouteOverlay);
				mMapView.invalidate();
			}
			
			View view = mPanel.getPopupForPoint(item.getPoint()); // point on the map
			switch (mState) {
			case shop:
				((TextView) view.findViewById(R.id.mapBalloonTvAddress)).setText(item.getTitle()); // address, placed in 'title' member of OverlayItem
				((TextView) view.findViewById(R.id.mapBalloonBRoute)).setOnClickListener(new OnClickListener() {
					@Override
					public void onClick (View v) {
						GeoPoint me = mMyLocationOverlay.getMyLocation();
						//GeoPoint me = new GeoPoint((int)(39*1E6), (int)(51*1E6));
						/* old
						if( me!=null ){
							if( act.getRoutes()!=null ){ //check routes
								Road road = act.getRoutes().get(mCurrentShop.getId());
								if(road==null){
									loadRoads(me,mCurrentShop.getId());
								}else{
									drawRoad(road, mCurrentShop.getId());
								}
							}else{
								loadRoads(me,mCurrentShop.getId());
							}
						}else{
							Toast.makeText(mContext, "Ваше местоположение еще не определено", Toast.LENGTH_SHORT).show();
						}*/
						if( me!=null ){
							SparseArray<Road> routes = act.getRoutes();
							if( routes!=null ){ //check routes
								Road road = routes.get(mCurrentShop.getId());
								if(road==null){
									//loadRoads(me,mCurrentShop.getId());
									loadRoad(me, mCurrentShop); //loading single route instead of all routes;
								}else{
									drawRoad(road, mCurrentShop.getId());
								}
							}
							else{
								loadRoads(me,mCurrentShop.getId());
							}
						}else{
							Toast.makeText(mContext, "Ваше местоположение еще не определено", Toast.LENGTH_SHORT).show();
						}
					}
				});
				((TextView) view.findViewById(R.id.mapBalloonBInfo)).setOnClickListener(new OnClickListener() {
					@Override
					public void onClick (View v) {
						ShopsActivity act = (ShopsActivity) ((Activity)mContext).getParent();
						act.setCurrentShop(mCurrentShop);
						act.setShopMode(ShopsActivity.MODE_INFO);
						act.refreshInfoMode();
					}
				});
				break;
			case shopcheck:
				((TextView) view.findViewById(R.id.mapBalloonTvTitle)).setText(item.getTitle()); // address, placed in 'title' member of OverlayItem
				((TextView) view.findViewById(R.id.mapBalloonBGo)).setOnClickListener(new OnClickListener() {
					@Override
					public void onClick (View v) {
						//TODO : переход на требуемую активити после выбора магазина(в интенте передать ID)
						// id берется у mCurrentShop
						Toast.makeText(mContext, "Выбрать", Toast.LENGTH_SHORT).show();
						/* snippet from smart
						// если это оформление заказа просто закрываю карту и запоминаю
						// название и айди магазина
						BasketData data = BasketData.getInstance();
						CheckoutBean b = data.getCheckoutBean();
						b.setShop_id(customItem.getId());
						b.setAddress(customItem.getTitle());
						((Activity) customItem.getContext()).finish();
						*/
					}
				});
				break;
			default:
				break;
			}
						
			mPanel.show();
			mMapController.animateTo(item.getPoint());
			return true;
		}
	}
	
	private void reloadFailedRoutes(List<Integer> failed,GeoPoint from){
		//Log.d(TAG, "failed:" + failed.toArray());
		for(Integer id:failed){
			Log.d(TAG, "failed:" + id);
			RouteAsyncLoader l = new RouteAsyncLoader(this, from,findById(id));
			l.execute();
		}
	}

	class RoutesAsyncLoader extends AsyncTask<Void, Void, SparseArray<Road>>{

		private Context mContext;
		private GeoPoint from;
		private List<ShopBean> shops;
		private int shopId;
		private ProgressSmallDialogFragment progress;
		private boolean ok = true;
		private int LOAD_ATTEMPTS = 15;
		private int TIMEOUT = 100;

		
		public RoutesAsyncLoader (Context ctx, GeoPoint from,List<ShopBean> shops,int shopId){
			mContext = ctx;
			this.shops = shops;
			this.from = from;
			this.shopId = shopId;
		}
		
		@Override
		protected void onPreExecute() {
			progress = ProgressSmallDialogFragment.getInstance();
			progress.show(getFragmentManager(),ProgressSmallDialogFragment.TAG);
		}

		@Override
		protected SparseArray<Road> doInBackground(Void... params) {
			if( shops!=null && shops.size()>0 ){
				SparseArray<Road> routes = new SparseArray<Road>();
				for(ShopBean shop:shops){
					Road r = loadRouteByShop(shop, from);
					int attemptCount = 1;
					//loading route up to 15 times
					while (r==null && attemptCount<LOAD_ATTEMPTS){
						r = loadRouteByShop(shop, from);
						attemptCount += 1;
						try{ Thread.sleep(TIMEOUT);}catch(Exception e){}
						//Log.d(TAG, "loadinf:#"+shop.getId()+",count="+attemptCount);
					}
					if(r==null){
						//failed.add(shop.getId());
						ok = false;
						//Log.d(TAG, "failed for #"+shop.getId());
					}else{
						routes.append(shop.getId(),r);
					}
				}
				return routes;
			}
			return null;
		}
		
		@Override
		protected void onPostExecute(SparseArray<Road> result) {
			super.onPostExecute(result);
			ShopsActivity act = (ShopsActivity)getParent();
			act.setRoutes(result);
			progress.dismiss();
			if(!ok){
				Toast.makeText(mContext, "Не удалось загрузить некоторые данные", Toast.LENGTH_SHORT).show();
			}
			if(shopId >= 0 && result!=null ){
				drawRoad(result.get(shopId), shopId);
			}
		}
	}
	
	
	
	class RouteAsyncLoader extends AsyncTask<Void, Void, Road>{

		private GeoPoint from;
		private ShopBean shop;
		private ProgressSmallDialogFragment progress;

		
		public RouteAsyncLoader (Context ctx, GeoPoint from,ShopBean shop){
			this.from = from;
			this.shop = shop;
		}
		
		@Override
		protected void onPreExecute() {
			progress = ProgressSmallDialogFragment.getInstance();
			progress.show(getFragmentManager(),ProgressSmallDialogFragment.TAG);
		}

		@Override
		protected Road doInBackground(Void... params) {
			return loadRouteByShop(shop, from);
		}
		
		@Override
		protected void onPostExecute(Road result) {
			super.onPostExecute(result);
			ShopsActivity act = (ShopsActivity)getParent();
			act.updateShopRoute(shop.getId(), result);
			progress.dismiss();
		}
	}
	
	/* maps marker mode listener*/
    protected class ReceiverMaps extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(RECEIVER_MAPS_MARKER)) {
                int mode = intent.getIntExtra(EXTRA_RECEIVER_MARKER_MODE,0);
                setMarkerMode(mode);
            }
            if (intent.getAction().equals(RECEIVER_MAPS_ZOOM)) {
            	zoomToArea();
            }
        }
    }
	
	@Override
	public void onLocationChanged(Location me) {
		ShopsActivity act = (ShopsActivity)getParent();
		act.setRoutes(null);//delte previous routes
		//routes are not loaded until user taps on 'make route' in popup
		//GeoPoint gp = new GeoPoint((int)(me.getLatitude()*1E6),(int)(me.getLongitude()*1E6)); 
		//loadRoads(gp,mCurrentShop.getId());
	}
	
	//unused location callbacks
	@Override
	public void onProviderDisabled(String arg0) {}
	@Override
	public void onProviderEnabled(String arg0) {}
	@Override
	public void onStatusChanged(String arg0, int arg1, Bundle arg2) {}
	
	
}
