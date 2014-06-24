package ru.enter.parsers;

import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import ru.enter.beans.FilterBean;
import ru.enter.beans.OptionsBean;
import ru.enter.utils.Log;
import ru.enter.utils.Utils;

public class FiltersParser {
	private final String DEBUG = "Filters";
	private URL feedUrl = null;

	public final static int FILTER_OPTIONAL = 5;
	public final static int FILTER_SOLID = 6;
	public final static int FILTER_DISCRET = 3;

	public FiltersParser(String url) {
		try {
			feedUrl = new URL(url);
		} catch (MalformedURLException e) {
			Log.e(DEBUG, e.getMessage() + e.toString());
		}
	}

	public ArrayList<FilterBean> parse () {

		FilterBean bean;
		ArrayList<FilterBean> objects = new ArrayList<FilterBean>();
		try {
			URLConnection uc = feedUrl.openConnection();
			InputStream is = uc.getInputStream();
			String data = Utils.getTextFromInputStream(is);
			try {
				JSONArray message = (JSONArray) new JSONTokener(data).nextValue();
				objects = new ArrayList<FilterBean>(message.length());
				for (int i = 0; i < message.length(); i++) {
					JSONObject item = (JSONObject) message.get(i);
					bean = new FilterBean();

					String id = item.getString("id");
					bean.setId(id);// TODO
					bean.setName(item.getString("name"));

					int type = Integer.parseInt(item.getString("type"));
					bean.setType(type);
					switch (type) {
					case FILTER_SOLID:
						float min = Float.valueOf(item.getString("min"));
						float max = Float.valueOf(item.getString("max"));
						bean.setMin((int) min);
						bean.setMax((int) max);
						break;
					case FILTER_DISCRET:
						float min_discret = Float.valueOf(item.getString("min"));
						float max_discret = Float.valueOf(item.getString("max"));
						bean.setMin((int) min_discret);
						bean.setMax((int) max_discret);
						break;
						// case case FILTER_OPTIONAL:
					default:
						JSONArray tags = item.getJSONArray("options");
						ArrayList<OptionsBean> array_fil = new ArrayList<OptionsBean>();
						for (int y = 0; y < tags.length(); y++) {
							JSONObject item2 = (JSONObject) tags.get(y);
							OptionsBean tag = new OptionsBean();
							tag.setId(item2.getInt("id"));
							tag.setName(item2.getString("name"));
							tag.setFilter_id(id);// TODO
							array_fil.add(tag);
						}
						bean.setOptions(array_fil);
						break;
					}
					objects.add(bean);
				}
			} catch (JSONException e) {
				e.toString();
			}
		} catch (Exception e) {
			Log.e(DEBUG, "parse:" + e.getMessage() + e.toString());
		}
		return objects;
	}
}
