package ru.enter.widgets;

import java.util.Arrays;
import java.util.List;

import android.view.View;
import android.widget.Button;

public class SortViewSelector {
	private List<View> list;

	public SortViewSelector(View... view) {
		list = Arrays.asList(view);
	}

	public void selectBtn(int selected) {
		for (int i = 0; i < list.size(); i++)
			list.get(i).setSelected(false);
		list.get(selected).setSelected(true);
	}

	public void selectBtn(View bt) {
		for (View b : list)
			b.setSelected(false);
		list.get(list.indexOf(bt)).setSelected(true);
	}

	public void releaseBtn() {
		for (View b : list)
			b.setSelected(false);
	}

	public boolean isSelected(Button btn) {
		for (View b : list)
			if (b.isSelected())
				return true;
		return false;
	}

}
