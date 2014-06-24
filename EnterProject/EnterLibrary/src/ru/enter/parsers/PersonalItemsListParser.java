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
//import ru.enter.beans.ProductBean;
//import ru.enter.utils.Log;
//import ru.enter.utils.URLManager;
//import ru.enter.utils.Utils;
//
//public class PersonalItemsListParser {
//	private final String DEBUG = "ItemListParser";
//	private URL feedUrl = null;
//	public PersonalItemsListParser(String url) {
//		try {
//			feedUrl = new URL(url);
//		} catch (MalformedURLException e) {
//			Log.e(DEBUG, e.getMessage()+e.toString());
//		}
//	}
//
//	public PersonalItemsListParser(){}
//
//	public ArrayList<ProductBean> parse(){
//
//		ArrayList<ProductBean> objects = new ArrayList<ProductBean>();
//		try {
//			URLConnection uc = feedUrl.openConnection();
//			InputStream is = uc.getInputStream();
//			String data = Utils.getTextFromInputStream(is);
//			objects = parseTags(data);
//		} catch (Exception e) {
//			Log.e(DEBUG, "parse:"+e.getMessage()+e.toString());
//		}
//		return objects;
//	}
//
//	public ArrayList<ProductBean> parseTags (String data_to_parse){
//
//		ProductBean bean;
//		ArrayList<ProductBean> objects = new ArrayList<ProductBean>();
//
//		try {
//			JSONArray message = null;
//			Object object = new JSONTokener(data_to_parse).nextValue();
//			if(object instanceof JSONObject){
//				JSONObject jsonObject = (JSONObject)object;
//				message = jsonObject.getJSONArray("products");
//			}else if(object instanceof JSONArray){
//				message = (JSONArray)object;
//			}
//			objects = new ArrayList<ProductBean>(message.length());
//			for(int i=0;i<message.length();i++){
//				JSONObject item = (JSONObject) message.get(i);
//				bean = new ProductBean();
//
//				bean.setId(item.getInt("id"));
//				bean.setName(item.getString("name"));
//				bean.setAnnounce(item.getString("announce"));
//				bean.setDescription(item.getString("description"));
//				//					bean.setRating(item.optDouble("rating"));
//				//					bean.setTagline(item.getString("tagline"));
//				bean.setLink(item.getString("link"));
//				bean.setPrice(Double.parseDouble(item.get("price").toString()));
//				//					bean.setPrice_old(Double.parseDouble(item.get("price_old").toString()));
//
//				JSONArray fotos = item.getJSONArray("fotos");
//				if(fotos != null){
//
//					ArrayList<PhotoBean> images = new ArrayList<PhotoBean>();
//					PhotoBean image;
//
//					for(int j = 0; j < fotos.length();j++){
//
//						JSONObject images_obj = (JSONObject) fotos.get(j);
//						image = new PhotoBean();
//
//						image.setSize(images_obj.getInt("size"));
//						JSONArray  images_arr = images_obj.getJSONArray("images");
//
//						StringBuilder photos = new StringBuilder();							
//						for(int k = 0;k<images_arr.length();k++){
//							String photoURL = URLManager.getHost() + images_arr.getString(k);
//							photos.append(photoURL);
//							if(images_arr.length()>=1 && k!=images_arr.length()-1){
//								photos.append("\n");
//							}
//						}
//
//						image.setImages(photos.toString());
//						images.add(image);
//						image = null;
//
//					}
//					bean.setMain_fotos(images);
//					images = null;
//				}
//
//				objects.add(bean);
//				bean = null;
//				System.gc();
//			}
//		} catch (JSONException e) {
//			Log.e(DEBUG, "parse:"+e.getMessage()+e.toString());
//		}catch (Exception e) {
//			Log.e(DEBUG, "parse:"+e.getMessage()+e.toString());
//		}
//		System.gc();
//		return objects;
//	}
//
//}
