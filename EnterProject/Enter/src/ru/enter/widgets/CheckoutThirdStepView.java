package ru.enter.widgets;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.flurry.android.FlurryAgent;
import com.google.analytics.tracking.android.*;
import com.google.analytics.tracking.android.Transaction.Item;

import ru.enter.BasketActivity;
import ru.enter.CheckoutActivity;
import ru.enter.MainMenuActivity;
import ru.enter.R;
import ru.enter.TermsActivity;
import ru.enter.DataManagement.BasketData;
import ru.enter.DataManagement.BasketManager;
import ru.enter.DataManagement.BasketManager.CountPrice;
import ru.enter.DataManagement.PersonData;
import ru.enter.adapters.OrderAdapter;
import ru.enter.beans.AddressBean;
import ru.enter.beans.CheckoutBean;
import ru.enter.beans.CheckoutBean.CheckoutFirstStepDelivery;
import ru.enter.beans.CheckoutBean.CheckoutPaymentMethod;
import ru.enter.beans.MetroBean;
import ru.enter.beans.ProductBean;
import ru.enter.beans.ServiceBean;
import ru.enter.interfaces.IBasketElement;
import ru.enter.parsers.CheckoutThirdStepViewParser;
import ru.enter.utils.Constants;
import ru.enter.utils.Converter;
import ru.enter.utils.Formatters;
import ru.enter.utils.ICheckoutInterface;
import ru.enter.utils.PreferencesManager;
import ru.enter.utils.RequestManagerThread;
import ru.enter.utils.URLManager;
import ru.enter.utils.Utils;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;
import android.text.Html;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
public class CheckoutThirdStepView extends FrameLayout implements ICheckoutInterface{
	private static final int RESULT_OK = 1;
	private TextView mPaymentMethodTV,mDeliveryTV,mAddressTV;
	private TextView mPriceAllTV,mPriceProductTV,mCountProductTV;
	private View mRootView;
	private Button mApply;
	private int mCurrentLaunchType = 1;
	private ProgressDialog mProgressDialog;
	private CheckBox mNotifyWithSMS;
	private int mGEO;
	private TextView mDeliveryPrice;
	private View mDeliveryLin;
	private CheckBox mAcceptCheck;
	private TextView mAcceptText;
	private Context mContext;
	private boolean isOneClick;
	private TextView mProductTV;
	private TextView mServicesTV;
	private TextView mPriceServicesTV;
	private TextView mCountServicesTV;
	private FrameLayout mPayOffCoupon;
	private boolean mDiscountInOrder;

	public CheckoutThirdStepView(final Context context) {
		this(context, false);
	}

