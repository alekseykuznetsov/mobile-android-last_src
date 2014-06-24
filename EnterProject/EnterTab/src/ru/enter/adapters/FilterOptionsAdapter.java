package ru.enter.adapters;

import java.util.ArrayList;
import java.util.List;

import ru.enter.R;
import ru.enter.beans.FilterBean;
import ru.enter.beans.OptionsBean;
import ru.enter.parsers.FiltersParser;
import ru.enter.utils.FiltersManager;
import ru.enter.utils.RangeSeekBar;
import ru.enter.utils.RangeSeekBar.OnRangeSeekBarChangeListener;
import ru.enter.utils.RangeSeekBarDiscret;
import ru.enter.utils.RangeSeekBarDiscret.OnRangeSeekBarDiscretChangeListener;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.CheckBox;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class FilterOptionsAdapter extends BaseExpandableListAdapter {

	private LayoutInflater mInflater;
	private List<FilterBean> mGroups;
	private FiltersManager mManager;

	public FilterOptionsAdapter(Context context) {
		mInflater = LayoutInflater.from(context);
		mGroups = new ArrayList<FilterBean>();
	}

	public void setFilterManager (FiltersManager manager) {
		List<FilterBean> filters = manager.getFilters();
		if (filters == null) {
			filters = new ArrayList<FilterBean>();
		}

		mGroups = filters;
		mManager = manager;
		notifyDataSetChanged();
	}

	@Override
	public OptionsBean getChild (int groupPosition, int childPosition) {
		return mGroups.get(groupPosition).getOptions().get(childPosition);
	}

	@Override
	public long getChildId (int groupPosition, int childPosition) {
		return childPosition;
	}

	@Override
	public View getChildView (int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {

		final FilterBean group = (FilterBean) getGroup(groupPosition);
		int type = group.getType();
		switch (type) {
		case FiltersParser.FILTER_OPTIONAL:

			convertView = mInflater.inflate(R.layout.filters_fr_explist_item_child, null);

			final OptionsBean child = (OptionsBean) getChild(groupPosition, childPosition);

			TextView childtxt = (TextView) convertView.findViewById(R.id.filters_fr_explist_child_text_name);
			final CheckBox checkbox = (CheckBox) convertView.findViewById(R.id.filters_fr_explist_child_checkbox);

			childtxt.setText(child.getName());
			checkbox.setChecked(mManager.contains(child.getId()));
			convertView.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick (View v) {
					boolean isChecked = mManager.contains(child.getId());
					// меняем текущее состояние флага
					if (!isChecked) {
						checkbox.setChecked(true);
						mManager.add(child.getId());
					} else {
						checkbox.setChecked(false);
						mManager.remove(child.getId());
					}
				}
			});
			break;
		case FiltersParser.FILTER_SOLID:
			convertView = setRangeSeekBar(group);
			break;
			
		case FiltersParser.FILTER_DISCRET:
			convertView = setRangeSeekBarDiscret(group);
			break;
		}
		return convertView;
	}

	private View setRangeSeekBar(FilterBean filter){
		View view = mInflater.inflate(R.layout.filters_fr_explist_item_child_interval, null);
		
		final TextView text_interval = (TextView) view.findViewById(R.id.filters_fr_explist_item_child_inteval_text);
		final String filter_id = filter.getId();
		int min = mManager.getSliderMinValue(filter_id);
		int max = mManager.getSliderMaxValue(filter_id);

		RangeSeekBar<Integer> seekBar = new RangeSeekBar<Integer>(filter.getMin(), filter.getMax(), mInflater.getContext());
		seekBar.setOnRangeSeekBarChangeListener(new OnRangeSeekBarChangeListener<Integer>() {
		        @Override
		        public void onRangeSeekBarValuesChanged(RangeSeekBar<?> bar, Integer minValue, Integer maxValue) {
		        	mManager.setSliderMinValue(filter_id, minValue);
					mManager.setSliderMaxValue(filter_id, maxValue);
		        	text_interval.setText(String.format("%s - %s", minValue, maxValue));
		        }
		});
		
		seekBar.setSelectedMinValue(min);
		seekBar.setSelectedMaxValue(max);
		text_interval.setText(String.format("%s - %s", min, max));
		
		RelativeLayout layout = (RelativeLayout) view.findViewById(R.id.filters_fr_explist_item_child_inteval_seekbar);
		layout.addView(seekBar);
		return view;
	}
	
	private View setRangeSeekBarDiscret(FilterBean filter){
		View view = mInflater.inflate(R.layout.filters_fr_explist_item_child_interval, null);
		
		final TextView text_interval = (TextView) view.findViewById(R.id.filters_fr_explist_item_child_inteval_text);
		final String filter_id = filter.getId();
		int min = mManager.getSliderMinValue(filter_id);
		int max = mManager.getSliderMaxValue(filter_id);
		
		RangeSeekBarDiscret<Integer> seekBar = new RangeSeekBarDiscret<Integer>(filter.getMin(), filter.getMax(), mInflater.getContext());
		seekBar.setOnRangeSeekBarDiscretChangeListener(new OnRangeSeekBarDiscretChangeListener<Integer>() {
				@Override
				public void onRangeSeekBarDiscretValuesChanged (RangeSeekBarDiscret<?> bar, Integer minValue, Integer maxValue) {
					mManager.setSliderMinValue(filter_id, minValue);
					mManager.setSliderMaxValue(filter_id, maxValue);
		        	text_interval.setText(String.format("%s - %s", minValue, maxValue));
					
				}
			});
		
		seekBar.setSelectedMinValue(min);
		seekBar.setSelectedMaxValue(max);
		text_interval.setText(String.format("%s - %s", min, max));
		
		RelativeLayout layout = (RelativeLayout) view.findViewById(R.id.filters_fr_explist_item_child_inteval_seekbar);
		layout.addView(seekBar);
		return view;
	}
	
	
	@Override
	public int getChildrenCount (int groupPosition) {
		int type = mGroups.get(groupPosition).getType();
		switch (type) {
		case FiltersParser.FILTER_OPTIONAL:
			return mGroups.get(groupPosition).getOptions().size();
		case FiltersParser.FILTER_SOLID:
			return 1;
		case FiltersParser.FILTER_DISCRET:
			return 1;
		}
		return 0;
	}

	@Override
	public FilterBean getGroup (int groupPosition) {
		return mGroups.get(groupPosition);
	}

	@Override
	public int getGroupCount () {
		return mGroups.size();
	}

	@Override
	public long getGroupId (int groupPosition) {
		return groupPosition;
	}

	@Override
	public View getGroupView (int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {

		FilterBean group = (FilterBean) getGroup(groupPosition);
		if (convertView == null) {
			convertView = mInflater.inflate(R.layout.filters_fr_explist_item_group, null);
		}
		TextView grouptxt = (TextView) convertView.findViewById(R.id.filters_fr_explist_group_text_name);

		grouptxt.setText(group.getName());

		if (isExpanded) {
			grouptxt.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.arrow_white_down, 0);
		} else {
			grouptxt.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.arrow_white_right, 0);
		}

		return convertView;
	}

	@Override
	public boolean hasStableIds () {
		return true;
	}

	@Override
	public boolean isChildSelectable (int arg0, int arg1) {
		return true;
	}

}
