package ru.enter.utils;

import ru.enter.ApplicationTablet;
import android.content.res.Configuration;

public enum ViewMode {
	
	LARGE,
	MIDDLE,
	SMALL;
	
	private boolean mIsHorizontal;

	private boolean checkHorizontal () {
		int orientation = ApplicationTablet.getContext().getResources().getConfiguration().orientation;
		return orientation == Configuration.ORIENTATION_LANDSCAPE;
	}
	
	public boolean isHorizontal() {
		mIsHorizontal = checkHorizontal();
		
		switch (this) {
		case LARGE:
			return true;
		case MIDDLE:
			return mIsHorizontal;
		case SMALL:
			return false;

		default:
			return false;
		}
	}
	
	public int getImageSize() {
		switch (this) {
		case LARGE:
			return mIsHorizontal ? 550 : 350;
		case MIDDLE:
			return mIsHorizontal ? 350 : 200;
		case SMALL:
			return mIsHorizontal ? 200 : 163;

		default:
			return 160;
		}
	}
}
