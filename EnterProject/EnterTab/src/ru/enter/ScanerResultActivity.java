package ru.enter;

import com.flurry.android.FlurryAgent;
import com.google.analytics.tracking.android.EasyTracker;

import ru.enter.base.BaseActivity;
import android.os.Bundle;

public class ScanerResultActivity extends BaseActivity {
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		EasyTracker.getInstance().setContext(this);
		setContentView(R.layout.scaner_result_ac);		
		setTitleCenter(getResources().getString(R.string.actionbar_search_results));
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
	
	public String getHashExtraString () {
		return getIntent().getStringExtra(ScanerActivity.QR_HASH);
	}
}
