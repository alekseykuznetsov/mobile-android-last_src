package ru.enter.loaders;

import java.util.List;

import ru.enter.beans.ProductBean;
import ru.enter.fragments.SearchFragment;
import ru.enter.parsers.ItemsListParser;
import ru.enter.utils.PreferencesManager;
import ru.enter.utils.URLManager;
import ru.enter.utils.Utils;
import android.content.Context;

public class SearchLoader extends FullScreenLoader<List<ProductBean>>{
	
	private int mCurrentPage;
	private String mSearchQuery;
	private static final int img_size = 163;
	
	private List<ProductBean> mProducts;
	
	public SearchLoader(Context context, String searchQuery, int currentPage) {
		super(context);
		mSearchQuery = searchQuery;
		mCurrentPage = currentPage;
	}
	
	@Override
	protected void onStartLoading() {
		
		if (Utils.isEmptyList(mProducts)) {
			if (mCurrentPage == 1) {
				showDialog();
			}
			
			forceLoad();
			
		} else {
			deliverResult(mProducts);
		}
		
	}

	@Override
	public List<ProductBean> loadInBackground() {
		ItemsListParser parser = new ItemsListParser(URLManager.getSearchV2(PreferencesManager.getCityid(),
				mSearchQuery, img_size,SearchFragment.PRODUCTS_ON_PAGE, mCurrentPage));
		return parser.parse();
	}
	
	@Override
	public void deliverResult(List<ProductBean> data) {
		
		dismissDialog();
		
		if (isStarted()) {
			if ( ! Utils.isEmptyList(data)) {
				mProducts = data;
			}
			
			super.deliverResult(data);
		}
	}
	
	@Override
	protected void onStopLoading() {
		cancelLoad();
	}
	
	@Override
	public void onCanceled(List<ProductBean> data) {
		super.onCanceled(data);
	}
	
	@Override
	protected void onReset() {
		super.onReset();
        onStopLoading();
	}
}