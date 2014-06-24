package ru.enter.parsers;

import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import ru.enter.beans.DeliveryBean;
import ru.enter.beans.LabelBean;
import ru.enter.beans.ModelProductBean;
import ru.enter.beans.OptionArrayBean;
import ru.enter.beans.OptionBean;
import ru.enter.beans.ProductBean;
import ru.enter.beans.ProductModelBean;
import ru.enter.beans.ServiceBean;
import ru.enter.beans.ShopBean;
import ru.enter.utils.Log;
import ru.enter.utils.Utils;

public class ProductInfoParser {
	
	private final String DEBUG = "ProductInfoParser";
	private URL feedUrl = null;
	
	public ProductInfoParser(String url) {
		try {
			feedUrl = new URL(url);
		} catch (MalformedURLException e) {
			Log.e(DEBUG, e.getMessage()+e.toString());
		}
	}
	
	public ProductInfoParser(){}
	
	public ProductBean parse(){
		ProductBean bean = new ProductBean();
		try {
			URLConnection uc = feedUrl.openConnection();
			InputStream is = uc.getInputStream();
			String data = Utils.getTextFromInputStream(is);
			if(data == null)
				return new ProductBean();
			bean = parseData(data);
		} catch (Exception e) {
			Log.e(DEBUG, "parse:"+e.getMessage()+e.toString());
		}
		return bean;
	}
	
