package ru.ideast.shopitemfragment.adapters;

import java.util.ArrayList;
import java.util.List;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

public class GenericListAdapter<T> extends BaseAdapter {
	protected ArrayList<T> items;
	protected LayoutInflater mInflater;
	protected Integer realCount;
	protected final Context context;
	protected Boolean isFirst = true;
	//protected ImageDownloader loader;
	
	public GenericListAdapter(Context c) {
		items = new ArrayList<T>();
		context = c;
		mInflater = LayoutInflater.from(context);
		//loader = new ImageDownloader(c);
		realCount = 0;
	}

	//rewrite
	public void addData(List<T> data) {
		if(data!=null){
			items.addAll(data);
			realCount = items.size();
			notifyDataSetChanged();
		}
	}

	public int getCount() {
		return items.size();
	}

	public Object getItem(int position) {
		return items.get(position);
	}

	//rewrite for all
	public View getView(final int position, View convertView, ViewGroup parent) {
		return null;
	}

	public long getItemId(int position) {
		return 0;
	}

	public int getRealCount() {
		return realCount;
	}
}
