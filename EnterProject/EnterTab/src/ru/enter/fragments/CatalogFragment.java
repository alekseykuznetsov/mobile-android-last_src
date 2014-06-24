package ru.enter.fragments;

import java.util.ArrayList;
import java.util.EmptyStackException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;

import com.flurry.android.FlurryAgent;
import com.google.analytics.tracking.android.EasyTracker;

import ru.enter.CatalogActivity;
import ru.enter.R;
import ru.enter.adapters.CatalogGridAdapter;
import ru.enter.beans.CatalogListBean;
import ru.enter.data.CatalogNode;
import ru.enter.data.CatalogTree;
import ru.enter.loaders.CatalogLoader;
import ru.enter.loaders.FullScreenLoader;
import ru.enter.utils.Constants;
import ru.enter.utils.PreferencesManager;
import ru.enter.widgets.CatalogNavigator;
import android.app.Fragment;
import android.app.LoaderManager.LoaderCallbacks;
import android.content.Loader;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class CatalogFragment extends Fragment implements OnItemClickListener, LoaderCallbacks<Void>{

	private CatalogNavigator mNavigator;
	private CatalogGridAdapter mAdapter;
	private Stack<CatalogNode> mWay = new Stack<CatalogNode>();

	public static CatalogFragment getInstance() {
		return new CatalogFragment();
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setHasOptionsMenu(true);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {

		mNavigator = (CatalogNavigator) getActivity().getActionBar().getCustomView(); 

		View view = inflater.inflate(R.layout.catalog_fr, null);
		GridView grid = (GridView) view.findViewById(R.id.catalog_fr_grid);
		TextView empty = (TextView) view.findViewById(R.id.catalog_fr_grid_empty);
		grid.setEmptyView(empty);
		mAdapter = new CatalogGridAdapter(getActivity());
		grid.setAdapter(mAdapter);
		grid.setOnItemClickListener(this);
		return view;

	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		getLoaderManager().initLoader(0, null, this);
	}

	@Override
	public void onResume () {

		RelativeLayout header = (RelativeLayout) getActivity().findViewById(R.id.catalog_ac_relative_header);
		header.setVisibility(View.VISIBLE);
		LinearLayout linear = (LinearLayout) getActivity().findViewById(R.id.shop_locator_widget_shops);
		linear.setVisibility(View.VISIBLE);

		TextView shop = (TextView) getActivity().findViewById(R.id.catalog_ac_txt_shop);
		ImageButton shop_btn = (ImageButton) getActivity().findViewById(R.id.catalog_ac_btn_location);
		shop_btn.setVisibility(View.VISIBLE);
		LinearLayout sortLinear = (LinearLayout) getActivity().findViewById(R.id.shop_locator_widget_sorts);
		sortLinear.setVisibility(View.GONE);
		//shop.setGravity(Gravity.LEFT);
		if(PreferencesManager.getHasShop()) {
			header.setVisibility(View.VISIBLE);
		} else {
			header.setVisibility(View.GONE);
		}

		if (PreferencesManager.getUserCurrentShopId() == 0){
			shop.setText("Общий каталог");
		}else{
			shop.setText(PreferencesManager.getUserCurrentShopName());
		}
		//updateShopNavbar();
		super.onResume();
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);

		int numColumns;
		if(newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE){
			numColumns = 5;
		}else{
			numColumns = 3;
		}

		GridView grid = (GridView) getView().findViewById(R.id.catalog_fr_grid);
		grid.setNumColumns(numColumns);
	}

	//--------------------------------------------------------------WORK_WITH_TREE--------------------------------------------------------------//

	private void showCurrentCategory () {
		List<CatalogNode> current = mWay.isEmpty() ? CatalogTree.getRoots() : mWay.lastElement().getChildren();
		setupNavigatorHeader();
		mAdapter.setObjects(current);
	}

	private void showNextCategory (CatalogNode fromThis) {
		mWay.push(fromThis);
		ArrayList<CatalogNode> next = fromThis.getChildren();
		setupNavigatorHeader();
		mAdapter.setObjects(next);
		
		int stackSize = mWay.size();
		if(stackSize <= 1){
			Map<String, String> flurryParam = new HashMap<String, String>();
			flurryParam.put(Constants.FLURRY_EVENT_PARAM.Category.toString(), fromThis.getNode().getName());
			FlurryAgent.logEvent(Constants.FLURRY_EVENT.Catalog_Section.toString(), flurryParam);
		}
	}

	private void showPrevCategory () throws EmptyStackException{
		mWay.pop();
		showCurrentCategory();
	}

	public void changeCategory(CatalogNode root) {
		mWay = new Stack<CatalogNode>();
		mWay.push(root);

		showCurrentCategory();
	}

	private void setupNavigatorHeader () {
		if (mWay.size() > 0) {
			mNavigator.setCategory (mWay.firstElement());
			mNavigator.setTitle(mWay.lastElement().getNode().getName());
		} else {
			mNavigator.setCategory (null);
			mNavigator.setTitle(null);
		}
		//updateShopNavbar();
	}


	//----------------------------------------------------------//
	/*
	private void updateShopNavbar(){
		RelativeLayout header = (RelativeLayout) getActivity().findViewById(R.id.catalog_ac_relative_header);
		header.setVisibility(View.VISIBLE);
		TextView shop = (TextView) getActivity().findViewById(R.id.catalog_ac_txt_shop);
		ImageButton shop_btn = (ImageButton) getActivity().findViewById(R.id.catalog_ac_btn_location);
		if(mWay!=null&&mWay.size()>0)
			shop_btn.setVisibility(View.GONE);
		else
			shop_btn.setVisibility(View.VISIBLE);
		//shop.setGravity(Gravity.LEFT);
		if (PreferencesManager.getUserCurrentShopId() == 0){
			shop.setText("Общий каталог");
			if(mWay!=null&&mWay.size()>0)
				header.setVisibility(View.GONE);
			else
				header.setVisibility(View.VISIBLE);
		}
		else{
			shop.setText(PreferencesManager.getUserCurrentShopName());
		}

	}
	 */
	//--------------------------------------------------------------------------------------------------------------------------------------------//

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int listPosition, long id) {
		CatalogActivity activity = (CatalogActivity) getActivity();

		CatalogNode node = mAdapter.getItem(listPosition);
		CatalogListBean bean = node.getNode();

		EasyTracker.getTracker().sendEvent("category/get", "buttonPress", bean.getName(), (long)bean.getId());
		if (bean.isIs_category_list()) {			
			showNextCategory(node);			
		} else {
			mNavigator.setTitle(bean.getName());
			activity.startProductsListFragment(bean.getId(), bean.getCount(), bean.getName());
		}
	}

	public void onBackPressed() {
		try {
			//предыдущий уровень
			showPrevCategory();
		} catch (EmptyStackException ex) {
			getActivity().finish();
		}
	}

	// ----------------------------LOADER--------------------------------//

	@Override
	public Loader<Void> onCreateLoader(int id, Bundle args) {
		return new CatalogLoader(getActivity());
	}

	@Override
	public void onLoadFinished(Loader<Void> loader, Void result) {
		showCurrentCategory();
		//заполняем меню переходов рутами
		setNavigator();
	}

	private void setNavigator () {
		mNavigator.setMenuItems(CatalogTree.getRoots());
	}

	@Override
	public void onLoaderReset(Loader<Void> loader) {
	}


}
