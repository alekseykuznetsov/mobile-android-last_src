package ru.enter.loaders;

import java.util.List;

import ru.enter.beans.FilterBean;
import ru.enter.parsers.FiltersParser;
import ru.enter.utils.Utils;
import android.content.AsyncTaskLoader;
import android.content.Context;

public class FiltersLoader extends AsyncTaskLoader<List<FilterBean>> {

	private List<FilterBean> mData;
	private String mUrl;

	public FiltersLoader(Context context, String url) {
		super(context);
		mUrl = url;
	}

	@Override
	protected void onStartLoading() {
		if (Utils.isEmptyList(mData)) {
			forceLoad();
		} else {
			deliverResult(mData);
		}
	}

	@Override
	public List<FilterBean> loadInBackground() {
		return new FiltersParser(mUrl).parse();
	}

	@Override
	protected void onStopLoading() {
		cancelLoad();
	}

	@Override
	public void onCanceled(List<FilterBean> data) {
		super.onCanceled(data);
	}

	@Override
	protected void onReset() {
	    onStopLoading();
	}

	@Override
	public void deliverResult(List<FilterBean> data) {
		mData = data;

		if (isStarted()) {
			super.deliverResult(data);
		}

	}
}
