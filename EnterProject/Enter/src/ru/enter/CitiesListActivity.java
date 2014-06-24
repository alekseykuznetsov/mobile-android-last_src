package ru.enter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import com.flurry.android.FlurryAgent;

import ru.enter.adapters.CitiesListAdapter;
import ru.enter.beans.CitiesBean;
import ru.enter.parsers.CitiesParser;
import ru.enter.utils.URLManager;
import ru.enter.utils.Utils;
import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;
import android.widget.Toast;

public class CitiesListActivity extends Activity{

	private ListView list;
	private EditText edit;
	public static final String CITY = "CITY";
	public static final String CITY_ID = "CITY_ID";
	public static final String CITY_HAS_SHOP = "CITY_HAS_SHOP";
	private Context context;
	private ArrayList<CitiesBean> mCities;
	private FrameLayout prg;
	
	private Handler handler = new Handler()	{
		@SuppressWarnings("unchecked")
		public void handleMessage(android.os.Message msg) {
			prg.setVisibility(View.GONE);
			if (Utils.isEmptyList((ArrayList<CitiesBean>) msg.obj))
				Toast.makeText(CitiesListActivity.this, "Необходимо интернет соединение", Toast.LENGTH_LONG).show();
			else
				initList((ArrayList<CitiesBean>) msg.obj);
		};
	};
	
	Comparator<CitiesBean> mCityComparator = new Comparator<CitiesBean>() {
	   @Override
	   public int compare(CitiesBean lhs, CitiesBean rhs) {
		   try {
			   return lhs.getName().compareToIgnoreCase(rhs.getName());
		   } catch (Exception e) {
			   return 0;
		   }
	   }
	};
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.cities);
		
		context = this;
		
		load();
		initView();
		manageView();
		
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
	
	private void initView()
	{
		list = (ListView)findViewById(R.id.cities_list);
		edit = (EditText)findViewById(R.id.cities_search);
		prg = (FrameLayout)findViewById(R.id.progress);
	}
	
	private void manageView()
	{
		edit.setOnEditorActionListener(new OnEditorActionListener() {
			
			@Override
			public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
				if (actionId == EditorInfo.IME_ACTION_SEARCH){
					insertInList(sortList(edit.getText().toString()));
					return true;
				}
				return false;
			}
		});
	}
	
	private ArrayList<CitiesBean> sortList(String string) {
		ArrayList<CitiesBean> sortedList = new ArrayList<CitiesBean>();
		if (string.equals("")) return mCities;
		for (CitiesBean city : mCities){
			if (city.getName().toLowerCase().startsWith(string.toLowerCase())){
				sortedList.add(city);
			}
		}
		return sortedList;
	}
	
	private void insertInList(ArrayList<CitiesBean> array){
		if(mCityComparator!=null)
			Collections.sort(array, mCityComparator);
		list.setAdapter(new CitiesListAdapter(context, 0, array));
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		setResult(0);
		return super.onKeyDown(keyCode, event);
	}
	
	private void initList(ArrayList<CitiesBean> cities)
	{
		mCities = cities;
		insertInList(mCities);
	}
	
	private void load()
	{
		Thread thread = new Thread(new Runnable() {
			
			@Override
			public void run() {
				CitiesParser parser = new CitiesParser(URLManager.getCities());
				handler.sendMessage(handler.obtainMessage(0, parser.parse()));
			}
		});
		thread.start();
	}
	
}
