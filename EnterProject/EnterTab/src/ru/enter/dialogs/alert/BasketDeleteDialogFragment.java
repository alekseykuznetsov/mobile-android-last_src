package ru.enter.dialogs.alert;

import ru.enter.R;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface.OnClickListener;
import android.os.Bundle;
import android.view.ContextThemeWrapper;

public class BasketDeleteDialogFragment extends DialogFragment{

	private static final String TAG = "DELETE_ELEMENT_FROM_BASKET_DIALOG";
	
	private String mMessage = "";

	private OnClickListener mListener;
	
	public static BasketDeleteDialogFragment getInstance () {
		return new BasketDeleteDialogFragment();
	}

	public void setProductMessage (String productName) {
		mMessage = String.format("Вы действительно хотите удалить из корзины товар \"%s\" ?", productName);
	}
	
	public void setServiceMessage (String serviceName) {
		mMessage = String.format("Вы действительно хотите удалить из корзины услугу \"%s\" ?", serviceName);
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
		.setMessage(mMessage)
		.setTitle("Удаление из корзины")
		.setPositiveButton("Да", mListener)
		.setNegativeButton("Нет", mListener);
		
		return builder.create();
	}
	
	public void show() {
		show(getFragmentManager(), TAG);
	}
}