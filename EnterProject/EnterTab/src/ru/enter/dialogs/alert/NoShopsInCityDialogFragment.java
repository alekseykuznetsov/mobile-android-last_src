package ru.enter.dialogs.alert;

import ru.enter.R;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface.OnClickListener;
import android.os.Bundle;
import android.view.ContextThemeWrapper;

public class NoShopsInCityDialogFragment extends DialogFragment {

	private OnClickListener mListener;
	private static final String TAG = "NO_SHOPS_IN_CITY_DLG_FRG";
	
	private String mMessage = "В данном городе пока нет магазинов.";
	
	public static NoShopsInCityDialogFragment getInstance() {
		NoShopsInCityDialogFragment dlg = new NoShopsInCityDialogFragment();
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
		.setNeutralButton("Ок", mListener);
		
		return builder.create();
	}    
		
	public void show() {
		show(getFragmentManager(), TAG);
	}
	
	
}
