package ru.enter.adapters;

import java.util.ArrayList;
import java.util.List;

import ru.enter.ImageManager.ImageDownloader;
import ru.enter.beans.ServiceBean;
import ru.enter.interfaces.IBasketElement;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

public abstract class BaseOrderAdapter extends BaseAdapter{
	
	private static final int PRODUCT_OR_SERVICE = 0;
	private static final int RELATED_SERVICE = 1;
	
	private List<IBasketElement> mObjects;
	private LayoutInflater mInflater;
	private ImageDownloader mDownloader;
	
	public BaseOrderAdapter(Context context) {
		mInflater = LayoutInflater.from(context);
		mDownloader = new ImageDownloader(context);
		mObjects = new ArrayList<IBasketElement>();
	}
	
	public void setObjects(List<IBasketElement> newObjects){
		if(newObjects == null){
			newObjects = new ArrayList<IBasketElement>();
		}
		
		mObjects = newObjects;
		notifyDataSetChanged();
	}
	
	@Override
	public int getCount() {
		return mObjects.size();
	}

	@Override
	public IBasketElement getItem(int position) {
		return mObjects.get(position);
	}

	@Override
	public long getItemId(int position) {
		return getItem(position).getId();
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		
		convertView = new View(mInflater.getContext());
		IBasketElement bean = getItem(position);
		
		switch (getItemViewType(position)) {
		case PRODUCT_OR_SERVICE:
			convertView = getProductOrServiceView(bean, mInflater);
			break;
		case RELATED_SERVICE:
			convertView = getRelatedServiceView((ServiceBean)bean, mInflater);
			break;	

		default:
			break;
		}
			
		return convertView;
	}
	
	abstract View getRelatedServiceView(ServiceBean bean, LayoutInflater inflater);

	abstract View getProductOrServiceView(IBasketElement bean, LayoutInflater inflater);
	
	@Override
	public int getItemViewType(int position) {
		IBasketElement item = getItem(position);
		return (item.isProduct() || ((ServiceBean)item).getProductId() == 0) ? PRODUCT_OR_SERVICE : RELATED_SERVICE;
	}
	
	@Override
	public int getViewTypeCount() {
		return 2;
	}
	
	public ImageDownloader getImageLoader () {
		return mDownloader;
	}

}
