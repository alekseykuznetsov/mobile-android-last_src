package ru.enter.adapters;

import java.util.ArrayList;

import ru.enter.R;
import ru.enter.ImageManager.ImageDownloader;
import ru.enter.beans.BannerBean;
import android.content.Context;
import android.os.Parcelable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.FrameLayout;
import android.widget.ImageView;

public class AdAdapter extends PagerAdapter{

	private Context context;
	private ArrayList<BannerBean> mObjects;
	private ImageDownloader mLoader;
	private OnClickListener mListener;
	private int mSize = 1;
	
	public AdAdapter (Context c, OnClickListener listener)
	{
		context = c;
		mLoader = new ImageDownloader(context);
		mObjects = new ArrayList<BannerBean>();
		mListener = listener;
	}
	
	public void setObjects(ArrayList<BannerBean> objects){
		mObjects = objects;
		mObjects.add(0, objects.get(objects.size()-1));
		mObjects.add(objects.get(1));
		mSize = mObjects.size();
		notifyDataSetChanged();
	}
	
	@Override
	public int getCount() {
		return mSize;
	}
	
	@Override
	public int getItemPosition(Object object) {
		return POSITION_NONE;
	}

    /**
     * Create the page for the given position.  The adapter is responsible
     * for adding the view to the container given here, although it only
     * must ensure this is done by the time it returns from
     * {@link #finishUpdate()}.
     *
     * @param container The containing View in which the page will be shown.
     * @param position The page position to be instantiated.
     * @return Returns an Object representing the new page.  This does not
     * need to be a View, but can be some other container of the page.
     */
	@Override
	public Object instantiateItem(View collection, int position) {
		
		LayoutInflater viewInflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		FrameLayout frame = (FrameLayout)viewInflater.inflate(R.layout.mainmenu_ad_row, null);
		
		ImageView iw = (ImageView)frame.findViewById(R.id.mainmenu_item_ad_row_img);
		
		try{
			BannerBean bean = mObjects.get(position);
			mLoader.download(bean.getPhotos(), iw);
			
			frame.setTag(bean);
			frame.setOnClickListener(mListener);
			
		}catch (IndexOutOfBoundsException e) {}
		
		((ViewPager) collection).addView(frame,0);
		
		return frame;
	}

    /**
     * Remove a page for the given position.  The adapter is responsible
     * for removing the view from its container, although it only must ensure
     * this is done by the time it returns from {@link #finishUpdate()}.
     *
     * @param container The containing View from which the page will be removed.
     * @param position The page position to be removed.
     * @param object The same object that was returned by
     * {@link #instantiateItem(View, int)}.
     */
	@Override
	public void destroyItem(View collection, int position, Object view) {
		((ViewPager) collection).removeView((FrameLayout) view);
	}

	
	
	@Override
	public boolean isViewFromObject(View view, Object object) {
		return view==((FrameLayout)object);
	}

	
    /**
     * Called when the a change in the shown pages has been completed.  At this
     * point you must ensure that all of the pages have actually been added or
     * removed from the container as appropriate.
     * @param container The containing View which is displaying this adapter's
     * page views.
     */
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