package ru.enter.widgets;

import ru.enter.utils.TypefaceUtils;
import android.content.Context;
import android.util.AttributeSet;
import android.widget.Button;

public class ButtonBold extends Button {

	public ButtonBold(Context context) {
		this(context, null);
	}

	public ButtonBold(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public ButtonBold(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs);
		setTypeface(TypefaceUtils.getBoldTypeface());
	}
}
