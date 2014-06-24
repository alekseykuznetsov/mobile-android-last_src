package ru.enter.parsers;

import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import ru.enter.utils.Log;
import ru.enter.utils.Utils;

public class GeoParser {
	private final String DEBUG = "GeoLocationParser";
	private URL feedUrl = null;
	public GeoParser(String url) {
		try {
			feedUrl = new URL(url);
		} catch (MalformedURLException e) {
			Log.e(DEBUG, e.getMessage()+e.toString());
		}
	}
	public String getCity(String data)
	{
		String str = "null";
		Pattern pat;
		Matcher mat;
		pat = Pattern.compile("\"LocalityName\"....(.+?)\"", Pattern.CASE_INSENSITIVE);
		mat = pat.matcher(data);
		if(mat.find())
			str = mat.group(1);
		
		return str;
	}
	public String parse(){
		String city = new String();
		try {
			URLConnection uc = feedUrl.openConnection();
			InputStream is = uc.getInputStream();
			String data = Utils.getTextFromInputStream(is);
			if(data.equals("null"))
				return new String();
			city = getCity(data);
		} catch (Exception e) {
//			if(main.D){
				Log.e(DEBUG, "parse:"+e.getMessage()+e.toString());
//			}
		}
		return city;
	}
}
