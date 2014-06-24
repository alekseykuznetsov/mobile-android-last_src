package ru.enter.parsers;

import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import ru.enter.beans.LabelBean;
import ru.enter.beans.ProductBean;
import ru.enter.utils.Formatters;
import ru.enter.utils.Log;
import ru.enter.utils.Utils;

public class ItemsListParser {
	private final String DEBUG = "ItemListParser";
	private URL feedUrl = null;
	public ItemsListParser(String url) {
		try {
			feedUrl = new URL(url);
		} catch (MalformedURLException e) {
			Log.e(DEBUG, e.getMessage()+e.toString());
		}
	}

	public ItemsListParser(){}

	public ArrayList<ProductBean> parse(){

		ArrayList<ProductBean> objects = new ArrayList<ProductBean>();
		try {
			URLConnection uc = feedUrl.openConnection();
			InputStream is = uc.getInputStream();
			String data = Utils.getTextFromInputStream(is);
			objects = parseTags(data);
		} catch (Exception e) {
			Log.e(DEBUG, "parse:"+e.getMessage()+e.toString());
		}
		return objects;
	}

	public ArrayList<ProductBean> parseTags (String data_to_parse){

		ProductBean bean;
		ArrayList<ProductBean> objects = new ArrayList<ProductBean>();

		try {
			JSONArray message = (JSONArray) new JSONTokener(data_to_parse).nextValue();
			objects = new ArrayList<ProductBean>(message.length());
			for(int i=0;i<message.length();i++){
				try{
					JSONObject item = (JSONObject) message.get(i);
					bean = new ProductBean();

					bean.setId(item.getInt("id"));
					bean.setShortname(item.getString("shortname"));
					bean.setAnnounce(item.optString("announce"));
					bean.setDescription(item.optString("description"));
					bean.setRating(Float.parseFloat(item.optString("rating")));
					bean.setRating_count(item.optInt("rating_count"));
					//					bean.setLink(item.getString("link"));//TODO
					bean.setPrice(item.optDouble("price"));
					bean.setPrice_old(item.optDouble("price_old", 0.0));
					bean.setFoto(item.getString("foto"));
					bean.setName(item.getString("name"));
					//					bean.setBrand(item.optString("brand"));//TODO
					bean.setPrefix(item.getString("prefix"));
					bean.setBuyable(item.getInt("is_buyable"));
					bean.setScopeShopsQty(item.getInt("scope_shops_qty"));
					bean.setScopeShopsQtyShowroom(item.getInt("scope_shops_qty_showroom"));
					bean.setScopeStoreQty(item.getInt("scope_store_qty"));
					bean.setShop(item.getInt("is_shop"));
					bean.setLabel(parseLabels(item.optJSONArray("label")));
					objects.add(bean);
				} catch (JSONException e) {
					Log.e(DEBUG, "parse:"+e.getMessage()+e.toString());
					// NOP - ignore broken item
				}
			}

		} catch (JSONException e) {
			Log.e(DEBUG, "parse:"+e.getMessage()+e.toString());
			objects.clear();
		}

		return objects;
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

}
