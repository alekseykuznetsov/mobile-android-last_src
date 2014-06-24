package ru.enter.fragments;

import ru.enter.R;
import ru.enter.ServicesActivity;
import android.app.Fragment;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout.LayoutParams;

public class ServicesMainFragment extends Fragment implements OnClickListener{
	
	public static ServicesMainFragment getInstance(){
		return new ServicesMainFragment();
	}

	@Override
	public void onStart() {
		super.onStart();
		onConfigurationChanged(getResources().getConfiguration());
	}	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		View view = inflater.inflate(R.layout.services_main_fr, null);
		
		Button furniture = (Button) view.findViewById(R.id.services_main_fr_button_furniture);
		furniture.setOnClickListener(this); 
		
		Button electronic = (Button) view.findViewById(R.id.services_main_fr_button_electronic);
		electronic.setOnClickListener(this); 
		Button household_appliances = (Button) view.findViewById(R.id.services_main_fr_button_household_appliances);
		household_appliances.setOnClickListener(this); 
		Button sport = (Button) view.findViewById(R.id.services_main_fr_button_sport);
		sport.setOnClickListener(this); 
		
		return view;	
	}

	@Override
	public void onClick(View v) {
		Integer result = 0;

		switch (v.getId()) {
		case R.id.services_main_fr_button_furniture:
			result = 2;
			break;
		case R.id.services_main_fr_button_electronic:
			result = 0;
			break;
		case R.id.services_main_fr_button_household_appliances:
			result = 1;
			break;
		case R.id.services_main_fr_button_sport:
			result = 3;
			break;
		
		default:
			break;
		}
		
		ServicesActivity mActiviy = (ServicesActivity)getActivity();
		mActiviy.startDetails(result);
	}
	
	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		
		ImageView image = (ImageView) getView().findViewById(R.id.services_main_fr_image);
		
		if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
			image.setBackgroundResource(R.drawable.services_main_fr_top_image_land);
			
		} else if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {
			image.setBackgroundResource(R.drawable.services_main_fr_top_image_port);
		}
		
	}
}