package ru.enter.fragments;

import java.util.List;

import com.google.analytics.tracking.android.EasyTracker;

import ru.enter.CatalogActivity;
import ru.enter.ProductCardActivity;
import ru.enter.R;
import ru.enter.DataManagement.BasketManager;
import ru.enter.adapters.GridAdapter;
import ru.enter.adapters.GridAdapter.ViewHolder;
import ru.enter.beans.ProductBean;
import ru.enter.dialogs.alert.BasketAddDialogFragment;
import ru.enter.utils.Constants;
import ru.enter.utils.ViewMode;
import ru.enter.widgets.OnGridColumnsChanged;
import ru.enter.widgets.PinchGridView;
import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.GridView;
import android.widget.ProgressBar;
import android.widget.TextView;

/**
 * Базовый класс для фрагментов с таблицей товаров. Вшита разметка.
 * @author ideast
 *
 */
public class GridFragment extends Fragment implements OnGridColumnsChanged {
	
	public static final int MIN_COLUMNS = 2;
	public static final int MID_COLUMNS = 3;
	public static final int MAX_COLUMNS = 4;
	
	private ProgressBar mLoadingProgress;
	private PinchGridView mGrid;
	private TextView mEmpty;

	private GridAdapter mAdapter;

	private OnGridEndReachedListener mEndListener;
	
	private boolean itemEnableClick;
	
