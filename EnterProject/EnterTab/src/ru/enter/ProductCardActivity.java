package ru.enter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import net.londatiga.android.QuickAction;
import ru.enter.DataManagement.BasketManager;
import ru.enter.adapters.ProductCardModelAdapter;
import ru.enter.base.BaseActivity;
import ru.enter.beans.DeliveryBean;
import ru.enter.beans.ModelProductBean;
import ru.enter.beans.ProductBean;
import ru.enter.beans.ProductModelBean;
import ru.enter.beans.ShopBean;
import ru.enter.dialogs.ProgressDialogFragment;
import ru.enter.dialogs.alert.BasketAddDialogFragment;
import ru.enter.dialogs.alert.ProductCardErrorDialogFragment;
import ru.enter.parsers.ProductInfoParser;
import ru.enter.utils.Constants;
import ru.enter.utils.Discount;
import ru.enter.utils.Formatters;
import ru.enter.utils.ImageSizesEnum;
import ru.enter.utils.NetworkManager;
import ru.enter.utils.PreferencesManager;
import ru.enter.utils.ShopLocator;
import ru.enter.utils.TypefaceUtils;
import ru.enter.utils.URLManager;
import ru.enter.utils.Utils;
import ru.enter.utils.ShopLocator.OnNearestShopLocateListener;
import ru.enter.widgets.MyModelTextView;
import ru.ideast.SocialServices.OAuthHelp;
import ru.ideast.SocialServices.SendTwitter;
import ru.ideast.SocialServices.VkApp;
import ru.ideast.SocialServices.VkApp.VkDialogListener;
import ru.ideast.shopitemfragment.ItemGalleryAdapter;
import ru.ideast.shopitemfragment.ScrollListener;
import ru.ideast.shopitemfragment.tabs.AccessoriesListTabActivity;
import ru.ideast.shopitemfragment.tabs.BaseTabActivity;
import ru.ideast.shopitemfragment.tabs.ProductDescriptionActivity;
import ru.ideast.shopitemfragment.tabs.ProductOptionsActivity;
import ru.ideast.shopitemfragment.tabs.ProductServicesActivity;
import ru.ideast.shopitemfragment.tabs.ProductShopsActivity;
import ru.ideast.shopitemfragment.tabs.RelativeGoodsListTabActivity;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import android.app.ActionBar;
import android.app.ActionBar.Tab;
import android.app.LocalActivityManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.text.Html;
import android.text.Spanned;
import android.text.TextUtils;
import android.util.Log;
import android.util.TypedValue;
import android.view.Display;
import android.view.GestureDetector;
import android.view.GestureDetector.OnGestureListener;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TabHost;
import android.widget.TabHost.OnTabChangeListener;
import android.widget.TabHost.TabSpec;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.android.DialogError;
import com.facebook.android.Facebook;
import com.facebook.android.Facebook.DialogListener;
import com.facebook.android.FacebookError;
import com.flurry.android.FlurryAgent;
import com.google.analytics.tracking.android.EasyTracker;
import com.webimageloader.ImageLoader;
import com.webimageloader.ext.ImageHelper;

public class ProductCardActivity extends BaseActivity implements OnClickListener {
	private final String TAG = ProductCardActivity.class.getName(); // DEBUG TAG
	public static final String EXTRA_FROM = "extra_from";

	// переключатель: скролить слайдер или контент таба.
	private Boolean SLIDER_ON = true;

	public static final String EXTRA_PRODUCT_CATEGORY = "productCategory";
	public static final String REFRESH_TAB_TAG = "ru.enter.slider.DATA_REFRESH";
	private CardLoader mProductCardLoader;

	private static int currentTab = -1; // for remembering current tab
	private static final float MAXIMUM_MAJOR_VELOCITY = 300.0f;
	private static final float MAXIMUM_ACCELERATION = 2000.0f;
	private static final int VELOCITY_UNITS = 1000;
	private static final int MSG_ANIMATE = 2106754281;
	private static final int ANIMATION_FRAME_DURATION = 1000 / 60;

	private static final String PRODUCT_TITLE = "product_title";
	private static final String PRODUCT_NAME = "product_name";
	private static final String PRODUCT_PRICE = "product_price";
	private static final String PRODUCT_IMAGES = "product_images";
	private static final String PRODUCT_BUYABLE = "product_buyable";
	private static final String SHOP_ID = "shop_id";

	private static final String PRODUCT_BEAN = "product_bean";

	public static final String PRODUCT_ID = "productid";

	private final Handler mHandler = new SlidingHandler();

	/* animation parameters */
	private VelocityTracker mVelocityTracker;
	private long mAnimationLastTime;
	private long mCurrentAnimationTime;
	private float mAnimationPosition;
	private float mAnimatedVelocity;
	private int mMaximumMajorVelocity;
	private int mMaximumAcceleration;
	private int mInitTop, startYPos, mTouchStart;
	private boolean isTracking, mAnimating;
	private int mStartTrackingPoint, mTrackingPoint;

	/* UI */
	private ViewPager mItemGallery; // gallery
	private Button mBtnPrev, mBtnNext; // gallery controls
	private ImageButton mButton360;
	private ImageButton mButtonGallery;
	public FrameLayout emptyspace;
	// slider
	private ImageButton mHandle;
	private LinearLayout mSliderContent; // содержит описание и кнопку хендла

	private RelativeLayout mRootContainer; // корневой контейнер для галереи
	private View mRoot;
	private RelativeLayout mGalleryContainer; // содержит галерею, кнопки назад,
	// вперёд
	private RelativeLayout mHeaderLayout; // description header (above tabs)

	/* SLIDER CONTENT UI */
	private TextView mTvArticle;
	private TextView mTvAviliable;
	private RatingBar mRbRating;
	private TextView mTvPrice;
	private TextView mTvOldPrice;
	private TextView mTvPriceRuble;
	private TextView mTvOldPriceRuble;
	private TextView mDiscount;
	private TextView mTvName;
	private TextView mTvDeliveryInfo;
	private TextView mDeliveryTitle;
	private Button mBBuy;
	private Button mBBuyNow;
	private Button mBShare;
	private ImageButton mIbFacebook;
	private ImageButton mIbVkontakte;
	private ImageButton mIbTwitter;
	private TabHost mThInfo;
	private ImageView mLable;
	private TextView mBuyNowLabel;
	private ProgressBar mBuyNowProgress;

	/* For Models  */
	private LinearLayout mModelList;
	private TextView mVarianModel;
	private Typeface tfNormal;
	//private QuickAction mQuickAction;

	// position of the half_opened/ half_closed slider
	private int mMiddlePos = 500;
	/* флаг, можно ли поднимать слайдер выше половины экрана */
	private Boolean isLiftable = false;
	/* скролировался ли контент в табе */
	private Boolean scrolled = false;
	/* срабатывал ли ACTION_MOVE */
	private Boolean moved = false;
	private int statusBarHeight;

	private LocalActivityManager mLocalActivityManager;
	private STATE state = STATE.HALF_CLOSED;

	private boolean mFirstTouch = true;

	/* SHARING */
	// FaceBook
	private String FB_ID = "262075720509710";
	private Facebook fb = new Facebook(FB_ID);
	// Twitter
	private Twitter twitterConnection = null;
	private OAuthHelp oHelper = null;
	private ProgressDialog mProgress;
	// extras
	public static final String EX_URL = "EX_URL";
	public static final String EX_TITLE = "EX_TITLE";
	public static final String EX_IMAGE = "EX_IMAGE";
	public static final String EX_DESC = "EX_DESC";
	public static final String EX_PRICE = "EX_PRICE";

	private int mId;
	public ProductBean mProductBean;
	private String mFrom;
	/*****/

	/* state of the slider */
	enum STATE {
		/* слайдер закрыт, хендл внизу экрана, видна галерея */
		CLOSED,
		/* выдвинут на половину, видно описание товара (артикул, купить) */
		HALF_CLOSED,
		/* тоже самое */
		HALF_OPENED,
		/* полностью открыт на весь экран, хендл вверху */
		OPENED,
		/* табы видны полностью, хендл за верхним краем экрана */
		FULL_DESCRIPTION
	}

