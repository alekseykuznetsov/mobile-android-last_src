package ru.enter;


import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import com.flurry.android.FlurryAgent;

import ru.enter.utils.HTTPUtils;
import ru.enter.utils.URLManager;
import ru.enter.widgets.HeaderFrameManager;
import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

public class FeedBackActivity extends Activity{

	private Context context;
	private Button send;
	private static final int SUCCESS = 0;
	private static final int EMAIL_ERROR = 1;
	private static final int NEED_USER = 2;
	private static final int NEED_MESSAGE = 3;
	private static final int NEED_EMAIL = 5;
	private EditText name,email,title,mesg;
	private ProgressBar prg;
	private SendMessage sendMessageLoader;
	
	private Handler handler = new Handler(){
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case SUCCESS:
				try {
					JSONObject answer = (JSONObject) new JSONTokener(msg.obj.toString()).nextValue();
					if("true".equals(answer.getString("success")))
					{
						Toast.makeText(context, "Отзыв отправлен", Toast.LENGTH_LONG).show();
						name.setText("");
					    email.setText("");
					    title.setText("");
					    mesg.setText("");
					}
					else
						Toast.makeText(context, "Ошибка при отправке " + answer.getString("success"), Toast.LENGTH_LONG).show();
					
				} catch (JSONException e) {
					e.printStackTrace();
				}
				
				break;
			case EMAIL_ERROR:
				Toast.makeText(context, "Проверьте E-Mail", Toast.LENGTH_LONG).show();
				email.requestFocus();
				break;
			case NEED_USER:
				Toast.makeText(context, "Введите имя", Toast.LENGTH_LONG).show();
				name.requestFocus();
				break;
			case NEED_MESSAGE:
				Toast.makeText(context, "Введите сообщение", Toast.LENGTH_LONG).show();
				mesg.requestFocus();
				break;
			case NEED_EMAIL:
				Toast.makeText(context, "Введите адресс электронной почты", Toast.LENGTH_LONG).show();
				email.requestFocus();
				break;

			default:
				break;
			}
			send.setEnabled(true);
			prg.setVisibility(View.GONE);
			
		};
	};
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.feed_back);
		
		context = this;
		
		LinearLayout frame = (LinearLayout) findViewById(R.id.feed_back_header);
		frame.addView(HeaderFrameManager.getHeaderView(this, "Отправить сообщение", false));
		
		name = (EditText)findViewById(R.id.feed_back_name);
		email = (EditText)findViewById(R.id.feed_back_email);
		title = (EditText)findViewById(R.id.feed_back_title);
		mesg = (EditText)findViewById(R.id.feed_back_msg);
				
		send = (Button)findViewById(R.id.feed_back_send);
		prg = (ProgressBar)findViewById(R.id.progress);
		prg.setVisibility(View.GONE);
		
		
			    
		send.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if(name.getText().length()!=0 &&  mesg.getText().length() != 0 && email.getText().length() !=0)
				{
					if(email.getText().length()>0)
						if(!isEmailValid(email.getText().toString()))
						{
							handler.sendEmptyMessage(EMAIL_ERROR);
							return;
						}
							
						if (sendMessageLoader != null) sendMessageLoader.cancel(true);
						sendMessageLoader = new SendMessage();
						sendMessageLoader.execute();
					/*
					try {
						send.setEnabled(false);
						prg.setVisibility(View.VISIBLE);
						String data = URLEncoder.encode("name", "UTF-8") + 		"=" + URLEncoder.encode(name.getText().toString(), "UTF-8")
							    + 	"&" + URLEncoder.encode("mail", "UTF-8") + 	"=" + URLEncoder.encode(email.getText().toString(), "UTF-8")
							    +	"&" + URLEncoder.encode("theme", "UTF-8") + "=" + URLEncoder.encode(title.getText().toString(), "UTF-8")
							    +	"&" + URLEncoder.encode("text", "UTF-8") + 	"=" + URLEncoder.encode(mesg.getText().toString(), "UTF-8");
						
						RequestManagerThread request = new RequestManagerThread(handler, data, URLManager.getFeedBack(), SUCCESS);
						request.start();
					} catch (UnsupportedEncodingException e) {
						e.printStackTrace();
					}*/
				}
				else if (name.getText().length()==0) {
					handler.sendEmptyMessage(NEED_USER);
				}
				else if (email.getText().length()==0) {
					handler.sendEmptyMessage(NEED_EMAIL);
				}
				else if (mesg.getText().length()==0 && name.getText().length()!=0) {
					handler.sendEmptyMessage(NEED_MESSAGE);
				}
			}
		});
	}
	
	@Override
	protected void onStart()
	{
		super.onStart();
		FlurryAgent.onStartSession(this, "3X5JRBVFV7QWXNP3Q8H2");
	}
	 
	@Override
	protected void onStop()
	{
		super.onStop();		
		FlurryAgent.onEndSession(this);
	}
	
	@Override
	public void onPause() {
		if (sendMessageLoader != null) sendMessageLoader.cancel(true);
		super.onPause();
	}
	
	public static boolean isEmailValid(String email) {
		
	    String expression = "^[\\w\\.-]+@([\\w\\-]+\\.)+[A-Z]{2,4}$";
	    CharSequence inputStr = email;
	    
	    Pattern pattern = Pattern.compile(expression, Pattern.CASE_INSENSITIVE);
	    Matcher matcher = pattern.matcher(inputStr);
	    
	    return matcher.matches();
	}
	
	class SendMessage extends AsyncTask<Void, Void, String> {

		@Override
		protected void onPreExecute() {
			prg.setVisibility(View.VISIBLE);
			send.setEnabled(false);
		}
		
		@Override
		protected String doInBackground(Void... params) {

			String result = null;
			
				try {
					JSONObject object = new JSONObject();	
					object.put("name", name.getText());
					object.put("mail", email.getText());
					object.put("theme", title.getText());
					object.put("text", mesg.getText());
					
					if (object != null) {
						result = HTTPUtils.sendPostJSON(URLManager.getFeedBack(), object);
					}
				} catch (Exception e) {
					
				}
			return result;
		}

		@Override
		protected void onPostExecute(String result) {
			if (isCancelled())
				return;
			if (result == null) {
				Toast.makeText(context, "Ошибка при отправке", Toast.LENGTH_LONG).show();
			} else {
				try {
					JSONObject answer = (JSONObject) new JSONTokener(result).nextValue();
					if ("true".equals(answer.getString("success"))) {
						Toast.makeText(context, "Отзыв отправлен",Toast.LENGTH_LONG).show();
						name.setText("");
						email.setText("");
						title.setText("");
						mesg.setText("");
					} else 
						Toast.makeText(	context,"Ошибка при отправке "+ answer.getString("success"),Toast.LENGTH_LONG).show();

				} catch (JSONException e) {
					e.printStackTrace();
				} catch (Exception e) {
					// TODO: handle exception
					Toast.makeText(context, "Ошибка на сервере ",	Toast.LENGTH_LONG).show();
				}
			}
			send.setEnabled(true);
			prg.setVisibility(View.GONE);
		}
	}
}
