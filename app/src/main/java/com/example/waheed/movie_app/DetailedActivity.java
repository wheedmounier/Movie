package com.example.waheed.movie_app;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class DetailedActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detailed);
        Bundle data=getIntent().getExtras();
        if(null==savedInstanceState) {
            Detailed_Fragment details = new Detailed_Fragment();
            details.setArguments(data);
            getSupportFragmentManager().beginTransaction().add(R.id.panel_two, details).commit();
        }
    }
}
