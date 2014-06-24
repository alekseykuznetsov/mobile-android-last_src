package ru.enter;

import com.flurry.android.FlurryAgent;
import com.google.analytics.tracking.android.EasyTracker;

import ru.enter.DataManagement.BasketManager;
import ru.enter.base.BaseMenuActivity;
import ru.enter.beans.OrderBean;
import ru.enter.dialogs.CitiesDialogFragment;
import ru.enter.dialogs.PasswordChangeDialogFragment;
import ru.enter.dialogs.PersonalInfoDialogFragment;
import ru.enter.dialogs.PersonalSettingsDialogFragment;
import ru.enter.dialogs.PersonalInfoDialogFragment.OnUpdateInfoListener;
import ru.enter.dialogs.alert.LogoutDialogFragment;
import ru.enter.fragments.PersonalFormFragment;
import ru.enter.fragments.PersonalLeftFragment;
import ru.enter.fragments.PersonalRightFragment;
import ru.enter.utils.Constants;
import ru.enter.utils.PreferencesManager;
import ru.enter.utils.TypefaceUtils;
import ru.enter.utils.Utils;
import android.app.ActionBar;
import android.app.ActionBar.Tab;
import android.app.ActionBar.TabListener;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.DialogInterface;
import android.content.res.Configuration;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Spannable;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;
import android.widget.LinearLayout.LayoutParams;

public class PersonalActivity extends BaseMenuActivity implements TabListener, OnClickListener{
	
	private static final String ORDER_SER = "order";
	private static final String SELECTED_TAB = "selected_tab";

	LinearLayout mOrderLinear;
	FrameLayout mFormFrame;
	
	private boolean mFirstTimeInSession;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		EasyTracker.getInstance().setContext(this);
		setContentView(R.layout.personal_ac);		
		
		onConfigurationChanged(getResources().getConfiguration());
		
		mOrderLinear = (LinearLayout) findViewById(R.id.personal_order_ac_linear);
		mFormFrame = (FrameLayout) findViewById(R.id.personal_order_ac_frame);
		
		setTitleLeft(getResources().getString(R.string.actionbar_personal));
	    
		getActionBar().setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
		
        ActionBar.Tab personTab = getActionBar().newTab().setText(TypefaceUtils.setSpannableColor(TypefaceUtils.makeBrash("Мои заказы"), Color.GRAY));
        ActionBar.Tab personFormTab = getActionBar().newTab().setText(TypefaceUtils.setSpannableColor(TypefaceUtils.makeBrash("Анкета"), Color.GRAY));
        
        personTab.setTabListener(this);
        personFormTab.setTabListener(this);
        
        getActionBar().addTab(personTab);
        getActionBar().addTab(personFormTab);
        
