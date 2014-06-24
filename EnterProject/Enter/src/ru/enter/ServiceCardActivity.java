package ru.enter;

import com.flurry.android.FlurryAgent;
import com.google.analytics.tracking.android.EasyTracker;

import ru.enter.DataManagement.BasketManager;
import ru.enter.ImageManager.ImageDownloader;
import ru.enter.beans.ServiceBean;
import ru.enter.tabUtils.TabGroupActivity;
import ru.enter.utils.Utils;
import ru.enter.widgets.HeaderFrameManager;
import ru.enter.widgets.HeaderFrameManager.HeaderButton;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

public class ServiceCardActivity extends TabGroupActivity {

	@Override
	protected void onCreate (Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		EasyTracker.getInstance().setContext(this);
		setContentView(R.layout.service_card_activity);

		((FrameLayout) findViewById(R.id.service_card_frame)).addView(HeaderFrameManager.getHeaderView(this, "Сервис F1", true));
		((TextView) findViewById(R.id.service_card_ruble)).setTypeface(Utils.getRoubleTypeFace(this), Typeface.BOLD);
		init();
		final ServiceBean bean = (ServiceBean) getIntent().getSerializableExtra("serviceObject");
		if (bean != null)
			EasyTracker.getTracker().sendEvent("service/get", "buttonPress", bean.getName(), (long) bean.getId());
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

	private void init () {
		double price;
		final ServiceBean bean = (ServiceBean) getIntent().getSerializableExtra("serviceObject");
		if (bean == null)
			return;

		TextView title = (TextView) findViewById(R.id.service_card_title);
		TextView priceView = (TextView) findViewById(R.id.service_card_price);
		TextView desc = (TextView) findViewById(R.id.service_card_desc);
		ImageView image = (ImageView) findViewById(R.id.service_card_image);
		TextView ruble = (TextView) findViewById(R.id.service_card_ruble);
		Button buy = (Button) findViewById(R.id.service_card_btn_buy);

		buy.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick (View v) {				
				BasketManager.addService(bean);
				EasyTracker.getTracker().sendEvent("cart/add-service", "buttonPress", bean.getName(), (long) bean.getId());
				showDialog(0);
			}
		});

		title.setText(bean.getName());
		desc.setText(bean.getWork());
		price = bean.getPrice();

		if (bean.getPriceTypeId() == 2) {
			String textPrice = String.format("%s%% от стоимости товара (минимум %s рублей)", bean.getPricePercent(), bean.getPriceMin());
			priceView.setText(textPrice);
			buy.setEnabled(false);
		} else {
			if (price == 0) {
				priceView.setText("Бесплатно");
			} else {
				priceView.setText(String.valueOf((int) price));
				ruble.setVisibility(View.VISIBLE);
			}
		}

		new ImageDownloader(this).download(bean.getFoto(), image);
	}

	@Override
	protected Dialog onCreateDialog (int id) {
		AlertDialog.Builder dlg = new AlertDialog.Builder(this.getParent());
		dlg.setMessage("Услуга добавлена в корзину").setPositiveButton("Перейти в корзину", new DialogInterface.OnClickListener() {
			public void onClick (DialogInterface dialog, int whichButton) {
				Intent intent = new Intent();
				intent.setClass(ServiceCardActivity.this, BasketActivity.class);
				intent.putExtra(BasketActivity.SHOW_BUTTON, true);
				startActivity(intent);
			}
		}).setNegativeButton("Продолжить покупки", new DialogInterface.OnClickListener() {
			public void onClick (DialogInterface dialog, int whichButton) {

			}
		});
		return dlg.create();
	}

}
