package ru.enter.data;

import java.util.ArrayList;

import ru.enter.beans.CatalogListBean;
import ru.enter.beans.ServiceBean;
import ru.enter.beans.ServiceCategoryBean;

public class ServicesNode {

	private long id;
	private ServiceCategoryBean node;
	private ArrayList<ServicesNode> children;
	private ServicesNode parent;
	private ArrayList<ServiceBean> services;

	public void setNode(ServiceCategoryBean node) {
		this.node = node;
	}

	public ServiceCategoryBean getNode() {
		return node;
	}

	public void setChildren(ArrayList<ServicesNode> children) {
		this.children = children;
	}

	public ArrayList<ServicesNode> getChildren() {
		return children;
	}

	public void setParent(ServicesNode parent) {
		this.parent = parent;
	}

	public ServicesNode getParent() {
		return parent;
	}

	public void setServices(ArrayList<ServiceBean> services) {
		this.services = services;
	}

	public ArrayList<ServiceBean> getServices() {
		return services;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

}
