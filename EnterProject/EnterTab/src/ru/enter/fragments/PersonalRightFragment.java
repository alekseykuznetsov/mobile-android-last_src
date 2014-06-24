package ru.enter.fragments;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import com.google.analytics.tracking.android.EasyTracker;

import ru.enter.ProductCardActivity;
import ru.enter.R;
import ru.enter.adapters.PersonalOrderRightListAdapter;
import ru.enter.beans.OrderBean;
import ru.enter.beans.ProductBean;
import ru.enter.beans.ServiceBean;
import ru.enter.interfaces.IBasketElement;
import ru.enter.parsers.ItemsListParser;
import ru.enter.parsers.ServiceListParser;
import ru.enter.utils.Constants;
import ru.enter.utils.Converter;
import ru.enter.utils.PreferencesManager;
import ru.enter.utils.URLManager;
import ru.enter.utils.Utils;
import android.app.Fragment;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

public class PersonalRightFragment extends Fragment implements OnItemClickListener{

	private static final String ORDER_SER = "order";
//	private static final String CASH_NAME = "наличными";
//	private static final String BANKCARD_NAME = "банковской картой";
//	private static final int CASH = 1;
//	private static final int BANKCARD = 2;
//	private static final int SELF = 3;
//	private static final int STANDART = 1;

	private static final String LAND = "land";
	private static final String PORT = "port";
	private PersonalOrderRightListAdapter mAdapter;
	private LoadProducts productLoader;
	private LoadServices serviceLoader;
	private OrderBean mOrder;
	private ArrayList<IBasketElement> mOrderInfo;
	private ArrayList<ServiceBean> mRelatedServices;
	private FrameLayout mProgress;
	
	public static PersonalRightFragment getInstance() {
		return new PersonalRightFragment();
	}
	
	private void showProgress(){
		mProgress.setVisibility(View.VISIBLE);
	}
	
	private void hideProgress(){
		mProgress.setVisibility(View.GONE);
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		mOrder = getExtras();
		View view = inflater.inflate(R.layout.personal_order_fr_right, null);
		
		if (mOrder != null){
			mProgress = (FrameLayout) view.findViewById(R.id.personal_order_fr_right_progress_frame);
			TextView date = (TextView) view.findViewById(R.id.personal_order_fr_right_text_date);
			TextView number = (TextView) view.findViewById(R.id.personal_order_fr_right_text_number);
			ImageView status_image = (ImageView) view.findViewById(R.id.personal_order_fr_right_image_status);
			if (mOrder.getStatus_id() == 0){
				// можно поставить 100, но для разделения ситуаций ставим 110
				mOrder.setStatus_id(110);
			}
			status_image.setBackgroundResource(getStatusImage(mOrder.getStatus_id()));
			
			if(!"null".equals(mOrder.getNumber()) && !"null".equals(mOrder.getAddedDate()))
			{
				date.setText(Converter.fromLineToDot(mOrder.getAddedDate()));
				number.setText("№ " + mOrder.getNumber());
			}
			

			// Footer
			/*
			TextView delivery_date = (TextView) view.findViewById(R.id.personal_order_fr_right_text_delivery);
			TextView delivery_type = (TextView) view.findViewById(R.id.personal_order_fr_right_text_delivery_type);
			TextView delivery_address_type = (TextView) view.findViewById(R.id.personal_order_fr_right_label_address);
			TextView delivery_address = (TextView) view.findViewById(R.id.personal_order_fr_right_text_address);
			TextView delivery_payment = (TextView) view.findViewById(R.id.personal_order_fr_right_text_payment);
			TextView goods_count = (TextView) view.findViewById(R.id.personal_order_fr_right_text_goods_count);
			TextView services_count = (TextView) view.findViewById(R.id.personal_order_fr_right_text_services_count);
			TextView delivery_price = (TextView) view.findViewById(R.id.personal_order_fr_right_text_delivery_price);
			TextView total_price = (TextView) view.findViewById(R.id.personal_order_fr_right_text_total);
			
			
			
			
			if(!"null".equals(order.getDelivery_date())) {
				delivery_date.setText(Converter.fromLineToDot(order.getDelivery_date()));
				switch (order.getDelivery_type_id()){
				case STANDART:
					delivery_type.setText("стандарт");
					delivery_address_type.setText("Привезем");
					break;
				case SELF:
					delivery_type.setText("самовывоз");
					delivery_address_type.setText("Можно забрать");
					break;
				}
			}
			
			if(!"null".equals(order.getPayment_id())) 
			{
				switch (order.getPayment_id()){
				case CASH:
					delivery_payment.setText(CASH_NAME);
					break;
				case BANKCARD:
					delivery_payment.setText(BANKCARD_NAME);
					break;
				}
			}
			if(!"null".equals(order.getAddress())) delivery_address.setText(order.getAddress());			

			if(order.getStatus_id() == 100){
				delivery_date.setVisibility(View.GONE);
				delivery_type.setVisibility(View.GONE);
				delivery_address_type.setVisibility(View.GONE);
				delivery_address.setVisibility(View.GONE);
				delivery_payment.setVisibility(View.GONE);
			}else{
				delivery_date.setVisibility(View.VISIBLE);
				delivery_type.setVisibility(View.VISIBLE);
				delivery_address_type.setVisibility(View.VISIBLE);
				delivery_address.setVisibility(View.VISIBLE);
				delivery_payment.setVisibility(View.VISIBLE);
			}
			if (! Utils.isEmptyList(order.getProductsID()) ){
				goods_count.setText(String.valueOf(order.getProductsID().size()));
			}
			else
				goods_count.setText("0");
			
			if (! Utils.isEmptyList(order.getServicesIDs()) ){
				services_count.setText(String.valueOf(order.getServicesIDs().size()));
			}
			else
				services_count.setText("0");
			
			*/
		}
		return view;
	}
	
