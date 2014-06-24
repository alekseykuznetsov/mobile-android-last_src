package ru.enter.loaders;

import ru.enter.beans.ProductBean;
import ru.enter.parsers.ProductInfoParser;
import android.content.Context;

public class ScanerBarcodeLoader extends FullScreenLoader<ProductBean>{
	
	private String mUrl;
	private ProductBean mBean;

	public ScanerBarcodeLoader(Context context, String stringUrl) {
		super(context);
		mUrl = stringUrl;
	}
	
	@Override
	protected void onStartLoading() {
		
		if (mBean == null) {
			forceLoad();
			
		} else {
			deliverResult(mBean);
		}
		
	}

	@Override
	public ProductBean loadInBackground() {
		ProductInfoParser parser = new ProductInfoParser(mUrl);
		return parser.parse();
	}
	
	@Override
	public void deliverResult(ProductBean data) {
		
		if (isStarted()) {
			if ( data != null) {
				mBean = data;
			}
			
			super.deliverResult(data);
		}
	}
	
	@Override
	protected void onStopLoading() {
		cancelLoad();
	}
	
	@Override
	public void onCanceled(ProductBean data) {
		super.onCanceled(data);
	}
	
	@Override
	protected void onReset() {
		super.onReset();
        onStopLoading();
	}
}