package com.example.waheed.movie_app;


import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 */
public class Detailed_Fragment extends Fragment  {

    ImageView poster;
    CheckBox favorite;
    TextView title, overview, release_date;
    MovieItems detailed_movie = new MovieItems();
    Movie_db db;
    String[] trailers={""};
    String[] reviews = {""};
    String trailer_link = "https://www.youtube.com/watch?v=";
    ListView trailer_list, reviews_list;
    String[] Trailers_Temp;
    private boolean firstcall = true;
    TextView t;
    Spinner sp;
    List<String> lt = new ArrayList<String>();
    ArrayAdapter<String> dataAdapter;
    Button b;
    String review = "";
    Bundle bundle;
    public Detailed_Fragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        bundle = getArguments();
        detailed_movie.title = bundle.getString("title");
        detailed_movie.poster = bundle.getString("poster");
        detailed_movie.overview = bundle.getString("overview");
        detailed_movie.vote_average = bundle.getFloat("vote_average");
        detailed_movie.releasedate = bundle.getString("releasedate");
        detailed_movie.id = bundle.getInt("id");
        if(savedInstanceState==null)
        {
            new Review().execute(String.valueOf(detailed_movie.id));
            new Trailer().execute(String.valueOf(detailed_movie.id));
        }
        else
        {
            review=savedInstanceState.getString("review");
            trailers=savedInstanceState.getStringArray("trailers");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View root = inflater.inflate(R.layout.fragment_detailed_, container, false);
        db = new Movie_db(getContext());


        //new Trailer().execute(String.valueOf(detailed_movie.id));
        //new Review().execute(String.valueOf(detailed_movie.id));

        t = (TextView) root.findViewById(R.id.new_reviews);
        trailer_list = (ListView) root.findViewById(R.id.list_details);
        t.setText(review);

        LayoutInflater inflate = getActivity().getLayoutInflater();
        View header = inflate.inflate(R.layout.list_header_detail, trailer_list, false);
        trailer_list.addHeaderView(header);

        poster = (ImageView) trailer_list.findViewById(R.id.iv_poster);
        title = (TextView) trailer_list.findViewById(R.id.tv_title);
        overview = (TextView) trailer_list.findViewById(R.id.tv_overview);
        release_date = (TextView) trailer_list.findViewById(R.id.tv_date);
        favorite = (CheckBox) trailer_list.findViewById(R.id.cb_favorite);
        b=(Button)root.findViewById(R.id.sp_trailers);
        Picasso.with(root.getContext()).load("https://image.tmdb.org/t/p/w300" + detailed_movie.poster).into(poster);
        title.setText(detailed_movie.title);
        release_date.setText(detailed_movie.releasedate);
        overview.setText(detailed_movie.overview);

        if (db.ifexist(detailed_movie.title)) {
            favorite.setChecked(true);
        } else {
            favorite.setChecked(false);
        }
        favorite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean checked = ((CheckBox) v).isChecked();
                if (checked) {
                    db.add_movie(detailed_movie);
                } else {
                    db.delete_movie(detailed_movie.title);
                }
            }
        });


        ArrayAdapter<String> trailer_adapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_expandable_list_item_1);
        trailer_list.setAdapter(trailer_adapter);

        if (trailers.length > 0) {
            for (int i = 0; i < trailers.length; i++) {
                lt.add("Play - Trailer " + (i + 1));
            }
        } else {
            lt.add("No Trailers");
        }

        final ArrayAdapter<String> ad=new ArrayAdapter<String>(getContext(),android.R.layout.simple_expandable_list_item_1,lt);
        b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AlertDialog.Builder(getActivity())
                        .setAdapter(ad, new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                //TODO - Code when list item is clicked (int which - is param that gives you the index of clicked item)
                                String temp = lt.get(which);
                                if (!temp.equals("No Trailers")) {
                                    String Link = trailer_link + trailers[which];
                                    Log.v("link", Link);
                                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(Link)));
                                }
                            }
                        })
                        .setTitle("Trailers")
                        .setCancelable(true)
                        .show();
            }
        });
        return root;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString("review", review);
        outState.putStringArray("trailers",trailers);
    }


    public class Trailer extends AsyncTask<String, String[], String[]> {
        public String[] get_trailers(String json) throws JSONException {
            String results = "results";
            String id = "key";

            JSONObject moviejson = new JSONObject(json);
            JSONArray moviearray = moviejson.getJSONArray(results);

            String[] result = new String[moviearray.length()];
            for (int i = 0; i < moviearray.length(); i++) {
                JSONObject movie = moviearray.getJSONObject(i);
                result[i] = movie.getString(id);
            }
            return result;
        }

        @Override
        protected String[] doInBackground(String... params) {
            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;
            String JsonStr = null;
            try {


                URL url = new URL("http://api.themoviedb.org/3/movie/" + params[0] + "/videos?api_key=11cfc2ad10f26cbd932760da40aabce8");
                Log.d("url", String.valueOf(url));
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
                return get_trailers(JsonStr);
            } catch (JSONException e) {

                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String[] strings) {
            super.onPostExecute(strings);
            if (strings.length > 0 && strings != null) {
                trailers = new String[strings.length];

                trailers = strings;


            } else {
                trailers = new String[1];
                trailers[0] = "No Trailers";

            }
            /*
            if (trailers.length > 0) {
                for (int i = 0; i < trailers.length; i++) {
                    lt.add("Play - Trailer " + (i + 1));
                }
            } else {
                lt.add("No Trailers");
            }*/

        }
    }

    public class Review extends AsyncTask<String, Void, String[]> {
        public String[] get_review(String json) throws JSONException {
            String results = "results";
            String id = "content";

            JSONObject moviejson = new JSONObject(json);
            JSONArray moviearray = moviejson.getJSONArray(results);

            String[] result = new String[moviearray.length()];
            for (int i = 0; i < moviearray.length(); i++) {
                JSONObject movie = moviearray.getJSONObject(i);
                result[i] = movie.getString(id);
            }
            return result;
        }

        @Override
        protected String[] doInBackground(String... params) {
            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;
            String JsonStr = null;
            try {


                URL url = new URL("http://api.themoviedb.org/3/movie/" + params[0] + "/reviews?api_key=11cfc2ad10f26cbd932760da40aabce8");
                Log.v("url", String.valueOf(url));
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
                return get_review(JsonStr);
            } catch (JSONException e) {

                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String[] strings) {
            super.onPostExecute(strings);
            if (strings.length > 0 && strings != null) {
                reviews = new String[strings.length];
                reviews = strings;
                for (int i = 0; i < strings.length; i++) {
                    review += strings[i] + "\n";
                }
            } else {
                review="No Reviews";
            }
            //t.setText(review);
        }
    }

}





