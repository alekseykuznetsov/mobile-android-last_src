package ru.enter.dialogs;
import java.util.Calendar;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.widget.DatePicker;

public class BirthdayDialogFragment extends DialogFragment{
	
	private static final String DAY = "day";
	private static final String MONTH = "month";
	private static final String YEAR = "year";
	
	Handler mHandler ;
	private Bundle mBundle;
	
	public BirthdayDialogFragment(Handler handler){
		mHandler = handler;
		mBundle = new Bundle();		
	}

	@Override
    public Dialog onCreateDialog(Bundle savedInstanceState){

		mBundle = getArguments();
		
		DatePickerDialog.OnDateSetListener listener  = new DatePickerDialog.OnDateSetListener() {		
			@Override
			public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {

				mBundle.putInt(DAY, dayOfMonth);
				mBundle.putInt(MONTH, monthOfYear);
				mBundle.putInt(YEAR, year);
			}
		};	
		
		DatePickerDialog pickerDialog = new DatePickerDialog(getActivity(), listener, mBundle.getInt(YEAR), mBundle.getInt(MONTH), (mBundle.getInt(DAY)));

		long maxDate = 0;
		long minDate = 1;
		Calendar calendarMax = Calendar.getInstance();
		calendarMax.set(calendarMax.get(Calendar.YEAR) - 5, calendarMax.get(Calendar.MONTH), calendarMax.get(Calendar.DAY_OF_MONTH), 0, 0, 0);
        maxDate = calendarMax.getTimeInMillis();
        
		Calendar calendarMin = Calendar.getInstance();
		calendarMin.set(calendarMin.get(Calendar.YEAR) - 100, calendarMin.get(Calendar.MONTH), calendarMin.get(Calendar.DAY_OF_MONTH), 0, 0, 0);
        minDate = calendarMin.getTimeInMillis();
        
        pickerDialog.getDatePicker().setMinDate(minDate);
        pickerDialog.getDatePicker().setMaxDate(maxDate);
        
		return pickerDialog;
    }	
	
	@Override
	public void onDismiss(DialogInterface dialog) {
		if (mHandler != null){
			Message msg = new Message();
			msg.what = 0;
			msg.obj = mBundle;
			mHandler.sendMessage(msg);
		}
		super.onDismiss(dialog);
	}
}