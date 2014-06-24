package ru.enter.fragments;

import ru.enter.ApplicationTablet;
import ru.enter.R;
import ru.enter.ShopsActivity;
import ru.enter.beans.ShopBean;
import ru.enter.dialogs.FullImageDialogFragment;
import ru.enter.maps.OnShopChangeListener;
import ru.enter.utils.URLManager;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.webimageloader.ImageLoader;
import com.webimageloader.ext.ImageHelper;
import com.webimageloader.transformation.SimpleTransformation;
import com.webimageloader.transformation.Transformation;

public class ShopTopFragment extends Fragment implements OnClickListener,OnShopChangeListener{
	
	private ImageHelper mImageLoader;
	
	private Button map;
	private Button description;
	private ShopBean mShop;
	
	public static ShopTopFragment getInstance(){
		return new ShopTopFragment();
	}

	// TODO подключить эту шнягу
	/*Transformation t = new SimpleTransformation() {

		@Override
		public Bitmap transform(Bitmap bitmap) {
			float max_size = 300;
						
			int x = bitmap.getWidth();
			int y = bitmap.getHeight();
			
			if (x >= max_size || y >= max_size){

				float kmax = Math.max((x / max_size), (y / max_size));
				x = (int) (x / kmax);
				y = (int) (y / kmax);
			}
			return Bitmap.createScaledBitmap(bitmap, x, y, true);
		}

		@Override
		public String getIdentifier() {
			return null;
		}
	};*/

	private void setupView(View view,ShopBean currentShop){

		mShop = currentShop;
		FrameLayout frame_with_images = (FrameLayout) view.findViewById(R.id.shop_fr_top_frame);
		frame_with_images.setOnClickListener(this);
		
		ImageView image = (ImageView) view.findViewById(R.id.shop_fr_top_image);
		mImageLoader.load(image, currentShop.getFoto());
		
//		ImageView magnifier = (ImageView) view.findViewById(R.id.shop_fr_top_image_magnifier);
//		magnifier.setOnClickListener(this);
		
		TextView address = (TextView) view.findViewById(R.id.shop_fr_top_text_address);
		address.setText(currentShop.getAddress());
		
		TextView time = (TextView) view.findViewById(R.id.shop_fr_top_text_time);
		time.setText(currentShop.getWorking_time());
		
		TextView phone = (TextView) view.findViewById(R.id.shop_fr_top_text_phone);
		phone.setText(currentShop.getPhone());
		
		description = (Button) view.findViewById(R.id.shop_fr_top_button_description);
		description.setOnClickListener(this);
		
		map = (Button) view.findViewById(R.id.shop_fr_top_button_map);
		map.setOnClickListener(this);
		
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
		
		ShopsActivity activity = (ShopsActivity) getActivity();
		
		ImageLoader loader = ApplicationTablet.getLoader(activity);
		mImageLoader = new ImageHelper(activity, loader)
        .setFadeIn(true)        
        .setLoadingResource(R.drawable.tmp_3)
        .setErrorResource(R.drawable.tmp_3);//TODO
				
		
		ShopBean currentShop = activity.getCurrentShop();	
		int mode = activity.getShopMode();
		View view = (View) inflater.inflate(R.layout.shop_fr_top, null);
		setupView(view, currentShop);
		setButtonMode(mode==ShopsActivity.MODE_MAP);
				
		return view;
	}

	
	@Override
	public void onStart() {
		super.onStart();
		onConfigurationChanged(getResources().getConfiguration());
	}	

	@Override
	public void onClick(View v) {
		FragmentManager manager = getFragmentManager();
		FragmentTransaction transaction = manager.beginTransaction();
		ShopsActivity activity = (ShopsActivity) getActivity();
		
		switch (v.getId()) {
		case R.id.shop_fr_top_button_description:
			description.setSelected(true);
			map.setSelected(false);
			activity.setShopMode(ShopsActivity.MODE_INFO);
			ShopBottomFragment bottomFragment = ShopBottomFragment.getInstance();
			transaction.replace(R.id.shop_ac_bottom_frame, bottomFragment);
			break;
		case R.id.shop_fr_top_button_map:
			description.setSelected(false);
			map.setSelected(true);
			activity.setShopMode(ShopsActivity.MODE_MAP);
			ShopBottomMapFragment bottomMapFragment = ShopBottomMapFragment.getInstance();					
			transaction.replace(R.id.shop_ac_bottom_frame, bottomMapFragment);
			break;		
		case R.id.shop_fr_top_frame:
			ShopBean currentShop = activity.getCurrentShop();			
			if (!TextUtils.isEmpty(currentShop.getFoto())){
				
//				FullImageDialogFragment image = FullImageDialogFragment.getInstance(currentShop.getFoto());
//				image.show(getFragmentManager(), "fullImage");
				
				String url = URLManager.getShopImageList(mShop.getId());
				FullImageDialogFragment image = FullImageDialogFragment.getInstance(url);
				image.show(getFragmentManager(), "fullImage");
			}
			break;
		default:
			break;
		}
		
		transaction.commit();
	}
	

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		
		LinearLayout linear = (LinearLayout) getView().findViewById(R.id.shop_fr_top_linear);
		RelativeLayout relative = (RelativeLayout) getView().findViewById(R.id.shop_fr_top_relative);
		FrameLayout frame = (FrameLayout) getView().findViewById(R.id.shop_fr_top_frame);
		
		if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
			
			linear.setOrientation(LinearLayout.HORIZONTAL);
			
			LayoutParams frame_params = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT, 2f);
			frame.setLayoutParams(frame_params);
			
			LayoutParams relative_params = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT, 1f);
			relative.setLayoutParams(relative_params);
		
		} else if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {
			
			linear.setOrientation(LinearLayout.VERTICAL);
			
			LayoutParams frame_params = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT, 1f);
			frame.setLayoutParams(frame_params);
			
			LayoutParams relative_params = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT, 2f);
			relative.setLayoutParams(relative_params);
			
		}
	}
	
	public void setButtonMode(boolean isMapButton){
		description.setSelected(!isMapButton);
		map.setSelected(isMapButton);
	}
	
	@Override
	public void onShopChanged(ShopBean shop) {
		setupView(getView(), shop);
	}
}
