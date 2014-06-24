package ru.enter.maps.json;

import java.io.Serializable;
import java.util.List;

import com.google.gson.annotations.SerializedName;

public class MapRouteResponse implements Serializable {

	private static final long serialVersionUID = 8455126760656957265L;
	
	@SerializedName("status")
	public String  status;
	
	//@SerializedName("routes")
	public List<MapRoute> routes;
}
