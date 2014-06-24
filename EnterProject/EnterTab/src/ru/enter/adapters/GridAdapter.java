package ru.enter.adapters;

import ru.enter.ApplicationTablet;
import ru.enter.ProductCardActivity;
import ru.enter.R;
import ru.enter.beans.ProductBean;
import ru.enter.utils.Discount;
import ru.enter.utils.PreferencesManager;
import ru.enter.utils.TypefaceUtils;
import ru.enter.utils.Utils;
import ru.enter.utils.ViewMode;
import ru.ideast.enter.R.color;
import android.content.Context;
import android.graphics.Paint;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.webimageloader.ImageLoader;
import com.webimageloader.ext.ImageHelper;

public class GridAdapter extends AbstractListAdapter<ProductBean> {
	
	private OnClickListener mListener;
	
	private ViewMode mMode;
	private ImageLoader mLoader;
	private ImageHelper mLoaderHelper;
	private Context mContext;

	private boolean mIsLoad = true;
	
	public void setOnClickListener (OnClickListener listener) {
		mListener = listener;
	}

	public GridAdapter(Context context) {
		super(context);
		mContext = context;
		mLoader = ApplicationTablet.getLoader(context);
		mLoaderHelper = new ImageHelper(context, mLoader).setErrorResource(R.drawable.tmp_1);
		mMode = ViewMode.SMALL;
	}

	public void setViewMode (ViewMode mode) {
		mMode = mode;
	}
	

	public void setLoad(boolean isLoad) {
		mIsLoad = isLoad;
	}

	@Override
	public int getViewTypeCount() {
		return 2;
	}
	
	@Override
	public int getItemViewType(int position) {
		return mMode.isHorizontal() ? 0 : 1;
	}
	
	@Override
	public long getItemId(int position) {
		return getItem(position).getId();
	}
	
