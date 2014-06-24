package ru.enter.maps.json;

import java.io.Serializable;

import com.google.gson.annotations.SerializedName;

public class MapRouteTextValueItem implements Serializable {

	private static final long serialVersionUID = 8455126760656957265L;
	
	@SerializedName("text")
	public String  text;
	
	@SerializedName("value")
	public Integer value;
	
	
}
