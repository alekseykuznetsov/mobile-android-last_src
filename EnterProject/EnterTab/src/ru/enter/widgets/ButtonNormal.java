package ru.enter.widgets;

import ru.enter.utils.TypefaceUtils;
import android.content.Context;
import android.util.AttributeSet;
import android.widget.Button;

public class ButtonNormal extends Button {

	public ButtonNormal(Context context) {
		this(context, null);
	}

	public ButtonNormal(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public ButtonNormal(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs);
		setTypeface(TypefaceUtils.getNormalTypeface());
	}
}
