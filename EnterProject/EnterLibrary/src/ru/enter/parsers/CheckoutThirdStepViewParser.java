package ru.enter.parsers;

import org.json.JSONException;
import org.json.JSONObject;

import ru.enter.utils.Log;
import android.content.Context;

public class CheckoutThirdStepViewParser extends AbstractParser{

	public CheckoutThirdStepViewParser(Context context) {
		super(context);
	}

	@SuppressWarnings("unchecked")
	@Override
	public String parseData(Object objectResult) {
		JSONObject object = (JSONObject) objectResult;
		try {
			//остальные поля игнорим пока
			String confirmed = object.getString("confirmed");
			if (confirmed.equalsIgnoreCase("true")) {
				String number = object.getString("number");
				return number;
			}
		}catch (JSONException e) {
			Log.d("SendOrder", e.toString());
		}	
		return null;
	}
}