	private void getStatusBarH () {
		Rect rectgle = new Rect();
		Window window = getWindow();
		window.getDecorView().getWindowVisibleDisplayFrame(rectgle);
		statusBarHeight = rectgle.top;
		int contentViewTop = window.findViewById(Window.ID_ANDROID_CONTENT).getTop();
		int titleBarHeight = contentViewTop - statusBarHeight;

		Log.i("getStatusBarH", "StatusBar Height= " + statusBarHeight + " , TitleBar Height = " + titleBarHeight);

	}

	private OnGlobalLayoutListener rootLayoutListener = new OnGlobalLayoutListener() {
		public void onGlobalLayout () {
			// Log.d(TAG,"rootLayoutListener");
			getStatusBarH();
			mRootContainer.getViewTreeObserver().removeGlobalOnLayoutListener(this);
			mHeaderLayout = (RelativeLayout) findViewById(R.id.headerLayout);

			int galleryHeight = mGalleryContainer.getBottom();
			emptyspace.getLayoutParams().height = mGalleryContainer.getHeight();
			mInitTop = galleryHeight - mHandle.getHeight();
			// Log.d("=SLIDER=","mHandle.getHeight() = " +
			// String.valueOf(mHandle.getHeight()));
			mItemGallery.getLayoutParams().height = galleryHeight;

			mThInfo.setOnTabChangedListener(new OnTabChangeListener() {
				@Override
				public void onTabChanged (String tabId) {
					setListenersToTab(tabId);

				}
			});
			if (mProductBean != null) {
				fillInfo(mProductBean);
			}
		}
	};

	// this listener fires when the layout is constructed
	private OnGlobalLayoutListener mItemHeaderListener = new OnGlobalLayoutListener() {
		public void onGlobalLayout () {
			Log.d(TAG, "mItemHeaderListener");
			// mHeaderLayout.getViewTreeObserver().removeGlobalOnLayoutListener(this);
			int fullHeight = mItemGallery.getLayoutParams().height * 2 + mHeaderLayout.getHeight() + 15;
			mRoot.getLayoutParams().height = fullHeight;

			mRootContainer.getLayoutParams().height = fullHeight + 15;
			mMiddlePos = mThInfo.getTabWidget().getHeight() + mHeaderLayout.getBottom() - mHandle.getBottom();
			setSliderState(state);
		}
	};

	public boolean dispatchTouchEvent (MotionEvent event) {

		int location_top[] = { 0, 0 };
		int location_bot[] = { 0, 0 };
		mHandle.getLocationOnScreen(location_top);
		mThInfo.getTabWidget().getLocationOnScreen(location_bot);
		location_bot[1] += mThInfo.getTabWidget().getHeight();

		int action = event.getAction();

		switch (action) {
		case MotionEvent.ACTION_DOWN:

			isTracking = true;
			mTouchStart = startYPos = (int) event.getY();
			moved = false;
			break;
		case MotionEvent.ACTION_MOVE:

			// перелючатель листания контента в положении FULL_DESCRIPTION
			if (state == STATE.FULL_DESCRIPTION) {
				// Если листаем в низ
				if (mTouchStart < (int) event.getY()) {
					if (!scrolled)
						SLIDER_ON = true;
				} else {
					scrolled = true;
				}

			}
			// не обрабатываем есль тапнули выше хендла
			if (startYPos < location_top[1])
				return super.dispatchTouchEvent(event);

			// если слайден выключен и тапаем по контенту, то проборасывать
			// эвент в контент
			if (!SLIDER_ON && startYPos > location_bot[1])
				return super.dispatchTouchEvent(event);
			// если тапнули, но с небольшим сдвигом, но листать не хотели
			if (state != STATE.FULL_DESCRIPTION && Math.abs((int) event.getY() - startYPos) < 4)
				break;

			// если нет табов, то запретить открывать слайдер выше половины
			if (!isLiftable && ((int) event.getY() - startYPos) < 0 && state != STATE.CLOSED) {
				break;
			}
			moveHandle((int) event.getY());
			moved = true;
			// mTrackingPoint = (int) event.getY();
			break;
		case MotionEvent.ACTION_CANCEL:
			break;
		case MotionEvent.ACTION_UP:
			// не обрабатываем есль тапнули выше хендла
			if (startYPos < location_top[1])
				return super.dispatchTouchEvent(event);
			// если слайден выключен и тапаем по контенту, то проборасывать
			// эвент в контент
			if (!SLIDER_ON && startYPos > location_bot[1])
				return super.dispatchTouchEvent(event);
			// если тапнули, но с небольшим сдвигом, но листать не хотели
			if (Math.abs((int) event.getY() - mTouchStart) < 4)
				break;
			// если нет табов, то запретить открывать слайдер выше половины
			if (!isLiftable && ((int) event.getY() - startYPos) < 0 && state != STATE.CLOSED) {
				break;
			}

			if (SLIDER_ON) {
				// Log.d("ACTION_UP", "SLIDER_ON");
				chooseDirection(mTouchStart, (int) event.getY());
			}

			break;
		}

		if (SLIDER_ON && startYPos > location_bot[1]) {
			// Log.d("DROP!!", "EVENT="+event.getAction());
			// для проброса кликов на кнопки в купить и купить в 1 клик в табах
			if (!moved) {
				// Log.d("DROP", "Op!");
				return super.dispatchTouchEvent(event);
			} else {
				// прокидывает CANCEL, что бы состояние кнопки "В корзину"
				// вернулось в ненажатое
				event.setAction(MotionEvent.ACTION_CANCEL);
				return super.dispatchTouchEvent(event);
			}

		}
		return super.dispatchTouchEvent(event);

	};

	private void chooseDirection (int position, int target) {
		Log.d("chooseDirection", "position=" + position + " target=" + target);
		if (position < target && isTracking) {

			animateDown();
			return;
		}

		if (position > target && isTracking) {
			animateUp();
			return;
		}
	}

	@SuppressWarnings ("deprecation")
	@Override
	protected void onCreate (Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		EasyTracker.getInstance().setContext(this);
		setContentView(R.layout.product_card_ac_f);
		mRoot = (RelativeLayout) findViewById(R.id.root);
		currentTab = -1;
		// actionbar section
		ActionBar actionBar = getActionBar();
		LayoutInflater inflater = getLayoutInflater();
		View customActionBarView = (View) inflater.inflate(R.layout.actionbar_custom_product, null);
		ActionBar.LayoutParams params = new ActionBar.LayoutParams(ActionBar.LayoutParams.MATCH_PARENT,
				ActionBar.LayoutParams.MATCH_PARENT, Gravity.CENTER);
		actionBar.setCustomView(customActionBarView, params);

		// Log.i("ZNLog", "onCreate");

		Bundle extras = getIntent().getExtras();
		if (extras != null && extras.containsKey(PRODUCT_ID)) {
			mId = extras.getInt(PRODUCT_ID);
			//mId = 93963;
		}

		if (extras != null && extras.containsKey("barcode_bean")) {
			mProductBean = (ProductBean) extras.getSerializable("barcode_bean");
		}
		tfNormal = TypefaceUtils.getNormalTypeface();

		mLocalActivityManager = new LocalActivityManager(this, false);
		setupUI();
		mLocalActivityManager.dispatchCreate(savedInstanceState); // note
		// for twitter
		mProgress = new ProgressDialog(this);
		mProgress.setMessage("Подождите...");
		twitterConnection = new TwitterFactory().getInstance();
		oHelper = new OAuthHelp(this);
		
		if(getIntent().getExtras().containsKey(EXTRA_FROM)){
			mFrom = getIntent().getExtras().getString(EXTRA_FROM);
		} else {
			mFrom = Constants.FLURRY_FROM.Catalog.toString();
		}
	}

	@Override
	protected void onStart() {
		super.onStart();
		FlurryAgent.onStartSession(this, "3X5JRBVFV7QWXNP3Q8H2");
		
		if(mFrom != null){
			Map<String, String> flurryParam = new HashMap<String, String>();
			flurryParam.put(Constants.FLURRY_EVENT_PARAM.From.toString(), mFrom);
			FlurryAgent.logEvent(Constants.FLURRY_EVENT.Product_Screen.toString(), flurryParam);
			mFrom = null;
		}
	}

