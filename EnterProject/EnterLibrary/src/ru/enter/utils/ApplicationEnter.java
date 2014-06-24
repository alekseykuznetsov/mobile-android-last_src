package ru.enter.utils;

import android.app.Application;
import android.content.Context;
import android.graphics.Typeface;

public class ApplicationEnter extends Application{
	
    private static Context mContext;
	private static String mAppClientId = "";

    @Override
    public void onCreate() {
        super.onCreate();
        mContext = getApplicationContext();
        PreferencesManager.showGetCouponDialog(true);
    }
    
	public static Context getContext(){
		return mContext;
	}
	
	public static Typeface getRoubleTypeface() {
		return Utils.getRoubleTypeFace(mContext);
	}
	
	public static void setAppClientId (String clientId) {
		mAppClientId = clientId;
	}
	
	public static String getAppClientId () {
		return mAppClientId;
	}

}
