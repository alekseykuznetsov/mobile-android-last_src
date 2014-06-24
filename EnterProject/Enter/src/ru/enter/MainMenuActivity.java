package ru.enter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.flurry.android.FlurryAgent;
import com.google.analytics.tracking.android.EasyTracker;

import ru.enter.AuthorizationActivity.RunType;
import ru.enter.adapters.AdAdapter;
import ru.enter.adapters.MainMenuGridAdapter;
import ru.enter.beans.BannerBean;
import ru.enter.beans.CitiesBean;
import ru.enter.enums.MainSections;
import ru.enter.parsers.BannersParser;
import ru.enter.parsers.CitiesParser;
import ru.enter.parsers.UserAuthParser;
import ru.enter.utils.Constants;
import ru.enter.utils.PreferencesManager;
import ru.enter.utils.ShopLocator;
import ru.enter.utils.URLManager;
import ru.enter.utils.Utils;
import ru.enter.widgets.Dots;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.Pair;
import android.view.Display;
import android.view.DragEvent;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnDragListener;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup.LayoutParams;
import android.view.animation.AlphaAnimation;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

public class MainMenuActivity extends Activity implements OnTouchListener, OnClickListener{

	//data for testlite
	private boolean isTestLite = true;
	private int versionBuild = 8;
	private TextView labelTestLite;


	private GridView mainGrid;
	private ViewPager adPager;
	private Context context;
	private AdAdapter adAdapter;
	private int ad_current = 0;
	private ImageView splash, ad_left, ad_right;
	private Object data;
	private Dots dots;
	private String[] titles;


	private FrameLayout mProgress;
	private Location mMyLocation;
	private LocationManager mManager;
	private ArrayList<CitiesBean> mCities;
	private String cityName;
	private boolean isMyLoc, isGetCitys,isCityDone;
	private int citysState=0;
	private GetCityLoader mLoader;
	private final static int DISABLE_LOCATION = 1;//отказ от определения места, только через выбор города
	private final static int ENABLE_LOCATION_NO_CITYS = 2;//разрешено определять, но не смогли загрузить списки городов
	private final static int ENABLE_LOCATION_NO_CITY = 3;//разрешено определять, загрузили списки городов, не определили позицию
	private final static int ENABLE_LOCATION_CITY_NO_FIND = 4;//разрешено определять, загрузили списки городов, определили позицию, но не устроило
	private final static int ENABLE_LOCATION_CITY = 5;//разрешено определять, загрузили списки городов, определили позицию

	private static int NUM = 0;

	private static final int COMPUTE_AD_SCREEN = 0;
	private static final int SELECT_AD_SCREEN = 1;
	private static final int REMOVE_SPLASH = 2;
	private static final int GET_CITY = 3;
	private static final int SHOW_CITY = 4;
	private static final int BANNER = 5;

	private static final int SPLASH_TIME = 4000;
	private static final int AD_CHANGE_TIME = 5000;
	//	private static final int CATALOG = 0;
	//	private static final int SEARCH = 1;
	//	private static final int FEED_BACK = 8;
	//	private static final int MY_CART = 3;
	//	private static final int MY_ACCOUNT = 4;
	//	private static final int SHOPS = 5;
	//	private static final int SERVICES = 6;
	//	private static final int SCANER = 2;
	//	private static final int ABOUT = 7;
	private static final int CATALOG = 0;
	private static final int SEARCH = 1;
	private static final int FEED_BACK = 7;
	private static final int MY_CART = 3;
	private static final int MY_ACCOUNT = 4;
	private static final int SHOPS = 5;	
	private static final int SCANER = 2;
	private static final int ABOUT = 6;

	/**
	 * Templates for http request executing
	 * UI beh 
	 */
	private WindowManager.LayoutParams mLayoutParams;
	private WindowManager mWindowManager;
	private FrameLayout mProgressFrameLayout;

	private static boolean iIsFirst = true;

