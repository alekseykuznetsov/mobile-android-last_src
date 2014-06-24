package ru.ideast.SocialServices;

/*
Copyright [2010] [Abhinava Srivastava]

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
*/

import twitter4j.Twitter;
import twitter4j.http.AccessToken;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

public class OAuthHelp {

	private static final String APPLICATION_PREFERENCES = "tweet_preferences";
	private static final String TWEET_AUTH_KEY = "auth_key";
	private static final String TWEET_AUTH_SEKRET_KEY = "auth_secret_key";
	private SharedPreferences preferences;
	private AccessToken accessToken;
	private String consumerSecretKey;
	private String consumerKey;

	 /**
	  * OAuthHelp class constructor loads the consumer keys
	  *
	  * @param context
	  */
	 public OAuthHelp(Context context) {
		 preferences = context.getSharedPreferences(APPLICATION_PREFERENCES, Context.MODE_PRIVATE);
		 loadConsumerKeys();
		 accessToken = loadAccessToken();
	 }
	
	 /**
	  * Depricated method has been used
	  *
	  * @param twitter
	  */
	 public void configureOAuth(Twitter twitter) {
		 
		 try
		 {
			 twitter.setOAuthConsumer(consumerKey, consumerSecretKey);
			 twitter.setOAuthAccessToken(accessToken);
		 }
		 catch(IllegalStateException e){}
		 
	 }
	
	 /**
	  * true is accesstoken available false other wise
	  *
	  * @return boolean
	  *
	  */
	 public boolean hasAccessToken() {
		 return null != accessToken;
	 }
	
	 /**
	  * Stores access token in preferences
	  *
	  * @param accessToken
	  */
	 public void storeAccessToken(AccessToken accessToken) {
		 Editor editor = preferences.edit();
		 editor.putString(TWEET_AUTH_KEY, accessToken.getToken());
		 editor.putString(TWEET_AUTH_SEKRET_KEY, accessToken.getTokenSecret());
		 editor.commit();
		 this.accessToken = accessToken;
	 }
	
	 /**
	  * Loads acess token from SharedPreferences
	  * @return
	  */
	 private AccessToken loadAccessToken() {
		 String token = preferences.getString(TWEET_AUTH_KEY, null);
		 String tokenSecret = preferences.getString(TWEET_AUTH_SEKRET_KEY, null);
		 if (null != token && null != tokenSecret) {
			 return new AccessToken(token, tokenSecret);
		 } else {
			 return null;
		 }
	 }
	
	 /**
	  * Loads default consumer keys
	  */
	 private void loadConsumerKeys() {
		 consumerKey = "7K03hMuZRDCs5WZyio1g";
		 consumerSecretKey = "VzkbHWtgTAv0UDwQoAUnl01MgWkg8RPVln3lZt0ASQ";
 	}
}