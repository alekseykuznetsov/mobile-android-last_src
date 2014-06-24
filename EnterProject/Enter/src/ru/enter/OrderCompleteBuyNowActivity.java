package ru.enter;

import java.util.Date;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.flurry.android.FlurryAgent;
import com.google.analytics.tracking.android.EasyTracker;
import com.google.analytics.tracking.android.Tracker;
import com.google.analytics.tracking.android.Transaction;
import com.google.analytics.tracking.android.Transaction.Item;

import ru.enter.DataManagement.BasketManager;
import ru.enter.DataManagement.ProductCacheManager;
import ru.enter.ImageManager.ImageDownloader;
import ru.enter.beans.ProductBean;
import ru.enter.utils.Formatters;
import ru.enter.utils.HTTPUtils;
import ru.enter.utils.PreferencesManager;
import ru.enter.utils.URLManager;
import ru.enter.utils.Utils;
import ru.enter.widgets.CheckoutFirstStepView;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;


public class OrderCompleteBuyNowActivity extends Activity implements OnClickListener {

	private static final String PAYMENT_ID = "payment_id";
	private static final String SHOP_ID = "shop_id";
	public static final String PRODUCT_BEAN = "product_bean";

//	private ProgressDialogFragment mProgressDialog;
	private CheckBox mAgreement;
	private SendOrder sendOrderLoader;
	private ProductBean mProductBean;
	private ProgressDialog mProgressDialog;

	@Override
	protected void onCreate (Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.order_complete_buy_now_ac);

		TextView title = (TextView) findViewById(R.id.order_complete_buy_now_ac_title);
		title.setTypeface(Utils.getTypeFace(this));
		
		TextView acceptText = (TextView) findViewById(R.id.order_complete_buy_now_ac_text_accept);
		
		acceptText.setText("Я согласен(-на) с условиями продажи и доставки");
		acceptText.setPaintFlags(acceptText.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
		acceptText.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				startActivity(new Intent(OrderCompleteBuyNowActivity.this, TermsActivity.class));
			}
		});

		mAgreement = (CheckBox) findViewById(R.id.order_complete_buy_now_ac_checkbox_accept);
		Button confirm_order = (Button) findViewById(R.id.order_complete_buy_now_ac_btn_ok);
		confirm_order.setOnClickListener(this);

//		mProductBean = (ProductBean) getIntent().getSerializableExtra(PRODUCT_BEAN);
		mProductBean = ProductCacheManager.getInstance().getProductInfo();
		initCounts(mProductBean);
		initProductInfo(mProductBean);
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
	protected void onPause () {
		if (sendOrderLoader != null)
			sendOrderLoader.cancel(true);
		super.onPause();
	}

	private void initCounts (ProductBean bean) {

		TextView product = (TextView) findViewById(R.id.order_complete_buy_now_ac_price_products);
		TextView total = (TextView) findViewById(R.id.order_complete_buy_now_ac_price_all);

		product.setTypeface(Utils.getRoubleTypeFace(this),Typeface.BOLD);
		total.setTypeface(Utils.getRoubleTypeFace(this),Typeface.BOLD);
		String price = formatNumber ((int)bean.getPrice());
		product.setText(price);
		total.setText(price);
	}
	
	private String formatNumber(int value){
		return Formatters.createPriceStringWithRouble(value);
	}

	private void initProductInfo (ProductBean bean) {

		ImageView image = (ImageView) findViewById(R.id.order_complete_buy_now_ac_image);
		TextView name = (TextView) findViewById(R.id.order_complete_buy_now_ac_product_name);
		TextView price = (TextView) findViewById(R.id.order_complete_buy_now_ac_price);

		ImageDownloader downloader = new ImageDownloader(this);
		downloader.download(bean.getFoto(), image);
		
		name.setText(bean.getName());
		price.setText(formatNumber ((int)bean.getPrice()));
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

	private void showProgressDialog(){
		mProgressDialog = new ProgressDialog(this);
		mProgressDialog.setMessage("Подождите...");
		mProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
		mProgressDialog.setOnKeyListener(new DialogInterface.OnKeyListener() {
			
			@Override
			public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
				if(keyCode==KeyEvent.KEYCODE_BACK){
					if(sendOrderLoader != null)
						sendOrderLoader.cancel(false);
					return true;
				}
				return false;
			}
		});
		mProgressDialog.show();
	}
	
	
	class SendOrder extends AsyncTask<Void, Void, String> {

		@Override
		protected void onPreExecute () {
			showProgressDialog();
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
				object.put("delivery_date", CheckoutFirstStepView.FORMAT_CHECKOUT.format(new Date()));
				object.put("product", getProductsArray());

				if (PreferencesManager.isAuthorized()) {
					result = HTTPUtils.sendPostJSON(URLManager.getOrdersCreate(PreferencesManager.getToken()), object);
				} else {
					result = HTTPUtils.sendPostJSON(URLManager.getOrdersCreateAnonim(), object);
				}
			} catch (Exception e) {
				Log.d("OrderCompleteByNow", e.toString());
			}

			return result;
		}

		@Override
		protected void onPostExecute (String result) {
			mProgressDialog.dismiss();
			
			if (!isCancelled()) {
				// Если нет результата от сервера
				try {
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
		String msg = "Ваш заказ " + number+". Пройдите на кассу для оплаты заказа.";// + "\n\nВ ближайшее время с Вами свяжется специалист контакт-cEnter для подтверждения заказа.";
		
    	AlertDialog.Builder dlg = new AlertDialog.Builder(this);
    	dlg.setTitle("Ваш Заказ успешно принят")
        .setMessage(msg)
        .setPositiveButton("ОК", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
        		Intent intent = new Intent();
        		intent.setClass(OrderCompleteBuyNowActivity.this,MainMenuActivity.class);
        		intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        		intent.putExtra("fromOrder", true);
        		startActivity(intent);
            }
        }).create().show();
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
