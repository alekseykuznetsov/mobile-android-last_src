package ru.enter;

import java.util.ArrayList;
import java.util.List;

import com.flurry.android.FlurryAgent;

import ru.enter.adapters.CallbacksListAdapter;
import ru.enter.beans.CallbackBean;
import ru.enter.utils.Constants;
import ru.enter.widgets.HeaderFrameManager;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ListView;


public class About extends Activity implements OnClickListener, OnItemClickListener{

	public static final int ABOUT_TYPE = 1;
	public static final int CALLBACK_TYPE = 2;
	public static final  String ACTIVITY_TYPE = "ACTIVITY_TYPE";

	private WebView mAboutWebView;
	private ListView mCallbackList;
	private Button mNewsButton,mInformationButton,mResponsibilityButton,mFeedbackButton;
	private Context mContext;
	private ArrayList<CallbackBean> callbaks;
	private boolean mFirstTimeInSession;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.about_activity);

		//		String manufacturer = android.os.Build.MANUFACTURER;
		//		String brand = android.os.Build.BRAND;
		//		String device = android.os.Build.DEVICE;

		FrameLayout frame = (FrameLayout) findViewById(R.id.about_activity_frame);
		frame.addView(HeaderFrameManager.getHeaderView(About.this, "О проекте", false));

		int type = getIntent().getIntExtra(ACTIVITY_TYPE, ABOUT_TYPE);

		mContext = getParent(); //parent context due to Tab1Groupactivity( 'this' causes PD crash on show() )

		mNewsButton = (Button)findViewById(R.id.news_Button);
		mNewsButton.setOnClickListener(this);


		mInformationButton = (Button)findViewById(R.id.information_Button);
		mInformationButton.setOnClickListener(this);

		mResponsibilityButton = (Button)findViewById(R.id.responsibility_Button);
		mResponsibilityButton.setOnClickListener(this);

		mFeedbackButton = (Button)findViewById(R.id.feedback_Button);
		mFeedbackButton.setOnClickListener(this);		

		mAboutWebView = (WebView)findViewById(R.id.about_web_view);
		mAboutWebView.setWebViewClient(new WebClientClass()); //custom webclient for progressdialog support, fix 2492
		mAboutWebView.getSettings().setSupportZoom(false);

		mCallbackList = (ListView) findViewById(R.id.about_list_callbacks);
		mCallbackList.setOnItemClickListener(this);
		initCallbacks();
		if(type == ABOUT_TYPE){
			mNewsButton.setBackgroundColor(Color.WHITE);
			mNewsButton.setTextColor(Color.BLACK);
			mNewsButton.performClick();
		} else {
			mFeedbackButton.setBackgroundColor(Color.WHITE);
			mFeedbackButton.setTextColor(Color.BLACK);
			mFeedbackButton.performClick();
		}

		mFirstTimeInSession = true;
	}

	@Override
	protected void onStart(){
		super.onStart();
		FlurryAgent.onStartSession(this, "3X5JRBVFV7QWXNP3Q8H2");

//		if(mFirstTimeInSession){
//			mFirstTimeInSession = false;
//			int type = getIntent().getIntExtra(ACTIVITY_TYPE, ABOUT_TYPE);
//			if(type == ABOUT_TYPE){
//				FlurryAgent.logEvent(Constants.FLURRY_EVENT.About_Project.toString());
//			} else {
//				FlurryAgent.logEvent(Constants.FLURRY_EVENT.About_Feedback.toString());
//			}
//			
//		}
	}

	@Override
	protected void onStop()
	{
		super.onStop();		
		FlurryAgent.onEndSession(this);
	}

	private void setControlButtonGray(Button button) {

		mNewsButton.setBackgroundColor(Color.rgb(147, 147, 147));
		mInformationButton.setBackgroundColor(Color.rgb(147, 147, 147));
		mResponsibilityButton.setBackgroundColor(Color.rgb(147, 147, 147));
		mFeedbackButton.setBackgroundColor(Color.rgb(147, 147, 147));
		mNewsButton.setTextColor(Color.WHITE);
		mInformationButton.setTextColor(Color.WHITE);
		mResponsibilityButton.setTextColor(Color.WHITE);
		mFeedbackButton.setTextColor(Color.WHITE);

		button.setBackgroundColor(Color.WHITE);
		button.setTextColor(Color.BLACK);

	}

	private void initCallbacks(){

		CallbackBean bean;
		callbaks = new ArrayList<CallbackBean>();
		bean = new CallbackBean("Отправить сообщение",CallbackBean.MESSAGE);
		callbaks.add(bean);
		TelephonyManager mng = (TelephonyManager) mContext.getSystemService(Context.TELEPHONY_SERVICE);
		if(mng.getPhoneType()!=TelephonyManager.PHONE_TYPE_NONE){
			bean = new CallbackBean("Позвонить",CallbackBean.PHONE);
			callbaks.add(bean);
		}
		mCallbackList.setAdapter(new CallbacksListAdapter(mContext, R.layout.callback_list_item, callbaks));		
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.news_Button:
			mCallbackList.setVisibility(View.GONE);
			mAboutWebView.setVisibility(View.VISIBLE);
			mAboutWebView.loadUrl("http://content.enter.ru/mobile/about_company");
//			FlurryAgent.logEvent(Constants.FLURRY_EVENT.About_Project.toString());
			setControlButtonGray((Button) v);
			break;
		case R.id.information_Button:
			mCallbackList.setVisibility(View.GONE);
			mAboutWebView.setVisibility(View.VISIBLE);
			mAboutWebView.loadUrl("http://content.enter.ru/mobile/how_to_order");
//			FlurryAgent.logEvent(Constants.FLURRY_EVENT.About_Order.toString());
			setControlButtonGray((Button) v);
			break;
		case R.id.responsibility_Button:
			mCallbackList.setVisibility(View.GONE);
			mAboutWebView.setVisibility(View.VISIBLE);
			mAboutWebView.loadUrl("http://content.enter.ru/mobile/how_to_pay");
//			FlurryAgent.logEvent(Constants.FLURRY_EVENT.About_How_To_Pay.toString());
			setControlButtonGray((Button) v);
			break;
		case R.id.feedback_Button:
			mAboutWebView.setVisibility(View.GONE);
			mCallbackList.setVisibility(View.VISIBLE);
			FlurryAgent.logEvent(Constants.FLURRY_EVENT.About_Feedback.toString());
			setControlButtonGray((Button) v);

			break;
		default:
			break;
		}
	}

	public class WebClientClass extends WebViewClient {
		ProgressDialog pd;

		@Override
		public void onPageStarted(WebView view, String url, Bitmap favicon) {
			super.onPageStarted(view, url, favicon);
			if (pd != null && pd.isShowing()) return;
			pd = ProgressDialog.show(mContext, "","Загрузка...",true);
			//if(!isFinishing()) pd.show();
		}

		@Override
		public boolean shouldOverrideUrlLoading(WebView view, String url) {
			Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
			startActivity(intent);
			return true;
		}

		@Override
		public void onPageFinished(WebView view, String url) {
			super.onPageFinished(view, url);
			if(pd.isShowing()) pd.dismiss();
		}
	}


	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		// TODO Auto-generated method stub
		CallbackBean bean = (CallbackBean) view.getTag();
		switch(bean.getType()){
		case CallbackBean.MESSAGE:
			startActivity(new Intent().setClass(About.this, FeedBackActivity.class));
			break;
		case CallbackBean.PHONE:
			Uri uri = Uri.parse("tel:88007000009");
			Intent intent = new Intent(Intent.ACTION_VIEW, uri);
			startActivity(intent);
			break;
		default:
			break;

		}		
	}	
}

