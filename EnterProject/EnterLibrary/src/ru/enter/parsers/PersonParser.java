package ru.enter.parsers;

import org.json.JSONException;
import org.json.JSONObject;

import ru.enter.DataManagement.PersonData;
import ru.enter.beans.PersonBean;
import ru.enter.utils.Log;
import android.content.Context;

public class PersonParser extends AbstractParser{
	private final String DEBUG = "Svyaznoy:PersonParser";
	private Context mContext;
	
	public PersonParser(Context context) {
		super(context);
		mContext = context;
	}
	
	@SuppressWarnings("unchecked")
	@Override
	Boolean parseData(Object objectResult) {
		try {
				JSONObject item = (JSONObject) objectResult;
				
				PersonBean bean = new PersonBean(); 
				
				bean.setId(item.getInt("id"));
				bean.setName(item.getString("first_name"));
				bean.setLastName(item.getString("last_name"));
				bean.setMiddleName(item.optString("middle_name"));
				bean.setSex(item.getString("sex"));
				bean.setBirthday(item.getString("birthday"));
				bean.setEmail(item.getString("email"));
				bean.setPhone(item.getString("phone"));
				bean.setMobile(item.getString("mobile"));
				bean.setSkype(item.getString("skype"));
				bean.setUi(item.getString("ui"));
				bean.setType_id(item.getString("type_id"));
				bean.setIsActive(item.getInt("is_active"));
				bean.setOccupation(item.getString("occupation"));
				bean.setIdentity(item.getString("identity"));
				bean.setGeo_id(item.getString("geo_id"));
				bean.setAddress(item.getString("address"));
				bean.setZip_code(item.getString("zip_code"));
				bean.setSubscribe(item.getString("is_subscribe"));
				bean.setIp(item.getString("ip"));
				bean.setLastLogin(item.getString("last_login"));
				bean.setAdded(item.getString("added"));
				bean.setUpdated(item.getString("updated"));
				
				bean.setAddressList(new MyBlankParser(mContext).parseData(item.optJSONArray("address_list")));
				PersonData.getInstance().setPersonBean(bean);
				
		} catch (JSONException e) {
			PersonData.getInstance().setPersonBean(new PersonBean());
			Log.e(DEBUG, e.toString());
			return false;
		}
			
	return true;
	}
}

