package com.example.waheed.movie_app;

import android.content.Context;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

/**
 * Created by waheed on 25-Mar-16.
 */
public class ImageAdapter extends BaseAdapter {
    private  Context c;
    //private Integer[] ids = {R.drawable.download,R.drawable.grid,R.drawable.grido,R.drawable.m};
    MovieItems []movies=null;
    ImageAdapter(Context c,MovieItems[] movies)
    {
        this.c=c;
        this.movies=movies;
    }

    public void clear() {
       movies=null;

    }
    @Override
    public int getCount() {
        if (movies==null)
        return 0;
        else
            return movies.length;
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        ImageView image = null;
        if (convertView == null) {
            LayoutInflater g_item = (LayoutInflater) this.c
                   .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView=new View(this.c);
            convertView =g_item.inflate(R.layout.grid_item,null);
            image=(ImageView)convertView.findViewById(R.id.iv_grid_item);
        } else {
            image=(ImageView)convertView.findViewById(R.id.iv_grid_item);

        }
        Picasso.with(convertView.getContext()).load("https://image.tmdb.org/t/p/w300"+movies[position].poster).into(image);

        return convertView;
    }

}
