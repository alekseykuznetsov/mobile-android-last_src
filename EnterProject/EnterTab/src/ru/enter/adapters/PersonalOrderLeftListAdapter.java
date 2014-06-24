package ru.enter.adapters;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import ru.enter.R;
import ru.enter.beans.OrderBean;
import ru.enter.utils.Converter;
import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class PersonalOrderLeftListAdapter extends BaseAdapter {
	
	private LayoutInflater mInflater;
	private ArrayList<OrderBean> mOrders;
	private int lastSelectedPosition = 0;
	
	public PersonalOrderLeftListAdapter(Context context, ArrayList<OrderBean> orders) {
        mOrders = orders;
        mInflater = LayoutInflater.from(context);
        Collections.sort(mOrders, mDateComparator);
    }

	private static Comparator<OrderBean> mDateComparator = new Comparator<OrderBean>() {
		public int compare(OrderBean first, OrderBean second) {
			long dateFirst = first.getAddedDateToCompare();
			long dateSecond = second.getAddedDateToCompare();
			if(dateFirst < dateSecond) return 1;
			else if (dateFirst > dateSecond) return -1;
			else return 0;
		};
	};

	public void setSelectedChild(View view){

		notifyDataSetInvalidated();
		setSelected(view);
		
	}
	
	@Override
	public int getCount() {		
		return mOrders.size();
	}

	@Override
	public OrderBean getItem(int position) {
		return mOrders.get(position);
	}

	@Override
	public long getItemId(int position) {
		return mOrders.get(position).getId();
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {

		OrderBean order = (OrderBean) getItem(position);
		ViewHolder holder = null;
    	if (convertView == null) {
            convertView = mInflater.inflate(R.layout.personal_order_fr_left_list_item, null);
            holder = new ViewHolder();
    		holder.date = (TextView) convertView.findViewById(R.id.personal_order_fr_list_item_text_date);
    		holder.number = (TextView) convertView.findViewById(R.id.personal_order_fr_list_item_text_number);
    		holder.status = (TextView) convertView.findViewById(R.id.personal_order_fr_list_item_text_status);
            convertView.setTag(holder);
        } 
    	else {
    		holder = (ViewHolder) convertView.getTag();
    	}
    	
    	holder.position = position;
		holder.date.setText(Converter.fromLineToDot(order.getAddedDate()));
		holder.number.setText("№ " + order.getNumber());
		holder.status.setText(getStatusName(order.getStatus_id()));

		if(lastSelectedPosition == position)
			setSelected(convertView);
		else
			unsetSelected(convertView);
			
		return convertView;
	}
	
	public void setSelected(View view){
		ViewHolder holder = (ViewHolder) view.getTag();
		view.setBackgroundResource(R.drawable.list_right_border_selected);
		holder.status.setTextColor(Color.parseColor("#ffffff"));
		lastSelectedPosition = holder.position;
	}
	
	public void unsetSelected(View view){
		ViewHolder holder = (ViewHolder) view.getTag();
		view.setBackgroundResource(R.drawable.list_right_border);
		holder.status.setTextColor(Color.parseColor("#777777"));
	}
	
	public static String getStatusName(int id){
		switch (id) {
		case 0:
			return "Не установлен";
		case 1:
			return "Сформирован";
		case 2:
			return "Одобрен call центром";
		case 3:
			return "Сформирован на складе";
		case 4:
			return "В доставке";
		case 5:
			return "Доставлен";
		case 100:
			return "Отменен";
		case 110:
			return "Не установлен";
		default:
			return "Не установлен";
		}
		
	}
	
	private static class ViewHolder{
		int position;
		TextView date;
		TextView number;
		TextView status;
	}
	
}