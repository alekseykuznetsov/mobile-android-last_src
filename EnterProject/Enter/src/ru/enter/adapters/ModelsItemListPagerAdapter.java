package ru.enter.adapters;

import java.util.ArrayList;

import ru.enter.ProductCardActivity;
import ru.enter.R;
import ru.enter.beans.ProductModelBean;
import ru.enter.utils.Constants;
import android.content.Context;
import android.content.Intent;
import android.os.Parcelable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.AdapterView.OnItemClickListener;

public class ModelsItemListPagerAdapter extends PagerAdapter implements OnItemClickListener{
	public static final int NUM_ITEM_PER_PAGE = 3; 
	private Context mContext;
	private ArrayList<ProductModelBean> mObjects;
	
	public ModelsItemListPagerAdapter (Context context){
		mContext = context;
		mObjects = new ArrayList<ProductModelBean>();
	}
	
	public void setObjects (ArrayList<ProductModelBean> objectsArray){
	//	if (!Utils.isEmptyList(objectsArray)) {
			mObjects = objectsArray;
			notifyDataSetChanged();
	//	}
	}
	
	
	@Override
	public int getCount() {
		return mObjects.size() % NUM_ITEM_PER_PAGE > 0 ? 
				mObjects.size() / NUM_ITEM_PER_PAGE + 1 :
					mObjects.size()/NUM_ITEM_PER_PAGE;
	}
	
	//для notify()
	@Override
	public int getItemPosition(Object object) {
		return POSITION_NONE;
	}

	@Override
	public Object instantiateItem(View collection, int position) {
		
		LayoutInflater viewInflater = LayoutInflater.from(mContext);
		GridView grid = (GridView)viewInflater.inflate(R.layout.models_card_items_lst_grid, null);
		
		ModelItemsGridAdapter iga = new ModelItemsGridAdapter(mContext, 0);

			for(int j = position*NUM_ITEM_PER_PAGE; j<position * NUM_ITEM_PER_PAGE + NUM_ITEM_PER_PAGE; j++)
				if(j<mObjects.size())
					iga.add(mObjects.get(j));
		grid.setAdapter(iga);
		grid.setOnItemClickListener(this);
		
		((ViewPager) collection).addView(grid,0);
		
		return grid;
	}
	
	@Override
	public void destroyItem(View collection, int position, Object view) {
		((ViewPager) collection).removeView((GridView) view);
	}
	@Override
	public boolean isViewFromObject(View view, Object object) {
		return view==((GridView)object);
	}
	@Override
	public void finishUpdate(View arg0) {}

	@Override
	public void restoreState(Parcelable arg0, ClassLoader arg1) {}
	@Override
	public Parcelable saveState() {
		return null;
	}
	@Override
	public void startUpdate(View arg0) {}

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
		try {
			ProductModelBean b = (ProductModelBean) arg1.getTag();
			Intent intent = new Intent().setClass(mContext, ProductCardActivity.class);
			intent.putExtra(ProductCardActivity.EXTRA_FROM, Constants.FLURRY_FROM.OtherProduct.toString());
			intent.putExtra(ProductCardActivity.ID, b.getProductBean().getId());
			mContext.startActivity(intent);
		} catch (Exception e) {}
		
	}

}