        mFirstTimeInSession = true;
	}
	
	@Override
	protected void onStart(){
		super.onStart();
		FlurryAgent.onStartSession(this, "3X5JRBVFV7QWXNP3Q8H2");
		
		if(mFirstTimeInSession){
			mFirstTimeInSession = false;
			FlurryAgent.logEvent(Constants.FLURRY_EVENT.Go_To_Cabinet.toString());
		}
	}
	 
	@Override
	protected void onStop(){
		super.onStop();		
		FlurryAgent.onEndSession(this);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		
		getMenuInflater().inflate(R.menu.actionbar_menupart_settings, menu);
		ImageView image = new ImageView(this);
		image.setImageResource(R.drawable.icn_settings);
		image.setPadding(Utils.dp2pix(this, 10), 0, Utils.dp2pix(this, 25), 0);
		
		image.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if(((PersonalFormFragment)getFragmentManager().findFragmentByTag("personalForm")).isLoaded())
					showSettingsDialog();
			}
		});
		menu.findItem(R.id.menu_settings).setActionView(image);
		
		
		getMenuInflater().inflate(R.menu.actionbar_menupart_logout, menu);
		//раньше был просто элемент меню, но из-за темы появилась стрелочка спиннера, пришлось делать так
		image = new ImageView(this);
		image.setImageResource(R.drawable.icn_logout);
		image.setPadding(Utils.dp2pix(this, 10), 0, Utils.dp2pix(this, 15), 0);
		image.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				showLogoutDialog();
			}
		});
		menu.findItem(R.id.menu_logout).setActionView(image);
						
		return super.onCreateOptionsMenu(menu);
	}
	
	public void startRight(OrderBean currentOrder){

		FragmentManager manager = getFragmentManager();
		FragmentTransaction transaction = manager.beginTransaction();

		PersonalRightFragment rightFragment = PersonalRightFragment.getInstance();
		
		Bundle bundle = new Bundle();
		bundle.putSerializable(ORDER_SER, currentOrder);
		rightFragment.setArguments(bundle);

		transaction.replace(R.id.personal_order_ac_right_fragment, rightFragment);
		transaction.commit();		
	}

	@Override
	protected void onSaveInstanceState(Bundle savedInstanceState) {
		savedInstanceState.putInt(SELECTED_TAB, getActionBar().getSelectedNavigationIndex());
	}
	
	@Override
	protected void onRestoreInstanceState(Bundle savedInstanceState) {
        if ( savedInstanceState != null ) {
        	getActionBar().setSelectedNavigationItem(savedInstanceState.getInt(SELECTED_TAB));
        }
	}

	@Override
	public void onTabReselected(Tab tab, FragmentTransaction ft) {}

	@Override
	public void onTabSelected(Tab tab, FragmentTransaction transaction) {
		if (getFragmentManager().findFragmentByTag("orderList") == null)
		{
			PersonalLeftFragment leftFragment = PersonalLeftFragment.getInstance();
			transaction.replace(R.id.personal_order_ac_left_fragment, leftFragment, "orderList");
		}
		if (getFragmentManager().findFragmentByTag("personalForm") == null)
		{
			PersonalFormFragment formFragment = PersonalFormFragment.getInstance();
			transaction.replace(R.id.personal_order_ac_frame, formFragment, "personalForm");
		}
		if (tab.getPosition() == 0){
			mOrderLinear.setVisibility(View.VISIBLE);
			mFormFrame.setVisibility(View.GONE);

//			if (getFragmentManager().findFragmentByTag("orderList") == null)
//			{
//				PersonalLeftFragment leftFragment = PersonalLeftFragment.getInstance();
//				transaction.replace(R.id.personal_order_ac_left_fragment, leftFragment, "orderList");
//			}
		}
		if (tab.getPosition() == 1){
			mFormFrame.setVisibility(View.VISIBLE);
			mOrderLinear.setVisibility(View.GONE);
//			if (getFragmentManager().findFragmentByTag("personalForm") == null)
//			{
//				PersonalFormFragment formFragment = PersonalFormFragment.getInstance();
//				transaction.replace(R.id.personal_order_ac_frame, formFragment, "personalForm");
//			}
		}
		
		Spannable title = (Spannable) tab.getText();
		tab.setText(TypefaceUtils.setSpannableColor(title, Color.WHITE));
	}

	@Override
	public void onTabUnselected(Tab tab, FragmentTransaction ft) {
		Spannable title = (Spannable) tab.getText();
		tab.setText(TypefaceUtils.setSpannableColor(title, Color.GRAY));
	}
	
	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		
		FrameLayout leftFrame = (FrameLayout) findViewById(R.id.personal_order_ac_left_fragment);
		
		if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
			LayoutParams params = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT, 2.7f);
			leftFrame.setLayoutParams(params);
		} else if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {
			LayoutParams params_left = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT, 1.5f);
			leftFrame.setLayoutParams(params_left);
		}
	}
	
	private void showLogoutDialog () {
		LogoutDialogFragment dialog = LogoutDialogFragment.getInstance();
		dialog.setCancelable(false);
		dialog.setonClickListener(new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				switch (which) {
				case DialogInterface.BUTTON_POSITIVE:
					String personData =  PreferencesManager.getUserEmail();
					if(personData.isEmpty()) personData = PreferencesManager.getUserMobile(); 
					EasyTracker.getTracker().sendEvent("user/logout", "buttonPress", personData, null);
					logout();
		        	finish();
					break;
				default:
					break;
				}
			}
		});
		
		dialog.show(getFragmentManager(), "logout_dialog");//TODO
	}
	
	
	private void showSettingsDialog () {
		PersonalSettingsDialogFragment settFragment = PersonalSettingsDialogFragment.getInstance();
		settFragment.setOnClickListner(this);
		settFragment.show(getFragmentManager(), "personalSettings");//TODO
	}	
	
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		PersonalSettingsDialogFragment dlg = (PersonalSettingsDialogFragment) getFragmentManager().findFragmentByTag("personalSettings");
		final PersonalFormFragment formFragment = (PersonalFormFragment) getFragmentManager().findFragmentByTag("personalForm");
		
		switch (v.getId()) {
		case R.id.personal_dialog_settings_btn_chang_city:
			CitiesDialogFragment dialogFragment = CitiesDialogFragment.getInstance();
			dialogFragment.show(getFragmentManager(), "changeCity");
			// TODO перетостить сообщеньку
			if ( ! BasketManager.isEmpty()) {
				Toast.makeText(this, "Осторожно! При смене города цена на товары и их наличие может измениться! \n Для уточнения информации звоните в Контакт-cENTER 8-800-7000009", Toast.LENGTH_LONG).show();
			}
			dlg.dismiss();
			break;
		case R.id.personal_dialog_settings_btn_chang_data:
			PersonalInfoDialogFragment infoFragment = PersonalInfoDialogFragment.getInstance();
			infoFragment.setOnUpdateInfoListener(new OnUpdateInfoListener() {
				@Override
				public void onUpdateInfo() {
					if(formFragment!=null)
						formFragment.refreshPersonalInfo();
				}
			});
			infoFragment.show(getFragmentManager(), "changeInfo");
			dlg.dismiss();
			break;
		case R.id.personal_dialog_settings_btn_chang_pass:
			PasswordChangeDialogFragment passwordDialog = PasswordChangeDialogFragment.getInstance();
			passwordDialog.show(getFragmentManager(), "changePasswordDialog");
			dlg.dismiss();
			break;
		case R.id.personal_dialog_settings_btn_clear_cache:
			ApplicationTablet.getLoader(this).clearDiskCache();
			dlg.dismiss();			
			break;
		default:
			break;
		}
	}

	private void logout(){
		PreferencesManager.setToken("");
		PreferencesManager.setUserId(0);
		PreferencesManager.setUserEmail("");
		PreferencesManager.setUserName("");
		PreferencesManager.setUserLastName("");
		PreferencesManager.setUserMobile("");
		finish();
	}

	
	
}
