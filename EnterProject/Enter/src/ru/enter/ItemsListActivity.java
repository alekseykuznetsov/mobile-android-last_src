package ru.enter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import com.flurry.android.FlurryAgent;

import ru.enter.DataManagement.BasketManager;
import ru.enter.ImageManager.ImageDownloader;
import ru.enter.Listeners.RemoveTagListener;
import ru.enter.adapters.FiltersHolder;
import ru.enter.adapters.ItemsListPageAdapter;
import ru.enter.beans.FilterBean;
import ru.enter.beans.OptionsBean;
import ru.enter.beans.ProductBean;
import ru.enter.beans.SliderSolidBean;
import ru.enter.parsers.ItemsListParser;
import ru.enter.tabUtils.TabGroupActivity;
import ru.enter.utils.Constants;
import ru.enter.utils.FiltersManager;
import ru.enter.utils.Log;
import ru.enter.utils.Pair;
import ru.enter.utils.PreferencesManager;
import ru.enter.utils.URLManager;
import ru.enter.utils.Utils;
import ru.enter.widgets.ButtonSelector;
import ru.enter.widgets.Dots;
import ru.enter.widgets.DragAndDropListener;
import ru.enter.widgets.FrameWithChildCount;
import ru.enter.widgets.NewHeaderFrameManager;
import ru.enter.widgets.FrameWithChildCount.OnChildCalculateListener;
import ru.enter.widgets.HeaderFrameManager;
import ru.enter.widgets.HeaderFrameManager.HeaderButton;
import ru.enter.widgets.MyPager;
import ru.enter.widgets.TagsView;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Rect;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

public class ItemsListActivity extends TabGroupActivity implements OnClickListener, RemoveTagListener {
	public static final String COUNT = "count";
	public static final String IDs = "IDs";
	private MyPager itemsPager;
	private ItemsListPageAdapter adapter;
	private ProgressBar prg;
	private Context context;
	private ArrayList<ProductBean> beans;
	private int id;
	private TagsView tag_view;
	private FrameLayout root;
	private Dots dots;
	private ButtonSelector selector_up, selector_second;
	private Button toSort, toTags, toFilters, by_reit, by_name, by_price;
	private LinearLayout sort_linear;

	private FiltersManager mFiltersManager;

	private int FLAG = 0;
	private int mProductCount, mProductPage = 1;
	private ImageView mArrowLeft, mArrawRight;
	private ArrayList<Integer> ids;
	private boolean isMoreDownload = false;// режим догрузки или нет
	Thread _thread;

	private Handler mHandler = new Handler() {
		@SuppressWarnings ("unchecked")
		@Override
		public void handleMessage (Message msg) {
			prg.setVisibility(View.GONE);

			switch (msg.what) {
			case 0:
				ArrayList<ProductBean> products = (ArrayList<ProductBean>) msg.obj;
				init(products);
				Bundle b = new Bundle();
				b.putString(SearchGlobalActivity.CATEGORY, String.valueOf(id));
				HeaderFrameManager.setBundle(b);
				break;
			case 1:
				// TODO
				break;
			case 2:
				new DownloadFilters().execute();
				break;
			case 3:
				Bundle bundle = getIntent().getExtras();
				mProductCount = bundle.getInt(COUNT);
				load();
				break;
			default:
				break;
			}
		}

	};

	protected int mMaxProductCount;
	protected boolean childCountCalculated = false;

	@Override
	protected void onCreate (Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.items_list_activity_new);
		context = this;

		FrameLayout frame = (FrameLayout) findViewById(R.id.items_list_catalog_frame);
		//frame.addView(HeaderFrameManager.getHeaderView(ItemsListActivity.this, "Каталог", false, HeaderButton.search), 0);
		if (PreferencesManager.getUserCurrentShopId() == 0){
			frame.addView(HeaderFrameManager.getHeaderView(ItemsListActivity.this, "Каталог", false));
		} else {
			frame.addView(NewHeaderFrameManager.getHeaderView(ItemsListActivity.this, PreferencesManager.getUserCurrentShopName()));
		}
		//frame.addView(HeaderFrameManager.getHeaderView(ItemsListActivity.this, "Каталог", false), 0);

