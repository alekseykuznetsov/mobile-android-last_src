package ru.enter.beans;

import java.util.ArrayList;

public class BannerBean {
	private String photos;
	private int id;
	private String name;
	private ArrayList<Integer> product_ids;
	private String url;
	
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
	public void setPhotos(String photos) {
		this.photos = photos;
	}
	public String getPhotos() {
//		return "http://mobile.enter.ru".concat(photos);
		return photos;
	}
	public ArrayList<Integer> getProduct_ids() {
		return product_ids;
	}
	public void setProduct_ids(ArrayList<Integer> product_ids) {
		this.product_ids = product_ids;
	}
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	

}
