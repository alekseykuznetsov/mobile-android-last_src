package ru.ideast.SocialServices;

import java.io.IOException;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import ru.enter.ImageManager.ImageDownloader;
import ru.ideast.enter.R;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.net.Uri;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

public class VkApp {
    //constants for OAUTH AUTHORIZE in Vkontakte
	private static final int APP_ID = 2984251;
	
	private static final int CAPTHCA = 2;
	private static final int ERROR = 1;
	private static final int SUCCESS = 0;
	
	public static final String CALLBACK_URL = "http://api.vk.com/blank.html";
	private static final String OAUTH_AUTHORIZE_URL = "http://oauth.vk.com/authorize" +
					"?client_id="+APP_ID+"" +
					"&scope=8192" +
					"&redirect_uri=http://api.vk.com/blank.html" +
					"&display=touch" +
					"&response_type=token"; 
		 
	private Context _context;
	private VkDialogListener _listener;
	private VkSession _vkSess;
	
	private String VK_API_URL = "https://api.vk.com/method/";
	private String VK_POST_TO_WALL_URL = VK_API_URL + "wall.post?";
	private String VK_GET_USER_INFO = VK_API_URL +"getProfiles?";
	private ProgressDialog d;
	public VkApp(){}
	
	public VkApp(Context context){
		_context = context;
		_vkSess = new VkSession(_context);
	}
	private Handler handler = new Handler(){
		public void handleMessage(android.os.Message msg) {
			try {
				d.dismiss();
			} catch (Exception e) {
			}
			switch (msg.what) {
			case SUCCESS:
				_listener.onSend("");
				break;
			case ERROR:
				
				_listener.onError("");
				break;
			case CAPTHCA:
				createCaptchaDialog((CaptchaBean) msg.obj);
				break;

			default:
				break;
			}
		};
	};
	private void createCaptchaDialog(final CaptchaBean captchaBean){
		AlertDialog.Builder d = new AlertDialog.Builder(_context);
		View captcha = LayoutInflater.from(_context).inflate(R.layout.captcha, null);
		ImageView im = (ImageView)captcha.findViewById(R.id.capthca_img);
		final EditText ed = (EditText)captcha.findViewById(R.id.capthca_edit);
		
		d.setView(captcha);
		ImageDownloader iDownloader = new ImageDownloader(_context);
		iDownloader.download(captchaBean.getUrl(), im);
		d.setTitle("Введите код с картинки");
		d.setPositiveButton("Ок", new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				CaptchaBean capth = new CaptchaBean();
				capth.setKey(ed.getText().toString());
				capth.setSig(captchaBean.getSig());
				postToWall("","", capth);
			}
		});
		d.setNegativeButton("Отмена", new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				handler.sendEmptyMessage(ERROR);
			}
		});
		d.create().show();
	}
	public void setListener(VkDialogListener listener) { _listener = listener; }
	
	public void showLoginDialog(){
	    new VkDialog(_context,OAUTH_AUTHORIZE_URL,_listener).show();	
	}
	
	private void postResponseParser(String str){
		JSONObject jsonObj = null;
		try {
		   jsonObj = new JSONObject(str);
		   JSONObject errorObj = null;
		   
		   if( jsonObj.has("error") ) {
		       errorObj = jsonObj.getJSONObject("error");
		       int errCode = errorObj.getInt("error_code");
		       if( errCode == 14){
		    	   CaptchaBean b = new CaptchaBean();
		    	   b.setSig(errorObj.getLong("captcha_sid"));
		    	   b.setUrl(errorObj.getString("captcha_img"));
		    	   handler.sendMessage(handler.obtainMessage(CAPTHCA, b));
		       }else{
		    	   handler.sendEmptyMessage(ERROR);
		       }
		   }else if(jsonObj.has("response")){
			   handler.sendEmptyMessage(SUCCESS);
		   }
		}catch (JSONException e) {
			 handler.sendEmptyMessage(ERROR);
		}
	}
	public void postToWall(final String message, final String link, final CaptchaBean captchaBean) {
        d = new ProgressDialog(_context);
        d.setMessage("Подождите...");
        d.show();
        Thread t = new Thread(new Runnable() {
			
			@Override
			public void run() {
				String[] params = _vkSess.getAccessToken();
				
				String accessToken = params[0];
				String ownerId = params[2];
				String requestString = VK_POST_TO_WALL_URL;
				requestString += 
					"&owner_id"+ownerId+
					"&message="+Uri.encode(message);
				if(link != null && !"".equals(link))
					requestString +=
					"&attachments="+Uri.encode(link);
				requestString += "&access_token="+accessToken;
				
				if(captchaBean != null)
					requestString += 
					"&captcha_sid="+captchaBean.getSig()+
					"&captcha_key="+captchaBean.getKey();

				HttpClient client = new DefaultHttpClient();
		        HttpGet request = new HttpGet(requestString);
		        
		        try {
		            HttpResponse response = client.execute(request);
		            HttpEntity entity = response.getEntity();

		            String responseText = EntityUtils.toString(entity);
		            Log.i("VK_POST_TO_WALL_URL", requestString);
		            Log.i("responseText", responseText);
		            postResponseParser(responseText);
		            
		        }
		        catch(ClientProtocolException cexc){
		        	cexc.printStackTrace();
		        	handler.sendEmptyMessage(ERROR);
		        }
		        catch(IOException ioex){
		        	handler.sendEmptyMessage(ERROR);
		        	ioex.printStackTrace();
		        }
			}
		});
        t.start();
		
	}
	public Object getUserInfo(){
		
		String[] params = _vkSess.getAccessToken();
		
		String accessToken = params[0];
		String ownerId = params[2];
		
		VK_GET_USER_INFO +="uid="+ownerId+"&access_token="+ accessToken;
		HttpClient client = new DefaultHttpClient();
        HttpGet request = new HttpGet(VK_GET_USER_INFO);
        
        try {
            HttpResponse response = client.execute(request);
            HttpEntity entity = response.getEntity();

            String responseText = EntityUtils.toString(entity);
            VKParser parser = new VKParser(responseText);
            return parser.parse();
            
        }
        catch(ClientProtocolException cexc){
        	cexc.printStackTrace();
        }
        catch(IOException ioex){
        	ioex.printStackTrace();
        }
        catch (Exception e) {
        	e.printStackTrace();
		}
		return null;
	}
	
	public String[] getAccessToken(String url) {
		String[] query = url.split("#");
		String[] params = query[1].split("&");
		return params;
	}
	
	public boolean hasAccessToken() {
		String[] params = _vkSess.getAccessToken();
		if( params != null ) {
			long accessTime = Long.parseLong(params[3]); 
			long currentTime = System.currentTimeMillis();
			long expireTime = (currentTime - accessTime) / 1000;
			
			if( params[0].equals("") & params[1].equals("") & params[2].equals("") & Long.parseLong(params[3]) ==0 ){
				return false;
			}
			else if( expireTime >= Long.parseLong(params[1]) ) {
				return false;
			}
			else {
				return true;
			}
		}
		return false;
	}
	
	public void saveAccessToken(String accessToken, String expires, String userId) {
		_vkSess.saveAccessToken(accessToken, expires, userId);
	}
	
	public void resetAccessToken() { _vkSess.resetAccessToken(); }
	
	public interface VkDialogListener {
		void onComplete(String url);
		void onError(String description);
		void onSend(String description);
	}
}