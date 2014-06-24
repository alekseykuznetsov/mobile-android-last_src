package ru.enter.beans;

import ru.enter.enums.MainSections;

public class SliderMenuItem {
	private int imageSrc;
	private String title;
	private MainSections section;
	public void setImageSrc(int imageSrc) {
		this.imageSrc = imageSrc;
	}
	public int getImageSrc() {
		return imageSrc;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getTitle() {
		return title;
	}
	public void setSection(MainSections section) {
		this.section = section;
	}
	public MainSections getSection() {
		return section;
	}

}
