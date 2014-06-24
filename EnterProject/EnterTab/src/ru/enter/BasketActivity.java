package ru.enter;

import com.flurry.android.FlurryAgent;
import com.google.analytics.tracking.android.EasyTracker;

import ru.enter.DataManagement.BasketManager;
import ru.enter.adapters.BasketListAdapter;
import ru.enter.adapters.GridAdapter.ViewHolder;
import ru.enter.base.BaseActivity;
import ru.enter.beans.ProductBean;
import ru.enter.beans.ServiceBean;
import ru.enter.dialogs.alert.BasketDeleteAllDialogFragment;
import ru.enter.dialogs.alert.BasketDeleteDialogFragment;
import ru.enter.interfaces.IBasketElement;
import ru.enter.interfaces.OnBasketChangeListener;
import ru.enter.utils.Constants;
import ru.enter.utils.PreferencesManager;
import ru.enter.utils.TypefaceUtils;
import android.app.ActionBar;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.LinearLayout.LayoutParams;

public class BasketActivity extends BaseActivity implements OnClickListener, OnBasketChangeListener{
	
	private BasketListAdapter mAdapter;
	private TextView mProductsCountText;
	private TextView mServicesCountText;
	private TextView mTotalCountText;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		EasyTracker.getInstance().setContext(this);
		EasyTracker.getTracker().sendEvent("cart/show", "buttonPress", "Просмотр корзины", null);

		setContentView(R.layout.basket_ac);
		setTitleLeft(getResources().getString(R.string.actionbar_basket));
	    
	    //подписываемся на изменение корзины
	    BasketManager.setOnBasketChangeListener(this);
		
	    onConfigurationChanged(getResources().getConfiguration());
	    
