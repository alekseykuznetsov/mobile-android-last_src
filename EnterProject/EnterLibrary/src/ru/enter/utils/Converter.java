package ru.enter.utils;

import java.util.Calendar;
import java.util.StringTokenizer;

public class Converter {

	private Converter(){}	
	
	public static String fromDotToLine(String in){
		try{
		     StringTokenizer st = new StringTokenizer(in, ".");
		     String day = st.nextToken();
		     String month = st.nextToken();
		     String year = st.nextToken();
		     
		     return year + "-" + month + "-" + day;
		}
		catch(Exception e){
			return "";
		}
	}
	
	public static String fromLineToDot(String in){
		try{
		     StringTokenizer st = new StringTokenizer(in, "-");
		     String year = st.nextToken();
		     String month = st.nextToken();
		     String day = st.nextToken();     
		     
		     String result = day + "." + month + "." + year;
		     
		     return result.equals("00.00.0000") ? "Не указано" : result;
		}
		catch(Exception e){
			return "Не указано";
		}
	}
	
	public static Calendar fromDot(String in){
		Calendar calendar = Calendar.getInstance();
		StringTokenizer st = new StringTokenizer(in, ".");
		int day = Integer.parseInt(st.nextToken());
		calendar.set(Calendar.DAY_OF_MONTH, day);
		int month = Integer.parseInt(st.nextToken());
		calendar.set(Calendar.MONTH, month - 1);
		int year = Integer.parseInt(st.nextToken());
		calendar.set(Calendar.YEAR, year);
		return calendar;
	}
	
	public static String from3Int(int year, int monthOfYear, int dayOfMonth){
		return ((dayOfMonth < 10)? "0": "") + dayOfMonth + "." + 
				((monthOfYear < 10)? "0": "") + monthOfYear + "." +
				year;
	}
	
	public static String priceFormat(double price){
		return String.format(" %,.0f", price).replace(",", " ");
	}
}