	@Override
	protected void onStop() {
		super.onStop();		
		FlurryAgent.onEndSession(this);
	}

	@Override
	protected void onDestroy () {
		// Log.i("ProductCard","onDestroy");
		mHeaderLayout.getViewTreeObserver().removeGlobalOnLayoutListener(mItemHeaderListener);
		// clear viewPager from memory
		mGalleryContainer.removeAllViews();
		mGalleryContainer = null;
		mItemGallery.removeAllViews();
		mItemGallery = null;
		mLocalActivityManager.dispatchDestroy(true);
		super.onDestroy();
	}

	@Override
	protected void onSaveInstanceState (Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putSerializable("pbean", mProductBean);
		outState.putSerializable("state", state);
		outState.putInt("current_tab", currentTab);
	}

	@Override
	protected void onRestoreInstanceState (Bundle savedInstanceState) {
		super.onRestoreInstanceState(savedInstanceState);
		if (savedInstanceState != null) {
			state = (STATE) savedInstanceState.getSerializable("state");
			currentTab = savedInstanceState.getInt("current_tab", -1);
			mProductBean = (ProductBean) savedInstanceState.getSerializable("pbean");

		}
	}

	@Override
	protected void onActivityResult (int requestCode, int resultCode, Intent data) {
		// if(requestCode == 10) finish();
		// if(requestCode == 15) finish();
		fb.authorizeCallback(requestCode, resultCode, data);
		super.onActivityResult(requestCode, resultCode, data);
	}

	@Override
	public void onPause () {
		if (mProductCardLoader != null)
			mProductCardLoader.cancel(true);

		currentTab = mThInfo.getCurrentTab();
		if (mVelocityTracker != null)
			mVelocityTracker.recycle(); // TODO нет проверки, если зашли и
		// вышли, не двигая скролл
		mLocalActivityManager.dispatchPause(true);
		super.onPause();
	}

	@Override
	public void onResume () {
		if (mProductBean == null) {
			if (mProductCardLoader != null)
				mProductCardLoader.cancel(true);
			mProductCardLoader = new CardLoader();
			mProductCardLoader.execute();
		}
		mLocalActivityManager.dispatchResume();
		super.onResume();
	}

	private void createTab (int lableId, Class<?> className, Bundle extra) {
		isLiftable = true;
		String header = getText(lableId).toString();
		TabSpec tabSpec = mThInfo.newTabSpec(header);

		Button tab = (Button) LayoutInflater.from(this).inflate(R.layout.product_card_tab_indicator, null);
		tab.setText(header);

		tabSpec.setIndicator(tab);
		Intent intent = new Intent(this, className);
		if (extra != null)
			intent.putExtras(extra);
		tabSpec.setContent(intent);
		mThInfo.addTab(tabSpec);

		// and on click listeners for open slider on tab click
		final int childs = mThInfo.getTabWidget().getChildCount();

		if (childs > 0) {
			mThInfo.getTabWidget().getChildAt(childs - 1).setOnClickListener(new OnClickListener() {
				@Override
				public void onClick (View v) {
					mAnimating = false;
					mThInfo.setCurrentTab(childs - 1);
					if (state == STATE.CLOSED || state == STATE.HALF_CLOSED || state == STATE.HALF_OPENED) {
						Log.d("mThInfo", "OnClickListener animateUp ");
						animateUp();
					}

					if (state == STATE.FULL_DESCRIPTION) {
						SLIDER_ON = false;
					}
					scrolled = false;
					reloadTabs();
				}
			});
		}
	}

	private void setupUI () {
		mThInfo = (TabHost) findViewById(android.R.id.tabhost);
		mThInfo.setup(mLocalActivityManager);
		mThInfo.getTabWidget().setStripEnabled(false);
		mThInfo.getTabWidget().setDividerDrawable(R.drawable.product_card_tab_widget_empty_divider);

		// mVelocityTracker = VelocityTracker.obtain();
		final float density = getResources().getDisplayMetrics().density;
		mMaximumMajorVelocity = (int) (MAXIMUM_MAJOR_VELOCITY * density + 0.5f);
		mMaximumAcceleration = (int) (MAXIMUM_ACCELERATION * density + 0.5f);

		/* =============== ShopLocator =============== */

		RelativeLayout header = (RelativeLayout) findViewById(R.id.catalog_ac_relative_header);
		header.setVisibility(View.VISIBLE);
		LinearLayout sortLinear = (LinearLayout) findViewById(R.id.shop_locator_widget_sorts);
		sortLinear.setVisibility(View.GONE);
		TextView shop = (TextView) findViewById(R.id.catalog_ac_txt_shop);
		ImageButton shop_btn = (ImageButton) findViewById(R.id.catalog_ac_btn_location);
		shop_btn.setVisibility(View.GONE);
		if (PreferencesManager.getUserCurrentShopId() == 0){
			header.setVisibility(View.GONE);			
		}else{
			shop.setText(PreferencesManager.getUserCurrentShopName());
		}

		/* =============== GALLERY =================== */
		mBtnPrev = (Button) findViewById(R.id.btnImagePrev);
		mBtnPrev.setOnClickListener(this);
		mBtnNext = (Button) findViewById(R.id.btnImageNext);
		mBtnNext.setOnClickListener(this);
		mButton360 = (ImageButton) findViewById(R.id.product_card_ac_img_360);
		mButton360.setOnClickListener(this);
		mButtonGallery = (ImageButton) findViewById(R.id.product_card_ac_img_gallery);
		mButtonGallery.setOnClickListener(this);
		mItemGallery = (ViewPager) findViewById(R.id.itemGallery);

		mGalleryContainer = (RelativeLayout) findViewById(R.id.galleryContainer);
		mGalleryContainer.setDrawingCacheEnabled(true);

		/* ================ SLIDER ================ */
		emptyspace = (FrameLayout) findViewById(R.id.emptyspace);
		mSliderContent = (LinearLayout) findViewById(R.id.sliderContent);

		mHandle = (ImageButton) findViewById(R.id.sliderHandle);
		// mHandle.setOnTouchListener(mHandleTouchListener);
		mHandle.setOnClickListener(this);

		mHeaderLayout = (RelativeLayout) mSliderContent.findViewById(R.id.headerLayout);
		mHeaderLayout.getViewTreeObserver().addOnGlobalLayoutListener(mItemHeaderListener);
		/* ================ ROOT ================= */
		mRootContainer = (RelativeLayout) mRoot.findViewById(R.id.rootContainer);
		mRootContainer.getViewTreeObserver().addOnGlobalLayoutListener(rootLayoutListener);

		/**/
		mTvArticle = (TextView) mSliderContent.findViewById(R.id.product_card_ac_text_article_number);
		mTvAviliable = (TextView) mSliderContent.findViewById(R.id.product_card_ac_text_is_buyable);
		mRbRating = (RatingBar) mSliderContent.findViewById(R.id.product_card_ac_rating_bar);
		mTvPrice = (TextView) mSliderContent.findViewById(R.id.product_card_ac_text_current_price);
		mTvPriceRuble = (TextView) mSliderContent.findViewById(R.id.product_card_ac_text_current_price_ruble);
		mTvOldPrice = (TextView) mSliderContent.findViewById(R.id.product_card_ac_text_old_price);
		mTvOldPriceRuble = (TextView) mSliderContent.findViewById(R.id.product_card_ac_text_old_price_ruble);
		mDiscount = (TextView) mSliderContent.findViewById(R.id.product_card_ac_text_discount);
		mModelList = (LinearLayout) mSliderContent.findViewById(R.id.product_card_model_list);
		mVarianModel = (TextView) mSliderContent.findViewById(R.id.product_card_model_list_variant);
		mVarianModel.setTypeface(tfNormal);
		mTvName = (TextView) mSliderContent.findViewById(R.id.product_card_ac_text_name);
		mDeliveryTitle = (TextView) mSliderContent.findViewById(R.id.product_card_ac_text_delivery_title);
		mTvDeliveryInfo = (TextView) mSliderContent.findViewById(R.id.product_card_ac_text_delivery);
		mBBuy = (Button) mSliderContent.findViewById(R.id.product_card_ac_button_buy);
		mBBuyNow = (Button) mSliderContent.findViewById(R.id.product_card_ac_button_buy_now);
		mLable = (ImageView) findViewById(R.id.product_card_ac_lable);

		mBShare = (Button) mSliderContent.findViewById(R.id.product_card_ac_button_share);
		mBShare.setOnTouchListener(null); // disable dragging by handler
		// mBShare.setOnClickListener(this);
		mIbFacebook = (ImageButton) mSliderContent.findViewById(R.id.product_card_share_fb);
		mIbFacebook.setOnClickListener(this);
		mIbVkontakte = (ImageButton) mSliderContent.findViewById(R.id.product_card_share_vk);
		mIbVkontakte.setOnClickListener(this);
		mIbTwitter = (ImageButton) mSliderContent.findViewById(R.id.product_card_share_tw);
		mIbTwitter.setOnClickListener(this);

		mBuyNowLabel = (TextView) findViewById(R.id.product_card_progress_layout_text);
		mBuyNowProgress = (ProgressBar) findViewById(R.id.product_card_progress_layout_bar);

	}

