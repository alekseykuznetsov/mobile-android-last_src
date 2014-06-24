package ru.enter;

import java.net.URLEncoder;
import java.util.ArrayList;

import com.flurry.android.FlurryAgent;
import com.google.analytics.tracking.android.EasyTracker;

import ru.enter.adapters.SearchListAdapter;
import ru.enter.beans.ProductBean;
import ru.enter.enums.SearchMode;
import ru.enter.parsers.ItemsListParser;
import ru.enter.parsers.SearchCountParser;
import ru.enter.utils.Constants;
import ru.enter.utils.PreferencesManager;
import ru.enter.utils.URLManager;
import ru.enter.utils.Utils;
import ru.enter.widgets.ButtonSelector;
import ru.enter.widgets.HeaderFrameManager;
import ru.enter.widgets.SearchNextButton;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.DialogInterface.OnDismissListener;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputFilter;
import android.text.InputType;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnKeyListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;

public class SearchGlobalActivity extends Activity implements
		OnItemClickListener, OnClickListener {

	private static final String NEXT = "next";
	private static final int NUM_PER_PAGE = 20;// кол-во товаров на одной странице

	private ArrayList<ProductBean> _beans;// список товаров при загрузке

	private EditText ed;// поле ввода
	private ImageView searchImageClear; // картикна для очистки
	private String searchQuery = ""; // строка запроса
	private TextView errorText; // ошибка
	private SearchListAdapter adapter; // адаптер для отображения
	private InputMethodManager imm; // метод ввода
	private SearchMode MODE = SearchMode.SEARCH_ALL; // поиск по какому параметру
	private Context context;
	private ListView list; // список на экране
	private boolean isSimpleSearch = false; // простой поиск
	private int category; // категория
	private int page = 1; // страница, с которой поиск
	private ButtonSelector bt_sel; // выбранная кнопка
	private Button search_all_btn, search_by_qr_btn, search_by_articul_btn,
			search_by_numb_btn;// кнопки выбора поиска
	private LinearLayout search_btn_bar; // бар с поиском
	public static final String PRODUCT_ID = "PRODUCT_ID";
	public static final String CATEGORY = "CATEGORY";
	private static final String SEARCH = "Введите запрос для поиска";
	private static final String SEARCH_AR = "Введите артикул для поиска";
	private static final String SEARCH_NUM = "Введите номер для поиска";
	private int SEARCH_COUNT = 5;
	private SearchNextButton more_results; // дозагрузка результатов

	private LoaderCount countLoader; // при первом поиске выдает кол-во найденных товаров
	private LoaderProducts productsLoader; // грузит сами продукты

	private int countProducts; // переменная для хранения общего кол-ва продуктов

	private ProgressDialog prgDialog; // диалог на время поиска товаров
	
	private boolean isFoterAdd;
	private String mCatalogRequestBackup = "";
	private String mArticulRequestBackup = "";

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		EasyTracker.getInstance().setContext(this); 

		context = this;
		if (getIntent().getExtras() != null) {
			isSimpleSearch = true;
			category = getIntent().getExtras().getInt(CATEGORY);
		}
		setContentView(R.layout.search_list_tab);
		initView();
		manageView();
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
	
	
	private void initView() {
		searchImageClear = (ImageView) findViewById(R.id.search_list_tab_image_clear);
		ed = (EditText) findViewById(R.id.search_list_tab_edit_text);
		list = (ListView) findViewById(R.id.search_list_tab_list);
		prgDialog = new ProgressDialog(this.getParent());

		prgDialog.setMessage("Идет поиск");
		prgDialog.setOnDismissListener(new OnDismissListener() {

			@Override
			public void onDismiss(DialogInterface dialog) {
				if (countLoader != null && !countLoader.isCancelled())
					countLoader.cancel(false);
				if (productsLoader != null && !productsLoader.isCancelled())
					productsLoader.cancel(false);
			}
		});
		if (Build.VERSION.SDK_INT >= 11) {
			prgDialog.setCancelable(false);
			prgDialog.setOnKeyListener(new DialogInterface.OnKeyListener() {

				@Override
				public boolean onKey(DialogInterface dialog, int keyCode,
						KeyEvent event) {
					if (event.getAction() == KeyEvent.ACTION_DOWN
							&& keyCode == KeyEvent.KEYCODE_BACK) {
						if (prgDialog.isShowing())
							prgDialog.cancel();
					}
					return false;
				}
			});
		} else {
			prgDialog.setCancelable(true);
		}
		errorText = (TextView) findViewById(R.id.search_list_tab_error_text);
		search_all_btn = (Button) findViewById(R.id.search_list_tab_full_search_btn);
		search_by_qr_btn = (Button) findViewById(R.id.search_list_tab_by_qr_code_btn);
		search_by_articul_btn = (Button) findViewById(R.id.search_list_tab_by_articul_btn);
		search_by_numb_btn = (Button) findViewById(R.id.search_list_tab_by_number_btn);
		search_btn_bar = (LinearLayout) findViewById(R.id.search_list_tab_search_bar);
		FrameLayout frame = (FrameLayout) findViewById(R.id.search_list_tab_frame);
		frame.addView(HeaderFrameManager.getHeaderView(
				SearchGlobalActivity.this, "Поиск", false));
		bt_sel = new ButtonSelector(search_all_btn, search_by_articul_btn, search_by_numb_btn);
		bt_sel.selectBtn(search_all_btn);
		more_results = new SearchNextButton(context);
		more_results.setTag(NEXT);
		list.addFooterView(more_results.getView());
		isFoterAdd = true;
		more_results.disable();
	}

	private void manageView() {
		imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);

		errorText.setText(SEARCH);

		_beans = new ArrayList<ProductBean>();
		adapter = new SearchListAdapter(this, R.layout.search_list_row, _beans);
		list.setAdapter(adapter);
		list.setOnItemClickListener(this);

		ed.setHint(R.string.search);
		ed.setMaxLines(1);
		ed.addTextChangedListener(new TextWatcher() {
			private boolean delete = false;

			@Override
			public void afterTextChanged(Editable s) {
				ed.requestFocus();
				if (MODE == SearchMode.SEARCH_BY_ARTICUL) {
					if (s.length() == 3)
						if (!delete)
							s.append("-");
						else
							s.delete(2, 3);
					if (s.length() == 8)
						newSearch();

				}
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
				if (MODE == SearchMode.SEARCH_BY_ARTICUL) {
					if (count == 1 && after == 0 && start == 3) // удаление одного символа
						delete = true;
					else
						delete = false;
				}
			}

			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				if (countLoader != null && !countLoader.isCancelled())
					countLoader.cancel(false);
				
				if (productsLoader != null && !productsLoader.isCancelled())
					productsLoader.cancel(false);
				
				searchQuery = s.toString();	
				more_results.setQueryChanged();
				if (s.length() == 0) {
					searchImageClear.setVisibility(View.GONE);
//					adapter.clear();
//					adapter.notifyDataSetChanged();
				} else
					searchImageClear.setVisibility(View.VISIBLE);
			}

		});
		ed.setOnKeyListener(new OnKeyListener() {

			@Override
			public boolean onKey(View v, int keyCode, KeyEvent event) {
				if (event.getAction() == KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_ENTER)
					newSearch();
				return false;
			}
		});

		searchImageClear.setVisibility(View.GONE);
		searchImageClear.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (searchQuery != null) {
					ed.setText("");
					searchQuery = "";
					more_results.disable();
					ed.requestFocus();
					if (!imm.isActive())
						imm.showSoftInput(ed, 0);
				}

			}
		});
		search_all_btn.setOnClickListener(this);
		search_by_articul_btn.setOnClickListener(this);

		search_by_qr_btn.setOnClickListener(this);

		search_by_numb_btn.setOnClickListener(this);
		search_by_numb_btn.setVisibility(View.GONE);

		if (isSimpleSearch)
			search_btn_bar.setVisibility(View.GONE);
	}

	/**
	 * Запускает процесс поиска
	 */
	private void newSearch() {
		if (searchQuery != null)
			if (!searchQuery.equals("")) {
				countProducts = 0;
				page = 1;
//				adapter.clear();
				more_results.disable();
//				_beans.clear();
				prgDialog.show();
				InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
				imm.hideSoftInputFromWindow(ed.getWindowToken(), 0);
				// init(URLEncoder.encode(searchQuery).replace("+", "%20"));
				countLoader = new LoaderCount();
				countLoader.execute();
				errorText.setVisibility(View.INVISIBLE);
				
				if(MODE == SearchMode.SEARCH_BY_QR){
//					FlurryAgent.logEvent(Constants.FLURRY_EVENT.Qr_Code_Search.toString());
				} else if(MODE == SearchMode.SEARCH_BY_ARTICUL){
					FlurryAgent.logEvent(Constants.FLURRY_EVENT.Artikyl_Search.toString());
				} else {
					FlurryAgent.logEvent(Constants.FLURRY_EVENT.Catalog_Search.toString());
				} 
			}
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
	}

	@Override
	public void onClick(View v) {
		switch (MODE) {
		case SEARCH_ALL:
			mCatalogRequestBackup = ed.getEditableText().toString();
			break;
		case SEARCH_BY_ARTICUL:
			mArticulRequestBackup = ed.getEditableText().toString();
			break;
		default:
			break;
		}
		
		switch (v.getId()) {
			case R.id.search_list_tab_full_search_btn:
				MODE = SearchMode.SEARCH_ALL;
				ed.setInputType(InputType.TYPE_CLASS_TEXT);
				ed.setFilters(new InputFilter[0]);
				ed.setText(mCatalogRequestBackup);
				ed.setSelection(ed.getText().length());
				search_all_btn.setPressed(true);
				errorText.setText(SEARCH);
				more_results.disable();
				list.setVisibility(View.VISIBLE);
				bt_sel.selectBtn(search_all_btn);
				break;
			case R.id.search_list_tab_by_qr_code_btn:
				MODE = SearchMode.SEARCH_BY_QR;
//				FlurryAgent.logEvent(Constants.FLURRY_EVENT.Qr_Code_Search.toString());
				startActivity(new Intent().setClass(context, CameraActivity.class));
				break;
			case R.id.search_list_tab_by_articul_btn:
				MODE = SearchMode.SEARCH_BY_ARTICUL;
				ed.setText(mArticulRequestBackup);
				ed.setInputType(InputType.TYPE_CLASS_PHONE);
				ed.setSelection(ed.getText().length());
				InputFilter[] FilterArray = new InputFilter[1];
				FilterArray[0] = new ArticalFilter();
				ed.setFilters(FilterArray);
				errorText.setText(SEARCH_AR);
				search_by_articul_btn.setPressed(true);
				list.setVisibility(View.GONE);
				bt_sel.selectBtn(search_by_articul_btn);
				break;
			case R.id.search_list_tab_by_number_btn:
				MODE = SearchMode.SEARCH_BY_NUM;
				errorText.setText(SEARCH_NUM);
				search_by_numb_btn.setPressed(true);
				list.setVisibility(View.GONE);
				bt_sel.selectBtn(search_by_numb_btn);
				break;

			default:
				break;
		}

	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,	long id) {
		if(MODE == SearchMode.SEARCH_ALL) {
			if(view.getTag() instanceof ProductBean){
				Intent intent = new Intent().setClass(context, ProductCardActivity.class);
				ProductBean tmp = (ProductBean) view.getTag();
				intent.putExtra(ProductCardActivity.ID, tmp.getId());
				intent.putExtra(ProductCardActivity.NAME, tmp.getName());
				intent.putExtra(ProductCardActivity.EXTRA_FROM, Constants.FLURRY_FROM.Search.toString());
				startActivity(intent);
			}else if(view.getTag() instanceof String){
				if(more_results.isEnabled()){
					page ++;
					//init(URLEncoder.encode(searchQuery));
					if(productsLoader!=null&&productsLoader.isCancelled())
						productsLoader.cancel(false);
					productsLoader = new LoaderProducts();
					productsLoader.execute();
					more_results.setSearchInAction();
				}
				
			}
		}

	}
	
	/**
     *  Обработка нажатий кнопок
     * @param v view
     * @param keyCode код кнопки
     * @param event событие
     * @return возвращает номер кнопки и событие с ней О_о
     */
  
    public boolean onKey(View v, int keyCode, KeyEvent event) {
        return super.onKeyDown(keyCode, event);
    }
    
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event){
    	if (event.getAction() == KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_BACK)  {

    		if(searchQuery.length()> 0) {
        		ed.setText("");
        		ed.clearFocus();
        		return true;
        	} else return super.onKeyDown(keyCode, event);
        }
    	
    	if (event.getAction() == KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_SEARCH || keyCode == KeyEvent.KEYCODE_ENTER) {
    		newSearch();
    	}
    	
        return false;
		        
    }

	// Loader's AsyncTask

	private class LoaderCount extends AsyncTask<Void, Void, Integer> {

		@Override
		protected void onPreExecute() {			
			if(productsLoader!=null&&productsLoader.isCancelled())
				productsLoader.cancel(false);
		}

		@Override
		protected Integer doInBackground(Void... params) {
			String search = URLEncoder.encode(searchQuery).replace("+", "%20");
			SearchCountParser count_parser = new SearchCountParser(
					URLManager.getSearchV2Count(PreferencesManager.getCityid(), search));
			int count = count_parser.parse();
			return count;
		}

		@Override
		protected void onPostExecute(Integer count) {
			countProducts = count;
			EasyTracker.getTracker().sendEvent("search", "buttonPress",searchQuery, (long) countProducts);
			if (productsLoader != null && productsLoader.isCancelled())
				productsLoader.cancel(false);			
			productsLoader = new LoaderProducts();
			productsLoader.execute();
		}

	}

	private class LoaderProducts extends AsyncTask<Void, Void, ArrayList<ProductBean>> {

		@Override
		protected void onPreExecute() {
			// NOP
		}

		@Override
		protected ArrayList<ProductBean> doInBackground(Void... params) {
			String search = URLEncoder.encode(searchQuery).replace("+", "%20");
			ItemsListParser parser = new ItemsListParser(
					URLManager.getSearchV2(PreferencesManager.getCityid(),
							search, Utils.getSizeForSearchListImg(context),
							NUM_PER_PAGE, page));
			return parser.parse();
		}
		
		@Override
		protected void onCancelled() {
			if(page>1) page--;
			super.onCancelled();
		}
		
		@Override
		protected void onPostExecute(ArrayList<ProductBean> result) {

			if (page * NUM_PER_PAGE >= countProducts) {				
				more_results.setNoResults();
			} else
				more_results.setStartSearch();

			if (result == null) {
				prgDialog.cancel();
				if (page >= 1) {
					errorText.setVisibility(View.VISIBLE);
					errorText.setText("Ошибка загрузки.");
					page--;
				}
				return;
			}
			switch (MODE) {

				case SEARCH_ALL:
				// initAdapter(msg.what, (ArrayList<ProductBean>) msg.obj);
					if(!result.isEmpty() ){//&& SEARCH_COUNT == count
						_beans.clear();
						_beans.addAll(result);
						adapter.clear();
						adapter.add(_beans);
						adapter.notifyDataSetChanged();
					}
					else {
						if(page == 1){
							errorText.setVisibility(View.VISIBLE);
							errorText.setText(R.string.nothing_find);
							adapter.clear();
						}
					}
					break;

				case SEARCH_BY_ARTICUL:
					if (!(result.isEmpty())) {
						errorText.setText("");
						Intent intent = new Intent().setClass(context,ProductCardActivity.class);
						intent.putExtra(ProductCardActivity.ID, result.get(0).getId());
						intent.putExtra(ProductCardActivity.NAME, result.get(0).getName());
						intent.putExtra(ProductCardActivity.EXTRA_FROM, Constants.FLURRY_FROM.Search.toString());
						startActivity(intent);
					} else {
						if (page == 1) {
							errorText.setVisibility(View.VISIBLE);
							errorText.setText(R.string.nothing_find);
						}
						more_results.disable();
					}
					break;

				case SEARCH_BY_NUM:
					
					break;

				default:
					break;
				}
			prgDialog.cancel();
		}

	}

}
