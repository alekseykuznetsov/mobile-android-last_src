package ru.enter.adapters;

import ru.enter.R;
import ru.enter.utils.Utils;
import android.content.Context;
import android.graphics.Color;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class TempServicesGridAdapter extends BaseAdapter {
    private Context mContext;
    
    private int []img = {R.drawable.btn_f1_4,R.drawable.btn_f1_8,R.drawable.btn_f1_3,R.drawable.btn_f1_2};
    private String[] titles = {"Мебель", "Спорт", "Бытовая техника", "Электроника"};

    public TempServicesGridAdapter(Context c) {
        mContext = c;
    }
    

    public int getCount() {
        return 4;
    }

    public Object getItem(int position) {
        return null;
    }

    public long getItemId(int position) {
    	switch (position){
    		case 0: return 305;//мебель
    		case 1: return 306;//спорт
    		case 2: return 309;//бытовая техника
    		case 3: return 308;//электроника
    	}
        return position;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
    	
    	LayoutInflater viewInflater = (LayoutInflater)mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View view = viewInflater.inflate(R.layout.catalog_grid_row, null);

		TextView text = (TextView)view.findViewById(R.id.catalog_grid_row_text);
		ImageView iw = (ImageView)view.findViewById(R.id.catalog_grid_row_img);
        
        iw.setImageResource(img[position]);
        text.setGravity(Gravity.CENTER);
        text.setText(titles[position]);
        text.setTypeface(Utils.getTypeFace(mContext));
       	text.setTextColor(Color.WHITE);
       	text.setPadding(0, 0, 0, 40);
       	
        return view;
    }
}