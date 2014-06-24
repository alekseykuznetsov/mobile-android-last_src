package ru.enter.beans;

import java.io.Serializable;

public class ShopBean implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -3660359131349620768L;
	
	private String name;
	private int id;
	private int quantity;
	private String foto;
	private String latitude;
	private String longitude;
	private String description;
	private String working_time;
	private String phone;
	private String address;
	private String way_walk;
	private String way_auto;
	private int stick;
	private int quantity_showroom;
	
	private float distance;
	
	public void setName(String name) {
		this.name = name;
	}
	public String getName() {
		return name;
	}
	public void setLatitude(String latitude) {
		this.latitude = latitude;
	}
	public String getLatitude() {
		return latitude;
	}
	public void setLongitude(String longitude) {
		this.longitude = longitude;
	}
	public String getLongitude() {
		return longitude;
	}
	public void setId(int id) {
		this.id = id;
	}
	public int getId() {
		return id;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public String getWorking_time() {
		return working_time;
	}
	public void setWorking_time(String working_time) {
		this.working_time = working_time;
	}
	public String getPhone() {
		return phone;
	}
	public void setPhone(String phone) {
		this.phone = phone;
	}
	public String getAddress() {
		return address;
	}
	public void setAddress(String address) {
		this.address = address;
	}
	public int getQuantity() {
		return quantity;
	}
	public void setQuantity(int quantity) {
		this.quantity = quantity;
	}
	public String getWay_auto() {
		return way_auto;
	}
	public void setWay_auto(String way_auto) {
		this.way_auto = way_auto;
	}
	public String getWay_walk() {
		return way_walk;
	}
	public void setWay_walk(String way_walk) {
		this.way_walk = way_walk;
	}
	public String getFoto() {
		return foto;
	}
	public void setFoto(String foto) {
		this.foto = foto;
	}
	public int getStick() {
		return stick;
	}
	public void setStick(int stick) {
		this.stick = stick;
	}
	public void setDistance(float distance) {
		this.distance = distance;
	}
	public float getDistance() {
		return distance;
	}
	public String getDistanceString (){
		if(distance < 1000)
			return String.format("%.1f м", distance);
		else
			return String.format("%.0f км", distance / 1000);
	}
	public int getQuantityShowroom () {
		return quantity_showroom;
	}
	public void setQuantityShowroom (int quantity_showroom) {
		this.quantity_showroom = quantity_showroom;
	}
	
}
