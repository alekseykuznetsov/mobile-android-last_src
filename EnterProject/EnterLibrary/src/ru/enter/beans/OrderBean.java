package ru.enter.beans;

import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

public class OrderBean implements Serializable{
	
	private static final long serialVersionUID = -8939938271426427909L;

	private int id;
	private int type_id;
	private String first_name;
	private String last_name;
	private String middle_name;
	private String address;
	private String mobile;
	private int payment_status_id;
	private int payment_id;
	private String payment_detail;
	private int delivery_interval_id;
	private int delivery_type_id;
	private String delivery_date;	
	private int geo_id;
	private String geo_name;
	private int is_receive_sms;
	private String extra;
	private String ip;
	private String number;
	private String number_erp;
	private ArrayList<ProductBean> products;
	private ArrayList<ServiceBean> services;
	private int status_id;
	private String added;
	private SimpleDateFormat mFormat;
	private List<Long> productsIDs;
	private List<Long> servicesIDs;
	private double deliveryPrice;
	private double sum;
	
	public void setId(int id) {
		this.id = id;
	}
	public int getId() {
		return id;
	}
	public void setFirst_name(String first_name) {
		this.first_name = first_name;
	}
	public String getFirst_name() {
		return first_name;
	}
	public void setLast_name(String last_name) {
		this.last_name = last_name;
	}
	public String getLast_name() {
		return last_name;
	}
	public void setMiddle_name(String middle_name) {
		this.middle_name = middle_name;
	}
	public String getMiddle_name() {
		return middle_name;
	}
	public void setAddress(String address) {
		this.address = address;
	}
	public String getAddress() {
		return address;
	}
	public void setMobile(String mobile) {
		this.mobile = mobile;
	}
	public String getMobile() {
		return mobile;
	}
	public void setPayment_status_id(int payment_status_id) {
		this.payment_status_id = payment_status_id;
	}
	public int getPayment_status_id() {
		return payment_status_id;
	}
	public void setPayment_detail(String payment_detail) {
		this.payment_detail = payment_detail;
	}
	public String getPayment_detail() {
		return payment_detail;
	}
	public void setDelivery_interval_id(int delivery_interval_id) {
		this.delivery_interval_id = delivery_interval_id;
	}
	public int getDelivery_interval_id() {
		return delivery_interval_id;
	}
	public void setDelivery_type_id(int delivery_type_id) {
		this.delivery_type_id = delivery_type_id;
	}
	public int getDelivery_type_id() {
		return delivery_type_id;
	}
	public void setDelivery_date(String delivery_date) {
		this.delivery_date = delivery_date;
	}
	public String getDelivery_date() {
		return delivery_date;
	}
	public void setGeo_id(int geo_id) {
		this.geo_id = geo_id;
	}
	public int getGeo_id() {
		return geo_id;
	}
	public void setGeo_name(String geo_name) {
		this.geo_name = geo_name;
	}
	public String getGeo_name() {
		return geo_name;
	}
	public void setIs_receive_sms(int is_receive_sms) {
		this.is_receive_sms = is_receive_sms;
	}
	public int getIs_receive_sms() {
		return is_receive_sms;
	}
	public void setExtra(String extra) {
		this.extra = extra;
	}
	public String getExtra() {
		return extra;
	}
	public void setIp(String ip) {
		this.ip = ip;
	}
	public String getIp() {
		return ip;
	}
	public void setProducts(ArrayList<ProductBean> products) {
		this.products = products;
	}
	public void updateProducts(ArrayList<ProductBean> newProducts) {
		HashMap<Integer, ProductBean> upd = new HashMap<Integer, ProductBean>();
		for(ProductBean bean: products)
			upd.put(bean.getId(), bean);
		
		for(ProductBean bean: newProducts){
			if(upd.containsKey(bean.getId())){
				ProductBean updBean = upd.get(bean.getId());
				bean.setCount(updBean.getCount());
				bean.setPrice(updBean.getPrice());
			}
		}
		
		this.products = newProducts;
	}
	
	public ArrayList<ProductBean> getProducts() {
		return products;
	}
	public void setNumber(String number) {
		this.number = number;
	}
	public String getNumber() {
		return number;
	}
	public void setNumber_erp(String number_erp) {
		this.number_erp = number_erp;
	}
	public String getNumber_erp() {
		return number_erp;
	}
	public void setType_id(int type_id) {
		this.type_id = type_id;
	}
	public int getType_id() {
		return type_id;
	}
	public void setPayment_id(int payment_id) {
		this.payment_id = payment_id;
	}
	public int getPayment_id() {
		return payment_id;
	}
	public void setStatus_id(int status_id) {
		this.status_id = status_id;
	}
	public int getStatus_id() {
		return status_id;
	}
	public String getAdded() {
		return added;
	}
	public String getAddedDate() {
		String parsedDate = added.split(" ")[0];
		return parsedDate.equals("none") ? "Не указано" : parsedDate;
	}
	public long getAddedDateToCompare(){
		mFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
		long dateInMillis;
		try {
			Date dateToCompare = mFormat.parse(added);
			dateInMillis = dateToCompare.getTime();
		} catch (ParseException e) {
			return new Date().getTime();
		}
		return dateInMillis;
	}
	public void setAdded(String added) {
		this.added = added;
	}
	public void setProductsID(List<Long> ids) {
		this.productsIDs = ids;
	}
	public List<Long> getProductsID() {
		return productsIDs;
	}
	public double getSum() {
		return sum;
	}
	public void setSum(String sum) {
		try{
			this.sum = Double.parseDouble(sum);
		}
		catch(Exception e){
			this.sum = 0;
		}
	}
	public void setServices(ArrayList<ServiceBean> serviceBeans) {
		this.services = serviceBeans;
	}
	
	public ArrayList<ServiceBean> getServices(){
		return services;
	}
	public void setServicesIDs(List<Long> servicesIDs) {
		this.servicesIDs = servicesIDs;
	}
	public List<Long> getServicesIDs() {
		return servicesIDs;
	}
	public double getDeliveryPrice() {
		return deliveryPrice;
	}
	public void setDeliveryPrice(String deliveryPrice) {
		try{
			this.deliveryPrice = Double.parseDouble(deliveryPrice);
		}
		catch(Exception e){
			this.deliveryPrice = 0;
		}
	}
}
