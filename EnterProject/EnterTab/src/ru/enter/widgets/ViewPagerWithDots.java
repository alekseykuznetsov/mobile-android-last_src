package ru.enter.widgets;

import java.util.Timer;
import java.util.TimerTask;

import ru.enter.R;
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

public class ViewPagerWithDots extends RelativeLayout implements OnClickListener, OnPageChangeListener{
	
	private static final int CHANGE_DELAY = 7000;
	private static final int MAX_DOTS_DEFAULT = 30;

	private ViewPager mPager;
	private RadioGroup mRadioGroup;
	private ImageButton mButtonLeft;
	private ImageButton mButtonRight;
	private ImageView mEmptyImage;

	private PagerAdapter mAdapter;
	private Timer mTimer;

	private int mDotsNumber;

	private boolean mChange;

	public ViewPagerWithDots(Context context) {
		this(context, null);
	}

	public ViewPagerWithDots(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public ViewPagerWithDots(Context context, AttributeSet attrs, int defStyle) {
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

		mDotsNumber = MAX_DOTS_DEFAULT;
		
	}
	
	public void setMaxDots (int max) {
		if (max >= 0) mDotsNumber = max;
	}
	
	public void setAdapter (PagerAdapter adapter) {
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
		
		//не проверяю на null, при таком раскладе сюда вообще не должны заходить
		int count = mAdapter.getCount();
		
		mEmptyImage.setVisibility(count == 0 ? View.VISIBLE : View.GONE);
		
		//изначально позиция в нуле, левая стрелка не видна
		mButtonLeft.setVisibility(View.GONE);
		mButtonRight.setVisibility(count > 1 ? View.VISIBLE : View.GONE);
		
		int counter = (count < mDotsNumber) ? count : mDotsNumber;
		
		
		//при одном объекте точку не рисуем
		if (counter > 1) {
			for (int i = 0; i < counter; i++) {
				
				RadioButton dot = new RadioButton(getContext());
				dot.setButtonDrawable(R.drawable.selector_btn_radio_dots);
				
				//ставлю id для кнопок для удобного выбора. Простой, от 0
				dot.setId(i);
				dot.setEnabled(false);
				// устанавливаем размеры точек
				mRadioGroup.addView(dot, 20, 20);
			}
			
			//выберем первую точку
			changeDot(0);
		}
	}

	private void changeDot(int position) {
		//кол-во элементов для отображения
		int count = mAdapter.getCount();
		//середина четная или нет
		boolean isEven = (mDotsNumber % 2 == 0);
		//ищем середину. Если четная то не попадаем на точку, нужно сдвинуться назад
		int half = isEven ? (mDotsNumber / 2) - 1 : mDotsNumber / 2;
		//еще столько раз надо показывать выделенную точку
		int loopCount = count - mDotsNumber;
		
		if (position < half) {
			mRadioGroup.check(position);
		} else if (position <=  half + loopCount) {
			mRadioGroup.check(half);
		} else {
			//последняя позиция = кол-во - 1. Сначала считаю позицию относительно оставшегося куска данных. Далее относительно точек
			mRadioGroup.check(mDotsNumber - (count - position - 1) - 1);
		}
	}

	private void checkArrows(int selectedPosition) {
		mButtonLeft.setVisibility(selectedPosition > 0 ? View.VISIBLE : View.GONE);
		mButtonRight.setVisibility(selectedPosition < mAdapter.getCount() - 1 ? View.VISIBLE : View.GONE);
	}
	
	public void enableChange (boolean toChange) {
		mChange = toChange;
		if (mChange) {
			startAnimation();
		} else {
			stopAnimation();
		}
	}
	
	private void startAnimation () {
		//проверяю нужна ли анимация
		if ( ! mChange || mAdapter.getCount() < 2) return;
		
		mTimer = new Timer();
		
		//запустить смену страниц в пейджере
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
	
	private void stopAnimation () {
		//если анимация уже запущена, очищаем таймер
		if(mChange && mTimer != null){
			mTimer.cancel();
			mTimer.purge();
		}
	}

	@Override
	public void onClick(View v) {
		int currentPosition = mPager.getCurrentItem();
		
		switch (v.getId()) {
		case R.id.button_left :
			mPager.setCurrentItem(currentPosition - 1, true);
			break;
		case R.id.button_right:
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
		checkArrows(selectedPosition);
		changeDot(selectedPosition);
		
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
