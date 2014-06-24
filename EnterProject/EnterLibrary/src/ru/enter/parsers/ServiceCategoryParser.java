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

import ru.enter.beans.ServiceCategoryBean;
import ru.enter.utils.Log;
import ru.enter.utils.Utils;

public class ServiceCategoryParser {
	private final String DEBUG = "ServicesListParser";
	private URL feedUrl = null;
	public ServiceCategoryParser(String url) {
		try {
			feedUrl = new URL(url);
		} catch (MalformedURLException e) {
			Log.e(DEBUG, e.getMessage()+e.toString());
		}
	}

	public ArrayList<ServiceCategoryBean> parse(){

		ServiceCategoryBean bean;
		ArrayList<ServiceCategoryBean> objects = new ArrayList<ServiceCategoryBean>();
		try {
			URLConnection uc = feedUrl.openConnection();
			InputStream is = uc.getInputStream();
			String data = Utils.getTextFromInputStream(is);
			if(data == null)
				return new ArrayList<ServiceCategoryBean>();

			JSONArray message = (JSONArray) new JSONTokener(data).nextValue();
			objects = new ArrayList<ServiceCategoryBean>(message.length());
			for(int i=0;i<message.length();i++){
				try{
					JSONObject item = (JSONObject) message.get(i);
					bean = new ServiceCategoryBean();

					bean.setId(item.getInt("id"));
					bean.setName(item.getString("name"));
					bean.setFoto(item.getString("foto"));
					bean.setCategory_list(item.optBoolean("is_category_list"));

					objects.add(bean);
				} catch (JSONException e) {
					// NOP - ignore broken item
				}	
			}
		} catch (JSONException e) {
			e.toString();
		}	catch (Exception e) {
			Log.e(DEBUG, "parse:"+e.getMessage()+e.toString());
		}
		return objects;
	}
}
