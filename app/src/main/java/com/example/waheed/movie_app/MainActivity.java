package com.example.waheed.movie_app;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.os.PersistableBundle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.webkit.WebView;
import android.widget.FrameLayout;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity implements NameListener {
    boolean twopane;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        android.net.NetworkInfo wifi = cm
                .getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        android.net.NetworkInfo datac = cm
                .getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
        if ((wifi != null & datac != null) && (wifi.isConnected() || datac.isConnected())) {


            setContentView(R.layout.activity_main);
            FrameLayout panel_two=(FrameLayout)findViewById(R.id.panel_two);
            if(null==panel_two)
            {
                twopane=false;
            }
            else
            {
                twopane=true;
            }
            if(null==savedInstanceState) {
                Movie_Fragment moviefragment=new Movie_Fragment();
                moviefragment.setdata(this);
                getSupportFragmentManager().beginTransaction()
                        .add(R.id.panel_one,moviefragment)
                        .commit();
            }

        } else
        {
            //no connection
            Toast toast = Toast.makeText(MainActivity.this, "No Internet Connection",Toast.LENGTH_LONG);
            toast.show();
        }

    }
    public void setmoviedata(Bundle item)
    {
        if(twopane) {
            Detailed_Fragment detailsfragment=new Detailed_Fragment();
            Log.e("saad", item.toString());
            detailsfragment.setArguments(item);
            getSupportFragmentManager().beginTransaction().replace(R.id.panel_two, detailsfragment).commit();
        }
        else
        {
            Intent i = new Intent(getApplicationContext(), DetailedActivity.class);
            i.putExtras(item);
            startActivity(i);
        }

    }
}
