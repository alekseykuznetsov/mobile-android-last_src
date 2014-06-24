//package ru.enter.parsers;
//
//import java.io.InputStream;
//import java.net.MalformedURLException;
//import java.net.URL;
//import java.net.URLConnection;
//import java.util.ArrayList;
//
//import org.json.JSONArray;
//import org.json.JSONException;
//import org.json.JSONObject;
//import org.json.JSONTokener;
//
//import ru.enter.beans.PhotoBean;
//import ru.enter.utils.Log;
//import ru.enter.utils.URLManager;
//import ru.enter.utils.Utils;
//
//public class Pseudo3DParser {
//	private final String DEBUG = "Pseudo3D";
//	private URL feedUrl = null;
//	public Pseudo3DParser(String url) {
//		try {
//			feedUrl = new URL(url);
//		} catch (MalformedURLException e) {
//			Log.e(DEBUG, e.getMessage()+e.toString());
//		}
//	}
//	public ArrayList<PhotoBean> parse(){
//		
//		PhotoBean bean;
//		ArrayList<PhotoBean> objects = null;
//		try {
//			URLConnection uc = feedUrl.openConnection();
//			InputStream is = uc.getInputStream();
//			String data = Utils.getTextFromInputStream(is);
//			if(data.equals("null"))
//				return new ArrayList<PhotoBean>();
//			try {
//				JSONArray message = (JSONArray) new JSONTokener(data).nextValue();
//				objects = new ArrayList<PhotoBean>(message.length());
//				for(int i=0;i<message.length();i++){
//					bean = new PhotoBean();
//					JSONObject images_obj = message.getJSONObject(i);
//					bean.setSize(images_obj.getInt("size"));
//					
//					ArrayList<String> images = new ArrayList<String>();					
//					JSONArray images_arr = images_obj.getJSONArray("images");
//					
//					StringBuilder photos = new StringBuilder();							
//					for(int k = 0;k<images_arr.length();k++){
//						String photoURL = URLManager.getHost() + images_arr.getString(k);
//						photos.append(photoURL);
//						if(images_arr.length()>=1 && k!=images_arr.length()-1){
//							photos.append("\n");
//						}
//					}
//						
//					bean.setImages(photos.toString());
//					
//					objects.add(bean);
//				}
//			} catch (JSONException e) {
//				e.toString();
//			}	
//		} catch (Exception e) {
////			if(main.D){
//				Log.e(DEBUG, "parse:"+e.getMessage()+e.toString());
////			}
//		}
//		return objects;
//	}
//}
