package ru.enter.parsers;

import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import ru.enter.utils.Log;
import ru.enter.utils.Utils;

public class SearchCountParser {
	private final String DEBUG = "SearchCountParser";
	private URL feedUrl = null;
	public SearchCountParser(String url) {
		try {
			feedUrl = new URL(url);
		} catch (MalformedURLException e) {
			Log.e(DEBUG, e.getMessage()+e.toString());
		}
	}

	public SearchCountParser(){}

	public int parse(){

		try {
			URLConnection uc = feedUrl.openConnection();
			InputStream is = uc.getInputStream();
			String data = Utils.getTextFromInputStream(is);
			return parseTags(data);
		} catch (Exception e) {
			Log.e(DEBUG, "parse:"+e.getMessage()+e.toString());
		}
		return 0;
	}

	public int parseTags (String data_to_parse){

		try {
			JSONObject message = (JSONObject) new JSONTokener(data_to_parse).nextValue();
			return message.getInt("count");

		} catch (JSONException e) {
			Log.e(DEBUG, "parse:"+e.getMessage()+e.toString());
		}catch (Exception e) {
			Log.e(DEBUG, "parse:"+e.getMessage()+e.toString());
		}

		return 0;
	}

}
