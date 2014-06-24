package ru.enter.widgets;

import java.util.List;

import ru.enter.ApplicationTablet;
import ru.enter.R;
import ru.enter.adapters.AbstractListAdapter;
import ru.enter.data.CatalogNode;
import ru.enter.utils.OnBarNavigationListener;
import ru.enter.utils.Utils;
import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListPopupWindow;
import android.widget.TextView;

import com.webimageloader.ImageLoader;
import com.webimageloader.ext.ImageHelper;

public class CatalogNavigator extends LinearLayout {

	private static final int ICON_SIZE = 0; 
	private OnBarNavigationListener<CatalogNode> mNavigationListener;
	
	private Adapter mAdapter;
	private ListPopupWindow mListPopupWindow;
	private LinearLayout mCategoryLinear;
	private TextView mCategoryName;
	private TextView mTitle;
	
	private ImageView mCategoryImage;
	private ImageHelper mImageLoader;
	
	private int mPopupWidth;
	
	public CatalogNavigator(Context context) {
		this(context, null);
	}
	
	public CatalogNavigator(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}
	
	public CatalogNavigator(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs);
		
		setOrientation(LinearLayout.HORIZONTAL);
		
		LayoutInflater inflater = LayoutInflater.from(context);
		inflater.inflate(R.layout.catalog_navigator, this);
		
		mAdapter = new Adapter(getContext());
		
		mCategoryLinear = (LinearLayout) findViewById(R.id.catalog_navigator_category_linear);
		mCategoryName = (TextView) findViewById(R.id.catalog_navigator_category_name);
		mCategoryImage = (ImageView) findViewById(R.id.catalog_navigator_image);
		
		mTitle = (TextView) findViewById(R.id.catalog_navigator_title);
		
		createPopup();
		initLoader();
		
		mCategoryLinear.setOnClickListener(mClick);
		
	}
	
	private void initLoader() {
		ImageLoader loader = ApplicationTablet.getLoader(getContext());
		mImageLoader = new ImageHelper(getContext(), loader);
	}

	private void createPopup() {
		mListPopupWindow = new ListPopupWindow(getContext());
		mListPopupWindow.setModal(true);
		mListPopupWindow.setAnchorView(mCategoryLinear);
	    mListPopupWindow.setAdapter(mAdapter);
	    mListPopupWindow.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int pos, long id) {
				// меняем шапку
				CatalogNode node = (CatalogNode) view.getTag();
				
				mListPopupWindow.dismiss();
				// вызываем колбэк в активити\фрагмент
				if (mNavigationListener != null) {
					mNavigationListener.onNavigationBarItemSelected(pos, node, view);
				}
			}
		});
	}
	
	private void showPopup() {
		if (mPopupWidth == 0) {
			mPopupWidth = calculatePopupWidth();
			//заполняется только один раз, поэтому и ширину посчитаем раз
			mListPopupWindow.setContentWidth(mPopupWidth);
		}
		mListPopupWindow.show();
	}
	
	private int calculatePopupWidth() {
		int maxWidth = 0;
		View view = null;
		FrameLayout fakeParent = new FrameLayout(getContext());
		for (int i = 0, count = mAdapter.getCount(); i < count; i++) {
			view = mAdapter.getView(i, view, fakeParent);
			view.measure(View.MeasureSpec.UNSPECIFIED,View.MeasureSpec.UNSPECIFIED);
			int width = view.getMeasuredWidth();
			if (width > maxWidth) {
				maxWidth = width;
			}
		}
		return maxWidth;
	}

	private OnClickListener mClick = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			showPopup();
		}
	};
	
	private static String formatIcon(CatalogNode node, int icon_size){
		String url = node.getNode().getIcon();
		String size = node.getNode().getIconSize().get(icon_size);
		
		String formated_url = url.replace("__icon_size__", size);
		
		return formated_url;
	}
	
	/**
	 * Устанавливает элементы выпадающего меню
	 * @param items элементы для отображения в меню
	 */
	public void setMenuItems (List<CatalogNode> items) {
		mAdapter.setObjects(items);
	}
	
	/**
	 * Установить шапку меню
	 * @param node
	 */
	public void setCategory(CatalogNode node) {
		if (node == null) {
			mCategoryName.setText("Выберите категорию");
			mCategoryImage.setVisibility(View.GONE);
		} else {
			mCategoryName.setText(node.getNode().getName());
			String icon_url = formatIcon(node, ICON_SIZE);
			mCategoryImage.setVisibility(View.VISIBLE);
			mImageLoader.load(mCategoryImage, icon_url);
		}
	}

	/**
	 * Установить правый заголовок
	 * @param title
	 */
	public void setTitle(String title){
		if (title == null || title.equals(mCategoryName.getText())) {
			title = "";
		}
		mTitle.setText(title);
	}
	
	/**
	 * Установить слушателя изменений меню
	 * @param listener
	 */
	public void setOnBarNavigationListener (OnBarNavigationListener<CatalogNode> listener) {
		mNavigationListener = listener;
	}
	
	
	//------------------------------------ADAPTER------------------------------------//
	
	private static class Adapter extends AbstractListAdapter<CatalogNode> {

		private ImageHelper mImageLoader;

		public Adapter(Context context) {
			super(context);
			ImageLoader loader = ApplicationTablet.getLoader(context);
			mImageLoader = new ImageHelper(context, loader);//TODO images
		}

		@Override
		public long getItemId(int position) {
			return getItem(position).getNode().getId();
		}

		@Override
		public View getView(int position, View convertView, CatalogNode bean) {
			if (convertView == null) {
				convertView = getInflater().inflate(R.layout.catalog_navigator_row, null);
			}
			
			ImageView image = (ImageView) convertView.findViewById(R.id.catalog_navigator_row_image);
			TextView name = (TextView) convertView.findViewById(R.id.catalog_navigator_row_category_name);
			
			String icon_url = formatIcon(bean, ICON_SIZE);
			mImageLoader.load(image, icon_url);
			
			name.setText(bean.getNode().getName());
			
			convertView.setTag(bean);
			return convertView;
		}
		
	}

}
