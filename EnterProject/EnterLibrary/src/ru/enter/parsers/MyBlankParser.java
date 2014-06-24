package ru.enter.parsers;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import ru.enter.beans.AddressBean;
import ru.enter.utils.Log;
import android.content.Context;

public class MyBlankParser extends AbstractParser {
	private final String DEBUG = "Svyaznoy:MyBlankParser";

	public MyBlankParser(Context context) {
		super(context);
	}

	@Override
	@SuppressWarnings ("unchecked")
	public ArrayList<AddressBean> parseData (Object objectResult) {
		if (objectResult == null)
			return new ArrayList<AddressBean>();
		ArrayList<AddressBean> objects = new ArrayList<AddressBean>();
		try {
			JSONArray message = (JSONArray) objectResult;// TODO
			objects = new ArrayList<AddressBean>(message.length());
			for (int i = 0; i < message.length(); i++) {
				try{
					JSONObject item = (JSONObject) message.get(i);
					AddressBean bean = new AddressBean();

					bean.setId(item.getInt("id"));
					bean.setFirst_name(item.getString("first_name"));
					bean.setLast_name(item.getString("last_name"));
					bean.setMiddle_name(item.getString("middle_name"));
					bean.setIs_primary(item.optInt("is_primary"));
					bean.setGeo_id(item.getInt("geo_id"));

					bean.setAddress(formatIfNull(item.optString("address")));
					bean.setStreet(formatIfNull(item.optString("address_street")));
					bean.setHouse(formatIfNull(item.optString("address_building")));
					bean.setHousing(formatIfNull(item.optString("address_number")));
					bean.setFloor(formatIfNull(item.optString("address_floor")));
					bean.setFlat(formatIfNull(item.optString("address_apartment")));

					bean.setZip_code(item.getString("zip_code"));
					bean.setMobile(item.getString("mobile"));
					bean.setAdded(item.getString("added"));
					bean.setUpdated(item.optString("updated"));
					objects.add(bean);
				} catch (JSONException e) {
					Log.e(DEBUG, e.toString());
					// NOP - ignore broken item
				}
			}
		} catch (ClassCastException cce) {
			Log.d("MyBlank", "no addresses");
			return new ArrayList<AddressBean>();
		}
		return objects;
	}

	private String formatIfNull(String string){
		return "null".equals(string) ? "" : string;
	}
}
