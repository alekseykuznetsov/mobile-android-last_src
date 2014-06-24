package ru.enter;


import com.flurry.android.FlurryAgent;

import ru.enter.utils.Constants;
import ru.enter.widgets.ButtonSelector;
import ru.enter.widgets.HeaderFrameManager;
import ru.enter.widgets.HeaderFrameManager.HeaderButton;
import android.app.TabActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TabHost;
import android.widget.TabHost.TabContentFactory;

public class ShopMapTabs extends TabActivity implements TabContentFactory,OnClickListener{

	private TabHost tabHost;
	private ButtonSelector bt_sel;
	private boolean buttonMe;
	private boolean mFirstTimeInSession;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.shop_tab);
		
		LinearLayout header = (LinearLayout) findViewById(R.id.shops_tab_header_layout);
		header.addView(HeaderFrameManager.getHeaderView(this, "Магазины", false));
		
		Button list_btn = (Button)findViewById(R.id.shops_tab_button_list);
		Button map_btn = (Button)findViewById(R.id.shops_tab_button_map);
		ImageButton geopoint_btn = (ImageButton)findViewById(R.id.shops_tab_button_geopoint);
		
		bt_sel = new ButtonSelector(list_btn,map_btn);
		bt_sel.selectBtn(0);
		buttonMe=false;
		
		list_btn.setOnClickListener(this); 
		map_btn.setOnClickListener(this);
		geopoint_btn.setOnClickListener(this);
		
		tabHost = getTabHost();
		tabHost.getTabWidget().setVisibility(View.GONE);
		Button button = new Button(this);
		button.setText("1");
		button.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
		
		Button button1 = new Button(this);
		button1.setText("1");
		button1.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
		Intent map = new Intent(this, Maps.class);
		map.putExtra(Maps.FORWARD, true);
		map.putExtra(Maps.STATE, Maps.State.shop.name());
		
		tabHost.addTab(tabHost.newTabSpec("tab1")
                .setIndicator(button)
                .setContent(new Intent(this, ShopsActivity.class)));
        tabHost.addTab(tabHost.newTabSpec("tab3")
                .setIndicator(button1)
                .setContent(map));
        
        mFirstTimeInSession = true;
	}
	
	
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.shops_tab_button_list:
			tabHost.setCurrentTab(0);
			bt_sel.selectBtn(0);
			buttonMe=false;
			break;
		case R.id.shops_tab_button_map:
			tabHost.setCurrentTab(1);
			bt_sel.selectBtn(1);
			buttonMe=true;
			break;
		case R.id.shops_tab_button_geopoint:
			if(buttonMe) ((Maps)getLocalActivityManager().getActivity("tab3")).showMe();
			break;
		
			}
	}
	
	@Override
	protected void onStart() {
		super.onStart();
		FlurryAgent.onStartSession(this, "3X5JRBVFV7QWXNP3Q8H2");
		
		if(mFirstTimeInSession){
			mFirstTimeInSession = false;
			FlurryAgent.logEvent(Constants.FLURRY_EVENT.Go_To_Shops.toString());
		}
	}
	 
	@Override
	protected void onStop()
	{
		super.onStop();		
		FlurryAgent.onEndSession(this);
	}
	
	@Override
	public View createTabContent(String tag) {
		return null;
	}
}
