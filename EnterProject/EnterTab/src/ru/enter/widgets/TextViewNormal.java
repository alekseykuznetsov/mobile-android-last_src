package ru.enter.widgets;

import ru.enter.utils.TypefaceUtils;
import android.content.Context;
import android.util.AttributeSet;
import android.widget.TextView;

public class TextViewNormal extends TextView {

	public TextViewNormal(Context context) {
		this(context, null);
	}

	public TextViewNormal(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public TextViewNormal(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs);
		setTypeface(TypefaceUtils.getNormalTypeface());
		
	}
}
