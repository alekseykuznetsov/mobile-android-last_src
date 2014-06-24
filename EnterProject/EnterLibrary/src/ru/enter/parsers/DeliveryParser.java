package ru.enter.parsers;

import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import ru.enter.beans.DeliveryBean;
import ru.enter.beans.ErrorBean;
import ru.enter.utils.Utils;
import ru.enter.utils.Wrapper;

public class DeliveryParser {
	private final String DEBUG = "DeliveryParser";

	public Wrapper<DeliveryBean> parse (String url) {

		Wrapper<DeliveryBean> wrapper = new Wrapper<DeliveryBean>();
		try {
			URL feedUrl = new URL(url);
			URLConnection uc = feedUrl.openConnection();
			InputStream is = uc.getInputStream();
			String data = Utils.getTextFromInputStream(is);
			if (data != null)
				wrapper = parseData(data);

		} catch (Exception e) {
		}

		return wrapper;
	}

	public Wrapper<DeliveryBean> parseData (String data_to_parse) {// TODO

		Wrapper<DeliveryBean> wrapper = new Wrapper<DeliveryBean>();
		try {
			JSONObject root = (JSONObject) new JSONTokener(data_to_parse).nextValue();

			if (root.has("result")) {
				ArrayList<DeliveryBean> objects = new ArrayList<DeliveryBean>(root.length());
				JSONArray message = (JSONArray) root.getJSONArray("result");
				for (int i = 0; i < message.length(); i++) {
					JSONObject item = (JSONObject) message.get(i);
					DeliveryBean bean = new DeliveryBean();

					bean.setDate(item.getString("date"));
					bean.setPrice(item.getInt("price"));
					bean.setMode_id(item.getInt("mode_id"));
					bean.setName(item.getString("name"));

					objects.add(bean);
				}
				wrapper.setResult(objects);
			} else if (root.has("core_errors")) {
				JSONArray message = (JSONArray) root.getJSONArray("core_errors");
				JSONObject item = (JSONObject) message.get(0);
				ErrorBean bean = new ErrorBean();

				bean.setMessage(item.getString("message"));
				bean.setCode(item.getInt("code"));
				wrapper.setError(bean);
			}
		} catch (JSONException e) {
			e.toString();
		} catch (ClassCastException e) {
			// TODO: handle exception
		}

		return wrapper;
	}
}
