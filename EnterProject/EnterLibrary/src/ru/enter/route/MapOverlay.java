package ru.enter.route;

import java.util.ArrayList;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapController;
import com.google.android.maps.MapView;
import com.google.android.maps.Overlay;

//накладывается сверху на карту, отрисовывает наш маршрут
public class MapOverlay extends Overlay {
        Road mRoad;
        ArrayList<GeoPoint> mPoints;
        int minLatitude = Integer.MAX_VALUE;
		int maxLatitude = Integer.MIN_VALUE;
		int minLongitude = Integer.MAX_VALUE;
		int maxLongitude = Integer.MIN_VALUE;
        public MapOverlay(Road road, final MapView mv) {
                mRoad = road;

                if (road.mGeoPoint.size() > 0) {
                        mPoints = new ArrayList<GeoPoint>();
                        for (int i = 0; i < road.mGeoPoint.size(); i++) {
                        	mPoints.add(road.mGeoPoint.get(i));
            				
            				maxLatitude = Math.max(maxLatitude, road.mGeoPoint.get(i).getLatitudeE6());
            				minLatitude = Math.min(minLatitude, road.mGeoPoint.get(i).getLatitudeE6());
            				maxLongitude = Math
            						.max(maxLongitude, road.mGeoPoint.get(i).getLongitudeE6());
            				minLongitude = Math
            						.min(minLongitude, road.mGeoPoint.get(i).getLongitudeE6());
                        }
                        
                        new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
						//mv.postDelayed(new Runnable(){
							@Override
							public void run() {
								 MapController mapController = mv.getController();
								 Log.d("MAP","w="+mv.getWidth() + ",h="+mv.getHeight());
								 	if (mapController!=null){
				            			mapController.zoomToSpan(Math.abs(maxLatitude - minLatitude),Math.abs(maxLongitude - minLongitude));
				            			mapController.animateTo(new GeoPoint((maxLatitude + minLatitude) / 2,(maxLongitude + minLongitude) / 2));
				            			int w=0,h=0;
				            			w = mv.getMeasuredWidth();
				            			h = mv.getMeasuredHeight();
				            			//strange bug that can throw an Exception when mapview is destroyed, but handler fires anyway
				            			if( w>0 && h>0 )
				            				mapController.zoomOut();
			            			}
							}
						},1000);
                       
                }
        }

        @Override
        public boolean draw(Canvas canvas, MapView mv, boolean shadow, long when) {
                super.draw(canvas, mv, shadow);
                drawPath(mv, canvas);
                return true;
        }

        public void drawPath(MapView mv, Canvas canvas) {
                int x1 = -1, y1 = -1, x2 = -1, y2 = -1;
                Paint paint = new Paint();
                paint.setColor(Color.GREEN);
                paint.setStyle(Paint.Style.STROKE);
                paint.setStrokeWidth(3);
                for (int i = 0; i < mPoints.size(); i++) {
                        Point point = new Point();
                        mv.getProjection().toPixels(mPoints.get(i), point);
                        x2 = point.x;
                        y2 = point.y;
                        if (i > 0) {
                                canvas.drawLine(x1, y1, x2, y2, paint);
                        }
                        x1 = x2;
                        y1 = y2;
                }
        }
}     
