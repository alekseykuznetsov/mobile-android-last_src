package ru.enter;

import com.flurry.android.FlurryAgent;

import ru.enter.R;
import ru.enter.base.BaseActivity;
import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.webkit.WebView;
import android.webkit.WebViewClient;


public class BannerWebActivity extends BaseActivity{
	
	public static final String NAME = "NAME";
	public static final String URL = "URL";
	
	private WebView mView;
	
	private Context mContext;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mContext = this;
		setContentView(R.layout.banner_web_ac);
		mView = (WebView) findViewById(R.id.banner_web_ac_web_view);
		setTitleCenter(getBannerName());
		mView.setWebViewClient(new BannerWebClientClass()); //custom webclient for progressdialog support, fix 2492
        mView.getSettings().setBuiltInZoomControls(true);        
        mView.loadUrl(getBannerURL());
	}
	
	@Override
	protected void onStart()
	{
		super.onStart();
		FlurryAgent.onStartSession(this, "3X5JRBVFV7QWXNP3Q8H2");
	}
	 
	@Override
	protected void onStop()
	{
		super.onStop();		
		FlurryAgent.onEndSession(this);
	}
	
	public String getBannerName() {
		return getIntent().getStringExtra(NAME);
	}
	
	public String getBannerURL() {
		return getIntent().getStringExtra(URL);
	}
	
	public class BannerWebClientClass extends WebViewClient {
		ProgressDialog pd;

		@Override
		public void onPageStarted(WebView view, String url, Bitmap favicon) {
			super.onPageStarted(view, url, favicon);
			if (pd != null && pd.isShowing())
				return;
			pd = ProgressDialog.show(mContext, "", "Загрузка...", true);
			// if(!isFinishing()) pd.show();
		}
		
		
		@Override
		public boolean shouldOverrideUrlLoading(WebView view, String url) {
			// TODO Auto-generated method stub			
			return true;
		}

		@Override
		public void onPageFinished(WebView view, String url) {
			super.onPageFinished(view, url);
			if (pd.isShowing())
				pd.dismiss();
		}
	}
}
