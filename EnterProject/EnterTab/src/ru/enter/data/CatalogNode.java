package ru.enter.data;

import java.util.ArrayList;

import ru.enter.beans.CatalogListBean;

public class CatalogNode {
	
	private CatalogListBean node;
	private ArrayList<CatalogNode> children;
	
	public void setNode(CatalogListBean node) {
		this.node = node;
	}
	
	public CatalogListBean getNode() {
		return node;
	}

	public void setChildren(ArrayList<CatalogNode> children) {
		this.children = children;
	}

	public ArrayList<CatalogNode> getChildren() {
		return children;
	}
	
}
