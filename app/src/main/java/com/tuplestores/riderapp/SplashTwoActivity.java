package com.tuplestores.riderapp;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

public class SplashTwoActivity extends AppCompatActivity {


   EditText edtMobileNumber;
   ProgressBar pgBar;
   Activity thisActivity;
   Button btnNext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_two);
        initialize();



    }

    private void initialize(){

        thisActivity = this;

        edtMobileNumber = (EditText)findViewById(R.id.edtMobileNumber);
        pgBar = (ProgressBar)findViewById(R.id.pgBar);
        btnNext =(Button)findViewById(R.id.btnNext);

        showHidePgBar(false);

        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(edtMobileNumber.getText().toString().equals("") || edtMobileNumber.getText()==null){

                    ShowAlert(thisActivity,getResources().getString(R.string.pls_input_mobile));
                }
                else{

                    showHidePgBar(true);
                    //Logic for mobile verification
                }
            }
        });

    }

    private void  showHidePgBar(boolean toShow){

        if(toShow){

            pgBar.setVisibility(View.VISIBLE);
        }
        else{
            pgBar.setVisibility(View.GONE);
        }
    }


    private void ShowAlert(Context ctx,String msg){

        androidx.appcompat.app.AlertDialog.Builder dlg = null;

        dlg =   new AlertDialog.Builder(ctx,R.style.AlertDialogTheme)
                .setTitle("")
                .setMessage(msg)

                // Specifying a listener allows you to take an action before dismissing the dialog.
                // The dialog is automatically dismissed when a dialog button is clicked.
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {

                        edtMobileNumber.requestFocus();
                    }
                });


        if(dlg!=null){

            dlg.show();
        }
    }//ShowAlert



}
