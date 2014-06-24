package ru.enter.dialogs;

import ru.enter.R;
import ru.enter.adapters.OrderUserAddressListAdapter;
import ru.enter.beans.AddressBean;
import ru.enter.beans.PersonBean;
import ru.enter.parsers.PersonInfoParser;
import ru.enter.utils.PreferencesManager;
import ru.enter.utils.URLManager;
import android.app.DialogFragment;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.TextView;

public class OrderUserAddressDialogFragment extends DialogFragment {

	private PersonBean mPersonBean;
	private OnSelectAddressListener mListener;
	private FrameLayout mAddressLoadingProgress;
	
	public void showAddressProgress () {
		if (mAddressLoadingProgress != null)
			mAddressLoadingProgress.setVisibility(View.VISIBLE);
	}
	
	public void hideAddressProgress () {
		if (mAddressLoadingProgress != null)
			mAddressLoadingProgress.setVisibility(View.GONE);
	}
	
	private Handler mHandler = new Handler() {
		public void handleMessage(Message msg) {
			if (mPersonBean.getAddressList().size() != 0) {
				if (mListener != null) {
					mListener.onSelectAddress(mPersonBean.getAddressList().get(msg.arg1));
					dismiss();
				}
			}
		};
	};
	private AddressLoader mAddressLoader;

	public static OrderUserAddressDialogFragment getInstance() {
		return new OrderUserAddressDialogFragment();
	}

	@Override
	public void onCreate(Bundle bundle) {
		super.onCreate(bundle);
		setStyle(0, R.style.custom_dialog_dark);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		View view = inflater.inflate(R.layout.order_dialog_address, null);
		getDialog().setTitle("Выберете адрес для доставки");
		
		mAddressLoadingProgress = (FrameLayout) view.findViewById(R.id.order_dialog_address_progress_frame);
		
		return view;
	}

	@Override
	public void onStart() {
		super.onStart();
		mPersonBean = new PersonBean();
		if (mAddressLoader != null) mAddressLoader.cancel(true);
		mAddressLoader = new AddressLoader();
		mAddressLoader.execute();
	}
	
	@Override
	public void onPause() {
		if (mAddressLoader != null) mAddressLoader.cancel(true);
		super.onPause();
	}
	
	private void start() {

		ListView list = (ListView) getView().findViewById(R.id.order_dialog_address_list);
		TextView empty = (TextView) getView().findViewById(R.id.order_dialog_address_empty_view);
		list.setEmptyView(empty);
		OrderUserAddressListAdapter adapter = new OrderUserAddressListAdapter(getActivity());
		adapter.setObjects(mPersonBean.getAddressList());
		adapter.setHandler(mHandler);
		list.setAdapter(adapter);

	}

	class AddressLoader extends AsyncTask<Void, Void, PersonBean> {

		@Override
		protected void onPreExecute() {
			showAddressProgress();
		}

		@Override
		protected PersonBean doInBackground(Void... params) {
			return new PersonInfoParser().parse(URLManager.getUser(PreferencesManager.getToken()));
		}

		@Override
		protected void onPostExecute(PersonBean result) {
			if (isCancelled())
				return;
			mPersonBean = result;
			hideAddressProgress();
			if (mPersonBean == null) {
				mPersonBean = new PersonBean();
			}
			start();
		}
	}

	public void setOnSelectAddressListener(OnSelectAddressListener listener) {
		mListener = listener;
	}

	public interface OnSelectAddressListener {
		void onSelectAddress(AddressBean bean);
	}

}