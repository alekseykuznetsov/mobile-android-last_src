package ru.enter.adapters;

import java.util.ArrayList;
import java.util.List;

import ru.enter.R;
import ru.enter.ImageManager.ImageDownloader;
import ru.enter.beans.ProductBean;
import ru.enter.utils.Discount;
import ru.enter.utils.PreferencesManager;
import ru.enter.utils.Utils;
import android.content.Context;
import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class ProductListAdapter extends BaseAdapter {

	private List<ProductBean> mObjects;

	private Context mContext;
	private ImageDownloader downloader;

	private LayoutInflater viewInflater;

	private boolean mIsLoad = true;

	public ProductListAdapter(Context context, int textViewResourceId) {

		mContext = context;		
		mObjects = new ArrayList<ProductBean>();
		downloader = new ImageDownloader(context);
		downloader.setMaxThreads(3);
		viewInflater = (LayoutInflater)this.mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}

	public void setObjects (List<ProductBean> objects) {
		if (objects == null) {
			objects = new ArrayList<ProductBean>();
		}

		mObjects = objects;
		downloader.OrderClear();
		notifyDataSetChanged();
	}

	public boolean addObjects (List<ProductBean> objects) {
		boolean result = false;

		if ( ! mObjects.containsAll(objects)){
			mObjects.addAll(objects);
			result = true;
			notifyDataSetChanged();
		}

		return result;
	}

	public void clearObjects () {
		mObjects = new ArrayList<ProductBean>();
		notifyDataSetChanged();
	}

	public void setLoad(boolean isLoad) {
		downloader.OrderClear();
		mIsLoad = isLoad;
	}

	public int getCount() {
		return mObjects.size();
	}

	public ProductBean getItem(int position) {
		return mObjects.get(position);
	}

	@Override
	public final View getView(int position, View convertView, ViewGroup parent) {
		ProductBean bean = getItem(position);
		return getView(position, convertView, bean);
	}



	public Context getContext() {
		return mContext;
	}

	public LayoutInflater getInflater() {
		return viewInflater;
	}

	public List<ProductBean> getObjects() {
		return mObjects;
	}

	@Override
	public long getItemId(int position) {
		return getItem(position).getId();
	}

	public View getView(int position, View convertView, ProductBean bean) {
		View v = viewInflater.inflate(R.layout.product_list_element_grid, null); 

		TextView title = (TextView)v.findViewById(R.id.product_list_el_grid_title);
		TextView price = (TextView)v.findViewById(R.id.product_list_el_grid_price);
		TextView rub = (TextView)v.findViewById(R.id.product_list_el_grid_price_symbol);
		TextView oldPrice = (TextView)v.findViewById(R.id.product_list_el_grid_old_price);
		TextView oldRub = (TextView)v.findViewById(R.id.product_list_el_grid_old_price_symbol);
		TextView discount = (TextView)v.findViewById(R.id.product_list_el_grid_discount);
		ImageView iw = (ImageView)v.findViewById(R.id.product_list_el_grid_img);
		ImageView tag = (ImageView)v.findViewById(R.id.product_list_el_grid_option);
		TextView exist = (TextView)v.findViewById(R.id.product_list_el_grid_exist);

		title.setText(bean.getName());
		price.setText(bean.getPriceFormattedString());
		rub.setTypeface(Utils.getRoubleTypeFace(mContext));
		rub.setText(" p");
		iw.setImageResource(R.drawable.cap);
		
		price.setTypeface(Utils.getTahomaTypeFace(mContext));
		oldPrice.setTypeface(Utils.getTahomaTypeFace(mContext));
		discount.setTypeface(Utils.getTahomaTypeFace(mContext));

		double oldPriceValue = bean.getPrice_old();
//		oldPriceValue  = 10000;

		if(oldPriceValue > 0){
			oldPrice.setVisibility(View.VISIBLE);
			oldRub.setVisibility(View.VISIBLE);
			discount.setVisibility(View.VISIBLE);

			oldPrice.setText(bean.getOldPriceFormattedString());
			oldRub.setTypeface(Utils.getRoubleTypeFace(mContext));
//			oldRub.setText(" р");
			discount.setText(Discount.getDiscount(bean.getPrice(), oldPriceValue));

			oldPrice.setPaintFlags(price.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
			oldRub.setPaintFlags(price.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);

			price.setTextColor(mContext.getResources().getColor(R.color.red));
			price.setTextSize(14);
			rub.setTextColor(mContext.getResources().getColor(R.color.red));
			rub.setTextSize(14);
		}

		setExistText(exist, bean);
		if (mIsLoad) {
			try {
				int substitution = Utils.getDpiForItemList(mContext);
				String foto = bean.getFoto().replaceAll("__media_size__",
						String.valueOf(substitution));
				downloader.download(foto, iw);
			} catch (Exception e) {
			}
			try {
				downloader.download(bean.getLabel().get(0).getFoto(mContext),
						tag);
			} catch (Exception e) {
			}
		}
		v.setTag(bean);

		return v;
	}

	private void setExistText(TextView text, ProductBean bean){
		String exist_txt = "нет в наличиии";
		int is_shop = bean.getShop();
		int scope_store_qty = bean.getScopeStoreQty();
		int scope_shops_qty = bean.getScopeShopsQty();
		int scope_shops_qty_showroom = bean.getScopeShopsQtyShowroom();
		int buyable = bean.getBuyable();
		boolean in_shop = (PreferencesManager.getUserCurrentShopId()!=0?true:false);
		if (in_shop && buyable == 1 && scope_shops_qty_showroom > 0) {
			exist_txt = "есть на витрине";
			text.setTextColor(android.graphics.Color.rgb(122, 111, 149));
		} else {
			if (buyable == 1) {
				exist_txt = "";
				text.setTextColor(android.graphics.Color.BLACK);
			} else {
				if (is_shop == 1 && scope_store_qty == 0 && scope_shops_qty > 0) {
					exist_txt = "только в магазинах";
					text.setTextColor(android.graphics.Color.rgb(122, 111, 149));
				} else {
					if (scope_shops_qty == 0 && scope_shops_qty_showroom > 0) {
						exist_txt = "только на витрине";
						text.setTextColor(android.graphics.Color.rgb(122, 111,149));
					} else {
						if (scope_shops_qty == 0) {
							exist_txt = "нет в наличии";
							text.setTextColor(android.graphics.Color.rgb(165,74, 73));
						}
					}
				}
			}
		}
		text.setText(exist_txt);
	}

}