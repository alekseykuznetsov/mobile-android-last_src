package ru.enter.fragments;

import ru.enter.R;
import ru.enter.ShopsActivity;
import ru.enter.beans.ShopBean;
import android.app.Fragment;
import android.os.Bundle;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class ShopBottomFragment extends Fragment{
	
	public static ShopBottomFragment getInstance(){
		return new ShopBottomFragment();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
	
		ShopsActivity mActiviy = (ShopsActivity) getActivity();
		ShopBean currentShop = mActiviy.getCurrentShop();
		
		View view = (View) inflater.inflate(R.layout.shop_fr_bottom_text, null);

		TextView description = (TextView) view
				.findViewById(R.id.shop_bottom_fr_text_description);
		description.setText(Html.fromHtml(currentShop.getDescription()));
		
		TextView way_walk = (TextView) view
				.findViewById(R.id.shop_bottom_fr_text_way_walk);
		way_walk.setText(Html.fromHtml(currentShop.getWay_walk()));

		TextView way_auto = (TextView) view
				.findViewById(R.id.shop_bottom_fr_text_way_auto);
		way_auto.setText(Html.fromHtml(currentShop.getWay_auto()));
		
		
		return view;
	}
}