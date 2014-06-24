package ru.enter.ImageManager;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import ru.enter.utils.Log;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.os.Handler;
import android.os.Message;
import android.widget.ImageView;

public class ImageDownloader 
{  
	//Текущее число работающих потоков
    private int mThreads = 0;
    //Максимальное число работающих потоков
    private int mMaxThreads;
    //Каталог для загрузки кешированных изображений 
    private final String mMainCacheDir = android.os.Environment.getExternalStorageDirectory() + "/.iDEast/images/";
    //Очередь загрузки 
    private ArrayList<OrderElement> mOrder = new ArrayList<OrderElement>();
    //private ImagesDataBase mDatabase = null;  
    //Таблица загруженных картинок
    private static HashMap<String, Bitmap> mBitmapsHashMap = new HashMap<String, Bitmap>();
    //Список фоток которые уже запрошены, но еще не загружены
    private static HashSet<String> mRequest = new HashSet<String>(); 
    //Максимальный размер кэша памяти в кПикс
    private static long mACCache = 0;		
    private static long mCurrCacheSize = 0;
    //Ограничение по площади кешируемых в память изображений
    private long mSquareLimit = 0;
    //Степень закругления краев    
    private int mRoundness = 0; 	//Степень закругления краев изображения изображения    
    private Context mContext;
    
    private ImagesDataBase mIdb;
    
