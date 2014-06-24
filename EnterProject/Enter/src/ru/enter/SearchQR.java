package ru.enter;

import java.util.ArrayList;

import com.flurry.android.FlurryAgent;

import ru.enter.adapters.SearchQRAdapter;
import ru.enter.beans.ProductBean;
import ru.enter.parsers.SearchQRParser;
import ru.enter.utils.Constants;
import ru.enter.utils.PreferencesManager;
import ru.enter.utils.URLManager;
import ru.enter.widgets.HeaderFrameManager;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

public class SearchQR extends Activity implements OnItemClickListener{
	
	public static final String QR = "enterqr";
	
	private String mHash;
	private SearchQRAdapter mAdapter;
	private ProgressBar mProgress;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.search_qr);
		mHash = getIntent().getStringExtra(QR);
		setWidgets();
		loadContent();
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

	private void setWidgets() {
        FrameLayout frame = (FrameLayout) findViewById(R.id.search_qr_frame);
        frame.addView(HeaderFrameManager.getHeaderView(SearchQR.this, "Поиск по QR", false));
        ListView list = (ListView) findViewById(R.id.search_qr_list);
        mProgress = (ProgressBar) findViewById(R.id.search_qr_list_progress);
        
        mAdapter = new SearchQRAdapter(this);
        list.setAdapter(mAdapter);
        list.setOnItemClickListener(this);
	}
	
	private void loadContent() {
		Thread thread = new Thread(new Runnable() {
			@Override
			public void run() {
				int geo_id = PreferencesManager.getCityid();
				String url = URLManager.getSearchByQR(mHash, geo_id, "200");//TODO
				SearchQRParser parser = new SearchQRParser(url);
				Message message = new Message();
				message.obj = parser.parse();
				mHandler.sendMessage(message);
			}
		});
		mProgress.setVisibility(View.VISIBLE);
		thread.start();
	}
	
	private Handler mHandler = new Handler(){
		@Override
		public void handleMessage(Message msg) {
			mAdapter.setObjects((ArrayList<ProductBean>) msg.obj);
			mProgress.setVisibility(View.INVISIBLE);
		}
	};

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
		Intent intent = new Intent().setClass(this, ProductCardActivity.class);
		intent.putExtra(ProductCardActivity.ID, (int)arg3);
		TextView mTitle = (TextView)arg1.findViewById(R.id.qr_search_row_title);
		intent.putExtra(ProductCardActivity.NAME, (String) mTitle.getText());
		intent.putExtra(ProductCardActivity.EXTRA_FROM, Constants.FLURRY_FROM.Search.toString());
		startActivity(intent);
	}

}
