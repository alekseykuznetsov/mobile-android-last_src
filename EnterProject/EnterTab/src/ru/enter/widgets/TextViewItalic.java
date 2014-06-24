package ru.enter.widgets;

import ru.enter.utils.TypefaceUtils;
import android.content.Context;
import android.util.AttributeSet;
import android.widget.TextView;

public class TextViewItalic extends TextView {

	public TextViewItalic(Context context) {
		this(context, null);
	}

	public TextViewItalic(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public TextViewItalic(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs);
		setTypeface(TypefaceUtils.getItalicTypeface());
	}
}
