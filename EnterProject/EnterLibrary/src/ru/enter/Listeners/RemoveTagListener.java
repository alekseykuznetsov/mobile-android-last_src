package ru.enter.Listeners;

import ru.enter.beans.FilterBean;
import ru.enter.beans.OptionsBean;

public interface RemoveTagListener {
	public void removeFilter(OptionsBean bean);
	public void removeSeekFilter (FilterBean bean);

}
