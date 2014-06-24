package ru.enter;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

import org.json.JSONException;
import org.json.JSONObject;

import com.flurry.android.FlurryAgent;
import com.google.analytics.tracking.android.EasyTracker;

import ru.enter.DataManagement.PersonData;
import ru.enter.beans.PersonBean;
import ru.enter.parsers.PersonParser;
import ru.enter.parsers.RegistrationParser;
import ru.enter.parsers.UserAuthParser;
import ru.enter.utils.Constants;
import ru.enter.utils.Log;
import ru.enter.utils.PreferencesManager;
import ru.enter.utils.RequestManagerThread;
import ru.enter.utils.ResponceServerException;
import ru.enter.utils.URLManager;
import ru.enter.utils.Utils;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

public class AuthorizationActivity extends Activity implements OnClickListener,Runnable{
	
	public static enum RunType {
		checkout,
		personal_account
	}
	
	public static final String RUNTYPE = "runtype";
	private final int REQUEST_CODE_REG = 0;
	private final int REQUEST_CODE_AUTH = 1;
	private final int REQUEST_CODE_PARSE_USER_DATA = 2;
	private final int REQUEST_CODE_REMEMBER_PASSW = 3;
	
	private RunType mCurrentRunType;
	private EditText mLoginET,mPasswET,mNameET,mEmailET;
	private Button mLoginB;
	private Dialog mProgressDialog;
	private boolean isInterupt = false;
	private Button continueBtn;
	private Button mRememberPassw;
	private Button mSendButton;
	private WindowManager.LayoutParams mLayoutParams;
	private WindowManager mWindowManager;
	private PersonBean pData;
	private String _token;
	
	private String loginForGA; 
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		EasyTracker.getInstance().setContext(this);
		setContentView(R.layout.authorization_activity);
		
		mLayoutParams = new WindowManager.LayoutParams();
		mLayoutParams.height = WindowManager.LayoutParams.FILL_PARENT;
		mLayoutParams.width = WindowManager.LayoutParams.FILL_PARENT;
		mLayoutParams.flags = WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON;
		mLayoutParams.format = PixelFormat.TRANSLUCENT;
		mLayoutParams.windowAnimations = android.R.anim.fade_in;
		mLayoutParams.type = WindowManager.LayoutParams.TYPE_APPLICATION;
		
		mWindowManager = getWindowManager();
		
		TextView title = (TextView)findViewById(R.id.authorization_activity_title_tv);
		title.setTypeface(Utils.getTypeFace(this));
		
		mLoginET = (EditText)findViewById(R.id.authorization_activity_login_et);
		mPasswET = (EditText)findViewById(R.id.authorization_activity_passw_et);
		mNameET = (EditText)findViewById(R.id.authorization_activity_name_et);
		mEmailET = (EditText)findViewById(R.id.authorization_activity_email_et);
		
		mSendButton = (Button)findViewById(R.id.authorization_activity_otpravit2_Button);
		mSendButton.setOnClickListener(this);
		
		mLoginB = (Button)findViewById(R.id.authorization_activity_login_b);
		mLoginB.setOnClickListener(this);
		
		mRememberPassw = (Button)findViewById(R.id.authorization_activity_remember_passw);
		mRememberPassw.setOnClickListener(this);
		
		_token="";
				
		Bundle bundle = getIntent().getExtras();
		if(bundle!=null){
			RunType runType = RunType.valueOf(bundle.getString(RUNTYPE));
			mCurrentRunType = runType;
		}
		
		continueBtn = (Button) findViewById(R.id.authorization_activity_continue);
		if(!RunType.checkout.equals(mCurrentRunType))
			continueBtn.setVisibility(View.GONE);
		else
			continueBtn.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					PreferencesManager.setUserId(0);
					Intent intent = new Intent();
					intent.setClass(AuthorizationActivity.this,CheckoutActivity.class);
					intent.putExtras(getIntent().getExtras());
					startActivity(intent);
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

