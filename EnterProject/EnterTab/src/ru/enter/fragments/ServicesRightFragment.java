package ru.enter.fragments;

import com.google.analytics.tracking.android.EasyTracker;

import ru.enter.R;
import ru.enter.ServicesActivity;
import ru.enter.adapters.ServicesRightExpListViewAdapter;
import ru.enter.beans.ServiceBean;
import ru.enter.data.ServicesNode;
import ru.enter.dialogs.ServiceCardDialogFragment;
import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;
import android.widget.ExpandableListView.OnChildClickListener;
import android.widget.ExpandableListView.OnGroupClickListener;

public class ServicesRightFragment extends Fragment implements OnChildClickListener, OnGroupClickListener{

	ServicesRightExpListViewAdapter serviceAdapter;

	public static ServicesRightFragment getInstance() {
		return new ServicesRightFragment();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		View view = inflater.inflate(R.layout.services_right_fr, null);

		ServicesActivity mActiviy = (ServicesActivity) getActivity();
		ServicesNode currentParent = mActiviy.getCurrentParent();
		final ExpandableListView exp_list_view = (ExpandableListView) view.findViewById(R.id.services_right_fr_expListView);
		exp_list_view.setEmptyView(view.findViewById(R.id.services_right_fr_empty));
		
		if (currentParent != null && currentParent.getChildren() != null) {
			serviceAdapter = new ServicesRightExpListViewAdapter(this.getActivity(), currentParent.getChildren());
			exp_list_view.setAdapter(serviceAdapter);
			exp_list_view.setOnChildClickListener(this);
		}
		return view;
	}

	@Override
	public boolean onChildClick(ExpandableListView parent, View v,
			int groupPosition, int childPosition, long id) {
		ServicesActivity mActiviy = (ServicesActivity) getActivity();
		ServicesNode currentParent = mActiviy.getCurrentParent();
	
		ServiceBean bean = currentParent.getChildren().get(groupPosition).getServices().get(childPosition);
		EasyTracker.getTracker().sendEvent("service/get", "buttonPress", bean.getName(), (long) bean.getId());
		ServiceCardDialogFragment serviceDialog = ServiceCardDialogFragment.getInstance(bean);
		serviceDialog.show(getFragmentManager(), "serviceDialog");
		
		return true;
	}

	@Override
	public boolean onGroupClick(ExpandableListView parent, View v,
			int groupPosition, long id) {
		// TODO Auto-generated method stub
		return false;
	}
}
