package ru.enter.widgets;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.widget.GridView;
/**
 * В xml нужно вместо обычного GridView, указывать <ru.enter.widgets.PinchGridView />
 * пример использования:
 * 
 *     @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mGvTest = (PinchGridView)findViewById(R.id.pinchGridView);
        mGvTest.setNumColumns(3);
        mGvTest.setMinimumColumns(2);
        mGvTest.setMaximumColumns(4);
        mAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, samples);
        mGvTest.setAdapter(mAdapter);
        
    }
 * 
 * @author hotfix
 *
 */
public class PinchGridView extends GridView {

	//private static String TAG = "PinchGridView";
	
	private boolean usePinchDetector = true;
	private int DEFAULT_MIN_COLUMNS = 1;
	private int DEFAULT_MAX_COLUMNS = 10;
	private int mMinimumColumns = 1;
	private int mMaximumColumns = 10;
	
	//ScaleGestureDetector is avaliable since android 2.1
	private ScaleGestureDetector mScaleDetector;
	private float mScaleFactor = 1.0f;
	private float MAX_SCALE = 5.0f;
	private float MIN_SCALE = 0.1f;
	
	private OnGridColumnsChanged mOnColumnsChangedListener;

	public PinchGridView(Context context) {
		this(context, null, 0);
	}

	public PinchGridView(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public PinchGridView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		mScaleDetector = new ScaleGestureDetector(context, new ScaleListener());
	}

	/*
	@Override
	public boolean onTouchEvent(MotionEvent ev) {
		if(usePinchDetector){
			mScaleDetector.onTouchEvent(ev);
		}
		return super.onTouchEvent(ev);
	} */
	
	//fix 2542
	@Override
	public boolean dispatchTouchEvent(MotionEvent ev) {
		if(usePinchDetector){
			mScaleDetector.onTouchEvent(ev);
		}
		return super.dispatchTouchEvent(ev);
	}
	
	private class ScaleListener extends ScaleGestureDetector.SimpleOnScaleGestureListener {
		@Override
		public boolean onScale(ScaleGestureDetector detector) {
			mScaleFactor *= detector.getScaleFactor(); //increasing current scaleFactor
			//scaleFactor must be bounded
			mScaleFactor = Math.max(MIN_SCALE, Math.min(mScaleFactor, MAX_SCALE));
			//calculating numcols with bounds checking
			int targetNumCols = mMaximumColumns - (int)(mScaleFactor*mMaximumColumns / MAX_SCALE);
			if(targetNumCols > mMaximumColumns){ //check max
				targetNumCols = mMaximumColumns;
			}
			if(targetNumCols < mMinimumColumns){ //check in
				targetNumCols = mMinimumColumns;
			}
			
			if(getNumColumns()!=targetNumCols){ //setup new numCols
				setNumColumns(targetNumCols);
			}
			//Log.d(TAG, String.format("Pinch(sc=%f,cols=%d [%d,%d])",mScaleFactor,targetNumCols,mMinimumColumns,mMaximumColumns));
			return true;
		}
	}

	public boolean isUsePinchDetector() {
		return usePinchDetector;
	}

	public void setUsePinchDetector(boolean usePinchDetector) {
		this.usePinchDetector = usePinchDetector;
	}

	public int getMinimumColumns() {
		return mMinimumColumns;
	}

	public void setMinimumColumns(int mMinimumColumns) {
		this.mMinimumColumns = mMinimumColumns > DEFAULT_MIN_COLUMNS ? mMinimumColumns : DEFAULT_MIN_COLUMNS;
	}

	public int getMaximumColumns() {
		return mMaximumColumns;
	}

	public void setMaximumColumns(int mMaximumColumns) {
		if(mMaximumColumns <= mMinimumColumns){
			this.mMaximumColumns = mMinimumColumns + 1;
		}
		else{
			this.mMaximumColumns = Math.min(mMaximumColumns, DEFAULT_MAX_COLUMNS);
		}
	}
	
	@Override
	public void setNumColumns(int numColumns) {
		super.setNumColumns(numColumns);
		if(mOnColumnsChangedListener!=null)
			mOnColumnsChangedListener.onColumnsChanged(numColumns);
	}

	public OnGridColumnsChanged getOnColumnsChangedListener() {
		return mOnColumnsChangedListener;
	}

	public void setOnColumnsChangedListener(OnGridColumnsChanged mOnColumnsChangedListener) {
		this.mOnColumnsChangedListener = mOnColumnsChangedListener;
	}
	
	
		

}