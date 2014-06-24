package ru.enter;

import java.util.ArrayList;

import com.flurry.android.FlurryAgent;

import ru.enter.base.BaseActivity;
import android.os.Bundle;

public class BannersActivity extends BaseActivity{
	
	public static final String ID_LIST = "products_ids";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.banners_ac);
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
	
	public ArrayList<Integer> getIDExtraArray () {
		return getIntent().getIntegerArrayListExtra(ID_LIST);
	}

}
