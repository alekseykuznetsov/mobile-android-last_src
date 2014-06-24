package ru.enter.adapters;

import java.util.ArrayList;
import java.util.List;

import ru.enter.ApplicationTablet;
import ru.enter.R;
import ru.enter.beans.BannerBean;
import ru.enter.utils.Utils;
import android.content.Context;
import android.graphics.Bitmap;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.FrameLayout;
import android.widget.FrameLayout.LayoutParams;
import android.widget.ImageView;

import com.webimageloader.ImageLoader;
import com.webimageloader.ImageLoader.Listener;
import com.webimageloader.ext.ImageHelper;

public class BannerAdapter extends PagerAdapter {

	private List<BannerBean> mObjects;
	private int mSize;
	private LayoutInflater mInflater;
	private ImageHelper mLoaderHelper;
	private OnClickListener mListener;
	private ImageLoader mLoader;

	public BannerAdapter(Context context) {
		mInflater = LayoutInflater.from(context);
		mObjects = new ArrayList<BannerBean>();

		mLoader = ApplicationTablet.getLoader(context);
		mLoaderHelper = new ImageHelper(context, mLoader).setFadeIn(true)
				.setLoadingResource(R.drawable.tmp_banner)
				.setErrorResource(R.drawable.tmp_banner);// TODO
	}

	public void setObjects(final List<BannerBean> objects) {
		if (objects == null) {
			mObjects = new ArrayList<BannerBean>();
		}

		mObjects = new ArrayList<BannerBean>(objects);
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

	@Override
	public Object instantiateItem(View collection, int position) {

		FrameLayout frame = (FrameLayout) mInflater.inflate(R.layout.banner_item_view, null);
		ImageView image = (ImageView) frame.findViewById(R.id.banner_item_view_image);

		BannerBean bean = mObjects.get(position);

		Bitmap b = mLoader.load(image, bean.getPhotos(), new Listener<ImageView>() {
			@Override
			public void onSuccess(ImageView v, Bitmap b) {
				LayoutParams params = (LayoutParams) v.getLayoutParams();
				params.width = LayoutParams.WRAP_CONTENT;
				params.height = LayoutParams.WRAP_CONTENT;
				v.setImageBitmap(b);
			}

			@Override
			public void onError(ImageView v, Throwable t) {
				// Something went wrong
			}
		});

		// Did we get an image immediately?
		if (b != null) {
			image.setImageBitmap(b);
		}

		image.setTag(bean);
		image.setOnClickListener(mListener);

		// TODO
		((ViewPager) collection).addView(frame, 0);

		return frame;
	}

	@Override
	public void destroyItem(View collection, int position, Object view) {
		((ViewPager) collection).removeView((FrameLayout)view);
	}

	@Override
	public boolean isViewFromObject(View view, Object object) {
		return view == ((FrameLayout) object);
	}

	public void setOnClickListener (OnClickListener listener) {
		mListener = listener;
	}

}