	/**
	 * Set/update listeners of the current tab. Each inner activty must provide
	 * a way to notify about scrolling.
	 */
	private void setListenersToTab (String tabId) {
		BaseTabActivity ta = (BaseTabActivity) mLocalActivityManager.getActivity(tabId);
		ta.setScrollListener(new ScrollListener() {
			@Override
			public void onScrolledUp () {
				// switchFullDescriptionMode();
				// ===== switchFullDescriptionModeOFF();
				// Log.i("SHOPITEM","onScrolledUp");

			}

			@Override
			public void onScrolledDown () {
				// switchFullDescriptionMode();
				// ===== switchFullDescriptionModeON();
				// Log.i("SHOPITEM","onScrolledDown");
			}

			@Override
			public void onSliderOn () {
				SLIDER_ON = true;
				Log.d("TOUCH_EVENT_NAYSU", "SLIDER ON");

			}
		});
	}

	/**
	 * Calculates new position for animation.
	 */
	private void calculateNewPosition () {
		// Log.e("===JOB==", "calculateNewPosition");
		int position = mRootContainer.getScrollY();
		float v = mAnimatedVelocity;
		long now = SystemClock.uptimeMillis();
		float t = (now - mAnimationLastTime) / 1000.0f; // ms -> s
		mAnimationLastTime = now;
		int a = 0;
		if (mTrackingPoint > mStartTrackingPoint) { // moving down
			a = mMaximumAcceleration;
		} else if (mTrackingPoint < mStartTrackingPoint) { // moving up
			a = -mMaximumAcceleration;
		}

		mAnimationPosition = position + (v * t) + (0.5f * a * t * t); // px
		mAnimatedVelocity = v + (a * t); // px/s

	}

	/**
	 * Animates a slider.
	 */
	private void doAnimation () {
		if (mAnimating) {
			calculateNewPosition();
			float delta = mAnimationPosition - mRootContainer.getScrollY();

			moveHandleAnimate((int) delta);
			mCurrentAnimationTime += ANIMATION_FRAME_DURATION;
			mHandler.sendMessageAtTime(mHandler.obtainMessage(MSG_ANIMATE), mCurrentAnimationTime);
		}

	}

	/**
	 * Moves Handle between 4 positions: closed -> half_opened -> opened ->
	 * half_closed -> closed
	 * 
	 * @param d
	 *            tracking path length
	 */
	private void moveHandleAnimate (int d) {
		// max - top, 0 - bottom

		if (isTracking || mAnimating) {
			int delta = -d;
			int currentPos = mRootContainer.getScrollY() + delta;
			// Log.e("moveHandle", "currentPos="+currentPos);

			if (currentPos <= 0) {
				// Log.e("MOVE_HANDLE_A", "SetTo CLOSED");
				setSliderState(STATE.CLOSED);
				isTracking = false;
				mAnimating = false;
				return;
			}

			if (state == STATE.CLOSED && (currentPos >= mMiddlePos)) {
				// Log.e("MOVE_HANDLE_A", "SetTo HALF_CLOSED");
				setSliderState(STATE.HALF_CLOSED);
				isTracking = false;
				mAnimating = false;
				return;
			}

			if ((state == STATE.HALF_CLOSED || state == STATE.HALF_OPENED) && (currentPos >= mInitTop)) {
				// Log.e("MOVE_HANDLE_A", "SetTo OPENED");
				setSliderState(STATE.OPENED);
				scrolled = false;
				isTracking = false;
				mAnimating = false;
				return;
			}

			if (state == STATE.OPENED && (currentPos >= getOverTopPos())) {
				// Log.e("MOVE_HANDLE_A", "SetTo FULL");
				setSliderState(STATE.FULL_DESCRIPTION);
				SLIDER_ON = false;
				isTracking = false;
				mAnimating = false;
				return;
			}

			if (state == STATE.FULL_DESCRIPTION && (currentPos <= mInitTop)) {
				// Log.e("MOVE_HANDLE_A", "SetTo OPENED");
				setSliderState(STATE.OPENED);
				isTracking = false;
				mAnimating = false;
				return;
			}

			if (state == STATE.OPENED && (currentPos <= mMiddlePos)) {
				// Log.e("MOVE_HANDLE_A", "SetTo HALF_OPENED");
				setSliderState(STATE.HALF_OPENED);
				isTracking = false;
				mAnimating = false;
				return;
			}

			// default
			if (currentPos < getOverTopPos()) {
				// Log.e("MOVE_HANDLE_A", "default");
				mRootContainer.scrollBy(mRootContainer.getLeft(), delta);

			}

		}
	}

	private void reloadTabs () {
		Intent i = new Intent(REFRESH_TAB_TAG);
		sendBroadcast(i);
	}

	/**
	 * Moves Handle between 4 positions: closed -> half_opened -> opened ->
	 * half_closed -> closed
	 * 
	 * @param d
	 *            - tracking path length
	 */
	private void moveHandle (int newYPos) {
		// max - top, 0 - bottom
		if (isTracking || mAnimating) {
			int delta = startYPos - newYPos;

			startYPos = newYPos;
			mStartTrackingPoint = startYPos;

			int currentPos = mRootContainer.getScrollY() + delta;

			if (currentPos <= 0) {
				// Log.e("MOVE_HANDLE", "SetTo CLOSED");
				setSliderState(STATE.CLOSED);
				isTracking = false;
				mAnimating = false;
				return;
			}

			if (state == STATE.CLOSED && (currentPos >= mMiddlePos)) {
				// Log.e("MOVE_HANDLE", "SetTo HALF_CLOSED");
				setSliderState(STATE.HALF_CLOSED);
				isTracking = false;
				mAnimating = false;
				return;
			}

			if ((state == STATE.HALF_CLOSED || state == STATE.HALF_OPENED) && (currentPos >= mInitTop)) {
				// Log.e("MOVE_HANDLE", "SetTo OPENED");
				setSliderState(STATE.OPENED);
				scrolled = false;
				isTracking = false;
				mAnimating = false;
				return;
			}

			if (state == STATE.OPENED && (currentPos >= getOverTopPos())) {
				// Log.e("MOVE_HANDLE", "SetTo FULL");
				setSliderState(STATE.FULL_DESCRIPTION);
				SLIDER_ON = false;
				isTracking = false;
				mAnimating = false;
				return;
			}

			if (state == STATE.FULL_DESCRIPTION && (currentPos <= mInitTop)) {
				// Log.e("MOVE_HANDLE", "SetTo OPENED");
				setSliderState(STATE.OPENED);
				isTracking = false;
				mAnimating = false;
				return;
			}

			if (state == STATE.OPENED && (currentPos <= mMiddlePos)) {
				// Log.e("MOVE_HANDLE", "SetTo HALF_OPENED");
				setSliderState(STATE.HALF_OPENED);
				isTracking = false;
				mAnimating = false;
				return;
			}

			// default
			if (currentPos <= getOverTopPos()) {
				mRootContainer.scrollBy(mRootContainer.getLeft(), delta);
			}

		}
	}

