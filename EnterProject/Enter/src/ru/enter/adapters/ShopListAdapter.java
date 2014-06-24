package ru.enter.adapters;

import java.util.ArrayList;

import ru.enter.R;
import ru.enter.beans.ShopBean;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

public class ShopListAdapter extends BaseAdapter implements OnClickListener{
	
	private Context context;
	private ArrayList<ShopBean> objects;
	private OnClickListener mOnClickListener;
	private boolean mCanBuy;
	private boolean mIsShowWindow;
	
	public ShopListAdapter(Context context,OnClickListener onClickListener, boolean isShowWindow) {
		this.context = context;
		objects = new ArrayList<ShopBean>(0);
		mOnClickListener = onClickListener;
		mIsShowWindow = isShowWindow;
	}
	
	public void setObjects(ArrayList<ShopBean> objects){
		if(objects!=null)
			this.objects = objects;
		notifyDataSetChanged();
	}
	
	@Override
	public int getCount() {		
		return objects.size();
	}

	@Override
	public ShopBean getItem(int position) {
		return objects.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		LayoutInflater viewInflater = (LayoutInflater)this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View view = viewInflater.inflate(R.layout.shop_list_row, null);
		
		ShopBean bean = getItem(position);
		
		TextView address = (TextView)view.findViewById(R.id.shop_list_row_address);
		TextView countText = (TextView)view.findViewById(R.id.shop_list_row_text_count);
		ImageView image = (ImageView)view.findViewById(R.id.shop_list_row_image);
		Button buy = (Button) view.findViewById(R.id.shop_list_row_button_buy);
		
		buy.setTag(bean);
		buy.setOnClickListener(mOnClickListener);
		
		if (mIsShowWindow && bean.getQuantityShowroom() > 0){
			image.setVisibility(View.GONE);
			buy.setVisibility(View.GONE);
		}
		else{
			countText.setVisibility(View.GONE);
			switch (bean.getStick()) {
			case 0:
				image.setBackgroundResource(R.drawable.pic_amount0);
				break;
			case 1:
				image.setBackgroundResource(R.drawable.pic_amount1);
				break;
			case 2:
				image.setBackgroundResource(R.drawable.pic_amount2);	
				break;
			default:
				image.setBackgroundResource(R.drawable.pic_amount3);
				break;
			}
		}
//		Button path = (Button) view.findViewById(R.id.shop_list_row_button_path);
//		path.setOnClickListener(this);
//		path.setTag(bean);
		
		address.setText(bean.getName());
		buy.setEnabled(mCanBuy);
		
		return view;
	}

	@Override
	public void onClick(View v) {
		
	}

	public void setBuyable(boolean buyable) {
		mCanBuy = buyable;
	}

}
