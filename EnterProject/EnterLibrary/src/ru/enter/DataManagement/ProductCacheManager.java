package ru.enter.DataManagement;

import ru.enter.beans.ProductBean;

public class ProductCacheManager {
	private static ProductCacheManager mInstance;
	private ProductBean mProduct;
	
	public static ProductCacheManager getInstance(){
		if(mInstance==null)
			   synchronized (PersonData.class){
				   if(mInstance==null)
					   mInstance = new ProductCacheManager();
			   }		   
		   return mInstance;
	}
	
	private ProductCacheManager(){
		mProduct = new ProductBean();
	}
	
	public void addProductInfo(ProductBean bean){
		mProduct = bean;
	}
	
	public ProductBean getProductInfo(){
		return mProduct;
	}
}