	private Handler handler = new Handler() {
		@SuppressWarnings ("unchecked")
		public void handleMessage (Message msg) {
			switch (msg.what) {
			case COMPUTE_AD_SCREEN:
				handler.sendEmptyMessageDelayed(SELECT_AD_SCREEN, AD_CHANGE_TIME);
				break;
			case SELECT_AD_SCREEN:
				if (ad_current < NUM - 1)
					ad_current++;
				else
					ad_current = 0;
				adPager.setCurrentItem(ad_current, true);
				handler.sendEmptyMessage(COMPUTE_AD_SCREEN);
				break;
			case REMOVE_SPLASH:
				AlphaAnimation anim = new AlphaAnimation(1F, 0F);
				labelTestLite.setVisibility(View.GONE);
				anim.setDuration(500);
				splash.startAnimation(anim);
				splash.setVisibility(View.GONE);

				handler.sendEmptyMessage(GET_CITY);

				break;
			case GET_CITY:
				if (PreferencesManager.getCityName().equals("")){
					//showDialog();					
					switch (citysState) {
					case ENABLE_LOCATION_NO_CITYS:
						showDialogNoCitys();
						break;

					case ENABLE_LOCATION_NO_CITY:
						showDialogNoCity();
						break;

					case ENABLE_LOCATION_CITY:
						showDialogCity();
						break;

					default:
						break;
					};
				}
				else
					handler.sendEmptyMessage(SHOW_CITY);
				break;
			case SHOW_CITY:
				Toast.makeText(context, "Ваш город - " + PreferencesManager.getCityName(), Toast.LENGTH_SHORT).show();
				EasyTracker.getTracker().sendEvent("session/initialized", "buttonPress",PreferencesManager.getCityName() , null);
				//								offerCouponForFirstLogin();
				break;
			case BANNER:
				if (!Utils.isEmptyList((ArrayList<BannerBean>) msg.obj)) {
					stopRollAd();
					adAdapter.setObjects((ArrayList<BannerBean>) msg.obj);

					
					NUM = ((ArrayList<BannerBean>) msg.obj).size();
					dots.initDots(NUM, 1);
					adPager.setCurrentItem(1);

					if (NUM <= 1) {
						ad_left.setVisibility(View.GONE);
						ad_right.setVisibility(View.GONE);
						dots.setVisibility(View.INVISIBLE);
					} else {
						ad_right.setVisibility(View.VISIBLE);
						ad_left.setVisibility(View.VISIBLE);
						dots.setVisibility(View.VISIBLE);
					}

					startRollAd();
				}
				break;
			default:
				break;
			}

		};
	};

	private void startRollAd () {
		handler.sendEmptyMessageDelayed(COMPUTE_AD_SCREEN, SPLASH_TIME + AD_CHANGE_TIME);
	}

	private void stopRollAd () {
		handler.removeMessages(COMPUTE_AD_SCREEN);
		handler.removeMessages(SELECT_AD_SCREEN);

	}

	private void setCity (Intent intent, String city, int id_city, boolean hasShop) {
		PreferencesManager.setCityName(city);
		PreferencesManager.setCityId(id_city);
		PreferencesManager.setCityHasShop(hasShop);

		if (intent != null)
			stopService(intent);

		handler.sendMessage(handler.obtainMessage(SHOW_CITY, city));

		new Thread(new Runnable() {

			@Override
			public void run() {
				Message msg = new Message();
				msg.what = BANNER;
				Display display = getWindowManager().getDefaultDisplay();
				int width = display.getWidth();
				int height = display.getHeight();
				String bannerSize = Utils
						.getSizesForBanner(new Pair<Integer, Integer>(
								width, height));
				int cityId = PreferencesManager.getCityid();
				String url = URLManager.getPromoNew(cityId, bannerSize);
				msg.obj = new BannersParser().parseDataNew(url);
				handler.sendMessage(msg);
			}
		}).start();

	}

	@Override
	public Object onRetainNonConfigurationInstance () {
		boolean isFirst = false;
		return isFirst;
	}

