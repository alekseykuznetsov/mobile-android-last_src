package ru.enter.beans;

import java.util.ArrayList;

public class TagRubricBean {
	
	private String rubric_name;
	private int rubric_id;
	private ArrayList<TagBean> beans;
	
	public void setRubric_name(String rubric_name) {
		this.rubric_name = rubric_name;
	}

	public String getRubric_name() {
		return rubric_name;
	}

	public void setRubric_id(int rubric_id) {
		this.rubric_id = rubric_id;
	}

	public int getRubric_id() {
		return rubric_id;
	}

	public void setBeans(ArrayList<TagBean> beans) {
		this.beans = beans;
	}

	public ArrayList<TagBean> getBeans() {
		return beans;
	}
	
//	@Override
//	public boolean equals(Object o) {
//		 // Not strictly necessary, but often a good optimization
//	    if (this == o)
//	      return true;
//	    if (!(o instanceof TagRubricBean))
//	      return false;
//	    TagRubricBean otherBean = (TagRubricBean) o;
//	    return 
//	      (rubric_name.equals(otherBean.rubric_name))
//	        && (rubric_id == otherBean.rubric_id)&& ((beans == null) 
//	                ? otherBean.beans == null 
//	                        : beans.equals(otherBean.beans));//TODO?
//	}
//	
//	@Override
//	public int hashCode() {
//	    int hash = 1;
//	    hash = hash * 31 + rubric_name.hashCode();
//	    hash = hash * 31 
//	                + (beans == null ? 0 : beans.hashCode());
//	    return hash;
//	}
}
