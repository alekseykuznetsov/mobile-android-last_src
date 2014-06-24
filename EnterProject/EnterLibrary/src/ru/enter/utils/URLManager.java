package ru.enter.utils;

import java.util.ArrayList;
import java.util.List;


public class URLManager {

	//--------------------------------------API----------------------------------------------------//
	private static final String host = "https://mobile.enter.ru/api2_1/";
//	private static final String host = "http://mobile.ent3.ru/api2_1/";
//	private static final String host = "http://api.enter.ru/api2_1/";
	
	private static final String cities = host + "cities/";
	private static final String categories_list = host + "categories/%s/%s/%s/";
	
	private static final String products_in_category = host + "products/in_shop/%s/%s/%s/%s/%s/%s/";
	private static final String products_count_in_category = host + "products/in_shop/%s/%s/%s/count/";
	private static final String productCard = host + "product_card/in_shop/%s/%s/%s/%s";
	private static final String filters = host + "filters/%s/%s/all";
	private static final String searchByFilters = host + "search_by_filters/";
	private static final String delivery_calc = host + "delivery/calc/";
	private static final String delivery_calc_with_errors = host + "delivery/calc_with_errors";
	private static final String products_info = host + "products/list/%s/%s/[%s]/";
	private static final String product_list_by_ids = host + "products/list/%s/%s/%s/";
	private static final String catalog_tree = host + "categories/tree/%s/%s/";
	private static final String services_tree = host + "f1/categories/expanded_tree/%s/%s/";
	
	private static final String services_info = host + "f1/services/list/%s/%s/[%s]/";

	private static final String shop_list = host + "shops/%s/";
	private static final String shop = host + "shop_card/%s/";
	
	private static final String search_v2 = host + "search/products/fulltext/%s/%s/%s/%s/%s";
	private static final String search_v2_count = host + "search/products/fulltext/%s/%s/count/";
	private static final String searchByQR = host + "qrcode/%s/%s/%s/";
	private static final String searchByBarCode = host + "product_card_by_barcode/%s/%s/%s/";
//	private static final String searchByBarCode = host + "product_card_by_barcode/in_shop/%s/%s/%s/%s/";
	
	private static final String promo = host + "promo/%s/%s/";
	private static final String promoNew = host + "promo/empty,product/%s/%s/";
	private static final String feed_back = host + "postmail/";
	
	private static final String servicesCategory = host + "f1/categories/%s/%s/%s/";
	private static final String servicesList = host + "f1/services/%s/%s/%s/";
	
	//старые сервисы получения галереи к магазину
	private static final String shop_image_lis="https://mobile.enter.ru/api/shops/galary/";
	private static final String shop_image_adress="https://mobile.enter.ru";
	
	//список станций метро
	//private static final String subway_list = "http://api.enter.ru/v2/subway/get/?geo_id=%s";
	private static final String subway_list = "https://mobile.enter.ru/original/v2/subway/get/?geo_id=%s";
	
	
	private static final String geo = "http://maps.google.com/maps/geo?q=";
	
	//----------------------------------USER_API-------------------------------------------------//
	private static final String CLIENT_ID = ApplicationEnter.getAppClientId();
	//http://mobile.enter.ru/original/v2/
//	private static final String templateWithToken = "https://api.enter.ru/v2/%s?token=%s&client_id=" + CLIENT_ID;
//	private static final String templateWithoutToken = "https://api.enter.ru/v2/%s?client_id=" + CLIENT_ID;
//	private static final String templateAddress = "https://api.enter.ru/v2/%s?token=%s&client_id="+ CLIENT_ID +"&id=%s";
	
	private static final String templateWithToken = "https://mobile.enter.ru/original/v2/%s?token=%s&client_id=" + CLIENT_ID;
	private static final String templateWithoutToken = "https://mobile.enter.ru/original/v2/%s?client_id=" + CLIENT_ID;
	private static final String templateAddress = "https://mobile.enter.ru/original/v2/%s?token=%s&client_id="+ CLIENT_ID +"&id=%s";

	private static final String user_auth = "user/auth";
	private static final String user_get = "user/get";
	private static final String user_delete = "user/delete";
	private static final String user_create = "user/create";
	private static final String user_update = "user/update";
	
	private static final String user_get_address = "user/get-address";
	private static final String user_create_address = "user/create-address";
	private static final String user_update_address = "user/update-address/";
	private static final String user_delete_address = "user/delete-address";
	
