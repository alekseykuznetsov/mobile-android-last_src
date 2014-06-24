package ru.enter.utils;

import java.util.ArrayList;

import ru.enter.R;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class NavigationViewAdapter extends ArrayAdapter<ActionbarMenuItem> {

	Context context;
	ArrayList<ActionbarMenuItem> data;
	LayoutInflater inflater;
	OnBarNavigationListener<ActionbarMenuItem> mNavigationListener;

	public NavigationViewAdapter(Context a) {
		this(a,null);
	}
	
	public NavigationViewAdapter(Context a,ArrayList<ActionbarMenuItem> data) {
		super(a, 0);
		this.data = data;
		this.context = a;
		inflater = (LayoutInflater) a.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		TextView v = (TextView) inflater.inflate(R.layout.actionbar_dropdown, null);
		final int itemPosition = position;
		final ActionbarMenuItem item = data.get(position);
		if (item != null) {
			v.setText(item.text);
			Drawable icon = context.getResources().getDrawable(item.icon);
			v.setCompoundDrawablesWithIntrinsicBounds(icon, null,null, null);
		}
		v.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View itemView) {
				if (mNavigationListener != null) {
					mNavigationListener.onNavigationBarItemSelected(itemPosition, item, itemView);
				}
			}
		});
		return v;
	}
	

	public ArrayList<ActionbarMenuItem> getData() {
		return data;
	}

	public void setData(ArrayList<ActionbarMenuItem> data) {
		this.data = data;
	}

	@Override
	public int getCount() {
		return data==null ? 0 : data.size();
	}

	@Override
	public ActionbarMenuItem getItem(int position) {
		return data==null ? null : data.get(position);
	}

	@Override
	public long getItemId(int position) {
		return data==null ? 0 : data.get(position).id;
	}

	public void setNavigationListener(OnBarNavigationListener<ActionbarMenuItem> mNavigationListener) {
		this.mNavigationListener = mNavigationListener;
	}


}
