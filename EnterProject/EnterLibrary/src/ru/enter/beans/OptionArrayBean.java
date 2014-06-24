package ru.enter.beans;

import java.io.Serializable;
import java.util.ArrayList;

import com.j256.ormlite.field.DatabaseField;

public class OptionArrayBean implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 2468174930916066052L;
	
	@DatabaseField(generatedId = true)
	private int id;
	@DatabaseField
	private int productForeignKeyID;
	@DatabaseField
	private String name;
	
	private ArrayList<OptionBean> option;
	
	public ArrayList<OptionBean> getOption() {
		return option;
	}
	public void setOption(ArrayList<OptionBean> option) {
		this.option = option;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public void setProductForeignKeyID(int productForeignKeyID) {
		this.productForeignKeyID = productForeignKeyID;
	}
	public int getProductForeignKeyID() {
		return productForeignKeyID;
	}
	public int getId() {
		return id;
	}
}
