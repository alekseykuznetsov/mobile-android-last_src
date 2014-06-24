package ru.enter.widgets;

import ru.enter.utils.TypefaceUtils;
import android.content.Context;
import android.util.AttributeSet;
import android.widget.EditText;

public class EditTextNormal extends EditText {

	public EditTextNormal(Context context) {
		this(context, null);
	}

	public EditTextNormal(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public EditTextNormal(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs);
		setTypeface(TypefaceUtils.getNormalTypeface());
	}
}
