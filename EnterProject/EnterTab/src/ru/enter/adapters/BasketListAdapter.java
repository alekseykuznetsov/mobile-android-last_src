package ru.enter.adapters;

import java.util.List;

import ru.enter.ApplicationTablet;
import ru.enter.BasketActivity;
import ru.enter.R;
import ru.enter.DataManagement.BasketManager;
import ru.enter.beans.ProductBean;
import ru.enter.beans.ServiceBean;
import ru.enter.interfaces.IBasketElement;
import ru.enter.utils.Formatters;
import ru.enter.utils.TypefaceUtils;
import ru.enter.widgets.NumberPicker;
import ru.enter.widgets.NumberPicker.OnNumberChangedListener;
import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.webimageloader.ImageLoader;
import com.webimageloader.ext.ImageHelper;

public class BasketListAdapter extends AbstractListAdapter<IBasketElement>{
	

	private ImageHelper mImageLoader;
	private OnClickListener mClick;

	public BasketListAdapter(Context context) {
		super(context);
		
		ImageLoader loader = ApplicationTablet.getLoader(context);
		mImageLoader = new ImageHelper(context, loader)
        .setFadeIn(true)
        .setLoadingResource(R.drawable.tmp_1)
        .setErrorResource(R.drawable.tmp_1);
	}
	
	public void setOnClickListener (OnClickListener onClick) {
		mClick = onClick;
	}
	
	@Override
	public long getItemId(int position) {
		return getItem(position).getId();
	}

	@Override
	public View getView(int position, View convertView, final IBasketElement bean) {
		ViewHolder holder = null;
		if (convertView == null) {
			holder = new ViewHolder();

			convertView = getInflater().inflate(R.layout.basket_list_item, null);
			
			
			holder.image = (ImageView) convertView.findViewById(R.id.basket_list_item_image);
			holder.name = (TextView) convertView.findViewById(R.id.basket_list_item_text_name);
			holder.description = (TextView) convertView.findViewById(R.id.basket_list_item_text_description);
			holder.price = (TextView) convertView.findViewById(R.id.basket_list_item_text_price);
			holder.ruble = (TextView) convertView.findViewById(R.id.basket_list_item_text_ruble);
			holder.delete = (ImageButton) convertView.findViewById(R.id.basket_list_item_imagebutton_delete);
			holder.numberPicker = (NumberPicker) convertView.findViewById(R.id.basket_list_item_number_picker);
			holder.relatedServicesLinear = (LinearLayout) convertView.findViewById(R.id.basket_list_item_linear_additional);
			
			holder.numberPicker.setRange(1, 20);
			convertView.setTag(R.id.basket_holder,holder);
			
			
		} else {
			holder = (ViewHolder) convertView.getTag(R.id.basket_holder);
		}

		String fotoUrl = Formatters.createFotoString(bean.getFoto(), 163);
		mImageLoader.load(holder.image, fotoUrl);
		
		holder.name.setText(bean.isProduct() ? "Товар" : "Услуга");
		
		holder.description.setText(bean.getName());
		holder.price.setText(String.valueOf((int) (bean.getPrice())));
		holder.ruble.setText(TypefaceUtils.makeRouble(TypefaceUtils.NORMAL));
		
		holder.numberPicker.setCurrent(bean.getCount());
		holder.numberPicker.setOnNumberChangedListener(new OnNumberChangedListener() {
			
			@Override
			public void onChanged(int newNumber) {
				bean.setCount(newNumber);	
				BasketManager.notifyListeners();
			}
		});
		
		holder.delete.setTag(bean);
		holder.delete.setOnClickListener(mClick);
						
		holder.relatedServicesLinear.removeAllViews();
		if (bean.isProduct()){
			convertView.setTag(R.id.basket_bean,bean);			
			convertView.setOnClickListener(mClick);
			List<ServiceBean> relatedServices = BasketManager.getRelatedServices((ProductBean)bean);
			createRelatedServicesLinear(holder.relatedServicesLinear, relatedServices);	
		}

		return convertView;
	}
	
	public void createRelatedServicesLinear(LinearLayout parent, List<ServiceBean> relatedServices) {
	
		for (int i = 0; i < relatedServices.size(); i++) {
			final ServiceBean service = relatedServices.get(i);
			View view = getInflater().inflate(R.layout.basket_list_item_additional, null);

			TextView name = (TextView) view.findViewById(R.id.basket_list_item_additional_text_name);
			TextView price = (TextView) view.findViewById(R.id.basket_list_item_additional_text_price);
			TextView ruble = (TextView) view.findViewById(R.id.basket_list_item_additional_text_ruble);
			NumberPicker numberPicker = (NumberPicker) view.findViewById(R.id.basket_list_item_additional_number_picker);
			
			name.setText(service.getName());
			ruble.setText(TypefaceUtils.makeRouble(TypefaceUtils.NORMAL));
			
			price.setText(String.valueOf((int)service.getPrice()));
			numberPicker.setRange(1, 20);
			numberPicker.setCurrent(service.getCount());
			
			numberPicker.setOnNumberChangedListener(new OnNumberChangedListener() {
				@Override
				public void onChanged(int newNumber) {
					service.setCount(newNumber);
					BasketManager.notifyListeners();
				}
			});
			
			parent.addView(view);
		}
	}
	
	private static class ViewHolder {
		private ImageView image;
		private TextView name;
		private TextView description;
		private TextView price;
		private TextView ruble;
		private ImageButton delete;
		private NumberPicker numberPicker;
		private LinearLayout relatedServicesLinear;
	}
	
}
