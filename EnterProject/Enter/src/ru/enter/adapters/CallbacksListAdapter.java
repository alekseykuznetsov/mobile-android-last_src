package ru.enter.adapters;

import java.util.ArrayList;

import ru.enter.R;
import ru.enter.beans.CallbackBean;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class CallbacksListAdapter extends ArrayAdapter<CallbackBean>{
	private Context context;
	   
	/**
	 * Конструктор     
	 * @param handler контекст
	 * @param textViewResourceId layout
	 * @param messages сообщения в лист
	 */
	
	private LayoutInflater viewInflater;
	public CallbacksListAdapter(Context handler, int textViewResourceId, ArrayList<CallbackBean> messages) 
	{
		super(handler, textViewResourceId);
		add(messages);
		
		this.context = handler;
		viewInflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}
	/**
	 * добавляем в лист
	 * @param messages что добавляем
	 */
	private void addToList(ArrayList<CallbackBean> messages)
	{
		for(CallbackBean message : messages)
			add(message);
	}
	@Override
	public void clear() {
		super.clear();
	}
	
	/**
	 * Добавляет записи из messages в лист источник данных для ListView. Отображаются не все
	 *  добавленные записи, а только удовлетворяющие фильтру
	 * @param messages - список записей для добавления
	 */
	public void add(ArrayList<CallbackBean> messages)
	{
		addToList(messages);
	}
	
	 @Override
	 public View getView(int position, View convertView, ViewGroup parent) 
	 {
		 View view = viewInflater.inflate(R.layout.callback_list_item, null);
		 
		 CallbackBean bean = getItem(position);
		 
		 TextView text = (TextView) view.findViewById(R.id.callback_list_item_text);
		 text.setText(bean.getText());
		 view.setTag(bean);
		 return view;
	 }
}
