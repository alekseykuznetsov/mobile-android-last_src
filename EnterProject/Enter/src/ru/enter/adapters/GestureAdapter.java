package ru.enter.adapters;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import ru.enter.ImageManager.ImageDownloader;
import ru.enter.beans.PhotoBean;
import ru.enter.utils.Utils;
import ru.enter.R;
import android.content.Context;
import android.os.Parcelable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;

import com.polites.android.GestureImageView;
import com.polites.android.GesturePagerAdapter;

public class GestureAdapter extends PagerAdapter implements GesturePagerAdapter{

	private ArrayList<String> imgUrls;
	private HashMap<Integer, GestureImageView> aroundImgs;
	private Context context;
	private ImageDownloader loader;
	
	public GestureAdapter(Context context, ArrayList<PhotoBean> beans){
		if(beans == null)
			this.imgUrls = new ArrayList<String>();
		else{
			int pos = Utils.getPhoto(beans, 500);
			this.imgUrls = beans.get(pos).getImages();
		}
		this.context = context;
		loader = new ImageDownloader(context);
		aroundImgs = new HashMap<Integer, GestureImageView>();
	}
	
	public GestureAdapter(Context context, List<String> beans) {
		this.imgUrls = new ArrayList<String>(beans);
		this.context = context;
		loader = new ImageDownloader(context);
		aroundImgs = new HashMap<Integer, GestureImageView>();
	}

	@Override
	public Object instantiateItem(ViewGroup collection, int position) {
        LayoutParams params = new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT);
        
        GestureImageView view = new GestureImageView(context);
        view.setLayoutParams(params);
        view.setImageResource(R.drawable.cap);
       
        loader.download(imgUrls.get(position), view);

        aroundImgs.put(position, view);
		((ViewPager) collection).addView(view, 0);
		return view;
	}

	@Override
	public void destroyItem(ViewGroup collection, int position, Object view) {
		super.destroyItem(collection, position, view);
		aroundImgs.remove(position);
	}
	
	@Override
	public int getCount() {
		return imgUrls.size();
	}

	@Override
	public GestureImageView getImage(int position) {
		return aroundImgs.get(position);
	}

	@Override
	public void finishUpdate(View arg0) {
	}

	@Override
	public void restoreState(Parcelable arg0, ClassLoader arg1) {
	}

	@Override
	public Parcelable saveState() {
		return null;
	}

	@Override
	public void startUpdate(View arg0) {
	}

	@Override
	public boolean isViewFromObject (View arg0, Object arg1) {
		return arg0 == arg1;
	}
	
}
