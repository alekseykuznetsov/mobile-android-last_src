package ru.enter;

import java.util.ArrayList;

import com.flurry.android.FlurryAgent;
import com.google.analytics.tracking.android.EasyTracker;

import ru.enter.DataManagement.BasketManager;
import ru.enter.DataManagement.ProductCacheManager;
import ru.enter.ImageManager.ImageDownloader;
import ru.enter.beans.ProductBean;
import ru.enter.utils.ImageSizesEnum;
import ru.enter.utils.Utils;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.Display;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class Product3D extends Activity implements OnClickListener{

	private float x;
	private int num;
	private int current = 0, last = 0;
	private ImageView im3d;
	private ProductBean bean;
	private ImageDownloader mImageDownloader;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		EasyTracker.getInstance().setContext(this);
		
		setContentView(R.layout.pseudo3d);
		
		im3d = (ImageView)findViewById(R.id.pseudo3d_image);
		Button wish_list_btn = (Button)findViewById(R.id.pseudo3d_wish_btn);
		Button busket_btn = (Button)findViewById(R.id.pseudo3d_basket_btn);
		
		wish_list_btn.setOnClickListener(this);
		busket_btn.setOnClickListener(this);
		
		bean = ProductCacheManager.getInstance().getProductInfo();
		
		TextView price_tw = (TextView)findViewById(R.id.pseudo3d_price);
		TextView symbol = (TextView)findViewById(R.id.pseudo3d_price_symb);
		
		price_tw.setText(bean.getPriceFormattedString());
		symbol.setTypeface(Utils.getRoubleTypeFace(this));
		
		mImageDownloader = new ImageDownloader(this);
	    TextView mName = (TextView)findViewById(R.id.pseudo3d_name);
	    mName.setText(bean.getName());
		init(bean.getGallery_3d(ImageSizesEnum.s550));
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
	
	private void init(final ArrayList<String> images)
	{
		Display display = getWindowManager().getDefaultDisplay(); 
	    final int width = display.getWidth();
	    
		for(int i = 0; i < images.size();i++)
			mImageDownloader.download(images.get(i), null);
		
		mImageDownloader.download(images.get(0), im3d);
		
    	num = width/images.size();
	
		im3d.setOnTouchListener(new OnTouchListener() {
			
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				switch (event.getAction()) {
				case MotionEvent.ACTION_DOWN:
					x = event.getRawX();
					break;
				case MotionEvent.ACTION_MOVE:
					int dx = (int) (event.getX()- x);
					current = last + dx/num;
					if(current<0) current= images.size()+current;
					if(current >= images.size()) {current = current - images.size();}
					mImageDownloader.download(images.get(current), im3d);
					
					break;
				case MotionEvent.ACTION_UP:
					last = current;
					break;

				default:
					break;
				}
				
				return true;
			}
		});
	}
	
	private void showDialog(){
		AlertDialog.Builder dlg = new AlertDialog.Builder(this);				    			
        dlg
        .setMessage("Товар добавлен в корзину")
        .setPositiveButton("Перейти в корзину", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) { 
            	Intent intent = new Intent();
				intent.setClass(Product3D.this, BasketActivity.class);
				intent.putExtra(BasketActivity.SHOW_BUTTON, true);				
				startActivity(intent);
        }})
        .setNegativeButton("Продолжить покупки", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {   
            	
            }
        })
        .create().show(); 
	}
	
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.pseudo3d_wish_btn:
			
			break;
			
		case R.id.pseudo3d_basket_btn:
			if(bean!=null){
				BasketManager.addProduct(bean);				
				EasyTracker.getTracker().sendEvent("cart/add-product", "buttonPress", bean.getName(), (long) bean.getId());
				showDialog();
				Toast.makeText(this, "Товар добавлен в корзину", Toast.LENGTH_SHORT).show();
			}
			break;
			
		default:
			break;
		}
		
	}
}
