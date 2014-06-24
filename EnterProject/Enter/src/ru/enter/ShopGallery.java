package ru.enter;

import java.io.IOException;
import java.util.ArrayList;

import org.json.JSONException;

import com.flurry.android.FlurryAgent;

import ru.enter.adapters.ShopGalleryAdapter;
import ru.enter.beans.ShopImgBean;
import ru.enter.parsers.ShopImgParser;
import ru.enter.utils.URLManager;
import ru.enter.utils.Utils;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.ProgressBar;

public class ShopGallery extends Activity{
	private int shopId;
	private ProgressBar progress;
	private ViewPager pager;
	public static final String SHOP_ID = "SHOP_ID";
	private ShopGalleryAdapter adapter;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.shop_gallery);
		pager=(ViewPager) findViewById(R.id.shop_gallery_img);
		progress=(ProgressBar) findViewById(R.id.shop_gallery_progress);
		adapter=new ShopGalleryAdapter(this);
		pager.setAdapter(adapter);
		shopId=getIntent().getIntExtra(SHOP_ID, 0);
		new Download().execute(URLManager.getShopImageList(shopId));
	}
	
	@Override
	protected void onStart()
	{
		super.onStart();
		FlurryAgent.onStartSession(this, "3X5JRBVFV7QWXNP3Q8H2");
	}
	 
	@Override
	protected void onStop()
	{
		super.onStop();		
		FlurryAgent.onEndSession(this);
	}
	
private class Download extends AsyncTask<String, Void, ArrayList<ShopImgBean>> {
	    
	    @Override
		 protected void onPreExecute() {
			 progress.setVisibility(View.VISIBLE);
		 }
	    
		protected ArrayList<ShopImgBean> doInBackground(String... urls) {
			ArrayList<ShopImgBean> result = null;

			try {
				result = (ArrayList<ShopImgBean>) new ShopImgParser(URLManager.getShopImageList(shopId)).parse();
			} catch (IOException e) {
				// NOP
			} catch (JSONException e) {
				// NOP
			}
			 
			 return result;
	     }

	     protected void onPostExecute(ArrayList<ShopImgBean> result) {
	    	 
	    	 if (Utils.isEmptyList(result)) {
	    		 showNoShopsDialog("Данные отсутствуют");
	    	 } else {
	    		 adapter.setList(result);
	    	 }
	    	 progress.setVisibility(View.GONE);
	     }
	 }

private void showNoShopsDialog(String mes) {
	//Context context = getParent();
	AlertDialog.Builder dlg = new AlertDialog.Builder(this);
	dlg.setMessage(mes)
			.setPositiveButton("Ок", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int whichButton) {
					finish();
				}
			}).create().show();
}
	
}
