package ru.enter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import com.flurry.android.FlurryAgent;
import com.google.analytics.tracking.android.EasyTracker;

import ru.enter.DataManagement.BasketManager;
import ru.enter.ImageManager.ImageDownloader;
import ru.enter.Listeners.RemoveTagListener;
import ru.enter.adapters.FiltersHolder;
import ru.enter.adapters.ProductListAdapter;
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
import ru.enter.widgets.DragAndDropGridView;
import ru.enter.widgets.DragAndDropListenerNew;
import ru.enter.widgets.HeaderFrameManager;
import ru.enter.widgets.NewButtonSelector;
import ru.enter.widgets.NewHeaderFrameManager;
import ru.enter.widgets.TagsView;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Rect;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;
import android.widget.AbsListView.OnScrollListener;
//, RemoveTagListener
public class ProductListActivity extends TabGroupActivity implements OnClickListener, OnItemClickListener, OnItemLongClickListener, RemoveTagListener{


	public static final String COUNT = "count";//кол-во товаров, на всякий случай

	public static final int COUNT_PAGE = 40;//считаем что на странице по 40 элементов будет

	private static final int PRICE_LOWER = 1;//сортировка по цене
	private static final int PRICE_TOP = 2;//сортировка по цене

	private static final int PRODUCER_LOWER = 3;// сортировка по алфавиту
	private static final int PRODUCER_TOP = 4;// сортировка по алфавиту
	private static final int RATING = 5;//Сортировка по рейтингу
	/*
	private static final int PRICE = 1;//сортировка по цене

	private static final int PRODUCER = 2;// сортировка по алфавиту
	private static final int RATING = 3;//Сортировка по рейтингу
	 */
	private ProgressBar mPrgBar;//спинер на этапе первой загрузки
	private ProgressBar mPrg;//спинер на этапе первой загрузки
	private Context context;
	private int id;// айдишник каталога
	private TagsView tag_view;
	private FrameLayout root;
	//private ButtonSelector selector_up;
	private NewButtonSelector selector_second;
	//private Button toSort, toTags, toFilters, by_reit, by_name, by_price;
	private Button   btn_by_name_lover, btn_by_name_top;
	private ImageButton btnFilters, btn_by_reit,  btn_by_price_top, btn_by_price_down;
	private LinearLayout sort_linear;

	private DragAndDropGridView mGrid;

	private FiltersManager mFiltersManager;//менеджер фильтров

	//private ArrayList<ProductBean> beans;//список товаров
	private ProductListAdapter mAdapter;

	private int mSortType = 0;


	private int mProductPage = 1;
	//private int FLAG = 0;

	boolean isFilters,isReload;


	private boolean isMoreDownload;// режим догрузки или нет
	private boolean isLoad;

	private String categoryName; 

	private Loader mLoader;

	private OnScrollListener mScrollListener = new OnScrollListener() {

		@Override
		public void onScrollStateChanged(AbsListView view, int scrollState) {
			//остановлили скролл - начали грузить фотки
			if (scrollState == OnScrollListener.SCROLL_STATE_IDLE) {
				mAdapter.setLoad(true);
				mAdapter.notifyDataSetChanged();
			} else {
				mAdapter.setLoad(false);
			}
		}

		@Override
		public void onScroll(AbsListView view, int firstVisible, int visibleCount, int totalCount) {
			boolean isBottom = firstVisible + visibleCount >= totalCount;
			//чтобы не срабатывал листенер на пустом гриде
			boolean isEmpty = (totalCount == 0);

			if (isBottom && !isEmpty) {
				if(!isLoad) {
					startLoad();
					mPrg.setVisibility(View.VISIBLE);
				}
			}
		}
	};


