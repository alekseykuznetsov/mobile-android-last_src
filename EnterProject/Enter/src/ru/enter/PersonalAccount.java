package ru.enter;

import com.flurry.android.FlurryAgent;
import com.google.analytics.tracking.android.EasyTracker;

import ru.enter.AuthorizationActivity.RunType;
import ru.enter.beans.CheckoutBean;
import ru.enter.beans.PersonBean;
import ru.enter.tabUtils.TabGroupActivity;
import ru.enter.utils.Constants;
import ru.enter.utils.PreferencesManager;
import ru.enter.widgets.NewHeaderFrameManager;
import ru.enter.widgets.TabButton;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.InputType;
import android.text.TextUtils;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TabHost;
import android.widget.Toast;
import android.widget.LinearLayout.LayoutParams;
import ru.enter.DataManagement.BasketData;
import ru.enter.DataManagement.PersonData;

public class PersonalAccount extends TabGroupActivity{

	private LinearLayout dialog;
	private Button button_auth;
	private TabHost tabHost;
	private Button exitAcc;

	private boolean mFirstTimeInSession;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		EasyTracker.getInstance().setContext(this);
		setContentView(R.layout.personal_account_activity);
		setupWidgets();
		mFirstTimeInSession = true;
	}

	@Override
	protected void onStart() {
		super.onStart();
		FlurryAgent.onStartSession(this, "3X5JRBVFV7QWXNP3Q8H2");
	}

	@Override
	protected void onStop() {
		super.onStop();		
		FlurryAgent.onEndSession(this);
	}

	private void setupWidgets() {
		FrameLayout header_lay = (FrameLayout) findViewById(R.id.personal_activity_header_layout);
		//header_lay.addView(HeaderFrameManager.getHeaderView(PersonalAccount.this, "Кабинет", false));
		header_lay.addView(NewHeaderFrameManager.getHeaderViewPersonalAccount(PersonalAccount.this, "Кабинет","Выход", new OnClickListener() {

			@Override
			public void onClick(View v) {
				PersonalForm.logout = true;
				String personData =  PreferencesManager.getUserEmail();
				if(TextUtils.isEmpty(personData)) personData = PreferencesManager.getUserMobile();
				EasyTracker.getTracker().sendEvent("user/logout", "buttonPress", personData, null);
				logout();
				PersonData.getInstance().setPersonBean(new PersonBean());
				BasketData.getInstance().setCheckoutBean(new CheckoutBean());
				Toast.makeText(PersonalAccount.this, "Вы успешно вышли", Toast.LENGTH_SHORT).show();
				finish();
			}
		}));

		exitAcc = NewHeaderFrameManager.getExitBtn();		

		tabHost = (TabHost) findViewById(android.R.id.tabhost);
		tabHost.setup(getLocalActivityManager());
		initTabHost();

		dialog = (LinearLayout) findViewById(R.id.personal_account_activity_dialog);
		button_auth = (Button) findViewById(R.id.personal_account_activity_dialog_button);

		button_auth.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent().setClass(PersonalAccount.this, AuthorizationActivity.class);
				intent.putExtra(AuthorizationActivity.RUNTYPE, RunType.personal_account.name());
				startActivity(intent);
			}
		});
	}

	private void initTabHost() {
		tabHost.addTab(tabHost.newTabSpec("tab1")
				.setIndicator(getIndicator("Мои заказы"))
				.setContent(new Intent(this, PersonalOrders.class)));

		tabHost.addTab(tabHost.newTabSpec("tab2")
				.setIndicator(getIndicator("Анкета"))
				.setContent(new Intent(this, PersonalForm.class)));
	}

	private Button getIndicator(String text){
		TabButton button = new TabButton(this);
		button.setText(text);
		return button;
	}

	@Override
	protected void onResume() {
		super.onResume();
		if (PreferencesManager.isAuthorized()){
			hideDialog();
			
			if(mFirstTimeInSession){
				mFirstTimeInSession = false;
				FlurryAgent.logEvent(Constants.FLURRY_EVENT.Go_To_Cabinet.toString());
			}
		} else showDialog();
	}


	private void showDialog(){
		dialog.setVisibility(View.VISIBLE);
		if(exitAcc!=null) exitAcc.setVisibility(View.INVISIBLE);
	}

	private void hideDialog(){
		dialog.setVisibility(View.GONE);
		if(exitAcc!=null) exitAcc.setVisibility(View.VISIBLE);
	}
	/*Новые методы и диалоги*/


	private void logout () {
		PreferencesManager.setToken("");
		PreferencesManager.setUserId(0);
		PreferencesManager.setUserEmail("");
		PreferencesManager.setUserName("");
		PreferencesManager.setUserLastName("");
		PreferencesManager.setUserMobile("");
		PreferencesManager.showGetCouponDialog(true);
	}

}
