package ru.enter.route;

import java.util.ArrayList;

import com.google.android.maps.GeoPoint;

public class Road {

	public final static int OK = 0;// ответ содержит допустимый result
	public final static int ZERO_RESULTS = 1;// между исходной точкой и пунктом
												// назначения не найдено ни
												// одного маршрута.
	public final static int NOT_FOUND = 2;// по крайней мере для одной заданной
											// точки (исходной точки, пункта
											// назначения или путевой точки)
											// геокодирование невозможно.
	public final static int MAX_WAYPOINTS_EXCEEDED = 3;// в запросе задано
														// слишком много
														// waypoints
	public final static int INVALID_REQUEST = 4;// означает, что запрос
												// недопустим
	public final static int OVER_QUERY_LIMIT = 5;// служба получила слишком
													// много запросов от вашего
													// приложения в разрешенный
													// период времени
	public final static int REQUEST_DENIED = 6;// означает, что служба
												// Directions отклонила запрос
												// вашего приложения
	public final static int UNKNOWN_ERROR = 7;// означает, что обработка запроса
												// маршрута невозможна из-за
												// ошибки сервера. При повторной
												// попытке запрос может быть
												// успешно выполнен
	public final static int CONNECTION_ERROR = 8;// означает, что обработка
													// запроса маршрута
													// невозможна из-за ошибки
													// сервера. При повторной
													// попытке запрос может быть
													// успешно выполнен
	public String mName;
	public String mDescription;
	public String mDuration;
	public String mLength;
	public int result;
	public int mColor;
	public int mWidth;
	public ArrayList<GeoPoint> mGeoPoint=new ArrayList<GeoPoint>();
}