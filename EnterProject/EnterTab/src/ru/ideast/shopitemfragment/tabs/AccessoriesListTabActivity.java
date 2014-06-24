package ru.ideast.shopitemfragment.tabs;

import com.google.analytics.tracking.android.EasyTracker;

import ru.enter.ProductCardActivity;
import ru.enter.R;
import ru.enter.DataManagement.BasketManager;
import ru.enter.adapters.ProductCardProductsGridAdapter;
import ru.enter.beans.ProductBean;
import ru.enter.dialogs.alert.BasketAddDialogFragment;
import ru.enter.utils.Constants;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;

public class AccessoriesListTabActivity extends GridTabActivity {

	public static String P_BEAN = "theBeanAcc";
	private ProductBean pBean;
	ProductCardProductsGridAdapter adapter;
	UpdaterBroadcastReceiver receiver;

	public class UpdaterBroadcastReceiver extends BroadcastReceiver {
		@Override
		public void onReceive (Context context, Intent intent) {
			// Log.i("ProductCardProductsGridAdapter adapter ", "RESTART!");
			if (adapter != null) {
				adapter.notifyDataSetInvalidated();
			}
		}
	}

	@Override
	protected void onCreate (Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		pBean = (ProductBean) getIntent().getExtras().getSerializable(P_BEAN);
		if (pBean != null) {
			setAdapterToListView();
		}

	}

	@Override
	protected void onResume () {
		IntentFilter filter = new IntentFilter(ProductCardActivity.REFRESH_TAB_TAG);
		receiver = new UpdaterBroadcastReceiver();
		registerReceiver(receiver, filter);
		super.onResume();
	}

	@Override
	protected void onPause () {
		unregisterReceiver(receiver);
		super.onPause();
	}

	@Override
	void setAdapterToListView () {

		adapter = new ProductCardProductsGridAdapter(this, pBean.getAccessories());
		adapter.setonClickListener(mClickListener);

		lvGrid.setAdapter(adapter);
		lvGrid.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick (AdapterView<?> arg0, View arg1, int arg2, long arg3) {
				startNewProductCard((int) arg3);
			}
		});

	}

	private OnClickListener mClickListener = new OnClickListener() {

		@Override
		public void onClick (View v) {
			switch (v.getId()) {
			case R.id.product_card_tab_products_button_buy:
				int pos = (Integer) v.getTag(R.string.grid_key_item_pos);
				ProductBean bean = (ProductBean) lvGrid.getAdapter().getItem(pos);				
				BasketManager.addProduct(bean);
				EasyTracker.getTracker().sendEvent("cart/add-product", "buttonPress", bean.getName(), (long) bean.getId());
				BasketAddDialogFragment dialog = BasketAddDialogFragment.getInstance();
				dialog.setProductMessage(bean.getName());
				Activity activity = AccessoriesListTabActivity.this;
				dialog.show(activity.getFragmentManager(), "basket_add");
				break;
			case R.id.product_card_tab_products_relative_main:
				Intent intent = new Intent(AccessoriesListTabActivity.this, ProductCardActivity.class);
				int product_id = (Integer) v.getTag(R.string.grid_key_id);
				intent.putExtra(ProductCardActivity.PRODUCT_ID, product_id);
				intent.putExtra(ProductCardActivity.EXTRA_FROM, Constants.FLURRY_FROM.OtherProduct.toString());
				startActivity(intent);
				break;

			}
		}
	};

	private void startNewProductCard (int product_id) {
		Intent intent = new Intent();
		intent.setClass(this, ProductCardActivity.class);
		intent.putExtra(ProductCardActivity.PRODUCT_ID, product_id);
		intent.putExtra(ProductCardActivity.EXTRA_FROM, Constants.FLURRY_FROM.OtherProduct.toString());
		startActivity(intent);
	}
}
