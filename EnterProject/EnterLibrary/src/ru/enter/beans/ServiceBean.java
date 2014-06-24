package ru.enter.beans;

import java.io.Serializable;

import ru.enter.interfaces.IBasketElement;


public class ServiceBean implements Serializable, IBasketElement{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 8914125677512786471L;
	
	private String foto;
	private String description;
	private int price;
	private String work;
	private int id;
	private String name;
	private int count = 1;
	//поле для связанной услуги
	private int product_id;
	
	private int min_sum_cost_to_deliver;
	private int price_percent;
	private int price_type_id;
	private boolean is_delivery;
	private int price_min;
	private boolean is_in_shop;
	
	public void setDescription(String description) {
		this.description = description;
	}
	public String getDescription() {
		return description;
	}
	public void setWork(String work) {
		this.work = work;
	}
	public String getWork() {
		return work;
	}
	public void setId(int id) {
		this.id = id;
	}
	public int getId() {
		return id;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getName() {
		return name;
	}
	public void setPrice(int price) {
		this.price = price;
	}
	public double getPrice() {
		return price;
	}
	public void setFoto(String foto) {
		this.foto = foto;
	}
	public String getFoto() {
		return foto;
	}
	@Override
	public boolean isProduct() {
		return false;
	}
	@Override
	public void setCount(int count) {
		this.count = count;
	}
	@Override
	public int getCount() {
		return count;
	}
	@Override
	public void increaseCount() {
		count++;
	}
	public int getProductId() {
		return product_id;
	}
	public void setProductId(int product_id) {
		this.product_id = product_id;
	}
	public void setMinSumCostToDeliver(int min_sum_cost_to_deliver) {
		this.min_sum_cost_to_deliver = min_sum_cost_to_deliver;
	}
	public int getMinSumCostToDeliver() {
		return min_sum_cost_to_deliver;
	}
	public void setPricePercent(int price_percent) {
		this.price_percent = price_percent;
	}
	public int getPricePercent() {
		return price_percent;
	}
	public void setPriceTypeId(int price_type_id) {
		this.price_type_id = price_type_id;
	}
	public int getPriceTypeId() {
		return price_type_id;
	}
	public void setDelivery(boolean is_delivery) {
		this.is_delivery = is_delivery;
	}
	public boolean isDelivery() {
		return is_delivery;
	}
	public void setPriceMin(int price_min) {
		this.price_min = price_min;
	}
	public int getPriceMin() {
		return price_min;
	}
	public void setInShop(boolean is_in_shop) {
		this.is_in_shop = is_in_shop;
	}
	public boolean isInShop() {
		return is_in_shop;
	}
	
	@Override
	public boolean equals(Object obj){
	    if (this == obj)
	        return true;
	    if (!(obj instanceof ServiceBean))
	        return false;
	    ServiceBean bean = (ServiceBean)obj;
	    return ((id == bean.getId())&&name.equals(bean.getName())&&((int)(price-bean.getPrice())==0)&&(product_id==bean.getProductId()));
	}
	
	@Override
	public int hashCode(){
	    int code = 11;
	    int k = 7;
	    code = k*code + id;
	    code = k*code + name.hashCode();
	    code = k*code + (int)(price);
	    code = k*code + product_id;
	    return code;
	}
	
}
