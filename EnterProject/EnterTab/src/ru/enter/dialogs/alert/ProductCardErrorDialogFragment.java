package ru.enter.dialogs.alert;

import ru.enter.R;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface.OnClickListener;
import android.os.Bundle;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class ProductCardErrorDialogFragment extends DialogFragment{

	private static final String TAG = "PRODUCT_CARD_ERROR_DIALOG";
	
	private OnClickListener mListener;

	@Override
	public View onCreateView (LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		return super.onCreateView(inflater, container, savedInstanceState);
	}
	
	public static ProductCardErrorDialogFragment getInstance () {
		return new ProductCardErrorDialogFragment();
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
		.setMessage("Произошла ошибка при загрузке информации о товаре, попробуйте позже")
		.setTitle("Ошибка загрузки")
		.setPositiveButton("Вернуться", mListener);
		
		return builder.create();
	}
	
	public void show() {
		show(getFragmentManager(), TAG);
	}
}