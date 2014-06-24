package ru.ideast.shopitemfragment.tabs;

import ru.enter.OrderCompleteActivity;
import ru.enter.OrderOneClickActivity;
import ru.enter.ProductCardActivity;
import ru.enter.adapters.ProductCardWhereBuyListAdapter;
import ru.enter.beans.ProductBean;
import ru.enter.beans.ShopBean;
import ru.ideast.shopitemfragment.tabs.AccessoriesListTabActivity.UpdaterBroadcastReceiver;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;


public class ProductShopsActivity extends ListTabActivity {
	public final static String PRODUCT_BEAN = "product_bean";
	public final static String SHOP_ID = "shop_id";
	public final static String SHOP_ADDRESS = "shop_address";
	ProductCardWhereBuyListAdapter adapter;
	private ProductBean pBean;
	UpdaterBroadcastReceiver receiver;
	public class UpdaterBroadcastReceiver extends BroadcastReceiver {
		@Override
		public void onReceive(Context context, Intent intent) {
			//Log.i("ProductCardProductsGridAdapter adapter ", "RESTART!");
			if (adapter != null) {
				adapter.notifyDataSetInvalidated();
			}
		}
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		pBean = (ProductBean) getIntent().getExtras().getSerializable(PRODUCT_BEAN);
		if(pBean != null){
			setAdapterToListView();
		}
	}
	
	@Override
	protected void onResume() {
		IntentFilter filter = new IntentFilter(ProductCardActivity.REFRESH_TAB_TAG);
        receiver = new UpdaterBroadcastReceiver();
        registerReceiver(receiver,filter);
		super.onResume();
	}
	
	@Override
	protected void onPause () {
		unregisterReceiver(receiver);
		super.onPause();
	}

	@Override
	void setAdapterToListView() {
		Boolean isShowWindow = checkShowWindowStatus(pBean);
		adapter = new ProductCardWhereBuyListAdapter(this, pBean.getShop_list(), pBean.getBuyable(), isShowWindow);
		OnClickListener onClick = new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				ShopBean bean = (ShopBean) v.getTag();
				Intent intent = new Intent(ProductShopsActivity.this, OrderOneClickActivity.class);
				intent.putExtra(OrderCompleteActivity.ADDRESS, bean.getAddress());
				intent.putExtra(OrderCompleteActivity.SHOP_ID, bean.getId());
				intent.putExtra(PRODUCT_BEAN, pBean);
				startActivity(intent);				
			}
		};
		adapter.setonClickListener(onClick);
		lvList.setAdapter(adapter);
	}

	private boolean checkShowWindowStatus (ProductBean bean) {

		boolean status = false;
		int is_shop = bean.getShop();
		int scope_store_qty = bean.getScopeStoreQty();
		int scope_shops_qty = bean.getScopeShopsQty();
		int scope_shops_qty_showroom = bean.getScopeShopsQtyShowroom();
		if (bean.getBuyable() == 1) {

		} else {
			if (is_shop == 1 && scope_store_qty == 0 && scope_shops_qty > 0) {
			} else {
				if (scope_shops_qty == 0 && scope_shops_qty_showroom > 0) {
					status = true;
				}
			}
		}
		return status;
	}
}