	private int getOverTopPos () {
		WindowManager wm = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
		Display display = wm.getDefaultDisplay();
		int top = display.getHeight() + mHeaderLayout.getHeight() - mHandle.getHeight() - mHandle.getPaddingTop() - statusBarHeight;
		// Log.i("getOverTopPos","getOverTopPos="+top);
		return top;
	}

	public void onClick (View v) {

		switch (v.getId()) {
		case R.id.sliderHandle:
			if (isLiftable) { // if we can pull up (there are some tabs)
				Log.i("ONCLICK", "isLiftable");
				if ((state == STATE.CLOSED || state == STATE.HALF_CLOSED)) {
					animateUp();
				} else if ((state == STATE.OPENED || state == STATE.HALF_OPENED)) {
					animateDown();
				}
			} else { // if we cannot pull up (there are no tabs)
				// Log.i("ONCLICK", "isLiftable");
				if ((state == STATE.CLOSED)) {
					Log.i("ONCLICK", "isLiftable - 1");
					animateUp();
				} else if ((state == STATE.HALF_CLOSED)) {
					Log.i("ONCLICK", "isLiftable - 2");
					animateDown();
				}
			}
			break;
		case R.id.btnImagePrev:
			int prev = mItemGallery.getCurrentItem() - 1;
			if (prev >= 0) {
				mItemGallery.setCurrentItem(prev);
			} else {
				prev = 0;
			}
			break;
		case R.id.btnImageNext:
			int next = mItemGallery.getCurrentItem() + 1;
			if (next <= mItemGallery.getAdapter().getCount() - 1) {
				mItemGallery.setCurrentItem(next);
			} else {
				next = mItemGallery.getAdapter().getCount() - 1;
			}
			break;
		case R.id.product_card_ac_img_360:
			ArrayList<String> gallery = mProductBean.getGallery_3d(ImageSizesEnum.s350);
			String[] images = new String[gallery.size()];
			images = gallery.toArray(images);

			String title = mProductBean.getPrefix();
			String name = mProductBean.getShortname();
			int price = (int) mProductBean.getPrice();
			int buyable = mProductBean.getBuyable();

			Intent intent = new Intent(this, ProductCard360Activity.class);
			intent.putExtra(PRODUCT_BEAN, mProductBean);

			intent.putExtra(PRODUCT_NAME, name);
			intent.putExtra(PRODUCT_TITLE, title);
			intent.putExtra(PRODUCT_PRICE, price);
			intent.putExtra(PRODUCT_IMAGES, images);
			intent.putExtra(PRODUCT_BUYABLE, buyable);

			startActivity(intent);
			break;
		case R.id.product_card_ac_img_gallery:
			Intent gallery_intent = new Intent(this, ProductCardGalleryActivity.class);
			EasyTracker.getTracker().sendEvent("product/gallery", "buttonPress", mProductBean.getName(),(long) mProductBean.getId());
			gallery_intent.putExtra(ProductCardGalleryActivity.PRODUCT_BEAN, mProductBean);
			gallery_intent.putExtra(ProductCardGalleryActivity.GALLERY_ARRAY_POSITION, mItemGallery.getCurrentItem());
			startActivity(gallery_intent);
			break;
		case R.id.product_card_ac_button_share:

			break;
		case R.id.product_card_share_fb:
			mAnimating = false;
			if (NetworkManager.isConnected(this))
				FBSend();
			break;
		case R.id.product_card_share_vk:
			mAnimating = false;
			if (NetworkManager.isConnected(this))
				VKSend();
			break;
		case R.id.product_card_share_tw:
			mAnimating = false;
			if (NetworkManager.isConnected(this))
				TwitterSend();
			break;
		}

	}

	/*
	 * Move up with animation
	 */
	private void animateUp () {
		mAnimating = true;
		mStartTrackingPoint = 1;
		mTrackingPoint = 0;
		long now = SystemClock.uptimeMillis();
		mAnimationLastTime = now;
		mCurrentAnimationTime = now + ANIMATION_FRAME_DURATION;
		mAnimatedVelocity = -mMaximumMajorVelocity;
		mHandler.removeMessages(MSG_ANIMATE);
		mHandler.sendMessageAtTime(mHandler.obtainMessage(MSG_ANIMATE), mCurrentAnimationTime);
	}

	/*
	 * Move down with animation
	 */
	private void animateDown () {
		mAnimating = true;
		mStartTrackingPoint = 0;
		mTrackingPoint = 1;
		long now = SystemClock.uptimeMillis();
		mAnimationLastTime = now;
		mCurrentAnimationTime = now + ANIMATION_FRAME_DURATION;
		mAnimatedVelocity = mMaximumMajorVelocity;
		mHandler.removeMessages(MSG_ANIMATE);
		mHandler.sendMessageAtTime(mHandler.obtainMessage(MSG_ANIMATE), mCurrentAnimationTime);
	}

	private void setSliderState (STATE newState) {
		state = newState;
		switch (newState) {
		case OPENED:
			// Log.i("setSliderState", "OPENED");

			mRootContainer.scrollTo(mRootContainer.getLeft(), mInitTop);
			break;
		case HALF_OPENED:
			// Log.i("setSliderState", "H_OPENED");
			mRootContainer.scrollTo(mRootContainer.getLeft(), mMiddlePos);
			break;
		case HALF_CLOSED:
			// Log.i("setSliderState", "H_CLOSED");
			mRootContainer.scrollTo(mRootContainer.getLeft(), mMiddlePos);
			break;
		case CLOSED:
			// Log.i("setSliderState", "CLOSED");
			mRootContainer.scrollTo(mRootContainer.getLeft(), 0);
			break;
		case FULL_DESCRIPTION:
			// Log.i("setSliderState", "FULL_DESCR");
			int newPos = getOverTopPos();
			mRootContainer.scrollTo(mRootContainer.getLeft(), newPos);

			break;
		default:
			break;
		}

	}

	/*
	 * Handle for animation
	 */
	private class SlidingHandler extends Handler {

		public void handleMessage (Message m) {
			switch (m.what) {
			case MSG_ANIMATE:
				doAnimation();
				break;
			}
		}
	}

	// from the project

