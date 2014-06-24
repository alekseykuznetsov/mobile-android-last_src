package ru.enter.adapters;

import java.util.ArrayList;

import ru.enter.R;
import ru.enter.beans.ServiceBean;
import ru.enter.data.ServicesNode;
import ru.enter.utils.TypefaceUtils;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.TextView;

public class ServicesRightExpListViewAdapter extends BaseExpandableListAdapter {

	private LayoutInflater mInflater;
	private ArrayList<ServicesNode> mGroups;

	public ServicesRightExpListViewAdapter(Context context,ArrayList<ServicesNode> groups) {
		mGroups = groups;
		mInflater = LayoutInflater.from(context);
	}

	@Override
	public boolean areAllItemsEnabled() {
		return true;
	}

	@Override
	public ServiceBean getChild(int groupPosition, int childPosition) {
		return mGroups.get(groupPosition).getServices().get(childPosition);
	}

	@Override
	public long getChildId(int groupPosition, int childPosition) {
		return childPosition;
	}

	@Override
	public View getChildView(int groupPosition, int childPosition,
			boolean isLastChild, View convertView, ViewGroup parent) {

		ServiceBean child = (ServiceBean) getChild(groupPosition, childPosition);
		if (convertView == null) {

			convertView = mInflater.inflate(R.layout.service_right_fr_explist_child, null);
		}

		TextView description_view = (TextView) convertView.findViewById(R.id.service_right_fr_child_text_name);

		StringBuilder builder = new StringBuilder();
		builder.append(child.getName());
		builder.append(" ");
		builder.append(String.valueOf((int) child.getPrice()));
		builder.append(String.valueOf(TypefaceUtils.ROUBLE));
		
		CharSequence description = TypefaceUtils.formatRoubleString(builder.toString(), Typeface.NORMAL, TypefaceUtils.NORMAL);
		description_view.setText(description);
		
		return convertView;
	}

	@Override
	public int getChildrenCount(int groupPosition) {
		return mGroups.get(groupPosition).getServices().size();
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
	public View getGroupView(int groupPosition, boolean isExpanded,
			View convertView, ViewGroup parent) {

		ServicesNode group = (ServicesNode) getGroup(groupPosition);
		if (convertView == null) {
			convertView = mInflater.inflate(R.layout.service_right_fr_explist_group, null);
		}
		TextView grouptxt = (TextView) convertView.findViewById(R.id.service_right_fr_group_text_name);

		grouptxt.setText(group.getNode().getName());

		if (isExpanded) grouptxt.setBackgroundResource(R.color.light_gray_service);
		else  			grouptxt.setBackgroundResource(R.color.white);

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