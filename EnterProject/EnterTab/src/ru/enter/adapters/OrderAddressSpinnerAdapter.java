package ru.enter.adapters;

import java.util.ArrayList;

import ru.enter.R;
import ru.enter.beans.ShopBean;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class OrderAddressSpinnerAdapter extends BaseAdapter {

	private LayoutInflater mInflater;
	private Context mContext;	
	private ArrayList<ShopBean> shops;

	public OrderAddressSpinnerAdapter(Context context, ArrayList<ShopBean> shops) {
        this.mContext = context;
        this.shops = shops;
        mInflater = LayoutInflater.from(mContext);
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

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		
		ShopBean shop = (ShopBean) getItem(position);
		
    	if (convertView == null) {
            convertView = (TextView) mInflater.inflate(R.layout.order_merge_spinner_address_item, null);
        }
    	TextView address = (TextView) convertView.findViewById(R.id.order_merge_spinner_address_text);
  
    	address.setText(shop.getName());

    	return convertView;
	}


}