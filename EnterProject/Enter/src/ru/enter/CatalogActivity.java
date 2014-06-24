package ru.enter;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import com.flurry.android.FlurryAgent;
import com.google.analytics.tracking.android.EasyTracker;

import ru.enter.adapters.CatalogGridAdapter;
import ru.enter.beans.CatalogListBean;
import ru.enter.beans.ShopBean;
import ru.enter.parsers.CatalogListParser;
import ru.enter.tabUtils.TabGroupActivity;
import ru.enter.utils.Constants;
import ru.enter.utils.PreferencesManager;
import ru.enter.utils.ShopLocator;
import ru.enter.utils.ShopLocator.OnNearestShopLocateListener;
import ru.enter.utils.URLManager;
import ru.enter.utils.Utils;
import ru.enter.widgets.NewHeaderFrameManager;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.ImageButton;
//import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

public class CatalogActivity extends TabGroupActivity implements OnItemClickListener, OnNearestShopLocateListener{
	
	public final static String CATALOG_ID = "CATALOG_ID";
	public final static String CATALOG_NAME = "CATALOG_NAME";
	public static final String SAVED_OBJECTS = "SAVED_OBJECTS";
	
	private FrameLayout mProgress;
	private ShopLocator mLocator;
	private ImageButton locator;
	private TextView shop_name;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		EasyTracker.getInstance().setContext(this); 
	    setContentView(R.layout.catalog);
	    
		FrameLayout frame = (FrameLayout) findViewById(R.id.catalog_linear_layout);
		//listner for shop locator button
		OnClickListener m_listner = new OnClickListener(){
			@Override
			public void onClick (View v) {
				locator.setClickable(false);
				if(!mLocator.isStart()&&PreferencesManager.getHasShop()) {
					mLocator.start();
				}
			}
		};
		//frame.addView(HeaderFrameManager.getHeaderView(CatalogActivity.this, "Каталог", false, HeaderButton.search), 0);
		//frame.addView(HeaderFrameManager.getHeaderView(CatalogActivity.this, "Каталог", false), 0);
		frame.addView(NewHeaderFrameManager.getHeaderViewShopLocation(CatalogActivity.this, "Общий каталог",m_listner), 0);
		
		mProgress = (FrameLayout) findViewById(R.id.catalog_frame_progress);
		
		mLocator = new ShopLocator (CatalogActivity.this);
		mLocator.setOnNearestShopLocateListener(CatalogActivity.this);
		
		//ImageButton 
		//locator = (ImageButton) findViewById(R.id.catalog_header_shoplocator_btn);		
		locator = NewHeaderFrameManager.getShopBtn();
		shop_name = NewHeaderFrameManager.getTitle();
		if(PreferencesManager.getHasShop())
			locator.setVisibility(View.VISIBLE);
		else
			locator.setVisibility(View.INVISIBLE);

