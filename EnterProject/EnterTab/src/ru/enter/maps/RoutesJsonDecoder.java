package ru.enter.maps;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

import ru.enter.maps.json.MapRouteResponse;
import android.content.Context;
import android.util.Log;

import com.google.android.maps.GeoPoint;
import com.google.gson.Gson;

public class RoutesJsonDecoder {
	private static String TAG = "RoutesJSON";
	private Gson gson;

	public RoutesJsonDecoder(Context c) {
		gson = new Gson();
	}

	private <T> T getJSONResponse(String url,Class<T> cls) {
		T response = null;
		try {
			//Log.d(TAG, "URL:" + url);
			InputStream source = getStream(url);
			Reader reader = new InputStreamReader(source);
			response = gson.fromJson(reader, cls);
			//Log.d(TAG, "ANS:" + response);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		return response;
	}
	
	public MapRouteResponse getRoute(GeoPoint me,GeoPoint enter){
		String myPoint = String.format("%s,%s",String.valueOf((me.getLatitudeE6()/1e6)).replace(",","."),String.valueOf(me.getLongitudeE6()/1e6).replace(",","."));
		String enterPoint = String.format("%s,%s", String.valueOf((enter.getLatitudeE6()/1e6)).replace(",","."),String.valueOf(enter.getLongitudeE6()/1e6).replace(",","."));
		String url = "http://maps.googleapis.com/maps/api/directions/json?origin="+myPoint+"&destination="+enterPoint+"&region=ru&sensor=true&mode=driving&language=ru";
		return getJSONResponse(url,MapRouteResponse.class);
	}
	
	private InputStream getStream(String url) {
        InputStream is = null;
        try {
                URLConnection conn = new URL(url).openConnection();
                is = conn.getInputStream();
        } catch (MalformedURLException e) {
                e.printStackTrace();
        } catch (IOException e) {
                e.printStackTrace();
        }
        return is;
	}
  
}
