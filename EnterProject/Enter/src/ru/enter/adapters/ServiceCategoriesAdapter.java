package ru.enter.adapters;

import java.util.ArrayList;

import ru.enter.R;
import ru.enter.beans.ServiceCategoryBean;
import ru.enter.utils.Utils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class ServiceCategoriesAdapter extends BaseAdapter {
	
	private ArrayList<ServiceCategoryBean> mObjects = new ArrayList<ServiceCategoryBean>();
	private LayoutInflater mInflater;

	public void setObjects(ArrayList<ServiceCategoryBean> objects){
		if (!Utils.isEmptyList(objects)){
			mObjects = objects;
			notifyDataSetChanged();
		}
	}

	@Override
	public int getCount() {
		return mObjects.size();
	}

	@Override
	public ServiceCategoryBean getItem(int position) {
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
		
		ServiceCategoryBean bean = getItem(position);
		text.setText(bean.getName());
		
		convertView.setTag(bean);
		return convertView;
	}

}
