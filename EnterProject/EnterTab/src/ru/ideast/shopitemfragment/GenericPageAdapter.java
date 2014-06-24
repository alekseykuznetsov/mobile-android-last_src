package ru.ideast.shopitemfragment;

import java.util.ArrayList;

//import ru.ideast.minisdk.imagecache.ImageDownloader;
import android.content.Context;
import android.os.Parcelable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RelativeLayout;

public class GenericPageAdapter<T> extends PagerAdapter {
	protected ArrayList<T> items;
    protected Context context;
    protected LayoutInflater mInflater; 
    //protected ImageDownloader loader;

	public GenericPageAdapter(Context c, ArrayList<T> data) {
		context = c;
		mInflater = LayoutInflater.from(context);
		//loader =  new ImageDownloader(context);
		items = (data!=null) ? data : new ArrayList<T>();
	}
		
	@Override
    public int getCount() {
		return items.size();
    }
		
	@Override
    public Object instantiateItem(View collection, int position) {
		return null;
    }
	
    @Override
    public void destroyItem(View collection, int position, Object view) {
        ((ViewPager) collection).removeView((RelativeLayout) view);
    }
    
    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == ((RelativeLayout)object);
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
