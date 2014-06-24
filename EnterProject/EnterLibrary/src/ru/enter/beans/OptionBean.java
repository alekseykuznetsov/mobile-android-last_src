package ru.enter.beans;

import java.io.Serializable;

import com.j256.ormlite.field.DatabaseField;

public class OptionBean implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -4645670881627385290L;
	
	@DatabaseField(generatedId = true)
	private int id;
	@DatabaseField
	private int optionArrayForeignKeyID;
	@DatabaseField
	private String option;
	@DatabaseField
	private String value;
	@DatabaseField
	private String unit;
	@DatabaseField
	private String property;
	
	public String getOption() {
		return option;
	}
	public void setOption(String option) {
		this.option = option;
	}
	public String getValue() {
		return value;
	}
	public void setValue(String value) {
		this.value = value;
	}
	public String getProperty() {
		return property;
	}
	public void setProperty(String property) {
		this.property = property;
	}
	public String getUnit() {
		return unit;
	}
	public void setUnit(String unit) {
		this.unit = unit;
	}
	public int getId() {
		return id;
	}
	public void setOptionArrayForeignKeyID(int optionArrayForeignKeyID) {
		this.optionArrayForeignKeyID = optionArrayForeignKeyID;
	}
	public int getOptionArrayForeignKeyID() {
		return optionArrayForeignKeyID;
	}
}
