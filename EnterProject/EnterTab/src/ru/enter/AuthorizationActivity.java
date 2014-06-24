package ru.enter;

import org.json.JSONObject;

import com.flurry.android.FlurryAgent;
import com.google.analytics.tracking.android.EasyTracker;

import ru.enter.beans.PersonBean;
import ru.enter.dialogs.RememmberPasswordDialog;
import ru.enter.dialogs.alert.AuthFailDialogFragment;
import ru.enter.dialogs.alert.AuthRegistrationSuccessDialogFragment;
import ru.enter.parsers.PersonInfoParser;
import ru.enter.parsers.UserAuthorizationParser;
import ru.enter.utils.Constants;
import ru.enter.utils.HTTPUtils;
import ru.enter.utils.PreferencesManager;
import ru.enter.utils.ResponceServerException;
import ru.enter.utils.URLManager;
import ru.enter.utils.Utils;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Paint;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

public class AuthorizationActivity extends Activity implements OnClickListener {

	private static final int BASKET_MODE = 1;
	private static final int ORDER_ERROR_MODE = 2;
	private static final int MENU_MODE = 3;
	private static final int PERSONAL_ERROR = 4;
	
	private static final String WHERE = "WHERE_WE_NOW";

	private int mWhereFlag;

	private FrameLayout mProgress;
	private EditText mLogin;
	private EditText mPassword;
	private EditText mRegName;
	private EditText mRegLogin;

	private AuthTask authLoader;
	private UserInfoTask userInfoLoader;
	private RegistrationTask registrationTaskLoader;
	
	//For GA
	private String mob_mail;
	
	
	public static void launch (Activity thisActivity) {
		Intent intent = new Intent(thisActivity, AuthorizationActivity.class);
		Bundle extras = new Bundle();
		extras.putString(WHERE, thisActivity.getClass().getSimpleName());
		intent.putExtras(extras);
		thisActivity.startActivity(intent);
	}

	@Override
	public void onCreate(Bundle bundle) {
		super.onCreate(bundle);
		EasyTracker.getInstance().setContext(this);
		setContentView(R.layout.auth_ac);
		getLaunchMode();

		mProgress = (FrameLayout) findViewById(R.id.auth_ac_registration_progress_frame);

		getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
		
		setTitle("Введите данные для входа в аккаунт");
		mLogin = (EditText) findViewById(R.id.auth_ac_edittext_email);
		mPassword = (EditText) findViewById(R.id.auth_ac_edittext_password);

		mRegName = (EditText) findViewById(R.id.auth_ac_edittext_registration_edittext_name);
		mRegLogin = (EditText) findViewById(R.id.auth_ac_registration_edittext_email);

		TextView rememberPass = (TextView) findViewById(R.id.auth_ac_label_forgot);
		rememberPass.setPaintFlags(rememberPass.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);

		Button signIn = (Button) findViewById(R.id.auth_ac_btn_signin);
		Button withoutReg = (Button) findViewById(R.id.auth_ac_registration_button_without_reg);

		// кнопка войти без регистрации только при переходе из корзины
		if (mWhereFlag != BASKET_MODE) {
			View empty_view_left = (View) findViewById(R.id.auth_ac_registration_view_balance_left);
			View empty_view_right = (View) findViewById(R.id.auth_ac_registration_view_balance_rigth);
			empty_view_left.setVisibility(View.VISIBLE);
			empty_view_right.setVisibility(View.VISIBLE);
			withoutReg.setVisibility(View.GONE);
		}

		Button send = (Button) findViewById(R.id.auth_ac_registration_button_reg);

		signIn.setOnClickListener(this);
		withoutReg.setOnClickListener(this);
		send.setOnClickListener(this);
		rememberPass.setOnClickListener(this);
	}
	
	private void getLaunchMode() {
		String from = getIntent().getExtras().getString(WHERE);
		
		if (from.equals(BasketActivity.class.getSimpleName())) {
			mWhereFlag = BASKET_MODE;
		} else if (from.equals(OrderActivity.class.getSimpleName()) || from.equals(OrderCompleteActivity.class.getSimpleName())) {
			mWhereFlag = ORDER_ERROR_MODE;
		} else if (from.equals(PersonalActivity.class.getSimpleName())) {
			mWhereFlag = PERSONAL_ERROR;
		} else {
			mWhereFlag = MENU_MODE;
		}
	}
	
