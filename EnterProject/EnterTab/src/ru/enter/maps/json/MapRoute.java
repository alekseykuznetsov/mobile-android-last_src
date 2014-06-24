package ru.enter.maps.json;

import java.io.Serializable;
import java.util.List;

import com.google.gson.annotations.SerializedName;

public class MapRoute implements Serializable {

	private static final long serialVersionUID = 8455126760656957265L;
	
	@SerializedName("boundes")
	public List<MapRouteGeopoint>  boundes;
	
	
	@SerializedName("copyrights")
	public String copyrights;
	
	//@SerializedName("legs")
	public List<MapRouteLeg> legs;
	
	@SerializedName("summary")
	public String summary;
	
	
	@SerializedName("overview_polyline")
	public MapRoutePolyline polyline;
	
	//skipped warnings,waypoint_order

	
}
