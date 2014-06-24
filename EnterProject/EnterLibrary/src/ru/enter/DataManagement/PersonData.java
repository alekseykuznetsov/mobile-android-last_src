package ru.enter.DataManagement;

import ru.enter.beans.PersonBean;
/**Класс для хранения данных о пользователе*/
public class PersonData {
	private static PersonData mInstance;
	public static PersonData getInstance(){
		if(mInstance==null)
			   synchronized (PersonData.class){
				   if(mInstance==null)
					   mInstance = new PersonData();
			   }		   
		   return mInstance;
	}
	private PersonBean mPersonBean;//бин с данными пользователя
	private boolean mOrdersChanged;
	private String mToken;
	
	private PersonData(){
		mPersonBean = new PersonBean();
	}
	
	public PersonBean getPersonBean() {
		return mPersonBean;
	}
	
	public void setPersonBean(PersonBean bean){//для того чтобы при парсинге не получилось, что одни поля забиты, а другие нет, потому что возникла ошибка
		mPersonBean = bean;
	}

	public long getAuthorized(){
		return mPersonBean!=null ? mPersonBean.getId() : 0;
	}
	
	public void setOrdersChanged(boolean ordersChanged) {
		mOrdersChanged = ordersChanged;
	}
	
	public boolean isOrdersChanged() {
		return mOrdersChanged;
	}
	
//	public void setToken(String token){
//		mToken = token;
//	}
//	
//	public String getToken(){
//		return mToken;
//	}
}
