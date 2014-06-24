package ru.enter.beans;

public class CitiesBean {

	private int id;
    private String name;
    private boolean hasShop;
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public boolean isHasShop() {
		return hasShop;
	}
	public void setHasShop(boolean has_shop) {
		this.hasShop = has_shop;
	}
}
