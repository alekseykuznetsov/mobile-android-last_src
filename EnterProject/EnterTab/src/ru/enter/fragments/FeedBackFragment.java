package ru.enter.fragments;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import ru.enter.MainActivity;
import ru.enter.OrderCompleteActivity;
import ru.enter.R;
import ru.enter.DataManagement.BasketManager;
import ru.enter.dialogs.alert.FeedbackSuccessDialogFragment;
import ru.enter.dialogs.alert.OrderCompleteSuccessDialogFragment;
import ru.enter.fragments.FeedBackFragment.SendMessage;
import ru.enter.utils.HTTPUtils;
import ru.enter.utils.PreferencesManager;
import ru.enter.utils.URLManager;
import android.app.Fragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
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
import android.widget.ProgressBar;
import android.widget.Toast;

public class FeedBackFragment extends Fragment {

	public static FeedBackFragment getInstance(){
		return new FeedBackFragment();
	}	
	
	private EditText name,email,title,mesg;
	private Button send;
	public static final String NAME_TEXT = "name";
	public static final String EMAIL_TEXT = "email";
	public static final String TITLE_TEXT = "title";
	public static final String MSG_TEXT = "message";
	private Bundle mBundleRotate;
	private SendMessage sendMessageLoader;
	private ProgressBar prg;
	
	@Override
	public void onPause() {
		if (sendMessageLoader != null) sendMessageLoader.cancel(true);
		super.onPause();
	}
	
	public void onSaveInstanceState( Bundle savedInstanceState ) {
		if (name != null ) savedInstanceState.putString( NAME_TEXT, name.getText().toString() );
		if (email != null ) savedInstanceState.putString( EMAIL_TEXT, email.getText().toString() );
		if (title != null ) savedInstanceState.putString( TITLE_TEXT, title.getText().toString() );
		if (mesg != null ) savedInstanceState.putString( MSG_TEXT, mesg.getText().toString() );
		}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
		mBundleRotate = new Bundle();
		
		Bundle args = savedInstanceState != null? savedInstanceState: getArguments();
	    if (args != null ) {
	        String savedText = args.getString( NAME_TEXT );
	        mBundleRotate.putString(NAME_TEXT, savedText);
	        savedText = args.getString( EMAIL_TEXT );
	        mBundleRotate.putString(EMAIL_TEXT, savedText);
	        savedText = args.getString( TITLE_TEXT );
	        mBundleRotate.putString(TITLE_TEXT, savedText);
	        savedText = args.getString( MSG_TEXT );
	        mBundleRotate.putString(MSG_TEXT, savedText);
	    }
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		
		View view = inflater.inflate(R.layout.feedback_fr, null);
		send = (Button) view.findViewById(R.id.feedback_fr_button_send);
				
		name = (EditText) view.findViewById(R.id.feedback_fr_name_text);
		email = (EditText) view.findViewById(R.id.feedback_fr_email_text);
		title = (EditText) view.findViewById(R.id.feedback_fr_topic_text);
		mesg = (EditText) view.findViewById(R.id.feedback_fr_message_text);
		
		prg = (ProgressBar) view.findViewById(R.id.feedback_fr_progress);
		prg.setVisibility(View.GONE);
		
	    if ( mBundleRotate != null ) {
	    	if (!TextUtils.isEmpty(mBundleRotate.getString(NAME_TEXT)))
	    		name.setText( mBundleRotate.getString(NAME_TEXT) );
	    	else{
				name.setText(PreferencesManager.getUserName());
	    	}
	    	if (!TextUtils.isEmpty(mBundleRotate.getString(EMAIL_TEXT)))
	    		email.setText( mBundleRotate.getString(EMAIL_TEXT) );
	    	else{
				email.setText(PreferencesManager.getUserEmail());
	    	}
	        title.setText( mBundleRotate.getString(TITLE_TEXT) );
	        mesg.setText( mBundleRotate.getString(MSG_TEXT) );
	    }
	    else
	    {
			name.setText(PreferencesManager.getUserName());
			email.setText(PreferencesManager.getUserEmail());
	    }
		send.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				
				if(name.getText().length()!=0 &&  mesg.getText().length() != 0 && email.getText().length() !=0)
				{
					if(email.getText().length()>0)
						if(!isEmailValid(email.getText().toString()))
						{
							Toast.makeText(getActivity(), "Проверьте E-Mail", Toast.LENGTH_LONG).show();
							email.requestFocus();
						}
						else{
							if (sendMessageLoader != null) sendMessageLoader.cancel(true);
							sendMessageLoader = new SendMessage();
							sendMessageLoader.execute();
						}
					}
				else if (name.getText().length()==0) {
					Toast.makeText(getActivity(), "Введите имя", Toast.LENGTH_LONG).show();
					name.requestFocus();
				}
				else if (email.getText().length()==0) {
					Toast.makeText(getActivity(), "Введите адрес электронной почты", Toast.LENGTH_LONG).show();
					email.requestFocus();
				}
				else if (mesg.getText().length()==0 && name.getText().length()!=0) {
					Toast.makeText(getActivity(), "Введите сообщение", Toast.LENGTH_LONG).show();
					mesg.requestFocus();
				}
			}				
		});
		return view;
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
			if(isCancelled())
				return;
			// Если нет результата от сервера
			if (result == null) {
				Toast.makeText(getActivity(), "Ошибка при отправке", Toast.LENGTH_LONG).show();
			} else {
				try {
					JSONObject answer = (JSONObject) new JSONTokener(result).nextValue();
					if ("true".equals(answer.getString("success"))) {
						showSendSuccessDialog();
					} else 
						Toast.makeText(	getActivity(),"Ошибка при отправке "+ answer.getString("success"),Toast.LENGTH_LONG).show();

				} catch (JSONException e) {
					e.printStackTrace();
				} catch (Exception e) {
					// TODO: handle exception
					Toast.makeText(getActivity(), "Ошибка на сервере ",	Toast.LENGTH_LONG).show();
				}
			}
			
		/*	
			if (result == null)
			{
				Toast.makeText(getActivity(), "Ошибка при отправке", Toast.LENGTH_LONG).show();
			}
			else{
				// TODO проблемы с серваком, выводим OK
//					раскоментить, когда получим правильный ответ от сервера
//				try {
//					JSONObject answer = (JSONObject) new JSONTokener(result).nextValue();
//					if("true".equals(answer.getString("success")))
//					{
						showSendSuccessDialog();
//					}
//					else
//						Toast.makeText(getActivity(), "Ошибка при отправке " + answer.getString("success"), Toast.LENGTH_LONG).show();
//					
//				} catch (JSONException e) {
//					e.printStackTrace();
//				}
			}*/
		prg.setVisibility(View.GONE);
		send.setEnabled(true);
		}
	}
	

	private void showSendSuccessDialog () {
		FeedbackSuccessDialogFragment dialog = FeedbackSuccessDialogFragment.getInstance();
		dialog.setCancelable(false);
		dialog.setonClickListener(new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				switch (which) {
				case DialogInterface.BUTTON_POSITIVE:
					getActivity().finish();
					break;
	
				default:
					break;
				}
			}
		});
		
		dialog.show(getFragmentManager(), "feedback_send_success");//TODO
	}
	
}
