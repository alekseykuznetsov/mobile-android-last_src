package ru.enter.animations;

import android.view.View;
import android.view.animation.Animation;
import android.view.animation.Transformation;
import android.widget.RelativeLayout;

// @formatter:off
public class HeaderSlideToTopAnimation extends Animation {
	
	protected View mView;			
	
	protected float topOffset;

	
	public HeaderSlideToTopAnimation (View v, float topOffset) {
		mView = v;
		this.topOffset = topOffset;
		setFillEnabled(true);
		setFillAfter(true);
	}

	@Override
	protected void applyTransformation (float interpolatedTime,Transformation t) {
		RelativeLayout.LayoutParams rlParams = (RelativeLayout.LayoutParams)mView.getLayoutParams();
		//rlParams.setMargins(0, (int)(-1*topOffset*interpolatedTime), 0, 0);//(int)topOffset);
		rlParams.setMargins(0, (int)(-topOffset*interpolatedTime), 0, 0);//(int)topOffset);
		//mView.setLayoutParams(rlParams);
		mView.requestLayout();
	}
}