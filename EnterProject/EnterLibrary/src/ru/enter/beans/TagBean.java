package ru.enter.beans;

public class TagBean {
	
	private String name;
	private int id;
	

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
		 // Not strictly necessary, but often a good optimization
	    if (this == o)
	      return true;
	    if (!(o instanceof TagBean))
	      return false;
	    TagBean otherBean = (TagBean) o;
	    return 
	      (name.equals(otherBean.name))
	        && (id == otherBean.id);
	}
	
	@Override
	public int hashCode() {
	    int hash = 1;
	    hash = hash * 31 + ((Integer)id).hashCode();
	    hash = hash * 31 
	                + (name == null ? 0 : name.hashCode());
	    return hash;
	}

}
