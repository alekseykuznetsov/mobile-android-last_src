package ru.enter.adapters;

import java.util.ArrayList;

import ru.enter.R;
import ru.enter.ImageManager.ImageDownloader;
import ru.enter.gallery.CoverFlow;
import android.app.Activity;
import android.content.Context;
import android.view.Display;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

public class ProductCarouselAdapter extends BaseAdapter {

    private Context mContext;
	private int mPictureSide;
	private ArrayList<String> mFotos = new ArrayList<String>();
	private ImageDownloader mImageDownloader;
	
	public ProductCarouselAdapter(Context context, ArrayList<String> fotos) {
		mContext = context;
		Display display = ((Activity)context).getWindowManager().getDefaultDisplay();
		mPictureSide = (int)(display.getWidth()*0.80);
		mFotos = fotos;
		mImageDownloader = new ImageDownloader(context);
	}

	@Override
	public int getCount() {
		return mFotos.size();
	}

	@Override
    public String getItem(int position) {
        return mFotos.get(position);
    }

	@Override
    public long getItemId(int position) {
        return position;
    }

	@Override
    public View getView(int position, View convertView, ViewGroup parent) {

    	String bean = getItem(position);
    	
        ImageView image = new ImageView(mContext);
        image.setImageResource(R.drawable.cap);
        image.setLayoutParams(new CoverFlow.LayoutParams(mPictureSide,mPictureSide));
        image.setScaleType(ImageView.ScaleType.MATRIX);	 
        
        mImageDownloader.download(bean, image);
        return image;
    }
}