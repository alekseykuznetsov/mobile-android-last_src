package ru.enter.parsers;

import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import ru.enter.beans.CitiesBean;
import ru.enter.utils.Log;
import ru.enter.utils.Utils;

public class CitiesParser {
	private final String DEBUG = "CitiesParser";
	private URL feedUrl = null;
	public CitiesParser(String url) {
		try {
			feedUrl = new URL(url);			
		} catch (MalformedURLException e) {
			Log.e(DEBUG, e.getMessage()+e.toString());
		}	
	}

	private TrustManager[] trustAllCerts = new TrustManager[]{
			new X509TrustManager() {
				public java.security.cert.X509Certificate[] getAcceptedIssuers() {
					return null;
				}
				public void checkClientTrusted(
						java.security.cert.X509Certificate[] certs, String authType) {
				}
				public void checkServerTrusted(
						java.security.cert.X509Certificate[] certs, String authType) {
				}
			}	
	};

	public ArrayList<CitiesBean> parse(){

		CitiesBean bean;
		ArrayList<CitiesBean> objects = new ArrayList<CitiesBean>();
		try {
			//			SSLContext sc = SSLContext.getInstance("TLS"); 
			//		    sc.init(null, trustAllCerts, new java.security.SecureRandom()); 
			//		    HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory()); 
			//			HttpsURLConnection uc = (HttpsURLConnection) feedUrl.openConnection();
			//			InputStream is = uc.getInputStream();
			//			String data = Utils.getTextFromInputStream(is);
			URLConnection uc = feedUrl.openConnection();
			InputStream is = uc.getInputStream();
			String data = Utils.getTextFromInputStream(is);
			if(data.equals("null"))
				return objects;

			JSONArray message = (JSONArray) new JSONTokener(data).nextValue();
			objects = new ArrayList<CitiesBean>(message.length());
			for(int i=0;i<message.length();i++){
				try {
					JSONObject item = (JSONObject) message.get(i);
					bean = new CitiesBean();

					bean.setHasShop(item.getBoolean("has_shop"));
					bean.setId(item.getInt("id"));
					bean.setName(item.getString("name"));

					objects.add(bean);
				} catch (JSONException e) {
					// NOP - ignore broken item
				}	
			}
		} catch (JSONException e) {
			e.toString();
			objects.clear();
		} catch (Exception e) {
			Log.e(DEBUG, "parse:"+e.getMessage()+e.toString());
			objects.clear();
		}
		return objects;
	}
}
