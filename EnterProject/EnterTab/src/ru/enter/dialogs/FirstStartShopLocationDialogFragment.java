package ru.enter.dialogs;

import java.util.ArrayList;

import ru.enter.R;
import ru.enter.beans.ShopBean;
import ru.enter.utils.PreferencesManager;
import ru.enter.utils.ShopLocator;
import ru.enter.utils.ShopLocator.OnNearestShopLocateListener;
import android.app.DialogFragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.analytics.tracking.android.EasyTracker;

public class FirstStartShopLocationDialogFragment extends DialogFragment implements OnClickListener {

	public static FirstStartShopLocationDialogFragment getInstance () {
		FirstStartShopLocationDialogFragment fr =new FirstStartShopLocationDialogFragment();
		fr.setCancelable(false);
		return fr;//new FirstStartShopLocationDialogFragment();
	}

	private TextView mTitle;
	private RelativeLayout mProgress;
	private Button mOk;
	private Button mAgain;
	private Button mChoose;
	private Button mCancel;
	private ShopLocator mLocator;
	private ShopBean mCurrentShop; 
	private void showProgress(){
		if (mProgress != null)
			mProgress.setVisibility(View.VISIBLE);
	}

	private void hideProgress(){
		if (mProgress != null)
			mProgress.setVisibility(View.GONE);
	}
	
	@Override
	public void onCreate (Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setStyle(STYLE_NO_TITLE, 0);
	}
	
	@Override
	public View onCreateView (LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = (View) inflater.inflate(R.layout.shop_location_dialog, null);

		
		mTitle = (TextView) view.findViewById(R.id.shop_location_dialog_title);

		mOk = (Button) view.findViewById(R.id.shop_location_dialog_btn_ok);
		mAgain = (Button) view.findViewById(R.id.shop_location_dialog_btn_again);
		mChoose = (Button) view.findViewById(R.id.shop_location_dialog_btn_choose);
		mCancel = (Button) view.findViewById(R.id.shop_location_dialog_btn_cancel);
		mProgress = (RelativeLayout) view.findViewById(R.id.shop_location_dialog_progress_layout);
		
		mOk.setOnClickListener(this);
		mAgain.setOnClickListener(this);
		mChoose.setOnClickListener(this);
		mCancel.setOnClickListener(this);

		startSearch();
		return view;
	}
	
	private void startSearch(){
		
		mLocator = new ShopLocator(getActivity());
		mLocator.setOnNearestShopLocateListener(new OnNearestShopLocateListener() {
			
			@Override
			public void onStartLocate () {
				showProgress();
				
			}
			
			@Override
			public void onShopLocated (ShopBean shop) {
				dismiss();
			}
			
			@Override
			public void onFailLocate () {				
				dismiss();
			}

			@Override
			public void onBackgroundLocated(ShopBean shop) {
				// TODO Auto-generated method stub
				showSuccessDialog(shop);
				hideProgress();
				PreferencesManager.setFirstStartCatalog(0);
			}

			@Override
			public void onBackgroundFailLocate() {
				// TODO Auto-generated method stub
				hideProgress();
				PreferencesManager.setFirstStartCatalog(0);
				dismiss();				
			}
		});
		mLocator.startBackground();
		
	}

	private void showSuccessDialog(ShopBean shop){
	
		String text = "Ваше местонахождение определено в магазине " + shop.getName() + ". Хотите ли Вы просмотреть каталог товаров, находящихся в данном магазине?";
		mTitle.setText(text);
		mOk.setVisibility(View.VISIBLE);
		mAgain.setVisibility(View.GONE);
		mChoose.setVisibility(View.GONE);
		mCancel.setText("Смотреть общий");
		mCurrentShop = shop;
	}
	
		
	
	@Override
	public void onClick (View v) {

		ArrayList<ShopBean> shops = (ArrayList<ShopBean>) mLocator.getLoadedShops();
		final TextView shop = (TextView) getActivity().findViewById(R.id.catalog_ac_txt_shop);
		
		switch (v.getId()) {
		case R.id.shop_location_dialog_btn_ok:
			EasyTracker.getTracker().sendEvent("filter/shop", "buttonPress", mCurrentShop.getName(), (long) mCurrentShop.getId());
			PreferencesManager.setUserCurrentShopId(mCurrentShop.getId());
			PreferencesManager.setUserCurrentShopName(mCurrentShop.getName());
			shop.setText(mCurrentShop.getName());			
			dismiss();
			break;		
		case R.id.shop_location_dialog_btn_cancel:
			PreferencesManager.setUserCurrentShopId(0);
			PreferencesManager.setUserCurrentShopName("");
			shop.setText("Выберете магазин");
			dismiss();
			break;
		}

	}

}
