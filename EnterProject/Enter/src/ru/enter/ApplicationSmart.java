package ru.enter;

import ru.enter.utils.ApplicationEnter;
import ru.enter.utils.StatisticsInfoSender;

public class ApplicationSmart extends ApplicationEnter {

	private static final String ENTER_SMARTPHONE_CLIENT_ID = "AndroidSmart";
//	private static final String ENTER_SMARTPHONE_CLIENT_ID = "Megafon";

	@Override
	public void onCreate () {
		super.onCreate();
		setAppClientId(ENTER_SMARTPHONE_CLIENT_ID);
		StatisticsInfoSender.send();
	}
}
