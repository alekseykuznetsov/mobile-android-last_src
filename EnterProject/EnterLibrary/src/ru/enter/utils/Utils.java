package ru.enter.utils;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.URL;
import java.net.URLConnection;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import ru.enter.beans.PhotoBean;
import ru.ideast.enter.R;
import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.location.Location;
import android.util.DisplayMetrics;
import android.util.FloatMath;
import android.util.Pair;
import android.util.TypedValue;
import android.view.Display;

/**
 * Utils
 * 
 * @author ideast
 * 
 */
public class Utils {
	public static boolean CITY_CHANGED_FLAG = false;
	private static final String DEBUG_GEO_EMELYANENKO = "0cYaUHrcSeIolaZh8ZUepoJkQTcGECicUTV6GCQ";
	private static final String DEBUG_GEO_EMELYANENKO_2 = "0m7l1f2rKZaotlxYxNlhZVlSp7tbLJ9jkWFHBFw";
	private static final String DEBUG_GEO_MEREZHKO = "0oS2dSL_guUhFFDS8_8N1vZpAdX6ca036Pm14RA";
	private static final String RELEASE_KEY = "0m7l1f2rKZaofWsd-KRYjxS-hJ_iZZJvIoPpr1Q";
	private static final ArrayList<Pair<Integer, Integer>> demensions;
	
	
	static{
	 		demensions = new ArrayList<Pair<Integer,Integer>>();
	 		demensions.add(new Pair<Integer, Integer>(240, 95));
	 		demensions.add(new Pair<Integer, Integer>(320, 126));
	 		demensions.add(new Pair<Integer, Integer>(360, 219));
	 		demensions.add(new Pair<Integer, Integer>(480, 189));
	 		demensions.add(new Pair<Integer, Integer>(640, 252));
	 		demensions.add(new Pair<Integer, Integer>(750, 295));
	 		demensions.add(new Pair<Integer, Integer>(768, 302));
	 		Collections.reverse(demensions);
	 	}

	public static void CopyStream(InputStream is, OutputStream os) {
		final int buffer_size = 1024;
		try {
			byte[] bytes = new byte[buffer_size];
			for (;;) {
				int count = is.read(bytes, 0, buffer_size);
				if (count == -1)
					break;
				os.write(bytes, 0, count);
			}
		} catch (Exception ex) {
		}
	}

	public static String getTextFromInputStream(InputStream is) {
		StringBuilder sb = new StringBuilder();
		try {
			BufferedReader reader = new BufferedReader(
					new InputStreamReader(is));
			String line;
			while ((line = reader.readLine()) != null) {
				sb.append(line);
				System.gc();
			}
			reader.close();
			return sb.toString();
		} catch (Exception e) {
			// if(isLog){
			// Log.e(DEBUG, "getHtmlFromUrl():"+e.toString()+e.getMessage());
		}
		return null;
	}
	
	public static int dp2pix(Context context, float dp){
		return (int)(dp * context.getResources().getDisplayMetrics().density);
	}
	
	public static Typeface getTypeFace(Context context) {
		return Typeface.createFromAsset(context.getAssets(), "EnterBrash.otf");
	}

	public static Typeface getRoubleTypeFace(Context context) {
		return Typeface.createFromAsset(context.getAssets(), "rouble.otf");
	}
	
	public static Typeface getTahomaTypeFace(Context context) {
		return Typeface.createFromAsset(context.getAssets(), "tahoma.ttf");
	}

	public static int getColor(Context context, int colorId) {
		return context.getResources().getColor(colorId);
	}

	public static String getGeoApi() {
		return RELEASE_KEY;
	}
 
	public String getLocalIpAddress() {
		try {
			for (Enumeration<NetworkInterface> en = NetworkInterface
					.getNetworkInterfaces(); en.hasMoreElements();) {
				NetworkInterface intf = en.nextElement();
				for (Enumeration<InetAddress> enumIpAddr = intf
						.getInetAddresses(); enumIpAddr.hasMoreElements();) {
					InetAddress inetAddress = enumIpAddr.nextElement();
					if (!inetAddress.isLoopbackAddress()) {
						return inetAddress.getHostAddress().toString();
					}
				}
			}
		} catch (SocketException ex) {
			Log.e(getClass().toString(), ex.toString());
		}
		return null;
	}

	public static BitmapDrawable imageRotate(Context context, int degres) {
		Bitmap bmp = BitmapFactory.decodeResource(context.getResources(),
				R.drawable.arr_white);
		int w = bmp.getWidth();
		int h = bmp.getHeight();
		Matrix mtx = new Matrix();
		mtx.postRotate(90);
		Bitmap rotatedBMP = Bitmap.createBitmap(bmp, 0, 0, w, h, mtx, true);
		return new BitmapDrawable(rotatedBMP);
	}

