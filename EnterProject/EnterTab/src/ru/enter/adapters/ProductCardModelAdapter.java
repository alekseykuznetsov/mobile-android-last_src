package ru.enter.adapters;

import java.util.ArrayList;

import com.webimageloader.ImageLoader;
import com.webimageloader.ext.ImageHelper;


import ru.enter.ApplicationTablet;
import ru.enter.R;
import ru.enter.beans.ModelProductBean;
import ru.enter.beans.ProductModelBean;
import ru.enter.utils.Formatters;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class ProductCardModelAdapter extends BaseAdapter{
	
	private LayoutInflater mInflater;
	private ModelProductBean mModelBean;
	private ArrayList<ProductModelBean> mModels;
	private ImageHelper mImageLoader;
	
	public ProductCardModelAdapter(Context context, ModelProductBean modelBean) {
		mModelBean = modelBean;
		mModels = mModelBean.getProducts();
        mInflater = LayoutInflater.from(context);
        ImageLoader loader = ApplicationTablet.getLoader(context);
		
		mImageLoader = new ImageHelper(context, loader)
        .setFadeIn(false)
        .setLoadingResource(R.drawable.tmp_1)
        .setErrorResource(R.drawable.tmp_1);
    }

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return mModels.size();
	}

	@Override
	public ProductModelBean getItem(int position) {
		// TODO Auto-generated method stub
		return mModels.get(position);
	}
	

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return mModels.get(position).getProductBean().getId();
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		ViewHolder holder = null;
		if (convertView == null) {
			holder = new ViewHolder();

			convertView = mInflater.inflate(R.layout.product_card_model_item, null);

			holder.image = (ImageView) convertView.findViewById(R.id.product_card_model_item_image);
			holder.name = (TextView) convertView.findViewById(R.id.product_card_model_item_text_description);
		
			//convertView.setOnClickListener(mListener);
			convertView.setTag(R.string.list_key_product_model_holder, holder);
		} else {
			holder = (ViewHolder) convertView.getTag(R.string.list_key_product_model_holder);
		}
		
		ProductModelBean bean = getItem(position);
				
		
		String imgUrl = Formatters.createFotoString(bean.getProductBean().getFoto(), 120);
		if (!imgUrl.equals(holder.image_url)) {
			mImageLoader.load(holder.image, imgUrl);
			holder.image_url = imgUrl;
		}

		holder.name.setText(bean.getValue()+" "+mModelBean.getUnit());
		
		convertView.setTag(R.string.list_key_product_model_id, bean.getProductBean().getId());
		
		return convertView;
	}
	
	private static class ViewHolder {
		private ImageView image;
		private TextView name;
		private String image_url;
	}

}
