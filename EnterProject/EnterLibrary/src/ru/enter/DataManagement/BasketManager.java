package ru.enter.DataManagement;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.flurry.android.FlurryAgent;

import ru.enter.beans.ProductBean;
import ru.enter.beans.ServiceBean;
import ru.enter.interfaces.IBasketElement;
import ru.enter.interfaces.OnBasketChangeListener;
import ru.enter.utils.Constants;
import ru.enter.utils.Utils;

public enum BasketManager {

	INSTANCE;

	// ссылки для обновления графического представления
	private List<WeakReference<OnBasketChangeListener>> mBasketListeners = new ArrayList<WeakReference<OnBasketChangeListener>>();

	private List<ProductBean> mProducts = new ArrayList<ProductBean>();
	private List<ServiceBean> mServices = new ArrayList<ServiceBean>();
	private List<ServiceBean> mRelatedServices = new ArrayList<ServiceBean>();

	private ProductBean mOneClickProduct;

	/**
	 * Очистить корзину
	 */
	public static void clear () {
		INSTANCE.mProducts = new ArrayList<ProductBean>();
		INSTANCE.mServices = new ArrayList<ServiceBean>();
		INSTANCE.mRelatedServices = new ArrayList<ServiceBean>();
		notifyListeners();
	}

	/**
	 * Взять все товары из корзины
	 * 
	 * @return
	 */
	public static List<ProductBean> getProducts () {
		return INSTANCE.mProducts;
	}

	/**
	 * Взять все услуги из корзины. Не связанные!
	 * 
	 * @return
	 */
	public static List<ServiceBean> getServices () {
		return INSTANCE.mServices;
	}

	/**
	 * Взять все связаные услуги к продукту
	 * 
	 * @param product
	 * @return
	 */
	public static List<ServiceBean> getRelatedServices (ProductBean product) {
		List<ServiceBean> resultCollection = new ArrayList<ServiceBean>();

		if (product != null) {
			int productId = product.getId();
			for (ServiceBean service : INSTANCE.mRelatedServices) {
				if (service.getProductId() == productId) {
					resultCollection.add(service);
				}
			}
		}
		return resultCollection;
	}

	/**
	 * Взять все услуги из корзины, включая связанные
	 * 
	 * @return
	 */
	public static List<ServiceBean> getServicesAll () {
		List<ServiceBean> result = new ArrayList<ServiceBean>(INSTANCE.mServices);
		result.addAll(INSTANCE.mRelatedServices);
		return result;
	}

	/**
	 * Взять все товары и услуги из корзины. Без связанных
	 * 
	 * @return
	 */
	public static List<IBasketElement> getAll () {
		List<IBasketElement> all = new ArrayList<IBasketElement>(INSTANCE.mProducts);
		all.addAll(INSTANCE.mServices);
		return all;
	}

	/**
	 * Добавить продукт в корзину. Если продукт есть в корзине, увеличивается
	 * его количество. Максимум до 20.
	 * 
	 * @param product
	 */
	public static void addProduct (ProductBean product) {
		if (product != null) {
			int position = INSTANCE.mProducts.indexOf(product);
			// если товара нет в корзине добавляем
			if (position == -1) {
				INSTANCE.mProducts.add(product);
				FlurryAgent.logEvent(Constants.FLURRY_EVENT.Goods_Sent_To_Basket.toString());
			} else {
				// иначе увеличиваем его количество на 1
				ProductBean productInBasket = INSTANCE.mProducts.get(position);
				int productCount = productInBasket.getCount();
				// можем добавить до 20 раз
				if (productCount < 20){
					productInBasket.setCount(productCount + 1);
					FlurryAgent.logEvent(Constants.FLURRY_EVENT.Goods_Sent_To_Basket.toString());
				}
			}

			notifyListeners();
		}
	}

	/**
	 * Добавить услугу в корзину. Если услуга есть в корзине, увеличивается ее
	 * количество. Максимум до 20. Для связанных услуг есть отдельный метод
	 * {@link #addRelatedService(ProductBean, ServiceBean)}
	 * 
	 * @param service
	 */
	public static void addService (ServiceBean service) {
		if (service != null) {
			int position = INSTANCE.mServices.indexOf(service);
			if (position == -1) {
				INSTANCE.mServices.add(service);
			} else {
				ServiceBean serviceInBasket = INSTANCE.mServices.get(position);
				int serviceCount = serviceInBasket.getCount();
				if (serviceCount < 20)
					serviceInBasket.setCount(serviceCount + 1);
			}

			notifyListeners();
		}
	}

