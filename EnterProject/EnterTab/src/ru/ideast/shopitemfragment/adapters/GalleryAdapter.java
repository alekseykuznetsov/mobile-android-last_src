package ru.ideast.shopitemfragment.adapters;

import java.util.ArrayList;

import ru.enter.R;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Parcelable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;

public class GalleryAdapter extends PagerAdapter {

	private Context context;
	private ArrayList<Drawable> pics;
	private LayoutInflater mInflater;

	public GalleryAdapter (Context c) {

		context = c;
		mInflater = LayoutInflater.from(context);
		pics = new ArrayList<Drawable>();
		/*pics.add(context.getResources().getDrawable(R.drawable.pic01));
		pics.add(context.getResources().getDrawable(R.drawable.pic02));
		pics.add(context.getResources().getDrawable(R.drawable.pic03));*/
	}

	@Override
	public int getCount () {

		return pics.size();
	}

	@Override
	public Object instantiateItem (View collection, int position) {
		RelativeLayout container = (RelativeLayout) mInflater.inflate(				R.layout.image_item, null);
		ImageView image = (ImageView) container.findViewById(R.id.previewImgView);
		image.setBackgroundDrawable(pics.get(position));

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
