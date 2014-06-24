package ru.enter.widgets;

import java.util.Arrays;
import java.util.List;

import android.widget.Button;

public class ButtonSelector {

	private List<Button> list;

	public ButtonSelector(Button... buttons) {
		list = Arrays.asList(buttons);
	}

	public void selectBtn(int selected) {
		for (int i = 0; i < list.size(); i++)
			list.get(i).setSelected(false);
		list.get(selected).setSelected(true);
	}

	public void selectBtn(Button bt) {
		for (Button b : list)
			b.setSelected(false);
		list.get(list.indexOf(bt)).setSelected(true);
	}

	public void releaseBtn() {
		for (Button b : list)
			b.setSelected(false);
	}

	public boolean isSelected(Button btn) {
		for (Button b : list)
			if (b.isSelected())
				return true;
		return false;
	}
}
