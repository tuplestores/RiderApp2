package com.tuplestores.riderapp;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.LinearLayout;

public class RiderSplashActivity extends AppCompatActivity {


    LinearLayout linlay_touchbar;
    Activity thisActivity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rider_splash);
        thisActivity = this;
    }


    private void initialize(){

        linlay_touchbar = (LinearLayout)findViewById(R.id.linlay_touchbar);

        linlay_touchbar.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                Intent ii = new Intent(thisActivity,SplashTwoActivity.class);
                startActivity(ii);
                finish();
                return true;
            }
        });
    }


}