		FrameWithChildCount frameForPager = (FrameWithChildCount) findViewById(R.id.items_list_catalog_frame_for_pager);
		frameForPager.setOnChildCalculateListener(new OnChildCalculateListener() {

			@Override
			public void onChildCountCalculated (int childCount, int verticalSpacing) {

				if (!childCountCalculated) {
					context = ItemsListActivity.this;
					mMaxProductCount = childCount * 2 * 5;// по 2 в ряд, 5
															// страниц

					Bundle bundle = getIntent().getExtras();
					id = bundle.getInt(CatalogActivity.CATALOG_ID);
					mProductCount = bundle.getInt(COUNT);
					ids = bundle.getIntegerArrayList(IDs);
					if (ids != null) {
						mProductCount = ids.size();
					}
					if (mProductCount > mMaxProductCount) {
						isMoreDownload = true;
					} else {
						isMoreDownload = false;
					}

					initViews();
					load();
					manageViews(childCount, verticalSpacing);
					TagsView.setListener(ItemsListActivity.this);
					childCountCalculated = true;
				}
			}
		});
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
		mArrowLeft = (ImageView) findViewById(R.id.items_list_catalog_arrow_left);
		mArrawRight = (ImageView) findViewById(R.id.items_list_catalog_arrow_right);
		mArrawRight.setOnClickListener(this);
		mArrowLeft.setOnClickListener(this);
		sort_linear = (LinearLayout) findViewById(R.id.items_list_catalog_buttons_sort_frame);
		toFilters = (Button) findViewById(R.id.items_list_catalog_buttons_toFilters);
		toTags = (Button) findViewById(R.id.items_list_catalog_buttons_toTags);
		toSort = (Button) findViewById(R.id.items_list_catalog_buttons_toSort);
		by_name = (Button) findViewById(R.id.items_list_catalog_buttons_sort_by_name);
		by_reit = (Button) findViewById(R.id.items_list_catalog_buttons_sort_by_reit);
		by_price = (Button) findViewById(R.id.items_list_catalog_buttons_sort_by_price);
		tag_view = (TagsView) findViewById(R.id.items_list_catalog_tags);
		itemsPager = (MyPager) findViewById(R.id.awesomepager);
		root = (FrameLayout) findViewById(R.id.items_list_catalog_root);
		dots = (Dots) findViewById(R.id.items_list_catalog_dots);
		prg = (ProgressBar) findViewById(R.id.progress);

