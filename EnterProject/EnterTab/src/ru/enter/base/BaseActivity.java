package ru.enter.base;

import ru.enter.utils.TypefaceUtils;
import android.app.ActionBar;
import android.app.Activity;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.TextView;

public class BaseActivity extends Activity {
	
	private ActionBar mActionBar;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		//нужно для получения экшнбара
//		getWindow().requestFeature(Window.FEATURE_ACTION_BAR);
		setContentView(new View(this));
		//иначе здесь null
		mActionBar = getActionBar();
		mActionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM | ActionBar.DISPLAY_SHOW_HOME);
		//добавляем стелочку
		mActionBar.setDisplayHomeAsUpEnabled(true);
	}

	@Override
	public boolean onMenuItemSelected(int featureId, MenuItem item) {
		switch (item.getItemId()) {
		//обработчик нажатия на иконку со стрелкой
		case android.R.id.home:
			onBackPressed();
			break;
		default:
			break;
		}
		return super.onMenuItemSelected(featureId, item);
	}
	
	public void setTitleLeft (String title) {
		 TextView tv = makeTitle();
		 tv.setPadding(40, 0, 0, 0);
		 tv.setGravity(Gravity.CENTER_VERTICAL | Gravity.LEFT);
		 tv.setText(title);
		 tv.setTypeface(TypefaceUtils.getBrashTypeface());
		 mActionBar.setCustomView(tv);
	}
	
	public void setTitleCenter (String title) {
		 TextView tv = makeTitle();
		 tv.setGravity(Gravity.CENTER);
		 tv.setText(title);
		 tv.setTypeface(TypefaceUtils.getBrashTypeface());
		 mActionBar.setCustomView(tv);
	}
	
	private TextView makeTitle () {
		 ActionBar.LayoutParams params = new ActionBar.LayoutParams(ActionBar.LayoutParams.MATCH_PARENT,ActionBar.LayoutParams.MATCH_PARENT);
		 TextView tv = new TextView(this);
		 tv.setLayoutParams(params);
		 tv.setGravity(Gravity.CENTER_VERTICAL | Gravity.LEFT);
		 tv.setTextColor(Color.WHITE);
		 tv.setTextSize(26);
		 tv.setTypeface(TypefaceUtils.getBrashTypeface());
		 return tv;
	}

}
