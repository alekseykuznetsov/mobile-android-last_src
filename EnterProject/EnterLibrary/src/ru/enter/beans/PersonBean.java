package ru.enter.beans;

import java.util.ArrayList;

public class PersonBean {
	private long id;
	
	private String ui;
	private String ip;
	private String type_id;
	private int isActive;
	private String occupation;
	private String identity;
	private String geo_id;
	private String address;
	private String zip_code;
	private String subscribe;
	private String lastLogin;
	private String added;
	private String updated;
	private ArrayList<AddressBean> addressList;
	//private String legal;
	
	private String firstName="";
	private String lastName="";
	private String middleName;
//	private String nickname;
	private String sex;
	private String birthday;
//	private String metier;
	private String email="";
	private String phone="";
	private String mobile="";
	private String skype;
//	private String media_avatar;
//	private ArrayList<AddressBean> address;
//	private ArrayList<WishListBean> wishlists;
	private ArrayList<OrderBean> orders;
	

	public void setName(String name) {
		this.firstName = name;
	}
	public String getName() {
		return firstName;
	}

	public void setId(long id) {
		this.id = id;
	}
	public long getId() {
		return id;
	}
	public void setLastName(String lastName) {
		this.lastName = lastName;
	}
	public String getLastName() {
		return lastName;
	}
	public void setMiddleName(String middleName) {
		this.middleName = middleName;
	}
	public String getMiddleName() {
		return middleName;
	}
//	public void setNickname(String nickname) {
//		this.nickname = nickname;
//	}
//	public String getNickname() {
//		return nickname;
//	}
	
	public void setBirthday(String birthday) {
		this.birthday = birthday;
	}
	public String getBirthday() {
		return birthday;
	}
//	public void setMetier(String metier) {
//		this.metier = metier;
//	}
//	public String getMetier() {
//		return metier;
//	}
	public void setEmail(String email) {
		this.email = email;
	}
	public String getEmail() {
		return email;
	}
	public void setPhone(String phone) {
		this.phone = phone;
	}
	public String getPhone() {
		return phone;
	}
	public void setMobile(String mobile) {
		this.mobile = mobile;
	}
	public String getMobile() {
		return mobile;
	}
	public void setSkype(String skype) {
		this.skype = skype;
	}
	public String getSkype() {
		return skype;
	}
//	public void setMedia_avatar(String media_avatar) {
//		this.media_avatar = media_avatar;
//	}
//	public String getMedia_avatar() {
//		return media_avatar;
//	}
	public void setAddressList(ArrayList<AddressBean> address) {
		this.addressList = address;
	}
	public ArrayList<AddressBean> getAddressList() {
		return addressList;
	}
//	public void setWishlists(ArrayList<WishListBean> wishlists) {
//		this.wishlists = wishlists;
//	}
//	public ArrayList<WishListBean> getWishlists() {
//		return wishlists;
//	}
	public void setOrders(ArrayList<OrderBean> orders) {
		this.orders = orders;
	}
	public ArrayList<OrderBean> getOrders() {
		return orders;
	}
	public String getSex() {
		return sex;
	}
	public void setSex(String sex) {
		this.sex = sex;
	}
	public void setUi(String ui) {
		this.ui = ui;
	}
	public String getUi() {
		return ui;
	}
	public void setIp(String ip) {
		this.ip = ip;
	}
	public String getIp() {
		return ip;
	}
	public void setType_id(String type_id) {
		this.type_id = type_id;
	}
	public String getType_id() {
		return type_id;
	}
	public void setIsActive(int isActive) {
		this.isActive = isActive;
	}
	public int getIsActive() {
		return isActive;
	}
	public void setOccupation(String occupation) {
		this.occupation = occupation;
	}
	public String getOccupation() {
		return occupation;
	}
	public void setIdentity(String identity) {
		this.identity = identity;
	}
	public String getIdentity() {
		return identity;
	}
	public void setGeo_id(String geo_id) {
		this.geo_id = geo_id;
	}
	public String getGeo_id() {
		return geo_id;
	}
	public void setAddress(String address) {
		this.address = address;
	}
	public String getAddress() {
		return address;
	}
	public void setZip_code(String zip_code) {
		this.zip_code = zip_code;
	}
	public String getZip_code() {
		return zip_code;
	}
	public void setSubscribe(String subscribe) {
		this.subscribe = subscribe;
	}
	public String isSubscribe() {
		return subscribe;
	}
	public void setLastLogin(String lastLogin) {
		this.lastLogin = lastLogin;
	}
	public String getLastLogin() {
		return lastLogin;
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

}
