package ru.enter.adapters;

import java.util.ArrayList;

import ru.enter.ApplicationTablet;
import ru.enter.R;
import ru.enter.beans.ServiceBean;
import ru.enter.utils.Formatters;
import ru.enter.utils.TypefaceUtils;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.TextView;

import com.webimageloader.ImageLoader;
import com.webimageloader.ext.ImageHelper;

public class ProductCardServicesListAdapter extends BaseAdapter {
	
	private static final int IMG_SIZE = 200;
	
	private LayoutInflater mInflater;
	private ImageHelper mImageLoader;
	private ArrayList<ServiceBean> mServices;
	private OnClickListener mClick;
	private boolean mEnableBuy;

	public ProductCardServicesListAdapter(Context context, ArrayList<ServiceBean> services, int buyable) {
		mInflater = LayoutInflater.from(context);
		ImageLoader loader = ApplicationTablet.getLoader(context);
		mImageLoader = new ImageHelper(context, loader)
        .setFadeIn(true)
        .setLoadingResource(R.drawable.tmp_1)
        .setErrorResource(R.drawable.tmp_1);//TODO
		
		mServices = services;
		mEnableBuy = (buyable == 1);
	}

	
	
	
	@Override
	public int getCount() {		
		return mServices.size();
	}

	@Override
	public ServiceBean getItem(int position) {
		return mServices.get(position);
	}

	@Override
	public long getItemId(int position) {
		return getItem(position).getId();
	}
	
	 public boolean isEnabled(int position) { 
             return false; 
     } 

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder = null;
		if (convertView == null) {
			holder = new ViewHolder();

			convertView = mInflater.inflate(R.layout.product_card_tab_services_list_item, null);

			holder.buttonBuy = (Button) convertView.findViewById(R.id.product_card_tab_service_list_item_button_buy);
			holder.image = (ImageView) convertView.findViewById(R.id.product_card_tab_service_list_item_image);
			holder.description = (TextView) convertView.findViewById(R.id.product_card_tab_service_list_item_text_description);
			holder.price = (TextView) convertView.findViewById(R.id.product_card_tab_service_list_item_text_price);
			holder.ruble = (TextView) convertView.findViewById(R.id.product_card_tab_service_list_item_text_ruble);
			holder.title = (TextView) convertView.findViewById(R.id.product_card_tab_service_list_item_text_title);
			
			LayoutParams param = new LayoutParams(IMG_SIZE, IMG_SIZE);
			holder.image.setLayoutParams(param);
			convertView.setTag(holder);
			
		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		ServiceBean bean = getItem(position);
		
		holder.ruble.setText(TypefaceUtils.makeRouble(TypefaceUtils.NORMAL));
		
		String foto = Formatters.createFotoString(bean.getFoto(), IMG_SIZE);
		
		holder.image.setImageResource(R.drawable.tmp_1);
		mImageLoader.load(holder.image, foto);
		
		holder.price.setText(String.valueOf((int)bean.getPrice()));
		holder.description.setText(bean.getWork());
		holder.title.setText(bean.getName());
		
		holder.buttonBuy.setEnabled(mEnableBuy);
		if (mEnableBuy){
			holder.buttonBuy.setTag(bean);
			holder.buttonBuy.setOnClickListener(mClick);
		}
		return convertView;
	}
	
	private static class ViewHolder {
		private Button buttonBuy;
		private ImageView image;
		private TextView description;
		private TextView price;
		private TextView title;
		private TextView ruble;

	}

	public void setOnClickListener(OnClickListener onClick) {
		mClick = onClick;
		
	}
}
