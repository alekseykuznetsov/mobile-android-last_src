package ru.enter.utils;


public class ResponceServerException extends Exception {
	public ResponceServerException() {
		super();
	}
	@Override
	public String toString() {
//		return mErrorCode+" "+mErrorMsg;
		return mErrorMsg;
	}
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private int mErrorCode;
	private String mErrorMsg;
	public int getErrorCode() {
		return mErrorCode;
	}
	public void setErrorCode(int mErrorCode) {
		this.mErrorCode = mErrorCode;
	}
	public String getErrorMsg() {
		return mErrorMsg;
	}
	public void setErrorMsg(String mErrorMsg) {
		this.mErrorMsg = mErrorMsg;
	}
	
}
