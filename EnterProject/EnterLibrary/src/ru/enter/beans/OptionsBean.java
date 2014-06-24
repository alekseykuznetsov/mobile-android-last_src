package ru.enter.beans;

public class OptionsBean {
	
	private String name;
	private int id;
	private String filter_id; // нужно для поиска по фильтрам
	private boolean isChecked; //нужно для места в списке где поставить галочку
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getId() {
		return id;
	}
	
	@Override
	public boolean equals(Object o) {
	    if (this == o)
	      return true;
	    if (!(o instanceof OptionsBean))
	      return false;
	    OptionsBean otherBean = (OptionsBean) o;
	    return 
	      (name.equals(otherBean.name))
	        && (id == otherBean.id) && (filter_id == otherBean.filter_id);
	}
	
	@Override
	public int hashCode() {
	    int hash = 1;
	    hash = hash * 33 + ((Integer)id).hashCode();
	    hash = hash * 33 
	                + (name == null ? 0 : name.hashCode());
	    return hash;
	}

	public void setFilter_id(String filter_id) {
		this.filter_id = filter_id;
	}

	public String getFilter_id() {
		return filter_id;
	}

	public void setChecked(boolean isChecked) {
		this.isChecked = isChecked;
	}

	public boolean isChecked() {
		return isChecked;
	}


}
