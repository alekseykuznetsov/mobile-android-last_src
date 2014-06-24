package ru.enter;

import java.util.ArrayList;

import com.flurry.android.FlurryAgent;

import ru.enter.adapters.BannerListAdapter;
import ru.enter.beans.ProductBean;
import ru.enter.parsers.ItemsListParser;
import ru.enter.utils.Constants;
import ru.enter.utils.PreferencesManager;
import ru.enter.utils.URLManager;
import ru.enter.utils.Utils;
import ru.enter.widgets.HeaderFrameManager;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;

public class BannerListActivity extends Activity implements OnItemClickListener {
	
	public static final String IDs = "IDs";//список id сли их куча в связке
	
	private Context context;
    private ListView list;
    private ProgressBar mPrgBar;//спинер на этапе первой загрузки
    private TextView errorText;

    private BannerListAdapter adapter;
    
    private ArrayList<Integer> ids;//список товаров
    private ArrayList<ProductBean> beans;
	
	
	
	@Override
    public void onCreate(Bundle savedInstanceState) 
    {
        super.onCreate(savedInstanceState);
        
        context = this;
        setContentView(R.layout.banner_list_activity);
              
        init();
        
        Bundle bundle = getIntent().getExtras();
		ids = bundle.getIntegerArrayList(IDs);
		if (ids != null)
			startLoad();
		else
			showError();        
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
	
	private void init()
    {    	
    	list = (ListView)findViewById(R.id.banner_list_activity_list);        
    	mPrgBar =(ProgressBar) findViewById(R.id.banner_list_activity_progress); 
        errorText = (TextView)findViewById(R.id.banner_list_activity_error_text); 
        FrameLayout frame = (FrameLayout) findViewById(R.id.banner_list_activity_frame);
        frame.addView(HeaderFrameManager.getHeaderView(BannerListActivity.this, "Список товаров", false));        
    }
	
	private void startLoad(){
		new Loader().execute();
	}
	
	private void showError(){
		errorText.setText("Ошибка данных");
		errorText.setVisibility(View.VISIBLE);
	}
	
	private void manageView(){		
        adapter = new BannerListAdapter(this, R.layout.banner_list_row, beans);
        list.setAdapter(adapter);
        list.setOnItemClickListener(this);
	}
	
	 @Override
	    public void onDestroy()
	    {
	    	super.onDestroy();
	    }
	

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
		if(arg1.getTag() instanceof ProductBean){
			Intent intent = new Intent().setClass(context, ProductCardActivity.class);
			ProductBean tmp = (ProductBean) arg1.getTag();
			intent.putExtra(ProductCardActivity.ID, tmp.getId());
			intent.putExtra(ProductCardActivity.NAME, (String) tmp.getName());
			intent.putExtra(ProductCardActivity.EXTRA_FROM, Constants.FLURRY_FROM.Banner.toString());
			startActivity(intent);
		}
		
	}
	
/* === Loader === */
	
	
	private class Loader extends AsyncTask<Void, Void, ArrayList<ProductBean>> {
	   
		@Override
		protected void onPreExecute () {
			mPrgBar.setVisibility(View.VISIBLE);
		}
		
		@Override
		protected ArrayList<ProductBean> doInBackground(Void... params) {
			String parseUrl;
			
			parseUrl = URLManager.getProductListByIds(PreferencesManager.getCityid(), Utils.getDpiForItemList(context), ids);
			
			return new ItemsListParser(parseUrl).parse();
		}
	   @Override
		protected void onPostExecute (ArrayList<ProductBean> result) {
		   if (Utils.isEmptyList(result)) {
				showError();
			} else {
				beans = result;	
				manageView();
			}
			mPrgBar.setVisibility(View.GONE);
	     }
	 }

}
