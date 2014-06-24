package ru.enter.parsers;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;


import ru.enter.beans.ShopImgBean;
import ru.enter.utils.Log;
import ru.enter.utils.Utils;

public class ShopImgParser {

	private final String DEBUG = "Svyaznoy:ShopsParser";
	private URL feedUrl = null;

	public ShopImgParser(String url) {
		try {
			feedUrl = new URL(url);
		} catch (MalformedURLException e) {
			Log.e(DEBUG, e.getMessage() + e.toString());
		}
	}

	public List<ShopImgBean> parse() throws IOException, JSONException {

		ShopImgBean bean;
		List<ShopImgBean> objects = new ArrayList<ShopImgBean>();

		URLConnection uc = feedUrl.openConnection();
		InputStream is = uc.getInputStream();
		String data = Utils.getTextFromInputStream(is);
		if (data == null)
			return new ArrayList<ShopImgBean>();

		JSONArray message = (JSONArray) new JSONTokener(data).nextValue();
		objects = new ArrayList<ShopImgBean>(message.length());
		for (int i = 0; i < message.length(); i++) {
			try{
				JSONObject item = (JSONObject) message.get(i);
				bean = new ShopImgBean();
				JSONArray list= item.getJSONArray("images");
				ArrayList<String> listURL=new ArrayList<String>(); 
				for(int j=0;j<list.length();j++)
					listURL.add(list.getString(j));

				bean.setImages(listURL);
				bean.setSize(item.getInt("size"));

				objects.add(bean);
			} catch (JSONException e) {
				// NOP - ignore broken item
			}
		}

		return objects;
	}

}
