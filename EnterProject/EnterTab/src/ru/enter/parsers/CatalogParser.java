package ru.enter.parsers;

import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Scanner;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import ru.enter.beans.CatalogListBean;
import ru.enter.data.CatalogNode;

public class CatalogParser {
	
	
	public ArrayList<CatalogNode> parse(String url) {

		ArrayList<CatalogNode> result = null;
		
		try {
			URLConnection uc = new URL(url).openConnection();
			Scanner scanner = new Scanner(uc.getInputStream());
			String data = scanner.useDelimiter("\\A").next();
			JSONArray roots = (JSONArray) new JSONTokener(data).nextValue();
			result = parse(roots);
			
		} catch (Exception e) {}
		
		return result;
	}
	
	
	private ArrayList<CatalogNode> parse(JSONArray childrenJSON){
		
		ArrayList<CatalogNode> result = null;
		
		try {
			for (int childCounter = 0; childCounter < childrenJSON.length(); childCounter ++){
				JSONObject childObject = childrenJSON.getJSONObject(childCounter);
				
				//парсим наполнение для узла
				CatalogListBean bean = new CatalogListBean();
				bean.setId(childObject.getInt("id"));
				bean.setName(childObject.getString("name"));
				bean.setLink(childObject.getString("link"));
				bean.setIs_category_list(childObject.optBoolean("is_category_list",false));
				bean.setCount(childObject.getInt("count"));
				bean.setFoto(childObject.getString("foto"));
				
				if (childObject.has("icon")){
					bean.setIcon(childObject.getString("icon"));
					
					JSONArray iconSizeArray = childObject.getJSONArray("icon_sizes");
					ArrayList<String> sizes = new ArrayList<String>();
					for (int i = 0; i < iconSizeArray.length(); i ++){
						String size = (String) iconSizeArray.get(i);
						sizes.add(size);
					}
					bean.setIconSize(sizes);
				}
				
				//создаем узел
				CatalogNode node = new CatalogNode();
				//наполняем узел данными
				node.setNode(bean);
				
				//если это категория товаров
				if(bean.isIs_category_list()){
					JSONArray childrenArray = childObject.getJSONArray("children");
					//то рекурсивно парсим
					ArrayList<CatalogNode> childrenNodes = parse(childrenArray);
					//и устанавливаем детей для узла
					node.setChildren(childrenNodes);
				}
				
				//добавляем к списку возвращаемых детей у parent
				if(result == null) result = new ArrayList<CatalogNode>();
				result.add(node);
			}
			
		} catch (JSONException e) {}
		
		return result;
	}
}
