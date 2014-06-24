package ru.enter.utils;

import java.io.InputStream;
import java.util.Scanner;

import android.content.Context;
import android.util.DisplayMetrics;

public class Converters {

	public static int dp2px(Context context, int dp) {
		DisplayMetrics dm = context.getResources().getDisplayMetrics();
		return (int) (dp * dm.density);
	}

	public static String inputStreamToString(InputStream is) {
		return new Scanner(is).useDelimiter("\\A").next();
	}

}
