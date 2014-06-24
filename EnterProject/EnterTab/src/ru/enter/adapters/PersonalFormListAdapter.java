package ru.enter.adapters;

import ru.enter.R;
import ru.enter.beans.AddressBean;
import ru.enter.utils.Formatters;
import android.content.Context;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

public class PersonalFormListAdapter extends AbstractListAdapter<AddressBean>{

	private OnClickListener mClick;

	public PersonalFormListAdapter(Context context) {
		super(context);
	}
	
	public void setOnClickListener (OnClickListener onClick) {
		mClick = onClick;
	}
	
	@Override
	public long getItemId(int position) {
		return getItem(position).getId();
	}
	
	@Override
	public View getView(int position, View convertView, AddressBean bean) {
		ViewHolder holder = null;
		
		if (convertView == null) {
			holder = new ViewHolder();

			convertView = getInflater().inflate(R.layout.personal_form_fr_list_item, null);
			holder.address = (TextView) convertView.findViewById(R.id.personal_form_fr_list_item_text_address);
			holder.edit = (Button) convertView.findViewById(R.id.personal_form_fr_text_button_edit);

			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		holder.address.setText(Formatters.createAddressString(bean));
		
		holder.edit.setTag(position);
		holder.edit.setOnClickListener(mClick);

		
		return convertView;
	}

	private static class ViewHolder {
		private TextView address;
		private Button edit;
	}

}
