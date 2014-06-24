package ru.enter.adapters;

import java.util.ArrayList;
import java.util.List;

import ru.enter.R;
import ru.enter.beans.CitiesBean;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

public class CitiesListAdapter extends BaseAdapter implements Filterable {

	private static final int SEPARATOR = 0;
	private static final int CITY = 1;
	
	private static final int HOLDER_TAG = R.string.cities_holder_tag;
	public static final int BEAN_TAG = R.string.cities_bean_tag;

	//первоначальный список
	private List<CitiesBean> mObjects;
	//отсортированный список
	private List<CitiesBean> mSortedObjects;
	private LayoutInflater mInflater;
	//для синхронизации
	private Object mLock = new Object();

	public CitiesListAdapter(Context context) {
		mInflater = LayoutInflater.from(context);
		mObjects = mSortedObjects = new ArrayList<CitiesBean>();
	}

	public void setObjects(List<CitiesBean> objects) {
		if (objects == null) {
			objects = new ArrayList<CitiesBean>();
		}

		mObjects = objects;
		init(mObjects);
		notifyDataSetChanged();
	}
	
	/**
	 * Ищет по id города его позицию в листе
	 * @param mCurrentCityId id текущего города
	 * @return позиция которую нужно отметить. 
	 * Если массив пустой возвращает -1
	 */
	public int getPositionToCheck(int currentCityId) {
		for (int i = 0; i < mSortedObjects.size(); i++){
			if(mSortedObjects.get(i).getId() == currentCityId)
				return i;
		}
		
		return -1;
	}

	/**
	 * Сортирует объекты, добавляет разделы с названием "первая буква города"
	 * @param objects
	 */
	private void init(List<CitiesBean> objects){
		
		mSortedObjects = new ArrayList<CitiesBean>();
		
		char currentChar = 0;
		for(CitiesBean bean : objects){
			char newChar = bean.getName().charAt(0);
			if( newChar != currentChar ){
				currentChar = newChar;
				//add separator
				CitiesBean separator = new CitiesBean();
				separator.setName(String.valueOf(currentChar));
				mSortedObjects.add(separator);
			}
				//add item
				mSortedObjects.add(bean);
			}
	}

	@Override
	public int getCount() {
		return mSortedObjects.size();
	}

	@Override
	public CitiesBean getItem(int position) {
		return mSortedObjects.get(position);
	}

	@Override
	public long getItemId(int position) {
		return mSortedObjects.get(position).getId();
	}

	@Override
	public int getViewTypeCount() {
		return 2;
	}

	@Override
	public int getItemViewType(int position) {
		return getItem((position)).getId() == 0 ? SEPARATOR : CITY;
	}

	@Override
	public boolean areAllItemsEnabled() {
		return false;
	}

	@Override
	public boolean isEnabled(int position) {
		return getItemViewType(position) == CITY;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder = null;
		CitiesBean bean = getItem(position);
		int type = getItemViewType(position);
		if (convertView == null) {
			holder = new ViewHolder();
			switch (type) {
			case SEPARATOR:
				convertView = mInflater.inflate(R.layout.cities_dialog_fr_list_separator, null);
				holder.separatorName = (TextView) convertView;
				break;
			case CITY:
				convertView = mInflater.inflate(R.layout.cities_dialog_fr_list_row, null);
				holder.cityName = (TextView) convertView.findViewById(R.id.cities_dialog_fr_list_row_name);
				break;
			}
			convertView.setTag(HOLDER_TAG, holder);
		} else {
			holder = (ViewHolder) convertView.getTag(HOLDER_TAG);
		}

		holder.setCityName(bean.getName());
		holder.setSeparatorName(bean.getName());
		
		convertView.setTag(BEAN_TAG, bean);

		return convertView;
	}

	private static class ViewHolder {
		public TextView cityName;
		public TextView separatorName;

		public void setCityName(String name) {
			if (cityName != null)
				cityName.setText(name);
		}

		public void setSeparatorName(String name) {
			if (separatorName != null)
				separatorName.setText(name);
		}
		
	}

	/**
	 * Возвращает мой фильтр
	 */
	@Override
	public Filter getFilter() {
		return new ArrayFilter();
	}
	
	private class ArrayFilter extends Filter{

		@Override
		protected FilterResults performFiltering(CharSequence constraint) {
			FilterResults results = new FilterResults();
			
			if (constraint == null || constraint.length() == 0) {
				results.values = mObjects;
				results.count = mObjects.size();
				
			} else {
				String prefix = constraint.toString().toLowerCase();
				
				final ArrayList<CitiesBean> preValues = new ArrayList<CitiesBean>(mObjects);
				final int preCount = preValues.size();
				
				ArrayList<CitiesBean> postValues = new ArrayList<CitiesBean>();
				
				for (int i = 0; i < preCount; i++){
					CitiesBean city = preValues.get(i);
					String cityName = city.getName().toLowerCase();
					
					//ищет чтобы слово начиналось с введенной строчки
					if (cityName.startsWith(prefix)) {
						postValues.add(city);
					}
				}
				
				results.values = postValues;
				results.count = postValues.size();
			}
			
			return results;
		}

		@SuppressWarnings("unchecked")
		@Override
		protected void publishResults(CharSequence constraint, FilterResults result) {
			
			init((List<CitiesBean>) result.values);
			
            if (result.count > 0) {
                notifyDataSetChanged();
            } else {
                notifyDataSetInvalidated();
            }
		}
	}

}
