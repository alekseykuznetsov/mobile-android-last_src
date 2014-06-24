package ru.enter.utils;

import android.view.View;

public interface OnBarNavigationListener<T> {
	public void onNavigationBarItemSelected(int pos, T item, View v);
}