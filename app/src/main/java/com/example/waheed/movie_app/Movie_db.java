package com.example.waheed.movie_app;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by waheed on 4/16/2016.
 */
public class Movie_db extends SQLiteOpenHelper {
    static String db_name="Movie_db";
    SQLiteDatabase db;
    public Movie_db(Context context) {
        super(context, db_name, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table favorites(id int primary key,poster text,title text,overview text,vote_average int,releasedate text,movie_id int)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("Drop table if exists favorites");
        onCreate(db);
    }
    public void add_movie(MovieItems item)
    {
        ContentValues content=new ContentValues();
        content.put("poster",item.poster);
        content.put("title",item.title);
        content.put("overview",item.overview);
        content.put("vote_average",item.vote_average);
        content.put("releasedate",item.releasedate);
        content.put("movie_id",item.id);
        db=getWritableDatabase();
        db.insert("favorites", null, content);
        db.close();

    }
    public Cursor Fetch_all()
    {
        db=getReadableDatabase();
        Cursor cursor=db.rawQuery("select * from favorites",null);
        if(cursor.getCount()!=0)
        {
            cursor.moveToFirst();

        }
        else
        {
            cursor=null;
        }
        db.close();
        return cursor;
    }
    public void delete_movie(String Movie_title)
    {
        db=getWritableDatabase();
        db.delete("favorites","title like ?",new String[]{Movie_title});
        db.close();

    }
    public Boolean ifexist(String Movie_title)
    {
        db=getReadableDatabase();
        Cursor cursor=db.rawQuery("select * from favorites where title=?", new String[]{Movie_title});
        if(cursor.getCount()!=0) {
            db.close();
            return true;
        }
        return false;
    }
}
