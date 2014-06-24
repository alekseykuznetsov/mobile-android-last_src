package ru.enter;

import java.net.URL;

import ru.enter.utils.Constants;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;

import com.flurry.android.FlurryAgent;
import com.google.zxing.Result;
import com.google.zxing.client.android.CaptureActivity;

// ----------------------------------------------------------------------

public class CameraActivity extends CaptureActivity {

	private final static String ENTER_ADDRESS = "enter.ru/qr/";

	@Override
	public void onCreate (Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.capture);
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
	public void handleDecode (Result rawResult, Bitmap barcode) {
		super.handleDecode(rawResult, barcode);
		analizeQR(rawResult.getText());
	}

	private void analizeQR (String text) {
		FlurryAgent.logEvent(Constants.FLURRY_EVENT.Qr_Code_Search.toString());
		int substringEnterBeginsAt = text.indexOf(ENTER_ADDRESS);
		if (substringEnterBeginsAt != -1) {
			try {
				int offsetOfEnterName = substringEnterBeginsAt + ENTER_ADDRESS.length();
				String hash = text.substring(offsetOfEnterName, offsetOfEnterName + 5);
				Intent intent = new Intent();
				intent.setClass(this, SearchQR.class);
				intent.putExtra(SearchQR.QR, hash);
				finish();
				startActivity(intent);
			} catch (Exception e) {
				showLongText(text);
			}
		} else {
			if (text.contains("http://")) {
				// пытаемся грузить чужую ссылку в браузере
				showDialog(text);
			} else if (text.length() == 13 && TextUtils.isDigitsOnly(text)) {
				// если содержит баркод
				Intent intent = new Intent();
				intent.setClass(this, ProductCardActivity.class);
				intent.putExtra(ProductCardActivity.EXTRA_FROM, Constants.FLURRY_FROM.Search.toString());
				intent.putExtra(ProductCardActivity.BARCODE, text);
				finish();
				startActivity(intent);
			} else {
				showLongText(text);
			}
		}
	}

	private void showLongText (String text) {
		AlertDialog.Builder dlg = new AlertDialog.Builder(this);
		dlg.setTitle("Текст").setMessage(text).setPositiveButton("ОК", new DialogInterface.OnClickListener() {
			public void onClick (DialogInterface dialog, int whichButton) {
				restartPreviewAfterDelay(BULK_MODE_SCAN_DELAY_MS);
			}
		}).create().show();
	}

	private void showDialog (final String Url) {
		AlertDialog.Builder dlg = new AlertDialog.Builder(this);
		dlg.setTitle("Открыть в браузере?");
		dlg.setMessage(Url);
		dlg.setPositiveButton("Переход", new DialogInterface.OnClickListener() {
			public void onClick (DialogInterface dialog, int whichButton) {
				Uri uri = Uri.parse(Url);
				Intent intent = new Intent(Intent.ACTION_VIEW, uri);
				startActivity(intent);
			}
		}).setNegativeButton("Отменить", new DialogInterface.OnClickListener() {
			public void onClick (DialogInterface dialog, int whichButton) {
				restartPreviewAfterDelay(BULK_MODE_SCAN_DELAY_MS);
			}
		}).create().show();
	}

}
