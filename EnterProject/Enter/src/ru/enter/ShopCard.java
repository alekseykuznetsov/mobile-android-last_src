package ru.enter;

import com.flurry.android.FlurryAgent;

import ru.enter.ImageManager.ImageDownloader;
import ru.enter.beans.ShopBean;
import ru.enter.parsers.ShopParser;
import ru.enter.utils.URLManager;
import ru.enter.widgets.HeaderFrameManager;
import android.app.ActivityGroup;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.text.Html;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TabHost;
import android.widget.TextView;

public class ShopCard extends ActivityGroup implements OnClickListener{

	public static final String SHOP_ID = "SHOP_ID";
	private int id;
	private String mFotoString;
	private LinearLayout head;
	private ProgressBar prg; 
	private Context context;
	private ScrollView scroll;
	private ImageView main_foto;
	private String lat;
	private String lon;
	private ImageDownloader iDownloader;
	private LinearLayout how_to_find,how_to_find_text;
	private TextView name,phone,path_walk, path_car,work_time,adress,desc;
	private Handler handler = new Handler(){
		public void handleMessage(android.os.Message msg) {
			ManageView((ShopBean)msg.obj);
		};
	};
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.shop_card);
		
		id = getIntent().getExtras().getInt(SHOP_ID);
		lat = getIntent().getExtras().getString(Maps.LATITUDE);
		lon = getIntent().getExtras().getString(Maps.LONGITUDE);
		
		context = this;
		iDownloader = new ImageDownloader(context);
		initView();
		
		Intent map_Intent = new Intent(this, MapRoute.class);
		map_Intent.putExtra(Maps.LATITUDE, Double.parseDouble(lat));
		map_Intent.putExtra(Maps.LONGITUDE, Double.parseDouble(lon));
		
		TabHost tabHost = (TabHost)findViewById(android.R.id.tabhost);
		tabHost.setBackgroundResource(R.drawable.product_card_image_background);
		tabHost.setPadding(1, 1, 1, 1);
		tabHost.setup(getLocalActivityManager());
		tabHost.addTab(tabHost.newTabSpec("tab1")
                .setIndicator("MAP")
                .setContent(map_Intent));
		tabHost.setClickable(false);
		load();
		
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
		main_foto = (ImageView)findViewById(R.id.shop_card_image);
		name = (TextView)findViewById(R.id.shop_card_name);
		phone = (TextView)findViewById(R.id.shop_card_tel);
		path_walk = (TextView)findViewById(R.id.shop_card_path_by_walk);
		path_car = (TextView)findViewById(R.id.shop_card_path_by_car);
		work_time = (TextView)findViewById(R.id.shop_card_work_time);
		adress = (TextView)findViewById(R.id.shop_card_adress);
		desc = (TextView)findViewById(R.id.shop_card_work_desc);
		head = (LinearLayout)findViewById(R.id.shop_card_tab_header_layout);
		scroll = (ScrollView)findViewById(R.id.shop_card_main_scroll);
		prg = (ProgressBar)findViewById(R.id.progress);
		how_to_find = (LinearLayout)findViewById(R.id.shop_card_how_to_find_linear);
		how_to_find_text = (LinearLayout)findViewById(R.id.shop_card_how_to_find_linear_text);
		
		
		scroll.setVisibility(View.GONE);
		how_to_find_text.setVisibility(View.GONE);
		
		how_to_find.setOnClickListener(this);
		main_foto.setOnClickListener(this);
		
		head.addView(HeaderFrameManager.getHeaderView(context, "Магазин", false));
	}
	private void ManageView(ShopBean obj)
	{
		if(obj != null)
		{
			scroll.setVisibility(View.VISIBLE);
			scroll.smoothScrollTo(0, 0);
			
			name.setText(obj.getName());
			String phone_num = obj.getPhone();
			if (!TextUtils.isEmpty(phone_num)){
				phone.setText(obj.getPhone());
				phone.setOnClickListener(this);
			}
			
			path_walk.setText(Html.fromHtml(obj.getWay_walk()!=null?"<b>Пешком: </b>" + obj.getWay_walk():""));
			path_car.setText(Html.fromHtml(obj.getWay_auto()!=null?"<b>На автомобиле:</b> "+obj.getWay_auto():""));
			work_time.setText(obj.getWorking_time());
			adress.setText(obj.getAddress());
			desc.setText(Html.fromHtml(obj.getDescription()!=null?obj.getDescription():""));
			mFotoString = obj.getFoto();
			iDownloader.download(mFotoString, main_foto);
		}
		prg.setVisibility(View.GONE);
		
		
	}
	private void load()
	{
		Thread th = new Thread(new Runnable() {
			
			@Override
			public void run() {
				String url = URLManager.getShop(id);
				ShopParser parser = new ShopParser(url);
				handler.sendMessage(handler.obtainMessage(0, parser.parse()));
			}
		});
		th.start();
	}
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.shop_card_how_to_find_linear:
			switch (how_to_find_text.getVisibility()) {
			case View.VISIBLE:
				how_to_find_text.setVisibility(View.GONE);
				break;
			case View.GONE:
				how_to_find_text.setVisibility(View.VISIBLE);
				break;
			default:
				break;
			}
			break;
		case R.id.shop_card_image:
			Intent galleryIntent = new Intent().setClass(context, ShopGallery.class);//TODO
			galleryIntent.putExtra(ShopGallery.SHOP_ID, id);
			startActivity(galleryIntent);
			break;
		case R.id.shop_card_tel:
			Uri uri = Uri.parse("tel://" + phone.getText());
	   	 	Intent intent = new Intent(Intent.ACTION_VIEW, uri);
	   	 	startActivity(intent);
			break;
		default:
			break;
		}
		
	}
}
