package ru.enter.widgets;

import ru.enter.utils.TypefaceUtils;
import android.content.Context;
import android.util.AttributeSet;
import android.widget.RadioButton;

public class RadioButtonNormal extends RadioButton {

	public RadioButtonNormal(Context context) {
		this(context, null);
	}

	public RadioButtonNormal(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public RadioButtonNormal(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs);
		setTypeface(TypefaceUtils.getNormalTypeface());
	}
}
