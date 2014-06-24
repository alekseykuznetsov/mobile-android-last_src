package ru.enter;

import java.util.ArrayList;

import com.flurry.android.FlurryAgent;
import com.google.analytics.tracking.android.EasyTracker;

import ru.enter.adapters.CatalogListAdapter;
import ru.enter.beans.CatalogListBean;
import ru.enter.parsers.CatalogListParser;
import ru.enter.tabUtils.TabGroupActivity;
import ru.enter.utils.PreferencesManager;
import ru.enter.utils.URLManager;
import ru.enter.utils.Utils;
import ru.enter.widgets.HeaderFrameManager;
import ru.enter.widgets.NewHeaderFrameManager;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AbsListView;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.Toast;

public class PodCatalogActivity extends TabGroupActivity implements OnItemClickListener{
	
	public static final String SAVED_OBJECTS = "SAVED_OBJECTS";
	private CatalogListAdapter mAdapter;

	@Override
	protected void onCreate (Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		EasyTracker.getInstance().setContext(this);
		setContentView(R.layout.pod_catalog);
		
		FrameLayout frame = (FrameLayout) findViewById(R.id.pod_catalog_frame);
		//frame.addView(HeaderFrameManager.getHeaderView(PodCatalogActivity.this, "Каталог", false, HeaderButton.search));
		if (PreferencesManager.getUserCurrentShopId() == 0){
			frame.addView(HeaderFrameManager.getHeaderView(PodCatalogActivity.this, "Каталог", false));
		} else {
			frame.addView(NewHeaderFrameManager.getHeaderView(PodCatalogActivity.this, PreferencesManager.getUserCurrentShopName()));
		}
		
		//frame.addView(HeaderFrameManager.getHeaderView(PodCatalogActivity.this, "Каталог", false));
		
		Bundle extras = getIntent().getExtras();
		if (extras != null && extras.containsKey(SAVED_OBJECTS)){
			ArrayList<CatalogListBean> beans = (ArrayList<CatalogListBean>) extras.getSerializable(SAVED_OBJECTS);
			initList(beans);
		} else {
			load();
		}
		int id = getIntent().getIntExtra(CatalogActivity.CATALOG_ID, 0);
		String name = getIntent().getStringExtra(CatalogActivity.CATALOG_NAME);
		EasyTracker.getTracker().sendEvent("category/get", "buttonPress", name, (long) id);
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
	
	private void initList (ArrayList<CatalogListBean> array) {
		ListView list = (ListView)findViewById(R.id.pod_catalog_listView);
		mAdapter = new CatalogListAdapter(PodCatalogActivity.this, 0, array);
		list.setAdapter(mAdapter);
		list.setOnItemClickListener(PodCatalogActivity.this);
		list.setOnScrollListener(mScrollListener);
	}
	
	@Override
	protected void onResume () {
		super.onResume();
		//reloadText();
	}
	/*
	private void reloadText () {
		LinearLayout linear = (LinearLayout) findViewById(R.id.pod_catalog_header_shoplocator);
		if (PreferencesManager.getUserCurrentShopId() == 0){ 
			linear.setVisibility(View.GONE);
		}
		else{
			linear.setVisibility(View.VISIBLE);
		
			TextView shop_name = (TextView) findViewById(R.id.pod_catalog_header_shoplocator_title);
			shop_name.setText(PreferencesManager.getUserCurrentShopName());
			shop_name.setGravity(Gravity.CENTER);
			
			ImageButton shop_btn = (ImageButton) findViewById(R.id.pod_catalog_header_shoplocator_btn);
			shop_btn.setVisibility(View.GONE);
		}
	}
	*/
	private void load () {
		new PodCatalogLoader().execute();
	}
	
	private class PodCatalogLoader extends AsyncTask<Void, Void, ArrayList<CatalogListBean>> {

		private FrameLayout progress;

		@Override
		protected void onPreExecute () {
			 progress = (FrameLayout)findViewById(R.id.pod_catalog_frame_progress);
			 progress.setVisibility(View.VISIBLE);
		}
		
		@Override
		protected ArrayList<CatalogListBean> doInBackground (Void... params) {
			int id = getIntent().getIntExtra(CatalogActivity.CATALOG_ID, 0);
			String url = URLManager.getCategoriesList(PreferencesManager.getCityid(), id, Utils.getDpiForItemList(PodCatalogActivity.this));
			return new CatalogListParser(url).parse();
		}
		
		@Override
		protected void onPostExecute (ArrayList<CatalogListBean> result) {
			if (Utils.isEmptyList(result)) {
				Toast.makeText(PodCatalogActivity.this, R.string.NoInternet, Toast.LENGTH_LONG).show();
			} else {
				initList(result);
				getIntent().putExtra(SAVED_OBJECTS, result);
			}
			
			progress.setVisibility(View.GONE);
		}
	}

	@Override
	public void onItemClick (AdapterView<?> list, View arg1, int position, long arg3) {
		Intent intent;
		CatalogListBean item = (CatalogListBean) list.getAdapter().getItem(position);
		if (item.isIs_category_list()) {
			intent = new Intent().setClass(PodCatalogActivity.this, PodCatalogActivity.class);
		} else {
			intent = new Intent().setClass(PodCatalogActivity.this, ProductListActivity.class);
			intent.putExtra(ProductListActivity.COUNT, item.getCount());
		}
		
		intent.putExtra(CatalogActivity.CATALOG_ID, item.getId());
		intent.putExtra(CatalogActivity.CATALOG_NAME, item.getName());
		runNext(intent, item.getLink());
	}
	
private OnScrollListener mScrollListener = new OnScrollListener() {
		
		@Override
		public void onScrollStateChanged(AbsListView view, int scrollState) {
			//остановлили скролл - начали грузить фотки
			if (scrollState == OnScrollListener.SCROLL_STATE_IDLE) {
				mAdapter.setLoad(true);
				mAdapter.notifyDataSetChanged();
			} else {
				mAdapter.setLoad(false);
			}
		}
		
		@Override
		public void onScroll(AbsListView view, int firstVisible, int visibleCount, int totalCount) {
			
		}
	};
	
	
}
