package ru.enter;

import com.flurry.android.FlurryAgent;

import ru.enter.widgets.HeaderFrameManager;
import android.app.Activity;
import android.os.Bundle;
import android.webkit.WebView;
import android.widget.FrameLayout;

public class TermsActivity extends Activity{
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.terms_layout);
		FrameLayout frame = (FrameLayout) findViewById(R.id.frameLayout1);
		frame.addView(HeaderFrameManager.getHeaderView(this, "Условия продажи", false));
		WebView web = (WebView) findViewById(R.id.web_view);
		web.loadUrl("file:///android_asset/terms.html");
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

}
