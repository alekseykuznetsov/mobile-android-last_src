package ru.enter.adapters;

import android.content.Context;
import android.view.View;
import android.widget.TextView;
import ru.enter.R;
import ru.enter.beans.ShopBean;

public class ShopListAdapter extends AbstractListAdapter<ShopBean>{

	public ShopListAdapter(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
	}

	@Override
	public long getItemId (int position) {
		return getItem(position).getId();
	}

	@Override
	public View getView (int position, View convertView, ShopBean bean) {
		
    	if (convertView == null) {
            convertView = (TextView) getInflater().inflate(R.layout.order_merge_spinner_address_item, null);
        }
    	TextView address = (TextView) convertView.findViewById(R.id.order_merge_spinner_address_text);
  
    	address.setText(bean.getName());

    	return convertView;
	}

}
