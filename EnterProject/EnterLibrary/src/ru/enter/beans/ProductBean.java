package ru.enter.beans;

import java.io.Serializable;
import java.util.ArrayList;

import ru.enter.interfaces.IBasketElement;
import ru.enter.utils.ImageSizesEnum;
import ru.enter.utils.Utils;
import android.text.TextUtils;

public class ProductBean implements Serializable, IBasketElement{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -7485426635172975662L;
	
	private int id;
	private String announce;
	private String name;
	private String shortname;
	private String description;
	private String brand;
	private String foto;
	private String link;
	private String article;
	private double price;
	private int count = 1;
	private float rating;
	private int rating_count;
	private int buyable;
	private int scope_shops_qty; // суммарное quantity по всем магазинам в выбранном контексте (город или отдельный магазин)
	private int scope_shops_qty_showroom; // showroom - суммарное quantity_showroom (колво товаров на витрине)по всем магазинам в выбранном контексте
	private int scope_store_qty; // суммарное quantity по всем !складам! в выбранном контексте
	private int shop; // флаг проброшенный от исходной выдачи, опытным путем выяснил, что он равен 1 если товар есть хотя бы в одном магазине не на витрине
	private String prefix;
	private double price_old;
	private ArrayList<ProductBean> accessories;
	private ArrayList<ProductBean> related;
	private ArrayList<ServiceBean> services;
	private ArrayList<LabelBean> label;
	private ArrayList<String> gallery;
	private ArrayList<String> gallery_3d;
	private ArrayList<DeliveryBean> delivery_mod;
	private ArrayList<OptionArrayBean> options;
	private ArrayList<ShopBean> shop_list;
	private ArrayList<ModelProductBean> modelsProduct;
	
	private ArrayList<PhotoBean> main_fotos;
	
	public void setId(int id) {
		this.id = id;
	}
	
	public int getId() {
		return id;
	}
	
	public void setAnnounce(String announce) {
		this.announce = announce;
	}
	
