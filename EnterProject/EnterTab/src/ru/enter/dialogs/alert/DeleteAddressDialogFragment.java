package ru.enter.dialogs.alert;

import ru.enter.R;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface.OnClickListener;
import android.os.Bundle;
import android.view.ContextThemeWrapper;

public class DeleteAddressDialogFragment extends DialogFragment {

	private static final String TAG = "PERSONAL_ADDRESS_DELETE_DIALOG";
	
	private OnClickListener mListener;

	private String mMessage;
	
	public static DeleteAddressDialogFragment getInstance () {
		return new DeleteAddressDialogFragment();
	}

	public void setonClickListener (OnClickListener listener) {
		mListener = listener;
	}

	public void setAddressText (String address) {
		mMessage = String.format(" Вы действительно хотите удалить адрес \n\"%s\" ? ", address);
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
		.setTitle("Удаление адреса")
		.setPositiveButton("Удалить", mListener)
		.setNegativeButton("Отмена", mListener);
		
		return builder.create();
	}
	
	public void show() {
		show(getFragmentManager(), TAG);
	}
}