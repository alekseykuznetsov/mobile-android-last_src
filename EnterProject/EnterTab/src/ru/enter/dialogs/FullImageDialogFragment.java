package ru.enter.dialogs;

import java.io.IOException;
import java.util.ArrayList;

import org.json.JSONException;

import ru.enter.R;
import ru.enter.ShopsActivity;
import ru.enter.adapters.ShopImageGallaryAdapter;
import ru.enter.beans.ShopImgBean;
import ru.enter.parsers.ShopImgParser;
import android.app.DialogFragment;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class FullImageDialogFragment extends DialogFragment {

	public static final String URL = "foto_url";
	private ShopImageGallaryAdapter galleryAdapter;
	private String url;
	
	public static FullImageDialogFragment getInstance(String url) {
		Bundle bundle = new Bundle();
		bundle.putString(URL, url);
		FullImageDialogFragment fragment = new FullImageDialogFragment();
		fragment.setArguments(bundle);
		return fragment;
	}
		
	@Override
	public void onCreate(Bundle bundle) {
		super.onCreate(bundle);
		setStyle(0, R.style.custom_dialog);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

		url = getArguments().getString(URL);
		if (url == null) url = "";
		
		getDialog().setTitle("Фотографии магазина");

		ShopsActivity mActivity = (ShopsActivity) getActivity();

		
//		ImageLoader loader = ApplicationEnter.getLoader(mActiviy);
//		ImageHelper imageLoader = new ImageHelper(mActiviy, loader).setFadeIn(true)
//				.setLoadingResource(R.drawable.shop_top_img)
//				.setErrorResource(R.drawable.shop_top_img);
//		imageLoader.load(image, url);
//		
		View root = inflater.inflate(R.layout.shop_dialog_fullimage_gallery, null);
		ViewPager gallery = (ViewPager) root.findViewById(R.id.itemShopGallery);
		galleryAdapter = new ShopImageGallaryAdapter(mActivity);
		gallery.setAdapter(galleryAdapter);
		new ImagesLoader().execute();
		return root;
	}
	
	class ImagesLoader extends AsyncTask<Void, Void, ArrayList<ShopImgBean>> {

		//private ProgressDialogFragment progress;
		
		@Override
		protected void onPreExecute() {
			//progress = ProgressDialogFragment.getInstance();
			//progress.show(getFragmentManager(), "progress");
		}

		@Override
		protected ArrayList<ShopImgBean> doInBackground(Void... params) {
			try {
				Log.d("URL",url);
				return (ArrayList<ShopImgBean>) new ShopImgParser(url).parse();
			} catch (IOException e) {
				e.printStackTrace();
			} catch (JSONException e) {
				e.printStackTrace();
			}
			
			return null;

		}
		
		@Override
		protected void onPostExecute(ArrayList<ShopImgBean> result) {
			//progress.dismiss();
			if(result != null && result.size() > 0){
				galleryAdapter.addImages(result);
			}
			else{
				// hide pager & show no-data textview, bug 2608
				((ViewPager)getView().findViewById(R.id.itemShopGallery)).setVisibility(View.GONE);
				((View)getView().findViewById(R.id.empty)).setVisibility(View.VISIBLE);
			}
		}
	}
	
	
	
	/*public static final String URL = "foto_url";
	
	public static FullImageDialogFragment getInstance(String url) {
		Bundle bundle = new Bundle();
		bundle.putString(URL, url);
		FullImageDialogFragment fragment = new FullImageDialogFragment();
		fragment.setArguments(bundle);
		return fragment;
	}
		
	@Override
	public void onCreate(Bundle bundle) {
		super.onCreate(bundle);
		setStyle(0, R.style.custom_dialog);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

		String url = getArguments().getString(URL);
		if (url == null) url = "";
		
		getDialog().setTitle("Фотография магазина");

		ShopsActivity mActiviy = (ShopsActivity) getActivity();

		
		ImageLoader loader = ApplicationEnter.getLoader(mActiviy);
		ImageHelper imageLoader = new ImageHelper(mActiviy, loader).setFadeIn(true)
				.setLoadingResource(R.drawable.shop_top_img)
				.setErrorResource(R.drawable.shop_top_img);

		ImageView image = (ImageView) inflater.inflate(R.layout.shop_dialog_fullimage, null);
		imageLoader.load(image, url);

		return image;
	}*/
}

