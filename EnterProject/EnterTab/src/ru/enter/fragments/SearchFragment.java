package ru.enter.fragments;

import java.net.URLEncoder;
import java.util.List;

import com.flurry.android.FlurryAgent;
import com.google.analytics.tracking.android.EasyTracker;

import ru.enter.beans.ProductBean;
import ru.enter.loaders.CountsSearchLoader;
import ru.enter.loaders.FullScreenLoader;
import ru.enter.loaders.SearchLoader;
import ru.enter.parsers.ItemsListParser;
import ru.enter.parsers.SearchCountParser;
import ru.enter.utils.Constants;
import ru.enter.utils.PreferencesManager;
import ru.enter.utils.URLManager;
import ru.enter.utils.Utils;
import ru.enter.widgets.LinearSearchWithIcons;
import ru.enter.widgets.LinearSearchWithIcons.OnEnterListener;
import android.app.Activity;
import android.app.LoaderManager.LoaderCallbacks;
import android.content.Context;
import android.content.Loader;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

//implements LoaderCallbacks<List<ProductBean>> 
public class SearchFragment extends GridFragment {

	public static final int PRODUCTS_ON_PAGE = 20;

	private static final int SEARCH_LOADER_ID = 200;
	private static final int COUNTS_LOADER_ID = 201;

	private String mSearchQuery = "";

	private final int START_SEARCH = 0;
	private final int EMPTY = 1;
	private final int ERROR = -1;

	private int mCurrentPage = 1;

	private boolean mLoading = false;
	private boolean isMore = true;

	private Activity mContext;
	private LinearSearchWithIcons search_view;
	
	private void setEmptyViewText(int type) {
		switch (type) {
		case START_SEARCH:
			getEmptyView().setText("Введите название или артикул товара для поиска");
			break;
		case EMPTY:
			getEmptyView().setText("К сожалению ничего не найдено. Попробуйте задать другой поисковый запрос.");
			break;
		case ERROR:
			getEmptyView().setText("Ошибка при загрузке данных.");
			break;
		default:
			break;
		}
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
		mContext = getActivity();
		
		search_view = new LinearSearchWithIcons(getActivity());
		mContext.getActionBar().setCustomView(search_view);
		
		search_view.setOnEnterListener(new OnEnterListener() {

			@Override
			public void onEnterPressed(String searchQuery) {
				FlurryAgent.logEvent(Constants.FLURRY_EVENT.Catalog_Search.toString());
				mSearchQuery = URLEncoder.encode(searchQuery).replace("+", "%20");
				newSearch();
			}
		});

		return super.onCreateView(inflater, container, savedInstanceState);
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		setEnableZoom(false);
		setEmptyViewText(START_SEARCH);
		setOnGridEndReachedListener(mEndListener);
		if (!TextUtils.isEmpty(mSearchQuery)) {
			//getLoaderManager().initLoader(LOADER_ID, null, this);
			getLoaderManager().initLoader(COUNTS_LOADER_ID, null, mCallbackAllCount);
			getLoaderManager().initLoader(SEARCH_LOADER_ID, null, mCallbackSearch);
		}
	}

	private OnGridEndReachedListener mEndListener = new OnGridEndReachedListener() {

		@Override
		public void onEndReached() {
			if (isMore && !mLoading) {
				mLoading = true;
				showProgress();
				//getLoaderManager().restartLoader(LOADER_ID, null,SearchFragment.this);
				getLoaderManager().restartLoader(SEARCH_LOADER_ID, null, mCallbackSearch);
			}
		}
	};

	private void newSearch() {
		if (!TextUtils.isEmpty(mSearchQuery)) {
			clearProducts();
			mLoading = true;
			isMore = true;
			mCurrentPage = 1;			
			//getLoaderManager().restartLoader(LOADER_ID, null, this);
			getLoaderManager().restartLoader(SEARCH_LOADER_ID, null, mCallbackSearch);
			getLoaderManager().restartLoader(COUNTS_LOADER_ID, null, mCallbackAllCount);
		}
	}

	// ----------------------------- LOADER --------------------------------------//
	LoaderCallbacks<Integer> mCallbackAllCount = new LoaderCallbacks<Integer>() {
		
		@Override
		public Loader<Integer> onCreateLoader(int id, Bundle args) {
			// TODO Auto-generated method stub
			return new CountsSearchLoader(mContext, mSearchQuery);
		}
		
		@Override
		public void onLoaderReset(Loader<Integer> loader) {
			// TODO Auto-generated method stub
			
		}
		
		@Override
		public void onLoadFinished(Loader<Integer> loader, Integer data) {
			// TODO Auto-generated method stub			
			EasyTracker.getTracker().sendEvent("search", "buttonPress",mSearchQuery, (long) data);
		}		
	};
	
	LoaderCallbacks<List<ProductBean>> mCallbackSearch = new LoaderCallbacks<List<ProductBean>>() {

		@Override
		public Loader<List<ProductBean>> onCreateLoader(int id, Bundle args) {
			// TODO Auto-generated method stub
			return new SearchLoader(mContext, mSearchQuery, mCurrentPage);
		}
		
		@Override
		public void onLoaderReset(Loader<List<ProductBean>> loader) {
			// TODO Auto-generated method stub			
		}
		
		@Override
		public void onLoadFinished(Loader<List<ProductBean>> loader, List<ProductBean> result) {
			// TODO Auto-generated method stub
			if (result.size() < PRODUCTS_ON_PAGE) {
				isMore = false;
			}

			if (addProducts(result)) {
				mCurrentPage++;
			} else {
				if (!TextUtils.isEmpty(mSearchQuery)) {
					setEmptyViewText(EMPTY);
				}
			}

			hideProgress();
			mLoading = false;			
		}		
	};
	
	/*
	@Override
	public Loader<List<ProductBean>> onCreateLoader(int id, Bundle args) {
		return new SearchLoader(mContext, mSearchQuery, mCurrentPage);
	}

	@Override
	public void onLoadFinished(Loader<List<ProductBean>> loader,
			List<ProductBean> result) {

		if (result.size() < PRODUCTS_ON_PAGE) {
			isMore = false;
		}

		if (addProducts(result)) {
			mCurrentPage++;
		} else {
			if (!TextUtils.isEmpty(mSearchQuery)) {
				setEmptyViewText(EMPTY);
			}
		}

		hideProgress();
		mLoading = false;
		
	}

	@Override
	public void onLoaderReset(Loader<List<ProductBean>> arg0) {
	}
	*/
}