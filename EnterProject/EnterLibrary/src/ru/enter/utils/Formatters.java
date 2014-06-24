package ru.enter.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import ru.enter.beans.AddressBean;
import ru.enter.beans.DeliveryBean;
import ru.enter.beans.MetroBean;
import ru.enter.beans.ProductBean;
import android.text.TextUtils;

public class Formatters {

	public static String createFotoString (String url, int size) {
		return TextUtils.isEmpty(url) ? "" : url.replaceAll("__media_size__", String.valueOf(size));
	}

	public static String createDeliveryString (DeliveryBean bean) {
		StringBuilder builder = new StringBuilder();

		try {
			Date dateServer = new SimpleDateFormat("yyyy-MM-dd").parse(bean.getDate());
			String dateToUser = new SimpleDateFormat(" (dd.MM.yyyy)").format(dateServer);

			long serverTime = dateServer.getTime();
			long nowTime = System.currentTimeMillis();
			long btw_temp = serverTime - nowTime;
			int MILLISINDAY = 1000 * 60 * 60 * 24;
			long daysBetween = (btw_temp) / MILLISINDAY + (btw_temp >= 0 ? 1 : 0);

			String var1 = "Можно заказать сейчас и самостоятельно забрать в магазине ";
			String var2 = "Можно заказать сейчас с доставкой ";

			builder.append(bean.getMode_id() == 3 ? var1 : var2);

			if (daysBetween == 0)
				builder.append("сегодня");
			else if (daysBetween == 1)
				builder.append("завтра");
			else
				builder.append("через ").append(daysBetween).append(forDaysString(daysBetween));

			builder.append(dateToUser);
		} catch (ParseException e) {
			e.toString();
		}

		return builder.toString();
	}
	
	public static String forDaysString (long num) {
		long mod = num % 20;
		if (mod == 1)
			return " день";
		else if (mod >= 2 && mod <= 4)
			return " дня";
		else
			return " дней";
	}

	public static String createAddressString (AddressBean bean) {
		StringBuilder builder = new StringBuilder();

		if (!TextUtils.isEmpty(bean.getStreet())) {
			
			builder.append(createMetroString(bean.getMetro()));

			builder.append("ул. ");
			builder.append(bean.getStreet());

			builder.append(", д. ");
			builder.append(bean.getHouse());

			if (!TextUtils.isEmpty(bean.getHousing())) {
				builder.append(", корп. ");
				builder.append(bean.getHousing());
			}

			if (!TextUtils.isEmpty(bean.getFloor())) {
				builder.append(", этаж ");
				builder.append(bean.getFloor());
			}

			if (!TextUtils.isEmpty(bean.getFlat())) {
				builder.append(", кв. ");
				builder.append(bean.getFlat());
			}
		}
		else{
			builder.append(bean.getAddress());
		}

		return builder.toString();
	}

	public static String createMetroString (MetroBean metro) {
		
		if (metro == null || metro.getId() == -1) {
			return "";
		}

		return String.format("ст. %s, ", metro.getName());
	}
	
	public static String createPriceStringWithRouble (int price) {
		return String.format("%,d p", price);
	}

}
