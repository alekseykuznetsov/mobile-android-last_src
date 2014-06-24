package ru.enter;

import java.util.ArrayList;
import java.util.List;

import com.flurry.android.FlurryAgent;
import com.google.analytics.tracking.android.EasyTracker;

import ru.enter.DataManagement.BasketManager;
import ru.enter.DataManagement.BasketManager.CountPrice;
import ru.enter.adapters.BannerAdapter;
import ru.enter.beans.BannerBean;
import ru.enter.beans.CitiesBean;
import ru.enter.dialogs.CitiesDialogFragment;
import ru.enter.listeners.OnCitySelectListener;
import ru.enter.interfaces.OnBasketChangeListener;
import ru.enter.loaders.BannersLoader;
import ru.enter.utils.Constants;
import ru.enter.utils.Formatters;
import ru.enter.utils.PreferencesManager;
import ru.enter.widgets.IndicatorViewPager;
import android.app.Activity;
import android.app.LoaderManager.LoaderCallbacks;
import android.content.Intent;
import android.content.Loader;
import android.os.Bundle;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

public class MainActivity extends Activity implements OnClickListener, OnBasketChangeListener, LoaderCallbacks<List<BannerBean>> {
	
	private static final int BANNER_LOADER_ID = 3;
	
	private BannerAdapter mAdapter;
	private IndicatorViewPager mPager;

	private Button mBasketButton;
	private TextView mBasketCountText;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		EasyTracker.getInstance().setContext(this);
		setContentView(R.layout.main_ac);		
//		Thread.setDefaultUncaughtExceptionHandler(new ExceptionCaughter("EnterErrors", null, null, this));//TODO

		Button catalog = (Button) findViewById(R.id.main_ac_button_catalog);
		Button personal = (Button) findViewById(R.id.main_ac_button_personal);
		Button search = (Button) findViewById(R.id.main_ac_button_search);
		Button shops = (Button) findViewById(R.id.main_ac_button_shops);
		//Button services = (Button) findViewById(R.id.main_ac_button_services);
		Button scaner = (Button) findViewById(R.id.main_ac_button_scaner);
		Button about = (Button) findViewById(R.id.main_ac_button_about);
		Button feedback = (Button) findViewById(R.id.main_ac_button_feedback);
		
		mBasketButton = (Button) findViewById(R.id.main_ac_button_basket);
		mBasketCountText = (TextView) findViewById(R.id.main_ac_txt_basket_count);

		catalog.setOnClickListener(this);
		personal.setOnClickListener(this);
		search.setOnClickListener(this);
		shops.setOnClickListener(this);
		mBasketButton.setOnClickListener(this);
		//services.setOnClickListener(this);
		scaner.setOnClickListener(this);
		about.setOnClickListener(this);
		feedback.setOnClickListener(this);

		mPager = (IndicatorViewPager) findViewById(R.id.main_ac_pager);
		mAdapter = new BannerAdapter(this);
		mAdapter.setOnClickListener(mBannerClick);
		mPager.setAdapter(mAdapter);

		if (PreferencesManager.getCityid() == -1) {
			if (getFragmentManager().findFragmentByTag("cities") == null) {
			CitiesDialogFragment dialogFragment = CitiesDialogFragment.getInstance();
			dialogFragment.setOnCitySelectListener(new OnCitySelectListener() {
				@Override
				public void onCitySelect(CitiesBean city) {
					EasyTracker.getTracker().sendEvent("session/initialized", "buttonPress",PreferencesManager.getCityName() , null);
					getLoaderManager().restartLoader(BANNER_LOADER_ID, null, MainActivity.this);
				}
			});
			dialogFragment.show(getFragmentManager(), "cities");
			}
		} else {
			EasyTracker.getTracker().sendEvent("session/initialized", "buttonPress",PreferencesManager.getCityName() , null);
			getLoaderManager().initLoader(BANNER_LOADER_ID, null, this);
		}
		
