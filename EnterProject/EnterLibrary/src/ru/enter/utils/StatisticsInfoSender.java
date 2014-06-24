package ru.enter.utils;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.pm.PackageManager.NameNotFoundException;
import android.os.AsyncTask;
import android.os.Build;
import android.provider.Settings.Secure;

public class StatisticsInfoSender extends AsyncTask<Void, Void, Void>{
	
	private JSONObject mObject;

	@Override
	protected void onPreExecute () {
		super.onPreExecute();
		
		String uid = Secure.getString(ApplicationEnter.getContext().getContentResolver(), Secure.ANDROID_ID);
		String os = Build.VERSION.RELEASE;
		String platform = ApplicationEnter.getAppClientId();
		
		String appVersion = "";
		
		try {
			appVersion = ApplicationEnter.getContext().getPackageManager().getPackageInfo(ApplicationEnter.getContext().getPackageName(), 0).versionName;
		} catch (NameNotFoundException e) {}
		
		mObject = new JSONObject();
		
		try {
			mObject.put("uid", uid);
			mObject.put("os_name", platform);
			mObject.put("os_version", os);
			mObject.put("app_version", appVersion);
		} catch (JSONException e) {}
		
	}

	@Override
	protected Void doInBackground (Void... params) {
		Utils.sendPostData(mObject.toString(), "http://mobile.enter.ru/stats/send");
		return null;
	}
	
	public static void send() {
		new StatisticsInfoSender().execute();
	}

}
