package ru.enter;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.flurry.android.FlurryAgent;

import ru.enter.base.BaseActivity;
import ru.enter.beans.ProductBean;
import ru.enter.dialogs.DatePickerDialogFragment;
import ru.enter.utils.PreferencesManager;
import ru.ideast.shopitemfragment.tabs.ProductShopsActivity;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Html;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.TextView.OnEditorActionListener;

public class OrderOneClickActivity extends BaseActivity implements OnClickListener {

	private static final String DAY = "day";
	private static final String MONTH = "month";
	private static final String YEAR = "year";
	private static final String BASE_DAY = "base_day";
	private static final String BASE_MONTH = "base_month";
	private static final String BASE_YEAR = "base_year";
	
	private Bundle mData;
	private Bundle mInfo;
	private int mShopId;
	private String mShopAddress;
	private ProductBean mProductBean;
			
	private Handler mHandler = new Handler() {
		public void handleMessage(Message msg) {
			Bundle bundle = (Bundle) msg.obj;

			mData.putInt(DAY, bundle.getInt(DAY));
			mData.putInt(MONTH, bundle.getInt(MONTH));
			mData.putInt(YEAR, bundle.getInt(YEAR));

			TextView date_txt = (TextView) findViewById(R.id.order_one_click_ac_text_user_date);

			date_txt.setText(mData.getInt(DAY) + "." + (mData.getInt(MONTH) + 1) + "." + mData.getInt(YEAR));
		};
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.order_one_click_ac);
		
		setTitleLeft(getResources().getString(R.string.actionbar_basket));
		
