package ru.enter.dialogs.alert;

import ru.enter.R;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface.OnClickListener;
import android.os.Bundle;
import android.view.ContextThemeWrapper;

public class RememberPasswordDialogFragment  extends DialogFragment{

	private static final String TAG = "REMMEMBER_PASSWORD_DIALOG";
	private OnClickListener mListener;
	private String mMessage;
	
	public static RememberPasswordDialogFragment getInstance () {
		return new RememberPasswordDialogFragment();
	}

	public void setonClickListener (OnClickListener listener) {
		mListener = listener;
	}
	
	public void setRemmemberMessage (String user_name) {
		mMessage = String.format("Новый пароль для %s будет\nвыслан на e-mail или мобильный телефон", user_name);
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
		.setTitle("Восстановление пароля")
		.setPositiveButton("ОК", mListener)
		.setNegativeButton("Отмена", mListener);
		
		return builder.create();
	}
	
	public void show() {
		show(getFragmentManager(), TAG);
	}
}		