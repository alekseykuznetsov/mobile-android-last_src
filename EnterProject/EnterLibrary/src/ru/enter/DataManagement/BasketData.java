package ru.enter.DataManagement;

import ru.enter.beans.CheckoutBean;

public class BasketData{
	private static BasketData mInstance;
	private CheckoutBean mCheckoutBean = new CheckoutBean();
	
	public static BasketData getInstance(){
		if(mInstance==null)
			   synchronized (BasketData.class){
				   if(mInstance==null)
					   mInstance = new BasketData();
			   }		   
		   return mInstance;
	   }

	public void setCheckoutBean(CheckoutBean mCheckoutBean) {
		this.mCheckoutBean = mCheckoutBean;
	}

	public CheckoutBean getCheckoutBean() {
		return mCheckoutBean;
	}
	
//	  public void initControl(){
//		   float priceAll = 0;
//		   int count = 0;
//		   for(IBasketElement element:mProductList){
//			   priceAll+=element.getPrice()*element.getCount();
//			   count +=element.getCount();
//		   }
//		   if(mAllPriceTV!=null&&mProductCountTV!=null){
//			   if(priceAll!=0){
//				   mAllPriceTV.setText(String.format("%.0f", priceAll)+" p");
//				   mAllPriceTV.setVisibility(View.VISIBLE);
//			   }else{
//				   mAllPriceTV.setVisibility(View.INVISIBLE);
//			   }
//			   if(count!=0){
//				   mProductCountTV.setText(String.valueOf(count));
//				   mProductCountTV.setVisibility(View.VISIBLE);
//			   }else {
//				   mProductCountTV.setVisibility(View.INVISIBLE);
//			   }
//		   }
//	   }
}

