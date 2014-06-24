package ru.enter.adapters;

import ru.enter.BasketActivity;
import ru.enter.R;
import ru.enter.DataManagement.BasketManager;
import ru.enter.beans.ServiceBean;
import ru.enter.interfaces.IBasketElement;
import ru.enter.utils.Utils;
import ru.enter.widgets.NumberPicker;
import ru.enter.widgets.NumberPicker.OnNumberChangedListener;
import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class BasketAdapter extends BaseOrderAdapter {

	private OnClickListener mOnClickListener;

	public BasketAdapter(Context context) {
		super(context);
	}
	
	public BasketAdapter(Context context, OnClickListener listener) {
		super(context);
		mOnClickListener = listener;
	}

	@Override
	View getRelatedServiceView(final ServiceBean bean, final LayoutInflater inflater) {
		View view = inflater.inflate(R.layout.basket_list_service_row, null);		
		
		TextView title = (TextView) view.findViewById(R.id.basket_list_service_row_title);
		NumberPicker num = (NumberPicker) view.findViewById(R.id.basket_list_service_row_number_picker);
		TextView price = (TextView) view.findViewById(R.id.basket_list_service_row_price);
		ImageButton delete = (ImageButton) view.findViewById(R.id.basket_list_row_button_delete);
		
		title.setText(bean.getName());
		num.setRange(1, 20);
		num.setCurrent(bean.getCount());
		price.setTypeface(Utils.getRoubleTypeFace(inflater.getContext()),Typeface.BOLD);
		String priceString = String.format(" %,.0f", bean.getPrice()).replace(",", " ");
		price.setText(priceString + " p");	
		
		delete.setTag(bean);
		
		delete.setOnClickListener(mOnClickListener);
		num.setOnNumberChangedListener(new OnNumberChangedListener() {
			
			@Override
			public void onChanged(int newNumber) {
				bean.setCount(newNumber);
				BasketManager.notifyListeners();
				BasketActivity activity = (BasketActivity) inflater.getContext();
				activity.refreshFooterData();//TODO
			}
		});
		
		return view;
	}

	@Override
	View getProductOrServiceView(final IBasketElement bean, final LayoutInflater inflater) {
		View view = inflater.inflate(R.layout.basket_list_row, null);
		
		RelativeLayout frame = (RelativeLayout) view.findViewById(R.id.basket_list_row_frame);
		
		frame.setTag(bean);
		frame.setOnClickListener(mOnClickListener);
		
		TextView title = (TextView) view.findViewById(R.id.basket_list_row_title);
		NumberPicker num = (NumberPicker) view.findViewById(R.id.basket_list_row_number_picker);
		TextView price = (TextView) view.findViewById(R.id.basket_list_row_price);
		ImageView image = (ImageView)view.findViewById(R.id.basket_list_row_image);
		ImageButton delete = (ImageButton) view.findViewById(R.id.basket_list_row_button_delete);
		
		title.setText(bean.getName());
		num.setRange(1, 20);
		num.setCurrent(bean.getCount());
		price.setTypeface(Utils.getRoubleTypeFace(inflater.getContext()),Typeface.BOLD);
		String priceString = String.format(" %,.0f", bean.getPrice()).replace(",", " ");
		price.setText(priceString + " p");	
		
		getImageLoader().download(bean.getFoto(), image);
		
		delete.setTag(bean);
		
		delete.setOnClickListener(mOnClickListener);
		num.setOnNumberChangedListener(new OnNumberChangedListener() {
			
			@Override
			public void onChanged(int newNumber) {
				bean.setCount(newNumber);
				BasketManager.notifyListeners();
				BasketActivity activity = (BasketActivity) inflater.getContext();
				activity.refreshFooterData();//TODO
			}
		});
		
		return view;
	}
	
	public void setOnClickListener (OnClickListener clickListener) {
		mOnClickListener = clickListener;
	}

}
