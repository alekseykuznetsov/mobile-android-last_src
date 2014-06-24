package ru.enter.utils;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

import org.json.JSONObject;

import android.os.Handler;

public class RequestManagerThread extends Thread {
	public static final int RESULT = 0;
	public static final int ERROR = 1;
	private Handler mHandler;
	private String data;
	private String mUrl;
	private int mRequestCode;
	public RequestManagerThread(Handler handler,JSONObject requestObject,String url,int requestCode) {
		mHandler = handler;
		data = requestObject.toString();
		mUrl = url;
		mRequestCode = requestCode;
		setName("RequestManager");
		setPriority(3);
	}
	public RequestManagerThread(Handler handler,String requestObject,String url,int requestCode) {
		mHandler = handler;
		data = requestObject;
		mUrl = url;
		mRequestCode = requestCode;
		setName("RequestManager");
		setPriority(3);
	}
	@Override
	public void run() {
		String responce = sendData(data, mUrl);
		Log.d("RequestManager:responce", responce);
		if(!isInterrupted()&&mHandler!=null){
			mHandler.sendMessage(mHandler.obtainMessage(mRequestCode, responce));
		}
	}
	private HttpURLConnection getHttpsURLConnection(String surl) {
		HttpURLConnection http = null;
		try {
			URL url = new URL(surl);
			if (url.getProtocol().toLowerCase().equals("https")) {
				HttpsURLConnection https = (HttpsURLConnection) url.openConnection();
				http = https;
			} else {
				http = (HttpURLConnection) url.openConnection();
			}
		} catch (Exception e) {
			Log.e("RequestManagerThread", "getHttpsURLConnection "+e.toString()+e.getMessage());
		}
		return http;
	}
	public String sendData(String data,String url){
		StringBuilder sb = new StringBuilder();
		HttpURLConnection httppost = null;
		try{
			httppost = getHttpsURLConnection(url);			
            httppost.setDoInput(true);
            httppost.setDoOutput(true);
            httppost.setRequestMethod("POST");
            httppost.setRequestProperty("Content-Type","application/json");     
			
	        DataOutputStream dos = new DataOutputStream(httppost.getOutputStream());
	        dos.write(data.getBytes());
	        dos.flush();
	        
	        
	        BufferedReader reader = new BufferedReader(new InputStreamReader(httppost.getInputStream()));
	        String line;	           	          
	        while ((line = reader.readLine()) != null) {
	        	sb.append(line);
	        }
	        reader.close();
	        dos.close();
		}catch(Exception e){
			Log.e("RequestManagerThread", "sendData "+e.toString()+"     "+ e.getMessage());
		}finally{
			if(httppost!=null){
				
				httppost.disconnect();
			}
		}
		return sb.toString();
	}
}
