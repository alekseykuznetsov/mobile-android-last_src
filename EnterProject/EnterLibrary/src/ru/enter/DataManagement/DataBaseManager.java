package ru.enter.DataManagement;

import java.sql.SQLException;
import java.util.ArrayList;

import ru.enter.beans.OptionArrayBean;
import ru.enter.beans.OptionBean;
import ru.enter.beans.PhotoBean;
import ru.enter.beans.ProductBean;
import ru.enter.utils.Log;
import ru.enter.utils.Utils;
import android.content.Context;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.RuntimeExceptionDao;

public class DataBaseManager {
	
	private static DataBaseManager mInstance;
	private static DatabaseHelper mHelperDB;
	
	public static DataBaseManager getInstance(Context context){
		   if(mInstance==null)
			   synchronized (DataBaseManager.class){
				   if(mInstance==null){
					   mInstance = new DataBaseManager();
					   mHelperDB = new DatabaseHelper(context);
				   }
			   }		   
		   return mInstance;
	   }
	
   private DataBaseManager() {}
   
   @SuppressWarnings("unchecked")
   public void insertProduct(ProductBean bean) {
	    Dao<ProductBean, Integer> productDao = (Dao<ProductBean, Integer>) mHelperDB.getDao(DaosTables.product);
	    try {
			productDao.createOrUpdate(bean);
		}catch (SQLException e) {
			Log.e("INSERT_DB_ERROR", e.toString());
		}
		insertPhotos(bean);
		insertOptions(bean);
	}
  
   @SuppressWarnings("unchecked")
   public void insertPhotos (ProductBean product) {
	   ArrayList<PhotoBean> photos = product.getMain_fotos();
	   if(!Utils.isEmptyList(photos)){
		   Dao<PhotoBean, Integer> photoDao = (Dao<PhotoBean, Integer>) mHelperDB.getDao(DaosTables.photos);
		   for(PhotoBean photo : photos){
			   photo.setProductForeignKey(product.getId());
			   try {
				   photoDao.createOrUpdate(photo);
			   } catch (SQLException sqle) {
				   Log.e("Insert Photos", sqle.toString());
			   }
		   }
	   }
  	}
   
   @SuppressWarnings("unchecked")
   public void insertOptions (ProductBean product) {
	   ArrayList<OptionArrayBean> categories = product.getOptions();
	   if(!Utils.isEmptyList(categories)){
		   Dao<OptionArrayBean, Integer> optionArrayDao =  (Dao<OptionArrayBean, Integer>) mHelperDB.getDao(DaosTables.options_category);
		   for(OptionArrayBean category : categories){
			   category.setProductForeignKeyID(product.getId());
			   try {
				   //записываем категорию опций
				   optionArrayDao.createOrUpdate(category);
				   //записываем следом опции в данную категорию
				   insertOption(category);
			   } catch (SQLException sqle) {
				   Log.e("Insert Option Category", sqle.toString());
			   }
		   }
	   }
  	}
   
   @SuppressWarnings("unchecked")
   public void insertOption (OptionArrayBean optionCategory) throws SQLException {
	   ArrayList<OptionBean> options = optionCategory.getOption();
	   if(!Utils.isEmptyList(options)){
		   Dao<OptionBean, Integer> optionDao =  (Dao<OptionBean, Integer>) mHelperDB.getDao(DaosTables.option);
		   for(OptionBean option : options){
			   option.setOptionArrayForeignKeyID(optionCategory.getId());
				   optionDao.createOrUpdate(option);
		   }
	   }
   }
	
   @SuppressWarnings("unchecked")
   public ProductBean getProduct(int productID) {
		RuntimeExceptionDao<ProductBean, Integer> productExceptionDao = (RuntimeExceptionDao<ProductBean, Integer>) mHelperDB.getRuntimeDao(DaosTables.product);
		ProductBean product = (ProductBean) productExceptionDao.queryForId(productID);
		if (product!=null) {
			product.setMain_fotos(getPhotos(productID));
			product.setOptions(getOptionsCategories(productID));
		}
		return product;
   }
   
   @SuppressWarnings("unchecked")
   public ArrayList<PhotoBean> getPhotos(int productForeignKeyID) {
	   	ArrayList<PhotoBean> photos = new ArrayList<PhotoBean>();
	   	RuntimeExceptionDao<PhotoBean, Integer> photoExceptionDao = (RuntimeExceptionDao<PhotoBean, Integer>) mHelperDB.getRuntimeDao(DaosTables.photos);
		try {
			photos = (ArrayList<PhotoBean>) photoExceptionDao.query(photoExceptionDao.queryBuilder().orderBy("size", true)
					.where().eq("productForeignKeyID", productForeignKeyID).prepare());
		} catch (SQLException e) {
			Log.d("Can't attach photos", e.toString());
		}
		return photos;
   }
   
   @SuppressWarnings("unchecked")
   public ArrayList<OptionArrayBean> getOptionsCategories(int productForeignKeyID) {
	   	ArrayList<OptionArrayBean> optionsCategories = new ArrayList<OptionArrayBean>();
	   	RuntimeExceptionDao<OptionArrayBean, Integer> categoriesExceptionDao = (RuntimeExceptionDao<OptionArrayBean, Integer>) mHelperDB.getRuntimeDao(DaosTables.options_category);
		try {
			optionsCategories =  (ArrayList<OptionArrayBean>) categoriesExceptionDao.query(categoriesExceptionDao.queryBuilder()
					.where().eq("productForeignKeyID", productForeignKeyID).prepare());
		} catch (SQLException e) {
			Log.d("Can't attach options", e.toString());
		}
		for (OptionArrayBean category : optionsCategories) category.setOption(getOptions(category.getId()));
		return optionsCategories;
   }
   
   @SuppressWarnings("unchecked")
   public ArrayList<OptionBean> getOptions(int optionsArrayForeignKey) {
	   	ArrayList<OptionBean> options = new ArrayList<OptionBean>();
	   	RuntimeExceptionDao<OptionBean, Integer> optionsExceptionDao = (RuntimeExceptionDao<OptionBean, Integer>) mHelperDB.getRuntimeDao(DaosTables.option);
		try {
			options =  (ArrayList<OptionBean>) optionsExceptionDao.query(optionsExceptionDao.queryBuilder()
					.where().eq("optionArrayForeignKeyID", optionsArrayForeignKey).prepare());
		} catch (SQLException e) {
			Log.d("Can't attach options", e.toString());
		}
		return options;
   }
   
}