	    initList();
	    initFooter();
	}
	
	private void initList() {
		ListView listView = (ListView) findViewById(R.id.basket_ac_listview);
		
		mAdapter = new BasketListAdapter(this);
		mAdapter.setOnClickListener(this);
		
		listView.setEmptyView(findViewById(R.id.basket_ac_empty));
		listView.setAdapter(mAdapter);

		Button next = (Button) findViewById(R.id.basket_ac_button_make_order);
		next.setOnClickListener(this);

		Button clean = (Button) findViewById(R.id.basket_ac_button_clean);
		clean.setOnClickListener(this);

	}
	
	private void initFooter() {
		mProductsCountText = (TextView) findViewById(R.id.basket_ac_text_goods_count);
		mServicesCountText = (TextView) findViewById(R.id.basket_ac_text_services_count);
		mTotalCountText = (TextView) findViewById(R.id.basket_ac_text_total);
		TextView ruble = (TextView) findViewById(R.id.basket_ac_label_total_ruble);
		ruble.setText(TypefaceUtils.makeRouble(TypefaceUtils.NORMAL));
		
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		mAdapter.setObjects(BasketManager.getAll());

		refreshFooter();
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
	
	
	@Override
	public void onBasketChange() {
		refreshFooter();
	}

	private void refreshFooter() {
		int productsCount = 0;
		int servicesCount = 0;
		int totalPrice = 0;
		
		for (ProductBean product : BasketManager.getProducts()) {
			int productCnt = product.getCount();
			productsCount += productCnt;
			totalPrice += (product.getPrice() * productCnt);
		}
		
		for (ServiceBean service : BasketManager.getServicesAll()) {
			int serviceCnt = service.getCount();
			servicesCount += serviceCnt;
			totalPrice += (service.getPrice() * serviceCnt);
		}

		mProductsCountText.setText(String.valueOf(productsCount));
		mServicesCountText.setText(String.valueOf(servicesCount));
		mTotalCountText.setText(String.valueOf(totalPrice));
	}


	@Override
	public void onClick(View v) {
		IBasketElement element;
		switch (v.getId()) {
		// ОФОРМИТЬ ЗАКАЗ
		case R.id.basket_ac_button_make_order:
			if (BasketManager.isEmpty()) {
				Toast.makeText(this, "Корзина пуста", Toast.LENGTH_SHORT)
						.show();
			} else {
				// авторизован ли пользователь
				if (PreferencesManager.isAuthorized()) {
					startActivity(new Intent(this, OrderActivity.class));
				} else {
					AuthorizationActivity.launch(this);
				}
			}
			break;
		// ОЧИСТИТЬ КОРЗИНУ
		case R.id.basket_ac_button_clean:
			if (BasketManager.isEmpty()) {
				Toast.makeText(this, "Корзина пуста", Toast.LENGTH_SHORT)
						.show();
			} else {
				showDeleteAllDialog();
			}
			break;
		// УДАЛИТЬ ОДИН ЭЛЕМЕНТ
		case R.id.basket_list_item_imagebutton_delete:
			element = (IBasketElement) v.getTag();
			showDeleteDialog(element);
			break;

		// УДАЛИТЬ ОДИН ЭЛЕМЕНТ
		case R.id.basket_list_item_root_elemet:
			element = (IBasketElement) v.getTag(R.id.basket_bean);
			Intent intent = new Intent(BasketActivity.this, ProductCardActivity.class);			 
			intent.putExtra(ProductCardActivity.PRODUCT_ID, element.getId());
			intent.putExtra(ProductCardActivity.EXTRA_FROM, Constants.FLURRY_FROM.Basket.toString());
			EasyTracker.getTracker().sendEvent("product/get", "buttonPress", element.getName(), (long) element.getId());
			startActivity(intent);
			break;
		default:
			break;
		}
	}
	
	private void showDeleteDialog (final IBasketElement element) {
		BasketDeleteDialogFragment dialog = BasketDeleteDialogFragment.getInstance();
		if (element.isProduct()) {
			dialog.setProductMessage(element.getName());
		} else {
			dialog.setServiceMessage(element.getName());
		}
		
		dialog.setonClickListener(new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				switch (which) {
				case DialogInterface.BUTTON_POSITIVE:
					BasketManager.delete(element);
					if(element.isProduct())
						EasyTracker.getTracker().sendEvent("cart/remove-product", "buttonPress", element.getName(), (long) element.getId());
					else
						EasyTracker.getTracker().sendEvent("cart/remove-service", "buttonPress", element.getName(), (long) element.getId());
					mAdapter.setObjects(BasketManager.getAll());
					break;
	
				default:
					break;
				}
			}
		});
		
		dialog.show(getFragmentManager(), "delete_element");//TODO
	}
	
	private void showDeleteAllDialog () {
		BasketDeleteAllDialogFragment dialog = BasketDeleteAllDialogFragment.getInstance();
		dialog.setonClickListener(new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				switch (which) {
				case DialogInterface.BUTTON_POSITIVE:
					BasketManager.clear();
					EasyTracker.getTracker().sendEvent("cart/clear", "buttonPress", "Очистка корзины", null);
					mAdapter.clearObjects();
					break;
	
				default:
					break;
				}
			}
		});
		
		dialog.show(getFragmentManager(), "delete_all");//TODO
	}
	
	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		
    // пустая view для регулирования ширины кнопок
    View blankFooterView = (View) findViewById(R.id.basket_ac_view_blank);
	    
	if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
		android.view.ViewGroup.LayoutParams params = new LayoutParams(LayoutParams.MATCH_PARENT, 1, 0.5f);
		blankFooterView.setLayoutParams(params);	
		} else if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {
			LayoutParams params = new LayoutParams(LayoutParams.MATCH_PARENT, 1, 1.0f);
			blankFooterView.setLayoutParams(params);	
		}
	}
	
	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		//super.onBackPressed();
		EasyTracker.getTracker().sendEvent("cart/hide", "buttonPress", "Корзина закрыта", null);
		Intent intent = new Intent();
		intent.setClass(this, MainActivity.class);
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		startActivity(intent);
	}
}

