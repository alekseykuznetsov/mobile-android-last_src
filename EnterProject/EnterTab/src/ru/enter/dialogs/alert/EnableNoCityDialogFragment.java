package ru.enter.dialogs.alert;

import ru.enter.R;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface.OnClickListener;
import android.os.Bundle;
import android.view.ContextThemeWrapper;

public class EnableNoCityDialogFragment extends DialogFragment {
	
	private OnClickListener mListener;
	private static final String TAG = "ENABLE_NO_CITY_DIALOG";
	
	private String mMessage = "Не удалось определить Ваше местоположение.";
	
	public static EnableNoCityDialogFragment getInstance() {
		EnableNoCityDialogFragment dlg = new EnableNoCityDialogFragment();
		dlg.setCancelable(false);
        return dlg;
    }

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setStyle(0, R.style.custom_dialog);
	}
	
	public void setonClickListener (OnClickListener listener) {
		mListener = listener;
	}
    
	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		
		AlertDialog.Builder builder = new AlertDialog.Builder(new ContextThemeWrapper(getActivity(), R.style.custom_alert_dialog));
		builder
		.setMessage(mMessage)
		.setTitle("Определение города")		
		.setPositiveButton("Еще раз", mListener)
		.setNegativeButton("Выбрать из списка", mListener);
		
		return builder.create();
	}    
		
	public void show() {
		show(getFragmentManager(), TAG);
	}
}
