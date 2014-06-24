package ru.enter.widgets;

import java.util.ArrayList;
import java.util.Date;

import ru.enter.R;
import ru.enter.DataManagement.BasketData;
import ru.enter.beans.CheckoutBean;
import ru.enter.beans.CheckoutBean.CheckoutPaymentMethod;
import ru.enter.utils.ICheckoutInterface;
import ru.enter.utils.PreferencesManager;
import ru.enter.utils.Utils;
import android.content.Context;
import android.text.InputFilter;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

public class CheckoutSecondStepView extends FrameLayout implements OnCheckedChangeListener, ICheckoutInterface{
	private View mRootView;
	private RadioGroup mCheckoutSecondStepPaymentMethodsFizRG;
	private EditText mCheckoutSecondStepName,mCheckoutSecondStepSurname,mCheckoutSecondStepEmail,mCheckoutSecondStepPhoneNumber;
	private Button mNextB;
	private CheckoutBean mCheckoutBean;
	private Spinner mDateSpinner;
	private View mDateFrame;
	public int startDate = 0;
	private EditText svyaznoyCard;
	private EditText svyaznoyCard298;
	private InputFilter[] FilterArray10;
	private InputFilter[] FilterArray13;

	//при покупке в один клик есть выбор даты на 2м шаге заказа.
	public CheckoutSecondStepView(Context context,boolean isOneClick) {
		super(context);

		mCheckoutBean = BasketData.getInstance().getCheckoutBean();
		mCheckoutBean.setCheckoutPaymentMethod(CheckoutPaymentMethod.cash);

		mRootView = LayoutInflater.from(context).inflate(R.layout.checkout_second_step, null);
		TextView title = (TextView)mRootView.findViewById(R.id.checkout_second_step_title_tv);
		title.setTypeface(Utils.getTypeFace(context));

		mCheckoutSecondStepPaymentMethodsFizRG = (RadioGroup)mRootView.findViewById(R.id.checkout_second_step_payment_methods_fiz_rg);
		mCheckoutSecondStepPaymentMethodsFizRG.setOnCheckedChangeListener(this);

		mCheckoutSecondStepName = (EditText)mRootView.findViewById(R.id.checkout_second_step_name_et);
		mCheckoutSecondStepSurname = (EditText)mRootView.findViewById(R.id.checkout_second_step_surname_et);
		mCheckoutSecondStepEmail = (EditText)mRootView.findViewById(R.id.checkout_second_step_email_et);
		svyaznoyCard298 = (EditText)mRootView.findViewById(R.id.checkout_second_step_svyaznoyCard_298);
		svyaznoyCard298.setOnClickListener(null);

		FilterArray10 = new InputFilter[1];
		FilterArray10[0] = new InputFilter.LengthFilter(10);

		FilterArray13 = new InputFilter[1];
		FilterArray13[0] = new InputFilter.LengthFilter(13);

		svyaznoyCard = (EditText)mRootView.findViewById(R.id.checkout_second_step_svyaznoyCard);
		svyaznoyCard.setOnFocusChangeListener(new OnFocusChangeListener() {

			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				if(hasFocus){
					if(svyaznoyCard.getText().length() >= 3){
						svyaznoyCard.setText(svyaznoyCard.getText().toString().substring(3));
						svyaznoyCard.setSelection(svyaznoyCard.getText().length());
					}
					svyaznoyCard.setFilters(FilterArray10);
					svyaznoyCard298.setVisibility(View.VISIBLE);
				}
				else{
					svyaznoyCard.setFilters(FilterArray13);
					svyaznoyCard298.setVisibility(View.GONE);
					if(svyaznoyCard.getText().length() != 0)
						svyaznoyCard.setText("298" + svyaznoyCard.getText());
				}
			}
		});
		mCheckoutSecondStepPhoneNumber = (EditText)mRootView.findViewById(R.id.checkout_second_step_phonenumber_et);

		mNextB = (Button)mRootView.findViewById(R.id.checkout_second_step_next_b);

		String name = PreferencesManager.getUserName();
		if(name != null){
			mCheckoutSecondStepName.setText((name.equals("null") ? "" : name));
		}
		String surname = PreferencesManager.getUserLastName();
		if(surname != null){
			mCheckoutSecondStepSurname.setText((surname.equals("null") ? "" :surname));
		}
		String email = PreferencesManager.getUserEmail();
		if(email != null){
			mCheckoutSecondStepEmail.setText((email.equals("null") ? "" :email));
		}
		String mobile = PreferencesManager.getUserMobile();
		if(mobile != null){
			mCheckoutSecondStepPhoneNumber.setText((mobile.equals("null") ? "" :mobile));
		}