	private static final String order_create = "order/create";
	private static final String order_create_anonim = "order/create-anonymous";
	private static final String order_get = "order/get";
	private static final String order_cancel = "order/cancel";
	
	private static final String user_change_password = "user/change-password";
	private static final String user_reset_password = "user/reset-password";
	
	
	
	public static String getToken(String mobileORemail, String password){
		String login = mobileORemail.contains("@") ? "email=".concat(mobileORemail) : "mobile=".concat(mobileORemail);
		String pass = "password=".concat(password);
		StringBuilder builder = new StringBuilder();
		//builder.append("https://api.enter.ru/v2/").append(user_auth).append("?").append(login).append("&").append(pass).append("&").append("client_id=").append(CLIENT_ID);
		builder.append("https://mobile.enter.ru/original/v2/").append(user_auth).append("?").append(login).append("&").append(pass).append("&").append("client_id=").append(CLIENT_ID);
		return builder.toString();
	}
	
	public static String getUser(String token){
		return String.format(templateWithToken, user_get, token);
	}
	
	public static String getUserDelete(String token){
		return String.format(templateWithToken, user_delete, token);
	}
	
	public static String getUserCreate(){
		return String.format(templateWithoutToken, user_create);
	}
	
	public static String getUserGetAddress(String token){
		return String.format(templateWithToken, user_get_address, token);
	}
	
	public static String getCreateAddress(String token){
		return String.format(templateWithToken, user_create_address, token);
	}
	
	public static String getUpdateAddress(String token, long id){
		return String.format(templateAddress, user_update_address, token, id);
	}
	
	public static String getDeleteAddress(String token, long id){
		return String.format(templateAddress, user_delete_address, token, id);
	}
	
	public static String getUserUpdate(String token){
		return String.format(templateWithToken, user_update, token);
	}
	
	public static String getOrdersCreate(String token){
		return String.format(templateWithToken, order_create, token);
	}
	
	public static String getOrdersCreateAnonim(){
		//return String.format(templateWithoutToken, order_create_anonim);
		return String.format(templateWithoutToken, order_create);
	}
	
	public static String getOrders(String token){
		return String.format(templateWithToken, order_get, token);
	}
	
	public static String getOrdersCancel(String token, long id){
		return String.format(templateAddress, order_cancel, token, id);
	}
	
	public static String getUserChangePassword(String token, String oldPass, String newPass){
		return String.format(templateWithToken, user_change_password, token).concat("&password=").concat(oldPass).
		      concat("&new_password=").concat(newPass);
	}
	
	public static String getUserResetPassword(String mobileOrEmail){
		String request = mobileOrEmail.contains("@") ? "&email=".concat(mobileOrEmail) : "&mobile=".concat(mobileOrEmail);
		return String.format(templateWithoutToken, user_reset_password).concat(request);
	}
	
	//--------------------------------------------------------------------------------//
	
	public static String getShopImageList(int shopId){
		return shop_image_lis+Integer.toString(shopId)+"/";
	}
	
	public static String getShopImageAdress(String url){
		return shop_image_adress+url;
	}
	
	public static String getCategoriesList(int geo_id, int parent_id, int img_size) {
		return String.format(categories_list, geo_id, parent_id, img_size);
	}
	
	public static String getCategoriesList(int geo_id, int parent_id, String catalog_icn_size) {
		return String.format(categories_list, geo_id, parent_id, catalog_icn_size);
	}
	
	public static String getShopList(int geo_id) {
		return String.format(shop_list, geo_id);
	}
	
	public static String getCities() {
		return cities;
	}
		
	public static String getGeo(double lat, double lon, String format, String API) {
		return geo + lat +"," + lon + "&output=" +format + "&oe=utf8&sensor=false&key=" + API;
	}
	
	public static String getFilters(int geo_id, String category) {
		return String.format(filters, geo_id, category);
	}
	
	public static String getHost() {
		return host;
	}
	
	public static String getFeedBack() {
		return feed_back;
	}
	
	public static String getSearchbyfilters(int geo_id, String id) {
		return searchByFilters +geo_id +"/"+ id +"/";
	}
	
	public static String getSearchbyfilters(int geo_id, String id, int size, int page) {
		return searchByFilters + geo_id + "/" +id + "/" +size+"/"+page+"/";
	}
	
