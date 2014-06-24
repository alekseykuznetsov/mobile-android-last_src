package ru.enter.dialogs;

import java.util.Calendar;

import org.json.JSONObject;

import ru.enter.R;
import ru.enter.beans.PersonBean;
import ru.enter.fragments.PersonalFormFragment;
import ru.enter.utils.Converter;
import ru.enter.utils.HTTPUtils;
import ru.enter.utils.PreferencesManager;
import ru.enter.utils.URLManager;
import ru.enter.utils.Utils;
import android.app.DialogFragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

public class PersonalInfoDialogFragment extends DialogFragment implements OnClickListener {

	private static final String DAY = "day";
	private static final String MONTH = "month";
	private static final String YEAR = "year";
	
	private PersonBean mPersonData;
	private Bundle mDate;
	
	private EditText mFirstName;
	private EditText mLastName;
	private EditText mPatronymic;
	private EditText mEmail;
	private EditText mPhone;
	
	private EditText mAdditionalPhone;
	private EditText mSkype;
	private TextView mBirthday;
	private EditText mProfession;
	
	private RadioGroup mGenre;
	private FrameLayout mProgreses;

	private UserUpdate mUserUpdate;
	
	public void showProgress () {
		mProgreses.setVisibility(View.VISIBLE);
	}
	
	public void hideProgress () {
		mProgreses.setVisibility(View.GONE);
	}
	
	
	private Handler mHandler = new Handler() {
		public void handleMessage(Message msg) {
			Bundle bundle = (Bundle) msg.obj;

			mDate.putInt(DAY, bundle.getInt(DAY));
			mDate.putInt(MONTH, bundle.getInt(MONTH));
			mDate.putInt(YEAR, bundle.getInt(YEAR));

			mBirthday.setTextColor(Color.BLACK);
			mBirthday.setText(mDate.getInt(DAY) + "." + (mDate.getInt(MONTH) + 1) + "." + mDate.getInt(YEAR));
		};
	};
	
	public static PersonalInfoDialogFragment getInstance() {
		return new PersonalInfoDialogFragment();
	}
	
	@Override
	public void onCreate(Bundle bundle) {
	    super.onCreate(bundle);
	    setStyle(0, R.style.custom_dialog_dark);
	}

