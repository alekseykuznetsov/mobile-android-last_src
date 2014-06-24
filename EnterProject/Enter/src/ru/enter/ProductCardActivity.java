package ru.enter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONObject;

import com.flurry.android.FlurryAgent;
import com.google.analytics.tracking.android.EasyTracker;

import ru.enter.DataManagement.BasketManager;
import ru.enter.DataManagement.ProductCacheManager;
import ru.enter.ImageManager.ImageDownloader;
import ru.enter.adapters.ProductItemsListPageAdapter;
import ru.enter.beans.DeliveryBean;
import ru.enter.beans.ModelProductBean;
import ru.enter.beans.OptionArrayBean;
import ru.enter.beans.OptionBean;
import ru.enter.beans.ProductBean;
import ru.enter.beans.ServiceBean;
import ru.enter.beans.ShopBean;
import ru.enter.parsers.ProductInfoParser;
import ru.enter.utils.Constants;
import ru.enter.utils.Discount;
import ru.enter.utils.Formatters;
import ru.enter.utils.HTTPUtils;
import ru.enter.utils.PreferencesManager;
import ru.enter.utils.ShopLocator;
import ru.enter.utils.URLManager;
import ru.enter.utils.Utils;
import ru.enter.utils.ShopLocator.OnNearestShopLocateListener;
import ru.enter.widgets.Dots;
import ru.enter.widgets.HeaderFrameManager;
import ru.enter.widgets.NewHeaderFrameManager;
import ru.ideast.SocialServices.SendDialog;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.text.Html;
import android.text.Spanned;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.view.View.MeasureSpec;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.LinearLayout.LayoutParams;

public class ProductCardActivity extends Activity implements OnClickListener, OnNearestShopLocateListener{
	public static final String EXTRA_FROM = "extra_from";
	String mFrom;
	
	private TextView title, old_price, old_price_sym, new_price, new_price_sym, available, description, articulTV, discount;
	private RatingBar rating;
	private LinearLayout deliveryLinear;
	private FrameLayout prg;
	private ImageView image_main, image_tag;
	private LinearLayout main;
	private LinearLayout parameters;	
	private ImageDownloader imageDownloader;
	private Button button_like;
	private ImageButton button_video, button_rotate;//button_like, button_zoom;
	private Button button_fast_buy, button_basket, button_desc, button_features, button_services, button_where;
	private ProductItemsListPageAdapter smezhnye_adapter, accecories_adapter;//analog_adapter, 
	private Dots smezh_indicator, accecories_indicator;//analog_indicator, 

	private ViewPager accecories_view, smezhnie_view;//, analog_view;
	private LinearLayout acc_lay, smezh_lay, acc_lay_nobuyab, smezh_lay_nobuyab;//analog_lay, 
	private View smezh_view, acc_view;
	private ProgressBar deliveryProgress;
	private Context context;
	private Intent intent;
	private int id = -1;
	private double price;
	private LinearLayout services_linear;
	
