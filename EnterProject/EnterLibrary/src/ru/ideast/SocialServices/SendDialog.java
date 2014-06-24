package ru.ideast.SocialServices;



import java.util.HashMap;
import java.util.Map;

import ru.enter.utils.Constants;
import ru.enter.utils.Log;
import ru.enter.utils.NetworkManager;
import ru.ideast.SocialServices.VkApp.VkDialogListener;
import ru.ideast.enter.R;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Html;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;

import com.facebook.android.DialogError;
import com.facebook.android.Facebook;
import com.facebook.android.Facebook.DialogListener;
import com.facebook.android.FacebookError;
import com.flurry.android.FlurryAgent;

public class SendDialog extends Activity {

	/** 
	 * FaceBook
	 */
	private String FB_ID = "262075720509710";
	private Facebook fb = new Facebook(FB_ID);
	/**
	 * Twitter
	 */
	private Twitter twitterConnection = null;
	private OAuthHelp oHelper = null;

	private Handler handleEvent = null;


	
	
	private Button facebook_btn;
	private Button vk_btn;
	private Button twitter_btn;
	private Button lj_btn;
	private Button e_mail_btn;
	private Button cancel_btn;
	private Context context;
	
	public static final String EX_URL = "EX_URL";
	public static final String EX_TITLE = "EX_TITLE";
	public static final String EX_IMAGE = "EX_IMAGE";
	public static final String EX_DESC = "EX_DESC";
	public static final String EX_PRICE = "EX_PRICE";
	
	private String URL = "";
	private String TITLE = "";
	private String DESC = "";
	private String PRICE = "";
	private String IMAGE = "";
	protected ProgressDialog m_progress;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.article_send_dialog);
		
		context = this;
		
		m_progress = new ProgressDialog(this);
		m_progress.setMessage("Подождите..."); 
		
		URL = getIntent().getExtras().getString(EX_URL);
		TITLE = getIntent().getExtras().getString(EX_TITLE);
		DESC = getIntent().getExtras().getString(EX_DESC);
		PRICE = getIntent().getExtras().getString(EX_PRICE);
		IMAGE = getIntent().getExtras().getString(EX_IMAGE);
		
		/**
         * For Twitter
         */
        handleEvent = new Handler();
        twitterConnection = new TwitterFactory().getInstance();
        oHelper = new OAuthHelp(this);
        /**
         * LJ
         */

        initSendDialog();
        
	}
	
	@Override
	protected void onStart(){
		super.onStart();
		FlurryAgent.onStartSession(this, "3X5JRBVFV7QWXNP3Q8H2");
	}

	@Override
	protected void onStop(){
		super.onStop();		
		FlurryAgent.onEndSession(this);
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if(requestCode == 10)
			finish();
		if(requestCode == 15)
			finish();
		fb.authorizeCallback(requestCode, resultCode, data);
		
		
		super.onActivityResult(requestCode, resultCode, data);
	}
	
	private void initSendDialog(){
		facebook_btn = (Button)findViewById(R.id.article_viewer_send_dialog_facebook);
		facebook_btn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if(NetworkManager.isConnected(context))
					FBSend();
						
			}
		});
		vk_btn = (Button)findViewById(R.id.article_viewer_send_dialog_vk);
		vk_btn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if(NetworkManager.isConnected(context))
					VKSend();
						
			}
		});		
		twitter_btn = (Button)findViewById(R.id.article_viewer_send_dialog_twitter);
		twitter_btn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if(NetworkManager.isConnected(context))
					TwitterSend();
			}
		});
		lj_btn = (Button)findViewById(R.id.article_viewer_send_dialog_lj);
		lj_btn.setVisibility(View.GONE);
