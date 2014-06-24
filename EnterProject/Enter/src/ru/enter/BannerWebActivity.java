package ru.enter;

import com.flurry.android.FlurryAgent;

import ru.enter.widgets.HeaderFrameManager;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.FrameLayout;

public class BannerWebActivity extends Activity {
	
	public static final String URL = "URL";//список id сли их куча в связке
	public static final String NAME = "NAME";
	
	private WebView mView;
	
	private Context mContext;
	private String mName,mUrl;
    	
	@Override
    public void onCreate(Bundle savedInstanceState) 
    {
        super.onCreate(savedInstanceState);
        
        mContext = this;
        setContentView(R.layout.banner_web_activity);
        Bundle bundle = getIntent().getExtras();
		mName = bundle.getString(NAME);
		mUrl = bundle.getString(URL);
        init();
              
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
	
	private void init()
    {    
		mView = (WebView) findViewById(R.id.banner_web_activity_web_view);
    	FrameLayout frame = (FrameLayout) findViewById(R.id.banner_web_activity_frame);
        frame.addView(HeaderFrameManager.getHeaderView(BannerWebActivity.this, mName, false));
        mView.setWebViewClient(new BannerWebClientClass()); //custom webclient for progressdialog support, fix 2492
        mView.getSettings().setBuiltInZoomControls(true);        
        mView.loadUrl(mUrl);
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
