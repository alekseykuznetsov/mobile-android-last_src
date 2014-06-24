package ru.enter.widgets;

import ru.enter.utils.TypefaceUtils;
import android.content.Context;
import android.util.AttributeSet;
import android.widget.TextView;

public class TextViewBrash extends TextView {

	public TextViewBrash(Context context) {
		this(context, null);
	}

	public TextViewBrash(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public TextViewBrash(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs);
		
		if(!isInEditMode())
			setTypeface(TypefaceUtils.getBrashTypeface());
	}
}