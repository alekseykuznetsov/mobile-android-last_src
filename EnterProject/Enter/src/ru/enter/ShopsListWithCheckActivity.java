package ru.enter;

import java.io.IOException;
import java.util.ArrayList;

import org.json.JSONException;

import com.flurry.android.FlurryAgent;

import ru.enter.DataManagement.BasketData;
import ru.enter.Listeners.LoadListener;
import ru.enter.adapters.ShopWithChecksAdapter;
import ru.enter.beans.CheckoutBean;
import ru.enter.beans.ShopBean;
import ru.enter.parsers.ShopsParser;
import ru.enter.utils.PreferencesManager;
import ru.enter.utils.URLManager;
import ru.enter.utils.Utils;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

public class ShopsListWithCheckActivity extends Activity implements OnItemClickListener, OnCheckedChangeListener{
	private TextView title;
	private ListView list;
	private int id;
	private int mCityId;
	private static LoadListener loadListener;
	private ProgressBar prg;
	public static final String ID = "ID";
	public static ArrayList<ShopBean> bean;
	private ShopWithChecksAdapter adapter;
	private Handler handler = new Handler(){
		public void handleMessage(android.os.Message msg) {
			bean = (ArrayList<ShopBean>) msg.obj;
			if (bean == null) {
	    		 //Toast.makeText(ShopsListWithCheckActivity.this, "Ошибка получения данных", Toast.LENGTH_SHORT).show();
				showNoShopsDialog("Ошибка получения данных");
	    	 } else {
	    		 if(bean.isEmpty()){
	    			 //Toast.makeText(ShopsListWithCheckActivity.this, "В данном городе пока нет магазинов", Toast.LENGTH_SHORT).show();
	    			 showNoShopsDialog("В данном городе пока нет магазинов");
	    		 } else {	    			 
		    		 if(loadListener!=null) loadListener.isDone(true);
		    		 init(bean);
	    		 }
	    	 }
//			if(loadListener!=null)
//				loadListener.isDone(true);
//			init(bean);
		};
	};
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	    setContentView(R.layout.shop_list);
	    
	    mCityId = PreferencesManager.getCityid();
	    setupWidgets();
	    
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
	protected void onDestroy() {
		super.onDestroy();
	}
	private void setupWidgets() {
		title = (TextView) findViewById(R.id.shop_list_title);
		list = (ListView) findViewById(R.id.shop_list_view);
		prg= (ProgressBar) findViewById(R.id.progress);
		
		title.setVisibility(View.GONE);
		id = getIntent().getIntExtra(ID,0);
		
		load();
	    
		
	}
	public static void setLoadListener(LoadListener l)
	{
		loadListener = l;
	}
	private void load(){
		prg.setVisibility(View.VISIBLE);
		Thread th = new Thread(new Runnable() {
			
			@Override
			public void run() {
				ArrayList<ShopBean> result = null;
				ShopsParser parser = new ShopsParser(URLManager.getShopList(mCityId));
				

				try {
					result =  parser.parse();
				} catch (IOException e) {
					// TODO Auto-generated catch block
				} catch (JSONException e) {
					// TODO Auto-generated catch block
				}
				handler.sendMessage(handler.obtainMessage(0, result));
				
			}
		});
		th.start();
	}
	private void init(ArrayList<ShopBean> beans)
	{
		if(Utils.isEmptyList(beans)){
			Toast.makeText(this, "Магазины не найдены", Toast.LENGTH_SHORT).show();
		}else{
			adapter = new ShopWithChecksAdapter(ShopsListWithCheckActivity.this, this);
			adapter.setObjects(beans);
			list.setAdapter(adapter);
			list.setOnItemClickListener(this);
		}
		prg.setVisibility(View.GONE);
	}
	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
		finish();
	}
	
	private void showNoShopsDialog(String mes) {
		Context context = getParent().getParent();
		AlertDialog.Builder dlg = new AlertDialog.Builder(context);
		dlg.setMessage(mes)
				.setPositiveButton("Ок", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int whichButton) {
						finish();
					}
				}).create().show();
	}

	@Override
	public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
		BasketData data = BasketData.getInstance();
		CheckoutBean b = data.getCheckoutBean();
		ShopBean s = (ShopBean) buttonView.getTag();
		b.setShop_id(s.getId());
		b.setShopAddress(s.getName());
		finish();
		
	}
}
