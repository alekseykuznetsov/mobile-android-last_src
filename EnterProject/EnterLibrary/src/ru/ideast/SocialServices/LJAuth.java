package ru.ideast.SocialServices;

import java.net.URI;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import ru.ideast.SocialServices.xmlrpc.XMLRPCClient;
import ru.ideast.SocialServices.xmlrpc.XMLRPCException;
import ru.ideast.SocialServices.xmlrpc.XMLRPCFault;
import ru.ideast.SocialServices.xmlrpc.XMLRPCSerializable;
import ru.ideast.enter.R;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Handler;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class LJAuth{

	private URI uri= URI.create("http://www.livejournal.com/interface/xmlrpc");
	private XMLRPCClient client = new XMLRPCClient(uri);
	private String login;
	private String pass;
	private Context context;
	private SharedPreferences preferences;
	private static final String LJ_LOGIN = "lj_login";
	private static final String LJ_PASS = "lj_pass";
	private static final String LJ_PREFERENCES = "lj_preferences";
	private EditText edit_p,edit_l;
	private Button can,sen;
//	private Animation shake;
	
	public LJAuth(Context context)
	{
		this.context = context;
		preferences = context.getSharedPreferences(LJ_PREFERENCES, Context.MODE_PRIVATE);
		this.login = preferences.getString(LJ_LOGIN, "");
		this.pass = preferences.getString(LJ_PASS, "");

	}
	
//	private String md5(String s) {  
//	    try {  
//	        // Create MD5 Hash  
//	        MessageDigest digest = java.security.MessageDigest.getInstance("MD5");  
//	        digest.update(s.getBytes());  
//	        byte messageDigest[] = digest.digest();  
//	          
//	        // Create Hex String  
//	        StringBuffer hexString = new StringBuffer();  
//	        
//	        for (int i=0; i<messageDigest.length; i++)  
//	            hexString.append(Integer.toHexString(0xFF & messageDigest[i]));  
//	        return hexString.toString();  
//	          
//	    } catch (NoSuchAlgorithmException e) {  
//	        e.printStackTrace();  
//	    }  
//	    return "";  
//	}  
	

//	private String GetChallenge(String ch)
//	{
//		String str = "null";
//		Pattern pat;
//		Matcher mat;
//		pat = Pattern.compile("challenge=(.+)\\}", Pattern.CASE_INSENSITIVE);
//		mat = pat.matcher(ch);
//		if(mat.find()){
//			str = mat.group(1);
//		}
//		return str.trim();
//	}
	public void saveLogin(String login, String pass)
	{
		Editor editor = preferences.edit();
		editor.putString(LJ_LOGIN, login);
		editor.putString(LJ_PASS, pass);
		editor.commit();
	}
//	public void loadAccess() {
//		 this.login = preferences.getString(LJ_LOGIN, "");
//		 this.pass = preferences.getString(LJ_PASS, "");
//		 
//	 }
	public String getLogin()
	{
		return login;
	}
	public String getPass()
	{
		return pass;
	}
	private String chitat = "<p style=\"color:#777777;margin-top:10px\">Опубликовано с помощью <a href=\"http://gazeta.ru/services/#Mobile\">Газета.Ru (Android)</a></p>";
	private String CreateEvent(String title, String url)
	{
		String out= "<h3>Газета.Ru - "+ title+"</h3>" + 
				"<p></p>" +
				"<a href=\"" +	url + "\">Читать полностью...</a> " +
				chitat;
		return out;
		
	}
	private String CreateEvent(String title, String url, String iNTRO)
	{
		String out= "<h3>Газета.Ru - "+ title+"</h3>" + 
				"<p></p>" +
				"<p>" + iNTRO+ "</p>" +
				"<p></p>" +
				"<a href=\"" +	url + "\">Читать полностью...</a> " +
				chitat;
		return out;
		
	}
	public void sendLJ(final EditText login, final EditText pass, final Activity act, final String tITLE, final String uRL, final String iNTRO, Button cancel, Button send)
	{
		this.edit_l = login;
		this.edit_p = pass;
		this.can = cancel;
		this.sen = send;
//		shake = AnimationUtils.loadAnimation(context, R.anim.shake);
		
		        XMLRPCMethod getChal = new XMLRPCMethod("LJ.XMLRPC.getchallenge", new XMLRPCMethodCallback() {
					public void callFinished(Object result) {
						
						XMLRPCMethod log = new XMLRPCMethod("LJ.XMLRPC.postevent", new XMLRPCMethodCallback() {
							public void callFinished(Object result) {
						
								act.setResult(Activity.RESULT_OK);
								act.finish();
								saveLogin(login.getText().toString(), pass.getText().toString());
								
							}
				        });
						Map<String, String> map = new HashMap<String, String>();
				        map.put("username",			login.getText().toString().trim());
				        map.put("password" ,    	pass.getText().toString().trim());
				        map.put("auth_method" ,    	"clear");
//				        map.put("auth_challenge",  	chall);
//				        map.put("auth_response",   	md5(chall + md5(pass.getText().toString().trim())));
				        map.put("ver",             	"1");
				        map.put("year",           	Calendar.getInstance().get(Calendar.YEAR) + "");
				        map.put("mon",            	(Calendar.getInstance().get(Calendar.MONTH)+1)  + "");
				        map.put("day",            	Calendar.getInstance().get(Calendar.DAY_OF_MONTH) + "");
				        map.put("hour",           	Calendar.getInstance().get(Calendar.HOUR_OF_DAY) + "");
				        map.put("min",            	Calendar.getInstance().get(Calendar.MINUTE) + "");
				        map.put("subject",         	tITLE);
				        map.put("event",         	iNTRO == null ? CreateEvent(tITLE, uRL):CreateEvent(tITLE, uRL, iNTRO) );
				        map.put("security",        	"public");
				        Object[] params2 = {
				        		map
		    	        };
				        log.call(params2);
				        
					}
		        });
		        
		        Object[] params1 = {
    	        };
		        getChal.call(params1);
}
		        
			
	class Person implements XMLRPCSerializable {
		private String firstName;
		private String lastName;
		public Person(String firstName, String lastName) {
			this.firstName = firstName;
			this.lastName = lastName;
		}
		public Object getSerializable() {
			Map<String, String> map = new HashMap<String, String>();
			map.put("firstName", firstName);
			map.put("lastName", lastName);
			return map;
		}
	}
	
//	class TestAdapter extends ArrayAdapter<String> {
//		private LayoutInflater layouter;
//		private int layoutId;
//		public TestAdapter(Context context, int layoutId, int textId) {
//			super(context, layoutId, textId);
//			this.layoutId = layoutId;
//			layouter = LayoutInflater.from(LJAuth.this);
//		}
//		@Override
//		public View getView(int position, View convertView, ViewGroup parent) {
//			View view = layouter.inflate(layoutId, null);
//			TextView title = (TextView) view.findViewById(R.id.title);
//			TextView params = (TextView) view.findViewById(R.id.params);
//			String string = getItem(position);
//			String[] arr = string.split(";");
//			title.setText(arr[0]);
//			if (arr.length == 2) {
//				params.setText(arr[1]);
//			} else {
//				params.setVisibility(View.GONE);
//			}
//			return view;
//		}
//	}

	interface XMLRPCMethodCallback {
		void callFinished(Object result);
	}
	
	class XMLRPCMethod extends Thread {
		private String method;
		private Object[] params;
		private Handler handler;
		private XMLRPCMethodCallback callBack;
		public XMLRPCMethod(String method, XMLRPCMethodCallback callBack) {
			this.method = method;
			this.callBack = callBack;
			handler = new Handler();
		}
		public void call() {
			call(null);
		}
		public void call(Object[] params) {
			this.params = params;
			start();
		}
		@Override
		public void run() {
    		try {
    			
    			final Object result = client.callEx(method, params);
    			
    			handler.post(new Runnable() {
					public void run() {
						callBack.callFinished(result);
					}
    			});
    		} catch (final XMLRPCFault e) {
    			handler.post(new Runnable() {
					public void run() {
						Toast.makeText(context, e.getFaultString(), Toast.LENGTH_SHORT).show();
//						edit_l.startAnimation(shake);
						can.setEnabled(true);
//						edit_p.startAnimation(shake);
						sen.setEnabled(true);
						
					}
    			});
    		} catch (final XMLRPCException e) {
    			handler.post(new Runnable() {
					public void run() {		
//						edit_l.startAnimation(shake);
						can.setEnabled(true);
//						edit_p.startAnimation(shake);
						sen.setEnabled(true);
					}
    			});
    		}
		}
	}
}
