package ru.enter.beans;

import java.io.Serializable;

public class AddressBean implements Serializable{
	
	private static final long serialVersionUID = 4355663924750572038L;
	private int id;
	private String first_name;
	private String last_name;
	private String middle_name;
	private int is_primary;
	private int geo_id;
	
	private String address;
	private String street;
	private String house;
	private String housing;
	private String floor;
	private String flat;
	
	private MetroBean metro;

	private String zip_code;
	private String added;
	private String updated;
	private String mobile;

	public void setAddress(String address) {
		this.address = address;
	}

	public String getAddress() {
		return address;
	}

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

	public void setMiddle_name(String middle_name) {
		this.middle_name = middle_name;
	}

	public String getMiddle_name() {
		return middle_name;
	}

	public void setIs_primary(int is_primary) {
		this.is_primary = is_primary;
	}

	public int getIs_primary() {
		return is_primary;
	}

	public void setGeo_id(int geo_id) {
		this.geo_id = geo_id;
	}

	public int getGeo_id() {
		return geo_id;
	}

	public void setLast_name(String last_name) {
		this.last_name = last_name;
	}

	public String getLast_name() {
		return last_name;
	}

	public void setZip_code(String zip_code) {
		this.zip_code = zip_code;
	}

	public String getZip_code() {
		return zip_code;
	}

	public void setAdded(String added) {
		this.added = added;
	}

	public String getAdded() {
		return added;
	}

	public void setUpdated(String updated) {
		this.updated = updated;
	}

	public String getUpdated() {
		return updated;
	}

	public void setMobile(String mobile) {
		this.mobile = mobile;
	}

	public String getMobile() {
		return mobile;
	}

	public String getStreet () {
		return street;
	}

	public void setStreet (String street) {
		this.street = street;
	}

	public String getHouse () {
		return house;
	}

	public void setHouse (String house) {
		this.house = house;
	}

	public String getHousing () {
		return housing;
	}

	public void setHousing (String housing) {
		this.housing = housing;
	}

	public String getFloor () {
		return floor;
	}

	public void setFloor (String floor) {
		this.floor = floor;
	}

	public String getFlat () {
		return flat;
	}

	public void setFlat (String flat) {
		this.flat = flat;
	}

	public void setMetro (MetroBean metro) {
		this.metro = metro;
	}

	public MetroBean getMetro () {
		return metro;
	}

}
