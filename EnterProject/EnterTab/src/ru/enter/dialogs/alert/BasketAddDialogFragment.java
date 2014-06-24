package ru.enter.dialogs.alert;

import ru.enter.BasketActivity;
import ru.enter.R;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.os.Bundle;
import android.view.ContextThemeWrapper;

public class BasketAddDialogFragment extends DialogFragment{

	private static final String TAG = "ADD_TO_BASKET_DIALOG";
	
	private String mMessage = "";
	
	public static BasketAddDialogFragment getInstance () {
		return new BasketAddDialogFragment();
	}

	public void setProductMessage (String productName) {
		mMessage =  String.format("Товар %s добавлен в корзину", productName);
	}
	
	public void setServiceMessage (String serviceName) {
		mMessage = String.format("Услуга \"%s\" добавлена в корзину", serviceName);
	}
	
	public void setRelatedServiceMessage (String serviceName, String productName) {
		mMessage = String.format("Услуга \"%s\" и товар \"%s\" добавлены в корзину", serviceName , productName);
	}
	
	private OnClickListener mListener = new OnClickListener() {
		
		@Override
		public void onClick(DialogInterface dialog, int which) {
			switch (which) {
			case DialogInterface.BUTTON_POSITIVE:
				startActivity(new Intent(getActivity(), BasketActivity.class));
				break;
			case DialogInterface.BUTTON_NEGATIVE:
				dismiss();
				break;	

			default:
				break;
			}
		}
	};
	
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
		.setTitle("Добавление в корзину")
		.setPositiveButton("Перейти в корзину", mListener)
		.setNegativeButton("Продолжить покупки", mListener);
		
		return builder.create();
	}
	
//	public void show() {
//		show(getFragmentManager(), TAG);
//	}
}