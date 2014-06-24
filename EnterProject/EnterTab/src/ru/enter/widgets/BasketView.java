package ru.enter.widgets;

import ru.enter.BasketActivity;
import ru.enter.R;
import ru.enter.DataManagement.BasketManager;
import ru.enter.beans.ProductBean;
import ru.enter.beans.ServiceBean;
import ru.enter.dialogs.CatalogEmptyBasketDialogFragment;
import ru.enter.interfaces.OnBasketChangeListener;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.FrameLayout;
import android.widget.TextView;

public class BasketView extends FrameLayout implements OnClickListener, OnBasketChangeListener{

	private TextView mBasketSum;

	public BasketView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs);
		
		LayoutInflater inflater = LayoutInflater.from(context);
		inflater.inflate(R.layout.basket_view, this, true);
		
		mBasketSum = (TextView) findViewById(R.id.basket_view_price);
		
		setOnClickListener(this);
		BasketManager.setOnBasketChangeListener(this);
		onBasketChange();
	}

	public BasketView(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public BasketView(Context context) {
		this(context, null);
	}

	@Override
	public void onBasketChange() {
		int totalPrice = 0;
		
		for (ProductBean product : BasketManager.getProducts()) {
			totalPrice += (product.getPrice() * product.getCount());
		}
		
		for (ServiceBean service : BasketManager.getServicesAll()) {
			totalPrice += (service.getPrice() * service.getCount());
		}
		
		if (totalPrice > 0) {
			mBasketSum.setVisibility(View.VISIBLE);
			mBasketSum.setText(String.valueOf(totalPrice));
		} else {
			mBasketSum.setVisibility(View.INVISIBLE);
		}
	}

	@Override
	public void onClick(View v) {
		Activity activity = (Activity) getContext();
		// Если корзаина пуста
		if (BasketManager.isEmpty()) {
			CatalogEmptyBasketDialogFragment empty_basket_dialog = new CatalogEmptyBasketDialogFragment().getInstance();
			empty_basket_dialog.show(activity.getFragmentManager(), "basket_empty_dialog");
		} else {
			Intent intent = new Intent().setClass(activity, BasketActivity.class);
			activity.startActivity(intent);
		}
	}

}
