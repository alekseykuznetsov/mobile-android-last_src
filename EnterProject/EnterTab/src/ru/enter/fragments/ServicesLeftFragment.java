package ru.enter.fragments;

import java.util.ArrayList;

import ru.enter.R;
import ru.enter.ServicesActivity;
import ru.enter.adapters.ServicesLeftExpListViewAdapter;
import ru.enter.data.ServicesNode;
import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;
import android.widget.ExpandableListView.OnChildClickListener;

public class ServicesLeftFragment extends Fragment implements
		OnChildClickListener {

	ServicesLeftExpListViewAdapter mServiceAdapter;

	public static final String SERVICE_ID = "serviceid";

	public static ServicesLeftFragment getInstance() {
		return new ServicesLeftFragment();
	}

	private void checkExpand(ExpandableListView exp_list_view) {
		Bundle args = getArguments();
		if (args != null && args.containsKey(ServicesActivity.SERVICE_ID)) {
			int serviceId = args.getInt(ServicesActivity.SERVICE_ID, 0);
			exp_list_view.expandGroup(serviceId);
		}
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		View view = inflater.inflate(R.layout.services_left_fr, null);

		ExpandableListView exp_list_view = (ExpandableListView) view.findViewById(R.id.services_left_fr_expListView);
		
		ServicesActivity mActiviy = (ServicesActivity) getActivity();
		ArrayList<ServicesNode> roots = mActiviy.getServicesTreeRoots();
		if (roots != null){
			mServiceAdapter = new ServicesLeftExpListViewAdapter(mActiviy, roots);
			exp_list_view.setAdapter(mServiceAdapter);
			exp_list_view.setOnChildClickListener(this);
			checkExpand(exp_list_view);
		}
		return view;
	}

	@Override
	public boolean onChildClick(ExpandableListView parent, View view,
			int groupPosition, int childPosition, long id) {
		ServicesActivity mActiviy = (ServicesActivity) getActivity();
		ArrayList<ServicesNode> roots = mActiviy.getServicesTreeRoots();

		ServicesNode currentParent = roots.get(groupPosition).getChildren().get(childPosition);
		mServiceAdapter.setSelectedId(id);
		
		mActiviy.setCurrentParent(currentParent);
		mActiviy.startRight();

		return true;
	}

}
