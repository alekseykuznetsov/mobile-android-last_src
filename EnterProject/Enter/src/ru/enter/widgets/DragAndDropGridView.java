package ru.enter.widgets;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.PixelFormat;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ListView;

//public class DragAndDropGridView extends GridView {
public class DragAndDropGridView extends ListView {

	private WindowManager mWm;
	private WindowManager.LayoutParams mWindowParams;
	private Bitmap mDragBitmap;
	private ImageView mDragView;
	private int mFromPosition;
	private int mFromX, mFromY;
	private DragAndDropListenerNew mListener;
	private boolean dragging;
	private static boolean isMoving;

	public DragAndDropGridView(Context context) {
		super(context);
	}

	public DragAndDropGridView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	@SuppressWarnings ("static-access")
	public DragAndDropGridView(Context context, AttributeSet attrs) {
		this(context, attrs, 0);

		Activity p = ((Activity) context).getParent();
		if (p == null)
			p = (Activity) context;
		mWm = (WindowManager) p.getSystemService(Context.WINDOW_SERVICE);

		mWindowParams = new WindowManager.LayoutParams();
		mWindowParams.gravity = Gravity.TOP | Gravity.LEFT;
		mWindowParams.height = WindowManager.LayoutParams.WRAP_CONTENT;
		mWindowParams.width = WindowManager.LayoutParams.WRAP_CONTENT;
		mWindowParams.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE | WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE
				| WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON | WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS;
		mWindowParams.format = PixelFormat.TRANSLUCENT;
		mWindowParams.windowAnimations = 0;
		mWindowParams.x = 0;
		mWindowParams.y = 0;
	}

	public void setOnDragnDropListener (DragAndDropListenerNew listener) {
		mListener = listener;
	}

	public static void setMoving () {
		isMoving = true;
	}
	
	public void startDaD() {
		getParent().requestDisallowInterceptTouchEvent(true);
		startDrag();
		if (mListener != null)
			mListener.onStartDrag(mFromPosition);
		drag(mFromX, mFromY);
	}
	

	@Override
	public boolean onTouchEvent(MotionEvent event) {

		int action = event.getAction();

		if (action == MotionEvent.ACTION_DOWN) {
			mFromX = (int) event.getX();
			mFromY = (int) event.getY();
			mFromPosition = pointToPosition(mFromX, mFromY);
			
		} else if (action == MotionEvent.ACTION_MOVE) {
			int toX = (int) event.getX();
			int toY = (int) event.getY();
			if (dragging) {
				drag(toX, toY);
				if (mListener != null)
					mListener.onDrag(mFromPosition, toX, toY);
				return true;
			}

		} else if (action == MotionEvent.ACTION_UP) {
			isMoving = false;
			if (dragging) {
				if (mListener != null) {
					mListener.onStopDrag(mFromPosition, (int) event.getRawX(),
							(int) event.getRawY()); // Callback
				}
				endDrag();
				return true;
			}

		} else if (action == MotionEvent.ACTION_CANCEL
				|| action == MotionEvent.ACTION_OUTSIDE) {
			isMoving = false;
			if (dragging) {
				endDrag();
				return true;
			}
		}

		return super.onTouchEvent(event);
	}

	private void startDrag () {
		dragging = true;
		View view = getChildByPosition(mFromPosition);
		mDragBitmap = Bitmap.createBitmap(view.getWidth(), view.getHeight(), Bitmap.Config.ARGB_8888);
		Canvas canvas = new Canvas();
		canvas.setBitmap(mDragBitmap);
		view.draw(canvas);

		if (mDragView != null) {
			mWm.removeView(mDragView);
		}

		mDragView = new ImageView(getContext());
		mDragView.setImageBitmap(mDragBitmap);
		mDragView.setTag(view.getTag());

		mWm.addView(mDragView, mWindowParams);
	}

	private View getChildByPosition (int position) {
		return getChildAt(position - getFirstVisiblePosition());
	}

	private void drag (int x, int y) {
		if (!dragging)
			return;
		mWindowParams.x = x - mDragBitmap.getWidth() / 2;
		mWindowParams.y = y;

		mWm.updateViewLayout(mDragView, mWindowParams);
	}

	private void endDrag () {
		if (!dragging)
			return;
		dragging = false;
		mWm.removeView(mDragView);
		mDragView = null;
		mDragBitmap = null;
	}

}
