package ru.enter;

import java.util.ArrayList;

import ru.enter.DataManagement.BasketManager;
import ru.enter.base.BaseActivity;
import ru.enter.beans.ProductBean;
import ru.enter.dialogs.alert.BasketAddDialogFragment;
import ru.enter.utils.ImageSizesEnum;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.Display;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.flurry.android.FlurryAgent;
import com.google.analytics.tracking.android.EasyTracker;
import com.webimageloader.ImageLoader;
import com.webimageloader.ImageLoader.Listener;
import com.webimageloader.ext.ImageHelper;

public class ProductCard360Activity extends BaseActivity implements OnTouchListener{

	private static final String PRODUCT_BEAN = "product_bean";
	
	private ImageView mImage;
	
	private ImageLoader mLoader;
	private ImageHelper mHelper;

	private float x;
	private int mDeltaXToChangeImage;
	private int current = 0, last = 0, prev = -1;

	private ArrayList<String> mImageUrls;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.product_card_360_ac);
		setTitleLeft(getResources().getString(R.string.actionbar_gallery3d));

		mLoader = ApplicationTablet.getLoader(this);
		mHelper = new ImageHelper(this, mLoader);

		final ProductBean productBean = (ProductBean) getIntent().getSerializableExtra(PRODUCT_BEAN);
		mImageUrls = getNotAllImages(productBean.getGallery_3d(ImageSizesEnum.s550));
//		mImageUrls = productBean.getGallery_3d(ImageSizesEnum.s550);

		mImage = (ImageView) findViewById(R.id.product_card_360_ac_gallery);
		TextView title_view = (TextView) findViewById(R.id.product_card_360_ac_text_title);
		TextView name_view = (TextView) findViewById(R.id.product_card_360_ac_text_name);
		TextView price_view = (TextView) findViewById(R.id.product_card_360_ac_text_price);
		RelativeLayout relative = (RelativeLayout) findViewById(R.id.product_card_360_ac_relative_gallery);

		title_view.setText(productBean.getPrefix());
		name_view.setText(productBean.getShortname());
		price_view.setText(String.valueOf((int) productBean.getPrice()));
		relative.setOnTouchListener(this);
		
		boolean isBuyable = (productBean.getBuyable() == 1);
		Button buttonBuy = (Button) findViewById(R.id.product_card_360_ac_button_buy);
		buttonBuy.setEnabled(isBuyable);

		if (isBuyable) {
			buttonBuy.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					BasketManager.addProduct(productBean);
					EasyTracker.getTracker().sendEvent("cart/add-product", "buttonPress", productBean.getName(), (long) productBean.getId());
					BasketAddDialogFragment dialog = BasketAddDialogFragment.getInstance();
					dialog.setProductMessage(productBean.getName());
					dialog.show(getFragmentManager(), "basket_add");
				}
			});
		}
		
		init();
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
	
	private void init () {
		Display display = getWindowManager().getDefaultDisplay();
		int width = display.getWidth();
		
		mDeltaXToChangeImage = width / mImageUrls.size();

		for (int i = 0; i < mImageUrls.size(); i++) {
			mLoader.preload(mImageUrls.get(i));
		}

		mHelper.load(mImage, mImageUrls.get(0));
	}
	
	private ArrayList<String> getNotAllImages (ArrayList<String> urls) {
		ArrayList<String> result = new ArrayList<String>();
		for (int i=0; i < urls.size(); i+=2) {
			result.add(urls.get(i));
		}
		
		return result;
	}

	@Override
	public boolean onTouch(View v, MotionEvent event) {
		
		switch (event.getAction()) {
		
		case MotionEvent.ACTION_DOWN:
			x = event.getRawX();
			break;
			
		case MotionEvent.ACTION_MOVE:
			int dx = (int) (event.getX() - x);
			current = last + dx / mDeltaXToChangeImage;

			if (current < 0)
				current = mImageUrls.size() + current;
			if (current >= mImageUrls.size()) {
				current = current - mImageUrls.size();
			}
			
			//TODO
			if(prev == current)
				break;
			prev = current;

			Bitmap b = mLoader.load(mImage, mImageUrls.get(current), new Listener<ImageView>() {
				
			    @Override
			    public void onSuccess(ImageView v, Bitmap b) {
			        v.setImageBitmap(b);
			    }

			    @Override
			    public void onError(ImageView v, Throwable t) { }
			    
			});

			if (b != null) mImage.setImageBitmap(b);
			
			break;
			
		case MotionEvent.ACTION_UP:
			last = current;
			break;

		default:
			break;
		}

		return true;
	}

}