	@Override
	protected void onActivityResult (int requestCode, int resultCode, Intent data) {
		switch (resultCode) {
		case 0:
			switch (citysState) {
			case DISABLE_LOCATION:
				showDialogDisable();
				break;

			case ENABLE_LOCATION_CITY_NO_FIND:
				showDialogDisable();
				break;

			case ENABLE_LOCATION_NO_CITY:				
				showDialogNoCity();
				break;

			case ENABLE_LOCATION_CITY:				
				showDialogCity();
				break;

			default:
				break;
			}
			//showDialog();
			break;
		case 1:
			setCity(null, data.getStringExtra(CitiesListActivity.CITY), data.getIntExtra(CitiesListActivity.CITY_ID, 0),data.getBooleanExtra(CitiesListActivity.CITY_HAS_SHOP, false));
			break;
		default:
			break;
		}
		super.onActivityResult(requestCode, resultCode, data);
	}

	protected void onCreate (Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		EasyTracker.getInstance().setContext(this);
		isMyLoc=false; isGetCitys=false;isCityDone=false;
		// Thread.setDefaultUncaughtExceptionHandler(new
		// ExceptionCaughter("EnterErrors", null, null, this));//TODO
		context = this;

		mLayoutParams = new WindowManager.LayoutParams();
		mLayoutParams.height = WindowManager.LayoutParams.FILL_PARENT;
		mLayoutParams.width = WindowManager.LayoutParams.FILL_PARENT;
		mLayoutParams.flags = WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON;
		mLayoutParams.format = PixelFormat.TRANSLUCENT;
		mLayoutParams.windowAnimations = android.R.anim.fade_in;
		mLayoutParams.type = WindowManager.LayoutParams.TYPE_APPLICATION;

		mWindowManager = getWindowManager();

		/*
		 * GCMRegistrar.checkDevice(this); GCMRegistrar.checkManifest(this);
		 * 
		 * final String regId = GCMRegistrar.getRegistrationId(this);
		 * 
		 * if (regId.equals("")) { GCMRegistrar.register(this,
		 * GCMConfig.SENDER_ID); } else { Log.v("GCM", "Already registered: " +
		 * regId); }
		 */

		DisplayMetrics metrics = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(metrics);
		switch (metrics.densityDpi) {
		case DisplayMetrics.DENSITY_LOW:
		case DisplayMetrics.DENSITY_MEDIUM:
			setContentView(R.layout.mainmenu_mdpi);
			break;
		default:
			setContentView(R.layout.mainmenu);
			break;
		}

		initViews();
		manageViews();

		// если пришли из оформления заказа
		boolean fromOrder = getIntent().getBooleanExtra("fromOrder", false);
		if (fromOrder)
			return;

		data = getLastNonConfigurationInstance();

		if (data == null) {
			splash.setVisibility(View.VISIBLE);
			splash.setOnTouchListener(this);
			if(isTestLite) {
				labelTestLite.setVisibility(View.VISIBLE);
			} else {
				labelTestLite.setVisibility(View.GONE);
			}

			if(PreferencesManager.getCityName().equals("")) {
				showDialogStart();
			} else { 
				handler.sendEmptyMessageDelayed(REMOVE_SPLASH, SPLASH_TIME);
			}
		}		
	}

	@Override
	protected void onResume () {
		startRollAd();

		// onResume will fire only after choosing the city
		// if we want showFirstCouponDialog() to be opened only once and closed on user tape => move this call to handler.SHOW_CITY

//		if(!PreferencesManager.getCityName().equals("")){
//			// fire only if we successfully closed flow with choosing the city 
//			offerCouponForFirstLogin();
//		}
		super.onResume();
	}

