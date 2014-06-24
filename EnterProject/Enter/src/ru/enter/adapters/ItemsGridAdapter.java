package ru.enter.adapters;

import java.util.ArrayList;

import ru.enter.R;
import ru.enter.ImageManager.ImageDownloader;
import ru.enter.beans.ProductBean;
import ru.enter.utils.PreferencesManager;
import ru.enter.utils.Utils;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class ItemsGridAdapter  extends ArrayAdapter<ProductBean> {
    
	private Context mContext;
    private ImageDownloader downloader;
    private LayoutInflater viewInflater;
	public ItemsGridAdapter(Context context, int textViewResourceId) {
		super(context, textViewResourceId);
		mContext = context;
		downloader = new ImageDownloader(context);
		downloader.setMaxThreads(6);
		viewInflater = (LayoutInflater)this.mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}
	public void addNew(ArrayList<ProductBean> bean)
	{
		for(ProductBean b : bean)
			add(b);
	}

	@Override
	public int getCount() {
		return super.getCount();
	}
	@Override
	public ProductBean getItem(int position) {
		return super.getItem(position);
	}
    
    public View getView(int position, View convertView, ViewGroup parent) {
		View v = viewInflater.inflate(R.layout.items_lst_element_grid, null);  
		ProductBean bean = getItem(position);
		TextView title = (TextView)v.findViewById(R.id.items_list_el_grid_title);
		TextView price = (TextView)v.findViewById(R.id.items_list_el_grid_price);
		TextView rub = (TextView)v.findViewById(R.id.items_list_el_grid_price_symbol);
		ImageView iw = (ImageView)v.findViewById(R.id.items_list_el_grid_img);
		ImageView tag = (ImageView)v.findViewById(R.id.items_list_el_grid_option);
		TextView exist = (TextView)v.findViewById(R.id.items_list_el_grid_exist);
		
		title.setText(bean.getShortname());
		price.setText(bean.getPriceFormattedString());
		rub.setTypeface(Utils.getRoubleTypeFace(mContext));
		rub.setText(" p");
		iw.setImageResource(R.drawable.cap);
		
		setExistText(exist, bean);
		
		try{
			int substitution = Utils.getDpiForItemList(mContext);
			String foto = bean.getFoto().replaceAll("__media_size__", String.valueOf(substitution));
			downloader.download(foto, iw);
		}catch(Exception e){}
		try {
			downloader.download(bean.getLabel().get(0).getFoto(mContext), tag);
		} catch (Exception e) {}
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
