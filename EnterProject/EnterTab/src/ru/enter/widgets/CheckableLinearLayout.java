package ru.enter.widgets;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.Checkable;
import android.widget.CheckedTextView;
import android.widget.LinearLayout;

/*
 * Этот класс полезен при использовании в Листе, где нужно выделять чекбокс
 * Обязательно ! Иметь в разметке CheckedTextView  с id = android.R.id.checkbox
 */
public class CheckableLinearLayout extends LinearLayout implements Checkable {
	private CheckedTextView mCheckBox;
    	
    public CheckableLinearLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
	}
    
    @Override
    protected void onFinishInflate() {
    	super.onFinishInflate();
    	mCheckBox = (CheckedTextView) findViewById(android.R.id.checkbox);
    }
    
    @Override 
    public boolean isChecked() { 
        return mCheckBox != null ? mCheckBox.isChecked() : false; 
    }
    
    @Override 
    public void setChecked(boolean checked) {
    	if (mCheckBox != null) mCheckBox.setChecked(checked);
    }
    
    @Override 
    public void toggle() { 
    	if (mCheckBox != null) mCheckBox.toggle();
    } 
}