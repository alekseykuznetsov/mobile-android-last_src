package ru.enter.fragments;

import java.util.ArrayList;
import java.util.List;

import ru.enter.BannersActivity;
import ru.enter.beans.ProductBean;
import ru.enter.loaders.BannerProductsLoader;
import ru.enter.utils.PreferencesManager;
import ru.enter.utils.URLManager;
import android.app.LoaderManager.LoaderCallbacks;
import android.content.Loader;
import android.os.Bundle;

public class BannersFragment extends GridFragment implements LoaderCallbacks<List<ProductBean>>{
	
	private static final int LOADER_ID = 400;
	private static final int IMAGE_SIZE = 163;
	
	private String mUrlString;
	
	public static BannersFragment getInstance() {
		return new BannersFragment();
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		int geo = PreferencesManager.getCityid();
		ArrayList<Integer> ids = ((BannersActivity) getActivity()).getIDExtraArray();
		mUrlString = URLManager.getProductListByIds(geo, IMAGE_SIZE, ids);

	}
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		setEnableZoom(false);
		getLoaderManager().initLoader(LOADER_ID, null, this);
	}
	
	
	
	//-----------------------------------------LOADER---------------------------------------------//

	@Override
	public Loader<List<ProductBean>> onCreateLoader(int id, Bundle args) {
		return new BannerProductsLoader(getActivity(), mUrlString);
	}

	@Override
	public void onLoadFinished(Loader<List<ProductBean>> loader, List<ProductBean> result) {
		setProducts(result);
		getEmptyView().setText("Товары не обнаружены");
	}

	@Override
	public void onLoaderReset(Loader<List<ProductBean>> loader) {}
	

}
