package ru.enter;

import java.util.HashSet;
import java.util.List;

import com.flurry.android.FlurryAgent;

import ru.enter.adapters.FilterAdapter;
import ru.enter.adapters.FiltersHolder;
import ru.enter.beans.FilterBean;
import ru.enter.beans.OptionsBean;
import ru.enter.parsers.FiltersParser;
import ru.enter.utils.FiltersManager;
import ru.enter.utils.PreferencesManager;
import ru.enter.utils.URLManager;
import android.app.ExpandableListActivity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ExpandableListView;
import android.widget.ProgressBar;
import android.widget.TextView;

public class FiltersActivity extends ExpandableListActivity implements OnClickListener {
	private Button apply, clear, cancel;
	private ExpandableListView list;
	private ProgressBar progress;
	private FilterAdapter adapter;
	private TextView empty;

	private FiltersManager mFiltersManager;

	@Override
	protected void onCreate (Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.filters_list);
		mFiltersManager = FiltersHolder.getFilterManager();
		setupWidgets();
		mFiltersManager.init();
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

	private void setupWidgets () {
		progress = (ProgressBar) findViewById(R.id.filters_list_progress);
		apply = (Button) findViewById(R.id.filters_list_button_apply);
		clear = (Button) findViewById(R.id.filters_list_button_clear);
		cancel = (Button) findViewById(R.id.filters_list_button_cancel);
		empty = (TextView) findViewById(android.R.id.empty);

		apply.setOnClickListener(this);
		clear.setOnClickListener(this);
		cancel.setOnClickListener(this);

		adapter = new FilterAdapter(this);

		int categoryId = getIntent().getIntExtra(CatalogActivity.CATALOG_ID, -1);
		new DownloadFilters().execute(String.valueOf(categoryId));

		setListAdapter(adapter);

		list = getExpandableListView();
		list.setGroupIndicator(null);
	}

	@Override
	public void onClick (View v) {
		switch (v.getId()) {

		case R.id.filters_list_button_apply:

			mFiltersManager.apply();
			setResult(1);
			finish();			
			break;

		case R.id.filters_list_button_clear:

			mFiltersManager.clear();
			adapter.notifyDataSetChanged();
			break;

		case R.id.filters_list_button_cancel:
			setResult(0);
			finish();
			break;
		}
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		setResult(0);
		return super.onKeyDown(keyCode, event);
	}

	private void expandSelectedFilters () {
		for (int i = 0; i < mFiltersManager.getFilters().size(); i++) {
			FilterBean filter = mFiltersManager.getFilters().get(i);
			list.collapseGroup(i);
			if (filter.getType() == FiltersParser.FILTER_OPTIONAL) {
				if (filter.getOptions().size() < 4){
					list.expandGroup(i);
				}
				else{
					HashSet<Integer> newOptions = mFiltersManager.getNewOptions();
					for (int j = 0; j < filter.getOptions().size(); j++) {
						OptionsBean option = filter.getOptions().get(j);
						if (newOptions.contains(option.getId())) {
							j = filter.getOptions().size();
							list.expandGroup(i);
						}
					}
				}
			}
			else if (filter.getType() == FiltersParser.FILTER_SOLID || filter.getType() == FiltersParser.FILTER_DISCRET) {
				list.expandGroup(i);
			}
		}
	}

	private class DownloadFilters extends AsyncTask<String, Void, List<FilterBean>> {

		private boolean isFirst;

		@Override
		protected void onPreExecute () {
			progress.setVisibility(View.VISIBLE);
		}

		protected List<FilterBean> doInBackground (String... id_category) {
			isFirst = mFiltersManager.isEmpty();
			return isFirst ? new FiltersParser(URLManager.getFilters(PreferencesManager
					.getCityid(), id_category[0])).parse() : mFiltersManager.getFilters();
		}

		protected void onPostExecute (List<FilterBean> result) {
			if (result == null) {
				empty.setText("Ошибка загрузки данных. Попробуйте позднее.");
				list.setEmptyView(empty);
			} else {
				if (result.isEmpty()) {
					empty.setText("Для данной категории фильтры отсутствуют.");
					list.setEmptyView(empty);
				} else {
					if (isFirst) mFiltersManager.setFilters(result);
					adapter.setObjects(result);
					adapter.notifyDataSetChanged();
					expandSelectedFilters();
				}
			}
			progress.setVisibility(View.GONE);
		}
	}
}