		// скрываем авто-появление клавиатуры при повороте
		this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
		mShopId = getIntent().getIntExtra(OrderCompleteActivity.SHOP_ID, -1);
		mShopAddress = getIntent().getStringExtra(OrderCompleteActivity.ADDRESS);
		mProductBean = (ProductBean) getIntent().getSerializableExtra(ProductShopsActivity.PRODUCT_BEAN);
		// доставка - только самовывоз (забито в разметке)
		initCardNumber();
		start();
	}
	
	@Override
	protected void onStart()
	{
		super.onStart();
		FlurryAgent.onStartSession(this, "3X5JRBVFV7QWXNP3Q8H2");
	}
	 
	@Override
	protected void onStop()
	{
		super.onStop();		
		FlurryAgent.onEndSession(this);
	}

	private void initCardNumber(){
		
		final TextView label = (TextView) findViewById(R.id.order_one_click_ac_label_card);
		final EditText card = (EditText) findViewById(R.id.order_one_click_ac_edittext_card);
		
		card.setOnEditorActionListener(new OnEditorActionListener() {
			
			@Override
			public boolean onEditorAction (TextView v, int actionId, KeyEvent event) {
				if (actionId == EditorInfo.IME_ACTION_DONE){
					v.clearFocus();
					InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
					imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
				}
				return true;
			}
		});
		
		card.setOnFocusChangeListener(new OnFocusChangeListener() {
			
			@Override
			public void onFocusChange (View v, boolean hasFocus) {
				if (hasFocus){
					label.setText("298");
				}else{
					if (card.getText().toString().equals(""))
						label.setText("");
				}
			}
		});
	}
	
	private void start() {

		mInfo = new Bundle();

		// Выводим выбранный пользователем город
		TextView city = (TextView) findViewById(R.id.order_one_click_ac_text_city);
		city.setText(PreferencesManager.getCityName());

		Button next = (Button) findViewById(R.id.order_one_click_ac_button_next);
		next.setOnClickListener(this);
		setUserInfo();
		initDatePicker();
	}

	private void setUserInfo(){
		
		TextView address = (TextView) findViewById(R.id.order_one_click_ac_text_address);
		
		EditText name = (EditText) findViewById(R.id.order_one_click_ac_edittext_firstname);
		EditText last_name = (EditText) findViewById(R.id.order_one_click_ac_edittext_lastname);
		EditText email = (EditText) findViewById(R.id.order_one_click_ac_edittext_email);
		EditText mobile = (EditText) findViewById(R.id.order_one_click_ac_edittext_mobile);
		
		setIfNotNull(name,PreferencesManager.getUserName());
		setIfNotNull(last_name,PreferencesManager.getUserLastName());
		setIfNotNull(email,PreferencesManager.getUserEmail());
		setIfNotNull(mobile,PreferencesManager.getUserMobile());
		
		address.setText(mShopAddress);
	}
	
	private void setIfNotNull(EditText ed, String text){
		if (!TextUtils.isEmpty(text) && !text.equals("null")){
			ed.setText(text);
		}
	}
	
	private void initDatePicker() {
		initdate();

		RelativeLayout date_field = (RelativeLayout) findViewById(R.id.order_one_click_ac_linear_date);
		date_field.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				FragmentManager manager = getFragmentManager();
				FragmentTransaction transaction = manager.beginTransaction();

				DatePickerDialogFragment datePicker = new DatePickerDialogFragment(mHandler);
				datePicker.setArguments(mData);
				transaction.add(datePicker, "date_picker");

				transaction.commit();
			}
		});
	}

	private void initdate() {

		mData = new Bundle();

		Calendar cal = Calendar.getInstance();
		cal.add(Calendar.DAY_OF_MONTH, 1);
		
		mData.putInt(DAY, cal.get(Calendar.DAY_OF_MONTH));
		mData.putInt(MONTH, cal.get(Calendar.MONTH));
		mData.putInt(YEAR, cal.get(Calendar.YEAR));

		mData.putInt(BASE_DAY, cal.get(Calendar.DAY_OF_MONTH));
		mData.putInt(BASE_MONTH, cal.get(Calendar.MONTH));
		mData.putInt(BASE_YEAR, cal.get(Calendar.YEAR));
	}

	private boolean isValidEditText(EditText edit) {
		edit.setFocusableInTouchMode(true);
		edit.requestFocus();
		String stringToCheck = edit.getText().toString().trim();
		return stringToCheck.equals("");
	}

	private boolean isValidTextView(TextView text) {
		String stringToCheck = text.getText().toString().trim();
		return stringToCheck.equals("");
	}

	private boolean isValidRadioGroupSelect(RadioGroup group) {
		int select = group.getCheckedRadioButtonId();
		if (select == -1) {
			return true;
		}
		return false;
	}

	public static boolean isEmailValid(String email) {
		
	    String expression = "^[\\w\\.-]+@([\\w\\-]+\\.)+[A-Z]{2,4}$";
	    CharSequence inputStr = email;
	    Pattern pattern = Pattern.compile(expression, Pattern.CASE_INSENSITIVE);
	    Matcher matcher = pattern.matcher(inputStr);
	    return matcher.matches();
	}

	
	private void putEditTextToBundle(EditText text, String name) {
		String txt = text.getText().toString().trim();
		mInfo.putString(name, txt);
	}

	private void putDateToBundle(String name_show, String name_send) {
		String date_show = mData.getInt(DAY)+ "." + (mData.getInt(MONTH) + 1) + "." + mData.getInt(YEAR);
		
		// hard date format
		String day;
		String month;
		
		if (mData.getInt(DAY) < 10){
			day = "0" + String.valueOf(mData.getInt(DAY)); 
		} else {
			day = String.valueOf(mData.getInt(DAY));
		}
		
		if (mData.getInt(MONTH) < 9){
			month = "0" + String.valueOf(mData.getInt(MONTH)); 
		} else {
			month = String.valueOf(mData.getInt(MONTH));
		}
		
		String date_send = String.valueOf(mData.getInt(YEAR)).concat("-").concat(month).concat("-").concat(day);
		
		Date current_date = new Date();
		
		SimpleDateFormat formater = new SimpleDateFormat("yyyy-MM-dd");
		
		mInfo.putString(name_show, date_show);
		mInfo.putString(name_send, date_send);
	}

	private void putRadioGroupSelectToBundle(RadioGroup group, String name) {
		int select = group.getCheckedRadioButtonId();
		switch (select) {
		case R.id.order_one_click_ac_radiogroup_widget_cash:
			mInfo.putInt(name, OrderCompleteActivity.CASH);
			break;
		case R.id.order_one_click_ac_radiogroup_widget_bankcard:
			mInfo.putInt(name, OrderCompleteActivity.BANKCARD);
			break;
		default:
			break;
		}
	}

	private boolean checkAllFields() {

		EditText firstname = (EditText) findViewById(R.id.order_one_click_ac_edittext_firstname);
		EditText lastname = (EditText) findViewById(R.id.order_one_click_ac_edittext_lastname);
		EditText email = (EditText) findViewById(R.id.order_one_click_ac_edittext_email);
		EditText mobile = (EditText) findViewById(R.id.order_one_click_ac_edittext_mobile);
		EditText card = (EditText) findViewById(R.id.order_one_click_ac_edittext_card);
		RadioGroup payment_type = (RadioGroup) findViewById(R.id.order_one_click_ac_radiogroup_payment);

		if (isValidEditText(firstname)) {
			Toast.makeText(this, Html.fromHtml("Введите <b>Имя</b>"), Toast.LENGTH_SHORT).show();
			return false;
		}
	
		if (email.getText().toString().length() > 0){
			if (!isEmailValid(email.getText().toString())) {
				email.setFocusableInTouchMode(true);
				email.requestFocus();
				Toast.makeText(this, Html.fromHtml("Введённый <b>E-mail не корректен, проверьте правильность ввода</b>"), Toast.LENGTH_SHORT).show();
				return false;
			}
		}
		
		if (isValidEditText(mobile)) {
			Toast.makeText(this, Html.fromHtml("Введите <b>Номер телефона</b>"), Toast.LENGTH_SHORT).show();
			return false;
		}
		if (isValidRadioGroupSelect(payment_type)) {

			Toast.makeText(this, Html.fromHtml("Выберете <b>Способ оплаты</b>"), Toast.LENGTH_SHORT).show();
			return false;
		}
		if (!isCardValid(card)) {
			Toast.makeText(this, 
					Html.fromHtml("<b>В номере карты «Связной-Клуб» допущена ошибка.</b>\nПроверьте правильность ввода номера, расположенного на обороте под штрих кодом, и повторите попытку"),
					Toast.LENGTH_LONG).show();
			return false;
		}

		TextView self_date = (TextView) findViewById(R.id.order_one_click_ac_text_user_date);

		if (isValidTextView(self_date)) {
			Toast.makeText(this, Html.fromHtml("Укажите <b>Дату самовывоза</b> товара"), Toast.LENGTH_SHORT).show();
			return false;
		}

		mInfo.putInt(OrderCompleteActivity.SHOP_ID, mShopId);
		mInfo.putString(OrderCompleteActivity.ADDRESS, mShopAddress);
	
		putDateToBundle(OrderCompleteActivity.DATE_SHOW, OrderCompleteActivity.DELIVERY_DATE);

		putEditTextToBundle(firstname, OrderCompleteActivity.FIRSTNAME);
		putEditTextToBundle(lastname, OrderCompleteActivity.LASTNAME);
		putEditTextToBundle(email, OrderCompleteActivity.EMAIL);
		putEditTextToBundle(mobile, OrderCompleteActivity.MOBILE);
		putCardTextToBundle(card);
		putRadioGroupSelectToBundle(payment_type, OrderCompleteActivity.PAYMENT_ID);

		return true;		
	}

	private void setDeliveryInfo() {
		mInfo.putInt(OrderCompleteActivity.DELIVERY_PRICE, 0);
		mInfo.putInt(OrderCompleteActivity.DELIVERY_ID, OrderCompleteActivity.SELF);
	}

	private void setOneClickProduct(){
		mInfo.putSerializable(ProductShopsActivity.PRODUCT_BEAN, mProductBean);
	}
	
	@Override
	public void onClick(View v) {

		switch (v.getId()) {
		case R.id.order_one_click_ac_button_next:
			// Проверяем и записываем данные, введённые пользователем
			if (checkAllFields()) {
				// Записываем тип и цену доставки товаров
				setDeliveryInfo();
				setOneClickProduct();
				Intent intent = new Intent(OrderOneClickActivity.this, OrderCompleteOneClickActivity.class);
				intent.putExtra(OrderCompleteActivity.INFO, mInfo);
				intent.putExtra(ProductShopsActivity.PRODUCT_BEAN, mProductBean);
				startActivity(intent);
			}
			break;
		default:
			break;
		}
	}

	private boolean isCardValid(EditText card) {
		String key = card.getText().toString();
		if (key.length() == 0) {
			return true;
		}

		if (!key.startsWith("298"))
			key = "298" + key;
		
		if (key.length() != 13) {
			return false;
		}

		int[] val = new int[13];
		for (int i = 0; i < 13; i++) {
			val[i] = key.charAt(i) - '0';
			if (val[i] < 0 || val[i] > 9) {
				return false;
			}
		}

		int sumNechet = 0;
		int sumChet = 0;
		for (int i = 11; i >= 0; i--)
			if (i % 2 == 0)
				sumChet += val[i];
			else
				sumNechet += val[i];
		sumNechet *= 3;
		int sum = sumNechet + sumChet;
		int ost = sum % 10;
		if (ost != 0)
			ost = 10 - ost;
		boolean result = (val[12] == ost);
		return result;
	}

	private void putCardTextToBundle(EditText card) {
		String key = card.getText().toString();
		if (key.length() == 0) {
			mInfo.putString(OrderCompleteActivity.SVYAZNOY_CARD, "");
			return;
		}
		
		if (!key.startsWith("298"))
			key = "298" + key;
		
		mInfo.putString(OrderCompleteActivity.SVYAZNOY_CARD, key);		
	}
}