		Bundle extras = getIntent().getExtras();
		if (extras != null && extras.containsKey(SAVED_OBJECTS)){
			ArrayList<CatalogListBean> beans = (ArrayList<CatalogListBean>) extras.getSerializable(SAVED_OBJECTS);
			initGrid(beans);
			if(PreferencesManager.getUserFirstStartCatalog()==0&&PreferencesManager.getHasShop()){
				mLocator.startBackground();
				locator.setClickable(false);
			}
		} else {
			load();
		}
		EasyTracker.getTracker().sendEvent("category/get", "buttonPress", "", (long) 0);
			
	}
	
	
	@Override
	protected void onResume () {
		super.onResume();
		reloadText();
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
	
	private void reloadText () {
		/*
		LinearLayout linear = (LinearLayout) findViewById(R.id.catalog_header_shoplocator);
		linear.setVisibility(View.VISIBLE);
		
		
		TextView shop_name = (TextView) findViewById(R.id.catalog_header_shoplocator_title);
		if (PreferencesManager.getUserCurrentShopId() == 0){
			shop_name.setText("Выберите магазин");
		}
		else{
			shop_name.setText(PreferencesManager.getUserCurrentShopName());
		}
		shop_name.setGravity(Gravity.LEFT);
		
		ImageButton shop_btn = (ImageButton) findViewById(R.id.catalog_header_shoplocator_btn);
		shop_btn.setVisibility(View.VISIBLE);
		*/
		
		if(PreferencesManager.getHasShop())
			locator.setVisibility(View.VISIBLE);
		else
			locator.setVisibility(View.INVISIBLE);
		
		if (PreferencesManager.getUserCurrentShopId() == 0){
			shop_name.setText("Общий каталог");
		}
		else{
			shop_name.setText(PreferencesManager.getUserCurrentShopName());
		}
		
		
	}
	
	
	private void initGrid (ArrayList<CatalogListBean> array) {
		GridView gridview = (GridView) findViewById(R.id.catalog_grid);
		CatalogGridAdapter adapter = new CatalogGridAdapter(CatalogActivity.this, array);
		gridview.setAdapter(adapter);
		gridview.setOnItemClickListener(CatalogActivity.this);
	}

	private void load () {
		new CatalogLoader().execute();
	}
	
	private class CatalogLoader extends AsyncTask<Void, Void, ArrayList<CatalogListBean>> {

		@Override
		protected void onPreExecute () {
			 mProgress.setVisibility(View.VISIBLE);
		}
		
		@Override
		protected ArrayList<CatalogListBean> doInBackground (Void... params) {
			String url = URLManager.getCategoriesList(PreferencesManager.getCityid(), 0, getIconDimensions());
			return new CatalogListParser(url).parse();
		}
		
		@Override
		protected void onPostExecute (ArrayList<CatalogListBean> result) {
			if (Utils.isEmptyList(result)) {
				Toast.makeText(CatalogActivity.this, R.string.NoInternet, Toast.LENGTH_LONG).show();
				mProgress.setVisibility(View.GONE);	
			} else {
				initGrid(result);
				getIntent().putExtra(SAVED_OBJECTS, result);
				if(PreferencesManager.getUserFirstStartCatalog()==0&&PreferencesManager.getHasShop()){
					mLocator.startBackground();
					locator.setClickable(false);
				} else {
					mProgress.setVisibility(View.GONE);	
				}
			}
					
		}
	}
	
	public String getIconDimensions () {//TODO
		DisplayMetrics metrics = CatalogActivity.this.getResources().getDisplayMetrics();
		switch (metrics.densityDpi) {
		case DisplayMetrics.DENSITY_LOW:
			return "75x71";
		case DisplayMetrics.DENSITY_MEDIUM:
			return "100x95";
		case DisplayMetrics.DENSITY_HIGH:
			return "200x190";

		default:
			return "150x143";
		}
	}

	@Override
	public void onItemClick (AdapterView<?> grid, View arg1, int position, long arg3) {
		CatalogListBean item = (CatalogListBean) grid.getAdapter().getItem(position);
		
		Map<String, String> flurryParam = new HashMap<String, String>();
		flurryParam.put(Constants.FLURRY_EVENT_PARAM.Category.toString(), item.getName());
		FlurryAgent.logEvent(Constants.FLURRY_EVENT.Catalog_Section.toString(), flurryParam);
		
		Intent intent = new Intent().setClass(CatalogActivity.this, item.isIs_category_list() ? PodCatalogActivity.class : ProductListActivity.class);
		intent.putExtra(CATALOG_ID, item.getId());
		intent.putExtra(CATALOG_NAME, item.getName());
		runNext(intent, item.getLink());
	}

	@Override
	public void onStartLocate () {
		mProgress.setVisibility(View.VISIBLE);
	}

	@Override
	public void onFailLocate () {
		mProgress.setVisibility(View.GONE);
		if(!this.isFinishing())
			showFailLocateDialog();
	}

	@Override
	public void onShopLocated (ShopBean shop) {
		mProgress.setVisibility(View.GONE);
		if(!this.isFinishing())
			showLocateDialog(shop.getName(), shop.getId());
		
	}
	@Override
	public void onBackgroundLocated(ShopBean shop) {
		mProgress.setVisibility(View.GONE);
		if(!this.isFinishing()){
			showBackgroundLocateDialog(shop.getName(), shop.getId());
			PreferencesManager.setFirstStartCatalog(1);
		} else 
			PreferencesManager.setFirstStartCatalog(0);
	}
	@Override
	public void onBackgroundFailLocate() {
		mProgress.setVisibility(View.GONE);
		locator.setClickable(true);
		if(!this.isFinishing())			
			PreferencesManager.setFirstStartCatalog(1);
		else 
			PreferencesManager.setFirstStartCatalog(0);
	}
	
	private void showLocateDialog (final String shopName, final int shopId) {
		AlertDialog.Builder builder = new AlertDialog.Builder(this.getParent());
		builder.setMessage(String.format("Ваше местоположение определено в магазине %s. Хотите ли Вы просмотреть каталог товаров находящихся в данном магазине?", shopName));
		builder.setPositiveButton("Да", new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick (DialogInterface dialog, int which) {				
				PreferencesManager.setUserCurrentShopId(shopId);
				PreferencesManager.setUserCurrentShopName(shopName);
				EasyTracker.getTracker().sendEvent("filter/shop", "buttonPress", shopName, (long) shopId);
				reloadText();
			}
		});
		builder.setNeutralButton("Выбрать из списка", new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick (DialogInterface dialog, int which) {
				Intent intent = new Intent(CatalogActivity.this, ShopLocatorListActivity.class);
				intent.putExtra(ShopLocatorListActivity.SHOPS_OBJECTS, (Serializable) mLocator.getLoadedShops());
				startActivity(intent);
			}
		});
		builder.setNegativeButton("Смотреть общий", new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick (DialogInterface dialog, int which) {
				PreferencesManager.setUserCurrentShopId(0);
				PreferencesManager.setUserCurrentShopName("");
				reloadText();
			}
		});
		
		locator.setClickable(true);

		builder.create().show();
	}
	
	private void showFailLocateDialog () {		
		AlertDialog.Builder builderFail = new AlertDialog.Builder(this.getParent());
		builderFail.setMessage("В данный момент Вы не находитесь ни в одном из магазинов Enter, либо мы не смогли определить Ваше местоположение.");
		builderFail.setPositiveButton("Еще раз", new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick (DialogInterface dialog, int which) {
				mLocator.start();				
			}
		});
		builderFail.setNeutralButton("Выбрать из списка", new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick (DialogInterface dialog, int which) {
				Intent intent = new Intent(CatalogActivity.this, ShopLocatorListActivity.class);
				intent.putExtra(ShopLocatorListActivity.SHOPS_OBJECTS, (Serializable) mLocator.getLoadedShops());
				startActivity(intent);
			}
		});
		builderFail.setNegativeButton("Смотреть общий", new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick (DialogInterface dialog, int which) {
				PreferencesManager.setUserCurrentShopId(0);
				PreferencesManager.setUserCurrentShopName("");
				reloadText();
			}
		});
		locator.setClickable(true);
		builderFail.create().show();
	}	
	
	private void showBackgroundLocateDialog (final String shopName, final int shopId) {
		AlertDialog.Builder builder = new AlertDialog.Builder(this.getParent());
		builder.setMessage(String.format("Ваше местонахождение определено в магазине %s. Хотите ли Вы просмотреть каталог товаров, находящихся в данном магазине?", shopName));
		builder.setPositiveButton("Да", new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick (DialogInterface dialog, int which) {				
				PreferencesManager.setUserCurrentShopId(shopId);
				PreferencesManager.setUserCurrentShopName(shopName);
				EasyTracker.getTracker().sendEvent("filter/shop", "buttonPress", shopName, (long) shopId);
				reloadText();
			}
		});		
		builder.setNegativeButton("Смотреть общий", new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick (DialogInterface dialog, int which) {
				PreferencesManager.setUserCurrentShopId(0);
				PreferencesManager.setUserCurrentShopName("");
				reloadText();
			}
		});
		
		locator.setClickable(true);

		builder.create().show();
	}
}
