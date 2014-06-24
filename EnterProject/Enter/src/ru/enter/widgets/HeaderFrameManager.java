package ru.enter.widgets;

import ru.enter.SearchGlobalActivity;
import ru.enter.MainActivity;
import ru.enter.R;
import ru.enter.tabUtils.TabGroupActivity;
import ru.enter.utils.Utils;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;

public class HeaderFrameManager{
	public static enum HeaderButton{
		search,
		settings,
		personal_account
	}
	private static Bundle bundle;

	private HeaderFrameManager() {}

	public static View getHeaderView(final Context context,String titleText, boolean hasIcon, final HeaderButton ...buttons){

		LinearLayout.LayoutParams linear_params = new LinearLayout.LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT);
		LinearLayout.LayoutParams title_params = new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		LinearLayout.LayoutParams image_params = new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);

		LinearLayout main = new LinearLayout(context);
		main.setLayoutParams(linear_params);
		main.setGravity(Gravity.CENTER_VERTICAL);
		main.setBackgroundResource(R.drawable.navbar);

		TextView title = new TextView(context);		
		title.setLayoutParams(title_params);
		title.setTextColor(Color.WHITE);
		title.setTextSize(22);
		title.setTypeface(Utils.getTypeFace(context));
		if(titleText!=null&&!"".equals(titleText))title.setText(titleText);

		LinearLayout header = new LinearLayout(context);
		linear_params.weight = 1;
		header.setLayoutParams(linear_params);
		header.setGravity(Gravity.CENTER);

		if (hasIcon) { 

			ImageView image = new ImageView(context);//TODO
			image.setLayoutParams(image_params);
			image.setBackgroundResource(R.drawable.tab6);

			header.addView(image);
		}

		header.addView(title);
		main.addView(header);

		OnClickListener listener = new OnClickListener() {

			@Override
			public void onClick(View v) {
				switch ((Integer)v.getTag()) {
				case 1:
					Intent intent = new Intent().setClass(context, SearchGlobalActivity.class);

					if(bundle != null)
					{
						intent.putExtras(bundle);
						((TabGroupActivity)context).runNext(intent, "SearchGlobalActivity_cut");//startActivity(intent);
					}
					else
						MainActivity.runSearch();


					break;
				case 2:
					break;
				case 3:
					MainActivity.runPersonalAccount();
					break;	

				default:
					break;
				}
			}
		};

		int padding = 0;
		int width = ((WindowManager) context.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay().getWidth()/7; 

		for(HeaderButton button:buttons){

			ImageButton buttonNew = new ImageButton(context);
			title_params.setMargins(0, 0, 10, 0);
			buttonNew.setLayoutParams(title_params);
			switch (button) {
			case search:
				buttonNew.setBackgroundResource(R.drawable.btn_search_background);
				buttonNew.setTag(1);
				buttonNew.setOnClickListener(listener);
				break;
			case settings:
				buttonNew.setBackgroundResource(R.drawable.btn_options_background);
				buttonNew.setTag(2);
				buttonNew.setOnClickListener(listener);
				break;
			case personal_account:
				buttonNew.setBackgroundResource(R.drawable.btn_account_background);
				buttonNew.setTag(3);
				buttonNew.setOnClickListener(listener);
				break;

			}


			padding += (width+10);
			main.addView(buttonNew);

		}

		header.setPadding(padding, 0, 0, 0);

		return main;

	}
	public static void setBundle(Bundle b)
	{
		bundle = b;
	}
}