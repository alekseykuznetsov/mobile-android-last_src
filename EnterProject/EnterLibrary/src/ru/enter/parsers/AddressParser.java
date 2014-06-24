package ru.enter.parsers;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
public class AddressParser extends AbstractParser{
	
	public AddressParser(Context context) {
			super(context);
	}

	@SuppressWarnings("unchecked")
	@Override
	Integer parseData(Object objectResult) {
		JSONObject object = (JSONObject) objectResult;
			String confirmed = object.optString("confirmed");
			if (confirmed.equalsIgnoreCase("true")) {
				//0 - если это создание нового адреса
				return object.optInt("id", 0);
			}
		
		return -1;
	}
}