	private void showProgress() {
		mProgress.setVisibility(View.VISIBLE);
	}

	private void hideProgress() {
		mProgress.setVisibility(View.GONE);
	}

	@Override
	protected void onPause() {
		if (authLoader != null)
			authLoader.cancel(true);
		if (userInfoLoader != null)
			userInfoLoader.cancel(true);
		if (registrationTaskLoader != null)
			registrationTaskLoader.cancel(true);
		super.onPause();
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
	public void onClick(View v) {
		String login = mLogin.getText().toString();
		String password = mPassword.getText().toString();
		mob_mail=login;
		switch (v.getId()) {
		// АВТОРИЗАЦИЯ
		case R.id.auth_ac_btn_signin:
			if (login.length() == 0) {
				mLogin.requestFocus();
				Toast.makeText(AuthorizationActivity.this, "Введите логин(email или номер телефона)",
						Toast.LENGTH_SHORT).show();
			} else if (password.length() == 0) {
				mPassword.requestFocus();
				Toast.makeText(AuthorizationActivity.this, "Введите пароль", Toast.LENGTH_SHORT).show();
			} else {
				authLoader = new AuthTask();
				authLoader.execute(login, password);
			}
			break;

		// ВХОД БЕЗ РЕГИСТРАЦИИ
		case R.id.auth_ac_registration_button_without_reg:
			Intent intent = new Intent(this, OrderActivity.class);
			startActivity(intent);
			break;

		// РЕГИСТРАЦИЯ
		case R.id.auth_ac_registration_button_reg:
			String name = mRegName.getText().toString();
			String email = mRegLogin.getText().toString();
			mob_mail=email;
			if (name.length() == 0) {
				mRegLogin.requestFocus();
				Toast.makeText(AuthorizationActivity.this, "Имя обязательно для заполнения", Toast.LENGTH_SHORT).show();
			} else if (email.length() == 0) {
				mRegName.requestFocus();
				Toast.makeText(AuthorizationActivity.this, "Номер телефона обязателен для заполнения", Toast.LENGTH_SHORT).show();
			} else {
				registrationTaskLoader = new RegistrationTask();
				registrationTaskLoader.execute(name, email);
			}
			break;

		// НАПОМИНАНИЕ ПАРОЛЯ
		case R.id.auth_ac_label_forgot:
			showRememberPasswordDialog();
			break;

		default:
			break;
		}
	}

	@Override
	public void onBackPressed() {
		onFailAuth();
		super.onBackPressed();
	}

	private void authorize(String token) {
		// прокидываем токен в следущую загрузку, чтобы синхронизировать
		// сохранение данных
		userInfoLoader = new UserInfoTask();
		userInfoLoader.setToken(token);
		userInfoLoader.execute();
	}

	private void saveUserInfo(String token, PersonBean result) {
		EasyTracker.getTracker().sendEvent("user/login", "buttonPress", mob_mail, null);
		PreferencesManager.setToken(token);
		PreferencesManager.setUserId(result.getId());
		PreferencesManager.setUserEmail(result.getEmail());
		PreferencesManager.setUserName(result.getName());
		PreferencesManager.setUserLastName(result.getLastName());
		PreferencesManager.setUserMobile(result.getMobile());
	}

	private void onSuccessAuth() {
		switch (mWhereFlag) {
		case BASKET_MODE:
			startActivity(new Intent(this, OrderActivity.class));
			break;
		case ORDER_ERROR_MODE:
			finish();
			break;
		case PERSONAL_ERROR:
			finish();
			break;
		case MENU_MODE:
			startActivity(new Intent(this, PersonalActivity.class));
			break;
		}

	}

	private void onFailAuth() {
		switch (mWhereFlag) {
		case BASKET_MODE:
			finish();
			break;
		case ORDER_ERROR_MODE:
			startActivity(new Intent(this, BasketActivity.class));
			break;
		case PERSONAL_ERROR:
			startActivity(new Intent(this, MainActivity.class));
			break;
		case MENU_MODE:
			finish();
			break;
		}
	}

	// ---------------------------ПОЛУЧЕНИЕ ДАННЫХ ПОЛЬЗОВАТЕЛЯ---------------------------//

	private class UserInfoTask extends AsyncTask<Void, Void, PersonBean> {

		private String mToken = "";

		public void setToken(String token) {
			mToken = token;
		}

		@Override
		protected void onPreExecute() {
			showProgress();
		}

		@Override
		protected PersonBean doInBackground(Void... params) {
			return new PersonInfoParser().parse(URLManager.getUser(mToken));
		}

		@Override
		protected void onPostExecute(PersonBean result) {
			if (!isCancelled()) {
				hideProgress();
				// если не удалось спарсить
				if (result.getId() == 0) {
					Toast.makeText(AuthorizationActivity.this,"Не удалось получить данные", Toast.LENGTH_SHORT).show();
				} else {
					saveUserInfo(mToken, result);
					onSuccessAuth();
				}
			}
		}
	}

	// ------------------------------------АВТОРИЗАЦИЯ--------------------------------------//

	private class AuthTask extends AsyncTask<String, Void, String> {

		@Override
		protected void onPreExecute() {
			showProgress();
		}

		@Override
		protected String doInBackground(String... params) {

			String url = URLManager.getToken(params[0], params[1]);
			String result = "";
			try {
				result = HTTPUtils.getStringDataFromUrl(url);
			} catch (Exception e) {
				e.printStackTrace();
			}
			return result;
		}

		@Override
		protected void onPostExecute(String result) {
			if (!isCancelled()) {
				hideProgress();
				if (!TextUtils.isEmpty(result)) {
					try {
						String token = new UserAuthorizationParser()
								.parse(result);
						authorize(token);
					} catch (ResponceServerException ex) {
						showFailDialog(ex.getErrorMsg());
					}
				}
			}
		}
	}

	// ----------------------------------------РЕГИСТРАЦИЯ----------------------------------//

	private class RegistrationTask extends AsyncTask<String, Void, String> {

		@Override
		protected void onPreExecute() {
			showProgress();
		}

		@Override
		protected String doInBackground(String... params) {

			String result = "";
			try {
				JSONObject object = new JSONObject();
			    
				object.put("first_name", params[0]);
				object.put("mobile", params[1]);

				result = Utils.sendPostData(object.toString(), URLManager.getUserCreate());
				
			} catch (Exception e) {
				// NOP
			}
			return result;
		}

		@Override
		protected void onPostExecute(String result) {
			if (!isCancelled()) {
				hideProgress();
				if (!TextUtils.isEmpty(result)) {
					try {
						String token = new UserAuthorizationParser().parse(result);
						showRegSuccessDialog(token);
						
						FlurryAgent.logEvent(Constants.FLURRY_EVENT.Registration.toString());
					} catch (ResponceServerException ex) {
						showFailDialog(ex.getErrorMsg());
					}
				}
			}
		}
	}

	// -------------------------------------- Диалог ошибки  -------------------------------------------//

	private void showFailDialog(String msg) {
		AuthFailDialogFragment dialog = AuthFailDialogFragment.getInstance();
		dialog.setCancelable(false);
		dialog.setErrorMessage(msg);
		dialog.setonClickListener(new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				switch (which) {
				case DialogInterface.BUTTON_POSITIVE:
					break;

				default:
					break;
				}
			}
		});

		dialog.show(getFragmentManager(), "auth_fail");// TODO
	}

	// -------------------------------------- Диалог успешной регистрации----------------------------------//

	private void showRegSuccessDialog(final String token) {
		AuthRegistrationSuccessDialogFragment dialog = AuthRegistrationSuccessDialogFragment.getInstance();
		dialog.setCancelable(false);
		dialog.setonClickListener(new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				switch (which) {
				case DialogInterface.BUTTON_POSITIVE:
					EasyTracker.getTracker().sendEvent("user/register", "buttonPress", mob_mail, null);
					authorize(token);
					break;

				default:
					break;
				}
			}
		});

		dialog.show(getFragmentManager(), "registration_success");// TODO
	}

	// -------------------------------------- Диалог восстановления пароля----------------------------------//

	private void showRememberPasswordDialog() {
		
		RememmberPasswordDialog dialogFragment = RememmberPasswordDialog.getInstance();
		dialogFragment.show(getFragmentManager(), "rememmber_password");
	}
}