	public void fillInfo (ProductBean pBean) {
		// Log.d(TAG,"fillInfo");
		mTvName.setText(mProductBean.getName());
		mTvArticle.setText(mProductBean.getArticle());

		mTvPrice.setText(String.valueOf((int) mProductBean.getPrice()));
		mTvPriceRuble.setText(TypefaceUtils.makeRouble(TypefaceUtils.NORMAL));

		//model
		if(Utils.isEmptyList(mProductBean.getModelsProduct())) {
			mModelList.setVisibility(View.GONE);
		} else {
			final ArrayList<ModelProductBean> models = mProductBean.getModelsProduct();
			LayoutParams params = new LayoutParams(LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT);
			int margin = Utils.dp2pix(this, 5);
			params.setMargins(margin, margin, margin,margin);
			Spanned textSpan;
			TextView view;
			for(int i=0;i<models.size();i++){			
				view = new MyModelTextView(this);
				textSpan  =  android.text.Html.fromHtml("<u>"+models.get(i).getProperty()+(i<models.size()-1?"</u>,":"</u>"));				
				//textSpan  =  android.text.Html.fromHtml("<u>"+models.get(j).getProperty()+(i<2?"</u>,":"</u>"));
				view.setText(textSpan);
				view.setLayoutParams(params);
				view.setTextColor(Color.rgb(231, 151, 54));
				view.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 20);
				view.setTypeface(tfNormal);
				mModelList.addView(view);
				MyGesture myGesture = new MyGesture(view,models.get(i));
				final GestureDetector gDetector = new GestureDetector(myGesture);
				//				final GestureDetector gDetector = new GestureDetector(new OnGestureListener() {
				//					
				//					public boolean onSingleTapUp(MotionEvent e) {
				//						// TODO Auto-generated method stub
				//						QuickAction quickAction = new QuickAction(ProductCardActivity.this,QuickAction.VERTICAL,true);
				//						LayoutInflater inf = getLayoutInflater();
				//						ListView listModel = (ListView) inf.inflate(R.layout.product_card_model_list, null);
				//						ProductCardModelAdapter modelAdapter = new ProductCardModelAdapter(ProductCardActivity.this,models.get(j));
				//						listModel.setAdapter(modelAdapter);
				//						quickAction.addActionItem(listModel);
				//						quickAction.show(_view);x
				//						return false;
				//					}
				//					
				//					public void onShowPress(MotionEvent e) {
				//						// TODO Auto-generated method stub
				//						int i;
				//						i=0;						
				//					}
				//					
				//					
				//					public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX,
				//							float distanceY) {
				//						// TODO Auto-generated method stub
				//						int i;
				//						i=0;
				//						return false;
				//					}
				//					
				//					
				//					public void onLongPress(MotionEvent e) {
				//						// TODO Auto-generated method stub
				//						int i;
				//						i=0;
				//						
				//					}
				//					
				//					
				//					public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
				//							float velocityY) {
				//						// TODO Auto-generated method stub
				//						return false;
				//					}
				//					
				//					
				//					public boolean onDown(MotionEvent e) {
				//						// TODO Auto-generated method stub
				//						int i;
				//						i=0;
				//						return false;
				//					}
				//				});
				//				
				view.setOnTouchListener(new OnTouchListener() {

					@Override
					public boolean onTouch(View v, MotionEvent event) {
						// TODO Auto-generated method stub
						if(gDetector.onTouchEvent(event))
							return true;
						return false;
						//return false;
					}
				});

				/*
				view.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						QuickAction quickAction = new QuickAction(ProductCardActivity.this,QuickAction.VERTICAL,true);
						LayoutInflater inf = getLayoutInflater();
						ListView listModel = (ListView) inf.inflate(R.layout.product_card_model_list, null);
						ProductCardModelAdapter modelAdapter = new ProductCardModelAdapter(ProductCardActivity.this,models.get(j));
						listModel.setAdapter(modelAdapter);
						quickAction.addActionItem(listModel);
						quickAction.show(v);

//						final QuickAction quickAction = new QuickAction(ProductCardActivity.this,QuickAction.VERTICAL,true);
//						TextView text = new TextView(ProductCardActivity.this);
//						text.setText("sdvlsbndvljsbnlv sdvlsbndvljsbnlv sdvlsbndvljsbnlv sdvlsbndvljsbnlv sdvlsbndvljsbnlv sdvlsbndvljsbnlv sdvlsbndvljsbnlv sdvlsbndvljsbnlv sdvlsbndvljsbnlv sdvlsbndvljsbnlv sdvlsbndvljsbnlv ");
//						//text.setText("sdvlsbndvljsbnlv");
//						text.setOnClickListener(new OnClickListener() {
//							
//							@Override
//							public void onClick(View v) {
//								// TODO Auto-generated method stub
//								
//							}
//						});
//						quickAction.addActionItem(text);
//						quickAction.show(v);
					}
				});*/
			}
		}
		//end model
		
		mTvPrice.setTypeface(Utils.getTahomaTypeFace(ProductCardActivity.this));
		mTvOldPrice.setTypeface(Utils.getTahomaTypeFace(ProductCardActivity.this));
		mDiscount.setTypeface(Utils.getTahomaTypeFace(ProductCardActivity.this));

		if (mProductBean.getPrice_old() != 0 && mProductBean.getPrice_old() > mProductBean.getPrice()) {
			mTvOldPrice.setText(String.valueOf((int) mProductBean.getPrice_old()));
			mTvOldPrice.setPaintFlags(mTvOldPrice.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
			mTvOldPriceRuble.setText(TypefaceUtils.makeRouble(TypefaceUtils.NORMAL));
			mTvOldPriceRuble.setPaintFlags(mTvOldPriceRuble.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
			mDiscount.setText(Discount.getDiscount(mProductBean.getPrice(), mProductBean.getPrice_old()));
		} else {
			mTvOldPrice.setVisibility(View.INVISIBLE);
			mTvOldPriceRuble.setVisibility(View.INVISIBLE);
		}

		boolean isBuyable = mProductBean.getBuyable() == 1;
		mBBuy.setEnabled(isBuyable);
		setExistText();
		if (PreferencesManager.getUserCurrentShopId() != 0){
			int is_shop = mProductBean.getShop();
			int scope_store_qty = mProductBean.getScopeStoreQty();
			int scope_shops_qty = mProductBean.getScopeShopsQty();

			if (is_shop > 0 && (scope_store_qty > 0 || scope_shops_qty > 0)) {
				mBBuyNow.setVisibility(View.VISIBLE);
				mBBuyNow.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick (View v) {
						checkCurrentShop();
					}
				});
			} else mBBuyNow.setVisibility(View.INVISIBLE);
		} else mBBuyNow.setVisibility(View.INVISIBLE);