	public ProductBean parseData(String data){
		ProductBean bean = new ProductBean();
		try {
			JSONObject item = new JSONObject(data);
				bean.setId(item.getInt("id"));
				bean.setName(item.getString("name"));
				bean.setShortname(item.getString("shortname"));
				bean.setAnnounce(item.getString("announce"));
				bean.setBrand(item.optString("brand"));
				bean.setDescription(item.getString("description"));
				bean.setPrice(item.optDouble("price"));
				bean.setPrice_old(item.optDouble("price_old", 0.0));
				bean.setArticle(item.optString("article"));
				bean.setFoto(item.getString("foto"));
				bean.setLink(item.getString("link"));
				bean.setPrefix(item.getString("prefix"));
				bean.setRating(Float.parseFloat(item.optString("rating")));
				bean.setRating_count(item.getInt("rating_count"));
				bean.setBuyable(item.getInt("is_buyable"));
				bean.setScopeShopsQty(item.getInt("scope_shops_qty"));
				bean.setScopeShopsQtyShowroom(item.getInt("scope_shops_qty_showroom"));
				bean.setScopeStoreQty(item.getInt("scope_store_qty"));
				bean.setShop(item.getInt("is_shop"));
				if(item.has("model"))
					bean.setModelsProduct(parceModels(item.optJSONObject("model")));
				
				bean.setOptions(parseOptions(item.optJSONObject("options")));
				bean.setDelivery_mod(parseDelivery(item.optJSONArray("delivery_mod")));
				bean.setGallery(parseGallery(item.optJSONArray("gallery")));
				bean.setGallery_3d(parseGallery(item.optJSONArray("gallery_3d")));
				bean.setShop_list(parseShop(item.optJSONArray("shop_list")));
				bean.setAccessories(parseAccecories(item.optJSONArray("accessories")));
				bean.setRelated(parseAccecories(item.optJSONArray("related")));
				bean.setServices(parseServices(item.optJSONArray("services")));
				bean.setLabel(parseLabels(item.optJSONArray("label")));
				
		} catch (JSONException e) {
			Log.e(DEBUG, "parseData"+e.getMessage()+"   "+e.toString());
		}
		
		return bean;
	}
	private ArrayList<LabelBean> parseLabels(JSONArray labels){
		try {
			ArrayList<LabelBean> beans = new ArrayList<LabelBean>(labels.length());
			for(int i = 0; i<labels.length(); i++){
				JSONObject pr_bean = (JSONObject) labels.get(i);
				LabelBean bean = new LabelBean();
				bean.setId(pr_bean.getInt("id"));
				bean.setName(pr_bean.getString("name"));
				bean.setFoto(pr_bean.optString("foto"));
				beans.add(bean);
			}
			return beans;
		} catch (JSONException e) {
			return new ArrayList<LabelBean>();
		}
	}
	private ArrayList<ServiceBean> parseServices(JSONArray services){
		try {
			ArrayList<ServiceBean> beans = new ArrayList<ServiceBean>(services.length());
			for(int i = 0; i<services.length(); i++){
				JSONObject pr_bean = (JSONObject) services.get(i);
				ServiceBean bean = new ServiceBean();
				bean.setId(pr_bean.getInt("id"));
				bean.setName(pr_bean.getString("name"));
				bean.setPrice(pr_bean.optInt("price"));
				bean.setFoto(pr_bean.getString("foto"));
				bean.setWork(pr_bean.getString("work"));
				beans.add(bean);
			}
			return beans;
		} catch (JSONException e) {
			return new ArrayList<ServiceBean>();
		}
	}
	private ArrayList<ProductBean> parseAccecories(JSONArray acc){
		try {
			ArrayList<ProductBean> beans = new ArrayList<ProductBean>(acc.length());
			for(int i = 0; i<acc.length(); i++){
				JSONObject pr_bean = (JSONObject) acc.get(i);
				ProductBean bean = new ProductBean();
				bean.setId(pr_bean.getInt("id"));
				bean.setName(pr_bean.getString("name"));
				bean.setShortname(pr_bean.getString("shortname"));
				bean.setAnnounce(pr_bean.getString("announce"));
				bean.setDescription(pr_bean.getString("description"));
				bean.setPrice(pr_bean.optDouble("price"));
				bean.setPrice_old(pr_bean.optDouble("price_old"));
				bean.setFoto(pr_bean.getString("foto"));
				bean.setPrefix(pr_bean.getString("prefix"));
				bean.setRating(Float.parseFloat(pr_bean.optString("rating")));
				bean.setRating_count(pr_bean.getInt("rating_count"));
				bean.setBuyable(pr_bean.getInt("is_buyable"));
				bean.setLabel(parseLabels(pr_bean.optJSONArray("label")));
				beans.add(bean);
			}
			return beans;
		} catch (JSONException e) {
			return new ArrayList<ProductBean>();
		}
	}
	private ArrayList<ShopBean> parseShop(JSONArray shop_list){
		try {
			ArrayList<ShopBean> shops = new ArrayList<ShopBean>(shop_list.length());
			for(int i = 0; i<shop_list.length(); i++){
				JSONObject shop_bean = (JSONObject) shop_list.get(i);
				ShopBean shop = new ShopBean();
				shop.setId(shop_bean.getInt("id"));
				shop.setName(shop_bean.getString("name"));
				shop.setLongitude(shop_bean.getString("longitude"));
				shop.setLatitude(shop_bean.getString("latitude"));
				shop.setAddress(shop_bean.getString("address"));
				shop.setStick(shop_bean.getInt("stick"));
				shop.setQuantity(shop_bean.getInt("quantity"));
				shop.setQuantityShowroom(shop_bean.getInt("quantity_showroom"));
				
				shops.add(shop);
			}
			return shops;
		} catch (JSONException e) {
			return new ArrayList<ShopBean>();
		}
	}
	private ArrayList<String> parseGallery(JSONArray gallery){
		try {
			ArrayList<String> gallery_img = new ArrayList<String>(gallery.length());
			for(int i = 0;i<gallery.length();i++){
				gallery_img.add((String) gallery.get(i));
			}
			return gallery_img;
		} catch (JSONException e) {
			return new ArrayList<String>();
		}
	}
	private ArrayList<DeliveryBean> parseDelivery(JSONArray delivery){
		try {
			ArrayList<DeliveryBean> deliveries = new ArrayList<DeliveryBean>(delivery.length());
			for(int i = 0;i<delivery.length();i++){
				JSONObject del_bean = (JSONObject) delivery.get(i);
				DeliveryBean delivbean = new DeliveryBean();
				delivbean.setDate(del_bean.getString("date"));
				delivbean.setPrice(del_bean.optInt("price"));
				delivbean.setMode_id(del_bean.getInt("mode_id"));
				delivbean.setName(del_bean.getString("name"));
				
				deliveries.add(delivbean);
			}
			return deliveries;
		} catch (JSONException e) {
			return new ArrayList<DeliveryBean>();
		}
	}
	
