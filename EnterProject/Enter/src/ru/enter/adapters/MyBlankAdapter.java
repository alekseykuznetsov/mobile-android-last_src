package ru.enter.adapters;

import java.util.ArrayList;

import ru.enter.PersonalForm;
import ru.enter.R;
import ru.enter.beans.AddressBean;
import ru.enter.utils.Formatters;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;

public class MyBlankAdapter extends BaseAdapter {
	
	private Context context;
	private ArrayList<AddressBean> mObjects;
	
	public MyBlankAdapter(Context context) {
		this.context = context;
		mObjects = new ArrayList<AddressBean>();
	}
	
	public void clean(){
		mObjects = new ArrayList<AddressBean>();
		notifyDataSetChanged();
	}
	
	public void setObjects(ArrayList<AddressBean> objects){
		if (objects != null && !objects.isEmpty()){
			mObjects = objects;
			notifyDataSetChanged();
		}
	}
	
	public void addRow(AddressBean bean){
		if (mObjects.contains(bean)) return;//TODO equals in BEAN
		mObjects.add(bean);
		notifyDataSetChanged();
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
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		LayoutInflater viewInflater = (LayoutInflater)this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View view = viewInflater.inflate(R.layout.personal_acc_blank_list_row, null);
		TextView address = (TextView) view.findViewById(R.id.personal_acc_blank_list_row_adress);
		Button delete = (Button) view.findViewById(R.id.personal_acc_blank_list_row_delete);
		Button edit = (Button) view.findViewById(R.id.personal_acc_blank_list_row_edit);
		
		final AddressBean bean =  getItem(position);
		
		address.setText(Formatters.createAddressString(bean));
		
		OnClickListener listener = new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				switch (v.getId()) {
				
				case R.id.personal_acc_blank_list_row_delete:
					((PersonalForm) context).showDeleteDialog(bean);
					break;

				case R.id.personal_acc_blank_list_row_edit:
					((PersonalForm) context).showEditDialog(bean);
					break;
					
				default:
					break;
				}
				
			}
		};
		
		delete.setOnClickListener(listener);
		edit.setOnClickListener(listener);
		return view;
	}

}

