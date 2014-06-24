package ru.enter.adapters;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import ru.enter.R;
import ru.enter.beans.OrderBean;
import ru.enter.utils.Converter;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class MyOrdersAdapter extends BaseAdapter {
	
	private Context context;
	private ArrayList<OrderBean> mObjects;
	
	public MyOrdersAdapter(Context context) {
		this.context = context;
		mObjects = new ArrayList<OrderBean>();
	}
	
	public void setObjects(ArrayList<OrderBean> objects){
		if(objects!=null&&!objects.isEmpty()){
			Collections.sort(objects, mComparator);
			mObjects = objects;
			
		}
		else
			mObjects = new ArrayList<OrderBean>();
		notifyDataSetChanged();
	}
	
	@Override
	public int getCount() {		
		return mObjects.size();
	}

	@Override
	public OrderBean getItem(int position) {
		return mObjects.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		LayoutInflater viewInflater = (LayoutInflater)this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View view = viewInflater.inflate(R.layout.personal_acc_my_orders_list_row, null);
		
		TextView date = (TextView) view.findViewById(R.id.personal_acc_my_orders_list_row_date);
		TextView number = (TextView) view.findViewById(R.id.personal_acc_my_orders_list_row_number);
		TextView status = (TextView) view.findViewById(R.id.personal_acc_my_orders_list_row_status);
		
		OrderBean bean = getItem(position);
		
		date.setText("Заказ от " + Converter.fromLineToDot(bean.getAddedDate()));
		number.setText("№ " + bean.getNumber());
		status.setText(getStatusName(bean.getStatus_id()));

		return view;
	}
	
	public static String getStatusName(int id){
		switch (id) {
		case 1:
			return "Сформирован";
		case 2:
			return "Подтвержден";
		case 4:
			return "Передан в доставку";
		case 5:
			return "Получен";
		case 100:
			return "Отменен";
		default:
			return "Не установлен";
		}
		
	}
	
	static Comparator<OrderBean> mComparator = new Comparator<OrderBean>() {
		public int compare(OrderBean first, OrderBean second) {
			long dateFirst = first.getAddedDateToCompare();
			long dateSecond = second.getAddedDateToCompare();
			if(dateFirst < dateSecond) return 1;
			else if (dateFirst > dateSecond) return -1;
			else return 0;
		};
	};

}