	/**
	 * Добавляет связанную услугу в корзину.
	 * 
	 * @param product
	 * @param service
	 * @return true если товар был добавлен в корзину вместе с услугой, false
	 *         если добалена только услуга
	 */
	public static boolean addRelatedService (ProductBean product, ServiceBean service) {
		if (service != null && product != null) {
			service.setProductId(product.getId());

			// аналогично добавлению услуги
			int position = INSTANCE.mRelatedServices.indexOf(service);
			if (position == -1) {
				INSTANCE.mRelatedServices.add(service);
			} else {
				ServiceBean serviceInBasket = INSTANCE.mRelatedServices.get(position);
				int serviceCount = serviceInBasket.getCount();
				if (serviceCount < 20)
					serviceInBasket.setCount(serviceCount + 1);
			}

			if (!INSTANCE.mProducts.contains(product)) {
				INSTANCE.mProducts.add(product);
				return true;
			}

			notifyListeners();
		}

		return false;
	}

	/**
	 * Удалить продукт из корзины. Вместе со связанными с ним услугами
	 * 
	 * @param product
	 */
	public static void deleteProduct (ProductBean product) {
		if (product != null && INSTANCE.mProducts.contains(product)) {
			// удаляем связанные с этим товаром услуги
			int productId = product.getId();
			Iterator<ServiceBean> iterator = INSTANCE.mRelatedServices.iterator();
			while (iterator.hasNext()) {
				if (iterator.next().getProductId() == productId)
					iterator.remove();
			}

			product.setCount(1); // TODO hardcode against deepcopy

			// удаляем сам товар
			INSTANCE.mProducts.remove(product);

			notifyListeners();

		}
	}

	/**
	 * Удалить услугу из корзины. Для удаления связанной услуги используется
	 * {@link #deleteRelatedService(ServiceBean)}
	 * 
	 * @param service
	 */
	public static void deleteService (ServiceBean service) {
		if (service != null && INSTANCE.mServices.contains(service)) {

			service.setCount(1); // TODO harcode against deepcopy

			INSTANCE.mServices.remove(service);
			notifyListeners();
		}
	}

	/**
	 * Удалить связанную услугу из корзины
	 * 
	 * @param service
	 */
	public static void deleteRelatedService (ServiceBean service) {
		if (service != null && INSTANCE.mRelatedServices.contains(service)) {

			service.setCount(1); // TODO harcode against deepcopy

			INSTANCE.mRelatedServices.remove(service);
			notifyListeners();
		}
	}

	/**
	 * Удалить элемент из корзины
	 * 
	 * @param element
	 */
	public static void delete (IBasketElement element) {
		if (element == null)
			return;

		if (element.isProduct()) {
			deleteProduct((ProductBean) element);
		} else {
			ServiceBean service = (ServiceBean) element;
			if (service.getProductId() == 0) {
				deleteService(service);
			} else {
				deleteRelatedService(service);
			}
		}

		notifyListeners();
	}

	/**
	 * Добавить товар для покупки в один клик
	 * 
	 * @param product
	 */
	public static void addOneClickProduct (ProductBean product) {
		INSTANCE.mOneClickProduct = product;
	}

	/**
	 * Взять товар для покупки в один клик из корзины
	 * 
	 * @return
	 */
	public static ProductBean getOneClickProduct () {
		return INSTANCE.mOneClickProduct;
	}

	/**
	 * Проверяет пустая ли корзина
	 * 
	 * @return
	 */
	public static boolean isEmpty () {
		return Utils.isEmptyList(INSTANCE.mProducts) && Utils.isEmptyList(INSTANCE.mServices)
				&& Utils.isEmptyList(INSTANCE.mRelatedServices);
	}

	/**
	 * Устанавливаем слушателя на изменение корзины
	 * 
	 * @param listener
	 */
	public static void setOnBasketChangeListener (OnBasketChangeListener listener) {
		WeakReference<OnBasketChangeListener> ref = new WeakReference<OnBasketChangeListener>(listener);
		INSTANCE.mBasketListeners.add(ref);
	}

	/**
	 * Уведомляет что надо обновить графику для корзины
	 */
	public static void notifyListeners () {
		for (WeakReference<OnBasketChangeListener> ref : INSTANCE.mBasketListeners) {
			OnBasketChangeListener listener = ref.get();
			if (listener != null) {
				listener.onBasketChange();
			}
		}
	}

	public static class CountPrice {
		public int servicesCount;
		public int productsCount;
		public int servicesPrice;
		public int productsPrice;
		public int allCount;
		public int allPrice;
	}


	/**
	 * Получает объект для вывода цены и количества элементов корзины
	 * @return oбъект с public полями
	 */
	public static CountPrice getCountPriceObject () {

		CountPrice object = new CountPrice();

		for (ProductBean product : INSTANCE.mProducts) {
			object.productsPrice += product.getPrice() * product.getCount();
			object.productsCount += product.getCount();
		}

		for (ServiceBean service : getServicesAll()) {
			object.servicesPrice += service.getPrice() * service.getCount();
			object.servicesCount += service.getCount();
		}

		object.allCount = object.productsCount + object.servicesCount;
		object.allPrice = object.productsPrice + object.servicesPrice;

		return object;
	}

}
