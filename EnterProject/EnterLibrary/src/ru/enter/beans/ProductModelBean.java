package ru.enter.beans;

import java.io.Serializable;

public class ProductModelBean implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -7485426635172975661L;
	private String value;
	private ProductBean product;
		
	public String getValue() {
		return value;
	}
	public void setValue(String value) {
		this.value = value;
	}
	public ProductBean getProductBean() {
		return product;
	}
	public void setProductBean(ProductBean product) {
		this.product = product;
	}

}
