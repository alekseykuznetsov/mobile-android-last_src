package ru.enter.beans;

import java.util.ArrayList;

public class FilterBean {
	
	private String name;
	private String id;
	private int min;
	private int max;
	private int type;
	private ArrayList<OptionsBean> options;
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getId() {
		return id;
	}
	
	@Override
	public boolean equals(Object o) {
		 // Not strictly necessary, but often a good optimization
	    if (this == o)
	      return true;
	    if (!(o instanceof FilterBean))
	      return false;
	    FilterBean otherBean = (FilterBean) o;
	    return 
	      (name.equals(otherBean.name))
	        && (id == otherBean.id);
	}
	
	@Override
	public int hashCode() {
	    int hash = 1;
	    hash = hash * 32 + id.hashCode();
	    hash = hash * 32 
	                + (name == null ? 0 : name.hashCode());
	    return hash;
	}

	public void setOptions(ArrayList<OptionsBean> options) {
		this.options = options;
	}

	public ArrayList<OptionsBean> getOptions() {
		return options;
	}

	public int getType () {
		return type;
	}

	public void setType (int type) {
		this.type = type;
	}

	public int getMin () {
		return min;
	}

	public void setMin (int min) {
		this.min = min;
	}

	public int getMax () {
		return max;
	}

	public void setMax (int max) {
		this.max = max;
	}
}