	private ArrayList<ModelProductBean> parceModels(JSONObject models_obj){
		try {
			JSONArray property = models_obj.getJSONArray("property");
			ArrayList<ModelProductBean> models_beans = new ArrayList<ModelProductBean>(property.length());
			ModelProductBean modelBean = null;			
			for(int i = 0;i<property.length();i++){
				modelBean = new ModelProductBean();
				JSONObject model = property.getJSONObject(i);
				modelBean.setValue(model.getString("value"));
				modelBean.setProperty(model.getString("property"));
				modelBean.setOption(model.getString("option"));
				modelBean.setUnit(model.getString("unit"));
				
				JSONArray products = model.getJSONArray("products");
				ArrayList<ProductModelBean> productsList = new ArrayList<ProductModelBean>(products.length());
				
				for(int j = 0; j< products.length();j++){
					JSONObject item = products.optJSONObject(j);
					ProductModelBean modl = new ProductModelBean();
					ProductBean product = new ProductBean();
					modl.setValue(item.getString("value"));
					JSONObject itm = item.getJSONObject("product");
					product.setRating(Float.parseFloat(itm.optString("rating")));
					product.setFoto(itm.getString("foto"));
					product.setName(itm.getString("name"));
					product.setScopeStoreQty(itm.getInt("scope_store_qty"));
					product.setPrice(itm.optDouble("price"));
					product.setPrice_old(itm.optDouble("price_old"));
					product.setPrefix(itm.getString("prefix"));					
					product.setShop(itm.getInt("is_shop"));
					product.setBuyable(itm.getInt("is_buyable"));
					product.setScopeShopsQtyShowroom(itm.getInt("scope_shops_qty_showroom"));
					product.setAnnounce(itm.getString("announce"));
					product.setShortname(itm.getString("shortname"));
					product.setScopeShopsQty(itm.getInt("scope_shops_qty"));
					product.setId(itm.getInt("id"));
					product.setDescription(itm.getString("description"));
					product.setLabel(parseLabels(itm.optJSONArray("label")));					
					modl.setProductBean(product);
					productsList.add(modl);
				}
				
				modelBean.setProducts(productsList);
				models_beans.add(modelBean);
			}
			return models_beans;
		} catch (JSONException e) {
			return new ArrayList<ModelProductBean>();
		}
	}
	
	
	
	private ArrayList<OptionArrayBean> parseOptions(JSONObject options_obj){
		try {
			ArrayList<OptionArrayBean> options_beans = new ArrayList<OptionArrayBean>(options_obj.length());
			OptionArrayBean options_bean = null;
			JSONArray options_names = options_obj.names();
			
			for(int i = 0;i<options_names.length();i++){
				options_bean = new OptionArrayBean();
				String name = options_names.getString(i);
				options_bean.setName(name);
				
				JSONArray options_arr = options_obj.getJSONArray(name);
				ArrayList<OptionBean> option_beans = new ArrayList<OptionBean>(options_arr.length());
				
				for(int j = 0; j< options_arr.length();j++){
					JSONObject options = options_arr.optJSONObject(j);
					OptionBean option = new OptionBean();
					option.setOption(options.getString("option"));
					option.setValue(options.getString("value"));
					option.setUnit(options.getString("unit"));
					option.setProperty(options.getString("property"));
					option_beans.add(option);
				}
				
				options_bean.setOption(option_beans);
				options_beans.add(options_bean);
			}
			return options_beans;
		} catch (JSONException e) {
			return new ArrayList<OptionArrayBean>();
		}
	}
}
