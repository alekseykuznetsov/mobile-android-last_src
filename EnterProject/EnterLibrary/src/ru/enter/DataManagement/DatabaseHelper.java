package ru.enter.DataManagement;

import java.sql.SQLException;

import ru.enter.beans.OptionArrayBean;
import ru.enter.beans.OptionBean;
import ru.enter.beans.PhotoBean;
import ru.enter.beans.ProductBean;
import ru.enter.utils.Log;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.RuntimeExceptionDao;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;

public class DatabaseHelper extends OrmLiteSqliteOpenHelper{
	
	private static final String DATABASE_NAME = "enter.db";
	private static final int DATABASE_VERSION = 1;
	
	private Dao<ProductBean, Integer> productDao = null;
	private RuntimeExceptionDao<ProductBean, Integer> productRuntimeExceptionDao = null;
	
	private Dao<PhotoBean, Integer> photoDao = null;
	private RuntimeExceptionDao<PhotoBean, Integer> photoRuntimeExceptionDao = null;
	
	private Dao<OptionArrayBean, Integer> optionsArrayDao = null;
	private RuntimeExceptionDao<OptionArrayBean, Integer> optionsArrayRuntimeExceptionDao = null;
	
	private Dao<OptionBean, Integer> optionDao = null;
	private RuntimeExceptionDao<OptionBean, Integer> optionRuntimeExceptionDao = null;
	
	public DatabaseHelper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db, ConnectionSource connection) {
		try {
			Log.i(DatabaseHelper.class.getName(), "onCreate");
			TableUtils.createTable(connection, ProductBean.class);
			TableUtils.createTable(connection, PhotoBean.class);
			TableUtils.createTable(connection, OptionArrayBean.class);
			TableUtils.createTable(connection, OptionBean.class);
	
		} catch (SQLException e) {
			Log.e(DatabaseHelper.class.getName(), "Can't create table", e);
			throw new RuntimeException(e);
		}
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, ConnectionSource connection, int oldVersion,
			int newVersion) {
		try {
			Log.i(DatabaseHelper.class.getName(), "onUpgrade");
			TableUtils.dropTable(connection, ProductBean.class, true);
			TableUtils.dropTable(connection, PhotoBean.class, true);
			TableUtils.dropTable(connection, OptionArrayBean.class, true);
			TableUtils.dropTable(connection, OptionBean.class, true);
			onCreate(db, connection);
		} catch (SQLException e) {
			Log.e(DatabaseHelper.class.getName(), "Can't drop table", e);
			throw new RuntimeException(e);
		}
	}
	
   public Dao<?, Integer> getDao(DaosTables name){
	   try{
		   switch (name) {
				case product:
					if (productDao == null) productDao = getDao(ProductBean.class);
					return productDao;
				case photos:
					if (photoDao == null) photoDao = getDao(PhotoBean.class);
					return photoDao;
				case options_category:
					if (optionsArrayDao == null) optionsArrayDao = getDao(OptionArrayBean.class);
					return optionsArrayDao;
				case option:
					if (optionDao == null) optionDao = getDao(OptionBean.class);
					return optionDao;
				default:
					break;
			}
	   }catch (SQLException sqle) {
		   Log.e("DaoError", sqle.toString());
	   }   
	   return null;
   }
   
   public RuntimeExceptionDao<?, Integer> getRuntimeDao(DaosTables name){
	   switch (name) {
			case product:
				if (productRuntimeExceptionDao == null) productRuntimeExceptionDao = getRuntimeExceptionDao(ProductBean.class);
				return productRuntimeExceptionDao;
			case photos:
				if (photoRuntimeExceptionDao == null) photoRuntimeExceptionDao = getRuntimeExceptionDao(PhotoBean.class);
				return photoRuntimeExceptionDao;
			case options_category:
				if (optionsArrayRuntimeExceptionDao == null) optionsArrayRuntimeExceptionDao = getRuntimeExceptionDao(OptionArrayBean.class);
				return optionsArrayRuntimeExceptionDao;
			case option:
				if (optionRuntimeExceptionDao == null) optionRuntimeExceptionDao = getRuntimeExceptionDao(OptionBean.class);
				return optionRuntimeExceptionDao;
			default:
				return null;
		}
   }
	
}
