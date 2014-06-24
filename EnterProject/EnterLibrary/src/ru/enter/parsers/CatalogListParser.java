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

import ru.enter.beans.CatalogListBean;
import ru.enter.utils.Log;
import ru.enter.utils.Utils;

public class CatalogListParser {
	private final String DEBUG = "CatalogListParser";
	private URL feedUrl = null;
	public CatalogListParser(String url) {
		try {
			feedUrl = new URL(url);
		} catch (MalformedURLException e) {
			Log.e(DEBUG, e.getMessage()+e.toString());
		}
	}
	public CatalogListParser(){}//для парсинга поиска по тэгам

	public ArrayList<CatalogListBean> parse(){

		CatalogListBean bean;
		ArrayList<CatalogListBean> objects = null;
		try {
			URLConnection uc = feedUrl.openConnection();
			InputStream is = uc.getInputStream();
			String data = Utils.getTextFromInputStream(is);
			if(data == null)
				return new ArrayList<CatalogListBean>();

			JSONArray message = (JSONArray) new JSONTokener(data).nextValue();
			objects = new ArrayList<CatalogListBean>(message.length());
			for(int i = 0; i < message.length(); i++){
				try{
					JSONObject item = (JSONObject) message.get(i);
					bean = new CatalogListBean();

					bean.setId(item.getInt("id"));
					bean.setName(item.getString("name"));
					bean.setLink(item.optString("link"));
					bean.setIs_category_list(item.optBoolean("is_category_list",false));
					bean.setCount(item.getInt("count"));
					bean.setFoto(item.getString("foto"));
					bean.setIcon(item.optString("icon"));

					objects.add(bean);
				} catch (JSONException e) {
					// NOP - ignore broken item
				}
			}
		} catch (JSONException e) {
			Log.e(DEBUG, "parse:"+e.getMessage()+e.toString());
			objects = null;
		} catch (Exception e) {
			//			if(main.D){
			Log.e(DEBUG, "parse:"+e.getMessage()+e.toString());
			objects = null;
			//			}
		}
		return objects;
	}

	//		public ArrayList<CatalogListBean> parseTags(String data_to_parse){//TODO
	//			
	//			CatalogListBean bean;
	//			ArrayList<CatalogListBean> objects = new ArrayList<CatalogListBean>();
	//			
	//				try {
	//					JSONArray message = (JSONArray) new JSONTokener(data_to_parse).nextValue();
	//					objects = new ArrayList<CatalogListBean>(message.length());
	//					for(int i=0;i<message.length();i++){
	//						JSONObject item = (JSONObject) message.get(i);
	//						bean = new CatalogListBean();
	//						
	//						bean.setId(item.getInt("id"));
	//						bean.setName(item.getString("name"));
	//						bean.setLink(item.getString("link"));
	//						bean.setIs_category_list(item.optBoolean("is_category_list",false));
	//						
	//						objects.add(bean);
	//					}
	//				} catch (JSONException e) {
	//					e.toString();
	//				}	
	//				
	//			return objects;
	//	}
}
