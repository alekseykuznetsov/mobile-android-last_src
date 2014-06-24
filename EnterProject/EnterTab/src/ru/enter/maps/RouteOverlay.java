package ru.enter.maps;

import java.util.ArrayList;
import java.util.List;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapView;
import com.google.android.maps.Overlay;

public class RouteOverlay extends Overlay {
	
	private ArrayList<GeoPoint> mPoints = new ArrayList<GeoPoint>();
	private Paint mPaint;
	private int mShopId;

	
	public RouteOverlay(String poly,int shopId) {
		mShopId = shopId;
		List<GeoPoint> lst = PolylineDecoder.decodePoints(poly);
		mPoints.addAll(lst);
		createPaint();
	}

	public RouteOverlay(List<GeoPoint> points,int shopId) {
		mShopId = shopId;
		mPoints.addAll(points);
		createPaint();
	}
	
	private void createPaint(){
		mPaint = new Paint();
		mPaint.setColor(Color.BLUE);
		mPaint.setStyle(Paint.Style.STROKE);
		mPaint.setStrokeWidth(4);
	}
	
	public int getShopId() {
		return mShopId;
	}

	@Override
	public void draw(Canvas canvas, MapView mapView, boolean shadow) {
		drawPath(mapView, canvas);
	}

	private void drawPath(MapView mv, Canvas canvas) {
		int x1 = -1;
		int y1 = -1;
		int x2 = -1;
		int y2 = -1;
		Point point = new Point();
		GeoPoint gp;
		for (int i = 0; i < mPoints.size(); i++) {
			gp = mPoints.get(i);
			mv.getProjection().toPixels(gp, point);
			x2 = point.x;
			y2 = point.y;
			if (i > 0) canvas.drawLine(x1, y1, x2, y2, mPaint);
			x1 = x2;
			y1 = y2;
		}
	}
}