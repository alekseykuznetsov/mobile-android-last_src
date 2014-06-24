package ru.enter.beans;

import java.io.Serializable;

public class MetroBean implements Serializable{

	private static final long serialVersionUID = 320601972387546460L;
	private int id;
	private String name;
	private int geoId;
	private boolean isActive;
	
	public void setId (int id) {
		this.id = id;
	}
	public int getId () {
		return id;
	}
	public void setName (String name) {
		this.name = name;
	}
	public String getName () {
		return name;
	}
	public void setGeoId (int geoId) {
		this.geoId = geoId;
	}
	public int getGeoId () {
		return geoId;
	}
	public void setActive (boolean isActive) {
		this.isActive = isActive;
	}
	public boolean isActive () {
		return isActive;
	}
}
