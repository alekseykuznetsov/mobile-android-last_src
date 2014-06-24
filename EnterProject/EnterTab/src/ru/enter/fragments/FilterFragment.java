package ru.enter.fragments;

import java.util.HashSet;
import java.util.List;

import ru.enter.CatalogActivity;
import ru.enter.R;
import ru.enter.adapters.FilterOptionsAdapter;
import ru.enter.beans.FilterBean;
import ru.enter.beans.OptionsBean;
import ru.enter.loaders.FiltersLoader;
import ru.enter.parsers.FiltersParser;
import ru.enter.utils.FiltersManager;
import ru.enter.utils.PreferencesManager;
import ru.enter.utils.URLManager;
import android.app.Fragment;
import android.app.LoaderManager.LoaderCallbacks;
import android.content.Loader;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ExpandableListView;
import android.widget.FrameLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;

public class FilterFragment extends Fragment implements OnClickListener, OnTouchListener, LoaderCallbacks<List<FilterBean>>{//, OnCheckedChangeListener

	protected static final int PRICE_LOWER = 1;
	protected static final int PRICE_TOP = 2;
	protected static final int PRODUCER_LOWER = 3;
	protected static final int PRODUCER_TOP = 4;
	protected static final int RATING = 5;
	
	private static final int LOADER_ID = 500;
	
	private int currentCheckRadioButtonId = 0;
	
	//private RadioGroup mRadioGroupSort;
	private ExpandableListView mList;

	private FiltersManager mFiltersManager;
	
	private FilterOptionsAdapter mAdapter;
	private String mUrl;
	private FrameLayout mLoadingProgress;
	
	public static FilterFragment getInstance() {
		return new FilterFragment();
	}
	
	public void showProgress () {
		if (mLoadingProgress != null)
			mLoadingProgress.setVisibility(View.VISIBLE);
	}
	
