package ru.enter.maps.json;

import java.io.Serializable;

import com.google.gson.annotations.SerializedName;

public class MapRouteStep implements Serializable {

	private static final long serialVersionUID = 8455126760656957265L;
	
	@SerializedName("distance")
	public MapRouteTextValueItem distance;

	@SerializedName("duration")
	public MapRouteTextValueItem duration;
		
	@SerializedName("end_location")
	public MapRouteGeopoint endLocation;
	
	@SerializedName("start_location")
	public MapRouteGeopoint startLocation;
	
	@SerializedName("html_instructions")
	public String htmlInstructions;
	
	@SerializedName("travel_mode")
	public String travelMode;
	
	//skipped polyline
	
}