		if (isBuyable) {
			mBBuy.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick (View v) {
					mAnimating = false;
					BasketManager.addProduct(mProductBean);
					EasyTracker.getTracker().sendEvent("cart/add-product", "buttonPress", mProductBean.getName(), (long) mProductBean.getId());
					BasketAddDialogFragment dialog = BasketAddDialogFragment.getInstance(); 
					dialog.setProductMessage(mProductBean.getName());
					dialog.show(getFragmentManager(), "basket_add");
				}
			});
			// выводи информацию о доставке только для доступного к покупке
			// товара
			initDelivery();
		} else {
			// Скрываем все поля поля наличия товара, если он не доступен к
			// покупке
			mDeliveryTitle.setVisibility(View.INVISIBLE);
			mTvDeliveryInfo.setVisibility(View.INVISIBLE);			
		}

		mRbRating.setRating(mProductBean.getRating());

		mButton360.setVisibility(Utils.isEmptyList(mProductBean.getGallery3D()) ? View.GONE : View.VISIBLE);
		mButtonGallery.setVisibility(Utils.isEmptyList(mProductBean.getGallery()) ? View.GONE : View.VISIBLE);
		addGallery();
		addTabContent();

		// загружаем шильдик
		if (!Utils.isEmptyList(mProductBean.getLabel())) {
			String fotoUrl = mProductBean.getLabel().get(0).getFotoWithSize("124x38");
			ImageLoader loader = ApplicationTablet.getLoader(this);
			ImageHelper imageLoader = new ImageHelper(this, loader).setFadeIn(true);
			imageLoader.load(mLable, fotoUrl);
			mLable.setVisibility(View.VISIBLE);
		}
		int count = mThInfo.getTabWidget().getTabCount();
		if(!isBuyable){
			if (!Utils.isEmptyList(mProductBean.getAccessories())){
				for(int index=0; index<count;index++){
					Button tab =  (Button) mThInfo.getTabWidget().getChildAt(index);
					if (tab.getText().toString().equalsIgnoreCase(getString(R.string.tab_title_accessories))) {						
						mThInfo.setCurrentTab(index);																	
						//reloadTabs();
						break;
					}
				}
			} else if (!Utils.isEmptyList(mProductBean.getRelated())) {
				for(int index=0; index<count;index++){
					Button tab =  (Button) mThInfo.getTabWidget().getChildAt(index);
					if (tab.getText().toString().equalsIgnoreCase(getString(R.string.tab_title_otheritems))) {						
						mThInfo.setCurrentTab(index);						
						//reloadTabs();
						break;
					}
				}
			}
		}

	}

	private void showBuyNowProgress(){
		mBuyNowLabel.setVisibility(View.VISIBLE);
		mBuyNowProgress.setVisibility(View.VISIBLE);
		mBBuyNow.setVisibility(View.INVISIBLE);	
	}

	private void hideBuyNowProgress(){
		mBuyNowLabel.setVisibility(View.INVISIBLE);
		mBuyNowProgress.setVisibility(View.INVISIBLE);
	}

	private void checkCurrentShop(){
		ShopLocator locator = new ShopLocator(this);
		locator.setOnNearestShopLocateListener(new OnNearestShopLocateListener() {
			@Override
			public void onStartLocate () {
				showBuyNowProgress();
			}

			@Override
			public void onShopLocated (ShopBean shop) {
				buyNowInShop(shop);
				hideBuyNowProgress();

			}

			@Override
			public void onFailLocate () {
				buyNowNoShop();
				hideBuyNowProgress();
			}

			@Override
			public void onBackgroundLocated(ShopBean shop) {
				// TODO Auto-generated method stub				
			}

			@Override
			public void onBackgroundFailLocate() {
				// TODO Auto-generated method stub

			}


		});
		locator.start();
	}

	private void buyNowInShop(ShopBean shop){
		if (shop.getId() == PreferencesManager.getUserCurrentShopId()){
			Intent intent = new Intent(ProductCardActivity.this, OrderCompleteBuyNowActivity.class);
			intent.putExtra(PRODUCT_BEAN, mProductBean);
			intent.putExtra(SHOP_ID, PreferencesManager.getUserCurrentShopId());
			startActivity(intent);
			mBBuyNow.setVisibility(View.VISIBLE);
		}
		else{
			buyNowNoShop();
		}
	}

	private void buyNowNoShop(){
		Toast.makeText(ProductCardActivity.this, "Вы находитесь не в магазине", Toast.LENGTH_SHORT).show();
		mBBuyNow.setVisibility(View.INVISIBLE);	
	}

	private void setExistText() {

		String exist = "Нет в наличиии";
		int is_shop = mProductBean.getShop();
		int scope_store_qty = mProductBean.getScopeStoreQty();
		int scope_shops_qty = mProductBean.getScopeShopsQty();
		int scope_shops_qty_showroom = mProductBean.getScopeShopsQtyShowroom();
		int buyable = mProductBean.getBuyable();
		boolean in_shop = (PreferencesManager.getUserCurrentShopId()!=0?true:false);
		if (in_shop && buyable == 1 && scope_shops_qty_showroom > 0) {
			exist = "Есть на витрине";
		} else {
			if (buyable == 1) {
				exist = "Есть в наличии";
			} else {
				if (is_shop == 1 && scope_store_qty == 0 && scope_shops_qty > 0) {
					exist = "Только в магазинах";
				} else {
					if (scope_shops_qty == 0 && scope_shops_qty_showroom > 0) {
						exist = "Только на витрине";
					} else {
						if (scope_shops_qty == 0) {
							exist = "Нет в наличии";
						}
					}
				}
			}
		}
		mTvAviliable.setText(exist);
	}

	private void addGallery () {
		ArrayList<String> images = new ArrayList<String>();
		String replaced;
		if (mProductBean.getGallery().size() > 0) {
			for (String path : mProductBean.getGallery()) {
				replaced = Formatters.createFotoString(path, 500);
				images.add(replaced);
			}
		}
		ItemGalleryAdapter adapter = new ItemGalleryAdapter(this, images);
		if (mItemGallery == null)
			return;
		mItemGallery.setAdapter(adapter);
		if (mItemGallery.getAdapter().getCount() > 1) {
			mBtnPrev.setVisibility(View.INVISIBLE);
			mItemGallery.setOnPageChangeListener(new OnPageChangeListener() {

				@Override
				public void onPageSelected (int arg0) {}

				@Override
				public void onPageScrolled (int pos, float offset, int offsetPixels) {
					// Log.d(TAG, "pos="+pos+"  offset="+offset);
					if (pos == 0) {
						mBtnPrev.setVisibility(View.INVISIBLE);
						mBtnNext.setVisibility(View.VISIBLE);
					} else if (pos == mItemGallery.getAdapter().getCount() - 1) {
						mBtnPrev.setVisibility(View.VISIBLE);
						mBtnNext.setVisibility(View.INVISIBLE);
					} else {
						mBtnPrev.setVisibility(View.VISIBLE);
						mBtnNext.setVisibility(View.VISIBLE);
					}
				}

				@Override
				public void onPageScrollStateChanged (int arg0) {}
			});
		} else {
			mBtnPrev.setVisibility(View.INVISIBLE);
			mBtnNext.setVisibility(View.INVISIBLE);

		}

	}

	private void addTabContent () {
		Bundle extra = new Bundle();

		// Options
		if (mProductBean.getOptions().size() > 0) {

			extra.putSerializable(ProductOptionsActivity.P_BEAN, mProductBean);
			createTab(R.string.tab_title_options, ProductOptionsActivity.class, extra);
		}

		// description
		if (mProductBean.getDescription().length() > 0) {

			extra.putSerializable(ProductDescriptionActivity.P_BEAN, mProductBean);
			createTab(R.string.tab_title_decription, ProductDescriptionActivity.class, extra);
		}

		// Accessories
		if (mProductBean.getAccessories() != null && mProductBean.getAccessories().size() > 0) {

			extra = new Bundle();
			extra.putSerializable(AccessoriesListTabActivity.P_BEAN, mProductBean);
			createTab(R.string.tab_title_accessories, AccessoriesListTabActivity.class, extra);
		}

		// RElated
		if (mProductBean.getRelated() != null && mProductBean.getRelated().size() > 0) {

			extra = new Bundle();
			extra.putSerializable(RelativeGoodsListTabActivity.P_BEAN, mProductBean);
			createTab(R.string.tab_title_otheritems, RelativeGoodsListTabActivity.class, extra);
		}
		// SHOPS
		if (mProductBean.getShop_list() != null && mProductBean.getShop_list().size() > 0) {

			extra = new Bundle();
			extra.putSerializable(ProductShopsActivity.PRODUCT_BEAN, mProductBean);
			createTab(R.string.tab_title_wheretobuy, ProductShopsActivity.class, extra);
		}

		// F1
		if (mProductBean.getServices() != null && mProductBean.getServices().size() > 0) {

			extra = new Bundle();
			extra.putSerializable(ProductServicesActivity.P_BEAN, mProductBean);
			createTab(R.string.tab_title_f1, ProductServicesActivity.class, extra);
		}

		if (currentTab != -1) {
			mThInfo.setCurrentTab(currentTab);
		}

	}

	private void initDelivery () {
		ArrayList<DeliveryBean> deliveries = mProductBean.getDelivery_mod();
		StringBuilder builder = new StringBuilder();

		if (Utils.isEmptyList(deliveries)) {
			mDeliveryTitle.setVisibility(View.INVISIBLE);
			mTvDeliveryInfo.setVisibility(View.INVISIBLE);
		} else {
			int size = deliveries.size();
			for (int i = 0; i < size; i++) {
				builder.append(Formatters.createDeliveryString(deliveries.get(i)));
				if (i != size - 1)
					builder.append("\n");
			}
			mTvDeliveryInfo.setText(builder.toString());
		}
	}

	/* ###################### SHARING ##################### */
	private Bundle createBundle (String title, String desc, String url, String image, String price) {
		Bundle parameters = new Bundle();
		title = title.replaceAll("\"", "&quot;");
		if (desc != null) {
			desc = desc.replaceAll("\"", "&quot;");
			desc = desc.replaceAll("–", "-");
		}
		if (desc != null && image != null)
			parameters.putString("attachment",
					"{\"name\":\"" + title + "\"," + "\"href\":\"" + url + "\"," + "\"description\":\"" + Html.fromHtml(desc) + "\","
							+ "\"caption\":\"" + price + " руб." + "\"," + "\"media\":[" + "{\"type\":\"image\"," + "\"src\":\"" + image
							+ "\"," + "\"href\":\"" + url + "\"}]," + "\"properties\":" + "{\"Читать полностью\":" + "{\"text\":"
							+ "\"Enter!\"," + "\"href\":\"" + url + "\"" + "}" + "}" + "}");
		return parameters;

	}

	private void FBSend () {
		Bundle params = createBundle(mProductBean.getName(), mProductBean.getAnnounce(),
				(mProductBean.getLink()==null?"http://www.enter.ru":mProductBean.getLink()), mProductBean.getFoto(), String.valueOf(mProductBean.getPrice()));
		fb.dialog(this, "stream.publish", params, new DialogListener() {
			@Override
			public void onFacebookError (FacebookError e) {
				Log.e("onFacebookError", e.toString());
			}

			@Override
			public void onError (DialogError e) {
				Log.e("onError", e.toString());
			}

			@Override
			public void onComplete (Bundle values) {
				Log.e("onComplete", values.toString());
			}

			@Override
			public void onCancel () {
				Log.e("onCancel", "");
			}
		});
	}

	private void VKSend () {
		final VkApp ap = new VkApp(this);
		ap.setListener(new VkDialogListener() {

			@Override
			public void onError (String description) {
				Toast.makeText(getApplicationContext(), "При отправке сообщения произошла ошибка", Toast.LENGTH_LONG).show();
			}

			@Override
			public void onComplete (String url) {
				ap.postToWall(mProductBean.getAnnounce(), (mProductBean.getLink()==null?"http://www.enter.ru":mProductBean.getLink()), null);
			}

			@Override
			public void onSend (String description) {
				Toast.makeText(getApplicationContext(), "Сообщение успешно отправлено.", Toast.LENGTH_LONG).show();
			}
		});
		if (!ap.hasAccessToken())
			ap.showLoginDialog();
		else
			ap.postToWall(mProductBean.getAnnounce(), mProductBean.getLink(), null);
	}

	public String createTwitterStatus (String news, String url) {
		String tmp = "";
		if (news.length() + url.length() > 140)
			tmp = news.substring(0, news.length() - (news.length() + url.length() - 140) - 4) + "... " + url;
		else
			tmp = news + " " + url;
		return tmp;

	}

	private Handler m_twitterHandler = new Handler() {
		@Override
		public void handleMessage (Message msg) {
			mProgress.dismiss();
			switch (msg.what) {
			case 0:
				Toast.makeText(ProductCardActivity.this, "Сообщение в Twitter отправлено", Toast.LENGTH_SHORT).show();
				break;
			case 1:
				Toast.makeText(ProductCardActivity.this, "При отправке сообщения произошла ошибка", Toast.LENGTH_SHORT).show();
				break;
			case 2:
				Intent intent = new Intent().setClass(ProductCardActivity.this, SendTwitter.class);
				intent.putExtra(EX_TITLE, mProductBean.getName());
				intent.putExtra(EX_URL, (mProductBean.getLink()==null?"http://www.enter.ru":mProductBean.getLink()));
				startActivityForResult(intent, 15);
				break;
			}
		}
	};

	public void TwitterSend () {
		final Twitter twitterConnection = new TwitterFactory().getInstance();
		final OAuthHelp oHelper = new OAuthHelp(this);
		new Thread(new Runnable() {
			@Override
			public void run () {
				if (oHelper.hasAccessToken()) {
					oHelper.configureOAuth(twitterConnection);

					try {
						twitterConnection.updateStatus(createTwitterStatus(mProductBean.getName(),(mProductBean.getLink()==null?"http://www.enter.ru":mProductBean.getLink())));
						m_twitterHandler.sendEmptyMessage(0);
					} catch (TwitterException e) {
						m_twitterHandler.sendEmptyMessage(1);
						e.printStackTrace();
					}
				} else {
					m_twitterHandler.sendEmptyMessage(2);
				}
			}
		}).start();

	}

	// =============================== LOADER
	// =====================================
	private class CardLoader extends AsyncTask<Void, Void, ProductBean> {

		private ProgressDialogFragment mProgress;

		@Override
		protected void onPreExecute () {
			mProgress = ProgressDialogFragment.getInstance();
			mProgress.show(getFragmentManager(), "progress");
			mProgress.setOnCancelListener(new OnCancelListener() {

				@Override
				public void onCancel (DialogInterface dialog) {
					ProductCardActivity.this.finish();

				}
			});
		}

		@Override
		protected ProductBean doInBackground (Void... params) {
			String url = URLManager.getProductCard(PreferencesManager.getCityid(), PreferencesManager.getUserCurrentShopId(), mId, 500);
			ProductInfoParser parser = new ProductInfoParser(url);
			return parser.parse();
		}

		@Override
		protected void onPostExecute (ProductBean result) {
			if (isCancelled()) {
				ProductCardActivity.this.finish();
				return;
			}

			mProgress.dismiss();
			if (result == null || result.getId() == 0) {
				showErrorProductCardDialog();
			} else {
				mProductBean = result;
				fillInfo(mProductBean);
				setTitleWithCategoryName(result.getName());
			}
		}
	}

	private void showErrorProductCardDialog () {
		mRoot.setBackgroundDrawable(getResources().getDrawable(R.color.black));
		ProductCardErrorDialogFragment dialog = ProductCardErrorDialogFragment.getInstance();
		dialog.setCancelable(false);
		dialog.setonClickListener(new DialogInterface.OnClickListener() {

			@Override
			public void onClick (DialogInterface dialog, int which) {
				switch (which) {
				case DialogInterface.BUTTON_POSITIVE:
					finish();
					break;

				default:
					break;
				}
			}
		});

		dialog.show(getFragmentManager(), "product_card_error");// TODO
	}

	private void setTitleWithCategoryName (String productName) {

		TextView product_txt = ((TextView) getActionBar().getCustomView().findViewById(R.id.actionbar_custom_product_name));
		product_txt.setText(productName);

		String categoryName = getIntent().getStringExtra(CatalogActivity.CATEGORY_NAME);
		if (!TextUtils.isEmpty(categoryName)) {
			((TextView) getActionBar().getCustomView().findViewById(R.id.actionbar_custom_product_category))
			.setText(categoryName == null ? "" : (" / " + categoryName));
			product_txt.setTextSize(22);
		} else {
			LinearLayout linear = ((LinearLayout) getActionBar().getCustomView().findViewById(R.id.actionbar_product_root));
			linear.setGravity(Gravity.CENTER);
		}
	}

	//for model listner 
	private class MyGesture extends SimpleOnGestureListener{

		private TextView mView;
		private ModelProductBean mModel;

		private boolean isNotScroled;

		public MyGesture(TextView p, ModelProductBean model){
			mView = p;
			mModel = model;
		}

		@Override
		public boolean onSingleTapConfirmed(MotionEvent e) {

			final QuickAction quickAction = new QuickAction(ProductCardActivity.this,QuickAction.VERTICAL, true);
			LayoutInflater inf = getLayoutInflater();
			ListView listModel = (ListView) inf.inflate(R.layout.product_card_model_list, null);
			ProductCardModelAdapter modelAdapter = new ProductCardModelAdapter(ProductCardActivity.this, mModel);
			listModel.setAdapter(modelAdapter);
			listModel.setOnItemClickListener(new OnItemClickListener() {

				@Override
				public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
					ProductModelBean modBean = (ProductModelBean) parent.getItemAtPosition(position);
					ProductBean prodBean = modBean.getProductBean();
					EasyTracker.getTracker().sendEvent("product/get", "buttonPress", prodBean.getName(), (long) prodBean.getId());
					Intent intent = new Intent();
					intent.setClass(ProductCardActivity.this, ProductCardActivity.class);
					intent.putExtra(ProductCardActivity.PRODUCT_ID, prodBean.getId());
					intent.putExtra(ProductCardActivity.EXTRA_FROM, Constants.FLURRY_FROM.OtherProduct.toString());
					quickAction.dismiss();
					startActivity(intent);
				}
			});
			quickAction.addActionItem(listModel);
			if (isNotScroled) {				
				quickAction.show(mView);
			}
			return true;
		}
		@Override
		public boolean onDown(MotionEvent e) {
			isNotScroled=true;
			return true;
		}
		@Override
		public boolean onDoubleTap(MotionEvent e) {
			isNotScroled=false;
			return false;
		}
		@Override
		public boolean onScroll(MotionEvent e1, MotionEvent e2,	float distanceX, float distanceY) {
			isNotScroled=false;
			return false;
		}
		@Override
		public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,	float velocityY) {
			isNotScroled=false;
			return false;
		}

	}


}
