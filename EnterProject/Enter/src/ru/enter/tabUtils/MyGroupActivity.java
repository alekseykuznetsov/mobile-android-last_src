package ru.enter.tabUtils;

import java.util.HashMap;

import android.app.*;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;

public class MyGroupActivity extends FragmentActivity {
	private static final String STATES_KEY = "android:states";
    static final String PARENT_NON_CONFIG_INSTANCE_KEY = "android:parent_non_config_instance";

    /**
     * This field should be made private, so it is hidden from the SDK.
     * {@hide}
     */
    protected LocalActivityManager mLocalActivityManager;
    
    public MyGroupActivity() {
        this(true);
    }
    
    public MyGroupActivity(boolean singleActivityMode) {
        mLocalActivityManager = new LocalActivityManager(this, singleActivityMode);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle states = savedInstanceState != null
                ? (Bundle) savedInstanceState.getBundle(STATES_KEY) : null;
        mLocalActivityManager.dispatchCreate(states);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mLocalActivityManager.dispatchResume();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        Bundle state = mLocalActivityManager.saveInstanceState();
        if (state != null) {
            outState.putBundle(STATES_KEY, state);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        mLocalActivityManager.dispatchPause(isFinishing());
    }

    @Override
    protected void onStop() {
        super.onStop();
        mLocalActivityManager.dispatchStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mLocalActivityManager.dispatchDestroy(isFinishing());
    }

    /**
     * Returns a HashMap mapping from child activity ids to the return values
     * from calls to their onRetainNonConfigurationInstance methods.
     *
     * {@hide}
     */
    
    /*
    public HashMap<String,Object> onRetainNonConfigurationChildInstances() {
        return mLocalActivityManager.dispatchRetainNonConfigurationInstance();
    }
    */

    public Activity getCurrentActivity() {
        return mLocalActivityManager.getCurrentActivity();
    }

    public final LocalActivityManager getLocalActivityManager() {
        return mLocalActivityManager;
    }
/*
      @Override
 
    void dispatchActivityResult(String who, int requestCode, int resultCode,
            Intent data) {
        if (who != null) {
            Activity act = mLocalActivityManager.getActivity(who);
            
            if (act != null) {
                act.onActivityResult(requestCode, resultCode, data);
                return;
            }
        }
        super.dispatchActivityResult(who, requestCode, resultCode, data);
    }
    */
}
