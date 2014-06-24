package ru.enter.dialogs.alert;

import ru.enter.R;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface.OnClickListener;
import android.os.Bundle;
import android.view.ContextThemeWrapper;

public class AuthFailDialogFragment extends DialogFragment{

	private static final String TAG = "AUTH_FAIL_DIALOG";
	private String mMessage;	
	private OnClickListener mListener;
	
	public static AuthFailDialogFragment getInstance () {
		return new AuthFailDialogFragment();
	}

	public void setonClickListener (OnClickListener listener) {
		mListener = listener;
	}
	
	public void setErrorMessage (String error) {
		mMessage = String.format(error);
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}
	
	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		
		AlertDialog.Builder builder = new AlertDialog.Builder(new ContextThemeWrapper(getActivity(), R.style.custom_alert_dialog));
		
		builder
		.setMessage(mMessage)
		.setTitle("Ошибка")
		.setPositiveButton("ОК", mListener);
		
		return builder.create();
	}
	
	public void show() {
		show(getFragmentManager(), TAG);
	}
}		