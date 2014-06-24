package ru.enter.dialogs;

import org.json.JSONException;
import org.json.JSONObject;

import ru.enter.R;
import ru.enter.beans.AddressBean;
import ru.enter.beans.PersonBean;
import ru.enter.fragments.PersonalFormFragment;
import ru.enter.parsers.AddressParser;
import ru.enter.utils.PreferencesManager;
import ru.enter.utils.URLManager;
import ru.enter.utils.Utils;
import android.app.DialogFragment;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnKeyListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

public class AddressDialogFragment extends DialogFragment implements OnClickListener {

	private static final String POSITION = "position";
	public static final String ADDRESS_STREET = "address_street";
	public static final String ADDRESS_HOUSE = "address_building";
	public static final String ADDRESS_HOUSING = "address_number";
	public static final String ADDRESS_FLOOR = "address_floor";
	public static final String ADDRESS_FLAT = "address_apartment";

	private AddressBean mCurrentAddress;
	private PersonBean mPersonData;
	private OnAddAddressListener mListener;

	private TextView mStreet;
	private TextView mHouse;
	private TextView mHousing;
	private TextView mFloor;
	private TextView mFlat;

	private Button mSend;
	private FrameLayout mProgress;
	private SaveAddress mSaveAddressLoader;

	private void showProgress () {
		mProgress.setVisibility(View.VISIBLE);
	}

	private void hideProgress () {
		mProgress.setVisibility(View.GONE);
	}

	public static AddressDialogFragment getInstance (int position) {
		Bundle bundle = new Bundle();
		bundle.putInt(POSITION, position);
		AddressDialogFragment fragment = new AddressDialogFragment();
		fragment.setArguments(bundle);
		return fragment;
	}

	@Override
	public void onCreate (Bundle bundle) {
		super.onCreate(bundle);

		setStyle(0, R.style.custom_dialog_dark);
	}

	@Override
	public void onPause () {
		if (mSaveAddressLoader != null)
			mSaveAddressLoader.cancel(true);
		super.onPause();
	}

	@Override
	public View onCreateView (LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		int position = getArguments().getInt(POSITION, -1);

		PersonalFormFragment fragment = (PersonalFormFragment) getFragmentManager().findFragmentByTag("personalForm");
		mPersonData = fragment.getInfo();

		getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

		View view = inflater.inflate(R.layout.personal_dialog_address, null);
		mProgress = (FrameLayout) view.findViewById(R.id.personal_dialog_address_progress_frame);
		mStreet = (EditText) view.findViewById(R.id.personal_dialog_address_edittext_street);
		mHouse = (EditText) view.findViewById(R.id.personal_dialog_address_edittext_house);
		mHousing = (EditText) view.findViewById(R.id.personal_dialog_address_edittext_housing);
		mFloor = (EditText) view.findViewById(R.id.personal_dialog_address_edittext_floor);
		mFlat = (EditText) view.findViewById(R.id.personal_dialog_address_edittext_flat);

		mSend = (Button) view.findViewById(R.id.personal_dialog_address_button_send);
		mSend.setOnClickListener(this);

		if (position != -1) {
			getDialog().setTitle("Редактирование адреса");

			if (!TextUtils.isEmpty(mPersonData.getAddressList().get(position).getStreet())) {
				mStreet.setText(mPersonData.getAddressList().get(position).getStreet());
				mHouse.setText(mPersonData.getAddressList().get(position).getHouse());
				mHousing.setText(mPersonData.getAddressList().get(position).getHousing());
				mFloor.setText(mPersonData.getAddressList().get(position).getFloor());
				mFlat.setText(mPersonData.getAddressList().get(position).getFlat());
			} else {
				mStreet.setText(mPersonData.getAddressList().get(position).getAddress());
			}
			mSend.setText("Сохранить");
			mCurrentAddress = mPersonData.getAddressList().get(position);
		} else {
			getDialog().setTitle("Добавьте адрес доставки");
			mCurrentAddress = null;
		}
		return view;
	}

	@Override
	public void onClick (View v) {
		String address_street = mStreet.getText().toString();
		String address_house = mHouse.getText().toString();
		String address_housing = mHousing.getText().toString();
		String address_floor = mFloor.getText().toString();
		String address_flat = mFlat.getText().toString();

		if (!TextUtils.isEmpty(address_street)) {
			if (!TextUtils.isEmpty(address_house)) {
				JSONObject object = new JSONObject();
				try {
					object.put("first_name", mPersonData.getName());
					object.put("last_name", mPersonData.getLastName());
					object.put("middle_name", mPersonData.getMiddleName());
					object.put("is_primary", 0);// TODO
					object.put("geo_id", PreferencesManager.getCityid());
					object.put("address", "");

					object.put(ADDRESS_STREET, address_street);
					object.put(ADDRESS_HOUSE, address_house);
					object.put(ADDRESS_HOUSING, address_housing);
					object.put(ADDRESS_FLOOR, address_floor);
					object.put(ADDRESS_FLAT, address_flat);

					object.put("zip_code", 1);// TODO
				} catch (JSONException e) {
					e.printStackTrace();
				}
				if (mSaveAddressLoader != null)
					mSaveAddressLoader.cancel(true);
				mSaveAddressLoader = new SaveAddress();
				mSaveAddressLoader.execute(object.toString());
			} else
				Toast.makeText(getActivity(), "Введите номер дома", Toast.LENGTH_SHORT).show();
		} else
			Toast.makeText(getActivity(), "Введите название улицы", Toast.LENGTH_SHORT).show();
	}

	private class SaveAddress extends AsyncTask<String, Void, Integer> {

		@Override
		protected void onPreExecute () {
			mSend.setEnabled(false);
			showProgress();
		}

		@Override
		protected Integer doInBackground (String... data_to_send) {
			String request = "";
			if (mCurrentAddress != null) {
				// редактирование адреса
				request = Utils.sendPostData(data_to_send[0],
						URLManager.getUpdateAddress(PreferencesManager.getToken(), mCurrentAddress.getId()));
				return new AddressParser(getActivity()).parseString(request);
			} else {
				// добавление адреса
				request = Utils.sendPostData(data_to_send[0], URLManager.getCreateAddress(PreferencesManager.getToken()));
				return new AddressParser(getActivity()).parseString(request);
			}
		}

		protected void onPostExecute (Integer result) {
			if (isCancelled())
				return;
			if (result != null && result >= 0) {
				if (mCurrentAddress != null) {
					Toast.makeText(getActivity(), "Адрес отредактирован", Toast.LENGTH_SHORT).show();
				} else {
					Toast.makeText(getActivity(), "Адрес добавлен", Toast.LENGTH_SHORT).show();
				}
				if (mListener != null)
					mListener.onAddAddress();
			} else
				Toast.makeText(getActivity(), "Сохранить адрес не удалось", Toast.LENGTH_SHORT).show();

			hideProgress();
			dismiss();
		}
	}

	public void setOnAddAddressListener (OnAddAddressListener listener) {
		mListener = listener;
	}

	public interface OnAddAddressListener {
		void onAddAddress ();
	}
}