	//при этом флаге появляется меню сверху с вариантами увеличения и грид становится скалируемым
	private boolean mZoomEnable = true;
	private MenuItem mZoomItem;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		
		View view = inflater.inflate(R.layout.product_list_fr_grid, null);
		mEmpty = (TextView) view.findViewById(R.id.product_list_fr_grid_empty);
		mGrid = (PinchGridView) view.findViewById(R.id.product_list_fr_pinchgrid);
		mLoadingProgress = (ProgressBar) view.findViewById(R.id.product_list_fr_grid_progress);
		mAdapter = new GridAdapter(getActivity());
		mAdapter.setOnClickListener(mClickListener);
		mGrid.setMinimumColumns(MIN_COLUMNS);
		mGrid.setMaximumColumns(MAX_COLUMNS);
		mGrid.setFriction(0.1f);
		mGrid.setEmptyView(mEmpty);
		mGrid.setAdapter(mAdapter);
		mGrid.setOnScrollListener(mScrollListener);
		mGrid.setOnColumnsChangedListener(this);
		return view;
	}
	
	@Override
	public void onResume() {
		super.onResume();
		itemEnableClick = true;
	}
	
	public GridView getGrid () {
		return mGrid;
	}
	
	public GridAdapter getAdapter () {
		return mAdapter;
	}
	
	public TextView getEmptyView () {
		return mEmpty;
	}
	
	public List<ProductBean> getProducts () {
		return mAdapter.getObjects();
	}
	
	public void setProducts (List<ProductBean> products) {
		mAdapter.setObjects(products);
	}
	
	public boolean addProducts (List<ProductBean> products) {
		return mAdapter.addObjects(products);
	}
	
	public void clearProducts () {
		mAdapter.clearObjects();
	}
	
	public void setEnableZoom (boolean zoomEnable) {
		mZoomEnable = zoomEnable;
		if (mZoomItem != null) {
			mZoomItem.setVisible(mZoomEnable);
		}
		mGrid.setUsePinchDetector(mZoomEnable);
	}
	
	public void showProgress () {
		mLoadingProgress.setVisibility(View.VISIBLE);
	}
	
	public void hideProgress () {
		mLoadingProgress.setVisibility(View.GONE);
	}
	
	
	//--------------------------------------------------------------------------------------//
	
	public interface OnGridEndReachedListener {
		void onEndReached();
	}
	
	public void setOnGridEndReachedListener (OnGridEndReachedListener listener) {
		mEndListener = listener;
	}
	
	//----------------------------------MENU-----------------------------------------------//
	
	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		super.onCreateOptionsMenu(menu, inflater);
		if (mZoomEnable) { //TODO
			inflater.inflate(R.menu.actionbar_menupart_zoom, menu);
			mZoomItem = menu.findItem(R.id.menu_zoom);
			mZoomItem.setIcon(R.drawable.icn_actionbar_items9);
		}
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		//ZOOM
		switch (item.getItemId()) {
		case R.id.menu_zoom2:
			mGrid.setNumColumns(MIN_COLUMNS);
			changeColumnsIcon(MIN_COLUMNS);
			break;
		case R.id.menu_zoom4:
			mGrid.setNumColumns(MID_COLUMNS);
			changeColumnsIcon(MID_COLUMNS);
			break;
		case R.id.menu_zoom9:
			mGrid.setNumColumns(MAX_COLUMNS);
			changeColumnsIcon(MAX_COLUMNS);
			break;
		default:
			break;
		}
		return super.onOptionsItemSelected(item);
	}
	
	private void changeColumnsIcon (int num) {
		switch (num) {
		case MIN_COLUMNS:
			mZoomItem.setIcon(R.drawable.icn_actionbar_items2);
			break;
		case MID_COLUMNS:
			mZoomItem.setIcon(R.drawable.icn_actionbar_items4);
			break;
		case MAX_COLUMNS:
			mZoomItem.setIcon(R.drawable.icn_actionbar_items9);
			break;
		}
	}
	
	//-----------------------------------------------------------------------------------//

	private OnClickListener mClickListener = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			switch(v.getId()){
			//кнопка купить в гриде
			case R.id.product_grid_row_button:
				ProductBean bean = (ProductBean) v.getTag();
				BasketManager.addProduct(bean);
				EasyTracker.getTracker().sendEvent("cart/add-product", "buttonPress", bean.getName(), (long) bean.getId());
				BasketAddDialogFragment dialog = BasketAddDialogFragment.getInstance();
				dialog.setProductMessage(bean.getName());
				Activity activity = (Activity) getActivity();
				dialog.show(activity.getFragmentManager(), "basket_add");
				break;
			
			//тап по товару
			case R.id.product_grid_row_root:
				if(itemEnableClick){
					Intent intent = new Intent(getActivity(), ProductCardActivity.class);
					ViewHolder holder = (ViewHolder) v.getTag(); 
					EasyTracker.getTracker().sendEvent("product/get", "buttonPress", holder.bean.getName(), (long) holder.bean.getId());
					intent.putExtra(ProductCardActivity.PRODUCT_ID, holder.bean.getId());
					intent.putExtra(ProductCardActivity.EXTRA_FROM, Constants.FLURRY_FROM.Catalog.toString());
					
					Bundle bundle = getArguments();
					if (bundle != null && bundle.containsKey(CatalogActivity.CATEGORY_NAME)){
						String categoryName = bundle.getString(CatalogActivity.CATEGORY_NAME);
						intent.putExtra(CatalogActivity.CATEGORY_NAME, categoryName);
					}
					startActivity(intent);
					itemEnableClick = false;
				}
				break;
			
			}
		}
	};

	private OnScrollListener mScrollListener = new OnScrollListener() {
		
		@Override
		public void onScrollStateChanged(AbsListView view, int scrollState) {
			//остановлили скролл - начали грузить фотки
			if (scrollState == OnScrollListener.SCROLL_STATE_IDLE) {
				mAdapter.setLoad(true);
				mAdapter.notifyDataSetChanged();
			} else {
				mAdapter.setLoad(false);
			}
		}
		
		@Override
		public void onScroll(AbsListView view, int firstVisible, int visibleCount, int totalCount) {
			boolean isBottom = firstVisible + visibleCount >= totalCount;
			//чтобы не срабатывал листенер на пустом гриде
			boolean isEmpty = (totalCount == 0);
	
			if (isBottom && !isEmpty) {
				if (mEndListener != null) mEndListener.onEndReached();
			}
		}
	};
	
	@Override
	public void onColumnsChanged(int newCount) {
		
		if(newCount > 0 && newCount <= MIN_COLUMNS) {
			changeColumnsIcon(MIN_COLUMNS);
			mAdapter.setViewMode(ViewMode.LARGE);
		} else if(newCount > mGrid.getMinimumColumns() && newCount < mGrid.getMaximumColumns()) {
			changeColumnsIcon(MID_COLUMNS);
			mAdapter.setViewMode(ViewMode.MIDDLE);
		} else {
			changeColumnsIcon(MAX_COLUMNS);
			mAdapter.setViewMode(ViewMode.SMALL);
		}
		
	}
	
}
