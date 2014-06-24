package ru.enter.fragments;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import com.google.analytics.tracking.android.EasyTracker;

import ru.enter.CatalogActivity;
import ru.enter.R;
import ru.enter.beans.ProductBean;
import ru.enter.beans.SliderSolidBean;
import ru.enter.loaders.ProductsListLoader;
import ru.enter.utils.Pair;
import ru.enter.utils.PreferencesManager;
import ru.enter.utils.TypefaceUtils;
import ru.enter.widgets.SortLabelView;
import ru.enter.widgets.SortViewSelector;
import android.app.LoaderManager.LoaderCallbacks;
import android.content.Loader;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class ProductsListFragment extends GridFragment implements LoaderCallbacks<List<ProductBean>>{

	public static final int PRODUCTS_ON_PAGE = 60;
	private static final int LOADER_ID = 100;
	
	private static final int PRICE_LOWER = 1;
	private static final int PRICE_TOP = 2;
	private static final int PRODUCER_LOWER = 3;
	private static final int PRODUCER_TOP = 4;
	private static final int RATING_TOP = 5;
	private static final int RATING_LOWER = 6;
	
	private int mCategoryId;
	private int mCurrentPage = 1;	//в сервисе нумерация начинается не с 0 страницы, а с 1
	private int mSortType = 0;
	private int mMaxPage;
	private ArrayList<Pair<String, Integer>> mOptionsId;
	private ArrayList<SliderSolidBean> mSliders;
	
	private SortViewSelector mSortSelector;
	private SortLabelView priceSortView, raytingSortView, nameSortView;
	
	private boolean mLoading = false;
	private boolean isMore = true;
	
	public static ProductsListFragment getInstance() {
		return new ProductsListFragment();
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setHasOptionsMenu(true); // своё меню для фрагмента
		getExtras();		
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = super.onCreateView(inflater, container, savedInstanceState);
		// for sorting
		priceSortView = (SortLabelView) getActivity().findViewById(
				R.id.shop_locator_widget_sorts_by_price);
		priceSortView.setText("По цене");
		priceSortView.setOnClickListener(sortListner);
		raytingSortView = (SortLabelView) getActivity().findViewById(
				R.id.shop_locator_widget_sorts_by_rat);
		raytingSortView.setText("По рейтингу");
		raytingSortView.setOnClickListener(sortListner);
		nameSortView = (SortLabelView) getActivity().findViewById(
				R.id.shop_locator_widget_sorts_by_name);
		nameSortView.setText("По алфавиту");
		nameSortView.setOnClickListener(sortListner);
		mSortSelector = new SortViewSelector(priceSortView, raytingSortView,
				nameSortView);
		//raytingSortView.setSelected(true);
		mSortSelector.selectBtn(raytingSortView);
		mSortType = RATING_TOP;
		sortProductList();
		return view;
	}
	
	private void getExtras() {
		Bundle args = getArguments();
		if (args != null && args.containsKey(CatalogActivity.CATEGORY_ID)) {
			mCategoryId = args.getInt(CatalogActivity.CATEGORY_ID, 0);
//			int productsCount = args.getInt(CatalogActivity.PRODUCTS_COUNT, 0);
//			calcPages(productsCount);
		}
	}
	
//	private void calcPages(int productsCount){
//		if (productsCount % PRODUCTS_ON_PAGE == 0){
//			mMaxPage = (productsCount / PRODUCTS_ON_PAGE);
//		}else{
//			mMaxPage = (productsCount / PRODUCTS_ON_PAGE) + 1;
//		}
//	}
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		setOnGridEndReachedListener(mEndListener);
		getLoaderManager().initLoader(LOADER_ID, null, this);
	}
	
	@Override
	public void onResume () {
		
		RelativeLayout header = (RelativeLayout) getActivity().findViewById(R.id.catalog_ac_relative_header);
		header.setVisibility(View.VISIBLE);
		
		LinearLayout sortLinear = (LinearLayout) getActivity().findViewById(R.id.shop_locator_widget_sorts);
		sortLinear.setVisibility(View.VISIBLE);
		
		if (PreferencesManager.getUserCurrentShopId() == 0){
			LinearLayout linear = (LinearLayout) getActivity().findViewById(R.id.shop_locator_widget_shops);
			linear.setVisibility(View.GONE);
		}
//		else{
//			TextView shop = (TextView) getActivity().findViewById(R.id.catalog_ac_txt_shop);
//			shop.setGravity(Gravity.CENTER);
//		}
		
		ImageButton shop_btn = (ImageButton) getActivity().findViewById(R.id.catalog_ac_btn_location);
		shop_btn.setVisibility(View.GONE);
				
		super.onResume();
	}
	
	private OnGridEndReachedListener mEndListener = new OnGridEndReachedListener() {
		
		@Override
		public void onEndReached() {
//			boolean isAll = (mCurrentPage > mMaxPage);
	
			if (isMore && !mLoading) {
				mLoading = true;
				showProgress();
				getLoaderManager().restartLoader(LOADER_ID, null, ProductsListFragment.this);
			}
		}
	};
	
	//----------------------------- LOADER --------------------------------------//

	@Override
	public Loader<List<ProductBean>> onCreateLoader(int id, Bundle args) {
		getEmptyView().setText("");		
		return new ProductsListLoader(getActivity(), mCategoryId, mCurrentPage, mOptionsId, mSliders);
	}
	
	@Override
	public void onLoadFinished(Loader<List<ProductBean>> loader, List<ProductBean> result) {
		if (addProducts(result)) {
			if (mCurrentPage == 1){
				sortProductList();
			}
			mCurrentPage ++;
		}
		

		if (result.size() < PRODUCTS_ON_PAGE) {
			isMore = false;
		}
		
		hideProgress();
		mLoading = false;
		
		setEmptyView (((ProductsListLoader) loader).isFilters());
			
	}
	
	@Override
	public void onLoaderReset(Loader<List<ProductBean>> arg0) { }

	// --------------------------------------------------------------------------- //
	
	private void setEmptyView (boolean filters){
		if (PreferencesManager.getUserCurrentShopId() != 0) {
			getEmptyView().setText("В выбранной категории отсутствуют товары с возможность покупки и получения сейчас. Для просмотра полного списка товаров вам необходимо сбросить фильтр сокращения ассортимента до магазина.");
		} else {
			if (filters)
				getEmptyView().setText("По данным критериям нет товаров. Попробуйте задать другие фильтры.");
			else
				getEmptyView().setText("В данной категории нет товаров.");
		}
	}

	// --------------------------------  Меню -------------------------------------- //
	
	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		super.onCreateOptionsMenu(menu, inflater);
		inflater.inflate(R.menu.actionbar_menupart_filters, menu);
		TextView filter_item = (TextView) menu.findItem(R.id.menu_filters).getActionView();
		filter_item.setTypeface(TypefaceUtils.getBrashTypeface());
		filter_item.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				((CatalogActivity) getActivity()).starFilters(mCategoryId);
			}
		});
	}
	/* Листнер для выбора сортировки */
	OnClickListener sortListner = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			switch (v.getId()) {
			
			case R.id.shop_locator_widget_sorts_by_price:
				if(priceSortView.isSelected()) {
					if(priceSortView.isLowerType()) {
						priceSortView.setSortType(SortLabelView.TOP);
						setSortType(PRICE_TOP);
					} else {
						priceSortView.setSortType(SortLabelView.LOWER);
						setSortType(PRICE_LOWER);
					}
				} else {
					mSortSelector.selectBtn(priceSortView);
					if(priceSortView.isLowerType()) {					
						setSortType(PRICE_LOWER);
					} else {						
						setSortType(PRICE_TOP);
					}
				}			
				break;
			case R.id.shop_locator_widget_sorts_by_rat:
				if(raytingSortView.isSelected()) {
					if(raytingSortView.isLowerType()) {
						raytingSortView.setSortType(SortLabelView.TOP);
						setSortType(RATING_TOP);
					} else {
						raytingSortView.setSortType(SortLabelView.LOWER);
						setSortType(RATING_LOWER);
					}
				} else {
					mSortSelector.selectBtn(raytingSortView);
					if(raytingSortView.isLowerType()) {					
						setSortType(RATING_LOWER);
					} else {						
						setSortType(RATING_TOP);
					}
				}			
				break;				
			case R.id.shop_locator_widget_sorts_by_name:
				if(nameSortView.isSelected()) {
					if(nameSortView.isLowerType()) {
						nameSortView.setSortType(SortLabelView.TOP);
						setSortType(PRODUCER_TOP);
					} else {
						nameSortView.setSortType(SortLabelView.LOWER);
						setSortType(PRODUCER_LOWER);
					}
				} else {
					mSortSelector.selectBtn(nameSortView);
					if(nameSortView.isLowerType()) {					
						setSortType(PRODUCER_LOWER);
					} else {						
						setSortType(PRODUCER_TOP);
					}
				}							

			default:
				break;
			}
			
		}
	};

	
	/*
	 * Сортировка
	 */
	
	public void setSortType(int type){
		mSortType = type;
		switch(mSortType){
			case PRICE_LOWER:
				EasyTracker.getTracker().sendEvent("sort", "По цене", "По цене", (long) 0);
				break;
			case PRICE_TOP:
				EasyTracker.getTracker().sendEvent("sort", "По цене", "По цене", (long) 1);
				break;
			case PRODUCER_LOWER:
				EasyTracker.getTracker().sendEvent("sort", "По алфавиту", "По цене", (long) 0);
				break;
			case PRODUCER_TOP:
				EasyTracker.getTracker().sendEvent("sort", "По алфавиту", "По цене", (long) 1);
				break;
			case RATING_LOWER:
				EasyTracker.getTracker().sendEvent("sort", "По рейтингу", "По цене", (long) 0);
				break;				
			case RATING_TOP:
				EasyTracker.getTracker().sendEvent("sort", "По рейтингу", "По цене", (long) 1);
				break;
		}	
		sortProductList();
	}
	
	public void sortProductList(){
		
		List<ProductBean> objects = getProducts();
		switch(mSortType){
			case PRICE_LOWER:				
				Collections.sort(objects, compare_by_price_lower);
				break;
			case PRICE_TOP:				
				Collections.sort(objects, compare_by_price_top);
				break;
			case PRODUCER_LOWER:				
				Collections.sort(objects, compare_by_producer_lower);
				break;
			case PRODUCER_TOP:				
				Collections.sort(objects, compare_by_producer_top);
				break;
			case RATING_TOP:				
				Collections.sort(objects, compare_by_rait_top);
				break;				
			case RATING_LOWER:				
				Collections.sort(objects, compare_by_rait_lower);
				break;
		}
		setProducts(objects);
	}
	
	private Comparator<ProductBean> compare_by_price_top = new Comparator<ProductBean>() {   
		@Override
	   	public int compare(ProductBean object1, ProductBean object2) {
		   	if(object1.getPrice()>object2.getPrice()){
		   		return 1;
		   	}else if(object1.getPrice()<object2.getPrice()){
		   		return -1;
		   	}else{
		   	    return 0; 
		    }    
		}   
	};
	private Comparator<ProductBean> compare_by_price_lower = new Comparator<ProductBean>() {   
		@Override
	   	public int compare(ProductBean object1, ProductBean object2) {
		   	if(object1.getPrice()<object2.getPrice()){
		   		return 1;
		   	}else if(object1.getPrice()>object2.getPrice()){
		   		return -1;
		   	}else{
		   	    return 0; 
		    }    
		}   
	};
	private Comparator<ProductBean> compare_by_producer_top = new Comparator<ProductBean>() {   
		@Override
	   	public int compare(ProductBean object1, ProductBean object2) {
	   		return object1.getShortname().compareTo(object2.getShortname());
		}   
	};
	private Comparator<ProductBean> compare_by_producer_lower = new Comparator<ProductBean>() {   
		@Override
	   	public int compare(ProductBean object1, ProductBean object2) {
			int result = object1.getShortname().compareTo(object2.getShortname());
			return result*(-1);
		}   
	};
	private Comparator<ProductBean> compare_by_rait_lower = new Comparator<ProductBean>() {   
		@Override
	   	public int compare(ProductBean object1, ProductBean object2) {
		   	if(object1.getRating()<object2.getRating()){
		   		return 1;
		   	}else if(object1.getRating()>object2.getRating()){
		   		return -1;
		   	}else{
		   	    return 0; 
		    } 
		}   
	};
	
	private Comparator<ProductBean> compare_by_rait_top = new Comparator<ProductBean>() {   
		@Override
	   	public int compare(ProductBean object1, ProductBean object2) {
		   	if(object1.getRating()<object2.getRating()){
		   		return -1;
		   	}else if(object1.getRating()>object2.getRating()){
		   		return 1;
		   	}else{
		   	    return 0; 
		    } 
		}   
	};
	
	/*
	 * Фильтрация
	 */
	public void setFilters (ArrayList<Pair<String, Integer>> options_id, ArrayList<SliderSolidBean> sliders){
		mOptionsId = options_id;
		mSliders = sliders;
		mCurrentPage = 1;
		isMore = true;
		clearProducts();
		getLoaderManager().restartLoader(LOADER_ID, null, ProductsListFragment.this);
	}
	
}