	public static float gps2m(float lat_a, float lng_a, Location loc) {

		if (loc == null)
			return 0;

		float lat_b = (float) loc.getLatitude();
		float lng_b = (float) loc.getLongitude();
		float pk = (float) (180 / Math.PI);

		float a1 = lat_a / pk;
		float a2 = lng_a / pk;
		float b1 = lat_b / pk;
		float b2 = lng_b / pk;

		float t1 = FloatMath.cos(a1) * FloatMath.cos(a2) * FloatMath.cos(b1)
				* FloatMath.cos(b2);
		float t2 = FloatMath.cos(a1) * FloatMath.sin(a2) * FloatMath.cos(b1)
				* FloatMath.sin(b2);
		float t3 = FloatMath.sin(a1) * FloatMath.sin(b1);
		double tt = Math.acos(t1 + t2 + t3);

		double itog = 6366000 * tt;

		return (float) itog;
	}
	public static String gps2m_formated(float lat_a, float lng_a, Location loc) {

		if (loc == null)
			return "";

		float lat_b = (float) loc.getLatitude();
		float lng_b = (float) loc.getLongitude();
		float pk = (float) (180 / Math.PI);

		float a1 = lat_a / pk;
		float a2 = lng_a / pk;
		float b1 = lat_b / pk;
		float b2 = lng_b / pk;

		float t1 = FloatMath.cos(a1) * FloatMath.cos(a2) * FloatMath.cos(b1)
				* FloatMath.cos(b2);
		float t2 = FloatMath.cos(a1) * FloatMath.sin(a2) * FloatMath.cos(b1)
				* FloatMath.sin(b2);
		float t3 = FloatMath.sin(a1) * FloatMath.sin(b1);
		double tt = Math.acos(t1 + t2 + t3);

		double itog = 6366000 * tt;
		
        DecimalFormat df = new DecimalFormat("#.##");
        
		return 	itog>1000? df.format(itog/1000) + " км": df.format(itog) + " м";
		
	}
	
    private static HttpURLConnection getHttpsURLConnection(String surl) {
 		HttpURLConnection http = null;
 		try {
 			URL url = new URL(surl);
 			if (url.getProtocol().toLowerCase().equals("https")) {
 				//trustAllHosts();
 				HttpsURLConnection https = (HttpsURLConnection) url.openConnection();
 				http = https;
 			} else {
 				http = (HttpURLConnection) url.openConnection();
 			}
 		} catch (Exception e) {
 			Log.e("Utils", "getHttpsURLConnection "+e.toString()+e.getMessage());
 		}
 		return http;
 	}
     
 	public static String sendPostData (String data, String url){
 		StringBuilder sb = new StringBuilder();
 		HttpURLConnection httppost = null;
 		try{
 			httppost = getHttpsURLConnection(url);			
             httppost.setDoInput(true);
             httppost.setDoOutput(true);
             httppost.setRequestMethod("POST");
             //httppost.setRequestProperty("Content-Type","application/json");     
             httppost.setRequestProperty("Content-Type","application/x-www-form-urlencoded");
 			
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
 			Log.e("Utils", "sendData "+e.toString()+"     "+ e.getMessage());
 		}finally{
 			if(httppost!=null){
 				
 				httppost.disconnect();
 			}
 		}
 		return sb.toString();
 	}
 	
 	 private static void trustAllHosts() {
 		  TrustManager[] trustAllCerts = new TrustManager[] {new X509TrustManager() {

 		   @Override
 		   public void checkClientTrusted(java.security.cert.X509Certificate[] chain, String authType)
 		     throws java.security.cert.CertificateException {

 		   }

 		   @Override
 		   public void checkServerTrusted(java.security.cert.X509Certificate[] chain, String authType)
 		     throws java.security.cert.CertificateException {

 		   }

 		   @Override
 		   public java.security.cert.X509Certificate[] getAcceptedIssuers() {
 		    return null;
 		   }

 		  } };

 		  HttpsURLConnection.setDefaultHostnameVerifier(new HostnameVerifier() {
 		   public boolean verify(String hostname, SSLSession session) {
 		    return true;
 		   }
 		  });
 		  
 		  try {
 		   SSLContext sc = SSLContext.getInstance("TLS");
 		   sc.init(null, trustAllCerts, new java.security.SecureRandom());
 		   HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
 		  } 
 		  catch (Exception e) {}
	 }
 	