	public CheckoutThirdStepView(final Context context, boolean isOneClick) {
		super(context);
		this.isOneClick = isOneClick;
		mContext = context;
		mGEO = PreferencesManager.getCityid();

		mRootView = LayoutInflater.from(context).inflate(R.layout.checkout_third_step, null);

		TextView title = (TextView)mRootView.findViewById(R.id.checkout_third_step_title_tv);
		title.setTypeface(Utils.getTypeFace(context));

		mPaymentMethodTV = (TextView)mRootView.findViewById(R.id.checkout_third_step_payment);
		mDeliveryTV = (TextView)mRootView.findViewById(R.id.checkout_third_step_delivery);
		mAddressTV = (TextView)mRootView.findViewById(R.id.checkout_third_step_address);

		ListView listView = (ListView)mRootView.findViewById(android.R.id.list);

		View footerView = LayoutInflater.from(context).inflate(R.layout.basket_activity_footer_row, null);
		footerView.findViewById(R.id.basket_activity_clear_b).setVisibility(View.GONE);
		footerView.findViewById(R.id.basket_activity_checkout_b).setVisibility(View.GONE);

//		mPayOffCoupon = (FrameLayout) footerView.findViewById(R.id.l_pay_off_coupon);
//		if(context instanceof Activity){
//			View child = ((Activity)context).getLayoutInflater().inflate(R.layout.pay_off_coupon, null);
//			mPayOffCoupon.addView(child);
//			mPayOffCoupon.setVisibility(View.VISIBLE);
//
//			String[] coupVal = {"купон 1", "купон 2", "купон 3", "купон 4", "купон 5"};
//			Spinner coupons = (Spinner) child.findViewById(R.id.checkout_available_coupons);
//			ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<String>(context, android.R.layout.simple_spinner_item, coupVal);
//			coupons.setAdapter(spinnerArrayAdapter);
//
//		}

		mAcceptCheck = (CheckBox)footerView.findViewById(R.id.basket_activity_footer_checkbox_accept);
		mAcceptCheck.setVisibility(View.VISIBLE);

		mAcceptText = (TextView) footerView.findViewById(R.id.basket_activity_footer_text_accept);
		mAcceptText.setVisibility(View.VISIBLE);
		mAcceptText.setText(Html.fromHtml("<u> Я согласен(-на) с условиями продажи  и доставки</u>"));
		mAcceptText.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				context.startActivity(new Intent(context, TermsActivity.class));
			}
		});


		mPriceAllTV = (TextView) footerView.findViewById(R.id.basket_text_price_all);
		mPriceAllTV.setTypeface(Utils.getRoubleTypeFace(context),Typeface.BOLD);

		mProductTV = (TextView) footerView.findViewById(R.id.basket_text_products);
		mPriceProductTV = (TextView) footerView.findViewById(R.id.basket_text_price_products);
		mPriceProductTV.setTypeface(Utils.getRoubleTypeFace(context),Typeface.BOLD);
		mCountProductTV = (TextView) footerView.findViewById(R.id.basket_text_num_products);

		mServicesTV = (TextView) footerView.findViewById(R.id.basket_text_services);
		mPriceServicesTV = (TextView) footerView.findViewById(R.id.basket_text_price_services);
		mPriceServicesTV.setTypeface(Utils.getRoubleTypeFace(context),Typeface.BOLD);
		mCountServicesTV = (TextView) footerView.findViewById(R.id.basket_text_num_services);

		mDeliveryLin = footerView.findViewById(R.id.basket_linear_delivery);

		mDeliveryPrice = (TextView) footerView.findViewById(R.id.basket_text_price_delivery);
		mDeliveryPrice.setTypeface(Utils.getRoubleTypeFace(context),Typeface.BOLD);

		mApply = (Button)footerView.findViewById(R.id.basket_activity_footer_btn_ok);
		mApply.setVisibility(View.VISIBLE);
		mApply.setText("Подтвердить");
		mApply.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				sendOrder();
			}
		});
		mApply.setEnabled(false);

		mAcceptCheck.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				mApply.setEnabled(isChecked);
			}
		});

		mNotifyWithSMS = (CheckBox)footerView.findViewById(R.id.basket_activity_notify_with_sms_checkbox);

		initFooterData();
		listView.addFooterView(footerView);
		OrderAdapter adapter = new OrderAdapter(context);
		mCurrentLaunchType = ((Activity)getContext()).getIntent().getExtras().getInt(CheckoutActivity.LAUNCH_TYPE);
		if(mCurrentLaunchType==CheckoutActivity.LAUNCH_TYPE_NORMAL){
			adapter.setObjects(BasketActivity.getSortedBasket());//TODO
		}else if(mCurrentLaunchType==CheckoutActivity.LAUNCH_TYPE_BUY_ONE_CLICK){
			ProductBean bean = BasketManager.getOneClickProduct();
			ArrayList<IBasketElement> beans = new ArrayList<IBasketElement>();
			beans.add(bean);
			adapter.setObjects(beans);
		}


		listView.setAdapter(adapter);

		addView(mRootView);
	}
	private int getNotifySMSCheckBoxValue(){
		if(mNotifyWithSMS.isChecked()){
			return 1;
		}else{
			return 0;
		}
	}
	private Handler handler = new Handler(){
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case RESULT_OK:
				String respone = (String)msg.obj;
				CheckoutThirdStepViewParser parser = new CheckoutThirdStepViewParser(mContext);
				try {
					String result = parser.parseString(respone);
					if(result!=null){
						BasketManager.clear();
						PersonData.getInstance().setOrdersChanged(true);
						showLongText(result, getDeliveryID() == 1);
					}
				} catch (Exception e) {
					Toast.makeText(getContext(), "Непредвиденная ошибка.", Toast.LENGTH_SHORT).show();
				}
				mProgressDialog.dismiss();
				break;

			default:
				break;
			}
		}
	};
	private AnonimLoader loader;

	private void finishOrder(){
		Intent intent = new Intent();
		intent.setClass(mContext,MainMenuActivity.class);
		intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		intent.putExtra("fromOrder", true);
		mContext.startActivity(intent);
	}

	private void showLongText(String result, boolean delivery){
		//<b>Заказ ХА-123456 отправлен для подбора на складе Enter. <b>Ожидайте СМС или звонок от оператора о статусе доставки вашего заказа
		String msg = "Заказ "  + result + " отправлен для подбора на складе Enter.\nОжидайте СМС или звонок от оператора о статусе доставки Вашего заказа.";

		AlertDialog.Builder dlg = new AlertDialog.Builder(getContext());
		dlg.setTitle("Ваш Заказ успешно принят")
		.setMessage(msg)
		.setPositiveButton("ОК", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int whichButton) {
				finishOrder();
			}
		}).create().show();
	}
	private void showProgressDialog(){
		mProgressDialog = new ProgressDialog(getContext());
		mProgressDialog.setMessage("Подождите...");
		mProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
		mProgressDialog.setOnKeyListener(new DialogInterface.OnKeyListener() {

			@Override
			public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
				if(keyCode==KeyEvent.KEYCODE_BACK){
					if(loader != null)
						loader.cancel(true);
					return true;
				}
				return false;
			}

		});

		mProgressDialog.show();
	}

	private JSONArray getOneClick () {
		mDiscountInOrder = false;
		JSONArray array = new JSONArray();
		try{
			ProductBean product = BasketManager.getOneClickProduct();

			JSONObject object = new JSONObject();
			object.put("id", product.getId());
			object.put("price", product.getPrice());
			object.put("quantity", product.getCount());
			array.put(object);
			
			if(product.getPrice_old() > product.getPrice()){
				mDiscountInOrder = true;
			}
		}catch (Exception e) {}

		return array;
	}

	private JSONArray getProductArray() throws JSONException{
		JSONArray array = new JSONArray();
		List<ProductBean> list = BasketManager.getProducts();

		for(ProductBean bean:list){
			JSONObject object = new JSONObject();
			object.put("id", bean.getId());
			object.put("price", bean.getPrice());
			object.put("quantity", bean.getCount());
			array.put(object);
			
			if(bean.getPrice_old() > bean.getPrice()){
				mDiscountInOrder = true;
			}
		}
		return array;
	}

	private JSONArray getServicesArray() throws JSONException{

		JSONArray array = new JSONArray();
		List<ServiceBean> list = BasketManager.getServicesAll();

		for(ServiceBean bean:list){
			JSONObject object = new JSONObject();
			object.put("id", bean.getId());
			object.put("quantity", bean.getCount());
			if (bean.getProductId() != 0) {
				object.put("product_id", bean.getProductId());
			}
			array.put(object);
		}
		return array;
	}

	private int getPaymentId(){
		CheckoutBean bean = BasketData.getInstance().getCheckoutBean();
		CheckoutPaymentMethod method = bean.getCheckoutPaymentMethod();
		switch (method) {
		case cash:
			return 1;
		case bank_card:
			return 2;
		default:
			return 0;
		}
	}
	private int getDeliveryID(){
		CheckoutBean bean = BasketData.getInstance().getCheckoutBean();//TODO
		CheckoutFirstStepDelivery delivery = bean.getCheckoutFirstStepDelivery();
		switch (delivery) {
		case standart:
			return 1;
		case express:
			return 2;
		case pickup:
			return 3;
		case shop:
			return 4;
		default:
			return 0;
		}
	}

	private void sendOrder(){
		showProgressDialog();
		
		Map<String, String> flurryParam = new HashMap<String, String>();
		
		try{
			CheckoutBean bean = BasketData.getInstance().getCheckoutBean();//TODO
			JSONObject object = new JSONObject();
			object.put("type_id", 1);
			object.put("last_name", bean.getLastName());
			object.put("first_name", bean.getName());
			object.put("mobile", bean.getPhoneNumber());
			object.put("payment_status_id", 1);
			object.put("payment_id", getPaymentId());
			object.put("delivery_type_id", getDeliveryID());
			object.put("delivery_interval_id", bean.getDeliveryTimeId());
			object.put("delivery_date", bean.getDeliveryDate());
			object.put("geo_id", mGEO);
			
			if(PreferencesManager.isAuthorized()){
				flurryParam.put(Constants.FLURRY_EVENT_PARAM.Is_Authorized.toString(), Constants.FLURRY_IS_AUTHORIZED.True.toString());
			} else {
				flurryParam.put(Constants.FLURRY_EVENT_PARAM.Is_Authorized.toString(), Constants.FLURRY_IS_AUTHORIZED.False.toString());
			}
			
			if(getPaymentId() == 1) {
				flurryParam.put(Constants.FLURRY_EVENT_PARAM.Payment_Type.toString(), Constants.FLURRY_PAYMENT_TYPE.Cash.toString());
			} else {
				flurryParam.put(Constants.FLURRY_EVENT_PARAM.Payment_Type.toString(), Constants.FLURRY_PAYMENT_TYPE.PaymentCard.toString());
			}
			
			if(getDeliveryID() == 3 || getDeliveryID() == 4){
				flurryParam.put(Constants.FLURRY_EVENT_PARAM.Delivery_Type.toString(), Constants.FLURRY_DELIVERY_TYPE.Samovivoz.toString());
			} else {
				flurryParam.put(Constants.FLURRY_EVENT_PARAM.Delivery_Type.toString(), Constants.FLURRY_DELIVERY_TYPE.Dostavka.toString());
			}
			
			if(TextUtils.isEmpty(PreferencesManager.getCityName())){
				flurryParam.put(Constants.FLURRY_EVENT_PARAM.City_Purchase.toString(), PreferencesManager.getCityName());
			} else {
				flurryParam.put(Constants.FLURRY_EVENT_PARAM.City_Purchase.toString(), "");
			}
			
			switch (bean.getCheckoutFirstStepDelivery()) {
			case express:
			case standart:
				AddressBean userAddress = bean.getUserAddress();

				MetroBean metro = userAddress.getMetro();
				if (metro != null && metro.getId() != -1) {
					object.put("subway_id", userAddress.getMetro().getId());
				}

				object.put("address_street", userAddress.getStreet());
				object.put("address_building", userAddress.getHouse());
				object.put("address_number", userAddress.getHousing());
				object.put("address_floor", userAddress.getFloor());
				object.put("address_apartment", userAddress.getFlat());
				break;
			case shop:
			case pickup:
				object.put("address", bean.getShopAddress());
				break;
			default:
				break;
			}

			if (mCurrentLaunchType == CheckoutActivity.LAUNCH_TYPE_BUY_ONE_CLICK) {
				object.put("product", getOneClick());
			} else {
				object.put("product", getProductArray());
				object.put("service", getServicesArray());
			}

			object.put("email", bean.getEmail());
			object.put("user_first", bean.getName());
			object.put("user_last", bean.getLastName());
			object.put("add_address", String.valueOf(bean.isAddDeliveryAddress()));
			String svyaznoyCard = bean.getSvyaznoyCard();
			if(svyaznoyCard != null && !svyaznoyCard.equals("")) 
				object.put("svyaznoy_club_card_number", svyaznoyCard);
			if(getDeliveryID() == 3) object.put("shop_id", bean.getShop_id());

			if(PreferencesManager.getUserId() == 0){
				loader = new AnonimLoader(mContext, object, mProgressDialog);
				loader.execute();
			} else {
				RequestManagerThread requestManagerThread = new RequestManagerThread(handler, 
						object,URLManager.getOrdersCreate(PreferencesManager.getToken()), RESULT_OK);
				requestManagerThread.start();
				
				// If request has been delivered successfuly here won't be any exception 
				FlurryAgent.logEvent(Constants.FLURRY_EVENT.Good_Sales.toString(), flurryParam);
				if(mDiscountInOrder){
					FlurryAgent.logEvent(Constants.FLURRY_EVENT.Discounted_Goods_Sales.toString());
				}
			}
		}catch (Exception e) {
			mProgressDialog.dismiss();
			Toast.makeText(getContext(), "Ошибка при отправке. Попробуйте еще раз", Toast.LENGTH_SHORT).show();
		}
	}

	private class AnonimLoader extends AsyncTask<Void, Void, Void>{

		private JSONObject object;
		private String result;
		private ProgressDialog progressDialog;
		private Context context;

		public AnonimLoader(Context context, JSONObject toSend, ProgressDialog progressDialog){
			this.object = toSend;
			this.progressDialog = progressDialog;
			this.context = context;
		}

		@Override
		protected Void doInBackground(Void... params) {
			result = Utils.sendPostData(object.toString(), URLManager.getOrdersCreateAnonim());
			return null;
		}

		@Override
		protected void onPostExecute(Void state) {
			progressDialog.dismiss();
			if(isCancelled())
				return;
			try{
				JSONObject res = new JSONObject(result);
				if(res.has("error")){
					onError();
					return;
				} else{
					JSONObject jsonResult = res.getJSONObject("result");
					boolean confirmed = jsonResult.getBoolean("confirmed");
					if(!confirmed){
						onError();
						return;
					}

					String number = jsonResult.getString("number");
					int price = jsonResult.getInt("price");
					if(isOneClick){
						onPurchaseCompletedOneClick(number, price);
					}else{
						onPurchaseCompletedNormal(number, price);
						BasketManager.clear();
					}
					Builder builder = new AlertDialog.Builder(context);
					builder.setTitle("Ваш заказ успешно принят")
					//<b>Заказ ХА-123456 отправлен для подбора на складе Enter. <b>Ожидайте СМС или звонок от оператора о статусе доставки вашего заказа
					.setMessage("Заказ " + number + " отправлен для подбора на складе Enter.\n Ожидайте СМС или звонок от оператора о статусе доставки Вашего заказа.")
					.setCancelable(false)
					.setPositiveButton("ОК", new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int id) {
							//BasketManager.clear();
							Intent intent = new Intent();
							intent.setClass(context.getApplicationContext(), MainMenuActivity.class);
							intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
							intent.putExtra("fromOrder", true);
							context.startActivity(intent);
						}
					})
					.create().show();
				}
			}
			catch(Exception e){
				e.printStackTrace();
				onError();
				return;
			}
		}

		private void onError(){
			Toast.makeText(context, "При подтверждении заказа произошла ошибка\nПовторите попытку через некоторое время", Toast.LENGTH_SHORT).show();
		}
	}

	public void initHeader(){

		CheckoutBean mCheckoutBean = BasketData.getInstance().getCheckoutBean();
		switch (mCheckoutBean.getCheckoutPaymentMethod()) {
		case cash:	
			mPaymentMethodTV.setText("Наличными");	
			break;
		case bank_card:
			mPaymentMethodTV.setText("Банковской картой");
			break;
		default:
			break;
		}

		String timeDelivery = Converter.fromLineToDot(mCheckoutBean.getDeliveryDate()).concat(" ").concat(mCheckoutBean.getInterval());
		switch (mCheckoutBean.getCheckoutFirstStepDelivery()) {
		case standart:
			mDeliveryTV.setText("Курьером ".concat(timeDelivery));
			String address = Formatters.createAddressString(mCheckoutBean.getUserAddress());
			mAddressTV.setText(address);
			break;
		case pickup:
			mDeliveryTV.setText("Самостоятельно заберу в магазине ".concat(timeDelivery));
			mAddressTV.setText(mCheckoutBean.getShopAddress());
			break;
		default:
			break;
		}
	}
	@Override
	public void setOnClickListener(OnClickListener l) {
		mApply.setOnClickListener(l);
	}
	public void initFooterData(){
		if (isOneClick) {
			initOneclickFooter();
		} else {
			initNormalFooter();
		}
	}

	private void initOneclickFooter() {
		ProductBean bean = BasketManager.getOneClickProduct();
		int price = (int) bean.getPrice();

		mDeliveryLin.setVisibility(View.GONE);

		mServicesTV.setVisibility(View.GONE);
		mPriceServicesTV.setVisibility(View.GONE);
		mCountServicesTV.setVisibility(View.GONE);

		mProductTV.setVisibility(View.VISIBLE);
		mPriceProductTV.setVisibility(View.VISIBLE);
		mCountProductTV.setVisibility(View.VISIBLE);

		mPriceAllTV.setText(formatNumber(price));
		mPriceProductTV.setText(formatNumber(price));
		mCountProductTV.setText(bean.getCount() + " шт.");

	}

	private void initNormalFooter() {

		CountPrice object = BasketManager.getCountPriceObject();

		if (object.productsCount < 1) {
			mProductTV.setVisibility(View.GONE);
			mPriceProductTV.setVisibility(View.GONE);
			mCountProductTV.setVisibility(View.GONE);
		} else {
			mProductTV.setVisibility(View.VISIBLE);
			mPriceProductTV.setVisibility(View.VISIBLE);
			mCountProductTV.setVisibility(View.VISIBLE);
		}

		if (object.servicesCount < 1) {
			mServicesTV.setVisibility(View.GONE);
			mPriceServicesTV.setVisibility(View.GONE);
			mCountServicesTV.setVisibility(View.GONE);
		} else {
			mServicesTV.setVisibility(View.VISIBLE);
			mPriceServicesTV.setVisibility(View.VISIBLE);
			mCountServicesTV.setVisibility(View.VISIBLE);
		}

		//доставка
		int deliveryPrice =  (int) BasketData.getInstance().getCheckoutBean().getDeliveryPrice();

		if (deliveryPrice == 0) {
			mDeliveryLin.setVisibility(View.GONE);
		} else {
			mDeliveryLin.setVisibility(View.VISIBLE);
			mDeliveryPrice.setText(formatNumber(deliveryPrice));
		}

		int priceAll = object.allPrice + deliveryPrice;

		mPriceAllTV.setText(formatNumber(priceAll));
		mPriceProductTV.setText(formatNumber(object.productsPrice));
		mCountProductTV.setText(object.productsCount + " шт.");
		mPriceServicesTV.setText(formatNumber(object.servicesPrice));
		mCountServicesTV.setText(object.servicesCount+ " шт.");

	}

	private String formatNumber(int value){
		return Formatters.createPriceStringWithRouble(value);
	}

	@Override
	public boolean isNext() {
		return false;
	}
	@Override
	public boolean isSvyaznoyCardValid() {
		return true;
	}

	// GA посыл реквизитов заказа через транзакцию
	public void onPurchaseCompletedOneClick(String number, int price) {
		ProductBean product = BasketManager.getOneClickProduct();		

		Transaction myTrans = new Transaction.Builder(
				number,                                           // (String) Transaction Id, should be unique. номер заказа
				(long) price*1000000)                              // (long) Order total (in micros) стоимость полная (price)
		.setAffiliation("")                       // (String) Affiliation
		.setShippingCostInMicros(0)                           // (long) Total shipping cost (in micros)
		.setCurrencyCode("RUB")                              // (String) Set currency code to Rub
		.build();
		// перечисление всех бинов	
		myTrans.addItem(new Item.Builder(
				String.valueOf(product.getId()),                                              // (String) Product SKU (id)
				product.getName(),                                  // (String) Product name (name)
				(long) product.getPrice()*1000000,                              // (long) Product price (in micros) (price)
				(long) 1)                                             // (long) Product quantity (count)
		.setProductCategory("")                // (String) Product category {category for products}
		.build());/**/

		Tracker myTracker = EasyTracker.getTracker(); // Get reference to tracker.
		myTracker.sendTransaction(myTrans); // Send the transaction.
	}

	// GA посыл реквизитов заказа через транзакцию
	public void onPurchaseCompletedNormal(String number, int price) {
		Transaction myTrans = new Transaction.Builder(
				number,                                           // (String) Transaction Id, should be unique. номер заказа
				(long) price*1000000)                              // (long) Order total (in micros) стоимость полная (price)
		.setAffiliation("")                       // (String) Affiliation
		.setShippingCostInMicros(0)                           // (long) Total shipping cost (in micros)
		.setCurrencyCode("RUB")                              // (String) Set currency code to Rub
		.build();
		// перечисление всех бинов	
		List<ProductBean> listProduct = BasketManager.getProducts();
		List<ServiceBean> listService = BasketManager.getServicesAll();

		for(ProductBean bean:listProduct){

			myTrans.addItem(new Item.Builder(
					String.valueOf(bean.getId()),                                              // (String) Product SKU (id)
					bean.getName(),                                  // (String) Product name (name)
					(long) bean.getPrice()*1000000,                              // (long) Product price (in micros) (price)
					(long) bean.getCount())                                             // (long) Product quantity (count)
			.setProductCategory("")                // (String) Product category {category for products}
			.build());/**/
		}

		for(ServiceBean bean:listService){

			myTrans.addItem(new Item.Builder(
					String.valueOf(bean.getId()),                                              // (String) Product SKU (id)
					bean.getName(),                                  // (String) Product name (name)
					(long) bean.getPrice()*1000000,                              // (long) Product price (in micros) (price)
					(long) bean.getCount())                                             // (long) Product quantity (count)
			.setProductCategory("")                // (String) Product category {category for products}
			.build());/**/
		}

		Tracker myTracker = EasyTracker.getTracker(); // Get reference to tracker.
		myTracker.sendTransaction(myTrans); // Send the transaction.
	}
}
