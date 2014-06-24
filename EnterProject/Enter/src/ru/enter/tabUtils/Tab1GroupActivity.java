package ru.enter.tabUtils;

import com.flurry.android.FlurryAgent;

import ru.enter.About;
import ru.enter.BasketActivity;
import ru.enter.CameraActivity;
import ru.enter.CatalogActivity;
import ru.enter.FakeActivity;
import ru.enter.MainActivity;
import ru.enter.SearchGlobalActivity;
import ru.enter.PersonalAccount;
import ru.enter.ServicesActivity;
import ru.enter.ShopMapTabs;
import ru.enter.enums.MainSections;
import ru.enter.utils.Constants;
import android.content.Intent;
import android.os.Bundle;
//промежуточный класс для формирования стека активити в одной вкладке.
public class Tab1GroupActivity extends TabGroupActivity{
	public static final String ACTIVITY = "activity";
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Bundle bundle = getIntent().getExtras();
		int type=About.ABOUT_TYPE;
		if(bundle!=null){
			MainSections mainSection = MainSections.valueOf(bundle.getString(ACTIVITY));
			Intent intent = new Intent();
			switch (mainSection) {
				case about:
					intent.setClass(getApplicationContext(), About.class);
					type=bundle.getInt(About.ACTIVITY_TYPE, About.ABOUT_TYPE);
					intent.putExtra(About.ACTIVITY_TYPE, type);
					break;
				case catalog:
					intent.setClass(getApplicationContext(), CatalogActivity.class);
					break;
				case shops:
					intent.setClass(getApplicationContext(), ShopMapTabs.class);
					break;
//				case services:
//					intent.setClass(getApplicationContext(), ServicesActivity.class);
//					break;
				case search:
					intent.setClass(getApplicationContext(), SearchGlobalActivity.class);
					break;
				case qr_scanner:
//					FlurryAgent.logEvent(Constants.FLURRY_EVENT.Go_To_Scanner.toString());
					intent.setClass(getApplicationContext(), CameraActivity.class);
					break;
				case basket:
//					FlurryAgent.logEvent(Constants.FLURRY_EVENT.Go_To_Basket.toString());
					intent.setClass(getApplicationContext(), BasketActivity.class);
					intent.putExtra(BasketActivity.SHOW_BUTTON, true);
					break;
				case personal_account:
					intent.setClass(getApplicationContext(), PersonalAccount.class);
					break;
				case fake:
					intent.setClass(getApplicationContext(), FakeActivity.class);
					break;
				default:
					
					break;
			}
			startChildActivity(mainSection.name(), intent);
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
}
