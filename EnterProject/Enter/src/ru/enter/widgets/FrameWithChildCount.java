package ru.enter.widgets;

import ru.enter.utils.Utils;
import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.FrameLayout;

public class FrameWithChildCount extends FrameLayout{

	private static final int CHILD_SIZE_DP = 70; //размер элемента в строчке грида в списке товаров
	private static final int MIN_VERTICAL_SPACE_DP = CHILD_SIZE_DP / 14;
	
	private static final int CALCULATE_ERROR_PX = 2; //приблизительная погрешность при расчетах 
	
	private int mChildSizePx;
	private int mMinVerticalSpace;
	
	private OnChildCalculateListener mListener;
	
	//расстояние по вертикали между элементами
	private int mVerticalSpacing;
	//кол-во элементов
	private int mChildrenCount;
	
	public FrameWithChildCount(Context context, AttributeSet attrs) {
		super(context, attrs);
		mChildSizePx = Utils.dp2pix(getContext(), CHILD_SIZE_DP);
		mMinVerticalSpace = Utils.dp2pix(getContext(), MIN_VERTICAL_SPACE_DP);
	}
	
	@Override
	protected void onSizeChanged (int w, int h, int oldw, int oldh) {
		super.onSizeChanged(w, h, oldw, oldh);
		if (mListener != null) {
			
			calculateVerticalSpacing(h, h / mChildSizePx);
			mListener.onChildCountCalculated(mChildrenCount, mVerticalSpacing - CALCULATE_ERROR_PX);
		}
		
	}
	
	private void calculateVerticalSpacing (int heightAll, int childrenCount) {
		int allSpace = heightAll - (childrenCount * mChildSizePx);
		int verticalSpacing = allSpace / (childrenCount + 1);
		
		if (verticalSpacing > mMinVerticalSpace) {
			mVerticalSpacing = verticalSpacing;
			mChildrenCount = childrenCount;
		} else {
			calculateVerticalSpacing(heightAll, childrenCount - 1);
		}
	}
	
	public static interface OnChildCalculateListener {
		void onChildCountCalculated (int childCount, int verticalSpacing);
	}
	
	public void setOnChildCalculateListener (OnChildCalculateListener listener) {
		mListener = listener;
	}

}
