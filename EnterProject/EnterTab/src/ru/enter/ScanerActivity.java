package ru.enter;

import ru.enter.fragments.ScanerBarcodeFragment;
import ru.enter.utils.Constants;
import android.app.AlertDialog;
import android.app.FragmentTransaction;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.view.ContextThemeWrapper;

import com.flurry.android.FlurryAgent;
import com.google.analytics.tracking.android.EasyTracker;
import com.google.zxing.Result;
import com.google.zxing.client.android.CaptureActivity;

public class ScanerActivity extends CaptureActivity{

	public static final String QR_HASH = "qr_hash";
	public static final String BARCODE= "barcode";


	private static final String ENTER_ADDRESS = "enter.ru/qr/";

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		EasyTracker.getInstance().setContext(this);
		setContentView(R.layout.scaner_ac);
	}

	@Override
	protected void onStart() {
		super.onStart();
		FlurryAgent.onStartSession(this, "3X5JRBVFV7QWXNP3Q8H2");
	}

	@Override
	protected void onStop() {
		super.onStop();		
		FlurryAgent.onEndSession(this);
	}

	@Override
	public void handleDecode(Result rawResult, Bitmap barcode) {
		super.handleDecode(rawResult, barcode);
		analizeQR(rawResult.getText());
	}

	private void analizeQR(String text){
		FlurryAgent.logEvent(Constants.FLURRY_EVENT.Qr_Code_Search.toString());
		int substringEnterBeginsAt = text.indexOf(ENTER_ADDRESS);
		if(substringEnterBeginsAt != -1){
			int offsetOfEnterName = substringEnterBeginsAt + ENTER_ADDRESS.length();
			String hash = text.substring(offsetOfEnterName, offsetOfEnterName + 5);
			Intent intent = new Intent();
			intent.setClass(this, ScanerResultActivity.class);
			intent.putExtra(QR_HASH, hash);
			startActivity(intent);
		} else {
			if (text.contains("http://")){
				// пытаемся грузить чужую ссылку в браузере
				showDialog(text);
			}
			else{
				if (text.length() == 13){
					// если содержит баркод
					FragmentTransaction transaction = getFragmentManager().beginTransaction();
					ScanerBarcodeFragment fragment = ScanerBarcodeFragment.getInstance();
					Bundle extras = new Bundle();
					extras.putString(BARCODE, text);
					fragment.setArguments(extras);
					transaction.replace(R.id.scaner_ac_frame_main, fragment);
					transaction.addToBackStack("scaner_barcode");
					transaction.commit();
				} else {
					showLongText(text);
				}
			}
		}    	
	}

	private void showLongText(String text){
		AlertDialog.Builder dlg = new AlertDialog.Builder(new ContextThemeWrapper(this, R.style.custom_alert_dialog));
		dlg.setTitle("Результат сканирования")
		.setMessage(text)
		.setPositiveButton("ОК", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int whichButton) {
				restartPreviewAfterDelay(BULK_MODE_SCAN_DELAY_MS);
			}
		}).create().show();
	}

	private void showDialog(final String url){
		AlertDialog.Builder dlg = new AlertDialog.Builder(new ContextThemeWrapper(this, R.style.custom_alert_dialog));
		dlg.setTitle("Открыть в браузере?");
		dlg.setMessage(url);
		dlg.setPositiveButton("Переход", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int whichButton) {                	                	                	
				Uri uri = Uri.parse(url);
				Intent intent = new Intent(Intent.ACTION_VIEW, uri);
				startActivity(intent);
			}})
			.setNegativeButton("Отменить", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int whichButton) {
					restartPreviewAfterDelay(BULK_MODE_SCAN_DELAY_MS);
				}
			})
			.create().show(); 
	}


}