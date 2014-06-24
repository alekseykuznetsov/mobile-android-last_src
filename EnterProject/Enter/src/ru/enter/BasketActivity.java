package ru.enter;

import java.util.ArrayList;
import java.util.List;

import com.flurry.android.FlurryAgent;
import com.google.analytics.tracking.android.EasyTracker;

import ru.enter.AuthorizationActivity.RunType;
import ru.enter.DataManagement.BasketManager;
import ru.enter.DataManagement.BasketManager.CountPrice;
import ru.enter.adapters.BasketAdapter;
import ru.enter.beans.ProductBean;
import ru.enter.beans.ServiceBean;
import ru.enter.interfaces.IBasketElement;
import ru.enter.tabUtils.TabGroupActivity;
import ru.enter.utils.Constants;
import ru.enter.utils.Formatters;
import ru.enter.utils.PreferencesManager;
import ru.enter.utils.Utils;
import ru.enter.widgets.HeaderFrameManager;
import ru.enter.widgets.HeaderFrameManager.HeaderButton;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class BasketActivity extends TabGroupActivity implements OnClickListener{

	public final static String SHOW_BUTTON = "TO_SHOW_ACCOUNT_BUTTON";


	private View mFooterView; 
	private ListView mList;

	private TextView mTextPriceAll;
	private TextView mTextProductsPrice;
	private TextView mTextServicesPrice;
	private TextView mTextProductsCount;
	private TextView mTextServicesCount;
	private TextView mTextProducts;
	private TextView mTextServices;

	private BasketAdapter mAdapter;

	private boolean mShowPersonalButton;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		EasyTracker.getInstance().setContext(this);
		setContentView(R.layout.basket_activity);
		preInit();
		initFooter();
		initList();	    
		EasyTracker.getTracker().sendEvent("cart/show", "buttonPress", "Просмотр корзины", null);
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

	private void preInit() {
		Bundle bundle = getIntent().getExtras();
		mShowPersonalButton = bundle.getBoolean(SHOW_BUTTON);

		FrameLayout frame = (FrameLayout) findViewById(R.id.basket_activity_frame);
		frame.addView(HeaderFrameManager.getHeaderView(BasketActivity.this, "Корзина", false));
	}

	private void initList () {
		mList = (ListView) findViewById(R.id.basket_list_view);
		mList.addFooterView(mFooterView);
		mAdapter = new BasketAdapter(this, this);
		mList.setAdapter(mAdapter);
	}

	private void initFooter() {
		Typeface typeFace = Utils.getRoubleTypeFace(this);

		LayoutInflater inflater = LayoutInflater.from(this);
		mFooterView = inflater.inflate(R.layout.basket_activity_footer_row, null);

		Button buttonNext = (Button) mFooterView.findViewById(R.id.basket_activity_checkout_b);
		Button buttonClearAll = (Button) mFooterView.findViewById(R.id.basket_activity_clear_b);

		buttonNext.setOnClickListener(this);
		buttonClearAll.setOnClickListener(this);

		mTextProducts = (TextView) mFooterView.findViewById(R.id.basket_text_products);
		mTextProductsCount = (TextView) mFooterView.findViewById(R.id.basket_text_num_products);
		mTextProductsPrice = (TextView) mFooterView.findViewById(R.id.basket_text_price_products);

		mTextServices = (TextView) mFooterView.findViewById(R.id.basket_text_services);
		mTextServicesCount = (TextView) mFooterView.findViewById(R.id.basket_text_num_services);
		mTextServicesPrice = (TextView) mFooterView.findViewById(R.id.basket_text_price_services);

		mTextPriceAll = (TextView) mFooterView.findViewById(R.id.basket_text_price_all);

		mTextPriceAll.setTypeface(typeFace, Typeface.BOLD);
		mTextProductsPrice.setTypeface(typeFace,Typeface.BOLD);
		mTextServicesPrice.setTypeface(typeFace,Typeface.BOLD);
	}

	@Override
	protected void onResume() {
		super.onResume();
		loadData();
	}

	private void loadData() {
		//получили данные
		List<IBasketElement> basketElements = getSortedBasket();

		mAdapter.setObjects(basketElements);

		if (Utils.isEmptyList(basketElements)) {
			mList.removeFooterView(mFooterView);
			Toast.makeText(BasketActivity.this, "Корзина пуста", Toast.LENGTH_SHORT).show();
		}else{
			if(mList.getFooterViewsCount()==0) {
				mList.addFooterView(mFooterView);
			}

			refreshFooterData();
		}
	}

	/**
	 * Формирует лист элементов из корзины по правилу связанные услуги после товара, потом просто услуги
	 * @return
	 */
	public static List<IBasketElement> getSortedBasket () {
		List<IBasketElement> result = new ArrayList<IBasketElement>();
		for (ProductBean product : BasketManager.getProducts()) {
			result.add(product);
			List<ServiceBean> relatedServices = BasketManager.getRelatedServices(product);
			if ( ! Utils.isEmptyList(relatedServices)) {
				result.addAll(relatedServices);
			}
		}

		List<ServiceBean> services = BasketManager.getServices();
		if ( ! Utils.isEmptyList(services)) {
			result.addAll(services);
		}

		return result;
	}

	public void refreshFooterData() {

		CountPrice object = BasketManager.getCountPriceObject();

		//TODO так некрасиво только из-за кривой разметки футера. Переделать
		if (object.productsCount < 1) {
			mTextProducts.setVisibility(View.GONE);
			mTextProductsPrice.setVisibility(View.GONE);
			mTextProductsCount.setVisibility(View.GONE);
		} else {
			mTextProducts.setVisibility(View.VISIBLE);
			mTextProductsPrice.setVisibility(View.VISIBLE);
			mTextProductsCount.setVisibility(View.VISIBLE);
		}

		if (object.servicesCount < 1) {
			mTextServices.setVisibility(View.GONE);
			mTextServicesPrice.setVisibility(View.GONE);
			mTextServicesCount.setVisibility(View.GONE);
		} else {
			mTextServices.setVisibility(View.VISIBLE);
			mTextServicesPrice.setVisibility(View.VISIBLE);
			mTextServicesCount.setVisibility(View.VISIBLE);
		}

		mTextPriceAll.setText(Formatters.createPriceStringWithRouble(object.allPrice));
		mTextProductsPrice.setText(Formatters.createPriceStringWithRouble(object.productsPrice));
		mTextServicesPrice.setText(Formatters.createPriceStringWithRouble(object.servicesPrice));
		mTextProductsCount.setText(object.productsCount + " шт.");
		mTextServicesCount.setText(object.servicesCount+ " шт.");
	}

	@Override
	public void onClick(View v) {
		Intent intent;
		switch (v.getId()) {		
		case R.id.basket_list_row_button_delete:
			IBasketElement element = (IBasketElement) v.getTag();
			showDeleteDialog(element);
			break;
		case R.id.basket_list_row_frame:
			ProductBean prodBean = (ProductBean) v.getTag();
			intent = new Intent().setClass(this, ProductCardActivity.class);
			intent.putExtra(ProductCardActivity.ID, prodBean.getId());
			intent.putExtra(ProductCardActivity.NAME, prodBean.getName());
			intent.putExtra(ProductCardActivity.EXTRA_FROM, Constants.FLURRY_FROM.Basket.toString());
			startActivity(intent);
			break;			
		case R.id.basket_activity_checkout_b:
			if(PreferencesManager.isAuthorized()){
				intent = new Intent();
				intent.setClass(this, CheckoutActivity.class);
				intent.putExtra(CheckoutActivity.LAUNCH_TYPE, CheckoutActivity.LAUNCH_TYPE_NORMAL);
				startActivity(intent);
			}else{
				showRegistrationDialog();
			}
			break;
		case R.id.basket_activity_clear_b:
			showChoiseDialog();
			break;
		default:
			break;
		}
	}

	private void showChoiseDialog(){
		Context context = (getParent() == null) ? this : getParent();

		AlertDialog.Builder dlg = new AlertDialog.Builder(context);				    			
		dlg.setTitle("Вы уверены?")
		.setPositiveButton("Да", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int whichButton) {      
				BasketManager.clear();
				EasyTracker.getTracker().sendEvent("cart/clear", "buttonPress", "Очистка корзины", null);
				loadData();
			}})
			.setNegativeButton("Нет", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int whichButton) {                	                        
				}
			})
			.create()
			.show(); 
	}

	private void showDeleteDialog(final IBasketElement element) {
		Context context = (getParent() == null) ? this : getParent();

		String name = element.getName();

		AlertDialog.Builder dlg = new AlertDialog.Builder(context);				    			
		dlg.setMessage("Вы действительно хотите удалить из корзины " + name + " ?")
		.setPositiveButton("Да", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int whichButton) {      
				BasketManager.delete(element);
				if(element.isProduct())
					EasyTracker.getTracker().sendEvent("cart/remove-product", "buttonPress", element.getName(), (long) element.getId());
				else
					EasyTracker.getTracker().sendEvent("cart/remove-service", "buttonPress", element.getName(), (long) element.getId());
				loadData();
			}})
			.setNegativeButton("Нет", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int whichButton) {                	                        
				}
			})
			.create()
			.show(); 
	}

	private void showRegistrationDialog(){
		final Context context = (getParent() == null) ? this : getParent();

		AlertDialog.Builder dlg = new AlertDialog.Builder(context);				    			
		dlg.setTitle("Мы вас не узнали :(")
		.setMessage("Хотите продолжить без регистрации?")
		.setNegativeButton("Войти/Регистрация", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int whichButton) {
				Intent intent = new Intent();
				intent.setClass(context, AuthorizationActivity.class);
				intent.putExtra(AuthorizationActivity.RUNTYPE, RunType.checkout.name());
				startActivity(intent);
			}})
			.setPositiveButton("Да", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int whichButton) {       
					Intent intent = new Intent();
					intent.setClass(context,CheckoutActivity.class);
					intent.putExtras(getIntent().getExtras());
					startActivity(intent);
				}
			})
			.create()
			.show(); 
	}

	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		//super.onBackPressed();
		EasyTracker.getTracker().sendEvent("cart/hide", "buttonPress", "Корзина закрыта", null);
		Intent intent = new Intent();
		intent.setClass(this, MainMenuActivity.class);
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		startActivity(intent);
	}
}
