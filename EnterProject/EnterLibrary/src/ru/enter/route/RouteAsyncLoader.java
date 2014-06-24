package ru.enter.route;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.List;

import ru.ideast.enter.R;


import android.content.Context;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.os.AsyncTask;
import android.widget.Toast;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapController;
import com.google.android.maps.MapView;
import com.google.android.maps.Overlay;
import com.google.android.maps.OverlayItem;

public class RouteAsyncLoader extends AsyncTask<Void, Void, Road>{
	private enum MarkerType{
		mylocation,
		store
	}
	
	private double mFromLat, mFromLon, mToLat, mToLon;
	private MapView mMap;
	private List<Overlay> mOverlays;
	private Context mContext;

	public RouteAsyncLoader (Location from, Location to, MapView map){
		mMap = map;
		mContext = map.getContext();
		mOverlays = mMap.getOverlays();
		mFromLat = from.getLatitude();
		mFromLon = from.getLongitude();
		mToLat = to.getLatitude();
		mToLon = to.getLongitude();
	}
	
	@Override
	protected Road doInBackground(Void... params) {
		String url = RoadProvider.getUrl(mFromLat, mFromLon, mToLat, mToLon);//получаем урл для построения 
        InputStream is = getConnection(url);
        //try{ Thread.sleep(6000);}catch (Exception e) {} //for testing
        
		return RoadProvider.getRoute(is);
	}
	
	private InputStream getConnection(String url) {
        InputStream is = null;
        try {
                URLConnection conn = new URL(url).openConnection();
                is = conn.getInputStream();
        } catch (MalformedURLException e) {
                e.printStackTrace();
        } catch (IOException e) {
                e.printStackTrace();
        }
        return is;
	}
	
