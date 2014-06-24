package ru.enter.beans;

public class CallbackBean {
	
	public static final int PHONE = 1;
	public static final int NONE = 0;
	public static final int MESSAGE = 2;	
		
	private String text;
	private int type;
	
	public CallbackBean(){
		text="";
		type = NONE;
	}
	
	public CallbackBean(String text, int type){
		this.text=text;
		this.type = type;
	}

	public String getText() {
		return text;
	}

	public void setData(String text, int type) {
		this.text=text;
		this.type = type;
	}

	public int getType() {
		return type;
	}
}
