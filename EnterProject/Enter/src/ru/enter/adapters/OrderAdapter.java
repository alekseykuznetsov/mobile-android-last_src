package ru.enter.adapters;

import ru.enter.R;
import ru.enter.beans.ServiceBean;
import ru.enter.interfaces.IBasketElement;
import ru.enter.utils.Utils;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

public class OrderAdapter extends BaseOrderAdapter{

	public OrderAdapter(Context context) {
		super(context);
	}

	@Override
	View getRelatedServiceView(ServiceBean bean, LayoutInflater inflater) {
		View view = inflater.inflate(R.layout.checkout_third_step_related_row, null);
		
		TextView title = (TextView)view.findViewById(R.id.checkout_third_step_related_row_title);
		title.setText(bean.getName());
		
		TextView count = (TextView)view.findViewById(R.id.checkout_third_step_related_row_num);
		count.setText(bean.getCount()+" шт.");
		
		TextView price = (TextView)view.findViewById(R.id.checkout_third_step_related_row_price);
		price.setTypeface(Utils.getRoubleTypeFace(inflater.getContext()));
		price.setText(String.format(" %,.0f p",bean.getPrice()).replace(",", " "));// bean.getCount()*
		
		return view;
	}

	@Override
	View getProductOrServiceView(IBasketElement bean, LayoutInflater inflater) {
		View view = inflater.inflate(R.layout.checkout_third_step_row, null);
		
		ImageView imageView = (ImageView)view.findViewById(R.id.checkout_third_step_row_image);
		
		getImageLoader().download(bean.getFoto(), imageView);
		
		TextView name = (TextView)view.findViewById(R.id.checkout_third_step_row_product_name);
		name.setText(bean.getName());
		
		TextView count = (TextView)view.findViewById(R.id.checkout_third_step_row_count);
		count.setText(bean.getCount()+" шт.");
		
		TextView price = (TextView)view.findViewById(R.id.checkout_third_step_row_price);
		price.setTypeface(Utils.getRoubleTypeFace(inflater.getContext()));
		price.setText(String.format(" %,.0f p", bean.getPrice()).replace(",", " "));//bean.getCount()*
		
		return view;
	}
}
