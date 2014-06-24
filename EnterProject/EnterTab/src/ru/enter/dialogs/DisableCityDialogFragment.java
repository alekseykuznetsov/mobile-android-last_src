package ru.enter.dialogs;

import ru.enter.R;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface.OnClickListener;
import android.os.Bundle;
import android.view.ContextThemeWrapper;

public class DisableCityDialogFragment extends DialogFragment {
	
	private OnClickListener mListener;
	private static final String TAG = "DISABLE_CITY_DIALOG";
	
	private String mMessage = "Ваше местоположение не было выбрано.";
	
	public static DisableCityDialogFragment getInstance() {
		DisableCityDialogFragment dlg = new DisableCityDialogFragment();
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
		.setTitle("Внимание")
		.setPositiveButton("Выбрать город из списка", mListener)
		.setNegativeButton("Выйти из приложения", mListener);
		
		return builder.create();
	}    
		
	public void show() {
		show(getFragmentManager(), TAG);
	}

}
