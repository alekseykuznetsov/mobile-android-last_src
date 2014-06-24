package ru.enter.parsers;

import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import ru.enter.beans.ShopBean;
import ru.enter.utils.Log;
import ru.enter.utils.Utils;

public class ShopParser {
	private final String DEBUG = "ShopParser";
	private URL feedUrl = null;
	public ShopParser(String url) {
		try {
			feedUrl = new URL(url);
		} catch (MalformedURLException e) {
			Log.e(DEBUG, e.getMessage()+e.toString());
		}
	}
	public ShopBean parse(){
		
		ShopBean bean = new ShopBean();
		try {
			URLConnection uc = feedUrl.openConnection();
			InputStream is = uc.getInputStream();
			String data = Utils.getTextFromInputStream(is);
			if(data != null){
				try {
					JSONObject item = (JSONObject) new JSONTokener(data).nextValue();
						
						bean.setId(item.getInt("id"));
						bean.setName(item.getString("name"));
						bean.setAddress(item.getString("address"));
						bean.setDescription(item.getString("description"));
						bean.setPhone(item.getString("phone"));
						bean.setFoto(item.getString("foto"));
						bean.setWorking_time(item.optString("working_time"));
						bean.setLatitude(item.getString("latitude"));
						bean.setLongitude(item.getString("longitude"));
						bean.setWay_auto(item.optString("way_auto"));
						bean.setWay_walk(item.optString("way_walk"));
						
						
				} catch (JSONException e) {
					e.toString();
				}
			}
		} catch (Exception e) { }
		
		return bean;
	}
}
