package ru.enter.dialogs;

import ru.enter.ApplicationTablet;
import ru.enter.R;
import ru.enter.DataManagement.BasketManager;
import ru.enter.beans.ServiceBean;
import ru.enter.dialogs.alert.BasketAddDialogFragment;
import ru.enter.utils.RoubleTypefaceSpan;
import android.app.DialogFragment;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableString;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.analytics.tracking.android.EasyTracker;
import com.webimageloader.ImageLoader;
import com.webimageloader.ext.ImageHelper;

public class ServiceCardDialogFragment extends DialogFragment implements OnClickListener{

	private ServiceBean mService;
	private ImageHelper mImageLoader;

	public ServiceCardDialogFragment(ServiceBean service) {
		mService = service;
	}

    @Override
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setStyle(0, R.style.custom_dialog);
        
        ImageLoader loader = ApplicationTablet.getLoader(getActivity());
		mImageLoader = new ImageHelper(getActivity(), loader)
        .setFadeIn(true)
        .setLoadingResource(R.drawable.tmp_1)
        .setErrorResource(R.drawable.tmp_1);//TODO
    }
    
	public static ServiceCardDialogFragment getInstance(ServiceBean service) {
		return new ServiceCardDialogFragment(service);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		
		getDialog().setTitle(mService.getName());
		
		View view = inflater.inflate(R.layout.services_dialog_info, null);
		TextView priceView = (TextView) view.findViewById(R.id.services_dialog_info_text_price);
		TextView priceText = (TextView) view.findViewById(R.id.services_dialog_info_text_price_text);
		TextView ruble = (TextView) view.findViewById(R.id.services_dialog_info_text_price_P);
		TextView work = (TextView) view.findViewById(R.id.services_dialog_info_text_work);
		ImageView image = (ImageView) view.findViewById(R.id.services_dialog_info_image);
		Button buy = (Button) view.findViewById(R.id.services_dialog_info_button_buy);
		
		ruble.setText("p");
		ruble.setTypeface(ApplicationTablet.getRoubleTypeface());
	
		work.setText(mService.getWork());
		mImageLoader.load(image, mService.getFoto());
		buy.setOnClickListener(this);
		
		if (mService.getPriceTypeId() == 2) {
			String textPrice = String.format("%s%% от стоимости товара (минимум %s рублей)", mService.getPricePercent(), mService.getPriceMin());
			priceText.setText(textPrice);
			priceView.setVisibility(View.GONE);
			ruble.setVisibility(View.GONE);
			buy.setEnabled(false);
		} else {
			priceText.setVisibility(View.GONE);
			int price = (int)mService.getPrice();
			if (price == 0) {
				priceView.setText("Бесплатно");
				ruble.setVisibility(View.GONE);
			} else {
				priceView.setText(String.valueOf(price));
				ruble.setVisibility(View.VISIBLE);				
			}
		}
		
		return view;
	}

	@Override
	public void onClick(View v) {
		BasketManager.addService(mService);
		EasyTracker.getTracker().sendEvent("cart/add-service", "buttonPress", mService.getName(), (long) mService.getId());		
		BasketAddDialogFragment dialog = BasketAddDialogFragment.getInstance();
		dialog.setServiceMessage(mService.getName());
		dialog.show(getFragmentManager(), "basket_add");
	}
}
