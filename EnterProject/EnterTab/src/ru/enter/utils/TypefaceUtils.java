package ru.enter.utils;

import ru.enter.ApplicationTablet;
import android.graphics.Typeface;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;

public class TypefaceUtils {
	
	public static final char ROUBLE = 230;
	
	public static final char NORMAL = 'a';
	public static final char BOLD = 'c';
	
	private static Spannable mNormalRouble;
	private static Typeface mNormalTypeface;
	private static Typeface mBoldTypeface;
	private static Typeface mBrashTypeface;
	private static Typeface mItalicTypeface;
	
	public static Spannable getNormalRoubleSpannable(){
		
		if (mNormalRouble == null){
			mNormalRouble = makeRouble(NORMAL);
		}
		return mNormalRouble;
	}
	
	/**
	 * 
	 * @param stringToFormat must be with TypefaceUtils.ROUBLE
	 * @param style Typeface.BOLD, Typeface.NORMAL, etc
	 * @param rouble (english 'a' - 's') get from rouble.ttf
	 * @return spanned result
	 */
	public static CharSequence formatRoubleString (String string, int style, char rouble) {
		
		SpannableStringBuilder builder = new SpannableStringBuilder();
		
		String[] parts = string.split(String.valueOf(ROUBLE));
		int partsNum = parts.length;
		
		for (int i = 0; i < partsNum; i++) {
			String str = parts[i];
			SpannableString span = new SpannableString(str);
			span.setSpan(new StyleSpan(style), 0, str.length(), Spannable.SPAN_INCLUSIVE_INCLUSIVE);
			builder.append(span);
			
			if (i < partsNum - 1 || (partsNum == 1)) {
				builder.append(makeRouble(rouble));
			}
		}
		
		return builder;
	}
	
	/**
	 * 
	 * @param rouble small english a-s get from rouble.ttf
	 * @return span with rouble symbol
	 */
	public static Spannable makeRouble (char rouble) {
		String str = String.valueOf(rouble);
		SpannableString span = new SpannableString(str);
		span.setSpan(new RoubleTypefaceSpan(), 0, str.length(), Spannable.SPAN_INCLUSIVE_INCLUSIVE);
		return span;
	}
	
	
	public static Spannable makeBrash (String text){
		SpannableString span = new SpannableString(text);
		span.setSpan(new BrashTypefaceSpan(), 0, text.length(), Spannable.SPAN_INCLUSIVE_INCLUSIVE);
		return span;
	}
	
	public static Spannable setSpannableColor (Spannable span, int color){
		span.setSpan(new ForegroundColorSpan(color), 0, span.length(), Spannable.SPAN_INCLUSIVE_INCLUSIVE);
		return span;
	}
	
	public static Typeface getRoubleTypeface() {
		return Utils.getRoubleTypeFace(ApplicationTablet.getContext());
	}
	
	public static Typeface getNormalTypeface() {
		if (mNormalTypeface == null)
			mNormalTypeface = Typeface.createFromAsset(ApplicationTablet.getContext().getAssets(), "fonts/EnterType_Normal.otf");
		return mNormalTypeface;
	}

	public static Typeface getBoldTypeface() {
		if (mBoldTypeface == null)
			mBoldTypeface = Typeface.createFromAsset(ApplicationTablet.getContext().getAssets(), "fonts/EnterType_Bold.ttf");
		return mBoldTypeface;
	}	

	public static Typeface getBrashTypeface() {
		if (mBrashTypeface == null)
			mBrashTypeface = Typeface.createFromAsset(ApplicationTablet.getContext().getAssets(), "fonts/EnterType_Brash.otf");
		return mBrashTypeface;
	}
	
	public static Typeface getItalicTypeface() {
		if (mItalicTypeface == null)
			mItalicTypeface = Typeface.createFromAsset(ApplicationTablet.getContext().getAssets(), "fonts/EnterType_Italic.ttf");
		return mItalicTypeface;
	}
	
		
}
