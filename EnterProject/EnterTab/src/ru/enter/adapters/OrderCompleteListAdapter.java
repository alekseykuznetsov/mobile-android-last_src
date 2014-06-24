package ru.enter.adapters;

import java.util.Collections;
import java.util.List;

import ru.enter.ApplicationTablet;
import ru.enter.R;
import ru.enter.DataManagement.BasketManager;
import ru.enter.beans.ProductBean;
import ru.enter.beans.ServiceBean;
import ru.enter.interfaces.IBasketElement;
import ru.enter.utils.Formatters;
import ru.enter.utils.TypefaceUtils;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;

import com.webimageloader.ImageLoader;
import com.webimageloader.ext.ImageHelper;

public class OrderCompleteListAdapter extends BaseAdapter {

	private LayoutInflater mInflater;
	private List<IBasketElement> mObjects;
	private ImageHelper mImageLoader;

	public OrderCompleteListAdapter(Context context) {

		mInflater = LayoutInflater.from(context);
		ImageLoader loader = ApplicationTablet.getLoader(context);
		mImageLoader = new ImageHelper(context, loader).setFadeIn(true)
				.setLoadingResource(R.drawable.tmp_1)
				.setErrorResource(R.drawable.tmp_1);// TODO
		mObjects = Collections.emptyList();
	}

	public void setObjects(List<IBasketElement> objects) {
		if (objects == null)
			return;
		mObjects = objects;
		notifyDataSetChanged();
	}

	@Override
	public int getCount() {
		return mObjects.size();
	}

	@Override
	public boolean isEnabled(int position) {
		return false;
	}
	
	@Override
	public IBasketElement getItem(int position) {
		return mObjects.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
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
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {

		ViewHolder holder = null;
		if (convertView == null) {
			holder = new ViewHolder();

			convertView = mInflater.inflate(R.layout.order_complete_list_item,
					null);

			holder.image = (ImageView) convertView.findViewById(R.id.order_complete_list_item_image);
			holder.name = (TextView) convertView.findViewById(R.id.order_complete_list_item_text_name);
			holder.description = (TextView) convertView.findViewById(R.id.order_complete_list_item_text_description);
			holder.price = (TextView) convertView.findViewById(R.id.order_complete_list_item_text_price);
			holder.ruble = (TextView) convertView.findViewById(R.id.order_complete_list_item_text_ruble);
			holder.rating = (RatingBar) convertView.findViewById(R.id.order_complete_list_item_rating_bar);
			holder.count = (TextView) convertView.findViewById(R.id.order_complete_list_item_text_count);
			holder.additional_layout = (LinearLayout) convertView.findViewById(R.id.order_complete_list_item_linear_additional);

			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		final IBasketElement object = getItem(position);
		String fotoUrl = Formatters.createFotoString(object.getFoto(), 163);
		mImageLoader.load(holder.image, fotoUrl);
		holder.description.setText(object.getName());
		holder.ruble.setText(TypefaceUtils.makeRouble(TypefaceUtils.NORMAL));
		
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
		holder.additional_layout .removeAllViews();
		
		if (object.isProduct()) {
			List<ServiceBean> relatedServices = BasketManager.getRelatedServices((ProductBean) object);
			fullAdditionalLinearLayout(holder.additional_layout,relatedServices);
		}

		return convertView;
	}

	public Void fullAdditionalLinearLayout(LinearLayout additional_layout,
			List<ServiceBean> relatedServices) {
		for (int i = 0; i < relatedServices.size(); i++) {
			final ServiceBean service = relatedServices.get(i);
			View view = mInflater.inflate(R.layout.order_complete_list_item_additional_layout,
					null);

			TextView name = (TextView) view.findViewById(R.id.order_complete_list_item_additional_text_name);
			TextView price = (TextView) view.findViewById(R.id.order_complete_list_item_additional_text_price);
			TextView ruble = (TextView) view.findViewById(R.id.order_complete_list_item_additional_text_ruble);
			TextView count = (TextView) view.findViewById(R.id.order_complete_list_item_additional_text_count);

			name.setText(service.getName());
			ruble.setText(TypefaceUtils.makeRouble(TypefaceUtils.NORMAL));
			price.setText(String.valueOf((int) service.getPrice()));
			count.setText(String.valueOf((int) (service.getCount())));

			additional_layout.addView(view);
		}
		return null;
	}
}