	private Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			isInterupt = false;
			if(msg.what == REQUEST_CODE_REMEMBER_PASSW){
				String result = (String) msg.obj;
				RegistrationParser parser = new RegistrationParser(result);
				try {
					parser.parse();
					Toast.makeText(AuthorizationActivity.this,"Спасибо! Ваш новый пароль отправлен!", Toast.LENGTH_SHORT).show();
					EasyTracker.getTracker().sendEvent("user/restore-password", "buttonPress", loginForGA, null);
				} catch (ResponceServerException e) {
					Toast.makeText(AuthorizationActivity.this, e.getErrorMsg(), Toast.LENGTH_SHORT).show();
				}catch (Exception e) {
					Toast.makeText(AuthorizationActivity.this, "Непредвиденная ошибка.", Toast.LENGTH_SHORT).show();
				}
				hideDialog();
			}else if (msg.what == REQUEST_CODE_PARSE_USER_DATA) {
				hideDialog();
				boolean result = (Boolean) msg.obj;
				if (!result) {
					PersonData.getInstance().setPersonBean(new PersonBean());
					Toast.makeText(getApplicationContext(),
							"Ошибка при авторизации.Попробуйте еще раз",
							Toast.LENGTH_SHORT).show();
				} else {
					//сохраняю id пользователя, который уже авторизовался
					pData=PersonData.getInstance().getPersonBean();
					//long personId = PersonData.getInstance().getPersonBean().getId();
					PreferencesManager.setUserId(pData.getId());
					PreferencesManager.setToken(_token);
					PreferencesManager.setUserName(pData.getName());
					PreferencesManager.setUserLastName(pData.getLastName());
					PreferencesManager.setUserMobile(pData.getMobile());
					PreferencesManager.setUserEmail(pData.getEmail());
					String personData =  PreferencesManager.getUserEmail();
					if(TextUtils.isEmpty(personData)) personData = PreferencesManager.getUserMobile();
					EasyTracker.getTracker().sendEvent("user/login", "buttonPress", personData, null);
					if (RunType.checkout.equals(mCurrentRunType)) {
						Intent intent = new Intent();
						intent.setClass(AuthorizationActivity.this,CheckoutActivity.class);
						intent.putExtras(getIntent().getExtras());
						startActivity(intent);
					}else{
						finish();
					}
				}
				AuthorizationActivity.this.finish();
			}else if (msg.what == REQUEST_CODE_REG){
				String result = (String)msg.obj;
				if (!TextUtils.isEmpty(result)) {
					RegistrationParser parser = new RegistrationParser(result);
					try {
						long id = parser.parse();
						if (id != 0) {
							Log.d("Authorisation", String.valueOf(id));
							JSONObject userData = new JSONObject(result);
							JSONObject resData = userData.getJSONObject("result");
							final String passwd = resData.optString("password");
							final String login = mEmailET.getEditableText().toString();
							EasyTracker.getTracker().sendEvent("user/register", "buttonPress", login, null);
							String message;
							if(passwd.length() == 0)
								message = "Пароль будет выслан на указанный Вами телефон";
							else
								message = "Ваш пароль:\n" + passwd + "\n\nОн также будет выслан на указанный Вами телефон";
							PersonData.getInstance().getPersonBean().setId(id);
							
							FlurryAgent.logEvent(Constants.FLURRY_EVENT.Registration.toString());
							
							AlertDialog.Builder builder = new AlertDialog.Builder(AuthorizationActivity.this);
							builder.setTitle("Вы успешно зарегистрированы")
								   .setMessage(message)
							       .setCancelable(false)
							       .setPositiveButton("Продолжить", new DialogInterface.OnClickListener() {
							           public void onClick(DialogInterface dialog, int id) {
							        	   
											new Thread(new Runnable() {
												
												@Override
												public void run() {
													String parsedResult = new UserAuthParser(AuthorizationActivity.this).parse(URLManager.getToken(login, passwd));
													
													// TODO check EventList from response to handle user coupon status
													// if needed. Get this info from product owner
//													handleObtainingCoupon();
													
													Message msg = new Message();
													msg.what = REQUEST_CODE_AUTH;
													msg.obj = parsedResult;
													handler.sendMessage(msg);
													
												}
											}).start();
							           }
							       }).create().show();

						}
					} catch (ResponceServerException e) {
						hideDialog();
						Toast.makeText(AuthorizationActivity.this,
								e.getErrorMsg(), Toast.LENGTH_SHORT).show();
					}catch (Exception e) {
						Toast.makeText(AuthorizationActivity.this, "Непредвиденная ошибка.", Toast.LENGTH_SHORT).show();
						hideDialog();
					}
				}else{
					Toast.makeText(AuthorizationActivity.this, "Непредвиденная ошибка.", Toast.LENGTH_SHORT).show();
					hideDialog();
				}
			}else {
				String result = (String) msg.obj;
				if (TextUtils.isEmpty(result)) {
					hideDialog();
					return;
				}
				
				if (result.split("-").length >= 5){
//					PersonData.getInstance().setToken(result);
					//PreferencesStorage.getInstance(AuthorizationActivity.this).setToken(result);
					_token=result;
				}else{
					Toast.makeText(getApplicationContext(), result, Toast.LENGTH_SHORT).show();
				}
				
				Thread thread = new Thread(AuthorizationActivity.this);
				thread.start();
			}
		}
	};
	private FrameLayout mProgressFrameLayout;

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.authorization_activity_otpravit2_Button://зарегистрироваться
			String name = mNameET.getEditableText().toString();
			String email = mEmailET.getEditableText().toString();
			if(!name.equals("")){
				if(!email.equals("")){
						try{
							JSONObject object = new JSONObject();
							object.put("first_name", name);
//							if(email.contains("@"))
//								object.put("email", email);
//							else
							object.put("mobile", email);
							RequestManagerThread load = new RequestManagerThread(handler, object, URLManager.getUserCreate(), REQUEST_CODE_REG);
							load.start();
							showDialog();
						}catch (JSONException e) {
							
						}
						//regisration
				}else{
					Toast.makeText(this, "Номер обязателен для ввода", Toast.LENGTH_SHORT).show();
					mEmailET.requestFocus();
				}
			}else{
				Toast.makeText(this, "Имя и фамилия обязательны для ввода", Toast.LENGTH_SHORT).show();
				mNameET.requestFocus();
			}
			break;
		case R.id.authorization_activity_remember_passw:
			showRememberPaswwDialog();
			break;
		case R.id.authorization_activity_login_b://логин
			final String login = mLoginET.getEditableText().toString();
			final String passw = mPasswET.getEditableText().toString();
			
