package ru.enter.adapters;

import java.util.ArrayList;

import ru.enter.CitiesListActivity;
import ru.enter.R;
import ru.enter.beans.CitiesBean;
import ru.enter.utils.PreferencesManager;
import ru.enter.utils.Utils;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;

public class CitiesListAdapter extends ArrayAdapter<CitiesBean> implements OnClickListener{

	private Context context;
	public CitiesListAdapter(Context context, int textViewResourceId,ArrayList<CitiesBean> bean) {
		super(context, textViewResourceId);
		this.context = context;
		
		if(bean!= null)
			addAll(bean);
	}
	public void addAll(ArrayList<CitiesBean> bean)
	{
		for(CitiesBean b : bean)
			add(b);
	}
	@Override
	public int getCount() {
		return super.getCount();
	}
	@Override
	public CitiesBean getItem(int position) {
		return super.getItem(position);
	}
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		LayoutInflater viewInflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View v = viewInflater.inflate(R.layout.cities_list_row, null);
		
		CitiesBean bean = getItem(position);
		
		CheckBox checkBox = (CheckBox)v.findViewById(R.id.cities_list_row_check);
		
		checkBox.setText(bean.getName());
		checkBox.setOnClickListener(this);
		checkBox.setTag(position);
		
		if(PreferencesManager.getCityName().equals(bean.getName()))
			checkBox.setChecked(true);
		
		
		return v;
	}
	
	@Override
	public void onClick(View v) {
		int pos = (Integer) v.getTag();
		CheckBox ch = (CheckBox) v.findViewById(R.id.cities_list_row_check);

		ch.setChecked(true);
		
		String cityName = getItem(pos).getName();
		int cityID = getItem(pos).getId();
		boolean hasShop = getItem(pos).isHasShop();
		
		Intent i = new Intent();
		i.putExtra(CitiesListActivity.CITY, cityName);
		i.putExtra(CitiesListActivity.CITY_ID, cityID);
		i.putExtra(CitiesListActivity.CITY_HAS_SHOP, hasShop);
		
		//не удержался здесь сразу ставить город, т.к. в личном кабинете были проблемы с onActivityResult()
		PreferencesManager.setCityId(cityID);
		PreferencesManager.setCityName(cityName);
		PreferencesManager.setCityHasShop(hasShop);
		
		//магазин до которого сузились в каталоге перетирается при смене города
        PreferencesManager.setUserCurrentShopId(0);
		PreferencesManager.setUserCurrentShopName("");//TODO
		PreferencesManager.setFirstStartCatalog(0);
		
		Utils.CITY_CHANGED_FLAG = true;
		((Activity)context).setResult(1, i);
		((Activity)context).finish();
		
	}
	

}
