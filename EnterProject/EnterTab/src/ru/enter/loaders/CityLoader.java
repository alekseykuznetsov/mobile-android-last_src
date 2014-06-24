package ru.enter.loaders;

import java.util.List;

import ru.enter.beans.CitiesBean;
import ru.enter.parsers.CitiesParser;
import ru.enter.utils.URLManager;
import android.content.AsyncTaskLoader;
import android.content.Context;

public class CityLoader extends AsyncTaskLoader<List<CitiesBean>> {


	public CityLoader(Context context) {
		super(context);
	}

	@Override
	protected void onStartLoading() {
		forceLoad();
	}

	@Override
	public List<CitiesBean> loadInBackground() {
		return new CitiesParser(URLManager.getCities()).parse();
	}

	@Override
	protected void onStopLoading() {
		cancelLoad();
	}

	@Override
	public void onCanceled(List<CitiesBean> data) {
		super.onCanceled(data);
	}

	@Override
	protected void onReset() {
	    onStopLoading();
	}

	@Override
	public void deliverResult(List<CitiesBean> data) {

		if (isStarted()) {
			super.deliverResult(data);
		}

	}

}
