package ru.enter;

import java.util.ArrayList;

import org.json.JSONException;
import org.json.JSONObject;

import com.flurry.android.FlurryAgent;

import ru.enter.DataManagement.BasketData;
import ru.enter.DataManagement.BasketManager;
import ru.enter.DataManagement.PersonData;
import ru.enter.ImageManager.ImageDownloader;
import ru.enter.Listeners.LoadListener;
import ru.enter.adapters.MyBlankAdapter;
import ru.enter.beans.AddressBean;
import ru.enter.beans.PersonBean;
import ru.enter.parsers.AddressParser;
import ru.enter.parsers.PersonParser;
import ru.enter.utils.PreferencesManager;
import ru.enter.utils.URLManager;
import ru.enter.utils.Utils;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.InputType;
import android.text.TextUtils;
import android.text.method.PasswordTransformationMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

public class PersonalForm extends Activity {

	private LayoutInflater mViewInflater;
	private PersonalForm mContext;
	private PersonBean mBean;
	private MyBlankAdapter mAdapter;
	private FrameLayout mProgress;
	private TextView blank_name;
	private TextView blank_email;
	private TextView blank_tel;
	public static boolean logout;
	
	

	@Override
	protected void onCreate (Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		logout = false;
		setContentView(R.layout.personal_account_activity_blank);
		mContext = this;
		mViewInflater = LayoutInflater.from(mContext);
		setupWidgets();
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
	protected void onResume () {
		super.onResume();
		reloadAddress();
	}

	private void setupWidgets () {
		ListView blank_list = (ListView) findViewById(R.id.personal_account_activity_blank_list);
		mProgress = (FrameLayout) findViewById(R.id.personal_account_activity_blank_progress);

		View view = mViewInflater.inflate(R.layout.personal_account_blank_headerview_new, null);
		blank_name = (TextView) view.findViewById(R.id.personal_account_blank_name);
		blank_email = (TextView) view.findViewById(R.id.personal_account_blank_email);
		blank_tel = (TextView) view.findViewById(R.id.personal_account_blank_tel);
		Button add = (Button) view.findViewById(R.id.personal_account_blank_button_add);
		Button sett = (Button) view.findViewById(R.id.personal_account_settings);
		sett.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick (View v) {
				showSettingsDialog();
			}
		});

		/*Button exit = (Button) view.findViewById(R.id.personal_account_blank_button_change);
		Button change_pwd = (Button) view.findViewById(R.id.personal_account_blank_button_change_password);
		Button change_city = (Button) view.findViewById(R.id.personal_account_blank_button_change_city);
		Button change_data = (Button) view.findViewById(R.id.personal_account_blank_button_change_data);*/

		add.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick (View v) {
				showAddDialog();
			}
		});
/*
		change_pwd.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick (View v) {
				showRememberPaswwDialog();
			}
		});

		change_city.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick (View v) {
				if (!BasketManager.isEmpty())
					showChangeCityDialog();
				else
					mContext.startActivityForResult(new Intent().setClass(mContext, CitiesListActivity.class), 1);
			}
		});

		change_data.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick (View v) {
				Intent intent = new Intent(PersonalForm.this, PersonalChangeData.class);
				startActivity(intent);
			}
		});
*/
		mAdapter = new MyBlankAdapter(mContext);
		blank_list.addHeaderView(view);
		blank_list.setAdapter(mAdapter);