		addView(mRootView);


		mDateFrame = mRootView.findViewById(R.id.checkout_first_step_time_frame);
		mDateSpinner = (Spinner)mRootView.findViewById(R.id.checkout_first_step_date_spinner);

		if(isOneClick){
			mDateFrame.setVisibility(View.VISIBLE);
			initSpinner();
		}else{
			mDateFrame.setVisibility(View.GONE);
		}
	}

	@Override
	public void setOnClickListener(OnClickListener l) {
		mNextB.setOnClickListener(l);
	}
	@Override
	public void onCheckedChanged(RadioGroup group, int checkedId) {
		switch (group.getId()) {
		case R.id.checkout_second_step_payment_methods_fiz_rg://способ оплаты для физ лиц
			switch (checkedId) {
			case R.id.checkout_second_step_payment_methods_fiz_rb1:
				mCheckoutBean.setCheckoutPaymentMethod(CheckoutPaymentMethod.cash);
				break;
			case R.id.checkout_second_step_payment_methods_fiz_rb2:
				mCheckoutBean.setCheckoutPaymentMethod(CheckoutPaymentMethod.bank_card);
				break;
			default:
				break;
			}
			break;
		default:
			break;
		}
	}

	private ArrayList<String> getDate(){
		long day = 24*60*60*1000;
		ArrayList<String> dateList = new ArrayList<String>(7);
		for(int i=startDate;i<7;i++){
			dateList.add(CheckoutFirstStepView.FORMAT.format(new Date(new Date().getTime()+i*day)));	
		}
		return dateList;
	}

	private void initSpinner(){

		ArrayAdapter<String> adapterDate = new ArrayAdapter<String>(getContext(),android.R.layout.simple_spinner_item,getDate());
		adapterDate.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		mDateSpinner.setAdapter(adapterDate);
		mDateSpinner.setOnItemSelectedListener(
				new OnItemSelectedListener() {
					public void onItemSelected( AdapterView<?> parent, View view, int position, long id) {
						long day = 24*60*60*1000;
						mCheckoutBean.setDeliveryDate(CheckoutFirstStepView.FORMAT_CHECKOUT.format(new Date(new Date().getTime()+(position+1)*day)));
					}

					public void onNothingSelected(AdapterView<?> parent) {
					}
				});
	}

	@Override
	public boolean isNext() {
		mCheckoutBean.setName(mCheckoutSecondStepName.getEditableText().toString());
		mCheckoutBean.setLastName(mCheckoutSecondStepSurname.getEditableText().toString());
		mCheckoutBean.setEmail(mCheckoutSecondStepEmail.getEditableText().toString());
		mCheckoutBean.setPhoneNumber(mCheckoutSecondStepPhoneNumber.getEditableText().toString());

		if(mCheckoutBean.getName().equals("")){
			Toast.makeText(getContext(), "Имя обязательно для заполнения", Toast.LENGTH_SHORT).show();
			return false;
		}
		if(mCheckoutBean.getPhoneNumber().equals("")){
			Toast.makeText(getContext(), "Номер телефона обязателен для заполнения", Toast.LENGTH_SHORT).show();
			return false;
		}

		return true;
	}

	@Override
	public boolean isSvyaznoyCardValid() {
		String key = svyaznoyCard.getText().toString();
		if (key.length() == 0) {
			mCheckoutBean.setSvyaznoyCard("");
			return true;
		}

		if (!key.startsWith("298"))
			key = "298" + key;
		if (key.length() != 13 || !key.startsWith("298")) {
			mCheckoutBean.setSvyaznoyCard("");
			return false;
		}

		int[] val = new int[13];
		for(int i = 0; i < 13; i++){
			val[i] = key.charAt(i) - '0';
			if(val[i] < 0 || val[i] > 9){
				mCheckoutBean.setSvyaznoyCard("");
				return false;
			}
		}

		int sumNechet = 0;
		int sumChet = 0;
		for(int i = 11; i >= 0; i--)
			if(i % 2 == 0)
				sumChet += val[i];
			else
				sumNechet += val[i];
		sumNechet *= 3;
		int sum = sumNechet + sumChet;
		int ost = sum % 10;
		if(ost != 0)
			ost = 10 - ost;
		boolean result = (val[12] == ost);
		if(result)
			mCheckoutBean.setSvyaznoyCard(key);
		else
			mCheckoutBean.setSvyaznoyCard("");
		return result;
	}
}

