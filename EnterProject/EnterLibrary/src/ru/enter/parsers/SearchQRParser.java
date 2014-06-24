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

import ru.enter.beans.PhotoBean;
import ru.enter.beans.ProductBean;
import ru.enter.utils.Log;
import ru.enter.utils.URLManager;
import ru.enter.utils.Utils;

public class SearchQRParser {
	private final String DEBUG = "SearchQRParser";
	private URL feedUrl = null;
	public SearchQRParser(String url) {
		try {
			feedUrl = new URL(url);
		} catch (MalformedURLException e) {
			Log.e(DEBUG, e.getMessage()+e.toString());
		}
	}

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
			JSONArray message = null;
			Object object = new JSONTokener(data_to_parse).nextValue();
			if(object instanceof JSONObject){
				JSONObject jsonObject = (JSONObject)object;
				message = jsonObject.getJSONArray("products");
			}else if(object instanceof JSONArray){
				message = (JSONArray)object;
			}
			objects = new ArrayList<ProductBean>(message.length());
			for(int i=0;i<message.length();i++){
				try{
					JSONObject item = (JSONObject) message.get(i);
					bean = new ProductBean();

					bean.setId(item.getInt("id"));
					bean.setShortname(item.getString("shortname"));
					bean.setAnnounce(item.getString("announce"));
					bean.setDescription(item.getString("description"));
					bean.setRating(Float.parseFloat(item.optString("rating")));
					bean.setRating_count(item.optInt("rating_count"));
					bean.setPrice(item.optDouble("price"));
					bean.setPrice_old(item.optDouble("price_old", 0.0));
					bean.setFoto(item.getString("foto"));
					bean.setName(item.getString("name"));
					bean.setPrefix(item.getString("prefix"));
					bean.setBuyable(item.getInt("is_buyable"));

					objects.add(bean);
				} catch (JSONException e) {
					Log.e(DEBUG, "parse:"+e.getMessage()+e.toString());
					// NOP - ignore broken item
				}
			}
		} catch (JSONException e) {
			Log.e(DEBUG, "parse:"+e.getMessage()+e.toString());
		} catch (Exception e) {
			Log.e(DEBUG, "parse:"+e.getMessage()+e.toString());
		}
		return objects;
	}

}
