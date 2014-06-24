package ru.enter.utils;

import android.app.Fragment;
import android.app.LocalActivityManager;
import android.os.Bundle;

public class LocalActivityManagerFragment extends Fragment{

	private static final String TAG = LocalActivityManagerFragment.class.getSimpleName();
	private static final String STATE = "localActivityManagerState";

	protected LocalActivityManager mLocalActivityManager;
	

	@Override
	public void onCreate (Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Bundle state = null;
		if (savedInstanceState != null) {
			state = savedInstanceState.getBundle(STATE);
		}
		mLocalActivityManager = new LocalActivityManager(getActivity(), true);
		mLocalActivityManager.dispatchCreate(state);
	}

	@Override
	public void onActivityCreated (Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
	}

	@Override
	public void onSaveInstanceState (Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putBundle(STATE, mLocalActivityManager.saveInstanceState());
	}

	@Override
	public void onResume () {
		super.onResume();
		mLocalActivityManager.dispatchResume();
	}

	@Override
	public void onPause () {
		super.onPause();
		mLocalActivityManager.dispatchPause(getActivity().isFinishing());
	}

	@Override
	public void onStop () {
		super.onStop();
		mLocalActivityManager.dispatchStop();
	}

	@Override
	public void onDestroy () {
		super.onDestroy();
		mLocalActivityManager.dispatchDestroy(getActivity().isFinishing());
	}

}