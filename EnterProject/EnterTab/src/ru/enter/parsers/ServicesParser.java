package ru.enter.parsers;

import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Scanner;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import ru.enter.beans.ServiceBean;
import ru.enter.beans.ServiceCategoryBean;
import ru.enter.data.ServicesNode;

public class ServicesParser {
	
	
	public ArrayList<ServicesNode> parse(String url) {

		ArrayList<ServicesNode> result = null;
		
		try {
			URLConnection uc = new URL(url).openConnection();			
			Scanner scanner = new Scanner(uc.getInputStream());
			String data = scanner.useDelimiter("\\A").next();
			JSONArray rootf1 = (JSONArray) new JSONTokener(data).nextValue();
			JSONObject roots = rootf1.getJSONObject(0);
			result = parse(roots.getJSONArray("children"), null);
			
		} catch (Exception e) {
			e = null;
		}
		
		return result;
	}
	
	
	private ArrayList<ServicesNode> parse(JSONArray childrenJSON, ServicesNode parentNode){
		
		ArrayList<ServicesNode> result = null;
		
		try {
			for (int childCounter = 0; childCounter < childrenJSON.length(); childCounter ++){
				JSONObject childObject = childrenJSON.getJSONObject(childCounter);
				
				//парсим наполнение для узла
				ServiceCategoryBean bean = new ServiceCategoryBean();
				bean.setId(childObject.getInt("id"));
				bean.setName(childObject.getString("name"));
				bean.setCategory_list(childObject.optBoolean("is_category_list",false));
				bean.setFoto(childObject.getString("foto"));
				
				//создаем узел
				ServicesNode node = new ServicesNode();
				//наполняем узел данными
				node.setNode(bean);
				
				// устанавливаем родителя
				node.setParent(parentNode);
				
				//если это категория услуг
				if(bean.isCategory_list()){
					JSONArray childrenArray = childObject.getJSONArray("children");
					//то рекурсивно парсим
					ArrayList<ServicesNode> childrenNodes = parse(childrenArray, node);
					//и устанавливаем детей для узла
					node.setChildren(childrenNodes);
				}				
				//  если это список услуг
				else{
					JSONArray servicesArray = childObject.getJSONArray("services");
					//то парсим список услуг
					ArrayList<ServiceBean> servicesNodes = parseSevices(servicesArray, node);
					//и добавляем в узел
					node.setServices(servicesNodes);
				}
				
				//добавляем к списку возвращаемых детей у parent
				if(result == null) result = new ArrayList<ServicesNode>();
				result.add(node);
			}
			
		} catch (JSONException e) {}
		
		return result;
	}
	
	private ArrayList<ServiceBean> parseSevices(JSONArray servicesJSON, ServicesNode parentNode){  
		

		ArrayList<ServiceBean> service = new ArrayList<ServiceBean>();
				
		try {
			for (int servicesCounter = 0; servicesCounter < servicesJSON.length(); servicesCounter ++){
				JSONObject servicesObject = servicesJSON.getJSONObject(servicesCounter);
				
				ServiceBean currentService = new ServiceBean();		
				currentService.setDescription(servicesObject.getString("description"));
				currentService.setFoto(servicesObject.getString("foto"));
				currentService.setName(servicesObject.getString("name"));		
				currentService.setPrice(servicesObject.getInt("price"));
				currentService.setWork(servicesObject.getString("work"));
				currentService.setId(servicesObject.getInt("id"));
				
				currentService.setMinSumCostToDeliver(servicesObject.getInt("min_sum_cost_to_deliver"));
				currentService.setDelivery(servicesObject.getBoolean("is_delivery"));
				currentService.setPricePercent(servicesObject.getInt("price_percent"));
				currentService.setPriceTypeId(servicesObject.getInt("price_type_id"));
				currentService.setPriceMin(servicesObject.getInt("price_min"));
				currentService.setInShop(servicesObject.getBoolean("is_in_shop"));
         
				service.add(currentService);
			
			}
		} catch (JSONException e) {}
	
		return service;
		
	}
}
