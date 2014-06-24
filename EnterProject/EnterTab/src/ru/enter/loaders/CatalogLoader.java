package ru.enter.loaders;

import java.util.ArrayList;

import ru.enter.data.CatalogNode;
import ru.enter.data.CatalogTree;
import ru.enter.parsers.CatalogParser;
import ru.enter.utils.PreferencesManager;
import ru.enter.utils.URLManager;
import android.content.Context;
import android.content.DialogInterface;

public class CatalogLoader extends FullScreenLoader<Void> {

	private static final int FOTO_SIZE = 160;
	
	public CatalogLoader(Context context) {
		super(context);
	}

	@Override
	protected void onStartLoading() {
		//если данных нет, то грузим
		if (CatalogTree.isEmpty()) {
			showDialog();
			forceLoad();
		} else {
			deliverResult(null);
		}
		//иначе оставляем как есть
	}

	@Override
	public Void loadInBackground() {
		ArrayList<CatalogNode> roots = new CatalogParser().parse(URLManager.getCatalogTree(PreferencesManager.getCityid(), FOTO_SIZE));
		CatalogTree.setRoots(roots);
		return null;
	}

	@Override
	public void deliverResult(Void data) {
		
		dismissDialog();
		
		if (isStarted()) {
			super.deliverResult(data);
		}

	}
	
	@Override
	protected void onStopLoading() {
		cancelLoad();
	}
	
	@Override
	public void onCanceled(Void data) {
		super.onCanceled(data);
	}
	
	@Override
	protected void onReset() {
		super.onReset();
		
		//убедиться что остановили 
        onStopLoading();
	}
	
	@Override
	public void onCancel(DialogInterface dialog) {
		super.onCancel(dialog);
		getActivity().finish();
	}
	
}
