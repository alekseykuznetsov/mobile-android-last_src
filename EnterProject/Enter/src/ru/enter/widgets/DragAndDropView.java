package ru.enter.widgets;

import ru.enter.utils.Log;
import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.PixelFormat;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;

public class DragAndDropView extends GridView {

	private static final int LONG_CLICK = 0;
	private static final int CLICK = 1;

	private WindowManager mWm;
	private WindowManager.LayoutParams mWindowParams;
	private Bitmap mDragBitmap;
	private ImageView mDragView;
	private int mFromPosition;
	private int mLongClickDelay;
	private int mFromX, mFromY;
	private DragAndDropListener mListener;
	private boolean dragging;
	private static boolean isMoving;

	public DragAndDropView(Context context) {
		super(context);
	}

	public DragAndDropView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	@SuppressWarnings ("static-access")
	public DragAndDropView(Context context, AttributeSet attrs) {
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

		ViewConfiguration vc = ViewConfiguration.get(context);
		mLongClickDelay = vc.getLongPressTimeout();
	}

	public void setOnDragnDropListener (DragAndDropListener listener) {
		mListener = listener;
	}

	public static void setMoving () {
		isMoving = true;
	}

	Handler mClickHandler = new Handler() {
		public void handleMessage (Message msg) {
			switch (msg.what) {
			case LONG_CLICK:
				Log.d("DRAG", "STARTING DRAG");
				getParent().requestDisallowInterceptTouchEvent(true);
				startDrag();
				if (mListener != null)
					mListener.onStartDrag(mFromPosition);
				drag(mFromX, mFromY);
				break;
			case CLICK:
				if (hasMessages(LONG_CLICK)) {
					removeMessages(LONG_CLICK);
					if (mListener != null)
						mListener.onClick(mFromPosition);
				}
				break;

			default:
				break;
			}
		};
	};

	@Override
	public boolean onTouchEvent (MotionEvent event) {

		if (!dragging && isMoving)
			mClickHandler.removeMessages(LONG_CLICK);

		int action = event.getAction();

		if (action == MotionEvent.ACTION_DOWN) {
			mFromX = (int) event.getX();
			mFromY = (int) event.getY();
			mFromPosition = pointToPosition(mFromX, mFromY);
			if (mFromPosition == AdapterView.INVALID_POSITION)
				return false;
			mClickHandler.sendEmptyMessageDelayed(LONG_CLICK, mLongClickDelay);
			return true;

		} else if (action == MotionEvent.ACTION_MOVE) {
			int toX = (int) event.getX();
			int toY = (int) event.getY();
			if (dragging) {
				drag(toX, toY);
				if (mListener != null)
					mListener.onDrag(mFromPosition, toX, toY);
			}
			return true;

		} else if (action == MotionEvent.ACTION_UP) {
			isMoving = false;
			mClickHandler.sendEmptyMessage(CLICK);
			if (mListener != null && dragging) {
				mListener.onStopDrag(mFromPosition, (int) event.getRawX(), (int) event.getRawY()); // Callback
			}
			endDrag();
			return true;

		} else if (action == MotionEvent.ACTION_CANCEL || action == MotionEvent.ACTION_OUTSIDE) {
			isMoving = false;
			endDrag();
			return true;
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
