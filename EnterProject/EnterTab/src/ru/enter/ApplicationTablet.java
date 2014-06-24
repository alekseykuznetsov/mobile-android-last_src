package ru.enter;


import ru.enter.utils.ApplicationEnter;
import ru.enter.utils.StatisticsInfoSender;
import android.annotation.TargetApi;
import android.app.ActivityManager;
import android.app.Application;
import android.content.Context;
import android.util.Log;

import com.webimageloader.ImageLoader;
import com.webimageloader.ImageLoader.Logger;
import com.webimageloader.loader.MemoryCache;

public class ApplicationTablet extends ApplicationEnter{
	
	private static final String ENTER_TABLET_CLIENT_ID = "AndroidTablet";
//	private static final String ENTER_TABLET_CLIENT_ID = "Megafon";
	private static final String TAG = "ImageLoaderApplication";
    public static final String IMAGE_LOADER_SERVICE = "image_loader";
    private static final String CACHE_FOLDER_NAME = "images";
    private static final int MEMORY_DIVIDER = 8;
    private static final int DISK_CACHE_SIZE = 100 * 1024 * 1024;
    
    private ImageLoader imageLoader;

    @Override
    public void onCreate() {
        super.onCreate();
        imageLoader = getBuilder().build();
        setAppClientId(ENTER_TABLET_CLIENT_ID);
        StatisticsInfoSender.send();
    }
    
    @Override
    public Object getSystemService(String name) {
        if (IMAGE_LOADER_SERVICE.equals(name)) {
            return imageLoader;
        } else {
            return super.getSystemService(name);
        }
    }

    @Override
    public void onTerminate() {
        super.onTerminate();

        imageLoader.destroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();

        MemoryCache memoryCache = imageLoader.getMemoryCache();
        if (memoryCache != null) {
            if (Logger.DEBUG) Log.d(TAG, "onLowMemory() called, eviciting all bitmaps");
            memoryCache.evictAll();
        }
    }

    @Override
    @TargetApi(14)
    public void onTrimMemory(int level) {
        super.onTrimMemory(level);

        MemoryCache memoryCache = imageLoader.getMemoryCache();
        if (memoryCache == null) {
            return;
        }

        if (level >= TRIM_MEMORY_MODERATE) {
            // Nearing middle of list of cached background apps
            // Evict our entire bitmap cache
            if (Logger.DEBUG) Log.d(TAG, "onTrimMemory(), level>=TRIM_MEMORY_MODERATE called, eviciting all bitmaps");
            memoryCache.evictAll();
        } else if (level >= TRIM_MEMORY_BACKGROUND) {
            // Entering list of cached background apps
            // Evict oldest half of our bitmap cache
            if (Logger.DEBUG) Log.d(TAG, "onTrimMemory(), level>=TRIM_MEMORY_BACKGROUND called, evicing half of all bitmaps");
            memoryCache.trimToSize(memoryCache.size() / 2);
        }
    }

    /**
     * Get the loader provided by this {@link Application}
     *
     * @param context the current context
     * @return an {@link ImageLoader}
     */
    public static ImageLoader getLoader(Context context) {
        ImageLoader loader = (ImageLoader) context.getApplicationContext().getSystemService(IMAGE_LOADER_SERVICE);
        if (loader == null) {
            throw new IllegalStateException("ImageLoaderApplication not set as application class in" +
                    "AndroidManifest.xml");
        } else {
            return loader;
        }
    }

    /**
     * Get folder name to use for the disk cache, by default "images"
     *
     * @return the folder name
     */
    protected String getCacheFolderName() {
        return CACHE_FOLDER_NAME;
    }

    /**
     * Get the size of the disk cache, by default 10Mb
     *
     * @return the disk cache size
     */
    protected int getDiskCacheSize() {
        return DISK_CACHE_SIZE;
    }

    /**
     * The divider of the total memory to use for memory cache
     *
     * @return the memory divider
     */
    protected int getMemoryDivider() {
        return MEMORY_DIVIDER;
    }

    /**
     * The builder used to construct the {@link ImageLoader} used by this {@link Application}.
     *
     * @return the builder
     */
    protected ImageLoader.NewBuilder getBuilder() {
        // Get memory class of this device, exceeding this amount will throw an
        // OutOfMemory exception.
        ActivityManager am = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        int memClass = am.getMemoryClass();

        // Use part of the available memory for memory cache.
        final int memoryCacheSize = 1024 * 1024 * memClass / getMemoryDivider();

        int diskCache = getDiskCacheSize();
        if (Logger.DEBUG) Log.d(TAG, "Using memory cache of size: " + humanReadableByteCount(memoryCacheSize, false));
        if (Logger.DEBUG) Log.d(TAG, "Using disk cache of size: " + humanReadableByteCount(diskCache, false));

        //File cacheDir = IOUtil.getDiskCacheDir(this, getCacheFolderName());
        
        return new ImageLoader.NewBuilder(this).enableDiskCache(getCacheFolderName(), diskCache).enableMemoryCache(memoryCacheSize);
        
       // return new ImageLoader.Builder(this)
                //.enableDiskCache(getCacheFolderName(), diskCache)
                //.enableMemoryCache(memoryCacheSize);
    }

    // http://stackoverflow.com/a/3758880/253583
    private static String humanReadableByteCount(long bytes, boolean si) {
        int unit = si ? 1000 : 1024;
        if (bytes < unit) return bytes + " B";
        int exp = (int) (Math.log(bytes) / Math.log(unit));
        String pre = (si ? "kMGTPE" : "KMGTPE").charAt(exp-1) + (si ? "" : "i");
        return String.format("%.1f %sB", bytes / Math.pow(unit, exp), pre);
    }
}
