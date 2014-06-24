package ru.enter.parsers;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import ru.enter.beans.MetroBean;
import ru.enter.utils.HTTPUtils;
import ru.enter.utils.Log;
import android.text.TextUtils;

public class MetroParser {

	private static final String TAG = "MetroParser";

	/**
	 * 
	 * @param url
	 * @return new ArrayList if exception
	 */
	public List<MetroBean> parse (String url) {

		ArrayList<MetroBean> result = new ArrayList<MetroBean>();

		try {
			String data = HTTPUtils.getStringDataFromUrl(url);

			if (TextUtils.isEmpty(data)) {
				return result;
			}

			JSONArray message = new JSONObject(data).getJSONArray("result");

			for (int i = 0; i < message.length(); i++) {
				try{
					MetroBean metro = new MetroBean();
					JSONObject object = message.getJSONObject(i);

					metro.setId(object.getInt("id"));
					metro.setName(object.getString("name"));
					metro.setGeoId(object.getInt("geo_id"));
					metro.setActive(object.getBoolean("is_active"));

					result.add(metro);
				} catch (JSONException e) {
					// NOP - ignore broken item
				}
			}

		} catch (JSONException e) {
			Log.d(TAG, e.toString());
			result.clear();
		} catch (Exception e) {
			Log.d(TAG, e.toString());
			result.clear();
		}

		return result;
	}

}
