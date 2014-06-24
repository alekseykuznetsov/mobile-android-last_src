package ru.enter;



import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import ru.enter.DataManagement.PersonData;
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

public class OrderCompleteBuyNowActivity extends BaseActivity implements OnClickListener {

	private static final String PAYMENT_ID = "payment_id";
	private static final String SHOP_ID = "shop_id";

	private ProgressDialogFragment mProgressDialog;
	private CheckBox mAgreement;
	private SendOrder sendOrderLoader;
	private ProductBean mProductBean;
	private boolean mDiscountInOrder;
	
	private final SimpleDateFormat FORMAT_CHECKOUT = new SimpleDateFormat("yyyy-MM-dd");

	@Override
	protected void onCreate (Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		EasyTracker.getInstance().setContext(this);

		setContentView(R.layout.order_complete_buy_now_ac);

		setTitleLeft(getResources().getString(R.string.actionbar_basket));

		// подчеркнутый текст у соглашения
		mAgreement = (CheckBox) findViewById(R.id.order_complete_buy_now_ac_chekbox_agreement);
		mAgreement.setTypeface(TypefaceUtils.getBoldTypeface());

		TextView agreementText = (TextView) findViewById(R.id.order_complete_buy_now_ac_text_agreement);
		agreementText.setPaintFlags(agreementText.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
		agreementText.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick (View v) {
				// вывод текста соглашения
				AgreementDialogFragment agreementDialog = AgreementDialogFragment.getInstance();
				agreementDialog.show(getFragmentManager(), "agreement_dialog");
			}
		});

