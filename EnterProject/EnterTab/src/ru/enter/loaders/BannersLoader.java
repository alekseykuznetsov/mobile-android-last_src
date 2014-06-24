package ru.enter.loaders;

import java.util.List;

import ru.enter.beans.BannerBean;
import ru.enter.parsers.BannersParser;
import ru.enter.utils.PreferencesManager;
import ru.enter.utils.URLManager;
import ru.enter.utils.Utils;
import android.content.AsyncTaskLoader;
import android.content.Context;

public class BannersLoader extends AsyncTaskLoader<List<BannerBean>> {

	private List<BannerBean> mData;

	public BannersLoader(Context context) {
		super(context);
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
	public List<BannerBean> loadInBackground() {
		int cityId = PreferencesManager.getCityid();
		String url = URLManager.getPromoNew(cityId, String.valueOf("640x252"));
		return new BannersParser().parseDataNew(url);
	}

	@Override
	protected void onStopLoading() {
		cancelLoad();
	}

	@Override
	public void onCanceled(List<BannerBean> data) {
		super.onCanceled(data);
	}

	@Override
	protected void onReset() {
	    onStopLoading();
	}

	@Override
	public void deliverResult(List<BannerBean> data) {
		mData = data;

		if (isStarted()) {
			super.deliverResult(data);
		}

	}

}
