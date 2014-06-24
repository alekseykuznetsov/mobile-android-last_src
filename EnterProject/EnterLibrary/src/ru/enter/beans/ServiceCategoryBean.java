package ru.enter.beans;

public class ServiceCategoryBean {
	
	private String foto;
	private boolean category_list;
	private int id;
	private String name;
	
	public void setFoto(String foto) {
		this.foto = foto;
	}
	public String getFoto() {
		return foto;
	}
	public void setCategory_list(boolean category_list) {
		this.category_list = category_list;
	}
	public boolean isCategory_list() {
		return category_list;
	}
	public void setId(int id) {
		this.id = id;
	}
	public int getId() {
		return id;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getName() {
		return name;
	}
}