    private static boolean D = false;
    /**
     * Конструктор класса загрузчика картинок<br>
     * По умолчанию максимальное число работающих одновременно потоков - 5
     */
    public ImageDownloader(Context context)
    {
    	mMaxThreads = 5;
    	mContext = context;
    	mIdb = ImagesDataBase.getInstance(context);
//    	clearFlashCache();
    }
    /**
     * @return максимально возможное число работающих вместе потоков
     */
	public int getMaxThreads() 
	{
		return mMaxThreads;
	}
	/**
	 * Устанавливает максимальное количество потоков
	 * @param i
	 */
	public void setMaxThreads(int i)
	{
		mMaxThreads = i;
	}
	/**
	 * @return возвращает число работающих потоков 
	 */
	public int getThreadsCount()
	{
		return mThreads;
	}
	/**
	 * Очистка очереди
	 */
	public void OrderClear()
	{
		mOrder.clear();
	}
	/**
	 * @return возвращает доступное число потоков
	 */
	public int getThreadsAvailable()
	{
		return mMaxThreads - mThreads;
	}
	/**
	 * @return возвращает каталог кеширования
	 */
	public final String getCacheDir()
	{
		return mMainCacheDir;
	}
	/**
	 * Возвращает из памяти картинку с заданым url
	 * @param url - url картинки
	 * @return картинку, если огна кеширована, или null в противном случае
	 */
	public static Bitmap getBitmapFromMemory(String url)
	{
		synchronized (mBitmapsHashMap) 
		{
			return mBitmapsHashMap.get(url);
		}
	}
	/**
	 * Удаляет все загруженные картинки из памяти
	 */
	public static void clearMemoryCache()
	{		
		synchronized (mBitmapsHashMap) 
		{
			mBitmapsHashMap.clear();
		}
		mCurrCacheSize = 0;	
		if (D)
			Log.i("ImageDownloader", "Memory cache cleared!");
		Runtime.getRuntime().gc();
	}
	/**
	 * Очищает кэш на флешке<br>
	 * Делает это только в случае если ничего не загружается
	 */
	public void clearFlashCache()
	{
		//Получаем все записи из БД
		if (mThreads == 0)
		{
			//Получаем ссылки удаляемых файлов
			synchronized (mIdb) 
			{
				ArrayList<String> links = mIdb.getLinks();
				for (String link : links)
				{
					//Удаляем файл
					File file = new File(link);
					try
					{
						file.delete();
					}
					catch(SecurityException e)
					{					
					}
				}
				mIdb.clearDatabase();	
			}
		}
		else if (D)
			Log.i("ImageDownloader:clearFlashCache", "Cache not cleared!");
	}
	/**
	 * @return возвращает уровень ограничения кеширования изображения по площади
	 */
	public long getSquareMemoryLimit()
	{
		return mSquareLimit;
	}
	/**
	 * Устанавливает уровень ограничения по площади кеширования изображения в память
	 * Изображения, имеющие размер больше чем squareLimit не будут кешированы.
	 * @param squareLimit - ограничение по площади (если squareLimit <= 0 изображения кешируются всегда)
	 */
	public void setSquareMemoryLimit(long squareLimit)
	{
		mSquareLimit = squareLimit;
	}
	/**
	 * Устанавливает степень закругления краев изображения
	 * @param mRoundness - степень в пикселях
	 */
	public void setRoundness(int roundness) 
	{
		this.mRoundness = roundness;
	}
	/**
	 * @return степень закругления краев изображения
	 */
	public int getRoundness() 
	{
		return mRoundness;
	}
	/**
	 * Устанавливает значение размера кеша памяти при котором
	 * произойдет автоматическая очистка кеша
	 * @param cache - размер кеша
	 */
	public static void setAutoClearCache(long cache) 
	{
		mACCache = cache;
	}
	/**
	 * @return возвращает значение максимального размера кеша памяти в кПикс
	 */
	public static long getAutoClearCache() 
	{
		return mACCache;
	}
	/**
	 * Устанавливает картинку из интернета 
	 * @param url - ссылка на картинку
	 * @param iv - куда ее вставить
	 */
	public void download(final String url, final ImageView iv)
	{
		Bitmap result;
		//Проверяем есть ли картинка в памяти
		if ((result = getBitmapFromMemory(url)) != null && iv != null)
		{
			iv.setImageBitmap(result);
			if (mThreads < mMaxThreads)
				downloadLastElementFromOrder();
		}
		else
		{
			//Картинки в памяти нету, значит грузим
			if (mThreads >= mMaxThreads)
			{
				OrderElement oe = new OrderElement(url, iv);
				synchronized (mOrder) 
				{
					//Если такой элемент отсутствует в очереди, то добавляем его 
					if (!mOrder.contains(oe))
						mOrder.add(oe);	
				}
			}
			else
			{				
				final Handler hlr = new Handler() 
				{
					public void handleMessage(android.os.Message msg) 
					{
						//Достаем картинку и устанавливаем ее
						Bitmap bmp = (Bitmap) msg.obj;
						if (bmp != null && iv != null)
							iv.setImageBitmap(bmp);		
						
						//Если картинка не загрузилась, то читаем ее снова
						if (bmp == null)
						{
							download(url, iv);
							return;
						}
											
						downloadLastElementFromOrder();						
					}
				};
				
				Thread thread = new Thread()
				{
					private String 	mCacheDir = null;
					private URL 	mUrl = null;	
					
					@Override
					public void run() 
					{
						super.run();
						mThreads ++;
						
						Bitmap result = null;
						
						//если запрос есть в списке, то ждем пока картинка запишется в кеш памяти
						if (mRequest.contains(url))
						{
							while (mRequest.contains(url));  
							if ((result = getBitmapFromMemory(url)) != null)
							{
								finishDownload(url, result);
								return;
							}								
						}
						else
						{
							mRequest.add(url);	//Если ссылки в списке запросов нет, значит добавляем
						}
						
						//Получаем путь кеширования
						synchronized (mIdb) 
						{
							mCacheDir = mIdb.getImageLink(url);	
						}
						
						if (mCacheDir == null)
						{
							//Записи в БД нет
							//Создаем URL из строки			
							try 
							{
								mUrl = new URL(url);
							}
							catch (MalformedURLException e) 
							{
								Log.e("ImageDownloader", "Invalid URL: " + url);
								finishDownload(url, null);
								return;
							}
							//создаем путь для кеширования
							String pkg[] = mContext.getPackageName().split("\\.");
							mCacheDir = String.format("%s%s%s", mMainCacheDir, pkg[pkg.length -1], mUrl.getFile());
							
							result = loadDrawableAndCaching(mUrl, mCacheDir);
									
							//Удалось загрузить и кешировать?
							if (result != null && mCacheDir != null)
							{
								//АГА
								//Вставляем картинку в БД
								synchronized (mIdb) 
								{
									mIdb.insertImage(url, mCacheDir);
								}
							}
							else
								result = loadDrawableFromInternet(mUrl);	//Не получилось кешировать, грузим из инета
						}
						else
						{
							//Запись в БД есть, грузим с флешки
							result = loadDrawableFromCache(mCacheDir);
							//Если картинка не загрузилась, то надо удалить запись из БД и запустить закачку заново
							if (result == null)
							{
								synchronized (mIdb) 
								{
									mIdb.deleteRecord(url);
								}
								finishDownload(url, null);
								if (D)
									Log.w("ImageDownloader:download", "Record deleted from database");
								hlr.sendEmptyMessage(0);
								return;
							}
						}
						
						finishDownload(url, result);
					}
					
					/**
					 * Создает сообщение с картинкой и передает его Handler
					 * @param bmp
					 */
					private void finishDownload(String url, Bitmap bmp)
					{						
						//Уменьшаем счетчик потоков и удаляем загрузку из списка
						mThreads --;
						synchronized (mRequest) 
						{
							mRequest.remove(url);
						}
						//Если есть что устанавливать в список - ставим
						if (bmp != null)
						{		
							
							//Закругляем края усли нужно
							if (mRoundness > 0)
								bmp = getRoundedCornerBitmap(bmp, mRoundness);
							
							//Если картинка загружена, то помещаем ее в кеш памяти
							putImageToMemoryCache(url, bmp);	
							
							Message msg = new Message();
							msg.obj = bmp;
							hlr.sendMessage(msg);
						}						
					}
				};		
				thread.setPriority(3);
				thread.setName("ImageDownloader");
				thread.start();
			}
			//Log.d("IDLDR", String.format("Images: THDS[%d] MC[%d] ORDR[%d] RQST[%d] ", mThreads, mBitmapsHashMap.size(), mOrder.size(), mRequest.size()));
		}
	}	
	/**
	 *  Загружает последний элемент из очереди
	 */
	private void downloadLastElementFromOrder() 
	{
		synchronized (mOrder) 
		{
			if (!mOrder.isEmpty())
			{
				//Очередь не пуста, значит берем последний добавленный элемент и грузим его
				OrderElement oe = mOrder.remove(mOrder.size() - 1);
				//Рекурсивный вызов
				download(oe.getUrl(), oe.getImageView());
			}
		}
	};
	/**
	 * Загружает картинку из интернета
	 * @param src - источник загрузки
	 * @return загруженная картинка
	 */
	private Bitmap loadDrawableFromInternet(URL src) 
	{
		Bitmap result = null;
		InputStream is = null;
		try
		{
			//Открываем поток для чтения
			is = src.openStream();
			result = BitmapFactory.decodeStream(is);
			is.close();				
		}
		catch (OutOfMemoryError e)
		{
			clearMemoryCache();
			return null;//loadDrawableFromInternet(src);
		}
		catch (Exception e)
		{
			if (D)
				Log.e("ImageDownloader", "Error in loading image from internet");
			return null;
		}
		return result;
	}
	
