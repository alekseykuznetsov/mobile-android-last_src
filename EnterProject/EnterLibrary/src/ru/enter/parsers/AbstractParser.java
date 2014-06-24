package ru.enter.parsers;

import java.io.InputStream;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import ru.enter.utils.Log;
import ru.enter.utils.ResponceServerException;
import ru.enter.utils.Utils;
import android.app.Activity;
import android.content.Context;
import android.widget.Toast;

public abstract class AbstractParser {
	public static final String TAG = "AbstarctParser";
	private Context mContext;
	private Runnable runAuth;
	
	
	public AbstractParser(Context context){
		mContext = context;
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
	
	/**
	 * Обычный метод парсинга из урла
	 * @param url источник
	 * @return нужные нам объекты
	 */
	public <T> T parse (String url){
		try {
			URL feedUrl = new URL(url);
			//SSLContext sc = SSLContext.getInstance("TLS");			
		    //sc.init(null, trustAllCerts, new java.security.SecureRandom()); 
		    //HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory()); 
			HttpsURLConnection uc = (HttpsURLConnection) feedUrl.openConnection();
			InputStream is = uc.getInputStream();
			String data = Utils.getTextFromInputStream(is);
			//этот метод можно вызывать и отдельно
			return parseString(data);
		}catch (Exception e) {
			Log.d(TAG, e.toString());
		}
		return null;
	}
	
	/**
	 * Парсит строку, полученную до этого
	 * @param source строка-источник
	 * @return нужные нам объекты
	 */
	public <T> T parseString (String source){
		try {
			return parseData(getResult(source));
		} catch (final ResponceServerException e) {
			//выброшено сообщение о токене
			Log.d(TAG, e.toString());
			if (e.getErrorCode() == 402){
				//ActivityManager.authorizate(mContext);
				runAuth.run();
			}else{
				((Activity) mContext).runOnUiThread(new Runnable() {
				    public void run() {
				        Toast.makeText(mContext, e.toString(), Toast.LENGTH_SHORT).show();
				    }
				});
			}
		}
		return null;
	}
	
	
	private Object getResult(String data) throws ResponceServerException{
		JSONObject nextValue = null;
		try{
			nextValue = (JSONObject) new JSONTokener(data).nextValue();
			return nextValue.get("result");
		}catch (JSONException e) {
			try {
				pushWarning(nextValue.getJSONObject("error"));
			} catch (JSONException noElements) {
				//непонятно, нет ни ошибки, ни результата
				return new Object();
			}
		}
		return data;
	}
	
	abstract <T> T parseData (Object objectResult);
	
	private void pushWarning(JSONObject error) throws ResponceServerException{
			try {
				String msg = error.getString("message");
				int code = error.getInt("code");
				
				ResponceServerException exception = new ResponceServerException();
				exception.setErrorCode(code);
				exception.setErrorMsg(msg);
				throw exception;
				
			}catch (JSONException jex) {
				//не понятно что делать в этом случае
			}	
			
		}
}
