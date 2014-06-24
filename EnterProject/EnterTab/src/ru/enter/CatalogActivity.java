package ru.enter;

import java.util.HashMap;
import java.util.Map;

import com.flurry.android.FlurryAgent;
import com.google.analytics.tracking.android.EasyTracker;

import ru.enter.base.BaseMenuActivity;
import ru.enter.data.CatalogNode;
import ru.enter.dialogs.FirstStartShopLocationDialogFragment;
import ru.enter.dialogs.ShopLocationDialogFragment;
import ru.enter.fragments.CatalogFragment;
import ru.enter.fragments.FilterFragment;
import ru.enter.fragments.ProductsListFragment;
import ru.enter.utils.Constants;
import ru.enter.utils.OnBarNavigationListener;
import ru.enter.utils.PreferencesManager;
import ru.enter.widgets.CatalogNavigator;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;


public class CatalogActivity extends BaseMenuActivity implements OnBarNavigationListener<CatalogNode>, OnClickListener {

	public static final String CATEGORY_ID = "categoryId";
	public static final String PRODUCTS_COUNT = "productsCountInCategory";
	public static final String CATEGORY_NAME = "categoryName";

	public static final String CATALOG_TAG = "catalogFragment";
	public static final String PRODUCT_TAG = "productsListFragment";
	public static final String FILTER_TAG = "filtersFragment";

	//CatalogActivity -> CatalogFragment -> ProductsListFragment -> FiltersFragment
	private static final int CATALOG_PAGE = 0;
	private static final int FILTERS_PAGE = 2;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		EasyTracker.getInstance().setContext(this);
		setContentView(R.layout.catalog_ac);

		CatalogNavigator navigator = new CatalogNavigator(this);
		navigator.setOnBarNavigationListener(this);
		getActionBar().setCustomView(navigator);

		LinearLayout sortLinear = (LinearLayout) findViewById(R.id.shop_locator_widget_sorts);
		sortLinear.setVisibility(View.GONE);

		RelativeLayout heafer = (RelativeLayout) findViewById(R.id.catalog_ac_relative_header);

		TextView shop_name = (TextView) findViewById(R.id.catalog_ac_txt_shop);
		ImageButton shop_btn = (ImageButton) findViewById(R.id.catalog_ac_btn_location);

		if(PreferencesManager.getHasShop())
			heafer.setVisibility(View.VISIBLE);
		else
			heafer.setVisibility(View.GONE);

		shop_btn.setOnClickListener(this);

		startCatalogFragment();
		EasyTracker.getTracker().sendEvent("category/get", "buttonPress", "", (long)0);

		if(PreferencesManager.getUserFirstStartCatalog()==0&&PreferencesManager.getHasShop()){
			FirstStartShopLocationDialogFragment fragment = FirstStartShopLocationDialogFragment.getInstance();
			fragment.show(getFragmentManager(), "firstShopLocation");
		}
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

	public void startCatalogFragment () {
		FragmentTransaction transaction = getFragmentManager().beginTransaction();
		transaction.replace(R.id.catalog_ac_main_frame, CatalogFragment.getInstance(), CATALOG_TAG);
		transaction.commit();
	}

	public void changeCategory (CatalogNode root) {
		Map<String, String> flurryParam = new HashMap<String, String>();
		flurryParam.put(Constants.FLURRY_EVENT_PARAM.Category.toString(), root.getNode().getName());
		FlurryAgent.logEvent(Constants.FLURRY_EVENT.Catalog_Section.toString(), flurryParam);

		getFragmentManager().popBackStack(PRODUCT_TAG, FragmentManager.POP_BACK_STACK_INCLUSIVE);
		CatalogFragment catalog = (CatalogFragment) getFragmentManager().findFragmentByTag(CATALOG_TAG);
		catalog.changeCategory (root);
	}

	public void startProductsListFragment (int categoryId, int productsCount, String categoryName) {
		FragmentTransaction transaction = getFragmentManager().beginTransaction();
		ProductsListFragment fragment = ProductsListFragment.getInstance();
		Bundle extras = new Bundle();
		extras.putInt(CATEGORY_ID, categoryId);
		extras.putInt(PRODUCTS_COUNT, productsCount);
		extras.putString(CATEGORY_NAME, categoryName);
		fragment.setArguments(extras);
		transaction.replace(R.id.catalog_ac_main_frame, fragment, PRODUCT_TAG);
		transaction.addToBackStack(PRODUCT_TAG);
		transaction.commit();
	}

	public void starFilters (int categoryId) {
		FilterFragment filterFragment = (FilterFragment) getFragmentManager().findFragmentByTag(FILTER_TAG);
		if (filterFragment == null){
			FragmentTransaction transaction = getFragmentManager().beginTransaction();
			FilterFragment fragment = FilterFragment.getInstance();
			Bundle extras = new Bundle();
			extras.putInt(CATEGORY_ID, categoryId);
			fragment.setArguments(extras);
			transaction.add(R.id.catalog_ac_main_frame, fragment, FILTER_TAG);
			transaction.addToBackStack(FILTER_TAG);
			transaction.commit();
		} else {
			if (filterFragment.isHidden()){
				getFragmentManager().beginTransaction().show(filterFragment).commit();
			} else {
				getFragmentManager().beginTransaction().hide(filterFragment).commit();
			}
		}
	}

	@Override
	public void onBackPressed() {
		int currentPage = getFragmentManager().getBackStackEntryCount();
		switch (currentPage){

		case CATALOG_PAGE:
			CatalogFragment catalogFragment = (CatalogFragment) getFragmentManager().findFragmentByTag(CATALOG_TAG);
			catalogFragment.onBackPressed();
			break;

		case FILTERS_PAGE:
			FilterFragment filterFragment = (FilterFragment) getFragmentManager().findFragmentByTag(FILTER_TAG);

			if (filterFragment == null || filterFragment.isHidden()) {
				getFragmentManager().popBackStack(PRODUCT_TAG, FragmentManager.POP_BACK_STACK_INCLUSIVE);
			} else {
				getFragmentManager().beginTransaction().hide(filterFragment).commit();
			}
			break;

		default:
			super.onBackPressed();
			break;
		}
	}

	@Override
	public void onNavigationBarItemSelected(int pos, CatalogNode item, View v) {
		changeCategory(item);
	}

	@Override
	public void onClick (View v) {
		switch (v.getId()){
		case R.id.catalog_ac_btn_location:
			ShopLocationDialogFragment fragment = ShopLocationDialogFragment.getInstance();
			fragment.show(getFragmentManager(), "shopLocation");
			break;
		}

	}

}
