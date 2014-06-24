package ru.enter.fragments;

import java.util.List;

import ru.enter.ScanerResultActivity;
import ru.enter.beans.ProductBean;
import ru.enter.loaders.ScanerQRLoader;
import ru.enter.utils.PreferencesManager;
import ru.enter.utils.URLManager;
import android.app.LoaderManager.LoaderCallbacks;
import android.content.Loader;
import android.os.Bundle;

public class ScanerResultFragment extends GridFragment implements LoaderCallbacks<List<ProductBean>>{
	
	private static final int LOADER_ID = 300;
	private static final String IMAGE_SIZE = "163";
	
	private String mUrlString;
	
	public static ScanerResultFragment getInstance() {
		return new ScanerResultFragment();
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		int geo = PreferencesManager.getCityid();
		String hash = ((ScanerResultActivity) getActivity()).getHashExtraString();
		mUrlString = URLManager.getSearchByQR(hash, geo, IMAGE_SIZE);

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
		return new ScanerQRLoader(getActivity(), mUrlString);
	}

	@Override
	public void onLoadFinished(Loader<List<ProductBean>> loader, List<ProductBean> result) {
		setProducts(result);
		getEmptyView().setText("Товары не обнаружены");
	}

	@Override
	public void onLoaderReset(Loader<List<ProductBean>> loader) {}
	

}
