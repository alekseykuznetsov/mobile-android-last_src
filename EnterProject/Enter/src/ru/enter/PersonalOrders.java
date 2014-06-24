package ru.enter;

import java.util.ArrayList;
import java.util.List;

import com.flurry.android.FlurryAgent;

import ru.enter.DataManagement.PersonData;
import ru.enter.adapters.MyOrdersAdapter;
import ru.enter.adapters.OrderAdapter;
import ru.enter.beans.OrderBean;
import ru.enter.beans.ProductBean;
import ru.enter.beans.ServiceBean;
import ru.enter.interfaces.IBasketElement;
import ru.enter.parsers.ItemsListParser;
import ru.enter.parsers.MyOrdersParser;
import ru.enter.parsers.ServiceListParser;
import ru.enter.utils.Constants;
import ru.enter.utils.Converter;
import ru.enter.utils.PreferencesManager;
import ru.enter.utils.URLManager;
import ru.enter.utils.Utils;
import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

public class PersonalOrders extends Activity {

	private ListView order_list;
	private ListView order_list_product;
	private FrameLayout progress;
	private ImageView status_frame;
	private OrderAdapter adapter;
	private MyOrdersAdapter main_adapter;
	private TextView warninng;
	protected OrderBean mCurrentOrder;
	public View footer;
	private LoadProducts mLoadProducts;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.personal_account_activity_my_orders);
		initWidgets();
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

	private void initWidgets() {
		order_list  = (ListView) findViewById(R.id.personal_account_activity_my_orders_listview);
		order_list_product = (ListView) findViewById(R.id.personal_account_activity_my_orders_product_list_view);
		progress = (FrameLayout) findViewById(R.id.personal_account_activity_my_orders_progress);
		warninng = (TextView) findViewById(R.id.personal_account_activity_my_orders_warning);

		LayoutInflater viewInflater = LayoutInflater.from(this);
		View header = viewInflater.inflate(R.layout.presonal_acc_my_orders_header, null);
		status_frame = (ImageView) header.findViewById(R.id.personal_account_activity_my_orders_status_frame);
		final TextView date_num = (TextView) header.findViewById(R.id.personal_acc_my_orders_date_number);
		final TextView delivery_date = (TextView) header.findViewById(R.id.personal_acc_my_orders_delivery_date);
		final TextView delivery_address = (TextView) header.findViewById(R.id.personal_acc_my_orders_delivery_address);
		final TextView delivery_type = (TextView) header.findViewById(R.id.personal_acc_my_orders_delivery_type);
		final TextView address = (TextView) header.findViewById(R.id.personal_acc_my_orders_address);
		final TextView canceled = (TextView) header.findViewById(R.id.personal_acc_my_orders_delivery_canceled);


		//для списка заказов
		main_adapter = new MyOrdersAdapter(this);
		main_adapter.setObjects(null);
		order_list.setAdapter(main_adapter);

		//для списка продуктов в заказе
		adapter = new OrderAdapter(this);
		order_list_product.addHeaderView(header, null, false);
		order_list_product.setAdapter(adapter);

		order_list.setOnItemClickListener(new OnItemClickListener() {


			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				mCurrentOrder = main_adapter.getItem(arg2);
				order_list.setVisibility(View.INVISIBLE);
				if(!"null".equals(mCurrentOrder.getNumber()) && !"null".equals(mCurrentOrder.getAddedDate()))
					date_num.setText("№ " + mCurrentOrder.getNumber() + " от " + Converter.fromLineToDot(mCurrentOrder.getAddedDate()));
				if(!"null".equals(mCurrentOrder.getDelivery_date()))
					delivery_date.setText(Converter.fromLineToDot(mCurrentOrder.getDelivery_date()));
				if(!"null".equals(mCurrentOrder.getAddress()))
					delivery_address.setText(mCurrentOrder.getAddress());
				makeStatus(mCurrentOrder.getStatus_id());
				if ((mCurrentOrder.getStatus_id() > 0 && mCurrentOrder.getStatus_id() < 6) || (mCurrentOrder.getStatus_id() == 100)) {
					if (mCurrentOrder.getStatus_id() == 100) {
						delivery_type.setVisibility(View.GONE);
						delivery_date.setVisibility(View.GONE);
						delivery_address.setVisibility(View.GONE);
						address.setVisibility(View.GONE);
						canceled.setVisibility(View.VISIBLE);
						canceled.setText("Отменен");
					} else {
						delivery_type.setVisibility(View.VISIBLE);
						delivery_date.setVisibility(View.VISIBLE);
						delivery_address.setVisibility(View.VISIBLE);
						address.setVisibility(View.VISIBLE);
						canceled.setVisibility(View.GONE);
					}
				} else {
					delivery_type.setVisibility(View.GONE);
					delivery_date.setVisibility(View.GONE);
					delivery_address.setVisibility(View.GONE);
					address.setVisibility(View.GONE);
					canceled.setVisibility(View.VISIBLE);
					canceled.setText("Не установлен");
				}

				if(mCurrentOrder.getDelivery_type_id() == 3)
					delivery_type.setText("Можно забрать");
				else
					delivery_type.setText("Привезем");

				if (mLoadProducts != null){
					mLoadProducts.cancel(true);
				}

				mLoadProducts = new LoadProducts();
				mLoadProducts.execute();
			}
		});

		order_list_product.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
				IBasketElement bean = adapter.getItem(arg2 - 1); //из-за хедера
				if (bean.isProduct()){
					Intent intent = new Intent();
					intent.setClass(PersonalOrders.this, ProductCardActivity.class);
					intent.putExtra(ProductCardActivity.ID, bean.getId()); 
					intent.putExtra(ProductCardActivity.EXTRA_FROM, Constants.FLURRY_FROM.OrderDetails.toString());
					startActivity(intent);
				}
			}
		});
	}

	/**
	 * Форматирует адрес, добавляет поле geo
	 * @param link урл
	 * @return урл вида products/гео/продукт_ид
	 */
	private String formatUrlToGEO(String link){
		String product_id = link.substring("products/".length());
		String geo_id = String.valueOf(PreferencesManager.getCityid());
		return "products/".concat(geo_id).concat("/").concat(product_id);
	}

	private void makeStatus(int status){
		int resource_id = R.drawable.status_slider_01;
		switch (status) {
		case 1:
			resource_id = R.drawable.status_slider_01;
			break;
		case 2:
			resource_id = R.drawable.status_slider_02;		
			break;
		case 3:
			resource_id = R.drawable.status_slider_03;
			break;
		case 4:
			resource_id = R.drawable.status_slider_04;
			break;
		case 5:
			resource_id = R.drawable.status_slider_05;
			break;
		case 100:
			resource_id = R.drawable.status_slider_00;
			break;

		default:
			resource_id = R.drawable.status_slider_00;
			break;
		}
		status_frame.setBackgroundResource(resource_id);
	}

	@Override
	protected void onResume() {//TODO
		super.onResume();
		if (PreferencesManager.isAuthorized()) reloadOrders();
	}

	private void reloadOrders(){
		new LoadOrders().execute();
	}

	private class LoadOrders extends AsyncTask<Void, Void, ArrayList<OrderBean>> {

		@Override
		protected void onPreExecute() {
			progress.setVisibility(View.VISIBLE);

		}

		@Override
		protected ArrayList<OrderBean> doInBackground(Void... params) {
			ArrayList<OrderBean> answer;
			PersonData data = PersonData.getInstance();
			answer = new MyOrdersParser(PersonalOrders.this).parse(URLManager.getOrders(PreferencesManager.getToken()));
			data.getPersonBean().setOrders(answer);
			data.setOrdersChanged(false);
			return answer;
		}

		@Override
		protected void onPostExecute(ArrayList<OrderBean> result) {
			main_adapter.setObjects(result);
			if(Utils.isEmptyList(result)){
				warninng.setVisibility(View.VISIBLE);
			}else{
				warninng.setVisibility(View.GONE);

			}
			progress.setVisibility(View.GONE);
		}
	}

	private class LoadProducts extends AsyncTask<Void, Void, List<IBasketElement>> {

		private List<ProductBean> mProducts = new ArrayList<ProductBean>();
		private List<ServiceBean> mServices = new ArrayList<ServiceBean>();
		private List<ServiceBean> mRelatedServices = new ArrayList<ServiceBean>();

		private boolean mCancel = false;

		@Override
		protected void onCancelled() {
			super.onCancelled();
			mCancel = true;
		}

		@Override
		protected void onPreExecute() {
			order_list_product.removeFooterView(footer);
			adapter.setObjects(null);
			progress.setVisibility(View.VISIBLE);
		}

		@Override
		protected List<IBasketElement> doInBackground(Void... params) {

			//загрузка продуктов
			List<ProductBean> productsAll;
			loadProducts();

			//загрузка услуг
			List<ServiceBean> servicesAll = loadServicesAll();

			//разбор услуг на 2 массива: mServices и mRelatedServices
			if(!Utils.isEmptyList(servicesAll)){
				sortServices(servicesAll);
			}

			return getSortedAll();


		}

		@Override
		protected void onPostExecute(List<IBasketElement> result) {
			if ( !mCancel) {

				if( ! Utils.isEmptyList(result)){
					adapter.setObjects(result);
					//добавляем футер
					if(footer!=null) {
						order_list_product.removeFooterView(footer);
					}
					footer = createFooter(result);
					order_list_product.addFooterView(footer, null, false);
					//и только теперь ставим адаптер
					order_list_product.setAdapter(adapter);
				}

			}

			progress.setVisibility(View.GONE);
		}

		private void loadProducts () {
			//получаем все идентификаторы товаров заказа
			List<Long> productIDs = mCurrentOrder.getProductsID();
			//загрузить продукты по идентификаторам
			if ( ! Utils.isEmptyList(productIDs)){
				//формируем ссылки для загрузки
				String urlProducts = URLManager.getProductsInfo(PreferencesManager.getCityid(),
						Utils.getDpiForItemList(PersonalOrders.this),
						productIDs);

				ItemsListParser productsParser = new ItemsListParser(urlProducts);
				//получили список продуктов
				mProducts =  productsParser.parse();
			}
			//Сливаем данные из текущего ордера в продукты
			if (mCurrentOrder.getProducts() != null)
				for (ProductBean beanOrd : mCurrentOrder.getProducts()) {
					for (ProductBean bean : mProducts) {
						if (bean.getId() == beanOrd.getId()) {
							bean.setCount(beanOrd.getCount());
							bean.setPrice(beanOrd.getPrice());
						}
					}
				}			
		}

		private List<ServiceBean> loadServicesAll () {
			List<ServiceBean> result = new ArrayList<ServiceBean>();
			//получаем все идентификаторы услуг заказа
			List<Long> serviceIDs = mCurrentOrder.getServicesIDs();
			if ( ! Utils.isEmptyList(serviceIDs)){

				String urlServices = URLManager.getServicesInfo(PreferencesManager.getCityid(),
						Utils.getDpiForItemList(PersonalOrders.this),
						serviceIDs);

				ServiceListParser servicesParser = new ServiceListParser(urlServices);
				result = servicesParser.parse();
			}

			for(ServiceBean beanOrd:mCurrentOrder.getServices()){
				for(ServiceBean bean:result){
					if(bean.getId()==beanOrd.getId()){
						bean.setCount(beanOrd.getCount());
						bean.setPrice((int) beanOrd.getPrice());
					}
				}
			}

			return result;
		}

		//разбираем на связанные и не связанные
		private void sortServices (List<ServiceBean> servicesAll) {
			List<ServiceBean> servicesWithIdFromOrder = mCurrentOrder.getServices();
			for (int i = 0; i < servicesWithIdFromOrder.size(); i++) {
				int serviceOrderId = servicesWithIdFromOrder.get(i).getProductId();
				ServiceBean service = servicesAll.get(i);
				if (serviceOrderId != 0) {
					//т.к. servicesAll приход с сервиса без productId
					service.setProductId(serviceOrderId);
					//связанные
					mRelatedServices.add(servicesAll.get(i));
				} else {
					//не связанные
					mServices.add(service);
				}
			}
		}

		private List<IBasketElement> getSortedAll () {
			List<IBasketElement> result = new ArrayList<IBasketElement>();
			for (ProductBean product : mProducts) {
				result.add(product);
				for (ServiceBean service : mRelatedServices) {
					if (product.getId() == service.getProductId()) {
						result.add(service);
					}
				}
			}

			result.addAll(mServices);
			return result;
		}

	}

	private View createFooter(List<IBasketElement> result){

		int productsCount = 0, servicesCount = 0,count;
		int productsPrice = 0, servicesPrice = 0;

		for (IBasketElement element : result) {
			if (element.isProduct()) {
				count = element.getCount();
				productsCount += count;
				productsPrice += count * element.getPrice();
			} else {
				count = element.getCount();
				servicesCount += count;
				servicesPrice += count * element.getPrice();
			}
		}

		View view = LayoutInflater.from(this).inflate(R.layout.my_orders_product_list_row_sum, null);

		//все продукты
		if(productsCount == 0){
			view.findViewById(R.id.orders_sum_product_label_count).setVisibility(View.GONE);
			view.findViewById(R.id.orders_sum_product_count).setVisibility(View.GONE);
			view.findViewById(R.id.orders_sum_product_sum).setVisibility(View.GONE);
			view.findViewById(R.id.orders_sum_product_rouble).setVisibility(View.GONE);
		}else{
			((TextView) view.findViewById(R.id.orders_sum_product_rouble)).setTypeface(Utils.getRoubleTypeFace(this));

			TextView productCountText = (TextView) view.findViewById(R.id.orders_sum_product_count);
			productCountText.setText(String.valueOf(productsCount) + " шт.");

			TextView productSumText = (TextView) view.findViewById(R.id.orders_sum_product_sum);
			productSumText.setText(Converter.priceFormat(productsPrice));
		}

		//все услуги
		if(servicesCount == 0){
			view.findViewById(R.id.orders_sum_service_label_count).setVisibility(View.GONE);
			view.findViewById(R.id.orders_sum_service_count).setVisibility(View.GONE);
			view.findViewById(R.id.orders_sum_service_sum).setVisibility(View.GONE);
			view.findViewById(R.id.orders_sum_service_rouble).setVisibility(View.GONE);
		}else{
			((TextView) view.findViewById(R.id.orders_sum_service_rouble)).setTypeface(Utils.getRoubleTypeFace(this));

			TextView serviceCountText = (TextView) view.findViewById(R.id.orders_sum_service_count);
			serviceCountText.setText(String.valueOf(servicesCount) + " шт.");

			TextView serviceSumText = (TextView) view.findViewById(R.id.orders_sum_service_sum);
			serviceSumText.setText(Converter.priceFormat(servicesPrice));
		}


		// Доставка
		if ( mCurrentOrder.getDelivery_type_id() != 1) {
			view.findViewById(R.id.orders_delivery_label).setVisibility(View.GONE);
			view.findViewById(R.id.orders_delivery).setVisibility(View.GONE);
			view.findViewById(R.id.orders_delivery_rouble).setVisibility(View.GONE);
		} else {
			((TextView) view.findViewById(R.id.orders_delivery_rouble)).setTypeface(Utils.getRoubleTypeFace(this));

			TextView deliveryText = (TextView) view.findViewById(R.id.orders_delivery);
			deliveryText.setText(Converter.priceFormat(mCurrentOrder.getDeliveryPrice()));
		}

		//Итого
		((TextView) view.findViewById(R.id.orders_sum_all_rouble)).setTypeface(Utils.getRoubleTypeFace(this));

		TextView allSum = (TextView) view.findViewById(R.id.orders_sum_all);

		//allSum.setText(Converter.priceFormat(productsPrice + servicesPrice));
		allSum.setText(Converter.priceFormat(mCurrentOrder.getSum()));

		return view;
	}

	@Override
	public void onBackPressed() {
		if (order_list == null || order_list.getVisibility()==View.VISIBLE){
			super.onBackPressed();
		}else{
			order_list.setVisibility(View.VISIBLE);
		}
	}


}
