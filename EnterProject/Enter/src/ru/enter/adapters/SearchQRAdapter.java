package ru.enter.adapters;

import java.util.ArrayList;

import com.google.analytics.tracking.android.EasyTracker;

import ru.enter.BasketActivity;
import ru.enter.R;
import ru.enter.DataManagement.BasketManager;
import ru.enter.ImageManager.ImageDownloader;
import ru.enter.beans.ProductBean;
import ru.enter.utils.Utils;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

public class SearchQRAdapter extends BaseAdapter implements OnClickListener{
	private ArrayList<ProductBean> mObjects;
	private ImageDownloader mImageDownloader;
	private LayoutInflater mInflater;
	private Context mContext;
	private ProductBean mBean;
	
	public SearchQRAdapter(Context context){
		mContext = context;
		mObjects = new ArrayList<ProductBean>();
		mImageDownloader = new ImageDownloader(context);
		mInflater = LayoutInflater.from(context);
	}
	
	public void setObjects(ArrayList<ProductBean> objects){
		if(!Utils.isEmptyList(objects)){
			mObjects = objects;
			notifyDataSetChanged();
		}
	}

	@Override
	public int getCount() {
		return mObjects.size();
	}

	@Override
	public ProductBean getItem(int position) {
		return mObjects.get(position);
	}

	@Override
	public long getItemId(int position) {
		return getItem(position).getId();
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View root = mInflater.inflate(R.layout.qr_search_list_row, null);
		ImageView image = (ImageView) root.findViewById(R.id.qr_search_list_row_image);
		TextView mPrice = (TextView) root.findViewById(R.id.qr_search_row_price);
		TextView mPriceSign = (TextView) root.findViewById(R.id.qr_search_row_price_sum);
		TextView mTitle = (TextView)root.findViewById(R.id.qr_search_row_title);
		Button buttonBuy = (Button)root.findViewById(R.id.qr_search_list_row_button_buy);
		
		mBean = getItem(position);
		
		mImageDownloader.download(mBean.getMain_fotos().get(Utils.getPhoto(mBean.getMain_fotos(), 200)).getImages().get(0), image);
		mTitle.setText(mBean.getName());
		mPrice.setText(String.valueOf(mBean.getPrice()));
		mPriceSign.setTypeface(Utils.getRoubleTypeFace(mContext));
		buttonBuy.setFocusable(false);
		buttonBuy.setOnClickListener(this);
		return root;
	}

	@Override
	public void onClick(View v) {
		if(mBean!=null){
			BasketManager.addProduct(mBean);
			EasyTracker.getInstance().setContext(mContext);
			EasyTracker.getTracker().sendEvent("cart/add-product", "buttonPress", mBean.getName(), (long) mBean.getId());
			showDialog();
		}
	}
	
	private void showDialog(){
		AlertDialog.Builder dlg = new AlertDialog.Builder(mContext);				    			
        dlg
        .setMessage("Товар добавлен в корзину")
        .setPositiveButton("Перейти в корзину", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) { 
            	Intent intent = new Intent();
            	intent.setClass(mContext, BasketActivity.class);
				intent.putExtra(BasketActivity.SHOW_BUTTON, true);
				mContext.startActivity(intent);
        }})
        .setNegativeButton("Продолжить покупки", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {   
            	
            }
        })
        .create().show(); 
	}

}
