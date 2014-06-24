package ru.enter.parsers;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import ru.enter.beans.ShopBean;
import ru.enter.utils.Log;
import ru.enter.utils.Utils;

public class ShopsParser {
	private final String DEBUG = "Svyaznoy:ShopsParser";
	private URL feedUrl = null;

	public ShopsParser(String url) {
		try {
			feedUrl = new URL(url);
		} catch (MalformedURLException e) {
			Log.e(DEBUG, e.getMessage() + e.toString());
		}
	}

	public ArrayList<ShopBean> parse() throws IOException, JSONException {

		ShopBean bean;
		ArrayList<ShopBean> objects = new ArrayList<ShopBean>();

		URLConnection uc = feedUrl.openConnection();
		InputStream is = uc.getInputStream();
		String data = Utils.getTextFromInputStream(is);
		if (data == null)
			return new ArrayList<ShopBean>();
		JSONArray message = (JSONArray) new JSONTokener(data).nextValue();
		objects = new ArrayList<ShopBean>(message.length());
		for (int i = 0; i < message.length(); i++) {
			try{
				JSONObject item = (JSONObject) message.get(i);
				bean = new ShopBean();

				bean.setId(Integer.valueOf(item.getString("id")));
				bean.setName(item.getString("name"));
				bean.setLatitude(item.getString("latitude"));
				bean.setLongitude(item.getString("longitude"));
				// bean.setAddress(item.getString("address"));

				objects.add(bean);
			} catch (JSONException e) {
				// NOP - ignore broken item
			}
		}

		return objects;
	}

	// получение полного массива информации о магазинах (для таблетки)
	public ArrayList<ShopBean> parseFull() {

		ArrayList<ShopBean> objects = new ArrayList<ShopBean>();
		try {
			URLConnection uc = feedUrl.openConnection();
			InputStream is = uc.getInputStream();
			String data = Utils.getTextFromInputStream(is);
			if (data == null)
				return new ArrayList<ShopBean>();

			JSONArray message = (JSONArray) new JSONTokener(data).nextValue();

			objects = new ArrayList<ShopBean>(message.length());
			for (int i = 0; i < message.length(); i++) {
				try{
					JSONObject item = (JSONObject) message.get(i);
					ShopBean bean = new ShopBean();

					bean.setId(item.getInt("id"));
					bean.setName(item.getString("name"));
					bean.setAddress(item.getString("address"));
					bean.setDescription(item.getString("description"));
					bean.setPhone(item.getString("phone"));
					bean.setFoto(item.getString("foto"));
					bean.setWorking_time(item.getString("working_time"));
					bean.setLatitude(item.getString("latitude"));
					bean.setLongitude(item.getString("longitude"));
					bean.setWay_auto(item.getString("way_auto"));
					bean.setWay_walk(item.getString("way_walk"));

					objects.add(bean);
				} catch (JSONException e) {
					// NOP - ignore broken item
				}
			}
		} catch (JSONException e) {
			Log.e(DEBUG, "parse:" + e.getMessage() + e.toString());
		} catch (Exception e) {
			// if(main.D){
			Log.e(DEBUG, "parse:" + e.getMessage() + e.toString());
			// }
		}
		return objects;
	}
}
