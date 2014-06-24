package ru.enter.dialogs;

import ru.enter.R;
import android.app.DialogFragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;

public class AgreementDialogFragment extends DialogFragment {

	public static AgreementDialogFragment getInstance() {
		return new AgreementDialogFragment();
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setStyle(0, R.style.custom_dialog_dark);
	}
	
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		
		getDialog().setTitle("Условия продажи и доставки");
		
		View view = (View) inflater.inflate(R.layout.agreement_dialog_fr, null);
		WebView web = (WebView) view.findViewById(R.id.agreement_dialog_fr_web_view);
		web.loadUrl("file:///android_asset/terms.html");
		
		return view;
	}
}
