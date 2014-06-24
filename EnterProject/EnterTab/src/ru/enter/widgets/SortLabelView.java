package ru.enter.widgets;



import ru.enter.R;
import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;


public class SortLabelView extends RelativeLayout {
	
	public static final int TOP = 0;//по возрастанию 
	public static final int LOWER = 1;// по убыванию
	private int sortType;
	
		
	private ImageView mImage;
	private TextViewBold mText;

	public SortLabelView(Context context) {
		super(context);
		init(context);			
	}
	
	public SortLabelView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context);
	}

	public SortLabelView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init(context);
	}
	
	private void init(Context context){
		sortType = TOP;
		inflate(context, R.layout.sort_label_view, this);
		//LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		//View _view = inflater.inflate(R.layout.sort_label_view, this, false);
		mImage = (ImageView) findViewById(R.id.sort_label_view_img);
		mText = (TextViewBold) findViewById(R.id.sort_label_view_text);
	}
	
	public void setText(String label){
		mText.setText(label);
//		requestLayout();
//		invalidate();
	}
	
	
	@Override
	public void setSelected(boolean selected) {
		// TODO Auto-generated method stub
		super.setSelected(selected);		
		if(selected) {
			mText.setTextColor(Color.rgb(234, 127, 0));			
		} else {
			mText.setTextColor(Color.WHITE);			
		}	
		updateImg();
//		requestLayout();
//		invalidate();
	}
	
	public void setSortType(int type) {
		switch (type) {
		case LOWER:
			sortType = LOWER;			
			break;

		default:
			sortType = TOP;			
			break;
		}
		updateImg();
//		requestLayout();
//		invalidate();
	}
	
	private void updateImg(){
		switch (sortType) {
		case LOWER:			
			if(isSelected()) {
				mImage.setImageResource(R.drawable.icn_arr_sort_down_2x);
			} else {
				mImage.setImageResource(R.drawable.icn_arr_sort_down2x);
			}			
			break;

		default:			
			if(isSelected()) {
				mImage.setImageResource(R.drawable.icn_arr_sort_up_2x);
			} else {
				mImage.setImageResource(R.drawable.icn_arr_sort_up2x);
			}
			break;
		}
		
	}
	
	public boolean isLowerType(){
		boolean key = (sortType == LOWER?true:false);
		return key;
	}
	
}
