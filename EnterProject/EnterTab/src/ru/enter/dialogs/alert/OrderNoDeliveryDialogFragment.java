package ru.enter.dialogs.alert;

import ru.enter.R;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface.OnClickListener;
import android.os.Bundle;
import android.view.ContextThemeWrapper;

public class OrderNoDeliveryDialogFragment extends DialogFragment{

	private static final String TAG = "ORDER_NO_DELIVERY_DIALOG";
	
	private OnClickListener mListener;
	private String mMessage;
	
	public static OrderNoDeliveryDialogFragment getInstance () {
		return new OrderNoDeliveryDialogFragment();
	}

	public void setonClickListener (OnClickListener listener) {
		mListener = listener;
	}
	
	public void setMessage(String message){
		mMessage = message;
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
		.setTitle("Расчёт доставки товаров")
		.setPositiveButton("Вернуться", mListener);
		
		return builder.create();
	}
	
	public void show() {
		show(getFragmentManager(), TAG);
	}
}