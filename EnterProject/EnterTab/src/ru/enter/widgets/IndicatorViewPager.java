package ru.enter.widgets;

import java.util.Timer;
import java.util.TimerTask;

import ru.enter.R;
import ru.enter.adapters.BannerAdapter;
import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;

public class IndicatorViewPager extends RelativeLayout implements OnClickListener, OnPageChangeListener{
	
	private static final int CHANGE_DELAY = 7000;

	private ViewPager mPager;
	private RadioGroup mRadioGroup;
	private ImageButton mButtonLeft;
	private ImageButton mButtonRight;
	private ImageView mEmptyImage;

	private PagerAdapter mAdapter;
	private Timer mTimer;

	public IndicatorViewPager(Context context) {
		this(context, null);
	}

	public IndicatorViewPager(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public IndicatorViewPager(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs);
		
		LayoutInflater inflater = LayoutInflater.from(context);
		inflater.inflate(R.layout.custom_view_pager, this, true);

		mPager = (ViewPager) findViewById(R.id.view_pager);
		mRadioGroup = (RadioGroup) findViewById(R.id.radio_dots);
		mButtonLeft = (ImageButton) findViewById(R.id.button_left);
		mButtonRight = (ImageButton) findViewById(R.id.button_right);
		mEmptyImage  = (ImageView) findViewById(android.R.id.empty);

		mButtonLeft.setOnClickListener(this);
		mButtonRight.setOnClickListener(this);
		
		mPager.setOnPageChangeListener(this);
	}
	
	public void setAdapter (BannerAdapter adapter) {
		mAdapter = adapter;
		mPager.setAdapter(adapter);
	}
	
	public void setEmptyViewSource (int source) {
		mEmptyImage.setBackgroundResource(source);
	}
	
	public void notifyDataSetChanged () {
		mAdapter.notifyDataSetChanged();
		init();
	}
	
	private void init() {
		int count = mAdapter.getCount();
		
		mEmptyImage.setVisibility(count == 0 ? View.VISIBLE : View.GONE);
		
		mButtonLeft.setVisibility(count > 1 ? View.VISIBLE : View.GONE);
		mButtonRight.setVisibility(count > 1 ? View.VISIBLE : View.GONE);
		
		if (count > 1) {
			for (int i = 0; i < count; i++) {
				RadioButton dot = new RadioButton(getContext());
				dot.setButtonDrawable(R.drawable.selector_btn_radio_dots);
				dot.setId(i);
				dot.setEnabled(false);
				
				if(i == 0 || i == count - 1){
					dot.setVisibility(View.GONE);
				}
				
				mRadioGroup.addView(dot, 20, 20);
			}
			
			changeDot(1);
			mPager.setCurrentItem(1);
		}
	}

	private void changeDot(int position) {
		mRadioGroup.check(position);
	}

//	private void checkArrows(int selectedPosition) {
//		mButtonLeft.setVisibility(selectedPosition > 0 ? View.VISIBLE : View.GONE);
//		mButtonRight.setVisibility(selectedPosition < mAdapter.getCount() - 1 ? View.VISIBLE : View.GONE);
//	}
	
	public void startAnimation () {
		if (mAdapter.getCount() < 2) return;
		
		mTimer = new Timer();
		
		TimerTask task = new TimerTask() {
			
			@Override
			public void run() {
				post(new Runnable() {
					@Override
					public void run() {
						int curPos = mPager.getCurrentItem();
						mPager.setCurrentItem((curPos + 1) % mAdapter.getCount(), true);
					}
				});
			}
		};
		
		mTimer.schedule(task, CHANGE_DELAY, CHANGE_DELAY);
	}
	
	public void stopAnimation () {
		if(mTimer != null){
			mTimer.cancel();
			mTimer.purge();
		}
	}

	@Override
	public void onClick(View v) {
		int currentPosition = mPager.getCurrentItem();
		
		switch (v.getId()) {
		case R.id.button_left :
//			if(currentPosition == 1)
//				mPager.setCurrentItem(mAdapter.getCount() - 2, true);
//			else
				mPager.setCurrentItem(currentPosition - 1, true);
			break;
		case R.id.button_right:
//			if(currentPosition == mAdapter.getCount() - 2)
//				mPager.setCurrentItem(1, true);
//			else
				mPager.setCurrentItem(currentPosition + 1, true);
			break;
		}
	}

	@Override
	public void onPageScrollStateChanged(int arg0) {}

	@Override
	public void onPageScrolled(int arg0, float arg1, int arg2) {}

	@Override
	public void onPageSelected(int selectedPosition) {
		int ad_current = selectedPosition;

		if (ad_current == 0) {
			ad_current =  mAdapter.getCount() - 2;
			mPager.setCurrentItem(ad_current, false);
		} else if (ad_current ==  mAdapter.getCount() - 1) {
			ad_current = 1;
			mPager.setCurrentItem(ad_current, false);
		} else {
			mPager.setCurrentItem(ad_current, true);
		}


//		checkArrows(selectedPosition);
		changeDot(ad_current);
		
	}

	@Override
	public boolean onInterceptTouchEvent(MotionEvent ev) {
		switch (ev.getAction()) {
		case MotionEvent.ACTION_DOWN:
			stopAnimation();
			break;
		case MotionEvent.ACTION_UP:	
			startAnimation();
			break;
		default:
			break;
		}
		return super.onInterceptTouchEvent(ev);
	}
}
