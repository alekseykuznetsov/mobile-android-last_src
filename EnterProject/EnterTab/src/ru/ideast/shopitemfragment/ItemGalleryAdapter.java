package ru.ideast.shopitemfragment;

import java.util.ArrayList;

import ru.enter.ApplicationTablet;
import ru.enter.R;
import android.content.Context;
import android.graphics.Bitmap;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.webimageloader.ImageLoader;
import com.webimageloader.ImageLoader.Listener;
import com.webimageloader.ext.ImageHelper;



public class ItemGalleryAdapter extends GenericPageAdapter<String> {
	
	private ImageHelper mImageLoader;
	private boolean isFirst = true;
	
		
	public ItemGalleryAdapter (Context c,ArrayList<String> data) {
		super(c,data);
		ImageLoader loader = ApplicationTablet.getLoader(context);

		mImageLoader = new ImageHelper(context, loader)
        .setFadeIn(true)
        .setLoadingResource(R.drawable.tmp_1)
        .setErrorResource(R.drawable.tmp_1);//TODO
	}
	
	@Override
	public Object instantiateItem (View collection, int position) {
		RelativeLayout container = (RelativeLayout) mInflater.inflate(R.layout.image_item, null);
		ImageView image = (ImageView) container.findViewById(R.id.previewImgView);
		mImageLoader.load(image, items.get(position));
		((ViewPager)collection).addView(container);
		return container;
	}

	
	public boolean isViewFromObject (View arg0, Object arg1) {
		return arg0 == ((RelativeLayout)arg1);
	}

	
	
	@Override
	public void destroyItem(ViewGroup container, int position, Object object) {
		 View view = (View)object;
	        ((ViewPager) container).removeView(view);
	        view = null;
	}
	
}
