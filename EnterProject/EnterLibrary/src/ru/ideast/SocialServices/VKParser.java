package ru.ideast.SocialServices;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;


public class VKParser{
	private String data;

	public VKParser(String data) {
		this.data = data;
	}

	public Object parse() {
		FBBean fbBean = null;
		try {
			try {
				JSONObject object = (JSONObject) new JSONTokener(data).nextValue();
				fbBean = new FBBean();
				JSONArray response = object.getJSONArray("response");
				for(int i = 0; i<response.length();i++){
					JSONObject obj = response.getJSONObject(i);
					fbBean.setId(obj.getString("uid"));
					fbBean.setName(obj.getString("first_name").concat(" ").concat(obj.getString("last_name")));
				}
				
			} catch (JSONException e) {
				e.toString();
			}
		} catch (Exception e) {
			e.toString();
		}
		return fbBean;
	}
}