	/**
	 * Загружает картинку из интернета и кеширует ее
	 * @param src - источник
	 * @param dst - путь кеширования
	 * @return загруженная картинка
	 */
	private Bitmap loadDrawableAndCaching(URL src, String dst) 
	{
		try
		{
			//Перегоняем поток
			File dirs = new File(getDirFromPath(dst));
			if (!dirs.exists())
				dirs.mkdirs();
			OutputStream fos = new FileOutputStream(dst, false);
										
			//Поток открыт значит можно сохранять				
			//Пытаемся прочитать картинку из интернет
			InputStream isr = src.openStream();
								//Копирование потока
			int count;
			byte[] bytes = new byte[1024];  
	        do
	        {
	        	count = isr.read(bytes, 0, 1024);
	            if(count == -1)
	            	break;
	            fos.write(bytes, 0, count);	              
	        }
	        while (true);
	            
	        fos.close();
	        isr.close();
	        
			return loadDrawableFromCache(dst);
		}
		catch (FileNotFoundException e)
		{
			//Поток для записи не получилось открыть, значит просто грузим картинку из инета
			if (D)
				Log.e("ImageDownloader", "File not found! " + e.getMessage());
		}
		catch (IOException e)
		{
			//Не получилось открыть поток из Интернета. возможно нет сети
			if (D)
				Log.e("ImageDownloader", "Error in loading image and caching");
		}
		catch (OutOfMemoryError e)
		{
			if (D)
				Log.e("ImageDownloader", "Out of memory. Memory cache erased");
			clearMemoryCache();		
			return null;//loadDrawableAndCaching(src, dst);
		}
		catch (NullPointerException e)
		{
			if (D)
				Log.e("ImageDownloader", "FIX slashes");
		}
		return null;
	}
	
