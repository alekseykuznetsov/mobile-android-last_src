package ru.enter.fragments;

import java.util.ArrayList;

import ru.enter.PersonalActivity;
import ru.enter.R;
import ru.enter.adapters.PersonalOrderLeftListAdapter;
import ru.enter.beans.OrderBean;
import ru.enter.dialogs.ProgressDialogFragment;
import ru.enter.parsers.MyOrdersParser;
import ru.enter.utils.PreferencesManager;
import ru.enter.utils.URLManager;
import ru.enter.utils.Utils;
import android.app.Fragment;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.TextView;

public class PersonalLeftFragment extends Fragment implements OnItemClickListener{

	private static final String CURRENT_ORDER = "current_order";
	private int currentOrderId = 0;
	private ArrayList<OrderBean> mOrderList;
	private UserOrders mUserOrdersLoader;
	private PersonalOrderLeftListAdapter mAdapter;
	public static PersonalLeftFragment getInstance() {
		return new PersonalLeftFragment();
	}

	@Override
	public void onPause() {
		if (mUserOrdersLoader != null) mUserOrdersLoader.cancel(true);
		super.onPause();
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		if (savedInstanceState != null){
			currentOrderId = savedInstanceState.getInt(CURRENT_ORDER, 0);
		}
		else{
			currentOrderId = 0;
		}
		
		View view = inflater.inflate(R.layout.personal_order_fr_left, null);
		return view;
	}
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		if (mOrderList == null) {
			if (mUserOrdersLoader != null) mUserOrdersLoader.cancel(true);
			mUserOrdersLoader = new UserOrders();
			mUserOrdersLoader.execute();
		}
		else{
			start();
		}
		
	}
	
	private void start(){

		TextView empty = (TextView) getView().findViewById(R.id.personal_order_fr_left_list_empty);
		ListView listView = (ListView) getView().findViewById(R.id.personal_order_fr_left_list_listview);
		listView.setEmptyView(empty);
		mAdapter = new PersonalOrderLeftListAdapter(getActivity(), mOrderList);
		listView.setAdapter(mAdapter);
		listView.setOnItemClickListener(this);
		if (mOrderList.size() != 0){
			
			OrderBean currentOrder = mOrderList.get(currentOrderId);
			((PersonalActivity) getActivity()).startRight(currentOrder);
		}
	}
	
	@Override
	public void onSaveInstanceState(Bundle savedInstanceState) {
		savedInstanceState.putInt(CURRENT_ORDER, currentOrderId);
	}

	private class UserOrders extends AsyncTask<Void, Void, ArrayList<OrderBean>> {
		
		ProgressDialogFragment progress;
		@Override
		protected void onPreExecute() {
			progress = ProgressDialogFragment.getInstance();
			progress.show(getFragmentManager(), "progress");
			progress.setOnCancelListener(mCancel);
		}

		private OnCancelListener mCancel = new OnCancelListener() {
			
			@Override
			public void onCancel(DialogInterface dialog) {
				mUserOrdersLoader.cancel(true);
				dialog.dismiss();
				getActivity().finish();
			}
		};
		
		@Override
		protected ArrayList<OrderBean> doInBackground(Void... params) {
			ArrayList<OrderBean> answer;
			
			answer = new MyOrdersParser(getActivity()).parse(URLManager.getOrders(PreferencesManager.getToken()));
			return answer;
		}
		
		@Override
		protected void onPostExecute(ArrayList<OrderBean> result) {
			if(isCancelled())
				return;
			progress.dismiss();
			if (Utils.isEmptyList(result)){
				result = new ArrayList<OrderBean>();
			}
				mOrderList = result;
				start();
		}
	}
	
	@Override
	public void onItemClick(AdapterView<?> arg0, View view, int position, long id) {

		mAdapter.setSelectedChild(view);
		
		currentOrderId = position;
		OrderBean currentOrder = mOrderList.get(currentOrderId);
		((PersonalActivity) getActivity()).startRight(currentOrder);
	}
}
