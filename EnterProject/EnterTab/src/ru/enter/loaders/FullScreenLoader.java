package ru.enter.loaders;

import ru.enter.dialogs.ProgressDialogFragment;
import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.AsyncTaskLoader;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;

public abstract class FullScreenLoader<T> extends AsyncTaskLoader<T> implements OnCancelListener {
	
	private static final String PROGRESS_DIALOG_TAG = "progress_dialog_fragment";
	
	private ProgressDialogFragment mProgress;
	private Activity mActivity;

	public FullScreenLoader(Context context) {
		super(context);
		mActivity = (Activity) context;
	}
	
	protected void showDialog () {
		mProgress = ProgressDialogFragment.getInstance();
		mProgress.show(mActivity.getFragmentManager(), PROGRESS_DIALOG_TAG);
		mProgress.setOnCancelListener(this);
	}
	
	protected void dismissDialog () {
		if (mProgress != null && mProgress.isVisible()) {
			mProgress.dismiss();
		}
	}
	
	public Activity getActivity () {
		return mActivity;
	}

	@Override
	public void onCancel(DialogInterface dialog) {
		cancelLoad();
		dialog.dismiss();
	}

}