 	public static String getDataFromServer (String surl){
 		String request = "";
		try {
			URL url = new URL(surl);
			URLConnection uc = url.openConnection();
			InputStream is = uc.getInputStream();
			request = Utils.getTextFromInputStream(is);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return request;
 	}
 	/**
 	 * Возвращает урл фоты по позиции, автоматом
 	 * @param bean 
 	 * @param context
 	 * @param position
 	 * @return String
 	 */
 	public static String getPhoto(ArrayList<PhotoBean> bean, Context context, int position){
 		Display d = ((Activity)context).getWindowManager().getDefaultDisplay();
 		int max = d.getWidth()<d.getHeight()?d.getHeight():d.getWidth();
 		int num = 0;
 		for(int i = bean.size()-1;i>=0;i--)
 			if(bean.get(i).getSize()>=max) 
 				num = i;
 		
		return bean.get(num).getImages().get(position);
 	}
 	/**
 	 * Возвращает урл фоты по позиции, по необходимому размеру
 	 * @param bean
 	 * @param size
 	 * @param position
 	 * @return String
 	 */
 	public static String getPhoto(ArrayList<PhotoBean> bean, int size, int position){
 		int num = 0;
 		for(int i = bean.size()-1;i>=0;i--)
 			if(bean.get(i).getSize()>=size) 
 				num = i;
 		
		return bean.get(num).getImages().get(position);
 	}
 	/**
 	 * Возвращает номер номер необходиомго разрешения, автоматом
 	 * @param bean
 	 * @param context
 	 * @return int
 	 */
 	
 	public static int getPhoto(ArrayList<PhotoBean> bean, Context context){
 		Display d = ((Activity)context).getWindowManager().getDefaultDisplay();
 		int max = d.getWidth()<d.getHeight()?d.getHeight():d.getWidth();
 		int num = 0;
 		for(int i = bean.size()-1;i>=0;i--)
 			if(bean.get(i).getSize()>=max) 
 				num = i;
 		
		return num;
 	}
 	/**
 	 * возвращает номер необходимого разрешения, по размеру
 	 * @param bean
 	 * @param size
 	 * @return int
 	 */
 	public static int  getPhoto(ArrayList<PhotoBean> bean, int size){
 		int num = 0;
 		for(int i = bean.size()-1;i>=0;i--)
 			if(bean.get(i).getSize()>=size) 
 				num = i;
 		
		return num;
 	}
 	/**
 	 * Вычисляем размер специально для списка товаров
 	 * @param context
 	 * @return
 	 */
 	public static int getDpiForItemList(Context context){
 		Display d = ((Activity)context).getWindowManager().getDefaultDisplay();
 		DisplayMetrics dm = new DisplayMetrics();
 		d.getMetrics(dm);
 		
 		switch (dm.densityDpi) {
		case DisplayMetrics.DENSITY_LOW:
			return 60;
		case DisplayMetrics.DENSITY_MEDIUM:
			return 60;
		case DisplayMetrics.DENSITY_HIGH:
			return 120;
		}
		return 120;
 		
 	}
 	public static String getLabelSize(Context context){
 		Display d = ((Activity)context).getWindowManager().getDefaultDisplay();
 		DisplayMetrics dm = new DisplayMetrics();
 		d.getMetrics(dm);
 		
 		switch (dm.densityDpi) {
		case DisplayMetrics.DENSITY_LOW:
		case DisplayMetrics.DENSITY_MEDIUM:
			return "66x23";
		case DisplayMetrics.DENSITY_HIGH:
			return "124x38";
		}
		return "124x38";
 		
 	}
 	
 	public static int getSizeForMainImg(Context context){
 		Display d = ((Activity)context).getWindowManager().getDefaultDisplay();
 		DisplayMetrics dm = new DisplayMetrics();
 		d.getMetrics(dm);
 		
 		switch (dm.densityDpi) {
		case DisplayMetrics.DENSITY_HIGH:
			return 350;
		case DisplayMetrics.DENSITY_MEDIUM:
			return 160;
		case DisplayMetrics.DENSITY_LOW:
			return 120;
		}
		return 350;		
 	}
 	public static int getSizeForSearchListImg(Context context){
 		Display d = ((Activity)context).getWindowManager().getDefaultDisplay();
 		DisplayMetrics dm = new DisplayMetrics();
 		d.getMetrics(dm);
// 		'60', '120', '160', '163', '200', '350', '500', '550', '1500', '2500'
 		switch (dm.densityDpi) {
		case DisplayMetrics.DENSITY_HIGH:
			return 200;
		case DisplayMetrics.DENSITY_MEDIUM:
			return 160;
		case DisplayMetrics.DENSITY_LOW:
			return 120;
		}
		return 200;		
 	}
 	
 	public static boolean isEmptyList(List<?> list){
 		return (list==null || list.isEmpty());
 	}
 	
 	public static String useIfNotNull (String string) {
 		return string == null ? "" : string;
 	}
 	
 	
 	public static String getSizesForBanner(Pair<Integer, Integer> pair){
 		StringBuilder builder = new StringBuilder();
 		for (Pair<Integer, Integer> dimension : demensions){
 			if(pair.first>=dimension.first && pair.second>=dimension.second){
 				return builder.append(dimension.first).append("x").append(dimension.second).toString();
 			}
 		}
 		return null;
 	}
 	
 	public static String parseURLfromJSONString(Context context, String source){
 		int resolution;
 		DisplayMetrics metrics = context.getResources().getDisplayMetrics();
 		switch (metrics.densityDpi) {
		case DisplayMetrics.DENSITY_LOW:
			resolution = 120; 
			break;
		case DisplayMetrics.DENSITY_MEDIUM:
			resolution = 160; 
			break;
		case DisplayMetrics.DENSITY_HIGH:
			resolution = 200; 
			break;

		default:
			resolution = 200;
			break;
		}
 		
 		String x = source.replaceAll("\\\\", "");
 		Pattern pattern = Pattern.compile("/static_media/upload/products/main/\\d+/"+resolution+"/\\d+.jpg");
        Matcher matcher = pattern.matcher(x);
        String newString = matcher.find()? x.substring(matcher.start(), matcher.end()) : "";
 		
		return newString;
 	}
}