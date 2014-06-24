package ru.enter.adapters;

import java.util.ArrayList;

import ru.enter.R;
import ru.enter.beans.ShopBean;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;

public class ShopLocatorListAdapter extends BaseAdapter {
	
	private ArrayList<ShopBean> mObjects;
	private LayoutInflater mInflater;
	
	public ShopLocatorListAdapter(Context context) {
		mInflater = LayoutInflater.from(context);
		mObjects = new ArrayList<ShopBean>();
	}
	
	public void setObjects(ArrayList<ShopBean> newObjects){
		
		if (newObjects == null) {
			newObjects = new ArrayList<ShopBean>();
		}
		
		mObjects = newObjects;
		notifyDataSetChanged();
	}
	
	@Override
	public int getCount() {
		return mObjects.size();
	}

	@Override
	public ShopBean getItem(int position) {
		return mObjects.get(position);
	}

	@Override
	public long getItemId(int position) {
		return getItem(position).getId();
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		convertView = mInflater.inflate(R.layout.shops_list_row, null);
		
		TextView adress = (TextView) convertView.findViewById(R.id.shops_list_row_adress);
		TextView distance = (TextView) convertView.findViewById(R.id.shops_list_row_distance);
		Button path = (Button) convertView.findViewById(R.id.shops_list_row_button_path);
		
		ShopBean bean = getItem(position);
		
		adress.setText(bean.getName());
		
		distance.setVisibility(View.GONE);
		path.setVisibility(View.GONE);
		
		convertView.setTag(bean);
		
		return convertView;
	}

}
