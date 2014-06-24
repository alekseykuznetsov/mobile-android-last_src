package ru.enter.ImageManager;

import java.util.ArrayList;

import ru.enter.utils.Log;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteStatement;

public class ImagesDataBase 
{
	public static String DATABASE_NAME = "images.db";
	private static final int DATABASE_VERSION = 1;	  
	private static final String TABLE_NAME = "IMAGES";
	
	private Context mContext;
	private SQLiteDatabase mDataBase;	
	
	private SQLiteStatement insertImage;
	
	private static ImagesDataBase idb;
	
	//private DataStorage mDataStorage;
	
	public static ImagesDataBase getInstance(Context context)
	{
		if(idb ==null)
		{
			synchronized (ImagesDataBase.class)
			{
				idb = new ImagesDataBase(context);
			}
		}
		return idb;
	}
	
	private ImagesDataBase(Context context)
	{
		mContext = context;
		//mDataStorage = new DataStorage();
		OpenHelper openHelper = new OpenHelper(mContext); 
		mDataBase = openHelper.getWritableDatabase();
		
		insertImage = mDataBase.compileStatement("INSERT INTO " + TABLE_NAME + " (url,cache) VALUES (?,?)");		
	}
	
	public void close()
	{
		mDataBase.close();
	}
	
	public boolean isClosed()
	{
		return !mDataBase.isOpen();
	}
	/**
	 * Добавляет информацию о картинке в БД
	 * @param url - Интернет адрес
	 * @param cache - Локальный адрес
	 * @return true - если картинка добавлена успешно
	 */
	public boolean insertImage(String url, String cache)
	{
		try
		{
			insertImage.bindString (1, url);
			insertImage.bindString (2, cache);
			insertImage.executeInsert();
		}
		catch (SQLException e)
		{
			//Log.e("ImagesDataBase:insertImage", e.getMessage());
			return false;
		}
		return true;		
	}
	/**
	 * Возвращает ссылку на кешированную картинку
	 * @param url - Интернет адрес картинки
	 * @return Локальный адрес картинки или null если запись не нашлась
	 */
	public String getImageLink(String url)
	{
		String result = null;
		String sqlQuery = String.format("SELECT cache FROM %s WHERE url=\"%s\"", TABLE_NAME, url);
		try
		{
			Cursor cursor = mDataBase.rawQuery(sqlQuery, null);
			if (cursor.moveToFirst())
			{
				
				//Запись нашлась
				result = cursor.getString(0);
			}
			else
				result = null;
			
			if (!cursor.isClosed())
				cursor.close();
		}
		catch (SQLException e)
		{
			Log.e("ImagesDataBase:getImage", e.getMessage());
			return null;
		}
		return result;		
	}
	/**
	 * @return Возвращает список путей кеширования всех записей 
	 */
	public ArrayList<String> getLinks()
	{
		ArrayList<String> result = new ArrayList<String>();
		String sqlQuery = String.format("SELECT cache FROM %s", TABLE_NAME);
		try
		{
			Cursor cursor = mDataBase.rawQuery(sqlQuery, null);
			if (cursor.moveToFirst())
			{
				do
				{
					//Запись нашлась
					result.add(cursor.getString(0));
				}
				while (cursor.moveToNext());
			}
			
			if (!cursor.isClosed())
				cursor.close();
		}
		catch (SQLException e)
		{
			Log.e("ImagesDataBase:getImage", e.getMessage());
			return null;
		}
		return result;
	}
	/**
	 * Очищает базу данных полностью
	 */
	public void clearDatabase()
	{
		String sqlQuery = String.format("DELETE FROM %s", TABLE_NAME);
		try
		{
			mDataBase.execSQL(sqlQuery);
		}
		catch (SQLException e)
		{
			Log.e("ImagesDataBase:clearDatabase", e.getMessage());
		}
	}
	/**
	 * Удаляет url из базы данных
	 * @param url
	 */
	public void deleteRecord(String url)
	{
		String sqlQuery = String.format("DELETE FROM %s WHERE url=\"%s\"", TABLE_NAME, url);
		try
		{
			mDataBase.execSQL(sqlQuery);
		}
		catch (SQLException e)
		{
			Log.e("ImagesDataBase:deleteRecord", e.getMessage());
		}
	}
	
	private class OpenHelper extends SQLiteOpenHelper 
	{
		OpenHelper(Context context) 
	    {
			super(context, DATABASE_NAME, null, DATABASE_VERSION);
	    }

	    @Override
	    public void onCreate(SQLiteDatabase db) 
	    {
	    	//таблица с главными категориями
	    	db.execSQL("CREATE TABLE " + TABLE_NAME + " (url TEXT PRIMARY KEY, cache TEXT)");
	    }

	    @Override
	    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) 
	    {
	    	Log.w("Images Data Base", "Upgrading database, this will drop tables and recreate.");
	    }
	}
}