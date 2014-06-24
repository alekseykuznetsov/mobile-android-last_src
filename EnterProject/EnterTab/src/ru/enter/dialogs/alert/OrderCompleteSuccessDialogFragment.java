package ru.enter.dialogs.alert;

import ru.enter.R;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface.OnClickListener;
import android.os.Bundle;
import android.view.ContextThemeWrapper;

public class OrderCompleteSuccessDialogFragment extends DialogFragment{

	private static final String TAG = "ORDER_COMPLETE_STATUS_DIALOG";
	
	private OnClickListener mListener;

	private String mMessage;
	
	public static OrderCompleteSuccessDialogFragment getInstance () {
		return new OrderCompleteSuccessDialogFragment();
	}

	public void setonClickListener (OnClickListener listener) {
		mListener = listener;
	}

	public void setOrderNumber (String order_num) {
		//mMessage = String.format("	Номер заказа: %s \nВнимание! Цены и условия доставки вы можите уточнить при подтверждении заказа в контакт cENTER", order_num);
		mMessage = "Заказ "+order_num+" отправлен для подбора на складе Enter.\nОжидайте СМС или звонок от оператора о статусе доставки вашего заказа.";
	}
	
	public void setOrderNumberBuyNow (String order_num) {
		//mMessage = String.format("	Номер заказа: %s \nВнимание! Цены и условия доставки вы можите уточнить при подтверждении заказа в контакт cENTER", order_num);
		mMessage = "Ваш заказ "+order_num+". Пройдите на кассу для оплаты заказа.";
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
		.setTitle("Ваш заказ успешно принят")
		.setPositiveButton("ОК", mListener);
		
		return builder.create();
	}
	
	public void show() {
		show(getFragmentManager(), TAG);
	}
}