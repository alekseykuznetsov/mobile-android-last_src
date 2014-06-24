package ru.ideast.shopitemfragment.tabs;

import ru.enter.R;
import ru.enter.adapters.ProductCardProductsGridAdapter;
import android.os.Bundle;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.GridView;

public abstract class GridTabActivity extends BaseTabActivity implements OnScrollListener {

	GridView lvGrid;
	boolean lock = false;
	boolean justSwitched = false;
	float startY, endY;
	float mLastFirstVisibleItem = 0;

	@Override
	protected void onCreate (Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.product_info_grid_activity);
		lvGrid = (GridView) findViewById(R.id.infoGrid);

		lvGrid.setFriction(0.1f);
		lvGrid.setVerticalFadingEdgeEnabled(false);
		lvGrid.setOnScrollListener(this);
	}

	abstract void setAdapterToListView ();

	@Override
	public void onScroll (AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

	}

	@Override
	public void onScrollStateChanged (AbsListView view, int scrollState) {

		ProductCardProductsGridAdapter adapter = (ProductCardProductsGridAdapter) lvGrid.getAdapter(); // danger
																										// TODO
		View v = lvGrid.getChildAt(0);
		int top = (v == null) ? 0 : v.getTop();

		if (scrollState == OnScrollListener.SCROLL_STATE_IDLE) {
			adapter.setLoad(true);
			adapter.notifyDataSetChanged();
			if (top == 10) {
				if (mScrollListener != null) {
					lvGrid.setSelected(false);
					mScrollListener.onSliderOn();
					lvGrid.setSelection(0);
				}
			}
		} else {
			adapter.setLoad(false);
		}

	}

}
