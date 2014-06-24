package ru.enter.adapters;

import java.util.ArrayList;

import ru.enter.CatalogActivity;
import ru.enter.R;
import ru.enter.ImageManager.ImageDownloader;
import ru.enter.beans.CatalogListBean;
import ru.enter.utils.Utils;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class CatalogGridAdapter extends BaseAdapter {
    private Context mContext;
    private ArrayList<CatalogListBean> bean;
    private ImageDownloader mImageLoader;
	private OnTouchListener externalTouchListener;
    
    public CatalogGridAdapter(Context c, ArrayList<CatalogListBean> b) {
        mContext = c;
        bean = b;
        mImageLoader = new ImageDownloader(mContext);
    }

    public int getCount() {
        return bean.size();
    }

    public CatalogListBean getItem(int position) {
        return bean.get(position);
    }

    public long getItemId(int position) {
        return 0;
    }
    
    public void setExternalTouchListenerToItem(OnTouchListener otl){
    	externalTouchListener = otl;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
    	
    	LayoutInflater viewInflater = (LayoutInflater)mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View view = viewInflater.inflate(R.layout.catalog_grid_row, null);

		TextView text = (TextView)view.findViewById(R.id.catalog_grid_row_text);
		ImageView iw = (ImageView)view.findViewById(R.id.catalog_grid_row_img);
        
		CatalogListBean msg = getItem(position);
		
		String size = ((CatalogActivity) mContext).getIconDimensions();
		mImageLoader.download(msg.getIcon().replaceAll("__icon_size__", size), iw);

        text.setText(msg.getName());
        text.setTypeface(Utils.getTypeFace(mContext));
        if(externalTouchListener != null){
        	view.setOnTouchListener(externalTouchListener);
        }
        
        return view;
    }
}