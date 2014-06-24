package ru.enter;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.json.JSONArray;
import org.json.JSONObject;

import com.flurry.android.FlurryAgent;
import com.google.analytics.tracking.android.EasyTracker;
import com.google.analytics.tracking.android.Tracker;
import com.google.analytics.tracking.android.Transaction;
import com.google.analytics.tracking.android.Transaction.Item;

import ru.enter.DataManagement.BasketManager;
import ru.enter.adapters.OrderAddressSpinnerAdapter;
import ru.enter.adapters.OrderMetroSpinnerAdapter;
import ru.enter.base.BaseActivity;
import ru.enter.beans.AddressBean;
import ru.enter.beans.DeliveryBean;
import ru.enter.beans.MetroBean;
import ru.enter.beans.ProductBean;
import ru.enter.beans.ServiceBean;
import ru.enter.beans.ShopBean;
import ru.enter.dialogs.DatePickerDialogFragment;
import ru.enter.dialogs.OrderUserAddressDialogFragment;
import ru.enter.dialogs.OrderUserAddressDialogFragment.OnSelectAddressListener;
import ru.enter.dialogs.ProgressDialogFragment;
import ru.enter.dialogs.alert.OrderNoDeliveryDialogFragment;
import ru.enter.parsers.DeliveryParser;
import ru.enter.parsers.MetroParser;
import ru.enter.parsers.ShopsParser;
import ru.enter.utils.HTTPUtils;
import ru.enter.utils.PreferencesManager;
import ru.enter.utils.URLManager;
import ru.enter.utils.Utils;
import ru.enter.utils.Wrapper;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Html;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;
import android.widget.Toast;

public class OrderActivity extends BaseActivity implements OnClickListener {

	private static final String DELIVERIES_DATA = "deliveries_data";
	private static final String DELIVERY_CHOICE = "delivery_choice";
	private static final String SHOPS_DATA = "shops_data";
	private static final String METRO_DATA = "metros_data";

	private static final String DAY = "day";
	private static final String MONTH = "month";
	private static final String YEAR = "year";
	private static final String BASE_DAY = "base_day";
	private static final String BASE_MONTH = "base_month";
	private static final String BASE_YEAR = "base_year";

	private static final int MOSCOW_GEO_ID = 14974;
	private FrameLayout mShopLoadingProgress;
	private FrameLayout mMetroLoadingProgress;

	private DeliveryLoader mDeliveryLoader;
	private MetroLoader mMetroLoader;
	private ShopsLoader mShopsLoader;

	private Bundle mData;
	private Bundle mInfo;

	private DeliveryBean mCurrentDelivery;
	private ArrayList<DeliveryBean> mDeliveries;
	private ArrayList<ShopBean> mShopArray;
	private ArrayList<MetroBean> mMetroArray;

	private Handler mHandler = new Handler() {
		public void handleMessage (Message msg) {
			Bundle bundle = (Bundle) msg.obj;

			mData.putInt(DAY, bundle.getInt(DAY));
			mData.putInt(MONTH, bundle.getInt(MONTH));
			mData.putInt(YEAR, bundle.getInt(YEAR));

			TextView date_txt = (TextView) findViewById(R.id.order_merge_text_user_date);

			date_txt.setText(mData.getInt(DAY) + "." + (mData.getInt(MONTH) + 1) + "." + mData.getInt(YEAR));
		};
	};

	private int mCurrentDeliveryChoice = -1;

	public void showShopProgress () {
		if (mShopLoadingProgress != null)
			mShopLoadingProgress.setVisibility(View.VISIBLE);
	}

	public void hideShopProgress () {
		if (mShopLoadingProgress != null)
			mShopLoadingProgress.setVisibility(View.GONE);
	}

	public void showMetroProgress () {
		if (mMetroLoadingProgress != null)
			mMetroLoadingProgress.setVisibility(View.VISIBLE);
	}

	public void hideMetroProgress () {
		if (mMetroLoadingProgress != null)
			mMetroLoadingProgress.setVisibility(View.GONE);
	}

	public void onSaveInstanceState (Bundle savedInstanceState) {
		RadioGroup delivery = (RadioGroup) findViewById(R.id.order_ac_radiogroup_delivery);
		savedInstanceState.putInt(DELIVERY_CHOICE, delivery.getCheckedRadioButtonId());
	}

