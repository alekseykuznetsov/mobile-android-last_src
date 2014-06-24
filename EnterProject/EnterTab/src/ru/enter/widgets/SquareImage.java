package ru.enter.widgets;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ImageView;

public class SquareImage extends ImageView{
	
	public SquareImage(Context context) {
		this(context, null);
	}
	
	public SquareImage(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}
	
	public SquareImage(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs);
	}
	
	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		int width = MeasureSpec.getSize(widthMeasureSpec);
		setMeasuredDimension(width, width);
	}

}
