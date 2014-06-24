package ru.enter.dialogs.alert;

import ru.enter.R;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface.OnClickListener;
import android.os.Bundle;
import android.view.ContextThemeWrapper;

public class LogoutDialogFragment extends DialogFragment{

	private static final String TAG = "LOGOUT_DIALOG";
	
	private OnClickListener mListener;
	
	public static LogoutDialogFragment getInstance () {
		return new LogoutDialogFragment();
	}

	public void setonClickListener (OnClickListener listener) {
		mListener = listener;
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setStyle(0, R.style.custom_dialog);
	}
	
	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		
		AlertDialog.Builder builder = new AlertDialog.Builder(new ContextThemeWrapper(getActivity(), R.style.custom_alert_dialog));
		builder
		.setMessage("Вы действительно хотите выйти?")
		.setPositiveButton("Да", mListener)
		.setNegativeButton("Нет", mListener);
		
		return builder.create();
	}
	
	public void show() {
		show(getFragmentManager(), TAG);
	}
}