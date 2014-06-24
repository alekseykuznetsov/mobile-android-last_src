package ru.ideast.shopitemfragment;

import android.app.Activity;

/**
 * Parent activity for description tabs
 * @author Dmitry Kuznetsov
 *
 */
public class TabActivity extends Activity {
	
	ScrollDownLitener scrollDownListener = null;
	ScrollUpLitener scrollUpListener = null;
	
	/**
	 * Interface to listen if we scrolled content up
	 *
	 */
	public interface ScrollUpLitener {
		void onScrolledUp();
	}
	
	/**
	 * Interface to listen if we scrolled content down
	 *
	 */
	public interface ScrollDownLitener {
		void onScrolledDown();
	}

	
	public void setScrollDownListener (ScrollDownLitener scrollDownListener) {
		this.scrollDownListener = scrollDownListener;
	}

	
	public void setScrollUpListener (ScrollUpLitener scrollUpListener) {
		this.scrollUpListener = scrollUpListener;
	}
	


}
