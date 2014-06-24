package ru.enter.adapters;

import ru.enter.R;
import ru.enter.DataManagement.BasketManager;
import ru.enter.DataManagement.BasketManager.CountPrice;
import ru.enter.interfaces.OnBasketChangeListener;
import ru.enter.utils.Formatters;
import ru.enter.utils.Utils;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class MainMenuGridAdapter extends BaseAdapter implements OnBasketChangeListener {
	private Context mContext;
	private String[] bean;
	private int[] img = { R.drawable.btn_home1, R.drawable.btn_home2, R.drawable.btn_home9, R.drawable.btn_home4, R.drawable.btn_home5,
			R.drawable.btn_home6, R.drawable.btn_home8, R.drawable.fb };//R.drawable.btn_home7,
	private TextView mBasketCountText;
	private TextView mBasketPriceText;

	public MainMenuGridAdapter(Context c, String[] titles) {
		mContext = c;
		bean = titles;
		BasketManager.setOnBasketChangeListener(this);
	}

	public int getCount () {
		return bean.length;
	}

	public String getItem (int position) {
		return bean[position];
	}

	public long getItemId (int position) {
		return 0;
	}

	public View getView (int position, View convertView, ViewGroup parent) {

		LayoutInflater viewInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View view = viewInflater.inflate(R.layout.catalog_grid_row, null);

		// Для корзины
		if (position == 3) {
			return createBasketView(view);
		}
		
		TextView text = (TextView) view.findViewById(R.id.catalog_grid_row_text);
		ImageView iw = (ImageView) view.findViewById(R.id.catalog_grid_row_img);

		String msg = getItem(position);
		iw.setImageResource(img[position]);
		text.setText(msg);
		text.setTypeface(Utils.getTypeFace(mContext));

		return view;
	}

	private View createBasketView (View source) {
		mBasketCountText = (TextView) source.findViewById(R.id.catalog_grid_row_text_count);
		
		ImageView iw = (ImageView) source.findViewById(R.id.catalog_grid_row_img);
		iw.setImageResource(R.drawable.btn_home4);
		
		mBasketPriceText = (TextView) source.findViewById(R.id.catalog_grid_row_text);
		mBasketPriceText.setTypeface(Utils.getTypeFace(mContext));
		
		refreshBasketView();
		
		return source;
	}
	
	private void refreshBasketView () {
		CountPrice object = BasketManager.getCountPriceObject();
		
		if (object.allCount > 0) {
			mBasketCountText.setVisibility(View.VISIBLE);
			mBasketCountText.setText(String.valueOf(object.allCount));
			mBasketCountText.setTypeface(Utils.getTypeFace(mContext));
			if (object.allCount < 10) {
				RelativeLayout.LayoutParams params = (android.widget.RelativeLayout.LayoutParams) mBasketCountText.getLayoutParams();
				params.setMargins(10, 0, 0, 0);
			}
		} else {
			mBasketCountText.setVisibility(View.GONE);
		}
		
		if (object.allPrice > 0) {
			mBasketPriceText.setText(makePrice(object.allPrice));//TODO
		} else {
			mBasketPriceText.setText("Моя корзина");
		}
	}

	@Override
	public void onBasketChange () {
		refreshBasketView();
	}
	
	private String makePrice (int price) {
		return Formatters.createPriceStringWithRouble(price).concat(".");
	}
}
