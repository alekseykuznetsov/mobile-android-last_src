package ru.enter;

import com.flurry.android.FlurryAgent;
import com.google.analytics.tracking.android.EasyTracker;

import ru.enter.base.BaseMenuActivity;
import ru.enter.utils.Constants;
import ru.enter.utils.TypefaceUtils;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

public class SearchActivity extends BaseMenuActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		EasyTracker.getInstance().setContext(this);
		setContentView(R.layout.search_ac);
	}
	
	@Override
	protected void onStart(){
		super.onStart();
		FlurryAgent.onStartSession(this, "3X5JRBVFV7QWXNP3Q8H2");
	}
	 
	@Override
	protected void onStop(){
		super.onStop();		
		FlurryAgent.onEndSession(this);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.actionbar_menupart_qr, menu);
		// handler for qr item
		TextView actionView = (TextView) menu.findItem(R.id.menu_qr_local).getActionView();
		actionView.setTypeface(TypefaceUtils.getBrashTypeface());
		actionView.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
//				FlurryAgent.logEvent(Constants.FLURRY_EVENT.Qr_Code_Search.toString());
				
				Intent intent = new Intent(SearchActivity.this, ScanerActivity.class);
				startActivity(intent);
			}
		});
		
		return super.onCreateOptionsMenu(menu);
	}

}
