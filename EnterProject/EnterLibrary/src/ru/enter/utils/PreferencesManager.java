package ru.enter.utils;

import android.content.Context;
import android.content.SharedPreferences;

public enum PreferencesManager {
	INSTANCE;
	
	private static final String PREFERENCES = "ENTER_PREFERENCES";
	private static final String PREFERENCES_CITY = "ENTER_PREFERENCES_CURRENT_CITY";
	private static final String PREFERENCES_CITY_ID = "ENTER_PREFERENCES_CURRENT_CITY_ID";
	private static final String PREFERENCES_CITY_HAS_SHOP = "ENTER_PREFERENCES_CURRENT_CITY_HAS_SHOP";
	private static final String PREFERENCES_CASHE_LIFE = "ENTER_PREFERENCES_CASHE_LIFE";
	private static final String PREFERENCES_SMS = "ENTER_PREFERENCES_SMS";
	private static final String PREFERENCES_IS_EMAIL = "ENTER_PREFERENCES_IS_EMAIL";
	private static final String PREFERENCES_USER_ID = "ENTER_PREFERENCES_USER_ID";
	private static final String PREFERENCES_TOKEN = "ENTER_PREFERENCES_TOKEN";
	private static final String PREFERENCES_USER_NAME = "ENTER_PREFERENCES_USER_NAME";
	private static final String PREFERENCES_USER_LASTNAME = "ENTER_PREFERENCES_USER_LASTNAME";
	private static final String PREFERENCES_USER_EMAIL = "ENTER_PREFERENCES_USER_EMAIL";
	private static final String PREFERENCES_USER_MOBILE = "ENTER_PREFERENCES_USER_MOBILE";
	private static final String PREFERENCES_USER_CURRENT_SHOP_ID = "ENTER_PREFERENCES_USER_CURRENT_SHOP_ID";
	private static final String PREFERENCES_USER_CURRENT_SHOP_NAME = "ENTER_PREFERENCES_USER_CURRENT_SHOP_NAME";
	private static final String PREFERENCES_USER_FIRST_START_CATALOG = "ENTER_PREFERENCES_USER_FIRST_START_CATALOG";
	private static final String PREFERENCES_FLAG_SHOW_COUPON_DIALOG = "ENTER_PREFERENCES_FLAG_SHOW_COUPON_DIALOG";
	
	private SharedPreferences mPreferences;
	private Context mContext;
	
	private PreferencesManager() {
		mContext = ApplicationEnter.getContext();
		mPreferences = mContext.getSharedPreferences(PREFERENCES, Context.MODE_PRIVATE);
	}

	public static void setCityName(String cityName){
		INSTANCE.mPreferences
		.edit()
		.putString(PREFERENCES_CITY, cityName)
		.commit();
	}
	
	public static String getCityName(){
		return INSTANCE.mPreferences.getString(PREFERENCES_CITY,"");
	}
	
	public static void setCityId(int cityId){
		INSTANCE.mPreferences
		.edit()
		.putInt(PREFERENCES_CITY_ID, cityId)
		.commit();
	}
	
	public static int getCityid(){
		return INSTANCE.mPreferences.getInt(PREFERENCES_CITY_ID, -1);
	}
	
	public static void setCityHasShop(boolean hasShop){
		INSTANCE.mPreferences
		.edit()
		.putBoolean(PREFERENCES_CITY_HAS_SHOP, hasShop)
		.commit();
	}
	
	public static boolean getHasShop(){
		return INSTANCE.mPreferences.getBoolean(PREFERENCES_CITY_HAS_SHOP, false);
	}
	
	public static void setCacheLife(int time){
		INSTANCE.mPreferences
		.edit()
        .putInt(PREFERENCES_CASHE_LIFE, time)        
        .commit();
	}
	
	public static int getCacheLife(){
		return INSTANCE.mPreferences.getInt(PREFERENCES_CASHE_LIFE, 0);
	}
	
	public static void setSMS(boolean isSMS){
		INSTANCE.mPreferences
		.edit()
        .putBoolean(PREFERENCES_SMS, isSMS)       
        .commit();
	}
	
