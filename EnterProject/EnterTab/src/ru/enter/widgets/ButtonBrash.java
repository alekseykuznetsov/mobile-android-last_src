package ru.enter.widgets;

import ru.enter.utils.TypefaceUtils;
import android.content.Context;
import android.util.AttributeSet;
import android.widget.Button;

public class ButtonBrash extends Button {

	public ButtonBrash(Context context) {
		this(context, null);
	}

	public ButtonBrash(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public ButtonBrash(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs);
		setTypeface(TypefaceUtils.getBrashTypeface());
	}
}
