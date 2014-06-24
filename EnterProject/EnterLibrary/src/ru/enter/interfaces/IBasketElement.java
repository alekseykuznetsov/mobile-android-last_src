package ru.enter.interfaces;

public interface IBasketElement {
	String getFoto();
	double getPrice();
	int getId();
	String getName();
	String getDescription();
	boolean isProduct();
	
	//добавил только ради совместимости в корзине. В будущем убрать и переделать
	void setCount(int count);
	int getCount();
	void increaseCount();
	
}
