package ru.enter;

import com.flurry.android.FlurryAgent;
import com.google.analytics.tracking.android.EasyTracker;

import ru.enter.DataManagement.BasketData;
import ru.enter.beans.AddressBean;
import ru.enter.beans.CheckoutBean;
import ru.enter.beans.CheckoutBean.CheckoutFirstStepDelivery;
import ru.enter.utils.ICheckoutInterface;
import ru.enter.widgets.CheckoutFirstStepView;
import ru.enter.widgets.CheckoutSecondStepView;
import ru.enter.widgets.CheckoutThirdStepView;
import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;
import android.widget.ViewAnimator;

public class CheckoutActivity extends Activity implements OnClickListener{
	public static final String LAUNCH_TYPE = "launch_type";
	public static final String LAUNCH_CLICK_ONE_TYPE = "launch_click_one_type";
	public static final String SHOP_ID = "shop_id";
	public static final String SHOP_ADDRESS = "shop_address";
	
	public static final int LAUNCH_TYPE_NORMAL = 0;
	public static final int LAUNCH_TYPE_BUY_ONE_CLICK = 1;
	public static final int LAUNCH_CLICK_ONE_TYPE_1 = 2;
	public static final int LAUNCH_CLICK_ONE_TYPE_2 = 3;
	
	private int mCurrentLaunchType = 1;
	private ViewAnimator mViewAnimator;
	private CheckoutFirstStepView mFirstStepView;
	private CheckoutSecondStepView mSecondStepView;
	private CheckoutThirdStepView mThirdStepView;
//	private boolean isRunAuth = false;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		EasyTracker.getInstance().setContext(this);
		setContentView(R.layout.checkout_activity);
		mViewAnimator = (ViewAnimator)findViewById(R.id.checkout_activity_viewanimator);

		mViewAnimator.setInAnimation(this, android.R.anim.fade_in);
		mViewAnimator.setOutAnimation(this, android.R.anim.fade_out);
		
		mCurrentLaunchType = getIntent().getExtras().getInt(LAUNCH_TYPE);
		
		if(mCurrentLaunchType==LAUNCH_TYPE_NORMAL){
			launchNormal();
		}else if(mCurrentLaunchType==LAUNCH_TYPE_BUY_ONE_CLICK){
			
			launchBuyOneClick();
		}
		
		
		
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
	
	private void launchNormal(){
		mFirstStepView = new CheckoutFirstStepView(this);
		mSecondStepView = new CheckoutSecondStepView(this,false);
		mThirdStepView = new CheckoutThirdStepView(this);
		
		mViewAnimator.addView(mFirstStepView);
		mViewAnimator.addView(mSecondStepView);
		mViewAnimator.addView(mThirdStepView);
		
		mFirstStepView.setOnClickListener(this);
		mSecondStepView.setOnClickListener(this);
		mSecondStepView.setOnClickListener(this);
	}
	
	private void launchBuyOneClick(){
		mSecondStepView = new CheckoutSecondStepView(this,true);
		mThirdStepView = new CheckoutThirdStepView(this, true);
		
		mViewAnimator.addView(mSecondStepView);
		mViewAnimator.addView(mThirdStepView);
		
		mSecondStepView.setOnClickListener(this);
		mSecondStepView.setOnClickListener(this);
		
		String address = getIntent().getExtras().getString(SHOP_ADDRESS);
		int shop_id = getIntent().getExtras().getInt(SHOP_ID);
		CheckoutBean checkoutBean = BasketData.getInstance().getCheckoutBean();
		checkoutBean.setCheckoutFirstStepDelivery(CheckoutFirstStepDelivery.pickup);
		
		checkoutBean.setShopAddress(address);
		checkoutBean.setShop_id(shop_id);
	}
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if(keyCode==KeyEvent.KEYCODE_BACK){
			if(mViewAnimator.getDisplayedChild()!=0){//CurrentView().equals(mFirstStepView)){
				mViewAnimator.showPrevious();
				return true;
			}
			
		}
		return super.onKeyDown(keyCode, event);
	}
	
	@Override
	public void onClick(View v) {
		InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
		imm.hideSoftInputFromWindow(mViewAnimator.getWindowToken(), 0);

		if(!((ICheckoutInterface)mViewAnimator.getCurrentView()).isSvyaznoyCardValid()){
			Toast.makeText(this, "В номере карты «Связной-Клуб» допущена ошибка.\nПроверьте правильность ввода номера, расположенного на обороте под штрих кодом, и повторите попытку", Toast.LENGTH_LONG).show();
		}
		else if(((ICheckoutInterface)mViewAnimator.getCurrentView()).isNext()){
			if(mViewAnimator.getCurrentView().equals(mSecondStepView)){
				mThirdStepView.initHeader();
				mThirdStepView.initFooterData();
			}
			if(!mViewAnimator.getCurrentView().equals(mThirdStepView)){
				mViewAnimator.showNext();
			}
		}
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		if(mViewAnimator.getCurrentView().equals(mFirstStepView)){
			mFirstStepView.refreshEdit();
		}
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		BasketData.getInstance().getCheckoutBean().setUserAddress(new AddressBean());
		BasketData.getInstance().getCheckoutBean().setShop_id(0);
		BasketData.getInstance().getCheckoutBean().setShopAddress("");
	}
}
