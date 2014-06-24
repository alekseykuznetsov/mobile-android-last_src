package ru.enter;

import java.util.ArrayList;

import com.flurry.android.FlurryAgent;

import ru.enter.adapters.ServiceCategoriesAdapter;
import ru.enter.beans.CatalogListBean;
import ru.enter.beans.ServiceCategoryBean;
import ru.enter.parsers.ServiceCategoryParser;
import ru.enter.tabUtils.TabGroupActivity;
import ru.enter.utils.PreferencesManager;
import ru.enter.utils.URLManager;
import ru.enter.utils.Utils;
import ru.enter.widgets.HeaderFrameManager;
import ru.enter.widgets.HeaderFrameManager.HeaderButton;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.Toast;

public class ServiceCategories extends TabGroupActivity implements OnItemClickListener {

	private ServiceCategoriesAdapter mAdapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.service_list);
		initView();
		load();
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

	private void initView() {
		((FrameLayout) findViewById(R.id.service_list_frame))
		.addView(HeaderFrameManager.getHeaderView(this, "Сервисы", true));
		ListView list = (ListView) findViewById(R.id.service_list);
		mAdapter = new ServiceCategoriesAdapter();
		list.setAdapter(mAdapter);
		list.setOnItemClickListener(this);
	}

	private void load() {
		int id = getIntent().getIntExtra("id", -1);
		new ServiceCategoryLoader().execute(id);
	}

	private class ServiceCategoryLoader extends AsyncTask<Integer, Void, ArrayList<ServiceCategoryBean>> {

		@Override
		protected ArrayList<ServiceCategoryBean> doInBackground(Integer... params) {
			String url = URLManager.getServiceCategories(
					PreferencesManager.getCityid(), params[0], "200");//TODO
			return new ServiceCategoryParser(url).parse();
		}

		@Override
		protected void onPostExecute(ArrayList<ServiceCategoryBean> result) {
			mAdapter.setObjects(result);
			if (Utils.isEmptyList(result)) {
				Toast.makeText(ServiceCategories.this, R.string.NoInternet, Toast.LENGTH_LONG).show();
			}
		}
	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
		Intent intent = new Intent();
		ServiceCategoryBean item = (ServiceCategoryBean) arg1.getTag(); 
		if (item.isCategory_list()) {
			intent = new Intent().setClass(ServiceCategories.this, ServiceCategories.class);
		} else {
			intent = new Intent().setClass(ServiceCategories.this, ServiceList.class);
		}
		
		intent.putExtra("id", (int) arg3);
		runNext(intent, arg1.toString());
	}

}