	public static String getSearchbyfiltersCount(int geo_id, String id) {
		return searchByFilters + geo_id +"/"+ id +"/count/";
	}

	public static String getDeliveryCalc() {
		return delivery_calc;
	}
	
	public static String getDeliveryCalcWithErrors() {
		return delivery_calc_with_errors;
	}

	public static String getPromo(int geo_id, String size) {
		return String.format(promo, geo_id, size);
	}
	
	public static String getPromoNew(int geo_id, String size) {
		return String.format(promoNew, geo_id, size);
	}

	public static String getProductsInfo(int geo_id, int img_size, List<Long> arrayOfId) {
		StringBuilder builder = new StringBuilder();
		for (long id: arrayOfId){
			builder.append(id).append(",");
		}
		String temp = builder.toString();
		String request = temp.substring(0, temp.length()-1);
		return String.format(products_info, geo_id, img_size, request);
	}

	public static String getProductsInCategory(int geo_id, int shop_id, int parent_id, int img_size, int limit, int page) {
		return String.format(products_in_category, geo_id, shop_id, parent_id, img_size, limit, page);
	}

	public static String getProductsCountInCategory(int geo_id, int shop_id, int parent_id) {
		return String.format(products_count_in_category, geo_id, shop_id, parent_id);
	}
	
	public static String getProductCard(int geoId, int shop_id, int productId, int imgSize){
		return String.format(productCard, geoId, shop_id, productId, imgSize);
	}

	public static String getSearchV2(int geo_id,String str, int img_size, int limit, int page) {
		//http://SERVICE_BASE_URL/api2/search/products/fulltext/GEO_ID/STROKA/[{count/}|{IMG_SIZE/[LIMIT/PAGE/]}],
		return String.format(search_v2, geo_id, str,img_size,limit,page);
	}

	public static String getSearchV2Count(int geo_id, String str) {
		return String.format(search_v2_count, geo_id, str);
	}

	public static String getProductListByIds(int geo_id, int img_size, ArrayList<Integer> ids) {
		//http://SERVICE_BASE_URL/api2/products/list/GEO_ID/IMG_SIZE/LIST/
		StringBuilder sb = new StringBuilder();
		sb.append("[");
		for(int i = 0; i < ids.size(); i++)
			sb.append(ids.get(i)).append(",");
		sb.deleteCharAt(sb.length()-1);
		sb.append("]");
		
		return String.format(product_list_by_ids,geo_id, img_size, sb.toString());
	}
	
	public static String getProductListByIds(int geo_id, int img_size, int ids) {
		return String.format(product_list_by_ids,geo_id, img_size, "["+ids+"]");
	}
	
	public static String getServiceCategories(int geo_id, int parentCategoryId, String img_size){
		return String.format(servicesCategory, geo_id, parentCategoryId, img_size);
	}
	
	public static String getServiceList(int geo_id, int categoryId, String img_size){
		return String.format(servicesList, geo_id, categoryId, img_size);
	}
	
	public static String getSearchByQR(String qrCode, int geo_id, String img_size){
		return String.format(searchByQR, qrCode, geo_id, img_size);
	}
	
//	public static String getSearchByBarCode(String barCode, int geo_id, int shop_id, String img_size){
//		return String.format(searchByBarCode, shop_id, geo_id, barCode, img_size);
//	}
	
	public static String getSearchByBarCode(String barCode, int geo_id, String img_size){
		return String.format(searchByBarCode, geo_id, barCode, img_size);
	}
	
	public static String getShop(int id) {
		return String.format(shop, id);
	}

	public static String getCatalogTree(int geo_id, int img_size) {
		return String.format(catalog_tree, geo_id, img_size);
	}
	
	public static String getServicesInfo(int geo_id, int img_size, List<Long> arrayOfId) {
		StringBuilder builder = new StringBuilder();
		for (long id: arrayOfId){
			builder.append(id).append(",");
		}
		String temp = builder.toString();
		String request = temp.substring(0, temp.length()-1);
		return String.format(services_info, geo_id, img_size, request);
	}
	
	public static String getServicesTree(int geo_id, int img_size) {
		return String.format(services_tree, geo_id, img_size);
	}

	public static String getMetro (int geo_id) {
		return String.format(subway_list, geo_id);
	}
	
}
