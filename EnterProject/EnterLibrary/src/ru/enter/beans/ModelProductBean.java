package ru.enter.beans;

import java.io.Serializable;
import java.util.ArrayList;

public class ModelProductBean implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = -7485426635172975660L;
	private String property;
	private String value;
	private String unit;
	private String option;
	private ArrayList<ProductModelBean> products;
	
	public String getProperty() {
		return property;
	}
	public void setProperty(String property) {
		this.property = property;
	}
	public String getValue() {
		return value;
	}
	public void setValue(String value) {
		this.value = value;
	}
	public String getUnit() {
		return unit;
	}
	public void setUnit(String unit) {
		this.unit = unit;
	}
	public String getOption() {
		return option;
	}
	public void setOption(String option) {
		this.option = option;
	}
	public ArrayList<ProductModelBean> getProducts() {
		return products;
	}
	public void setProducts(ArrayList<ProductModelBean> products) {
		this.products = products;
	}
}
