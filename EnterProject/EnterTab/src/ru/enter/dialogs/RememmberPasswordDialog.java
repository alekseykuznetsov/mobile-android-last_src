package ru.enter.dialogs;

import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;

import com.google.analytics.tracking.android.EasyTracker;

import ru.enter.R;
import ru.enter.parsers.RegistrationParser;
import ru.enter.utils.ResponceServerException;
import ru.enter.utils.URLManager;
import ru.enter.utils.Utils;
import android.app.DialogFragment;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.Toast;

public class RememmberPasswordDialog extends DialogFragment implements OnClickListener {

	private EditText mLogin;
	private FrameLayout mProgreses;
	private RememberPasswordTask mRememberPasswordTaskLoader;
	
	//For GA
	private String mob_mail;

	public void showProgress() {
		mProgreses.setVisibility(View.VISIBLE);
	}

	public void hideProgress() {
		mProgreses.setVisibility(View.GONE);
	}

	public static RememmberPasswordDialog getInstance() {
		return new RememmberPasswordDialog();
	}

	@Override
	public void onCreate(Bundle bundle) {
		super.onCreate(bundle);
		setStyle(0, R.style.custom_dialog_dark);
	}

	@Override
	public void onPause() {
		if (mRememberPasswordTaskLoader != null) mRememberPasswordTaskLoader.cancel(true);
		super.onPause();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

		getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
		getDialog().setTitle("Восстановление пароля");

		View view = inflater.inflate(R.layout.auth_dialog_password_recovery, null);
		mProgreses = (FrameLayout) view.findViewById(R.id.auth_dialog_password_recovery_frame);
		mLogin = (EditText) view.findViewById(R.id.auth_dialog_password_recovery_edittext_email);
		Button send = (Button) view.findViewById(R.id.auth_dialog_password_recovery_button_send);
		send.setOnClickListener(this);

		return view;
	}

	@Override
	public void onClick(View v) {

		String login = mLogin.getText().toString();
		mob_mail = login;
		if (!TextUtils.isEmpty(login)) {
			if (mRememberPasswordTaskLoader != null) mRememberPasswordTaskLoader.cancel(true);
			mRememberPasswordTaskLoader = new RememberPasswordTask();
			mRememberPasswordTaskLoader.execute(login);
		}
		Toast.makeText(getActivity(), "Введите ваш логин", Toast.LENGTH_SHORT).show();
	}

	private class RememberPasswordTask extends AsyncTask<String, Void, String> {

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			showProgress();
		}

		@Override
		protected String doInBackground(String... params) {
			String answer = null;
			try {
				String url = URLManager.getUserResetPassword(params[0]);
				URL feedUrl = new URL(url);
				URLConnection uc = feedUrl.openConnection();
				InputStream is = uc.getInputStream();
				answer = Utils.getTextFromInputStream(is);

				// в данном случае важно выкинет ли эксепшн. А результат нам не важен
				new RegistrationParser(answer).parse();
			} catch (ResponceServerException rse){
				answer = null;
			} catch (Exception e) {
				// NOP
			}
			return answer;
		}

		@Override
		protected void onPostExecute(String result) {
			if (!isCancelled()) {
				hideProgress();
				if (result == null) {
					Toast.makeText(getActivity(), "Непредвиденная ошибка.", Toast.LENGTH_SHORT).show();
				} else {
					EasyTracker.getTracker().sendEvent("user/register", "buttonPress", mob_mail, null);
					Toast.makeText(getActivity(), "Спасибо! Ваш новый пароль отправлен!",Toast.LENGTH_SHORT).show();
				}
			}
			dismiss();
		}
	}
}