	/**
	 * Загружает картинку из кеша
	 * @param src - адрес загрузки
	 * @return загруженная картинка
	 */
	private Bitmap loadDrawableFromCache(String src)
	{
		try
		{
			Bitmap result =  BitmapFactory.decodeStream(new FileInputStream(src), null, null);
			return result;
		}
		catch (IOException e)
		{
			if (D)
				Log.e("ImageDownloader:loadDrawableFromCache", "IO Error!");
			return null;
		}
		catch (OutOfMemoryError e)
		{
			if (D)
				Log.e("ImageDownloader", "Out of memory. Memory cache erased");
			clearMemoryCache();	
			return null;//loadDrawableFromCache(src);
		}					
	}
	
	/**
	 * Помещает картинку в кеш памяти
	 * @param url - адрес источника
	 * @param bitmap - картинка
	 */
	private void putImageToMemoryCache(String url, Bitmap bitmap)
	{
		if (bitmap != null)
		{
			long square = 0;
			//Вычисляем площадь картинки и проверяем ограничение
			square = bitmap.getWidth() * bitmap.getHeight();
			//Проверяем есть ли ограничение по площади
			if (mSquareLimit > 0 && square > mSquareLimit)
				return;
			
			synchronized (mBitmapsHashMap) 
			{
				mBitmapsHashMap.put(url, bitmap);
			}
			if (((mCurrCacheSize += square / 1000) > mACCache) && (mACCache > 0))
				clearMemoryCache();			
		}
	}
	/**
	 * Вырезает из полного пути к файлу каталог
	 * @param path - путь к файлу
	 * @return Если каталог найден, то путь, иначе null
	 */
	private String getDirFromPath(String path)
	{
		//Шаблон поиска. Первая группа содержит каталог
		Pattern pat = Pattern.compile("(.+/).+?\\.....?");
		Matcher mat = pat.matcher(path);
		if(mat.find())
			return mat.group(1);			
		return null;
	}	
	/**
	 * Возвращает скругленную картинку от исходной
	 * @param bitmap
	 * @param pixels
	 * @return
	 */
	public static Bitmap getRoundedCornerBitmap(Bitmap bitmap, int pixels) 
	{
		try
		{
	        Bitmap output = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), Config.ARGB_8888);
	        Canvas canvas = new Canvas(output);
	
	        final int color = 0xff424242;
	        final Paint paint = new Paint();
	        final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
	        final RectF rectF = new RectF(rect);
	        final float roundPx = pixels;
	
	        paint.setAntiAlias(true);
	        canvas.drawARGB(0, 0, 0, 0);
	        paint.setColor(color);
	        canvas.drawRoundRect(rectF, roundPx, roundPx, paint);
	
	        paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));
	        canvas.drawBitmap(bitmap, rect, rect, paint);
	        
	        return output;
		}
		catch (OutOfMemoryError e)
		{
			clearMemoryCache();
			if (D)
				Log.e("ImageDownloader", "Out of memory. Memory cache erased");
			//getRoundedCornerBitmap(bitmap, pixels);
		}
        return null;
    }
	/**
	 * Описывает элемент очереди загрузки
	 * @author ideast
	 *
	 */
	private class OrderElement
	{
		private String mUrl;
		private ImageView mImageView;
		
		public OrderElement(String url, ImageView iv)
		{
			mUrl = url;
			mImageView = iv;
		}
		
		public String getUrl()
		{
			return mUrl;
		}
		
		public ImageView getImageView()
		{
			return mImageView;
		}
	}
}