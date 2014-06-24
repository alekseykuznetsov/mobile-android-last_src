package ru.enter;

import java.util.ArrayList;

import com.flurry.android.FlurryAgent;
import com.google.analytics.tracking.android.EasyTracker;

import ru.enter.AuthorizationActivity.RunType;
import ru.enter.DataManagement.BasketManager;
import ru.enter.DataManagement.ProductCacheManager;
import ru.enter.adapters.ShopListAdapter;
import ru.enter.beans.ProductBean;
import ru.enter.beans.ShopBean;
import ru.enter.utils.PreferencesManager;
import ru.enter.utils.Utils;
import ru.enter.widgets.HeaderFrameManager;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

public class ProductWhereToBuy extends Activity implements OnClickListener{
	
	private ProductBean mBean;
	private final static String SHOW_WINDOW = "isShowWindow";
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		EasyTracker.getInstance().setContext(this);
	    setContentView(R.layout.shop_list);
	    
		((FrameLayout) findViewById(R.id.shop_list_frame)).addView(HeaderFrameManager.getHeaderView(ProductWhereToBuy.this, "В магазинах", false));
		TextView title = (TextView) findViewById(R.id.shop_list_title);
		ListView list = (ListView) findViewById(R.id.shop_list_view);
		ProgressBar prg = (ProgressBar) findViewById(R.id.progress);
		prg.setVisibility(View.GONE);
		
		title.setText(getIntent().getStringExtra("title"));
		mBean = ProductCacheManager.getInstance().getProductInfo();
		ArrayList<ShopBean> shops = mBean.getShop_list();
		
		boolean isShowWindow = getIntent().getExtras().getBoolean(SHOW_WINDOW, false);
		
		ShopListAdapter adapter = new ShopListAdapter(ProductWhereToBuy.this,this, isShowWindow);
		adapter.setObjects(shops);
		adapter.setBuyable(mBean.getBuyable() == 1);
		list.setAdapter(adapter);
		
		if(Utils.isEmptyList(shops))
			title.setText("Магазины не найдены");
		else{
			title.setText(mBean.getName());
		}
			
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
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.shop_list_row_button_buy:
			ShopBean bean = (ShopBean)v.getTag();
			BasketManager.addOneClickProduct(mBean);
			EasyTracker.getTracker().sendEvent("cart/add-product", "buttonPress", mBean.getName(), (long) mBean.getId());
			Intent intent = new Intent();
			intent.putExtra(CheckoutActivity.LAUNCH_TYPE, CheckoutActivity.LAUNCH_TYPE_BUY_ONE_CLICK);
			intent.putExtra(CheckoutActivity.SHOP_ADDRESS, bean.getAddress());
			intent.putExtra(CheckoutActivity.SHOP_ID, bean.getId());
			if(PreferencesManager.isAuthorized()){
				intent.setClass(this, CheckoutActivity.class);
			}else{
				intent.setClass(this, AuthorizationActivity.class);
				intent.putExtra(AuthorizationActivity.RUNTYPE, RunType.checkout.name());
			}
			
			startActivity(intent);
			break;
//		case R.id.shop_list_row_button_path:
//			break;
		default:
			break;
		}
	}
}
