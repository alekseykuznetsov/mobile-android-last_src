package ru.enter.beans;

import java.util.Date;

import ru.enter.utils.Constants;

public class CheckoutBean {
	public static enum CheckoutFirstStepPayment{
		for_themselves,//для себя
		for_companies//для компании
	}
	public static enum CheckoutFirstStepDelivery{
		express,//экспресс
		standart,//стандарт
		pickup,//самовывоз
		shop//забрать в магазине
	}
	public static enum CheckoutPaymentMethod{//вариант оплаты
		cash,//наличные
		bank_card//банковской картой
	}
	private int cityID=82;//id города
	private int shop_id = 0;
	private CheckoutFirstStepPayment mCheckoutFirstStepPayment = CheckoutFirstStepPayment.for_themselves;
	private CheckoutFirstStepDelivery mCheckoutFirstStepDelivery = CheckoutFirstStepDelivery.standart;
	private CheckoutPaymentMethod mCheckoutPaymentMethod = CheckoutPaymentMethod.cash;
	
	long day = 24*60*60*1000;
	private String mDeliveryDate = Constants.CheckoutFirstStepView_FORMAT_CHECKOUT.format(new Date(new Date().getTime()+day));
	private int mDeliveryTimeId = 1;
	private boolean isAddDeliveryAddress;
	private String mName = "",mLastName = "",mEmail = "",mPhoneNumber = "";
	private String interval = "";
	private float deliveryPrice;
	private String svyaznoyCard;
	
	private String mShopAddress = "";
	
	private AddressBean mUserAddress;
	
	public void setAddDeliveryAddress(boolean value){
		isAddDeliveryAddress = value;
	}
	public boolean isAddDeliveryAddress(){
		return isAddDeliveryAddress;
	}
	public void setCheckoutFirstStepPayment(CheckoutFirstStepPayment mCheckoutFirstStepPayment) {
		this.mCheckoutFirstStepPayment = mCheckoutFirstStepPayment;
	}
	public CheckoutFirstStepPayment getCheckoutFirstStepPayment() {
		return mCheckoutFirstStepPayment;
	}
	public void setCheckoutFirstStepDelivery(CheckoutFirstStepDelivery mCheckoutFirstStepDelivery) {
		this.mCheckoutFirstStepDelivery = mCheckoutFirstStepDelivery;
	}
	public CheckoutFirstStepDelivery getCheckoutFirstStepDelivery() {
		return mCheckoutFirstStepDelivery;
	}
	public void setCityID(int cityID) {
		this.cityID = cityID;
	}
	public int getCityID() {
		return cityID;
	}
	public CheckoutPaymentMethod getCheckoutPaymentMethod() {
		return mCheckoutPaymentMethod;
	}
	public void setCheckoutPaymentMethod(CheckoutPaymentMethod mCheckoutPaymentMethod) {
		this.mCheckoutPaymentMethod = mCheckoutPaymentMethod;
	}
	public int getShop_id() {
		return shop_id;
	}
	public void setShop_id(int shop_id) {
		this.shop_id = shop_id;
	}
	public String getShopAddress() {
		return mShopAddress;
	}
	public void setShopAddress(String mShopAddress) {
		this.mShopAddress = mShopAddress;
	}
	public String getName() {
		return mName;
	}
	public void setName(String mName) {
		this.mName = mName;
	}
	public String getLastName() {
		return mLastName;
	}
	public void setLastName(String mLastName) {
		this.mLastName = mLastName;
	}
	public String getEmail() {
		return mEmail;
	}
	public void setEmail(String mEmail) {
		this.mEmail = mEmail;
	}
	public String getPhoneNumber() {
		return mPhoneNumber;
	}
	public void setPhoneNumber(String mPhoneNumber) {
		this.mPhoneNumber = mPhoneNumber;
	}
	public String getDeliveryDate() {
		return mDeliveryDate;
	}
	public void setDeliveryDate(String mDeliveryDate) {
		this.mDeliveryDate = mDeliveryDate;
	}
	public int getDeliveryTimeId() {
		return mDeliveryTimeId;
	}
	public void setDeliveryTimeId(int mDeliveryTimeId) {
		this.mDeliveryTimeId = mDeliveryTimeId;
	}
	public void setInterval(String interval) {
		this.interval = interval;
	}
	public String getInterval() {
		return interval;
	}
	public void setDeliveryPrice(float price) {
		this.deliveryPrice = price;
	}
	public float getDeliveryPrice() {
		return deliveryPrice;
	}
	public String getSvyaznoyCard() {
		return svyaznoyCard;
	}
	public void setSvyaznoyCard(String svyaznoyCard) {
		this.svyaznoyCard = svyaznoyCard;
	}
	public void setUserAddress (AddressBean userAddress) {
		mUserAddress = userAddress;
	}
	public AddressBean getUserAddress () {
		return mUserAddress;
	}
	
}
