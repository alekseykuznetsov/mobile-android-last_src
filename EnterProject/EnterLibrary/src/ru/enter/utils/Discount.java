package ru.enter.utils;

public class Discount {
	public static String getDiscount(double newPrice, double oldPrice){
		if(oldPrice == 0.0) return "";
		
		double res = Math.round((1 - newPrice/oldPrice) * 100);
		if(res > 0) res = -res;

		return " " + String.valueOf(res) + "%";
	}
}
