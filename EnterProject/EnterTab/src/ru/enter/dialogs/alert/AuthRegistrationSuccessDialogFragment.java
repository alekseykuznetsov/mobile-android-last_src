package ru.enter.dialogs.alert;

import ru.enter.R;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface.OnClickListener;
import android.os.Bundle;
import android.view.ContextThemeWrapper;

public class AuthRegistrationSuccessDialogFragment  extends DialogFragment{

	private static final String TAG = "AUTH_REGISTRATION_SUCCESS_DIALOG";
	private OnClickListener mListener;
	
	public static AuthRegistrationSuccessDialogFragment getInstance () {
		return new AuthRegistrationSuccessDialogFragment();
	}

	public void setonClickListener (OnClickListener listener) {
		mListener = listener;
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}
	
	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		
		AlertDialog.Builder builder = new AlertDialog.Builder(new ContextThemeWrapper(getActivity(), R.style.custom_alert_dialog));
		
		builder
		.setMessage("Покупатель зарегистрирован.\nПароль выслан на телефон, указанный при регистрации")
		.setTitle("Регистрация")
		.setPositiveButton("ОК", mListener);
		
		return builder.create();
	}
	
	public void show() {
		show(getFragmentManager(), TAG);
	}
}		