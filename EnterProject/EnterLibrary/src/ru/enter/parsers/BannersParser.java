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

import ru.enter.beans.BannerBean;
import ru.enter.utils.Log;
import ru.enter.utils.URLManager;
import ru.enter.utils.Utils;

public class BannersParser {
	private final String DEBUG = "BannersParser";

	public ArrayList<BannerBean> parseData(String url){	
		ArrayList<BannerBean> array = new ArrayList<BannerBean>();
		BannerBean bean = new BannerBean();
		try {
			URL feedUrl = new URL(url);
			URLConnection uc = feedUrl.openConnection();
			InputStream is = uc.getInputStream();
			String data = Utils.getTextFromInputStream(is);
			if(data == null) return null;

			JSONArray message = (JSONArray)new JSONTokener(data).nextValue();
			for (int i = 0; i< message.length(); i++){
				try{
					JSONObject item = (JSONObject) message.get(i);
					bean = new BannerBean();
					JSONArray product_ids_array = item.getJSONArray("product_ids");
					ArrayList<Integer> product_ids = new ArrayList<Integer>();
					for(int j = 0; j< product_ids_array.length();j++)
						product_ids.add(product_ids_array.getInt(j));
					bean.setProduct_ids(product_ids);
					bean.setId(item.getInt("id"));
					bean.setName(item.getString("name"));
					bean.setPhotos(item.getString("foto"));
					array.add(bean);
				} catch (JSONException e) {
					// NOP - ignore broken item
				}
			}

		} catch (JSONException e) {
			Log.d(DEBUG, e.toString());
			return null;
		} catch (Exception e) {
			Log.d(DEBUG, e.toString());
			return null;
		}
		return array;
	}

	public ArrayList<BannerBean> parseDataNew(String url){	
		ArrayList<BannerBean> array = new ArrayList<BannerBean>();
		BannerBean bean = new BannerBean();
		try {
			URL feedUrl = new URL(url);
			URLConnection uc = feedUrl.openConnection();
			InputStream is = uc.getInputStream();
			String data = Utils.getTextFromInputStream(is);
			if(data == null) return null;

			JSONArray message = (JSONArray)new JSONTokener(data).nextValue();
			for (int i = 0; i<message.length(); i++){
				JSONObject item = (JSONObject) message.get(i);
				bean = new BannerBean();
				if(item.has("product_ids")){
					JSONArray product_ids_array = item.getJSONArray("product_ids");
					ArrayList<Integer> product_ids = new ArrayList<Integer>();
					for(int j = 0; j< product_ids_array.length();j++)
						product_ids.add(product_ids_array.getInt(j));
					bean.setProduct_ids(product_ids);
				} else bean.setProduct_ids(null);
				bean.setId(item.getInt("id"));
				bean.setName(item.getString("name"));
				bean.setPhotos(item.getString("foto"));
				bean.setUrl(item.optString("url"));
				array.add(bean);
			}

		} catch (JSONException e) {
			Log.d(DEBUG, e.toString());
			return null;
		} catch (Exception e) {
			Log.d(DEBUG, e.toString());
			return null;
		} 

		return array;
	}
}
