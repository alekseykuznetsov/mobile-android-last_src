package ru.enter.dialogs;

import java.util.ArrayList;

import ru.enter.R;
import ru.enter.adapters.ShopListAdapter;
import ru.enter.beans.ShopBean;
import android.app.DialogFragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.TextView;

public class ShopsListDialogFragment extends DialogFragment implements OnItemClickListener {

	private static ArrayList<ShopBean> mShopArray;
	private FrameLayout mShopsLoadingProgress;
	
	public void showShopProgress () {
		if (mShopsLoadingProgress != null)
			mShopsLoadingProgress.setVisibility(View.VISIBLE);
	}
	
	public void hideShopProgress () {
		if (mShopsLoadingProgress != null)
			mShopsLoadingProgress.setVisibility(View.GONE);
	}
	
	private OnSelectShopistener mListener;

	public static ShopsListDialogFragment getInstance(ArrayList<ShopBean> shops) {
		mShopArray = shops;
		return new ShopsListDialogFragment();
	}

	@Override
	public void onCreate(Bundle bundle) {
		super.onCreate(bundle);
		setStyle(0, R.style.custom_dialog_dark);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		View view = inflater.inflate(R.layout.shop_list_dialog, null);
		getDialog().setTitle("Выберете магазин");
		mShopsLoadingProgress = (FrameLayout) view.findViewById(R.id.shop_list_dialog_progress_frame);
//		start();
		
//	}
	
//	private void start() {

		ListView list = (ListView) view.findViewById(R.id.shop_list_dialog_list);
		TextView empty = (TextView) view.findViewById(R.id.shop_list_dialog_empty_view);
		list.setEmptyView(empty);
		ShopListAdapter adapter = new ShopListAdapter(getActivity());
		adapter.setObjects(mShopArray);
		list.setAdapter(adapter);
		list.setOnItemClickListener(this);
		return view;
	}
	
	public interface OnSelectShopistener {
		void OnSelectShop (ShopBean shop);
	}
	
	public void setOnSelectShopistener (OnSelectShopistener listener) {
		mListener = listener;
	}
	
	@Override
	public void onItemClick (AdapterView<?> arg0, View arg1, int position, long id) {
		if (mListener != null)
			mListener.OnSelectShop(mShopArray.get(position));
		dismiss();
	}
}