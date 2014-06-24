package ru.enter.adapters;

import java.util.ArrayList;

import ru.enter.R;
import ru.enter.ImageManager.ImageDownloader;
import ru.enter.beans.ProductModelBean;
import ru.enter.utils.Utils;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class ModelItemsGridAdapter extends ArrayAdapter<ProductModelBean> {
    
	private Context mContext;
    private ImageDownloader downloader;
    private LayoutInflater viewInflater;
	public ModelItemsGridAdapter(Context context, int textViewResourceId) {
		super(context, textViewResourceId);
		mContext = context;
		downloader = new ImageDownloader(context);
		downloader.setMaxThreads(6);
		viewInflater = (LayoutInflater)this.mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}
	public void addNew(ArrayList<ProductModelBean> bean)
	{
		for(ProductModelBean b : bean)
			add(b);
	}

	@Override
	public int getCount() {
		return super.getCount();
	}
	@Override
	public ProductModelBean getItem(int position) {
		return super.getItem(position);
	}
    
    public View getView(int position, View convertView, ViewGroup parent) {
		View v = viewInflater.inflate(R.layout.models_items_lst_element_grid, null);  
		ProductModelBean bean = getItem(position);
		TextView value = (TextView)v.findViewById(R.id.models_items_list_el_grid_value);		
		ImageView iw = (ImageView)v.findViewById(R.id.models_items_list_el_grid_img);
		ImageView tag = (ImageView)v.findViewById(R.id.models_items_list_el_grid_option);
		
		value.setText(bean.getValue());		
		iw.setImageResource(R.drawable.cap);
		try{
			int substitution = Utils.getDpiForItemList(mContext);
			String foto = bean.getProductBean().getFoto().replaceAll("__media_size__", String.valueOf(substitution));			
			downloader.download(foto, iw);
		}catch(Exception e){}
		try {
			downloader.download(bean.getProductBean().getLabel().get(0).getFoto(mContext), tag);
		} catch (Exception e) {}
		v.setTag(bean);
		
        return v;
    }   
}
