package ru.enter.parsers;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import ru.enter.utils.ResponceServerException;

public class UserAuthorizationParser {

	public String parse(String result) throws ResponceServerException{
		JSONObject nextValue = null;
		try{
			nextValue = (JSONObject) new JSONTokener(result).nextValue();
			return nextValue.getJSONObject("result").optString("token");
		}catch (JSONException e) {
			try {
				pushWarning(nextValue.getJSONObject("error"));
			} catch (JSONException noElements) {
				return null;
			}
		}
		return null; 
	}

	private void pushWarning(JSONObject error) throws ResponceServerException{
		try {
			String msg = error.getString("message");
			int code = error.getInt("code");
			
			ResponceServerException exception = new ResponceServerException();
			exception.setErrorCode(code);
			exception.setErrorMsg(msg);
			throw exception;
			
		}catch (JSONException jex) {
		}	
	}
}		
		
		