package ru.enter;

import com.flurry.android.FlurryAgent;
import com.google.analytics.tracking.android.EasyTracker;

import ru.enter.DataManagement.BasketManager;
import ru.enter.DataManagement.ProductCacheManager;
import ru.enter.adapters.ProductCarouselAdapter;
import ru.enter.beans.ProductBean;
import ru.enter.gallery.CoverAdapterView;
import ru.enter.gallery.CoverFlow;
import ru.enter.utils.ImageSizesEnum;
import ru.enter.utils.Utils;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

public class ProductCarousel extends Activity implements OnClickListener, CoverAdapterView.OnItemClickListener{

	public static final String POSITION = "position";
	
	private ProductBean mBean;
    
	@Override
	public void onCreate(Bundle savedInstanceState) {
		
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.photo_cover_flow_view);
	    
	    getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
	    	    WindowManager.LayoutParams.FLAG_FULLSCREEN);
	    
	    Button wish_list_btn = (Button)findViewById(R.id.photo_cover_flow_wish_btn);
		Button busket_btn = (Button)findViewById(R.id.photo_cover_flow_basket_btn);
		wish_list_btn.setOnClickListener(this);
		busket_btn.setOnClickListener(this);
		
	    CoverFlow coverFlow = (CoverFlow)findViewById(R.id.gallery_cover);
	    coverFlow.setSpacing(-20);
	    coverFlow.setmScale(1);
			
		mBean = ProductCacheManager.getInstance().getProductInfo();
		
	    TextView price_tw = (TextView)findViewById(R.id.photo_cover_flow_price);
		TextView symbol = (TextView)findViewById(R.id.photo_cover_flow_price_symb);
		
		price_tw.setText(mBean.getPriceFormattedString());
		symbol.setTypeface(Utils.getRoubleTypeFace(this));
	    
		TextView  mName = (TextView)findViewById(R.id.photo_cover_flow_name);
		mName.setText(mBean.getName());
		
		coverFlow.setAdapter(new ProductCarouselAdapter(this, mBean.getGallery(ImageSizesEnum.s550)));
	    coverFlow.setOnItemClickListener(this);
	    
	    super.onCreate(savedInstanceState);
	    EasyTracker.getInstance().setContext(this);
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
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.photo_cover_flow_wish_btn:
			break;
			
		case R.id.photo_cover_flow_basket_btn:
			if(mBean!=null){
				BasketManager.addProduct(mBean);
				EasyTracker.getTracker().sendEvent("cart/add-product", "buttonPress", mBean.getName(), (long) mBean.getId());
				showDialog();
			}
			break;
			
		default:
			break;
		}
	}
	
	@Override
	public void onItemClick(CoverAdapterView<?> parent, View view,
			int position, long id) {
		Intent intent = new Intent().setClass(ProductCarousel.this, ProductGallery.class);
		intent.putExtra(POSITION, position);
		startActivity(intent);
	}

	private void showDialog(){
		AlertDialog.Builder dlg = new AlertDialog.Builder(this);				    			
        dlg
        .setMessage("Товар добавлен в корзину")
        .setPositiveButton("Перейти в корзину", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) { 
            	Intent intent = new Intent();
				intent.setClass(ProductCarousel.this, BasketActivity.class);
				intent.putExtra(BasketActivity.SHOW_BUTTON, true);
				startActivity(intent);
        }})
        .setNegativeButton("Продолжить покупки", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {   
            	
            }
        })
        .create().show(); 
	}
}
