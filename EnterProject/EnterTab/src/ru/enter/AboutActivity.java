package ru.enter;

import com.flurry.android.FlurryAgent;

import ru.enter.base.BaseMenuActivity;
import ru.enter.fragments.AboutWebViewFragment;
import ru.enter.fragments.FeedBackFragment;
import ru.enter.utils.Constants;
import ru.enter.utils.TypefaceUtils;
import android.app.ActionBar;
import android.app.ActionBar.Tab;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Spannable;

public class AboutActivity extends BaseMenuActivity {

	private static final String ABOUT_URL = "http://content.enter.ru/mobile/about_company";
	private static final String MAKE_ORDER = "http://content.enter.ru/mobile/how_to_order";
	private static final String PAYMENT_URL = "http://content.enter.ru/mobile/how_to_pay";

	
	private static final String ABOUT_PROJECT = "О проекте";
	private static final String HOW_TO_ORDER = "Как сделать заказ";
	private static final String HOW_TO_PAY = "Как оплатить";
	private static final String FEEDBACK = "Обратная связь";
	
	private ActionBar mActionbar;
	private FeedBackFragment feedbackTabFragment;
	public static final String SELECTED_TAB = "selected_tab";
	
	public static final int ABOUT_PROJECT_TAB = 0;
	public static final int FEEDBACK_TAB = 3;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.about_ac);
		
		mActionbar = getActionBar();
		mActionbar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
		
		ActionBar.Tab aboutTab = mActionbar.newTab().setText(TypefaceUtils.setSpannableColor(TypefaceUtils.makeBrash(ABOUT_PROJECT), Color.GRAY));
		ActionBar.Tab makeOrderTab = mActionbar.newTab().setText(TypefaceUtils.setSpannableColor(TypefaceUtils.makeBrash(HOW_TO_ORDER), Color.GRAY));
		ActionBar.Tab paymentTab = mActionbar.newTab().setText(TypefaceUtils.setSpannableColor(TypefaceUtils.makeBrash(HOW_TO_PAY), Color.GRAY));
		ActionBar.Tab feedbackTab = mActionbar.newTab().setText(TypefaceUtils.setSpannableColor(TypefaceUtils.makeBrash(FEEDBACK), Color.GRAY));

		Fragment aboutTabFragment = AboutWebViewFragment.getInstance(ABOUT_URL);
		Fragment makeOrderTabFragment = AboutWebViewFragment.getInstance(MAKE_ORDER);
		Fragment paymentTabFragment = AboutWebViewFragment.getInstance(PAYMENT_URL);
		feedbackTabFragment = FeedBackFragment.getInstance();

		aboutTab.setTabListener(new AboutTabsListener(aboutTabFragment));
		makeOrderTab.setTabListener(new AboutTabsListener(makeOrderTabFragment));
		paymentTab.setTabListener(new AboutTabsListener(paymentTabFragment));
		feedbackTab.setTabListener(new AboutTabsListener(feedbackTabFragment));

		mActionbar.addTab(aboutTab);
		mActionbar.addTab(makeOrderTab);
		mActionbar.addTab(paymentTab);
		mActionbar.addTab(feedbackTab);
		
		if (savedInstanceState == null) {
			selectCurrentTab(getIntent());
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

	private void selectCurrentTab(Intent intent){
		int selected_tab = intent.getExtras().getInt(SELECTED_TAB);
		
//		if(selected_tab == FEEDBACK_TAB){
//			FlurryAgent.logEvent(Constants.FLURRY_EVENT.About_Feedback.toString());
//		}
		
		mActionbar.setSelectedNavigationItem(selected_tab);
	}
	
	@Override
	protected void onNewIntent (Intent intent) {
		super.onNewIntent(intent);
		selectCurrentTab(intent);
	}
	
	@Override
	protected void onSaveInstanceState(Bundle savedInstanceState) {
		savedInstanceState.putInt(SELECTED_TAB, mActionbar.getSelectedNavigationIndex());
		feedbackTabFragment.onSaveInstanceState(savedInstanceState);
	}

	@Override
	protected void onRestoreInstanceState(Bundle savedInstanceState) {
		if (savedInstanceState != null) {
			mActionbar.setSelectedNavigationItem(savedInstanceState.getInt(SELECTED_TAB));
			// для поворота
			feedbackTabFragment.setArguments(savedInstanceState);
		}
	}

	class AboutTabsListener implements ActionBar.TabListener {
		public Fragment fragment;

		public AboutTabsListener(Fragment fragment) {
			this.fragment = fragment;
		}

		@Override
		public void onTabReselected(Tab tab, FragmentTransaction ft) {

		}

		@Override
		public void onTabSelected(Tab tab, FragmentTransaction ft) {
			ft.replace(R.id.about_ac_tab_fragment_container, fragment);
			Spannable title = (Spannable) tab.getText();
			tab.setText(TypefaceUtils.setSpannableColor(title, Color.WHITE));
			
			if(tab.getPosition() == FEEDBACK_TAB){
				FlurryAgent.logEvent(Constants.FLURRY_EVENT.About_Feedback.toString());
			}
		}

		@Override
		public void onTabUnselected(Tab tab, FragmentTransaction ft) {
			Spannable title = (Spannable) tab.getText();
			tab.setText(TypefaceUtils.setSpannableColor(title, Color.GRAY));
		}

	}

}
