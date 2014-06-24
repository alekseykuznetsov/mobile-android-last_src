package ru.enter.utils;

import java.util.ArrayList;

import ru.enter.R;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

public class ActionbarSpinnerAdapter extends ArrayAdapter<ActionbarMenuItem> {

	Context context;
	int layoutResourceId;
	ArrayList<ActionbarMenuItem> data;
	LayoutInflater inflater;
	
	public ActionbarSpinnerAdapter(Context a, int textViewResourceId,ArrayList<ActionbarMenuItem> data) {
		super(a, textViewResourceId);
		this.data = data;
		inflater = (LayoutInflater) a.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		this.context = a;
		this.layoutResourceId = textViewResourceId;
	}
		
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View v = inflater.inflate(R.layout.actionbar_spinner_header, null);
		//TextView text = (TextView) v.findViewById(R.id.actionbar_dropdown_text);
		TextView text = (TextView) v.findViewById(android.R.id.text1);
		final ActionbarMenuItem item = data.get(position);
		if (item != null) {
			text.setText(item.text);
			Drawable icon = context.getResources().getDrawable(item.icon);
			text.setCompoundDrawablesWithIntrinsicBounds(icon, null,null, null);
			//holder.icon.setImageResource(item.icon);
		}
		return v;
        //return super.getView(position, convertView, parent);   
	} 
	
	@Override
	public View getDropDownView(int position, View convertView, ViewGroup parent) {
		View v = convertView;
		ViewHolder holder;
		if (v == null) {
			v = inflater.inflate(R.layout.actionbar_dropdown, null);
			//v = inflater.inflate(android.R.layout.simple_spinner_item, null);
			holder = new ViewHolder();
			holder.text = (TextView) v.findViewById(R.id.actionbar_dropdown_text);
			//holder.text = (TextView) v.findViewById(android.R.id.text1);
			//holder.icon = (ImageView) v.findViewById(R.id.actionbar_dropdown_icon);
			v.setTag(holder);
		} else
			holder = (ViewHolder) v.getTag();

		final ActionbarMenuItem item = data.get(position);
		if (item != null) {
			holder.text.setText(item.text);
			Drawable icon = context.getResources().getDrawable(item.icon);
			holder.text.setCompoundDrawablesWithIntrinsicBounds(icon, null,null, null);
			//holder.icon.setImageResource(item.icon);
		}
		//v.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
		return v;
	}
	
	@Override
	public int getCount() {
		return data.size();
	}

	@Override
	public ActionbarMenuItem getItem(int position) {
		return data.get(position);
	}

	@Override
	public long getItemId(int position) {
		return data.get(position).id;
	}
	
	static class ViewHolder {
		ImageView icon;
		TextView text;
	}
}
