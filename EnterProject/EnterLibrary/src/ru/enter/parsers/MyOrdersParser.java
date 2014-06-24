package ru.enter.parsers;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import ru.enter.beans.OrderBean;
import ru.enter.beans.ProductBean;
import ru.enter.beans.ServiceBean;
import ru.enter.utils.Log;
import android.content.Context;

public class MyOrdersParser extends AbstractParser{
	private final String DEBUG = "Svyaznoy:MyOrdersParser";
	
	public MyOrdersParser(Context context) {
		super(context);
	}
	
	@Override
	@SuppressWarnings("unchecked")
	public ArrayList<OrderBean> parseData(Object objectResult){
		
		ArrayList<OrderBean> objects = new ArrayList<OrderBean>();
			try {
				JSONArray message = (JSONArray) objectResult;
				objects = new ArrayList<OrderBean>(message.length());
				for(int i=0;i<message.length();i++){
					JSONObject item = (JSONObject) message.get(i);
					OrderBean bean = new OrderBean();
					
					bean.setId(item.getInt("id"));
					bean.setType_id(item.getInt("type_id"));
					bean.setFirst_name(item.getString("first_name"));
					bean.setLast_name(item.getString("last_name"));
					bean.setMiddle_name(item.getString("middle_name"));
					bean.setAddress(item.getString("address"));
					bean.setAdded(item.getString("added"));
					bean.setMobile(item.getString("mobile"));
					bean.setPayment_status_id(item.optInt("payment_status_id"));
					bean.setPayment_id(item.optInt("payment_id"));
					bean.setPayment_detail(item.getString("payment_detail"));
					bean.setDelivery_interval_id(item.optInt("delivery_interval_id"));	
					bean.setDelivery_type_id(item.optInt("delivery_type_id"));
					bean.setDelivery_date(item.getString("delivery_date"));
					bean.setGeo_id(item.optInt("geo_id"));
//					bean.setGeo_name(item.getString("geo_name"));
					bean.setIs_receive_sms(item.optInt("is_receive_sms"));
					bean.setExtra(item.getString("extra"));
					bean.setIp(item.getString("ip"));
					bean.setNumber(item.getString("number"));
//					bean.setNumber_erp(item.getString("number_erp"));
					bean.setStatus_id(item.getInt("status_id"));
					
					
					bean.setSum(item.getString("sum"));
					
					if(item.has("delivery")){
						JSONArray deliveryArr = item.getJSONArray("delivery");
						JSONObject deliveryItem = (JSONObject) deliveryArr.get(0); 
						bean.setDeliveryPrice(deliveryItem.getString("price"));
					}
				

					JSONArray products = item.optJSONArray("product");
					List<Long> ids = new ArrayList<Long>();
					ArrayList<ProductBean> productsBeans = new ArrayList<ProductBean>();
					if (products != null){
						for(int y = 0;y < products.length(); y++){
							JSONObject tempObj = (JSONObject) products.get(y);
							long product_id = tempObj.getLong("id");
							ids.add(product_id);
							
							ProductBean product = new ProductBean();
							product.setId((int)product_id);
							
							double price = tempObj.getDouble("price");
							product.setPrice(price);
							
							int count = tempObj.getInt("quantity");
							product.setCount(count);
							
							productsBeans.add(product);
						}
					}
					bean.setProducts(productsBeans);
					bean.setProductsID(ids);
					
					JSONArray services = item.optJSONArray("service");
					List<Long> serviceIds = new ArrayList<Long>();
					ArrayList<ServiceBean> serviceBeans = new ArrayList<ServiceBean>();
					if (services != null){
						for(int y = 0;y < services.length(); y++){
							JSONObject tempObj = (JSONObject) services.get(y);
							long service_id = tempObj.getLong("id");
							serviceIds.add(service_id);
							
							ServiceBean service = new ServiceBean();
							service.setId((int)service_id);
							
							double price = tempObj.getDouble("price");
							service.setPrice((int)price);
							
							int count = tempObj.getInt("quantity");
							service.setCount(count);
							
							int product_id = tempObj.getInt("product_id");
							service.setProductId(product_id);
							
							serviceBeans.add(service);
						}
					}
					bean.setServices(serviceBeans);
					bean.setServicesIDs(serviceIds);
					
					objects.add(bean);
				}
			} catch (JSONException e) {
				Log.e(DEBUG, e.toString());
				objects.clear(); // to avoid incomplete list
			}catch (ClassCastException cce) {
				Log.d(DEBUG, "empty_orders");
				return new ArrayList<OrderBean>();
			}
		return objects;
	}
}
