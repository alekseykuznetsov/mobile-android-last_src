package ru.ideast.shopitemfragment.tabs;

import ru.enter.ProductCardActivity;
import ru.enter.R;
import ru.enter.adapters.ProductCardInfoListAdapter;
import ru.enter.beans.ProductBean;
import ru.ideast.shopitemfragment.tabs.AccessoriesListTabActivity.UpdaterBroadcastReceiver;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.ListView;
import android.widget.TextView;

/**
 * Activity for TextView content
 * 
 * @author Dmitry Kuznetsov
 * 
 */
public class ProductOptionsActivity extends BaseTabActivity implements OnScrollListener {
	
	public static String P_BEAN = "theBeanD";

	private ProductBean pBean;
	ProductCardInfoListAdapter adapter;
	ListView lvList;
	float startY, endY;
	float mLastFirstVisibleItem = 0;
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
	protected void onCreate (Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.product_card_tab_options);
		lvList = (ListView)findViewById(R.id.product_card_tab_options_list);
		lvList.setVerticalFadingEdgeEnabled(false);
		lvList.setOnScrollListener(this);

		mInflater = LayoutInflater.from(this);
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
	
	void setAdapterToListView() {
		
		//TextView header = (TextView) mInflater.inflate(R.layout.product_card_tab_options_list_item_header, null);
		//lvList.addHeaderView(header);
		//TextView footer = (TextView) mInflater.inflate(R.layout.product_card_tab_description_list_item_footer, null);
		//footer.setText(pBean.getDescription());
		//lvList.addFooterView(footer);
		adapter = new ProductCardInfoListAdapter(this, pBean);
		lvList.setAdapter(adapter);
		
	}
	

	@Override
	public void onScroll(AbsListView view, int firstVisibleItem,
			int visibleItemCount, int totalItemCount) {
		
//		Log.i("onScroll DECRIPT","firstVisibleItem="+firstVisibleItem + " visibleItemCount=" + visibleItemCount + " totalItemCount="+totalItemCount + 
//				" getLastVisiblePosition=" + lvList.getLastVisiblePosition());
		
	}

	
	@Override
	public void onScrollStateChanged(AbsListView view, int scrollState) {
//		Log.i("onScrollStateChanged","onScrollStateChanged");
//		
//		int firstVisible = lvList.getFirstVisiblePosition();
//		
//		int total = lvList.getCount()-1;
//		int lastVisible = lvList.getLastVisiblePosition();
//		if(scrollState == SCROLL_STATE_IDLE && total == (lastVisible - firstVisible)){
//			if(mScrollListener != null){
//				mScrollListener.onSliderOn();
//			}
//		}
//		
//		if(scrollState == SCROLL_STATE_IDLE && firstVisible == 0){
		View v = lvList.getChildAt(0);
		int top = (v == null) ? 0 : v.getTop();
		if(scrollState == SCROLL_STATE_IDLE && top == 0){
			Log.e("onScrollStateChanged","IDLE");
			//ProductCardActivity.SLIDER_ON = true;
			if(mScrollListener != null){
				mScrollListener.onSliderOn();
			}
		}
	}
	
	
	
}
