package ru.enter.adapters;

import java.util.ArrayList;

import ru.enter.R;
import ru.enter.beans.MetroBean;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class OrderMetroSpinnerAdapter extends BaseAdapter {

	private LayoutInflater mInflater;
	private Context mContext;	
	private ArrayList<MetroBean> metros;

	public OrderMetroSpinnerAdapter(Context context, ArrayList<MetroBean> metro) {
        this.mContext = context;
        this.metros = metro;
        mInflater = LayoutInflater.from(mContext);
    }
	
	@Override
	public int getCount() {		
		return metros.size();
	}

	@Override
	public Object getItem(int position) {
		return metros.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		
		MetroBean metro_bean = (MetroBean) getItem(position);
		
    	if (convertView == null) {
            convertView = (TextView) mInflater.inflate(R.layout.order_merge_spinner_address_item, null);
        }
    	TextView metro_txt = (TextView) convertView.findViewById(R.id.order_merge_spinner_address_text);
    	metro_txt.setText(metro_bean.getName());
    	

    	return convertView;
	}
}