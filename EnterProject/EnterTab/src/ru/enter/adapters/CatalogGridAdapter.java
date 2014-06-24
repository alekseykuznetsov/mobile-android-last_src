package ru.enter.adapters;

import ru.enter.ApplicationTablet;
import ru.enter.R;
import ru.enter.beans.CatalogListBean;
import ru.enter.data.CatalogNode;
import android.content.Context;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.webimageloader.ImageLoader;
import com.webimageloader.ext.ImageHelper;

public class CatalogGridAdapter extends AbstractListAdapter<CatalogNode>{

	private ImageHelper mImageLoader;

	public CatalogGridAdapter(Context context) {
		super(context);
		
		ImageLoader loader = ApplicationTablet.getLoader(context);
		mImageLoader = new ImageHelper(context, loader)
        .setFadeIn(true)
        .setLoadingResource(R.drawable.tmp_1)
        .setErrorResource(R.drawable.tmp_1);//TODO
	}

	@Override
	public long getItemId(int position) {
		return getItem(position).getNode().getId();
	}

	@Override
	public View getView(int position, View convertView, CatalogNode node) {
		ViewHolder holder = null;
		
		if(convertView == null){
			holder = new ViewHolder();
			convertView = getInflater().inflate(R.layout.catalog_grid_row, null);
		
			holder.image = (ImageView) convertView.findViewById(R.id.catalog_image);
			holder.grid_row_image = (ImageView) convertView.findViewById(R.id.catalog_grid_row_image);
			holder.name = (TextView) convertView.findViewById(R.id.catalog_name);
			holder.num = (TextView) convertView.findViewById(R.id.catalog_num);
			
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		
		CatalogListBean bean = node.getNode();
		holder.name.setText(bean.getName());
		holder.num.setText(String.valueOf(bean.getCount()) + " товара");

		holder.grid_row_image.setBackgroundResource(bean.isIs_category_list() ? R.drawable.bg_item_catalog : R.drawable.bg_item_pic);
		mImageLoader.load(holder.image, bean.getFoto());
	
		return convertView;
	}

	private static class ViewHolder {

		public TextView num;
		public TextView name;
		public ImageView image;
		public ImageView grid_row_image;
		
	}

}
