package ru.enter.fragments;

import java.util.ArrayList;
import java.util.List;

import ru.enter.R;
import ru.enter.ShopsActivity;
import ru.enter.adapters.ShopLeftListFragmentAdapter;
import ru.enter.beans.ShopBean;
import ru.enter.maps.OnShopChangeListener;
import ru.enter.route.Road;
import ru.enter.utils.Utils;
import android.app.ActionBar;
import android.app.ActionBar.OnNavigationListener;
import android.app.ListFragment;
import android.app.Service;
import android.os.Bundle;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.SpinnerAdapter;
import android.widget.Toast;

public class ShopLeftFragment extends ListFragment implements OnShopChangeListener{//,OnNavigationListener {
	public static String ARG_TAPONFIRST;
	ShopLeftListFragmentAdapter adapter;
	
	public static ShopLeftFragment getInstance() {
		return new ShopLeftFragment();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

		ShopsActivity mActivity = (ShopsActivity) getActivity();
		ArrayList<ShopBean> shops = mActivity.getShopArray();
		adapter = new ShopLeftListFragmentAdapter(mActivity, shops);
		setListAdapter(adapter);
		//setHasOptionsMenu(true);
		
		//RelativeLayout customActionBarView = (RelativeLayout)inflater.inflate(R.layout.actionbar_custom, null);
		//ActionBar actionBar = getActivity().getActionBar();
		//actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM | ActionBar.DISPLAY_SHOW_HOME);
		//((LinearLayout)customActionBarView.findViewById(R.id.actionbar_nav_left)).addView(new SearchView(getActivity()));
		//((LinearLayout)customActionBarView.findViewById(R.id.actionbar_nav_right)).addView(b);
		//actionBar.setCustomView(customActionBarView);
	    //actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_LIST);
	    //SpinnerAdapter sa = new ArrayAdapter<String>(getActivity(),android.R.array.imProtocols,android.R.layout.simple_spinner_dropdown_item);
	    //actionBar.setListNavigationCallbacks(sa, this);
	    //actionBar.setDisplayHomeAsUpEnabled(true);
//		
//		LinearLayout customActionBarView = (LinearLayout)inflater.inflate(R.layout.actionbar_custom, null);
//		ActionBar actionBar = getActivity().getActionBar();
//		actionBar.setDisplayHomeAsUpEnabled(true);
//		
//	    actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM | ActionBar.DISPLAY_SHOW_HOME);
//	    actionBar.setCustomView(customActionBarView);

		return super.onCreateView(inflater, container, savedInstanceState);
	}

	@Override
	public void onResume() {
		super.onResume();
		getListView().setOverScrollMode(ListView.OVER_SCROLL_NEVER);
		getListView().setDivider(getResources().getDrawable(R.drawable.personal_order_divider_line));
		getListView().setVerticalScrollBarEnabled(false);
		Bundle args = getArguments();
		boolean tapOnFirst = args!=null && args.getBoolean(ARG_TAPONFIRST);
		if(tapOnFirst)
			getListView().performItemClick(getListView(), 0, adapter.getItemId(0));
	}

	@Override
	public void onListItemClick(ListView l, View v, int position, long id) {
		super.onListItemClick(l, v, position, id);
		
		ShopsActivity mActivity = (ShopsActivity) getActivity();
		ArrayList<ShopBean> array = mActivity.getShopArray();
		
		ShopBean shop = (!Utils.isEmptyList(array)) ? array.get(position) : null;
				
//		ShopBean shop = (array!=null && array.size()>position+1)? array.get(position) : null;
		if(shop!=null){ 
			l.requestFocusFromTouch();
			l.setSelection(position);
	
			adapter.setCurrent(position);
			adapter.notifyDataSetChanged();
			
			mActivity.setCurrentShop(shop);
			mActivity.startRight();
		}

	}
	
	public void updateShopRoute(int shopId,Road route){
		adapter.updateShopRoute(shopId, route);
	}
	
	public void setRoutes(SparseArray<Road> routes){
		adapter.setRoutes(routes);
	}
	
	@Override
	public void onShopChanged(ShopBean shop) {
		adapter.setCurrent(shop);
	}
	
	/*
	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		//new NavMenuHandler(getActivity()).onCreateOptionsMenu(menu, inflater);
		//LayoutInflater inf = (LayoutInflater)getActivity().getSystemService(Service.LAYOUT_INFLATER_SERVICE);
		//menu.add("Save");
		//menu.getItem(0).setActionView(R.layout.actionbar_custom);		
		//super.onCreateOptionsMenu(menu, inflater);
		inflater.inflate(R.menu.shops_menu, menu);
		
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()){
        case android.R.id.home:
           
           break;
        default:
        	Toast.makeText(getActivity(), item.getTitle(), Toast.LENGTH_SHORT).show();
        	break;
        }
        			
		return super.onOptionsItemSelected(item);
	}

	@Override
	public boolean onNavigationItemSelected(int arg0, long arg1) {
		Toast.makeText(getActivity(), "num:"+arg0, Toast.LENGTH_SHORT).show();
		return false;
	}*/
	
}