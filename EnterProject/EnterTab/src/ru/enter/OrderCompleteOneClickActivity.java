package ru.enter;

import java.util.HashMap;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import ru.enter.DataManagement.BasketManager;
import ru.enter.base.BaseActivity;
import ru.enter.beans.ProductBean;
import ru.enter.dialogs.AgreementDialogFragment;
import ru.enter.dialogs.ProgressDialogFragment;
import ru.enter.dialogs.alert.OrderCompleteSuccessDialogFragment;
import ru.enter.utils.Constants;
import ru.enter.utils.Formatters;
import ru.enter.utils.HTTPUtils;
import ru.enter.utils.PreferencesManager;
import ru.enter.utils.TypefaceUtils;
import ru.enter.utils.URLManager;
import ru.ideast.shopitemfragment.tabs.ProductShopsActivity;
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
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.flurry.android.FlurryAgent;
import com.google.analytics.tracking.android.EasyTracker;
import com.google.analytics.tracking.android.Tracker;
import com.google.analytics.tracking.android.Transaction;
import com.google.analytics.tracking.android.Transaction.Item;
import com.webimageloader.ImageLoader;
import com.webimageloader.ext.ImageHelper;

public class OrderCompleteOneClickActivity extends BaseActivity implements OnClickListener {

	public static final String INFO = "info";
	public static final String DATE_SHOW = "date";
	public static final String DELIVERY_PRICE = "delivery_price";
	public static final String DELIVERY_DATE = "delivery_date";
	public static final String DELIVERY_ID = "delivery_type_id";

	public static final String ADDRESS = "address";
	public static final String PAYMENT_ID = "payment_id";

	public static final String FIRSTNAME = "user_first";
	public static final String LASTNAME = "user_last";
	public static final String EMAIL = "email";
	public static final String MOBILE = "mobile";
	public static final String SHOP_ID = "shop_id";
	public static final String SVYAZNOY_CARD = "svyaznoy_club_card_number";

	public static final String CASH_NAME = "наличными";
	public static final String BANKCARD_NAME = "банковской картой";
	public static final int CASH = 1;
	public static final int BANKCARD = 2;

	public static final String SELF_NAME = "самовывоз";
	public static final String STANDART_NAME = "стандарт";
	public static final int SELF = 3;
	public static final int STANDART = 1;

	private ProgressDialogFragment mProgressDialog;
	private CheckBox mAgreement;
	private SendOrder sendOrderLoader;
	private ProductBean mProductBean;
	private Bundle mInfo;
	private boolean mDiscountInOrder; 

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		EasyTracker.getInstance().setContext(this);

		setContentView(R.layout.order_complete_one_click_ac);
		mInfo = new Bundle();

		setTitleLeft(getResources().getString(R.string.actionbar_basket));

		// подчеркнутый текст у соглашения
		mAgreement = (CheckBox) findViewById(R.id.order_complete_one_click_ac_chekbox_agreement);
		mAgreement.setTypeface(TypefaceUtils.getBoldTypeface());

