package ru.enter.dialogs.alert;


import ru.enter.R;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface.OnClickListener;
import android.os.Bundle;
import android.view.ContextThemeWrapper;


public class FirstStartDialogFragment extends DialogFragment {
	
	private OnClickListener mListener;
	private static final String TAG = "FIRST_START_DIALOG";
	
	private String mMessage = "Программа \"Enter\" хочет использовать ваше текущее местонахождение";
	
	public static FirstStartDialogFragment getInstance() {
		FirstStartDialogFragment dlg = new FirstStartDialogFragment();
		dlg.setCancelable(false);
        return dlg;
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
		.setMessage(mMessage)
		.setTitle("Определение города")
		.setPositiveButton("Запретить", mListener)
		.setNegativeButton("Разрешить", mListener);
		
		return builder.create();
	}    

	public void setonClickListener (OnClickListener listener) {
		mListener = listener;
	}
	
	public void show() {
		show(getFragmentManager(), TAG);
	}	

}
