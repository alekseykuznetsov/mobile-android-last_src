package ru.enter.adapters;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import ru.enter.R;
import ru.enter.ImageManager.ImageDownloader;
import ru.enter.interfaces.IBasketElement;
import ru.enter.utils.Converter;
import ru.enter.utils.Utils;
import android.content.Context;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class MyOrdersBottomAdapter extends BaseAdapter {
	
	private List<IBasketElement> mObjects;
	private ImageDownloader mDownloader;
	private LayoutInflater mInflater;
	private Context mContext;
	
	public MyOrdersBottomAdapter(Context context) {
		mObjects = Collections.emptyList();
		mContext = context;
		mDownloader = new ImageDownloader(context);
		mInflater = LayoutInflater.from(context);
	}
	
	public void setObjects(List<IBasketElement> objects){
		if(objects == null){
			mObjects = Collections.emptyList();
		}else{
			mObjects = objects;
		}
		
		notifyDataSetChanged();
	}
	
	@Override
	public int getCount() {
		return mObjects.size();
	}

	@Override
	public IBasketElement getItem(int position) {
		return mObjects.get(position);
	}

	@Override
	public long getItemId(int position) {
		return getItem(position).getId();
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		
		ViewHolder holder;
		
		if(convertView == null){
			convertView = mInflater.inflate(R.layout.my_orders_product_list_row, null);
		
			holder = new ViewHolder();
			
			holder.image = (ImageView) convertView.findViewById(R.id.my_orders_product_list_row_image);
			holder.num = (TextView) convertView.findViewById(R.id.my_orders_product_list_row_num);
			holder.price = (TextView) convertView.findViewById(R.id.my_orders_product_list_row_price);
			holder.title = (TextView) convertView.findViewById(R.id.my_orders_product_list_row_title);
			holder.symbol = (TextView) convertView.findViewById(R.id.my_orders_product_list_row_price_symbol);
			
			convertView.setTag(holder);
			
		}else{
			
			holder = (ViewHolder) convertView.getTag();
			
		}	
			
		IBasketElement bean = getItem(position);
		
		holder.num.setText(Html.fromHtml("Количество: <b>" + String.valueOf(bean.getCount() + " шт.</b>")));
		holder.price.setText(Converter.priceFormat(bean.getPrice() * bean.getCount()));
		holder.symbol.setTypeface(Utils.getRoubleTypeFace(mContext));
		holder.title.setText(bean.getName());
		mDownloader.download(bean.getFoto(), holder.image);
		
		return convertView;
	}
	
	private static class ViewHolder{
		ImageView image;
		TextView num;
		TextView price;
		TextView title;
		TextView symbol;
	}

}

