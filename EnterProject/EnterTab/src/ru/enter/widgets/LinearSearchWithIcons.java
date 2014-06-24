package ru.enter.widgets;

import ru.enter.R;
import ru.enter.utils.TypefaceUtils;
import android.content.Context;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.View.OnKeyListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;

public class LinearSearchWithIcons extends LinearLayout implements
		OnKeyListener, OnClickListener {

	private EditText mSearch;
	private ImageView mClose;
	private OnEnterListener mEnterListener;

	public LinearSearchWithIcons(Context context) {
		this(context, null);
	}

	public LinearSearchWithIcons(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public LinearSearchWithIcons(Context context, AttributeSet attrs,
			int defStyle) {
		super(context, attrs);

		LayoutInflater inflater = LayoutInflater.from(context);
		inflater.inflate(R.layout.custom_search, this, true);

		mSearch = (EditText) findViewById(R.id.custom_search_edit);
		mSearch.setTypeface(TypefaceUtils.getBrashTypeface());
		mClose = (ImageView) findViewById(R.id.custom_search_img_x);

		mClose.setOnClickListener(this);
		mSearch.setOnKeyListener(this);
	}

	public String getSearchQuery() {
		return mSearch.getText().toString();
	}

	public void setOnEnterListener(OnEnterListener listener) {
		mEnterListener = listener;
	}

	public interface OnEnterListener {
		void onEnterPressed(String searchQuery);
	}

	@Override
	public boolean onKey(View v, int keyCode, KeyEvent event) {
		if (event.getAction() == KeyEvent.ACTION_DOWN
				&& keyCode == KeyEvent.KEYCODE_ENTER) {
			if (mEnterListener != null) {
				mEnterListener.onEnterPressed(getSearchQuery());
			}
		}
		return false;
	}

	@Override
	public void onClick(View v) {
		mSearch.setText("");
	}
}
