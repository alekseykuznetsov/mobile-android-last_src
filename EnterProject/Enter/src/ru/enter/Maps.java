package ru.enter;

import java.util.ArrayList;
import java.util.List;

import javax.microedition.khronos.opengles.GL;

import ru.enter.Listeners.LoadListener;
import ru.enter.beans.ShopBean;
import ru.enter.route.BaloonItem;
import ru.enter.utils.Constants;
import ru.enter.utils.CustomMapOverlay;
import ru.enter.utils.Log;
import ru.enter.utils.MapCustomView;
import ru.enter.utils.MapItem;
import ru.enter.utils.OnBalloonTap;
import ru.enter.utils.Utils;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.DrawFilter;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Picture;
import android.graphics.PorterDuff;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Region;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.flurry.android.FlurryAgent;
import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapActivity;
import com.google.android.maps.MapController;
import com.google.android.maps.MapView;
import com.google.android.maps.MyLocationOverlay;
import com.google.android.maps.Overlay;

public class Maps extends MapActivity implements OnBalloonTap {

	private static final String MAP_API_KEY = Utils.getGeoApi();
	private RotateView mRotateView;
	private MapCustomView mMapView;
	private MyLocationOverlay mMyLocationOverlay;
	private ArrayList<GeoPoint> points;
	private int WAIT = 2000;
	private List<Overlay> mapOverlays;
	private Drawable drawable;
	private BaloonItem baloonItem;
	public static final String LATITUDE = "LATITUDE";
	public static final String LONGITUDE = "LONGITUDE";
	public static final String FORWARD = "FORWARD";
	public static final String STATE = "STATE";
	private String lat_ext;
	private String lon_ext;
	private boolean forward = false;

	public enum State {
		shop, shopcheck, other
	};

	private State state = State.other;

	private Double currShopLat, currShopLong;
	private boolean fromMapRoute = false;

	private class RotateView extends ViewGroup {
		private static final float SQ2 = 1.414213562373095f;
		private final SmoothCanvas mCanvas = new SmoothCanvas();

		public RotateView(Context context) {
			super(context);
		}

		@Override
		protected void dispatchDraw(Canvas canvas) {
			canvas.save(Canvas.MATRIX_SAVE_FLAG);
			mCanvas.delegate = canvas;

			super.dispatchDraw(mCanvas);
			canvas.restore();
		}

		@Override
		protected void onLayout(boolean changed, int l, int t, int r, int b) {
			final int width = getWidth();
			final int height = getHeight();
			final int count = getChildCount();
			for (int i = 0; i < count; i++) {
				final View view = getChildAt(i);
				final int childWidth = view.getMeasuredWidth();
				final int childHeight = view.getMeasuredHeight();
				final int childLeft = (width - childWidth) / 2;
				final int childTop = (height - childHeight) / 2;
				view.layout(childLeft, childTop, childLeft + childWidth,
						childTop + childHeight);
			}
		}

		@Override
		protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
			int w = getDefaultSize(getSuggestedMinimumWidth(), widthMeasureSpec);
			int h = getDefaultSize(getSuggestedMinimumHeight(),
					heightMeasureSpec);
			int sizeSpec;
			if (w > h) {
				sizeSpec = MeasureSpec.makeMeasureSpec((int) (w * SQ2),
						MeasureSpec.EXACTLY);
			} else {
				sizeSpec = MeasureSpec.makeMeasureSpec((int) (h * SQ2),
						MeasureSpec.EXACTLY);
			}
			final int count = getChildCount();
			for (int i = 0; i < count; i++) {
				getChildAt(i).measure(sizeSpec, sizeSpec);
			}
			super.onMeasure(widthMeasureSpec, heightMeasureSpec);
		}