//			final String login = "r1@m.ru";
//			final String passw = "t5k3t5";
//			final String login = "89195401823";
//			final String passw = "qwerty";
//			final String login = "kolkiy1@gmail.com";
//			final String passw = "3o37da";
//			final String login = "89250429309";
//			final String passw = "123456";
//			final String login = "qweqwe@qw.ru";
//			final String passw = "752u4i";
			if(!login.equals("")){
				if(!passw.equals("")){
					showDialog();
					new Thread(new Runnable() {
						
						@Override
						public void run() {
							String parsedResult = new UserAuthParser(AuthorizationActivity.this).parse(URLManager.getToken(login, passw));
							
							// TODO check EventList from response to handle user coupon status
							// if needed. Get this info from product owner
//							handleObtainingCoupon();
							
							
							Message msg = new Message();
							msg.what = REQUEST_CODE_AUTH;
							msg.obj = parsedResult;
							handler.sendMessage(msg);
							
						}
					}).start();
				}else{
					mPasswET.requestFocus();
					Toast.makeText(this, "Введите пароль", Toast.LENGTH_SHORT).show();
				}
			}else{
				mLoginET.requestFocus();
				Toast.makeText(this, "Введите логин(email или номер телефона)", Toast.LENGTH_SHORT).show();
			}
			break;			
		default:
			break;
		}
	}
	
	@Override
	public void onBackPressed() {
		isInterupt = true;
		super.onBackPressed();
	}
	
	@Override
	protected Dialog onCreateDialog(int id) {
		Context context = AuthorizationActivity.this;
		mProgressDialog = new Dialog(context, android.R.style.Theme_Translucent_NoTitleBar_Fullscreen);
//		mProgressDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		
		FrameLayout progressFrameLayout = new FrameLayout(context);
		progressFrameLayout.setLayoutParams(new FrameLayout.LayoutParams(FrameLayout.LayoutParams.FILL_PARENT, FrameLayout.LayoutParams.FILL_PARENT));
		progressFrameLayout.setBackgroundColor(Color.argb(50, 10, 10, 10));
		FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		params.gravity = Gravity.CENTER;
		ProgressBar bar = new ProgressBar(context);
		bar.setLayoutParams(params);
		bar.setIndeterminateDrawable(getResources().getDrawable(R.drawable.progress_spin));
		progressFrameLayout.addView(bar);
		mProgressDialog.setContentView(progressFrameLayout);
		return mProgressDialog;
	}
	
	private void showRememberPaswwDialog(){
        final EditText editText = new EditText(this);
        editText.setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT,LayoutParams.WRAP_CONTENT));
        editText.setHint("Введите логин (почту или телефон)");
        AlertDialog.Builder dlg =  new AlertDialog.Builder(this);
        
            dlg.setIcon(android.R.drawable.ic_dialog_alert)
            .setTitle("Напомнить пароль")
            .setView(editText)
            .setPositiveButton("Отправить", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int whichButton) {
                	final String login = editText.getEditableText().toString();
                	loginForGA = login;
                	if(!login.equals("")){
                		showDialog();
                		new Thread(new Runnable() {
							public void run() {
								try{
									String url = URLManager.getUserResetPassword(login);
									URL feedUrl = new URL(url);
									URLConnection uc = feedUrl.openConnection();
									InputStream is = uc.getInputStream();
									String data = Utils.getTextFromInputStream(is);
									
									Message msg = new Message();
									msg.what = REQUEST_CODE_REMEMBER_PASSW;
									msg.obj = data;
									handler.sendMessage(msg);
								}catch (MalformedURLException e) {}
								catch (IOException e) {}	
							}
						}).start();
                	}
                }
            })
            .setNegativeButton("Отмена", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int whichButton) {

                }
            })
            .create().show();
	}
	

	@Override
	public void run() {
		//String token = PreferencesStorage.getInstance(this).getToken();
		if (!TextUtils.isEmpty(_token)){
			
			PersonParser parser = new PersonParser(AuthorizationActivity.this);
			Object res = parser.parse(URLManager.getUser(_token));
			if(!isInterupt)
				handler.sendMessage(handler.obtainMessage(REQUEST_CODE_PARSE_USER_DATA, res==null ? false : res));
		}
	}
	
