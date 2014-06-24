package ru.enter.adapters;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import ru.enter.ApplicationTablet;
import ru.enter.R;
import ru.enter.beans.ProductBean;
import ru.enter.beans.ServiceBean;
import ru.enter.interfaces.IBasketElement;
import ru.enter.utils.Formatters;
import ru.enter.utils.TypefaceUtils;
import android.content.Context;
import android.graphics.Typeface;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.webimageloader.ImageLoader;
import com.webimageloader.ext.ImageHelper;

public class PersonalOrderRightListAdapter extends BaseAdapter {

	private static final String LAND = "land";
	private static final String PORT = "port";
	
	private LayoutInflater mInflater;
	private List<IBasketElement> mObjects;
	private ImageHelper mImageLoader;
	private String mOrientation;
	private ArrayList<ServiceBean> mRelatedservices;
	

	public PersonalOrderRightListAdapter(Context context) {

		mInflater = LayoutInflater.from(context);
		ImageLoader loader = ApplicationTablet.getLoader(context);
		mImageLoader = new ImageHelper(context, loader).setFadeIn(true)
				.setLoadingResource(R.drawable.tmp_1)
				.setErrorResource(R.drawable.tmp_1);// TODO
		mObjects = Collections.emptyList();
		mRelatedservices = new ArrayList<ServiceBean>();
		mOrientation = LAND;
	}

	public void setObjects(List<IBasketElement> objects) {
		if (objects == null)
			return;
		mObjects = objects;
		notifyDataSetChanged();
	}

	public void setRelatedServices(ArrayList<ServiceBean> related_services) {
		if (related_services == null)
			return;
		mRelatedservices = related_services;
		notifyDataSetChanged();
	}
	
	public void setOrientation (String orientation){
		mOrientation = orientation;
		notifyDataSetChanged();
	}
	@Override
	public int getCount() {
		return mObjects.size();
	}

	@Override
	public IBasketElement getItem(int position) {
		return mObjects.get(position);
	}

	@Override
	public long getItemId(int position) {
		return getItem(position).getId();
	}

