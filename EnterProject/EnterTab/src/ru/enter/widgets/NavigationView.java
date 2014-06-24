package ru.enter.widgets;

import java.util.ArrayList;

import ru.enter.R;
import ru.enter.utils.ActionbarMenuItem;
import ru.enter.utils.NavigationViewAdapter;
import ru.enter.utils.OnBarNavigationListener;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ListPopupWindow;
import android.widget.TextView;
import android.view.View.OnClickListener;

public class NavigationView extends LinearLayout implements	OnClickListener,OnBarNavigationListener<ActionbarMenuItem> {

	private static final String TAG = "Nav";
	private Context mContext;
	private NavigationViewAdapter mAdapter;
	private OnBarNavigationListener<ActionbarMenuItem> mNavigationListener;
	private LinearLayout mHeader;
	private TextView mSpinner;
	private TextView mTitle;
	private ListPopupWindow mListPopupWindow;

	public NavigationView(Context context) {
		this(context, null, null);
	}

	public NavigationView(Context context, ViewGroup parent, AttributeSet attrs) {
		super(context, attrs);
		mContext = context;
		View.inflate(mContext, R.layout.actionbar_nav, this);

		mAdapter = new NavigationViewAdapter(mContext, null);
		mAdapter.setNavigationListener(this);
		
		mListPopupWindow = new ListPopupWindow(mContext);
		mListPopupWindow.setModal(true);
		//mListPopupWindow.setBackgroundDrawable(new BitmapDrawable());
		mHeader = (LinearLayout) findViewById(R.id.actionbar_nav_root);
		mTitle = (TextView) findViewById(R.id.actionbar_nav_text);
		mSpinner = (TextView) findViewById(R.id.actionbar_nav_spinner);
		mSpinner.setOnClickListener(this);
		
		setFocusable(true);
		setClickable(true);
	}

	public void addData(ArrayList<ActionbarMenuItem> data) {
		if (data != null && data.size() > 0) {
			mAdapter.setData(data);
			fillHeader(data.get(0));
			MeasuredDimendions dim = getMeashuredDimensions(mContext, mAdapter);
			mListPopupWindow.setContentWidth(dim.width);
		}
	}
	
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.actionbar_nav_spinner:
			showPopup();
			break;

		default:
			break;
		}
	}
	
	
	private void showPopup() {
        mListPopupWindow.setAnchorView(mHeader);
        mListPopupWindow.setAdapter(mAdapter);
		mListPopupWindow.show();
	}

	public OnBarNavigationListener getNavigationListener() {
		return mNavigationListener;
	}

	public void setNavigationListener(OnBarNavigationListener mNavigationListener) {
		this.mNavigationListener = mNavigationListener;
	}

	public NavigationViewAdapter getAdapter() {
		return mAdapter;
	}

	public void setAdapter(NavigationViewAdapter mAdapter) {
		this.mAdapter = mAdapter;		
	}

	/**
	 * Computes the widest view in an adapter, best used when you need to
	 * wrap_content on a ListView, please be careful and don't use it on an
	 * adapter that is extremely numerous in items or it will take a long time.
	 * 
	 * @param context
	 *            Some context
	 * @param adapter
	 *            The adapter to process
	 * @return The pixel width of the widest View
	 */
	public MeasuredDimendions getMeashuredDimensions(Context context, Adapter adapter) {
		int maxWidth = 0;
		int fullHeight = 0;
		View view = null;
		FrameLayout fakeParent = new FrameLayout(context);
		for (int i = 0, count = adapter.getCount(); i < count; i++) {
			view = adapter.getView(i, view, fakeParent);
			view.measure(View.MeasureSpec.UNSPECIFIED,View.MeasureSpec.UNSPECIFIED);
			int width = view.getMeasuredWidth();
			if (width > maxWidth) {
				maxWidth = width;
			}
			fullHeight+=view.getMeasuredHeight();
		}
		return new MeasuredDimendions(maxWidth,fullHeight);
	}

	public void setTitle(String title){
		mTitle.setText(title);
	}
	
	private void fillHeader(ActionbarMenuItem item) {
		// change textview appearence
		mSpinner.setText(item.text);
		Drawable icon = mContext.getResources().getDrawable(item.icon);
		mSpinner.setCompoundDrawablesWithIntrinsicBounds(icon, null, null, null);
	}

	@Override
	public void onNavigationBarItemSelected(int pos, ActionbarMenuItem item, View v) {
		// change textview appearence
		fillHeader(item);
		mListPopupWindow.dismiss();
		// call fragments/activity's callback
		if (mNavigationListener != null) {
			mNavigationListener.onNavigationBarItemSelected(pos, item, v);
		}
	}
	
	class MeasuredDimendions{
		public int width;
		public int height;
		public MeasuredDimendions(int width, int height) {
			super();
			this.width = width;
			this.height = height;
		}
	}

}
