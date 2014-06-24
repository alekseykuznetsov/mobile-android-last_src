package ru.enter.animations;

import android.view.View;
import android.view.animation.Animation;
import android.view.animation.Transformation;
import android.widget.RelativeLayout;

// @formatter:off
public class ListSlideFromLeftAnimation extends Animation {
	
	protected View mView;			
	
	protected float leftOffset;

	
	public ListSlideFromLeftAnimation (View v, float leftOffset) {
		mView = v;
		this.leftOffset = leftOffset;
		setFillEnabled(true);
		setFillAfter(true);
	}

	@Override
	protected void applyTransformation (float interpolatedTime,Transformation t) {
		RelativeLayout.LayoutParams rlParams = (RelativeLayout.LayoutParams)mView.getLayoutParams();
		rlParams.setMargins((int)(-leftOffset + leftOffset*interpolatedTime), 0, 0, 0);
		mView.requestLayout();
	}
}