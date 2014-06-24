package ru.enter.adapters;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import ru.enter.ApplicationTablet;
import ru.enter.R;
import android.content.Context;
import android.os.Parcelable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;

import com.polites.android.GestureImageView;
import com.polites.android.GesturePagerAdapter;
import com.webimageloader.ImageLoader;
import com.webimageloader.ext.ImageHelper;

public class ProductCardGalleryGestureAdapter extends PagerAdapter implements GesturePagerAdapter {

	private ArrayList<String> imgUrls;
	private HashMap<Integer, GestureImageView> aroundImgs;
	private Context context;
	private ImageHelper mImageLoader;	
	
	private OnClickListener mListner;

	public ProductCardGalleryGestureAdapter (Context context, List<String> beans) {
		this.imgUrls = new ArrayList<String>(beans);
		this.context = context;
		aroundImgs = new HashMap<Integer, GestureImageView>();
		
		ImageLoader loader = ApplicationTablet.getLoader(context);
		mImageLoader = new ImageHelper(context, loader);
		mListner = null;
	}
	
	
	public ProductCardGalleryGestureAdapter (Context context, List<String> beans,OnClickListener listner) {
		this.imgUrls = new ArrayList<String>(beans);
		this.context = context;
		aroundImgs = new HashMap<Integer, GestureImageView>();
		
		ImageLoader loader = ApplicationTablet.getLoader(context);
		mImageLoader = new ImageHelper(context, loader);
		
		mListner = listner;
	}

	@Override
	public Object instantiateItem (ViewGroup collection, int position) {
		LayoutParams params = new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT);

		GestureImageView view = new GestureImageView(context);
		view.setLayoutParams(params);
		view.setBackgroundResource(R.drawable.tmp_3);
		
		if (mListner != null)
			view.setOnSingleTapListener(mListner);

		mImageLoader.load(view, imgUrls.get(position));
		
		aroundImgs.put(position, view);
		((ViewPager) collection).addView(view, 0);
		return view;
	}

	@Override
	public void destroyItem (ViewGroup collection, int position, Object view) {
		GestureImageView v = (GestureImageView) view;
        v.setImageBitmap(null);
		aroundImgs.remove(position);
		((ViewPager) collection).removeView(v);
	}

	@Override
	public int getCount () {
		return imgUrls.size();
	}

	@Override
	public GestureImageView getImage (int position) {
		return aroundImgs.get(position);
	}

	@Override
	public void finishUpdate (View arg0) {}

	@Override
	public void restoreState (Parcelable arg0, ClassLoader arg1) {}

	@Override
	public Parcelable saveState () {
		return null;
	}

	@Override
	public void startUpdate (View arg0) {}

	@Override
	public boolean isViewFromObject (View arg0, Object arg1) {
		return arg0 == arg1;
	}

}
