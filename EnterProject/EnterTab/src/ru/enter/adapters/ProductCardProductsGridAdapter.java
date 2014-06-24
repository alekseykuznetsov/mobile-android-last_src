package ru.enter.adapters;

import java.util.ArrayList;

import ru.enter.ApplicationTablet;
import ru.enter.R;
import ru.enter.beans.ProductBean;
import ru.enter.utils.Formatters;
import ru.enter.utils.TypefaceUtils;
import ru.enter.utils.Utils;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.webimageloader.ImageLoader;
import com.webimageloader.ext.ImageHelper;

public class ProductCardProductsGridAdapter extends BaseAdapter {

	private LayoutInflater mInflater;
	private ImageHelper mImageLoader;
	private ImageHelper mImageLoaderTag;
	private ArrayList<ProductBean> mAccessories;
	private OnClickListener mListener;
	
	private boolean mIsLoad = true;
	
	public ProductCardProductsGridAdapter(Context context, ArrayList<ProductBean> accessories) {
		mInflater = LayoutInflater.from(context);
		ImageLoader loader = ApplicationTablet.getLoader(context);
		
		mImageLoader = new ImageHelper(context, loader)
        .setFadeIn(false)
        .setLoadingResource(R.drawable.tmp_1)
        .setErrorResource(R.drawable.tmp_1);//TODO
		mImageLoaderTag = new ImageHelper(context, loader)
        .setFadeIn(true);
		
		mAccessories = accessories;
	}
	
	public void setLoad(boolean isLoad) {
		mIsLoad = isLoad;
	}
	
	@Override
	public boolean areAllItemsEnabled() {
		return false;
	}

	@Override
	public boolean isEnabled(int position)
	{
	    return false;
	}

	public void setonClickListener (OnClickListener listener) {
		mListener = listener;
	}
	
	@Override
	public int getCount() {
		return mAccessories.size();
	}

	@Override
	public ProductBean getItem(int position) {
		return mAccessories.get(position);
	}

	@Override
	public long getItemId(int position) {
		return mAccessories.get(position).getId();
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		
		ViewHolder holder = null;
		if (convertView == null) {
			holder = new ViewHolder();

			convertView = mInflater.inflate(R.layout.product_card_tab_products_grid_item, null);

			holder.buttonBuy = (Button) convertView.findViewById(R.id.product_card_tab_products_button_buy);
			holder.image = (ImageView) convertView.findViewById(R.id.product_card_tab_products_image);
			holder.image_tag = (ImageView) convertView.findViewById(R.id.product_card_tab_products_image_tag);
			holder.rating = (RatingBar) convertView.findViewById(R.id.product_card_tab_products_rating);
			
			holder.category = (TextView) convertView.findViewById(R.id.product_card_tab_products_category);
			holder.price = (TextView) convertView.findViewById(R.id.product_card_tab_products_price); //Bold
			holder.price_p = (TextView) convertView.findViewById(R.id.product_card_tab_products_price_P);
			holder.name = (TextView) convertView.findViewById(R.id.product_card_tab_products_name);
		
			convertView.setOnClickListener(mListener);
			convertView.setTag(R.string.grid_key_holder, holder);
		} else {
			holder = (ViewHolder) convertView.getTag(R.string.grid_key_holder);
		}
		
		ProductBean bean = mAccessories.get(position);
		
		holder.image_tag.setImageDrawable(null);
		
		if (mIsLoad) {
			String imgUrl = Formatters.createFotoString(bean.getFoto(), 120);
			if(!imgUrl.equals(holder.image_url)){
				mImageLoader.load(holder.image, imgUrl);
				holder.image_url = imgUrl;
			}

			if ( ! Utils.isEmptyList(bean.getLabel())) {
				mImageLoaderTag.load(holder.image_tag, bean.getLabel().get(0).getFotoWithSize("66x23"));
			}
		}
		else{
			holder.image_url = "";
			holder.image.setImageResource(R.drawable.tmp_1);
		}
		
		holder.category.setText(bean.getPrefix());
		holder.name.setText(bean.getShortname());
		holder.rating.setRating(bean.getRating());
		holder.price.setText(String.valueOf((int)bean.getPrice()));
		
		holder.price_p.setText(TypefaceUtils.makeRouble(TypefaceUtils.NORMAL));
		
		holder.buttonBuy.setEnabled(bean.getBuyable() == 1);
		holder.buttonBuy.setTag(R.string.grid_key_item_pos, position);
		holder.buttonBuy.setOnClickListener(mListener);
		
		convertView.setTag(R.string.grid_key_id, bean.getId());
		
		return convertView;
	}
	
	private static class ViewHolder {
		private Button buttonBuy;
		private ImageView image;
		private ImageView image_tag;
		private TextView name;
		private TextView category;
		private TextView price;
		private TextView price_p;
		private RatingBar rating;
		private String image_url;
	}
	
}
