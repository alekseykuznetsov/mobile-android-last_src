package ru.enter.utils;

public enum ImageSizesEnum {
	s60(60),
	s120(120),
	s160(160),
	s163(163),
	s200(200),
	s350(350),
	s500(500),
	s550(550),
	s1500(1500),
	s2500(2500);
	
	private int size;
	
	private ImageSizesEnum(int size){
		this.size = size;
	}
	
	public int getSize(){
		return size;
	}
}
