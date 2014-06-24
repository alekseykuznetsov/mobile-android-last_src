package ru.enter.data;

import java.util.List;

import ru.enter.utils.Utils;

public enum CatalogTree {
    INSTANCE;

	private List<CatalogNode> mRoots;
    
    public static void setRoots(List<CatalogNode> roots) {
		INSTANCE.mRoots = roots;
	}

	public static List<CatalogNode> getRoots() {
		return INSTANCE.mRoots;
	}
	
	public static boolean isEmpty(){
		return Utils.isEmptyList(INSTANCE.mRoots);
	}
}