	private static class ViewHolder {
		private ImageView image;
		private TextView name;
		private TextView description;
		private TextView price;
		private TextView ruble;
		private TextView count;
		private RatingBar rating;
		private LinearLayout additional_layout;
		private LinearLayout main_layout;
		private LinearLayout middle_layout;
		private LinearLayout price_layout;
		
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {

		ViewHolder holder = null;
		if (convertView == null) {
			holder = new ViewHolder();

			convertView = mInflater.inflate(R.layout.personal_order_fr_right_list_item,
					null);

			holder.image = (ImageView) convertView.findViewById(R.id.personal_order_fr_right_list_item_image_main);
			holder.name = (TextView) convertView.findViewById(R.id.personal_order_fr_right_list_item_text_name);
			holder.description = (TextView) convertView.findViewById(R.id.personal_order_fr_right_list_item_text_description);
			holder.price = (TextView) convertView.findViewById(R.id.personal_order_fr_right_list_item_text_price);
			holder.ruble = (TextView) convertView.findViewById(R.id.personal_order_fr_right_list_item_text_ruble);
			holder.rating = (RatingBar) convertView.findViewById(R.id.personal_order_fr_right_list_item_rating_bar);
			holder.count = (TextView) convertView.findViewById(R.id.personal_order_fr_right_list_item_text_goods_count);
			holder.additional_layout = (LinearLayout) convertView.findViewById(R.id.personal_order_fr_right_list_item_additional_linear);
			holder.main_layout = (LinearLayout) convertView.findViewById(R.id.personal_order_fr_right_list_item_main_linear);
			holder.middle_layout = (LinearLayout) convertView.findViewById(R.id.personal_order_fr_right_list_item_middle_linear); 
			holder.price_layout = (LinearLayout) convertView.findViewById(R.id.personal_order_fr_right_list_item_price_linear);
			
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		final IBasketElement object = getItem(position);
		String fotoUrl = Formatters.createFotoString(object.getFoto(), 163);
		mImageLoader.load(holder.image, fotoUrl);
		holder.ruble.setText(TypefaceUtils.makeRouble(TypefaceUtils.NORMAL));
		
		holder.description.setText(object.getName());
		
		if (object.isProduct()){
			holder.name.setText("Товар");
			ProductBean product = (ProductBean) object;
			holder.rating.setRating(product.getRating());
		}
		else{
			holder.name.setText("Услуга");
			holder.rating.setVisibility(View.GONE);
		}
		
		holder.price.setText(String.valueOf((int) (object.getPrice())));
		holder.count.setText(String.valueOf((int) (object.getCount())));
		
		holder.additional_layout.removeAllViews();
		if (object.isProduct()) {
			List<ServiceBean> relatedServices = getRelatedServices((int) object.getId());
			fullAdditionalLinearLayout(holder.additional_layout,relatedServices);
		}
		
		if (mOrientation == LAND){
			
			holder.middle_layout.setOrientation(LinearLayout.HORIZONTAL);
			holder.price_layout.setGravity(Gravity.RIGHT);

			RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams)holder.additional_layout.getLayoutParams();
			params.addRule(RelativeLayout.BELOW, holder.main_layout.getId());
			params.addRule(RelativeLayout.ALIGN_LEFT, holder.main_layout.getId());
			holder.additional_layout.setLayoutParams(params); 
			
		} else if (mOrientation == PORT){
			holder.middle_layout.setOrientation(LinearLayout.VERTICAL);
			holder.price_layout.setGravity(Gravity.LEFT);
			
			RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams)holder.additional_layout.getLayoutParams();
			params.addRule(RelativeLayout.ALIGN_LEFT, holder.image.getId());
			
			if (holder.main_layout.getWidth() < holder.image.getWidth()){
				params.addRule(RelativeLayout.BELOW, holder.image.getId());
			}
			else
				params.addRule(RelativeLayout.BELOW, holder.main_layout.getId());
			
			holder.additional_layout.setLayoutParams(params);
		}

		return convertView;
	}

	private ArrayList<ServiceBean> getRelatedServices(int product_id){
		ArrayList<ServiceBean> relatedServices = new ArrayList<ServiceBean>();
		for(int i=0; i < mRelatedservices.size(); i++){
			ServiceBean currentService = mRelatedservices.get(i);
			if (currentService.getProductId() == product_id){
				relatedServices.add(currentService);
			}
		}
		return relatedServices;
	}
	
	public Void fullAdditionalLinearLayout(LinearLayout additional_layout, List<ServiceBean> relatedServices) {
		for (int i = 0; i < relatedServices.size(); i++) {
			final ServiceBean service = relatedServices.get(i);
			View view = mInflater.inflate(R.layout.personal_order_fr_right_list_item_additional,null);

			TextView name = (TextView) view.findViewById(R.id.personal_order_fr_right_list_item_additional_text_name);
			String text = String.format("%s - %s%s (%s шт.)", service.getName(), String.valueOf((int) service.getPrice()), String.valueOf(TypefaceUtils.ROUBLE), String.valueOf((int) (service.getCount())));
			CharSequence description = TypefaceUtils.formatRoubleString(text, Typeface.NORMAL, TypefaceUtils.NORMAL);
			name.setText(description);
//			
//			String text1 = String.format("%s - %s%s", service.getName(), String.valueOf((int) service.getPrice()), String.valueOf(TypefaceUtils.ROUBLE));
//			CharSequence description1 = TypefaceUtils.formatRoubleString(text, Typeface.NORMAL, TypefaceUtils.NORMAL);
//			String text2 = String.format("%s(%s шт.)", description1, String.valueOf((int) (service.getCount())));
//			name.setText(text2);
			
			
			additional_layout.addView(view);
		}
		return null;
	}

}
