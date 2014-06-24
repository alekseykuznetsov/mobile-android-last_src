package ru.enter.beans;

import java.util.ArrayList;
import java.util.Arrays;

import com.j256.ormlite.field.DatabaseField;

/**
 * Формируем сообщение для галлерей
 * @author ideast
 *
 */
public class PhotoBean
{
	@DatabaseField(generatedId = true)
	private int id;
	@DatabaseField
	private int productForeignKeyID;
	@DatabaseField
	private int size;
	//предполагалось что приходит массив изображений для одного размера. Сделано строкой, для удобства БД
	//массив изображений упаковывается в одну строку с разделителем
	@DatabaseField
	private String images;
	
	public ArrayList<String> getImages() {
		String[] imagesArray = images.split("\\n");
		return new ArrayList<String>(Arrays.asList(imagesArray));
	}
	
	public void setImages(String images) {
		this.images = images;
	}
	
	public int getSize() {
		return size;
	}
	
	public void setSize(int size) {
		this.size = size;
	}
	
	public void setProductForeignKey(int productForeignKey) {
		this.productForeignKeyID = productForeignKey;
	}
	
	public int getProductForeignKey() {
		return productForeignKeyID;
	}

}