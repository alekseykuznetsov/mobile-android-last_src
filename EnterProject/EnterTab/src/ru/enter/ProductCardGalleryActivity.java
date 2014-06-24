package ru.enter;

import java.util.List;

import ru.enter.adapters.ProductCardGalleryGestureAdapter;
import ru.enter.beans.ProductBean;
import ru.enter.utils.ImageSizesEnum;
import android.app.Activity;
import android.os.Bundle;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;

import com.flurry.android.FlurryAgent;
import com.polites.android.GestureViewPager;

public class ProductCardGalleryActivity extends Activity implements OnClickListener, OnPageChangeListener{
    
	public final static String PRODUCT_BEAN = "product_gallery_array";
	public final static String GALLERY_ARRAY_POSITION = "product_gallery_array_position";
	
	private int mSize;
	private GestureViewPager pager;
	private Button mBtn_left,mBtn_right;
	
	private boolean m_isShowBtn;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.product_card_gallery_ac);		
	    
	    getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
	    	    WindowManager.LayoutParams.FLAG_FULLSCREEN);
	    
	    pager = (GestureViewPager)findViewById(R.id.product_card_gallery_ac_view_pager);    
	   
	    int position = getIntent().getIntExtra(GALLERY_ARRAY_POSITION, 0);
	    
	    ProductBean mBean = (ProductBean) getIntent().getSerializableExtra(PRODUCT_BEAN);
	    List<String> urls = mBean.getGallery(ImageSizesEnum.s1500);
	    mSize = urls.size();
	    ProductCardGalleryGestureAdapter adapter = new ProductCardGalleryGestureAdapter(this, urls,this);	    
	    pager.setAdapter(adapter);
		pager.setCurrentItem(position, false);
		
		pager.setOnPageChangeListener(this);
		mBtn_left = (Button) findViewById(R.id.btn_galery_image_prev);
		mBtn_left.setOnClickListener(this);
		
		mBtn_right = (Button) findViewById(R.id.btn_galery_image_next);
		mBtn_right.setOnClickListener(this);
		m_isShowBtn = true;		
		
		visibilityButtons();
	    super.onCreate(savedInstanceState);
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

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		int position;
		switch (v.getId()) {
		case R.id.btn_galery_image_prev:
			position = pager.getCurrentItem();
			if(position>0)
				pager.setCurrentItem(position-1);
			break;

		case R.id.btn_galery_image_next:
			position = pager.getCurrentItem();
			if(position<mSize-1)
				pager.setCurrentItem(position+1);
			break;

		default:
			if (m_isShowBtn)
				hideButton();
			else
				showButton();
			break;
		}
		
	}
	
	private void showButton(){
		m_isShowBtn = true;
		visibilityButtons();
	}
	
	private void visibilityButtons(){
		if (m_isShowBtn) {
			int position = pager.getCurrentItem();
			if (position > 0)
				mBtn_left.setVisibility(View.VISIBLE);
			else
				mBtn_left.setVisibility(View.INVISIBLE);
			if (position < mSize - 1)
				mBtn_right.setVisibility(View.VISIBLE);
			else
				mBtn_right.setVisibility(View.INVISIBLE);
		} else {
			mBtn_left.setVisibility(View.INVISIBLE);
			mBtn_right.setVisibility(View.INVISIBLE);
		}
	}
	
	
	private void hideButton(){
		m_isShowBtn = false;
		visibilityButtons();
	}

	@Override
	public void onPageScrollStateChanged(int arg0) {
		// TODO Auto-generated method stub
	}

	@Override
	public void onPageScrolled(int arg0, float arg1, int arg2) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onPageSelected(int arg0) {
		// TODO Auto-generated method stub
		visibilityButtons();
		
	}
}