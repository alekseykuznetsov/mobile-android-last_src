package ru.ideast.shopitemfragment.tabs;


import ru.enter.R;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.ListView;

public abstract class ListTabActivity extends BaseTabActivity implements OnScrollListener {
	
	ListView lvList;
	boolean lock = false;
	float startY, endY;
	int direction = 1;
	float mLastFirstVisibleItem = 0;
	
	@Override
	protected void onCreate (Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.product_info_list_activity);
		lvList = (ListView)findViewById(R.id.infoList);
		lvList.setVerticalFadingEdgeEnabled(false);
		lvList.setOnScrollListener(this);
		
	}
	
	abstract void setAdapterToListView();


	@Override
	public void onScroll(AbsListView view, int firstVisibleItem,
			int visibleItemCount, int totalItemCount) {

	}

	
	@Override
	public void onScrollStateChanged(AbsListView view, int scrollState) {
	
		int firstVisible = lvList.getFirstVisiblePosition();
		
		
		View v = lvList.getChildAt(0);
		int top = (v == null) ? 0 : v.getTop();
		if(scrollState == SCROLL_STATE_IDLE && top == 0){
			Log.e("onScrollStateChanged","IDLE");
			//ProductCardActivity.SLIDER_ON = true;
			if(mScrollListener != null){
				mScrollListener.onSliderOn();
			}
		}
	}
	
}
