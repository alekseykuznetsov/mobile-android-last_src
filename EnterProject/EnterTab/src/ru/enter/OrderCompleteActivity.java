package ru.enter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.flurry.android.FlurryAgent;
import com.google.analytics.tracking.android.EasyTracker;
import com.google.analytics.tracking.android.Tracker;
import com.google.analytics.tracking.android.Transaction;
import com.google.analytics.tracking.android.Transaction.Item;

import ru.enter.DataManagement.BasketManager;
import ru.enter.DataManagement.PersonData;
import ru.enter.adapters.OrderCompleteListAdapter;
import ru.enter.base.BaseActivity;
import ru.enter.beans.AddressBean;
import ru.enter.beans.ProductBean;
import ru.enter.beans.ServiceBean;
import ru.enter.dialogs.AddressDialogFragment;
import ru.enter.dialogs.AgreementDialogFragment;
import ru.enter.dialogs.ProgressDialogFragment;
import ru.enter.dialogs.alert.OrderCompleteSuccessDialogFragment;
import ru.enter.interfaces.IBasketElement;
import ru.enter.utils.Constants;
import ru.enter.utils.Formatters;
import ru.enter.utils.HTTPUtils;
import ru.enter.utils.PreferencesManager;
import ru.enter.utils.TypefaceUtils;
import ru.enter.utils.URLManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Paint;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class OrderCompleteActivity extends BaseActivity implements OnClickListener {

	public static final String INFO = "info";
	public static final String DATE_SHOW = "date";
	public static final String DELIVERY_PRICE = "delivery_price";
	public static final String DELIVERY_DATE = "delivery_date";
	public static final String DELIVERY_ID = "delivery_type_id";
	
	public static final String ADDRESS = "address";
	public static final String SHOP_ID = "shop_id";
	public static final String METRO_ID = "metro_id";
	public static final String METRO = "metro";
	
	public static final String ADDRESS_BEAN = "address_bean";
	
	public static final String FIRSTNAME = "user_first";
	public static final String LASTNAME = "user_last";
	public static final String EMAIL = "email";
	public static final String MOBILE = "mobile";
	public static final String SVYAZNOY_CARD = "svyaznoy_club_card_number";

	public static final String PAYMENT_ID = "payment_id";
	public static final String CASH_NAME = "наличными";
	public static final String BANKCARD_NAME = "банковской картой";
	public static final int CASH = 1;
	public static final int BANKCARD = 2;
	
	public static final String SELF_NAME = "самовывоз";
	public static final String STANDART_NAME = "курьером";
	public static final int SELF = 3;
	public static final int STANDART = 1;
	
	private OrderCompleteListAdapter mAdapter;
	private ProgressDialogFragment mProgressDialog;
	private CheckBox mAgreement;
	private SendOrder sendOrderLoader;
	private AddressBean mAddress;
	
	private Bundle info;
	private boolean mDiscountInOrder;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		EasyTracker.getInstance().setContext(this);

		setContentView(R.layout.order_complete_ac);
		info = new Bundle();
		
		setTitleLeft(getResources().getString(R.string.actionbar_basket));

		// подчеркнутый текст у соглашения
		mAgreement = (CheckBox) findViewById(R.id.order_complete_ac_chekbox_agreement);
		mAgreement.setTypeface(TypefaceUtils.getBoldTypeface());
		
		TextView agreementText = (TextView) findViewById(R.id.order_complete_ac_text_agreement);
		agreementText.setPaintFlags(agreementText.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
		agreementText.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// вывод текста соглашения
				AgreementDialogFragment agreementDialog = AgreementDialogFragment.getInstance();
				agreementDialog.show(getFragmentManager(), "agreement_dialog");
			}
		});
		
		ListView listView = (ListView) findViewById(R.id.order_complete_ac_listview);

		mAdapter = new OrderCompleteListAdapter(this);
		listView.setAdapter(mAdapter);

		 Button confirm_order = (Button) findViewById(R.id.order_complete_ac_button_confirm_order);
		 confirm_order.setOnClickListener(this);

	}

	@Override
	protected void onPause() {
		if (sendOrderLoader != null) sendOrderLoader.cancel(true);
		super.onPause();
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
	
	private void initCounts(List<IBasketElement> objects) {

		int products_count = 0;
		int services_count = 0;
		int total_price = 0;

		List<ProductBean> products = BasketManager.getProducts();
		List<ServiceBean> services = BasketManager.getServicesAll();

		for (int i = 0; i < products.size(); i++) {
			ProductBean product = products.get(i);
			int count = product.getCount();
			total_price += product.getPrice() * count;
			products_count += count;
			
			if(product.getPrice_old() > product.getPrice()){
				mDiscountInOrder = true;
			}
		}

		for (int i = 0; i < services.size(); i++) {
			ServiceBean service = services.get(i);
			int count = service.getCount();
			total_price += service.getPrice() * count;
			services_count += count;
		}

		View view = this.findViewById(android.R.id.content).getRootView();

		TextView payment = (TextView) view.findViewById(R.id.order_complete_ac_text_payment);
		TextView delivery_info = (TextView) view.findViewById(R.id.order_complete_ac_text_delivery);
		TextView address = (TextView) view.findViewById(R.id.order_complete_ac_text_address);

		TextView products_count_txt = (TextView) view.findViewById(R.id.order_complete_ac_text_products_count);
		TextView services_count_txt = (TextView) view.findViewById(R.id.order_complete_ac_text_services_count);
		TextView delivery = (TextView) view.findViewById(R.id.order_complete_ac_text_delivery_price);
		TextView delivery_ruble = (TextView) view.findViewById(R.id.order_complete_ac_text_delivery_ruble);
		TextView total = (TextView) view.findViewById(R.id.order_complete_ac_text_total);
		TextView total_ruble = (TextView) view.findViewById(R.id.order_complete_ac_label_total_ruble);

		LinearLayout products_linear = (LinearLayout) view.findViewById(R.id.order_complete_ac_linear_products_count);
		LinearLayout services_linear = (LinearLayout) view.findViewById(R.id.order_complete_ac_linear_services_count);
		
		String delivery_info_txt = "";
		switch (info.getInt(DELIVERY_ID)){
		case STANDART:
				delivery_info_txt = STANDART_NAME + " " + info.getString(DATE_SHOW);
				mAddress = (AddressBean) info.getSerializable(ADDRESS_BEAN);
				address.setText(Formatters.createAddressString(mAddress));
				
			break;
		case SELF:
				address.setText(info.getString(ADDRESS));
				delivery_info_txt = SELF_NAME + " " + info.getString(DATE_SHOW);
			break;
		}

		int delivery_price = info.getInt(DELIVERY_PRICE, 0);

		switch (info.getInt(PAYMENT_ID)){
		case CASH:
			payment.setText(CASH_NAME);
			break;
		case BANKCARD:
			payment.setText(BANKCARD_NAME);
			break;
		}

		delivery_ruble.setText(TypefaceUtils.makeRouble(TypefaceUtils.NORMAL));
		total_ruble.setText(TypefaceUtils.makeRouble(TypefaceUtils.NORMAL));
		
		total_price += delivery_price;
		delivery_info.setText(delivery_info_txt);
		
		if (products_count > 0){
			products_count_txt.setText(String.valueOf(products_count));
		} else {
			products_linear.setVisibility(View.GONE);
		}
		if (services_count > 0){
			services_count_txt.setText(String.valueOf(services_count));
		} else{
			services_linear.setVisibility(View.GONE);
		}
		
		delivery.setText(String.valueOf(delivery_price));
		total.setText(String.valueOf((int) total_price));
	}

	@Override
	protected void onResume() {
		super.onResume();
		Intent intent = getIntent();
		info = intent.getBundleExtra(INFO);
		
		List<IBasketElement> objects = BasketManager.getAll();
		mAdapter.setObjects(objects);

		initCounts(objects);
	}

	private JSONArray getProductsArray() throws JSONException{
		JSONArray array = new JSONArray();
		ArrayList<ProductBean> list = null;

		list = (ArrayList<ProductBean>) BasketManager.getProducts();
				
		for(ProductBean bean:list){
			JSONObject object = new JSONObject();
			object.put("id", bean.getId());
			object.put("quantity", bean.getCount());
			array.put(object);
		}
		return array;
	}
	
	private JSONArray getServicesArray() throws JSONException{
		JSONArray array = new JSONArray();
		ArrayList<ServiceBean> list = null;

		list = (ArrayList<ServiceBean>) BasketManager.getServicesAll();
				
		for(ServiceBean bean:list){
			JSONObject object = new JSONObject();
			object.put("id", bean.getId());
			object.put("quantity", bean.getCount());
			if (bean.getProductId() != 0){
				object.put("product_id", bean.getProductId());	
			}			
			array.put(object);
		}
		return array;
	}
	
	@Override
	public void onClick(View v) {
		// проверяем соглашение
		if (mAgreement.isChecked()){
			try{
				if (sendOrderLoader != null) sendOrderLoader.cancel(true); 
				sendOrderLoader = new SendOrder();
				sendOrderLoader.execute();
			}catch (Exception e) {
				mProgressDialog.dismiss();
				Toast.makeText(this, "Ошибка при отправке.Попробуйте еще раз", Toast.LENGTH_SHORT).show();
			}
		}
		else{
			Toast.makeText(this, "Вам необходимо принять условия продажи и доставки", Toast.LENGTH_SHORT).show();
		}
	}
	
	class SendOrder extends AsyncTask<Void, Void, String> {

		@Override
		protected void onPreExecute() {
			mProgressDialog = ProgressDialogFragment.getInstance();
			mProgressDialog.show(getFragmentManager(), "progress");
		}

		@Override
		protected String doInBackground(Void... params) {

			String result = "";
			// TODO переделать треш с полями, привести к статичным переменным в обоих страницах заказа
			try {
				JSONObject object = new JSONObject();
				object.put("product", getProductsArray());
				object.put("service", getServicesArray());
				
				object.put(FIRSTNAME, info.getString(FIRSTNAME));
				object.put(LASTNAME, info.getString(LASTNAME));
				object.put("type_id", 1);
				object.put(PAYMENT_ID, info.getInt(PAYMENT_ID));
				object.put("payment_status_id", 1);
				object.put(MOBILE, info.getString(MOBILE));
				object.put(DELIVERY_ID, info.getInt(DELIVERY_ID));
				object.put(DELIVERY_DATE, info.getString(DELIVERY_DATE));
				object.put("geo_id", PreferencesManager.getCityid());
				 
				object.put(EMAIL, info.getString(EMAIL));
				String card = info.getString(SVYAZNOY_CARD);
				if(card != null && !card.equals("")) object.put(SVYAZNOY_CARD, card);
				
				if(info.getInt(DELIVERY_ID) == SELF){
					object.put(SHOP_ID, info.getInt(SHOP_ID));
					object.put(ADDRESS, info.getString(ADDRESS));
				}
				else if(info.getInt(DELIVERY_ID) == STANDART){
					object.put(AddressDialogFragment.ADDRESS_STREET, mAddress.getStreet());
					object.put(AddressDialogFragment.ADDRESS_HOUSE, mAddress.getHouse());
					object.put(AddressDialogFragment.ADDRESS_HOUSING, mAddress.getHousing());
					object.put(AddressDialogFragment.ADDRESS_FLOOR, mAddress.getFloor());
					object.put(AddressDialogFragment.ADDRESS_FLAT, mAddress.getFlat());
					if (mAddress.getMetro() != null){
						int metro_id = mAddress.getMetro().getId();
						if (metro_id != -1) object.put(METRO_ID, metro_id);
					}
				}
				
				// отправлялись в айфоне, не отправлялись в андроиде
				object.put("extra", "");
				object.put("payment_detail", "");
				object.put("delivery_price", info.getInt(DELIVERY_PRICE, 0));
				
				// TODO Непонятные поля
				// отправлялись в андроиде, не отправлялись в айфоне
//				object.put("first_name", PreferencesManager.getUserName());
//				object.put("last_name", PreferencesManager.getUserLastName());
//				object.put("add_address", String.valueOf(bean.isAddDeliveryAddress())); ???
				
				if(PreferencesManager.isAuthorized()){
					result = HTTPUtils.sendPostJSON(URLManager.getOrdersCreate(PreferencesManager.getToken()), object);
				}else{
					result = HTTPUtils.sendPostJSON(URLManager.getOrdersCreateAnonim(), object);
				}
			} catch (Exception e) {
			}

			return result;
		}
	    
		@Override
		protected void onPostExecute(String result) {
			if(!isCancelled()){
			// Если нет результата от сервера
				try{
					mProgressDialog.dismiss();
					JSONObject res = new JSONObject(result);
					if(res.has("error")){
	//					onError(); TODO
						Toast.makeText(OrderCompleteActivity.this, "Ошибка при отправке.Попробуйте еще раз", Toast.LENGTH_SHORT).show();
						return;
					}
					else{
						JSONObject jsonResult = res.getJSONObject("result");
						boolean confirmed = jsonResult.getBoolean("confirmed");
						if(!confirmed){
	//						onError();
							return;
						}
						String number = jsonResult.getString("number");
						int price = jsonResult.getInt("price");
						onPurchaseCompletedNormal(number,price);
						showOrderSuccessDialog(number);
//						Toast.makeText(OrderCompleteActivity.this, "Ваш заказ успешно отправлен", Toast.LENGTH_SHORT).show();
						
						Map<String, String> flurryParam = new HashMap<String, String>();
						
						if(PreferencesManager.isAuthorized()){
							flurryParam.put(Constants.FLURRY_EVENT_PARAM.Is_Authorized.toString(), Constants.FLURRY_IS_AUTHORIZED.True.toString());
						} else {
							flurryParam.put(Constants.FLURRY_EVENT_PARAM.Is_Authorized.toString(), Constants.FLURRY_IS_AUTHORIZED.False.toString());
						}
						
						if(info.getInt(PAYMENT_ID) == CASH) {
							flurryParam.put(Constants.FLURRY_EVENT_PARAM.Payment_Type.toString(), Constants.FLURRY_PAYMENT_TYPE.Cash.toString());
						} else {
							flurryParam.put(Constants.FLURRY_EVENT_PARAM.Payment_Type.toString(), Constants.FLURRY_PAYMENT_TYPE.PaymentCard.toString());
						}
						
						if(info.getInt(DELIVERY_ID) == STANDART){
							flurryParam.put(Constants.FLURRY_EVENT_PARAM.Delivery_Type.toString(), Constants.FLURRY_DELIVERY_TYPE.Dostavka.toString());
						} else {
							flurryParam.put(Constants.FLURRY_EVENT_PARAM.Delivery_Type.toString(), Constants.FLURRY_DELIVERY_TYPE.Samovivoz.toString());
						}
						
						if(TextUtils.isEmpty(PreferencesManager.getCityName())){
							flurryParam.put(Constants.FLURRY_EVENT_PARAM.City_Purchase.toString(), PreferencesManager.getCityName());
						} else {
							flurryParam.put(Constants.FLURRY_EVENT_PARAM.City_Purchase.toString(), "");
						}
						
						FlurryAgent.logEvent(Constants.FLURRY_EVENT.Good_Sales.toString(), flurryParam);
						if(mDiscountInOrder){
							FlurryAgent.logEvent(Constants.FLURRY_EVENT.Discounted_Goods_Sales.toString());
						}
					}
				}
				catch(Exception e){
					e.printStackTrace();
	//				onError();
					return;
				}
			}
		}
	}
	
	private void showOrderSuccessDialog (String number) {
		OrderCompleteSuccessDialogFragment dialog = OrderCompleteSuccessDialogFragment.getInstance();
		BasketManager.clear();
		dialog.setCancelable(false);
		dialog.setOrderNumber(number);
		dialog.setonClickListener(new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				switch (which) {
				case DialogInterface.BUTTON_POSITIVE:
					//BasketManager.clear();
					startActivity(new Intent(OrderCompleteActivity.this, MainActivity.class)); //TODO check this up
					break;
	
				default:
					break;
				}
			}
		});
		
		dialog.show(getFragmentManager(), "order_complete_success");//TODO
	}
	
	// GA посыл реквизитов заказа через транзакцию
	public void onPurchaseCompletedNormal(String number, int price) {
		Transaction myTrans = new Transaction.Builder(number, // (String) Transaction  Id, should be  unique. номер заказа
				(long) price*1000000) // (long) Order total (in micros) стоимость полная (price)
				.setAffiliation("") // (String) Affiliation
				.setShippingCostInMicros(0) // (long) Total shipping cost (in micros)
				.setCurrencyCode("RUB")                              // (String) Set currency code to Rub
				.build();
		// перечисление всех бинов
		List<ProductBean> listProduct = BasketManager.getProducts();
		List<ServiceBean> listService = BasketManager.getServicesAll();

		for (ProductBean bean : listProduct) {

			myTrans.addItem(new Item.Builder(String.valueOf(bean.getId()), // (String)
																			// Product
																			// SKU
																			// (id)
					bean.getName(), // (String) Product name (name)
					(long) bean.getPrice()*1000000, // (long) Product price (in micros)
											// (price)
					(long) bean.getCount()) // (long) Product quantity (count)
					.setProductCategory("") // (String) Product category
											// {category for products}
					.build());/**/
		}

		for (ServiceBean bean : listService) {

			myTrans.addItem(new Item.Builder(String.valueOf(bean.getId()), // (String)
																			// Product
																			// SKU
																			// (id)
					bean.getName(), // (String) Product name (name)
					(long) bean.getPrice()*1000000, // (long) Product price (in micros)
											// (price)
					(long) bean.getCount()) // (long) Product quantity (count)
					.setProductCategory("") // (String) Product category
											// {category for products}
					.build());/**/
		}

		Tracker myTracker = EasyTracker.getTracker(); // Get reference to
														// tracker.
		myTracker.sendTransaction(myTrans); // Send the transaction.
	}
}