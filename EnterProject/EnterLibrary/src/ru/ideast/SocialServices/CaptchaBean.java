package ru.ideast.SocialServices;

public class CaptchaBean {

	private long sig;
	private String url;
	private String key;
	
	public long getSig() {
		return sig;
	}
	public void setSig(long sig) {
		this.sig = sig;
	}
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	public String getKey() {
		return key;
	}
	public void setKey(String key) {
		this.key = key;
	}
}
