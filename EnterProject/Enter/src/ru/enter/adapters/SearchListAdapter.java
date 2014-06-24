package ru.enter.adapters;


import java.util.ArrayList;

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
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * 
 * @author ideast
 *
 */
public class SearchListAdapter extends ArrayAdapter<ProductBean>{
	private Context context;

	/**
	 * Конструктор     
	 * @param handler контекст
	 * @param textViewResourceId layout
	 * @param messages сообщения в лист
	 */
	private ImageDownloader imageDownloader;
	private LayoutInflater viewInflater;
	public SearchListAdapter(Context handler, int textViewResourceId, ArrayList<ProductBean> messages) {
		super(handler, textViewResourceId);
		add(messages);

		this.context = handler;
		this.imageDownloader = new ImageDownloader(handler);
		viewInflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}
	
	/**
	 * добавляем в лист
	 * @param messages что добавляем
	 */
	private void addToList(ArrayList<ProductBean> messages) {
		for(ProductBean message : messages){
			add(message);
		}
	}
	
	@Override
	public void clear() {
		super.clear();
	}

	/**
	 * Добавляет записи из messages в лист источник данных для ListView. Отображаются не все
	 *  добавленные записи, а только удовлетворяющие фильтру
	 * @param messages - список записей для добавления
	 */
	public void add(ArrayList<ProductBean> messages) {
		addToList(messages);
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View view = viewInflater.inflate(R.layout.search_list_row, null);

		ProductBean bean = getItem(position);

		TextView title = (TextView) view.findViewById(R.id.search_row_title);
		TextView price = (TextView)view.findViewById(R.id.search_row_price);
		TextView price_sym = (TextView)view.findViewById(R.id.search_row_price_sym);
		TextView oldPrice = (TextView)view.findViewById(R.id.search_list_row_old_price);
		TextView oldRub = (TextView)view.findViewById(R.id.search_list_row_old_price_symbol);
		TextView discount = (TextView)view.findViewById(R.id.search_list_row_discount);
		TextView exist = (TextView)view.findViewById(R.id.search_row_exist);

		ImageView foto = (ImageView)view.findViewById(R.id.search_list_row_image);
		ImageView tag = (ImageView)view.findViewById(R.id.search_list_row_option);

		title.setText(bean.getName());
		price.setText(bean.getPriceFormattedString());

		price_sym.setTypeface(Utils.getRoubleTypeFace(context));
		price_sym.setText(" p");
		setExistText(exist, bean);
		
		price.setTypeface(Utils.getTahomaTypeFace(context));
		oldPrice.setTypeface(Utils.getTahomaTypeFace(context));
		discount.setTypeface(Utils.getTahomaTypeFace(context));

		double oldPriceValue = bean.getPrice_old();

		if(oldPriceValue > 0){
			oldPrice.setVisibility(View.VISIBLE);
			oldRub.setVisibility(View.VISIBLE);
			discount.setVisibility(View.VISIBLE);

			oldPrice.setText(bean.getOldPriceFormattedString());
			oldRub.setTypeface(Utils.getRoubleTypeFace(context));
			discount.setText(Discount.getDiscount(bean.getPrice(), oldPriceValue));

			oldPrice.setPaintFlags(price.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
			oldRub.setPaintFlags(price.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);

			price.setTextColor(context.getResources().getColor(R.color.red));
			price.setTextSize(14);
			price_sym.setTextColor(context.getResources().getColor(R.color.red));
			price_sym.setTextSize(14);
		}

		try {
			int substitution = Utils.getDpiForItemList(context);
			String fotoUrl = bean.getFoto().replaceAll("__media_size__",String.valueOf(substitution));
			imageDownloader.download(fotoUrl, foto);
		} catch (Exception e) {
		}
		try {
			imageDownloader.download(bean.getLabel().get(0).getFoto(context), tag);
		} catch (Exception e) {
		}

		view.setTag(bean);
		return view;
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