	@Override
	public void onResume() {
		super.onResume();
		if (mOrder != null){
			
			if(productLoader != null) productLoader.cancel(true);
			if(serviceLoader != null) serviceLoader.cancel(true);
			
			mOrderInfo = new ArrayList<IBasketElement>();
			 
			productLoader = new LoadProducts();
			productLoader.execute(mOrder);
		}
	}
	
	@Override
	public void onPause() {
		super.onPause();
		if(productLoader != null) productLoader.cancel(true);
		if(serviceLoader != null) serviceLoader.cancel(true);
	}
	
	private void addServices(){
		if (mOrder != null){
			
			if(serviceLoader != null) serviceLoader.cancel(true);
			mRelatedServices = new ArrayList<ServiceBean>();
			
			serviceLoader = new LoadServices();
			serviceLoader.execute(mOrder);
		}
	}
	
	private void start(){
		View view = getView();
		if (view == null)
			return;
		ListView listView = (ListView) view.findViewById(R.id.personal_order_fr_right_listview_order);
		mAdapter = new PersonalOrderRightListAdapter(getActivity());
		mAdapter.setObjects(mOrderInfo);
		mAdapter.setRelatedServices(mRelatedServices);
		listView.setAdapter(mAdapter);
		listView.setOnItemClickListener(this);
		onConfigurationChanged(getResources().getConfiguration());
	}
	
	
	private OrderBean getExtras() {
		OrderBean bean = null;
		Bundle args = getArguments();		
		if (args != null && args.containsKey(ORDER_SER)){
			 bean = (OrderBean) args.getSerializable(ORDER_SER);
		}
		return bean;
	}
	
	private int getStatusImage(int status){
		int resource_id = R.drawable.personal_order_status_slider_0;
		switch (status) {
		// port
		case 1:
			resource_id = R.drawable.personal_order_status_slider_1;
			break;
		case 2:
			resource_id = R.drawable.personal_order_status_slider_2;		
			break;
		case 3:
			resource_id = R.drawable.personal_order_status_slider_3;
			break;
		case 4:
			resource_id = R.drawable.personal_order_status_slider_4;
			break;
		case 5:
			resource_id = R.drawable.personal_order_status_slider_5;
			break;
		case 100:
			resource_id = R.drawable.personal_order_status_slider_0;
			break;
		case 110:
			resource_id = R.drawable.personal_order_status_slider_0;
			break;
		// land
		case 10:
			resource_id = R.drawable.personal_order_status_slider_10;
			break;
		case 20:
			resource_id = R.drawable.personal_order_status_slider_20;		
			break;
		case 30:
			resource_id = R.drawable.personal_order_status_slider_30;
			break;
		case 40:
			resource_id = R.drawable.personal_order_status_slider_40;
			break;
		case 50:
			resource_id = R.drawable.personal_order_status_slider_50;
			break;
		case 1000:
			resource_id = R.drawable.personal_order_status_slider_00;
			break;
		case 1100:
			resource_id = R.drawable.personal_order_status_slider_00;
			break;
		default:
			resource_id = R.drawable.personal_order_status_slider_00;
			break;
		}
		return resource_id;
	}
	
	static Comparator<OrderBean> mComparator = new Comparator<OrderBean>() {
		public int compare(OrderBean first, OrderBean second) {
			long dateFirst = first.getAddedDateToCompare();
			long dateSecond = second.getAddedDateToCompare();
			if(dateFirst < dateSecond) return 1;
			else if (dateFirst > dateSecond) return -1;
			else return 0;
		};
	};

	private class LoadProducts extends AsyncTask<OrderBean, Void, ArrayList<ProductBean>> {

		@Override
		protected void onPreExecute() {
			showProgress();
		}

