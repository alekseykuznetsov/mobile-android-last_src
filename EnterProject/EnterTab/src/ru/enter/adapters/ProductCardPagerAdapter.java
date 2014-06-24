package ru.enter.adapters;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import ru.enter.ApplicationTablet;
import ru.enter.R;
import ru.enter.utils.Formatters;
import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.ImageView;

import com.webimageloader.ImageLoader;
import com.webimageloader.ext.ImageHelper;

public class ProductCardPagerAdapter extends PagerAdapter{

	private Context mContext;
	private ImageHelper mImageLoader;
	private List<String> mFotos;
	
	public ProductCardPagerAdapter (Context context, ArrayList<String> fotos){
		mContext = context;
		ImageLoader loader = ApplicationTablet.getLoader(context);
		mImageLoader = new ImageHelper(context, loader)
        .setFadeIn(true)
        .setLoadingResource(R.drawable.tmp_1)
        .setErrorResource(R.drawable.tmp_1);//TODO
		
		if (fotos == null) {
			mFotos = Collections.emptyList();
		}else{
			mFotos = fotos;
		}
	}
	
	@Override
	public int getCount() {
		return mFotos.size();
	}
	
	@Override
	public int getItemPosition(Object object) {
		return POSITION_NONE;
	}

	@Override
	public Object instantiateItem(View collection, int position) {
		
		ImageView image = new ImageView(mContext);
		String foto = Formatters.createFotoString(mFotos.get(position), 500);
		mImageLoader.load(image, foto);
		
		((ViewPager) collection).addView(image,0);
		
		return image;
	}
	
	@Override
	public void destroyItem(View collection, int position, Object view) {
		((ViewPager) collection).removeView((ImageView)view);
	}
	
	@Override
	public boolean isViewFromObject(View view, Object object) {
		return view==((ImageView)object);
	}

}
