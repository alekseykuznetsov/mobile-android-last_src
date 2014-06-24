package ru.enter.loaders;

import java.util.List;

import ru.enter.beans.ProductBean;
import ru.enter.parsers.ItemsListParser;
import ru.enter.utils.Utils;
import android.content.Context;

public class BannerProductsLoader extends FullScreenLoader<List<ProductBean>>{
	
	private String mUrl;
	private List<ProductBean> mProducts;

	public BannerProductsLoader(Context context, String stringUrl) {
		super(context);
		mUrl = stringUrl;
	}
	
	@Override
	protected void onStartLoading() {
		
		if (Utils.isEmptyList(mProducts)) {
			showDialog();
			forceLoad();
		} else {
			deliverResult(mProducts);
		}
		
	}

	@Override
	public List<ProductBean> loadInBackground() {
		ItemsListParser parser = new ItemsListParser(mUrl);
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