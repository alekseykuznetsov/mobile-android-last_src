package ru.enter.dialogs;

import java.util.List;

import ru.enter.R;
import ru.enter.adapters.CitiesListAdapter;
import ru.enter.beans.CitiesBean;
import ru.enter.listeners.OnCitySelectListener;
import ru.enter.listeners.OnFirstStartListener;
import ru.enter.loaders.CityLoader;
import ru.enter.parsers.ShopParser;
import ru.enter.utils.DialogSize;
import ru.enter.utils.PreferencesManager;
import ru.enter.utils.Utils;
import android.app.DialogFragment;
import android.app.LoaderManager.LoaderCallbacks;
import android.content.Loader;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.View.OnKeyListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout.LayoutParams;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class CitiesDialogFragment extends DialogFragment implements OnClickListener, LoaderCallbacks<List<CitiesBean>>{
	
	private static final int CITY_LOADER_ID = 200;
	
	private CitiesListAdapter mAdapter;
	private ListView mList;
	private CitiesBean mCityBean;
	private OnCitySelectListener mCityListener;
	private OnFirstStartListener mFirstListner;

	private List<CitiesBean> mCities;

	private FrameLayout mProgress;

    public static CitiesDialogFragment getInstance() {
        return new CitiesDialogFragment();
    }

    private void showProgress(){
    	if (mProgress != null)
    		mProgress.setVisibility(View.VISIBLE);
    }
    
    private void hideProgress(){
    	if (mProgress != null)
    		mProgress.setVisibility(View.GONE);
    }
    
    
    @Override
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setStyle(0, R.style.custom_dialog_dark);
        //чтобы пересоздавалась только разметка
        setRetainInstance(true);
        
        mCityBean = new CitiesBean();
        
        //делаем городом по умолчанию Москву
        if (PreferencesManager.getCityid() == -1){
            mCityBean.setName("");
            mCityBean.setId(-1);
        }else{
        	mCityBean.setName(PreferencesManager.getCityName());
        	mCityBean.setId(PreferencesManager.getCityid());
        }
    }
    
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    	getDialog().setTitle("Выберите город");
    	View view = inflater.inflate(R.layout.cities_dialog_fr, null);
    	
    	mProgress = (FrameLayout) view.findViewById(R.id.cities_dialog_fr_progress_frame);
    	
    	TextView empty = (TextView) view.findViewById(R.id.cities_dialog_fr_list_empty);
    	
    	ImageView x_icon = (ImageView) view.findViewById(R.id.cities_dialog_fr_edit_img_x);
    	x_icon.setOnClickListener(this);
    	
		mList = (ListView) view.findViewById(R.id.cities_dialog_fr_list);
		mList.setEmptyView(empty);
		mAdapter = new CitiesListAdapter(getActivity());
		mList.setAdapter(mAdapter);
		mAdapter.setObjects(mCities);
		mList.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
		getDialog().setOnDismissListener(null);
		mList.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
				
				//устанавливаем текущим город из списка
				mCityBean = (CitiesBean) arg1.getTag(CitiesListAdapter.BEAN_TAG);
				PreferencesManager.setCityName(mCityBean.getName());
		    	PreferencesManager.setCityId(mCityBean.getId());
		    	PreferencesManager.setCityHasShop(mCityBean.isHasShop());
		    	
		    	//затираем данные о магазине, если таковой был выбран
		    	PreferencesManager.setUserCurrentShopId(0);
				PreferencesManager.setUserCurrentShopName("");
				PreferencesManager.setFirstStartCatalog(0);
				
		    	if (mCityListener != null) mCityListener.onCitySelect(mCityBean);
		    	Toast.makeText(getActivity(), String.format("Вы выбрали город %s", mCityBean.getName()), Toast.LENGTH_SHORT).show();
//				dismissAllowingStateLoss(); TODO
				dismiss();
			}
		});
		
		EditText edit = (EditText) view.findViewById(R.id.cities_dialog_fr_edit);
		edit.addTextChangedListener(new TextWatcher() {
			
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				mAdapter.getFilter().filter(s);
			}
			
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,int after) {}
			
			@Override
			public void afterTextChanged(Editable s) {}
		});

    	return view;
    }
    
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
    	super.onActivityCreated(savedInstanceState);
    	//загрузчик
    	showProgress();
    	getLoaderManager().initLoader(CITY_LOADER_ID, null, this);
    }
    
    //обходной метод. Только с ним при повороте onDestroy на setRetainInstance(true)не срабатывает
    public void onDestroyView() {
		if (getDialog() != null && getRetainInstance())
			getDialog().setDismissMessage(null);
		super.onDestroyView();
	}
    
    @Override
    public void onDestroy() {
    	//сохраняем текущий выбор при выходе, чтобы не писать в настройки каждый раз при изменении
    	//PreferencesManager.setCityName(mCityBean.getName());
    	//PreferencesManager.setCityId(mCityBean.getId());
    	//if (mCityListener != null) mCityListener.onCitySelect(mCityBean);
    	//Toast.makeText(getActivity(), String.format("Вы выбрали город %s", mCityBean.getName()), Toast.LENGTH_SHORT).show();
    	if(mFirstListner!=null) mFirstListner.updates();
    	super.onDestroy();
    }
    
    public void setOnCitySelectListener (OnCitySelectListener listener) {
    	mCityListener = listener;
    }
    
    public void setOnFirstStartListener (OnFirstStartListener listener) {
    	mFirstListner = listener;
    }
    

	@Override
	public Loader<List<CitiesBean>> onCreateLoader(int id, Bundle args) {
		return new CityLoader(getActivity());
	}

	@Override
	public void onLoadFinished(Loader<List<CitiesBean>> loader, List<CitiesBean> result) {
		if ( ! Utils.isEmptyList(result) && Utils.isEmptyList(mCities)){
			mCities = result;
			mAdapter.setObjects(mCities);
			int toCheck = mAdapter.getPositionToCheck(mCityBean.getId());
			if (toCheck != -1) mList.setItemChecked(toCheck, true);
		}
		
		hideProgress();
	}

	@Override
	public void onLoaderReset(Loader<List<CitiesBean>> arg0) {}

	@Override
	public void onClick(View v) {
		EditText edit = (EditText) getView().findViewById(R.id.cities_dialog_fr_edit);
		edit.setText("");
	}
	
}