		if (!isMoreDownload) {
			mArrawRight.setVisibility(View.INVISIBLE);
			mArrowLeft.setVisibility(View.INVISIBLE);
		}
	}

	private void manageViews (int numRowsInPager, int verticalSpace) {
		toFilters.setOnClickListener(this);
		toTags.setOnClickListener(this);
		toSort.setOnClickListener(this);
		by_name.setOnClickListener(this);
		by_reit.setOnClickListener(this);
		by_price.setOnClickListener(this);

		beans = new ArrayList<ProductBean>();

		selector_up = new ButtonSelector(toFilters, toSort, toTags);
		selector_second = new ButtonSelector(by_name, by_price, by_reit);

		toSort.setSelected(true);

		itemsPager.setOnPageChangeListener(new OnPageChangeListener() {
			@Override
			public void onPageSelected (int arg0) {
				dots.setSelected(arg0);
				arrowAction(arg0);

			}

			@Override
			public void onPageScrolled (int arg0, float arg1, int arg2) {}

			@Override
			public void onPageScrollStateChanged (int arg0) {}
		});

		adapter = new ItemsListPageAdapter(this, numRowsInPager, verticalSpace);
		itemsPager.setAdapter(adapter);
		createDragAndDropListeners();
	}

	private void load () {
		prg.setVisibility(View.VISIBLE);
		mArrawRight.setVisibility(View.INVISIBLE);
		mArrowLeft.setVisibility(View.INVISIBLE);
		itemsPager.setVisibility(View.INVISIBLE);

		if (mProductCount > mMaxProductCount) {
			isMoreDownload = true;
		} else {
			isMoreDownload = false;
		}

		Thread thread = new Thread(new Runnable() {

			@Override
			public void run () {
				String parseUrl;
				if (ids == null)
					parseUrl = URLManager.getProductsInCategory(PreferencesManager.getCityid(), PreferencesManager.getUserCurrentShopId(),
							id, Utils.getDpiForItemList(context), mMaxProductCount, mProductPage);
				else
					parseUrl = URLManager.getProductListByIds(PreferencesManager.getCityid(), Utils.getDpiForItemList(context), ids);
				ItemsListParser parser = new ItemsListParser(parseUrl);
				mHandler.sendMessage(mHandler.obtainMessage(0, parser.parse()));

			}
		});

		thread.start();
	}

	private void init (ArrayList<ProductBean> bean) {
		if (bean != null) {
			itemsPager.setVisibility(View.VISIBLE);

			beans = bean;
			adapter.setObjects(bean);
			itemsPager.setCurrentItem(0);
			dots.initDots(bean.size(), adapter.getNumRows() * 2, 0);

			if (!isMoreDownload) {
				mArrawRight.setVisibility(View.INVISIBLE);
				mArrowLeft.setVisibility(View.INVISIBLE);
			}
			arrowAction(0);
			if (bean.isEmpty() && PreferencesManager.getUserCurrentShopId() != 0) {
				Toast.makeText(context, "В выбранной категории отсутствуют товары с возможность покупки и получения сейчас. Для просмотра полного списка товаров вам необходимо сбросить фильтр сокращения ассортимента до магазина.", Toast.LENGTH_LONG).show();
				//Toast.makeText(context, "Товары не найдены", Toast.LENGTH_SHORT).show();
			}
		}
	}

	@Override
	protected void onResume () {
		super.onResume();
		mFiltersManager = FiltersHolder.getFilterManager();
		if (FLAG == 2) {
			mProductPage = 1;
			tag_view.init(false);// TODO
			prg.setVisibility(View.VISIBLE);
			mArrawRight.setVisibility(View.INVISIBLE);
			mArrowLeft.setVisibility(View.INVISIBLE);
			itemsPager.setVisibility(View.INVISIBLE);
			if (_thread != null && _thread.isAlive())
				_thread.interrupt();
			_thread = new Thread() {
				@Override
				public void run () {
					try {
						String stringToSend = makeStringJSONFromManager(mFiltersManager);
						String parseUrl = URLManager.getProductsCountInCategory(PreferencesManager.getCityid(),
								PreferencesManager.getUserCurrentShopId(), id);
						String responceStr = Utils.sendPostData(stringToSend, parseUrl);

						JSONObject responceObject = (JSONObject) new JSONTokener(responceStr).nextValue();
						int count = responceObject.getInt("count");
						if (count > mMaxProductCount) {
							if (!this.isInterrupted()) {
								isMoreDownload = true;
								mProductCount = count;
							}
						} else {
							if (!this.isInterrupted())
								isMoreDownload = false;
						}
						if (!this.isInterrupted())
							mHandler.sendEmptyMessage(2);

					} catch (JSONException e) {
						Log.e("Отправка фильтров", e.toString());
					} catch (ClassCastException e) {

					}

				}
			};
			_thread.start();

		}
	}

	private void arrowAction (int pagerPosition) {
		if (isMoreDownload) {
			int childCount = itemsPager.getAdapter().getCount() - 1;
			mArrawRight.setVisibility(View.INVISIBLE);
			mArrowLeft.setVisibility(View.INVISIBLE);
			if (pagerPosition == 0) {
				if (mProductPage != 1)
					mArrowLeft.setVisibility(View.VISIBLE);
			}

			if (pagerPosition == childCount) {
				int size = mProductCount / mMaxProductCount;
				if (mProductPage <= size) {
					mArrawRight.setVisibility(View.VISIBLE);
				}
			}
		}
	}

	private void sortItemsByPrice (ArrayList<ProductBean> objects) {
		Collections.sort(objects, compare_by_price);
		init(objects);
	}

	private void sortItemsByName (ArrayList<ProductBean> objects) {
		Collections.sort(objects, compare_by_name);
		init(objects);
	}

	private void sortItemsByRait (ArrayList<ProductBean> objects) {
		Collections.sort(objects, compare_by_rait);
		init(objects);
	}

	private Comparator<ProductBean> compare_by_price = new Comparator<ProductBean>() {
		@Override
		public int compare (ProductBean object1, ProductBean object2) {
			if (object1.getPrice() > object2.getPrice()) {
				return 1;
			} else if (object1.getPrice() < object2.getPrice()) {
				return -1;
			} else {
				return 0;
			}
		}
	};
	private Comparator<ProductBean> compare_by_rait = new Comparator<ProductBean>() {
		@Override
		public int compare (ProductBean object1, ProductBean object2) {
			if (object1.getRating() > object2.getRating()) {
				return 1;
			} else if (object1.getRating() < object2.getRating()) {
				return -1;
			} else {
				return 0;
			}
		}
	};
	private Comparator<ProductBean> compare_by_name = new Comparator<ProductBean>() {
		@Override
		public int compare (ProductBean object1, ProductBean object2) {
			return object1.getShortname().compareTo(object2.getShortname());
		}
	};

	@Override
	public void onClick (View v) {
		switch (v.getId()) {

		case R.id.items_list_catalog_buttons_toFilters:
			FLAG = 2;
			if (tag_view.getVisibility() == View.VISIBLE) {
				tag_view.setVisibility(View.GONE);
				selector_up.releaseBtn();
			} else {
				tag_view.init(false);
				sort_linear.setVisibility(View.GONE);
				selector_up.selectBtn(toFilters);
			}

			break;

		case R.id.items_list_catalog_buttons_toSort:
			FLAG = 3;
			if (sort_linear.getVisibility() == View.VISIBLE) {
				sort_linear.setVisibility(View.GONE);
				selector_up.releaseBtn();
			} else {
				tag_view.setVisibility(View.GONE);
				sort_linear.setVisibility(View.VISIBLE);
				selector_up.selectBtn(toSort);
			}

			break;

		case R.id.items_list_catalog_buttons_sort_by_name:
			selector_second.selectBtn(by_name);
			sortItemsByName(beans);
			break;
		case R.id.items_list_catalog_buttons_sort_by_price:
			selector_second.selectBtn(by_price);
			sortItemsByPrice(beans);
			break;
		case R.id.items_list_catalog_buttons_sort_by_reit:
			selector_second.selectBtn(by_reit);
			sortItemsByRait(beans);
			break;
		case R.id.items_list_catalog_arrow_left:
			if (mProductPage > 1) {
				mProductPage--;
				if (FLAG == 2) {
					new DownloadFilters().execute();
				} else {
					load();
				}
			}
			break;
		case R.id.items_list_catalog_arrow_right:
			int size = mProductCount / mMaxProductCount;
			if (mProductPage <= size) {
				mProductPage++;
				if (FLAG == 2) {
					new DownloadFilters().execute();
				} else {
					load();
				}
			}
			break;
		default:
			break;
		}

	}

	private JSONArray makeOptionsJSON (ArrayList<Pair<String, Integer>> options) {
		JSONArray array = new JSONArray();

		for (Pair<String, Integer> pair : options) {
			JSONArray temp = new JSONArray();
			temp.put(pair.getLeft());
			temp.put(pair.getRight());
			array.put(temp);
		}

		return array;
	}

	private JSONArray makeSlidersJSON (ArrayList<SliderSolidBean> sliders) {
		JSONArray array = new JSONArray();

		for (SliderSolidBean slider : sliders) {
			JSONArray temp = new JSONArray();
			temp.put(slider.getId());
			temp.put(slider.getCurrentMin());
			temp.put(slider.getCurrentMax());
			array.put(temp);
		}

		return array;
	}

	private String makeStringJSONFromManager (FiltersManager manager) {
		ArrayList<Pair<String, Integer>> options = mFiltersManager.getSelectedOptionsIDs();
		ArrayList<SliderSolidBean> sliders = mFiltersManager.getSelectedSliders();

		JSONObject object = new JSONObject();

		try {
			// обычные фильтры
			JSONArray opJSON = makeOptionsJSON(options);
			object.put("options", opJSON);

			// seekbars
			JSONArray opSliders = makeSlidersJSON(sliders);
			object.put("sliders", opSliders);
		} catch (Exception e) {
		}

		return object.toString();
	}

	private class DownloadFilters extends AsyncTask<Void, Void, ArrayList<ProductBean>> {

		private String request;
		private boolean isShow = true;

		@Override
		protected void onPreExecute () {
			prg.setVisibility(View.VISIBLE);
			itemsPager.setVisibility(View.INVISIBLE);
		}

		protected ArrayList<ProductBean> doInBackground (Void... params) {

			String stringToSend = makeStringJSONFromManager(mFiltersManager);
			String parseUrl = URLManager.getProductsInCategory(PreferencesManager.getCityid(), PreferencesManager.getUserCurrentShopId(),
					id, Utils.getDpiForItemList(context), mMaxProductCount, mProductPage);
			request = Utils.sendPostData(stringToSend, parseUrl);

			return new ItemsListParser().parseTags(request);// TODO request
															// !=null
		}

		protected void onPostExecute (ArrayList<ProductBean> result) {
			if (isShow) {
				init(result);
				if (result.isEmpty())
					Toast.makeText(context, "Товары не найдены", Toast.LENGTH_SHORT).show();
				prg.setVisibility(View.GONE);
			}
		}
	}

	@Override
	protected void onDestroy () {
		super.onDestroy();
		mFiltersManager.reset();// TODO
		mFiltersManager.setFilters(new ArrayList<FilterBean>());
		ImageDownloader.clearMemoryCache();
	}

	private void createDragAndDropListeners () {

		DragAndDropListener gridListener = new DragAndDropListener() {

			@Override
			public void onStartDrag (int positionInGrid) {
				MainActivity.showDragAndDrop();
			}

			@Override
			public void onDrag (int positionInGrid, int x, int y) {
				// не используется сейчас
			}

			@Override
			public void onStopDrag (int positionInGrid, int x, int y) {
				MainActivity.hideDragAndDrop();
				Rect basketRect = MainActivity.getBasketRect();

				if (x > (basketRect.left) && (y > (basketRect.top))) {
					try {
						ProductBean selectedBean = beans.get(itemsPager.getCurrentItem() * adapter.getNumRows() * 2 + positionInGrid);
						BasketManager.addProduct(selectedBean);
						// showDialog();
					} catch (IndexOutOfBoundsException iobe) {
						Log.d("Index error", iobe.toString());
					}
				}

			}

			@Override
			public void onClick (int positionInGrid) {
				try {
					ProductBean selectedBean = beans.get(itemsPager.getCurrentItem() * adapter.getNumRows() * 2 + positionInGrid);
					Intent intent = new Intent().setClass(context, ProductCardActivity.class);
					intent.putExtra(ProductCardActivity.ID, selectedBean.getId());
					intent.putExtra(ProductCardActivity.EXTRA_FROM, Constants.FLURRY_FROM.Catalog.toString());
					startActivity(intent);
				} catch (IndexOutOfBoundsException iobe) {
					Log.d("Index error", iobe.toString());
				}
			}
		};
		adapter.setDragnDropListener(gridListener);
	}

	private void showDialog () {
		Context scontext = ((Activity) context).getParent();
		AlertDialog.Builder dlg = new AlertDialog.Builder(scontext);
		dlg.setMessage("Товар добавлен в корзину").setPositiveButton("Перейти в корзину", new DialogInterface.OnClickListener() {
			public void onClick (DialogInterface dialog, int whichButton) {
				Intent intent = new Intent();
				intent.setClass(context, BasketActivity.class);
				intent.putExtra(BasketActivity.SHOW_BUTTON, true);
				context.startActivity(intent);
			}
		}).setNegativeButton("Продолжить покупки", new DialogInterface.OnClickListener() {
			public void onClick (DialogInterface dialog, int whichButton) {

			}
		}).create().show();
	}

	@Override
	public void removeFilter (OptionsBean bean) {
		mFiltersManager.removeOptionsTag(bean);
		if (FLAG == 2) {
			mProductPage = 1;
			if (mFiltersManager.countOptionsTag() == 0)
				tag_view.init(false);// TODO
			prg.setVisibility(View.VISIBLE);
			mArrawRight.setVisibility(View.INVISIBLE);
			mArrowLeft.setVisibility(View.INVISIBLE);
			itemsPager.setVisibility(View.INVISIBLE);
			if (_thread != null && _thread.isAlive())
				_thread.interrupt();
			_thread = new Thread() {
				@Override
				public void run () {

					try {
						String stringToSend = makeStringJSONFromManager(mFiltersManager);
						String parseUrl = URLManager.getProductsCountInCategory(PreferencesManager.getCityid(),
								PreferencesManager.getUserCurrentShopId(), id);
						String responceStr = Utils.sendPostData(stringToSend, parseUrl);
						JSONObject responceObject = (JSONObject) new JSONTokener(responceStr).nextValue();
						int count = responceObject.getInt("count");

						if (count > mMaxProductCount) {
							if (!this.isInterrupted()) {
								isMoreDownload = true;
								mProductCount = count;
							}
						} else {
							if (!this.isInterrupted())
								isMoreDownload = false;
						}
						if (!this.isInterrupted())
							mHandler.sendEmptyMessage(2);

					} catch (JSONException e) {
						Log.e("Отправка фильтров", e.toString());
					} catch (ClassCastException e) {

					}

				}
			};
			_thread.start();

		}
	}

	@Override
	public void removeSeekFilter (FilterBean bean) {
		// типа удаление ... на самом деле не удаляется, а ставится в дефолтное
		// состояние
		SliderSolidBean info = mFiltersManager.getSliderInfo(bean);
		info.setCurrentMin(bean.getMin());
		info.setCurrentMax(bean.getMax());

		if (FLAG == 2) {
			mProductPage = 1;
			if (mFiltersManager.countOptionsTag() == 0)
				tag_view.init(false);// TODO
			prg.setVisibility(View.VISIBLE);
			mArrawRight.setVisibility(View.INVISIBLE);
			mArrowLeft.setVisibility(View.INVISIBLE);
			itemsPager.setVisibility(View.INVISIBLE);
			if (_thread != null && _thread.isAlive())
				_thread.interrupt();
			_thread = new Thread() {
				@Override
				public void run () {

					try {
						String stringToSend = makeStringJSONFromManager(mFiltersManager);
						String parseUrl = URLManager.getProductsCountInCategory(PreferencesManager.getCityid(),
								PreferencesManager.getUserCurrentShopId(), id);
						String responceStr = Utils.sendPostData(stringToSend, parseUrl);
						JSONObject responceObject = (JSONObject) new JSONTokener(responceStr).nextValue();
						int count = responceObject.getInt("count");

						if (count > mMaxProductCount) {
							if (!this.isInterrupted()) {
								isMoreDownload = true;
								mProductCount = count;
							}
						} else {
							if (!this.isInterrupted())
								isMoreDownload = false;
						}

						if (!this.isInterrupted())
							mHandler.sendEmptyMessage(2);

					} catch (JSONException e) {
						Log.e("Отправка фильтров", e.toString());
					} catch (ClassCastException e) {

					}

				}
			};
			_thread.start();
			
		}
	}
}