	@Override
	protected void onPause () {
		stopRollAd();
		super.onPause();
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

	private void initViews () {
		titles = getResources().getStringArray(R.array.main_menu_titles);
		mainGrid = (GridView) findViewById(R.id.mainmenu_grid);
		adPager = (ViewPager) findViewById(R.id.mainmenu_ad_pager);
		splash = (ImageView) findViewById(R.id.splash);
		labelTestLite = (TextView) findViewById(R.id.splash_text_build);
		if(isTestLite){
			String text=getVersionTestLite(this,versionBuild);
			labelTestLite.setText(text);
		}
		dots = (Dots) findViewById(R.id.mainmenu_dots);
		ad_left = (ImageView) findViewById(R.id.mainmenu_ad_pager_left);
		ad_right = (ImageView) findViewById(R.id.mainmenu_ad_pager_right);
		mProgress = (FrameLayout) findViewById(R.id.mainmenu_frame_progress);
	}

//	public void offerCouponForFirstLogin(){
//		Log.d("NEW_COUPON", PreferencesManager.getCityName());
//
//		if(PreferencesManager.showGetCouponDialog()){
//			final String userName = PreferencesManager.getUserName();
//			if(TextUtils.isEmpty(userName)){
//				// user is not authorized
//				showNewCouponDialog(false);
//
//			} else {
//				// TODO check if user hasn't gotten this coupon yet. Issue blocked: no info about endpoint
//				showNewCouponDialog(true);
//
//			}
//		} else {
//			// user has already gotten this coupon or declined the offer
//			// NOP
//		}
//
//		//		if(calledAfterGettingCity) Log.d("NEW_COUPON", "calledAfterGettingCity");
//		// check if we need to show this dialog at all
//
//	}

//	private void showNewCouponDialog(final boolean authorizedUser){
//		String dialogMessage = null;
//		if(authorizedUser){
//			dialogMessage = "Получите купон!";
//		} else {
//			dialogMessage = "Залогинься - получи купон!";
//		}
//
//		AlertDialog.Builder dlg = new AlertDialog.Builder(context);
//
//		dlg.setMessage(dialogMessage).setCancelable(false)
//		.setPositiveButton("OK", new DialogInterface.OnClickListener() {
//			public void onClick (DialogInterface dialog, int whichButton) {
//				if(authorizedUser){
//					executeHttpNewCoupon();
//				} else {
//					Intent intent = new Intent().setClass(MainMenuActivity.this, AuthorizationActivity.class);
//					intent.putExtra(AuthorizationActivity.RUNTYPE, RunType.personal_account.name());
//					startActivity(intent);
//				}
//
//			}
//		}).setNegativeButton("Закрыть", new DialogInterface.OnClickListener() {
//
//			@Override
//			public void onClick (DialogInterface dialog, int which) {
//				PreferencesManager.showGetCouponDialog(false);
//				dialog.dismiss();
//			}
//		}).create().show();
//	}

	private void executeHttpNewCoupon(){
		// TODO	perform http request. Issue blocked: no info about endpoint
		showDialog();
		new Thread(new Runnable() {

			@Override
			public void run() {
				//				boolean response = new GetCouponParser(MainMenuActivity.this).parse(URLManager.getCoupon());

				// TODO check EventList from response to handle user coupon status
				// if needed. Get this info from product owner
				PreferencesManager.showGetCouponDialog(false);


				runOnUiThread(new Runnable() {
					public void run() {
						Toast.makeText(MainMenuActivity.this, "Поздравляем! Вы получили купон на заказ с мобильного приложения. Ваш купон был отправлен Вам на почту /в смс", Toast.LENGTH_SHORT).show();
						PreferencesManager.showGetCouponDialog(false);
						hideDialog();
					}
				});


			}
		}).start();
	}

	private void manageViews () {
		ad_left.setOnClickListener(this);
		ad_right.setOnClickListener(this);
		ad_left.setVisibility(View.GONE);
		ad_right.setVisibility(View.GONE);

		adAdapter = new AdAdapter(context, new OnClickListener() {

			@Override
			public void onClick (View v) {
				BannerBean bean = (BannerBean) v.getTag();
				FlurryAgent.logEvent(Constants.FLURRY_EVENT.Banner_Сlick.toString());
				EasyTracker.getTracker().sendEvent("promo/click", "buttonPress", bean.getName(), (long) bean.getId());
				if (bean.getProduct_ids() != null) {
					if (bean.getProduct_ids().size() > 1) {
						Intent intent = new Intent().setClass(context, BannerListActivity.class);
						intent.putExtra(BannerListActivity.IDs, bean.getProduct_ids());
						startActivity(intent);
					} else {
						Intent intent = new Intent().setClass(context, ProductCardActivity.class);
						intent.putExtra(ProductCardActivity.ID, bean.getProduct_ids().get(0));
						intent.putExtra(ProductCardActivity.NAME, bean.getName());
						intent.putExtra(ProductCardActivity.EXTRA_FROM, Constants.FLURRY_FROM.Banner.toString());
						startActivity(intent);
					}
				} else {
					Intent intent = new Intent().setClass(context, BannerWebActivity.class);
					intent.putExtra(BannerWebActivity.NAME, bean.getName());
					intent.putExtra(BannerWebActivity.URL, bean.getUrl());
					startActivity(intent);
				}				
			}
		});

		adPager.setAdapter(adAdapter);
		adPager.setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch (View v, MotionEvent event) {
				handler.removeMessages(0);
				handler.removeMessages(1);
				handler.sendEmptyMessageDelayed(0, AD_CHANGE_TIME);

				return false;
			}
		});