	@Override
	protected void onPause () {
		super.onPause();
		if (mDeliveryLoader != null)
			mDeliveryLoader.cancel(true);
		if (mMetroLoader != null)
			mMetroLoader.cancel(true);
		if (mShopsLoader != null)
			mShopsLoader.cancel(true);

	}

	@Override
	protected void onCreate (Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.order_ac);

		setTitleLeft(getResources().getString(R.string.actionbar_basket));

		// скрываем авто-появление клавиатуры при повороте
		this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

		// запрашиваем с сервера возможные варианты доставки
		Bundle bundle = (Bundle) getLastNonConfigurationInstance();
		if (bundle != null) {
			mDeliveries = (ArrayList<DeliveryBean>) bundle.getSerializable(DELIVERIES_DATA);
			mShopArray = (ArrayList<ShopBean>) bundle.getSerializable(SHOPS_DATA);
			mMetroArray = (ArrayList<MetroBean>) bundle.getSerializable(METRO_DATA);
		}
		
		if (savedInstanceState != null) {
			mCurrentDeliveryChoice = savedInstanceState.getInt(DELIVERY_CHOICE);
		}
		
		if (mDeliveries == null) {
			if (mDeliveryLoader != null)
				mDeliveryLoader.cancel(true);
			mDeliveryLoader = new DeliveryLoader();
			mDeliveryLoader.execute();
		} else {
			setCheckboxes();
		}
		initCardNumber();
	}

	@Override
	protected void onStart(){
		super.onStart();
		FlurryAgent.onStartSession(this, "3X5JRBVFV7QWXNP3Q8H2");
	}
	 
	@Override
	protected void onStop(){
		super.onStop();		
		FlurryAgent.onEndSession(this);
	}
	
	private void initCardNumber () {

		final TextView label = (TextView) findViewById(R.id.order_ac_label_card);
		final EditText card = (EditText) findViewById(R.id.order_ac_edittext_card);

		card.setOnEditorActionListener(new OnEditorActionListener() {

			@Override
			public boolean onEditorAction (TextView v, int actionId, KeyEvent event) {
				if (actionId == EditorInfo.IME_ACTION_DONE) {
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
				if (hasFocus) {
					label.setText("298");
				} else {
					if (card.getText().toString().equals(""))
						label.setText("");
				}
			}
		});
	}

	@Override
	public Object onRetainNonConfigurationInstance () {
		Bundle bundle = new Bundle();
		bundle.putSerializable(DELIVERIES_DATA, mDeliveries);
		bundle.putSerializable(SHOPS_DATA, mShopArray);
		bundle.putSerializable(METRO_DATA, mMetroArray);
		return bundle;
	}
	
	class DeliveryLoader extends AsyncTask<Void, Void, Wrapper<DeliveryBean>> {

		private ProgressDialogFragment progress;

		@Override
		protected void onPreExecute () {
			progress = ProgressDialogFragment.getInstance();
			progress.show(getFragmentManager(), "progress");
		}

		@Override
		protected Wrapper<DeliveryBean> doInBackground (Void... params) {

			Wrapper<DeliveryBean> deliveries = null;

			try {
				JSONObject object = new JSONObject();
				object.put("geo_id", PreferencesManager.getCityid());
				JSONArray arrayProducts = new JSONArray();
				JSONArray arrayServices = new JSONArray();

				List<ProductBean> products = BasketManager.getProducts();
				List<ServiceBean> services = BasketManager.getServicesAll();

				for (int i = 0; i < products.size(); i++) {
					ProductBean product = products.get(i);
					JSONObject tmp = new JSONObject();
					tmp.put("id", product.getId());
					tmp.put("quantity", product.getCount());
					arrayProducts.put(tmp);
				}

				for (int i = 0; i < services.size(); i++) {
					ServiceBean service = services.get(i);
					JSONObject tmp = new JSONObject();
					tmp.put("id", service.getId());
					tmp.put("quantity", service.getCount());
					if (service.getProductId() != 0) {
						tmp.put("product_id", service.getProductId());
					}
					arrayServices.put(tmp);
				}

				object.put("products", arrayProducts);
				object.put("services", arrayServices);

				if (object != null) {
					String result = HTTPUtils.sendPostJSON(URLManager.getDeliveryCalcWithErrors(), object);
					if (result != null) {
						deliveries = new DeliveryParser().parseData(result);
					}
				}
			} catch (Exception e) {
				// NOP
			}

			return deliveries;
		}

		@Override
		protected void onPostExecute (Wrapper<DeliveryBean> result) {
			if (isCancelled())
				return;
			if (Utils.isEmptyList(result.getResult())) {
				if (result.getError() != null){
					if(result.getError().getCode() == 772){
						showEmptyDeliveryDialog("Выбранные вами товары невозможно оформить в один заказ на доставку, если в заказе есть мебель оформите её отдельным заказом");
					}
					else{
						showEmptyDeliveryDialog(result.getError().getMessage());
					}
				}
				else
					showEmptyDeliveryDialog("Не удалось рассчитать доставку");
			} else {
				mDeliveries = (ArrayList<DeliveryBean>) result.getResult();
				progress.dismiss();
				setCheckboxes();
			}
		}
	}

	private void showEmptyDeliveryDialog (String message) {
		OrderNoDeliveryDialogFragment dialog = OrderNoDeliveryDialogFragment.getInstance();
		dialog.setCancelable(false);
		dialog.setMessage(message);
		dialog.setonClickListener(new DialogInterface.OnClickListener() {

			@Override
			public void onClick (DialogInterface dialog, int which) {
				switch (which) {
				case DialogInterface.BUTTON_POSITIVE:
					finish();
					break;

				default:
					break;
				}
			}
		});

		dialog.show(getFragmentManager(), "no_delivery");// TODO
	}

	// Разбираем и выводим полученные варианты доставки
	private void setCheckboxes () {

		for (int i = 0; i < mDeliveries.size(); i++) {

			DeliveryBean delivery = mDeliveries.get(i);
			switch (delivery.getMode_id()) {
			case OrderCompleteActivity.STANDART:
				// Стандартная доставка
				RadioButton delivery_standart = (RadioButton) findViewById(R.id.order_ac_radiobutton_standart);
				delivery_standart.setVisibility(View.VISIBLE);
				delivery_standart.setText(delivery.getDeliveryNameWithPrice());
				break;
			case OrderCompleteActivity.SELF:
				// Самовывоз
				RadioButton delivery_self = (RadioButton) findViewById(R.id.order_ac_radiobutton_self);
				delivery_self.setVisibility(View.VISIBLE);
				delivery_self.setText(delivery.getDeliveryNameWithPrice());
				break;
			default:
				break;
			}
		}
		start();
	}

	private void start () {

		mInfo = new Bundle();

		// Выводим выбранный пользователем город
		TextView city = (TextView) findViewById(R.id.order_ac_text_city);
		city.setText(PreferencesManager.getCityName());

		Button next = (Button) findViewById(R.id.order_ac_button_next);
		next.setOnClickListener(this);

		RadioGroup delivery = (RadioGroup) findViewById(R.id.order_ac_radiogroup_delivery);
		delivery.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged (RadioGroup group, int checkedId) {

				FrameLayout orderframe = (FrameLayout) findViewById(R.id.order_ac_frame);
				orderframe.removeAllViews();
				LayoutInflater inflater = getLayoutInflater();
				View include = null;

				switch (checkedId) {
				case R.id.order_ac_radiobutton_self:
					// Отрисовываем блоки адрес + дата для самовывоза
					if (mShopsLoader != null)
						mShopsLoader.cancel(true);
					if (mMetroLoader != null)
						mMetroLoader.cancel(true);
					include = inflater.inflate(R.layout.order_ac_merge_delivery_self, null);
					orderframe.addView(include);
					mShopLoadingProgress = (FrameLayout) findViewById(R.id.order_merge_progress_frame);
					initDelivery(OrderCompleteActivity.SELF);
					break;

				case R.id.order_ac_radiobutton_standart:
					// Отрисовываем блоки адрес + дата + время для стандартной
					// доставки
					if (mMetroLoader != null)
						mMetroLoader.cancel(true);
					if (mShopsLoader != null)
						mShopsLoader.cancel(true);
					include = inflater.inflate(R.layout.order_ac_merge_delivery_standart, null);
					orderframe.addView(include);
					mMetroLoadingProgress = (FrameLayout) findViewById(R.id.order_merge_progress_frame_metro);
					initDelivery(OrderCompleteActivity.STANDART);
					break;

				default:
					break;
				}
				checkAdditionalLinearVisibility();
			}
		});
		if (mCurrentDeliveryChoice != -1) {

			delivery.clearCheck();
			delivery.check(mCurrentDeliveryChoice);
		}
	}

	private void checkAdditionalLinearVisibility () {
		LinearLayout linear = (LinearLayout) findViewById(R.id.order_ac_additional_linear);
		if (linear.getVisibility() == View.GONE) {
			linear.setVisibility(View.VISIBLE);
			setUserInfo();
		}
	}

	private void setUserInfo () {

		EditText name = (EditText) findViewById(R.id.order_ac_edittext_firstname);
		EditText last_name = (EditText) findViewById(R.id.order_ac_edittext_lastname);
		EditText email = (EditText) findViewById(R.id.order_ac_edittext_email);
		EditText mobile = (EditText) findViewById(R.id.order_ac_edittext_mobile);

		setIfNotNull(name, PreferencesManager.getUserName());
		setIfNotNull(last_name, PreferencesManager.getUserLastName());
		setIfNotNull(email, PreferencesManager.getUserEmail());
		setIfNotNull(mobile, PreferencesManager.getUserMobile());
	}

	private void setIfNotNull (EditText ed, String text) {
		if (!TextUtils.isEmpty(text) && !text.equals("null")) {
			ed.setText(text);
		}
	}

	private void initDelivery (int type) {

		mCurrentDelivery = getDelivery(type);
		if (mCurrentDelivery != null) {
			if (type == OrderCompleteActivity.STANDART) {
				initDatePicker();
				initUserAddressButton();
				if (PreferencesManager.getCityid() == MOSCOW_GEO_ID)
					initMetroSpinner();
			}
			if (type == OrderCompleteActivity.SELF) {
				initDatePicker();
				initShopsSpinner();
			}
		} else
			finish();
	}

	private void initMetroSpinner () {

		if (mMetroArray == null) {
			if (mMetroLoader != null)
				mMetroLoader.cancel(true);
			mMetroLoader = new MetroLoader();
			mMetroLoader.execute();
		} else
			setMetroSpinner();
	}

	private void initShopsSpinner () {

		if (mShopArray == null) {
			if (mShopsLoader != null)
				mShopsLoader.cancel(true);
			mShopsLoader = new ShopsLoader();
			mShopsLoader.execute();
		} else
			setShopsSpinner();
	}

	private void initUserAddressButton () {
		ImageButton user_address = (ImageButton) findViewById(R.id.order_merge_imagebutton_user_address);
		user_address.setOnClickListener(this);
	}

	private void setMetroSpinner () {
		Spinner metro = (Spinner) findViewById(R.id.order_merge_spinner_metro);
		metro.setVisibility(View.VISIBLE);
		OrderMetroSpinnerAdapter adapter = new OrderMetroSpinnerAdapter(this, mMetroArray);
		metro.setAdapter(adapter);
	}

	private void setShopsSpinner () {
		Spinner address = (Spinner) findViewById(R.id.order_merge_spinner_address);
		OrderAddressSpinnerAdapter adapter = new OrderAddressSpinnerAdapter(this, mShopArray);
		address.setAdapter(adapter);
	}

	class MetroLoader extends AsyncTask<Void, Void, ArrayList<MetroBean>> {

		@Override
		protected void onPreExecute () {
			// TODO полупрозрачная крутяшка
			showMetroProgress();
		}

		@Override
		protected ArrayList<MetroBean> doInBackground (Void... params) {
			ArrayList<MetroBean> result = null;
			try {
				result = (ArrayList<MetroBean>) new MetroParser().parse(URLManager.getMetro(PreferencesManager.getCityid()));
			} catch (Exception e) {
			}
			return result;
		}

		@Override
		protected void onPostExecute (ArrayList<MetroBean> result) {

			if (isCancelled())
				return;

			hideMetroProgress();

			if (result == null) {
				// TODO "Ошибка получения данных"
			} else {
				if (result.isEmpty()) {
					// TODO "В данном городе пока нет метро"
					Toast.makeText(OrderActivity.this, "Не удалось загрузить станции метро", Toast.LENGTH_SHORT).show();
				} else {
					MetroBean empty_metro = new MetroBean();
					empty_metro.setId(-1);
					empty_metro.setName("Метро не указанно");
					mMetroArray = result;
					mMetroArray.add(0, empty_metro);
					setMetroSpinner();
				}
			}
		}
	}

	class ShopsLoader extends AsyncTask<Void, Void, ArrayList<ShopBean>> {

		@Override
		protected void onPreExecute () {
			// TODO полупрозрачная крутяшка
			showShopProgress();
		}

		@Override
		protected ArrayList<ShopBean> doInBackground (Void... params) {
			ArrayList<ShopBean> result = null;
			try {
				result = new ShopsParser(URLManager.getShopList(PreferencesManager.getCityid())).parse();
			} catch (Exception e) {
				// NOP
			}
			return result;
		}

		@Override
		protected void onPostExecute (ArrayList<ShopBean> result) {

			if (isCancelled())
				return;

			hideShopProgress();

			if (result == null) {
				// TODO "Ошибка получения данных"
			} else {
				if (result.isEmpty()) {
					// TODO "В данном городе пока нет магазинов"
				} else {
					mShopArray = result;
					setShopsSpinner();
				}
			}
		}
	}

	private void initDatePicker () {
		initdate();
		RelativeLayout date_field = (RelativeLayout) findViewById(R.id.order_merge_linear_date);
		
		date_field.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick (View v) {
				FragmentManager manager = getFragmentManager();
				FragmentTransaction transaction = manager.beginTransaction();

				DatePickerDialogFragment datePicker = new DatePickerDialogFragment(mHandler);
				datePicker.setArguments(mData);
				transaction.add(datePicker, "date_picker");

				transaction.commit();
			}
		});
		
		TextView date_txt = (TextView) findViewById(R.id.order_merge_text_user_date);

		date_txt.setText(mData.getInt(DAY) + "." + (mData.getInt(MONTH) + 1) + "." + mData.getInt(YEAR));
	}

	private void initdate () {

		mData = new Bundle();

		String base_string_date = mCurrentDelivery.getDateStringFromServer();

		SimpleDateFormat fromServer = new SimpleDateFormat("yyyy-MM-dd");
		Date dateServer = null;
		try {
			dateServer = fromServer.parse(base_string_date);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		Calendar cal = Calendar.getInstance();
		cal.setTime(dateServer);

		mData.putInt(DAY, cal.get(Calendar.DAY_OF_MONTH));
		mData.putInt(MONTH, cal.get(Calendar.MONTH));
		mData.putInt(YEAR, cal.get(Calendar.YEAR));

		mData.putInt(BASE_DAY, cal.get(Calendar.DAY_OF_MONTH));
		mData.putInt(BASE_MONTH, cal.get(Calendar.MONTH));
		mData.putInt(BASE_YEAR, cal.get(Calendar.YEAR));
	}

	private DeliveryBean getDelivery (int mode_id) {

		DeliveryBean delivery = null;
		for (int i = 0; i < mDeliveries.size(); i++) {
			delivery = mDeliveries.get(i);
			if (delivery.getMode_id() == mode_id) {
				return delivery;
			}
		}
		return null;
	}

	private boolean isValidEditText (EditText edit) {
		edit.setFocusableInTouchMode(true);
		edit.requestFocus();
		String stringToCheck = edit.getText().toString().trim();
		return stringToCheck.equals("");
	}

	private boolean isValidTextView (TextView text) {
		String stringToCheck = text.getText().toString().trim();
		return stringToCheck.equals("");
	}

	private boolean isValidRadioGroupSelect (RadioGroup group) {
		int select = group.getCheckedRadioButtonId();
		if (select == -1) {
			return true;
		}
		return false;
	}

	private boolean isValidSpinnerSelect (Spinner spinner) {
		spinner.setFocusableInTouchMode(true);
		spinner.requestFocus();
		int select = (int) spinner.getSelectedItemId();
		if (select == -1) {
			return true;
		}
		return false;
	}

	public static boolean isEmailValid (String email) {

		String expression = "^[\\w\\.-]+@([\\w\\-]+\\.)+[A-Z]{2,4}$";
		CharSequence inputStr = email;
		Pattern pattern = Pattern.compile(expression, Pattern.CASE_INSENSITIVE);
		Matcher matcher = pattern.matcher(inputStr);
		return matcher.matches();
	}

	private void putEditTextToBundle (EditText text, String name) {
		String txt = text.getText().toString().trim();
		mInfo.putString(name, txt);
	}

	private void putDateToBundle (String name_show, String name_send) {
		String date_show = mData.getInt(DAY) + "." + (mData.getInt(MONTH) + 1) + "." + mData.getInt(YEAR);

		// hard date format
		String day;
		String month;

		if (mData.getInt(DAY) < 10) {
			day = "0" + String.valueOf(mData.getInt(DAY));
		} else {
			day = String.valueOf(mData.getInt(DAY));
		}

		if (mData.getInt(MONTH) < 9) {
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

	private void putSpinnerShopAddressSelectToBundle (Spinner spinner, String name_id, String name) {
		int select = (int) spinner.getSelectedItemId();
		mInfo.putInt(name_id, mShopArray.get(select).getId());
		mInfo.putString(name, mShopArray.get(select).getName());
	}

	private void putRadioGroupSelectToBundle (RadioGroup group, String name) {
		int select = group.getCheckedRadioButtonId();
		switch (select) {
		case R.id.order_ac_radiogroup_widget_cash:
			mInfo.putInt(name, OrderCompleteActivity.CASH);
			break;
		case R.id.order_ac_radiogroup_widget_bankcard:
			mInfo.putInt(name, OrderCompleteActivity.BANKCARD);
			break;
		default:
			break;
		}
	}

	private boolean checkAllFields () {

		EditText firstname = (EditText) findViewById(R.id.order_ac_edittext_firstname);
		EditText lastname = (EditText) findViewById(R.id.order_ac_edittext_lastname);
		EditText email = (EditText) findViewById(R.id.order_ac_edittext_email);
		EditText mobile = (EditText) findViewById(R.id.order_ac_edittext_mobile);
		EditText card = (EditText) findViewById(R.id.order_ac_edittext_card);
		RadioGroup payment_type = (RadioGroup) findViewById(R.id.order_ac_radiogroup_payment);

		if (isValidEditText(firstname)) {
			Toast.makeText(this, Html.fromHtml("Введите <b>Имя</b>"), Toast.LENGTH_SHORT).show();
			return false;
		}
		
		if (email.getText().toString().length() > 0) {
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
			Toast.makeText(
					this,
					Html.fromHtml("<b>В номере карты «Связной-Клуб» допущена ошибка.</b>\nПроверьте правильность ввода номера, расположенного на обороте под штрих кодом, и повторите попытку")
					,Toast.LENGTH_LONG).show();
			return false;
		}

		switch (mCurrentDelivery.getMode_id()) {
		case OrderCompleteActivity.STANDART:
			EditText street = (EditText) findViewById(R.id.order_merge_edittext_address_street);
			EditText house = (EditText) findViewById(R.id.order_merge_edittext_address_house);
			EditText housing = (EditText) findViewById(R.id.order_merge_edittext_address_housing);
			EditText floor = (EditText) findViewById(R.id.order_merge_edittext_address_floor);
			EditText flat = (EditText) findViewById(R.id.order_merge_edittext_address_flat);

			TextView standart_date = (TextView) findViewById(R.id.order_merge_text_user_date);
			Spinner metro = (Spinner) findViewById(R.id.order_merge_spinner_metro);

			if (isValidEditText(street)) {
				Toast.makeText(this, Html.fromHtml("Введите <b>название Улицы для доставки</b>"), Toast.LENGTH_SHORT).show();
				return false;
			}
			if (isValidEditText(house)) {
				Toast.makeText(this, Html.fromHtml("Введите <b>номер Дома для доставки</b>"), Toast.LENGTH_SHORT).show();
				return false;
			}

			if (isValidTextView(standart_date)) {
				Toast.makeText(this, Html.fromHtml("Выберете <b>Дату доставки</b>"), Toast.LENGTH_SHORT).show();
				return false;
			}

			AddressBean bean_address = new AddressBean();
			bean_address.setStreet(street.getText().toString());
			bean_address.setHouse(house.getText().toString());
			bean_address.setHousing(housing.getText().toString());
			bean_address.setFloor(floor.getText().toString());
			bean_address.setFlat(flat.getText().toString());

			if (!Utils.isEmptyList(mMetroArray)) {
				int select = (int) metro.getSelectedItemId();
				if (select != -1)
					bean_address.setMetro(mMetroArray.get(select));
			}
			mInfo.putSerializable(OrderCompleteActivity.ADDRESS_BEAN, bean_address);

			putDateToBundle(OrderCompleteActivity.DATE_SHOW, OrderCompleteActivity.DELIVERY_DATE);
			break;

		case OrderCompleteActivity.SELF:
			Spinner select_address = (Spinner) findViewById(R.id.order_merge_spinner_address);
			TextView self_date = (TextView) findViewById(R.id.order_merge_text_user_date);
			if (isValidSpinnerSelect(select_address)) {
				Toast.makeText(this, Html.fromHtml("Выберете <b>Магазин для самовывоза</b>"), Toast.LENGTH_SHORT).show();
				return false;
			}

			if (isValidTextView(self_date)) {
				Toast.makeText(this, Html.fromHtml("Укажите <b>Дату самовывоза</b> товара"), Toast.LENGTH_SHORT).show();
				return false;
			}

			putSpinnerShopAddressSelectToBundle(select_address, OrderCompleteActivity.SHOP_ID, OrderCompleteActivity.ADDRESS);
			putDateToBundle(OrderCompleteActivity.DATE_SHOW, OrderCompleteActivity.DELIVERY_DATE);
			break;
		default:
			return false;
		}

		putEditTextToBundle(firstname, OrderCompleteActivity.FIRSTNAME);
		putEditTextToBundle(lastname, OrderCompleteActivity.LASTNAME);
		putEditTextToBundle(email, OrderCompleteActivity.EMAIL);
		putEditTextToBundle(mobile, OrderCompleteActivity.MOBILE);
		putCardTextToBundle(card);
		putRadioGroupSelectToBundle(payment_type, OrderCompleteActivity.PAYMENT_ID);

		return true;
	}

	private void setDeliveryInfo () {
		mInfo.putInt(OrderCompleteActivity.DELIVERY_PRICE, mCurrentDelivery.getPrice());

		switch (mCurrentDelivery.getMode_id()) {
		case OrderCompleteActivity.SELF:
			mInfo.putInt(OrderCompleteActivity.DELIVERY_ID, OrderCompleteActivity.SELF);

			break;
		case OrderCompleteActivity.STANDART:
			mInfo.putInt(OrderCompleteActivity.DELIVERY_ID, OrderCompleteActivity.STANDART);
			break;
		default:
			break;
		}
	}

	@Override
	public void onClick (View v) {

		switch (v.getId()) {
		case R.id.order_ac_button_next:
			// Проверяем и записываем данные, введённые пользователем
			if (checkAllFields()) {
				// Записываем тип и цену доставки товаров
				setDeliveryInfo();
				Intent intent = new Intent(OrderActivity.this, OrderCompleteActivity.class);
				intent.putExtra(OrderCompleteActivity.INFO, mInfo);
				startActivity(intent);
			}
			break;

		case R.id.order_merge_imagebutton_user_address:
			if (PreferencesManager.isAuthorized()) {
				OrderUserAddressDialogFragment addressFragment = OrderUserAddressDialogFragment.getInstance();
				addressFragment.setOnSelectAddressListener(new OnSelectAddressListener() {

					@Override
					public void onSelectAddress (AddressBean bean) {
						EditText street = (EditText) findViewById(R.id.order_merge_edittext_address_street);
						EditText house = (EditText) findViewById(R.id.order_merge_edittext_address_house);
						EditText housing = (EditText) findViewById(R.id.order_merge_edittext_address_housing);
						EditText floor = (EditText) findViewById(R.id.order_merge_edittext_address_floor);
						EditText flat = (EditText) findViewById(R.id.order_merge_edittext_address_flat);

						if (!TextUtils.isEmpty(bean.getStreet())) {
							street.setText(bean.getStreet());
							house.setText(bean.getHouse());
							housing.setText(bean.getHousing());
							floor.setText(bean.getFloor());
							flat.setText(bean.getFlat());
						} else {
							street.setText(bean.getAddress());
							house.setText("");
							housing.setText("");
							floor.setText("");
							flat.setText("");
						}
					}
				});
				addressFragment.show(getFragmentManager(), "addressList");
			} else {
				Toast.makeText(this, "Для получения Списка адресов необходимо авторизоваться", Toast.LENGTH_SHORT).show();
			}
			break;
		default:
			break;
		}
	}

	private boolean isCardValid (EditText card) {
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

	private void putCardTextToBundle (EditText card) {
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
