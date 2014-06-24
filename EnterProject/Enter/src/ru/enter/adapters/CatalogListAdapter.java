package ru.enter.adapters;

import java.util.ArrayList;

import ru.enter.R;
import ru.enter.ImageManager.ImageDownloader;
import ru.enter.beans.CatalogListBean;
import ru.enter.utils.Utils;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class CatalogListAdapter extends ArrayAdapter<CatalogListBean> {

	private Context context;
	private LayoutInflater mInflater;
	
	private ImageDownloader downloader;
	
	private boolean mIsLoad = true;

	public CatalogListAdapter(Context c, int textViewResourceId, ArrayList<CatalogListBean> objects) {
		super(c, textViewResourceId, objects);
		context = c;
		downloader = new ImageDownloader(context);
		downloader.setMaxThreads(2);
		mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}

	@Override
	public int getCount () {
		return super.getCount();
	}

	@Override
	public CatalogListBean getItem (int position) {
		return super.getItem(position);
	}
	
	public void setLoad(boolean isLoad) {
		downloader.OrderClear();
		mIsLoad = isLoad;
	}

	@Override
	public View getView (int position, View convertView, ViewGroup parent) {
		CatalogListBean bean = getItem(position);
		View v = mInflater.inflate(R.layout.pod_catalog_row, null);
		ImageView iw = (ImageView)v.findViewById(R.id.pod_catalog_row_img);
		TextView title = (TextView) v.findViewById(R.id.pod_catalog_row_title);
		title.setText(bean.getName());
		if (mIsLoad) {
			try {
				int substitution = Utils.getDpiForItemList(context);
				String foto = bean.getFoto().replaceAll("__media_size__",
						String.valueOf(substitution));
				downloader.download(foto, iw);
			} catch (Exception e) {
			}
			/*try {
				// downloader.download(bean.getLabel().get(0).getFoto(mContext),
				// tag);
			} catch (Exception e) {
			}*/
		}

		return v;
	}

}
