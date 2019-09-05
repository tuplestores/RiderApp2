package com.tuplestores.riderapp;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.tuplestores.riderapp.api.ApiClient;
import com.tuplestores.riderapp.api.ApiInterface;
import com.tuplestores.riderapp.model.ApiResponse;
import com.tuplestores.riderapp.utils.ISDUtils;
import com.tuplestores.riderapp.utils.UtilityFunctions;

public class SplashTwoActivity extends AppCompatActivity {


   EditText edtMobileNumber;
   TextView txtIsd;
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
        txtIsd = (TextView)findViewById(R.id.txtIsdCode) ;

        showHidePgBar(false);

        TelephonyManager tm = (TelephonyManager) this.getSystemService(Context.TELEPHONY_SERVICE);
        String countryCodeValue = tm.getNetworkCountryIso();


        txtIsd.setText(ISDUtils.getISDCode(countryCodeValue));

        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(edtMobileNumber.getText().toString().equals("") || edtMobileNumber.getText()==null){

                    ShowAlert(thisActivity,getResources().getString(R.string.pls_input_mobile),"MOB");
                }
                else{

                   generateOTP(UtilityFunctions.tenant_id,txtIsd.getText().toString(),
                                edtMobileNumber.getText().toString());
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


    private void ShowAlert(Context ctx, String msg, final String focus){

        androidx.appcompat.app.AlertDialog.Builder dlg = null;

        dlg =   new AlertDialog.Builder(ctx,R.style.AlertDialogTheme)
                .setTitle("")
                .setMessage(msg)

                // Specifying a listener allows you to take an action before dismissing the dialog.
                // The dialog is automatically dismissed when a dialog button is clicked.
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {

                        if(focus=="MOB") {

                            edtMobileNumber.requestFocus();
                        }
                    }
                });


        if(dlg!=null){

            dlg.show();
        }
    }//ShowAlert


    private void generateOTP( String tenantId,  String isd, String mobile){

        //Call the API
        final ApiInterface apiService =
                ApiClient.getClient().create(ApiInterface.class);


        Call<ApiResponse> call = apiService.generateOTP(tenantId,isd,mobile);

        showHidePgBar(true);
        call.enqueue(new Callback<ApiResponse>() {
            @Override
            public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {

                showHidePgBar(false);
                if(response.body()!=null){

                    ApiResponse api = response.body();
                    if(api.getStatus().trim().equals("S")){

                      //Go to the Verification Activity

                        Intent ii = new Intent(thisActivity,OtpActivity.class);
                        startActivity(ii);
                        finish();
                    }
                    else {

                        ShowAlert(thisActivity,getResources().getString(R.string.something_went_wrong),"");
                    }

                }
                else  if(response.body()==null){

                    ShowAlert(thisActivity,getResources().getString(R.string.something_went_wrong),"");

                }
            }//OnResponse

            @Override
            public void onFailure(Call<ApiResponse> call, Throwable t) {

                showHidePgBar(false);
                //Something went wrong
                ShowAlert(getBaseContext(),getResources().getString(R.string.something_went_wrong),"");

            }
        });

    }//generateOTP





}
