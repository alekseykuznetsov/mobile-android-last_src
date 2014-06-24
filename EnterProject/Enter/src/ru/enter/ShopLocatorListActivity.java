package ru.enter;

import java.util.ArrayList;
import java.util.List;

import com.flurry.android.FlurryAgent;

import ru.enter.adapters.ShopLocatorListAdapter;
import ru.enter.beans.ShopBean;
import ru.enter.utils.PreferencesManager;
import android.app.ListActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;

public class ShopLocatorListActivity extends ListActivity{

	public static final String SHOPS_OBJECTS = "shops";

	@Override
	protected void onCreate (Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.shoplocator_list_ac);
		
		Bundle extras = getIntent().getExtras();
		List<ShopBean> shops = new ArrayList<ShopBean>();
		if (extras != null && extras.containsKey(SHOPS_OBJECTS)) {
			shops = (List<ShopBean>) extras.getSerializable(SHOPS_OBJECTS);
		}
		
		ShopLocatorListAdapter adapter = new ShopLocatorListAdapter(this);
		adapter.setObjects(new ArrayList<ShopBean>(shops));
		setListAdapter(adapter);
		
		getListView().setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick (AdapterView<?> arg0, View arg1, int arg2, long arg3) {
				ShopBean bean = (ShopBean) arg0.getAdapter().getItem(arg2);
				PreferencesManager.setUserCurrentShopId(bean.getId());
				PreferencesManager.setUserCurrentShopName(bean.getName());
				finish();
			}
			
		});
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