/*
		exit.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick (View v) {
				logout();
				PersonData.getInstance().setPersonBean(new PersonBean());
				BasketData.getInstance().setCheckoutBean(new CheckoutBean());
				Toast.makeText(mContext, "Вы успешно вышли", Toast.LENGTH_SHORT).show();
				finish();
			}
		});*/
		
	}

	private void updateUserInfo () {
		blank_name.setText(mBean.getName().equals("null") ? "не указано" : mBean.getName());
		blank_email.setText(mBean.getEmail().equals("null") ? "не указано" : mBean.getEmail());
		blank_tel.setText(mBean.getMobile().equals("null") ? "не указано" : mBean.getMobile());
	}

	// ------------------------------//

	private AlertDialog mDialog;
	private EditText mDialogStreet;
	private EditText mDialogHouse;
	private EditText mDialogHousing;
	private EditText mDialogFloor;
	private EditText mDialogFlat;
	private Button mDialogButton;
	private View mView;

	private void createView () {
		mView = mViewInflater.inflate(R.layout.personal_ac_add_address, null);
		mDialogStreet = (EditText) mView.findViewById(R.id.personal_acc_add_address_edittext_street);
		mDialogHouse = (EditText) mView.findViewById(R.id.personal_acc_add_address_edittext_house);
		mDialogHousing = (EditText) mView.findViewById(R.id.personal_acc_add_address_edittext_housing);
		mDialogFloor = (EditText) mView.findViewById(R.id.personal_acc_add_address_edittext_floor);
		mDialogFlat = (EditText) mView.findViewById(R.id.personal_acc_add_address_edittext_flat);
		mDialogButton = (Button) mView.findViewById(R.id.personal_acc_add_address_button_send);
	}

	private void initView (AddressBean bean) {
		if (TextUtils.isEmpty(bean.getAddress())) {
			mDialogStreet.setText(Utils.useIfNotNull(bean.getStreet()));
			mDialogHouse.setText(Utils.useIfNotNull(bean.getHouse()));
			mDialogHousing.setText(Utils.useIfNotNull(bean.getHousing()));
			mDialogFloor.setText(Utils.useIfNotNull(bean.getFloor()));
			mDialogFlat.setText(Utils.useIfNotNull(bean.getFlat()));
		} else {
			mDialogStreet.setText(bean.getAddress());
		}
	}

	private void manageClick (final boolean isAdd, final AddressBean address) {
		mDialogButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick (View v) {

				if (isValid()) {
					String jsonString = createJSONString();
					if (isAdd) {
						new Download(Download.ADD_ADDRESS).execute(jsonString);
					} else {
						if (address != null) {
							new Download(Download.EDIT_ADDRESS, address.getId()).execute(jsonString);
						}
					}
					dismissDialog();
				}
			}
		});
	}

	private boolean isValid () {
		boolean good = !mDialogStreet.getText().toString().equals("") && !mDialogHouse.getText().toString().equals("");
		if (!good) {
			Toast.makeText(getParent().getParent(), "Улица и дом обязательны для заполнения", Toast.LENGTH_SHORT).show();
		}
		return good;
	}

	private String createJSONString () {
		JSONObject object = new JSONObject();

		try {
			object.put("first_name", mBean.getName());
			object.put("last_name", mBean.getLastName());
			object.put("middle_name", mBean.getMiddleName());
			object.put("is_primary", 0);
			object.put("geo_id", PreferencesManager.getCityid());
			object.put("zip_code", 1);

			object.put("address_street", mDialogStreet.getText().toString());
			object.put("address_building", mDialogHouse.getText().toString());
			object.put("address_number", mDialogHousing.getText().toString());
			object.put("address_floor", mDialogFloor.getText().toString());
			object.put("address_apartment", mDialogFlat.getText().toString());
		} catch (JSONException e) {
			e.printStackTrace();
		}

		return object.toString();
	}

	private void createDialog (String title) {
		Builder alertBuilder = new AlertDialog.Builder(getParent().getParent());
		alertBuilder.setView(mView);
		alertBuilder.setTitle(title);
		mDialog = alertBuilder.create();
	}

	private void showDialog () {
		if (mDialog != null && !mDialog.isShowing()) {
			mDialog.show();
		}
	}

	private void dismissDialog () {
		if (mDialog != null && mDialog.isShowing()) {
			mDialog.dismiss();
		}
	}

	private void showAddDialog () {
		createView();
		manageClick(true, null);
		createDialog("Добавьте адрес доставки");
		showDialog();
	}

	public void showEditDialog (AddressBean address) {
		createView();
		initView(address);
		manageClick(false, address);
		createDialog("Добавьте адрес доставки");
		showDialog();

	}

	// ------------------------------//

	public void showDeleteDialog (final AddressBean address_bean) {
		new AlertDialog.Builder(getParent().getParent()).setMessage("Вы действительно хотите удалить адрес доставки?").setCancelable(false)
				.setPositiveButton("Да", new DialogInterface.OnClickListener() {
					public void onClick (DialogInterface dialog, int id) {
						new Download(Download.DELETE_ADDRESS, address_bean.getId()).execute();
					}
				}).setNegativeButton("Нет", new DialogInterface.OnClickListener() {
					public void onClick (DialogInterface dialog, int id) {
						dialog.cancel();
					}
				}).create().show();
	}

	public void showChangeCityDialog () {
		new AlertDialog.Builder(getParent().getParent())
				.setMessage(
						"Осторожно! При смене города цена на товары и их наличие может измениться! Для уточнения информации звоните в Контакт-cENTER 8-800-7000009")
				.setCancelable(false).setPositiveButton("Ok", new DialogInterface.OnClickListener() {
					public void onClick (DialogInterface dialog, int id) {
						startActivityForResult(new Intent().setClass(mContext, CitiesListActivity.class), 1);
					}
				}).setNegativeButton("Отмена", new DialogInterface.OnClickListener() {
					public void onClick (DialogInterface dialog, int id) {
						dialog.cancel();
					}
				}).create().show();
	}

	private class Download extends AsyncTask<String, Void, Integer> {

		public static final int ADD_ADDRESS = 1;
		public static final int EDIT_ADDRESS = 2;
		public static final int DELETE_ADDRESS = 3;
		public static final int CHANGE_PW = 4;
		private int mode = 0;
		private long address_id;

		protected Download(int mode, long... address_id) {
			this.mode = mode;
			if (address_id.length != 0)
				this.address_id = address_id[0];
		}

		@Override
		protected void onPreExecute () {
			mProgress.setVisibility(View.VISIBLE);
		}

		@Override
		protected Integer doInBackground (String... data_to_send) {
			String request = "";
			String token = PreferencesManager.getToken();
			switch (mode) {

			case ADD_ADDRESS:
				request = Utils.sendPostData(data_to_send[0], URLManager.getCreateAddress(token));
				return new AddressParser(mContext).parseString(request);

			case EDIT_ADDRESS:
				request = Utils.sendPostData(data_to_send[0], URLManager.getUpdateAddress(token, address_id));
				return new AddressParser(mContext).parseString(request);

			case DELETE_ADDRESS:
				return new AddressParser(mContext).parse(URLManager.getDeleteAddress(token, address_id));

			case CHANGE_PW:
				return new AddressParser(mContext).parse(URLManager.getUserChangePassword(token, data_to_send[0], data_to_send[1]));

			default:
				break;
			}

			return -1;
		}

		protected void onPostExecute (Integer result) {
			if (result != null && result >= 0) {
				switch (mode) {
				case ADD_ADDRESS:
					Toast.makeText(mContext, "Адрес добавлен", Toast.LENGTH_SHORT).show();
					break;
				case EDIT_ADDRESS:
					Toast.makeText(mContext, "Адрес изменен", Toast.LENGTH_SHORT).show();
					break;
				case DELETE_ADDRESS:
					Toast.makeText(mContext, "Адрес удален", Toast.LENGTH_SHORT).show();
					break;
				case CHANGE_PW:
					Toast.makeText(mContext, "Пароль изменен", Toast.LENGTH_SHORT).show();
					break;
				default:
					break;
				}
				new ParseRequest().execute();
			}

			mProgress.setVisibility(View.GONE);
		}
	}

	private void reloadAddress () {
		new ParseRequest().execute();
	}

	private class ParseRequest extends AsyncTask<Void, Void, Boolean> {

		@Override
		protected void onPreExecute () {
			mProgress.setVisibility(View.VISIBLE);
		}

		@Override
		protected Boolean doInBackground (Void... params) {
			return new PersonParser(mContext).parse(URLManager.getUser(PreferencesManager.getToken()));
		}

		@Override
		protected void onPostExecute (Boolean result) {
			if (logout) {
				PersonData.getInstance().setPersonBean(new PersonBean());
				logout();

			} else {
				mBean = PersonData.getInstance().getPersonBean();
				// TODO/// for OneClick
				PreferencesManager.setUserName(mBean.getName());
				PreferencesManager.setUserLastName(mBean.getLastName());
				PreferencesManager.setUserEmail(mBean.getEmail());
				PreferencesManager.setUserMobile(mBean.getMobile());
				// /////////
				ArrayList<AddressBean> adresses = mBean.getAddressList();
				if (adresses != null && adresses.size() > 0) {
					mAdapter.setObjects(adresses);
				} else {
					mAdapter.clean();
				}
				updateUserInfo();
				mProgress.setVisibility(View.GONE);
			}
		}

	}