		refreshBasketView();
		BasketManager.setOnBasketChangeListener(this);
	}
	
	private void refreshBasketView () {
		CountPrice object = BasketManager.getCountPriceObject();
		
		if (object.allCount > 0) {
			mBasketCountText.setText(String.valueOf(object.allCount));
			mBasketCountText.setVisibility(View.VISIBLE);
		} else {
			mBasketCountText.setVisibility(View.GONE);
		}
		
		if (object.allPrice > 0) {
			mBasketButton.setText(Formatters.createPriceStringWithRouble(object.allPrice).concat("."));
		} else {
			mBasketButton.setText("Корзина");
		}
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		mPager.startAnimation();
	}
	
	@Override
	protected void onPause() {
		super.onPause();
		mPager.stopAnimation();
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

	@Override
	public void onClick(View v) {
		Intent intent = new Intent();

		switch (v.getId()) {
		case R.id.main_ac_button_catalog:
			intent.setClass(MainActivity.this, CatalogActivity.class);
			break;
		case R.id.main_ac_button_search:
			intent.setClass(MainActivity.this, SearchActivity.class);
			break;
		case R.id.main_ac_button_scaner:
			intent.setClass(MainActivity.this, ScanerActivity.class);
			break;
		case R.id.main_ac_button_basket:
			intent.setClass(MainActivity.this, BasketActivity.class);
			break;
		case R.id.main_ac_button_personal:
			intent.setClass(MainActivity.this, PersonalActivity.class);
			//авторизован ли пользователь
			if ( ! PreferencesManager.isAuthorized()) {
				AuthorizationActivity.launch(this);
				return;
			}
			break;
		case R.id.main_ac_button_shops:
			intent.setClass(MainActivity.this, ShopsActivity.class);
			break;
//		case R.id.main_ac_button_services:
//			intent.setClass(MainActivity.this, ServicesActivity.class);
//			break;
		case R.id.main_ac_button_about:
			intent.setClass(MainActivity.this, AboutActivity.class);
			intent.putExtra(AboutActivity.SELECTED_TAB, AboutActivity.ABOUT_PROJECT_TAB);
			break;
		case R.id.main_ac_button_feedback:
			intent.setClass(MainActivity.this, AboutActivity.class);
			intent.putExtra(AboutActivity.SELECTED_TAB, AboutActivity.FEEDBACK_TAB);
			break;
		default:
			break;
		}
		
		startActivity(intent);

	}
	
	private OnClickListener mBannerClick = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			BannerBean banner = (BannerBean) v.getTag();
			FlurryAgent.logEvent(Constants.FLURRY_EVENT.Banner_Сlick.toString());
			if (banner != null) {
				EasyTracker.getTracker().sendEvent("promo/click", "buttonPress", banner.getName(), (long) banner.getId());
				if (banner.getProduct_ids() != null) {
					ArrayList<Integer> productsId = banner.getProduct_ids();
					if (productsId.size() == 1) {
						Intent intent = new Intent();
						intent.setClass(MainActivity.this, ProductCardActivity.class);
						intent.putExtra(ProductCardActivity.PRODUCT_ID, banner.getProduct_ids().get(0));
						EasyTracker.getTracker().sendEvent("product/get", "buttonPress",banner.getName(), (long) banner.getProduct_ids().get(0));
						intent.putExtra(ProductCardActivity.EXTRA_FROM, Constants.FLURRY_FROM.Banner.toString());
						startActivity(intent);
					} else if (productsId.size() > 1) {
						Intent intent = new Intent();
						intent.setClass(MainActivity.this,
								BannersActivity.class);
						intent.putExtra(BannersActivity.ID_LIST,
								banner.getProduct_ids());
						startActivity(intent);
					}
				} else	{
					Intent intent = new Intent();
					intent.setClass(MainActivity.this,
							BannerWebActivity.class);
					intent.putExtra(BannerWebActivity.NAME,
							banner.getName());
					intent.putExtra(BannerWebActivity.URL,
							banner.getUrl());
					startActivity(intent);
				}
			}
		}
	};

	@Override
	public Loader<List<BannerBean>> onCreateLoader(int id, Bundle args) {
		return new BannersLoader(this);
	}

	@Override
	public void onLoadFinished(Loader<List<BannerBean>> loader, List<BannerBean> result) {
		mAdapter.setObjects(result);
		mPager.notifyDataSetChanged();
		mPager.startAnimation();
	}

	@Override
	public void onLoaderReset(Loader<List<BannerBean>> loader) {}

	@Override
	public void onBasketChange () {
		refreshBasketView();
	}
	
	@Override
	public void onBackPressed () {
		PreferencesManager.setUserCurrentShopId(0);
		PreferencesManager.setUserCurrentShopName("");
		PreferencesManager.setFirstStartCatalog(0);
		super.onBackPressed();
	}
	
}
