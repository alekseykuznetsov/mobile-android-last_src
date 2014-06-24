package ru.enter.widgets;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import ru.enter.CatalogActivity;
import ru.enter.FiltersActivity;
import ru.enter.R;
import ru.enter.Listeners.RemoveTagListener;
import ru.enter.adapters.FiltersHolder;
import ru.enter.beans.FilterBean;
import ru.enter.beans.OptionsBean;
import ru.enter.beans.SliderSolidBean;
import ru.enter.parsers.FiltersParser;
import ru.enter.utils.FiltersManager;
import ru.enter.utils.Utils;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class TagsView extends LinearLayout implements OnClickListener {

	private static RemoveTagListener listener;
	private LinearLayout temp;
	private LinearLayout base_layout, upper_layout, bottom_layout;
	private LayoutInflater inflater;
	private Context context;
	private TextView warning;
	private boolean FLAG;
	private FiltersManager mManager;

	public TagsView(Context context) {
		super(context);
		this.context = context;
	}

	public TagsView(Context context, AttributeSet attrs) {
		super(context, attrs);
		this.context = context;
	}

	public void init (boolean tagsOrFilters) {

		FLAG = tagsOrFilters;

		inflater = (LayoutInflater) context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
		temp = (LinearLayout) inflater.inflate(R.layout.header_tags, null);
		this.removeAllViews();
		this.addView(temp);
		temp.setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT));
		upper_layout = (LinearLayout) temp.findViewById(R.id.header_tags_upper_linear);
		bottom_layout = (LinearLayout) temp.findViewById(R.id.header_tags_bottom_linear);
		base_layout = (LinearLayout) temp.findViewById(R.id.header_tags_scroll_tags_layout);

		warning = (TextView) temp.findViewById(R.id.header_tags_title);
		warning.setTypeface(Utils.getTypeFace(context));
		if (FLAG)
			warning.setText("Используйте теги");
		else
			warning.setText("Используйте фильтры");

		Button btn_add = (Button) temp.findViewById(R.id.header_tags_btn_add);
		Button btn_empty = (Button) temp.findViewById(R.id.header_tags_btn_empty);

		OnClickListener listener = new OnClickListener() {

			@Override
			public void onClick (View v) {
				if (!FLAG){
					Intent intent = new Intent(context, FiltersActivity.class);
					Activity activity = (Activity) context;
					intent.putExtras(activity.getIntent());
					activity.getParent().startActivityForResult(intent, 123);
				}
			}
		};

		btn_add.setTypeface(Utils.getTypeFace(context));
		btn_empty.setTypeface(Utils.getTypeFace(context));
		btn_add.setOnClickListener(listener);
		btn_empty.setOnClickListener(listener);

		setupTags();

		this.setVisibility(View.VISIBLE);// TODO
	}

	private void setupTags () {

		base_layout.removeAllViews();
		mManager = FiltersHolder.getFilterManager();
		List<OptionsBean> usual = mManager.getOptions();
		ArrayList<FilterBean> seekbars = mManager.getSliders();
		
		if (Utils.isEmptyList(usual) && Utils.isEmptyList(seekbars)) {
			upper_layout.setVisibility(View.GONE);
			bottom_layout.setVisibility(View.VISIBLE);
		} else {
			upper_layout.setVisibility(View.VISIBLE);
			bottom_layout.setVisibility(View.GONE);
			
			//добавили обычные теги
			if ( ! Utils.isEmptyList(usual)) {
				for (OptionsBean tag : usual) {
					addTagView(tag);
				}
			}
			
			if ( ! Utils.isEmptyList(seekbars)) {
				for (FilterBean filter : seekbars) {
					addTagView(filter);
				}
			}
		}
	}

	private void addTagView (FilterBean filter) {
		if (inflater != null) {
			SliderSolidBean info = mManager.getSliderInfo (filter);
			
			RelativeLayout layout = (RelativeLayout) inflater.inflate(R.layout.tags_to_delete, null);
			if (layout != null) {
				TextView tag_name = (TextView) layout.findViewById(R.id.tags_to_delete_text);
				tag_name.setTextSize(18);
				tag_name.setPadding(5, 5, 10, 5);
				tag_name.setTextColor(Color.WHITE);
				tag_name.setTypeface(null, Typeface.BOLD);

				tag_name.setText(filter.getName() + String.format(" %s - %s", info.getCurrentMin(), info.getCurrentMax()));
				ImageButton button = (ImageButton) layout.findViewById(R.id.tags_to_delete_button);

				button.setTag(R.id.view_in_tags, layout);
				button.setTag(R.id.bean_in_tags, filter);
				button.setOnClickListener(this);

				if (base_layout != null)
					base_layout.addView(layout);
			}
		}
	}
	
	private void addTagView (OptionsBean bean) {
		if (inflater != null) {
			RelativeLayout layout = (RelativeLayout) inflater.inflate(R.layout.tags_to_delete, null);
			if (layout != null) {
				TextView tag_name = (TextView) layout.findViewById(R.id.tags_to_delete_text);
				tag_name.setTextSize(18);
				tag_name.setPadding(5, 5, 10, 5);
				tag_name.setTextColor(Color.WHITE);
				tag_name.setTypeface(null, Typeface.BOLD);

				tag_name.setText(bean.getName());
				ImageButton button = (ImageButton) layout.findViewById(R.id.tags_to_delete_button);

				button.setTag(R.id.view_in_tags, layout);
				button.setTag(R.id.bean_in_tags, bean);
				button.setOnClickListener(this);

				if (base_layout != null)
					base_layout.addView(layout);
			}
		}
	}

	public boolean isTagsVisible () {
		if (FLAG && this.getVisibility() == View.VISIBLE)
			return true;
		return false;
	}

	public boolean isFiltersVisible () {
		if (!FLAG && this.getVisibility() == View.VISIBLE)
			return true;
		return false;
	}

	@Override
	public void onClick (View v) {
		Object tag = v.getTag(R.id.bean_in_tags);
		if (tag instanceof OptionsBean) {
			listener.removeFilter((OptionsBean)tag);
		} else {
			listener.removeSeekFilter((FilterBean) tag);
		}
		
		base_layout.removeView((View) v.getTag(R.id.view_in_tags));
		invalidate();
	}

	public static void setListener (RemoveTagListener listner) {
		listener = listner;
	}
}
