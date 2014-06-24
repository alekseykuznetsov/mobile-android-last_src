package ru.enter.dialogs;

import ru.enter.R;
import android.app.DialogFragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;

public class CatalogEmptyBasketDialogFragment extends DialogFragment implements OnClickListener{

    @Override
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setStyle(0, R.style.custom_dialog_dark);
    }
    
	public CatalogEmptyBasketDialogFragment getInstance() {
		return new CatalogEmptyBasketDialogFragment();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		
		getDialog().setTitle("В корзине нет товаров");
		
		View view = inflater.inflate(R.layout.catalog_dialog_empty_basket, null);
		Button cancel = (Button) view.findViewById(R.id.catalog_dialog_empty_basket_button_cancel);
		
		cancel.setOnClickListener(this);
		return view;
	}

	@Override
	public void onClick(View v) {
		dismiss();
	}
}
