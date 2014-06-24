package ru.enter.beans;

import java.io.Serializable;
import java.util.ArrayList;

public class CatalogListBean implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = -2953122591028749856L;
	
	private String link;
	private boolean is_category_list;
	private int id;
	private String name;
	private int count;
	private String foto;
	private String icon;
	private ArrayList<String> icon_size;
	
	public String getLink() {
		return link;
	}
	public void setLink(String link) {
		this.link = link;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public boolean isIs_category_list() {
		return is_category_list;
	}
	public void setIs_category_list(boolean is_category_list) {
		this.is_category_list = is_category_list;
	}
	public int getCount() {
		return count;
	}
	public void setCount(int count) {
		this.count = count;
	}
	public void setFoto(String foto) {
		this.foto = foto;
	}
	public String getFoto() {
		return foto;
	}
	public void setIcon(String icon) {
		this.icon = icon;
	}
	public String getIcon(){
		return icon;
	}
	public ArrayList<String> getIconSize() {
		return icon_size;
	}
	public void setIconSize(ArrayList<String> icon_size) {
		this.icon_size = icon_size;
	}
	
}
