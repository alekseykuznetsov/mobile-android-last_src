package ru.enter;

import java.util.ArrayList;

import com.flurry.android.FlurryAgent;

import ru.enter.DataManagement.BasketManager;
import ru.enter.DataManagement.BasketManager.CountPrice;
import ru.enter.beans.SliderMenuItem;
import ru.enter.enums.MainSections;
import ru.enter.interfaces.OnBasketChangeListener;
import ru.enter.utils.Constants;
import ru.enter.utils.Formatters;
import ru.enter.utils.Utils;
import ru.enter.widgets.Panel;
import android.app.TabActivity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Rect;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TabHost;
import android.widget.TextView;

public class MainActivity extends TabActivity implements OnBasketChangeListener {
	public static final String MAINSECTION = "mainsection";
	private ArrayList<SliderMenuItem> menuItems;
	private TextView mCost;
	private TextView mCount;
	private static Animation mShake;
	private static Panel mPanel;
	private static TabHost mTabHost;
	private static View mBasketView;

	@Override
	public void onCreate (Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		mTabHost = getTabHost();
		TabHost.TabSpec spec;
		Intent intent;
		String section = getIntent().getExtras().getString(MAINSECTION);
		MainSections mainSection = MainSections.valueOf(section);
		int type = About.ABOUT_TYPE;
		if(mainSection == MainSections.about)
			type = getIntent().getExtras().getInt(About.ACTIVITY_TYPE, About.ABOUT_TYPE);

		LayoutInflater inflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		initTab();
		
		View view = inflater.inflate(R.layout.slider_menu_row, null);
		
		intent = new Intent().setClass(this, ru.enter.tabUtils.Tab1GroupActivity.class);
		intent.putExtra(ru.enter.tabUtils.Tab1GroupActivity.ACTIVITY, MainSections.fake.name());
		spec = mTabHost.newTabSpec(MainSections.fake.name()).setIndicator(view).setContent(intent);
		view.setVisibility(view.GONE);
		mTabHost.addTab(spec);
		
		for (SliderMenuItem item : menuItems) {
			view = inflater.inflate(R.layout.slider_menu_row, null);
			ImageView image = (ImageView) view.findViewById(R.id.slider_menu_row_image);
			TextView text = (TextView) view.findViewById(R.id.slider_menu_row_text);

			image.setImageResource(item.getImageSrc());
			text.setText(item.getTitle());
			text.setTypeface(Utils.getTypeFace(this));

			intent = new Intent().setClass(this, ru.enter.tabUtils.Tab1GroupActivity.class);
			intent.putExtra(ru.enter.tabUtils.Tab1GroupActivity.ACTIVITY, item.getSection().name());
			if(item.getSection().name() == MainSections.about.name())
				intent.putExtra(About.ACTIVITY_TYPE, type);
			spec = mTabHost.newTabSpec(item.getSection().name()).setIndicator(view).setContent(intent);
			mTabHost.addTab(spec);
		}

		view = inflater.inflate(R.layout.slider_menu_row, null);

		intent = new Intent().setClass(this, ru.enter.tabUtils.Tab1GroupActivity.class);
		intent.putExtra(ru.enter.tabUtils.Tab1GroupActivity.ACTIVITY, MainSections.personal_account.name());
		spec = mTabHost.newTabSpec(MainSections.personal_account.name()).setIndicator(view).setContent(intent);
		mTabHost.addTab(spec);

		view = inflater.inflate(R.layout.slider_menu_row, null);

		intent = new Intent().setClass(this, ru.enter.tabUtils.Tab1GroupActivity.class);
		intent.putExtra(ru.enter.tabUtils.Tab1GroupActivity.ACTIVITY, MainSections.basket.name());
		spec = mTabHost.newTabSpec(MainSections.basket.name()).setIndicator(view).setContent(intent);
		mTabHost.addTab(spec);		
		

		mTabHost.setCurrentTabByTag(mainSection.name());

		mCost = (TextView) findViewById(R.id.main_cost);
		mCount = (TextView) findViewById(R.id.main_product_count);
		mCost.setTypeface(Utils.getRoubleTypeFace(this));

		BasketManager.setOnBasketChangeListener(this);

		mBasketView = findViewById(R.id.main_basket_icon);
		mBasketView.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick (View v) {
				mTabHost.setCurrentTabByTag(MainSections.basket.name());
			}
		});

		mShake = AnimationUtils.loadAnimation(this, R.anim.shake);
		mPanel = (Panel) findViewById(R.id.main_slider_panel);

		mPanel.setOnPanelListener(new Panel.OnPanelListener() {

			@Override
			public void onPanelOpened (Panel panel) {
				mBasketView.setVisibility(View.INVISIBLE);
			}

			@Override
			public void onPanelClosed (Panel panel) {
				mBasketView.setVisibility(View.VISIBLE);
			}
		});

		// добавление сканера не как таба, чтобы сканер был на весь экран
		View llView = inflater.inflate(R.layout.slider_menu_row, null);
		ImageView image = (ImageView) llView.findViewById(R.id.slider_menu_row_image);
		TextView text = (TextView) llView.findViewById(R.id.slider_menu_row_text);

		image.setImageResource(R.drawable.tab8);
		text.setText("Сканер");
		text.setTypeface(Utils.getTypeFace(this));

		llView.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick (View v) {
				Intent intent = new Intent();
				intent.setClass(getApplicationContext(), CameraActivity.class);
//				FlurryAgent.logEvent(Constants.FLURRY_EVENT.Go_To_Scanner.toString());
				startActivity(intent);
			}
		});

		mTabHost.getTabWidget().addView(llView, 2);

	}

	@Override
	protected void onResume () {
		super.onResume();
		BasketManager.notifyListeners();
	}
	
	@Override
	protected void onStart()
	{
		super.onStart();
		FlurryAgent.onStartSession(this, "3X5JRBVFV7QWXNP3Q8H2");
	}
	 
	@Override
	protected void onStop(){
		super.onStop();		
		FlurryAgent.onEndSession(this);
	}

	public static void showDragAndDrop () {
		mPanel.setVisibility(View.GONE);
		mBasketView.startAnimation(mShake);
	}

	public static void hideDragAndDrop () {
		mPanel.setVisibility(View.VISIBLE);
	}

	@Override
	public boolean onSearchRequested () {
		return false;
	}

	@Override
	public boolean onKeyDown (int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_SEARCH) {
			runSearch();
			return false;
		}
		return super.onKeyDown(keyCode, event);
	}

	@Override
	public boolean onKeyUp (int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_SEARCH) {
			return false;
		}
		return super.onKeyUp(keyCode, event);
	}

	public static Rect getBasketRect () {
		Rect returnRect = new Rect();
		mBasketView.getGlobalVisibleRect(returnRect);
		return returnRect;
	}

	public static void runPersonalAccount () {
		mTabHost.setCurrentTabByTag(MainSections.personal_account.name());
	}

	public static void runSearch () {
		mTabHost.setCurrentTabByTag(MainSections.search.name());
	}
