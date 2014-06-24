package ru.enter.adapters;

import java.util.ArrayList;
import java.util.zip.Inflater;

import ru.enter.ApplicationTablet;
import ru.enter.R;
import ru.enter.beans.ShopImgBean;
import ru.enter.utils.URLManager;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Parcelable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import com.webimageloader.ImageLoader;
import com.webimageloader.ImageLoader.Listener;
import com.webimageloader.ext.ImageHelper;

public class ShopImageGallaryAdapter extends PagerAdapter {
	private ArrayList<String> images;
	private Context context;
	private LayoutInflater mInflater;
	private final static int SIZE = 550;
	
	public ShopImageGallaryAdapter(Context c) {
		context = c;
		mInflater = LayoutInflater.from(context);
		images = new ArrayList<String>();
	}
	
	
	public void addImages(ArrayList<ShopImgBean> imgList){
		for(ShopImgBean bean : imgList){
			if(bean.getSize() == SIZE){
				images.addAll(bean.getImages());
			}
		}
		notifyDataSetChanged();
		
	}
	
	@Override
	public int getCount() {
		return images.size();
	}

	@Override
	public Object instantiateItem (View collection, int position) {
		RelativeLayout container = (RelativeLayout) mInflater.inflate(R.layout.shop_dialog_fullimage, null);
		ImageView image = (ImageView) container.findViewById(R.id.gallery_image);
		//image.setBackgroundDrawable(context.getResources().getDrawable(R.drawable.progress_spin));
		//image.setBackgroundDrawable(images.get(position));
		ImageLoader loader = ApplicationTablet.getLoader(context);
		final ProgressBar pbLoading = (ProgressBar) container.findViewById(R.id.gallery_progress);

		pbLoading.setVisibility(View.VISIBLE);
//		ImageHelper imageLoader = new ImageHelper(context, loader).setFadeIn(true)
//				.setLoadingResource(R.drawable.progress_spin)
//				.setErrorResource(R.drawable.shop_top_img);
//		imageLoader.load(image, URLManager.getShopImageAdress(images.get(position)));

		final View parent = collection;
		Bitmap b = loader.load(image, URLManager.getShopImageAdress(images.get(position)), new Listener<ImageView>() {
			@Override
		    public void onSuccess(ImageView v, Bitmap b) {
		    	pbLoading.setVisibility(View.INVISIBLE);
		        v.setImageBitmap(b);
		    }

		    @Override
		    public void onError(ImageView v, Throwable t) {
		        Log.d("MyApp", "Failed to load image", t);
		        v.setImageResource(R.drawable.tmp_3);
		        //v.setImageDrawable(context.getResources().getDrawable(R.drawable.shop_top_img));
		        //parent.setBackgroundDrawable(context.getResources().getDrawable(R.drawable.shop_top_img));
				((ViewPager)parent).setVisibility(View.GONE); // hide pager & show no-data textview, bug 2608
				(((View) parent.getParent()).findViewById(R.id.empty)).setVisibility(View.VISIBLE);
		        pbLoading.setVisibility(View.INVISIBLE);
		    }
		});
		
		// Did we get an image immediately?
		if (b != null) {
			pbLoading.setVisibility(View.INVISIBLE);
			image.setImageBitmap(b);
		}

		((ViewPager) collection).addView(container);
		return container;
	}

	@Override
	public void destroyItem (View arg0, int arg1, Object arg2) {
		((ViewPager) arg0).removeView((View) arg2);
	}

	@Override
	public void finishUpdate (View arg0) {
	}

	@Override
	public void restoreState (Parcelable arg0, ClassLoader arg1) {
	}

	@Override
	public Parcelable saveState () {
		return null;
	}

	@Override
	public void startUpdate (View arg0) {
	}

	@Override
	public boolean isViewFromObject (View arg0, Object arg1) {
		return arg0 == ((RelativeLayout) arg1);
	}

}
