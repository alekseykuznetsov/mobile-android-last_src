package ru.enter.dialogs;



import ru.enter.R;
import ru.enter.utils.Log;
import ru.enter.utils.NetworkManager;
import ru.ideast.SocialServices.OAuthHelp;
import ru.ideast.SocialServices.SendTwitter;
import ru.ideast.SocialServices.VkApp;
import ru.ideast.SocialServices.VkApp.VkDialogListener;
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

public class SocialServicesDialog extends Activity {

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


	
	private Button likefb_btn;
	private Button likevk_btn;
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
		setContentView(R.layout.social_services_dialog);
		
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


        initSendDialog();
        
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
	
	private void initSendDialog()
	{
		likefb_btn = (Button)findViewById(R.id.social_services_dialog_likefb);
		likefb_btn.setOnClickListener(new OnClickListener() {	
			@Override
			public void onClick(View v) {
				if(NetworkManager.isConnected(context))
					Toast.makeText(context,"stub",Toast.LENGTH_SHORT).show();
						
			}
		});
		likevk_btn = (Button)findViewById(R.id.social_services_dialog_likevk);
		likevk_btn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if(NetworkManager.isConnected(context))
					Toast.makeText(context,"stub",Toast.LENGTH_SHORT).show();
						
			}
		});
		
		facebook_btn = (Button)findViewById(R.id.social_services_dialog_fb);
		facebook_btn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if(NetworkManager.isConnected(context))
					FBSend();
						
			}
		});
		vk_btn = (Button)findViewById(R.id.social_services_dialog_vk);
		vk_btn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if(NetworkManager.isConnected(context))
					VKSend();
						
			}
		});		
		twitter_btn = (Button)findViewById(R.id.social_services_dialog_twitter);
		twitter_btn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if(NetworkManager.isConnected(context))
					TwitterSend();
			}
		});

		e_mail_btn = (Button)findViewById(R.id.social_services_dialog_email);
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
		cancel_btn = (Button)findViewById(R.id.social_services_dialog_exit);
		cancel_btn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				finish();
			}
		});
		
		

		
	}
	private Bundle createBundle(String title, String desc, String url, String image, String price)
	{
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
//		if(desc == null && image != null)
//			parameters.putString("attachment", 
//					"{\"name\":\"" + title + "\"," +
//					"\"href\":\""+url+"\"," +
//					"\"media\":[" +
//						"{\"type\":\"image\",\"src\":\"" + image + "\"," +
//						"\"href\":\""+url+"\"}]," +
//					"\"properties\":" +
//						"{\"Читать полностью\":" +
//							"{\"text\":" +
//								"\"Enter!\"," +
//								"\"href\":\""+url+"\"" +
//					"}" +
//					"}" +
//					"}");
//		if(desc != null && image == null)
//			parameters.putString("attachment", 
//					"{\"name\":\"" + title + "\"," +
//					"\"href\":\""+url+"\"," +
//					"\"description\":\"" + desc +"\"," +
//					"\"properties\":" +
//						"{\"Читать полностью\":" +
//							"{\"text\":" +
//								"\"Enter!\"," +
//								"\"href\":\""+url+"\"" +
//					"}" +
//					"}" +
//					"}");
//		if(desc == null && image == null)
//			parameters.putString("attachment", 
//					"{\"name\":\"" + title + "\"," +
//					"\"href\":\""+url+"\"," +
//					"\"properties\":" +
//						"{\"Читать полностью\":" +
//							"{\"text\":" +
//								"\"Enter!\"," +
//								"\"href\":\""+url+"\"" +
//					"}" +
//					"}" +
//					"}");
		return parameters;
		
	}        /**
     * LJ
     */
	private void FBSend()
	{
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
				SocialServicesDialog.this.finish();
			}
			@Override
			public void onCancel() {
				Log.e("onCancel", "");
				SocialServicesDialog.this.finish();
			}
		});
	}
	
	private void VKSend(){
		final VkApp ap = new VkApp(this);
		ap.setListener(new VkDialogListener() {
			
			@Override
			public void onError(String description) {
				Toast.makeText(getApplicationContext(), "При отправке сообщения произошла ошибка", Toast.LENGTH_LONG).show();
				SocialServicesDialog.this.finish();
			}
			
			@Override
			public void onComplete(String url) {
				ap.postToWall(DESC, URL, null);
			}

			@Override
			public void onSend(String description) {
				Toast.makeText(getApplicationContext(), "Сообщение успешно отправлено.", Toast.LENGTH_LONG).show();
				SocialServicesDialog.this.finish();
			}
		});
		if(!ap.hasAccessToken())
			ap.showLoginDialog();
		else
			ap.postToWall(DESC, URL, null);

		
	}
	
	private void TwitterSend()
	{
		getTwitter();
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
	
	private Handler m_twitterHandler = new Handler(){
		@Override
		public void handleMessage(Message msg) {
			m_progress.dismiss();
			switch(msg.what){
			case 0:
				Toast.makeText(SocialServicesDialog.this, "Сообщение в Twitter отправлено", Toast.LENGTH_SHORT).show();
				SocialServicesDialog.this.finish();
				break;
			case 1:
				Toast.makeText(SocialServicesDialog.this, "При отправке сообщения произошла ошибка", Toast.LENGTH_SHORT).show();
				SocialServicesDialog.this.finish();
				break;
			case 2:
				Intent intent = new Intent().setClass(SocialServicesDialog.this, SendTwitter.class);
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
		new Thread(new Runnable() 
		{   
			@Override
			public void run() 
			{
			   if (oHelper.hasAccessToken()) 
			   {
				   oHelper.configureOAuth(twitterConnection);

				   try 
				   {
					   twitterConnection.updateStatus(createTwitterStatus(TITLE, URL));
					   m_twitterHandler.sendEmptyMessage(0);
				   }
				   catch (TwitterException e) 
				   {
					   m_twitterHandler.sendEmptyMessage(1);
					   e.printStackTrace();
				   }
			   } 
			   else 
			   {
				   m_twitterHandler.sendEmptyMessage(2);
			   }
			}
		}).start();
		
	}

	
	 private void EMailSend()
	 {
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
		
		startActivity(Intent.createChooser(emailIntent, "Отправить по E-Mail"));
	}

}
