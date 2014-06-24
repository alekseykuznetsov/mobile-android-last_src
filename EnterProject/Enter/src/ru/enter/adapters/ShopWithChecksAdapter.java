package ru.enter.adapters;

import java.util.ArrayList;

import ru.enter.R;
import ru.enter.beans.ShopBean;
import ru.enter.utils.Utils;
import android.content.Context;
import android.location.Location;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.TextView;

public class ShopWithChecksAdapter extends BaseAdapter {
	private ArrayList<ShopBean> mObjects;
	private LayoutInflater mInflater;
	private Location location;
	private OnCheckedChangeListener mOnClickListener;
	public ShopWithChecksAdapter(Context context, OnCheckedChangeListener listener) {
		mInflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		mObjects = new ArrayList<ShopBean>();
		mOnClickListener = listener;
	}
	public void setObjects(ArrayList<ShopBean> newObjects){
			mObjects = newObjects;
			notifyDataSetChanged();
	}
	@Override
	public int getCount() {
		try{
			return mObjects.size();
		}catch (NullPointerException e) {
			return 0;
		}
	}

	@Override
	public ShopBean getItem(int position) {
		return mObjects.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

//	class Tag
//	{
//		private int position;
//		private int View
//	}
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		convertView = mInflater.inflate(R.layout.shop_list_row_w_check, null);
		CheckBox adress = (CheckBox) convertView.findViewById(R.id.shop_list_row_adress_w_check);
		TextView dist = (TextView)convertView.findViewById(R.id.shop_list_row_distance_w_check);
		ShopBean bean = getItem(position);
		
		adress.setText(bean.getName());
		adress.setFocusable(false);
		adress.setTag(bean);
		adress.setOnCheckedChangeListener(mOnClickListener);
//		dist.setText("500m");
		Location loc = getLocation();
		try{
			dist.setText(Utils.gps2m_formated(Float.parseFloat(bean.getLatitude()), Float.parseFloat(bean.getLatitude()), loc));//TODO
		}catch(NullPointerException e){
			
		}
		
		
		
		return convertView;
	}
	public Location getLocation() {
		return location;
	}
	public void setLocation(Location location) {
		this.location = location;
	}

}
