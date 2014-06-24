package ru.enter;

import com.flurry.android.FlurryAgent;

import ru.enter.adapters.TempServicesGridAdapter;
import ru.enter.tabUtils.TabGroupActivity;
import ru.enter.widgets.HeaderFrameManager;
import ru.enter.widgets.HeaderFrameManager.HeaderButton;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.FrameLayout;
import android.widget.GridView;

public class ServicesActivity extends TabGroupActivity implements OnItemClickListener {

	protected void onCreate (Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.services_activity);
		initView();
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

	private void initView () {
		FrameLayout frame = (FrameLayout) findViewById(R.id.services_activity_frame);
		frame.addView(HeaderFrameManager.getHeaderView(ServicesActivity.this, "Сервисы", true));

		GridView grid = (GridView) findViewById(R.id.services_activity_grid);
		TempServicesGridAdapter adapter = new TempServicesGridAdapter(this);
		grid.setAdapter(adapter);
		grid.setOnItemClickListener(this);
	}

	@Override
	public void onItemClick (AdapterView<?> parent, View view, int position, long id) {

		Intent intent = new Intent().setClass(this, ServiceCategories.class);
		intent.putExtra("id", (int) id);
		runNext(intent, id + "ServicesActivity");
	}
}