	//for models
	private LinearLayout mModelList;
	private TextView textVariant;
	private OnClickListener myModelListner = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			intent = new Intent(ProductCardActivity.this, ModelsCardActivity.class);
			ProductCacheManager.getInstance().addProductInfo(mCurrentProductBean);
			startActivity(intent);
		}
	};

	public static final String ID = "productId";
	public static final String NAME = "productName";
	public static final String BARCODE = "productBarcode";
	public static final String PRODUCT = "product";

	private String social_img, social_title, social_url, social_price, social_desc;

	private ProductBean mCurrentProductBean;

	protected void onCreate (Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		EasyTracker.getInstance().setContext(this);

		setContentView(R.layout.product_cards);
		context = this;
		imageDownloader = new ImageDownloader(context);

		initView();

		manageView();

		if (getIntent().getExtras().containsKey(ID)) {
			int parsedId = getIntent().getExtras().getInt(ID);
			new ProductLoader().execute(parsedId);
		} else if (getIntent().getExtras().containsKey(BARCODE)) {
			// TODO
			String barcode = getIntent().getExtras().getString(BARCODE);
			new BarcodeLoader().execute(barcode);
		}
		int id = getIntent().getIntExtra(ID, 0);
		String name = getIntent().getStringExtra(NAME);
		EasyTracker.getTracker().sendEvent("product/get", "buttonPress", name, (long) id);

		if(getIntent().getExtras().containsKey(EXTRA_FROM)){
			mFrom = getIntent().getExtras().getString(EXTRA_FROM);
		} else {
			mFrom = Constants.FLURRY_FROM.Catalog.toString();
		}
	}
	
	@Override
	protected void onStart(){
		super.onStart();
		FlurryAgent.onStartSession(this, "3X5JRBVFV7QWXNP3Q8H2");
		
		if(mFrom != null){
			Map<String, String> flurryParam = new HashMap<String, String>();
			flurryParam.put(Constants.FLURRY_EVENT_PARAM.From.toString(), mFrom);
			FlurryAgent.logEvent(Constants.FLURRY_EVENT.Product_Screen.toString(),flurryParam);
			mFrom = null;
		}
	}
	 
	@Override
	protected void onStop(){
		super.onStop();		
		FlurryAgent.onEndSession(this);
	}

	private void setExistText (ProductBean bean) {

		String exist = "Нет в наличиии";
		int is_shop = bean.getShop();
		int scope_store_qty = bean.getScopeStoreQty();
		int scope_shops_qty = bean.getScopeShopsQty();
		int scope_shops_qty_showroom = bean.getScopeShopsQtyShowroom();
		int buyable = bean.getBuyable();
		boolean in_shop = (PreferencesManager.getUserCurrentShopId()!=0?true:false);
		if (in_shop && buyable == 1 && scope_shops_qty_showroom > 0) {
			exist = "Есть на витрине";
		} else {
			if (buyable == 1) {
				exist = "Есть в наличии";
				available.setTextSize(12);
			} else {
				if (is_shop == 1 && scope_store_qty == 0 && scope_shops_qty > 0) {
					exist = "Только в магазинах";
					available.setTextSize(10);
				} else {
					if (scope_shops_qty == 0 && scope_shops_qty_showroom > 0) {
						exist = "Только на витрине";
						available.setTextSize(10);
					} else {
						if (scope_shops_qty == 0) {
							exist = "Нет в наличии";
							available.setTextSize(12);
						}
					}
				}
			}
		}
		available.setText(exist);
	}

	private void init (final ProductBean obj) {
		if (obj != null) {
			articulTV.setText("#" + obj.getArticle());
			mCurrentProductBean = obj;
			button_basket.setEnabled(obj.getBuyable() == 1);
			title.setText(obj.getName());
			id = obj.getId();
			price = obj.getPrice();
			rating.setRating(obj.getRating());

			// if (obj.getBuyable() == 1) {
			// available.setText("Есть в наличии");
			// } else if (obj.getBuyable() == 0 && (!
			// Utils.isEmptyList(obj.getShop_list()))) {
			// available.setText("Только в магазинах");
			// } else {
			// available.setText("Нет в наличии");
			// }
			
			//model
			if(Utils.isEmptyList(mCurrentProductBean.getModelsProduct())) {
				mModelList.setVisibility(View.GONE);
			} else {
				final ArrayList<ModelProductBean> models = mCurrentProductBean.getModelsProduct();
				LayoutParams params = new LayoutParams(LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT);
				int margin = Utils.dp2pix(this, 4);
				params.setMargins(margin, margin, margin,margin);
				Spanned textSpan;
				TextView view;
				for(int i=0;i<models.size();i++){	
					view = new TextView(this);
					textSpan  =  android.text.Html.fromHtml("<u>"+models.get(i).getProperty()+(i<models.size()-1?"</u>,":"</u>"));					
					view.setText(textSpan);
					view.setLayoutParams(params);
					view.setTextColor(Color.rgb(231, 151, 54));
					view.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 15);
					view.setOnClickListener(myModelListner);
					mModelList.addView(view);
					
					/*
					view.setOnClickListener(new OnClickListener() {
						
						@Override
						public void onClick(View v) {
							// TODO Auto-generated method stub
							QuickAction quickAction = new QuickAction(ProductCardActivity.this,QuickAction.VERTICAL,true);
							LayoutInflater inf = getLayoutInflater();
							ListView listModel = (ListView) inf.inflate(R.layout.product_card_model_list, null);
							ProductCardModelAdapter modelAdapter = new ProductCardModelAdapter(ProductCardActivity.this,models.get(j));
							listModel.setAdapter(modelAdapter);
							quickAction.addActionItem(listModel);
							quickAction.show(v);
							
//							final QuickAction quickAction = new QuickAction(ProductCardActivity.this,QuickAction.VERTICAL,true);
//							TextView text = new TextView(ProductCardActivity.this);
//							text.setText("sdvlsbndvljsbnlv sdvlsbndvljsbnlv sdvlsbndvljsbnlv sdvlsbndvljsbnlv sdvlsbndvljsbnlv sdvlsbndvljsbnlv sdvlsbndvljsbnlv sdvlsbndvljsbnlv sdvlsbndvljsbnlv sdvlsbndvljsbnlv sdvlsbndvljsbnlv ");
//							//text.setText("sdvlsbndvljsbnlv");
//							text.setOnClickListener(new OnClickListener() {
//								
//								@Override
//								public void onClick(View v) {
//									// TODO Auto-generated method stub
//									
//								}
//							});
//							quickAction.addActionItem(text);
//							quickAction.show(v);
						}
					});*/
				}
				WindowManager windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
				int width = windowManager.getDefaultDisplay().getWidth();
				mModelList.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
				
				mModelList.measure(MeasureSpec.makeMeasureSpec(width, MeasureSpec.AT_MOST),MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED));
				int listWidth = mModelList.getMeasuredWidth();
				if(listWidth>width-10)
					textVariant.setVisibility(View.GONE);
			}
			//end model

			setExistText(obj);

			if (Utils.isEmptyList(mCurrentProductBean.getShop_list())) {
				button_where.setVisibility(View.GONE);
			}
			try {
				imageDownloader.download(obj.getLabel().get(0).getFoto(context), image_tag);
			} catch (Exception e) {
			}
			if (obj.getDescription() != null)
				if (!obj.getDescription().equals(""))
					description.setText(obj.getDescription());
				else {
					button_desc.setVisibility(View.GONE);
					button_features.performClick();
				}
			if (!obj.has3D())// TODO may crash with nullpointer! UPD: maybe has
								// been fixed (see has3D())
				button_rotate.setVisibility(View.GONE);
			//if (!obj.hasGallery())
			//	button_zoom.setVisibility(View.GONE);

			if (!Utils.isEmptyList(obj.getAccessories())) {
				accecories_adapter.setObjects(obj.getAccessories());
				accecories_adapter.notifyDataSetChanged();
				accecories_indicator.initDots(obj.getAccessories().size(), 2, 0);
				if(obj.getBuyable()==1){
					acc_lay.addView(acc_view);
					acc_lay.setVisibility(View.VISIBLE);
				} else {
					acc_lay_nobuyab.addView(acc_view);
					acc_lay_nobuyab.setVisibility(View.VISIBLE);
				}
			}
			if (!Utils.isEmptyList(obj.getRelated())) {
				smezhnye_adapter.setObjects(obj.getRelated());
				smezhnye_adapter.notifyDataSetChanged();
				smezh_indicator.initDots(obj.getRelated().size(), 2, 0);
				if(obj.getBuyable()==1){
					smezh_lay.addView(smezh_view);
					smezh_lay.setVisibility(View.VISIBLE);
				} else {
					smezh_lay_nobuyab.addView(smezh_view);
					smezh_lay_nobuyab.setVisibility(View.VISIBLE);
				}
			}

			// TODO
			if (!Utils.isEmptyList(obj.getServices())) {
				// инициализация услуг
				initServicesLayout(obj.getServices());
			} else {
				services_linear.setVisibility(View.GONE);
				button_services.setVisibility(View.GONE);
			}

			new_price.setText(obj.getPriceFormattedString());
			
			new_price.setTypeface(Utils.getTahomaTypeFace(context));
			old_price.setTypeface(Utils.getTahomaTypeFace(context));
			discount.setTypeface(Utils.getTahomaTypeFace(context));
			
			double oldPrice = obj.getPrice_old();
