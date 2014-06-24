package ru.enter.loaders;

import ru.enter.parsers.SearchCountParser;
import ru.enter.utils.PreferencesManager;
import ru.enter.utils.URLManager;
import android.content.Context;

public class CountsSearchLoader extends FullScreenLoader<Integer>{
	
	private String mSearchQuery;
	
	public CountsSearchLoader(Context context, String searchQuery) {
		super(context);
		mSearchQuery = searchQuery;	
	}
	
	@Override
	protected void onStartLoading() {
		forceLoad();
	}

	@Override
	public Integer loadInBackground() {
		SearchCountParser parser = new SearchCountParser(URLManager.getSearchV2Count(PreferencesManager.getCityid(),mSearchQuery));
		return parser.parse();
	}
	
	@Override
	public void deliverResult(Integer count) {	
		if (isStarted()) {
			super.deliverResult(count);
		}
	}
	
	@Override
	protected void onStopLoading() {
		cancelLoad();
	}
	
	@Override
	public void onCanceled(Integer count) {
		super.onCanceled(count);
	}
	
	@Override
	protected void onReset() {
		super.onReset();
        onStopLoading();
	}

}