	public void hideProgress () {
		if (mLoadingProgress != null)
			mLoadingProgress.setVisibility(View.GONE);
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setRetainInstance(true);
		mFiltersManager = new FiltersManager();
		mFiltersManager.init();
		int categoryId = getArguments().getInt(CatalogActivity.CATEGORY_ID, 0);
		int geo = PreferencesManager.getCityid();
		mUrl = URLManager.getFilters(geo, String.valueOf(categoryId));
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		
		View view = inflater.inflate(R.layout.filters_fr, null);
		mLoadingProgress = (FrameLayout) view.findViewById(R.id.filters_fr_progress_frame);
		showProgress(); // показываем крутяшку, пока не получим даныне с загрузчика
		
		Button apply = (Button) view.findViewById(R.id.filters_fr_button_apply);
		Button clear = (Button) view.findViewById(R.id.filters_fr_button_clear);
		apply.setOnClickListener(this);
		clear.setOnClickListener(this);
		
		mList = (ExpandableListView) view.findViewById(R.id.filters_fr_explist);
		View header = inflater.inflate(R.layout.filters_fr_explist_header, null);
		
		mAdapter = new FilterOptionsAdapter(getActivity());
		mList.addHeaderView(header);
		mList.setAdapter(mAdapter);
		mAdapter.setFilterManager(mFiltersManager);
		/*
		mRadioGroupSort = (RadioGroup) view.findViewById(R.id.filters_fr_radiogroup);
		// устанавливаем предыдущие значения сортировки
		if (currentCheckRadioButtonId != 0){
			mRadioGroupSort.check(currentCheckRadioButtonId);
		}

		mRadioGroupSort.setOnCheckedChangeListener(this);
		*/		
		view.setOnTouchListener(this);

		return view;
	}
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		getLoaderManager().initLoader(LOADER_ID, null, this);
	}
	
	@Override
	public void onHiddenChanged(boolean hidden) {
		super.onHiddenChanged(hidden);
		if(hidden){
			// при ручном скрытии фрагмента (имитация "отмены") - обнуляем все последние изменения 
	   		mFiltersManager.init();
		}
		else{
			// при открытии фрагмента - обновляем все ранее установленные флаги
			mAdapter.notifyDataSetChanged();
			expandSelectedFilters();
		}
	}
	
	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		View rightView = getView().findViewById(R.id.filters_fr_linear_full_info);
		LayoutParams params = (LayoutParams) rightView.getLayoutParams();
		
		if(newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE){
			params.weight = 2.5f;
		}else{
			params.weight = 1.2f;
		}
	}
	
	private void expandSelectedFilters(){
		HashSet<Integer> newOptions = mFiltersManager.getNewOptions();
		for(int i = 0; i < mFiltersManager.getFilters().size(); i++){
			FilterBean filter = mFiltersManager.getFilters().get(i);
			mList.collapseGroup(i);
			if (filter.getType() == FiltersParser.FILTER_OPTIONAL){
				if (filter.getOptions().size() < 4){
					mList.expandGroup(i);
				}
				else{
					for(int j = 0; j < filter.getOptions().size(); j++){
						OptionsBean option = filter.getOptions().get(j);
						if (newOptions.contains(option.getId())){
							j = filter.getOptions().size();
							mList.expandGroup(i);
						}
					}
				}
			}
			else
				if (filter.getType() == FiltersParser.FILTER_SOLID || filter.getType() == FiltersParser.FILTER_DISCRET){
					mList.expandGroup(i);
				}
		}
	}
	
	@Override
	public boolean onTouch(View v, MotionEvent event) {
		// если кликаем в любом месте, кроме окна с фильтрами
		//TODO не сработает при клике по меню
		if (v.getId() != R.id.filters_fr_linear_full_info ){
			getActivity().getFragmentManager().beginTransaction().hide(this).commit();
		}
		return true;
	}
	/*
	private void sortProductList(int type){
		ProductsListFragment productListFragment = (ProductsListFragment) getFragmentManager().findFragmentByTag(CatalogActivity.PRODUCT_TAG);
		clearNewFiltersSelections();
		productListFragment.setSortType(type);
		getActivity().getFragmentManager().beginTransaction().hide(this).commit();
	}
	*/
	@Override
	public void onClick(View v) {

		switch(v.getId()){
		case R.id.filters_fr_button_apply:
			mFiltersManager.apply();
			ProductsListFragment productListFragment = (ProductsListFragment) getFragmentManager().findFragmentByTag(CatalogActivity.PRODUCT_TAG);
			productListFragment.setFilters(mFiltersManager.getSelectedOptionsIDs(), mFiltersManager.getSelectedSliders());
			getActivity().getFragmentManager().beginTransaction().hide(this).commit();
			break;
		case R.id.filters_fr_button_clear:
			mFiltersManager.clear();
			mAdapter.notifyDataSetChanged();
			break;
		default:
			break;
		}
	}
	
	private void clearNewFiltersSelections(){

	}
/*
	@Override
	public void onCheckedChanged(RadioGroup group, int checkedId) {
		
		int currentSort = 0;
		switch (checkedId) {
		case R.id.filters_fr_radiobutton_price_lower:
			currentSort = PRICE_LOWER;
			break;
		case R.id.filters_fr_radiobutton_price_top:
			currentSort = PRICE_TOP;
			break;
		case R.id.filters_fr_radiobutton_producer_lower:
			currentSort = PRODUCER_LOWER;
			break;
		case R.id.filters_fr_radiobutton_producer_top:
			currentSort = PRODUCER_TOP;
			break;
		case R.id.filters_fr_radiobutton_rating:
			currentSort = RATING;
			break;
		default:
			break;
		}
		if (currentSort != 0){
			sortProductList(currentSort);
			currentCheckRadioButtonId = checkedId;
		}
	}
	*/
	
	//----------------------------------------LOADER------------------------------------------//

	@Override
	public Loader<List<FilterBean>> onCreateLoader(int id, Bundle args) {
		return new FiltersLoader(getActivity(), mUrl);
	}

	@Override
	public void onLoadFinished(Loader<List<FilterBean>> loader, List<FilterBean> result) {
		mFiltersManager.setFilters(result);
		mAdapter.setFilterManager(mFiltersManager);
		expandSelectedFilters();
		hideProgress();		
	}

	@Override
	public void onLoaderReset(Loader<List<FilterBean>> arg0) {}
	
}
