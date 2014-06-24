package ru.enter.base;

import ru.enter.AboutActivity;
import ru.enter.AuthorizationActivity;
import ru.enter.CatalogActivity;
import ru.enter.PersonalActivity;
import ru.enter.R;
import ru.enter.ScanerActivity;
import ru.enter.SearchActivity;
import ru.enter.ServicesActivity;
import ru.enter.ShopsActivity;
import ru.enter.utils.PreferencesManager;
import ru.enter.widgets.BasketView;
import android.content.Intent;
import android.view.Menu;
import android.view.MenuItem;

public class BaseMenuActivity extends BaseActivity {
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.actionbar_default_menu, menu);//TODO нормальные имена
		
		//логика отрисовки корзины вынесена в отдельный класс 
		//т.к. экземпляр ActionBar у нас будет каждый раз разный
		BasketView basketView = new BasketView(this);
		menu.findItem(R.id.item_basket).setActionView(basketView);
		
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onMenuItemSelected(int featureId, MenuItem item) {
		handleMenuItemClick(item);
		return super.onMenuItemSelected(featureId, item);
	}

	private void handleMenuItemClick(MenuItem item) {
		Intent intent = new Intent();

		switch (item.getItemId()) {
		case R.id.menu_catalog:
			intent.setClass(this, CatalogActivity.class);
			break;
		case R.id.menu_search:
			intent.setClass(this, SearchActivity.class);
			break;
		case R.id.menu_shops:
			intent.setClass(this, ShopsActivity.class);
			break;
//		case R.id.menu_f1:
//			intent.setClass(this, ServicesActivity.class);
//			break;
		case R.id.menu_personal:
			intent.setClass(this, PersonalActivity.class);
			// авторизован ли пользователь
			if ( ! PreferencesManager.isAuthorized()) {
				AuthorizationActivity.launch(this);
				return;
			}
			break;
		case R.id.menu_qr:
			intent.setClass(this, ScanerActivity.class);
			break;
		case R.id.menu_about:
			intent.setClass(this, AboutActivity.class);
			intent.putExtra(AboutActivity.SELECTED_TAB, AboutActivity.ABOUT_PROJECT_TAB);
			break;
		case R.id.menu_feedback:
			intent.setClass(this, AboutActivity.class);
			intent.putExtra(AboutActivity.SELECTED_TAB, AboutActivity.FEEDBACK_TAB);
			break;
		default:
			return;
		}
		
		startActivity(intent);
	}

}