		@Override
		public boolean dispatchTouchEvent(MotionEvent ev) {
			invalidate();
			return super.dispatchTouchEvent(ev);
		}
	}

	private Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			boolean isMy = true;
			GeoPoint my = mMyLocationOverlay.getMyLocation();
			if (my != null)
				points.add(my);
			else
				isMy = false;
			int minLatitude = Integer.MAX_VALUE;
			int maxLatitude = Integer.MIN_VALUE;
			int minLongitude = Integer.MAX_VALUE;
			int maxLongitude = Integer.MIN_VALUE;

			if (isMy) {

				for (int i = 0; i < points.size(); i++) {
					maxLatitude = Math.max(
							maxLatitude,
							Math.abs(points.get(i).getLatitudeE6()
									- my.getLatitudeE6()));
					minLatitude = Math.min(
							minLatitude,
							Math.abs(points.get(i).getLatitudeE6()
									- my.getLatitudeE6()));
					maxLongitude = Math.max(
							maxLongitude,
							Math.abs(points.get(i).getLongitudeE6()
									- my.getLongitudeE6()));
					minLongitude = Math.min(
							minLongitude,
							Math.abs(points.get(i).getLongitudeE6()
									- my.getLongitudeE6()));
				}
				/*
				 * int w=0,h=0; w = mv.getMeasuredWidth(); h =
				 * mv.getMeasuredHeight(); //strange bug that can throw an
				 * Exception when mapview is destroyed, but handler fires anyway
				 * if( w>0 && h>0 ) mapController.zoomOut();
				 */
				if (mMapView != null) {

					MapController controller = mMapView.getController();
					if (controller != null) {
						//controller.setZoom(getZoom(mMapView, my, points));
						// int
						// w=mMapView.getLatitudeSpan(),h=mMapView.getLongitudeSpan(),zoom=mMapView.getZoomLevel();
						controller.zoomToSpan(
								4 * Math.abs(maxLatitude - minLatitude),
								4 * Math.abs(maxLongitude - minLongitude));
						controller.animateTo(my);

						// int w=0,h=0;
						// w = mMapView.getMeasuredWidth();
						// h = mMapView.getMeasuredHeight();
						// // //strange bug that can throw an Exception when
						// mapview is destroyed, but handler fires anyway
						// if( w>0 && h>0 )
						// controller.zoomOut();
					}
				}

			} else {
				for (int i = 0; i < points.size(); i++)
					for (int j = i; j < points.size(); j++) {
						maxLatitude = Math.max(maxLatitude, Math.max(points
								.get(i).getLatitudeE6(), points.get(j)
								.getLatitudeE6()));
						minLatitude = Math.min(minLatitude, Math.min(points
								.get(i).getLatitudeE6(), points.get(j)
								.getLatitudeE6()));
						maxLongitude = Math.max(maxLongitude, Math.max(points
								.get(i).getLongitudeE6(), points.get(j)
								.getLongitudeE6()));
						minLongitude = Math.min(minLongitude, Math.min(points
								.get(i).getLongitudeE6(), points.get(j)
								.getLongitudeE6()));
					}
				/*
				 * int w=0,h=0; w = mv.getMeasuredWidth(); h =
				 * mv.getMeasuredHeight(); //strange bug that can throw an
				 * Exception when mapview is destroyed, but handler fires anyway
				 * if( w>0 && h>0 ) mapController.zoomOut();
				 */
				if (mMapView != null) {

					MapController controller = mMapView.getController();
					if (controller != null) {
						GeoPoint center = new GeoPoint(
								(maxLatitude + minLatitude) / 2,
								(maxLongitude + minLongitude) / 2);
						// controller.setZoom(getZoom(mMapView,center,points));
						// int
						// w=mMapView.getLatitudeSpan(),h=mMapView.getLongitudeSpan(),zoom=mMapView.getZoomLevel();
						controller.zoomToSpan(
								2*Math.abs(maxLatitude - minLatitude),
								2*Math.abs(maxLongitude - minLongitude));
						controller.animateTo(center);

						// int w=0,h=0;
						// w = mMapView.getMeasuredWidth();
						// h = mMapView.getMeasuredHeight();
						// // //strange bug that can throw an Exception when
						// mapview is destroyed, but handler fires anyway
						// if( w>0 && h>0 )
						// controller.zoomOut();
					}
				}
				// controller.zoomOut();
			}
		}
	};
