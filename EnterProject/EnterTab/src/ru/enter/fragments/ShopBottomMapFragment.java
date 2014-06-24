package ru.enter.fragments;

import java.util.ArrayList;

import ru.enter.Maps;
import ru.enter.R;
import ru.enter.ShopsActivity;
import ru.enter.beans.ShopBean;
import ru.enter.utils.LocalActivityManagerFragment;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TabHost;
import android.widget.TabHost.TabSpec;

public class ShopBottomMapFragment extends LocalActivityManagerFragment{

	private static String TAB_MAP = "map";
	public static String ARG_ZOOM = "zoom";
	public static String ARG_FIRSTRUN = "firstRun";
	
	TabHost thMap;
	ImageView ivTarget;
	ImageView ivMarker;
	ImageView ivArea;
	
	public static ShopBottomMapFragment getInstance(){
		return new ShopBottomMapFragment();
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
		View view =  inflater.inflate(R.layout.shop_fr_bottom_map, null);
		initViews(view,getActivity());
		
		ShopsActivity me = (ShopsActivity)getActivity();
		ArrayList<ShopBean> shops = me.getShopArray();
		ShopBean currentShop = me.getCurrentShop();
		
		Bundle args = getArguments();
		boolean firstRun = args!=null && args.getBoolean(ARG_FIRSTRUN);
		
		Bundle shopsBundle = new Bundle();
		shopsBundle.putSerializable(Maps.EXTRA_SHOPS, shops);
		shopsBundle.putSerializable(Maps.EXTRA_START_SHOP, currentShop);
		shopsBundle.putString(Maps.EXTRA_STATE, "shop");
		//shopsBundle.putString(Maps.EXTRA_STATE, "shopcheck");
		shopsBundle.putBoolean(Maps.EXTRA_FORWARD, true);
		shopsBundle.putBoolean(Maps.EXTRA_FIRST_START,firstRun);
		//Log.d("shopMapFr","r="+firstRun);

		addTab(TAB_MAP, Maps.class,shopsBundle);
		
		return view;
	}
	
	@Override
	public void onResume() {
		super.onResume();
		Bundle args = getArguments();
		if(args!=null){
			String init = args.getString(ARG_ZOOM);
			if(init!=null){
				zoomToArea();
			}
		}
	}
	
	private void initViews(View v,final Activity act){
		thMap = (TabHost)v.findViewById(android.R.id.tabhost);
		thMap.setup(mLocalActivityManager);
		ivTarget = (ImageView)v.findViewById(R.id.shop_fr_bottom_map_image_target);
		ivMarker = (ImageView)v.findViewById(R.id.shop_fr_bottom_map_image_marker);
		ivArea = (ImageView)v.findViewById(R.id.shop_fr_bottom_map_image_area);
		//Target button(first from top) : zoom & move map
		ivTarget.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				Intent i = new Intent().setAction(Maps.RECEIVER_MAPS_ZOOM);
				act.sendBroadcast(i);
			}
		});
		//Marker button(second from top) : set marker mode (all/current)
		ivMarker.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				ShopsActivity me = (ShopsActivity)getActivity();
				me.toggleMapMarkerMode();
			}
		});
		//Area button (bottom button) : set map mode (normal/fullscreen)
		ivArea.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				ShopsActivity me = (ShopsActivity)getActivity();
				me.toggleMapViewMode();
			}
		});
	}
		
	private void addTab (String name, Class<?> cls, Bundle extras) {
		Intent intent = new Intent();
		intent.setClass(getActivity(), cls);
		if (extras != null) intent.putExtras(extras);
		TabSpec spec = thMap.newTabSpec(name).setIndicator("").setContent(intent);
		thMap.addTab(spec);
	}
	
	public void zoomToArea(){
		Intent i = new Intent();
		i.setAction(Maps.RECEIVER_MAPS_ZOOM);
		getActivity().sendBroadcast(i);
	}
	
}
