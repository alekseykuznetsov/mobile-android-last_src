package ru.enter.widgets;

import ru.enter.R;
import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

public class NumberPicker extends LinearLayout implements OnClickListener {

	private TextView mText;
	
	private int mMin;
	private int mCurrent;
	private int mMax;

	private ImageButton mButtonMinus;
	private ImageButton mButtonPlus;

	private OnNumberChangedListener mListener;

	public NumberPicker(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs);
		setOrientation(HORIZONTAL);
		LayoutInflater inflater = LayoutInflater.from(context);
		inflater.inflate(R.layout.number_picker, this, true);

		mText = (TextView) findViewById(R.id.number_picker_count);
		mButtonMinus = (ImageButton) findViewById(R.id.number_picker_minus);
		mButtonPlus = (ImageButton) findViewById(R.id.number_picker_plus);

		mButtonMinus.setOnClickListener(this);
		mButtonPlus.setOnClickListener(this);
		
		mMin = mCurrent = mMax = 0;
	}

	public NumberPicker(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public NumberPicker(Context context) {
		this(context, null);
	}

	public void setRange(int start, int end) {
		if (end != 0 && start < end){
			mMin = start;
			mMax = end;
		}
	}

	public int getCurrent() {
		return mCurrent;
	}

	public void setCurrent(int current) {
		if (mMin <= current && current <= mMax){
			mCurrent = current;
		} else {
			mCurrent = mMin;
		}
		
		mText.setText(String.valueOf(mCurrent));
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.number_picker_minus:
			if (mMin < mCurrent){
				mCurrent --;
				setCurrent(mCurrent);
				notifyChange();
			}
			break;
		case R.id.number_picker_plus:
			if (mCurrent < mMax){
				mCurrent ++;
				setCurrent(mCurrent);
				notifyChange();
			}
			break;
		default:
			break;
		}
		
		mButtonMinus.setEnabled(mMin < mCurrent);
		mButtonPlus.setEnabled(mCurrent < mMax);
	}
	
	public interface OnNumberChangedListener {
		void onChanged(int newNumber);
	}
	
	public void setOnNumberChangedListener(OnNumberChangedListener listener) {
		mListener = listener;
	}
	
	private void notifyChange(){
		if (mListener != null) {
			mListener.onChanged(mCurrent);
		}
	}

}
