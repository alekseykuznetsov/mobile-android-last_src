package ru.enter.adapters;

import java.util.ArrayList;

import ru.enter.R;
import ru.enter.beans.ShopBean;
import android.content.Context;
import android.opengl.Visibility;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class ProductCardWhereBuyListAdapter extends BaseAdapter {
	
	private LayoutInflater mInflater;
	private ArrayList<ShopBean> mShops;
	private boolean mEnableBuy;
	private OnClickListener mListener;
	private boolean mIsShowWindow;

	public ProductCardWhereBuyListAdapter(Context context, ArrayList<ShopBean> shops, int buyable, boolean isShowWindow) {
		mInflater = LayoutInflater.from(context);
		mShops = shops;
		mEnableBuy = (buyable == 1);
		mIsShowWindow = isShowWindow;
	}
	public boolean isEnabled(int position) { 
		return false; 
    } 
	
	public void setonClickListener (OnClickListener listener) {
		mListener = listener;
	}

	@Override
	public int getCount() {		
		return mShops.size();
	}

	@Override
	public ShopBean getItem(int position) {
		return mShops.get(position);
	}

	@Override
	public long getItemId(int position) {
		return getItem(position).getId();
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder = null;
		if (convertView == null) {
			holder = new ViewHolder();

			convertView = mInflater.inflate(R.layout.product_card_tab_where_buy_list_item, null);

			holder.buttonBuy = (Button) convertView.findViewById(R.id.product_card_tab_where_buy_list_item_button_buy);
			holder.countText = (TextView) convertView.findViewById(R.id.product_card_tab_where_buy_list_item_text_count);
			holder.address = (TextView) convertView.findViewById(R.id.product_card_tab_where_buy_list_item_text_address);
			holder.countImage = (ImageView) convertView.findViewById(R.id.product_card_tab_where_buy_list_item_img_count);

			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		ShopBean bean = getItem(position);
		
		if (mIsShowWindow && bean.getQuantityShowroom() > 0){
			holder.countImage.setVisibility(View.GONE);
		}
		else{
			int stick = bean.getStick();
			holder.countText.setText(getStickText(stick));
			holder.countImage.setImageResource(getStickDrawableId(stick));
		}
		holder.address.setText(bean.getAddress());

		holder.buttonBuy.setEnabled(mEnableBuy);
		if (mEnableBuy){
			holder.buttonBuy.setTag(bean);
			holder.buttonBuy.setOnClickListener(mListener);
		}
		
		return convertView;
	}
	
	
	
	//кол-во товара на складе
	private String getStickText(int stick){
		String result = "";
		switch (stick) {
		case 0:
			result = "Нет";
			break;
		case 1:
			result = "Мало";
			break;
		case 2:
			result = "Средне";
			break;
		case 3:
			result = "Много";
			break;
		default:
			break;
		}
		
		return result;
	}
	
	private int getStickDrawableId(int stick) {
		int result;
		switch (stick) {
		case 0:
			result = R.drawable.product_card_icn_tab_where_buy_count_0;
			break;
		case 1:
			result = R.drawable.product_card_icn_tab_where_buy_count_1;
			break;
		case 2:
			result = R.drawable.product_card_icn_tab_where_buy_count_2;
			break;
		case 3:
			result = R.drawable.product_card_icn_tab_where_buy_count_3;
			break;
		default:
			result = R.drawable.product_card_icn_tab_where_buy_count_0;
			break;
		}
		
		return result;
	}
	
	private static class ViewHolder {
		private Button buttonBuy;
		private TextView countText;
		private TextView address;
		private ImageView countImage;

	}
	
}