		TextView agreementText = (TextView) findViewById(R.id.order_complete_one_click_ac_text_agreement);
		agreementText.setPaintFlags(agreementText.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
		agreementText.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// вывод текста соглашения
				AgreementDialogFragment agreementDialog = AgreementDialogFragment.getInstance();
				agreementDialog.show(getFragmentManager(), "agreement_dialog");
			}
		});

		Button confirm_order = (Button) findViewById(R.id.order_complete_one_click_ac_button_confirm_order);
		confirm_order.setOnClickListener(this);

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

	@Override
	protected void onPause() {
		if (sendOrderLoader != null) sendOrderLoader.cancel(true);
		super.onPause();
	}

	private void initCounts(ProductBean oneClickProduct) {

		int total_count = 0;
		int total_price = 0;

		// при заказе в 1 клик количество товаров равно 1
		int count = 1;
		total_price += oneClickProduct.getPrice() * count;
		total_count += count;

		View view = this.findViewById(android.R.id.content).getRootView();

		TextView payment = (TextView) view.findViewById(R.id.order_complete_one_click_ac_text_payment);
		TextView delivery_info = (TextView) view.findViewById(R.id.order_complete_one_click_ac_text_delivery);
		TextView address = (TextView) view.findViewById(R.id.order_complete_one_click_ac_text_address);

		TextView all = (TextView) view.findViewById(R.id.order_complete_one_click_ac_text_all_count);
		TextView delivery = (TextView) view.findViewById(R.id.order_complete_one_click_ac_text_delivery_price);
		TextView delivery_ruble = (TextView) view.findViewById(R.id.order_complete_one_click_ac_text_delivery_ruble);
		TextView total = (TextView) view.findViewById(R.id.order_complete_one_click_ac_text_total);
		TextView total_ruble = (TextView) view.findViewById(R.id.order_complete_one_click_ac_label_total_ruble);

		String delivery_info_txt = "";
		switch (mInfo.getInt(DELIVERY_ID)){
		case STANDART:
			delivery_info_txt = STANDART_NAME + " " + mInfo.getString(DATE_SHOW);
			break;
		case SELF:
			delivery_info_txt = SELF_NAME + " " + mInfo.getString(DATE_SHOW);
			break;
		}

		int delivery_price = mInfo.getInt(DELIVERY_PRICE, 0);

		switch (mInfo.getInt(PAYMENT_ID)){
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
		address.setText(mInfo.getString(ADDRESS));

		all.setText(String.valueOf(total_count));
		delivery.setText(String.valueOf(delivery_price));
		total.setText(String.valueOf((int) total_price));

		if(oneClickProduct.getPrice_old() > oneClickProduct.getPrice()){
			mDiscountInOrder = true;
		}
	}

	private void initProductInfo(ProductBean oneClickProduct){

		ImageHelper imageLoader;

		ImageLoader loader = ApplicationTablet.getLoader(this);
		imageLoader = new ImageHelper(this, loader).setFadeIn(true)
				.setLoadingResource(R.drawable.tmp_1)
				.setErrorResource(R.drawable.tmp_1);// TODO

		ImageView image = (ImageView) findViewById(R.id.order_complete_one_click_list_item_image);
		TextView name = (TextView) findViewById(R.id.order_complete_one_click_list_item_text_name);
		TextView description = (TextView) findViewById(R.id.order_complete_one_click_list_item_text_description);
		TextView price = (TextView) findViewById(R.id.order_complete_one_click_list_item_text_price);
		TextView ruble = (TextView) findViewById(R.id.order_complete_one_click_list_item_text_ruble);
		RatingBar rating = (RatingBar) findViewById(R.id.order_complete_one_click_list_item_rating_bar);
		TextView count = (TextView) findViewById(R.id.order_complete_one_click_list_item_text_count);

		String fotoUrl = Formatters.createFotoString(mProductBean.getFoto(), 163);
		imageLoader.load(image, fotoUrl);
		description.setText(mProductBean.getName());
		ruble.setText(TypefaceUtils.makeRouble(TypefaceUtils.NORMAL));

		name.setText("Товар");
		rating.setRating(mProductBean.getRating());

		price.setText(String.valueOf((int) (mProductBean.getPrice())));
		count.setText(String.valueOf((int) (mProductBean.getCount())));
	}

	@Override
	protected void onResume() {
		super.onResume();
		Intent intent = getIntent();
		mInfo = intent.getBundleExtra(INFO);
		mProductBean = (ProductBean) getIntent().getSerializableExtra(ProductShopsActivity.PRODUCT_BEAN);
		initCounts(mProductBean);
		initProductInfo(mProductBean);
	}

	private JSONArray getProductsArray() throws JSONException{
		JSONArray array = new JSONArray();

		JSONObject object = new JSONObject();
		object.put("id", mProductBean.getId());
		object.put("quantity", 1); // TODO

		array.put(object);
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

				object.put(FIRSTNAME, mInfo.getString(FIRSTNAME));
				object.put(LASTNAME, mInfo.getString(LASTNAME));
				object.put("type_id", 1);
				object.put(PAYMENT_ID, mInfo.getInt(PAYMENT_ID));
				object.put("payment_status_id", 1);
				object.put(MOBILE, mInfo.getString(MOBILE));
				object.put(DELIVERY_ID, mInfo.getInt(DELIVERY_ID));
				object.put(DELIVERY_DATE, mInfo.getString(DELIVERY_DATE));
				object.put("geo_id", PreferencesManager.getCityid());
				object.put(ADDRESS, mInfo.getString(ADDRESS)); 
				object.put(EMAIL, mInfo.getString(EMAIL));
				String card = mInfo.getString(SVYAZNOY_CARD);
				if(card != null && !card.equals(""))
					object.put(SVYAZNOY_CARD, card);
				if(mInfo.getInt(DELIVERY_ID) == SELF) object.put(SHOP_ID, mInfo.getInt(SHOP_ID));

				// отправлялись в айфоне, не отправлялись в андроиде
				object.put("extra", "");
				object.put("payment_detail", "");
				object.put("delivery_price", mInfo.getInt(DELIVERY_PRICE, 0));

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
						Toast.makeText(OrderCompleteOneClickActivity.this, "Ошибка при отправке.Попробуйте еще раз", Toast.LENGTH_SHORT).show();
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
						onPurchaseCompletedOneClick(number, price);
						showOrderSuccessDialog(number);

						Map<String, String> flurryParam = new HashMap<String, String>();

						if(PreferencesManager.isAuthorized()){
							flurryParam.put(Constants.FLURRY_EVENT_PARAM.Is_Authorized.toString(), Constants.FLURRY_IS_AUTHORIZED.True.toString());
						} else {
							flurryParam.put(Constants.FLURRY_EVENT_PARAM.Is_Authorized.toString(), Constants.FLURRY_IS_AUTHORIZED.False.toString());
						}

						if(mInfo.getInt(PAYMENT_ID) == CASH) {
							flurryParam.put(Constants.FLURRY_EVENT_PARAM.Payment_Type.toString(), Constants.FLURRY_PAYMENT_TYPE.Cash.toString());
						} else {
							flurryParam.put(Constants.FLURRY_EVENT_PARAM.Payment_Type.toString(), Constants.FLURRY_PAYMENT_TYPE.PaymentCard.toString());
						}

						if(mInfo.getInt(DELIVERY_ID) == STANDART){
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
		dialog.setCancelable(false);
		dialog.setOrderNumber(number);
		dialog.setonClickListener(new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				switch (which) {
				case DialogInterface.BUTTON_POSITIVE:
					startActivity(new Intent(OrderCompleteOneClickActivity.this, MainActivity.class)); //TODO check this up
					break;

				default:
					break;
				}
			}
		});

		dialog.show(getFragmentManager(), "order_complete_success");//TODO
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
}