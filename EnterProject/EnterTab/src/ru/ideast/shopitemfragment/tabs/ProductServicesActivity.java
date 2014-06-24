package ru.ideast.shopitemfragment.tabs;


import com.google.analytics.tracking.android.EasyTracker;

import ru.enter.ProductCardActivity;
import ru.enter.DataManagement.BasketManager;
import ru.enter.adapters.ProductCardServicesListAdapter;
import ru.enter.beans.ProductBean;
import ru.enter.beans.ServiceBean;
import ru.enter.dialogs.alert.BasketAddDialogFragment;
import ru.ideast.shopitemfragment.tabs.AccessoriesListTabActivity.UpdaterBroadcastReceiver;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;

public class ProductServicesActivity extends ListTabActivity {

	public static String P_BEAN = "theBeanF1";
	private ProductBean pBean;
	UpdaterBroadcastReceiver receiver;
	ProductCardServicesListAdapter adapter;
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
		pBean = (ProductBean) getIntent().getExtras().getSerializable(P_BEAN);
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
		
		adapter = new ProductCardServicesListAdapter(this, pBean.getServices(), pBean.getBuyable());
		OnClickListener onClick = new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				ServiceBean service = (ServiceBean) v.getTag();
				boolean isProductAdded = BasketManager.addRelatedService(pBean, service);
				BasketAddDialogFragment dialog = BasketAddDialogFragment.getInstance();
				dialog.setRelatedServiceMessage(service.getName(), pBean.getName());
				if (isProductAdded) {
					EasyTracker.getTracker().sendEvent("cart/add-product", "buttonPress", pBean.getName(), (long) pBean.getId());
					EasyTracker.getTracker().sendEvent("cart/add-service", "buttonPress", service.getName(), (long) service.getId());
					dialog.setRelatedServiceMessage(service.getName(), pBean.getName());
				} else {
					EasyTracker.getTracker().sendEvent("cart/add-service", "buttonPress", service.getName(), (long) service.getId());
					dialog.setServiceMessage(service.getName());
				}
				dialog.show(getFragmentManager(), "basket_add");
				
			}
		};
		adapter.setOnClickListener(onClick);
   	    lvList.setAdapter(adapter);
	}
	
}
