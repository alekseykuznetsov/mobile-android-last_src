package ru.enter.parsers;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import ru.enter.utils.Log;
import android.content.Context;

public class UserAuthParser extends AbstractParser{
	private static final String TAG_EVENTS = "events";

	public UserAuthParser(Context context) {
		super(context);
	}

	/**
	 * TODO implement valid return value to handle additional response parameters. Issue blocked. I don't know if there is any
	 * reason to send this info to app flow
	 */
	@SuppressWarnings("unchecked")
	@Override
	String parseData(Object objectResult) {
		JSONObject object = (JSONObject) objectResult;
		try {
			String token = object.getString("token");

			List<Event> events = new ArrayList<Event>();
			if(object.has(TAG_EVENTS)){
				JSONArray eventsJson = object.getJSONArray(TAG_EVENTS);
				for (int i=0; i < eventsJson.length(); i++) {
					JSONObject event = eventsJson.getJSONObject(i);
					String type = event.getString("type");
					String coupon_type = event.getString("coupon_type");
					events.add(new Event(type, coupon_type));
				}
			}

			return token;
		} catch (JSONException e) {
			Log.d("USER_AUTH", e.toString());
			return null;
		}
	}





	/*
	 * 
	 "events": [
        {
            "type": "create_coupon",
            "coupon_type": "first_auth_from_mobile_app"
        },
        ...
    ]
	 * */

	// TODO get info from product owner: is this tag actual only for UserAuthResponse or not?
	class Event{
		String type;
		String coupon_type;

		public Event(String type, String coupon_type){
			this.type = type;
			this.coupon_type = coupon_type;
		}
	}
}
