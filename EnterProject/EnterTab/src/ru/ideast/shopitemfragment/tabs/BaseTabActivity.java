package ru.ideast.shopitemfragment.tabs;

import ru.ideast.shopitemfragment.ScrollListener;
import android.app.Activity;
import android.util.Log;
import android.view.LayoutInflater;

/**
 * Base activity for item's info tabs.
 * 
 * @author Dmitry Kuznetsov
 * 
 */
public class BaseTabActivity extends Activity {

	ScrollListener mScrollListener = null;
	LayoutInflater mInflater;
	
	public ScrollListener getScrollListener () {
		return mScrollListener;
	}

	public void setScrollListener (ScrollListener mListener) {
		this.mScrollListener = mListener;
	}

	
	
}
