package ru.enter.dialogs;

import ru.enter.R;
import ru.enter.parsers.AddressParser;
import ru.enter.utils.PreferencesManager;
import ru.enter.utils.URLManager;
import android.app.DialogFragment;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

public class PasswordChangeDialogFragment extends DialogFragment implements OnClickListener {

	private TextView mOldPassword;
	private TextView mNewPassword;
	private TextView mConfirmPassword;
	private FrameLayout mProgreses;
	private ChangePassword mChangePasswordLoader;
	
	public void showProgress () {
		mProgreses.setVisibility(View.VISIBLE);
	}
	
	public void hideProgress () {
		mProgreses.setVisibility(View.GONE);
	}
	
	public static PasswordChangeDialogFragment getInstance() {
		return new PasswordChangeDialogFragment();
	}
	
	@Override
	public void onCreate(Bundle bundle) {
	    super.onCreate(bundle);
	    setStyle(0, R.style.custom_dialog_dark);
	}
	
	@Override
	public void onPause() {
		if (mChangePasswordLoader != null) mChangePasswordLoader.cancel(true);
		super.onPause();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		
		getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
		getDialog().setTitle("Смена пароля");
		
		View view = inflater.inflate(R.layout.personal_dialog_password_change, null);
		
		mProgreses = (FrameLayout) view.findViewById(R.id.personal_dialog_password_change_progress_frame);
		mOldPassword = (EditText) view.findViewById(R.id.personal_dialog_password_change_edittext_old);
		mNewPassword = (EditText) view.findViewById(R.id.personal_dialog_password_change_edittext_new);
		mConfirmPassword = (EditText) view.findViewById(R.id.personal_dialog_password_change_edittext_confirm);
		
		Button save = (Button) view.findViewById(R.id.personal_dialog_password_change_button_save);
		Button cancel = (Button) view.findViewById(R.id.personal_dialog_password_change_button_cancel);
		
		cancel.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				dismiss();				
			}
		});
		
		save.setOnClickListener(this);
	
		return view;
	}
	
	@Override
	public void onClick(View v) {
		String oldP = mOldPassword.getEditableText().toString().trim();
    	String newP = mNewPassword.getEditableText().toString().trim();
    	String confirmP = mConfirmPassword.getEditableText().toString().trim();
    	if(oldP.equals("")){
    		mOldPassword.requestFocus();
    		Toast.makeText(getActivity(), "Введите старый пароль", Toast.LENGTH_SHORT).show();
    	}else if (newP.equals("")){
    		mNewPassword.requestFocus();
    		Toast.makeText(getActivity(), "Введите новый пароль", Toast.LENGTH_SHORT).show();
    	}else if (confirmP.equals("")){
    		mConfirmPassword.requestFocus();
    		Toast.makeText(getActivity(), "Введите новый пароль ещё раз", Toast.LENGTH_SHORT).show();
    	}else{
    		if (newP.equals(confirmP)){
    			if (mChangePasswordLoader != null) mChangePasswordLoader.cancel(true);
    			mChangePasswordLoader = new ChangePassword();
    			mChangePasswordLoader.execute(oldP, newP);
    		}else{
    			Toast.makeText(getActivity(), "Введённые пароли не совпадают", Toast.LENGTH_SHORT).show();
    		}
    	}
	}

	private class ChangePassword extends AsyncTask<String, Void, Integer> {
		
		@Override
		protected void onPreExecute() {
			showProgress();
		}
		
		@Override
		protected Integer doInBackground(String... data_to_send) {
			return new AddressParser(getActivity()).parse(URLManager.getUserChangePassword(PreferencesManager.getToken(), data_to_send[0], data_to_send[1]));
		}
		
		protected void onPostExecute(Integer result) {
			if (isCancelled())
				return;

			hideProgress();
			if (result !=null && result >= 0) {
					Toast.makeText(getActivity(), "Пароль изменен", Toast.LENGTH_SHORT).show();
			}
//			else Toast.makeText(getActivity(), "Ошибка при изменении пароля", Toast.LENGTH_SHORT).show();
		
		dismiss();
		}
	 }
	
}