	public String getAnnounce() {
		return announce;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public String getName() {
		return name;
	}
	
	public void setShortname(String shortname) {
		this.shortname = shortname;
	}
	
	public String getShortname() {
		return shortname;
	}
	
	public String getNameIfExists(){
		return TextUtils.isEmpty(name) ? shortname : name;
	}
	
	public void setDescription(String description) {
		this.description = description;
	}
	
	public String getDescription() {
		return description;
	}
	
	public void setBrand(String brand) {
		this.brand = brand;
	}
	
	public String getBrand() {
		return brand;
	}
	
	public void setFoto(String foto) {
		this.foto = foto;
	}
	
	public String getFoto() {
		return foto;
	}
	
	public String getFotoWithDifferentSize(String old_size, String new_size) {
		return foto.replace("/" + old_size + "/", "/" + new_size + "/");
	}
	
	public void setLink(String link) {
		this.link = link;
	}
	
	public String getLink() {
		return link;
	}
	
	public void setArticle(String article) {
		this.article = article;
	}
	
	public String getArticle() {
		return article;
	}
	
	public void setPrice(double price) {
		this.price = price;
	}
	
	public double getPrice() {
		return price;
	}
	
	public void setCount(int count) {
		this.count = count;
	}
	
	public int getCount() {
		return count;
	}
	
	public void increaseCount(){
		count++;
	}
	
	public void decreaseCount(){
		if(count>0) count--;		
	}
	
	public String getPriceFormattedString(){
		return String.format(" %,.0f", price).replace(",", " ");
	}
	
	public String getOldPriceFormattedString(){
		return String.format(" %,.0f", price_old).replace(",", " ");
	}
	
	public boolean hasGallery(){
		return  ! Utils.isEmptyList(gallery);
	}
	
	public boolean has3D(){
		if(gallery_3d == null || gallery_3d.isEmpty()){
			return false;
		}
		return true;
		//return !gallery_3d.isEmpty();
	}
	
	public int getGalleryLength(){
		return gallery == null ? 0 : gallery.size();
	}
	
	public void setGallery(ArrayList<String> gallery) {
		this.gallery = gallery;
	}
	
	public ArrayList<String> getGallery() {
		return gallery;
	}
	
	public ArrayList<String> getGallery(ImageSizesEnum img_size) {
		int size = gallery.size();
		ArrayList<String> mygallery = new ArrayList<String>(size);
		for(int i = 0; i < size; i++)
			mygallery.add(gallery.get(i).replace("__media_size__", String.valueOf(img_size.getSize())));
		return mygallery;
	}
	
	public void setGallery_3d(ArrayList<String> gallery_3d) {
		this.gallery_3d = gallery_3d;
	}
	
	public ArrayList<String> getGallery3D(){
		return gallery_3d;
	}
	
	public ArrayList<String> getGallery_3d(ImageSizesEnum img_size) {
		int size = gallery_3d.size();
		ArrayList<String> mygallery = new ArrayList<String>(size);
		for(int i = 0; i < size; i++)
			mygallery.add(gallery_3d.get(i).replace("__media_size__", String.valueOf(img_size.getSize())));
		return mygallery;
	}
	
	public void setDelivery_mod(ArrayList<DeliveryBean> delivery_mod) {
		this.delivery_mod = delivery_mod;
	}
	
	public ArrayList<DeliveryBean> getDelivery_mod() {
		return delivery_mod;
	}
	
	public void setOptions(ArrayList<OptionArrayBean> options) {
		this.options = options;
	}
	
	public ArrayList<OptionArrayBean> getOptions() {
		return options;
	}
	
	public void setMain_fotos(ArrayList<PhotoBean> main_fotos) {
		this.main_fotos = main_fotos;
	}
	
	public ArrayList<PhotoBean> getMain_fotos() {
		return main_fotos;
	}
	
	public void setShop_list(ArrayList<ShopBean> shop_list) {
		this.shop_list = shop_list;
	}
	
	public ArrayList<ShopBean> getShop_list() {
		return shop_list;
	}

	public float getRating() {
		return rating;
	}

	public void setRating(float rating) {
		this.rating = rating;
	}

	public int getRating_count() {
		return rating_count;
	}

	public void setRating_count(int rating_count) {
		this.rating_count = rating_count;
	}

	public String getPrefix() {
		return prefix;
	}

	public void setPrefix(String prefix) {
		this.prefix = prefix;
	}

	public double getPrice_old() {
		return price_old;
	}

	public void setPrice_old(double price_old) {
		this.price_old = price_old;
	}

	public ArrayList<ProductBean> getAccessories() {
		return accessories;
	}

	public void setAccessories(ArrayList<ProductBean> accessories) {
		this.accessories = accessories;
	}

	public ArrayList<ProductBean> getRelated() {
		return related;
	}

	public void setRelated(ArrayList<ProductBean> related) {
		this.related = related;
	}

	public ArrayList<ServiceBean> getServices() {
		return services;
	}

	public void setServices(ArrayList<ServiceBean> services) {
		this.services = services;
	}

	public ArrayList<LabelBean> getLabel() {
		return label;
	}

	public void setLabel(ArrayList<LabelBean> label) {
		this.label = label;
	}

	@Override
	public boolean isProduct() {
		return true;
	}

	public int getBuyable() {
		return buyable;
	}

	public void setBuyable(int buyable) {
		this.buyable = buyable;
	}
	
	@Override
	public boolean equals(Object obj){
	    if (this == obj)
	        return true;
	    if (!(obj instanceof ProductBean))
	        return false;
	    ProductBean bean = (ProductBean)obj;
	    return ((id == bean.getId())&&name.equals(bean.getName())&&((int)(price-bean.getPrice())==0));
	}
	
	@Override
	public int hashCode(){
	    int code = 11;
	    int k = 7;
	    code = k*code + id;
	    code = k*code + name.hashCode();
	    code = k*code + (int)(price);
	    return code;
	}

	public int getScopeShopsQty () {
		return scope_shops_qty;
	}

	public void setScopeShopsQty (int scope_shops_qty) {
		this.scope_shops_qty = scope_shops_qty;
	}

	public int getScopeShopsQtyShowroom () {
		return scope_shops_qty_showroom;
	}

	public void setScopeShopsQtyShowroom (int scope_shops_qty_showroom) {
		this.scope_shops_qty_showroom = scope_shops_qty_showroom;
	}

	public int getScopeStoreQty () {
		return scope_store_qty;
	}

	public void setScopeStoreQty (int scope_store_qty) {
		this.scope_store_qty = scope_store_qty;
	}

	public int getShop () {
		return shop;
	}

	public void setShop (int is_shop) {
		this.shop = is_shop;
	}

	public ArrayList<ModelProductBean> getModelsProduct() {
		return modelsProduct;
	}

	public void setModelsProduct(ArrayList<ModelProductBean> modelsProduct) {
		this.modelsProduct = modelsProduct;
	}

}