/*
	private void showRememberPaswwDialog () {
		final LinearLayout linear = new LinearLayout(getParent().getParent());
		final EditText oldPass = new EditText(getParent().getParent());
		final EditText newPass = new EditText(getParent().getParent());
		final EditText newPassAgree = new EditText(getParent().getParent());

		LayoutParams params = new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT);
		oldPass.setLayoutParams(params);
		newPass.setLayoutParams(params);
		newPassAgree.setLayoutParams(params);

		oldPass.setInputType(InputType.TYPE_TEXT_VARIATION_PASSWORD | InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS);
		newPass.setTransformationMethod(PasswordTransformationMethod.getInstance());
		newPass.setInputType(InputType.TYPE_TEXT_VARIATION_PASSWORD | InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS);
		newPassAgree.setTransformationMethod(PasswordTransformationMethod.getInstance());
		newPassAgree.setInputType(InputType.TYPE_TEXT_VARIATION_PASSWORD | InputType.TYPE_CLASS_TEXT
				| InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS);

		oldPass.setHint("Старый пароль");
		newPass.setHint("Новый пароль");
		newPassAgree.setHint("Подтвердите пароль");

		oldPass.setLines(1);
		newPass.setLines(1);
		newPassAgree.setLines(1);

		linear.setOrientation(LinearLayout.VERTICAL);
		linear.setPadding(5, 5, 5, 5);
		linear.addView(oldPass);
		linear.addView(newPass);
		linear.addView(newPassAgree);

		AlertDialog.Builder dlg = new AlertDialog.Builder(getParent().getParent());

		dlg.setIcon(android.R.drawable.ic_dialog_alert).setTitle("Сменить пароль").setView(linear)
				.setPositiveButton("Сменить", new DialogInterface.OnClickListener() {
					public void onClick (DialogInterface dialog, int whichButton) {
						String oldP = oldPass.getEditableText().toString().trim();
						String newP = newPass.getEditableText().toString().trim();
						String confirmP = newPassAgree.getEditableText().toString().trim();
						if (oldP.equals("")) {
							oldPass.requestFocus();
						} else if (newP.equals("")) {
							newPass.requestFocus();
						} else if (confirmP.equals("")) {
							newPassAgree.requestFocus();
						} else {
							if (newP.equals(confirmP)) {
								new Download(Download.CHANGE_PW).execute(oldP, newP);
							} else {
								Toast.makeText(mContext, "Введенные пароли не совпадают", Toast.LENGTH_SHORT).show();
							}
						}
					}
				}).setNegativeButton("Отмена", new DialogInterface.OnClickListener() {
					public void onClick (DialogInterface dialog, int whichButton) {

					}
				}).create().show();
	}
*/
	private void logout () {
		PreferencesManager.setToken("");
		PreferencesManager.setUserId(0);
		PreferencesManager.setUserEmail("");
		PreferencesManager.setUserName("");
		PreferencesManager.setUserLastName("");
		PreferencesManager.setUserMobile("");
		PreferencesManager.showGetCouponDialog(true);
	}
	
	
	private void showSettingsDialog() {
		
		
			final LinearLayout linear = new LinearLayout(getParent().getParent());
			
			LayoutInflater inf = getLayoutInflater();
			View v = inf.inflate(R.layout.personal_account_settings_custom_dialog, null);
			
			Button changePass, changeData, changeCyti, clearCache, exitBtn;
			changeData = (Button) v.findViewById(R.id.personal_account_settings_cst_dlg_change_data);
			changePass = (Button) v.findViewById(R.id.personal_account_settings_cst_dlg_change_pass);
			changeCyti = (Button) v.findViewById(R.id.personal_account_settings_cst_dlg_change_city);
			clearCache = (Button) v.findViewById(R.id.personal_account_settings_cst_dlg_clear_cash);
			exitBtn = (Button) v.findViewById(R.id.personal_account_settings_cst_dlg_close_btn);
			linear.addView(v);
			
						AlertDialog.Builder dlg = new AlertDialog.Builder(getParent().getParent());
			
			
			final AlertDialog f = dlg.setIcon(android.R.drawable.ic_dialog_alert).setTitle("Настройки").setView(linear).create();
			
			changeData.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					Intent intent = new Intent(PersonalForm.this, PersonalChangeData.class);
					startActivity(intent);
					f.dismiss();
					}
				
			});
			
			changePass.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					showRememberPaswwDialog();
					f.dismiss();
					}
				
			});
			
			changeCyti.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					if (!BasketManager.isEmpty())
						showChangeCityDialog();
					else
						mContext.startActivityForResult(new Intent().setClass(mContext, CitiesListActivity.class), 1);
					f.dismiss();
					}
				
			});
			
			clearCache.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					ImageDownloader loader = new ImageDownloader(mContext);
					ImageDownloader.clearMemoryCache();
					loader.clearFlashCache();
					f.dismiss();
					}
				
			});
			
			exitBtn.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					f.dismiss();				
				}
			});
			
			f.show();		
	}
	
	private void showRememberPaswwDialog () {
		final LinearLayout linear = new LinearLayout(getParent().getParent());		
		final LinearLayout btnLinear = new LinearLayout(getParent().getParent());
		final EditText oldPass = new EditText(getParent().getParent());
		final EditText newPass = new EditText(getParent().getParent());
		final EditText newPassAgree = new EditText(getParent().getParent());
		final Button okBtn = new Button(getParent().getParent());
		final Button canBtn = new Button(getParent().getParent());
		

		LayoutParams params = new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT);
		LayoutParams btnParams = new LayoutParams(LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT);
		btnParams.weight=1;
		
		oldPass.setLayoutParams(params);
		newPass.setLayoutParams(params);
		newPassAgree.setLayoutParams(params);
		okBtn.setLayoutParams(btnParams);
		canBtn.setLayoutParams(btnParams);

		oldPass.setInputType(InputType.TYPE_TEXT_VARIATION_PASSWORD | InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS);
		newPass.setTransformationMethod(PasswordTransformationMethod.getInstance());
		newPass.setInputType(InputType.TYPE_TEXT_VARIATION_PASSWORD | InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS);
		newPassAgree.setTransformationMethod(PasswordTransformationMethod.getInstance());
		newPassAgree.setInputType(InputType.TYPE_TEXT_VARIATION_PASSWORD | InputType.TYPE_CLASS_TEXT
				| InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS);

		oldPass.setHint("Старый пароль");
		newPass.setHint("Новый пароль");
		newPassAgree.setHint("Подтвердите пароль");
		okBtn.setText("Сменить");
		
		canBtn.setText("Отмена");

		oldPass.setLines(1);
		newPass.setLines(1);
		newPassAgree.setLines(1);

		linear.setOrientation(LinearLayout.VERTICAL);
		linear.setPadding(5, 5, 5, 5);
		linear.addView(oldPass);
		linear.addView(newPass);
		linear.addView(newPassAgree);
		btnLinear.setOrientation(LinearLayout.HORIZONTAL);
		btnLinear.setBackgroundColor(Color.GRAY);
		btnLinear.setPadding(5, 5, 5, 5);
		btnLinear.addView(okBtn);
		btnLinear.addView(canBtn);
		linear.addView(btnLinear);		
		


		AlertDialog.Builder dlg = new AlertDialog.Builder(getParent().getParent());
		
		
		final AlertDialog f = dlg.setIcon(android.R.drawable.ic_dialog_alert).setTitle("Сменить пароль").setView(linear)
				/*.setPositiveButton("Сменить", new DialogInterface.OnClickListener() {
					public void onClick (DialogInterface dialog, int whichButton) {
						String oldP = oldPass.getEditableText().toString().trim();
						String newP = newPass.getEditableText().toString().trim();
						String confirmP = newPassAgree.getEditableText().toString().trim();
						if (oldP.equals("")) {
							oldPass.requestFocus();
						} else if (newP.equals("")) {
							newPass.requestFocus();
						} else if (confirmP.equals("")) {
							newPassAgree.requestFocus();
						} else {
							if (newP.equals(confirmP)) {
								mChangeListner.stratChange(oldP, newP);
								//new Download(Download.CHANGE_PW).execute(oldP, newP);
							} else {
								Toast.makeText(PersonalAccount.this, "Введенные пароли не совпадают", Toast.LENGTH_SHORT).show();
							}
						}
					}
				}).setNegativeButton("Отмена", new DialogInterface.OnClickListener() {
					public void onClick (DialogInterface dialog, int whichButton) {

					}
				})*/
				.create();
		
			okBtn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				String oldP = oldPass.getEditableText().toString().trim();
				String newP = newPass.getEditableText().toString().trim();
				String confirmP = newPassAgree.getEditableText().toString().trim();
				if (oldP.equals("")) {
					oldPass.requestFocus();
				} else if (newP.equals("")) {
					newPass.requestFocus();
				} else if (confirmP.equals("")) {
					newPassAgree.requestFocus();
				} else {
					if (newP.equals(confirmP)) {
						//mChangeListner.stratChange(oldP, newP);
						new Download(Download.CHANGE_PW).execute(oldP, newP);
					} else {
						Toast.makeText(PersonalForm.this, "Введенные пароли не совпадают", Toast.LENGTH_SHORT).show();
					}
					f.dismiss();
				}
			}
		});
		
		canBtn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				f.dismiss();				
			}
		});
		
		f.show();
	}
	
}
