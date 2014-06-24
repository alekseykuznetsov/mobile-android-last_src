//package ru.enter.parsers;
//
//import org.json.JSONException;
//import org.json.JSONObject;
//import org.json.JSONTokener;
//
//import ru.enter.utils.ResponceServerException;
///**класс для обработки ответа сервена на отправку заказа*/
//public class CheckoutParser {
//	private String mData = null;
//	public CheckoutParser(String data) {
//		mData = data;
//	}
//
//	public boolean parse() throws ResponceServerException, JSONException {
//		JSONObject object = (JSONObject) new JSONTokener(mData).nextValue();
//		try {
//			String confirmed = object.getString("confirmed");
//			if (confirmed.equalsIgnoreCase("true")) {
//				return true;
//			}
//		} catch (JSONException e) {
//			JSONObject errorDesc = object.getJSONObject("error");
//			String msg = errorDesc.getString("message");
//			int code = errorDesc.getInt("code");
//			ResponceServerException exception = new ResponceServerException();
//			exception.setErrorCode(code);
//			exception.setErrorMsg(msg);
//			throw exception;
//		}
//		return false;
//	}
//}
