package ru.enter.adapters;

import java.util.ArrayList;
import java.util.List;

import ru.enter.R;
import ru.enter.beans.FilterBean;
import ru.enter.beans.OptionsBean;
import ru.enter.utils.FiltersManager;
import ru.enter.utils.RangeSeekBar;
import ru.enter.utils.RangeSeekBar.OnRangeSeekBarChangeListener;
import ru.enter.utils.RangeSeekBarDiscret;
import ru.enter.utils.RangeSeekBarDiscret.OnRangeSeekBarDiscretChangeListener;
import ru.enter.utils.Utils;
import android.content.Context;
import android.graphics.Color;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class FilterAdapter extends BaseExpandableListAdapter{
	
	private static final int USUAL = 5;
	private static final int SEEK_BAR = 6;
	private static final int DESCRETE_SEEK_BAR = 3;
	
	private LayoutInflater mInflater;
	private List<FilterBean> mFilters;
	private Context mContext;
	
	private FiltersManager mFilterManager;
	
	public FilterAdapter(Context context) {
		mInflater = LayoutInflater.from(context);
		mFilters = new ArrayList<FilterBean>();
		mContext = context;
		
		mFilterManager = FiltersHolder.getFilterManager();
	}
	
	public void setObjects(List<FilterBean> filters){
		if ( ! Utils.isEmptyList(filters)) {
			mFilters = filters;
		}
	}

	@Override
	public OptionsBean getChild(int groupPosition, int childPosition) {
		return mFilters.get(groupPosition).getOptions().get(childPosition);
	}

	@Override
	public long getChildId(int groupPosition, int childPosition) {
		return childPosition;
	}
	
	@Override
	public int getChildrenCount(int groupPosition) {
		int type = getGroup(groupPosition).getType();
		switch (type) {
		case USUAL:
			return getGroup(groupPosition).getOptions().size();
		case SEEK_BAR:
			return 1;
		case DESCRETE_SEEK_BAR:
			return 1;
		}
		return 0;
	}

	@Override
	public View getChildView(final int groupPosition, final int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
		
		final FilterBean group = (FilterBean) getGroup(groupPosition);
		int type = group.getType();
		
		switch (type) {
		case USUAL:
			OptionsBean bean = getChild(groupPosition, childPosition);
			return getUsualView(bean);
			
		case SEEK_BAR:
			return getSeekBarView(group); 
			
		case DESCRETE_SEEK_BAR:
			return getDescreteSeekbar(group);

		default:
			return new View(mContext);
		}
		
	}
	
	private View getUsualView (final OptionsBean bean) {
		View view = mInflater.inflate(R.layout.tags_list_row, null);
		TextView text = (TextView) view.findViewById(R.id.tags_list_row_text);
		final CheckBox check = (CheckBox) view.findViewById(R.id.tags_list_row_check);

		text.setText(bean.getName());	
		
		//именно в таком порядке
		check.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				if (isChecked) {
					mFilterManager.add(bean.getId());
				} else {
					mFilterManager.remove(bean.getId());
				}
			}
		});
		
		check.setChecked(mFilterManager.contains(bean.getId()));	
			
		view.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				check.toggle();
			}
		});

		return view;
	}
	
	private View getSeekBarView (final FilterBean filter) {
		View view = mInflater.inflate(R.layout.tags_list_row_seek, null);
		
		final TextView text_interval = (TextView) view.findViewById(R.id.tags_list_row_seek_text);
		final String filter_id = filter.getId();
		int min = mFilterManager.getSliderMinValue(filter_id);
		int max = mFilterManager.getSliderMaxValue(filter_id);
		
		RangeSeekBar<Integer> seekBar = new RangeSeekBar<Integer>(filter.getMin(), filter.getMax(), mInflater.getContext());
		seekBar.setOnRangeSeekBarChangeListener(new OnRangeSeekBarChangeListener<Integer>() {
		        @Override
		        public void onRangeSeekBarValuesChanged(RangeSeekBar<?> bar, Integer minValue, Integer maxValue) {
		        	mFilterManager.setSliderMinValue(filter_id, minValue);
					mFilterManager.setSliderMaxValue(filter_id, maxValue);
		        	text_interval.setText(String.format("%s - %s", minValue, maxValue));
		        }
		});
		
		seekBar.setSelectedMinValue(min);
		seekBar.setSelectedMaxValue(max);
		text_interval.setText(String.format("%s - %s", min, max));
		
		RelativeLayout layout = (RelativeLayout) view.findViewById(R.id.tags_list_row_seek_seekbar);
		layout.addView(seekBar);
		return view;
	}
	
	private View getDescreteSeekbar (final FilterBean filter) {
		View view = mInflater.inflate(R.layout.tags_list_row_seek, null);
		
		final TextView text_interval = (TextView) view.findViewById(R.id.tags_list_row_seek_text);
		final String filter_id = filter.getId();
		int min = mFilterManager.getSliderMinValue(filter_id);
		int max = mFilterManager.getSliderMaxValue(filter_id);
		
		RangeSeekBarDiscret<Integer> seekBar = new RangeSeekBarDiscret<Integer>(filter.getMin(), filter.getMax(), mInflater.getContext());
		seekBar.setOnRangeSeekBarDiscretChangeListener(new OnRangeSeekBarDiscretChangeListener<Integer>() {
				@Override
				public void onRangeSeekBarDiscretValuesChanged (RangeSeekBarDiscret<?> bar, Integer minValue, Integer maxValue) {
					mFilterManager.setSliderMinValue(filter_id, minValue);
					mFilterManager.setSliderMaxValue(filter_id, maxValue);
		        	text_interval.setText(String.format("%s - %s", minValue, maxValue));
					
				}
			});
		
		seekBar.setSelectedMinValue(min);
		seekBar.setSelectedMaxValue(max);
		text_interval.setText(String.format("%s - %s", min, max));
		
		RelativeLayout layout = (RelativeLayout) view.findViewById(R.id.tags_list_row_seek_seekbar);
		layout.addView(seekBar);
		return view;
	}

	@Override
	public FilterBean getGroup(int groupPosition) {
		return mFilters.get(groupPosition);
	}

	@Override
	public int getGroupCount() {
		return mFilters.size();
	}

	@Override
	public long getGroupId(int groupPosition) {
		return groupPosition;//TODO
	}

	@Override
	public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
		
			FilterBean bean  = (FilterBean) getGroup(groupPosition);
			
			ListView.LayoutParams linear_params = new ListView.LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT);
			LinearLayout.LayoutParams text_params = new LinearLayout.LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT);
			LinearLayout.LayoutParams image_params = new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
			
			Context context = mInflater.getContext();
			
			View view = new LinearLayout(context);
			view.setLayoutParams(linear_params);
			((LinearLayout) view).setGravity(Gravity.CENTER_VERTICAL);
			view.setBackgroundResource(R.drawable.navbar);
			
			TextView text = new TextView(context);
			text_params.weight = 1;
			text.setLayoutParams(text_params);
			text.setTextColor(Color.WHITE);
			text.setTextSize(18);
			text.setText(bean.getName());
			text.setGravity(Gravity.LEFT);
			text.setPadding(20, 10, 0, 10);
			
			ImageView iv = new ImageView(context);
			image_params.setMargins(0, 0, 20, 0);
			iv.setLayoutParams(image_params);
			iv.setBackgroundResource(isExpanded ? R.drawable.arr_white_up : R.drawable.arr_white);
			
			
			((LinearLayout) view).addView(text);
			((LinearLayout) view).addView(iv);
		
		return view;
	}

	@Override
	public boolean hasStableIds() {
		return true;
	}

	@Override
	public boolean isChildSelectable(int groupPosition, int childPosition) {
		return true;
	}
	
}
