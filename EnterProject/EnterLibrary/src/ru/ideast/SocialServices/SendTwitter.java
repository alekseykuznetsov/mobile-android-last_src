package ru.ideast.SocialServices;

import ru.ideast.enter.R;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.http.AccessToken;
import twitter4j.http.RequestToken;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

public class SendTwitter extends Activity{
	
	private String URL = "";
	private String TITLE = "";
	
	public static final String CONSUMER_KEY = "7K03hMuZRDCs5WZyio1g";
	public static final String CONSUMER_SECRET = "VzkbHWtgTAv0UDwQoAUnl01MgWkg8RPVln3lZt0ASQ";
	
	/**
	 * Twitter
	 */
	private Twitter twitterConnection = null;
	private OAuthHelp oHelper = null;
	private RequestToken requestToken = null;
	private AccessToken accessToken = null;
	private WebView webView = null;
	private Context context;
	private LinearLayout ll;
	private Button next;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		setContentView(R.layout.twitter_login_webview);
		context = this;
		
		URL = getIntent().getExtras().getString(SendDialog.EX_URL);
		TITLE = getIntent().getExtras().getString(SendDialog.EX_TITLE);
		
		if(TITLE == null) TITLE = "Новости";

		ll = (LinearLayout)findViewById(R.id.twitter_progress);
		webView = (WebView)findViewById(R.id.twitter_login_view);
		webView.setVisibility(View.INVISIBLE);
		
		next = (Button)findViewById(R.id.twitter_login_next);
		
        twitterConnection = new TwitterFactory().getInstance();
        twitterConnection.setOAuthConsumer(CONSUMER_KEY, CONSUMER_SECRET);
		//new 
        oHelper = new OAuthHelp(context);
        new Thread(new Runnable() {
			@Override
			public void run() {
		        try {
					requestToken = twitterConnection.getOAuthRequestToken("");
				} catch (TwitterException e) {
					
				}
		        webViewDialog(requestToken.getAuthorizationURL(), 0);
				
			}
		}).start();
        /* old: dont work on 3.0+< no more inet operations on ui thread =\
         		try {
					requestToken = twitterConnection.getOAuthRequestToken("");
				} catch (TwitterException e) {
					
				}
				oHelper = new OAuthHelp(context);
		        webViewDialog(requestToken.getAuthorizationURL(), 0);
         */
        super.onCreate(savedInstanceState);
	}
	
	public String createTwitterStatus(String news, String url)
	{
		String tmp = "";
		if(news.length() + url.length() > 140)
			tmp = news.substring(0, news.length()-( news.length() + url.length() - 140)-4) +"... " + url;
		else 
			tmp = news + " " + url;
		return tmp;
			
	}
	 /**
	  * Shows Dialog for authentications
	  *
	  * @param authorizationURL
	  * @param type
	  */
	private void webViewDialog(final String authorizationURL, final int type) {
		 
		 webView.getSettings().setJavaScriptEnabled(true);
		 webView.setWebViewClient(new WebViewClient()
		 {
			 @Override
			public void onPageFinished(WebView view, String url) {
				ll.setVisibility(View.GONE);
				webView.setVisibility(View.VISIBLE);
			}
		 });
		 webView.loadUrl(authorizationURL);

		 next.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				twitterPinCodeDialog();
				
			}
		});
	}
	/**
	 * Pin code dialog Requests the user to enter pin shown on twitter
	 */
	public void twitterPinCodeDialog() {
		 LinearLayout pinHolder = new LinearLayout(this);
		 pinHolder.setGravity(Gravity.CENTER);
		 final EditText editPinCode = new EditText(this);
		 editPinCode.setGravity(Gravity.CENTER);
		 editPinCode.setHint("Pin Code");
		 editPinCode.setWidth(200);
		 pinHolder.addView(editPinCode);
		 Builder pinBuilder = new AlertDialog.Builder(this);
		 pinBuilder
		 	.setView(pinHolder).setTitle("Twitter Pin")
		 	.setMessage("Введите PIN код")
		 	.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
		 		@Override
		 		public void onClick(DialogInterface dialog, int which) {
		 			if (editPinCode.getText().toString() != null  && !editPinCode.getText().toString().equals("")) {
		 		        /* new */
		 				new Thread(new Runnable() {
		 					@Override
		 					public void run() {
				 				try {
				 					accessToken = twitterConnection.getOAuthAccessToken(requestToken, editPinCode.getText().toString());
				 					oHelper.storeAccessToken(accessToken);
				 					twitterConnection.updateStatus(createTwitterStatus(TITLE,URL));
				 					twitterHandler.sendEmptyMessage(0);//ok
				 				} catch (TwitterException te) {
				 					if (401 == te.getStatusCode()) {
		//		 						System.out.println("Unable to get the access token.");
				 						twitterHandler.sendEmptyMessage(1); //err
				 					} else {
				 						te.printStackTrace();
				 					}
				 				}
		 					}
		 		        }).start();
		 				/*old
		 				try {
		 					accessToken = twitterConnection.getOAuthAccessToken(requestToken, editPinCode.getText().toString());
		 					oHelper.storeAccessToken(accessToken);
		 					twitterConnection.updateStatus(createTwitterStatus(TITLE,URL));
		 					Toast.makeText(context, "Сообщение в Twitter отправлено", Toast.LENGTH_SHORT).show();
		 					setResult(RESULT_OK);
		 					finish();
		 				} catch (TwitterException te) {
		 					if (401 == te.getStatusCode()) {
//		 						System.out.println("Unable to get the access token.");
		 						Toast.makeText(context, "Невозможно получить доступ", Toast.LENGTH_SHORT).show();
		 					} else {
		 						te.printStackTrace();
		 					}
		 				}
		 				 
		 				 
		 				 
		 				 
		 				 */
		 			} else {
		 				Toast.makeText(context, "Pin необходим для получения доступа", Toast.LENGTH_SHORT).show();
		 				dialog.dismiss();
		 				twitterPinCodeDialog();
		 			}
			 		}
		 	})
		 	.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
			 		@Override
		 		public void onClick(DialogInterface dialog, int which) {
		 			dialog.dismiss();
		 		}
		 	}).show();
		}
	
	private Handler twitterHandler = new Handler(){
		@Override
		public void handleMessage(Message msg) {
			switch(msg.what){
			case 0:
				Toast.makeText(context, "Сообщение в Twitter отправлено", Toast.LENGTH_SHORT).show();
				setResult(RESULT_OK);
				finish();
				break;
			case 1:
				Toast.makeText(context, "Невозможно получить доступ", Toast.LENGTH_SHORT).show();
				break;

		}
		};
	};
	
}
