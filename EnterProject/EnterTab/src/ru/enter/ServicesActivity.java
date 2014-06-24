package ru.enter;

import java.util.ArrayList;

import com.flurry.android.FlurryAgent;
import com.google.analytics.tracking.android.EasyTracker;

import ru.enter.base.BaseMenuActivity;
import ru.enter.data.ServicesNode;
import ru.enter.dialogs.ProgressDialogFragment;
import ru.enter.fragments.ServicesLeftFragment;
import ru.enter.fragments.ServicesMainFragment;
import ru.enter.fragments.ServicesRightFragment;
import ru.enter.parsers.ServicesParser;
import ru.enter.utils.PreferencesManager;
import ru.enter.utils.URLManager;
import android.app.ActionBar;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.res.Configuration;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout.LayoutParams;

public class ServicesActivity extends BaseMenuActivity {

	public static final String SERVICE_ID = "serviceid";
	private int mServiceId;

	private ArrayList<ServicesNode> mServicesTree;
	private ServicesNode mCurrentParent;
	private ServicesLoader mLoader = null;
	
	public ArrayList<ServicesNode> getServicesTreeRoots(){
		return mServicesTree;
	}
	
	public ServicesNode getCurrentParent(){
		return mCurrentParent;
	}
	
	public void setCurrentParent(ServicesNode currentParent){
		mCurrentParent = currentParent;
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {		
		super.onCreate(savedInstanceState);
		EasyTracker.getInstance().setContext(this);
		setContentView(R.layout.services_ac);
		
		setTitleCenter(getResources().getString(R.string.actionbar_f1));
		
		onConfigurationChanged(getResources().getConfiguration());
		start();
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

	public void start() {
		FragmentManager manager = getFragmentManager();
		FragmentTransaction transaction = manager.beginTransaction();

		ServicesMainFragment mainFragment = ServicesMainFragment.getInstance();

		transaction.replace(R.id.services_ac_main_frame, mainFragment);
		transaction.commit();
	}

	
	public void startDetails(int serviceId) {
		mServiceId = serviceId;
		if(mServicesTree == null) {
			mLoader = new ServicesLoader();
			mLoader.execute();
		}
		else {
			startLeft();
			mCurrentParent = null;
			startRight();
		}
	}

	public void startLeft() {

		FragmentManager manager = getFragmentManager();
		Fragment main = manager.findFragmentById(R.id.services_ac_main_frame);
		FragmentTransaction transaction = manager.beginTransaction();
		transaction.hide(main);
		transaction.addToBackStack(null);

		ServicesLeftFragment leftFragment = ServicesLeftFragment.getInstance();
		
		Bundle extras = new Bundle();
		extras.putInt(SERVICE_ID, mServiceId);
		leftFragment.setArguments(extras);

		transaction.replace(R.id.services_ac_left_frame, leftFragment);
		transaction.commit();
	}

	public void startRight(){
		
		FragmentManager manager = getFragmentManager();
		FragmentTransaction transaction = manager.beginTransaction();

		ServicesRightFragment rightFragment = ServicesRightFragment.getInstance();

		transaction.replace(R.id.services_ac_right_frame, rightFragment);
		transaction.commit();
	}
	
	class ServicesLoader extends AsyncTask<Void, Void, ArrayList<ServicesNode>> {

		private ProgressDialogFragment progress;

		@Override
		protected void onPreExecute() {
			progress = ProgressDialogFragment.getInstance();
			progress.setCancelable(true);
			progress.setOnCancelListener(mCancel);
			progress.show(getFragmentManager(), "progress");
			
		}

		@Override
		protected ArrayList<ServicesNode> doInBackground(Void... params) {
			return new ServicesParser().parse(URLManager.getServicesTree(PreferencesManager.getCityid(),200));
		}
		
		@Override
		protected void onPostExecute(ArrayList<ServicesNode> result) {
			mServicesTree = result;
			startLeft();
			
			new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
				
				@Override
				public void run() {
					progress.dismiss();		
				}
			}, 1);
			
			
		}
	}
	
	private OnCancelListener mCancel = new OnCancelListener() {
			
			@Override
			public void onCancel(DialogInterface dialog) {
				mLoader.cancel(true);
				dialog.dismiss();
			}
		};
		
	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		
		FrameLayout frameLeft = (FrameLayout) findViewById(R.id.services_ac_left_frame);
		
		if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
			LayoutParams params = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT, 1.8f);
			frameLeft.setLayoutParams(params);
		} else if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {
			LayoutParams params = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT, 1.5f);
			frameLeft.setLayoutParams(params);
		}
		
	}

}
