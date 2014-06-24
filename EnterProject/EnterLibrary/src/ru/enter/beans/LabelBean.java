package ru.enter.beans;

import java.io.Serializable;

import ru.enter.utils.Utils;
import android.content.Context;
import android.util.DisplayMetrics;

public class LabelBean implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 15917705776303720L;
	
	private String foto;
	private String name;
	private int id;
	public String getFoto(Context context) {
		return foto.replace("__label_media_size__", Utils.getLabelSize(context));
	}
	public String getFotoWithSize(String size) {
		return foto.replace("__label_media_size__", size);
	}
	public void setFoto(String foto) {
		this.foto = foto;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	} 
}
