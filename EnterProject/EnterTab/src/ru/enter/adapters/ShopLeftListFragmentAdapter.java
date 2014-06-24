package ru.enter.adapters;

import java.util.ArrayList;

import ru.enter.R;
import ru.enter.beans.ShopBean;
import ru.enter.route.Road;
import android.content.Context;
import android.graphics.Color;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class ShopLeftListFragmentAdapter extends BaseAdapter {

	private LayoutInflater mInflater;
	private Context mContext;
	/* TODO: find a better solution
	 * Selector doesnot work when fragment looses focus.
	 * Replaced by this dumb solution : simple changing background 
	 * */
	private int mCurrent = -1;
	private ArrayList<ShopBean> shops;
	private SparseArray<Road> routes;

	public ShopLeftListFragmentAdapter(Context context,ArrayList<ShopBean> shops) {
		this.mContext = context;
		this.shops = shops;
		mInflater = LayoutInflater.from(mContext);
	}

	public void setRoutes(SparseArray<Road> routes) {
		this.routes = routes;
		notifyDataSetChanged();
	}

	public void updateShopRoute(int shopId,Road route){
		if(routes!=null && routes.get(shopId)==null){
			routes.put(shopId, route);
			notifyDataSetChanged();
		}
	}
	
	@Override
	public int getCount() {
		return shops.size();
	}

	@Override
	public Object getItem(int position) {
		return shops.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	public int getCurrent() {
		return mCurrent;
	}

	public void setCurrent(int mCurrent) {
		this.mCurrent = mCurrent;
	}
	
	public void setCurrent(ShopBean shop) {
		if(shops!=null){
			for(int index = 0; index < shops.size(); index++){
				if(shops.get(index).getName().equals(shop.getName())){
					mCurrent = index;
					break;
				}
			}
			notifyDataSetChanged();
		}
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {

		ShopBean shop = (ShopBean) getItem(position);

		if (convertView == null) {
			convertView = mInflater.inflate(R.layout.shop_fr_left_item, null);
		}

		if (position == mCurrent) {
			convertView.setBackgroundColor(Color.GRAY);
		}
		else{
			convertView.setBackgroundColor(Color.WHITE);
		}

		// @TODO distance - done
		TextView distance = (TextView) convertView.findViewById(R.id.shop_left_fr_item_text_distance);
		if (routes != null) {
			Road road = routes.get(shop.getId());
			distance.setText(road == null ? "" : road.mLength);
		}

		TextView addresstxt = (TextView) convertView.findViewById(R.id.shop_left_fr_item_text_address);
		addresstxt.setText(shop.getName());

		return convertView;
	}
}