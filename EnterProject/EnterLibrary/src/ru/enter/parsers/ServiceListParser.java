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

import ru.enter.beans.ServiceBean;
import ru.enter.utils.Log;
import ru.enter.utils.Utils;

public class ServiceListParser {
	private final String DEBUG = "ServiceCardParser";
	private URL feedUrl = null;

	public ServiceListParser(String url) {
		try {
			feedUrl = new URL(url);
		} catch (MalformedURLException e) {
			Log.e(DEBUG, e.getMessage()+e.toString());
		}
	}

	public ArrayList<ServiceBean> parse() {
		ArrayList<ServiceBean> beans = new ArrayList<ServiceBean>();
		try {
			URLConnection uc = feedUrl.openConnection();
			InputStream is = uc.getInputStream();
			String data = Utils.getTextFromInputStream(is);
			if(data == null) return beans;

			JSONArray message = (JSONArray)new JSONTokener(data).nextValue();
			for (int i = 0; i < message.length(); i++){
				try{
					JSONObject item = (JSONObject) message.get(i);
					ServiceBean bean = new ServiceBean();
					bean.setId(item.getInt("id"));
					bean.setName(item.getString("name"));
					bean.setPrice((int)item.getDouble("price"));
					bean.setDescription(item.getString("description"));
					bean.setWork(item.getString("work"));
					bean.setFoto(item.getString("foto"));
					bean.setMinSumCostToDeliver(item.getInt("min_sum_cost_to_deliver"));
					bean.setDelivery(item.getBoolean("is_delivery"));
					bean.setPricePercent(item.getInt("price_percent"));
					bean.setPriceTypeId(item.getInt("price_type_id"));
					bean.setPriceMin(item.getInt("price_min"));
					bean.setInShop(item.getBoolean("is_in_shop"));
					beans.add(bean);
				} catch (JSONException e) {
					// NOP - ignore broken item
				}	
			}

		} catch (Exception e) {
			return null;
		}
		return beans;
	}
}