	@Override
	public View getView(int position, View convertView, ProductBean bean) {
		ViewHolder holder = null;
		
		if (convertView == null) {
			holder = new ViewHolder();
			
			if (mMode.isHorizontal()){
				convertView = getInflater().inflate(R.layout.product_info_horizontal_grid_row, null);
			} else {
				convertView = getInflater().inflate(R.layout.product_info_vertical_grid_row, null);
			}
			
			holder.image = (ImageView) convertView.findViewById(R.id.product_grid_row_image);
			holder.image_tag = (ImageView) convertView.findViewById(R.id.product_grid_row_image_tag);
			holder.rating = (RatingBar) convertView.findViewById(R.id.product_grid_row_rating);
			holder.category = (TextView) convertView.findViewById(R.id.product_grid_row_category);
			holder.name = (TextView) convertView.findViewById(R.id.product_grid_row_name);
			holder.price = (TextView) convertView.findViewById(R.id.product_grid_row_price);
			holder.old_price = (TextView) convertView.findViewById(R.id.product_grid_row_old_price);
			holder.old_price_rouble = (TextView) convertView.findViewById(R.id.product_grid_row_old_rouble);
			holder.discount = (TextView) convertView.findViewById(R.id.product_grid_row_discount);
			holder.buttonBuy = (Button) convertView.findViewById(R.id.product_grid_row_button);
			holder.rouble = (TextView) convertView.findViewById(R.id.product_grid_row_rouble);
			holder.exist = (TextView) convertView.findViewById(R.id.product_grid_row_exist);
			
			holder.rouble.setText(TypefaceUtils.makeRouble(TypefaceUtils.NORMAL));
			holder.old_price_rouble.setText(TypefaceUtils.makeRouble(TypefaceUtils.NORMAL));
			
			holder.price.setTypeface(Utils.getTahomaTypeFace(mContext));
			holder.old_price.setTypeface(Utils.getTahomaTypeFace(mContext));
			holder.discount.setTypeface(Utils.getTahomaTypeFace(mContext));
			
			convertView.setOnClickListener(mListener);
			convertView.setTag(holder);
			
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		
		
		holder.image_tag.setImageDrawable(null);
		
		if (mIsLoad) {
			String imgUrl = bean.getFotoWithDifferentSize("163", String.valueOf(mMode.getImageSize()));
			if(!imgUrl.equals(holder.image_url)){
				mLoaderHelper.load(holder.image, imgUrl);
				holder.image_url = imgUrl;
			}

			if ( ! Utils.isEmptyList(bean.getLabel())) {
				mLoaderHelper.load(holder.image_tag, bean.getLabel().get(0).getFotoWithSize("66x23"));
			}
		}
		else{
			holder.image_url = "";
			holder.image.setImageResource(R.drawable.tmp_1);
		}
		
		holder.rating.setRating(bean.getRating());
		holder.category.setText(bean.getPrefix());
		holder.name.setText(bean.getShortname());
		holder.price.setText(bean.getPriceFormattedString());
		
		if(bean.getPrice_old() > 0){
			holder.old_price.setText(bean.getOldPriceFormattedString());
			holder.discount.setText(Discount.getDiscount(bean.getPrice(), bean.getPrice_old()));
			
			holder.old_price.setPaintFlags(holder.price.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
			holder.old_price_rouble.setPaintFlags(holder.price.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
			
			holder.old_price.setVisibility(View.VISIBLE);
			holder.old_price_rouble.setVisibility(View.VISIBLE);
			holder.discount.setVisibility(View.VISIBLE);
			
			holder.price.setTextColor(mContext.getResources().getColor(R.color.red));
			holder.rouble.setTextColor(mContext.getResources().getColor(R.color.red));
//			holder.price.setTextSize(24);
//			holder.rouble.setTextSize(24);
		} else {
			holder.old_price.setText("");
			holder.discount.setText("");
			
			holder.old_price.setPaintFlags(holder.price.getPaintFlags());
			holder.old_price_rouble.setPaintFlags(holder.price.getPaintFlags());
			
			holder.old_price.setVisibility(View.VISIBLE);
			holder.old_price_rouble.setVisibility(View.GONE);
			holder.discount.setVisibility(View.GONE);
			
			holder.price.setTextColor(mContext.getResources().getColor(R.color.black));
			holder.rouble.setTextColor(mContext.getResources().getColor(R.color.black));
		}
		
		
		String exist = "нет в наличиии";
		int is_shop = bean.getShop();
		int scope_store_qty = bean.getScopeStoreQty();
		int scope_shops_qty = bean.getScopeShopsQty();
		int scope_shops_qty_showroom = bean.getScopeShopsQtyShowroom();
		int buyable = bean.getBuyable(); 
		boolean in_shop = (PreferencesManager.getUserCurrentShopId()!=0?true:false);
		if (in_shop && buyable == 1 && scope_shops_qty_showroom > 0) {
			exist = "есть на витрине";
			holder.exist.setTextColor(android.graphics.Color.rgb(122,111, 149));
		} else {
			if (buyable == 1) {
				exist = "";
				holder.exist.setTextColor(android.graphics.Color.BLACK);
			} else {
				if (is_shop == 1 && scope_store_qty == 0 && scope_shops_qty > 0) {
					exist = "только в магазинах";
					holder.exist.setTextColor(android.graphics.Color.rgb(122,111, 149));
				} else {
					if (scope_shops_qty == 0 && scope_shops_qty_showroom > 0) {
						exist = "только на витрине";
						holder.exist.setTextColor(android.graphics.Color.rgb(122, 111, 149));
					} else {
						if (scope_shops_qty == 0) {
							exist = "нет в наличии";
							holder.exist.setTextColor(android.graphics.Color.rgb(165, 74, 73));
						}
					}
				}
			}
		}
		holder.exist.setText(exist);
		
		holder.buttonBuy.setTag(bean);
		holder.buttonBuy.setEnabled(bean.getBuyable() == 1);
		holder.buttonBuy.setOnClickListener(mListener);
		holder.bean = bean;

		return convertView;
	}
	
	public static class ViewHolder {
		public ProductBean bean;
		public TextView rouble;
		public ImageView image;
		public ImageView image_tag;
		public RatingBar rating;
		public TextView category;
		public TextView name;
		public TextView price;
		public TextView old_price;
		public TextView old_price_rouble;
		public TextView discount;
		public TextView exist;
		public Button buttonBuy;
		public String image_url;
	}
	
}
