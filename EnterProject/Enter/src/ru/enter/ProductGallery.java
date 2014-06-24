package ru.enter;

import ru.enter.DataManagement.ProductCacheManager;
import ru.enter.adapters.GestureAdapter;
import ru.enter.beans.ProductBean;
import ru.enter.utils.ImageSizesEnum;
import android.app.Activity;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;

import com.flurry.android.FlurryAgent;
import com.google.analytics.tracking.android.EasyTracker;
import com.polites.android.GestureViewPager;

public class ProductGallery extends Activity{
    
	@Override
	public void onCreate(Bundle savedInstanceState) {
		
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.photo_gallery_view);
	    
	    getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
	    	    WindowManager.LayoutParams.FLAG_FULLSCREEN);
	    
	    GestureViewPager pager = (GestureViewPager)findViewById(R.id.view_pager);
	   
	    int position = getIntent().getIntExtra(ProductCarousel.POSITION, 0);
	    ProductBean mBean = ProductCacheManager.getInstance().getProductInfo();
	    
	    pager.setAdapter(new GestureAdapter(this, mBean.getGallery(ImageSizesEnum.s1500)));
		pager.setCurrentItem(position, false);
	    
	    super.onCreate(savedInstanceState);
	    EasyTracker.getInstance().setContext(this);
	    EasyTracker.getTracker().sendEvent("product/gallery", "buttonPress", mBean.getName(), (long) mBean.getId());
	}
	
	@Override
	protected void onStart()
	{
		super.onStart();
		FlurryAgent.onStartSession(this, "3X5JRBVFV7QWXNP3Q8H2");
	}
	 
	@Override
	protected void onStop()
	{
		super.onStop();		
		FlurryAgent.onEndSession(this);
	}
	
}