	@Override
	protected void onPostExecute(Road road) {
		mOverlays.clear();
    	//dont add route overlay if task has been cancelled
		if(!isCancelled()){
			if(road.result==Road.OK) 
	    	{
	            MapOverlay mapOverlay = new MapOverlay(road, mMap);
	            
	          //маркер на мою позицию
	            GeoPoint myPositionPoint = new GeoPoint((int)(mFromLat * 1E6), (int)(mFromLon * 1E6));
	            OverlayItem myPositionMarked = new OverlayItem(myPositionPoint, null, null);
	            OverlayWithMarkers overlayMarked = new OverlayWithMarkers(getDrawable(MarkerType.mylocation));
	            overlayMarked.addOverlay(myPositionMarked);
	            mOverlays.add(overlayMarked);
	            //маркер на позицию магазина
	            GeoPoint lastPoint = new GeoPoint((int)(mToLat * 1E6), (int)(mToLon * 1E6));
	            OverlayItem lastPointRoute = new OverlayItem(lastPoint, null, null);
	            OverlayWithMarkers overlayMarkedPoint = new OverlayWithMarkers(getDrawable(MarkerType.store));
	            overlayMarkedPoint.addOverlay(lastPointRoute);
	            mOverlays.add(overlayMarkedPoint);
	                      
	          //маршрут
	            mOverlays.add(mapOverlay);
	            mMap.invalidate();
	                        
	    	}
	    	else{
	    		
	    		//маркер на мою позицию
	            GeoPoint myPositionPoint = new GeoPoint((int)(mFromLat * 1E6), (int)(mFromLon * 1E6));
	            OverlayItem myPositionMarked = new OverlayItem(myPositionPoint, null, null);
	            OverlayWithMarkers overlayMarked = new OverlayWithMarkers(getDrawable(MarkerType.mylocation));
	            overlayMarked.addOverlay(myPositionMarked);
	            mOverlays.add(overlayMarked);
	            //маркер на позицию магазина
	            GeoPoint lastPoint = new GeoPoint((int)(mToLat * 1E6), (int)(mToLon * 1E6));
	            OverlayItem lastPointRoute = new OverlayItem(lastPoint, null, null);
	            OverlayWithMarkers overlayMarkedPoint = new OverlayWithMarkers(getDrawable(MarkerType.store));
	            overlayMarkedPoint.addOverlay(lastPointRoute);
	            mOverlays.add(overlayMarkedPoint);
	            
	            int minLatitude = Math.min(myPositionPoint.getLatitudeE6(),lastPoint.getLatitudeE6());
	    		int maxLatitude = Math.max(myPositionPoint.getLatitudeE6(),lastPoint.getLatitudeE6());
	    		int minLongitude = Math.max(myPositionPoint.getLongitudeE6(),lastPoint.getLongitudeE6());
	    		int maxLongitude = Math.min(myPositionPoint.getLongitudeE6(),lastPoint.getLongitudeE6());
	    		 
				MapController mapController = mMap.getController();
				mapController.zoomToSpan(
						Math.abs(maxLatitude - minLatitude),
						Math.abs(maxLongitude - minLongitude));
				mapController.animateTo(
						new GeoPoint((maxLatitude + minLatitude) / 2,
								(maxLongitude + minLongitude) / 2));
				mapController.zoomOut();
	            
	            mMap.invalidate();
	    		
	               if(road.result==Road.CONNECTION_ERROR)
	            	   Toast.makeText(mContext, "Ошибка соединения", Toast.LENGTH_LONG).show();
	               else
	            	   Toast.makeText(mContext, "Не удалось построить маршрут", Toast.LENGTH_LONG).show();
	    	}
		}
//        MapOverlay mapOverlay = new MapOverlay(road, mMap);
//        //маркер на мою позицию
//        GeoPoint myPositionPoint = new GeoPoint((int)(mFromLat * 1E6), (int)(mFromLon * 1E6));
//        OverlayItem myPositionMarked = new OverlayItem(myPositionPoint, null, null);
//        OverlayWithMarkers overlayMarked = new OverlayWithMarkers(getDrawable(MarkerType.mylocation));
//        overlayMarked.addOverlay(myPositionMarked);
//        mOverlays.add(overlayMarked);
//        //маркер на позицию магазина
//        GeoPoint lastPoint = new GeoPoint((int)(mToLat * 1E6), (int)(mToLon * 1E6));
//        OverlayItem lastPointRoute = new OverlayItem(lastPoint, null, null);
//        OverlayWithMarkers overlayMarkedPoint = new OverlayWithMarkers(getDrawable(MarkerType.store));
//        overlayMarkedPoint.addOverlay(lastPointRoute);
//        mOverlays.add(overlayMarkedPoint);
//        //маршрут
//        mOverlays.add(mapOverlay);
//        mMap.invalidate();
        //центрируем
//        makeRouteInCenter(road);
	}
	
	private Drawable getDrawable(MarkerType type){
		Drawable drawable = null;
		switch (type) {
		case mylocation:
			drawable = mContext.getResources().getDrawable(R.drawable.flag);
			break;
		case store:
			drawable = mContext.getResources().getDrawable(R.drawable.icon_pin);
			break;
		}
		drawable.setBounds(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
		return drawable;
	}
	
//	private void makeRouteInCenter(Road road){
//		
//		double xMin, xMax, yMin, yMax;
//		List<Point> routePoints = Arrays.asList(road.mPoints);
//		xMin = xMax = routePoints.get(0).mLatitude;
//		yMin = yMax = routePoints.get(0).mLongitude;
//		
//		for (Point point : routePoints){
//			if(point.mLatitude == 0.0 || point.mLongitude == 0.0)
//				continue;
//			if(point.mLatitude < xMin) xMin = point.mLatitude;
//        	if(point.mLatitude > xMax) xMax = point.mLatitude;
//        	if(point.mLongitude < yMin) yMin = point.mLongitude;
//        	if(point.mLongitude > yMax) yMax = point.mLongitude;
//		}
//        
//        int deltaX = (int)((xMax - xMin)*1E6);
//        int deltaY = (int)((yMax - yMin)*1E6);
//        int centerX = (int)(((xMax + xMin)*1E6)/2);
//        int centerY = (int)(((yMax + yMin)*1E6)/2);
//        
//        mMap.getController().zoomToSpan(Math.abs(deltaX), Math.abs(deltaY));
//        mMap.getController().animateTo(new GeoPoint(centerX,centerY));
//	}
	
}
