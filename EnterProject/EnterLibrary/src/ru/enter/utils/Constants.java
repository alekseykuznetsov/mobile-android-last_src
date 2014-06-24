package ru.enter.utils;

import java.text.SimpleDateFormat;

public class Constants {
	
	public static final String ShopCard_SHOP_ID = "SHOP_ID";
	public static final String Maps_LATITUDE = "LATITUDE";
	public static final String Maps_LONGITUDE = "LONGITUDE";
	public static final String Maps_FORWARD = "FORWARD";
	public static final String Maps_STATE = "STATE";
	public static final String CheckoutActivity_LAUNCH_TYPE = "launch_type";
	public static final String CheckoutActivity_LAUNCH_CLICK_ONE_TYPE = "launch_click_one_type";
	public static final String CheckoutActivity_SHOP_ID = "shop_id";
	public static final String CheckoutActivity_SHOP_ADDRESS = "shop_address";
	public static final int CheckoutActivity_LAUNCH_TYPE_NORMAL = 0;
	public static final int CheckoutActivity_LAUNCH_TYPE_BUY_ONE_CLICK = 1;
	public static final int CheckoutActivity_LAUNCH_CLICK_ONE_TYPE_1 = 2;
	public static final int CheckoutActivity_LAUNCH_CLICK_ONE_TYPE_2 = 3;
	public static final int CheckoutGeoLocationActivity_CHOOSE_SUCCSES = 1;
	public static final int CheckoutGeoLocationActivity_CHOOSE_FAILED = 2;
	public static final String CheckoutGeoLocationActivity_CHOSED_BTN = "CHOSED_BTN";
	public static enum CheckoutGeoLocationActivity_SelectedButton {list,map};
	public static final SimpleDateFormat FORMAT = new SimpleDateFormat("dd.MM.yyyy");
	public static final SimpleDateFormat CheckoutFirstStepView_FORMAT_CHECKOUT = new SimpleDateFormat("yyyy-MM-dd");

	public static enum FLURRY_EVENT {Banner_Сlick, Catalog_Section, Discounted_Goods_Sales, Good_Sales, Share,
		Goods_Sent_To_Basket, Product_Screen, Go_To_Cabinet, Go_To_Shops, Go_To_Feedback,
		About_Feedback, Registration, Catalog_Search, Artikyl_Search, Qr_Code_Search,
//		Zoomed_Picture_Open, Go_To_Basket, About_Project, About_How_To_Pay, About_Order, Go_To_Scanner // deprecated
		};
	
	public static enum FLURRY_EVENT_PARAM {To, // Share
										Delivery_Type, City_Purchase, Payment_Type, Is_Authorized, // Good_Sales
										From, // Product_Screen
										Category // Catalog_Section
		}
	
	public static enum FLURRY_TO {FB, VK, Email, Twitter};
	public static enum FLURRY_DELIVERY_TYPE {Dostavka, Samovivoz};
	public static enum FLURRY_PAYMENT_TYPE {Cash, PaymentCard};
	public static enum FLURRY_IS_AUTHORIZED {True, False};
	public static enum FLURRY_FROM {Search, Catalog, Banner, Basket, OrderDetails, OtherProduct}
	
}

