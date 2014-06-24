package ru.enter.parsers;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import ru.enter.utils.ResponceServerException;


public class RegistrationParser {
	private String mData = null;
	public RegistrationParser(String data) {
		mData = data;
	}
	public long parse() throws ResponceServerException, JSONException{
		long id = 0;
		JSONObject object = (JSONObject) new JSONTokener(mData).nextValue();
		try {	
			JSONObject result = object.getJSONObject("result");
			id = result.optLong("id", 0);
		} catch (JSONException e) {
			JSONObject errorDesc = object.getJSONObject("error");
			String msg = errorDesc.getString("message");
			int code = errorDesc.getInt("code");
			ResponceServerException exception = new ResponceServerException();
			exception.setErrorCode(code);
			exception.setErrorMsg(msg);
			throw exception;
		}
		return id;
	}
}
