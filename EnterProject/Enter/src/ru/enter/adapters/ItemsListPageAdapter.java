package ru.enter.adapters;

import java.util.ArrayList;

import ru.enter.R;
import ru.enter.beans.ProductBean;
import ru.enter.utils.Utils;
import ru.enter.widgets.DragAndDropListener;
import ru.enter.widgets.DragAndDropView;
import android.content.Context;
import android.os.Parcelable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.GridView;

public class ItemsListPageAdapter extends PagerAdapter{
	private Context mContext;
	private ArrayList<ProductBean> mObjects;
	private DragAndDropListener mDragnDropListener;
	
	private int mRows;
	private int mVerticalSpacing;
	
	public ItemsListPageAdapter (Context context, int numRows, int verticalSpacing){
		mContext = context;
		mObjects = new ArrayList<ProductBean>();
		mRows = numRows;
		mVerticalSpacing = verticalSpacing;
	}
	
	public int getNumRows () {
		return mRows;
	}
	
	public void setObjects (ArrayList<ProductBean> objectsArray){
	//	if (!Utils.isEmptyList(objectsArray)) {
			mObjects = objectsArray;
			notifyDataSetChanged();
	//	}
	}
	
	
	@Override
	public int getCount() {
		//2 в строке
		int itemsPerPage = mRows * 2;
		return mObjects.size() % itemsPerPage > 0 ? 
				mObjects.size() / itemsPerPage + 1 :
					mObjects.size()/itemsPerPage;
	}
	
	//для notify()
	@Override
	public int getItemPosition(Object object) {
		return POSITION_NONE;
	}

	@Override
	public Object instantiateItem(View collection, int position) {
		
		LayoutInflater viewInflater = LayoutInflater.from(mContext);
		DragAndDropView grid = (DragAndDropView)viewInflater.inflate(R.layout.items_lst_grid, null);
		
		ItemsGridAdapter iga = new ItemsGridAdapter(mContext, 0);
		int itemsPerPage = mRows * 2;

			for(int j = position*itemsPerPage; j<position * itemsPerPage + itemsPerPage; j++)
				if(j<mObjects.size())
					iga.add(mObjects.get(j));
		
		grid.setVerticalSpacing(mVerticalSpacing);
		grid.setPadding(Utils.dp2pix(mContext, 10), mVerticalSpacing, Utils.dp2pix(mContext, 10), 0);
		grid.setAdapter(iga);
		grid.setOnDragnDropListener(mDragnDropListener);
		
		((ViewPager) collection).addView(grid,0);
		
		return grid;
	}
	
	public void setDragnDropListener(DragAndDropListener listener){
		mDragnDropListener = listener;
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
	
}