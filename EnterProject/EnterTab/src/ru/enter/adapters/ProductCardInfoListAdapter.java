package ru.enter.adapters;

import java.util.ArrayList;

import com.google.analytics.tracking.android.EasyTracker;

import net.londatiga.android.QuickAction;
import ru.enter.ProductCardActivity;
import ru.enter.R;
import ru.enter.beans.ModelProductBean;
import ru.enter.beans.OptionArrayBean;
import ru.enter.beans.OptionBean;
import ru.enter.beans.ProductBean;
import ru.enter.beans.ProductModelBean;
import ru.enter.utils.Constants;
import ru.enter.utils.TypefaceUtils;
import ru.enter.utils.Utils;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;

public class ProductCardInfoListAdapter extends BaseAdapter {
	
	private ArrayList<OptionBean> mOptions;
	private ArrayList<ModelProductBean> mModels; 
	private LayoutInflater mInflater;
	private Typeface tfBold, tfNormal;
	private Context mContext;
	
	//public ProductCardInfoListAdapter(Context context, ArrayList<OptionArrayBean> options) {
	public ProductCardInfoListAdapter(Context context, ProductBean bean) {
		mContext=context;
		mOptions = new ArrayList<OptionBean>();
		mInflater = LayoutInflater.from(context);
		setObjects(bean.getOptions());
		mModels = bean.getModelsProduct();
		tfBold = TypefaceUtils.getBoldTypeface();
		tfNormal = TypefaceUtils.getNormalTypeface();
	}

	 public boolean isEnabled(int position) { 
             return false; 
     } 
	
	/**
	 * Переводит ArrayList<OptionArrayBean> в ArrayList<OptionBean>
	 * @param optionArray
	 */
	private void setObjects(ArrayList<OptionArrayBean> optionArray){
		for (OptionArrayBean array : optionArray) {

			OptionBean option = new OptionBean();
			option.setProperty(array.getName());
			
			ArrayList<OptionBean> options = array.getOption();
			
			mOptions.add(option);
			mOptions.addAll(options);
		}
		
		notifyDataSetChanged();
	}

	@Override
	public int getCount() {
		return mOptions.size();
	}

	@Override
	public OptionBean getItem(int position) {
		return mOptions.get(position);
	}

	@Override
	public long getItemId(int position) {
		return 0;
	}
	
	

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		
		boolean isModel;
		
		ModelProductBean curMod = null;
		
		View view;
		
		OptionBean bean = getItem(position);
		String option = bean.getOption();
		String value = bean.getValue();
		String unit = bean.getUnit();
		String property = bean.getProperty();
		
		
		
		
		if (TextUtils.isEmpty(option) && TextUtils.isEmpty(value)) {
			view = mInflater.inflate(R.layout.product_card_tab_options_list_item_title, null);
			((TextView) view).setText(property);
			((TextView) view).setTypeface(tfBold);
		} else {
			isModel = false;
			if(!Utils.isEmptyList(mModels))
				for (ModelProductBean mod : mModels) {
					if (property.equals(mod.getProperty()))	{
						isModel = true;
						curMod = mod;
						break;
					}
				}
			if (isModel) {
				final ModelProductBean model = curMod;
				view = mInflater.inflate(R.layout.product_card_tab_options_list_item_model, null);
				TextView type = (TextView) view.findViewById(R.id.product_card_tab_model_description_list_item_text_type);
				type.setTypeface(tfNormal);
				TextView desc = (TextView) view.findViewById(R.id.product_card_tab_model_description_list_item_text_description);
				desc.setTypeface(tfNormal);
				TextView modView = (TextView) view.findViewById(R.id.product_card_tab_model_description_list_item_variants);
				modView.setTypeface(tfNormal);
				modView = (TextView) view.findViewById(R.id.product_card_tab_model_description_list_item_show);
				modView.setTypeface(tfNormal);
				modView.setText(android.text.Html.fromHtml("<u>Показать</u>"));
				
				modView.setOnClickListener(new OnClickListener() {
					
					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						
						
						final QuickAction quickAction = new QuickAction(mContext,QuickAction.VERTICAL, false);
						ListView listModel = (ListView) mInflater.inflate(R.layout.product_card_model_list, null);
						ProductCardModelAdapter modelAdapter = new ProductCardModelAdapter(mContext, model);
						listModel.setAdapter(modelAdapter);
						listModel.setOnItemClickListener(new OnItemClickListener() {

							@Override
							public void onItemClick(AdapterView<?> parent, View view,
									int position, long id) {
								// TODO Auto-generated method stub
								ProductModelBean modBean = (ProductModelBean) parent.getItemAtPosition(position);
								ProductBean prodBean = modBean.getProductBean();
								EasyTracker.getTracker().sendEvent("product/get", "buttonPress", prodBean.getName(), (long) prodBean.getId());
								Intent intent = new Intent();
								intent.setClass(mContext, ProductCardActivity.class);
								intent.putExtra(ProductCardActivity.PRODUCT_ID, prodBean.getId());
								intent.putExtra(ProductCardActivity.EXTRA_FROM, Constants.FLURRY_FROM.Catalog.toString());
								quickAction.dismiss();
								mContext.startActivity(intent);
							}
						});
						quickAction.addActionItem(listModel);
						quickAction.show(v);
						}
						
						
				});

				type.setText(property);

				String descText = "";
				if (!TextUtils.isEmpty(value) && value != "null") {
					descText = value;
				} else {
					// с сервиса/парсера приходит какашка, массив в строку
					// загоняется
					option = option.replace("[\"", "");
					option = option.replace("\"]", "");
					option = option.replace("\",\"", ", ");
					descText = option;
				}

				if (!TextUtils.isEmpty(unit) && unit != "null") {
					descText = descText + " " + unit;
				}
				desc.setText(descText);


			} else {
				view = mInflater.inflate(R.layout.product_card_tab_options_list_item, null);
				TextView type = (TextView) view.findViewById(R.id.product_card_tab_description_list_item_text_type);
				type.setTypeface(tfNormal);
				TextView desc = (TextView) view.findViewById(R.id.product_card_tab_description_list_item_text_description);
				desc.setTypeface(tfNormal);

				type.setText(property);

				String descText = "";
				if (!TextUtils.isEmpty(value) && value != "null") {
					descText = value;
				} else {
					// с сервиса/парсера приходит какашка, массив в строку
					// загоняется
					option = option.replace("[\"", "");
					option = option.replace("\"]", "");
					option = option.replace("\",\"", ", ");
					descText = option;
				}

				if (!TextUtils.isEmpty(unit) && unit != "null") {
					descText = descText + " " + unit;
				}
				desc.setText(descText);

			}
		}
		
		return view;
	}



}