		@Override
		protected ArrayList<ProductBean> doInBackground(OrderBean... params) {
			//получаем все идентификаторы товаров заказа
			List<Long> productIDs = params[0].getProductsID();
			
			//список для всех элементов заказа(продукты и услуги)
			ArrayList<ProductBean> productsList = new ArrayList<ProductBean>();
			
			//загрузить продукты по идентификаторам
			if ( ! Utils.isEmptyList(productIDs)){
				//формируем ссылки для загрузки
				String urlProducts = URLManager.getProductsInfo(PreferencesManager.getCityid(),160,productIDs);
				
				ItemsListParser productsParser = new ItemsListParser(urlProducts);
				productsList = productsParser.parse();
			}
			return productsList;
		}
		
		@Override
		protected void onPostExecute(ArrayList<ProductBean> productsList) {
			if (isCancelled())
				return;
			hideProgress();
			if( ! Utils.isEmptyList(productsList)){
				for(ProductBean beanOrd:mOrder.getProducts()){
					for(ProductBean bean:productsList){
						bean.setCount(beanOrd.getCount());
						bean.setPrice(beanOrd.getPrice());
					}
				}
				mOrderInfo.addAll(productsList);
			}
			addServices();
		}
	}
	
	private class LoadServices extends AsyncTask<OrderBean, Void, ArrayList<ServiceBean>> {

		@Override
		protected void onPreExecute() {
			showProgress();
		}

		@Override
		protected ArrayList<ServiceBean> doInBackground(OrderBean... params) {
			//получаем все идентификаторы услуг заказа
			List<Long> serviceIDs = params[0].getServicesIDs();
			
			ArrayList<ServiceBean> servicesList = new ArrayList<ServiceBean>();
			
			if ( ! Utils.isEmptyList(serviceIDs)){
				
				String urlServices = URLManager.getServicesInfo(PreferencesManager.getCityid(),160,serviceIDs);
			
				ServiceListParser servicesParser = new ServiceListParser(urlServices);
				servicesList = servicesParser.parse();
				
			}
			return servicesList;
		}
		
		@Override
		protected void onPostExecute(ArrayList<ServiceBean> servicesList) {
			
			if (isCancelled())
				return;
			hideProgress();
			if( ! Utils.isEmptyList(servicesList)){
				for(ServiceBean beanOrd:mOrder.getServices()){
					for(ServiceBean bean:servicesList){
						if(bean.getId()==beanOrd.getId()){
							bean.setCount(beanOrd.getCount());
							bean.setPrice((int) beanOrd.getPrice());
						}
					}
				}
				checkServices(servicesList);
			}
			start();
		}
	}
	
	
	private void checkServices(ArrayList<ServiceBean> servicesList){
		//разбиваем полученные сервисы на обычные и связаныне
		if( ! Utils.isEmptyList(servicesList)){
			ArrayList<ServiceBean> servises = new ArrayList<ServiceBean>();
			ArrayList<ServiceBean> related_servises = new ArrayList<ServiceBean>();;
			ArrayList<ServiceBean> servises_with_product_id = mOrder.getServices();
			int i;
			for (i = 0; i < servises_with_product_id.size(); i++){
				if (servises_with_product_id.get(i).getProductId() != 0){
					servicesList.get(i).setProductId(servises_with_product_id.get(i).getProductId());
					related_servises.add(servicesList.get(i));
				}
				else{
					servises.add(servicesList.get(i));
				}
			}
			mOrderInfo.addAll(servises);
			mRelatedServices.addAll(related_servises);
		}
	}
	
	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		ImageView status_image = (ImageView) getView().findViewById(R.id.personal_order_fr_right_image_status);
		if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
			mAdapter.setOrientation(LAND);
			
			// меняем изображение, загруженное в статус-бар
			// номера у файлов горизонтальной разметки n*10. Перед поворотом подменяем изображение на новое (для следущей ориентации)
			status_image.setBackgroundResource(getStatusImage(mOrder.getStatus_id() * 10));
			
		} else if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {
			mAdapter.setOrientation(PORT);
			
			status_image.setBackgroundResource(getStatusImage(mOrder.getStatus_id()));
		}
	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int position, long id) {
		if (mOrderInfo.get(position).isProduct()){
			Intent intent = new Intent(getActivity(), ProductCardActivity.class);
			intent.putExtra(ProductCardActivity.PRODUCT_ID, (int)id);
			intent.putExtra(ProductCardActivity.EXTRA_FROM, Constants.FLURRY_FROM.OrderDetails.toString());
			EasyTracker.getTracker().sendEvent("product/get", "buttonPress", mOrderInfo.get(position).getName(), (long) mOrderInfo.get(position).getId());
			startActivity(intent);
		}
	}
	
}
