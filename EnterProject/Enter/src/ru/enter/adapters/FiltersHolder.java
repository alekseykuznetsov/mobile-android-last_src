package ru.enter.adapters;

import ru.enter.utils.FiltersManager;

public enum FiltersHolder {

	INSTANCE;
	
	private FiltersManager mFilterManager = new FiltersManager();
	
	public static FiltersManager getFilterManager () {
		return INSTANCE.mFilterManager;
	}
	
}
