package ru.enter.adapters;

import java.util.ArrayList;

import ru.enter.R;
import ru.enter.data.ServicesNode;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class ServicesLeftExpListViewAdapter extends BaseExpandableListAdapter {
	
	private LayoutInflater mInflater;
	private ArrayList<ServicesNode> mGroups;
	private long mSelectedId = -1;
	
	public ServicesLeftExpListViewAdapter(Context context, ArrayList<ServicesNode> groups) {
        mGroups = groups;
        mInflater = LayoutInflater.from(context);

        // Добавляем ID-шники для корректной работы сеекторов
        long id = 0;
        for(int i = 0; i < groups.size(); i++){
        	ServicesNode child = groups.get(i);
        	for(int j = 0; j < child.getChildren().size(); j++){
        		child.getChildren().get(j).setId(id);
        		id++;
        	}
        }
    }
	
	public void setSelectedId(long id){
		mSelectedId  = id;
		notifyDataSetInvalidated();
	}

	@Override
    public boolean areAllItemsEnabled()
    {
        return true;
    }

    @Override
    public ServicesNode getChild(int groupPosition, int childPosition) {    	
        return mGroups.get(groupPosition).getChildren().get(childPosition);
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return mGroups.get(groupPosition).getChildren().get(childPosition).getId();
    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild,View convertView, ViewGroup parent) {

    	ServicesNode child = (ServicesNode) getChild(groupPosition, childPosition);
        if (convertView == null) {

            convertView = mInflater.inflate(R.layout.service_left_fr_explist_child, null);
        }

        if (mSelectedId == getChildId(groupPosition, childPosition)){
        	setSelected(convertView);
        }
        else {
        	unsetSelected(convertView);
        }
        	
        TextView childtxt = (TextView) convertView.findViewById(R.id.service_left_fr_child_text_name);

        childtxt.setText(child.getNode().getName());

        return convertView;
    }
    
    private void setSelected(View view){
    	view.setBackgroundResource(R.color.light_gray_service);
    }
    
    private void unsetSelected(View view){
    	view.setBackgroundResource(R.color.white);
    }
	
    @Override
    public int getChildrenCount(int groupPosition) {
        return mGroups.get(groupPosition).getChildren().size();
    }

    @Override
    public ServicesNode getGroup(int groupPosition) {
        return mGroups.get(groupPosition);
    }

    @Override
    public int getGroupCount() {
        return mGroups.size();
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {

    	ServicesNode group = (ServicesNode) getGroup(groupPosition);
    	if (convertView == null) {
            convertView = mInflater.inflate(R.layout.service_left_fr_explist_group, null);
        }
    	ImageView icon = (ImageView) convertView.findViewById(R.id.service_left_fr_group_image_icon);
        TextView grouptxt = (TextView) convertView.findViewById(R.id.service_left_fr_group_text_name);
        ImageView arrow = (ImageView) convertView.findViewById(R.id.service_left_fr_group_image_arrow);
        
        grouptxt.setText(group.getNode().getName());

        // Устанавливаем картинки для категорий по позициям в списке
        // 1 - Электроника
        // 2 - Бытовая техника
        // 3 - Мебель
        // 4 - Спорт
        
        switch (groupPosition){
        	case 0:
        		icon.setBackgroundResource(R.drawable.icn_services_f1_01);
        		break;
        	case 1:
        		icon.setBackgroundResource(R.drawable.icn_services_f1_02);
        		break;
        	case 2:
        		icon.setBackgroundResource(R.drawable.icn_services_f1_03);
        		break;
        	case 3:
        		icon.setBackgroundResource(R.drawable.icn_services_f1_04);
        		break;
        }

        // Добавляем чёрную стрелку в качестве индикатора
        if (isExpanded){
        	arrow.setBackgroundResource(R.drawable.arrow_black_down);
        }
        else{
        	arrow.setBackgroundResource(R.drawable.arrow_black_right);
        }
        
        return convertView;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }

    @Override
    public boolean isChildSelectable(int arg0, int arg1) {
        return true;
    }
		
}