//
//	public static void runBasket () {
//		mTabHost.setCurrentTabByTag(MainSections.basket.name());
//	}

	private void initTab () {
		menuItems = new ArrayList<SliderMenuItem>(8);

		SliderMenuItem sliderMenuItem = new SliderMenuItem();
		sliderMenuItem.setImageSrc(R.drawable.tab1);
		sliderMenuItem.setTitle("Каталог");
		sliderMenuItem.setSection(MainSections.catalog);
		menuItems.add(sliderMenuItem);

		sliderMenuItem = new SliderMenuItem();
		sliderMenuItem.setImageSrc(R.drawable.tab2);
		sliderMenuItem.setTitle("Поиск");
		sliderMenuItem.setSection(MainSections.search);
		menuItems.add(sliderMenuItem);

		sliderMenuItem = new SliderMenuItem();
		sliderMenuItem.setImageSrc(R.drawable.tab4);
		sliderMenuItem.setTitle("Кабинет");
		sliderMenuItem.setSection(MainSections.personal_account);
		menuItems.add(sliderMenuItem);

		sliderMenuItem = new SliderMenuItem();
		sliderMenuItem.setImageSrc(R.drawable.tab5);
		sliderMenuItem.setTitle("Магазины");
		sliderMenuItem.setSection(MainSections.shops);
		menuItems.add(sliderMenuItem);
/*
		sliderMenuItem = new SliderMenuItem();
		sliderMenuItem.setImageSrc(R.drawable.tab6);
		sliderMenuItem.setTitle("Сервис");
		sliderMenuItem.setSection(MainSections.services);
		menuItems.add(sliderMenuItem);
*/
		sliderMenuItem = new SliderMenuItem();
		sliderMenuItem.setImageSrc(R.drawable.tab7);
		sliderMenuItem.setTitle("О компании");
		sliderMenuItem.setSection(MainSections.about);
		menuItems.add(sliderMenuItem);
	}

	@Override
	public void onBasketChange () {
		CountPrice object = BasketManager.getCountPriceObject();

		if (mCost != null && mCount != null) {
			if (object.allPrice != 0) {
				mCost.setText(Formatters.createPriceStringWithRouble(object.allPrice));
				mCost.setVisibility(View.VISIBLE);
			} else {
				mCost.setVisibility(View.INVISIBLE);
			}

			if (object.allCount != 0) {
				mCount.setText(String.valueOf(object.allCount));
				mCount.setVisibility(View.VISIBLE);
			} else {
				mCount.setVisibility(View.INVISIBLE);
			}
		}
	}
}
