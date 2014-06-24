package ru.enter.utils;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import ru.enter.beans.FilterBean;
import ru.enter.beans.OptionsBean;
import ru.enter.beans.SliderSolidBean;
import ru.enter.parsers.FiltersParser;
import ru.enter.utils.Pair;
import ru.enter.utils.Utils;

public class FiltersManager {

	private HashSet<Integer> mSelected = new HashSet<Integer>();
	private HashSet<Integer> mNew = new HashSet<Integer>();

	private List<FilterBean> mFilters = new ArrayList<FilterBean>();

	private List<SliderSolidBean> mSolidSlidersSelected = new ArrayList<SliderSolidBean>();
	private List<SliderSolidBean> mSolidSlidersNew = new ArrayList<SliderSolidBean>();

	public void add (int option_id) {
		mNew.add(option_id);
	}

	public void remove (int option_id) {
		mNew.remove(option_id);
	}
	
	public void reset () {
		mNew.clear();
		mSelected.clear();
	}

	public void clear () {
		mNew.clear();
		clearSliders();
	}

	public void apply () {
		mSelected.clear();
		mSelected.addAll(mNew);
		mSolidSlidersSelected.clear();
		mSolidSlidersSelected.addAll(mSolidSlidersNew);
	}

	public void init () {
		mNew.clear();
		mNew.addAll(mSelected);
		mSolidSlidersNew.clear();
		mSolidSlidersNew.addAll(mSolidSlidersSelected);
	}

	public boolean contains (int option_id) {
		return mNew.contains(option_id);
	}

	public void setFilters (List<FilterBean> filters) {
		mFilters = filters;
		createSlidersList();
	}

	private void clearSliders () {
		mSolidSlidersNew.clear();
		for (FilterBean filter : mFilters) {
			if (filter.getType() == FiltersParser.FILTER_SOLID  || filter.getType() == FiltersParser.FILTER_DISCRET) {
				SliderSolidBean slider = new SliderSolidBean();
				slider.setId(filter.getId());
				slider.setCurrentMin(filter.getMin());
				slider.setCurrentMax(filter.getMax());
				mSolidSlidersNew.add(slider);
			}
		}
	}

	private void createSlidersList () {
		mSolidSlidersSelected.clear();
		for (FilterBean filter : mFilters) {
			if (filter.getType() == FiltersParser.FILTER_SOLID || filter.getType() == FiltersParser.FILTER_DISCRET) {
				SliderSolidBean slider = new SliderSolidBean();
				slider.setId(filter.getId());
				slider.setCurrentMin(filter.getMin());
				slider.setCurrentMax(filter.getMax());
				mSolidSlidersSelected.add(slider);
			}
		}
		mSolidSlidersNew.clear();
		mSolidSlidersNew.addAll(mSolidSlidersSelected);
	}

	public List<FilterBean> getFilters () {
		return mFilters;
	}

	public boolean isEmpty () {
		return Utils.isEmptyList(mFilters);
	}

	public boolean hasSelectedOptions () {
		return !mSelected.isEmpty();
	}

	public List<OptionsBean> getOptions () {
		List<OptionsBean> result = new ArrayList<OptionsBean>();
		for (FilterBean filter : mFilters) {
			if (filter.getType() == FiltersParser.FILTER_OPTIONAL)
				for (OptionsBean option : filter.getOptions()) {
					if (mSelected.contains(option.getId())) {
						result.add(option);
					}
				}
		}
		return result;
	}

	public void setSliderMinValue (String id, int min_value) {
		for (SliderSolidBean slider : mSolidSlidersNew) {
			if (slider.getId().equals(id)) {
				slider.setCurrentMin(min_value);
			}
		}
	}

	public void setSliderMaxValue (String id, int max_value) {
		for (SliderSolidBean slider : mSolidSlidersNew) {
			if (slider.getId().equals(id)) {
				slider.setCurrentMax(max_value);
			}
		}
	}

	public int getSliderMinValue (String id) {
		for (SliderSolidBean slider : mSolidSlidersNew) {
			if (slider.getId().equals(id)) {
				return slider.getCurrentMin();
			}
		}
		return 0;
	}

	public int getSliderMaxValue (String id) {
		for (SliderSolidBean slider : mSolidSlidersNew) {
			if (slider.getId().equals(id)) {
				return slider.getCurrentMax();
			}
		}
		return 0;
	}

	public HashSet<Integer> getNewOptions () {
		return mNew;
	}

	public ArrayList<Pair<String, Integer>> getSelectedOptionsIDs () {
		// Listof bean_id + options_id
		ArrayList<Pair<String, Integer>> array = new ArrayList<Pair<String, Integer>>();
		for (OptionsBean bean : getOptions()) {
			Pair<String, Integer> pair = new Pair<String, Integer>(bean.getFilter_id(), bean.getId());
			array.add(pair);
		}
		return array;
	}

	public ArrayList<SliderSolidBean> getSelectedSliders () {
		return (ArrayList<SliderSolidBean>) mSolidSlidersNew;
	}
	
	public boolean isSliderChanged(FilterBean filter){
		for (SliderSolidBean slider : mSolidSlidersSelected) {
			if (slider.getId().equals(filter.getId())) {
				if (slider.getCurrentMin() != filter.getMin() || slider.getCurrentMax() != filter.getMax())
					return true;
				else
					return false;
			}
		}
		return false;
	}
	
	public ArrayList<FilterBean> getSliders() {
		ArrayList<FilterBean> result = new ArrayList<FilterBean>();
		for (FilterBean filter : mFilters) {
			if (isSliderChanged(filter)) {
				result.add(filter);
			}
		}
		return result;
	}
	
	public SliderSolidBean getSliderInfo (FilterBean filter) {
		for (SliderSolidBean slider : mSolidSlidersSelected) {
			if (slider.getId().equals(filter.getId())) {
				return slider;
			}
		}
		
		return null;
	}
	
	public void removeOptionsTag(OptionsBean bean){
		mSelected.remove(bean.getId());
	}
	
	public int countOptionsTag(){
		return mSelected.size();
	}
}
