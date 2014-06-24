package ru.enter.widgets;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import ru.enter.CheckoutGeoLocationActivity;
import ru.enter.CitiesListActivity;
import ru.enter.R;
import ru.enter.DataManagement.BasketData;
import ru.enter.DataManagement.BasketManager;
import ru.enter.DataManagement.PersonData;
import ru.enter.beans.AddressBean;
import ru.enter.beans.CheckoutBean;
import ru.enter.beans.CheckoutBean.CheckoutFirstStepDelivery;
import ru.enter.beans.DeliveryBean;
import ru.enter.beans.MetroBean;
import ru.enter.beans.PersonBean;
import ru.enter.beans.ProductBean;
import ru.enter.beans.ServiceBean;
import ru.enter.parsers.DeliveryParser;
import ru.enter.parsers.MetroParser;
import ru.enter.parsers.PersonParser;
import ru.enter.utils.Formatters;
import ru.enter.utils.ICheckoutInterface;
import ru.enter.utils.Log;
import ru.enter.utils.PreferencesManager;
import ru.enter.utils.RequestManagerThread;
import ru.enter.utils.URLManager;
import ru.enter.utils.Utils;
import ru.enter.utils.Wrapper;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

public class CheckoutFirstStepView extends FrameLayout implements OnClickListener, OnCheckedChangeListener, ICheckoutInterface {
	private View mRootView;
	private RadioGroup mCheckoutFirstStepDeliveryRG;
	private View mFirstStepViewFrame_1, mFirstStepViewFrame_2, mDateFrame;
	private CheckoutBean mCheckoutBean;
	private Button mNextB;
//	private EditText mStreetNameET;
	private Spinner mDateSpinner;
	public static final SimpleDateFormat FORMAT = new SimpleDateFormat("dd.MM.yyyy");
	public static final SimpleDateFormat FORMAT_CHECKOUT = new SimpleDateFormat("yyyy-MM-dd");
	private static final int RESULT_OK = 1;
	private Context mContext;
	private ArrayList<DeliveryBean> mDeliveries;
	private DeliveryBean mBean;
	private Date mDate;
	private boolean loadAdress;
	private MetroBean mCurrentSelectMetro;
	
	private static final String NO_CHOICE = "не указано";

	private Handler mHandler = new Handler() {

		@Override
		public void handleMessage (Message msg) {
			switch (msg.what) {
			case RESULT_OK:
				Wrapper<DeliveryBean> result = new DeliveryParser().parseData((String) msg.obj); 
				if (Utils.isEmptyList(result.getResult())) {
					if (result.getError() != null){
						if(result.getError().getCode() == 772){
							showEmptyDeliveryDialog("Выбранные вами товары невозможно оформить в один заказ на доставку, если в заказе есть мебель оформите её отдельным заказом");
						}
						else{
							showEmptyDeliveryDialog(result.getError().getMessage());
						}
					}
					else{
						showEmptyDeliveryDialog("Не удалось рассчитать доставку");
					}
				} else {
					mDeliveries = (ArrayList<DeliveryBean>) result.getResult();
					for (DeliveryBean delivery : mDeliveries) {
						RadioButton radio = new RadioButton(mContext);
						radio.setText(delivery.getDeliveryNameWithPrice());
						radio.setId(delivery.getMode_id());
						radio.setButtonDrawable(R.drawable.radiobutton_background);
						mCheckoutFirstStepDeliveryRG.addView(radio);
					}
				}
				break;
			default:
				break;
			}
		}
	};
	
	private TextView mCheckoutFirstStepCurrentCityTV;
	private EditText mAddressEdit;
	private Spinner mSelfDateSpinner;
	private TextView textSelectDelivery1;
	private View topViewSelectDelivery;

	private TextView mMetro;
	private EditText mStreet;
	private EditText mHouse;
	private EditText mHousing;
	private EditText mFlat;
	private EditText mFloor;