//		lj_btn.setOnClickListener(new OnClickListener() {
//			
//			@Override
//			public void onClick(View v) {
//				if(NetworkManager.isConnected(context))
//				{
//					Intent intent = new Intent().setClass(context, SendLiveJournal.class);
//					intent.putExtra(EX_IMAGE, TITLE);
//					intent.putExtra(EX_DESC, DESC);
//					intent.putExtra(EX_URL, URL);
//					startActivityForResult(intent, 10);
//				}
//			}
//		});
		e_mail_btn = (Button)findViewById(R.id.article_viewer_send_dialog_e_mail);
		e_mail_btn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if(NetworkManager.isConnected(context))
				{
					EMailSend();
					finish();
				}
			}
		});
		cancel_btn = (Button)findViewById(R.id.article_viewer_send_dialog_exit);
		cancel_btn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				finish();
			}
		});
		
	}
	
	private Bundle createBundle(String title, String desc, String url, String image, String price) {
		Bundle parameters = new Bundle();
		
		title = title.replaceAll("\"", "&quot;");
		if(desc != null){
			desc = desc.replaceAll("\"", "&quot;");
			desc = desc.replaceAll("–", "-");
		}
		
//		JSONObject att = new JSONObject();
//		JSONObject media = new JSONObject();
//		JSONObject prop = new JSONObject();
//		try {
//			att.put("name", title);
//			att.put("href",url);
//			if(!desc.equals(""))
//				att.put("description", desc);
//			
//			media.put("type", "image");
//			media.put("src",image);
//			media.put("href", url);
//			att.put("media", media);
//			
//			prop.put("href", url);
//			att.put("properties", prop);
//		} catch (JSONException e) {
//			e.printStackTrace();
//		}
//		parameters.putString("attachment",att.toString());
		if(desc != null && image != null)
			parameters.putString("attachment", 
				"{\"name\":\"" + title + "\"," +
				"\"href\":\""+url+"\"," +
				"\"description\":\""+ Html.fromHtml(desc) + "\"," +
				"\"caption\":\""+ price + " руб."+"\"," +
				"\"media\":[" +
					"{\"type\":\"image\"," +
					"\"src\":\"" + image + "\"," +
					"\"href\":\""+url+"\"}]," +
				"\"properties\":" +
					"{\"Читать полностью\":" +
						"{\"text\":" +	"\"Enter!\"," +
							"\"href\":\""+url+"\"" +
				"}" +
				"}" +
				"}");
		return parameters;
		
	}
	
	private void FBSend() {
		fb.dialog(context, "stream.publish",createBundle(TITLE, DESC, URL, IMAGE,PRICE), new DialogListener() {
			@Override
			public void onFacebookError(FacebookError e) {
				Log.e("onFacebookError", e.toString());
			}
			
			@Override
			public void onError(DialogError e) {
				Log.e("onError", e.toString());
			}
			
			@Override
			public void onComplete(Bundle values) {
				Log.e("onComplete", values.toString());
				
				Map<String, String> flurryParam = new HashMap<String, String>();
				flurryParam.put(Constants.FLURRY_EVENT_PARAM.To.toString(), Constants.FLURRY_TO.FB.toString());
				FlurryAgent.logEvent(Constants.FLURRY_EVENT.Share.toString(), flurryParam);
				
				SendDialog.this.finish();
			}
			
			@Override
			public void onCancel() {
				Log.e("onCancel", "");
				SendDialog.this.finish();
			}
		});
	}
	
	private void VKSend(){
		final VkApp ap = new VkApp(this);
		ap.setListener(new VkDialogListener() {
			
			@Override
			public void onError(String description) {
				Toast.makeText(getApplicationContext(), "При отправке сообщения произошла ошибка", Toast.LENGTH_LONG).show();
				SendDialog.this.finish();
			}
			
			@Override
			public void onComplete(String url) {
				ap.postToWall(DESC, URL, null);
			}

			@Override
			public void onSend(String description) {
				Toast.makeText(getApplicationContext(), "Сообщение успешно отправлено.", Toast.LENGTH_LONG).show();
				
				Map<String, String> flurryParam = new HashMap<String, String>();
				flurryParam.put(Constants.FLURRY_EVENT_PARAM.To.toString(), Constants.FLURRY_TO.VK.toString());
				FlurryAgent.logEvent(Constants.FLURRY_EVENT.Share.toString(), flurryParam);
				
				SendDialog.this.finish();
			}
		});
		if(!ap.hasAccessToken())
			ap.showLoginDialog();
		else
			ap.postToWall(DESC, URL, null);

	}
	
	private void TwitterSend() {
		getTwitter();
	}
	
	public String createTwitterStatus(String news, String url) {
		String tmp = "";
		if(news.length() + url.length() > 140)
			tmp = news.substring(0, news.length()-( news.length() + url.length() - 140)-4) +"... " + url;
		else 
			tmp = news + " " + url;
		return tmp;
			
	}
	
	private Handler m_twitterHandler = new Handler(){
		@Override
		public void handleMessage(Message msg) {
			m_progress.dismiss();
			switch(msg.what){
			case 0:
				Toast.makeText(SendDialog.this, "Сообщение в Twitter отправлено", Toast.LENGTH_SHORT).show();
				
				Map<String, String> flurryParam = new HashMap<String, String>();
				flurryParam.put(Constants.FLURRY_EVENT_PARAM.To.toString(), Constants.FLURRY_TO.Twitter.toString());
				FlurryAgent.logEvent(Constants.FLURRY_EVENT.Share.toString(), flurryParam);
				
				SendDialog.this.finish();
				break;
			case 1:
				Toast.makeText(SendDialog.this, "При отправке сообщения произошла ошибка", Toast.LENGTH_SHORT).show();
				SendDialog.this.finish();
				break;
			case 2:
				Intent intent = new Intent().setClass(SendDialog.this, SendTwitter.class);
				intent.putExtra(EX_TITLE, TITLE);
				intent.putExtra(EX_URL, URL);
				startActivityForResult(intent, 15);
				break;
			}
		}
	};
	
	public void getTwitter() {
		final Twitter twitterConnection = new TwitterFactory().getInstance();
        final OAuthHelp oHelper = new OAuthHelp(this);
		m_progress.show();
		new Thread(new Runnable() {   
			@Override
			public void run() {
			   if (oHelper.hasAccessToken()) {
				   oHelper.configureOAuth(twitterConnection);

				   try  {
					   twitterConnection.updateStatus(createTwitterStatus(TITLE, URL));
					   m_twitterHandler.sendEmptyMessage(0);
				   } catch (TwitterException e) {
					   m_twitterHandler.sendEmptyMessage(1);
					   e.printStackTrace();
				   }
			   } else {
				   m_twitterHandler.sendEmptyMessage(2);
			   }
			}
		}).start();
		
	}

	
	 private void EMailSend() {
		final Intent emailIntent = new Intent(android.content.Intent.ACTION_SEND);
		emailIntent.setType("text/reachtext");
		emailIntent.putExtra(android.content.Intent.EXTRA_EMAIL, new String[]{""});
		emailIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "Смотри что я нашел на Enter");
		
		emailIntent.putExtra(android.content.Intent.EXTRA_TEXT, Html.fromHtml(
					"<h1>" + TITLE +"</h1>" +
					"<p><h5>Цена "	+	PRICE	+ " рублей.</h5></p>" +
					"<br>" + DESC +
					"<p><a href=" + URL + ">" + "Читать полностью..." + "</a></p>"
					
			));
		
		Map<String, String> flurryParam = new HashMap<String, String>();
		flurryParam.put(Constants.FLURRY_EVENT_PARAM.To.toString(), Constants.FLURRY_TO.Email.toString());
		FlurryAgent.logEvent(Constants.FLURRY_EVENT.Share.toString(), flurryParam);
		
		startActivity(Intent.createChooser(emailIntent, "Отправить по E-Mail"));
	}

}
