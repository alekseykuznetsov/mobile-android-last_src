package ru.enter.fragments;

import java.util.ArrayList;

import ru.enter.R;
import ru.enter.adapters.PersonalFormListAdapter;
import ru.enter.beans.AddressBean;
import ru.enter.beans.PersonBean;
import ru.enter.dialogs.AddressDialogFragment;
import ru.enter.dialogs.AddressDialogFragment.OnAddAddressListener;
import ru.enter.dialogs.alert.DeleteAddressDialogFragment;
import ru.enter.parsers.AddressParser;
import ru.enter.parsers.PersonInfoParser;
import ru.enter.utils.PreferencesManager;
import ru.enter.utils.URLManager;
import android.app.Fragment;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class PersonalFormFragment extends Fragment implements OnClickListener {

	private static final int NEW_ADDRESS = -1;
	
	private boolean mLoaded = false;
	
	private PersonalFormListAdapter mAdapter;
	private PersonBean mPerson;
	private FrameLayout mProgress;

	private ParseRequest mParseRequestLoader;
	private DeleteAddress mDeleteAddressloader;
	
	private void showProgress(){
		mProgress.setVisibility(View.VISIBLE);
	}
	
	private void hideProgress(){
		mProgress.setVisibility(View.GONE);
	}
	
	private OnClickListener mEditAddressClick = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			int tag = (Integer) v.getTag();
			AddressDialogFragment addressDialog = AddressDialogFragment.getInstance(tag);
			addressDialog.setOnAddAddressListener(new OnAddAddressListener() {
				@Override
				public void onAddAddress() {
					reloadPersonalInfo();
				}
			});
			addressDialog.show(getFragmentManager(), "addressDialog");
		}
	};

	private OnItemLongClickListener mDeleteListener = new OnItemLongClickListener() {

		@Override
		public boolean onItemLongClick(AdapterView<?> arg0, View view, int position, final long id) {
			
			showDeleteAddressDialog(mPerson.getAddressList().get(position-1).getAddress(), (int)id);
			return true;
		}
	};

	public static PersonalFormFragment getInstance() {
		return new PersonalFormFragment();
	}

	public void setInfo(PersonBean person){
		mPerson = person;
	}
	
	public PersonBean getInfo (){
		return mPerson;
	}
	
	@Override
	public void onPause() {
		if (mParseRequestLoader != null) mParseRequestLoader.cancel(true);
		if(mDeleteAddressloader != null) mDeleteAddressloader.cancel(true);
		super.onPause();
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		View view = inflater.inflate(R.layout.personal_form_fr, null);
		View footerView = inflater.inflate(R.layout.personal_form_fr_list_footer, null);
		mProgress = (FrameLayout) view.findViewById(R.id.personal_form_fr_progress_frame);
		ListView addressList = (ListView) view.findViewById(R.id.personal_form_fr_listview_address);
		addressList.addFooterView(footerView);
		addressList.setOnItemLongClickListener(mDeleteListener );

		View header = inflater.inflate(R.layout.personal_form_fr_list_header, null);
		addressList.addHeaderView(header);
		
		return view;
	}
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		if (mPerson == null) {
			reloadPersonalInfo();
		}
		else 
			start();
	}
	private void start(){
		
		refreshPersonalInfo();
		
		// список адресов
		ListView listView = (ListView) getView().findViewById(R.id.personal_form_fr_listview_address);
		
		mAdapter = new PersonalFormListAdapter(getActivity());
		ArrayList<AddressBean> addresses = mPerson.getAddressList();
		
		if(addresses != null){
			mAdapter.setObjects(addresses);
			mAdapter.setOnClickListener(mEditAddressClick );
		}else{
			mAdapter.clearObjects();
		}
		
		mAdapter.setObjects(addresses);
		listView.setAdapter(mAdapter);

		// добавить адрес
		ImageButton add_address = (ImageButton) getView().findViewById(R.id.personal_form_fr_list_footer_button_plus);
		add_address.setOnClickListener(this);
/*		
		// изменить информацию
		Button change_info = (Button) getView().findViewById(R.id.personal_form_fr_text_button_change);
		change_info.setOnClickListener(this);

		// изменить город
		Button change_city = (Button) getView().findViewById(R.id.personal_form_fr_text_button_city);
		change_city.setOnClickListener(this);

		// изменить пароль
		Button change_password = (Button) getView().findViewById(R.id.personal_form_fr_text_button_password);
		change_password.setOnClickListener(this);*/
	}

	private void reloadPersonalInfo(){
		if (mParseRequestLoader != null) mParseRequestLoader.cancel(true);
		mParseRequestLoader = new ParseRequest();
		mParseRequestLoader.execute();
	}

	public void refreshPersonalInfo(){
		TextView name = (TextView) getView().findViewById(R.id.personal_form_fr_text_name);
		TextView mobile = (TextView) getView().findViewById(R.id.personal_form_fr_text_mobile);
		TextView email = (TextView) getView().findViewById(R.id.personal_form_fr_text_email);
		
		name.setText(mPerson.getName());
		mobile.setText(mPerson.getMobile() != "null" ? mPerson.getMobile() : "Не указан");
		email.setText(mPerson.getEmail() != "null" ? mPerson.getEmail() : "Не указан");
	}
	
	@Override
	public void onClick(View v) {

		switch(v.getId()){
		case R.id.personal_form_fr_list_footer_button_plus :
		// добавление нового адреса
			AddressDialogFragment addressDialog = AddressDialogFragment.getInstance(NEW_ADDRESS);
			addressDialog.setOnAddAddressListener(new OnAddAddressListener() {
				@Override
				public void onAddAddress() {
					reloadPersonalInfo();
				}
			});
			addressDialog.show(getFragmentManager(), "addressDialog");
			break;
	/*		
		// изменение информации
		case R.id.personal_form_fr_text_button_change :
			PersonalInfoDialogFragment infoFragment = PersonalInfoDialogFragment.getInstance();
			infoFragment.setOnUpdateInfoListener(new OnUpdateInfoListener() {
				@Override
				public void onUpdateInfo() {
					refreshPersonalInfo();
				}
			});
			infoFragment.show(getFragmentManager(), "changeInfo");
			break;
			
		// смена города
		case R.id.personal_form_fr_text_button_city :
			CitiesDialogFragment dialogFragment = CitiesDialogFragment.getInstance();
			dialogFragment.show(getFragmentManager(), "changeCity");
			// TODO перетостить сообщеньку
			if ( ! BasketManager.isEmpty()) {
				Toast.makeText(getActivity(), "Осторожно! При смене города цена на товары и их наличие может измениться! \n Для уточнения информации звоните в Контакт-cENTER 8-800-7000009", Toast.LENGTH_LONG).show();
			}
			break;
			
		//смена пароля			
		case R.id.personal_form_fr_text_button_password :
			PasswordChangeDialogFragment passwordDialog = PasswordChangeDialogFragment.getInstance();
			passwordDialog.show(getFragmentManager(), "changePasswordDialog");
			break;*/
		default:
			break;			
		}		
	}
	
	public boolean isLoaded(){
		return mLoaded;
	}
	
	private class ParseRequest extends AsyncTask<Void, Void, PersonBean> {
		
		@Override
		protected void onPreExecute() {
			showProgress();
			mLoaded=false;
		}
		
		@Override
		protected PersonBean doInBackground(Void... params) {
			PersonBean result = new PersonInfoParser().parse(URLManager.getUser(PreferencesManager.getToken()));
			return result;
		}
		
		@Override
		protected void onPostExecute(PersonBean result) {
			if (isCancelled())
				return;
			mPerson = result;
			hideProgress();
			start();
			mLoaded=true;
		}
	}
	
	private class DeleteAddress extends AsyncTask<Integer, Void, Integer> {
		
		@Override
		protected void onPreExecute() {
			showProgress();
		}
		
		@Override
		protected Integer doInBackground(Integer... params) {
			return new AddressParser(getActivity()).parse(URLManager.getDeleteAddress(PreferencesManager.getToken(), params[0]));
		}
		
		@Override
		protected void onPostExecute(Integer result) {
			if (isCancelled())
				return;
			if (result !=null && result >= 0) {
					Toast.makeText(getActivity(), "Адрес удалён", Toast.LENGTH_SHORT).show();
			}
			hideProgress();
			reloadPersonalInfo();
		}
	}
	
	private void showDeleteAddressDialog (String address, final int id) {
		if (mPerson!=null){
			DeleteAddressDialogFragment dialog = DeleteAddressDialogFragment.getInstance();
			dialog.setAddressText(address);
			dialog.setonClickListener(new DialogInterface.OnClickListener() {

				@Override
				public void onClick(DialogInterface dialog, int which) {
					switch (which) {
					case DialogInterface.BUTTON_POSITIVE:
						if(mDeleteAddressloader != null) mDeleteAddressloader.cancel(true);
						mDeleteAddressloader = new DeleteAddress();
						mDeleteAddressloader.execute(id);
						break;
		
					default:
						break;
					}
				}
			});
			
			dialog.show(getFragmentManager(), "address_delete");//TODO
		}
	}
}