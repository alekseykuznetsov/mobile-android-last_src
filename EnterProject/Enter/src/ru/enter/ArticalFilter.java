package ru.enter;

import android.text.InputFilter;
import android.text.Spanned;

public class ArticalFilter implements InputFilter {

	@Override
	public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
		
	    String checkedText = dest.subSequence(0, dstart).toString() + source.subSequence(start, end) + dest.subSequence(dend,dest.length()).toString();
	    int length = checkedText.length();
	    if(length > 8)
	    	return "";
	    for(int i = 0; i < length; i++){
	    	Boolean b = match(i, checkedText.charAt(i));
	    	if(b == null)
	    		return "-";
	    	else if(b == false)
	    		return "";
	    }
	    return null;
	}
	
	private Boolean match(int position, char ch) {
		if(position >= 0 && position < 3)
			if(Character.isDigit(ch))
				return true;
			else
				return false;
		
		if(position == 3)
			if(ch == '-')
				return true;
			else
				return null;
		
		if(position > 3)
			if(Character.isDigit(ch))
				return true;
			else
				return false;
		
		return false;
	}

}