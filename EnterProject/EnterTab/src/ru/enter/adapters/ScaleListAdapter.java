package ru.enter.adapters;

import ru.enter.beans.ProductBean;
import android.content.Context;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

public class ScaleListAdapter extends AbstractListAdapter<ProductBean>{

	public ScaleListAdapter(Context context) {
		super(context);
	}

	@Override
	public long getItemId(int position) {
		return 0; //не нужен
	}

	@Override
	public View getView(int position, View convertView, ProductBean bean) {
		
		// TODO Auto-generated method stub
		return null;
	}
	
	private static class ViewHolder {
		public ProductBean bean;
		public TextView rouble;
		public ImageView image;
		public ImageView image_tag;
		public RatingBar rating;
		public TextView category;
		public TextView name;
		public TextView price;
		public Button buttonBuy;
		public String image_url;
	}

}
