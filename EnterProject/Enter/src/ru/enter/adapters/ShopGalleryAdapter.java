package ru.enter.adapters;

import java.util.ArrayList;

import ru.enter.R;
import ru.enter.ImageManager.ImageDownloader;
import ru.enter.beans.ShopImgBean;
import ru.enter.utils.URLManager;
import android.content.Context;
import android.os.Parcelable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;

public class ShopGalleryAdapter extends PagerAdapter{
	
	private Context context;
	private ArrayList<String> listURL;
	private ImageDownloader mLoader;
	
	public ShopGalleryAdapter (Context c)
	{
		context = c;
		mLoader = new ImageDownloader(context);
		listURL = new ArrayList<String>();
		
	}
	
	public void setList(ArrayList<ShopImgBean> objects){
		if(!objects.isEmpty())	{
			for(int i=0;i<objects.size();i++)
				if(objects.get(i).getSize()>=500){
					listURL=(ArrayList<String>) objects.get(i).getImages();
				}
			}
		if(listURL.isEmpty()) listURL=(ArrayList<String>) objects.get(0).getImages();		
			
		notifyDataSetChanged();
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		
		return listURL.size();
	}
	
	@Override
	public int getItemPosition(Object object) {
		return POSITION_NONE;
	}
	
	@Override
	public Object instantiateItem(View collection, int position) {
		ImageView iw = new ImageView(context);
		
		String url = URLManager.getShopImageAdress( listURL.get(position));
		mLoader.download(url, iw);
			
		((ViewPager) collection).addView(iw,0);
		
		return iw;
	}
	
	@Override
	public void destroyItem(View collection, int position, Object view) {
		((ViewPager) collection).removeView((ImageView) view);
	}

	
	@Override
	public boolean isViewFromObject(View view, Object object) {
		// TODO Auto-generated method stub
		return view==((ImageView)object);
	}
	
	@Override
	public void finishUpdate(View arg0) {}
	

	@Override
	public void restoreState(Parcelable arg0, ClassLoader arg1) {}

	@Override
	public Parcelable saveState() {
		return null;
	}

	@Override
	public void startUpdate(View arg0) {}
	
	

}
