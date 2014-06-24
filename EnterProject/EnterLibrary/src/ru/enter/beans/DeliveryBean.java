package ru.enter.beans;

import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import android.text.Html;
import android.text.Spanned;

public class DeliveryBean implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 6448131832234454352L;
	
	private String date;
	private int price;
	private int mode_id;
	private String name;
	private static final int MILLISINDAY = 1000*60*60*24;
	
	private SimpleDateFormat fromServer = new SimpleDateFormat("yyyy-MM-dd");
	private SimpleDateFormat toUser = new SimpleDateFormat(" (dd MMMM)", new Locale("ru"));
	private SimpleDateFormat _toUser = new SimpleDateFormat(" dd.MM.yyyy", new Locale("ru"));
	
	public void setDate(String date) {
		this.date = date;
	}
	
	public String getDateString() throws ParseException {
		Date dateServer = fromServer.parse(date);
		String dateToUser = toUser.format(dateServer);
		return dateToUser;
	}
	
	public String getDate(){
		return date;
	}
	
	public String getDateStringFromServer(){
		return date;
	}
	
	public void setPrice(int price) {
		this.price = price;
	}
	public int getPrice() {
		return price;
	}
	public void setMode_id(int mode_id) {
		this.mode_id = mode_id;
	}
	public int getMode_id() {
		return mode_id;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getName() {
		return name;
	}
	
	public String getDeliveryNameWithPrice(){
		StringBuilder result = new StringBuilder();
		
		if (name.equalsIgnoreCase("стандарт")){
			name = "доставка";
		}
		
		result.append(Character.toUpperCase(name.charAt(0))).append(name.substring(1)); //делаем в слове первую букву заглавной
		result.append(" - ");
		result.append(price == 0 ? "бесплатно, " : price + " рублей, ");
		
		return result.toString();
	}
	
	public Spanned getMyDelivery() {
		//final String myString = "Можно забрать в магазине ";
		long nowTime = new Date().getTime();
		long daysBetween = 0;
		String dateToUser = "";
		try {
			Date dateServer = fromServer.parse(date);
			dateToUser = _toUser.format(dateServer);
			long serverTime = dateServer.getTime();
			long btw_temp = serverTime  - nowTime;
			daysBetween = (btw_temp)/MILLISINDAY + (btw_temp >= 0 ? 1 : 0);
		} catch (ParseException e) {}
		
		//if(daysBetween == 0) return Html.fromHtml(myString.concat("<b>сегодня </b>").concat(dateToUser));
		//else if (daysBetween == 1) return Html.fromHtml(myString.concat("<b>завтра </b>").concat(dateToUser));
		//else return Html.fromHtml("через " + daysBetween + "дней" + dateToUser);
		//return Html.fromHtml(myString + dateToUser);
		return Html.fromHtml(dateToUser);
	}
	
	public Spanned getShopDelivery(){
		//final String myString = "Можем доставить ";
		long nowTime = new Date().getTime();
		long daysBetween = 0;
		String dateToUser = "";
		try {
			Date dateServer = fromServer.parse(date);
			dateToUser = _toUser.format(dateServer);
			long serverTime = dateServer.getTime();
			long btw_temp = serverTime  - nowTime;
			daysBetween = (btw_temp)/MILLISINDAY + (btw_temp >= 0 ? 1 : 0);
		} catch (ParseException e) {}
		
		//return Html.fromHtml(getRusDays(daysBetween).concat(dateToUser).concat(", ".concat(String.valueOf(price)).concat(" руб")));
		//return Html.fromHtml(myString+dateToUser);
		return Html.fromHtml(dateToUser);
	}
	
	private String getRusDays(long days) {
		if (days == 0)
			return "<b>сегодня </b>";
		else if (days == 1)
			return "<b>завтра </b>";

		long mod = days % 20;
		String result = null;
		if (mod == 1)
			result = " день";
		else if (mod >= 2 && mod <= 4)
			result = " дня";
		else
			result = " дней";
		return "через ".concat(String.valueOf(days)).concat(result);
	}
	
}
