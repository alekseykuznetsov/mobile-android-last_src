package ru.enter.DataManagement;

import java.util.ArrayList;

import ru.enter.beans.ProductBean;

public class WishListData {
	private static WishListData mInstance;
	private ArrayList<ProductBean> productList;
	
	public static WishListData getInstance(){
		if(mInstance==null)
			   synchronized (WishListData.class){
				   if(mInstance==null)
					   mInstance = new WishListData();
			   }		   
		   return mInstance;
	   }

	   private WishListData() {		  
		   productList = new ArrayList<ProductBean>();
			
			for (int i=0; i<4; i++) {
				productList.add(new ProductBean());
			}		  
	   }
}