	public static boolean isSMS(){
		return INSTANCE.mPreferences.getBoolean(PREFERENCES_SMS,  false);
	}
	
	public static void setEmail(boolean isEmail){
		INSTANCE.mPreferences
		.edit()
        .putBoolean(PREFERENCES_IS_EMAIL, isEmail)       
        .commit();
	}
	
	public static boolean isEmail(){
		return INSTANCE.mPreferences.getBoolean(PREFERENCES_IS_EMAIL,  false);
	}
	
	public static void setUserId(long userId){
		INSTANCE.mPreferences
		.edit()
        .putLong(PREFERENCES_USER_ID, userId)       
        .commit();
	}
	
	public static long getUserId(){
		return INSTANCE.mPreferences.getLong(PREFERENCES_USER_ID, -1);
	}
	
	public static void setToken(String token){
		INSTANCE.mPreferences
		.edit()
        .putString(PREFERENCES_TOKEN, token)       
        .commit();
	}
	
	public static String getToken(){
		return INSTANCE.mPreferences.getString(PREFERENCES_TOKEN, "");
	}
	
	public static void setUserName(String userName){
		INSTANCE.mPreferences
		.edit()
		.putString(PREFERENCES_USER_NAME, userName)
		.commit();
	}
	
	public static String getUserName(){
		return INSTANCE.mPreferences.getString(PREFERENCES_USER_NAME,"");
	}

	public static void setUserLastName(String userLastName){
		INSTANCE.mPreferences
		.edit()
		.putString(PREFERENCES_USER_LASTNAME, userLastName)
		.commit();
	}
	
	public static String getUserLastName(){
		return INSTANCE.mPreferences.getString(PREFERENCES_USER_LASTNAME,"");
	}

	public static void setUserEmail(String userEmail){
		INSTANCE.mPreferences
		.edit()
		.putString(PREFERENCES_USER_EMAIL, userEmail)
		.commit();
	}
	
	public static String getUserEmail(){
		return INSTANCE.mPreferences.getString(PREFERENCES_USER_EMAIL,"");
	}
	
	public static void setUserMobile(String userMobile){
		INSTANCE.mPreferences
		.edit()
		.putString(PREFERENCES_USER_MOBILE, userMobile)
		.commit();
	}
	
	public static String getUserMobile(){
		return INSTANCE.mPreferences.getString(PREFERENCES_USER_MOBILE,"");
	}
	
	public static boolean isAuthorized(){
//		return getUserId() != -1;
		return getToken() != "";
	}
	
	public static int getUserCurrentShopId(){
		return INSTANCE.mPreferences.getInt(PREFERENCES_USER_CURRENT_SHOP_ID, 0);
	}

	public static void setUserCurrentShopId(int shop_id){
		INSTANCE.mPreferences
		.edit()
		.putInt(PREFERENCES_USER_CURRENT_SHOP_ID, shop_id)
		.commit();
	}
	
	public static String getUserCurrentShopName(){
		return INSTANCE.mPreferences.getString(PREFERENCES_USER_CURRENT_SHOP_NAME, "");
	}

	public static void setUserCurrentShopName(String shop_name){
		INSTANCE.mPreferences
		.edit()
		.putString(PREFERENCES_USER_CURRENT_SHOP_NAME, shop_name)
		.commit();
	}
	
	public static int getUserFirstStartCatalog(){
		return INSTANCE.mPreferences.getInt(PREFERENCES_USER_FIRST_START_CATALOG, 0);
	}

	public static void setFirstStartCatalog(int first){
		INSTANCE.mPreferences
		.edit()
		.putInt(PREFERENCES_USER_FIRST_START_CATALOG, first)
		.commit();
	}
	
	public static boolean showGetCouponDialog(){
		return INSTANCE.mPreferences.getBoolean(PREFERENCES_FLAG_SHOW_COUPON_DIALOG, false);
	}

	public static void showGetCouponDialog(boolean flag){
		INSTANCE.mPreferences
		.edit()
		.putBoolean(PREFERENCES_FLAG_SHOW_COUPON_DIALOG, flag)
		.commit();
	}
	
}

