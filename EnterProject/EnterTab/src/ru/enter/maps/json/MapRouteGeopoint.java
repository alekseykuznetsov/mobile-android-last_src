package ru.enter.maps.json;

import java.io.Serializable;

import com.google.gson.annotations.SerializedName;

public class MapRouteGeopoint implements Serializable {

	private static final long serialVersionUID = 8455126760656957265L;
	
	@SerializedName("lat")
	public Double  lat;
	
	@SerializedName("lng")
	public Double lng;
	
}