		Button confirm_order = (Button) findViewById(R.id.order_complete_buy_now_ac_button_confirm_order);
		confirm_order.setOnClickListener(this);

	}

	@Override
	protected void onPause () {
		if (sendOrderLoader != null)
			sendOrderLoader.cancel(true);
		super.onPause();
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

	private void initCounts (ProductBean buyNowProduct) {

		int total_count = 0;
		int total_price = 0;

		int count = 1;
		total_price += buyNowProduct.getPrice() * count;
		total_count += count;

		View view = this.findViewById(android.R.id.content).getRootView();

		TextView all = (TextView) view.findViewById(R.id.order_complete_buy_now_ac_text_all_count);
		TextView delivery_ruble = (TextView) view.findViewById(R.id.order_complete_buy_now_ac_text_delivery_ruble);
		TextView total = (TextView) view.findViewById(R.id.order_complete_buy_now_ac_text_total);
		TextView total_ruble = (TextView) view.findViewById(R.id.order_complete_buy_now_ac_label_total_ruble);

		delivery_ruble.setText(TypefaceUtils.makeRouble(TypefaceUtils.NORMAL));
		total_ruble.setText(TypefaceUtils.makeRouble(TypefaceUtils.NORMAL));

		all.setText(String.valueOf(total_count));
		total.setText(String.valueOf((int) total_price));
		
		if(buyNowProduct.getPrice_old() > buyNowProduct.getPrice()){
			mDiscountInOrder = true;
		}
	}

	private void initProductInfo () {

		ImageHelper imageLoader;

		ImageLoader loader = ApplicationTablet.getLoader(this);
		imageLoader = new ImageHelper(this, loader).setFadeIn(true).setLoadingResource(R.drawable.tmp_1).setErrorResource(R.drawable.tmp_1);// TODO

		ImageView image = (ImageView) findViewById(R.id.order_complete_buy_now_list_item_image);
		TextView name = (TextView) findViewById(R.id.order_complete_buy_now_list_item_text_name);
		TextView description = (TextView) findViewById(R.id.order_complete_buy_now_list_item_text_description);
		TextView price = (TextView) findViewById(R.id.order_complete_buy_now_list_item_text_price);
		TextView ruble = (TextView) findViewById(R.id.order_complete_buy_now_list_item_text_ruble);
		RatingBar rating = (RatingBar) findViewById(R.id.order_complete_buy_now_list_item_rating_bar);
		TextView count = (TextView) findViewById(R.id.order_complete_buy_now_list_item_text_count);

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
	protected void onResume () {
		super.onResume();
		mProductBean = (ProductBean) getIntent().getSerializableExtra(ProductShopsActivity.PRODUCT_BEAN);
		initCounts(mProductBean);
		initProductInfo();
	}

	private JSONArray getProductsArray () throws JSONException {
		JSONArray array = new JSONArray();

		JSONObject object = new JSONObject();
		object.put("id", mProductBean.getId());
		object.put("quantity", 1); // TODO

		array.put(object);
		return array;
	}

	@Override
	public void onClick (View v) {
		// проверяем соглашение
		if (mAgreement.isChecked()) {
			try {
				if (sendOrderLoader != null)
					sendOrderLoader.cancel(true);
				sendOrderLoader = new SendOrder();
				sendOrderLoader.execute();
			} catch (Exception e) {
				mProgressDialog.dismiss();
				Toast.makeText(this, "Ошибка при отправке.Попробуйте еще раз", Toast.LENGTH_SHORT).show();
			}
		} else {
			Toast.makeText(this, "Вам необходимо принять условия продажи и доставки", Toast.LENGTH_SHORT).show();
		}
	}

	class SendOrder extends AsyncTask<Void, Void, String> {

		@Override
		protected void onPreExecute () {
			mProgressDialog = ProgressDialogFragment.getInstance();
			mProgressDialog.show(getFragmentManager(), "progress");
		}

		@Override
		protected String doInBackground (Void... params) {

			String result = "";
			// TODO переделать треш с полями, привести к статичным переменным в
			// обоих страницах заказа
			try {
				JSONObject object = new JSONObject();
				object.put("type_id", 1);
				if (PreferencesManager.isAuthorized()){
					object.put("user_id", PreferencesManager.getUserId());
				}
				object.put(PAYMENT_ID, 1);
				object.put("payment_status_id", 1);
				object.put("geo_id", PreferencesManager.getCityid());
				object.put(SHOP_ID, PreferencesManager.getUserCurrentShopId());
				object.put("delivery_type_id", 4);
				object.put("delivery_date", FORMAT_CHECKOUT.format(new Date()));
				object.put("product", getProductsArray());

				if (PreferencesManager.isAuthorized()) {
					result = HTTPUtils.sendPostJSON(URLManager.getOrdersCreate(PreferencesManager.getToken()), object);
				} else {
					result = HTTPUtils.sendPostJSON(URLManager.getOrdersCreateAnonim(), object);
				}
			} catch (Exception e) {
			}

			return result;
		}

		@Override
		protected void onPostExecute (String result) {
			if (!isCancelled()) {
				// Если нет результата от сервера
				try {
					mProgressDialog.dismiss();
					JSONObject res = new JSONObject(result);
					if (res.has("error")) {
						// onError(); TODO
						Toast.makeText(OrderCompleteBuyNowActivity.this, "Ошибка при отправке.Попробуйте еще раз", Toast.LENGTH_SHORT)
								.show();
						return;
					} else {
						JSONObject jsonResult = res.getJSONObject("result");
						boolean confirmed = jsonResult.getBoolean("confirmed");
						if (!confirmed) {
							// onError();
							return;
						}
						String number = jsonResult.getString("number");
						int price = jsonResult.getInt("price");
						
						onPurchaseCompletedBuyNow(number, price);

						showOrderSuccessDialog(number);
						
						Map<String, String> flurryParam = new HashMap<String, String>();
						
						if(PreferencesManager.isAuthorized()){
							flurryParam.put(Constants.FLURRY_EVENT_PARAM.Is_Authorized.toString(), Constants.FLURRY_IS_AUTHORIZED.True.toString());
						} else {
							flurryParam.put(Constants.FLURRY_EVENT_PARAM.Is_Authorized.toString(), Constants.FLURRY_IS_AUTHORIZED.False.toString());
						}
						
//						if(info.getInt(PAYMENT_ID) == CASH) {
							flurryParam.put(Constants.FLURRY_EVENT_PARAM.Payment_Type.toString(), Constants.FLURRY_PAYMENT_TYPE.Cash.toString());
//						} else {
//							flurryParam.put(Constants.FLURRY_EVENT_PARAM.Payment_Type.toString(), Constants.FLURRY_PAYMENT_TYPE.PaymentCard.toString());
//						}
						
//						if(info.getInt(DELIVERY_ID) == STANDART){
//							flurryParam.put(Constants.FLURRY_EVENT_PARAM.Delivery_Type.toString(), Constants.FLURRY_DELIVERY_TYPE.Dostavka.toString());
//						} else {
							flurryParam.put(Constants.FLURRY_EVENT_PARAM.Delivery_Type.toString(), Constants.FLURRY_DELIVERY_TYPE.Samovivoz.toString());
//						}
						
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
				} catch (Exception e) {
					e.printStackTrace();
					// onError();
					return;
				}
			}
		}
	}

	private void showOrderSuccessDialog (String number) {
		OrderCompleteSuccessDialogFragment dialog = OrderCompleteSuccessDialogFragment.getInstance();
		dialog.setCancelable(false);
		dialog.setOrderNumberBuyNow(number);
		dialog.setonClickListener(new DialogInterface.OnClickListener() {

			@Override
			public void onClick (DialogInterface dialog, int which) {
				switch (which) {
				case DialogInterface.BUTTON_POSITIVE:
					startActivity(new Intent(OrderCompleteBuyNowActivity.this, MainActivity.class));
					break;

				default:
					break;
				}
			}
		});

		dialog.show(getFragmentManager(), "order_complete_success");// TODO
	}
	
	// GA посыл реквизитов заказа через транзакцию
			public void onPurchaseCompletedBuyNow(String number, int price) {
						
				  Transaction myTrans = new Transaction.Builder(
				      number,                                           // (String) Transaction Id, should be unique. номер заказа
				      (long) price*1000000)                              // (long) Order total (in micros) стоимость полная (price)
				      .setAffiliation("")                       // (String) Affiliation
				      .setShippingCostInMicros(0)                           // (long) Total shipping cost (in micros)
				      .setCurrencyCode("RUB")                              // (String) Set currency code to Rub
				      .build();
		// перечисление всех бинов	
				  myTrans.addItem(new Item.Builder(
				      String.valueOf(mProductBean.getId()),                                              // (String) Product SKU (id)
				      mProductBean.getName(),                                  // (String) Product name (name)
				      (long) mProductBean.getPrice()*1000000,                              // (long) Product price (in micros) (price)
				      (long) 1)                                             // (long) Product quantity (count)
				      .setProductCategory("")                // (String) Product category {category for products}
				      .build());/**/

				    Tracker myTracker = EasyTracker.getTracker(); // Get reference to tracker.
				    myTracker.sendTransaction(myTrans); // Send the transaction.
				}
}
