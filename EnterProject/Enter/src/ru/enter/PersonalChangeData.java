package ru.enter;

import java.util.Calendar;

import org.json.JSONObject;

import com.flurry.android.FlurryAgent;

import ru.enter.DataManagement.PersonData;
import ru.enter.beans.PersonBean;
import ru.enter.utils.Converter;
import ru.enter.utils.PreferencesManager;
import ru.enter.utils.URLManager;
import ru.enter.utils.Utils;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.DatePickerDialog;
import android.app.DatePickerDialog.OnDateSetListener;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

public class PersonalChangeData extends Activity{

	private final int FAULT = 1;
	private final int SUCCESS = 2;
	private final int SET_DATE = 3;
	
	private EditText firstName;
	private EditText lastName;
	private EditText middleName;
	private EditText email;
	private EditText phone;
	private EditText homePhone;
	private EditText skype;
	private EditText birthday;
	private EditText profession;
	private RadioGroup genderGroup;
	private PersonBean personData;
	private UserUpdate loader;
	private Button save;
	private ProgressBar spinner;
	private int currentYear;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.personal_change_data);
		initWidgets();
		setWidgets();
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
	
	private void initWidgets(){
		Calendar c = Calendar.getInstance();
		currentYear = c.get(Calendar.YEAR);				
		TextView title = (TextView) findViewById(R.id.personal_change_data_title);
		title.setTypeface(Utils.getTypeFace(this));
		firstName = (EditText) findViewById(R.id.personal_change_data_firstName);
		lastName = (EditText) findViewById(R.id.personal_change_data_lastName);
		middleName = (EditText) findViewById(R.id.personal_change_data_middleName);
		email = (EditText) findViewById(R.id.personal_change_data_email);
		phone = (EditText) findViewById(R.id.personal_change_data_phone);
		homePhone = (EditText) findViewById(R.id.personal_change_data_homePhone);
		skype = (EditText) findViewById(R.id.personal_change_data_skype);
		birthday = (EditText) findViewById(R.id.personal_change_data_birthday);
		birthday.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				showDialog(SET_DATE);
			}
		});
		profession = (EditText) findViewById(R.id.personal_change_data_profession);
		genderGroup = (RadioGroup) findViewById(R.id.personal_change_data_group_gender);
		spinner = (ProgressBar) findViewById(R.id.personal_change_data_progress);
		
		save = (Button) findViewById(R.id.personal_change_data_save);
		save.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				String notFilledField = requiredFields();
				if(notFilledField != null){
					Toast.makeText(PersonalChangeData.this, "Заполните поле " + notFilledField, Toast.LENGTH_SHORT).show();
					return;
				}
				
				loader = new UserUpdate();
				loader.execute();
			}
		});
	}
	
	private class UserUpdate extends AsyncTask<Void, Void, Boolean>{

		private String result;
		private String newFirstName;
		private String newLastName;
		private String newMiddleName;
		private String newBirthday;
		private String newProfession;
		private String newHomePhone;
		private String newSkype;
		private String newGender;
		private String newEmail;
		private String newPhone;
		
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			save.setEnabled(false);
			spinner.setVisibility(View.VISIBLE);
		}
		
		@Override
		protected Boolean doInBackground(Void... params) {
			
			try {
				JSONObject object = new JSONObject();
				object.put("first_name", getField(firstName.getText().toString()));
				newFirstName = getNewField(firstName.getText().toString());
				object.put("last_name", getField(lastName.getText().toString()));
				newLastName = getNewField(lastName.getText().toString());
				object.put("middle_name", getField(middleName.getText().toString()));
				newMiddleName = getNewField(middleName.getText().toString());
				
				String birthDate = birthday.getText().toString();
				if(birthDate.length() == 0)
					newBirthday = getNewField(null);
				else{
					newBirthday = Converter.fromDotToLine(birthDate);
					object.put("birthday", newBirthday);
				}
				
				
				object.put("occupation", getField(profession.getText().toString()));
				newProfession = getNewField(profession.getText().toString());
				object.put("email", getField(email.getText().toString()));
				newEmail = getNewField(email.getText().toString());
				object.put("phone", getField(homePhone.getText().toString()));
				newHomePhone = getNewField(homePhone.getText().toString());
				object.put("mobile", getField(phone.getText().toString()));
				newPhone = getNewField(phone.getText().toString());
				object.put("skype", getField(skype.getText().toString()));
				newSkype = getNewField(skype.getText().toString());
				
				if(genderGroup.getCheckedRadioButtonId() == R.id.personal_change_data_male){
					object.put("sex", 1);
					newGender = "1";
				}
				else{
					object.put("sex", 0);
					newGender = "0";
				}
				result = Utils.sendPostData(object.toString(), URLManager.getUserUpdate(PreferencesManager.getToken()));
				return true;
			}
			catch(Exception e){
				e.printStackTrace();
				return false;
			}
		}
		
		@Override
		protected void onPostExecute(Boolean state) {
			save.setEnabled(true);
			spinner.setVisibility(View.GONE);
			if(isCancelled())
				return;
			if(state == true){
				try {
					JSONObject object = new JSONObject(result);
					if(object.has("error")){
						showDialog(object.getJSONObject("error").getString("message"));
					}
					else{
						JSONObject jsonResult = object.getJSONObject("result");
						boolean confirmed = jsonResult.getBoolean("confirmed");
						if(confirmed){
							applyData();
							showDialog(SUCCESS);
						}
					}
				} 
				catch (Exception e) {
					e.printStackTrace();
					showDialog(FAULT);
				}
			}
			else
				showDialog(FAULT);
		}
		
		private Object getField(String text){
			if(text == null || text.length() == 0)
				return JSONObject.NULL;
			else
				return text;
		}
		
		private String getNewField(String text){
			if(text == null || text.length() == 0)
				return "null";
			else
				return text;
		}
		
		private void applyData(){
			synchronized(personData){
				personData.setName(newFirstName);
				personData.setLastName(newLastName);
				personData.setMiddleName(newMiddleName);
				personData.setBirthday(newBirthday);
				personData.setOccupation(newProfession);
				personData.setPhone(newHomePhone);
				personData.setSkype(newSkype);
				personData.setSex(newGender);
				personData.setMobile(newPhone);
				personData.setEmail(newEmail);
				
				//TODO for one click
				PreferencesManager.setUserName(newFirstName);
				PreferencesManager.setUserLastName(newLastName);
				PreferencesManager.setUserMobile(newPhone);
				PreferencesManager.setUserEmail(newEmail);
			}
		}
	}
	
	private String requiredFields(){
		if(firstName.getText().length() == 0)
			return "Имя";
		if(email.getText().length() == 0 && phone.getText().length() == 0)
			return "Email или Телефон";
		return null;
	}
	
	private void setWidgets(){
		personData = PersonData.getInstance().getPersonBean();
		
		setIfNotNull(firstName, personData.getName());
		setIfNotNull(lastName, personData.getLastName());
		setIfNotNull(middleName, personData.getMiddleName());
		setIfNotNull(email, personData.getEmail());
		setIfNotNull(phone, personData.getMobile());
		setIfNotNull(homePhone, personData.getPhone());
		setIfNotNull(skype, personData.getSkype());
		setIfNotNull(profession, personData.getOccupation());

		
		if(!personData.getSex().equals("null")){
			String gender = personData.getSex();
			if(gender.equals("1"))
				genderGroup.check(R.id.personal_change_data_male);
			else
				genderGroup.check(R.id.personal_change_data_female);
		}
		
		if(!"null".equals(personData.getBirthday()))
			birthday.setText(Converter.fromLineToDot(personData.getBirthday()));
	}
	
	private void setIfNotNull(EditText et, String text){
		if(!"null".equals(text))
			et.setText(text);
	}
	
	private void showDialog(String errorMsg){
		Builder builder = new AlertDialog.Builder(this);
		builder.setMessage("При изменении данных произошла ошибка:\n" + errorMsg)
		       .setCancelable(false)
		       .setPositiveButton("ОК", new DialogInterface.OnClickListener() {
		           public void onClick(DialogInterface dialog, int id) {
		        	   dialog.dismiss();
		           }
		       }).create().show();		
	}
	
	@Override
	protected Dialog onCreateDialog(int id) {
	    Dialog dialog;
	    AlertDialog.Builder builder;
	    switch(id) {
	    case FAULT:
			builder = new AlertDialog.Builder(this);
			builder.setMessage("При изменении данных произошла ошибка\nПовторите попытку через некоторое время")
			       .setCancelable(false)
			       .setPositiveButton("ОК", new DialogInterface.OnClickListener() {
			           public void onClick(DialogInterface dialog, int id) {
			        	   dialog.dismiss();
			           }
			       });
			dialog = builder.create();	    	
	        break;
	    case SUCCESS:
			builder = new AlertDialog.Builder(this);
			builder.setMessage("Данные успешно изменены")
			       .setCancelable(false)
			       .setPositiveButton("ОК", new DialogInterface.OnClickListener() {
			           public void onClick(DialogInterface dialog, int id) {
			        	   dialog.dismiss();
			        	   finish();
			           }
			       });
			dialog = builder.create();
	        break;
	        
	    case SET_DATE:
	    	String date = birthday.getText().toString();
	    	Calendar oldDate = Calendar.getInstance();
	    	if(date.length() != 0)
	    		oldDate = Converter.fromDot(date);
	    	
	        DatePickerDialog dateDialog = new DatePickerDialog(this, new OnDateSetListener() {
	        	
				@Override
				public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
					if(year > currentYear - 7)
						Toast.makeText(PersonalChangeData.this, "Неверная дата", Toast.LENGTH_LONG).show();						
					else
						birthday.setText(Converter.from3Int(year, monthOfYear + 1, dayOfMonth));
					removeDialog(SET_DATE);
				}
			}, oldDate.get(Calendar.YEAR), oldDate.get(Calendar.MONTH), oldDate.get(Calendar.DAY_OF_MONTH));
	        
	        return dateDialog;
	        
	    default:
	        dialog = null;
	    }
	    return dialog;
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
	    if (keyCode == KeyEvent.KEYCODE_BACK){
	    	if(loader != null)
	    		loader.cancel(true);
	    }
	    return super.onKeyDown(keyCode, event);
	}

}
