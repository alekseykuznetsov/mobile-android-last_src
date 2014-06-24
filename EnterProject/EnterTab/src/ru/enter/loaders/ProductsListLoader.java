package ru.enter.loaders;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.google.analytics.tracking.android.EasyTracker;

import ru.enter.beans.ProductBean;
import ru.enter.beans.SliderSolidBean;
import ru.enter.fragments.ProductsListFragment;
import ru.enter.parsers.ItemsListParser;
import ru.enter.utils.Pair;
import ru.enter.utils.PreferencesManager;
import ru.enter.utils.URLManager;
import ru.enter.utils.Utils;
import android.content.Context;
import android.content.DialogInterface;

public class ProductsListLoader extends FullScreenLoader<List<ProductBean>>{
	
	private int mCurrentPage;
	private int mCategoryId;
	
	private List<ProductBean> mProducts;
	private ArrayList<Pair<String, Integer>> mOptionsId;
	private ArrayList<SliderSolidBean> mSliders;
	public ProductsListLoader(Context context, int categoryId, int currentPage, ArrayList<Pair<String, Integer>> options_id, ArrayList<SliderSolidBean> sliders) {
		super(context);
		mCategoryId = categoryId;
		mCurrentPage = currentPage;
		mOptionsId = options_id;
		mSliders = sliders;
	}
	
	public boolean isFilters() {
		return ! Utils.isEmptyList(mOptionsId);
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
		
		ArrayList<ProductBean> result = null;
		
		try {
			JSONObject object = new JSONObject();
			JSONArray array_filter = new JSONArray();
			
			if (mOptionsId != null){
				for (Pair<String, Integer>  pair: mOptionsId){
					JSONArray temp = new JSONArray();
					temp.put(pair.getLeft());
					temp.put(pair.getRight());
					array_filter.put(temp);
				}
			}
			if(array_filter.length()!=0){
				object.put("options", array_filter);
			}
			
			JSONArray slider_filter = new JSONArray();
			
			if (mSliders != null){
				for (SliderSolidBean slider  : mSliders){
					JSONArray temp = new JSONArray();
					temp.put(slider.getId());
					temp.put(slider.getCurrentMin());
					temp.put(slider.getCurrentMax());
					slider_filter.put(temp);
				}
			}
			if(slider_filter.length()!=0){
				object.put("sliders", slider_filter);
			}
			if(array_filter.length()!=0||slider_filter.length()!=0)
				EasyTracker.getTracker().sendEvent("filter", "buttonPress", object.toString(), null);
			
			// TODO подумать о загрузке
			String url = URLManager.getProductsInCategory(PreferencesManager.getCityid(), PreferencesManager.getUserCurrentShopId(), mCategoryId, 163, ProductsListFragment.PRODUCTS_ON_PAGE, mCurrentPage);
			String request = Utils.sendPostData(object.toString(), url);
			result = new ItemsListParser().parseTags(request);
			
		} catch (JSONException e) {}	
			
		return result;
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
	
	@Override
	public void onCancel(DialogInterface dialog) {
		super.onCancel(dialog);
		getActivity().getFragmentManager().popBackStack();
	}
	
}
