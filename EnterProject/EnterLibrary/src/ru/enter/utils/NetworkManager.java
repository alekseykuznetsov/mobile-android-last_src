package ru.enter.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

public class NetworkManager 
{
	public static boolean isConnected(Context currectContext)
	{
		boolean isConnected = false;
		ConnectivityManager connectionManager = (ConnectivityManager)currectContext.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo[] nerworksInfo = null;
		
		try
		{
			nerworksInfo = connectionManager.getAllNetworkInfo();
		}
		catch(Exception e)
		{			
			Log.e("NetworkManager", e.getMessage());
		}
		
		for (NetworkInfo nerworkInfo : nerworksInfo)
		{
			if (nerworkInfo.isConnected())
			{
				isConnected = true;
			}
		}			
				
		return isConnected;
	}
}