		adPager.setOnPageChangeListener(new OnPageChangeListener() {

			@Override
			public void onPageSelected (int position) {
				ad_current = position;

				if (ad_current == 0) {
					ad_current = NUM - 2;
					adPager.setCurrentItem(ad_current, false);
				} else if (ad_current == NUM - 1) {
					ad_current = 1;
					adPager.setCurrentItem(ad_current, false);
				} else {
					adPager.setCurrentItem(ad_current, true);
				}


				dots.setSelected(ad_current);
			}

			@Override
			public void onPageScrolled (int position, float positionOffset, int positionOffsetPixels) {}

			@Override
			public void onPageScrollStateChanged (int state) {}
		});

		if (!PreferencesManager.getCityName().equals("")) {

			new Thread(new Runnable() {

				@Override
				public void run() {
					Message msg = new Message();
					msg.what = BANNER;
					Display display = getWindowManager().getDefaultDisplay();
					int width = display.getWidth();
					int height = display.getHeight();
					String bannerSize = Utils
							.getSizesForBanner(new Pair<Integer, Integer>(
									width, height));
					int cityId = PreferencesManager.getCityid();
					String url = URLManager.getPromoNew(cityId, bannerSize);
					msg.obj = new BannersParser().parseDataNew(url);
					handler.sendMessage(msg);
				}
			}).start();
		}
		mainGrid.setAdapter(new MainMenuGridAdapter(context, titles));
		mainGrid.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick (AdapterView<?> parent, View view, int position, long id) {
				Intent intent = new Intent();
				intent.setClass(context, MainActivity.class);
				switch (position) {
				case CATALOG:
					intent.putExtra(MainActivity.MAINSECTION, MainSections.catalog.name());
					break;
				case SEARCH:
					intent.putExtra(MainActivity.MAINSECTION, MainSections.search.name());
					break;
				case FEED_BACK:
					// intent.putExtra(MainActivity.MAINSECTION,
					// MainSections.novelty.name());
					//showFeedBackDialog();
					intent.putExtra(MainActivity.MAINSECTION, MainSections.about.name());
					intent.putExtra(About.ACTIVITY_TYPE, About.CALLBACK_TYPE);
					break;
				case MY_CART:
					intent.putExtra(MainActivity.MAINSECTION, MainSections.basket.name());
					break;
				case MY_ACCOUNT:
					intent.putExtra(MainActivity.MAINSECTION, MainSections.personal_account.name());
					break;
				case SHOPS:
					intent.putExtra(MainActivity.MAINSECTION, MainSections.shops.name());
					break;
					//case SERVICES:
					//	intent.putExtra(MainActivity.MAINSECTION, MainSections.services.name());
					//	break;
				case SCANER:
					intent.setClass(context, CameraActivity.class);
					// intent.putExtra(MainActivity.MAINSECTION,
					// MainSections.qr_scanner.name());
					break;
				case ABOUT:
					intent.putExtra(MainActivity.MAINSECTION, MainSections.about.name());
					intent.putExtra(About.ACTIVITY_TYPE, About.ABOUT_TYPE);
					break;

				default:
					break;
				}
				//if (position != FEED_BACK)
				startActivity(intent);
			}
		});
		// DragAndDrop.create((FrameLayout)
		// findViewById(R.id.mainmenu_main_frame), context, a, mainGrid);

	}

	/*
	private void showFeedBackDialog(){
		AlertDialog.Builder dlg = new AlertDialog.Builder(context);		
		dlg.setIcon(android.R.drawable.ic_menu_more)
        .setTitle("Обратная связь")
        .setPositiveButton("Отправить сообщение", new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				context.startActivity(new Intent().setClass(context, FeedBackActivity.class));

			}
			})
		.setNegativeButton("Позвонить", new DialogInterface.OnClickListener() {

			@Override
			public void onClick (DialogInterface dialog, int which) {
				Uri uri = Uri.parse("tel:88007000009");
				Intent intent = new Intent(Intent.ACTION_VIEW, uri);
				context.startActivity(intent);

			}
		})
		.create();
		dlg.show();
	}
	 */

	/*
	private void showDialog () {
		AlertDialog.Builder dlg = new AlertDialog.Builder(context);
		dlg.setMessage("Выберите ваше местоположение").setCancelable(false)
				.setPositiveButton("Выбрать", new DialogInterface.OnClickListener() {
					public void onClick (DialogInterface dialog, int whichButton) {
						startActivityForResult(new Intent().setClass(context, CitiesListActivity.class), 0);
					}
				}).setNegativeButton("Выйти", new DialogInterface.OnClickListener() {

					@Override
					public void onClick (DialogInterface dialog, int which) {
						finish();
					}
				}).create().show();
	}
	 */


	@Override
	public boolean onTouch (View v, MotionEvent event) {
		switch (v.getId()) {
		case R.id.splash:
			return true;

		default:
			break;
		}
		return false;
	}

	@Override
	public void onClick (View v) {
		switch (v.getId()) {
		case R.id.mainmenu_ad_pager_left:
			handler.removeMessages(0);
			handler.removeMessages(1);
			handler.sendEmptyMessageDelayed(0, AD_CHANGE_TIME);
			adPager.setCurrentItem(ad_current - 1, true);
			break;

		case R.id.mainmenu_ad_pager_right:
			handler.removeMessages(0);
			handler.removeMessages(1);
			handler.sendEmptyMessageDelayed(0, AD_CHANGE_TIME);
			adPager.setCurrentItem(ad_current + 1, true);
			break;

		default:
			break;
		}
	}

	@Override
	public void onBackPressed () {
		//магазин до которого сузились в каталоге перетирается при выходе
		PreferencesManager.setUserCurrentShopId(0);
		PreferencesManager.setUserCurrentShopName("");//TODO
		PreferencesManager.setFirstStartCatalog(0);
		super.onBackPressed();
	}

	// @Override
	// protected void onDestroy() {
	// PersonData.getInstance().setPersonBean(null);//TODO
	// }
	//dialogs
	private void showDialogStart() {
		AlertDialog.Builder dlg = new AlertDialog.Builder(context);
		dlg.setMessage("Программа \"Enter\" хочет использовать ваше текущее местонахождение").setCancelable(false)
		.setPositiveButton("Запретить", new DialogInterface.OnClickListener() {
			public void onClick (DialogInterface dialog, int whichButton) {
				citysState=DISABLE_LOCATION;						
				handler.sendEmptyMessage(REMOVE_SPLASH);
				startActivityForResult(new Intent().setClass(context, CitiesListActivity.class), 0);
			}
		}).setNegativeButton("Разрешить", new DialogInterface.OnClickListener() {

			@Override
			public void onClick (DialogInterface dialog, int which) {						
				startLocation();
			}
		}).create().show();
	}

	private void showDialogDisable() {
		AlertDialog.Builder dlg = new AlertDialog.Builder(context);
		dlg.setMessage("Ваше местоположение не было выбрано.").setCancelable(false)
		.setPositiveButton("Выбрать город из списка", new DialogInterface.OnClickListener() {
			public void onClick (DialogInterface dialog, int whichButton) {						
				startActivityForResult(new Intent().setClass(context, CitiesListActivity.class), 0);
			}
		}).setNegativeButton("Выйти из приложения", new DialogInterface.OnClickListener() {

			@Override
			public void onClick (DialogInterface dialog, int which) {						
				finish();
			}
		}).create().show();
	}


	private void showDialogNoCitys () {
		AlertDialog.Builder dlg = new AlertDialog.Builder(context);
		dlg.setMessage("Внимание\nПроизошла ошибка при загрузке данных.").setCancelable(false)
		.setNeutralButton("Выйти из приложения", new DialogInterface.OnClickListener() {
			@Override
			public void onClick (DialogInterface dialog, int which) {
				finish();
			}
		}).create().show();
	}

	private void showDialogNoCity () {
		AlertDialog.Builder dlg = new AlertDialog.Builder(context);
		dlg.setMessage("Не удалось определить Ваше местоположение.").setCancelable(false)
		.setPositiveButton("Еще раз", new DialogInterface.OnClickListener() {
			public void onClick (DialogInterface dialog, int whichButton) {
				isMyLoc=false;
				mProgress.setVisibility(View.VISIBLE);
				startLocation();
			}
		}).setNegativeButton("Выбрать из списка", new DialogInterface.OnClickListener() {

			@Override
			public void onClick (DialogInterface dialog, int which) {
				startActivityForResult(new Intent().setClass(context, CitiesListActivity.class), 0);
			}
		}).create().show();
	}

	private void showDialogCity () {
		AlertDialog.Builder dlg = new AlertDialog.Builder(context);		
		dlg.setMessage("Ваш город "+cityName+"?").setCancelable(false)
		.setPositiveButton("Да", new DialogInterface.OnClickListener() {
			public void onClick (DialogInterface dialog, int whichButton) {
				findCityInCitys();
			}
		}).setNegativeButton("Выбрать город из списка", new DialogInterface.OnClickListener() {

			@Override
			public void onClick (DialogInterface dialog, int which) {
				startActivityForResult(new Intent().setClass(context, CitiesListActivity.class), 0);
			}
		}).create().show();
	}

	private void showDialogCityNoFind () {
		AlertDialog.Builder dlg = new AlertDialog.Builder(context);
		dlg.setMessage("Внимание\nВ данном городе отсутствуют магазины. Выберите другой город." ).setCancelable(false)
		.setNeutralButton("Выбрать город из списка", new DialogInterface.OnClickListener() {
			public void onClick (DialogInterface dialog, int whichButton) {
				startActivityForResult(new Intent().setClass(context, CitiesListActivity.class), 0);
			}
		}).create().show();
	}

	//sorting
	private void findCityInCitys()	{
		int i=0;
		CitiesBean bean=null;
		for(i=0;i<mCities.size();i++){
			bean=mCities.get(i);
			if(cityName.equals(bean.getName()))				
				break;			
		}
		if(i<mCities.size())
			setCity(null, bean.getName(), bean.getId(), bean.isHasShop());
		else {
			citysState=ENABLE_LOCATION_CITY_NO_FIND;
			showDialogCityNoFind();
		}
	}

	//For location user
	private void startLocation () {
		if (!isMyLoc) {
			cityName="";
			mLocationListener.alreadyStart = false;
			mManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);

			if(!mManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
				showDialogDisable();
			}
			else
			{
				mManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0,	mLocationListener);
				mManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, mLocationListener);
			}
		}
		if (!isGetCitys) {
			mLoader = new GetCityLoader();
			mLoader.execute();
		}
		mHandler.sendEmptyMessageDelayed(MSG_ID, TIMEOUT);
	}

	private class LocListener implements LocationListener{

		public boolean alreadyStart;

		@Override
		public void onLocationChanged(Location location) {
			if(alreadyStart)
				return;
			alreadyStart = true;
			cityName="";
			mMyLocation = location;
			mManager.removeUpdates(mLocationListener);
			Geocoder gc= new Geocoder(context);
			List<Address> list=null;
			Address adr=null; 
			//
			try {
				list = gc.getFromLocation(mMyLocation.getLatitude(),  mMyLocation.getLongitude(), 1);
				if(!Utils.isEmptyList(list)){
					adr=list.get(0);
					cityName=adr.getLocality();
					cityName=(cityName==null?"":cityName);
				}

			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			isMyLoc=true;
			myLocationIsDone();
		}

		@Override
		public void onStatusChanged(String provider, int status, Bundle extras) {
		}

		@Override
		public void onProviderEnabled(String provider) {
		}

		@Override
		public void onProviderDisabled(String provider) {
		}

	}

	private LocListener mLocationListener = new LocListener();	

	//asynk get city
	private class GetCityLoader extends AsyncTask<Void, Void, ArrayList<CitiesBean>> {

		@Override
		protected ArrayList<CitiesBean> doInBackground (Void... params) {
			CitiesParser parser = new CitiesParser(URLManager.getCities());
			return parser.parse();
		}

		@Override
		protected void onPostExecute (ArrayList<CitiesBean> result) {
			mCities=result;
			isGetCitys=true;
			myLocationIsDone();
		}
	}

	private void myLocationIsDone(){
		if(isGetCitys&&isMyLoc){
			mHandler.removeMessages(MSG_ID);
			if(Utils.isEmptyList(mCities)) citysState=ENABLE_LOCATION_NO_CITYS;
			else if(cityName.equals("")) citysState=ENABLE_LOCATION_NO_CITY;
			else citysState=ENABLE_LOCATION_CITY;
			if(!isCityDone){
				handler.sendEmptyMessage(REMOVE_SPLASH);
			} else{ 
				mProgress.setVisibility(View.GONE);
				handler.sendEmptyMessage(GET_CITY);
			}
			isCityDone=true;
		}
	}
	// для таймаута, если не успел определить или загрузить
	private static final int TIMEOUT = 10000;
	private static final int MSG_ID = 0;
	private Handler mHandler = new Handler() {
		public void handleMessage (Message msg) {
			mManager.removeUpdates(mLocationListener);			
			if(mLoader!=null) mLoader.cancel(false);
			if(Utils.isEmptyList(mCities)) {
				citysState=ENABLE_LOCATION_NO_CITYS;				
			} else {
				citysState=ENABLE_LOCATION_NO_CITY;
			}
			if(!isCityDone){
				handler.sendEmptyMessage(REMOVE_SPLASH);
			} else{ 
				mProgress.setVisibility(View.GONE);
				handler.sendEmptyMessage(GET_CITY);
			}
			isCityDone=true;
		}

	};


	private static String getVersionTestLite(Context context,int version) {

		String versionName = null, result;
		try {
			versionName = context.getPackageManager().getPackageInfo(context.getPackageName(), 0).versionName;
		}
		catch (final NameNotFoundException e) {            
		}
		if (versionName == null) {
			versionName = "unknown";
		}

		result = "version "+versionName+" , build "+Integer.toString(version);

		return result;
	}

	private void showDialog(){
		Context context = MainMenuActivity.this;
		mProgressFrameLayout = new FrameLayout(context);
		mProgressFrameLayout.setLayoutParams(new FrameLayout.LayoutParams(FrameLayout.LayoutParams.FILL_PARENT, FrameLayout.LayoutParams.FILL_PARENT));
		mProgressFrameLayout.setBackgroundColor(Color.argb(50, 10, 10, 10));
		FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		params.gravity = Gravity.CENTER;
		ProgressBar bar = new ProgressBar(context);
		bar.setLayoutParams(params);
		bar.setIndeterminateDrawable(getResources().getDrawable(R.drawable.progress_spin));
		mProgressFrameLayout.addView(bar);
		mProgressFrameLayout.setClickable(true);
		mWindowManager.addView(mProgressFrameLayout, mLayoutParams);
	}

	private void hideDialog(){
		try{
			mWindowManager.removeView(mProgressFrameLayout);
		}catch (IllegalArgumentException e) {}
	}


}