/*
	// Функция вычисляет какое должно быть приближение на карте
	private int getZoom(MapView map, GeoPoint center,
			ArrayList<GeoPoint> listPoint) {
		int zoom = 0;
		int lat = center.getLatitudeE6(), lng = center.getLongitudeE6();
		int farestLat = 0, farestLng = 0;
		for (int i = 0; i < listPoint.size(); i++) {
			int flagLatDistance = Math.abs(listPoint.get(i).getLatitudeE6()
					- lat);
			if (farestLat < flagLatDistance)
				farestLat = flagLatDistance;

			int flagLngDistance = Math.abs(listPoint.get(i).getLongitudeE6()
					- lng);
			if (farestLng < flagLngDistance)
				farestLng = flagLngDistance;
		}
		// compute how many times this screen we are far on lat
		float latQuotient = (float) farestLat
				/ ((float) map.getLatitudeSpan() / 2);
		// compute how many times this screen we are far on lng
		float lngQuotient = (float) farestLng
				/ ((float) map.getLongitudeSpan() / 2);

		if (latQuotient > 1 || lngQuotient > 1) {
			// must zoom out
			float qutient = Math.max((int) latQuotient, (int) lngQuotient);
			while ((qutient / 2) > 1) {
				qutient = qutient / 2;
				zoom--;
			}
		} else {
			float qutient = Math.max((int) (1 / (float) latQuotient),
					(int) (1 / (float) lngQuotient));
			while ((qutient / 2) > 1) {
				qutient = qutient / 2;
				zoom++;
			}

		}
		zoom += map.getZoomLevel();
		if (zoom < 1)
			zoom = 1;
		if (zoom > 21)
			zoom = 21;
		return (zoom);
	}
*/
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		mRotateView = new RotateView(this);
		mMapView = new MapCustomView(this, MAP_API_KEY);
		mRotateView.addView(mMapView);
		setContentView(mRotateView);

		mMyLocationOverlay = new MyLocationOverlay(this, mMapView);
		mMapView.getOverlays().add(mMyLocationOverlay);
		mMapView.setBuiltInZoomControls(false);
		mMapView.setClickable(true);
		mMapView.setEnabled(true);

		BaloonItem.setOnBalloopTap(this);
		MapItem.setOnBalloopTap(this);

		Bundle bundle = getIntent().getExtras();
		if (bundle != null) {
			lat_ext = bundle.getString(LATITUDE);
			lon_ext = bundle.getString(LONGITUDE);
			forward = bundle.getBoolean(FORWARD);
			state = State.valueOf(bundle.getString(STATE) != null ? bundle
					.getString(STATE) : "other");

		}
		mapOverlays = mMapView.getOverlays();
		drawable = this.getResources().getDrawable(R.drawable.icon_pin);
		baloonItem = new BaloonItem(drawable, mMapView);

		switch (state) {
		case other:
			CreateData();
			break;
		case shop:
			// if(ShopsActivity.shopBeans == null)
			if (ShopsActivity.shopBeans.isEmpty()) {
				ShopsActivity.setLoadListener(new LoadListener() {

					@Override
					public void isDone(boolean done) {
						CreateData();
					}
				});
			} else
				CreateData();
			break;
		case shopcheck:
			if (ShopsListWithCheckActivity.bean == null) {
				ShopsListWithCheckActivity.setLoadListener(new LoadListener() {

					@Override
					public void isDone(boolean done) {
						CreateData();
					}
				});
			} else
				CreateData();
			break;

		default:
			break;
		}

	}

	private void CreateData() {
		points = new ArrayList<GeoPoint>();
		int lat;
		int lon;
		GeoPoint point;
		CustomMapOverlay overlayitem;
		switch (state) {
		// непонятный кейс, я его немного попилил в CustomMapOverlay
		case other:
			lat = (int) (Float.parseFloat(lat_ext) * 1E6);
			lon = (int) (Float.parseFloat(lon_ext) * 1E6);
			point = new GeoPoint(lat, lon);
			overlayitem = new CustomMapOverlay(point, "", 0, this, false);
			baloonItem.addOverlay(overlayitem);
			baloonItem.setText("Подробнее");
			points.add(point);
			Log.i("test", "2");
			break;
		case shop:
			for (ShopBean b : ShopsActivity.shopBeans) {
				lat = (int) (Float.parseFloat(b.getLatitude()) * 1E6);
				lon = (int) (Float.parseFloat(b.getLongitude()) * 1E6);
				point = new GeoPoint(lat, lon);
				overlayitem = new CustomMapOverlay(point, b.getName(),
						b.getId(), this, forward);
				baloonItem.addOverlay(overlayitem);
				baloonItem.setText("Подробнее");
				points.add(point);
				Log.i("test", "1");
			}
			break;
		case shopcheck:
			// пришли из геолокации на первом шаге заказа товара
			for (ShopBean b : ShopsListWithCheckActivity.bean) {
				lat = (int) (Float.parseFloat(b.getLatitude()) * 1E6);
				lon = (int) (Float.parseFloat(b.getLongitude()) * 1E6);
				point = new GeoPoint(lat, lon);
				overlayitem = new CustomMapOverlay(point, b.getName(),
						b.getId(), this, false);
				baloonItem.addOverlay(overlayitem);
				baloonItem.setText("Выбрать");
				points.add(point);
				Log.i("test", "1");
			}
			break;

		default:
			break;
		}
		mapOverlays.add(baloonItem);
		mMyLocationOverlay.runOnFirstFix(new Runnable() {
			public void run() {
				handler.sendEmptyMessageDelayed(0, WAIT);
			}
		});
	}

	@Override
	protected void onResume() {
		super.onResume();
		mMyLocationOverlay.enableMyLocation();
		// if user return from MapRoute map will be animated to last tapped
		// shop, fix 2485
		if (fromMapRoute) {
			try {
				mMapView.getController().animateTo(
						new GeoPoint((int) (currShopLat * 1e6),
								(int) (currShopLong * 1e6)));
			} catch (Exception e) {
			}
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
		// if(mMyLocationOverlay!=null)
		FlurryAgent.onEndSession(this);
		mMyLocationOverlay.disableMyLocation();
		super.onStop();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		mMapView.cleanUpMemory(); // rescue from OOM exception
		// mMapView.getTileProvider().clearTileCache();
		System.gc();
	}

	public void showMe() {
		GeoPoint me;
		if (mMapView != null) {
			MapController controller = mMapView.getController();
			if (controller != null) {
				me = mMyLocationOverlay.getMyLocation();
				if (me != null) {
					int minLatitude = Integer.MAX_VALUE;
					int maxLatitude = Integer.MIN_VALUE;
					int minLongitude = Integer.MAX_VALUE;
					int maxLongitude = Integer.MIN_VALUE;

					for (int i = 0; i < points.size(); i++) {
						maxLatitude = Math.max(
								maxLatitude,
								Math.abs(points.get(i).getLatitudeE6()
										- me.getLatitudeE6()));
						minLatitude = Math.min(
								minLatitude,
								Math.abs(points.get(i).getLatitudeE6()
										- me.getLatitudeE6()));
						maxLongitude = Math.max(
								maxLongitude,
								Math.abs(points.get(i).getLongitudeE6()
										- me.getLongitudeE6()));
						minLongitude = Math.min(
								minLongitude,
								Math.abs(points.get(i).getLongitudeE6()
										- me.getLongitudeE6()));
					}
					controller.zoomToSpan(
							4 * Math.abs(maxLatitude - minLatitude),
							4 * Math.abs(maxLongitude - minLongitude));
					controller.animateTo(me);
				}

			}
		}
	}

	@Override
	protected boolean isRouteDisplayed() {
		return false;
	}

	static final class SmoothCanvas extends Canvas {
		Canvas delegate;

		private final Paint mSmooth = new Paint(Paint.FILTER_BITMAP_FLAG);

		public void setBitmap(Bitmap bitmap) {
			delegate.setBitmap(bitmap);
		}

		public void setViewport(int width, int height) {
			((SmoothCanvas) delegate).setViewport(width, height);
		}

		public boolean isOpaque() {
			return delegate.isOpaque();
		}

		public int getWidth() {
			return delegate.getWidth();
		}

		public int getHeight() {
			return delegate.getHeight();
		}

		public int save() {
			return delegate.save();
		}

		public int save(int saveFlags) {
			return delegate.save(saveFlags);
		}

		public int saveLayer(RectF bounds, Paint paint, int saveFlags) {
			return delegate.saveLayer(bounds, paint, saveFlags);
		}

		public int saveLayer(float left, float top, float right, float bottom,
				Paint paint, int saveFlags) {
			return delegate.saveLayer(left, top, right, bottom, paint,
					saveFlags);
		}

		public int saveLayerAlpha(RectF bounds, int alpha, int saveFlags) {
			return delegate.saveLayerAlpha(bounds, alpha, saveFlags);
		}

		public int saveLayerAlpha(float left, float top, float right,
				float bottom, int alpha, int saveFlags) {
			return delegate.saveLayerAlpha(left, top, right, bottom, alpha,
					saveFlags);
		}

		public void restore() {
			delegate.restore();
		}

		public int getSaveCount() {
			return delegate.getSaveCount();
		}

		public void restoreToCount(int saveCount) {
			delegate.restoreToCount(saveCount);
		}

		public void translate(float dx, float dy) {
			delegate.translate(dx, dy);
		}

		public void scale(float sx, float sy) {
			delegate.scale(sx, sy);
		}

		public void rotate(float degrees) {
			delegate.rotate(degrees);
		}

		public void skew(float sx, float sy) {
			delegate.skew(sx, sy);
		}

		public void concat(Matrix matrix) {
			delegate.concat(matrix);
		}

		public void setMatrix(Matrix matrix) {
			delegate.setMatrix(matrix);
		}

		public void getMatrix(Matrix ctm) {
			delegate.getMatrix(ctm);
		}

		public boolean clipRect(RectF rect, Region.Op op) {
			return delegate.clipRect(rect, op);
		}

		public boolean clipRect(Rect rect, Region.Op op) {
			return delegate.clipRect(rect, op);
		}

		public boolean clipRect(RectF rect) {
			return delegate.clipRect(rect);
		}

		public boolean clipRect(Rect rect) {
			return delegate.clipRect(rect);
		}

		public boolean clipRect(float left, float top, float right,
				float bottom, Region.Op op) {
			return delegate.clipRect(left, top, right, bottom, op);
		}

		public boolean clipRect(float left, float top, float right, float bottom) {
			return delegate.clipRect(left, top, right, bottom);
		}

		public boolean clipRect(int left, int top, int right, int bottom) {
			return delegate.clipRect(left, top, right, bottom);
		}

		public boolean clipPath(Path path, Region.Op op) {
			return delegate.clipPath(path, op);
		}

		public boolean clipPath(Path path) {
			return delegate.clipPath(path);
		}

		public boolean clipRegion(Region region, Region.Op op) {
			return delegate.clipRegion(region, op);
		}

		public boolean clipRegion(Region region) {
			return delegate.clipRegion(region);
		}

		public DrawFilter getDrawFilter() {
			return delegate.getDrawFilter();
		}

		public void setDrawFilter(DrawFilter filter) {
			delegate.setDrawFilter(filter);
		}

		public GL getGL() {
			return ((SmoothCanvas) delegate).getGL();
		}

		public boolean quickReject(RectF rect, EdgeType type) {
			return delegate.quickReject(rect, type);
		}

		public boolean quickReject(Path path, EdgeType type) {
			return delegate.quickReject(path, type);
		}

		public boolean quickReject(float left, float top, float right,
				float bottom, EdgeType type) {
			return delegate.quickReject(left, top, right, bottom, type);
		}

		public boolean getClipBounds(Rect bounds) {
			return delegate.getClipBounds(bounds);
		}

		public void drawRGB(int r, int g, int b) {
			delegate.drawRGB(r, g, b);
		}

		public void drawARGB(int a, int r, int g, int b) {
			delegate.drawARGB(a, r, g, b);
		}

		public void drawColor(int color) {
			delegate.drawColor(color);
		}

		public void drawColor(int color, PorterDuff.Mode mode) {
			delegate.drawColor(color, mode);
		}

		public void drawPaint(Paint paint) {
			delegate.drawPaint(paint);
		}

		public void drawPoints(float[] pts, int offset, int count, Paint paint) {
			delegate.drawPoints(pts, offset, count, paint);
		}

		public void drawPoints(float[] pts, Paint paint) {
			delegate.drawPoints(pts, paint);
		}

		public void drawPoint(float x, float y, Paint paint) {
			delegate.drawPoint(x, y, paint);
		}

		public void drawLine(float startX, float startY, float stopX,
				float stopY, Paint paint) {
			delegate.drawLine(startX, startY, stopX, stopY, paint);
		}

		public void drawLines(float[] pts, int offset, int count, Paint paint) {
			delegate.drawLines(pts, offset, count, paint);
		}

		public void drawLines(float[] pts, Paint paint) {
			delegate.drawLines(pts, paint);
		}

		public void drawRect(RectF rect, Paint paint) {
			delegate.drawRect(rect, paint);
		}

		public void drawRect(Rect r, Paint paint) {
			delegate.drawRect(r, paint);
		}

		public void drawRect(float left, float top, float right, float bottom,
				Paint paint) {
			delegate.drawRect(left, top, right, bottom, paint);
		}

		public void drawOval(RectF oval, Paint paint) {
			delegate.drawOval(oval, paint);
		}

		public void drawCircle(float cx, float cy, float radius, Paint paint) {
			delegate.drawCircle(cx, cy, radius, paint);
		}

		public void drawArc(RectF oval, float startAngle, float sweepAngle,
				boolean useCenter, Paint paint) {
			delegate.drawArc(oval, startAngle, sweepAngle, useCenter, paint);
		}

		public void drawRoundRect(RectF rect, float rx, float ry, Paint paint) {
			delegate.drawRoundRect(rect, rx, ry, paint);
		}

		public void drawPath(Path path, Paint paint) {
			delegate.drawPath(path, paint);
		}

		public void drawBitmap(Bitmap bitmap, float left, float top, Paint paint) {
			if (paint == null) {
				paint = mSmooth;
			} else {
				paint.setFilterBitmap(true);
			}
			delegate.drawBitmap(bitmap, left, top, paint);
		}

		public void drawBitmap(Bitmap bitmap, Rect src, RectF dst, Paint paint) {
			if (paint == null) {
				paint = mSmooth;
			} else {
				paint.setFilterBitmap(true);
			}
			delegate.drawBitmap(bitmap, src, dst, paint);
		}

		public void drawBitmap(Bitmap bitmap, Rect src, Rect dst, Paint paint) {
			if (paint == null) {
				paint = mSmooth;
			} else {
				paint.setFilterBitmap(true);
			}
			delegate.drawBitmap(bitmap, src, dst, paint);
		}

		public void drawBitmap(int[] colors, int offset, int stride, int x,
				int y, int width, int height, boolean hasAlpha, Paint paint) {
			if (paint == null) {
				paint = mSmooth;
			} else {
				paint.setFilterBitmap(true);
			}
			delegate.drawBitmap(colors, offset, stride, x, y, width, height,
					hasAlpha, paint);
		}

		public void drawBitmap(Bitmap bitmap, Matrix matrix, Paint paint) {
			if (paint == null) {
				paint = mSmooth;
			} else {
				paint.setFilterBitmap(true);
			}
			delegate.drawBitmap(bitmap, matrix, paint);
		}

		public void drawBitmapMesh(Bitmap bitmap, int meshWidth,
				int meshHeight, float[] verts, int vertOffset, int[] colors,
				int colorOffset, Paint paint) {
			delegate.drawBitmapMesh(bitmap, meshWidth, meshHeight, verts,
					vertOffset, colors, colorOffset, paint);
		}

		public void drawVertices(VertexMode mode, int vertexCount,
				float[] verts, int vertOffset, float[] texs, int texOffset,
				int[] colors, int colorOffset, short[] indices,
				int indexOffset, int indexCount, Paint paint) {
			delegate.drawVertices(mode, vertexCount, verts, vertOffset, texs,
					texOffset, colors, colorOffset, indices, indexOffset,
					indexCount, paint);
		}

		public void drawText(char[] text, int index, int count, float x,
				float y, Paint paint) {
			delegate.drawText(text, index, count, x, y, paint);
		}

		public void drawText(String text, float x, float y, Paint paint) {
			delegate.drawText(text, x, y, paint);
		}

		public void drawText(String text, int start, int end, float x, float y,
				Paint paint) {
			delegate.drawText(text, start, end, x, y, paint);
		}

		public void drawText(CharSequence text, int start, int end, float x,
				float y, Paint paint) {
			delegate.drawText(text, start, end, x, y, paint);
		}

		public void drawPosText(char[] text, int index, int count, float[] pos,
				Paint paint) {
			delegate.drawPosText(text, index, count, pos, paint);
		}

		public void drawPosText(String text, float[] pos, Paint paint) {
			delegate.drawPosText(text, pos, paint);
		}

		public void drawTextOnPath(char[] text, int index, int count,
				Path path, float hOffset, float vOffset, Paint paint) {
			delegate.drawTextOnPath(text, index, count, path, hOffset, vOffset,
					paint);
		}

		public void drawTextOnPath(String text, Path path, float hOffset,
				float vOffset, Paint paint) {
			delegate.drawTextOnPath(text, path, hOffset, vOffset, paint);
		}

		public void drawPicture(Picture picture) {
			delegate.drawPicture(picture);
		}

		public void drawPicture(Picture picture, RectF dst) {
			delegate.drawPicture(picture, dst);
		}

		public void drawPicture(Picture picture, Rect dst) {
			delegate.drawPicture(picture, dst);
		}
	}

	@Override
	public void showOnBalloonTap(Context context, String title, String lat,
			String longitude) {
		Intent intent = new Intent().setClass(context, ShopCard.class);
		intent.putExtra(ShopCard.SHOP_ID, Integer.parseInt(title));
		intent.putExtra(Constants.Maps_LATITUDE, lat);
		intent.putExtra(Constants.Maps_LONGITUDE, longitude);
		// saving last tapped shop info
		currShopLat = Double.parseDouble(lat);
		currShopLong = Double.parseDouble(longitude);
		fromMapRoute = true;
		context.startActivity(intent);

	}

	@Override
	public void onLowMemory() {
		super.onLowMemory();
		mMapView.cleanUpMemory(); // rescue from OOM exception
	}
}
