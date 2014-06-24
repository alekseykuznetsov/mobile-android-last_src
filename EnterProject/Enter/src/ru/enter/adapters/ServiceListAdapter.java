package ru.enter.adapters;

import java.util.ArrayList;

import ru.enter.R;
import ru.enter.beans.ServiceBean;
import ru.enter.utils.Utils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class ServiceListAdapter extends BaseAdapter{
	private LayoutInflater mInflater;
	private ArrayList<ServiceBean> mObjects = new ArrayList<ServiceBean>();

	public void setObjects(ArrayList<ServiceBean> objects){
		if (!Utils.isEmptyList(objects)) {
			mObjects = objects;
			notifyDataSetChanged();
		}
	}

	@Override
	public int getCount() {
		return mObjects.size();
	}

	@Override
	public ServiceBean getItem(int position) {
		return mObjects.get(position);
	}

	@Override
	public long getItemId(int position) {
		return mObjects.get(position).getId();
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		if(mInflater == null){
			mInflater = LayoutInflater.from(parent.getContext());
		}
		
		convertView = mInflater.inflate(R.layout.service_list_row, null);
		TextView text = (TextView) convertView.findViewById(R.id.service_list_row_text);
		
		ServiceBean bean = getItem(position);
		text.setText(bean.getName());
		
		convertView.setTag(bean);
		return convertView;
	}

}
