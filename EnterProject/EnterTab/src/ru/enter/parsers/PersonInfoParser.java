package ru.enter.parsers;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import ru.enter.beans.AddressBean;
import ru.enter.beans.PersonBean;
import ru.enter.utils.HTTPUtils;

public class PersonInfoParser {

	/**
	 * 
	 * @param url
	 * @return null if exception
	 */
	public PersonBean parse(String url) {

		PersonBean result = new PersonBean();
		
		try {
			String data = HTTPUtils.getStringDataFromUrl(url);
			JSONObject res = new JSONObject(data);
			
			if(res.has("error")) {
				return result;
				
			} else {
				
				JSONObject jsonResult = res.getJSONObject("result");
				PersonBean bean = new PersonBean();
				
				bean.setId(jsonResult.getInt("id"));
				bean.setName(jsonResult.getString("first_name"));
				bean.setLastName(jsonResult.getString("last_name"));
				bean.setMiddleName(jsonResult.getString("middle_name"));
				bean.setSex(jsonResult.getString("sex"));
				bean.setBirthday(jsonResult.getString("birthday"));
				bean.setEmail(jsonResult.getString("email"));
				bean.setPhone(jsonResult.getString("phone"));
				bean.setMobile(jsonResult.getString("mobile"));
				bean.setSkype(jsonResult.getString("skype"));
				bean.setUi(jsonResult.getString("ui"));
				bean.setType_id(jsonResult.getString("type_id"));
				bean.setIsActive(jsonResult.getInt("is_active"));
				bean.setOccupation(jsonResult.getString("occupation"));
				bean.setIdentity(jsonResult.getString("identity"));
				bean.setGeo_id(jsonResult.getString("geo_id"));
				bean.setAddress(jsonResult.getString("address"));
				bean.setZip_code(jsonResult.getString("zip_code"));
				bean.setSubscribe(jsonResult.getString("is_subscribe"));
				bean.setIp(jsonResult.getString("ip"));
				bean.setLastLogin(jsonResult.getString("last_login"));
				bean.setAdded(jsonResult.getString("added"));
				bean.setUpdated(jsonResult.getString("updated"));

				bean.setAddressList(parseAddress(jsonResult.optJSONArray("address_list")));
				
				result = bean;
			}
			
		} catch (Exception e) {
			// NOP
		}
		
		return result;
	}

	/**
	 * 
	 * @param message
	 * @return new ArrayList if exception
	 */
	private ArrayList<AddressBean> parseAddress(JSONArray message) {
		
		ArrayList<AddressBean> objects = new ArrayList<AddressBean>();
		
		if (message == null)
			return objects;
		
		try {
			for (int i = 0; i < message.length(); i++) {
				JSONObject item = (JSONObject) message.get(i);
				AddressBean bean = new AddressBean();

				bean.setId(item.getInt("id"));
				bean.setFirst_name(item.getString("first_name"));
				bean.setLast_name(item.getString("last_name"));
				bean.setMiddle_name(item.getString("middle_name"));
				bean.setIs_primary(item.optInt("is_primary"));
				bean.setGeo_id(item.getInt("geo_id"));

				if (!item.getString("address").equals("null"))
					bean.setAddress(item.getString("address"));
				else
					bean.setAddress("");
				
				if (!item.getString("address_street").equals("null"))
					bean.setStreet(item.getString("address_street"));
				else
					bean.setStreet("");
				
				if (!item.getString("address_building").equals("null"))
					bean.setHouse(item.getString("address_building"));
				else
					bean.setHouse("");
				
				if (!item.getString("address_number").equals("null"))
					bean.setHousing(item.getString("address_number"));
				else
					bean.setHousing("");
				
				if (!item.getString("address_floor").equals("null"))
					bean.setFloor(item.getString("address_floor"));
				else
					bean.setFloor("");
				
				if (!item.getString("address_apartment").equals("null"))
					bean.setFlat(item.getString("address_apartment"));
				else
					bean.setFlat("");
				
				bean.setZip_code(item.getString("zip_code"));
				bean.setMobile(item.getString("mobile"));
				bean.setAdded(item.getString("added"));
				bean.setUpdated(item.optString("updated"));
				objects.add(bean);
			}
			
		} catch (JSONException e) {} 
		  catch (ClassCastException cce) {}
		
		return objects;
	}

}