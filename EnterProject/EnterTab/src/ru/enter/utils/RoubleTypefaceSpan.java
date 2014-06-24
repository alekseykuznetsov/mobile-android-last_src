package ru.enter.utils;

import android.graphics.Paint;
import android.graphics.Typeface;
import android.text.TextPaint;
import android.text.style.TypefaceSpan;

public class RoubleTypefaceSpan extends TypefaceSpan {

	public RoubleTypefaceSpan() {
		super("");
	}

	@Override
	public void updateDrawState(TextPaint paint) {
		applyCustomTypeFace(paint);
	}

	@Override
	public void updateMeasureState(TextPaint paint) {
		applyCustomTypeFace(paint);
	}

	private static void applyCustomTypeFace(Paint paint) {
		paint.setTypeface(TypefaceUtils.getRoubleTypeface());
	}
}
