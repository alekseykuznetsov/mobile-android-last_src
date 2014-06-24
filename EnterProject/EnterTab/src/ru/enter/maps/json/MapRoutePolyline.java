package ru.enter.maps.json;

import java.io.Serializable;

import com.google.gson.annotations.SerializedName;

public class MapRoutePolyline implements Serializable {

	private static final long serialVersionUID = 8455126760656957265L;

	@SerializedName("points")
	public String points;
	
	
	@SerializedName("levels")
	public String levels;

	
}
