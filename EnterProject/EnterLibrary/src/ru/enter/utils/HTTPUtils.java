package ru.enter.utils;

import java.io.InputStream;
import java.net.URL;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONObject;

public class HTTPUtils {
	
	/**
	 * Посылает объект JSON на сервер. Получает ответ в виде строки
	 * @param url куда шлем
	 * @param json что шлем
	 * @return ответ сервера
	 * @throws Exception
	 */
	public static String sendPostJSON (String url, JSONObject json) throws Exception {
		
		//создаем httpclient для отправки запроса
	    DefaultHttpClient httpclient = new DefaultHttpClient();

	    //url с post data
	    HttpPost httpost = new HttpPost(url);

	    //готовим данные для отправки
	    StringEntity entity = new StringEntity(json.toString());

	    //ставим данные для отправки
	    httpost.setEntity(entity);
	    
	    //устанавливает заголовок запроса, чтобы приемник мог понять что делать с данными
	    httpost.setHeader("Content-type", "application/json");

	    //отправляем запрос
	    HttpResponse serverResponse = httpclient.execute(httpost);
	    
	    //получаем ответ, берем поток
	    InputStream serverIS = serverResponse.getEntity().getContent();
	    
	    //конвертируем в строку
	    String result = Converters.inputStreamToString(serverIS);
	    
	    return result;
	}
	
	/**
	 * Получает строку-ответ от сервера
	 * @param url откуда получаем
	 * @return ответ сервера
	 * @throws Exception
	 */
	public static String getStringDataFromUrl (String url) throws Exception{
		
		URL httpUrl = new URL(url);
		InputStream serverIS = httpUrl.openStream();
		return Converters.inputStreamToString(serverIS);
	}

}
