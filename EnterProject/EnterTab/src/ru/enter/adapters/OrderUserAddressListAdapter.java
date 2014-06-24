package ru.enter.adapters;

import java.util.ArrayList;

import ru.enter.R;
import ru.enter.beans.AddressBean;
import ru.enter.utils.Formatters;
import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

public class OrderUserAddressListAdapter extends BaseAdapter implements OnClickListener {

	private ArrayList<AddressBean> mObjects;
	private LayoutInflater mInflater;
	private Handler mHandler;
	

	public OrderUserAddressListAdapter(Context context) {
		mInflater = LayoutInflater.from(context);
		mObjects = new ArrayList<AddressBean>();
	}

	public void setObjects(ArrayList<AddressBean> objects) {
		if (objects != null && !objects.isEmpty()) {
			mObjects = objects;
			notifyDataSetChanged();
		}
	}

	public void clean() {
		mObjects = new ArrayList<AddressBean>();
		notifyDataSetChanged();
	}

	public void setHandler(Handler handler) {
		mHandler = handler;
	}

	@Override
	public int getCount() {
		return mObjects.size();
	}

	@Override
	public AddressBean getItem(int position) {
		return mObjects.get(position);
	}

	@Override
	public long getItemId(int position) {
		return getItem(position).getId();
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder = null;
		if (convertView == null) {
			holder = new ViewHolder();

			convertView = mInflater.inflate(R.layout.order_dialog_address_list_item, null);

			holder.address = (TextView) convertView.findViewById(R.id.order_dialog_address_list_item_text_address);
			holder.linear = (LinearLayout) convertView.findViewById(R.id.order_dialog_address_list_item_linear);
					
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		AddressBean bean = getItem(position);

		holder.address.setText(Formatters.createAddressString(bean));
		
		holder.linear.setTag(position);
		holder.linear.setOnClickListener(this);
		
		return convertView;
	}
	
	private static class ViewHolder {
		private TextView address;
		private LinearLayout linear;
	}
	
	@Override
	public void onClick(View v) {
		if (mHandler != null) {
			int position =  (Integer) v.getTag();
			Message msg = new Message();
			msg.arg1 = position;
			mHandler.sendMessage(msg);
		}
	}
}
