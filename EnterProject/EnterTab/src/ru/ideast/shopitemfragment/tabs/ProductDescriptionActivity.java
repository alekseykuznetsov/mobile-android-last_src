package ru.ideast.shopitemfragment.tabs;

import ru.enter.ProductCardActivity;
import ru.enter.R;
import ru.enter.beans.ProductBean;
import ru.enter.widgets.TextViewNormal;
import ru.ideast.shopitemfragment.customscroll.ObservableScrollView;
import ru.ideast.shopitemfragment.customscroll.ScrollViewListener;
import ru.ideast.shopitemfragment.tabs.AccessoriesListTabActivity.UpdaterBroadcastReceiver;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.widget.TextView;

/**
 * Activity for TextView content
 * 
 * @author Dmitry Kuznetsov
 * 
 */
public class ProductDescriptionActivity extends BaseTabActivity implements ScrollViewListener {
	
	public static String P_BEAN = "theBeanD";

	private ProductBean pBean;
	ObservableScrollView scroll;
	TextViewNormal descriptionTv;
	UpdaterBroadcastReceiver receiver;
	public class UpdaterBroadcastReceiver extends BroadcastReceiver {
		@Override
		public void onReceive(Context context, Intent intent) {
			Log.i("ProductCardProductsGridAdapter adapter ", "RESTART!");
			if (scroll != null) {
				scroll.scrollTo(0, 0);
			}
		}
	}
	

	float startY, endY;
	float mLastFirstVisibleItem = 0;

	
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
	protected void onCreate (Bundle savedInstanceState)  {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.product_card_tab_description);
		scroll = (ObservableScrollView) findViewById(R.id.description_scroll);
		scroll.setScrollViewListener(this);
		descriptionTv = (TextViewNormal) findViewById(R.id.description_content);
		
		mInflater = LayoutInflater.from(this);
		pBean = (ProductBean) getIntent().getExtras().getSerializable(P_BEAN);
		if(pBean != null){
			descriptionTv.setText(pBean.getDescription());
			//descriptionTv.setText(R.string.test_big_text);
		}
	}

	@Override
	public void onScrollChanged(ObservableScrollView scrollView, int x, int y,
			int oldx, int oldy) {
		//Log.d("ProductDecriptionActivity", "x="+x+" y="+y+" oldx="+oldx+" oldy="+oldy);
		if(y == 0 && mScrollListener != null){
			mScrollListener.onSliderOn();
		}
	}

	
	

	
	


	
	
}