	@Override
	public void onPause() {
		if (mUserUpdate != null) mUserUpdate.cancel(true);
		super.onPause();
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		
		getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
		getDialog().setTitle("Редактирование данных");
		
		PersonalFormFragment fragment = (PersonalFormFragment) getFragmentManager().findFragmentByTag("personalForm");
		mPersonData = fragment.getInfo();
		
		View view = inflater.inflate(R.layout.personal_dialog_edit_info, null);
		
		mProgreses = (FrameLayout) view.findViewById(R.id.personal_edit_info_progress_frame);
		mFirstName = (EditText) view.findViewById(R.id.personal_edit_info_edittext_name);
		mPatronymic = (EditText) view.findViewById(R.id.personal_edit_info_edittext_patronymic);
		mLastName = (EditText) view.findViewById(R.id.personal_edit_info_edittext_lastname);
		mGenre = (RadioGroup) view.findViewById(R.id.personal_edit_info_radiongroup_genre);
		
		mEmail = (EditText) view.findViewById(R.id.personal_edit_info_edittext_email);
		mPhone = (EditText) view.findViewById(R.id.personal_edit_info_edittext_phone);
		
		mAdditionalPhone = (EditText) view.findViewById(R.id.personal_edit_info_edittext_additional_phone);
		mSkype = (EditText) view.findViewById(R.id.personal_edit_info_edittext_skype);
		mProfession = (EditText) view.findViewById(R.id.personal_edit_info_edittext_work);
	
		mBirthday = (TextView) view.findViewById(R.id.personal_edit_info_text_birth);

		mDate = new Bundle();
		
		Calendar cal = Calendar.getInstance();		
		mDate.putInt(DAY, cal.get(Calendar.DAY_OF_MONTH));
		mDate.putInt(MONTH, cal.get(Calendar.MONTH));
		mDate.putInt(YEAR, cal.get(Calendar.YEAR));
		
		mBirthday.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				FragmentManager manager = getFragmentManager();
				FragmentTransaction transaction = manager.beginTransaction();

				BirthdayDialogFragment datePicker = new BirthdayDialogFragment(mHandler);
				datePicker.setArguments(mDate);
				transaction.add(datePicker, "date_picker");

				transaction.commit();
			}
		});
		
		Button save = (Button) view.findViewById(R.id.personal_edit_info_button_save);
		save.setOnClickListener(this);
	
		setData();
		return view;
	}
	
	private void setData(){
		setIfNotNull(mFirstName, mPersonData.getName());
		setIfNotNull(mLastName, mPersonData.getLastName());
		setIfNotNull(mPatronymic, mPersonData.getMiddleName());
		setIfNotNull(mEmail, mPersonData.getEmail());
		setIfNotNull(mPhone, mPersonData.getMobile());
		setIfNotNull(mAdditionalPhone, mPersonData.getPhone());
		setIfNotNull(mSkype, mPersonData.getSkype());
		setIfNotNull(mProfession, mPersonData.getOccupation());

		if(!mPersonData.getSex().equals("null")){
			String gender = mPersonData.getSex();
			if(gender.equals("1"))
				mGenre.check(R.id.personal_edit_info_radiobutton_male);
			else
				mGenre.check(R.id.personal_edit_info_radiobutton_female);
		}

		if(!"null".equals(mPersonData.getBirthday())){
			mBirthday.setTextColor(Color.BLACK);
			mBirthday.setText(Converter.fromLineToDot(mPersonData.getBirthday()));
		}
			 
	}
	
	private void setIfNotNull(EditText et, String text){
		if(!"null".equals(text))
			et.setText(text);
	}
	
	private boolean checkRequiredFields(){
		if(TextUtils.isEmpty(mFirstName.getText())){
			Toast.makeText(getActivity(), "Введите имя", Toast.LENGTH_LONG).show();
			mFirstName.requestFocus();
			return false;
		}
		if(TextUtils.isEmpty(mPhone.getText())){
			mEmail.requestFocus();
			Toast.makeText(getActivity(), "Введите телефон", Toast.LENGTH_LONG).show();
			return false;
		}
		return true;
	}
	
	@Override
	public void onClick(View v) {
		if(checkRequiredFields()){
			if (mUserUpdate != null) mUserUpdate.cancel(true);
			mUserUpdate = new UserUpdate();
			mUserUpdate.execute();
		}
	}
	
	private class UserUpdate extends AsyncTask<Void, Void, Boolean>{

		private String genre = "";	
		private String result;
		private String burthday;
		
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			showProgress();
		}
		
		@Override
		protected Boolean doInBackground(Void... params) {
			
			try {
				JSONObject object = new JSONObject();
				object.put("first_name", getField(mFirstName.getText().toString()));
				object.put("last_name", getField(mLastName.getText().toString()));
				object.put("middle_name", getField(mPatronymic.getText().toString()));
				
				burthday = mBirthday.getText().toString();
				if (burthday.length() != 0 ){
					burthday = Converter.fromDotToLine(burthday);
					object.put("birthday", burthday);
				}
								
				object.put("occupation", getField(mProfession.getText().toString()));
				object.put("email", getField(mEmail.getText().toString()));
				object.put("phone", getField(mAdditionalPhone.getText().toString()));
				object.put("mobile", getField(mPhone.getText().toString()));
				object.put("skype", getField(mSkype.getText().toString()));
				
				if(mGenre.getCheckedRadioButtonId() == R.id.personal_edit_info_radiobutton_male){
					genre = "1";
				}
				else{
					genre = "0";
				}
				
				object.put("sex", genre);
				result = Utils.sendPostData(object.toString(), URLManager.getUserUpdate(PreferencesManager.getToken()));
				return true;
			}
			catch(Exception e){
				e.printStackTrace();
				return false;
			}
		}
		
		@Override
		protected void onPostExecute(Boolean state) {
			if(isCancelled())
				return;
			
			hideProgress();
			if(state == true){
				try {
					JSONObject object = new JSONObject(result);
					if(object.has("error")){
						String errorMsg = object.getJSONObject("error").getString("message"); 
						Toast.makeText(getActivity(), "При изменении данных произошла ошибка:\n" + errorMsg, Toast.LENGTH_LONG).show();
					}
					else{
						JSONObject jsonResult = object.getJSONObject("result");
						boolean confirmed = jsonResult.getBoolean("confirmed");
						if(confirmed){
							applyData();
							Toast.makeText(getActivity(), "Данные успешно изменены", Toast.LENGTH_LONG).show();
						}
					}
				} 
				catch (Exception e) {
					e.printStackTrace();
					Toast.makeText(getActivity(), "Ошибка при получении данных", Toast.LENGTH_LONG).show();
				}
			}
			else{
				Toast.makeText(getActivity(), "Ошибка при изменении данных", Toast.LENGTH_LONG).show();
			}
			if (mListener != null) mListener.onUpdateInfo();
			dismiss();
		}
		
		private Object getField(String text){
			if(text == null || text.length() == 0)
				return JSONObject.NULL;
			else
				return text;
		}
		
		private String getNewField(String text){
			if(text == null || text.length() == 0)
				return "";
			else
				return text;
		}
		
		private void applyData(){
			mPersonData.setName(getNewField(mFirstName.getText().toString()));
			mPersonData.setLastName(getNewField(mLastName.getText().toString()));
			mPersonData.setMiddleName(getNewField(mPatronymic.getText().toString()));
			mPersonData.setOccupation(getNewField(mProfession.getText().toString()));
			mPersonData.setEmail(getNewField(mEmail.getText().toString()));
			mPersonData.setBirthday(burthday);
			mPersonData.setPhone(getNewField(mAdditionalPhone.getText().toString()));
			mPersonData.setMobile(getNewField(mPhone.getText().toString()));
			mPersonData.setSkype(getNewField(mSkype.getText().toString()));
			mPersonData.setSex(genre);
			
			setPreferences();
		}
		
		private void setPreferences(){
			PreferencesManager.setUserName(mPersonData.getName());
			PreferencesManager.setUserLastName(mPersonData.getLastName());
			PreferencesManager.setUserEmail(mPersonData.getEmail());
			PreferencesManager.setUserMobile(mPersonData.getMobile());
		}
	}
	
	private OnUpdateInfoListener mListener;
	
    public void setOnUpdateInfoListener (OnUpdateInfoListener listener) {
    	mListener = listener;
    }
    
    public interface OnUpdateInfoListener{
    	void onUpdateInfo ();
    }
}
