package ru.enter.fragments;

import ru.enter.R;
import android.app.Fragment;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.FrameLayout;

public class AboutWebViewFragment extends Fragment{
	
	public static final String URL = "about_url";
	private WebView mWebView;
	private FrameLayout mProgress;
	
	private void showProgress(){
		if (mProgress != null)
			mProgress.setVisibility(View.VISIBLE);
	}
	
	private void hideProgress(){
		if (mProgress != null)
			mProgress.setVisibility(View.GONE);
	}
	
	
	public static AboutWebViewFragment getInstance (String url){
		Bundle bundle = new Bundle();
		bundle.putString(URL, url);
		AboutWebViewFragment fragment = new  AboutWebViewFragment();
		fragment.setArguments(bundle);
		return fragment;
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = (View) inflater.inflate(R.layout.about_web_view_fr, null);
		mWebView = (WebView) view.findViewById(R.id.about_web_view_fr_web);
		mProgress = (FrameLayout) view.findViewById(R.id.about_web_view_fr_progress_frame);
		return view;
	}
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		String url = getArguments().getString(URL);
		if (url == null) url = "";
		mWebView.loadUrl(url);
		mWebView.setWebViewClient(new WebClientClass());
	}
	
	public class WebClientClass extends WebViewClient {

		@Override
		public void onPageStarted(WebView view, String url, Bitmap favicon) {
			super.onPageStarted(view, url, favicon);
			showProgress();
		}

		@Override
		public boolean shouldOverrideUrlLoading(WebView view, String url) {
			// TODO Auto-generated method stub
			Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
			startActivity(intent);
			return true;
		}

		@Override
		public void onPageFinished(WebView view, String url) {
			super.onPageFinished(view, url);
			hideProgress();
		}
	}
}
