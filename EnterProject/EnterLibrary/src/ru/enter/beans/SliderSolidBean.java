package ru.enter.beans;

public class SliderSolidBean {

	private String id;
	private int currentMin;
	private int currentMax;

	public String getId () {
		return id;
	}

	public void setId (String id) {
		this.id = id;
	}

	public int getCurrentMin () {
		return currentMin;
	}

	public void setCurrentMin (int current_min) {
		this.currentMin = current_min;
	}

	public int getCurrentMax () {
		return currentMax;
	}

	public void setCurrentMax (int current_max) {
		this.currentMax = current_max;
	}

}
