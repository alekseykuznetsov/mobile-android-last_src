package ru.enter.widgets;

import ru.enter.R;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

/**
 * Класс кнопки для подгрузки результатов поиска
 * @author ideast
 *
 */
public class SearchNextButton {
	
	/**
	 * Главная вьюшка
	 */
	private View v;
	
	/**
	 * Состояние элемента
	 */
	private boolean isEnabled = true;
	/**
	 * Кнопка
	 */
	private TextView text;
	
	/**
	 * Конструктор
	 * @param context
	 */
	public SearchNextButton(Context context){
		LayoutInflater inflater = LayoutInflater.from(context);
		v = inflater.inflate(R.layout.search_next_row,	null);
		text = (TextView)v.findViewById(R.id.search_next_button);
	}
	
	/**
	 * Возвращаем созданную вьюшку
	 * @return
	 */
	public View getView(){
		return v;
	}
	
	/**
	 * Состояние - текст изменен
	 */
	public void setQueryChanged(){
		text.setText("Текст запроса был изменен, повторите поиск заново");
		isEnabled = false;
	}
	
	/**
	 * Состояние - нет результатов
	 */
	public void setNoResults(){
		text.setText("Результатов больше нет");
		isEnabled = false;
	}
	
	/**
	 * Состояние - поиск
	 */
	public void setSearchInAction(){
		text.setText("Поиск...");
		isEnabled = false;
	}
	
	/**
	 * Состояние - Загрузить ещё
	 */
	public void setStartSearch(){
		enable();
		text.setText("Загрузить ещё");
		isEnabled = true;
	}
	
	public void setTag(Object object){
		v.setTag(object);
	}
	
	public Object getTag(){
		return v.getTag();
	}
	public boolean isEnabled(){
		return isEnabled;
	}
	public void disable(){
		v.setVisibility(View.GONE);
	}
	public void enable(){
		v.setVisibility(View.VISIBLE);
	}
}