	@Override
	protected void onCreate (Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		EasyTracker.getInstance().setContext(this);
		setContentView(R.layout.product_list_activity);
		context = this;

		FrameLayout frame = (FrameLayout) findViewById(R.id.product_list_catalog_frame);
		if (PreferencesManager.getUserCurrentShopId() == 0){
			frame.addView(HeaderFrameManager.getHeaderView(ProductListActivity.this, "Каталог", false));
		} else {
			frame.addView(NewHeaderFrameManager.getHeaderView(ProductListActivity.this, PreferencesManager.getUserCurrentShopName()));
		}
		init();
		manageViews();
		Bundle bundle = getIntent().getExtras();
		id = bundle.getInt(CatalogActivity.CATALOG_ID);
		//mProductCount = bundle.getInt(COUNT);//кол-во товаров в списке
		isMoreDownload = true;
		isReload=true;
		mSortType=RATING;
		int id = getIntent().getIntExtra(CatalogActivity.CATALOG_ID, 0);
		categoryName = getIntent().getStringExtra(CatalogActivity.CATALOG_NAME);
		EasyTracker.getTracker().sendEvent("category/get", "buttonPress", categoryName, (long) id);
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

	private void init(){
		//sort_linear = (LinearLayout) findViewById(R.id.product_list_catalog_buttons_sort_frame);
		btnFilters = (ImageButton) findViewById(R.id.product_list_catalog_buttons_toFilters);
		/*toFilters = (Button) findViewById(R.id.product_list_catalog_buttons_toFilters);
		toTags = (Button) findViewById(R.id.product_list_catalog_buttons_toTags);
		toSort = (Button) findViewById(R.id.product_list_catalog_buttons_toSort);
		by_name = (Button) findViewById(R.id.product_list_catalog_buttons_sort_by_name);
		by_reit = (Button) findViewById(R.id.product_list_catalog_buttons_sort_by_reit);
		by_price = (Button) findViewById(R.id.product_list_catalog_buttons_sort_by_price);*/
		btn_by_reit = (ImageButton) findViewById(R.id.product_list_catalog_buttons_by_reit);
		btn_by_name_lover = (Button) findViewById(R.id.product_list_catalog_buttons_by_name_lower);
		btn_by_name_top = (Button) findViewById(R.id.product_list_catalog_buttons_by_name_top);
		btn_by_price_top = (ImageButton) findViewById(R.id.product_list_catalog_buttons_by_price_top);
		btn_by_price_down = (ImageButton) findViewById(R.id.product_list_catalog_buttons_by_price_down);
		tag_view = (TagsView) findViewById(R.id.product_list_catalog_tags);
		root = (FrameLayout) findViewById(R.id.items_list_catalog_root);
		mGrid = (DragAndDropGridView) findViewById(R.id.product_drag_and_drop_grid);
		mGrid.setOnScrollListener(mScrollListener);
		mPrgBar = (ProgressBar) findViewById(R.id.product_list_catalog_progress);
		mPrgBar.setVisibility(View.VISIBLE);
		mPrg = (ProgressBar) findViewById(R.id.product_list_catalog_progress_add);
		mPrg.setVisibility(View.GONE);

	}

	private void manageViews () {
		//toFilters.setOnClickListener(this);
		btnFilters.setOnClickListener(this);
		//toTags.setOnClickListener(this);
		//toSort.setOnClickListener(this);
		//by_name.setOnClickListener(this);
		//by_reit.setOnClickListener(this);
		//by_price.setOnClickListener(this);

		btn_by_reit.setOnClickListener(this);
		btn_by_name_lover.setOnClickListener(this);
		btn_by_name_top.setOnClickListener(this);
		btn_by_price_top.setOnClickListener(this);
		btn_by_price_down.setOnClickListener(this);


		//selector_up = new ButtonSelector(btnFilters);
		selector_second = new NewButtonSelector(btn_by_reit, btn_by_name_lover, btn_by_name_top, btn_by_price_top, btn_by_price_down);

		filterSelectorState();
		btn_by_reit.setSelected(true);

		mAdapter = new ProductListAdapter(this, R.layout.product_list_element_grid);
		mGrid.setAdapter(mAdapter);
		mGrid.setOnItemClickListener(this);
		mGrid.setOnItemLongClickListener(this);
		TagsView.setListener(this);
		createDragAndDropListeners();
	}

	private void destroyLoader(){
		mLoader=null;
	}

	private void startLoad() {	
		if(mLoader == null){
			isLoad=true;
			mLoader = new Loader();
			mLoader.execute();
		} else {
			mLoader.onCancel();
			isLoad=true;
			mLoader = new Loader();
			mLoader.execute();
		}
	}

	@Override
	protected void onResume() {
		super.onResume();
		filterSelectorState();
		if (isFilters) {
			tag_view.init(false);
		} else {
			tag_view.setVisibility(View.GONE);
		}
		if (isReload) {
			mPrgBar.setVisibility(View.VISIBLE);
			clearProducts();
			mProductPage = 1;
			startLoad();
		}
	}


	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		if(resultCode==0)
			return;
		filterSelectorState();
		if (isFilters) {
			tag_view.init(false);
		} else {
			tag_view.setVisibility(View.GONE);
		}
		mPrgBar.setVisibility(View.VISIBLE);
		clearProducts();
		mProductPage = 1;
		startLoad();		
	}
	@Override
	protected void onDestroy () {
		super.onDestroy();
		mFiltersManager.reset();// TODO
		mFiltersManager.setFilters(new ArrayList<FilterBean>());
		ImageDownloader.clearMemoryCache();
	}


	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {

		case R.id.product_list_catalog_buttons_toFilters:
			Intent intent = new Intent(context, FiltersActivity.class);
			Activity activity = (Activity) context;
			intent.putExtras(activity.getIntent());
			getParent().startActivityForResult(intent,123);
			/*
			if (tag_view.getVisibility() == View.VISIBLE) {
				tag_view.setVisibility(View.GONE);
				selector_up.releaseBtn();
			} else {
				tag_view.init(false);
				sort_linear.setVisibility(View.GONE);
				//selector_up.selectBtn(toFilters);
			}
			 */

			break;
			/*
		case R.id.product_list_catalog_buttons_toSort:
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

		case R.id.product_list_catalog_buttons_sort_by_name:
			selector_second.selectBtn(by_name);
			mSortType = PRODUCER;
			sortProductList();
			EasyTracker.getTracker().sendEvent("sort", "По цене", categoryName, (long) 0);
			break;
		case R.id.product_list_catalog_buttons_sort_by_price:
			selector_second.selectBtn(by_price);
			mSortType = PRICE;
			sortProductList();
			EasyTracker.getTracker().sendEvent("sort", "По алфавиту", categoryName, (long) 0);
			break;
		case R.id.product_list_catalog_buttons_sort_by_reit:
			selector_second.selectBtn(by_reit);
			mSortType = RATING;
			sortProductList();
			EasyTracker.getTracker().sendEvent("sort", "По рейтингу", categoryName, (long) 0);
			break;
			 */		
		case R.id.product_list_catalog_buttons_by_name_lower:
			selector_second.selectBtn(btn_by_name_lover);
			setSortType(PRODUCER_LOWER);			
			break;
		case R.id.product_list_catalog_buttons_by_name_top:
			selector_second.selectBtn(btn_by_name_top);
			setSortType(PRODUCER_TOP);			
			break;
		case R.id.product_list_catalog_buttons_by_price_down:
			selector_second.selectBtn(btn_by_price_down);
			setSortType(PRICE_LOWER);			
			break;
		case R.id.product_list_catalog_buttons_by_price_top:
			selector_second.selectBtn(btn_by_price_top);
			setSortType(PRICE_TOP);			
			break;
		case R.id.product_list_catalog_buttons_by_reit:
			selector_second.selectBtn(btn_by_reit);
			setSortType(RATING);			
			break;
		default:
			break;
		}
	}

	/* === Loader === */


	private class Loader extends AsyncTask<Void, Void, ArrayList<ProductBean>> {

		private String request;
		private boolean isCanceled;

		@Override
		protected void onPreExecute () {
			isCanceled=false;
		}

		@Override
		protected ArrayList<ProductBean> doInBackground(Void... params) {
			String stringToSend = makeStringJSONFromManager(mFiltersManager);
			if((!Utils.isEmptyList(mFiltersManager.getOptions())) || (!Utils.isEmptyList(mFiltersManager.getSliders())))
				EasyTracker.getTracker().sendEvent("filter", "buttonPress", stringToSend, (long) 0);
			String parseUrl = URLManager.getProductsInCategory(PreferencesManager.getCityid(), PreferencesManager.getUserCurrentShopId(),
					id, Utils.getDpiForItemList(context), COUNT_PAGE , mProductPage);
			request = Utils.sendPostData(stringToSend, parseUrl);


			return new ItemsListParser().parseTags(request);

		}

		@Override
		protected void onPostExecute(ArrayList<ProductBean> result) {
			if (!isCanceled) {
				if (result.isEmpty()&&mProductPage==1){
					if(!isFilters && PreferencesManager.getUserCurrentShopId() != 0) {
						Toast.makeText(context, "В выбранной категории отсутствуют товары с возможность покупки и получения сейчас. Для просмотра полного списка товаров вам необходимо сбросить фильтр сокращения ассортимента до магазина.", Toast.LENGTH_LONG).show();
						//Toast.makeText(context, "Товары не найдены", Toast.LENGTH_SHORT).show();
					} else {
						Toast.makeText(context, "Товары не найдены", Toast.LENGTH_SHORT).show();
					}
				}

				if (addProducts(result)) {
					if (mProductPage == 1) {
						sortProductList();						
					}
					mProductPage++;
				}

				if (result.size() < COUNT_PAGE) {
					isMoreDownload = false;
				}

				/*
				 * if (Utils.isEmptyList(result)) {
				 * Toast.makeText(ProductListActivity.this, "Bad",
				 * Toast.LENGTH_SHORT).show(); } else { mProductPage++;
				 * Toast.makeText(ProductListActivity.this, "Good",
				 * Toast.LENGTH_SHORT).show(); }
				 */

				mPrgBar.setVisibility(View.GONE);
				mPrg.setVisibility(View.GONE);
				isLoad = false;
				isReload=false;
				destroyLoader();
			}
		}

		public void onCancel(){			
			isCanceled=true;
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


	/*
	 * Сортировка
	 */
	/*
	public void setSortType(int type){
		mSortType = type;
		sortProductList();
	}*/

	public void setSortType(int type){
		mSortType = type;
		switch(mSortType){
		case PRICE_LOWER:
			EasyTracker.getTracker().sendEvent("sort", "По цене", "По цене", (long) 0);
			break;
		case PRICE_TOP:
			EasyTracker.getTracker().sendEvent("sort", "По цене", "По цене", (long) 1);
			break;
		case PRODUCER_LOWER:
			EasyTracker.getTracker().sendEvent("sort", "По алфавиту", "По цене", (long) 0);
			break;
		case PRODUCER_TOP:
			EasyTracker.getTracker().sendEvent("sort", "По алфавиту", "По цене", (long) 1);
			break;
		case RATING:
			EasyTracker.getTracker().sendEvent("sort", "По рейтингу", "По цене", (long) 0);
			break;				
		}	
		sortProductList();
	}

	public void sortProductList(){

		List<ProductBean> objects = getProducts();
		switch(mSortType){
		case PRICE_LOWER:				
			Collections.sort(objects, compare_by_price_lower);
			break;
		case PRICE_TOP:				
			Collections.sort(objects, compare_by_price_top);
			break;
		case PRODUCER_LOWER:				
			Collections.sort(objects, compare_by_producer_lower);
			break;
		case PRODUCER_TOP:				
			Collections.sort(objects, compare_by_producer_top);
			break;
		case RATING:				
			Collections.sort(objects, compare_by_rait);
			break;				
		}
		setProducts(objects);
	}

	private Comparator<ProductBean> compare_by_price_lower = new Comparator<ProductBean>() {   
		@Override
		public int compare(ProductBean object1, ProductBean object2) {
			if(object1.getPrice()>object2.getPrice()){
				return 1;
			}else if(object1.getPrice()<object2.getPrice()){
				return -1;
			}else{
				return 0; 
			}    
		}   
	};
	private Comparator<ProductBean> compare_by_price_top = new Comparator<ProductBean>() {   
		@Override
		public int compare(ProductBean object1, ProductBean object2) {
			if(object1.getPrice()<object2.getPrice()){
				return 1;
			}else if(object1.getPrice()>object2.getPrice()){
				return -1;
			}else{
				return 0; 
			}    
		}   
	};
	private Comparator<ProductBean> compare_by_producer_lower = new Comparator<ProductBean>() {   
		@Override
		public int compare(ProductBean object1, ProductBean object2) {
			return object1.getName().compareTo(object2.getName());
		}   
	};
	private Comparator<ProductBean> compare_by_producer_top = new Comparator<ProductBean>() {   
		@Override
		public int compare(ProductBean object1, ProductBean object2) {
			int result = object1.getName().compareTo(object2.getName());
			return result*(-1);
		}   
	};
	private Comparator<ProductBean> compare_by_rait = new Comparator<ProductBean>() {   
		@Override
		public int compare(ProductBean object1, ProductBean object2) {
			if(object1.getRating()<object2.getRating()){
				return 1;
			}else if(object1.getRating()>object2.getRating()){
				return -1;
			}else{
				return 0; 
			} 
		}   
	};
	/*	
	public void sortProductList(){

		List<ProductBean> objects = getProducts();
		switch(mSortType){
			case PRICE:
				Collections.sort(objects, compare_by_price);
				break;

			case PRODUCER:
				Collections.sort(objects, compare_by_name);
				break;
			case RATING:
				Collections.sort(objects, compare_by_rait);
				break;				
		}
		setProducts(objects);
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
	 */
	public ArrayList<ProductBean> getProducts () {
		return (ArrayList<ProductBean>) mAdapter.getObjects();
	}

	public void setProducts (List<ProductBean> products) {
		mAdapter.setObjects(products);
	}

	public boolean addProducts (List<ProductBean> products) {
		return mAdapter.addObjects(products);
	}

	public void clearProducts () {
		mAdapter.clearObjects();
	}

	@Override
	public boolean onItemLongClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
		mGrid.startDaD();
		return false;
	}



	@Override
	public void onItemClick(AdapterView<?> grid, View arg1, int position, long arg3) {
		try {
			ProductBean selectedBean = (ProductBean) grid.getAdapter().getItem(position);// getProducts().get(positionInGrid);//beans.get(positionInGrid);//itemsPager.getCurrentItem() * adapter.getNumRows() * 2 + 
			Intent intent = new Intent().setClass(context, ProductCardActivity.class);
			intent.putExtra(ProductCardActivity.ID, selectedBean.getId());
			intent.putExtra(ProductCardActivity.NAME, selectedBean.getName());
			intent.putExtra(ProductCardActivity.EXTRA_FROM, Constants.FLURRY_FROM.Catalog.toString());
			startActivity(intent);
		} catch (IndexOutOfBoundsException iobe) {
			Log.d("Index error", iobe.toString());
		}
	}


	private void createDragAndDropListeners () {

		DragAndDropListenerNew gridListener = new DragAndDropListenerNew() {

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
						ProductBean selectedBean = getProducts().get(positionInGrid);//beans.get( positionInGrid);//itemsPager.getCurrentItem() * adapter.getNumRows() * 2 +
						BasketManager.addProduct(selectedBean);
						EasyTracker.getTracker().sendEvent("cart/add-product", "buttonPress", selectedBean.getName(), (long) selectedBean.getId());
						// showDialog();
					} catch (IndexOutOfBoundsException iobe) {
						Log.d("Index error", iobe.toString());
					}
				}

			}

		};
		mGrid.setOnDragnDropListener(gridListener);
	}

	private void showDialog () {
		Context scontext = ((Activity) context).getParent();
		AlertDialog.Builder dlg = new AlertDialog.Builder(scontext);
		dlg.setMessage("Товар добавлен в корзину").setPositiveButton("Перейти в корзину", new DialogInterface.OnClickListener() {
			public void onClick (DialogInterface dialog, int whichButton) {
				Intent intent = new Intent();
				intent.setClass(context, BasketActivity.class);
				intent.putExtra(BasketActivity.SHOW_BUTTON, true);
				//intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_NEW_TASK);
				context.startActivity(intent);
			}
		}).setNegativeButton("Продолжить покупки", new DialogInterface.OnClickListener() {
			public void onClick (DialogInterface dialog, int whichButton) {

			}
		}).create().show();
	}



	@Override
	public void removeFilter(OptionsBean bean) {
		// TODO Auto-generated method stub
		mFiltersManager.removeOptionsTag(bean);
		filterSelectorState();
		mProductPage = 1;
		clearProducts();
		if (!isFilters)
			tag_view.setVisibility(View.GONE);// TODO
		mPrg.setVisibility(View.GONE);
		mPrgBar.setVisibility(View.VISIBLE);
		filterSelectorState();
		startLoad();	
	}



	@Override
	public void removeSeekFilter(FilterBean bean) {
		// типа удаление ... на самом деле не удаляется, а ставится в дефолтное
		// состояние
		SliderSolidBean info = mFiltersManager.getSliderInfo(bean);
		info.setCurrentMin(bean.getMin());
		info.setCurrentMax(bean.getMax());
		filterSelectorState();
		mProductPage = 1;
		clearProducts();
		if (!isFilters)
			tag_view.setVisibility(View.GONE);// TODO
		mPrg.setVisibility(View.GONE);
		mPrgBar.setVisibility(View.VISIBLE);

		startLoad();


	}

	private void filterSelectorState(){
		mFiltersManager = FiltersHolder.getFilterManager();
		isFilters=((Utils.isEmptyList(mFiltersManager.getOptions()) && Utils.isEmptyList(mFiltersManager.getSliders()))?false:true);		
		btnFilters.setSelected(isFilters);
	}

}