	private void showEmptyDeliveryDialog(String message){
		final Activity activity = (Activity)mContext;
		Context context = (activity.getParent() == null) ? activity : activity.getParent();
		
		AlertDialog.Builder dlg = new AlertDialog.Builder(context);				    			
        dlg.setTitle("Расчёт доставки товаров")
        .setMessage(message)
        .setCancelable(false)
        .setPositiveButton("Вернуться", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {    
            	activity.finish();
        }})
        .create()
        .show(); 
 }
	
	public CheckoutFirstStepView(Context context) {
		super(context);
		mContext = context;
		loadAdress = false;
		mCheckoutBean = BasketData.getInstance().getCheckoutBean();

		// /////////////////////////////////////////////////////////////////
		calcDelivery(context);
		// /////////////////////////////////////////////////////////////////

		mRootView = LayoutInflater.from(context).inflate(R.layout.checkout_first_step, null);

		TextView title = (TextView) mRootView.findViewById(R.id.checkout_first_step_title_tv);
		title.setTypeface(Utils.getTypeFace(context));

		mCheckoutFirstStepCurrentCityTV = (TextView) mRootView.findViewById(R.id.checkout_first_step_current_city_tv);
		mCheckoutFirstStepCurrentCityTV.setText(PreferencesManager.getCityName());
		// === to add city choosing uncomment
		// mCheckoutFirstStepCurrentCityTV.setClickable(true);
		// mCheckoutFirstStepCurrentCityTV.setOnClickListener(this);
		// ===

		mCheckoutFirstStepDeliveryRG = (RadioGroup) mRootView.findViewById(R.id.checkout_first_step_delivery_rg);
		mCheckoutFirstStepDeliveryRG.setOnCheckedChangeListener(this);

		mFirstStepViewFrame_1 = mRootView.findViewById(R.id.checkout_first_step_frame_1);
		mFirstStepViewFrame_2 = mRootView.findViewById(R.id.checkout_first_step_frame_2);
		mFirstStepViewFrame_1.setVisibility(View.GONE);
		mFirstStepViewFrame_2.setVisibility(View.GONE);

		mNextB = (Button) mRootView.findViewById(R.id.checkout_first_step_next_b);

		mAddressEdit = (EditText) mRootView.findViewById(R.id.checkout_second_step_geo_address_edit);
		mAddressEdit.setOnClickListener(this);
		mAddressEdit.setFocusable(false);
		mAddressEdit.setKeyListener(null);

		textSelectDelivery1 = (TextView) mRootView.findViewById(R.id.checkout_second_step_address_text1);
		topViewSelectDelivery = mRootView.findViewById(R.id.checkout_second_step_address_top1);
		
		if ( ! PreferencesManager.isAuthorized()) {
			topViewSelectDelivery.setVisibility(View.GONE);
			textSelectDelivery1.setText("Введите адрес доставки");
		}
		
		mSelfDateSpinner = (Spinner) mRootView.findViewById(R.id.checkout_second_step_geo_spinner);
		Button chooseShop = (Button) mRootView.findViewById(R.id.checkout_second_step_geo_choose_shop_b);
		Button chooseGeoShop = (Button) mRootView.findViewById(R.id.checkout_second_step_geo_geolocation_b);

		chooseShop.setOnClickListener(this);
		chooseGeoShop.setOnClickListener(this);

		// TODO
		mMetro = (TextView) mRootView.findViewById(R.id.checkout_second_step_address_metro);
		mStreet = (EditText) mRootView.findViewById(R.id.checkout_second_step_address_street);
		mHouse = (EditText) mRootView.findViewById(R.id.checkout_second_step_address_house);
		mHousing = (EditText) mRootView.findViewById(R.id.checkout_second_step_address_housing);
		mFlat = (EditText) mRootView.findViewById(R.id.checkout_second_step_address_flat);
		mFloor = (EditText) mRootView.findViewById(R.id.checkout_second_step_address_floor);
		
		//загрузка метро
		if ( ! PreferencesManager.getCityName().equalsIgnoreCase("Москва")) {
			mMetro.setVisibility(View.GONE);
		} else {
			mMetro.setVisibility(View.VISIBLE);
			mMetro.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick (View v) {
					new MetroLoader().execute();
				}
			});
		}

		Button chooseAddress = (Button) mRootView.findViewById(R.id.checkout_second_step_address_change_btn);
		chooseAddress.setOnClickListener(this);

		mDateFrame = mRootView.findViewById(R.id.checkout_first_step_time_frame);
		mDateFrame.setVisibility(View.GONE);
		mDateSpinner = (Spinner) mRootView.findViewById(R.id.checkout_first_step_date_spinner);
		mDateSpinner.setEnabled(false);
		addView(mRootView);
	}

	public void refreshEdit () {
		mAddressEdit.setText(BasketData.getInstance().getCheckoutBean().getShopAddress());
		mCheckoutFirstStepCurrentCityTV.setText(PreferencesManager.getCityName());
	}

	private void calcDelivery (Context context) {
		try {
			JSONObject object = new JSONObject();
			object.put("geo_id", PreferencesManager.getCityid());
			JSONArray array = new JSONArray();
			for (ProductBean bean : BasketManager.getProducts()) {
				JSONObject tmp = new JSONObject();
				tmp.put("id", bean.getId());
				tmp.put("quantity", bean.getCount());
				array.put(tmp);
			}
			object.put("products", array);

			JSONArray array2 = new JSONArray();
			for (ServiceBean bean : BasketManager.getServicesAll()) {
				JSONObject tmp = new JSONObject();
				tmp.put("id", bean.getId());
				tmp.put("quantity", bean.getCount());
				if (bean.getProductId() != 0) {
					tmp.put("product_id", bean.getProductId());
				}
				array2.put(tmp);
			}
			object.put("services", array2);

			RequestManagerThread requestManagerThread = new RequestManagerThread(mHandler, object, URLManager.getDeliveryCalcWithErrors(), RESULT_OK);
			requestManagerThread.start();

		} catch (Exception e) {}
	}

	private ArrayList<String> getDate () {
		ArrayList<String> dateList = new ArrayList<String>(7);
		long day = 24 * 60 * 60 * 1000;
		try {
			mDate = FORMAT_CHECKOUT.parse(mBean.getDateStringFromServer());
			for (int i = 0; i < 7; i++) {
				dateList.add(FORMAT.format(new Date(mDate.getTime() + i * day)));
			}
		} catch (ParseException pe) {
			Log.e("FirstStep", pe.toString());
		}
		return dateList;
	}

	private void initSelfSpinner () {
		ArrayAdapter<String> adapterDate = new ArrayAdapter<String>(getContext(), android.R.layout.simple_spinner_item, getDate());
		adapterDate.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		mSelfDateSpinner.setAdapter(adapterDate);
		mSelfDateSpinner.setOnItemSelectedListener(new OnItemSelectedListener() {
			public void onItemSelected (AdapterView<?> parent, View view, int position, long id) {
				// самовывоз. шаг1
				long day = 24 * 60 * 60 * 1000;
				mCheckoutBean.setDeliveryDate(FORMAT_CHECKOUT.format(new Date(mDate.getTime() + position * day)));
			}

			public void onNothingSelected (AdapterView<?> parent) {}
		});
	}

	private void initSpinner () {
		ArrayAdapter<String> adapterDate = new ArrayAdapter<String>(getContext(), android.R.layout.simple_spinner_item, getDate());
		adapterDate.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		mDateSpinner.setAdapter(adapterDate);
		mDateSpinner.setOnItemSelectedListener(new OnItemSelectedListener() {
			public void onItemSelected (AdapterView<?> parent, View view, int position, long id) {
				// Доставка. шаг1
				long day = 24 * 60 * 60 * 1000;
				mCheckoutBean.setDeliveryDate(FORMAT_CHECKOUT.format(new Date(mDate.getTime() + position * day)));
			}

			public void onNothingSelected (AdapterView<?> parent) {}
		});
	}

	@Override
	public void setOnClickListener (OnClickListener l) {
		mNextB.setOnClickListener(l);
	}

	@Override
	public void onCheckedChanged (RadioGroup group, int checkedId) {
		mDateSpinner.setEnabled(true);
		for (DeliveryBean bean : mDeliveries) {
			if (bean.getMode_id() == checkedId) {
				mCheckoutBean.setDeliveryPrice(bean.getPrice());
				mBean = bean;
			}
		}
		switch (checkedId) {
		case 1:
			mCheckoutBean.setCheckoutFirstStepDelivery(CheckoutFirstStepDelivery.standart);
			mFirstStepViewFrame_1.setVisibility(View.VISIBLE);
			mFirstStepViewFrame_2.setVisibility(View.GONE);
			mDateFrame.setVisibility(View.VISIBLE);
			initSpinner();
			break;
		case 2:
			mCheckoutBean.setCheckoutFirstStepDelivery(CheckoutFirstStepDelivery.express);
			mFirstStepViewFrame_1.setVisibility(View.VISIBLE);
			mFirstStepViewFrame_2.setVisibility(View.GONE);
			mDateFrame.setVisibility(View.VISIBLE);
			initSpinner();
			break;
		case 3:
			mCheckoutBean.setCheckoutFirstStepDelivery(CheckoutFirstStepDelivery.pickup);
			mFirstStepViewFrame_1.setVisibility(View.GONE);
			mFirstStepViewFrame_2.setVisibility(View.VISIBLE);
			mDateFrame.setVisibility(View.GONE);
			initSelfSpinner();
			break;
		case 4:
			mCheckoutBean.setCheckoutFirstStepDelivery(CheckoutFirstStepDelivery.shop);
			mFirstStepViewFrame_1.setVisibility(View.GONE);
			mFirstStepViewFrame_2.setVisibility(View.VISIBLE);
			mDateFrame.setVisibility(View.GONE);
			initSelfSpinner();
			break;
		default:
			break;
		}
	}

	@Override
	public void onClick (View v) {
		switch (v.getId()) {
		case R.id.checkout_first_step_change_current_city_btn:// сменить город
		case R.id.checkout_first_step_current_city_tv:// сменить город по клику
			getContext().startActivity(new Intent(mContext, CitiesListActivity.class));
			break;
		case R.id.checkout_second_step_geo_choose_shop_b:
			runGeo(CheckoutGeoLocationActivity.SelectedButton.list.name());
			break;
		case R.id.checkout_second_step_geo_address_edit:
			runGeo(CheckoutGeoLocationActivity.SelectedButton.list.name());
			break;
		case R.id.checkout_second_step_geo_geolocation_b:
			runGeo(CheckoutGeoLocationActivity.SelectedButton.map.name());
			break;
		case R.id.checkout_second_step_address_change_btn:
			// showAddressDialog();
			startAdressDialog();
			break;
		default:
			break;
		}
	}

	private void runGeo (String string) {
		Intent intent = new Intent();
		intent.setClass(getContext(), CheckoutGeoLocationActivity.class);
		intent.putExtra(CheckoutGeoLocationActivity.CHOSED_BTN, string);
		getContext().startActivity(intent);
	}

	private void showAddressDialog () {
		PersonBean personBean = PersonData.getInstance().getPersonBean();
		final ArrayList<AddressBean> address = personBean.getAddressList();
		if (address != null) {
			if (address.size() > 0) {
				ArrayList<String> items = new ArrayList<String>(address.size());
				for (AddressBean bean : address) {
					items.add(Formatters.createAddressString(bean));
				}
				ArrayAdapter<String> adapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_list_item_1, items);
				AlertDialog.Builder adb = new AlertDialog.Builder(getContext());
				adb.setTitle("Выберите адрес").setAdapter(adapter, new DialogInterface.OnClickListener() {
					public void onClick (DialogInterface dialog, int which) {
						CheckoutBean checkoutBean = BasketData.getInstance().getCheckoutBean();
						
						AddressBean userAddress = address.get(which);
						if (TextUtils.isEmpty(userAddress.getAddress())) {
							mStreet.setText(Utils.useIfNotNull(userAddress.getStreet()));
							mHouse.setText(Utils.useIfNotNull(userAddress.getHouse()));
							mHousing.setText(Utils.useIfNotNull(userAddress.getHousing()));
							mFlat.setText(Utils.useIfNotNull(userAddress.getFlat()));
							mFloor.setText(Utils.useIfNotNull(userAddress.getFloor()));
						} else {
							//если адресс старого формата, то вставляем его весь в улицу
							mStreet.setText(userAddress.getAddress());
						}
						
						checkoutBean.setUserAddress(userAddress);
					}
				}).create().show();
			} else {
				Toast.makeText(getContext(), "У вас нет сохраненных адресов доставки", Toast.LENGTH_SHORT).show();
			}
			loadAdress = false;
		}

	}
	
	public void showMetroDialog (final List<MetroBean> metroList) {
		
		if (Utils.isEmptyList(metroList)) {
			Toast.makeText(mContext, "Не удалось загрузить станции метро", Toast.LENGTH_SHORT).show();
		} else {
			ArrayList<String> metroNames = new ArrayList<String>(metroList.size());
			
			metroNames.add(NO_CHOICE);
			
			for (MetroBean metro : metroList) {
				metroNames.add(metro.getName());
			}
			
			ArrayAdapter<String> adapter = new ArrayAdapter<String>(mContext, android.R.layout.simple_list_item_1, metroNames);
			
			AlertDialog.Builder adb = new AlertDialog.Builder(mContext);
			adb.setTitle("Выберите станцию")
			.setAdapter(adapter, new DialogInterface.OnClickListener() {
				public void onClick (DialogInterface dialog, int which) {
					if (which == 0) {
						mCurrentSelectMetro = null;
						mMetro.setText(NO_CHOICE);
					} else {
						mCurrentSelectMetro = metroList.get(which - 1);
						mMetro.setText (mCurrentSelectMetro.getName());
					}
				}
			}).create().show();
		}
	}

	private void startAdressDialog () {
		ArrayList<AddressBean> address = PersonData.getInstance().getPersonBean().getAddressList();
		if (address == null || address.isEmpty()) {
			if (!loadAdress)
				new AdressParser().execute();
			loadAdress = true;
		} else
			showAddressDialog();
	}

	private class AdressParser extends AsyncTask<Void, Void, Boolean> {

		@Override
		protected void onPreExecute () {
			// mProgress.setVisibility(View.VISIBLE);
		}

		@Override
		protected Boolean doInBackground (Void... params) {
			return new PersonParser(mContext).parse(URLManager.getUser(PreferencesManager.getToken()));
		}

		@Override
		protected void onPostExecute (Boolean result) {
			showAddressDialog();
		}

	}
	
	private class MetroLoader extends AsyncTask<Void, Void, List<MetroBean>> {//TODO
		
		ProgressDialog mProgress;

		@Override
		protected void onPreExecute () {
			mProgress = ProgressDialog.show(mContext, "", "Загрузка");
		}

		@Override
		protected List<MetroBean> doInBackground (Void... params) {
			int city = PreferencesManager.getCityid();
			return new MetroParser().parse(URLManager.getMetro(city));
		}

		@Override
		protected void onPostExecute (List<MetroBean> result) {
			mProgress.dismiss();
			showMetroDialog(result);
		}

	}

	public boolean isNext () {
		int checkID = mCheckoutFirstStepDeliveryRG.getCheckedRadioButtonId();
		switch (checkID) {
		case 1:
			String street = mStreet.getText().toString();
			String house = mHouse.getText().toString();
			String housing = mHousing.getText().toString();
			String flat = mFlat.getText().toString();
			String floor = mFloor.getText().toString();
			
			AddressBean userAddress = new AddressBean();
			
			if (mCurrentSelectMetro != null) {
				userAddress.setMetro(mCurrentSelectMetro);
			}
			
			userAddress.setStreet(street);
			userAddress.setHouse(house);
			userAddress.setHousing(housing);
			userAddress.setFlat(flat);
			userAddress.setFloor(floor);
			
			mCheckoutBean.setUserAddress(userAddress);
			
			mCheckoutBean.setCheckoutFirstStepDelivery(CheckoutFirstStepDelivery.standart);
			
			if (mCheckoutBean.getUserAddress().getStreet().equals("")) {
				Toast.makeText(mContext, "Улица обязательна для заполнения", Toast.LENGTH_SHORT).show();
				return false;
			}
			
			if (mCheckoutBean.getUserAddress().getHouse().equals("")) {
				Toast.makeText(mContext, "Номер дома обязателен для заполнения", Toast.LENGTH_SHORT).show();
				return false;
			}
			
			return true;
		case 3:
			mCheckoutBean.setCheckoutFirstStepDelivery(CheckoutFirstStepDelivery.pickup);
			
			//TODO
			if (mCheckoutBean.getShop_id() == 0) {
				Toast.makeText(mContext, "Выберите магазин", Toast.LENGTH_SHORT).show();
				return false;
			}
			
			return true;
		default:
			Toast.makeText(mContext, "Выберите тип доставки", Toast.LENGTH_SHORT).show();
			return false;
		}
	}

	@Override
	public boolean isSvyaznoyCardValid () {
		return true;
	}
}
