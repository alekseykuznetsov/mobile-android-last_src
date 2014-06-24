package ru.enter.dialogs;

import ru.enter.R;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class ProgressDialogFragment extends DialogFragment{
	
	OnCancelListener mCancelListener;
	
	public static ProgressDialogFragment getInstance() {
        return new ProgressDialogFragment();
    }

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setRetainInstance(true);
		setStyle(DialogFragment.STYLE_NO_TITLE, android.R.style.Theme_NoTitleBar_Fullscreen);
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.progress_dialog_fr, container, false);
	}
	
	public void setOnCancelListener (OnCancelListener listener) {
		mCancelListener = listener;
	}
	
	@Override
	public void onCancel(DialogInterface dialog) {
		if (mCancelListener == null) {
			super.onCancel(dialog);
		} else {
			mCancelListener.onCancel(dialog);
		}
		
	}

}
