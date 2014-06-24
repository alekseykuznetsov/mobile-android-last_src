package ru.enter;
import java.util.ArrayList;

import com.flurry.android.FlurryAgent;
import com.google.analytics.tracking.android.EasyTracker;

import ru.enter.DataManagement.ProductCacheManager;
import ru.enter.adapters.ModelsItemListPagerAdapter;
import ru.enter.adapters.ProductItemsListPageAdapter;
import ru.enter.beans.ModelProductBean;
import ru.enter.beans.ProductBean;
import ru.enter.utils.PreferencesManager;
import ru.enter.widgets.Dots;
import ru.enter.widgets.HeaderFrameManager;
import ru.enter.widgets.NewHeaderFrameManager;
import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;


public class ModelsCardActivity extends Activity {
	
	public static final String PRODUCT = "product";

	private Context context;
	private ProductBean mCurrentProductBean;

	private LinearLayout main;
	
	private View modelLayout;
	private TextView modelTitle;
	private ViewPager modelView;
	private Dots modelDots;
	
	
	protected void onCreate (Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		EasyTracker.getInstance().setContext(this);

		setContentView(R.layout.models_card_activity);
		context = this;
		mCurrentProductBean = ProductCacheManager.getInstance().getProductInfo();

		initView();

		//manageView();	

	}
	
	private void initView () {
		
		
		main = (LinearLayout) findViewById(R.id.models_card_main_linear);
		
		
		LayoutInflater inflater = getLayoutInflater();
		
		ArrayList<ModelProductBean> models = mCurrentProductBean.getModelsProduct();
		
		for(int i=0;i<models.size();i++)	{
			ModelProductBean model = models.get(i);
			modelLayout = inflater.inflate(R.layout.models_card_products, null);
			modelTitle = (TextView) modelLayout.findViewById(R.id.models_card_product_title);
			modelView = (ViewPager) modelLayout.findViewById(R.id.models_card_product_view);
			modelDots =  (Dots) modelLayout.findViewById(R.id.models_card_product_indicator);
			modelTitle.setText(model.getProperty());
			ModelsItemListPagerAdapter adapter = new ModelsItemListPagerAdapter(this);
			modelView.setAdapter(adapter);
			modelView.setOnPageChangeListener(new OnPageChangeListener() {
				
				@Override
				public void onPageSelected(int arg0) {
					// TODO Auto-generated method stub
					modelDots.setSelected(arg0);
				}
				
				@Override
				public void onPageScrolled(int arg0, float arg1, int arg2) {
					// TODO Auto-generated method stub
					
				}
				
				@Override
				public void onPageScrollStateChanged(int arg0) {
					// TODO Auto-generated method stub
					
				}
			});
			main.addView(modelLayout);
			adapter.setObjects(model.getProducts());
			adapter.notifyDataSetChanged();
			modelDots.initDots(model.getProducts().size(), 3, 0);
			
		}		
		
		FrameLayout frame = (FrameLayout) findViewById(R.id.models_card_frame);
		if (PreferencesManager.getUserCurrentShopId() == 0){
			frame.addView(HeaderFrameManager.getHeaderView(context, "Общий каталог", false));
		} else {
			frame.addView(NewHeaderFrameManager.getHeaderView(context, PreferencesManager.getUserCurrentShopName()));
		}
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

}
