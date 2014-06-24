package ru.enter.maps.json;

import java.io.Serializable;
import java.util.List;

import com.google.gson.annotations.SerializedName;

public class MapRouteLeg implements Serializable {

	private static final long serialVersionUID = 8455126760656957265L;
	
	@SerializedName("distance")
	public MapRouteTextValueItem distance;


	@SerializedName("duration")
	public MapRouteTextValueItem duration;
	

	@SerializedName("end_adress")
	public String endAddress;
	
	@SerializedName("end_location")
	public MapRouteGeopoint endLocation;
	
	@SerializedName("start_adress")
	public String startAddress;
	
	@SerializedName("start_location")
	public MapRouteGeopoint startLocation;
	
	//@SerializedName("steps")
	public List<MapRouteStep> steps;
	
	//skipped via_waypoint
	
}
