package ru.enter.dialogs;

import ru.enter.R;
import ru.enter.widgets.ButtonBold;
import android.app.DialogFragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.View.OnClickListener;

public class PersonalSettingsDialogFragment  extends DialogFragment implements OnClickListener {
	
	private ButtonBold closeBtn, changeCity, changePass, changeData, clearCache;
	private OnClickListener mListner;
	
	public static PersonalSettingsDialogFragment getInstance() {
		
		return new PersonalSettingsDialogFragment();
	}
	
	@Override
	public void onCreate(Bundle bundle) {
	    super.onCreate(bundle);
	    setStyle(0, R.style.custom_dialog_dark);	    
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		
		getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
		getDialog().setTitle("Настройки");
		
		//PersonalFormFragment fragment = (PersonalFormFragment) getFragmentManager().findFragmentByTag("personalForm");
		//mPersonData = fragment.getInfo();
		
		View view = inflater.inflate(R.layout.personal_dialog_settings, null);
		
		closeBtn = (ButtonBold) view.findViewById(R.id.personal_dialog_settings_btn_close);
		closeBtn.setOnClickListener(this);
		clearCache = (ButtonBold) view.findViewById(R.id.personal_dialog_settings_btn_clear_cache);
		changeCity = (ButtonBold) view.findViewById(R.id.personal_dialog_settings_btn_chang_city);
		changeData = (ButtonBold) view.findViewById(R.id.personal_dialog_settings_btn_chang_data);
		changePass = (ButtonBold) view.findViewById(R.id.personal_dialog_settings_btn_chang_pass);
		if(mListner!=null)
		{			
			clearCache.setOnClickListener(mListner);
			changeCity.setOnClickListener(mListner);
			changeData.setOnClickListener(mListner);
			changePass.setOnClickListener(mListner);
		}
	
		
		return view;
	}

	
	public void setOnClickListner(OnClickListener mListner) {
		this.mListner = mListner;
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		dismiss();
	}

}