//			oldPrice = 10000;
			if(oldPrice > 0){
//				LinearLayout oldPriceLayout = (LinearLayout) findViewById(R.id.l_old_price);
//				oldPriceLayout.setVisibility(View.VISIBLE);
				old_price.setText(Math.round(oldPrice)+"");
				discount.setText(Discount.getDiscount(obj.getPrice(), oldPrice));
				
				old_price.setVisibility(View.VISIBLE);
				old_price_sym.setVisibility(View.VISIBLE);
				discount.setVisibility(View.VISIBLE);
				
				new_price.setTextColor(getResources().getColor(R.color.red));
				new_price.setTextSize(16);
				new_price_sym.setTextColor(getResources().getColor(R.color.red));
				new_price_sym.setTextSize(16);
				//mTitleText.setTextColor(getResources().getColorStateList(R.color.defaulttextred));
			}
			
			if (obj.getOptions() != null)
				addParam(obj.getOptions());
			else
				button_features.setVisibility(View.GONE);

			String fotoUrl = obj.getFoto();
			if (!"".equals(fotoUrl)) {
				imageDownloader.download(fotoUrl, image_main);
				social_img = fotoUrl;
			}
			social_title = obj.getName();
			social_url = (obj.getLink() == null ? "http://www.enter.ru" : obj.getLink());
			social_price = String.valueOf(obj.getPrice());
			social_desc = obj.getAnnounce() != null ? obj.getAnnounce() : "";

			ArrayList<DeliveryBean> deliveries = obj.getDelivery_mod();
			if (Utils.isEmptyList(deliveries)) {
				TextView impossible = new TextView(context);
				impossible.setText("Только в магазинах");
				deliveryLinear.addView(impossible);
			} else {
				TextView howto = new TextView(context);
				howto.setText(Html.fromHtml("<b>Как получить заказ:</b>"));
				deliveryLinear.addView(howto);
				for (DeliveryBean bean : deliveries) {
					if (bean.getMode_id() > 2) {
						//TextView tv = new TextView(context);
						//tv.setText("Самовывоз - бесплатно");
						TextView tv2 = new TextView(context);
						tv2.setText(Html.fromHtml("<u>Самовывоз - бесплатно, " + bean.getMyDelivery() + "</u>"));
						//deliveryLinear.addView(tv);
						deliveryLinear.addView(tv2);
						tv2.setOnClickListener(new OnClickListener() {

							@Override
							public void onClick (View v) {
								Intent intent = new Intent(ProductCardActivity.this, ShopsActivity.class);
								intent.putExtra("LINK", true);
								startActivity(intent);
							}
						});
					} else {
						TextView tv = new TextView(context);
						tv.setText(bean.getDeliveryNameWithPrice()+bean.getShopDelivery());
						//TextView tv2 = new TextView(context);
						//tv2.setText(bean.getShopDelivery());
						deliveryLinear.addView(tv);
						//deliveryLinear.addView(tv2);
					}
				}
			}
			
			if (PreferencesManager.getUserCurrentShopId() != 0) {
				int is_shop = obj.getShop();
				int scope_store_qty = obj.getScopeStoreQty();
				int scope_shops_qty = obj.getScopeShopsQty();

				if (is_shop > 0 && (scope_store_qty > 0 || scope_shops_qty > 0)) {
					button_fast_buy.setVisibility(View.VISIBLE);
				}
			}
		}
		prg.setVisibility(View.GONE);
		main.setVisibility(View.VISIBLE);
	}

	private void initServicesLayout (ArrayList<ServiceBean> services) {
		LayoutInflater mInflater = LayoutInflater.from(context);
		ImageDownloader mImageLoader = new ImageDownloader(context);

		for (final ServiceBean service : services) {
			View newView = mInflater.inflate(R.layout.product_card_services_list_row, null);

			Button buttonBuy = (Button) newView.findViewById(R.id.product_card_services_button_buy);
			ImageView image = (ImageView) newView.findViewById(R.id.product_card_services_image);
			TextView title = (TextView) newView.findViewById(R.id.product_card_services_title);
			TextView price = (TextView) newView.findViewById(R.id.product_card_services_price);
			TextView ruble = (TextView) newView.findViewById(R.id.product_card_services_ruble);

			String foto = Formatters.createFotoString(service.getFoto(), 160);
			mImageLoader.download(foto, image);

			ruble.setTypeface(Utils.getRoubleTypeFace(mInflater.getContext()), Typeface.BOLD);

			price.setText(String.valueOf((int) service.getPrice()));
			title.setText(service.getName());

			buttonBuy.setTag(service);
			buttonBuy.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick (View v) {
					boolean is = BasketManager.addRelatedService(mCurrentProductBean, service);
					showServiceDialog(is, mCurrentProductBean.getName(), service.getName());
					if(is) {
						EasyTracker.getTracker().sendEvent("cart/add-product", "buttonPress", mCurrentProductBean.getName(), (long) mCurrentProductBean.getId());
						EasyTracker.getTracker().sendEvent("cart/add-service", "buttonPress", service.getName(), (long) service.getId());
					} else EasyTracker.getTracker().sendEvent("cart/add-service", "buttonPress", service.getName(), (long) service.getId());
				}
			});

			services_linear.addView(newView);
		}

	}

	private void addParam (ArrayList<OptionArrayBean> bean) {
		LayoutInflater viewInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		
		boolean isModel;		
		ModelProductBean curMod = null;
		
		ArrayList<ModelProductBean> models = mCurrentProductBean.getModelsProduct();
		
		for (OptionArrayBean oab : bean) {
			
			View nameView = (View) viewInflater.inflate(R.layout.two_tw_row_title, null);
			TextView nameText = (TextView) nameView.findViewById(R.id.two_tw_title_text_1);
			TextView t1 = (TextView) nameView.findViewById(R.id.two_tw_title_text_2);
			TextView t2 = (TextView) nameView.findViewById(R.id.two_tw_title_text_3);
			t1.setVisibility(View.GONE);
			t2.setVisibility(View.GONE);
			nameText.setText(oab.getName());
			nameView.setBackgroundColor(Color.LTGRAY);
			parameters.addView(nameView);

			for (OptionBean b : oab.getOption()) {
				
				//model
				isModel = false;
				if (!Utils.isEmptyList(models))
					for (ModelProductBean mod : models) {
						if (b.getProperty().equals(mod.getProperty())) {
							isModel = true;
							curMod = mod;
							break;
						}
					}
				if (isModel) {
					View frame = (View) viewInflater.inflate(R.layout.two_tw_row_model, null);

					TextView title = (TextView) frame.findViewById(R.id.two_tw_model_text_1);
					TextView option = (TextView) frame.findViewById(R.id.two_tw_model_text_2);
					TextView value = (TextView) frame.findViewById(R.id.two_tw_model_text_3);

					if (!b.getProperty().equals("null"))
						title.setText(android.text.Html.fromHtml("<u>"+capitalizeFirstChar(b.getProperty())+"</u>"));
					else
						title.setVisibility(View.GONE);

					String opt = b.getOption();
					if (!opt.equals("null") && opt.length() > 2) {

						option.setText(android.text.Html.fromHtml("<u>"+capitalizeFirstChar(clearOptionStr(opt))+"</u>"));
					} else
						option.setVisibility(View.GONE);// TODO

					if (!b.getValue().equals("null"))
						value.setText(android.text.Html.fromHtml("<u>"+capitalizeFirstChar(b.getValue())+(!b.getUnit().equals("null") ? " "+b.getUnit() : "")+"</u>"));
					else
						value.setVisibility(View.GONE);
					frame.setOnClickListener(myModelListner);

					parameters.addView(frame);
				} else {

					View frame = (View) viewInflater.inflate(R.layout.two_tw_row, null);

					TextView title = (TextView) frame.findViewById(R.id.two_tw_text_1);
					TextView option = (TextView) frame.findViewById(R.id.two_tw_text_2);
					TextView value = (TextView) frame.findViewById(R.id.two_tw_text_3);

					if (!b.getProperty().equals("null"))
						title.setText(capitalizeFirstChar(b.getProperty()));
					else
						title.setVisibility(View.GONE);

					String opt = b.getOption();
					if (!opt.equals("null") && opt.length() > 2) {

						option.setText(capitalizeFirstChar(clearOptionStr(opt)));
					} else
						option.setVisibility(View.GONE);// TODO

					if (!b.getValue().equals("null"))
						value.setText(capitalizeFirstChar(b.getValue())	+ (!b.getUnit().equals("null") ? " " + b.getUnit() : ""));
					else
						value.setVisibility(View.GONE);

					parameters.addView(frame);
				}
			}
		}

	}

	private String clearOptionStr (String str) {
		String result = "";
		result = str.substring(1, str.length() - 1);// remove [..]
		String[] tmp = result.split(",");
		result = "";
		for (int i = 0; i < tmp.length; i++) {
			result += tmp[i].substring(1, tmp[i].length() - 1);
			if (i != tmp.length - 1) {
				result += ",";
			}
		}
		return result;
	}

	private String capitalizeFirstChar (String str) {
		if ("true".equalsIgnoreCase(str))
			return "Да";
		if (str == null || "".equals(str))
			return str;
		if (str.length() == 1)
			return str.toUpperCase();
		return Character.toUpperCase(str.charAt(0)) + str.substring(1);
	}

	private void initView () {
		rating = (RatingBar) findViewById(R.id.product_card_rating);
		button_rotate = (ImageButton) findViewById(R.id.product_card_button_rotate);
		//button_zoom = (ImageButton) findViewById(R.id.product_card_button_zoom);
		button_like = (Button) findViewById(R.id.product_card_button_like);
		button_video = (ImageButton) findViewById(R.id.product_card_button_video);
		button_fast_buy = (Button) findViewById(R.id.product_card_button_fast_buy);
		button_basket = (Button) findViewById(R.id.product_card_button_basket);
		button_desc = (Button) findViewById(R.id.product_card_button_description);
		button_features = (Button) findViewById(R.id.product_card_button_features);
		button_services = (Button) findViewById(R.id.product_card_button_services);
		button_where = (Button) findViewById(R.id.product_card_button_where);
		title = (TextView) findViewById(R.id.product_card_title);
		old_price = (TextView) findViewById(R.id.product_card_old_price);
		new_price = (TextView) findViewById(R.id.product_card_new_price);
		old_price_sym = (TextView) findViewById(R.id.product_card_old_price_sym);
		new_price_sym = (TextView) findViewById(R.id.product_card_new_price_sym);
		discount = (TextView) findViewById(R.id.product_card_discount);
		available = (TextView) findViewById(R.id.product_card_available);
		deliveryLinear = (LinearLayout) findViewById(R.id.product_card_delivery);
		description = (TextView) findViewById(R.id.product_card_text_description);
		image_tag = (ImageView) findViewById(R.id.product_card_image_tag);
		image_main = (ImageView) findViewById(R.id.product_card_image_main);
		parameters = (LinearLayout) findViewById(R.id.product_card_parameters);
		deliveryProgress = (ProgressBar) findViewById(R.id.product_card_delivery_progress);
		deliveryProgress.setVisibility(View.GONE);					
		prg = (FrameLayout) findViewById(R.id.product_card_frame_progress);
		main = (LinearLayout) findViewById(R.id.product_card_main_linear);
		articulTV = (TextView) findViewById(R.id.product_card_articul);
		acc_lay = (LinearLayout) findViewById(R.id.product_card_accecories_lay);
		smezh_lay = (LinearLayout) findViewById(R.id.product_card_smezh_lay);
		
		acc_lay_nobuyab = (LinearLayout) findViewById(R.id.product_card_accecories_lay_no_buyable);
		smezh_lay_nobuyab = (LinearLayout) findViewById(R.id.product_card_smezh_lay_no_buyable);
		
		LayoutInflater inflater = getLayoutInflater();
		
		smezh_view = inflater.inflate(R.layout.product_card_smezh_products, null);
		
		smezhnie_view = (ViewPager) smezh_view.findViewById(R.id.product_card_smezh_tov_view);	
		smezh_indicator = (Dots) smezh_view.findViewById(R.id.product_card_smezh_tov_view_indicator);
		
		//analog_lay = (LinearLayout) findViewById(R.id.product_card_analog_lay);
		//analog_indicator = (Dots) findViewById(R.id.product_card_analog_tov_view_indicator);
		//analog_view = (ViewPager) findViewById(R.id.product_card_analog_tov_view);
		acc_view = inflater.inflate(R.layout.product_card_accecories_product, null);
		accecories_view = (ViewPager) acc_view.findViewById(R.id.product_card_accecories_view);
		accecories_indicator = (Dots) acc_view.findViewById(R.id.product_card_accecories_view_indicator);
		services_linear = (LinearLayout) findViewById(R.id.product_card_services_linear);
		
		mModelList = (LinearLayout) findViewById(R.id.product_card_model_list);
		textVariant = (TextView) findViewById(R.id.product_card_model_list_variant);

		FrameLayout frame = (FrameLayout) findViewById(R.id.product_card_frame);
		if (PreferencesManager.getUserCurrentShopId() == 0){
			frame.addView(HeaderFrameManager.getHeaderView(context, "Карточка товара", false));
		} else {
			frame.addView(NewHeaderFrameManager.getHeaderView(context, PreferencesManager.getUserCurrentShopName()));
		}
		//frame.addView(HeaderFrameManager.getHeaderView(context, "Карточка товара", false));
	}

	private void manageView () {
		// setControlButtonGray(button_desc);
		setControlButtonGray(button_features);
		button_rotate.setOnClickListener(this);
		//button_zoom.setOnClickListener(this);
		button_like.setOnClickListener(this);
		button_video.setOnClickListener(this);
		button_fast_buy.setOnClickListener(this);
		button_basket.setOnClickListener(this);
		button_desc.setOnClickListener(this);
		button_features.setOnClickListener(this);
		button_services.setOnClickListener(this);
		button_where.setOnClickListener(this);
		image_main.setOnClickListener(this);
		
		main.setVisibility(View.GONE); // убираем всё, до загрузки данных.
		// button_services.setVisibility(View.GONE);//Убрать отзывы//TODO
		// image_tag.setVisibility(View.GONE);//убрали тэг у товара, что он
		// "новинка"
		button_video.setVisibility(View.GONE); // Видео пока убрали
		description.setVisibility(View.GONE);
		// parameters.setVisibility(View.GONE);

		old_price_sym.setTypeface(Utils.getRoubleTypeFace(context));
		new_price_sym.setTypeface(Utils.getRoubleTypeFace(context));
		old_price.setPaintFlags(new_price.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
		old_price_sym.setPaintFlags(new_price.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);

		accecories_adapter = new ProductItemsListPageAdapter(this);
		//analog_adapter = new ProductItemsListPageAdapter(this);
		smezhnye_adapter = new ProductItemsListPageAdapter(this);

		accecories_view.setAdapter(accecories_adapter);
		smezhnie_view.setAdapter(smezhnye_adapter);
		//analog_view.setAdapter(analog_adapter);

		accecories_view.setOnPageChangeListener(new OnPageChangeListener() {
			@Override
			public void onPageSelected (int arg0) {
				accecories_indicator.setSelected(arg0);
			}

			@Override
			public void onPageScrolled (int arg0, float arg1, int arg2) {}

			@Override
			public void onPageScrollStateChanged (int arg0) {}
		});
		smezhnie_view.setOnPageChangeListener(new OnPageChangeListener() {
			@Override
			public void onPageSelected (int arg0) {
				smezh_indicator.setSelected(arg0);
			}

			@Override
			public void onPageScrolled (int arg0, float arg1, int arg2) {}

			@Override
			public void onPageScrollStateChanged (int arg0) {}
		});
		
		/*
		analog_view.setOnPageChangeListener(new OnPageChangeListener() {
			@Override
			public void onPageSelected (int arg0) {
				analog_indicator.setSelected(arg0);
			}

			@Override
			public void onPageScrolled (int arg0, float arg1, int arg2) {}

			@Override
			public void onPageScrollStateChanged (int arg0) {}
		});*/

	}

	private void setControlButtonGray (Button button) {

		button_desc.setBackgroundColor(Color.rgb(0, 0, 0));
		button_features.setBackgroundColor(Color.rgb(0, 0, 0));
		button_where.setBackgroundColor(Color.rgb(0, 0, 0));
		button_services.setBackgroundColor(Color.rgb(0, 0, 0));
		button_desc.setTextColor(Color.argb(204, 255, 255, 255));		
		button_features.setTextColor(Color.argb(204, 255, 255, 255));
		button_where.setTextColor(Color.argb(204, 255, 255, 255));
		button_services.setTextColor(Color.argb(204, 255, 255, 255));

		button.setBackgroundColor(Color.rgb(234, 127, 0));
		button.setTextColor(Color.WHITE);

	}

	private void showDialog () {
		AlertDialog.Builder dlg = new AlertDialog.Builder(context);
		dlg.setMessage("Товар добавлен в корзину").setPositiveButton("Перейти в корзину", new DialogInterface.OnClickListener() {
			public void onClick (DialogInterface dialog, int whichButton) {
				Intent intent = new Intent();
				intent.setClass(context, BasketActivity.class);
				intent.putExtra(BasketActivity.SHOW_BUTTON, true);
				startActivity(intent);
			}
		}).setNegativeButton("Продолжить покупки", new DialogInterface.OnClickListener() {
			public void onClick (DialogInterface dialog, int whichButton) {

			}
		}).create().show();
	}

	private void showServiceDialog (boolean isProductAdded, String productName, String serviceName) {
		String message = isProductAdded ? String.format("Услуга \"%s\" и товар \"%s\" добавлены в корзину", serviceName, productName)
				: String.format("Услуга \"%s\" добавлена в корзину", serviceName);		

		AlertDialog.Builder dlg = new AlertDialog.Builder(context);
		dlg.setMessage(message).setPositiveButton("Перейти в корзину", new DialogInterface.OnClickListener() {
			public void onClick (DialogInterface dialog, int whichButton) {
				Intent intent = new Intent();
				intent.setClass(context, BasketActivity.class);
				intent.putExtra(BasketActivity.SHOW_BUTTON, true);
				startActivity(intent);
			}
		}).setNegativeButton("Продолжить покупки", new DialogInterface.OnClickListener() {
			public void onClick (DialogInterface dialog, int whichButton) {

			}
		}).create().show();
	}

	private void showEmptyDialog () {
		AlertDialog.Builder dlg = new AlertDialog.Builder(context);
		dlg.setTitle("Ошибка").setMessage("Товар не найден").setPositiveButton("Вернуться", new DialogInterface.OnClickListener() {
			public void onClick (DialogInterface dialog, int whichButton) {
				finish();
			}
		}).create().show();
	}

	@Override
	public void onClick (View v) {
		switch (v.getId()) {
		case R.id.product_card_button_like:
			intent = new Intent().setClass(context, SendDialog.class);
			intent.putExtra(SendDialog.EX_TITLE, social_title);
			intent.putExtra(SendDialog.EX_URL, social_url);
			intent.putExtra(SendDialog.EX_IMAGE, social_img);
			intent.putExtra(SendDialog.EX_DESC, social_desc);
			intent.putExtra(SendDialog.EX_PRICE, social_price);

			startActivity(intent);
			break;
		case R.id.product_card_button_rotate:
			intent = new Intent().setClass(context, Product3D.class);
			ProductCacheManager.getInstance().addProductInfo(mCurrentProductBean);
			startActivity(intent);
			break;
		//case R.id.product_card_button_zoom:

		case R.id.product_card_image_main:

			if (id != -1) {
				if (mCurrentProductBean.getGalleryLength() > 1) {
					intent = new Intent(ProductCardActivity.this, ProductCarousel.class);
					ProductCacheManager.getInstance().addProductInfo(mCurrentProductBean);
					startActivity(intent);
				} else if (mCurrentProductBean.getGalleryLength() == 1) {
					intent = new Intent(ProductCardActivity.this, ProductGallery.class);
					ProductCacheManager.getInstance().addProductInfo(mCurrentProductBean);
					startActivity(intent);
				}
//				FlurryAgent.logEvent(Constants.FLURRY_EVENT.Zoomed_Picture_Open.toString());
			}
			break;
		case R.id.product_card_button_video:
			break;
		case R.id.product_card_button_fast_buy:
			ShopLocator locator = new ShopLocator(ProductCardActivity.this);
			locator.setOnNearestShopLocateListener(ProductCardActivity.this);
			locator.start();
			break;
		case R.id.product_card_button_basket:
			if (mCurrentProductBean != null) {
				BasketManager.addProduct(mCurrentProductBean);
				EasyTracker.getTracker().sendEvent("cart/add-product", "buttonPress", mCurrentProductBean.getName(), (long) mCurrentProductBean.getId());
				showDialog();
			}
			break;
		case R.id.product_card_button_description:
			description.setVisibility(View.VISIBLE);
			parameters.setVisibility(View.GONE);
			services_linear.setVisibility(View.GONE);
			setControlButtonGray((Button) v);
			break;
		case R.id.product_card_button_features:
			description.setVisibility(View.GONE);
			parameters.setVisibility(View.VISIBLE);
			services_linear.setVisibility(View.GONE);
			setControlButtonGray((Button) v);
			break;
		case R.id.product_card_button_services:// TODO
			description.setVisibility(View.GONE);
			parameters.setVisibility(View.GONE);
			services_linear.setVisibility(View.VISIBLE);
			setControlButtonGray((Button) v);
			break;
		case R.id.product_card_button_where:
			ProductCacheManager.getInstance().addProductInfo(mCurrentProductBean);
			// setControlButtonGray((Button) v);
			intent = new Intent(ProductCardActivity.this, ProductWhereToBuy.class);
			Boolean isShowWindow = checkShowWindowStatus(mCurrentProductBean);
			intent.putExtra("isShowWindow", isShowWindow);
			startActivity(intent);
			break;

		default:
			break;
		}
	}
	
	private boolean checkShowWindowStatus (ProductBean bean) {

		boolean status = false;
		int is_shop = bean.getShop();
		int scope_store_qty = bean.getScopeStoreQty();
		int scope_shops_qty = bean.getScopeShopsQty();
		int scope_shops_qty_showroom = bean.getScopeShopsQtyShowroom();
		if (bean.getBuyable() == 1) {

		} else {
			if (is_shop == 1 && scope_store_qty == 0 && scope_shops_qty > 0) {
			} else {
				if (scope_shops_qty == 0 && scope_shops_qty_showroom > 0) {
					status = true;
				}
			}
		}
		return status;
	}

	private class ProductLoader extends AsyncTask<Integer, Void, ProductBean> {

		@Override
		protected void onPreExecute () {
			super.onPreExecute();
			prg.setVisibility(View.VISIBLE);
		}

		@Override
		protected ProductBean doInBackground (Integer... params) {
			int cityId = PreferencesManager.getCityid();
			int size = Utils.getSizeForMainImg(ProductCardActivity.this);
			String url = URLManager.getProductCard(cityId, PreferencesManager.getUserCurrentShopId(), params[0], size);
			ProductInfoParser parser = new ProductInfoParser(url);
			//ProductInfoParser parser = new ProductInfoParser("http://enter.newswired.ru/api2_1/product_card/14974/94863/1500");
			return parser.parse();
		}

		@Override
		protected void onPostExecute (ProductBean result) {
			init(result);
		}
	}

	private class BarcodeLoader extends AsyncTask<String, Void, ProductBean> {

		private String barcode;

		@Override
		protected void onPreExecute () {
			super.onPreExecute();
			prg.setVisibility(View.VISIBLE);
		}

		@Override
		protected ProductBean doInBackground (String... params) {
			barcode = params[0];
			int cityId = PreferencesManager.getCityid();
			int size = Utils.getSizeForMainImg(ProductCardActivity.this);
			ProductInfoParser parser = new ProductInfoParser(URLManager.getSearchByBarCode(barcode, cityId, String.valueOf(size)));
			return parser.parse();
		}

		@Override
		protected void onPostExecute (ProductBean result) {
			if (result == null || result.getId() == 0) {
				sendLetter(barcode);
			} else {
				init(result);
			}
		}
	}

	private void showAlertBarcode () {
		AlertDialog.Builder builder = new AlertDialog.Builder(context);
		builder.setMessage("Этого товара пока нет в Enter, но каждую неделю мы расширяем ассортимент более чем на 100 позиций. Попробуйте просканировать штрих-код позже!");
		builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {

			@Override
			public void onClick (DialogInterface dialog, int which) {
				finish();
			}
		});

		builder.create();
		builder.show();

	}

	private void sendLetter (String text) {
		new SendLetter().execute(text);
	}

	private class SendLetter extends AsyncTask<String, Void, Void> {

		@Override
		protected void onPreExecute () {
			super.onPreExecute();
			prg.setVisibility(View.VISIBLE);
		}

		@Override
		protected Void doInBackground (String... params) {

			try {
				String cityname = PreferencesManager.getCityName();
				JSONObject object = new JSONObject();
				object.put("theme", "Добавить товара");
				String body = String.format("баркод - %s, город %s, платформа - Android-smartphone", params[0], cityname);
				object.put("text", body);

				HTTPUtils.sendPostJSON(URLManager.getFeedBack(), object);
			} catch (Exception e) {
			}

			return null;
		}

		@Override
		protected void onPostExecute (Void result) {
			prg.setVisibility(View.GONE);
			showAlertBarcode();
		}
	}

	@Override
	public void onStartLocate () {
		prg.setVisibility(View.VISIBLE);
	}

	@Override
	public void onFailLocate () {
		prg.setVisibility(View.GONE);
		button_fast_buy.setVisibility(View.GONE);
		Toast.makeText(ProductCardActivity.this, "Вы не в магазине", Toast.LENGTH_SHORT).show();
	}

	@Override
	public void onShopLocated (ShopBean shop) {
		prg.setVisibility(View.GONE);
		
		if (shop.getId() == PreferencesManager.getUserCurrentShopId()) {
			ProductCacheManager.getInstance().addProductInfo(mCurrentProductBean);//TODO
			Intent buyNowIntent = new Intent(ProductCardActivity.this, OrderCompleteBuyNowActivity.class);
//			buyNowIntent.putExtra(OrderCompleteBuyNowActivity.PRODUCT_BEAN, mCurrentProductBean);
			startActivity(buyNowIntent);
		} else {
			button_fast_buy.setVisibility(View.GONE);
			Toast.makeText(ProductCardActivity.this, "Вы находитесь в другом магазине", Toast.LENGTH_SHORT).show();
		}
		
	}
	
	@Override
	public void onBackgroundLocated(ShopBean shop) {
		
	}
	@Override
	public void onBackgroundFailLocate() {
	}

}
