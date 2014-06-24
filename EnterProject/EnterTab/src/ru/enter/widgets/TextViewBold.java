package ru.enter.widgets;

import ru.enter.utils.TypefaceUtils;
import android.content.Context;
import android.util.AttributeSet;
import android.widget.TextView;

public class TextViewBold extends TextView {

	public TextViewBold(Context context) {
		this(context, null);
	}

	public TextViewBold(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public TextViewBold(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs);
		
		if(!isInEditMode())
			setTypeface(TypefaceUtils.getBoldTypeface());
	}
}
