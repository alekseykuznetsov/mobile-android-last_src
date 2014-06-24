package ru.enter.fragments;

import org.json.JSONObject;

import com.google.analytics.tracking.android.EasyTracker;

import ru.enter.ProductCardActivity;
import ru.enter.R;
import ru.enter.ScanerActivity;
import ru.enter.beans.ProductBean;
import ru.enter.dialogs.alert.ScanerBarcodeErrorDialogFragment;
import ru.enter.loaders.ScanerBarcodeLoader;
import ru.enter.utils.Constants;
import ru.enter.utils.HTTPUtils;
import ru.enter.utils.PreferencesManager;
import ru.enter.utils.URLManager;
import android.app.Fragment;
import android.app.LoaderManager.LoaderCallbacks;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

public class ScanerBarcodeFragment extends Fragment implements LoaderCallbacks<ProductBean> {

	private static final int LOADER_ID = 301;
	private static final String IMAGE_SIZE = "163";

	private String mUrlString;
	private String mBarcode;
	private SendMessage mSendMessageLoader;
	private FrameLayout mFrameProgress;
	
	private ScanerBarcodeErrorDialogFragment mDialog;
	public static ScanerBarcodeFragment getInstance () {
		return new ScanerBarcodeFragment();
	}

	private void showProgressFrame() {
		mFrameProgress.setVisibility(View.VISIBLE);
	}
	
	private void hideProgressFrame() {
		mFrameProgress.setVisibility(View.GONE);
	}
	
	@Override
	public void onCreate (Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		int geo = PreferencesManager.getCityid();
		mBarcode = getArguments().getString(ScanerActivity.BARCODE);
		mUrlString = URLManager.getSearchByBarCode(mBarcode, geo, IMAGE_SIZE);
	}
	
	@Override
	public View onCreateView (LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

		View view = (View) inflater.inflate(R.layout.scaner_barcode_fr, null);
		mFrameProgress = (FrameLayout) view.findViewById(R.id.scaner_barcode_fr_progress_frame);
		return view;
		
	}

	@Override
	public void onActivityCreated (Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		getLoaderManager().initLoader(LOADER_ID, null, this);
	}
	
	@Override
	public void onPause () {
		super.onPause();
		if (mSendMessageLoader != null)
			mSendMessageLoader.cancel(true);
	}
	private void showNotificationDialog () {
		mDialog = ScanerBarcodeErrorDialogFragment.getInstance();
		mDialog.setCancelable(false);
		mDialog.setonClickListener(new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				switch (which) {
				case DialogInterface.BUTTON_POSITIVE:
					getActivity().finish();
					break;
	
				default:
					break;
				}
			}
		});
		
		mDialog.show(getFragmentManager(), "barcode_error");//TODO
	}

	// -----------------------------------------LOADER--------------------------------------------- //

	@Override
	public Loader<ProductBean> onCreateLoader (int id, Bundle args) {
		showProgressFrame();
		return new ScanerBarcodeLoader(getActivity(), mUrlString);
	}

	@Override
	public void onLoadFinished (Loader<ProductBean> arg0, ProductBean bean) {
		if (bean != null && bean.getId() != 0) {
			Intent intent = new Intent(getActivity(), ProductCardActivity.class);
			intent.putExtra("barcode_bean", bean);
			EasyTracker.getTracker().sendEvent("product/get", "buttonPress", bean.getName(), (long) bean.getId());
			intent.putExtra(ProductCardActivity.EXTRA_FROM, Constants.FLURRY_FROM.Search.toString());
//			intent.putExtra(ProductCardActivity.PRODUCT_ID, bean.getId());
			startActivity(intent);
		} else {
			if (mSendMessageLoader != null) mSendMessageLoader.cancel(true);
			mSendMessageLoader = new SendMessage();
			mSendMessageLoader.execute();
		}
	}

	@Override
	public void onLoaderReset (Loader<ProductBean> arg0) {
		// TODO Auto-generated method stub

	}
	
	
	// -----------------------------------------Send Message--------------------------------------------- //

	class SendMessage extends AsyncTask<Void, Void, String> {

		@Override
		protected String doInBackground(Void... params) {

			String result = null;
			
				try {
					JSONObject object = new JSONObject();	
					object.put("theme", "Добавить товар");
					String body = String.format("баркод - %s, город %s, платформа - Android-tablet", mBarcode, PreferencesManager.getCityName());
					object.put("text", body);
					
					if (object != null) {
						result = HTTPUtils.sendPostJSON(URLManager.getFeedBack(), object);
					}
				} catch (Exception e) {
					
				}
			return result;
		}

		@Override
		protected void onPostExecute(String result) {
			if(isCancelled())
				return;
			hideProgressFrame();
			showNotificationDialog();
		}
	}
}
