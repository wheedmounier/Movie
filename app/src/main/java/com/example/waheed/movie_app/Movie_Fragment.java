package com.example.waheed.movie_app;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;


/**
 * A simple {@link Fragment} subclass.
 */
public class Movie_Fragment extends Fragment implements SharedPreferences.OnSharedPreferenceChangeListener {
    GridView grid_movie;
    ImageAdapter adapter;
    MovieItems [] data;
    String sort_type="popular";
    NameListener moviedetails;

    public Movie_Fragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View root= inflater.inflate(R.layout.fragment_movie_, container, false);
        setHasOptionsMenu(true);
        LoadPreferences();
        grid_movie=(GridView)root.findViewById(R.id.gv_movies);
        new FetchMovies().execute(sort_type);
        grid_movie.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //Intent i = new Intent(getContext(),DetailedActivity.class);
                Bundle bundle=new Bundle();
                bundle.putString("title", data[position].title);
                bundle.putString("poster", data[position].poster);
                bundle.putString("overview", data[position].overview);
                bundle.putFloat("vote_average", data[position].vote_average);
                bundle.putString("releasedate", data[position].releasedate);
                bundle.putInt("id", data[position].id);
               // i.putExtras(bundle);
               // startActivity(i);
                moviedetails.setmoviedata(bundle);
            }
        });
        return  root;
    }

    public void setdata(NameListener namelistner)
    {
        moviedetails=namelistner;
    }




    public class FetchMovies extends AsyncTask<String , Void , MovieItems []> {

        public MovieItems[] getmoviedatafromjson(String jsonob) throws JSONException {
             String results ="results";
             String title = "title";
             String overview = "overview";
             String vote_average = "vote_average";
             String poster_path = "poster_path";
             String release_date = "release_date";
             String id="id";

            JSONObject moviejson = new JSONObject(jsonob);
            JSONArray moviearray = moviejson.getJSONArray(results);

            MovieItems[] result= new MovieItems[moviearray.length()];
            for(int j=0;j<moviearray.length();j++){
                result[j] = new MovieItems();
            }
            for(int i = 0; i < moviearray.length(); i++) {
                JSONObject movie = moviearray.getJSONObject(i);
                result[i].poster=movie.getString(poster_path);
                result[i].title=movie.getString(title);
                result[i].overview=movie.getString(overview);
                result[i].vote_average=movie.getInt(vote_average);
                result[i].releasedate =movie.getString(release_date);
                result[i].id=movie.getInt(id);

            }
            return result;
        }

        @Override
        public MovieItems[] doInBackground(String...Params) {

            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;
            String JsonStr = null;
            try {


                URL url=new URL("http://api.themoviedb.org/3/movie/"+Params[0]+"?api_key=11cfc2ad10f26cbd932760da40aabce8");
                Log.v("url",String.valueOf(url));
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();


                InputStream inputStream = urlConnection.getInputStream();
                StringBuffer buffer = new StringBuffer();
                if (inputStream == null) {
                    return null;
                }
                reader = new BufferedReader(new InputStreamReader(inputStream));
                String line;
                while ((line = reader.readLine()) != null) {
                    buffer.append(line + "\n");
                }

                if (buffer.length() == 0) {
                    return null;
                }
                JsonStr = buffer.toString();

            } catch (IOException e) {
                return null;
            } finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (final IOException e) {

                    }
                }
            }

            try {
                return getmoviedatafromjson(JsonStr);
            } catch (JSONException e) {

                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(MovieItems[] movieItemses) {
            super.onPostExecute(movieItemses);
            data=new MovieItems[movieItemses.length];
            for (int i=0;i<data.length;i++) {
                data[i]=new MovieItems();
            }
            adapter=new ImageAdapter(getActivity(),movieItemses);
            grid_movie.setAdapter(adapter);
            data=movieItemses;

        }
    }

    public void LoadPreferences()
    {
        SharedPreferences settings= PreferenceManager.getDefaultSharedPreferences(getContext());
        sort_type=settings.getString("list_settings","popular");
        settings.registerOnSharedPreferenceChangeListener(Movie_Fragment.this);
    }
    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
    LoadPreferences();
    new FetchMovies().execute(sort_type);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu,menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId()==R.id.settings)
        {
            Intent i=new Intent(getContext(),SettingsActivity.class);
            startActivity(i);
        }
        if(item.getItemId()==R.id.favorites)
        {
            Movie_db db=new Movie_db(getContext());
            Cursor cursor=db.Fetch_all();
            if(cursor!=null)
            {
                MovieItems temp[]=new MovieItems[cursor.getCount()];
                for (int i=0;i<temp.length;i++)
                {
                    temp[i]=new MovieItems();
                    temp[i].poster=cursor.getString(1);
                    temp[i].title=cursor.getString(2);
                    temp[i].overview=cursor.getString(3);
                    temp[i].vote_average=Float.parseFloat(cursor.getString(4));
                    temp[i].releasedate=cursor.getString(5);
                    temp[i].id=Integer.parseInt(cursor.getString(6));
                    cursor.moveToNext();
                }
                 adapter.clear();
                //solving favorites error
                 data=temp;
                 adapter=new ImageAdapter(getActivity(),temp);

                grid_movie.setAdapter(adapter);
            }
            else
            {
                Toast.makeText(getContext(),"No Favorites",Toast.LENGTH_SHORT).show();
            }
        }
        return super.onOptionsItemSelected(item);
    }
}