//	private void handleObtainingCoupon(){
//		PreferencesManager.showGetCouponDialog(false);
//		runOnUiThread(new Runnable() {
//			public void run() {
//				Toast.makeText(AuthorizationActivity.this, "Вы получили купон", Toast.LENGTH_SHORT).show();
//			}
//		});
//	}
	
	private void showDialog(){
		Context context = AuthorizationActivity.this;
		mProgressFrameLayout = new FrameLayout(context);
		mProgressFrameLayout.setLayoutParams(new FrameLayout.LayoutParams(FrameLayout.LayoutParams.FILL_PARENT, FrameLayout.LayoutParams.FILL_PARENT));
		mProgressFrameLayout.setBackgroundColor(Color.argb(50, 10, 10, 10));
		FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		params.gravity = Gravity.CENTER;
		ProgressBar bar = new ProgressBar(context);
		bar.setLayoutParams(params);
		bar.setIndeterminateDrawable(getResources().getDrawable(R.drawable.progress_spin));
		mProgressFrameLayout.addView(bar);
		mProgressFrameLayout.setClickable(true);
		mWindowManager.addView(mProgressFrameLayout, mLayoutParams);
	}
	
	private void hideDialog(){
		try{
			mWindowManager.removeView(mProgressFrameLayout);
		}catch (IllegalArgumentException e) {}
	}
}
